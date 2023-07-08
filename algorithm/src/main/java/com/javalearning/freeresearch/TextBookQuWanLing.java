package com.javalearning.freeresearch;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
  屈婉玲 《算法设计与分析》第三章

 */
public class TextBookQuWanLing {
    static final Logger log = LoggerFactory.getLogger(TextBookQuWanLing.class);

    /**
     # P60 投资问题
     有5万元，投资4个项目，每个项目只能投资整数万元
     现已知项目收益profits[i][j], i是项目序号, i∈[0,3], j是投资额 j∈[0,5]
     怎样投资收益最大
     */
    static class MaxProfits{
        static int[][] profits = {
                {0,11,12,13,14,15},
                {0, 0, 5,10,15,20},
                {0, 2,10,30,32,40},
                {0,20,21,22,23,24}};

        /*
         动态规划 - 5万元从1开始遍历很容易找到，第二个状态。。。
          问题规模怎么缩小，总资金是一个，项目数呢？
         前j个项目 是个激发灵感的不错的词
         */
        //ERROR 增长潜力思路错误
        static int dpSolution(){
            int total = 5, count = 4;
            int[][] dp = new int[total+1][count+1];

            /*     dp[x][0] = profit[0][x]
             (1,0) profit[0][1]
             (1,1) max(profit[0][1], profit[1][1])
             (1,2) ... dp[1][x] = max(profit[x][1])
             (2,0) profit[0][2]
             (2,1) dp[2][1] = dp[1]

             可以推出状态转移方程的大概内容
             dp[i][j] = dp[i-1][j]多1万元  dp[i][j-1]多来一个项目

                  0   1  2  3
             0-1 11  11 11 20
             1-2 12  (16) (21) (26)
             2-3 13
             3-4 14
             4-5 15
             dp[i-1][j]多1万元可以通过记录各项目的投资额和比较1万元增长潜力[ERROR]来确定分配给谁
             dp[i][j-1]多来一个项目 无解，所以通过第一个问题求解

             0 0  0 0  0
             0 11 1 2 20
             0 11 2 4 40
             0 11 3 6 60
             0 11 4 8 80
             0 11 5 10 100
             */
            for (int i = 1; i <= total; i++) {
                int[] invest = new int[count+1];
                for (int j = 1; j <= count; j++) {
                    //i万元投入前j个项目上
                    if (j == 1){
                        dp[i][j] = profits[0][i];//第一个项目总是拿到i万元投资
                        invest[j] = i;
                        continue;
                    }
                    int maxIncrease = 0;
                    int whichwin = 0;
                    for (int k = 1; k <= j; k++) {
                        int increase = profits[k-1][invest[k]+1] - profits[k-1][invest[k]];
                        if (increase > maxIncrease){
                            maxIncrease = increase;
                            whichwin = k;
                        }
                    }
                    invest[whichwin]+=1;
                    dp[i][j] = dp[i-1][j] + maxIncrease;
                }
            }
            return dp[total][count];
        }
        //dp[i][j]的递推。。。与(i-1,j)还是(i,j-1)有关？居然是跟(i-k,j-1)有关
        static int dpSolution3(){
            int total = 5, count = 4;
            int[][] dp = new int[total+1][count+1];
            int times = 0;
            for (int i = 1; i <= total; i++) {
                for (int j = 1; j <= count; j++) {
                    //i万元投入前j个项目上
                    //if (j == 1){
                    //    dp[i][j] = profits[0][i];//第一个项目总是拿到i万元投资
                    //    continue; 这种特例处理给TC代码了困扰。。。
                    //}
                    int currentmax = 0;
                    for (int k = 0; k <= i; k++) {
                        int profit = dp[i-k][j-1] + profits[j-1][k];
                        times++;
                        if (currentmax < profit){
                            currentmax = profit;
                        }
                    }
                    dp[i][j] = currentmax;
                }
            }
            return dp[total][count];
        }

        static int dpSolution4(){
            int total = 5, count = 4;
            int[][] dp = new int[count+1][total+1];
            int times = 0;
            for (int i = 1; i <= count; i++) {
                for (int j = 1; j <= total; j++) {
                    //固定i个项目接收j万元投资
                    int result = 0;
                    for (int k = 0; k <= j; k++) { //注意k要取到j
                        result = Math.max(result, dp[i-1][j-k] + profits[i-1][k]);
                        times++;
                    }
                    dp[i][j] = result;
                }
            }
            return dp[count][total];
        }
        /*
        时间复杂度分析
        dp3是 total > count > total层级的遍历
        dp4是 count > total > total层级的
        仅仅遍历顺序改变就  dp3 - 60, dp4 - 80

        n个项目m万元，dp TC = O(nm^2)，时间复杂度与遍历维度的先后次序无关。
        特例的处理可能可以去掉，代码能处理这种情况
         */
        public static void main(String[] args) {
            System.out.println(dpSolution4());
            dpSolution3();
        }

    }
    /**
     #P68 变位图像压缩技术
     对于像素点序列 P = {10,12,15,255,1,2,1,1,2,2,1,1}，共12个像素，像素点数量≤ 256,灰度数值 ≤ 256
     若对所有像素点都使用8位存储，则需要8*12=96位，但若分段存储，并在段头里描述像素点数量（≤ 256）和像素点位数（≤ 8），那么每段只需要使用段头规定的像素点位数进行灰度值描述了
     如上述像素序列分成3段：<10,12,15> <255> <1,2,1,1,2,2,1,1>
     共需要 11+4*3 + 11+8 + 11+2*8 = 69 位
     给定一个像素序列，求占用的最小位数
     case: {10,12,15,255,1,2} - 57
      */
    static class ImageDynamicBitCompressTech{
        /*
          状态：序列长度，
          dp数组内容
             0  1  2  3  4
         0   x  -  -  -  -
         1   x  x  -  -  -
         2   x  x  x  -  -
         3
         4            ?
         额，写来写去居然是个一维动态规划
         此算法树上说 TC=O(n) 内部j从i到0
         */
        static int dpSolution(int[] pixels){
            int length = pixels.length;
            int[] dp = new int[length];
            dp[0] = 11+getBitCount(pixels[0]);
            for (int i = 1; i < length; i++) {
                //序列长度i，第j个元素表示最后一个分段的起始元素
                int lastseg , result = Integer.MAX_VALUE;
                int maxnum = pixels[i];
                for (int j = i; j >= 0; j--) {
                    //j始终是<=i的，所以dp[i][j]上i<j的元素无意义]
                    //找到最后分段中的最大数字，计算位数作为该段的位数，乘以数量加11
                    maxnum = Math.max(maxnum,pixels[j]);
                    int bitCount = getBitCount(maxnum);
                    int numCount = i-j+1;
                    lastseg = 11 + bitCount*numCount;
                    int presegs = j>0 ? dp[j-1] : 0;
                    result = Math.min(result, lastseg + presegs);//result变更时记下j好输出分段信息
                }
                dp[i] = result;
            }
            return dp[length-1];
        }
        static int getBitCount(int num){
            int bits = 0;
            int temp = num;
            while (temp > 0){
                temp = temp >> 1;
                bits++;
            }
            return bits;
        }

        public static void main(String[] args) {
            int[] pixels0 = {10,12,15,255,1,2,1,1,2,2,1,1};
            int[] pixels1 = {10,12,15,255,1,2};
            log.info(dpSolution(pixels0)+"");
            log.info(dpSolution(pixels1)+"");
        }

    }













}
