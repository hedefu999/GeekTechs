package com.javalearning.leetcode;

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
// 使用备忘录 int[] memo 解决子问题重复计算问题
static int dp2LC509(int[] memo, int n){
    if (n == 1 || n == 2) return 1;
    //避免子问题重复计算
    if (memo[n] != 0) return memo[n];
    memo[n] = dp2LC509(memo, n-1) + dp2LC509(memo, n-2);
    return memo[n];
}

public static void main(String[] args) {
    System.out.println(fib(4));
}




}
