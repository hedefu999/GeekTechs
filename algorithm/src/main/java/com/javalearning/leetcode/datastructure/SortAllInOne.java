package com.javalearning.leetcode.datastructure;

import org.junit.Test;

import java.util.Arrays;

/**
 * 默写排序算法
 */
public class SortAllInOne {
    int array[] = {34, 18, 54, 5, 4, 69, 99, 98, 54, 56};
    /**
     * 冒泡排序
     */
    public void bubbleSort(int[] a){
        for (int i = 0; i < a.length; i++) {
            for (int j = i+1; j < a.length; j++) {
                if (a[i] > a[j]){
                    int temp = a[i];
                    a[i] = a[j];
                    a[j] = temp;
                }
            }
        }
    }
    @Test
    public void test26(){
        bubbleSort(array);
        System.out.println(Arrays.toString(array));
    }
    /**
     * 插入排序
     */
    void insertSort(int[] a){
        for (int i = 0; i < a.length-1; i++) {
            int temp = a[i+1];
            for (int j = 0; j <= i; j++) {
                if (a[j]>temp){
                    a[i+1] = a[j];
                    a[j] = temp;
                    temp = a[j+1];
                    a[j+1] = a[i+1];
                }
            }
            a[i+1] = temp;
        }
    }
    void insertSort2(int[] a){
        for (int i = 1; i < a.length; i++) {
            int temp = a[i];
            int j = i-1;
            for (; j >=0 ; j--) {
                if (a[j] > temp){
                    a[j+1] = a[j];
                }else {
                    break;
                }
            }
            a[j+1] = temp;
        }
    }
    void insertSort21(int[] a){
        for (int i = 1; i < a.length; i++) {
            int temp = a[i];
            int j = i-1;
            for (; j >=0 && a[j] > temp; j--) {
                a[j+1] = a[j];
            }
            a[j+1] = temp;
        }
    }
    void insertSort2X1(int[] a){
        for (int i = 1; i < a.length; i++) {
            int temp = a[i];
            for (int j = i-1; j >=0 ; j--) {
                if (a[j] > temp){
                    a[j+1] = a[j];
                }else {
                    a[j+1] = temp;//A
                    break;
                }
            }
            a[0] = temp;//B AB似乎有关联，B在break发生时是错误的
        }
    }

    //
    public void insertSortREdition(int[] a){
        for (int i = 0; i < a.length-1; i++) {
            int temp = a[i+1];
            int j = i;
            for (; j >= 0 ; j--) {
                if (temp > a[j]){
                    //a[j+1] = temp;//首次出现应当放在后面的情况，就放在后面，并且break，优化时发现可以略去
                    break;
                }else {
                    a[j+1] = a[j];//j一致做后移的动作，在temp始终小的情况下  挪！
                }
            }
            a[j+1] = temp;//都向后移动完了，最前面剩下的坑就是这个temp,
            // 一般理解这里应该是a[0] = temp; 但进入这里有可能是break过来的,等效的写法是
            //if (j == -1){
            //    a[0] = temp;
            //}else {
            //    //此时
            //     a[j+1] = temp;
            //} 这种写法可以简化
        }
    }

    @Test
    public void test25(){
        int array[] = {34, 18, 54, 5, 4, 69, 99, 98, 54, 56};
        //insertSort21(array);
        insertSortREdition(array);
        System.out.println(Arrays.toString(array));
    }

    /**
     * 归并排序 https://www.cnblogs.com/chengxiao/p/6194356.html
     */
    public void mergeSort(int[] arr,int left,int right,int[] temp){
        if (left < right){
            int middle = (left+right)/2;
            mergeSort(arr, left, middle, temp);
            mergeSort(arr, middle, right, temp);
            merge(arr,left,middle,right,temp);
        }
    }
    public static void merge(int[] arr,int left,int mid,int right,int[] temp){
        int i = left;//左序列指针
        int j = mid+1;//右序列指针
        int t = 0;//临时数组指针
        while (i<=mid && j<=right){
            if(arr[i]<=arr[j]){
                temp[t++] = arr[i++];
            }else {
                temp[t++] = arr[j++];
            }
        }
        while(i<=mid){//将左边剩余元素填充进temp中
            temp[t++] = arr[i++];
        }
        while(j<=right){//将右序列剩余元素填充进temp中
            temp[t++] = arr[j++];
        }
        t = 0;
        //将temp中的元素全部拷贝到原数组中
        while(left <= right){
            arr[left++] = temp[t++];
        }
    }
    @Test
    public void test101(){
        int[] arr = {3,2};
        int[] temp = new int[10];
        merge(arr,0,0,1,temp);
    }
    /**
     * 快速排序
     */
    public int oneTurn(int[] a,int i,int j){

        int pivot = a[i];
        while (i<j){
            while (i<j && pivot<=a[j]){
                j--;
            }
            a[i] = a[j];
            while (i<j && pivot>=a[i]){
                i++;
            }
            a[j] = a[i];
        }
        a[i] = pivot;
        return i;
    }
    @Test
    public void test178(){
        int[] a = {49,38,65,97,76,13,27};
        System.out.println(oneTurn(a,0,a.length-1));
        System.out.println(Arrays.toString(a));
    }
    public void quickSort(int[] a,int low,int high){
        if (low<high){//??判断条件的确定
            int middle = oneTurn(a, low, high);
            quickSort(a,low,middle-1);//middle不加减1会出StackOverflow
            quickSort(a,middle+1,high);
        }
    }
    @Test
    public void test192(){
        int[] a = {49,38,65,97,76,13,27};
        quickSort(a,0,a.length-1);
        System.out.println(Arrays.toString(a));
    }
    /**
     * 补充《JAVA核心面试知识整理》：桶排序算法
     */
}
