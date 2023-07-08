package com.javalearning.freeresearch;

import org.junit.Test;

/**
 * Java API自实现之String
 */
public class StringAPIImpl {
    /**
     * Java String的indexOf方法源码简化版：
     * @param source  在哪个字符串中查找
     * @param sourceOffset 被查找字符串的起始位置
     * @param sourceCount
     * @param target  要找的子集
     * @param targetOffset 子集的开始
     * @param targetCount
     * @param fromIndex
     * @return
     */
    static int indexOf(char[] source, int sourceOffset, int sourceCount,
                       char[] target, int targetOffset, int targetCount,
                       int fromIndex) {
        if (fromIndex >= sourceCount) {
            return (targetCount == 0 ? sourceCount : -1);
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (targetCount == 0) {
            return fromIndex;
        }
        char first = target[targetOffset];
        int max = sourceOffset + (sourceCount - targetCount);//source需要搜索到的最大偏移量

        for (int i = sourceOffset + fromIndex; i <= max; i++) {
            /* Look for first character. */
            if (source[i] != first) {
                while (++i <= max && source[i] != first);
            }

            /* Found first character, now look at the rest of v2 */
            if (i <= max) {
                int j = i + 1;
                int end = j + targetCount - 1;
                for (int k = targetOffset + 1; j < end && source[j]
                        == target[k]; j++, k++);

                if (j == end) {
                    /* Found whole string. */
                    return i - sourceOffset;
                }
            }
        }
        return -1;
    }
    //jinshihau(0-7 5) shi

    /**
     * indexOf的实现
     * jishnshihua 中查找 shi
     */
    static int indexOf2(String sourceStr,String targetStr){
        char[] source = sourceStr.toCharArray();
        char[] target = targetStr.toCharArray();
        int max = source.length - target.length;
        char first = target[0];
        for (int i = 0; i < max; i++) {
            while (first != source[i] && i++ <= max);
            int j = 0;
            for (; j < target.length; j++) {
                if (target[j] != source[i+j]){
                    break;
                }
            }
            if (j == target.length){
                return i;
            }
        }
        return -1;
    }
    @Test
    public void test179() {
        String source = "jishnshihua";
        String target = "shi";
        String source1 = "jishnshihua";
        String target1 = "shii";
        System.out.println(indexOf2(source1,target1));
    }
    /**
     * 待执行：String#toLowerCase Stirng#toCharArray
     * Character.isLetterOrDigit
     * StirngBuilder#reverse
     */
}
