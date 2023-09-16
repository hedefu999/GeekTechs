package com.javalearning.leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class _02Array {
//region 数组中的双指针解法

/* LC26 删除有序数组中的重复项 数组中的快慢指针
递增数组中原地删除重复元素，保持相对顺序
nums = [1,1,2]  输出：2, nums = [1,2,_]
nums = [0,0,1,1,1,2,2,3,3,4]  输出：5, nums = [0,1,2,3,4]
***/static int removeDuplicates(int[] nums) {
    if (nums.length==0) return 0;
    int slow = 0;
    for (int fast=0; fast < nums.length; fast++) {
        if (nums[fast] != nums[slow]){
            slow++;
            nums[slow]=nums[fast];
        }
    }
    return slow+1;
}

/* LC27 移除元素 原地移除无序数组中值为val的元素，返回新数组的长度
nums = [3,2,2,3], val = 3 输出：2, nums = [2,2]
***/static int removeElement(int[] nums, int val) {
    if (nums.length == 0) return 0;
    int index=-1;
    for (int i = 0; i < nums.length; i++) {
        if (nums[i] != val){
            index++;
            nums[index]=nums[i];
        }
    }
    return index+1;
}

/* LC283 移动零
***/static void moveZeroes(int[] nums) {
    if (nums==null || nums.length==0)return;
    int index=-1;
    for (int i = 0; i < nums.length; i++) {
        if (nums[i] != 0){
            index++;
            int temp = nums[i];
            nums[i]=0;
            nums[index]=temp;
        }
    }
}

/* LC167 两数之和 输入非递减有序数组
测试用例保证不会出现两个答案！
寻找两个和为target的数并返回下标（1-based）,时间复杂度应小于 O(N^2)，不要使用双层for循环
证明：二维搜索空间的快速削减，得到O(N)时间复杂度
***/static int[] twoSum(int[] numbers, int target) {
    int left = 0, right = numbers.length - 1;
//    while (numbers[right]>target){
//        right--;
//    } target可能是负数
    //代码很像二分查找法
    while (left < right){
        int sum = numbers[left] + numbers[right];
        if (sum == target){
            return new int[]{left+1,right+1};
        }else if (sum < target){
            left++;
        }else {
            right--;
        }
    }
    return null;
}// 类似此题的搜索空间削减的题还有 11 240

//endregion

//region 排序算法

//endregion

//region 数组中的滑动窗口算法

//endregion

//region stage II

/* LC15 三数之和 和为0
System.out.println(threeSum(new int[]{-4,-1,-1,0,1,2}));//[[-1, -1, 2], [-1, 0, 1]]
System.out.println(threeSum(new int[]{-1,0,1}));//[[-1, 0, 1]]
System.out.println(threeSum(new int[]{0,0,0}));//[[0, 0, 0]]
System.out.println(threeSum(new int[]{0,0,0,0}));//[[0, 0, 0]]
//下述是棘手的mid越过重复值的问题，有些是不能越过的 B
System.out.println(threeSum(new int[]{0,0,0,0,0,0,0,0}));//[[0, 0, 0]]
System.out.println(threeSum(new int[]{-1,-1,0,1,2,10}));//A [-1,-1,2],[-1,0,1]
System.out.println(threeSum(new int[]{-1,0,0,1}));//C [-1, 0, 1]]
System.out.println(threeSum(new int[]{-2,0,1,1,2}));//B [-2, 0, 2]], [-2, 1, 1]
***/static List<List<Integer>> threeSum0(int[] nums) {
    if (nums == null || nums.length < 3)return new ArrayList<>();
    Arrays.sort(nums);
    List<List<Integer>> lists = new ArrayList<>();
    //外层遍历中间指针对于内层相当于固定中间指针，转化成双指针解法
    int left,right;
    for (int mid = 1; mid < nums.length-1; mid++) {
        left = 0;right = nums.length-1;
        while (left < mid && mid < right){
            int sum = nums[left] + nums[mid] + nums[right];
            if (sum == 0){
                lists.add(Arrays.asList(nums[left], nums[mid], nums[right]));
                left++;right--;//找到命中的后记得收缩
            }else if (sum < 0){
                left++;
            }else {
                right--;
            }
            //如果下一个元素与上一个相同，需要跳过，避免重复
            while (left < mid && nums[left] == nums[left+1]){
                left++;
            }
            while (right > mid && right < nums.length-1 && nums[right] == nums[right+1]){
                right--;
            }
            //尝试解决结果集重复问题
//            while (mid < nums.length-1 && nums[mid]==nums[mid+1]){
//                mid++;
//            }
        }
    }
    return lists;
}//上述结果将mid作为遍历索引无法解决结果去重问题，改用对left遍历
static List<List<Integer>> threeSum1(int[] nums) {
    if (nums == null || nums.length < 3)return new ArrayList<>();
    Arrays.sort(nums);
    List<List<Integer>> lists = new ArrayList<>();
    //外层遍历中间指针对于内层相当于固定中间指针，转化成双指针解法
    int mid,right;
    for (int left = 0; left < nums.length - 2; left++) {
        if (nums[left]>0) break; //target=0才可以这样判断，如果target是负数就不可以，如 -4 -3 -2 -1 在target=-5时，-4显然是有机会的
        if (left>0 && nums[left-1] == nums[left]) continue;
        mid = left+1;right = nums.length-1;
        while (mid < right){
            int sum = nums[left] + nums[mid] + nums[right];
            if (sum == 0){
                lists.add(Arrays.asList(nums[left], nums[mid], nums[right]));
                //何时跳过元素？应在收集了一次进行，防止误跳过导致遗漏
                //如果下一个元素与上一个相同，需要跳过，避免重复(注意要向后看，向前看会遗漏，因为里面有++)
                while (mid < right && nums[mid] == nums[mid+1]){
                    mid++;
                }
                while (right > mid && right<nums.length-1 && nums[right] == nums[right+1]){
                    right--;
                }
                mid++;right--;//找到命中的后记得收缩
            }else if (sum < 0){
                mid++;
            }else {
                right--;
            }
        }
    }
    return lists;
}//仿照left遍历写出mid遍历，仍不成功，所以 threeSum问题不能使用mid遍历
static List<List<Integer>> threeSum(int[] nums) {
    if (nums == null || nums.length < 3)return new ArrayList<>();
    Arrays.sort(nums);
    List<List<Integer>> lists = new ArrayList<>();
    //外层遍历中间指针对于内层相当于固定中间指针，转化成双指针解法
    int left,right;
    for (int mid = 1; mid < nums.length-1; mid++) {
        //todo warning 无解,mid不论向前看还有向后看总有特例处理不了，所以不能使用mid遍历，而且不能像left遍历那样 if (nums[left]>0) break;
        if (nums[mid] == nums[mid+1] && mid < nums.length-2) continue;
        left = 0;right = nums.length-1;
        while (left < mid && mid < right){
            int sum = nums[left] + nums[mid] + nums[right];
            if (sum == 0){
                lists.add(Arrays.asList(nums[left], nums[mid], nums[right]));
                //如果下一个元素与上一个相同，需要跳过，避免重复
                while (left < mid && nums[left] == nums[left+1]){
                    left++;
                }
                while (right > mid && right<nums.length-1 && nums[right] == nums[right+1]){
                    right--;
                }
                left++;right--;//找到命中的后记得收缩
            }else if (sum < 0){
                left++;
            }else {
                right--;
            }
        }
    }
    return lists;
}

/* LC18 四数之和
数组nums中有n个数，从中找出4个数nums[a]、nums[b]、nums[c]、nums[d](a b c d不重复)和为target
仍然采用三数之和的解法，只不过此时要固定左边的两个游标。时间复杂度 O(N^3)
System.out.println(fourSum(new int[]{-2,-1,0,0,1,2},0));
System.out.println(fourSum(new int[]{2,2,2,2,2},8));
System.out.println(fourSum(new int[]{-4,-1,-1,0,1,2},-1));//[[-4,0,1,2],[-1,-1,0,1]]
System.out.println(fourSum(new int[]{-5,-4,-3,-2,1,3,3,5},-11));//[[-5,-4,-3,1]]
//避免溢出，要么改用long，要么不直接 a+b > c，换成 a-c>-b
System.out.println(fourSum(new int[]{1000000000,1000000000,1000000000,1000000000},-294967296));//[[1000000000,1000000000,1000000000,1000000000]]
***/static List<List<Integer>> fourSum(int[] nums, int target) {
    if (nums == null || nums.length < 4) return new ArrayList<>();
    Arrays.sort(nums);
    List<List<Integer>> result = new ArrayList<>();
    for (int i = 0; i < nums.length - 3; i++) {
        if (i>0 && nums[i]==nums[i-1]) continue;//去重（不必使用while）
        for (int j = i+1; j < nums.length - 2; j++) {
            if (j>i+1 && nums[j]==nums[j-1]) continue;//去重
            long twoSum = nums[i] + nums[j];//官方增加了一个溢出用例
            int left = j+1;int right = nums.length-1;
            while (left < right){
                long fourSum = twoSum + nums[left] + nums[right];
                if (fourSum == target){
                    result.add(Arrays.asList(nums[i], nums[j], nums[left], nums[right]));
                    left++;right--;
                }else if (fourSum < target){
                    left++;
                }else {
                    right--;
                }
                while (left>j+1 && left<right && nums[left] == nums[left-1]){
                    left++;
                }
                while (right>left && right<nums.length-1 && nums[right] == nums[right+1]){
                    right--;
                }
            }
        }
    }
    return result;
}
// todo fourSum 回溯剪枝解法？


//endregion

    public static void main(String[] args) {

    }
}
