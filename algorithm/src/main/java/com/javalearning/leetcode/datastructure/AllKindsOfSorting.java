package com.javalearning.leetcode.datastructure;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.Arrays;

/**
  ref https://www.runoob.com/w3cnote/ten-sorting-algorithm.html
 */
public class AllKindsOfSorting {
    static int[] nums = {34, 18, 54, 5, 4, 69, 99, 98, 54, 56};
    static int[] nums2 = {13,12,14,17,18,16};
    /**
    排序名称    tc    besttc    worsttc   sc    stability
    bubble    n^2      n         n^2     1        Y
    select    n^2     n^2        n^2     1        N (54放前面万一被最后一个最小元素替换了)
    insert    n^2      n         n^2     1        Y (也可以改成不稳定的，改成 >=)
    shell     nlogn
    merge     nlogn   nlgn       nlgn    n        Y
    quick     nlogn   nlogn      n^2    lgn       N
    heap      nlogn                      1        N


     bubble可以加个flag，在剩下的元素里已经有序时就不用再处理了
     select最好的情况也是O(n^2),不知道后面会不会出现更小的元素
     insert
     shell是一种带有不断缩小的step的insert排序
     */
    public static void main(String[] args) {
        //bubbleSort(nums);
        //bubbleSort(nums2);
        //selectSort(nums);
        //selectSort(nums2);
        //insertSort(nums);
        //insertSort(nums2);
        //shellSort(nums);
        //shellSort(nums2);
        //System.out.println(Arrays.toString(mergeSort(nums, 0, nums.length)));
        //System.out.println(Arrays.toString(mergeSort(nums2, 0, nums2.length)));
        //quickSort(nums, 0, nums.length-1);
        //quickSort(nums2, 0, nums2.length-1);
        //heapSort(nums);
        //heapSort(nums2);
        //System.out.println(Arrays.toString(nums));
        //System.out.println(Arrays.toString(nums2));

        ArrayList<Object> objects = new ArrayList<>();
        String join = Joiner.on(",").join(objects);
        System.out.println(join.length());
    }

