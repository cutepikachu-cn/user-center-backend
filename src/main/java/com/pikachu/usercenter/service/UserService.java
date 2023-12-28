package com.pikachu.usercenter.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pikachu.usercenter.model.dto.request.UserUpdateRequest;
import com.pikachu.usercenter.model.entity.User;
import com.pikachu.usercenter.model.vo.LoginUserVO;
import com.pikachu.usercenter.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author 28944
 * @description 针对表【user(用户表)】的数据库操作Service
 * @createDate 2023-12-03 17:15:34
 */
public interface UserService extends IService<User> {

    /**
     * 盐值
     * 用于混淆加密密码
     */
    String SALT = "pikachu";

    /**
     * 用户注册
     *
     * @return 新用户 id
     */
    Long userRegister(String account, String password);

    /**
     * 用户登录
     *
     * @param account  账户
     * @param password 密码
     * @return 脱敏后的用户信息对象
     */
    LoginUserVO userLogin(String account, String password, HttpServletRequest request);


    /**
     * 用户注销
     */
    void userLogout(HttpServletRequest request);

    /**
     * 查询用户列表（管理员操作）
     *
     * @param current  页码
     * @param pageSize 每页条数
     * @return 查询到的用户列表
     */
    IPage<User> pageUsers(Long current, Long pageSize);

    /**
     * 搜索用户列表（普通用户操作）
     *
     * @param conditions 搜索条件
     * @param current    页码
     * @param pageSize   每页条数
     * @return 搜索到的用户列表
     */
    IPage<UserVO> searchUsers(Map<String, Object> conditions, Long current, Long pageSize);

    /**
     * 根据 id 获取用户信息（脱敏）
     *
     * @param id 用户 id
     * @return 脱敏信息后的用户对象
     */
    UserVO getUserVOById(Long id);

    void updateUser(UserUpdateRequest userUpdateRequest, HttpServletRequest request);

}
