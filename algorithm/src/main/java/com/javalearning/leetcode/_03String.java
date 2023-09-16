package com.javalearning.leetcode;

public class _03String {
//region stage I

/* LC344 反转字符串
 ***/

/* LC5 最长回文子串
opqabcdcbaxy -> abcdcba
opabccbaxy -> abccba
先找出回文串，再比较长短
确定是否是回文串通常是双指针两端扫描。当不确定回文串位置时，从中心向外扩展更容易找到
注意回文串有奇数和偶数之分，所以两种情形都要单独试验
***/static String longestPalindrome(String str) {
    char[] chars = str.toCharArray();
    String result = "";
    for (int i = 0; i < chars.length-1; i++) {
        String oddPan = extend2GetPanlidrome(chars, i, i);
        result = result.length() > oddPan.length()?result:oddPan;
        String evenPan = extend2GetPanlidrome(chars, i, i + 1);
        result = result.length() > evenPan.length()?result:evenPan;
    }
    return result;
}//从 i j 处向外扩展找回文串，ij相差0或1
static String extend2GetPanlidrome(char[] chars, int i, int j){
    while (i>=0 && j<chars.length && chars[i]==chars[j]){
        i--;j++;
    }
    return new String(chars,i+1,j-i-1);
}



//endregion

    public static void main(String[] args) {
        System.out.println(longestPalindrome("opqpst1abcdcbaxyz3849xywwyx94wer"));
        System.out.println(longestPalindrome("qwertyuiop"));
    }

}
