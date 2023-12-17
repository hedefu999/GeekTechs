package com.javalearning.leetcode;

import java.util.*;
import java.util.function.BiFunction;

/**
 * 滑动窗口算法
 */
public class _0xSlideWindow {
/** LC76 最小覆盖子串
 ADOBECODEBANC 最小包含 ABC 的子串是 BANC
 a 最小包含 a 的子串是 a
 a 最小包含 aa 的子串是 ""
 最简单的思路就是从源字符串的每一个字符开始，逐个向后找包含全部目标串的子串，这是一个TC=O(n^2)
 题目要求采用O(n+m)复杂度解题
 新手，介绍下滑动窗口算法：
 - 1 框定一个区间 [left,right) left<=right, 初始[0,0) 区间内只有一个元素
 - 2 right不断右移，直到窗口中的所有字符串包含了目标串的全部字符
 - 3 现在停止移动right，右移left，直到窗口中不再包含全部的目标串字符
 - 4 重复第2步和第3步，直到 right 到达源字符串尽头
 注意：窗口中的字符串包含目标串，不需要保证字符顺序与目标串一致;目标串中的字符可以重复；
*/
/* 算法实现：准备两个HashMap window target，分别保存字符的计数，当window.get(c) >= target.get(c)时，表明目标串的c字符是覆盖了的
当目标串的全部字符都为window覆盖时，window就是一个符合要求的子串，但不一定是最优的
测试用例：
System.out.println(minWindow("ADBBEAFCHABRCD","ABC"));
System.out.println(minWindow("ADBBEACHABRCD","ABC"));
System.out.println(minWindow("ADBBECHABRCD","ABC"));
System.out.println(minWindow("ADBBCHABRCD","ABC"));
System.out.println(minWindow("ADBCHABRCD","ABC"));
System.out.println(minWindow("ADBCHABCD","ABC"));
System.out.println(minWindow("ADBCHARCD","ABC"));
System.out.println(minWindow("a","a"));
System.out.println(minWindow("a","aa"));
*/
static String minWindow(String s, String t) {
    //初始化一个map
    Map<Character,Integer> target = new HashMap<>();
    for (int i = 0; i < t.length(); i++) {
        target.compute(t.charAt(i), (character, count) -> target.getOrDefault(character,0)+1);
    }
    Map<Character, Integer> window = new HashMap<>();
    int left=0,right=0;//[left,right)
    int allTargetCharContainsCount = 0;
    String matchedSubStr = null;
    while (right < s.length()){
        char rightCursorChar = s.charAt(right);
        if (!target.containsKey(rightCursorChar)){ //空间优化：window可以只记录target中的元素
            right++;
            continue;
        }
        Integer rightCursorCharCount = window.compute(rightCursorChar, (character,count) -> //count第一次传进来是null
                window.getOrDefault(rightCursorChar,0)+1);
        //窗口增大的过程中只需要在目标char数量首次达到（相等）时计数+1，得到多余目标char不会导致计数+1，实现不同目标char独立判断的效果
        if (target.containsKey(rightCursorChar) && rightCursorCharCount.equals(target.get(rightCursorChar))){
            allTargetCharContainsCount++;
        }
        right++;
        //这个判断在while内层，出来后还要继续right++做判断，案例：在 ADBEFCHABC 中寻找ABC
        //此时left可以前进了
        if (allTargetCharContainsCount == target.size()){
            while (left <= right-target.size()){//优化技巧：left不必一直走到right处才停止
                char leftCursorChar = s.charAt(left);
                if (!target.containsKey(leftCursorChar)){//空间优化：window可以只记录target中的元素
                    left++;
                    continue;
                }
                Integer leftCursorCharCount = window.get(leftCursorChar);
                //注意substring是包含left处的char的，此处leftCursor不能走到下一个，如果需要退出while的话
                if (/*target.containsKey(leftCursorChar) &&*/ leftCursorCharCount-1 < target.get(leftCursorChar)){
                    break;
                }
                window.put(leftCursorChar, leftCursorCharCount-1);
                left++;
            }
            //此时 [left,right) 就是符合的子串
            if (matchedSubStr == null || matchedSubStr.length() > (right-left)){
                matchedSubStr = s.substring(left,right);
            }
        }
    }
    return matchedSubStr==null?"":matchedSubStr;
}

/** LC567 包含子串排列判断
 s1 = "ab" s2 = "eidbaooo" --> true(s2包含s1的排列 ba)
 s1= "ab" s2 = "eidboaoo" --> false(子串在s2中要求是连续的！)
 abb - rtwaboabccbabcc
        xyzabaaaaabxyz
        xyzabxabbxyz
 s1 s2 仅包含小写字母，s1可以包含重复字母
 连续要求的实现：right指针需要一直走到包含全部s1字符的地方，先不管s1是否连续
 然后left直接到达距离right仅 s1.length 的地方，直接判断此时窗口内是否覆盖s1就可以了（因为是s1的排列，所以窗口长度保持s1长度就可以了）
 是否覆盖的判断自然要依靠map的计数结果，但不是直接用map判断，而是在窗口变动时依据map中的计数维护的remainCharCount，这个变量才是是否覆盖的直接判断依据
 测试用例：
 System.out.println(checkInclusion("ab", "eidbaooo"));
 System.out.println(checkInclusion("ab", "eidboaoo"));
 System.out.println(checkInclusion("abb", "xyzababbxyz"));
 System.out.println(checkInclusion("abb", "xyzabxabbxyz"));
 System.out.println(checkInclusion("abb", "xyzabaxbbxyz"));
 */
static boolean checkInclusion(String s1, String s2) {
    Map<Character, Integer> s1CountMap = new HashMap<>();
    for (int i = 0; i < s1.length(); i++) {
        s1CountMap.compute(s1.charAt(i), (key, count) -> s1CountMap.getOrDefault(key, 0) + 1);
    }
    int left = 0,right = 0;
    Map<Character,Integer> windowCountMap = new HashMap<>();
    int remainCharCount = s1CountMap.size();
    while (right < s2.length()){
        char rightCursorChar = s2.charAt(right);
        if (!s1CountMap.containsKey(rightCursorChar)){
            right++;
            continue;
        }
        Integer windowCount = windowCountMap.compute(rightCursorChar, (key, count) -> windowCountMap.getOrDefault(key, 0) + 1);
        if (windowCount.equals(s1CountMap.get(rightCursorChar))){
            remainCharCount -= 1;
        }
        right++;
        if (remainCharCount == 0){
            while(right - left > s1.length()){
                char leftCursorChar = s2.charAt(left);
                left++;
                if (windowCountMap.containsKey(leftCursorChar)){
                    Integer windowRemainCount = windowCountMap.get(leftCursorChar);
                    if (windowRemainCount.equals(s1CountMap.get(leftCursorChar))){
                        remainCharCount++;
                    }
                    windowCountMap.put(leftCursorChar, windowRemainCount-1);
                }
            }
// remainCharCount 可以拿来判断当前window中是否完全覆盖s1，根本不需要两个map for循环比较，极大地简化了代码
//            boolean allmatch = true;
//            for (Map.Entry<Character, Integer> entry : s1CountMap.entrySet()) {
//                if (!windowCountMap.get(entry.getKey()).equals(entry.getValue())){
//                    allmatch = false;
//                    break;
//                }
//            }
            if (remainCharCount == 0) return true;
        }
    }
    return false;
}
/**
 * 如果能意识到这是一个 定长窗口滑动 的问题，代码可以再简化
 */
static boolean checkInclusion2(String s1, String s2) {
    Map<Character, Integer> s1CountMap = new HashMap<>();
    for (int i = 0; i < s1.length(); i++) {
        s1CountMap.compute(s1.charAt(i), (key, count) -> s1CountMap.getOrDefault(key, 0) + 1);
    }
    int left=0,right=0;
    Map<Character, Integer> windowCountMap = new HashMap<>();
    int matchedDistinctCharCount = 0;
    while (right < s2.length()){
        char rightCursorChar = s2.charAt(right);
        //定长窗口的话，right不能飞指针,每次窗口只移出一个字符，所以后面的窗口判断可以直接把while替换成if
//        if (!s1CountMap.containsKey(rightCursorChar)){
//            right++;
//            continue;
//        }
        if (s1CountMap.containsKey(rightCursorChar)){
            Integer windowCount = windowCountMap.compute(rightCursorChar, (key, count) -> windowCountMap.getOrDefault(key, 0) + 1);
            if (windowCount.equals(s1CountMap.get(rightCursorChar))){
                matchedDistinctCharCount++;
            }
        }
        right++;//写在这里 [left,right) 窗口左闭右开，窗口长度就是 left-right, 下一行就要 while((right - left) >= s1.length()) 或者 if((right - left) == s1.length()) 或者 if((right - left) >= s1.length())
        //定长窗口，left开始跟随right, 走一步看一下这个定长窗口中的字符串是否覆盖s1
        if((right - left) == s1.length()){ //这行代码控制窗口的宽度，但具体怎么控制还跟 right++;的位置有关
            if (matchedDistinctCharCount == s1CountMap.size()) return true;
            char leftCursorChar = s2.charAt(left);
            if (s1CountMap.containsKey(leftCursorChar)){
                Integer windowRemainCount = windowCountMap.get(leftCursorChar);
                if (windowRemainCount.equals(s1CountMap.get(leftCursorChar))){
                    matchedDistinctCharCount--;
                }
                windowCountMap.put(leftCursorChar, windowRemainCount-1);
            }
            left++;
        }
//        right++; 写在这里，窗口是闭区间 [left,right] 就要 while((right - left + 1) >= s1.length()) 或者 if((right - left + 1) == s1.length())
    }
    return false;
}
/** LC438 目标字符串中的相同字母的同异序词的startIndex收集
 *  s= cbaebabacd, p= abc => [0,6]
 *  abab,ab => [0,1,2]
 *  这是一个定长滑动窗口问题
 *  测试用例
 System.out.println(findAnagrams("cbaebabacd", "abc"));
 System.out.println(findAnagrams("cbaefbabacd", "abc"));
 System.out.println(findAnagrams("abab", "ab"));
 */
static List<Integer> findAnagrams(String s, String p) {
    List<Integer> answer = new ArrayList<>();
    Map<Character, Integer> pMap = new HashMap<>();
    for (int i = 0; i < p.length(); i++) {
        pMap.compute(p.charAt(i), (key, count) -> pMap.getOrDefault(key, 0) + 1);
    }
    Map<Character, Integer> sMap = new HashMap<>();
    int left=0,right=0;
    int coverCount = 0;
    while (right < s.length()){
        char rightChar = s.charAt(right);
        right++;
        if (!pMap.containsKey(rightChar)){
            continue;
        }
        sMap.compute(rightChar, (key,count) -> sMap.getOrDefault(key, 0) + 1);
        if (sMap.get(rightChar).equals(pMap.get(rightChar))){
            coverCount++;
        }
        while (left+p.length() <= right){
            char leftChar = s.charAt(left);
            if (pMap.containsKey(leftChar)) {
                if ((left+p.length()) == right && coverCount == pMap.size()){
                    answer.add(left);
                }
                if (sMap.get(leftChar).equals(pMap.get(leftChar))) {
                    coverCount--;
                }
                sMap.put(leftChar, sMap.get(leftChar) - 1);
            }
            left++;
        }
    }
    return answer;
}
/** LC3 最长无重复子串
 * abcabcbb -> 3
 * bbbb -> 1
 * pwwkew -> 3
 * pwwwwwwked
 * 连续不重复的字符串，窗口右侧扩展遇到重复字符right就停下来
 * 测试用例
 System.out.println(lengthOfLongestSubstring("abcabcbb"));
 System.out.println(lengthOfLongestSubstring("bbbb"));
 System.out.println(lengthOfLongestSubstring("pwwkew"));
 System.out.println(lengthOfLongestSubstring("pwwwwwwked"));
 System.out.println(lengthOfLongestSubstring("wpwwwked"));
 //right走到重复字符上left不能直接跳到right，这个是例子，需要逐个前进
 System.out.println(lengthOfLongestSubstring("dvwxdf"));
 System.out.println(lengthOfLongestSubstring("dvwxdfgh"));
 */
static int lengthOfLongestSubstring(String s) {
    if (s == null || s.length() == 0){
        return 0;
    }
    int res=1;
    int left=0,right=0;
    Set<Character> memos = new HashSet<>();
    while (right < s.length()){
        char rightChar = s.charAt(right);
        while (memos.contains(rightChar)){
            memos.remove(s.charAt(left));
            left++;
        }
        res = Math.max(res, right-left + 1);
        memos.add(rightChar);
        right++;
    }
    return res;
}
/**
 * 总结
滑动窗口算法应用的关键是要确定：
 什么时候应该扩大窗口
 什么时候应该缩小窗口
 什么时候应该更新答案
 */
    public static void main(String[] args) {

    }
}
