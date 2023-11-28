package com.javalearning.leetcode;

import java.util.HashMap;
import java.util.Map;
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
        if (target.containsKey(rightCursorChar) && rightCursorCharCount.equals(target.get(rightCursorChar))){
            allTargetCharContainsCount++;
        }
        right++;
        //这个判断在while内层，出来后还要继续right++做判断，案例：在 ADBEFCHABC 中寻找ABC
        //此时left可以前进了
        if (allTargetCharContainsCount == target.size()){
            while (left <= right-target.size()){
                char leftCursorChar = s.charAt(left);
                if (!target.containsKey(leftCursorChar)){//空间优化：window可以只记录target中的元素
                    left++;
                    continue;
                }
                Integer leftCursorCharCount = window.get(leftCursorChar);
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

    public static void main(String[] args) {

        System.out.println(minWindow("ADBBEAFCHABRCD","ABC"));
        System.out.println(minWindow("ADBBEACHABRCD","ABC"));
        System.out.println(minWindow("ADBBECHABRCD","ABC"));
        System.out.println(minWindow("ADBBCHABRCD","ABC"));
        System.out.println(minWindow("ADBCHABRCD","ABC"));
        System.out.println(minWindow("ADBCHABCD","ABC"));
        System.out.println(minWindow("ADBCHARCD","ABC"));
        System.out.println(minWindow("a","a"));
        System.out.println(minWindow("a","aa"));
    }
}
