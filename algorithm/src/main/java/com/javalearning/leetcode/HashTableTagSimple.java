package com.javalearning.leetcode;

import org.junit.Test;

import java.math.BigInteger;

public class HashTableTagSimple {
    /**
     * 136 只出现一次的数字 I
     *  官解：
     *  一般解法：使用集合记录数组中出现的数字，和的2倍与原数组和相减（OC=O(n)）
     *  不使用额外空间，常数时间复杂度：异或运算 a XOR b XOR b = a
     */
    public int singleNumber(int[] nums) {
        int single = 0;
        for(int num : nums){
            single = single ^ num;//BigInteger#xor也可以做异或
        }
        return single;
    }
    @Test
    public void test136() {
        BigInteger integer = new BigInteger("34");

    }
}
