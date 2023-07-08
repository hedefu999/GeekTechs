package com.javalearning.leetcode;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.LinkedList;

public class QueueTagHard {
    /**
     * 求滑动窗口每次移动的最大值，以数组形式返回
     */
    /**
     * 使用双端队列：
     * 双端队列顺序存放数组下标的，滑动窗口向后移动，新进入的结点index放到队尾，移出窗口的结点从队首移出
     * 最多只能放3个结点index，新进入的结点如果比队尾的大则把队尾去掉，重复此过程，不论是否比队尾大，都会被放入队尾
     * 比其他结点大就可以移除其他结点，比其他结点小就只能先存着，说不定后面会成为最大，只要保证队首始终是最大的就可以了
     *
     * clean_deque功能：
     * 判断滑动窗口是否不再覆盖deq中的头结点，如果是，要把头结点移除掉；
     * 判断新结点是否比末尾结点大，是就把末尾结点去掉
     *
     * 第一个for循环是初始化操作，第二个for循环才是处理大部分数据
     *
     * deq的变化情况：
     * 0
     * 1
     * 1 2
     * 1 2 3
     * 4
     * 4 5
     * 6
     * 7
     * output就是每次deq的首结点对应的数组元素
     */
    ArrayDeque<Integer> deq = new ArrayDeque<>();
    private void clean_deque(int[] nums, int i, int k){
        if (!deq.isEmpty() && deq.getFirst() == i-k){
            deq.removeFirst();
        }
        while (!deq.isEmpty() && nums[i] > nums[deq.getLast()]){
            deq.removeLast();
        }
    }
    public int[] solution(int[] nums, int k){
        int length = nums.length;
        if (length*k == 0) return new int[0];
        if (k == 1) return nums;

        int max_idx = 0;
        for (int i = 0; i < k; i++) {
            clean_deque(nums,i,k);
            deq.addLast(i);
            if (nums[i] > nums[max_idx])
                max_idx = i;
        }
        int[] output = new int[length-k+1];
        output[0] = nums[max_idx];
        for (int i = k; i < length; i++) {
            clean_deque(nums,i,k);
            deq.addLast(i);
            output[i-k+1] = nums[deq.getFirst()];
        }
        return output;
    }

    /**
     * 上面实现方式有点繁琐
     */
    public int[] maxSlidingWindow(int[] nums, int k) {
        if(nums==null||nums.length<2) return nums;
        LinkedList<Integer> list = new LinkedList();
        int[] result = new int[nums.length-k+1];
        for(int i=0;i<nums.length;i++){
            // 保证从大到小 如果前面数小 弹出
            while(!list.isEmpty()&&nums[list.peekLast()]<=nums[i]){
                list.pollLast();
            }
            // 添加当前值对应的数组下标
            list.addLast(i);
            // 初始化窗口 等到窗口长度为k时 下次移动在删除过期数值
            if(list.peek()<=i-k){
                list.poll();
            }
            // 窗口长度为k时 再保存当前窗口中最大值
            if(i-k+1>=0){
                result[i-k+1] = nums[list.peek()];
            }
        }
        return result;
    }

    /**
     * 下述声称采用了动态规划的算法
     * 根据k将nums分成num/k+1个小块
     * 两个数组left right长度与nums相同，left从左向右获取块内最左侧到left[i]的最大值，right从右向左获取块内最右侧到right[j]的最大值
     * 比较left[i] 与 right[i-2] 作为 i-2到i 这个窗口内的最大值（i从2到7，2是k-1,7是nums.length-1）
     * 当滑动窗口跨两个小块时这种算法也能应对
     * left数组是133-35567，right数组是33-155377
     */
    public int[] solutionByDP(int[] nums, int k){
        int n = nums.length;
        if (n * k == 0) return new int[0];
        if (k == 1) return nums;

        int [] left = new int[n];
        left[0] = nums[0];
        int [] right = new int[n];
        right[n - 1] = nums[n - 1];
        for (int i = 1; i < n; i++) {
            // from left to right
            if (i % k == 0) left[i] = nums[i];  // block_start
            else left[i] = Math.max(left[i - 1], nums[i]);

            // from right to left
            int j = n - i - 1;
            if ((j + 1) % k == 0) right[j] = nums[j];  // block_end
            else right[j] = Math.max(right[j + 1], nums[j]);
        }
        int [] output = new int[n - k + 1];
        for (int i = 0; i < n - k + 1; i++)
            output[i] = Math.max(left[i + k - 1], right[i]);

        return output;
    }
    @Test
    public void test239(){
        int[] nums = {1,3,-1,-3,5,3,6,7};
        int k = 3;//滑动窗口大小
        int[] result = solutionByDP(nums,3);
        System.out.println(Arrays.toString(result));
        //[3, 3, 5, 5, 6, 7]
    }
}
