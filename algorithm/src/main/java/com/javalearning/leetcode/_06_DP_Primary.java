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

public static void main(String[] args) {

}




}
