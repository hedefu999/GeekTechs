package com.javalearning.leetcode;

import java.util.Arrays;

/**
【参考资料】
 动态规划解题套路框架 https://labuladong.github.io/algo/di-ling-zh-bfe1b/dong-tai-g-1e688/

 动态规划（Dynamic Programming）
 动态规划问题的一般形式就是求最值，是运筹学的一种最优化方法。求解动态规划的核心问题是穷举
 动态规划三要素：
 列出正确的"状态转移方程"，通过递归进行穷举；
 需要判断算法是否具备"最优子结构"，通过子问题的最值得到原问题的最值；
 动态规划存在"重叠子问题"，暴力穷举效率很低，需要使用备忘录/DP table来优化穷举过程，提交效率；

 */
public class _06_DP_Primary {
//region 动态规划启蒙 - 斐波那契数列
/* LC509 Fibonacci Number 斐波那契数列
画出递归树
f(5) - f(4) - f(3) - f(2)
                   - f(1)
            - f(2)
     - f(3) - f(2)
            - f(1)
递归算法的时间复杂度计算：子问题个数乘以解决一个子问题需要的时间
上述递归树 f(3) 分支存在两次重复计算，比较低效
添加备忘录后，递归树就简化成了
f(5) - f(4) - f(3) - f(2) - f(1)
*/
static int fib(int n) {
//    return dpLC509(n);
    int[] memo = new int[n+1];
    return dp2LC509(memo,n);
}
//算法时间复杂度 O(2^n) 递归树节点总数为指数级别，子问题个数为O(2^n)，解决一个子问题只需要 f(n-1)+f(n-2) 两数相加，时间O(1)
static int dpLC509(int n){
    if (n == 1 || n == 2) return 1;
    return dpLC509(n-1) + dpLC509(n-2);
}
// 使用备忘录 int[] memo 解决子问题重复计算问题，时间复杂度是O(n)
static int dp2LC509(int[] memo, int n){
    if (n == 1 || n == 2) return 1;
    //避免子问题重复计算
    if (memo[n] != 0) return memo[n];
    memo[n] = dp2LC509(memo, n-1) + dp2LC509(memo, n-2);
    return memo[n];
}
//上述解法采用递归的方式，自顶向下，实际的动态规划大多采用自底向上的 "递推" 解法，不需要进行递归
static int dp3LC509(int[] memo, int n){
    for (int i = 2; i <= n; i++) {
        memo[i] = memo[i-1] + memo[i-2];
    }
    return memo[n];
}
/*
据此引出状态转移方程
f(n) = 1; n=1,2
     = f(n-1)+f(n-2); n>2
*/
//继续将空间复杂度降至1
//在实际动态规划中，如果发现每个子计算只需要备忘录中的很小一部分，就可以这样将SC降至O(1)
static int dp4LC509(int n){
    if (n==0 || n==1) return n;
    int i_1 = 1, i_2 = 0;
    for (int i = 2; i <= n; i++) {
        int dp_i = i_1 + i_2;
        i_2 = i_1;
        i_1 = dp_i;
    }
    return i_1;
}

//endregion

//region 动态规划入门
/** LC322 凑零钱问题
[1,2,5] - 11: 3(5,5,1)
排列组合的回溯算法可以得到所有组合成11的零钱方案，甚至限制同币值硬币数量也能很容易实现
但要求使用最小的TC、SC找到最优解就不能采用回溯算法
这里仍然画出回溯树以便跟DP树对比
1 - 1 - 1 ... 每一层都是1，2，5
      - 2
      - 5
  - 2
  - 5
-=-=-=-=-= DP树 =-=-=-=-=-
                      3<- 3 <-2 <-2 <-1<- 1
11 - 10 - 9 - 8 - 7 - 6 - 5 - 4 - 3 - 2 - 1 - +
                                        - +
                                    - 1*
                                - 2
                            - 3
                            +
                        - 4
                        - 1 +
                    - 5
                    - 2
                - 6
                - 3
            - 7
            - 4
        - 8
        - 5
   - 9
   - 6
上述是横向三叉树，反映了递归加法的堆栈过程，在不断减1的过程 通过深度优先直接递归到凑 1元需要几个硬币
在堆栈返回的过程中不断填充dp备忘录（从右向左），这样进入第一层分叉时比较 10 9 6 凑硬币，9 6两个分支完全不需要递归深入了
时间复杂度:通过备忘录进行了有效剪枝，每次开支 k（硬币种类数），递归深度 n （目标金额），共有 n个子问题要处理，每次for循环k次比较得出结果（最坏），所以TC=O(kn)

上述是自顶向下的递归思路，如果采用自底向上的迭代思路,需要两层for循环：
1   2   3   4
|
coins
【测试用例】
 int[] coins = {1,2,5};
 int[] coins2 = {2};
 int[] coins3 = {1};
 int[] coins4 = {2,5,10,1};
 int[] coins5 = {186,419,83,408};
 int[] coins6 = {1,3,6,7};
 int[] coins7 = {1,5,2};
 System.out.println(coinChangeIterator(coins,11));//3
 System.out.println(coinChangeIterator(coins7,11));//3
 System.out.println(coinChangeIterator(coins2,3));//-1
 System.out.println(coinChangeIterator(coins3,0));//0
 System.out.println(coinChangeIterator(coins4,27));//4
 System.out.println(coinChangeIterator(coins5,6249));//20
 System.out.println(coinChangeIterator(coins6,25));//4
 System.out.println(coinChangeIterator(coins2,0));
*/
static int coinChangeStack(int[] coins, int amount) {
    int[] dp = new int[amount+1];
    return dpStackLC32(coins, amount, dp);
}
/*
细节问题比较麻烦
特殊情形：凑足0要返回0，凑不出来要返回-1
dp数组针对每个amount的取值：能凑出来肯定是个 >0 的整数，凑不出来是-1，还没计算到就是初始的0
*/
static int dpStackLC32(int[] coins, int amount, int[] dp){
    //刚好凑出或者一开始amount就是0
    if (amount == 0) return 0;
    //使用dp数组剪枝，能确定结果的有两种情形：能凑出(>0)，不能凑出(<0),都要及时返回
    if (dp[amount] > 0) return dp[amount];
    if (dp[amount] < 0) return -1;
    int min = amount+1;//使用MAX_INTEGER会导致整数溢出，使用不可能的值
    for (int i = 0; i < coins.length; i++) {
        int leftAmount = amount - coins[i];
        if (leftAmount < 0) continue;
        int leftAmtCoinCount = dpStackLC32(coins, leftAmount, dp);
        if (leftAmtCoinCount >= 0){
            min = Math.min(min, 1 + leftAmtCoinCount);
        }
    }
    dp[amount] = ( min == (amount+1) ) ? -1 : min;
    return dp[amount];
}
static int coinChangeIterator(int[] coins, int amount) {
//    if (amount == 0) return 0;
//    Arrays.sort(coins);
    int[] dp = new int[amount+1];
    return dpIteratorLC32(coins, amount, dp);
}
static int dpIteratorLC32(int[] coins, int amount, int[] dp){
    for (int currentAmt = 1; currentAmt <= amount; currentAmt++) {
        dp[currentAmt] = amount+1;//防止后面math.min出错
        for (int coin : coins) {
            int nextAmt = currentAmt - coin;
            if (nextAmt == 0){
                dp[currentAmt] = 1;
                break;
            }
            if (nextAmt < 0) continue;
            if (dp[nextAmt] > 0){
                dp[currentAmt] = Math.min(dp[currentAmt], 1 + dp[nextAmt]);
            }
        }
        dp[currentAmt] = (dp[currentAmt] == (amount+1)) ? -1 : dp[currentAmt];
    }
    return dp[amount];
}
//自己写的总是有很多if-else，细节问题优化好了代码会很简洁，但。。。
static int coinChange(int[] coins, int amount) {
    if (amount == 0) return 0;
    int[] dp = new int[amount+1];
    Arrays.fill(dp, amount+1);dp[0]=0;
    return dpLC32(coins, amount, dp);
}
static int dpLC32(int[] coins, int amount, int[] dp){
    for (int currentAmt = 1; currentAmt <= amount; currentAmt++) {
        for (int coin : coins) {
            int nextAmt = currentAmt - coin;
            if (nextAmt < 0) continue;
            dp[currentAmt] = Math.min(dp[currentAmt], 1+dp[nextAmt]);
        }
    }
    return (dp[amount] == amount +1)?-1:dp[amount];
}
//endregion

//region 动态规划应用 - LC121\LC122\LC123\LC188\LC309\LC714 股票买卖的最佳时机，LC188 可以简化成其他股票问题）
/**
 股票价格 price[i] 经过 k 次交易所能获得的最大利润
 每次交易要操作全部，全部卖出前不能买入，一次交易分为买入-卖出，相当于只能右侧低吸高抛
 2,[2,4,1] -> 2
 2,[3,2,6,5,0,3] -> 7
    0 1 2 3 4 5
 股票交易问题的状态有 3 个

 状态转移方程
 先分析出状态：交易日i、至今至多进行的交易次数k、股票是否持有0-1 -> dp[i][k][0/1],注意k不是已交易次数
 对于长度n的数组 price[i] 至多进行K次交易的最大利润，就是求 dp[n-1][K][0]
 第i天卖出股票的最大盈利 dp[i][k][0] = 前一天(i-1)未持有股票的盈利，今天无操作 VS 前一天(i-1)持有了股票，今天卖出
                          = max(dp[i-1][k][0], dp[i-1][k][1] + price[i])
 同样的，第i天持有股票的最大盈利 dp[i][k][1] = max(dp[i-1][k][1], dp[i-1][k-1][0]-price[i])
 所以得到解题框架八股文
 int[][][] dp = new int[prices.length][k][2];
 for (int i = 0; i < prices.length; i++) {
     for (int j = 0; j < k; j++) {
         dp[i][k][0] = Math.max(dp[i-1][k][0], dp[i-1][k][1] + prices[i]);
         dp[i][k][1] = Math.max(dp[i-1][k][1], dp[i-1][k-1][0] - prices[i]);
     }
 }

 LC121 k=1,至多进行1次交易的最大盈利
 [7,1,5,3,6,4] -> 5
 [7,6,4,3,1] -> 0
 由于k=1,三维dp数组可以简化降1维
 */
static int maxProfit(int[] prices) {
    /*
    在解之前需要降维，简单点
    k=0时的dp三维数组都是0，问题简化为k值固定1，这个维度可以去掉
    dp数组定义：价格数组prices下至多进行1次交易的最大盈利
    dp[i][0] = max(dp[i-1][0], dp[i-1][1] + prices[i])
    dp[i][1] = max(dp[i-1][1], 0 - prices[i])
    注意 dp[i-1][k-1][0] 在 k 确定为1时值为0
    * */
    int[][] dp = new int[prices.length][2];
    dp[0][0]=0; dp[0][1]=-prices[0];
    for (int i = 1; i < prices.length; i++) {
        dp[i][0] = Math.max(dp[i-1][0], dp[i-1][1] + prices[i]);
        dp[i][1] = Math.max(dp[i-1][1],  - prices[i]);
        System.out.printf("第%d天空仓收益%d，满仓收益%d\n",i,dp[i][0],dp[i][1]);
    }
    return dp[prices.length-1][0];
}
/*
通常动态规划的优化路径是：符合常人思路的逆推递归解法（O(n)） -> 添加备忘录的正向迭代解法 -> 备忘录简化为有限个变量只记录前后几个（SC降至O(1)）
 */
static int maxProfit2(int[] prices){
    int i0,i1,im10=0;int im11=-prices[0];
//    int[][] dp = new int[prices.length][2];
//    dp[0][0]=0; dp[0][1]=-prices[0];
    for (int i = 1; i < prices.length; i++) {
//        dp[i][0] = Math.max(dp[i-1][0], dp[i-1][1] + prices[i]);
//        dp[i][1] = Math.max(dp[i-1][1],  - prices[i]);
        im10 = Math.max(im10, im11+prices[i]);
        im11 = Math.max(im11, -prices[i]);
    }
    return im10;
}
/** LC122 买卖股票的最佳时机,不限交易次数
 [7,1,5,3,6,4] -> 7
 [1,2,3,4,5] -> 4
 [7,6,4,3,1] -> 0
此题降维思路（不降维也能解，底下就是）
 不限交易次数，甚至当天买当天卖也可以，盈利是0，所以可以认为 k->无穷大，k 与 k-1 近似，所以
 dp[i][k][0] = Math.max(dp[i-1][k][0], dp[i-1][k][1] + prices[i]);
 dp[i][k][1] = Math.max(dp[i-1][k][1], dp[i-1][k-1][0] - prices[i]);
             = Math.max(dp[i-1][k][1], dp[i-1][k][0] - prices[i])
 ====>
 dp[i][0] = Math.max(dp[i-1][0], dp[i-1][1] + prices[i]);
 dp[i][1] = Math.max(dp[i-1][1], dp[i-1][0] - prices[i]);
 */
static int maxProfitII(int[] prices){
    int[][][] dp = new int[prices.length][prices.length/2][2];
    for (int i = 0; i < prices.length/2; i++) {
        dp[0][i][0] = 0;
        dp[0][i][1] = -prices[0];
    }
    for (int i = 1; i < prices.length; i++) {
        for (int k = 1; k < (prices.length/2); k++) {
            dp[i][k][0] = Math.max(dp[i-1][k][0], dp[i-1][k][1] + prices[i]);
            dp[i][k][1] = Math.max(dp[i-1][k][1], dp[i-1][k-1][0] - prices[i]);
        }
    }
    return dp[prices.length-1][prices.length/2-1][0];
}
static int maxProfitII2(int[] prices){
    int im1k0=0 ,im1k1=0,im1km10=-prices[0];
//    int[][][] dp = new int[prices.length][prices.length/2][2];
//    for (int i = 0; i < prices.length/2; i++) {
//        dp[0][i][0] = 0;
//        dp[0][i][1] = -prices[0];
//    }
    for (int i = 1; i < prices.length; i++) {
        for (int k = 1; k < (prices.length/2); k++) {
            im1k0 = Math.max(im1k0, im1k1+prices[i]);
            im1k1 = Math.max(im1k1, im1km10 - prices[i]);
//            dp[i][k][0] = Math.max(dp[i-1][k][0], dp[i-1][k][1] + prices[i]);
//            dp[i][k][1] = Math.max(dp[i-1][k][1], dp[i-1][k-1][0] - prices[i]);
        }
    }
    return im1k0;//dp[prices.length-1][prices.length/2-1][0];
}
/** LC309 最佳买卖股票时机含冷冻期
 不限交易次数，但卖后有冷静期1天
 [1,2,3,0,2] -> 3
 [1] -> 0
 冷静期1天，需要修改状态转移方程
 dp[i][1]=Math.max(dp[])
 由于sell后有冷静期，buy前就要保证前一天是空仓(往前看，不是往后看)，所以修改的是 dp[i][1] 而不是 dp[i][0]
 之前的错误：dp[i][0]=Math.max(dp[i-1][0], dp[i-2][1] + prices[i])
 测试用例：
 {1,2,3,0,2} - 3
 {1,2,5,0,8} - 9
 {1,2} - 1
 {1} - 0
 */
static int maxProfitIII(int[] prices) {
    if (prices.length <= 1) return 0;
    int[][] dp = new int[prices.length][2];
    dp[0][0]=0;dp[0][1]=-prices[0];
    dp[1][0]=Math.max(0, prices[1]-prices[0]);
    dp[1][1]=Math.max(-prices[0],-prices[1]);
    for (int i = 2; i < prices.length; i++) {
        dp[i][0] = Math.max(dp[i-1][0], dp[i-1][1] + prices[i]);
        dp[i][1] = Math.max(dp[i-1][1], dp[i-2][0] - prices[i]);
    }
    return dp[prices.length-1][0];
}
/** LC714 股票最大收益-含手续费
一次交易只收一次手续费,不限交易次数
 测试用例：
 2,{1,3,2,8,4,9} - 8  （1-8,4-9 交易盈利最多）
 3,{1,3,7,5,10,3} - 6 (1-10 盈利比两次手续费的 1-7,5-10盈利多)
 */
static int maxProfitIV(int[] prices, int fee) {
    int[][] dp = new int[prices.length][2];
    dp[0][0] = 0; dp[0][1]=-prices[0];
    for (int i = 1; i < prices.length; i++) {
        //手续费放在sell环节考虑
        dp[i][0] = Math.max(dp[i-1][0], dp[i-1][1] + prices[i] - fee);
        //如果将手续费放在buy环节考虑就要 dp[i-1][0] - prices[i] - fee
        dp[i][1] = Math.max(dp[i-1][1], dp[i-1][0] - prices[i]);
    }
    return dp[prices.length-1][0];
}
/** LC123 股票至多交易两次
 测试用例
 {3,3,5,0,0,3,1,4} -> 6
 {1,2,3,4,5} -> 4
 {7,6,4,3,1} - 0
 {1} - 0
 */
static int maxProfitV(int[] prices) {
    int[][][] dp = new int[prices.length][2+1][2];
    dp[0][0][0] = 0;dp[0][0][1]=0;
    //注意base case是 i=0时所有j的情形
    dp[0][1][0] = 0;dp[0][1][1]=-prices[0];
    dp[0][2][0] = 0;dp[0][2][1]=-prices[0];
    for (int i = 1; i < prices.length; i++) {
//        for (int j = 1; j < 2+1; j++) {
//            dp[i][j][0] = Math.max(dp[i-1][j][0], dp[i-1][j][1] + prices[i]);
//            dp[i][j][1] = Math.max(dp[i-1][j][1], dp[i-1][j-1][0] - prices[i]);
//        }
        //上述for循环，观察可见 i 只与 i-1 的状态有关，ij不需要看i(j-1)的值
        //所以for循环中j可以从大到小遍历
        for (int j = 2; j > 0; j--) {
            dp[i][j][0] = Math.max(dp[i-1][j][0], dp[i-1][j][1] + prices[i]);
            dp[i][j][1] = Math.max(dp[i-1][j][1], dp[i-1][j-1][0] - prices[i]);
        }//j从2减小也符合人的思路：刚开始交易时 j = 2，还剩2次交易机会
    }
    return dp[prices.length-1][2][0];
}
/*
j是有限的2，还是可以简化状态转移方程的，全部列出即可
j=2
dp[i][2][0] = Math.max(dp[i-1][2][0], dp[i-1][2][1]+prices[i]);
dp[i][2][1] = Math.max(dp[i-1][2][1], dp[i-1][1][0]-prices[i]);
j=1
dp[i][1][0] = Math.max(dp[i-1][1][0], dp[i-1][1][1]+prices[i]);
dp[i][1][1] = Math.max(dp[i-1][1][1], dp[i-1][0][0]-prices[i]);
            = Math.max(dp[i-1][1][1], -prices[i]);
此时只有一个变量i了，继续按优化SC的思路简化
dp[i][2][0] 可以视作 dp[i-1][2][0]的更新后值，两者使用同一个变量
dp_2_0 = Math.max(dp_2_0, dp_2_1+prices[i]);
dp_2_1 = Math.max(dp_2_1, dp_1_0-prices[i]);
dp_1_0 = Math.max(dp_1_0, dp_1_1+prices[i]);
dp_1_1 = Math.max(dp_1_1, -prices[i]);

* */
static int maxProfitV2(int[] prices) {
    int dp_1_0 = 0;int dp_1_1=Integer.MIN_VALUE;
    int dp_2_0=0;int dp_2_1=Integer.MIN_VALUE;//由于要max，可能出现赋值，所以起始值是Integer.MIN_VALUE
    for (int i = 0; i < prices.length; i++) {//4个表达式的顺序可以调整
        dp_1_1 = Math.max(dp_1_1, -prices[i]);
        dp_1_0 = Math.max(dp_1_0, dp_1_1+prices[i]);
        dp_2_0 = Math.max(dp_2_0, dp_2_1+prices[i]);
        dp_2_1 = Math.max(dp_2_1, dp_1_0-prices[i]);
    }
    return dp_2_0;
}
/** LC188 买卖股票的最佳时机-k值不定
k=2,{2,4,1} -> 2
k=2,{3,2,6,5,0,3} -> 7
 {3,3,5,0,0,3,1,4}
 三维数组在k值很大时会内存超限，需要简化
 状态方程的逻辑不包含当天买入当天卖出，所以k至多n/2,当k超过这个值时，就可以简化为不限交易次数的场景
 */
static int maxProfitCommon(int k, int[] prices) {
    if (prices.length == 0) return 0;
    //简化为不限交易次数的问题
    if (k >= prices.length/2){
        return maxProfitWithInfinityK(prices);
    }
    int[][][] dp = new int[prices.length][k+1][2];
    for (int i = 0; i <= k; i++) {
        dp[0][i][0]=0; dp[0][i][1]=-prices[0];
    }
    for (int i = 0; i < prices.length; i++) {
        dp[i][0][0]=0; dp[i][0][1]=Integer.MIN_VALUE;
    }
    for (int i = 1; i < prices.length; i++) {
        for (int j = k; j >= 1; j--) {
            // j-1 究竟放在 -prices[i] (买入)还是卖出时有所讲究
            //今天卖出时前天交易次数才减1得到的计算结果错误，结果总是 k-1 的最大利润
            //dp[i][j][1] = Math.max(dp[i-1][j][1], dp[i-1][j][0]-prices[i]);
            //dp[i][j][0] = Math.max(dp[i-1][j][0], dp[i-1][j-1][1]+prices[i]);
            //今天买入时，前一天的最大交易次数就要 - 1
            dp[i][j][1] = Math.max(dp[i-1][j][1], dp[i-1][j-1][0]-prices[i]);
            dp[i][j][0] = Math.max(dp[i-1][j][0], dp[i-1][j][1]+prices[i]);

        }
    }
    return dp[prices.length-1][k][0];
}
private static int maxProfitWithInfinityK(int[] prices){
    int[][] dp = new int[prices.length][2];
    dp[0][0] = 0; dp[0][1]=-prices[0];
    for (int i = 1; i < prices.length; i++) {
        dp[i][1] = Math.max(dp[i-1][1], dp[i-1][0]-prices[i]);
        dp[i][0] = Math.max(dp[i-1][0], dp[i-1][1]+prices[i]);
    }
    return dp[prices.length-1][0];
}
/** 股票最大收益问题终极版 - 限制交易次数、带手续费、冷冻期
 */
static int stockProfitUltimate(int[] prices, int maxK, int cooldown, int fee){
    if (prices.length == 0) return 0;
    //不限交易次数的版本，优化k特别大的场景，不然代码AC不通过
    if (maxK >= prices.length / 2){
        int[][] dp = new int[prices.length][2];
        dp[0][0]=0;dp[0][1]=Integer.MIN_VALUE;
        //cooldown 的引入，baseCase的初始化较多，此时的状态转移方程中，需要cooldown的那一项无实际意义，直接去掉
        for (int i = 1; i <= cooldown; i++) {
            dp[i][0] = Math.max(dp[i-1][0], dp[i-1][1] + prices[i] - fee);
            dp[i][1] = Math.max(dp[i-1][1], -prices[i]);
        }
        for (int i = cooldown+1; i < prices.length; i++) {
            dp[i][0] = Math.max(dp[i-1][0], dp[i-1][1] + prices[i]-fee);
            dp[i][1] = Math.max(dp[i-1][1], dp[i-1-cooldown][0] - prices[i]);
        }
    }
    int[][][] dp = new int[prices.length][maxK+1][2];
    //初始化base case
    for (int i = 0; i <= maxK; i++) {
        dp[0][i][0] = 0; dp[0][i][1] = Integer.MIN_VALUE;
    }
    for (int i = 1; i <= cooldown; i++) {
        for (int j = maxK; j > 0; j--) {
            dp[i][j][0] = Math.max(dp[i-1][j][0], dp[i-1][j][1]+prices[i]-fee);
            dp[i][j][1] = Math.max(dp[i-1][j][1], -prices[i]);
        }
    }
    for (int i = cooldown+1; i < prices.length; i++) {
        for (int j = maxK; j > 0; j--) {
            dp[i][j][0] = Math.max(dp[i-1][j][0], dp[i-1][j][1] + prices[i] - fee);
            dp[i][j][1] = Math.max(dp[i-1][j][1], dp[i-cooldown-1][j-1][0] - prices[i]);
        }
    }
    return dp[prices.length-1][maxK][0];
}
/** 股票最大收益问题 - 仅允许一次交易,要求在O(N)复杂度内完成
 * 不可以分别找最大最小值想减，这不是做空
 */
static int maxProfitOneShot(int[] prices){
    if (prices.length == 0) return 0;
    int minPrice = prices[0];
    int maxProfit = 0;
    for (int i = 1; i < prices.length; i++) {
        minPrice = Math.min(minPrice, prices[i]);
        maxProfit = Math.max(prices[i] - minPrice, maxProfit);
    }
    return maxProfit;
}
//endregion
public static void main(String[] args) {
    System.out.println(maxProfitOneShot(new int[]{1,2,5,3,8,0}));
}




}
