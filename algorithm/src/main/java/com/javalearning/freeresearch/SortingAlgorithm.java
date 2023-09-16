package com.javalearning.freeresearch;

import com.javalearning.leetcode.utils.PrintUtils;

/**
排序相关算法合集
排序算法合集页 https://www.runoob.com/w3cnote/ten-sorting-algorithm.html
排序算法特性
 排序名称    tc    besttc    worsttc   sc    stability
 bubble    n^2      n         n^2     1        Y
 select    n^2     n^2        n^2     1        N (54放前面万一被最后一个最小元素替换了)
 insert    n^2      n         n^2     1        Y (也可以改成不稳定的，改成 >=)
 shell     nlogn
 merge     nlogn   nlogn     nlogn    n        Y
 quick     nlogn   nlogn      n^2    lgn       N
 heap      nlogn                      1        N
 */
public class SortingAlgorithm {
    static int[] SORTS_01 = {34, 18, 54, 5, 4, 69, 99, 98, 54, 56};
    static int[] SORTS_02 = {13,12,14,17,18,16};
    static int[] SORTS_03 = {1,2,4,3};
    static int[] SORTS_04 = {1,2,3,4,5};
    static int[] SORTS_05 = {0};

    static int[][] SORTS = {SORTS_01,SORTS_02,SORTS_03,SORTS_04,SORTS_05};

//region 基础排序算法：十大经典排序算法

/* 冒泡排序 比较指针附近相邻的两个元素的大小，交换顺序
最佳时间复杂度可以降低到O(N)
**///最大的元素放在最后
static void bubbleSort(int[] nums){
    for (int i = 0; i < nums.length; i++) {
        boolean change =false;
        for (int j = 0; j < nums.length - 1 - i; j++) {
            if (nums[j] > nums[j+1]){
                int temp=nums[j];nums[j]=nums[j+1];nums[j+1]=temp;
                change = true;
            }
        }
        if (!change) break;
    }
}
//最小元素放前面
static void bubbleSort2(int[] nums){
    for (int i = nums.length-1; i >= 0; i--) {
        boolean change = false;
        for (int j = nums.length-1; j > nums.length-i-1 ; j--) {
            if (nums[j] < nums[j-1]){
                int temp=nums[j];nums[j]=nums[j-1];nums[j-1]=temp;
                change=true;
            }
        }
        if (!change)break;
    }
}

/* 选择排序 在剩下待排序元素中找到最小的放到前面
优化：有可能不需要进行交换，待比较元素本就是最小的，但算法最佳复杂度依然是O(N^2)
**/static void selectSort2(int[] nums){
    for (int i = 0; i < nums.length-1; i++) {
        int minIndex=i,minVlue=nums[i];
        for (int j = i+1; j < nums.length; j++) {
            if (minVlue>nums[j]){
                minVlue=nums[j];minIndex=j;
            }
        }
        if (minIndex != i){//交换数值
            nums[minIndex]=nums[i];nums[i]=minVlue;
        }
    }
}
static void selectSort(int[] nums){
    for (int i = 0; i < nums.length - 1; i++) {
        //找出最小的数放在i位置上
        for (int j = i+1; j < nums.length; j++) {
            if (nums[j] < nums[i]){
                int temp = nums[i];nums[i]=nums[j];nums[j]=temp;
            }
        }
    }
}

/* 插入排序
类似于对一摞扑克牌进行排序，将小的牌放在上面，方便找小牌押对方，排序方法是从底部抽牌插到上面牌堆的正确位置
需要数组进行一系列元素的挪动，但这种挪动有技巧：是从前部已排序元素往前找适合的插入位置，向前比较的过程进行元素的移动
**/static void insertSort(int[] nums){
    for (int i = 0; i < nums.length-1; i++) {
        //i划定已排序元素的范围，j从第二个元素开始
        int j = i;
        int current = nums[i+1];
        while (j>=0 && current<nums[j]){//逐个元素比较时顺带依次挪动元素位置
            nums[j+1]=nums[j];
            j--;
        }
        nums[j+1]=current;
    }
}

/*-=-=-=-=-=-=- 稍复杂的排序 -=-=-=-=-=-=-=-*/
/** 归并排序 merge
- 分治思想，缩小排序数组的规模，通过递归调用实现排序，递归返回时只比较两个元素的大小
- 写法类似 二叉树 后序 遍历
- 合并两个已排序的数组,作为归并排序的收尾方法
**/static int[] mergeTwoSortedArray(int[] left, int[] right){
    int[] result=new int[left.length+right.length];
    int i = 0;int j = 0;int k=0;
    while (i<left.length && j<right.length){
        result[k++]=left[i]<right[j]?left[i++]:right[j++];
    }
    while (i<left.length){
        result[k++]=left[i++];
    }
    while (j<right.length){
        result[k++]=right[j++];
    }
    return result;
}
// 采用 闭区间[start,end] 写法,数组会被分治到只有一个元素
static int[] mergeSortWithClosedInterval(int[] nums, int start, int end){
    if (start >= end){
        return new int[]{nums[end]};
    }
    int middle = (start + end)/2;
    int[] left = mergeSortWithClosedInterval(nums, start, middle);
    int[] right = mergeSortWithClosedInterval(nums, middle + 1, end);
    return mergeTwoSortedArray(left, right);
}
static int[] mergeSort(int[] nums){
    return mergeSortWithClosedInterval(nums, 0, nums.length-1);
}

/** 快速排序
- JDK中 Collections.sort使用归并排序，mergeSort比较稳定；Arrays.sort使用快速排序
- 实现过程：从数组中挑出一个基准pivot元素，小于它的元素放左侧，大于它的放右侧，这样再分别对左右两侧元素应用相同的操作
- 写法类似 二叉树 前序 遍历
- 快排实现的关键：分区方法，闭区间
*/
static int partition(int[] nums, int start, int end){
    int pivot = start;//将第一个元素作为枢轴，也可以选最后一个
    //重新框定扫描的范围
    int lowerBoundary = pivot + 1;
    for (int i = lowerBoundary; i<=end;i++){
        if (nums[i] < nums[pivot]){
            //交换 lowBoundary 跟 游标i 指向的元素(加个if判断可以避免i=lowerBoundary时的交换)
            if (i != lowerBoundary){
                int temp=nums[i];nums[i]=nums[lowerBoundary];nums[lowerBoundary]=temp;
            }
            lowerBoundary++;
        }
    }
    //此时lowBoundary左侧的元素都是小于pivot的，替换为pivot就可以将数组分成pivot左右两部分
    lowerBoundary--;//!
    if (pivot != lowerBoundary){
        int temp = nums[pivot];nums[pivot]=nums[lowerBoundary];nums[lowerBoundary]=temp;
    }
    return lowerBoundary;
}
static void quickSortWithClosedInterval(int[] nums, int start, int end){
    if (start >= end){
        return;
    }
    int pivotIndex = partition(nums, start, end);
    quickSortWithClosedInterval(nums, start, pivotIndex-1);
    quickSortWithClosedInterval(nums, pivotIndex+1, end);
}
static void quickSort(int[] nums){
    //注意是闭区间，要减1
    quickSortWithClosedInterval(nums, 0, nums.length-1);
}

/*-=-=-=-=-=-=- 默写起来有难度的排序，建议只掌握思路 -=-=-=-=-=-=-=-*/
/** 堆排序
- 需要先建一个堆，升序排列要创建大顶堆（每个节点的值都 >= 子节点的值），是一个近似完全二叉树的结构
- 对于数组 {34, 18, 54, 5, 4, 69, 99, 98, 54, 56}
 先建一个堆
           34
      18       54
   5     4   69  99
 98 54 56
- 操作数组时，父节点nums[i]的左右子节点分别是 nums[2i+1]、nums[2i+2]
- 调整堆，就是让三个节点中最大的成为父节点
- 调整的顺序：要从最底层的节点开始调整，所以i的初始值是 lenght/2=5，就是 69+两个空的子节点-> 4+56+空的右子节点
*/
static void heapSort(int[] nums){
    buildMaxHeap(nums);
    int len = nums.length;
    for (int i = len - 1; i > 0; i--) {
        int tmp=nums[0];nums[0]=nums[i];nums[i]=tmp;//交换0，i
        len --;//每次调整出最大元素在树根，与倒数第len-i个元素交换（依次放在最后），所以最后的元素不参与heapify，len要不断减小
        heapify(nums, 0, len);
    }
}
    static void buildMaxHeap(int[] nums){
        for (int i = nums.length/2; i >= 0; i--) {//前半部分元素位于顶端，所以只需要从这里开始调整结点
            heapify(nums,i,nums.length);
        }
    }
static void heapify(int[] nums, int start, int len){
    int left = 2*start + 1;
    int right = 2*start + 2;
    int parent = start;
    if (left < len && nums[left] > nums[parent]){
        parent = left;
    }
    if (right < len && nums[right] > nums[parent]){
        parent = right;
    }
    if (parent != start){
        int tmp=nums[start];nums[start]=nums[parent];nums[parent]=tmp;//交换parent与start
        heapify(nums,parent,len);//为啥还要递归调整？
    }
}

/** shell排序
 先分成两半，前一半的起始元素与后一半的起始元素比较，逐个向后进行，这个一半就是step
 step缩小一半再来一次
 34, 18, 54, 5, 4, 69, 99, 98, 54, 56  - 第一轮step=5 -> 0-5;1-6;2-7;3-8;4-9; 比较的元素前者大的话会发生交换
 - 第二轮step=2 -> 一次进行比较的元素：0-2;1-3;2-4;0-4; 3-5;4-6;5-7;6-8;4-8;7-9;5-9;3-9;
 - 第三轮step=1 -> 0-1;1-2;2-3;1-3; 3-4;4-5;5-6;4-6;6-7;7-8;8-9;7-9;0-3;1-4;2-5;0-1;1-2;
 。。。 图示见 https://www.cnblogs.com/chengxiao/p/6104371.html
*/
static void shellSort(int[] nums){
    int len = nums.length;
    int temp;
    for (int step = nums.length/2; step >= 1; step=step/2) {
        for (int i = step; i < len; i++) {
            temp = nums[i];
            int j = i - step;
            while (j>=0 && nums[j] > temp){
                nums[j+step] = nums[j];
                j-=step;
            }
            nums[j+step]=temp;
        }
    }
}

/**桶排序算法
 */


public static void main(String[] args) {
    shellSort(SORTS_01);
//    PrintUtils.printIntArray(SORTS_01);
//    int[] a = {1,4,5,9,12,15};
//    int[] b = {2,3,5,7,11,13};
//    PrintUtils.printIntArray(mergeTwoSortedArray(b,a));
    for (int[] sort : SORTS) {
        int[] ints = mergeSort(sort);
        PrintUtils.printIntArray(ints);
    }
}
//endregion



}
