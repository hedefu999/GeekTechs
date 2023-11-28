package com.javalearning.leetcode;

/** 二分查找算法
【参考资料】
我写了首诗，把二分搜索算法变成了默写题 https://labuladong.github.io/algo/di-ling-zh-bfe1b/wo-xie-le--3c789/
【算法框架】
int binarySearch(int[] nums, int target){
 int left=0,right=...;
 while(...){
    int mid=left+(right-left)/2;
    if(nums[mid]==target){
        ...
    }else if(nums[mid] < target){
        left = ...
    }else if(nums[mid] > target){
        right = ...
    }
 }
 return ...;
}
【细节问题】
(left+right)/2 会因相加带来整数溢出问题，改成 left + (right -left)/2 就可以解决
*/
public class _0xBinarySearch {
/** LC704 BinarySearch 在升序整型数组中寻找target，找不到返回-1

*/
static int search(int[] nums, int target) {
    int left = 0, right = nums.length - 1;
    while (left <= right){
        int middle = (left + right) / 2;
        if (nums[middle] == target){
            return middle;
        }else if (nums[middle] < target){
            left = middle+1;
        }else {
            right = middle-1;
        }
    }
    return -1;
}
/* 有序数组里如果有多个target，想在对数级复杂度下查找右侧边界的target
{1,2,3,3,3,5} -> 3
[0,5] - [2,5] - [3,5] - [4,5]
0,5-3,5-5,5
{-1,0,3,5,9,12,13} -> 2
0,6-0,3
*/
@Deprecated
static int searchRightSide(int[] nums, int target){
    int left = 0,right = nums.length - 1;
    while (left < right){
        int middle = (left + right)/2;
        if (nums[middle]==target){
            if (left==middle) return left;
            left=middle;
        }else if (nums[middle]<target){
            left = middle+1;
        }else {
            right=middle-1;
        }
    }
    return nums[left]==target?left:-1;
}
/* 上述写法反例：{1,2,3,3,3,3,5,5,5,5,5},5 总是得到9
修改一些细节处理,使用 (left,right] 左开右闭区间搜索可以得到右边界结果
{1,2,3,3,3,5},3 left冲到right，结果right减1得到正确答案，技巧性非常强
*/
static int searchRightSide2(int[] nums, int target){
    int left=0,right=nums.length-1;
    while (left <= right){
        int mid = left + (right-left)/2;
        if (nums[mid] == target){
            left = mid+1; //为寻找右边界，需要越过mid，这样while结束条件 left>right,right反倒可能是最终答案
        }else if (nums[mid] < target){
            left = mid+1;
        }else {
            right = mid-1;
        }
    }
    if (right<0){//这两种分别是 target小于最左元素 ||right>=nums.length
        return -1;
    }
    return nums[right] == target ? right : -1;
}
static int searchLeftSide(int[] nums, int target){
    int left=0,right=nums.length-1;
    while (left <= right){
        int mid = left + (right-left)/2;
        if (nums[mid] == target){
            right=mid-1;
        }else if (nums[mid] > target){
            right=mid-1;
        }else if (nums[mid] < target){
            left=mid+1;
        }
    }
    if (left>= nums.length) return -1;
    return nums[left]==target?left:-1;
}
//左侧和右侧匹配的二分查找算法与元素不重复的二分查找法，仅在nums[middle]==target 和 完成循环后的两行判断不同，基本可以背下来

public static void main(String[] args) {
    System.out.println(searchLeftSide(new int[]{-1,0,3,5,9,12,13}, 9));//4
    System.out.println(searchLeftSide(new int[]{-1,0,3,5,9,12,13}, 5));//3
    System.out.println(searchLeftSide(new int[]{-1,0,3,5,9,12,13,15}, 9));//4
    System.out.println(searchLeftSide(new int[]{-1,0,3,5,9,12}, 2));//-1
    System.out.println(searchLeftSide(new int[]{-1,0,3,5,9,12}, 8));//-1
    System.out.println(searchLeftSide(new int[]{0,3,5,9,12,13}, -2));//-1
    System.out.println(searchLeftSide(new int[]{0,3,5,9,12,13}, 15));//-1
    System.out.println(searchLeftSide(new int[]{1,2,3,3,3,5},3));//2 4
    System.out.println(searchLeftSide(new int[]{1,2,3,5,5,5},5));//3 5
}
}
