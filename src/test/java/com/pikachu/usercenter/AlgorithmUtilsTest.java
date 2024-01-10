package com.pikachu.usercenter;

import com.pikachu.usercenter.utils.AlgorithmUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@SpringBootTest
public class AlgorithmUtilsTest {
    @Test
    void minDistance() {
        List<String> tags1 = List.of("java,大一,男".split(","));
        List<String> tags2 = List.of("java,大二,男".split(","));
        List<String> tags3 = List.of("python,大三,女".split(","));
        List<String> tags4 = List.of("c++,后端,算法".split(","));

        System.out.println(AlgorithmUtils.minDistance(tags1, tags2));
        System.out.println(AlgorithmUtils.minDistance(tags1, tags3));
        System.out.println(AlgorithmUtils.minDistance(tags1, tags4));
    }
}
