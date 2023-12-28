package com.pikachu.usercenter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

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

        String newPassword = DigestUtils.md5DigestAsHex(("pikachu" + "123456789").getBytes(StandardCharsets.UTF_8));
        System.out.println(newPassword);
    }

    @Test
    void testDB() {

    }

    @Test
    void contextLoads() {

    }
}
