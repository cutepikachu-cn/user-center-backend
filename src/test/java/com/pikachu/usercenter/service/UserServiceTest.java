package com.pikachu.usercenter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pikachu.usercenter.model.entity.User;
import com.pikachu.usercenter.model.vo.UserVO;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 用户服务测试
 *
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void test() {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("account", "cute-pikachu");
        userQueryWrapper.eq("password", "7ba4f0807b7765615e23011ed4edcaf9");
        System.out.println(userService.getOne(userQueryWrapper));
    }

    @Test
    void searchUserByTags() {
        List<String> tagNameList = Arrays.asList("java", "C++");
        HashMap<String, Object> conditions = new HashMap<>();
        // conditions.put("nickname", "e");
        conditions.put("tags", tagNameList);
        IPage<UserVO> userList = userService.searchUsers(conditions, 1L, 5L);
        Assertions.assertNotNull(userList);
    }

    @Test
    void searchUsers() {
        HashMap<String, Object> conditions = new HashMap<>();
        conditions.put("nickname", "e");
        IPage<UserVO> users = userService.searchUsers(conditions, 3L, 5L);
        Assertions.assertNotNull(users);
    }

    @Test
    void pageUsers() {
        IPage<User> users = userService.pageUsers(1L, 5L);
        Assertions.assertNotNull(users);
    }

    @Test
    void testAdd() {
        User user = new User();
        user.setAccount("testPikachu");
        user.setNickname("testPikachu");
        user.setAvatarUrl("https://d-ssl.dtstatic.com/uploads/blog/202007/29/20200729133846_jxzpy.thumb.700_0.jpg_webp");
        user.setGender(true);
        user.setAge(20);
        user.setPassword("123456");
        user.setPhone("10000000000");
        user.setEmail("10000@cute-pikachu.cn");

        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void userRegister() {
        String account = "";
        String password = "12345678";
        String checkPassword = "12345678";
        Long result = userService.userRegister(account, password);
        Assertions.assertEquals(-1, result);

        account = "cut";
        result = userService.userRegister(account, password);
        Assertions.assertEquals(-1, result);

        account = "cute-pikachu^&*";
        result = userService.userRegister(account, password);
        Assertions.assertEquals(-1, result);

        account = "testPikachu";
        result = userService.userRegister(account, password);
        Assertions.assertEquals(-1, result);

        password = "1234567890";
        result = userService.userRegister(account, password);
        Assertions.assertEquals(-1, result);

        account = "cute-pikachu";
        password = "12345678";
        result = userService.userRegister(account, password);
        Assertions.assertTrue(result > 0);
    }
}
