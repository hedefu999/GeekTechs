package com.javalearning.leetcode.labuladong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class _04DynamicProgramme {
    private static final Logger log = LoggerFactory.getLogger(_04DynamicProgramme.class);

    /**
     # 651 VIP 四键问题
     对于打印一堆A到屏幕上，多个操作（N）可以选择一直按A，也可以选择拷贝后不断粘贴
     在有限的操作次数内，何种操作顺序得到的字母A最多
     操作A - 打印一个A；操作B - 选中全部的A；操作C - copy；操作D - 粘贴；
     case: N=3,可以得到3个A: A A A，操作序列是: AAA
     case: N=7,最多可以得到9个A，操作序列是: AAABCDD 3*(2+1)>4*2
     case: N=8 AAAABCDD=12 AAABCDDD=12
     case: N=9 AAAABCDDD=16 AAABCDBCD
     case: N=10 AAAABCDBCD=16 AAAABCDDDD=20
     case: N=11 AAAAABCDBCD=20  AAAABCDBCDD=24 AAABCDBCDDD=24 AAAAABCDDDD=25 最大是27
     */
    static class FourKeysProblem{
        /*
         N次操作，穷举共4^N种情形
         +1消耗1，*2消耗3，+n消耗1
         在N次消耗完之前，(+) *2 *(+) 的最大计算结果
         x+3y+z = N; [x*(2^yy+zz)]*(2^yyy+zzz) 的最大值
         ERROR: 此算法在N=11时错误，错误地认为得到最多的A就一定是 AAA...ABCDD...D 这样的操作序列，显然AA..ABCDD..DBCD..D会有更多可能
         */
        static int myMathmaticsSolution(int N){
            int maxVal = 0;
            for (int i = 1; i <= N; i++) {
                for (int j = 0; j <= (N-i)/3; j++) {
                    int left = N-i-j*3;
                    int current = 0;
                    if (j!=0){
                        current = i*((int)Math.pow(2,j)+left);
                    }else {
                        current = i;
                    }
                    maxVal = Math.max(maxVal, current);
                }
            }
            return maxVal;
        }

        /*
         一种类似编辑距离的动态规划解法
         动态规划的思路首先是找状态：剩余操作数i、当前A字符的数量j、剪贴板中A字符的数量k，则dp(i-1,j+1,k)表示按下A，dp(i-1,j+k,k)表示粘贴，dp(i-2,j,j)表示按下选择拷贝
         则这三者的最大值就是应进行的操作
        */
        static int mindistanceSoluiton(int N){
            return mindistanceRecursiveHelper(N,0,0);
        }
        static int mindistanceRecursiveHelper(int leftOpCount, int ACount, int acount){
            if (leftOpCount <= 0) return ACount;
            return Math.max(
                mindistanceRecursiveHelper(leftOpCount-1, ACount+1, acount)
                        ,Math.max(
                mindistanceRecursiveHelper(leftOpCount-1, ACount+acount, acount),
                mindistanceRecursiveHelper(leftOpCount-2, ACount, ACount)
                    ));
        }//这种递归解法无脑拷贝粘贴，很多子问题是低效的

        /*
        一种类似最大递增子序列的动态规划解法
        回到四种操作 ABCD, 正常的操作都是 AAA..BCD..BCDDDD,这样可以从后向前i，i表示后面全是D的取值范围，则i-2范围内的最大字符数进行计算即可，缩小了问题规模
        */
        static int subsequenceSolution2(int N){
            int[] dp = new int[N+1];
            //从后向前缩小了问题规模，但小规模的问题还未求解，所以就有问题从0开始逐步改变问题总规模从后向前的求解，这样就是二维数组了
            for (int i = 1; i <= N; i++) { //i递增表示问题总规模逐步扩大
                int cvcount = 0;//从后向前逐步进行cvv，扩大v的规模可得到的数量
                for (int j = i; j >=4 ; j--) {//在问题规模极小时恰好就是拷贝粘贴不如单击A结果多的情形
                    cvcount = Math.max(cvcount, dp[j-3]*(i-j+2));//j=i -2倍，i-j=1-3倍 i-j=2~4倍
                }
                dp[i] = Math.max(i,cvcount);//需要跟全部输入A的操作进行比较，仅在4-6时用得到，i从7起步可以进一步简化代码
            }
            return dp[N];
        }
        /*
         在动态规划中收集每个步骤,ops打断点可观察到具体操作序列
         N <= 6       全A
         N =  7 - 9   AAABCDD
         N =  8 - 12  AAABCDDD
         N =  9 - 16  AAAABCDDD
         N = 10 - 20  AAAABCDDDD
         N = 11 - 27  AAAAABCDDDD - 25, AAABCDDBCDD 9*3=27
         */
        static int subsequenceSolution3(int N){
            int[] dp = new int[N+1];
            List<List<String>> ops = new ArrayList<>(N+1);
            for (int i = 0; i < N+1; i++) {
                ops.add(new ArrayList<>(i));
            }
            for (int i = 1; i<=N && i < 7; i++) {
                dp[i] = i;
                for (int j = 0; j < i; j++) {
                    ops.get(i).add("A");
                }
            }
            for (int i = 7; i <= N; i++) {
                int firstDIndexInLastGroup = 0;
                for (int j = i; j >=4 ; j--) {
                    int cvcount = dp[j - 3] * (i - j + 2);
                    if (cvcount>dp[i]){
                        dp[i] = cvcount;
                        firstDIndexInLastGroup = j;
                    }
                }
                //firstDIndexInLastGroup及之后为全DD，前面有BC，拷贝firstDIndexInLastGroup-3的数据进来
                List<String> currentops = ops.get(i);
                currentops.addAll(ops.get(firstDIndexInLastGroup-3));
                currentops.add("B");currentops.add("C");
                for (int j = firstDIndexInLastGroup; j <= i; j++) {
                    currentops.add("D");
                }
            }
            return dp[N];
        }
        static int subsequenceSolution(int N){
            int[] dp = new int[N + 1];
            dp[0] = 0;
            for (int i = 1; i <= N; i++) {
                // 按 A 键
                dp[i] = dp[i - 1] + 1;
                for (int j = 2; j < i; j++) {
                    // 全选 & 复制 dp[j-2]，连续粘贴 i - j 次
                    // 屏幕上共 dp[j - 2] * (i - j + 1) 个 A
                    dp[i] = Math.max(dp[i], dp[j - 2] * (i - j + 1));
                }
            }
            return dp[N];
        }
        /*
         定义dp[i][j]表示拷贝粘贴操作的起始和数量，则i+j*3<=length
         j>=0,i∈[1,length-3] 以i为主，j为次遍历，，，似乎还是上面的
         是否可以总结成三种不同权重的操作，成为背包问题，但放置顺序有要求
         */
        public static void main(String[] args) {
            for (int i = 1; i < 20; i++) {
                log.info("N={}, mindistance - {}, subsequence - {}, mine - {}",
                        i,mindistanceSoluiton(i),subsequenceSolution(i),myMathmaticsSolution(i));
            }

        }
    }


    /**
        贪心算法可视作动态规划的一个特例（满足贪心选择性质），效率更高
     贪心选择性质：每一步都做出一个局部最优的选择，最终的结果就是全局最优，仅一部分dp问题才具备此性质
     */
    static class GeedAlgorithm{
        /*
        #435 贪心算法之区间冲突判断问题
         经典贪心算法 - 区间调度
         给定许多[start, end]区间，计算出这些区间中最多有几个互不相交的区间
         如日程安排中的不冲突活动筛选，求最大不相交区间子集
         */
        static class IntervalSchedule{
            /*
                递归解法,返回需要移除区间的最小数量
             */
            static int intervalCollisionRecursiveSolution(int[][] intvs){
                Arrays.sort(intvs, new Comparator<int[]>() {
                    @Override
                    public int compare(int[] o1, int[] o2) {
                        return o1[0] - o2[0];
                    }
                });
                return intervalCollisionHelper(-1,0,intvs);
            }
            //pre上一区间 cur当前区间（试图不移除）返回从当前下标开始需要移除的区间个数
            //检测当前区间是否与上一区间重叠，若不重叠就检测curr和curr+1
            //移除当前区间可能会带来更少的区间移除，所以不论当前区间是否与上一个重叠，都要移除再调用递归，此时数字要+1
            //返回较小值
            static int intervalCollisionHelper(int prev, int curr, int[][] intvs){
                if (curr == intvs.length) return 0;
                int keep = Integer.MAX_VALUE, remove;
                if (prev == -1 || intvs[prev][1] <= intvs[curr][0]){
                    keep = intervalCollisionHelper(curr, curr+1, intvs);
                }
                //这里的remove的计算之所以没有放到else里，是因为即使第二个区间跟上一个没有重叠，但它可能本身与后面的多个重叠，而后面的多个区间可能是不重叠的，所以还要移除参与一下计算
                remove = intervalCollisionHelper(prev, curr+1, intvs) + 1;
                return Math.min(keep, remove);
            }//递归解法反而难以看懂，并且性能也弱


            //从起点入手的动态规划解法，类似递增子序列的求解
            static int intervalScheduleDpSolution(int[][] intvs){
                int length = intvs.length;
                if (length == 0) return 0;
                //对起点排序
                Arrays.sort(intvs, new Comparator<int[]>() {
                    @Override
                    public int compare(int[] o1, int[] o2) {
                        return o1[0] - o2[0];
                    }
                });
                int[] dp = new int[length];
                //判断当前区间与前面所有的不重叠区间，取其计数+1
                for (int i = 0; i < length; i++) {
                    int[] curr = intvs[i];
                    int max = 0;
                    for (int j = i-1; j >= 0; j--) {
                        int[] comp = intvs[j];
                        //已经按start排序，重叠就是comp.end > curr.start,comp在curr前面
                        boolean overlap = comp[1] > curr[0];
                        if (overlap){
                            continue;
                        }else {
                            max = Math.max(max, dp[j]);
                        }
                    }
                    dp[i] = max + 1;
                }
                return dp[length-1];
            }

            //从终点入手的贪心算法,比上面的动态规划效率要高很多
            static int intervalScheduleExtremeSolution(int[][] intvs){
                int length = intvs.length, result = length;
                if (length == 0) return 0;
                //先按照区间end进行排序
                Arrays.sort(intvs, new Comparator<int[]>() {
                    @Override
                    public int compare(int[] o1, int[] o2) {
                        //return o1[1] - o2[1]; 在 (-2147483646) - (2147483646) 的情况下会溢出，导致排序出错
                        return o1[1] > o2[1]?1:o1[1] == o2[1]?0:-1;//改进
                        //return Integer.compare(o1[1],o2[1]);// Integer的封装果然是经过实践检验的
                    }
                });
                int[] curr = intvs[0];//在此收集不重叠子集
                for (int i = 1; i < length; i++) {
                    int[] comp = intvs[i];
                    //boolean intersect = (comp[0]>curr[0] && comp[0]<curr[1]) || (comp[1]>curr[0] && comp[1]<curr[1]);
                    //后部end排序过后判断两区间是否相交就比较简单了
                    boolean intersect = comp[0] < curr[1];
                    if (intersect){
                        result--;
                    }else {
                        curr = intvs[i];//在此收集不重叠子集
                    }
                }
                return result;
            }
            //此解法的贪心选择性质体现在curr的选择，curr的遍历顺序取决于区间的end排序

            public static void main(String[] args) {
                int[][] case1 = {{1,3},{2,4},{3,6}};//2
                System.out.println(intervalScheduleDpSolution(case1));
            }
        }

        /*
          #452 用最少的箭头射爆气球
          给出气球的直径区间，弓箭沿y轴方向，求戳爆所有气球所需的最小弓箭数量
         */
        static class BreakBalloon{
            static int findMinArrowShots(int[][] points) {
                if(points.length==0){
                    return 0;
                }
                Arrays.sort(points,(a,b)->Integer.compare(a[1],b[1]));
                int count = 1;
                int x_end = points[0][1];
                for(int[] num:points){
                    if(num[0]>x_end){//发现不重叠
                        count++;
                        x_end = num[1];
                    }
                }
                return count;
            }

            public static void main(String[] args) {
                int[][] case1 = {{10,16},{2,8},{1,6},{7,12}}; //2
                int[][] case2 = {{1,2},{3,4},{5,6},{7,8}};//4
                int[][] case3 = {{1,2},{2,3},{3,4},{4,5}};//2 注意区间相连也可以被箭射爆
                int[][] case4 = {{1,2}};//1
                int[][] case5 = {{2,3},{2,3}};//1
                int[][] case6 = {{-2147483646,-2147483645},{2147483646,2147483647}};
                //System.out.println(IntervalSchedule.intervalScheduleExtremeSolution(case1));
                //System.out.println(IntervalSchedule.intervalScheduleExtremeSolution(case2));
                //System.out.println(findMinArrowShots(case3));
                //System.out.println(IntervalSchedule.intervalScheduleExtremeSolution(case4));
                //System.out.println(IntervalSchedule.intervalScheduleExtremeSolution(case5));
                System.out.println(IntervalSchedule.intervalScheduleExtremeSolution(case6));
            }
        }

        /**
         从戳气球问题看贪心算法
         让气球上下平移对end进行排序，达到将接近的气球尽量放在一起的效果
         贪心算法一般用来解决需要"找到要做某事的最小数量" 或 "找到在某些情况下适合的最大物品数量"，且提供的是无序的输入
         通常需要对输入数据进行排序
         */
    }


    /**
     # 494 目标和
     */
    static class TargetSumWays{

    }


}
