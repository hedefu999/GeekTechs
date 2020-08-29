package labuladong;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class _01DynamicProgramming {
    /**
     * # 从fibonacci数列引出动态规划
     * index      1 2 3 4 5 6  7
     * fibonacci  1 1 2 3 5 8 13
     */
    /*
     * 使用原始的递归方法计算
     * 算法低效,时间复杂度 O(2^n)
     */
    int fibnacci(int N){
        if (N == 1 || N == 2) return 1;
        return fibnacci(N - 1) + fibnacci(N - 2);
    }

    /*
     * 带备忘录的解法，使用数组/哈希表.时间复杂度O(n)
     * 将备忘录作为递归入参传入
     */
    int fibnacciWithMemo(int N){
        if (N < 1) return 0;
        int[] memo = new int[N+1];
        return helper(memo, N);
    }
    int helper(int[] memo, int n){
        if (n == 1 || n == 2) return 1;
        if (memo[n] != 0) return memo[n];
        memo[n] = helper(memo, n-1) + helper(memo, n-2);
        return memo[n];
    }
    /*
     * 上述带备忘录的递归解法是 自顶向下的。而动态规划的做法通常是在循环迭代中自底向上，脱离了递归
     * 将这个备忘录独立出来成为一张表(dpTable[0]没有使用)
     * 时间复杂度O(N) 空间复杂度O(N)
     */
    int fibnacciDPSolution(int N){
        int[] dpTable = new int[N+1];
        dpTable[1] = dpTable[2] = 1;
        for (int i = 3; i <= N; i++)
            dpTable[i] = dpTable[i-1]+dpTable[i-2];//这里就是臭名昭著的状态转移方程
        return dpTable[N];
    }
    /*
     * 继续优化，时间复杂度应该是到顶了，但空间复杂度可以是O(1)的
     * 这个技巧就是状态压缩，如果每次状态转移都只需要DP Table中的一部分，就可以用状态压缩缩小DPTable的大小
     * 实际中的应用是将一个二维的DP table压缩成一维，将空间复杂度从O(n^2)压缩到O(n)
     */
    int fibnacciDPUltimateEdition(int N){
        int left = 1,right = 1;
        for (int i = 3; i <= N; i++) {
            int temp = right;
            right = left + right;
            left = temp;
        }
        return right;
    }
    @Test
    public void test33(){
        System.out.println(fibnacciDPUltimateEdition(4));
    }
    /*
     上述借助fibnacci数列介绍了动态规划的一个重要特性：重叠子问题的消除。
     fibonacci严格来说算不上动态规划，因为不涉及求最值
     动态规划的另一个重要特性：最优子结构。下面就来演示重叠子问题的消除方法

     默写总结：普通递归、带备忘录的递归、填充备忘录的动态规划、精简备忘录的动态规划
     */

    /**
     # 凑零钱问题 / 最优子结构问题
     ```
     给你 `k` 种面值的硬币，面值分别为 `c1, c2 ... ck`，每种硬币的数量无限，再给一个总金额 amount，
     问你最少需要几枚硬币凑出这个金额，如果不可能凑出，算法返回 -1 。
     ```
     */
    /*
      ## 暴力递归解法
      此问题具有最优子结构，属于动态规划问题，子问题间必须相互独立
      - 动态规划问题解题思路
      1. 确定base case: amount为0时返回0
      2. 确定状态，即原问题和子问题中会变化的变量：要求amount=11的，如果知道amount=10的，就+1好了。这amount就是状态，它可以递推到base case
      3. 确定选择，也就是导致状态产生变化的行为：所有硬币的面值
      4. 明确dp函数/数组的定义：一般来说，函数的参数就是状态转移中会变化的量，即状态
        所以定义的dp数组定义为: 输入一个目标金额n，返回凑出目标金额n的最小硬币数量dp(n)

        基于上述分析得出状态转移方程
                0, n = 0
      dp(n) =  -1, n < 0
               min{dp(n-coin)+1}, coin属于coins，n>0
      时间复杂度：子问题总数*每个子问题的时间 O(k*n^k)
      todo 硬币问题的暴力解法时间复杂度的推导
     */
    int coinChange(int[] coins, int amount){
        if (amount == 0) return 0;
        if (amount < 0) return -1;//
        int result = Integer.MAX_VALUE;
        for (int coin : coins){
            int subproblem = coinChange(coins, amount - coin);
            if (subproblem == -1) continue;
            result = Math.min(result, subproblem + 1);
        }
        return result == Integer.MAX_VALUE?-1:result;
    }

    /*
    ## 带备忘录的递归
     先遍历到amount=1, 求 1 - 各种coin 对应的amount的值： 0 -1 -4 均返回-1，for循环中对这一结果进行了忽略
     每个amount都需要进行forCoin遍历，而for循环中的递归存在大量重复子问题。
     每种结点只走一次，每个coin类型的都要走一遍找出最小值，所以amount * coinType根分支都需要走一遍，O(n) = O(nk)
    * */
    int coinCompositeWithDPTable(int[] coins, int amount){
        Map<Integer,Integer> dptable = new HashMap<>();
        return dptableHelper(coins,amount,dptable);
    }
    int dptableHelper(int[] coins, int amount, Map<Integer,Integer> memo){
        if (memo.containsKey(amount)) return memo.get(amount);
        if (amount == 0) return 0;
        if (amount < 0) return -1;
        int result = Integer.MAX_VALUE;
        for (int coin : coins){
            int subproblem = dptableHelper(coins, amount - coin, memo);
            if (subproblem == -1) continue;
            result = Math.min(result, subproblem+1);//subproblem需要加1，因为上面减掉的一个coin要算上
        }
        result = result == Integer.MAX_VALUE?-1:result;
        memo.put(amount, result);
        return result;
    }
    /*
     上述求解是自顶向下的递归（递归通常都是自顶向下）+ 备忘录（hash表）
     真正的动态规划是在循环中自底向上，采用dp数组
     初始化为最大值，如果最终还是这个最大值，表明不能使用这些硬币拼出total
            Math.min(Integer.MAX_VALUE+1, Integer.MAX_VALUE);使用过Integer.MAX 会出问题，+1时溢出成了负值
     */
    int coinCompoClassDPSolution(int[] coins, int amount){
        int[] dpArray = new int[amount+1];
        for (int i = 0; i < dpArray.length; i++) {
            dpArray[i] = amount+1;//amount最多需要amount个全是1的coin组成，全初始化成Integer.MAX也可以
        }
        dpArray[0]=0;
        for (int i = 1; i < dpArray.length; i++) {//外层循环遍历状态
            for (int coin : coins){//内层循环遍历一个状态下的几种选择
                if (i - coin < 0) continue;
                dpArray[i] = Math.min(dpArray[i], dpArray[i - coin]+1);//传说中的状态转移方程
            }
        }
        return dpArray[amount] == amount ? -1:dpArray[amount];//dp数组初始化但没用过，在[5,8] 4的情况下会出现
    }
    /*
    算法的一句话描述就是：从0到amount，每个amount减每一种coin，看看前面的结果，直接加1后比较几种coin情况的大小，取最小
    最终得到的dp数组是 0 1 1 2 2 1 2 2 3 3 。。。
     */
    @Test
    public void test133(){
        int[] coins = {1,2,5};//k=coins.length
        int amount = 11;
        //System.out.println(coinCompositeWithDPTable(coins, amount));
        //System.out.println(coinCompoClassDPSolution(coins,amount));
        System.out.println(coinCompoClassDPSolution(coins, amount));
    }
    /*
    计算机解决问题其实唯一的解决办法就是穷举，穷举所有可能性。算法设计无非就是先思考“如何穷举”，然后再追求“如何聪明地穷举”。
    # 动态规划答疑
    最优子结构问题不止在动态规划里存在，许多问题都具有
    如：一个年级有10个班，已知每个年级的最高成绩，求整个年级的最高成绩，就不必再重新遍历全年级每个学生的成绩，只需要在这10个班的最高成绩中取最大即可
        上述问题就符合 最优子结构：可以从子问题的最优解推出更大规模问题的最优结果。但由于这个问题没有重叠子问题，所以用不到动态规划求解
    再比如：一个年级有10个班，已知每个班的最大分数差，求整个年级的最大分数差，这时候就需要遍历全年级的每个学生的成绩了
        上述问题不符合最优子结构的原因是：子问题间不是相互独立的
     */
    /**
      # 最长递增子序列问题
       探讨设计动态规划的通用技巧：数学归纳思想
       Longest Increasing Subsequence,简写LIS，题目内容是：
     ```
     给定一个无序的整数数组，找到其中最长上升子序列的长度
     输入：10，9，2，5，3，7，101，18
     输出：4
     解释：最长上升子序列是[2，3，7，101]
     说明：可能会有多种最长上升字序列的组合，仅输出对应的长度。
          区分子序列和字串：字串是连续的，而子序列不一定
     ```
     */
    /*
     ## O(N^2)的解法
     > 数学归纳法的思路
     nums表示输入数组，dp[i]表示以nums[i]这个数结尾的最长递增子序列的长度。dp[0,1,,,i-1]已解出，如何计算出dp[i]。
     base case：dp[i]初始值为1，因为以nums[i]结尾的最长递增子序列最简单的情况就是包含其自身
     由dp[0,1,,,i-1]求dp[i]的思路：nums[0 ~ i-1]中比nums[i]小的元素index，dp[index]+1的最大值就是dp[i]
    * */
    public int lengthOfLIS(int[] nums) {
        int length = nums.length;
        int[] dpTable = new int[length];
        for (int i = 0; i < length; i++) {
            dpTable[i] = 1;
            int max = 1;
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]){
                    int tmp = dpTable[j]+1;
                    max = max < tmp?tmp:max;
                }
            }
            dpTable[i] = max;
        }
        int result = 0;
        for (int i = 0; i < length; i++) {
            result = result>dpTable[i]?result:dpTable[i];
        }
        return result;
    }
    @Test
    public void test200(){
        int[] nums = {10,9,2,5,3,7,101,18};
        System.out.println(lengthOfLIS(nums));
        //dpTable [1,1,1,2,2,3,4,4]
        System.out.println(lengthOfLISBinSearchSolution(nums));
    }
    /*
     > 总结 如何找到动态规划的状态转移关系
     - 明确dp数组所存数据的含义
     - 根据dp数组的定义，运用数学归纳法，假设dp[0,1,,,i-1]均已求出，想办法求出dp[i]
     如果无法完成第二步，很可能是dp数组定义不够恰当
     */
    /*
     ## 利用二分查找法
     TC = O(NlogN) 来源于一种纸牌游戏patience game，一种排序方法patience sorting(耐心排序)

     */
    int lengthOfLISBinSearchSolution(int[] nums){
        int length = nums.length;
        //直接初始化堆的个数为length个，堆的个数 <= length
        //堆只保留了最小的元素，所以声明为一维数组
        int[] top = new int[length];
        //牌堆数初始化为0
        int piles = 0;
        for (int i = 0; i < length; i++) {
            //要处理的扑克牌
            int poker = nums[i];
            //在所有的牌堆中按左侧边界的二分查找搜索应该放在哪一堆
            int replace = helper(poker, top, piles);
            if (replace == piles) piles++;//需要在右侧新建一个扑克堆
            top[replace] = poker;//统一替换掉top的元素，如果记录top历史就是一个二维数组
        }
        return piles;//最后堆的数量就是最长递增子序列，至于每个堆里有啥不需要
    }
    //poker应该放在从左到右、从小到大第1个比poker大或等于的top[i]上
    //也就是4 应该放在 1 2 5 7 8 11，替换掉5
    //所以需要在二分查找里找到第一个比其大或等于的元素
    int helper(int poker, int[] top, int realLength){
        int left = 0,right = realLength;
        while (left < right){
            int mid = (left + right)/2;

            if (poker < top[mid]){//找到大的应继续往小的方向找，但right不能mid+1,top[mid]有可能就是目标元素
                right = mid;
            }else if (top[mid] < poker){//找到小的肯定要往大的方向试，而且这个小的元素肯定不是目标元素
                left = mid+1;
            }else {
                right = mid;//mid并不是目标位置，在元素相等时还需要在向左试探，但不需要向右试探
            }
        }
        return left;//最后left总会与right相等
    }
    @Test
    public void test269(){
        //二分查找法中的while写法很难，单独提出来作为一个独立的函数调试
        int[] top = {1,2,5,7,8,11};
        int[] top2 = {1,2,4,4,5,7,9};
        System.out.println(helper(10,top2,top2.length));//2
        //上面的helper可以替换成api。。。。只不过拿到结果要处理下
        int index = Arrays.binarySearch(top2, 0, top2.length, 10);
        System.out.println(-index-1);
    }
    /**
     * # 最大子序和
     * 题目见leco # 53
     *
     */
    public int maxSubArray(int[] nums) {
        int max = nums[0], current = nums[0], pre;
        for (int i = 1; i < nums.length; i++) {
            pre = current;
            current = nums[i]+(pre>0?pre:0);
            max = max <= current?current:max;
        }
        return max;
    }
    @Test
    public void test285(){
        int[] nums =    {-2,1,-3,4,-1,2,1,-5,4};
        //dp数组的定义是：以nums[i]结尾的元素最大的连续元素的和
        //状态转移方程是dp[i] = dp[i-1]+(nums[i]>0?nums[i]:0)
        int[] dpTable = {-2,1,-2,4,3 ,5,6,1, 5};
        System.out.println(maxSubArray(nums));
    }

    /** -=-=-=-=-=-= 系列股票问题 =-=-=-=-=-=-= */
    /**
      # 购买股票的最佳时间I
     一支股票在i天的价格为prices[i]
     最多只允许完成一笔交易（即买入和卖出一支股票），计算所能获取的最大利润
     注意不能在买入股票前卖出股票
     {7,1,5,3,6,4} 最大利润为5，在第2天买入，在第5天卖出，6-1=5，注意7-1=6不是答案
     */
    // ## 最简单的O(N^2)穷举算法
    public int maxProfit(int[] prices) {
        int max = 0;
        for (int i = 0; i < prices.length; i++) {
            for (int j = i+1; j < prices.length; j++) {
                int diff = prices[j] - prices[i];
                max = max < diff?diff:max;
            }
        }
        return max;
    }

    /**
     * 前面是通过固定买入的i来逐个计算卖出的j下的利润 TC = O(N^2)
     * 可以先固定卖出的时间（第一次遍历时记录最小值，这样买入的最小值永远是在卖出时间之前），还可以省掉一次遍历，成为O(N)
     */
    public int fromEnd2Start(int[] prices){
        if (prices.length == 0){
            return 0;//提交时发现测试用例还有空数组，所以加了这个判断
        }
        int res = 0;
        int curMin = prices[0];
        for (int i = 1; i < prices.length; i++) {
            curMin = Math.min(curMin, prices[i]);
            res = Math.max(res, prices[i] - curMin);
        }
        return res;
    }
    //上一个方法的改进，似乎Math.min很影响性能
    public int fromEnd2Start2(int[] prices){
        //上面curMin = 0在进行i从0开始的比较时，会出错，比较最小值时的min应该初始化为Integer.MAX,这是常识
        int minprice = Integer.MAX_VALUE;
        int maxprofit = 0;
        for (int i = 0; i < prices.length; i++) {
            if (prices[i] < minprice){
                minprice = prices[i];//当前进来的元素是更小的，就不需要再计算profit
            }else{
                maxprofit = (prices[i] - minprice)>maxprofit?(prices[i]-minprice):maxprofit;
            }
        }
        return maxprofit;
    }

    /*
     ## 关于固定后面少一层遍历的原因
     对于元素集合N{n1,n2,,,ni} 已知其中的最大值，如果从中随机移除一个元素，则最大值需要重新遍历。但如果随机添加一个元素，最大值只需要比较一次。
     这种情况在记录最小值的情况下类似
     */
    @Test
    public void test311(){
        int[] prices = {7,1,5,3,6,4};
        int[] prices2 = {};
        //System.out.println(maxProfit(prices));
        System.out.println(fromEnd2Start2(prices));
    }
    /**
      # 购买股票的最佳时间II
     多次买卖一支股票达到赚取最大利润，必须在再次购买前出售股票
     */
    //按照问题I的穷举暴力解法，问题II的暴力解法是个无穷枚举
    public int stockMultiBuyAndSellFiercely(int[] prices){
        for (int buy = 0; buy < prices.length; buy++) {
            for (int sell = buy+1; sell < prices.length; sell++) {
                //两层for循环只是一次买卖
                if (sell == 1){
                    //0买1卖，此时再从1买(第二次买卖)
                    for (int buy2 = 1; buy2 < prices.length; buy2++) {
                        for (int sell2 = buy2+1; sell2 < prices.length; sell2++) {
                            //第三次买卖再考虑
                        }
                    }
                }
                if (sell == 2){
                    //0买2卖，此时再从2向后买（第二次买卖）
                    for (int buy2 = 2; buy2 < prices.length; buy2++) {
                        for (int sell2 = buy2+1; sell2 < prices.length; sell2++) {
                            //第三次买卖
                        }
                    }
                }
                // ... if的所有情况还要继续列举下去
            }
        }
        return 0;
    }
    //上述暴力解法在测试用例无限延长时就成了无穷穷举，但测试用例不会真的无限，而且在一次买卖后问题规模总是变小了，所以可以使用递归写出暴力解
    public int stockMultiBuyAndSellReversely(int[] prices){
        int result = 0;
        for (int buy = 0; buy < prices.length; buy++) {
            for (int sell = buy+1; sell < prices.length; sell++) {
                int[] newPrices = Arrays.copyOfRange(prices, sell, prices.length);
                int profitFromBottom = stockMultiBuyAndSellReversely(newPrices);
                int currentProfit = profitFromBottom + (prices[sell] - prices[buy]);
                //取第二层for循环中的最大值
                result = result>currentProfit?result:currentProfit;
            }
        }
        return result;
    }
    //对递归写法进一步优化
    public int improvedMultiBuyAndSell(int[] prices){
        int[] memo = new int[prices.length];
        helper4ImprovedMultiBuyAndSell(0,memo,prices);
        return memo[0];
    }//递归都可以加个备忘录做个判断，以减少计算
    private int helper4ImprovedMultiBuyAndSell(int start, int[] memo, int[] prices){
        if (start >= prices.length) return 0;
        if (memo[start] != 0) return memo[start];
        int res = 0;
        int curMin = prices[start];
        for (int sell = start + 1; sell < prices.length; sell++) {
            //记录各种卖出情况前的最小价格作为买入价格，以便计算最大收益，这样可以省略掉一层遍历，跟股票问题1的改进方案一致
            curMin = curMin < prices[sell] ? curMin : prices[sell];
            //改变遍历的起始点，相当于prices截短了
            int tmp = helper4ImprovedMultiBuyAndSell(sell+1, memo, prices) + prices[sell] - curMin;
            res = res > tmp?res:tmp;
        }
        memo[start] = res;
        return res;
    }

    /*
     上述依然无法通过全部测试用例，在prices很长时会因递归深度导致内存溢出
     递归深度来自于memo的大小dp(0)依赖dp(1)或dp(2)或dp(3)...的结果，不像fibonacci可以依赖前两个结果
     */

    /**
     * 贪心算法
     * 基于动态规划之上的一种特殊方法,既然可以预知未来（知道下一个时间结点的股价）能赚就多赚点
     * 只要下一个时间点比现在股价高，就进行一次买入卖出
     */
    public int greedySolutionInMaxProfit(int[] prices){
        int maxProfit = 0;
        for (int i = 1; i < prices.length; i++) {
             if (prices[i] > prices[i-1]){
                 maxProfit += prices[i] - prices[i-1];
             }
        }
        return maxProfit;
    }
    @Test
    public void test367(){
        int[] prices = {7,1,5,3,6,4};//7:1-5,3-6
        int[] prices2 = {1,2,3,4,5};//4:1-5
        int[] prices3 = {7,6,4,3,1};//0
        int result = improvedMultiBuyAndSell(prices);
        System.out.println(result);
    }

    /**
      购买股票的最佳时间III
        限定最大交易数，一次交易分为买入卖出
     test case 1: [2,4,1] times=2 maxprofit = 2
     test case 2: [3,2,6,5,0,3] times=2
     */
    public static int[] huge_test_case = {397,6621,4997,7506,8918,1662,9187,3278,3890,514,18,9305,93,5508,3031,2692,6019,1134,1691,4949,5071,799,8953,7882,4273,302,6753,4657,8368,3942,1982,5117,563,3332,2623,9482,4994,8163,9112,5236,5029,5483,4542,1474,991,3925,4166,3362,5059,5857,4663,6482,3008,3616,4365,3634,270,1118,8291,4990,1413,273,107,1976,9957,9083,7810,4952,7246,3275,6540,2275,8758,7434,3750,6101,1359,4268,5815,2771,126,478,9253,9486,446,3618,3120,7068,1089,1411,2058,2502,8037,2165,830,7994,1248,4993,9298,4846,8268,2191,3474,3378,9625,7224,9479,985,1492,1646,3756,7970,8476,3009,7457,8922,2980,577,2342,4069,8341,4400,2923,2730,2917,105,724,518,5098,6375,5364,3366,8566,8838,3096,8191,2414,2575,5528,259,573,5636,4581,9049,4998,2038,4323,7978,8968,6665,8399,7309,7417,1322,6391,335,1427,7115,853,2878,9842,2569,2596,4760,7760,5693,9304,6526,8268,4832,6785,5194,6821,1367,4243,1819,9757,4919,6149,8725,7936,4548,2386,5354,2222,8777,2041,1,2245,9246,2879,8439,1815,5476,3200,5927,7521,2504,2454,5789,3688,9239,7335,6861,6958,7931,8680,3068,2850,1181,1793,7138,2081,532,2492,4303,5661,885,657,4258,131,9888,9050,1947,1716,2250,4226,9237,1106,6680,1379,1146,2272,8714,8008,9230,6645,3040,2298,5847,4222,444,2986,2655,7328,1830,6959,9341,2716,3968,9952,2847,3856,9002,1146,5573,1252,5373,1162,8710,2053,2541,9856,677,1256,4216,9908,4253,3609,8558,6453,4183,5354,9439,6838,2682,7621,149,8376,337,4117,8328,9537,4326,7330,683,9899,4934,2408,7413,9996,814,9955,9852,1491,7563,421,7751,1816,4030,2662,8269,8213,8016,4060,5051,7051,1682,5201,5427,8371,5670,3755,7908,9996,7437,4944,9895,2371,7352,3661,2367,4518,3616,8571,6010,1179,5344,113,9347,9374,2775,3969,3939,792,4381,8991,7843,2415,544,3270,787,6214,3377,8695,6211,814,9991,2458,9537,7344,6119,1904,8214,6087,6827,4224,7266,2172,690,2966,7898,3465,3287,1838,609,7668,829,8452,84,7725,8074,871,3939,7803,5918,6502,4969,5910,5313,4506,9606,1432,2762,7820,3872,9590,8397,1138,8114,9087,456,6012,8904,3743,7850,9514,7764,5031,4318,7848,9108,8745,5071,9400,2900,7341,5902,7870,3251,7567,2376,9209,9000,1491,7030,2872,7433,1779,362,5547,7218,7171,7911,2474,914,2114,8340,8678,3497,2659,2878,2606,7756,7949,2006,656,5291,4260,8526,4894,1828,7255,456,7180,8746,3838,6404,6179,5617,3118,8078,9187,289,5989,1661,1204,8103,2,6234,7953,9013,5465,559,6769,9766,2565,7425,1409,3177,2304,6304,5005,9559,6760,2185,4657,598,8589,836,2567,1708,5266,1754,8349,1255,9767,5905,5711,9769,8492,3664,5134,3957,575,1903,3723,3140,5681,5133,6317,4337,7789,7675,3896,4549,6212,8553,1499,1154,5741,418,9214,1007,2172,7563,8614,8291,3469,677,4413,1961,4341,9547,5918,4916,7803,9641,4408,3484,1126,7078,7821,8915,1105,8069,9816,7317,2974,1315,8471,8715,1733,7685,6074,257,5249,4688,8549,5070,5366,2962,7031,6059,8861,9301,7328,6664,5294,8088,6500,6421,1518,4321,5336,2623,8742,1505,9941,1716,2820,4764,6783,906,2450,2857,7515,4051,7546,2416,9121,9264,1730,6152,1675,592,1805,9003,7256,7099,3444,3757,9872,4962,4430,1561,7586,3173,3066,3879,1241,2238,8643,8025,3144,7445,882,7012,1496,4780,9428,617,396,1159,3121,2072,1751,4926,7427,5359,8378,871,5468,8250,5834,9899,9811,9772,9424,2877,3651,7017,5116,8646,5042,4612,6092,2277,1624,7588,3409,1053,8206,3806,8564,7679,2230,6667,8958,6009,2026,7336,6881,3847,5586,9067,98,1750,8839,9522,4627,8842,2891,6095,7488,7934,708,3580,6563,8684,7521,9972,6089,2079,130,4653,9758,2360,1320,8716,8370,9699,6052,1603,3546,7991,670,3644,6093,9509,9518,7072,4703,2409,3168,2191,6695,228,2124,3258,5264,9645,9583,1354,1724,9713,2359,1482,8426,3680,6551,3148,9731,8955,4751,9629,6946,5421,9625,9391,1282,5495,6464,5985,4256,5984,4528,952,6212,6652,562,1476,6297,145,9182,8021,6211,1542,5856,4637,1574,2407,7785,1305,1362,2536,934,4661,4309,559,4052,1943,2406,516,4280,6662,2852,8808,7614,9064,1813,4529,6893,8110,4674,2427,2484,7237,3969,8340,1874,5543,7099,6011,3200,8461,8547,486,9474,9208,7397,9879,7503,9803,6747,1783,6466,9600,6944,432,8664,8757,4961,1909,6867,5988,4337,5703,3225,4658,4043,1452,6554,1142,7463,9754,5956,2363,241,1782,7923,7638,1661,5427,3794,8409,7210,260,8009,4154,692,3025,9263,2006,4935,2483,7994,5624,8186,7571,282,8582,9023,6836,6076,6487,6591,2032,8850,3184,3815,3125,7174,5476,8552,968,3885,2115,7580,8246,2621,4625,1272,1885,6631,6207,4368,4625,8183,2554,8548,8465,1136,7572,1654,7213,411,4597,5597,5613,7781,5764,8738,1307,7593,7291,8628,7830,9406,6208,6077,2027,833,7349,3912,7464,9908,4632,8441,8091,7187,6990,2908,4675,914,4562,8240,1325,9159,190,6938,3292,5954,2028,4600,9899,9319,3228,7730,5077,9436,159,7105,6622,7508,7369,4086,3768,2002,8880,8211,5541,2222,1119,216,3136,5682,4809,813,1193,4999,4103,4486,7305,6131,9086,7205,5451,2314,1287,528,8102,1446,3985,4724,5306,1355,5163,9074,9709,4043,7285,5250,2617,4756,1818,2105,6790,6627,2918,7984,7978,7021,2470,1636,3152,7908,8841,4955,222,6480,5484,4676,7926,5821,9401,3232,7176,916,8658,3237,1311,5943,8487,3928,7051,306,6033,3842,3285,8951,1826,7616,2324,648,9252,5476,8556,4445,6784};

    static class BestTime2BuyStock{
        public static int maxProfit(int[] prices, int times){
            return helper(prices, 0, times);
        }
        public static int helper(int[] prices, int start2buy, int times){
            if (start2buy >= prices.length) return 0;
            if (times == 0 || start2buy == prices.length - 1) return 0;
            int max = Integer.MIN_VALUE;
            for (int i = start2buy; i < prices.length; i++) {
                for (int j = i+1; j < prices.length; j++) {
                    int currProfit = prices[j] - prices[i];
                    int bottomProfit = helper(prices, j, times - 1);
                    int profit = currProfit + bottomProfit;
                    if (max < profit) max = profit;
                }
            }
            return max;
        }
        /*
         限制购买次数 在递归方案中仅对递归深度有影响
         限制购买次数显然是对问题II的贪心法的针对，频繁无脑购买增长线的确可以得到maxProfit
         上述递归的一个优化方案是添加 以start2Buy,times为key的map 作为备忘录
         进一步优化是固定卖出时间计算获利
         */
        static class MemoKey{
            private Integer start2buy;
            private Integer times;

            public MemoKey(Integer start2buy, Integer times) {
                this.start2buy = start2buy;
                this.times = times;
            }

            public Integer getStart2buy() {
                return this.start2buy;
            }

            public Integer getTimes() {
                return this.times;
            }

            public void setStart2buy(Integer start2buy) {
                this.start2buy = start2buy;
            }

            public void setTimes(Integer times) {
                this.times = times;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                MemoKey memoKey = (MemoKey) o;

                if (!start2buy.equals(memoKey.start2buy)) return false;
                return times.equals(memoKey.times);
            }

            @Override
            public int hashCode() {
                int result = start2buy.hashCode();
                result = 31 * result + times.hashCode();
                return result;
            }
        }
        public static int helper2(int[] prices, int start2buy, int times){
            if (start2buy >= prices.length) return 0;
            if (times == 0 || start2buy == prices.length - 1) return 0;
            Map<MemoKey, Integer> memo = new HashMap<>();//hash表做备忘录
            MemoKey memoKey = new MemoKey(start2buy, times);
            if (memo.containsKey(memoKey)) return memo.get(memoKey);
            int maxProfit = Integer.MIN_VALUE;
            //固定一次卖出的次数，所以要记先前可买入的最低点
            int min = Integer.MAX_VALUE;
            for (int i = start2buy; i < prices.length; i++) {
                min = Math.min(min, prices[i]);
                int currProfit = prices[i] - min;//有可能为0
                int bottomProfit = helper2(prices, i, times-1);
                maxProfit = Math.max(currProfit + bottomProfit, maxProfit);
            }
            return maxProfit;
        }

        public static void main(String[] args) {
            int[] prices0 = {7,6,4,3,1};
            int[] prices = {3,2,6,5,0,3,9,6,15};
            System.out.println(helper2(prices0, 0, 2));
        }
    }


    /**
     购买股票的最佳时间IV
        带资金冻结1天（卖出股票后，无法在第二天买入股票），不限交易次数
     test case 1：[1,2,3,0,2] output=3
     */
    /**
     * 股票问题V
     * 带固定手续费、不限次数的股票交易
     */
    //相关解法见_01DPRecite.java
    /**-=-=-=-=-=- 股票问题的状态机解法 -=-=-=-=-=-=-=*/





    /*-=-=-=-=-=-=-=-=- 系列背包问题 -=-=-=-=-*/
    /**
     * # 0-1背包问题
     * 0-1的含义就是物品不可分割，要么放进背包，要么不放
     * 一个可装载重量为W的背包和N个物品，每个物品有重量和价值两个属性。
     * 其中第i个物品的重量 wt[i]，价值为val[i]，现在让你用这个背包装物品，最多能装的价值是多少？
     */
    /*
    ## 分析
    一维动态规划就是：第i个物品的可放入的总价值，在没有重量限制的情况下就是前一个i-1判读结果加上当前
    二维动态规划就是：第i个物品要放，看第i-1个物品在总容量为减去当前物品后余量的情况下的最大价值，加上当前价值。
                    第i个物品不放，取第i-1个物品的最大价值
                    比较上述两种情况，作为当前最大价值
    此问题中
    「状态」有两个：背包容量、可选择物品，所以dp数组是一个二维数组。
    「选择」有两个：装进背包和不装
        dp[i][j]定义：对当前第i个物品，背包总容量为j，可以装入的最大价值。

     「base case」：dp[0][...] = dp[...][0] = 0
     将第i个物品装入背包：dp[i][j] = dp[i-1][j-wt[i-1]] + val[i-1]
     第i个物品不放入背包：dp[i][j] = dp[i-1][j]
     这样先写出dpTable
       0 1 2 3 第i个物品
     0 0 0 0 0
     1 0 0 2 2 二维数组表示在试验到第i个物品，总容量是j的情况下的最大容量
     2 0 4 4 4
     3 0 4 6 6
     4 0 4 6 6 背包总容量
     */
    public int bag0to1(int N, int W, int[] wt, int[] val){
        int[][] dp = new int[N+1][W+1];//index会从0到N,0表示什么都不装，所以数组初始行数是N+1
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= W; j++) {
                //先判断当前物品是否直接超过总容量
                if (j-wt[i-1] < 0){
                    dp[i][j] = dp[i-1][j];
                }else {
                    //要放进这个物品：总容量减去当前要放进的物品的质量，去查dpTable，加上当前物品的价值
                    //不放进此物品：取上一个物品的最大val判断结果
                    //对上述两个值做判断，取最大
                    dp[i][j] = Math.max(dp[i-1][j-wt[i-1]]+val[i-1],dp[i-1][j]);
                }
            }
        }
        return dp[N][W];
    }
    @Test
    public void test307(){
        int N = 3,W = 4;
        int[] wt = {2,1,3};
        int[] val = {4,2,3};
        //dp[i] 第i个物品
        System.out.println(bag0to1(N,W,wt,val));
    }



}
