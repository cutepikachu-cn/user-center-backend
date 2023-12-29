package com.pikachu.usercenter;

import com.pikachu.usercenter.utils.Tools;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@SpringBootTest
class UserCenterBackendApplicationTest {
    @Test
    void testDigest() throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] digest = md5.digest("abc123".getBytes(StandardCharsets.UTF_8));
        System.out.println(new String(digest));

        String newPassword = Tools.encrypString("12345678");
        System.out.println(newPassword);
    }

    @Test
    void testDB() {

    }

    @Test
    void contextLoads() {

    }
}
