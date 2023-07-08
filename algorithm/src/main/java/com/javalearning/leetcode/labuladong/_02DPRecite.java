package com.javalearning.leetcode.labuladong;

public class _02DPRecite {
    static class ZeroOnePPackage{
        /**
         * 0  1  2  3 - j 可选物品，1表示0，1；2表示0 1 2
         0 0  0  0
         1 0  2  2
         2 0
         3
         4
         |剩余容量 i
         */
        static void dpSolution(){

        }
        public static void main(String[] args) {
            int N = 3,W = 4;
            int[] wt = {2,1,3};
            int[] val = {4,2,3};
            int[][] dp = new int[W+1][N+1];
            for (int i = 1; i <= W; i++) {
                for (int j = 1; j <= N; j++) {
                    if (wt[j-1] > i){//不能放
                        dp[i][j] = dp[i][j-1];
                    }else {
                        dp[i][j] = Math.max(dp[i-wt[j-1]][j-1]+val[j-1],dp[i][j-1]);//能放也不一定放
                    }
                }
            }
            System.out.println(dp[4][3]);
        }
    }
}
