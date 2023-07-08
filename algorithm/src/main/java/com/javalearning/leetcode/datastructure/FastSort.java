package com.javalearning.leetcode.datastructure;

import java.util.ArrayList;
import java.util.List;

public class FastSort {

    public static void main(String[] args) {
        FastSort sort=new FastSort();
        System.out.println("各种排序的代码");
        System.out.print("没排序前的数据   ");
        int array[] = {34, 18, 54, 5, 4, 69, 99, 98, 54, 56};
        //这里不想让所有的方法都是静态的，所以才用对象去调用
        sort.printData(array);
        sort.insertSort();//直接插入排序
        sort.shellSort();//希尔排序
        sort.selectSort();//选择排序
        sort.heapSort();//堆排序
        sort.bubbleSort();//冒泡排序
        sort.quickSort();//快速排序
        sort.mergingSort();//归并排序
        sort.radixSort();//基数排序
    }
    /**
     * 打印数据
     */
    private static  void printData(int[] array) {
        //输出排列好的数组
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i] + "  ");
        }
    }
    public static  void insertSort() {
        System.out.print("\n1.简单插入排序 ：");
        int array[] = {34, 18, 54, 5, 4, 69, 99, 98, 54, 56};

        int temp = 0;

        for (int i = 1; i < array.length; i++) {
            temp = array[i];//选中要插入的数据，每次往下推一个
            int j = i - 1; //这里j，后面需要用到，所以抽出来
            for (; j >= 0 && temp < array[j]; j--) {//第一个条件也是可以写，也可以不写；这个写法我也是比较难想到，但是如果你把条件抽到括号就不对了
                array[j + 1] = array[j];     //将大于temp的值整体后移一个单位
            }
            array[j + 1] = temp;//这里的已经不是i-1，而是看它对比通过了几次后的值
        }
        printData(array);
    }
    public static void shellSort() {
        System.out.print("\n2.     希尔排序：");
        int array[] = {34, 18, 54, 5, 4, 69, 99, 98, 54, 56};
        double d1 = array.length;
        int temp = 0;
        while (true) {
            d1 = Math.ceil(d1 / 2);//四舍五入取值
            int d = (int) d1;//double类型的数据强转成int类型，这是“增量”
            for (int x = 0; x < d; x++) {//数据分组
                for (int i = x + d; i < array.length; i += d) {//各组内的数据进行排序
                    int j = i - d;
                    temp = array[i];
                    for (; j >= 0 && temp < array[j]; j -= d) {//插入数据
                        array[j + d] = array[j];
                    }
                    array[j + d] = temp;
                }
            }
            if (d == 1) //增量为1，并表明所以数据一起排了序，排序完成
                break;
        }
        //输出排列好的数组
        printData(array);
    }

    /**
     * 简单选择排序
     * 这个思想和代码都是比较简单的啦
     * 选择排序就是（从小到大排序）第一次选择所有数据中最小的数据，和第一个数据交换，然后从剩下的所有数据选择最小的放在第二个位置，以此类推，就可以得到排列好的数据。
     */

    public  void selectSort() {
        System.out.print("\n3.简单选择排序： ");
        int array[] = {34, 18, 54, 5, 4, 69, 99, 98, 54, 56};
        int position = 0;
        for (int i = 0; i < array.length; i++) {//遍历所有的数据
            position = i;//要遍历的游标值
            int temp = array[i];//该游标值对应的数值
            for (int j = i + 1; j < array.length; j++) {//遍历剩下的数据中的最小值
                if (array[j] < temp) {//如果temp比剩下的任何一个数据大，就交换，保证temp是最小的
                    temp = array[j];
                    position = j;
                }
            }
            //上面一个for、if保证了temp是剩下数中最小的数，position是最终交换的游标值
            //下面两步是把所有的数据的最小值和剩下数组数据的第一个数组数据数做交换
            array[position] = array[i];
            array[i] = temp;
        }
        //输出排列好的数组
        printData(array);
    }

    /**
     * 堆排序
     * 堆排序不仅思想上是所有排序中最麻烦的，而且代码也是！
     * 说白了堆排序就是选择排序的一种，就是不断选出最大值，最终完成排序。
     * 但是这里涉及到一个二叉树，二叉树是什么呢？二叉树就是一个根有两个节点或一个节点，并且根节点的值一定比支点的值大，就是那么简单。
     * 堆排序就是不断剔除根节点并不断重新建堆的过程，直到只剩下一个节点，就完成所有的排序了
     */
    public  void heapSort() {
        System.out.print("\n4.       堆排序：");
        int array[] = {34, 18, 54, 5, 4, 69, 99, 98, 54, 56};
        int arrayLength = array.length;
        //循环建堆
        for (int i = 0; i < arrayLength - 1; i++) {
            //建堆
            buildMaxHeap(array, arrayLength - 1 - i);
            //交换堆顶和最后一个元素
            swap(array, 0, arrayLength - 1 - i);
//                System.out.println(Arrays.toString(array));//建堆并交换后的数据
        }
        //输出排列好的数组
        printData(array);
    }

    private  void swap(int[] data, int i, int j) {
        int tmp = data[i];
        data[i] = data[j];
        data[j] = tmp;
    }

    //对data数组从0到lastIndex建大顶堆
    private  void buildMaxHeap(int[] data, int lastIndex) {
        //从lastIndex处节点（最后一个节点）的父节点开始
        for (int i = (lastIndex - 1) / 2; i >= 0; i--) {
            //k保存正在判断的节点
            int k = i;
            //如果当前k节点的子节点存在
            while (k * 2 + 1 <= lastIndex) {
                //k节点的左子节点的索引
                int biggerIndex = 2 * k + 1;
                //如果biggerIndex小于lastIndex，即biggerIndex+1代表的k节点的右子节点存在
                if (biggerIndex < lastIndex) {
                    //若果右子节点的值较大
                    if (data[biggerIndex] < data[biggerIndex + 1]) {
                        //biggerIndex总是记录较大子节点的索引
                        biggerIndex++;
                    }
                }
                //如果k节点的值小于其较大的子节点的值
                if (data[k] < data[biggerIndex]) {
                    //交换他们
                    swap(data, k, biggerIndex);
                    //将biggerIndex赋予k，开始while循环的下一次循环，重新保证k节点的值大于其左右子节点的值
                    k = biggerIndex;
                } else {
                    break;
                }
            }
        }
    }


    /**
     * 冒泡排序
     * 冒泡排序也是比较简单的一种，两两比较，这个在逻辑上和代码上都不难想到把
     * 一轮冒泡后，得到最小的值放在第一个位置，第二轮对剩下的数据进行冒泡，再把剩下数据中的最小的放在第二个位置，一次类推
     * 冒泡排序和选择排序在一轮后显示的数据相似，但是过程却是很不一样的。冒泡就是不断的比较，而选择是从所有的选出其中最小的。
     */
    public  void bubbleSort() {
        System.out.print("\n5.     冒泡排序：");
        int array[] = {34, 18, 54, 5, 4, 69, 99, 98, 54, 56};
        int temp = 0;
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - 1 - i; j++) {
                if (array[j] > array[j + 1]) {
                    temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }

        //输出排列好的数组
        printData(array);
    }

    /**
     * 快速排序
     * 快速排序也是冒泡排序中的一种，但是不一定选中最大或最小值，选中的是任意一个数值，并把它放到适当的游标值的位置，不断对各组数据冒泡，最终得到排列好的数据
     */
    public  void quickSort() {
        System.out.print("\n6.     快速排序：");
        int array[] = {34, 18, 54, 5, 4, 69, 99, 98, 54, 56};
        quick(array);
        //输出排列好的数组
        printData(array);
    }

    private  int getMiddle(int[] list, int low, int high) {
        int tmp = list[low];    //数组的第一个作为中轴
        while (low < high) {
            while (low < high && list[high] >= tmp) {

                high--;
            }
            list[low] = list[high];   //比中轴小的记录移到低端
            while (low < high && list[low] <= tmp) {
                low++;
            }
            list[high] = list[low];   //比中轴大的记录移到高端
        }
        list[low] = tmp;              //中轴记录到尾
        return low;                   //返回中轴的位置
    }

    private  void _quickSort(int[] list, int low, int high) {
        if (low < high) {
            int middle = getMiddle(list, low, high);  //将list数组进行一分为二
            _quickSort(list, low, middle - 1);        //对低字表进行递归排序
            _quickSort(list, middle + 1, high);       //对高字表进行递归排序
        }
    }

    private  void quick(int[] a2) {
        if (a2.length > 0) {    //查看数组是否为空
            _quickSort(a2, 0, a2.length - 1);//里面涉及到不断循环遍历
        }
    }


    /**
     * 归并排序
     * 其实归并排序和上面说到的希尔排序是有异曲同工的设计思想，都是先把一部分数据排列好，然后让一部分数据插入一部分数据，不断融合，直到完全融合。
     */
    public  void mergingSort() {
        System.out.print("\n7.     归并排序：");
        int array[] = {34, 18, 54, 5, 4, 69, 99, 98, 54, 56};
        sort(array, 0, array.length - 1);
        //输出排列好的数组
        printData(array);
    }

    private  void sort(int[] data, int left, int right) {
        if (left < right) {
            //找出中间索引
            int center = (left + right) / 2;
            //对左边数组进行递归
            sort(data, left, center);
            //对右边数组进行递归
            sort(data, center + 1, right);
            //合并
            merge(data, left, center, right);

        }
    }

    private  void merge(int[] data, int left, int center, int right) {
        int[] tmpArr = new int[data.length];
        int mid = center + 1;
        //third记录中间数组的索引
        int third = left;
        int tmp = left;
        while (left <= center && mid <= right) {

            //从两个数组中取出最小的放入中间数组
            if (data[left] <= data[mid]) {
                tmpArr[third++] = data[left++];
            } else {
                tmpArr[third++] = data[mid++];
            }
        }
        //剩余部分依次放入中间数组
        while (mid <= right) {
            tmpArr[third++] = data[mid++];
        }
        while (left <= center) {
            tmpArr[third++] = data[left++];
        }
        //将中间数组中的内容复制回原数组
        while (tmp <= right) {
            data[tmp] = tmpArr[tmp++];
        }
//            System.out.println(Arrays.toString(data));//过程
    }

    /**
     * 基数排序
     * 基数排序，比较的是各个数值的位数上的大小
     * 相对来说速度也是不错的，比如第一次就可以比较出只有个位数的数据的大小排列顺序，第二次就可以比较出只有十位和各位的数据，以此类推，一直比较，就会比较完所有的数。
     */
    public  void radixSort() {
        System.out.print("\n8.     基数排序：");
        int array[] = {34, 18, 54, 5, 4, 69, 99, 98, 54, 56};
        sort(array);
        //输出排列好的数组
        printData(array);
    }

    private  void sort(int[] array) {
        //首先确定排序的趟数;
        int max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }

        int time = 0;
        //判断位数;
        while (max > 0) {
            max /= 10;
            time++;
        }

        //建立10个队列;
        List<ArrayList> queue = new ArrayList<ArrayList>();
        for (int i = 0; i < 10; i++) {
            ArrayList<Integer> queue1 = new ArrayList<Integer>();
            queue.add(queue1);
        }

        //进行time次分配和收集;
        for (int i = 0; i < time; i++) {

            //分配数组元素;
            for (int j = 0; j < array.length; j++) {
                //得到数字的第time+1位数;
                int x = array[j] % (int) Math.pow(10, i + 1) / (int) Math.pow(10, i);
                ArrayList<Integer> queue2 = queue.get(x);
                queue2.add(array[j]);
                queue.set(x, queue2);
            }
            int count = 0;//元素计数器;
            //收集队列元素;
            for (int k = 0; k < 10; k++) {
                while (queue.get(k).size() > 0) {
                    ArrayList<Integer> queue3 = queue.get(k);
                    array[count] = queue3.get(0);
                    queue3.remove(0);
                    count++;
                }
            }
        }

    }

}
