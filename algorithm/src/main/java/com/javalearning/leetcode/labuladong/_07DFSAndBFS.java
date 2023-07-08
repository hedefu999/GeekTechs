package com.javalearning.leetcode.labuladong;

/**
 * 深度优先遍历和广度优先遍历
 */
public class _07DFSAndBFS {
    /**
     矩阵中相邻的1作为一个岛屿，相邻指上下左右。问一个矩阵中有多少个岛屿
     {{1,1,1,1,0},
     {1,1,0,1,0},
     {1,1,0,0,0},
     {0,0,0,0,0}}  有1个岛屿

     {{1,1,0,0,0},
     {1,1,0,0,0},
     {0,0,1,0,0},
     {0,0,0,1,1}}  有3个岛屿
     */
    static class IslandCount{
        static int countIsland(int[][] nums){
            int lands = 0;
            int n = nums.length;
            int m = nums[0].length;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (nums[i][j] == 1){
                        lands++;
                        dfs(nums,i,j);
                    }
                }
            }
            return lands;
        }
        //深度优先遍历，是要找的陆地就递归找下去，同时标记掉已访问过
        //每进行一次深度优先遍历就计数 +1
        static void dfs(int[][] nums, int i, int j){
            int n = nums.length, m = nums[0].length;
            nums[i][j]=0;//访问过的岛屿要标记掉，简单点改成0
            //要对4个方向做判断
            if (i-1 >= 0 && nums[i-1][j] == 1){
                dfs(nums, i-1, j);//递归访问
            }
            if (i+1 < n && nums[i+1][j] == 1){ //下面是陆地吗
                dfs(nums, i+1, j);
            }
            if (j-1 >= 0 && nums[i][j-1] == 1){//左边是陆地吗
                dfs(nums, i, j-1);
            }
            if (j+1 < m && nums[i][j+1] == 1){//右边是陆地吗
                dfs(nums, i, j+1);
            }
        }
        public static void main(String[] args) {
            int[][] map = {{1,1,1,1,0},
                    {1,1,0,1,0},
                    {1,1,0,0,0},
                    {0,0,0,0,0}};
            int[][] map2 = {{1,1,0,0,0},
                    {1,1,0,0,0},
                    {0,0,1,0,0},
                    {0,0,0,1,1}};
            System.out.println(countIsland(map2));
        }
    }
}
