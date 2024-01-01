package com.pikachu.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pikachu.usercenter.common.ResponseCode;
import com.pikachu.usercenter.exception.BusinessException;
import com.pikachu.usercenter.mapper.UserMapper;
import com.pikachu.usercenter.model.dto.request.UserUpdateRequest;
import com.pikachu.usercenter.model.entity.User;
import com.pikachu.usercenter.model.vo.LoginUserVO;
import com.pikachu.usercenter.model.vo.UserVO;
import com.pikachu.usercenter.service.UserService;
import com.pikachu.usercenter.utils.Tools;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.pikachu.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author 28944
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2023-12-03 17:15:34
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private RedisTemplate<String, Page<User>> redisTemplate;

    @Override
    public boolean updateById(User entity) {
        // 账户名 / 创建时间不可修改
        entity.setAccount(null);
        entity.setCreateTime(null);
        // 修改时间为当前时间
        entity.setUpdateTime(LocalDateTime.now());
        return super.updateById(entity);
    }

    @Override
    public Long userRegister(String account, String password) {

        // 查询用户是否存在
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("account", account);
        if (count(userQueryWrapper) > 0) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "用户已存在");
        }

        // 2. 密码加密
        String encryptPassword = Tools.encrypString(password);

        // 3. 插入数据
        User user = new User();
        user.setAccount(account);
        user.setPassword(encryptPassword);

        save(user);
        return user.getId();
    }

    @Override
    public LoginUserVO userLogin(String account, String password, HttpServletRequest request) {
        // 2. 查询用户
        String encryptPassword = Tools.encrypString(password);
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("account", account);
        User user = getOne(userQueryWrapper);
        if (user == null || !user.getPassword().equals(encryptPassword)) {
            log.info("User login failed, account: {}", account);
            throw new BusinessException(ResponseCode.NOT_LOGIN, "用户或密码错误");
        }

        // 3. 信息脱敏
        LoginUserVO loginUserVO = LoginUserVO.fromUser(user);

        // 4. 记录用户登录状态
        // 获取 Session，并存储登录信息
        HttpSession session = request.getSession();
        session.setAttribute(USER_LOGIN_STATE, loginUserVO);

        return loginUserVO;

    }

    @Override
    public LoginUserVO getCurrentLoginUser(HttpServletRequest request) {
        return (LoginUserVO) request.getSession().getAttribute(USER_LOGIN_STATE);
    }

    @Override
    public void userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
    }

    @Override
    public IPage<User> pageUsers(Long current, Long pageSize) {
        Page<User> page = page(new Page<>(current, pageSize));
        page.setRecords(page.getRecords().stream().peek(user -> user.setPassword(null)).collect(Collectors.toList()));
        return page;
    }

    @Override
    public IPage<UserVO> searchUsers(Map<String, Object> conditions, Long current, Long pageSize) {
        Page<User> userPage;
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        String nickname;
        List<String> searchedTagList;

        // 从 Redis 缓存读数据
        // 若缓存中存在数据，直接返回
        String redisKey = String.format("user-center:user:search-%d-%d", current, pageSize);
        ValueOperations<String, Page<User>> opsForValue = redisTemplate.opsForValue();
        if (!StringUtils.isBlank(nickname = (String) conditions.get("nickname"))) {
            redisKey = String.format("%s:nickname-%s", redisKey, nickname);
            userPage = opsForValue.get(redisKey);
            if (userPage == null) {
                userPage = searchUserByNickname(nickname, current, pageSize, userQueryWrapper);
            }

        } else if (!CollectionUtils.isEmpty(searchedTagList = (List<String>) conditions.get("tags"))) {
            redisKey = String.format("%s:tags-%s", redisKey, searchedTagList.stream().sorted().toList());
            userPage = opsForValue.get(redisKey);
            if (userPage == null) {
                userPage = searchUserByTags(searchedTagList, current, pageSize, userQueryWrapper);
            }

        } else {
            userPage = opsForValue.get(redisKey);
            if (userPage == null) {
                userPage = page(new Page<>(current, pageSize), userQueryWrapper);
            }

        }
        // 写缓存
        try {
            opsForValue.set(redisKey, userPage, 1, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("redis set key error", e);
        }

        // if (userPage == null)
        //     throw new BusinessException(ResponseCode.SYSTEM_ERROR);
        return userPage.convert(UserVO::fromUser);
    }

    private Page<User> searchUserByTags(List<String> searchedTagList,
                                        Long current,
                                        Long pageSize,
                                        QueryWrapper<User> queryWrapper) {
        // 方法一、直接在 SQL 筛选
        for (String tagName : searchedTagList) {
            queryWrapper.like("tags", tagName);
        }
        Page<User> userPage = page(new Page<>(current, pageSize), queryWrapper);

        // 方法二、先查询出用户，保存在内存中进行查询
        // List<User> userList = list(queryWrapper);
        // userList = userList.stream().filter(user -> {
        //     List<String> tags = user.getTags();
        //     if (CollectionUtils.isEmpty(tags))
        //         return false;
        //     for (String tagName : searchedTagList) {
        //         if (!tags.contains(tagName)) {
        //             return false;
        //         }
        //     }
        //     return true;
        // }).collect(Collectors.toList());
        // 手动分页
        // int fromIndex = Math.toIntExact((current - 1) * pageSize);
        // int toIndex = Math.min(fromIndex + pageSize.intValue(), userList.size());
        // Page<User> userPage = new Page<>(current, pageSize, userList.size());
        // userPage.setRecords(new ArrayList<>(userList.subList(fromIndex, toIndex)));

        return userPage;
    }

    private Page<User> searchUserByNickname(String nickname,
                                            Long current,
                                            Long pageSize,
                                            QueryWrapper<User> queryWrapper) {
        queryWrapper.like("nickname", nickname);
        return page(new Page<>(current, pageSize), queryWrapper);
    }

    @Override
    public UserVO getUserVOById(Long id) {
        return UserVO.fromUser(getById(id));
    }

    @Override
    public List<UserVO> listUserVO(QueryWrapper<User> queryWrapper) {
        List<User> userList = list(queryWrapper);
        return userList.stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).toList();
    }

    @Override
    public void updateUser(UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        HttpSession session = request.getSession();
        LoginUserVO currentUser = (LoginUserVO) session.getAttribute(USER_LOGIN_STATE);
        userUpdateRequest.setId(currentUser.getId());
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        if (!updateById(user)) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "修改用户信息失败");
        }
        user = getById(user.getId());
        currentUser = new LoginUserVO();
        BeanUtils.copyProperties(user, currentUser);
        session.setAttribute(USER_LOGIN_STATE, currentUser);
    }

}




