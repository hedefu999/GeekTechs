package com.javalearning.freeresearch;

import org.junit.Test;

import java.util.Arrays;

/**
 * Java API自实现之Array数组相关
 */
public class ArrayAPIImpl {

    //Arrays里提供了一个二分查找方法
    //我的自实现见 @see com.javalearning.leetcode.labuladong._01DynamicProgramme.helper(int, int[], int)
    @Test
    public void test12(){
        int[] top2 = {1,2,4,4,5,7,9};
        int index = Arrays.binarySearch(top2, 0, top2.length, 10);
        //需求：找到第一个比给定元素大的index
        System.out.println(-index-1);

    }

}
