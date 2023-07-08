package com.javalearning.leetcode.labuladong;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class _03DynamicProgramme {
    private static final Logger log = LoggerFactory.getLogger(_03DynamicProgramme.class);

    /**
      # 子序列问题的解法套路
        区分子序列与子串的概念，前者是不连续的元素集合，后者是连续的
      在最长递增子序列LIS中使用了 一维的动态规划数组：将当前扫描到的元素作为递增字序列的最后一个元素，计算此时的长度，依赖于从后向前第一个比它小的元素
     ```java
        int n = array.length;
        int[] dp = new int[n];

        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                dp[i] = 最值(dp[i], dp[j] + ...)
            }
        }
     ```
     在最长公共子序列和最小编辑距离问题中使用了二维的动态规划数组，这种情形涉及两个数组
     ```java
        int n = arr.length;
        int[][] dp = new dp[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 1; j < n; j++) {
                if (arr[i] == arr[j])
                    dp[i][j] = dp[i][j] + ...
                else
                    dp[i][j] = 最值(...)
            }
        }
     ```
     还有一种使用二维动态规划数组的动态规划问题，但只涉及一个数组，实际上一个数组拷贝作为另一个维度，也就是两个相同的数组的概念
     这样代码中处理的就是一个正方形矩阵


     */
    /**
     # 5  经典面试题 最长回文子串
     回文串的长度可能是奇数，也可能是偶数
     字符串 f b a b a d
     倒过来 d a b a b f
     寻找上述两个字符串的最长公共子串（连续的）


     dp[i][j]表示字符串s中以s[i]为开头，s[j]为结尾的字符串是否是回文子串，是回文子串就记录长度，dp[i][j]与dp[i+1][j-1]有关
     如果s[i+1] = s[j-1],则可以构成回文子串，如果不相等，那就要看s[i+1,j]和s[i,j-1]是否构成回文子串
     */
    static class LongestPanlidromeSubString{
        /*
        错误解法:
         找当前字符串和逆序串的最长公共子串
         公共子序列考虑的新加入的元素是否相等，而公共子串因为连续的原因需要假定以当前的元素为公共子串的结尾
            "" f b a b a d
         "" 0  0 0 0 0 0 0
         d  0  0 0 0 0 0 1
         a  0  0 0 1 0 1 0
         b  0  0 1 0 2 0 0
         a  0  0 0 2 0 3 0
         b  0  0 1 0 3 0 0
         f  0  1 0 0 0 0 0
         dp[i][j] 在i=j时表示 以chars1[j]结尾的字符串的回文串的长度，与dp[i-1][j-1]有关
         [ERROR]在 aacxycaa 情况下，算法被推翻，算法认为aac是回文串。另外，对于acxyca计算结果是ac
            "" a a c x y c a a
         "" 0  0 0 0 0 0 0 0 0
         a  0  1 1 0 0 0 0 1 1
         a  0  1 2 0 0 0 0 1 2
         c  0  0 0 3 0
         y  0        0 1
         x  0
         c  0
         a  0
         a  0
        * */
        @Deprecated
        static String getLPSubStringDPSolution(String str){
            char[] chars1 = str.toCharArray();
            int length = chars1.length;
            char[] chars2 = new char[length];
            for (int i = 0; i < length; i++) {
                chars2[i] = chars1[length - 1 - i];
            }
            int[][] dp = new int[length+1][length+1];
            int max = 0, maxj = 0;
            for (int i = 1; i <= length; i++) {
                for (int j = 1; j <= length; j++) {
                    if (chars2[i-1] == chars1[j-1]){
                        dp[i][j] = dp[i-1][j-1] + 1;
                        //需要找到dp[i][j]同样大的情况下更小的j，这样babad计算可以得到bab，而非aba
                        //更靠前的回文子串的在逆序串里更靠后，所以在max = dp下替换j可以得到i更大对应的j，也就是逆序串chars2更靠后的回文子串的开头
                        //chars1 - j， chars2 - i
                        if (max <= dp[i][j]){
                            //if (max == dp[i][j] && maxj > j){
                            //    maxj = j;
                            //}else {
                            //    max = dp[i][j];maxj = j;
                            //}
                            max = dp[i][j];maxj = j;
                        }
                    }
                }
            }
            StringBuilder builder = new StringBuilder();
            //注意取回文串是在chars1中以maxj为末尾向前取max个
            for (int i = 0; i < max; i++) {
                builder.append(chars1[maxj-1-i]);
            }
            return builder.toString();
        }

        //中心扩展解法
        //给定start和end从str中找回文子串
        //回文串可能是奇数长度也可能是偶数的，传入start end更方便向外围扩展
        static String panlidrome(char[] str, int start, int end){
            while (start>=0 && end < str.length && str[start] == str[end]){
                start--;end++;
            }
            String res = new String(str,start+1,end-start-1);//end=length,start=length-1时
            return res;
        }
        static String getLPSubString(String str){
            char[] chars = str.toCharArray();
            int length = chars.length;
            String res = "";
            for (int i = 0; i < length; i++) {
                //因为不确定是偶数还是奇数的回文子串，这里对两种情况都进行试验
                String s1 = panlidrome(chars, i, i);
                String s2 = panlidrome(chars, i, i+1);
                //res = longest(res,s1,s2) 这种写法的效果是同样长度的回文子串取后面的
                res = res.length()>s1.length()?res:s1;
                res = res.length()>s2.length()?res:s2;
            }
            return res;
        }
        /*
         动态规划解法
         状态转移方程：
         p(i,j) 表示给定的字符串第i到第j个字符 s[i,j] 是否构成回文串，取值true/false
         p(i,j) = p(i+1)(j-1) && (s[i] == s[j]) 只有s[i+1,j-1]是回文子串，并且s[i]==s[j],s[i,j]才是回文子串
         边界条件：
         p(i,i) = true; p(i,i+1)=(s[i] == s[i+1]) 一个字符属于回文子串。两个相同的字符也算作回文子串。
         举例：cabaf
            j0 1 2 3 4          j0 1 2 3 4
         i   c a b a f           b a b a d
         0 c 1 0 0 0 0      0b   1 0 1 0 0
         1 a - 1 0 1 0      1a   - 1 0 1 0
         2 b - - 1 0 0      2b   - - 1 0 0
         3 a - - - 1 0      3a   - - - 1 0
         4 f - - - - 1      4d   - - - - 1
         i>j没有意义，不参与计算
         dp[i+1][j-1]位于dp[i][j]左下角斜对角线，所以dp数组应从下向上遍历填充
         对每一个dp[i][j]为true，并且j-i大于现有回文子串的长度，表示找到了更长的回文子串，此时需要更新回文子串
         dp[i][j]需要依赖左下角元素判断，但不是所有元素，包括 i>j的、i=j、i+1=j的元素
        */
        static String longestPanlidromeDPSolution(String str){
            char[] chars = str.toCharArray();
            int length = chars.length;
            boolean[][] dp = new boolean[length][length];
            String res = "";
            //依据上述dp数组的举例：s[i,j]的判断中，i取值范围0 1 2...length-1开始，j取值范围i,i+1...length-1
            for (int i = length-1; i >=0 ; i--) {
                for (int j = i; j < length; j++) {
                    int currentLen = j-i;
                    if (i == j) dp[i][j] = true;
                    else if (i+1 == j) dp[i][j] = chars[i]==chars[j];
                    else dp[i][j] = dp[i+1][j-1] && (chars[i] == chars[j]);
                    if (dp[i][j] && j-i+1 > res.length()) //j-i+1 >= res.length就表示取i更小的回文串，即靠前的
                        res = new String(chars,i,j-i+1);
                }
            }
            return res;
        }
        //对比上面被推翻的动态规划，todo 原算法错在哪儿？

        /*
         马拉车算法 Manacher
         TC=O(N)
         定义"臂长"表示中心扩展算法向外扩展的长度，偶数长度的回文串就是一半，奇数的就是出去中间元素剩下的一半
         对于
             e b a b a b a b e
             0 1 2 3 4 5 6 7 8
         i = 0, list.put(0,0) j=0,right=0; start=0,end=0;
         i = 1, list.put(1,0) j=1,right=1; start=1,end=1;//此时可以看出是取更靠后的回文子串
         i = 2, list.put(2,1) j=2,right=3; start=1,end=3;//start end 紧跟新发现的最长回文子串
         i = 3, 以j为中心的对称点 i_sym=1, min(0,0) list.put(3,2) j=3,right=5,start=1,end=5; //right就是end
         i = 4, 以3为中心的对称点 i_sym=2, min(1,1) 3~5臂长4 list.put(4,4) j=4,right=8,start=0,end=8//right不会越界
         i = 5, 以4为中心的对称点 i_sym=3 min(2,3) 3~7计算臂长2 list.put(5,2) 其他数字不变
         i = 6  --------------- -----=2 min(1,2) 5~7计算臂长1 list.put(6,1) 其他数字不变
         。。。难以理解，start end已经跑到两端不动了，算法可以停下来了，臂长在计算中简化了什么？。。。

        */
        public String longestPalindromeManacherSolution(String s) {
            int start = 0, end = -1;
            StringBuffer t = new StringBuffer("#");
            for (int i = 0; i < s.length(); ++i) {
                t.append(s.charAt(i));
                t.append('#');
            }
            t.append('#');
            s = t.toString();

            List<Integer> arm_len = new ArrayList<Integer>();
            int right = -1, j = -1;
            for (int i = 0; i < s.length(); ++i) {
                int cur_arm_len;
                if (right >= i) {//right因为回文的扩展而跑到右臂变大，超过i
                    int i_sym = j * 2 - i;//j是上一个i，i_sym就是i以j为中心的对称点
                    int min_arm_len = Math.min(arm_len.get(i_sym), right - i);//min_arm_len就是以i为中心可以避免比较直接跳过进行回文扩展的长度
                    cur_arm_len = expand(s, i - min_arm_len, i + min_arm_len);
                } else {
                    cur_arm_len = expand(s, i, i);
                }
                arm_len.add(cur_arm_len);//arm_len存储了每个i对应的臂长，i从0开始计算
                if (i + cur_arm_len > right) {
                    j = i;
                    right = i + cur_arm_len;//right表示右臂的末点，因为是向后遍历，所以是右臂而非左臂
                }
                if (cur_arm_len * 2 + 1 > end - start) {//start end更新到更广的位置上，表示找到了更长的回文子串
                    start = i - cur_arm_len;
                    end = i + cur_arm_len;
                }
            }

            StringBuffer ans = new StringBuffer();
            for (int i = start; i <= end; ++i) {
                if (s.charAt(i) != '#') {
                    ans.append(s.charAt(i));
                }
            }
            return ans.toString();
        }
        //向外扩展求臂长，这里的s[left,right]是一个奇数长度的回文子串
        //求的是总臂长，不是新扩展出来的
        public int expand(String s, int left, int right) {
            while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
                --left;
                ++right;
            }
            return (right - left - 2) / 2;
        }

        public static void main(String[] args) {
            Assert.assertTrue(longestPanlidromeDPSolution("cabaf").equals("aba"));
            Assert.assertTrue(longestPanlidromeDPSolution("babad").equals("aba"));
            Assert.assertTrue(longestPanlidromeDPSolution("aacxycaa").equals("aa"));
            Assert.assertTrue(longestPanlidromeDPSolution("fbadeaedabd").equals("badeaedab"));
            Assert.assertTrue(longestPanlidromeDPSolution("cbbd").equals("bb"));
            Assert.assertTrue(longestPanlidromeDPSolution("cb").equals("b"));
            System.out.println(getLPSubStringDPSolution("acxyca"));
        }
    }

    /**
      最长回文子序列
     */
    static class LongestPanlidromeSubSequence{

    }

    /**
     # 1143 最长公共子序列 Longest Common Subsequence,LCS,与最小编辑距离解题思路接近
     字符串长度限制1000，只含有小写英文字母
     babcde 与 ace 的 LCS 是 ace
     sghregsa - gegad = 4
     sghregsa - goiuiybhn = 2
     abc - abc = 3
     abc - def = 0
        "" s g h r e g s a
     "" ?  0 0 0 0 0 0 0 0
     g  0  0 1 1 1 1 1 1 1
     e  0  0 1 1 1 2 2 2 2
     g  0  0 1 1 1 2 3 3 3
     a  0
     d  0
     sghj+b gha+j result = calc(2,) = 3
     sghre+g - ge+g = 2+1(后面新增加的g是否在前面字符串匹配的ge之后，str.substring(str.lastIndexof(e)).contains(g)? )
     dp[i][j] = dp[i-1][j] + str.substring(str.lastIndexof(e)).contains(g)?1:0;
     上面这种查找的方法在 s+g - ge+g 情形下不适用

     正确的思路是：sghj+b gha+j result = b跟j相等吗，相等的话sghj跟gha计算的结果+1，不相等取 （sghjb与gha，sghj与ghaj，sghj与gha）的值，取最大（由于sghj与gha的LCS肯定不会查过其他两个，所以可以不参与计算）
        这样 dp[i][j] 并不是从 dp[i-1][j] 上 +1 的
     字符串类的动态规划问题，通常都是跟前面三个角上的数据进行计算获得
     dp[i-1][j-1] dp[i-1][j]
     dp[i][j-1]   dp[i][j]
     */
    static class LongestCommonSubSequence{
        static int LCSDpSolution(String s1,String s2){
            char[] chars1 = s1.toCharArray();
            char[] chars2 = s2.toCharArray();
            int[][] dp = new int[chars2.length+1][chars1.length+1];
            for (int i = 0; i <= chars2.length; i++) {
                dp[i][0] = 0;
            }
            for (int i = 0; i <= chars1.length; i++) {
                dp[0][i] = 0;
            }
            for (int i = 1; i <= chars2.length; i++) {
                for (int j = 1; j <= chars1.length; j++) {
                    //s+g - ge+g 情况下会重复计算字符串，而且查找已经混乱了
                    int plusOne = 0;
                    char preChar = i>2?chars2[i-2]:0;
                    for (int k = j-1; k>=0 && chars1[k]!=preChar; k--) {
                        if (chars1[k] == chars2[i-1]){
                            plusOne = 1;
                            break;
                        }
                    }
                    dp[i][j] = dp[i-1][j]+plusOne;
                }
            }
            return dp[chars2.length][chars1.length];
        }
        static int LCSDpSolution2(String s1,String s2){
            char[] chars1 = s1.toCharArray();
            char[] chars2 = s2.toCharArray();
            StringBuilder lcs = new StringBuilder();
            int[][] dp = new int[chars2.length+1][chars1.length+1];
            for (int i = 0; i <= chars2.length; i++) {
                dp[i][0] = 0;
            }
            for (int i = 0; i <= chars1.length; i++) {
                dp[0][i] = 0;
            }
            int current = 0;
            for (int i = 1; i <= chars2.length; i++) {
                for (int j = 1; j <= chars1.length; j++) {
                    if (chars1[j-1] == chars2[i-1]){
                        dp[i][j] = dp[i-1][j-1] + 1;
                        if (dp[i][j] > current){ //如何把lcs打印出来，跟最小编辑距离的编辑路径的寻找很相似
                            lcs.append(chars1[j-1]);
                            current+=1;
                        }
                    }else {
                        dp[i][j] = Math.max(dp[i-1][j],dp[i][j-1]);
                    }
                }
            }
            System.out.println("lcs = "+lcs);
            return dp[chars2.length][chars1.length];
        }

        public static void main(String[] args) {
            String s1 = "babcde", s2 = "ace";
            String t1 = "sghregsa", t2 = "gegad";
            Assert.assertTrue(LCSDpSolution(s1,s2) == 3);
            Assert.assertTrue(LCSDpSolution(t1,t2) == 4);
        }
    }


    /**
     # 72 编辑距离
     单词word1转换成word2的最小操作数，操作指 插入、删除、替换字符
     case1: horse - ros = 3 : h替换为r，删除r，删除e
     case2: intention - execution = 5: 删除t,i->e,n->x,n->c,插入u
     case3: rad - apple = 5: delete r,d->p,insert p,insert l,insert e

     # 分析
     此处分析认为是 s1（索引是i） 转换到 s2（索引是j），从后向前扫描（？？？） todo 向后扫描是不是新解法？
     对于s1[i]与s2[j]有4种操作
     if (s1[i] == s2[j]){
     i,j同时向前移动
     }else{
     三种操作选一：insert、delete、replace
     }
     */
    static class MinEditDistance{
        /*
         容易理解的递归解法
         性能比较弱，tasecase4 jackmarry后面加上none会跑地很吃力
         */
        static int minDistanceRecursively(String si, String sj){
            //return helper(si.toCharArray(),sj.toCharArray(),si.length()-1,sj.length()-1);
            //return helperPositiveSequence(si.toCharArray(),sj.toCharArray(),0,0);
            int[][] memo = new int[si.length()][sj.length()];
            return helperPositiveSequenceWithMemo(si.toCharArray(),sj.toCharArray(),0,0,memo);
        }
        static int helper(char[] si, char[] sj, int i, int j){
            //base case
            //如果一个字符串扫描完，则另一个字符串的剩余字符都要删掉
            //if (j == -1){//sj匹配完了
            //    if (i == -1){
            //        return 0;//两个同时匹配完
            //    }else {
            //        return i+1;//i=0时还剩一个字符
            //    }
            //}
            //if (i == -1){//si用完了，只能insert了
            //    return j+1;//看sj还剩几个字符，i=j=-1的情况上面考虑了
            //}
            //basecase可以简化成两行
            if (j == -1) return i+1;
            if (i == -1) return j+1;
            if (si[i] == sj[j]){
                return helper(si,sj,i-1,j-1);
            }else {
                return Math.min(
                        helper(si,sj,i,j-1)+1, //si insert 则只需要j前移
                        Math.min(
                                helper(si,sj,i-1,j)+1, //si delete 则只需要i前移
                                helper(si,sj,i-1,j-1)+1 //si replace ij都需要前移
                        ));
            }
        }
        //以si为基准，从前向后扫描，额。。。也能跑通（所以选那种顺序，以谁为准没有区别，不影响递归的正确性）
        static int helperPositiveSequence(char[] si, char[] sj, int i, int j){
            if (i == si.length) return sj.length - j;
            if (j == sj.length) return si.length - i;
            if (si[i] == sj[j]){
                return helperPositiveSequence(si, sj, i+1, j+1);
            }else {
                return Math.min(
                        helperPositiveSequence(si, sj, i+1, j)+1, //create，向后扫描在前面插入元素，j不动
                        Math.min(
                                helperPositiveSequence(si, sj, i, j+1)+1, //delete
                                helperPositiveSequence(si, sj, i+1, j+1)+1 //update
                        )
                );
            }
        }

        /*
        ## 动态规划解法

        //si 源字符串动态
        int[] dp = new int[si.length];
        dp[0] = sj.length;
        dp[1] = ;//si[0,1) 到sj的编辑距离
        dp[2] = dp[1] - (dp[])

        //sj 目标字符串动态
        int[] dp = new int[sj.length];
        dp[0] = si.length;
        dp[1] = contain -1,否则 si.length;

        思路从重叠子问题的发现出发，使用备忘录dp数组(结果速度更慢？)
        memo是一个二维数据，dp数组也定为二维，并且dp数组的填充也要参考递归中的处理过程
         */
        static int helperPositiveSequenceWithMemo(char[] si, char[] sj, int i, int j, int[][] memo){
            if (i == si.length) return sj.length - j;
            if (j == sj.length) return si.length - i;
            if (si[i] == sj[j]){
                if (memo[i][j] != 0){
                    return memo[i][j];
                }
                memo[i][j] = helperPositiveSequence(si, sj, i+1, j+1);
                return memo[i][j];
            }else {
                memo[i][j] = Math.min(
                        helperPositiveSequence(si, sj, i+1, j)+1, //create，向后扫描在前面插入元素，j不动
                        Math.min(
                                helperPositiveSequence(si, sj, i, j+1)+1, //delete
                                helperPositiveSequence(si, sj, i+1, j+1)+1 //update
                        )
                );
                return memo[i][j];
            }
        }
        static int minDistanceDpSolution(String stri, String strj){
            char[] si = stri.toCharArray();
            char[] sj = strj.toCharArray();
            int[][] dp = new int[si.length+1][sj.length+1];
            for (int i = 0; i <= si.length; i++) {
                dp[i][0] = i;
            }
            for (int i = 1; i <= sj.length; i++) {
                dp[0][i] = i;
            }
            for (int i = 1; i <= si.length; i++) {
                for (int j = 1; j <= sj.length; j++) { //对j遍历时i有变化（i减1了），是不能使用状态机进行简化的
                    if (si[i-1] == sj[j-1]){
                        dp[i][j] = dp[i-1][j-1];
                    }else {
                        dp[i][j] = Math.min(
                                dp[i-1][j] + 1,
                                Math.min(
                                        dp[i][j-1] + 1,
                                        dp[i-1][j-1] + 1
                                )
                        );//显然，dp[i][j]的求解只需要其附近的3个元素即可
                    }
                }
            }
            return dp[si.length][sj.length];
        }

        /*
         * ERROR 做不了状态机化简，状态转移方程在二维遍历中两个维度均有变化
         */
        @Deprecated
        static int minDistanceDpSolutionPlusLowSC(String stri, String strj){
            char[] si = stri.toCharArray();
            char[] sj = strj.toCharArray();
            int left=1,up=1,leftup=0,current=0;
            for (int i = 1; i <= si.length; i++) {
                for (int j = 1; j <= sj.length; j++) {
                    if (si[i-1] == sj[j-1]){
                        current=leftup;
                    }else {
                        current = Math.min(left + 1, Math.min(up + 1, leftup+1));
                    }
                }
            }
            return current;
        }

        /*
         上述状态机SC简化方案不可性，但降维SC简化是可以的，SC = O(min(stri.length(),strj.length()))
         j维度遍历时i只变化了1，所以可以使SC将为O(min), ???如果i变化了2（有i,i-1,i-2）是否SC只能降为2*O(min)???
         对于 horse -> ros
             ""  r  o  s
         ""   0  1  2  3
         h    1  1  2  3
         o    2  2  1  2
         r    3  2  2  2
         s    4  3  3  2
         e    5  4  4  3

         实际写起来还是要两个一维数组记录上一轮操作，改进效果不大
         */
        static int minDistanceDpSolutionLowerSC(String stri, String strj){
            char[] si = stri.toCharArray();
            char[] sj = strj.toCharArray();
            int[] dp = new int[sj.length+1];
            int[] dp2 = new int[sj.length+1];
            for (int i = 0; i < dp2.length; i++) {
                dp2[i] = i;
            }
            for (int i = 1; i <= si.length; i++) {
                int left = i, leftup = dp2[0], up = dp2[1];
                dp[0] = i;
                for (int j = 1; j <= sj.length; j++) {
                    if (si[i-1] == sj[j-1]){
                        dp[j] = leftup;
                    }else {
                        dp[j] = Math.min(leftup+1,Math.min(up+1,left+1));
                    }
                    //error:下面的改值要放在if-else外面
                    left = dp[j];
                    up = j==sj.length ? 0 : dp2[j+1];
                    leftup = dp2[j];
                }
                dp2 = Arrays.copyOf(dp,dp.length);//还要拷贝
                //System.arraycopy(dp, 0, dp2, 0, sj.length + 1);
            }
            return dp[sj.length];
        }

        /**
         ## 算法应用
         如何将算法应用到程序中实现功能？
         将二维dp数组声明成Node(c,u,d,N)记录操作的具体内容，再根据
         替换去左上角  删除向上
         插入向左     什么都不做
         的方式回溯，可实现将字符串修改为目标字符串
         */

        public static void main(String[] args) {
            String si1 = "horse", sj1 = "ros";
            String si2 = "intention", sj2 = "execution";
            String si3 = "rad", sj3 = "apple";
            String si4 = "lucymarriedjack", sj4 = "jackmarrynone";
            long start = System.currentTimeMillis();
            Assert.assertTrue(minDistanceDpSolutionLowerSC(si1,sj1) == 3);
            Assert.assertTrue(minDistanceDpSolutionLowerSC(si2,sj2) == 5);
            Assert.assertTrue(minDistanceDpSolutionLowerSC(si3,sj3) == 5);
            Assert.assertTrue(minDistanceDpSolutionLowerSC(si4,sj4) == 10);//注意jack与lucy都有一个c，所以是10不是11
            System.out.println(System.currentTimeMillis()-start);
        }
    }

    /**
     # KMP字符串匹配算法
     KMP,Knuth-Morris-Pratt算法
     《算法4》一书中也有KMP的讲解（dfa 确定有限状态机）

     >题目描述
     在一个字符串中查找另一个字符串，如果找到返回出现的第一个位置
     "ll" 出现在 "hello" 的位置：2
     "bba" 出现在 "aaaaa" 的位置：-1
     */
    static class KMPAlgorithm{
        /*
         字串逐一比较/双指针比较 TC = O((N-L)L)，线性时间复杂度
         */
        static int strStr(String haystack, String needle) {
            if (needle == null || needle.length() == 0) return 0;
            if (haystack == null || haystack.length() == 0) return -1;
            int haystackLength = haystack.length();
            int needleLength = needle.length();
            for (int i = 0; i <= haystackLength - needleLength; i++) {
                if (haystack.charAt(i) == needle.charAt(0)){
                    int j = 0;
                    while (j < needleLength && haystack.charAt(j+i) == needle.charAt(j)){
                        j++;
                    }
                    if (j == needleLength){
                        return i;
                    }
                }
            }
            return -1;
        }
        /*
        Rabin Karp 常数复杂度 - hash码
        制定一个滑动窗口内字符串的hash生成策略，滑动窗口移动一次，出一个字符并进入一个字符
        由于只有小写字母，将字符串转成26进制数,窗口移动后新的hash值的计算可以依据旧值推算
        h1 = ( h0 - removedChar * a^(ptn.length - 1))*a + newChar * a^0; (进制数是a)
        化简得：
        h1 = h0*a - rmdChar * a^ptn.length + newChar
        hash可能会溢出，所以找一个很大的数进行取余，这个数取2^31, 此数的来源见 [线性同余生成器的wiki](https://en.wikipedia.org/wiki/Linear_congruential_generator#Parameters_in_common_use)
        TC = O(N)
        * */
        static int strStrHashCheck(String src, String ptn){
            char[] srcc = src.toCharArray();
            char[] ptnc = ptn.toCharArray();
            if(ptnc.length == 0) return 0;
            int a = 26;
            int upthreshold = (int) Math.pow(2,31);
            //计算ptn的hash
            int ptnHash = 0;
            for (int i = 0; i < ptnc.length; i++) {
                ptnHash = ptnHash * a + (ptnc[i]-'a');
            }
            ptnHash = ptnHash % upthreshold;
            for (int i = 0; i <= srcc.length - ptnc.length; i++) {
                int windowHash = 0;//滑动窗口的hash
                for (int j = 0; j < ptnc.length; j++) {
                    windowHash = windowHash * a + (srcc[i+j] - 'a');
                }
                windowHash = windowHash % upthreshold;
                if (windowHash == ptnHash) return i;
            }
            return -1;
        }
        /*
          上述算法对于src6 ptn6的情况效率很差，因为src指针没必要完全回溯，在两个字符串重复字符很多的情况下
            KMP算法可以免于完全回溯到起始点，并且对于src7的情况还能ptn整体继续向后滑动
          KMP算法不回退src的指针，不会重复扫描src，借助dp数组中存储的信息把pat移到正确的位置继续匹配
          确定有限状态自动机
          KMP中dp数组的计算仅与ptn有关，所以计算出的dp可以用于匹配不同的src
        * */
        static class KMPSolution{
            private int[][] dp;
            private String ptn;

            public KMPSolution(String ptn) {
                this.ptn = ptn;
                this.dp = buildbp(ptn);
            }

            /*
             KMP算法通过状态机的构造实现有穷的状态转移，用于在src中出现新的字符时作为激励，判断应该转移到哪个状态
                当前状态+激励 组成状态转移图
             将状态转移规则存入二维数组 dp[state][signal] = nextState
             */
            Map<Character,Integer> ptnMap = new HashMap<>();

            public int[][] buildbp(String ptn){
                char[] ptnChars = ptn.toCharArray();
                if (ptnChars.length == 0) return null;
                //先将所有的char映射到一个顺序编号中，目的是让dp数组第二维度小点，最大也只能达到256，可能不值得建立hash表,但256个长度调试比较麻烦
                for (int i = 0; i < ptnChars.length; i++) {
                    ptnMap.computeIfAbsent(ptnChars[i],nothing -> ptnMap.size());
                }
                dp = new int[ptnChars.length][ptnMap.size()];
                int counselorIndex = 0;//顾问状态/顾问字符/顾问结点，随ptn字符的扫描变化，也可以生成所有ptn字符的顾问点，这样就是又一个数组
                // dp[0][ptnMap.get(ptnChars[0])] = 1;//dp[0][]是确定的，只有一个1，其他都是0
                for (int i = 0; i < ptnChars.length; i++) {//i本身也表示状态，状态number与ptn的index一致
                    //这里是对ptn中的所有类型的字符进行一次状态转移演练，演练次数取决于ptn的长度
                    int currCharNo = ptnMap.get(ptnChars[i]);
                    for (int j = 0; j < ptnMap.size(); j++) {
                        if (j == currCharNo){
                            dp[i][j] = i+1;//转移到下一个状态
                        }else {
                            //状态需要回退，如何回退是这个dp构造的难点
                            //状态在需要回退时，新加到尾部的字符如何转移状态，可通过前面具有相同前缀的顾问状态来决定，如果没有就直接回到初始状态
                            dp[i][j] = dp[counselorIndex][j];
                        }
                    }
                    /*
                    顾问index更新的秘密
                    状态0依次遇到ptn中的字符一定会递增状态，i就是这样不断向前，counselorIndex也是接受当前的字符信号进行状态转移的，就是下面的dp[][currCharNo]
                    counselorIndex是跟随i的，但它在第0步没有跟随，否则就跟i没有区别，它相当于对 ptn(1,ptn.length) 的进行从0状态开始的状态转移，丢掉了开头的第一个字符
                    其效果就是不断等待i走过的相同的前缀在再次出现，从而达到跟踪到相同前缀结尾的目的
                    * */
                    if (i > 0){//i可以从1开始，这样这个判断就可以去掉，但为理解 counselorIndex从1开始进行状态转移 这一重要内容，还是要体现出来
                        counselorIndex = dp[counselorIndex][currCharNo];
                    }
                    //dp数组内可以存有一连串的顾问状态，如 abcabcabcedf
                }
                return dp;
            }
            //使用这个dp数组
            public int searchPtnInSrc(String src){
                if (dp == null || dp.length == 0) return 0;
                char[] chars = src.toCharArray();
                int currentState = 0;
                for (int i = 0; i < chars.length; i++) {
                    if (!ptnMap.containsKey(chars[i])){
                        currentState = 0;
                    }else {
                        int charNo = ptnMap.get(chars[i]);
                        currentState = dp[currentState][charNo];
                    }
                    if (currentState == ptn.length()) return i - ptn.length()+1;
                }
                return -1;
            }
        }
        /*
        化简过的KMP性能还是比不上上面的hash码的解法，可能要在其他场景才能显现KMP的威力
        * */
        public static int KMPSimplify(String haystack,String needle){
            char[] ptnChars = needle.toCharArray();
            if (ptnChars.length == 0) return 0;
            int[][] dp = new int[ptnChars.length][256];
            int counselorIndex = 0;
            dp[0][ptnChars[0]] = 1;
            for (int i = 1; i < ptnChars.length; i++) {
                for (int j = 0; j < 256; j++) {
                    if (j == ptnChars[i]){
                        dp[i][j] = i+1;
                    }else {
                        dp[i][j] = dp[counselorIndex][j];
                    }
                }
                counselorIndex = dp[counselorIndex][ptnChars[i]];
            }
            char[] chars = haystack.toCharArray();
            int currentState = 0;
            for (int i = 0; i < chars.length; i++) {
                currentState = dp[currentState][chars[i]];
                if (currentState == ptnChars.length) return i - ptnChars.length+1;
            }
            return -1;
        }
        public static void main(String[] args) {
            String src1 = "hello", ptn1 = "ll";
            String src2 = "aaaaa", ptn2 = "bba";
            String src3 = "", ptn3 = "";
            String src4 = "mississippi", ptn4 = "issip";
            String src5 = "a", ptn5 = "a";
            String src6 = "aaacaaab", ptn6 = "aaab";
            String src7 = "aaaaaaab", ptn7 = "aaab";
            KMPSolution kmpSolution = new KMPSolution("ababc");
            Assert.assertTrue(new KMPSolution(ptn1).searchPtnInSrc(src1) == 2);
            Assert.assertTrue(new KMPSolution(ptn2).searchPtnInSrc(src2) == -1);
            Assert.assertTrue(new KMPSolution(ptn3).searchPtnInSrc(src3) == 0);
            Assert.assertTrue(new KMPSolution(ptn4).searchPtnInSrc(src4) == 4);
            Assert.assertTrue(new KMPSolution(ptn5).searchPtnInSrc(src5) == 0);
            Assert.assertTrue(new KMPSolution(ptn6).searchPtnInSrc(src6) == 4);
            Assert.assertTrue(new KMPSolution(ptn7).searchPtnInSrc(src7) == 4);
        }
    }

    /**
      博弈类游戏问题 - 假定双方都足够聪明
     # 877 石子游戏
     几堆石子排一列，只能从头或尾取石子，两个人先后取石子，都足够聪明，石子最多的人胜，先取的人会胜吗？
     石子数 piles[i] 堆数i
     石头堆数为偶数，保证两人取走的石头堆数相同；石头总数是奇数，保证两人不会平手；
     # 292 Nim游戏
     一堆石子一次拿[1~3]颗，拿到最后一棵石子胜，第一个拿石子的人是否能胜？
     # 319 灯泡开关
     有n盏电灯，编号1-n，进行n轮操作：第i轮按编号为i的正整数倍的灯开关，i从1到n
     */
    static class GameCollection{
        /*
        最后面对4颗石子的人必输
        倒过来思考，我留下4颗石子 -> 对方留下5~7颗石子 -> 我留下8颗石子（7或9都不对） -> 对方留下9~11颗 -> 我留下12颗。。。
        可以看出自己要留下的石子数是一连串的定值：4的倍数
        即面对4的倍数的石子数的人必输，反之必胜
        * */
        static class NimGame{
            static boolean canWinNim(int n){return n%4 != 0;}
            /*
            如果一次只能拿[1,3]颗如何解
             自己要面对1/3颗石头 ->对方要面对2/4颗 ->自己要面对3/5/7颗 ->对方要面对4/6/8 ->自己要面对5/7/9/11
             遇到奇数颗石头就赢了，无脑取就行
            * */
            /*
            如果一次只能取[1,2]颗石头
            自己要面对1/2 -> 对方面对3 -> 自己面对4/5 ->对方面对6
            面对3n颗石头的人输
            * */
            /*
             如果一次只能取[1,4]颗石头
             自己面对1/4 -> 对方面对2/5 ->自己3/6/9 -> 对方7/10 ->自己8/11/17 ->对方12/15/22 ->自己13/16/19/23/26
             1 3 4 6 8 9 11 16 17 。。。
              2   5 7   10 12 15 。。。
              0开始 +2 +3 ，面对5n及5n-3的输
            */
        }

        /*
        共偶数堆石子，先手可以保证自己只取奇数序号堆还是偶数序号堆，从而提前计算两种石堆的石子总数大小
         */
        static class StoneGame{
            static boolean canWinStoneGame(int[] piles){
                //for循环层数 = piles.length
                for (int i = 0; i < piles.length; i++) {
                    for (int j = 0; j < piles.length && j!=i; j++) {
                        //...
                    }
                }
                return false;
            }
            //暴力递归所有可能的取法。。。还可以从中间取，稍加改进可以改成从两端取
            //穷举到的不一定是聪明的取法，聪明的做法，不露出更大的石头堆
            static void canWinStoneGameRecursively(int[] piles,int sumA, int sumB, boolean AorB){
                if (piles.length == 0){
                    System.out.println(sumA > sumB);
                    return;
                }
                for (int i = 0; i < piles.length; i++) {
                    if (AorB) sumA += piles[i];
                    else sumB += piles[i];
                    int[] leftpiles = new int[piles.length-1];
                    //丢掉一个元素拷贝数组,差点被绕进去
                    for (int j = 0; j < piles.length; j++) {
                        for (int k = 0; k < piles.length-1; k++) {
                            int rightIndex = k >= j?k+1:k;
                            leftpiles[k] = piles[rightIndex];
                        }
                    }
                    canWinStoneGameRecursively(leftpiles,sumA,sumB,!AorB);
                }
            }
            //先取石头的并且知道游戏漏洞的一定会赢，但后取石头的游戏漏洞即使知道也赢不了
            static boolean canWinStomeGame(){return true;}

            //如果两个人都不知道游戏漏洞(或者只知道两端和里面一个的石头数)，而是按照共同的法则取石头，即不露出更大的石头给对手，同时取尽可能多的石头
            //现实中大家都同样蠢，同时觉得自己聪明，而真理掌握在少数人手中
            //然而真理拥有者作为后手也不能保证赢？？？
            static void canWinStoneGameCommonRules(int[] piles,int sumA, int sumB,int start, int end, boolean AorB){
                int length = piles.length;
                if (start > end){
                    log.info("sumA = {}, sumB = {}",sumA,sumB);
                    return;
                }
                int head = piles[start], tail = piles[end];
                int headDiff = head - piles[start+1>end?start:start+1];
                int tailDiff = tail - piles[end-1<start?end:end-1];
                boolean fetchHead = headDiff == tailDiff ? head>tail : headDiff>tailDiff;
                if (fetchHead){
                    if (AorB){
                        sumA+=head;log.info("A取{}",head);
                    } else{
                        sumB+=head;log.info("B取{}",head);
                    }
                    canWinStoneGameCommonRules(piles, sumA, sumB, start+1, end, !AorB);
                }else {
                    if (AorB){
                        sumA+=tail;log.info("A取{}",tail);
                    } else{
                        sumB+=tail;log.info("B取{}",tail);
                    }
                    canWinStoneGameCommonRules(piles, sumA, sumB, start, end-1, !AorB);
                }
            }
            static void commonRulesHelper(int[] piles){
                canWinStoneGameCommonRules(piles,0,0,0,piles.length-1,true);
            }

            public static void main(String[] args) {
                int[] piles = {2,1,9,5};
                int[] piles1 = {3,1,9,5,4,6};
                int[] piles2 = {1,2,5,6,8,7,4,3};
                int[] piles3 = {2,4,6,8,6,5,3,1};
                int[] piles4 = {1,3,5,6,8,6,4,2};
                int[] piles5 = {2,4,8,6,6,5,3,1};
                int[] piles6 = {1,100,3};
                //canWinStoneGameRecursively(piles,0,0,true);
                commonRulesHelper(piles);
                System.out.println();
                commonRulesHelper(piles1);
                System.out.println();
                commonRulesHelper(piles2);
                System.out.println();
                commonRulesHelper(piles3);//A先取结果输了 2663 vs 4851
                System.out.println();
                commonRulesHelper(piles4);
                System.out.println();
                commonRulesHelper(piles5);
                System.out.println();
                commonRulesHelper(piles6);
            }
        }
        /*
         5盏
            1  2  3  4  5  6
         1  1  1  1  1  1  1  6
         2  1  0  1  0  1  0  3
         3  1  0  0  0  1  1  3
         4  1  0  0  1  1  1  4
         5  1  0  0  1  0  1  3
         6  1  0  0  1  0  0  2
         sm 1  2  2  3  2  4
         n轮操作奇数次命中的灯是开的
         6 6231
         5 51
         4 421
         因数分解能分成几个,奇数个因数的i是亮的
         16 124816
         可以发现4跟16都有个重复的因数，这使得他们有奇数个因数
         而这个重复的因数，就是这个序号一下有多少个这种带重复因数的数字
         这个重复因数的求解，数学上叫开方
        */
        static class BulbSwitch{
            static int buldSwitch(int n){
                return (int)Math.sqrt(n);
            }
        }
        /*
         由石头游戏引出的通用动态规划解法
         上面的比较头尾跟近邻元素的差值的算法不能保证先手永远胜，表明这种取法不是最优的
         使用动态规划进行最优决策，这样两个人都能达到最聪明，即博弈
        * */
        static class StoneGameDynamicProgramme{
            /*
             对于石头堆 [3 9 1 2] 运用动态规划
             dp数组定义：
             使用Pair存储元组 Pair<first,second>
             dp[i][j] = Pair<first,second> 对于第i到第j堆石头，先手得分First，后手得分Second
             i<=j,这样有近一半dp数组空间是不用的
             dp[0][piles.length-1]的Fist与Second差值越小表明算法越聪明？
             这个定义像子串
             状态转移：
             先手看来就是取第一堆或最后一堆哪一个得分最高
             dp[i][j].first = Math.max(piles[i]+dp[i+1][j].second, piles[j]+dp[i][j-1].second) //取首或尾堆后，先手就变成后手了
             dp[i][j].second = 先手取头-dp[i+1][j].first; 先手取尾-dp[i][j-1].first
             对于piles = {3,1,9,5,4,6}
                   0   1   2    3    4    5
                   3   1   9    5    4    6
             0-3  3-0 3-1 10-3
             1-1   -  1-0 9-1  6-9
             2-9   -   -  9-0  9-5  13-5
             3-5   -   -   -   5-0  5-4  10-5
             4-4   -   -   -    -   4-0  6-4
             5-6   -   -   -    -    -   6-0
             通常i作为纵轴指向下
             这样dp[i][j]依赖的dp[i+1][j],dp[i][j-1]分别位于下方和左侧，所以dp数组遍历的顺序是从副对角线平行向右上角
             所以i=j的情形就是base case
             dp[i][i].first = piles[i];
             dp[i][i].second = 0;
            * */
            //可以用commons.lang3的tuple替换，leetcode不认得
            static class Pair<T,V> {
                private T left;
                private V right;

                public Pair(T left, V right) {
                    this.left = left;
                    this.right = right;
                }

                public static <T,V> Pair<T,V> of(T t, V v){
                    return new Pair(t,v);
                }
                public T getLeft() {
                    return left;
                }

                public void setLeft(T left) {
                    this.left = left;
                }

                public V getRight() {
                    return right;
                }

                public void setRight(V right) {
                    this.right = right;
                }
            }
            static int solutionTwoDimenTwoMetaItem(int[] piles){
                int length = piles.length;
                Pair<Integer,Integer>[][] dp = new Pair[length][length];
                //第一轮遍历 (0,0) (1,1) (2,2) ...
                //第二轮遍历 (0,1) (1,2) (2,3) ...
                //最后一轮遍历 (0,length-1)
                for (int diff = 0; diff < length; diff++) {
                    //如何按副对角线的方向遍历数组，并且只要上半部分
                    for (int i = 0, j = i+diff; i < length && j < length; i++,j++) {
                        if (i==j){
                            dp[i][j] = Pair.of(piles[i],0);
                        }else {
                            Integer head = piles[i]+dp[i+1][j].getRight();
                            Integer tail = piles[j]+dp[i][j-1].getRight();
                            if (head > tail){
                                dp[i][j] = Pair.of(head,dp[i+1][j].getLeft());
                            }else {
                                dp[i][j] = Pair.of(tail, dp[i][j-1].getLeft());
                            }
                        }
                    }
                }
                return dp[0][length-1].getLeft()-dp[0][length-1].getRight();
            }
            /*
            dp数组的维度不好降，但可以将数组元素的二元改为一个数字，即left-right表示先手对后手的胜出石子数，这样状态转移方程就是
            dp[i][j].first - dp[i][j].second = Math.max(piles[i]+dp[i+1][j].second - dp[i+1][j].first, piles[j]+dp[i][j-1].second-dp[i][j-1].first) ;
             = Math.max(piles[i] - (dp[i+1][j].first - dp[i+1][j].second), piles[j] - (dp[i][j-1].first - dp[i][j-1].second))
             = Math.max(piles[i] - dp[i+1][j], piles[j] - dp[i][j-1])
             这个状态转移方程又可以降维了
            * */
            static int solutionWithSimplifiedDp(int[] piles){
                int length = piles.length;
                int[][] dp = new int[length][length];
                for (int diff = 0; diff < length; diff++) {
                    for (int i = 0, j = i+diff; i < length && j < length; i++,j++) {
                        if (i==j){
                            dp[i][j] = piles[i];
                        }else {
                            dp[i][j] = Math.max(piles[i] - dp[i+1][j], piles[j] - dp[i][j-1]);
                        }
                    }
                }
                return dp[0][length-1];
            }
            /*
            对dp数组进行降维
            观察最早的二维dp数组，最终的答案位于右上角，每一次求解只需要左侧和下方的数字，也就是上一轮遍历中的相邻两个数字
            左侧的数字在使用过一次后就没用了，可以覆盖掉，这样副对角线遍历的新数组始终位于dp数组的前部
            最后答案位于第一个
            * */
            static int solutionWithOneDimenDp(int[] piles){
                int length = piles.length;
                int[] dp = new int[length];
                System.arraycopy(piles,0,dp,0,length);
                for (int end = length-1; end > 0; end--) {
                    //第一轮遍历：dp[i]=piles[i]
                    //第二轮： dp[i]=Math.max(piles[i] - down,piles[i+(length-end)] - left), down就是dp[i+1],left就是dp[i],跳过i取piles的距离越来越大
                    for (int i = 0; i < end; i++) {
                        dp[i]=Math.max(piles[i] - dp[i+1],piles[i+(length-end)] - dp[i]);
                    }
                }
                return dp[0];
            }
            /*
            递归的解法是，如果拿左边，减去剩余石头的得分(少了一堆石头的得分是别人的，所以要用手中的石头减掉)，如果拿右边，减去剩余石头的得分，两者取最大
            并添加备忘录
            * */
            static int stoneGameRecursiveSolution(int[] piles){
                int length = piles.length;
                int[][] memo = new int[length][length];
                return stoneGameHelper(piles,0,length-1,memo);
            }
            static int stoneGameHelper(int[] piles, int start, int end, int[][] memo){
                if (start == end){
                    memo[start][end] = piles[start];
                    return piles[start];
                }
                if (memo[start][end] != 0){
                    return memo[start][end];
                }
                int getLeft = piles[start] - stoneGameHelper(piles, start+1, end,memo);
                int getRight = piles[end] - stoneGameHelper(piles, start, end-1,memo);
                int score = Math.max(getLeft,getRight);
                memo[start][end] = score;
                return score;
            }

            public static void main(String[] args) {
                //石头必须是偶数堆，石子总数应为奇数，如果是偶数，算法能保证至少平手
                int[] piles = {2,1,9,5};
                int[] piles1 = {3,1,9,5,4,6};
                int[] piles2 = {1,2,5,6,8,7,4,3};
                int[] piles3 = {2,4,6,8,6,5,3,1};
                int[] piles4 = {1,3,5,6,8,6,4,2};
                int[] piles5 = {2,4,8,6,6,5,3,1};
                int[] piles6 = {1,100,3,4};
                Assert.assertTrue(stoneGameRecursiveSolution(piles) == solutionWithOneDimenDp(piles));
                Assert.assertTrue(stoneGameRecursiveSolution(piles1) == solutionWithOneDimenDp(piles1));
                Assert.assertTrue(stoneGameRecursiveSolution(piles2) == solutionWithOneDimenDp(piles2));
                Assert.assertTrue(stoneGameRecursiveSolution(piles3) == solutionWithOneDimenDp(piles3));
                Assert.assertTrue(stoneGameRecursiveSolution(piles4) == solutionWithOneDimenDp(piles4));
                Assert.assertTrue(stoneGameRecursiveSolution(piles5) == solutionWithOneDimenDp(piles5));
                Assert.assertTrue(stoneGameRecursiveSolution(piles6) == solutionWithOneDimenDp(piles6));
            }
        }
    }


}
