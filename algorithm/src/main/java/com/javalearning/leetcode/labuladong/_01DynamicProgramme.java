package com.javalearning.leetcode.labuladong;

import com.javalearning.leetcode.utils.TestCases;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class _01DynamicProgramme {

    /**
     # 凑零钱问题 / 最优子结构问题
     */
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
     * # 最大子串和
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

    /*-=-=-=-=-=-=- 正则表达式匹配 =-=-=-=-=-=-=-=-*/
    /**
      # 正则表达式匹配
      字符串s和一个字符模式p，实现支持'.'和'*'的正则表达式匹配
      '.'匹配任意单个字符，'*'匹配零个或多个前面的元素
      测试用例：
     s               p           res
     aa             a*          true
     aab            c*a*b       true
     ab             .*          true
     mississippi    mis*is*p*.  false
     */
    static class RegularExpressDynamicProcess{
        //使用指针判断两个字符串是否完全匹配
        static boolean isPatternMatchString(String s, String p, int i){
            if (i>=s.length() || i>=p.length()){
                return true;
            }
            return s.charAt(i) == p.charAt(i) && isPatternMatchString(s,p,++i);
        }
        /*
        指针位置检查
        . 匹配当前任意一个char
        * 匹配前面任意次数的char
        当前指针位置上的char是否相等
        -- 上述判断有一个优先级
        * */
        static boolean mysolution2(String s, String p, int si, int pi){
            /* 终点情况又分多种
            si到头，pi到头，直接返回true
            si没到头，pi到头，返回false
            si到头，pi没到头，返回false
            bug: a - ab* 这种si到头，pi不到头的，要放行
            */
            //if (si >= s.length() || pi >= p.length()){
            //    return si>=s.length() && pi>=p.length();
            //}
            if (pi >= p.length()){
                return si>=s.length();
            }
            /*使用p消耗s - 共下述几种情况
                1 char-char 两个char是否相等
                2 char* pi一次扫描两个，如果特征char在s里则si++，s在si的char不匹配触发pi+2，si不动
                2.5 char*char a*a 匹配 aaa
                3 .*  si直接到头，判断pi+2是否到头，举例 ab 与 .*c 的匹配判断
                4 .EOF/.char  类似char-char si++ pi++ 但不比较是否相等
                5 SOF* 这种情况不考虑
            * */

            int type = 0;
            boolean starFollowed = pi + 1 < p.length() && p.charAt(pi + 1) == '*';
            if(p.charAt(pi) == '.'){
                if (starFollowed){
                    type=2;
                }else {
                    type=4;
                }
            }else if (starFollowed){
                type = 2;
            }else {
                type=1;
            }
            boolean firstMatch = si < s.length() && s.charAt(si) == p.charAt(pi) || p.charAt(pi) == '.';
            switch(type){
                case 1:
                    //bug_fix: si<s.length要判断
                    if (!firstMatch) return false;
                    else {si++;pi++;}
                    break;
                case 2:
                    //boolean atLeastOne = false;
                    //while (si<s.length() && s.charAt(si) == p.charAt(pi)){
                    //    si++;atLeastOne = true;
                    //}
                    //此写法面对a*c*a匹配aaa这种需要回归检查的无法实现
                    //if (atLeastOne && pi+2 < p.length() && p.charAt(pi+2) == p.charAt(pi)){
                    //    pi+=3;
                    //}else {
                    //    pi+=2;
                    //}


                    //bug: 消耗与非消耗都要判断
                    //终极bug-str5判断错误，放弃。。。
                    if (firstMatch){
                        return mysolution(s, p, si, pi+2) || si+1 == s.length() || mysolution(s, p, si+1, pi);
                    }else {
                        return mysolution(s, p, si, pi+2);
                    }
                case 3:
                    //avca - a*.*a;  ab - .*c
                    return pi+2 == p.length();
                case 4:
                    si++;pi++;
                    break;
                default:
                    throw new RuntimeException("这是算法不是业务代码！");
            }
            return mysolution(s, p, si, pi);
        }
        //博客上提供的写法
        static boolean matchPatternAndString2(String s, String p, int i, int j){
            if (j>=p.length()){
                return i>=s.length();
            }
            char pchar = p.charAt(j);
            boolean first_match = (i < s.length()) && ( pchar == s.charAt(i) || pchar == '.');
            boolean star_followed = j+1 < p.length() && p.charAt(j+1) == '*';
            if (star_followed){
                /*
                 char* 匹配一个字符时由于考虑到char*char这种情况，并且char*char还可以是char*char2*char,所以char*并不一定消耗s的字符
                 所以可以用一个或来判断 match(i,j+2) || match(i+1,j)
                 */
                return matchPatternAndString2(s,p,i,j+2) || first_match && matchPatternAndString2(s, p, i+1, j);
            }else {
                return first_match && matchPatternAndString2(s, p, i+1, j+1);
            }
        }
        /*
        si pi为key做备忘录可以简化递归
        todo 堆栈溢出，有bug
        * */
        @Data @AllArgsConstructor
        static class Key{
            private int i;
            private int j;
        }
        static boolean RegExpWithMemo(String s, String p){
            Map<Key,Boolean> memo = new HashMap<>();
            return RegExpWithMemoHelper(s, p, 0, 0,memo);
        }
        static boolean RegExpWithMemoHelper(String s, String p, int i, int j,Map<Key,Boolean> memo){
            Key currKey = new Key(i, j);
            if (memo.containsKey(currKey)) return memo.get(currKey);
            if (j == p.length()) return i==s.length();
            boolean firstMatch = i<s.length() && p.charAt(j)==s.charAt(i) || p.charAt(j)=='.';
            boolean res = false;
            if (j+2<=p.length() && p.charAt(j+1) == '*'){
                res = RegExpWithMemoHelper(s, p, i, j+2, memo) || firstMatch && RegExpWithMemoHelper(s, p, i+1, j, memo);
            }else {
                res = firstMatch && RegExpWithMemoHelper(s, p, i+1, j+1, memo);
            }
            memo.put(currKey, res);
            return res;
        }
        /*
        重叠子问题 如何看出来，从而应用动态规划
        对于斐波那契的求解：
        fbnacci(i) 依赖
            fbnacci(i-1) #A
            fbnacci(i-2) #B
        对于任意k<i,k>0, fbnacci(i-k)可以有多种方式到达，如k个#A，k/2个#B加#A组成零头，所以一定存在重叠子问题
        从正则表达式的递归求解中使用的递归调用，可以看出
        regexp(i,j)依赖
            regexp(i,j+2)  #A
            regexp(i+1,j)  #B
            regexp(i+1,j+1) #C
        那么对于求解regexp(i,j)到reg(i+2,j+2)就存在 #A#B#B 和 #C#C 两条路径，存在重叠子问题
        但这里的路径带有条件
        * */
        static boolean mysolution(String s, String p, int si, int pi){
            return RegExpWithMemo(s, p);
        }
        public static void main(String[] args) {
            String str = "aaabbc", ptn = "aa6bbc";//false
            String str1 = "mississippi", ptn1 = "mis*is*ip*.";//true
            String str2 = "aab", ptn2 = "c*a*b";//true
            String str3 = "hector", ptn3 = "hectorbre";//false
            String str4 = "mississippi", ptn4 = "mis*is*p*.";//false
            String str5 = "ab", ptn5 = ".*c";//false
            String str6 = "aaa", ptn6 = "a*a";//true
            String str7 = "aaa", ptn7 = "ab*a*c*a";//true
            String str8 = "aa", ptn8 = "a*";//true
            String str9 = "a", ptn9 = "ab*";//true
            String str10 = "aaa", ptn10 = "aaaa";//false
            String str11 = "bbbba", ptn11 = ".*a*a";
            String str12 = "a", ptn12 = "a*a";
            Assert.assertTrue(!mysolution(str,ptn,0,0));
            Assert.assertTrue(mysolution(str1,ptn1,0,0));
            Assert.assertTrue(mysolution(str2,ptn2,0,0));
            Assert.assertTrue(!mysolution(str3,ptn3,0,0));
            Assert.assertTrue(!mysolution(str4,ptn4,0,0));
            Assert.assertTrue(!mysolution(str5,ptn5,0,0));
            Assert.assertTrue(mysolution(str6,ptn6,0,0));
            Assert.assertTrue(mysolution(str7,ptn7,0,0));
            Assert.assertTrue(mysolution(str8,ptn8,0,0));
            Assert.assertTrue(mysolution(str9,ptn9,0,0));
            Assert.assertTrue(!mysolution(str10,ptn10,0,0));
            Assert.assertTrue(mysolution(str11,ptn11,0,0));
            Assert.assertTrue(mysolution(str12,ptn12,0,0));
        }
    }





}
