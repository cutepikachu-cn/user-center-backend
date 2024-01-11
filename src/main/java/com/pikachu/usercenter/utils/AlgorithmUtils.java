package com.pikachu.usercenter.utils;

import java.util.List;
import java.util.Objects;

/**
 * 算法工具类
 *
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
public class AlgorithmUtils {

    /**
     * 编辑距离算法
     * 原用法：计算两个字符串相似度
     * 由 word1 到达 word2 需要进行的最少增删草操作次数
     * <p>
     * 计算两个标签列表相似度
     *
     * @param tagList1 标签列表1
     * @param tagList2 标签列表2
     * @return
     */
    public static int minDistance(List<String> tagList1, List<String> tagList2) {
        int n = tagList1.size();
        int m = tagList2.size();

        if (n * m == 0)
            return n + m;

        int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i < n + 1; i++) {
            d[i][0] = i;
        }

        for (int j = 0; j < m + 1; j++) {
            d[0][j] = j;
        }

        for (int i = 1; i < n + 1; i++) {
            for (int j = 1; j < m + 1; j++) {
                int left = d[i - 1][j] + 1;
                int down = d[i][j - 1] + 1;
                int left_down = d[i - 1][j - 1];
                if (!Objects.equals(tagList1.get(i - 1), tagList2.get(j - 1)))
                    left_down += 1;
                d[i][j] = Math.min(left, Math.min(down, left_down));
            }
        }
        return d[n][m];
    }
}