    /**
     多次比较两个相邻元素，如果顺序错了，就调整他们
     */
    static void bubbleSort(int[] nums){
        for (int i = nums.length-1; i > 0 ; i--) {
            boolean flag = false;
            for (int j = 0; j < i; j++) {
                if (nums[j]>nums[j+1]){
                    int temp = nums[j];nums[j] = nums[j+1];nums[j+1] = temp;
                    flag = true;
                }
            }
            if (!flag){
                break;
            }
        }
    }
    /**
     先找出最小的，放在前面；在剩下的找最小的，也放在前面；
     待排序元素一个一个减少
     */
    static void selectSort(int[] nums){
        for (int i = 0; i < nums.length; i++) {
            int min = nums[i], minIndex = i;
            for (int j = i+1; j < nums.length; j++) {
                if (min > nums[j]){
                    min = nums[j];minIndex=j;
                }
            }
            nums[minIndex]=nums[i];nums[i]=min;
        }
    }
    /**
     一组麻将牌一字排开，上面写有数字，排个序才好用尽可能小的牌押对面的牌
     通常会不断从后面拿一块插入到前面的正确位置，插入式有一些麻将要统一向后挪一挪（代码里是一个一个挪的）
     2 1
     0 1 i=1,
     */
    static void insertSort(int[] nums){
        for (int i = 1; i < nums.length; i++) {
            int curr = nums[i];
            int j = i-1;
            while (j>=0 && nums[j] > curr){
                nums[j+1]=nums[j];//逐个往前比较时顺便挪位置
                j--;
            }
            nums[j+1]=curr;
        }
    }
    /**
     归并排序：分治，递归不断减半，最后剩两个元素比较，递归返回过程中比较前后两半进行merge
     merge排序写法类似二叉树后续遍历
     int[] a = {1,3,5,7};
     int[] b = {4,6,8,9,10};
     int[] merge = merge(a, b);
     System.out.println(Arrays.toString(merge));
     mergeSort要改成[start,end]比较难，按[)写容易实现
     */
    static int[] mergeSort(int[] nums, int start, int end){ //注意start end 是[start,end)所以传参end是nums.length
        if (end - start < 2){
            return Arrays.copyOfRange(nums, start, end);//只含nums[start]一个元素的数组,copyOfRange是前闭后开的
        }
        int middle = (start+end) / 2;
        int[] left = mergeSort(nums, start, middle);//[start,end)
        int[] right = mergeSort(nums, middle, end);
        return merge(left, right);
    }
    //两个有序数组如何合并到一起
    static int[] merge(int[] left, int[] right){
        int[] result = new int[left.length + right.length];
        int i = 0,j = 0,curr = 0;
        while (i < left.length && j < right.length){
            result[curr++] = left[i] < right[j] ? left[i++] : right[j++];//递归到两个元素比较大小时才产生排序的效果
        }
        while (i<left.length){
            result[curr++] = left[i++];
        }
        while (j<right.length){
            result[curr++] = right[j++];
        }
        return result;
    }
    /**
     最坏情形tc=O(n^2),但这种情形少见，快排优于其他类型的O(nlgn)算法，其
     分治法，本质上看，快速排序算是在冒泡排序的基础上的递归分治
     Collections.sort使用归并排序，mergeSort比较稳定；Arrays.sort使用快速排序
     挑一个元素作为pivot，所有小的放左边，大的放右边，一次操作就是一个分区。递归地将两边的子数列进行排序
     总体框架类似二叉树的前序遍历
     quickSort的区间都是采用[start,end],与mergeSort不同，不记住容易写出bug
     */
    static void quickSort(int[] nums, int start, int end){
        if (start < end){
            int partition_index = partition2(nums, start, end);
            quickSort(nums, start, partition_index - 1);
            quickSort(nums, partition_index+1, end);
        }
    }
    //教科书上的一个写法，发生交换时会改变比较扫描的方向，极难理解掌握
    static int partition(int[] nums, int start, int end){
        int pivot_index = start;
        while (start < end){
            while (start < end && nums[end] >= nums[pivot_index]){
                end--;
            }
            nums[start] = nums[end];
            while (start < end && nums[start] <= nums[pivot_index]){
                start++;
            }
            nums[end] = nums[start];
        }
        nums[start] = nums[pivot_index];
        return start;
    }
    //简单考虑就是把比我小的交换到前面，最后将pivot于最后一个小的元素替换，这样就实现了枢轴左右大小区分了
    static int partition2(int[] nums, int start, int end) {
        int pivot_index = start;
        int scan_index = pivot_index + 1;//记录比pivot小的最远index，index之前的都是要放在pivot之前的
        for (int i = scan_index; i <= end; i++) {
            if (nums[i] < nums[pivot_index]){
                if (i != scan_index){
                    int tmp = nums[i];nums[i]=nums[scan_index];nums[scan_index]=tmp;//交换i scan
                }
                scan_index++;
            }
        }
        scan_index--;//scan_index停在第一个大于pivot的元素上
        int tmp = nums[pivot_index];nums[pivot_index]=nums[scan_index];nums[scan_index]=tmp;//交换pivot与scan的前一个元素
        return scan_index;
    }

    /**
    堆排序也极难默写
     */
    static void heapSort(int[] nums){
        buildMaxHeap(nums);
        int len = nums.length;
        for (int i = len - 1; i > 0; i--) {//?????
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
            heapify(nums,parent,len);
        }
    }
    /**
     先分成两半，前一半的起始元素与后一半的起始元素比较，逐个向后进行，这个一半就是step
     step缩小一半再来一次
     34, 18, 54, 5, 4, 69, 99, 98, 54, 56  - 第一轮step=5 -> 0-5;1-6;2-7;3-8;4-9;
     - 第二轮step=2 -> 一次进行比较的元素：0-2;1-3;2-4;0-4; 3-5;4-6;5-7;6-8;4-8;7-9;5-9;3-9;
     - 第三轮step=1 -> 0-1;1-2;2-3;1-3; 3-4;4-5;5-6;4-6;6-7;7-8;8-9;7-9;0-3;1-4;2-5;0-1;1-2;
     。。。 图示见 https://www.cnblogs.com/chengxiao/p/6104371.html
     */
    static void shellSort(int[] nums){
        int len = nums.length;
        int temp;
        for (int step = len/2; step >= 1 ; step /= 2) {
            for (int i = step; i < len; i++) {
                temp = nums[i];
                int j = i - step;
                while (j >= 0 && nums[j] > temp){
                    nums[j+step] = nums[j];//交换或移动都可以
                    j-=step;
                }
                nums[j+step] = temp;
            }
        }
    }
    /**
     基数排序
     */
}
