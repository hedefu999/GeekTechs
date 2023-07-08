package com.javalearning.leetcode.labuladong;

import com.javalearning.element.ListNode;
import com.javalearning.utils.PrintUtils;
import com.javalearning.element.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class _05BaseLinkedList {
    private static final Logger log = LoggerFactory.getLogger(_05BaseLinkedList.class);

    /**
     # 380 常数时间插入、删除和获取随机元素

     ## 分析
     数组可以实现O(1)地获取随机元素（等概率获取元素）但不能O(1)地删除元素
     hash表可以O(1)地获取、删除元素，但不能O(1)地等概率获取元素
     将上述特性结合：
     使用数组存储元素的值
     使用HashMap存储元素的值与数组索引的映射关系

     这样添加元素时在array末尾添加（可能要扩容），并在map里加映射
     随机取元素时就计算一个随机Index 直接到array里取
     删除元素时map去掉一个记录，数组可能会留下大量中间空隙，所以每次删除时把末尾的元素挪过来

     */
    static class RandomizedSet {
        private int init_size = 16;
        int[] valArray;
        int size = 0;
        Map<Integer, Integer> itemValueIndexMap;

        public RandomizedSet() {
            valArray = new int[init_size];
            itemValueIndexMap = new HashMap<>();
        }

        public boolean insert(int val) {
            if (itemValueIndexMap.containsKey(val)) return false;
            //将元素添加到数组末尾
            itemValueIndexMap.put(val, size);
            if (size >= valArray.length){
                int[] tempArray = new int[valArray.length*2];
                System.arraycopy(valArray,0,tempArray,0,valArray.length);
                valArray = tempArray;
            }
            valArray[size] = val;
            size++;
            return true;
        }

        public boolean remove(int val) {
            Integer index = itemValueIndexMap.get(val);
            if (index == null) return false;
            itemValueIndexMap.remove(val);
            if (index == size-1){ //移除的恰好是最后一个元素，同时只剩一个元素时也能兼顾
                size --;
                return true;
            }
            //将最后一个元素搬过来
            int lastVal = valArray[size - 1];
            valArray[index] = lastVal;
            itemValueIndexMap.put(lastVal, index);
            size--;
            return true;
        }

        public int getRandom() {
            int randomIndex = (int) (size * Math.random());
            return valArray[randomIndex];
        }

        public static void main(String[] args) {
            RandomizedSet rdSet = new RandomizedSet();
            /*

            test case 1:
            rdSet.insert(1);
            rdSet.remove(2);
            rdSet.insert(2);
            System.out.println(rdSet.getRandom());
            rdSet.remove(1);
            rdSet.insert(2);
            System.out.println(rdSet.getRandom());

            test case 2:
             */
            rdSet.remove(0);
            rdSet.remove(0);
            rdSet.insert(0);
            rdSet.getRandom();
            rdSet.remove(0);
            System.out.println(rdSet.insert(0));

        }
    }

    /*-=-=-=-=-=-=-=-=-=-= 系列随机问题 在各种环境下返回随机元素 -=-=-=-=-=-=-=-=-=-=-=*/

    static class RandomProblem{
        /**
         # 710 黑名单外的随机数
         给定一个包含 [0，n) 中独特的整数的黑名单 B，写一个函数从 [0，n) 中返回一个不在 B 中的随机整数。

         思路： N-B个数如何映射到N个
         0 1 2 [3] 4 [5] [6] 7 8
         0 1 2     3         4 5
         */
        static class RandomInBlackList{
            //维护一个白名单，直接从map中取。会超时，在N很大时
            static class Solution {
                private int N;
                private int realSize;
                Map<Integer,Integer> map = new HashMap<>();
                public Solution(int N, int[] blacklist) {
                    this.N = N;
                    this.realSize = N-blacklist.length;
                    //int[]转ArrayList<Integer>咋就这么难,主要为了用一下
                    //List<Integer> blackCollect = Arrays.stream(blacklist).boxed().collect(Collectors.toList());
                    //Arrays.stream(blacklist).collect(ArrayList::new, (collect,item)-> collect.add(item) ,ArrayList::addAll);
                    Map<Integer,Object> blackMap = Arrays.stream(blacklist).boxed().collect(HashMap::new,(map,item) -> map.put(item,null), HashMap::putAll);
                    int index = 0;
                    for (int i = 0; i < N; i++) {
                        if (blackMap.containsKey(i)) continue;
                        map.put(index++,i);
                    }
                }
                public int pick() {
                    int midIndex = (int) (Math.random() * realSize);
                    return map.get(midIndex);
                }
            }
            /*
            二分查找

            示例分析：
                   3     4   4       6   6          -- 某个B[i]前面有多少个W[i]: B-b
                   0     1   2       3   4          -- 黑名单index - b
            0 1 2 [3] 4 [5] [6] 7 8 [9] [10] 11 12  -- 黑名单B和白名单W,原始数据O index - k
            0 1 2     3         4 5           6  7  -- 白名单index - w

            w = 3, w + 1 找<=3的最大index
            w = 4, w + 3
            w = 5, w + 3
            w = 6, w + 5
            可以总结规律，W[w] = w + (B-b中所有小于等于w的数量)

             */
            static class Solution2{
                private int N;
                //纯数组操作，性能爆表
                private int[] sortedBs;
                //private int[] sortedWs;
                public Solution2(int N, int[] blacklist){ //N=9,B={3,5,6},W={0,1,2,4,7,8}
                    Arrays.sort(blacklist);
                    sortedBs = blacklist;
                    //int wlength = N - blacklist.length;
                    this.N = N;
                }
                public int pick(){
                    int w = (int) (Math.random() * (N - sortedBs.length));//白名单中的索引，随机到的数必须是白名单中的索引
                    //知道w如何求值W[w],这个： W[w]在黑名单为空时 W[w] = w; 黑名单不为空时 W[w]>=w, 因为有些元素要跳过去；黑名单不为空时也有可能W[w]=w,发生在B(min)>w时
                    if (sortedBs.length == 0 || w < sortedBs[0]) return w;
                    //下面是比较难的情形
                    //可以将sortedBs转成 B-b，注意多次pick会重复使用sortedBs
                    //在寻找第一个大于w的元素的sortedBs[i]-i的i值
                    int b = binFindFirstGreater(sortedBs, w);
                    while (b < sortedBs.length && sortedBs[b]-b == w){
                        b++;
                    }
                    return w+b;
                    //如何优雅地使用二分查找法寻找第一个大于w的元素（w在input中可能是重复的）
                }
                //答案在这里，使用二分查找法寻找第一个大于key的元素，LeCo显示性能提升很多
                static int findFirstGeaterIndex(int[] input, int key){
                    int low = 0;
                    int high = input.length - 1;
                    while (low < high){
                        int internal = (low + high + 1)/2;
                        if (input[internal] > key) high = internal - 1;
                        else low = internal;
                    }//最后low总是 = high
                    return low + 1;
                }
            }

            static int simpleSearchFirstGreat(int[] input, int key){
                for (int i = 0; i < input.length; i++) {
                    if (key < input[i] - i){
                        return i;
                    }
                }
                return input.length;
            }
            static int binFindFirstGreater(int[] input, int key){
                int low = 0;
                int high = input.length - 1;
                int mid = 0;
                while (low <= high){
                    mid = (low + high)/2;
                    int curr = input[mid] - mid;
                    if (curr < key){
                        low = mid + 1;
                    }else if (curr > key){
                        high = mid - 1;
                    }else {
                        return mid;
                    }
                }
                return low;
            }//对返回结果再进行+++找到第一个较大的，而不是相等的

            public static void main(String[] args) {
                int[] black = {}; //black = {2,3}; array initializer is not allowed here
                int N = 100;

                //black = new int[]{};
                //N=1;

                //black = new int[]{};
                //N=2;

                //black = new int[]{1};
                //N=3;

                black = new int[]{3,5,6};
                N = 9;

                Solution2 solution = new Solution2(N, black);

                System.out.println(solution.pick());
                System.out.println(solution.pick());
                System.out.println(solution.pick());
                System.out.println(solution.pick());
            }


            //二分查找法写哭了也不对。。。。
            static void binSearch1(){
                int[] sortedBs = {2,4,6,8,12,13,66};
                int w = 7;
                int low = 0;
                int high = sortedBs.length-1;
                int mid = 0;
                while (low < high){ //如果这里把=号带上，会发现low始终是3
                    mid = (low + high)/2;
                    if (sortedBs[mid] < w){
                        low = mid + 1;
                    }else if (sortedBs[mid] > w){
                        high = mid - 1;
                    }else {
                        low = high = mid;
                    }
                }
                //随着后部元素的减少，收集low/mid/high的情况，如何能得到正确的2 ???
                //2,4,6,8,12,13,66  2,1,2
                //2,4,6,8,12,13  3,4,3
                //2,4,6,8,12  3,3,2
                //2,4,6,8  3,2,3
                System.out.println(low);
            }

            //看看JVM是怎么写二分查找的
            private static int binarySearch0(int[] a, int fromIndex, int toIndex, int key) {
                int low = fromIndex;
                int high = toIndex - 1;
                while (low <= high) { //多加一个=号可以让low = high时让mid与这两个数保持一致而不是上一步计算的值
                    int mid = (low + high) >>> 1;//除以2取整
                    if (a[mid] < key)
                        low = mid + 1;
                    else if (a[mid] > key)
                        high = mid - 1;
                    else
                        return mid; //找到元素
                }
                return -(low + 1);  //找不到元素时会发现low始终是大于key的最小index
            }

            /*
            还有一种技巧：
                   0     1   2       3   4          -- 黑名单index - b
            0 1 2 [3] 4 [5] [6] 7 8 [9] [10] 11 12  -- 黑名单B和白名单W,原始数据O index - k
            0 1 2     3         4 5           6  7  -- 白名单index - w
            N = 12，黑名单长度5，白名单长度8，可以发现8之前的黑名单数量与8之后的白名单数量相等
            这样把 >= 8的白名单映射到黑名单，这样就可以直接对[0,8)做随机取数了
            这样就只需要存储一些修改过的映射到map里，进一步节省空间，因为其他没改过的就是返回key，上述为例，map的内容就是[<3,8><5,11><6,12>]
             */
            static class Solution3{
                static void lastSolution(int n, int[] black){
                    int wl = n - black.length;
                    Map<Integer,Integer> map = new HashMap<>();
                    Set<Integer> w = new HashSet<>();
                    //收集后部的白名单数字
                    for (int i = wl; i < n; i++) {
                        w.add(i);
                    }
                    for (int blackItem : black){
                        w.remove(blackItem);
                    }
                    Iterator<Integer> iterator = w.iterator();
                    for (int blackItem : black){
                        if(blackItem < wl){
                            map.put(blackItem,iterator.next());
                        }
                    }
                    return;
                }//自己又实现了一个，结果超时了，因为里面的hashset太大了，官方写法还是不可替代的。。。我崩溃了
                static void lastSolution2(int N, int[] blacklist){
                    int wl = N - blacklist.length;
                    int left = wl-1, right = wl;
                    Map<Integer,Integer> map = new HashMap<>();
                    Set<Integer> blackSet = new HashSet<>();
                    for (int item : blacklist) {
                        blackSet.add(item);
                    }

                    while (left >= 0 && right < N){
                        while (left >= 0 && !blackSet.contains(left)){
                            left--;
                        }
                        while (right < N && blackSet.contains(right)){
                            right ++;
                        }
                        //这里会把-1，11两个极端的值放进去，但不影响功能
                        map.put(left--,right++);
                    }
                    return;
                }

                public static void main(String[] args) {
                    int[] a = {3,4,4};//2 2
                    int[] b = {3,4,7,9,10};//11
                    int[] c = {2,3,4,5,7,9};//22
                    int[] d = {2,3,5,7,10};//11
                    int[] e = {1,2,3};//22

                    int[] b1 = {3,5,6};//9 5-7,3-8
                    int[] b2 = {3,5,6,9,10};//11 5-7,3-8
                    int[] b3 = {3,5,6,9,10};//13 3-12,5-11,6-8

                    //lastSolution2(9,b1);
                    lastSolution2(11,b2);
                    //lastSolution2(13,b3);

                    //lastSolution(9,b1);
                    //lastSolution(11,b2);
                    //lastSolution(13,b3);


                    //System.out.println(whatsthis(a,4));
                }
                //最后的解法性能最好
                static class Solution {
                    Map<Integer,Integer> map;
                    int wl;
                    public Solution(int N, int[] blacklist) {
                        //this.wl = N - blacklist.length;
                        //this.map = new HashMap<>();
                        //Set<Integer> w = new HashSet<>();
                        ////收集后部的白名单数字
                        //for (int i = this.wl; i < N; i++) {
                        //    w.add(i);
                        //}
                        //for (int blackItem : blacklist){
                        //    w.remove(blackItem);
                        //}
                        //Iterator<Integer> iterator = w.iterator();
                        //for (int blackItem : blacklist){
                        //    if(blackItem < this.wl){
                        //        this.map.put(blackItem,iterator.next());
                        //    }
                        //}
                        this.wl = N - blacklist.length;
                        int left = this.wl-1, right = this.wl;
                        this.map = new HashMap<>();
                        Set<Integer> blackSet = new HashSet<>();
                        for (int item : blacklist) {
                            blackSet.add(item);
                        }

                        while (left >= 0 && right < N){
                            while (left >= 0 && !blackSet.contains(left)){
                                left--;
                            }
                            while (right < N && blackSet.contains(right)){
                                right ++;
                            }
                            //这里会把-1，11两个极端的值放进去，但不影响功能
                            this.map.put(left--,right++);
                        }
                    }

                    public int pick(){
                        int key = (int) (Math.random() * this.wl);
                        if (map.containsKey(key))
                            return map.get(key);
                        else
                            return key;
                    }

                }
            }

        }

        /**
         # 382 链表随机结点
         */
        static class RandomLinkedListItem{
            static Random random = new Random();
            /*
              实现一次遍历就返回链表的一个随机结点
              如何在链表遍历的过程中就返回一个随机结点的值
              通常在知道链表总长n，[0,n)生成个随机数取结点就是随机结点，但需要遍历两次
              但现在要求只遍历一次，上面的做法是计算随机数一次遍历多次，要遍历一次必然就得多次计算随机数

              对于遍历过程中不断增长的length，每遇到一个结点就考虑该结点是否应成为随机选中的结点
              做法是计算[0,length)的随机数，如果随机数等于 0或者i-1 表示选中当前结点（最新结点/末结点）作为随机结点
              选中的随机结点可以后续可以覆盖替换

              算法总结为一句话就是：每遍历到下一个元素就计算一次随机数，随机数满足某种对于所有结点公平的条件就认为当前结点是随机选中的结点，并继续遍历
                这种公平条件可以选择为 random == 0/length

              数学证明：
              结点被选中的概率 = 遍历到时选中的概率 1/length * 历次其他结点不被选中的概率 1-1/length
              以3个结点为例，①在刚开始遍历时选中概率是1，③的概率肯定是1/3，②在遍历到时选中概率是1/2,在3号结点遍历时被覆盖的概率是（1-1/3）,结果是1/2*2/3,①的概率就是1*1/2*2/3
              有点像买彩票先买后买中奖的概率是一样的,只不过倒过来了
             */
            static int getRandomNodeValue(ListNode head){
                int i = 0, result = 0;
                ListNode curr = head;
                while(curr != null){
                    i++;
                    int randomInt = random.nextInt(i);
                    if (randomInt == i-1){
                        result = curr.val;
                    }
                    curr = curr.next;
                }
                return result;
            }
            /*
             上述抽样算法的时间复杂度是O(n),但不是最优解法，更优解法基于几何分布（geometric distribution）TC=O(k+klog(n/k)) k是随机选择的数字数量
             如果一次可以取k个元素，如何等概
             下述解法的数学证明
             对于 1 2 3 4 n=4,k=2，每一个元素的选中的概率就是k/n
             3被选中的概率 = 遍历到3被选中(2/3) * {遍历到4被选中并且不会在result数组中被覆盖(2/4*1/2) + 4没有被选中（1-2/4）}  后面也可以 1 - 选中并且覆盖的概率
             第i个(i∈[1,n])元素选中的概率：
             k/i * Multi (k/(i+x)*1/k + (1-k/(i+x)))  x∈[1,n-i]
             k/i * Multi (1 - k/(i+x)*1/k) x∈[1,n-i]
             */
            static int[] getRandomNodes(int k, ListNode head){
                int[] result = new int[k];
                ListNode curr = head;
                for (int i = 0; i < k; i++) {
                    result[i] = curr.val;
                    curr = curr.next;
                }//注意此时curr停留在第k个元素上，应选择随机数[0,k+1)
                int index = k+1;
                while (curr != null){
                    int rindex = random.nextInt(index++);
                    if (rindex < k){
                        result[rindex] = curr.val;
                    }
                    curr = curr.next;
                }
                return result;
            }

            public static void main(String[] args) {
                ListNode head = new ListNode(2);
                ListNode second = new ListNode(3);
                ListNode third = new ListNode(4);
                ListNode fourth = new ListNode(5);
                head.next = second; second.next = third; third.next = fourth;
                System.out.println(getRandomNodeValue(head));
            }
        }

        /**
         # 398 随机数索引 - 重复元素数组返回重复数的随机索引
         一个数组里某些数是重复的，这样取这些数的索引，对于重复的数应该是随机的
         # 382与398也就是 Reservior sampling 水塘抽样算法
         */
        static class DuplicateItemArrayRandomIndex{
            private int[] nums;
            private Random random = new Random();
            public DuplicateItemArrayRandomIndex(int[] nums) {
                this.nums = nums;
            }
            /*
             与链表随机结点很像，就是在不知道未来是否还有内容的情况下计算当前的随机，并不断覆盖
             */
            int pick(int target){
                for (int i = 0; i < nums.length; i++) {
                    if (nums[i] == target){
                        return calcRandomIndex(i,target);
                    }
                }
                return -1;
            }
            private int calcRandomIndex(int index, int target) {
                int currentIndex = index, result = index;
                while (++currentIndex < nums.length && nums[currentIndex] == target){
                    if (random.nextInt(currentIndex - index + 1) == 0){
                        result = currentIndex;
                    }
                }
                return result;
            }

            public static void main(String[] args) {
                int[] nums = {1,2,3,3,3,4};//3返回2/3/4,其他返回唯一确定的index
                DuplicateItemArrayRandomIndex randomIndex = new DuplicateItemArrayRandomIndex(nums);
                System.out.println(randomIndex.pick(3));
            }
        }
        /**
         # Fisher-Yates洗牌算法
         用于打乱数组中元素的顺序
         使用"蒙特卡罗方法"检验打乱算法是否真的够乱
         分析洗牌算法的正确性的一个准则是：所能产生的结果共有n!种

         每遍历一个元素就从包括自身及之后的元素中（共 length-i 个）随机挑一个进行互换，也可以从后往前遍历
         共可以产生length!种可能
         */
        static class FisherYatesShuffleAlgorithm{
            static Random random = new Random();
            static void shuffle1(int[] nums){
                int length = nums.length;
                for (int i = 0; i < length; i++) {
                    int rand = random.nextInt(length - i) + i;
                    int temp = nums[i];nums[i] = nums[rand];nums[rand] = temp;
                }
            }
            //把最后一步的1选1的操作去掉，少一次迭代，仍然是length!种可能
            static void shuffle2(int[] nums){
                int length = nums.length;
                for (int i = 0; i < length - 1; i++) {
                    int rand = random.nextInt(length - i) + i;
                    int temp = nums[i];nums[i] = nums[rand];nums[rand] = temp;
                }
            }
            //下面两个就是上面两个倒过来，random计算时的代码易读性好一点
            static void shuffle3(int[] nums){
                int length = nums.length;
                for (int i = length - 1; i >= 0; i--) {
                    int rand = random.nextInt(i);
                    int temp = nums[i];nums[i] = nums[rand];nums[rand] = temp;
                }
            }
            static void shuffle4(int[] nums){
                int length = nums.length;
                for (int i = length - 1; i > 0; i--) {
                    int rand = random.nextInt(i);
                    int temp = nums[i];nums[i] = nums[rand];nums[rand] = temp;
                }
            }
            /*
            此算法错误，在输入{1 2 3}时
                    1(123)  (321)(132)(123)
            1(123)  2(123)  (321)(132)(123)
                    3(132)  (231)(123)(132)
                    1(123)  (321)(132)(123)
            2(213)  2(213)  (312)(231)(213)
                    3(231)  (132)(213)(231)
                    1(321)  (123)(312)(321)
            3(321)  2(321)  (123)(312)(321)
                    3(312)  (213)(321)(312)
            6种排列的发生概率不等：
            123 132 213 231 312 321 total
             6   5   3   3   4   6   27
            n^n 很难被 n! 整除，也就无法实现n!种排列情况可能出现次数的均摊
             */
            static void errorAlgorithm(int[] nums){
                int length = nums.length;
                for (int i = 0; i < length; i++) {
                    int rand = random.nextInt(length);
                    int temp = nums[i];nums[i]=nums[rand];nums[rand]=temp;
                }
            }
            /*
             蒙特卡罗方法验证随机性
             知识背景：一个正方形中紧贴一个圆，采用向正方形中随机打点大量次数，比较落入圆中的次数和总次数的方式计算圆周率
             比较各种情形在测试中出现的频数是否均等
             */
            static void monteCarloMethod(){
                int[] validator = {1,0,0};
                int length = validator.length;
                int[] counter = new int[length];
                int shuffleTimes = 100;
                for (int i = 0; i < shuffleTimes; i++) {
                    errorAlgorithm(validator);//这种蒙特卡罗方法似乎验证不了errorAlgorithm，还是应该对具体的排列做统计
                    for (int j = 0; j < length; j++) {
                        if (validator[j] == 1){
                            counter[j]++;
                            break;
                        }
                    }
                }
                System.out.println(Arrays.toString(counter));
            }

            public static void main(String[] args) {
                monteCarloMethod();
            }
        }

        /**
          # 加权随机抽样算法
         加权随机抽取数组 w = {10,20,30,40} 表示10%概率抽到0，20%概率抽到1
         ref https://blog.csdn.net/Code_LT/article/details/87626770 todo 待研究
         */


        /**
         # 随机不重复有限地取数组元素
          实现一个生成器类，构造函数传入一个很长的数组，实现一个randomGet方法，每次调用随机不重复地返回数组中的一个元素
         调用次数有限
         {1,2,3,4} 0 {4,2,3,4} 1 {}
         */
        static class NonRepeatedRandomGet{
            private int fetchRemainTimes;
            private int[] shadow;
            private Random random = new Random();
            public NonRepeatedRandomGet(int[] nums) {
                this.shadow = nums;
                this.fetchRemainTimes = nums.length;
            }
            //fetchRemainTimes控制了随机index的范围，不重复的关键就是把random选过的元素与最末尾（fetchRemainTimes-1）的元素互换，这样下一次就不会再取到这个元素了
            int nonRepeatedRandomGet(){
                if (fetchRemainTimes < 1) return -1;
                int random = this.random.nextInt(fetchRemainTimes);
                int result = shadow[random];
                int swapIndex = fetchRemainTimes - 1;
                shadow[random] = shadow[swapIndex];//shadow[swapIndex] = result; shadow搞乱也无所谓
                fetchRemainTimes--;
                return result;
            }
            public static void main(String[] args) {
                int[] arr = {1,2,3,4,5};
                NonRepeatedRandomGet rGet = new NonRepeatedRandomGet(arr);
                System.out.println(rGet.nonRepeatedRandomGet());
                System.out.println(rGet.nonRepeatedRandomGet());
                System.out.println(rGet.nonRepeatedRandomGet());
                System.out.println(rGet.nonRepeatedRandomGet());
                System.out.println(rGet.nonRepeatedRandomGet());
                System.out.println(rGet.nonRepeatedRandomGet());
                System.out.println(rGet.nonRepeatedRandomGet());
            }
        }


    }

    /**
      # 316/1081 去除重复字母 「HARD」
     一个字符串s，①去除重复的字母，②不能打乱其他字符的相对顺序，③保持最小字典序
     字典序：例如对于 4、5、1、3 四个数，1354排列比1354排列靠前，属于更小的字典序
     case1: bcabc -> abc
     case2: cbacdcbc -> acdb
     */
    static class RemoveDuplicatedLetters{
        /*
         简单将char放入Set，判断Set中是否已包含此char
         bcabc返回的是bca，不符合最小字典序. TC=O(N) SC=O(N)

         改进方案：用栈来存储最终返回的字符串，并维持字符串的最小字典序。每遇到一个字符，如果这个字符不存在于栈中，就需要将该字符串入栈。但在入栈之前，需要将之后还会出现，并且字典序比当前字符小的字符移除，然后再将当前字符压入
         先统计出各char的数量，使用stack保留最终结果
         最小字典序的实现方案：如果先前入栈的更大元素后面还有（统计数量不为0）就弹出这个元素放入更小的char
         元素使用在堆栈中使用了额外的数组标识，也可以使用Stack#contains判断，但性能很差
         这也属于贪心算法？？    TC = O(N) SC = O(1)
         */
        static String removeDuplicateLetters(String s){
            char[] chars = s.toCharArray();
            Stack<Character> stack = new Stack<>();//需求②的数据结构实现
            boolean[] existFlag = new boolean[256];//需求①的实现，也可以用HashSet做
            //维护一个Letter计数器
            int[] counter = new int[256];//需求③的实现，也可以用HashMap做，就不用256个长度了，但数组是最简单最原始的数据结构，性能最好，除非Hash能显著改善性能
            for (char c : chars){
                counter[c]++;
            }
            for (char c : chars){
                counter[c]--;//元素的计数修改发生在遍历而非while中pop之后
                if (existFlag[c]) continue;//重复元素不再入栈
                //为了满足最小字典序的要求：当前char c如果比栈顶元素小，并且后面还会再有 就尝试弹出
                while (!stack.isEmpty() && stack.peek() > c && counter[stack.peek()] > 0){
                    Character pop = stack.pop();
                    existFlag[pop] = false;
                }
                stack.push(c);
                existFlag[c] = true;
            }
            StringBuilder builder = new StringBuilder();
            while (!stack.empty()){
                builder.append(stack.pop());
            }
            return builder.reverse().toString();
        }
        //使用双端队列，元素计数判断后面是否还有的 可以改成 判断最后出现的index是否比当前i大
        static String removeDuplicateLetters2(String s) {
            char[] chars = s.toCharArray();
            int length = chars.length;
            Map<Character,Integer> dict = new HashMap();
            //双端队列进行首尾操作更方便
            LinkedList<Character> doubleOpQueue = new LinkedList();
            //只保留最后一次出现的index
            for (int i = 0; i < length; i++) {
                dict.put(chars[i],i);
            }
            for(int i = 0; i < length; i++){
                if(!doubleOpQueue.contains(chars[i])){
                    //dict.get(doubleOpQueue.peekLast()) > i: 打算从尾部去除的较大元素如果最后出现的index在i之后说明可以去掉
                    //对于 cbacdcbc d的稀缺性保证了c在队列中也没有被去掉，就形成了acd在结果中
                    while(!doubleOpQueue.isEmpty() && chars[i] < doubleOpQueue.peekLast() && dict.get(doubleOpQueue.peekLast()) > i){
                        doubleOpQueue.pollLast();
                    }
                    doubleOpQueue.add(chars[i]);
                }
            }
            //双端队列不需要再倒序
            StringBuilder sb = new StringBuilder(doubleOpQueue.size());
            for (Character c : doubleOpQueue) sb.append(c.charValue());
            return sb.toString();
        }

        //一种递归思路:对当前所有char计数，向后找较小的char，如果某个char只剩一个了就停止查找，将此较小char加入结果中,并且去掉后面所有的这个较小char
        //TC = O(N)*C C为不重复字母数量；SC = O(N)*C
        static String rmvDupCharRecursiveSolution(String s){
            if (s.length() == 0) return "";
            int[] counter = new int[26];
            int pos = 0;
            for (int i = 0; i < s.length(); i++) {
                counter[s.charAt(i) - 'a']++;
            }
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) < s.charAt(pos)) pos = i;//pos标记较小char
                if (--counter[s.charAt(i) - 'a'] == 0) break;//char只剩一个
            }
            return s.charAt(pos)+rmvDupCharRecursiveSolution(s.substring(pos+1).replaceAll(""+s.charAt(pos), ""));
        }

        /*
             有序数组去重
             原地删除重复出现的元素，使得每个元素只出现一次，返回移除后数组的新长度，不可使用额外的数组空间
             原地修改输入数组并使用O(1)额外空间
             case1 {1,1,2} -> 2,并且原数组nums的前2个元素被修改为 1，2，不需要考虑新数组长度后面的元素
             case2 {0,0,1,1,1,2,2,3,3,4} -> 5，并且原数组nums的前5个元素被修改为 0，1，2，3，4
             case3 {0,1,2,2,3}

             处理数组应尽可能对尾部元素操作，以避免移动元素

             */
        //双指针/快慢指针 解法
        static int removeDuplicateItemInArray(int[] nums){
            int length = nums.length;
            int pre = 0, post = pre + 1;
            while (post < length){
                while (post < length && nums[post] == nums[pre]){
                    post++;
                }
                if (post<length)
                    nums[++pre] = nums[post++];
            }
            return pre+1;
        }
        //大哥真是思路惊奇 ↑ 简单的也能写复杂
        static int removeDuplicateItemInArray2(int[] nums){
            int length = nums.length;
            if (length == 0) return 0;
            int slow = 0,fast = slow + 1;
            while (fast < length){
                if (nums[fast] != nums[slow]){
                    slow++;//先slow++还是先拷贝元素要留意下
                    nums[slow] = nums[fast];//如果是操作链表这里可以选择slow.next=fast/slow.next.value = fast.value?
                }
                fast++;
            }
            return slow + 1;
        }

        public static void main(String[] args) {
            String s0 = "bcab";
            String s = "bcabc";
            String s2 = "cbacdcbc";//acdb
            System.out.println(removeDuplicateLetters(s2));
            System.out.println(removeDuplicateLetters2(s2));
            //System.out.println(rmvDupCharRecursiveSolution(s2));
            int[] nums5 = {1,1,2};
            int[] nums6 = {0,0,1,1,1,2,2,3,3,4};
            int[] nums7 = {0,1,2,2,3};
            int[] nums8 = {0,1,2,3,3,3};
            System.out.println(removeDuplicateItemInArray(nums6));
            System.out.println(Arrays.toString(nums6));
        }
        //由字符串去重引出的几个问题  字符串去重综合使用了几种技巧，是一个不错的综合应用

        /**
         # 单调栈的应用
         */
        static class MonotonicStack{
            /*
             单调栈可以用于处理Next Greater Element问题
             举例：{2,1,2,4,3} 寻找每个元素的next greater元素替换掉，没有的话就填-1，结果是{4,2,4,-1,-1}
             分析：
             - 结果的最后一位总是-1，最后一个元素后面没有更大的元素
             - 从前向后看，前一个元素的next greater在下一个元素不一定能用，2-4，1-2
             - 从后向前看，后一个元素的next greater 可以先放在"汉诺塔"上，如2的next greater 4,前一个元素的next greater可以考虑能否叠摞在塔上
                往前的过程中出现更大元素会把塔弹空，这个更大元素完全可以作为前面元素的next greater,所以弹空后把它放在塔基上
                这个"塔"就是单调栈 Monotonic Statck todo 要不来个动画？
                显然必须从后向前才能解决问题
             */
            static int[] nextGreatElement(int[] nums){
                int length = nums.length;
                int[] answer = new int[length];
                Stack<Integer> stack = new Stack<>();
                for (int i = length - 1; i >= 0; i--) {
                    while (!stack.empty() && stack.peek() <= nums[i]){
                        stack.pop();
                    }
                    answer[i] = stack.empty()?-1:stack.peek();
                    stack.push(nums[i]);
                }
                return answer;
            }
            //{2,1,2,4,3} nextLower - {1,-1,-1,3,-1}, preLower - {-1,-1,1,2,2}(最后堆栈内容是1 2 3)
            //另一个角度的单调栈问题：前一个更小元素. 这次可以从前向后思考问题了，而且单调栈中的元素是单调递减的
            //有点像数学上的等价命题，双反
            static int[] preLowerElement(int[] nums){
                int length = nums.length;
                int[] answer = new int[length];
                Stack<Integer> stack = new Stack<>();
                for (int i = 0; i < length; i++) {
                    //比我大的元素都pop掉,然后我进去坐着
                    while (!stack.empty() && nums[i] < stack.peek()){
                        stack.pop();
                    }
                    if (stack.empty()){
                        answer[i] = -1;
                    }else {
                        answer[i] = stack.peek();
                    }
                    stack.push(nums[i]);
                }
                return answer;
            }
            //nextLower问题又需要从后向前了，而且要维护一个递增的栈，要用两个栈实现
            //{2,1,2,4,3} nextLower - {1,-1,-1,3,-1}   5,6,4,3
            static int[] nextLowerElement(int[] nums){
                int length = nums.length;
                int[] result = new int[length];
                Stack<Integer> stack = new Stack<>();
                for (int i = length - 1; i >= 0; i--) {
                    //更大元素放在上面，遇到新小元素会弹走上面的更大元素
                    while (!stack.empty() && nums[i] <= stack.peek()){
                        stack.pop();
                    }
                    if (stack.empty()){
                        result[i] = -1;
                    }else {
                        result[i] = stack.peek();
                    }
                    stack.push(nums[i]);
                }
                return result;
            }
            //preGreater问题呢？懒地写~

            /*
             # 单调栈问题变形
             近几天天气的气温：{23,24,25,21,19,22,26,23} 对于每一天还要至少等多少天才能等到更暖和的气温，如果没有数据返回0
             （股价，还要等多少天才能抛售赚钱 - 又是一个金融问题）
             case1: {23,24,25,21,19,22,26,23} - {1,1,4,2,1,1,0,0}
             nextGreater问题，只不过返回
             */
            static int[] nextWarmerWeather(int[] tempera){
                int length = tempera.length;
                int[] result = new int[length];
                Stack<Integer> stack = new Stack<>();
                for (int i = length - 1; i >= 0; i--) {
                    //留大的在栈底
                    while (!stack.empty() && tempera[i] > tempera[stack.peek()]){
                        stack.pop();
                    }
                    if (stack.empty()){
                        result[i] = 0;
                    }else {
                        result[i] = stack.peek() - i;
                    }
                    stack.push(i);
                }
                return result;
            }

            /*
              # 单调栈问题变形2 - 环形数组求nextGreater
              case1 {2,1,2,4,3} - {4,2,4,-1,4}
              case2 {3,4,5,6,4,3} - {4,5,6,-1,5,4}
              case3 {3,5,4,6,2} - {5,6,6,-1,3}
              解法1: 数组拷贝一份放在后面，问题重新变成线性数组的next greater问题
              解法2: 循环数组的取值可以采用取模法实现: index++ % length
              看过答案相出解题思路：线性变环形数组，求解结果变化的就是后面没有nextGreater的元素，nG可能在前面，所以前面的元素能搬到后面一次就足够了，再加上index取模取循环数组元素的方法，可以进一步优化SC
             */
            static int[] circleArrayNextGreater(int[] nums){
                int length = nums.length;
                int[] res = new int[length];
                Stack<Integer> stack = new Stack<>();
                int doublelen = 2 * length;//假装这个数组按方案1那样拷贝了一份接在后面
                for (int i = doublelen - 1; i >= 0; i--) {
                    //留大的在栈底
                    while (!stack.empty() && nums[i%length] >= stack.peek()){
                        stack.pop();
                    }
                    if (stack.empty()){
                        res[i%length] = -1;
                    }else {
                        res[i%length] = stack.peek();
                    }//res[i]会被赋值两次
                    stack.push(nums[i%length]);
                }
                return res;
            }

            public static void main(String[] args) {
                int[] nums1 = {2,1,2,4,3};//返回 {4,2,4,-1,-1}
                int[] nums2 = {3,4,5,6,4,3};
                //System.out.println(Arrays.toString(nextGreatElement(nums1)));
                int[] tempera = {23,24,25,21,19,22,26,23};
                int[] nums3 = {3,4,5,6,4,3};
                int[] nums4 = {3,5,4,6,2};
                //System.out.println(Arrays.toString(circleArrayNextGreater(nums3)));
            }
        }

    }


    static class ArraysOpsAlgorithm{
        /**
          二分查找算法花式集合
         */
        static class BinarySearch{
            /*
            经典二分查找
             */
            //经典二分查找 - 搜索区间为右闭区间
            static int classicBinSearchRightClose(int[] nums, int target) {
                int length = nums.length;
                int left = 0, right = length-1;
                while(left <= right){ //在{5}中搜索5，如果没有这个<=就会说找不到！
                    int middle = left +(right - left)/2 ;// (left+right)/2 = (left + right + left -left)/2 = left +(right - left)/2 可以有效防止整型溢出
                    if(nums[middle] == target){
                        return middle;
                    }else if(nums[middle] < target){
                        left = middle + 1;
                    }else if(nums[middle] > target){
                        right = middle - 1;
                    }
                }
                return -1;// return left; 找不到就返回稍大的元素
            }
            //经典二分查找 - 搜索区间为右开区间
            static int classicBinSearchRightOpen(int[] nums, int target){
                int length = nums.length;
                int left = 0, right = length;
                while (left < right){
                    int middle = left + (right - left)/2;
                    if (nums[middle] == target){
                        return middle;
                    }else if (nums[middle] < target){
                        left = middle + 1;
                    }else if (nums[middle] > target){
                        right = middle;
                    }
                }
                return -1;
            }
            //要不要尝试下左开区间？
            static int classBinSearchLeftOpen(int[] nums, int target){
                int length = nums.length;
                int left = -1, right = length - 1;
                while (left < right){
                    int middle = left + (right - left)/2;
                    if (nums[middle] == target){
                        return middle;
                    }else if (nums[middle] < target){
                        left = middle + 1;//左开区间这里有点反常，居然要+1
                    }else if (nums[middle] > target){
                        right = middle - 1;
                    }
                }
                return left;
            }
            /*
            对比上述两种经典二分查找的两种写法，可总结
            # 二分查找的一个规律
            初值决定搜索区间，搜索区间分为[left,right]和[left,right)两种
            在使用右闭区间时，应在middle不对应target时进行 middle +/- 1 赋给left/right,
            右闭区间在left=right时，[left,right]区间是有一个元素的，不像[left,right)不含任何元素，所以右闭区间的while循环结束条件是left>right,left==right时应该仍能进入循环体，而右开区间在left=right时就应当停止循环
            综上，右闭区间循环体是 while(left <= right), 而右开区间的循环体是 while(left<right)
             */

            /*
            经典二分查找的一个缺陷是：在含有重复元素的情况（...1,2,2,2,3,4...中搜索2）下，搜索结果不确定是重复元素的哪一个
            寻找左侧边界
            middle命中了元素也不能返回，因为有可能左侧还有相同元素，所以只能等到while结束了,while结束的条件受搜索区间选择的控制
             */
            static int searchLeftFirstRightClose(int[] nums, int target){
                int length = nums.length;
                if (length == 0) return -1;
                int left = 0,right = length - 1;
                while (left <= right){
                    int middle = (left + right)/2;
                    if (nums[middle] == target){
                        right = middle - 1;//
                    }else if (nums[middle] < target){
                        left = middle + 1;
                    }else if (nums[middle] > target){
                        right = middle - 1;
                    }
                }
                return left;
            }

            static int searchLeftFirstRightOpen(int[] nums, int target) {
                int length = nums.length;
                if (length == 0) return -1;
                int left = 0, right = length;
                while (left < right) {
                    int middle = (left + right) / 2;
                    if (nums[middle] == target) {
                        right = middle;
                    } else if (nums[middle] < target) {
                        left = middle + 1;
                    } else if (nums[middle] > target) {
                        right = middle - 1; // 开区间还要-1
                    }
                }
                return right;//right open的写法，while结束条件是left=right,返回left/right都一样
            }
            /*
            二分法查找左边界一个难以理解的点是：
            搜索区间是右闭区间时，middle对应target时right赋值middle-1而不是middle，如果middle-1直接小于target了岂不是找不到了
            搜索区间是右开区间时，middle对应target时right赋值middle，而right在右开区间是取不到的，middle - 1要是小于target岂不也是找不到了
            通过调试发现，如果出现上述情况，下一次也是最后一次进行while循环会将left赋值middle+1，left又指向正确的位置了
            left实际指向的是最小的Greater元素的位置
             */
            /*
             右边界查找

             */
            static int searchRightLastRightOpen(int[] nums, int target){
                int length = nums.length;
                if (length == 0) return -1;
                int left = 0, right = length;
                while (left < right){
                    int middle = (left + right) / 2;
                    if (nums[middle] == target){
                        left = middle + 1;//
                    }else if (nums[middle] < target){
                        left = middle + 1;
                    }else if (nums[middle] > target){
                        right = middle;//不是middle-1
                    }
                }
                return left - 1;//注意
            }
            static int searchRightLastRightClose(int[] nums, int target){
                int length = nums.length;
                if (length == 0) return -1;
                int left = 0,right = length - 1;
                while (left <= right){
                    int middle = (left + right) / 2;
                    if (nums[middle] == target){
                        left = middle + 1;//左侧边界让right过来，右侧边界让left过去
                    }else if (nums[middle] < target){
                        left = middle + 1;
                    }else if (nums[middle] > target){
                        right = middle - 1;
                    }
                }
                return left;
            }
            static void testnon(){
                int[] nums2 = {-1,0,3,5,9,12};
                int[] nums4 = {1,2,3,4,4,4,5,6,7};
                int[] nums5 = {1,5,5,6};
                System.out.println(searchRightLastRightClose(nums2, 5));//3
                System.out.println(searchRightLastRightClose(nums2, 4));//-1
                System.out.println(searchRightLastRightClose(nums2, 9));//4
                System.out.println(searchRightLastRightClose(nums2, 3));//2
                System.out.println(searchRightLastRightClose(nums2, -1));//0
                System.out.println(searchRightLastRightClose(nums2, 12));//
                System.out.println(searchRightLastRightClose(nums2, 14));
                System.out.println(searchRightLastRightClose(nums2, -2));
                System.out.println(searchRightLastRightClose(nums5,7));
                System.out.println(searchRightLastRightClose(nums5,5));
                System.out.println(searchRightLastRightClose(nums4,4));
            }
            static void test(){
                int[] nums = {1,3,4,5,5,6,7,8};
                log.info("{} - {}", searchLeftFirstRightClose(nums, 5), searchLeftFirstRightOpen(nums, 5));
                log.info("{} - {}", searchRightLastRightClose(nums, 5), searchRightLastRightClose(nums, 5));
            }
            //最后加点料，二分查找法用于查找firstGreater
            //已知的一种解法是：
            static int findFirstGeaterIndex(int[] input, int key){
                int low = 0;
                int high = input.length - 1;
                while (low < high){
                    int internal = (low + high + 1)/2;
                    if (input[internal] > key) high = internal - 1;
                    else low = internal;
                }//最后low总是 = high
                return low + 1;
            }
            //上面这个太难记住，太巧妙,根据经验模板，可以写出对应的版本，然后进行简化也能得到上面那个
            static int binSearchFirstGreater(int[] nums, int target){
                int length = nums.length;
                int left = 0, right = length - 1;
                while (left <= right){//结局是要left > right
                    int middle = (left + right)/2;
                    if (nums[middle] == target){
                        left = middle + 1;
                    }else if (nums[middle] < target){
                        left = middle + 1;
                    }else if (nums[middle] > target){
                        right = middle - 1;//万一是这个middle 3,4,5 找4的nextGreater,middle->5 ???
                    }
                }
                return left;//上面虽然right = middle - 1有疑问，但这里while退出时left = right+1,又加回来了
            }
            //同样的lastLower的求解
            static int binSearchLastLower(int[] nums, int target){
                int length = nums.length;
                int left = 0, right = length - 1;
                while (left <= right){
                    int middle = (left + right)/2;
                    if (nums[middle] == target){
                        right = middle - 1;//要找target的lower，自然要往前
                    }else if (nums[middle] < target){
                        left = middle + 1;//贸然加1，middle+1比target大怎么办？？？
                    }else if (nums[middle] > target){
                        right = middle - 1;
                    }
                }
                return right;//这里解答了上面的left=middle+1的疑问，循环退出时left=right+1，left可能指向>target的index，但此时right确是最终的答案
            }
            //如何求LastGreater???
            //1. 求target的LastGreater就是target的nextGreater的nextGreater的LastLower 2.FirstGreater相等的元素向后找
            /**
              在单调栈里也研究了nextGreater等问题，只不过那个是未排序的数组，针对数组每个元素计算
             而这里是一个查找操作，只针对传入的target
              */

            public static void main(String[] args) {
                int[] nums = {-1,0,3,5,9,12};
                int[] nums2 = {-1,0,3,5,9,12};
                int[] nums3 = {5};
                int[] nums4 = {1,2,3,4,4,4,5,6,7};
                int[] nums5 = {1,5,5,6};//0 3 - 1
                //System.out.println(left_bound(nums2, 6));//找2不存在的元素不对
                System.out.println(binSearchLastLower(nums4, 5));
            }
        }

        /*
        代码模板总结：
         二分查找使用右闭区间的写法比较容易记住代码模板
         除了搜索区间的选择（右闭还是右开）对于right初值、while循环条件的影响外
         其他规律就是while循环体中对于left、right的修改都是 middle +/- 1 的， 尤其在寻找边界时left/right都要在middle的基础上修改1
         */
        static class BinarySearchRulesResearch{
            //这个类用来对比代码，发现规律
            static int RightClose(int[] nums, int target){
                int length = 0;
                int left = 0, right = length-1;
                int middle = 0;
                //classic search
                while(left <= right){
                    middle = left +(right - left)/2;
                    if(nums[middle] == target){
                        return middle;
                    }else if(nums[middle] < target){
                        left = middle + 1;
                    }else if(nums[middle] > target){
                        right = middle - 1;
                    }
                }
                //return -1;

                //left boundary
                while (left <= right){
                    middle = (left + right)/2;
                    if (nums[middle] == target){
                        right = middle - 1;
                    }else if (nums[middle] < target){
                        left = middle + 1;
                    }else if (nums[middle] > target){
                        right = middle - 1;
                    }
                }
                //return left;

                //right boundary
                while (left <= right){
                    middle = (left + right) / 2;
                    if (nums[middle] == target){
                        left = middle + 1;
                    }else if (nums[middle] < target){
                        left = middle + 1;
                    }else if (nums[middle] > target){
                        right = middle - 1;
                    }
                }
                return left;
            }

            static int RightOpen(int[] nums, int target){
                int length = 0;
                int left = 0, right = length-1;
                int middle = 0;
                //classic search
                while (left < right){
                    middle = left + (right - left)/2;
                    if (nums[middle] == target){
                        return middle;
                    }else if (nums[middle] < target){
                        left = middle + 1;
                    }else if (nums[middle] > target){
                        right = middle;
                    }
                }
                //return -1;

                //left boundary
                while (left < right) {
                    middle = (left + right) / 2;
                    if (nums[middle] == target) {
                        right = middle;
                    } else if (nums[middle] < target) {
                        left = middle + 1;
                    } else if (nums[middle] > target) {
                        right = middle - 1; // 开区间还要-1
                    }
                }
                //return right;

                //right boundary
                while (left < right){
                    middle = (left + right) / 2;
                    if (nums[middle] == target){
                        left = middle + 1;//
                    }else if (nums[middle] < target){
                        left = middle + 1;
                    }else if (nums[middle] > target){
                        right = middle;//不是middle-1
                    }
                }
                return left - 1;
            }
        }
    }

    /**
      训练递归思维 - 反转链表系列
     # 92. 反转链表的部分结点
     */
    static class RecursionInRevertingLinkedList{
        static class RevertEntireLinkedList{
            /**
              方法1: 头结点插入法  ⓶ ① ③ ④ ⑤

             head -> 3 -> 2 -> 1 -> 4 -> 5
             就是不断扫描后面的结点放到top处，同时注意1这个最开始的结点

             */
            static void revertLocally(ListNode head){
                ListNode oldTop = head.next;
                ListNode scan = oldTop.next;
                oldTop.next = null;
                while (scan != null){
                    ListNode newTop = scan;
                    scan = scan.next;
                    ListNode preTop = head.next;
                    head.next = newTop;newTop.next = preTop;
                    //oldTop.next = scan;
                }
            }
            static void revertLocally2(ListNode head){
                if (head == null) return;
                //需要跳过第一个结点进行就地插入，并且第一个结点的next还要断开防止造成死循环
                ListNode scan = head.next == null?null:head.next.next;
                ListNode scanNext = scan == null?null:scan.next;
                if (head.next != null){
                    head.next.next = null;
                }
                while (scan != null){
                    //在头部插入结点
                    scan.next = head.next;
                    head.next = scan;
                    //双指针后移
                    scan = scanNext;
                    scanNext = scanNext == null?null:scanNext.next;
                }
            }
            /**
              方法2: 原地反转法
             直接把指针依次反向，缺点是要保存很多临时变量，这里用了3个。需要知道tail在哪，用于部分反转/K组反转的子方法
             思路类似 com.javalearning.leetcode 25题的官解的迭代解法
             只要给出[start,end]区间就能给出这部分的反转结果-新的头结点，居然要用三个指针扫描
             */
            static ListNode reverseKNodes(ListNode head, ListNode tail){
                //三指针原地反转的方式
                ListNode prev = head;
                ListNode scan = prev.next;
                ListNode next = scan.next;
                while (prev != tail && scan != null){
                    scan.next = prev;
                    prev = scan;
                    scan = next;
                    next = next == null ? null : next.next;
                }
                head.next = null;
                return tail;
            }
            /**
              方法3: 递归
             反转链表的递归写法比上面的指针扫描操作细节上更简单
             */
            //原地反转的递归，这样需要保持head不被丢掉
            static ListNode revertRecursively(ListNode head, ListNode current){
                if (current == null){
                    return head;
                }
                //if (current.next == null){
                //    head.next = current;
                //    return current;
                //}
                //递归就是不断缩小问题规模，找到最后一个结点用于连接current
                ListNode tail = revertRecursively(head, current.next);
                current.next = null;
                tail.next = current;
                return current;
            }
            //尝试只传一个参数
            static ListNode revertRecursively(ListNode current){
                if (current.next == null) return current;//找到了最后一个结点，但由于没有外部变量持有这个最终的top结点，会导致最后变成两根链表 head -> 1; 8->7->6...->2->1
                ListNode tail = revertRecursively(current.next);
                //tail表示当前递归子链表的最后一个结点，current作为当前扫描到的结点显然是要放在tail后面的
                tail.next = current;
                current.next = null;
                return current;
            }
            //上面递归写法还可以再精简，不过会难看懂
            static ListNode revertListRecursively(ListNode current){
                if (current.next == null) return current;//终末结点
                ListNode tail = revertListRecursively(current.next);
                current.next.next = current;//这段逻辑可以用1~8的结点，current=7来思考
                current.next = null;
                /*
                 上面两行过于巧妙，通过此案例可理解：current = 7->8 只含有两个结点的链表如何反转
                 因为递归返回时总是会出现这种两个结点反转的情况
                 */
                return tail;//这个终末结点tail就是最终得到的head
                /*
                上述解法最后返回的是current，而这里是tail
                返回current会导致新的top结点无人引用
                 */
            }
            //第二种递归写法会把dummy结点也给revert到末尾去,所以其使用方式还有不同：head.next = revertListRecursively(head.next);
            public static void main(String[] args) {
                int nodeCount = 8;//控制结点数量可以检查代码的健壮性
                ListNode node = new ListNode(-1);
                ListNode head = node;
                for (int i = 1; i <= nodeCount; i++) {
                    ListNode newNode = new ListNode(i);
                    node.next = newNode;
                    node = newNode;
                }
                PrintUtils.printListNodes(head);
                head.next = revertListRecursively(head.next);
                PrintUtils.printListNodes(head);
            }
        }

        static class ReverseTopNLinkedList{
            /**
              反转链表前N个结点,不带dummy结点的链表
             1. 链表分成两段处理,前半部分使用整体反转的方法 【略】
             2. 直接递归反转
             */
            /*
            对于 1->2->3->4->5->6 这样的链表反转前3个结点
             结果是: 3->2->1 ->4->5->6
             */
            static ListNode reverseTopN2(ListNode head, int n){
                if (n == 1){
                    //即将到达后半部不反转的结点
                    return head;//返回反转部分的最后一个结点作为head
                }
                ListNode tail = reverseTopN2(head.next, n - 1);//tail=4,head=3,n=1 //tail=3,head=2,n=2 //tail=3,head=1,n=3 还可以，本人脑子里可以压三个栈，写代码就舒服点
                //head.next = tail; return head; n=1
                //n=2
                ListNode tmpHead = tail;
                while (n-- > 2){
                    tmpHead = tmpHead.next;
                }
                head.next = tmpHead.next;
                tmpHead.next = head;
                //递归结果应返回最终能作为head使用的结点
                return tail;
            }
            /*
             上述解法里用了一个while向下找结点插入的位置，这种插入的思路完全是沿袭了非递归解法的思路，显然要对整体反转的递归算法有所理解
             //head.next == null || n <= 1 是在n超过链表长度的情况下加上的，通常n<链表长度时判断 n==1 就可以了
             ListNode back 可以拿出来作为全局变量
             */
            public static ListNode reverseTopN3(ListNode head, int n, ListNode back){
                if (head.next == null || n <= 1){//不可以将4返回来，即便返回来也没法进行任何操作，还会让代码臃肿
                    back.next = head.next;//找到后面不反转的head,back仅用于标记（在递归结束时才能赋上值），不做运算
                    //由于Java引用传递的问题，对于4的引用要放在back对象的字段里引用起来，比较费劲
                    return head;
                }
                ListNode tail = reverseTopN3(head.next, n-1, back);//逐步缩小问题，返回的自然是要反转部分的后面结点
                //返回情况 tail=3,head=2,n=2; tail=3,head=1,n=3
                head.next.next = head;//3指向2,第二轮就发现这种head.next.next的妙处了，此时head=1跟tail=3都指向2,只要将1->2反转就可以
                head.next = back.next;//2指向3的断开，2应该指向后半部分不反转的
                return tail;//3成为真正的头结点，返回
            }

            public static void main(String[] args) {
                int nodeCount = 3;//控制结点数量可以检查代码的健壮性
                ListNode head = new ListNode(1);
                ListNode tail = head;
                for (int i = 2; i <= nodeCount; i++) {
                    tail.next = new ListNode(i);
                    tail = tail.next;
                }
                PrintUtils.printListNodes(head);
                ListNode newHead = reverseTopN3(head, 2, new ListNode(-1));
                PrintUtils.printListNodes(newHead);

                ListNode second = new ListNode(1);second.next = new ListNode(2);
                second = reverseTopN3(second, 2, new ListNode(-1));
                PrintUtils.printListNodes(second);
            }
        }

        //进入最复杂的场景，反转的部分不限定开始结束位置
        static class ReversePartialLinkedList{
            //反转head链表中第 [start,end] 个元素，从1开始计数
            static ListNode reverseBetween(ListNode head, int start, int end){
                if (start <= 1){//到达要反转的部分的起始结点
                    //反转此时head开始恰好end个结点
                    return ReverseTopNLinkedList.reverseTopN3(head, end, new ListNode(-1));
                }
                ListNode tail = reverseBetween(head.next, start - 1, end - 1);
                //返回结点不需要反转的咋写？前面写了反转的
                head.next = tail;//这一行只在start-1处对head进行操作了，其他情况原本就是head -> tail
                return head;
            }
            //使用遍历/迭代，测试用例 [5],1,1; [1,2],1,2; 比较难处理
            static ListNode reverseBetween2(ListNode head, int start, int end){
                ListNode lastLeftNode = start > 1?head:null;
                ListNode middleHead = head;
                int count = end - start + 1;
                int startcopy = start;
                while (startcopy-- > 2){
                    lastLeftNode = lastLeftNode.next;
                }
                if (start >= 2){
                    middleHead = lastLeftNode.next;
                }
                if (count > 1){
                    middleHead = ReverseTopNLinkedList.reverseTopN3(middleHead, count, new ListNode(-1));
                    if (start == 1){
                        return middleHead;
                    }
                    lastLeftNode.next = middleHead;
                }

                return head;
            }
            //上述是将链表分成两段处理，但一些链表只有一个元素或者对全部结点反转时代码兼容很难，可以针对head.next=null和start=1处理，总之没有递归简洁
            //[5],1,1; [1,2],1,2; [1,2,3,4,5,6],3,5
            public static void main(String[] args) {
                //就是这两个测试用例搞的reverseBetween2乱
                ListNode first = new ListNode(5);
                first = reverseBetween(first,1,1);
                PrintUtils.printListNodes(first);

                ListNode second = new ListNode(1);second.next = new ListNode(2);
                second = reverseBetween(second,1,2);
                PrintUtils.printListNodes(second);
            }
        }

        //上述思路借用了reverseTopN的方法，如果直接处理部分反转会有新的思路
        static class RevertPartialLinkedList{
            /*
            字符串的反转可以使用双指针交换数组元素达到
            链表的反转也可以采用此思路，只不过链表的left指针不能反向遍历，所以要借助递归返回实现反向遍历，并与left指针交换数据
            方法的递归前半部分的代码的操作是让left指向第start个结点，right指向第end个结点
            1->2->3->4->5->6(start=3,end=5)
             */
            static boolean stop;
            static ListNode left;
            static void recurseAndReverse(ListNode right, int start, int end){
                if (end == 1){
                    return;
                }
                right = right.next;
                if (start > 1) left = left.next;
                recurseAndReverse(right, start-1, end-1);
                //回溯终止条件是left与right碰头，[left,right]之间有奇数个结点时终止条件是left==right, 有偶数个结点时right会在与left相邻后变成right.next = left
                if (left == right || right.next == left){
                    stop = true;
                }
                if (!stop){
                    //回溯时交换结点的值，同时left前进，与right相向运动
                    int t = left.val;
                    left.val = right.val;
                    right.val = t;
                    left = left.next;
                }
            }
            //交换结点值head就可以不变了 TC = O(N)
            static void reverseBetween(ListNode head, int start, int end){
                left = head;
                stop = false;
                recurseAndReverse(head, start, end);
            }
            /*
            非递归的纯迭代算法
            算法思路：
            1->2->3->4->5->6
            1->2<->3 4->5->6
            1->2<->3<-4 5->6
            1->2<->3<-4<-5 6
               .------->
            1->2<-3<-4<-5 6
               .------->
            1->2  3<-4<-5 6
                  .------->
            需要使用大量临时指针保存结点，细节难
             */
            static ListNode reverseBetween2(ListNode head, int start, int end){
                if (head == null) return null;
                ListNode curr = head;
                ListNode prev = null;
                //prev指向start的前一个结点
                while (start > 1){
                    prev = curr;
                    curr = curr.next;
                    start--;end--;
                }
                ListNode con = prev;
                ListNode tail = curr;
                ListNode third = null;
                //curr继续前进将后续结点的next直接反向
                while (end > 0){
                    third = curr.next;
                    curr.next = prev;//start结点会临时连到前一个结点上，后面会重新指向end的下一个结点 A
                    prev = curr;
                    curr = third;
                    end--;
                }
                if (con != null){
                    con.next = prev;//start结点的前一个元素指向反转部分的新head
                }else {
                    head = prev;
                }
                tail.next = curr;//A处说明的情况
                return head;
            }

            /* 使用堆栈反转链表（略） */

            public static void main(String[] args) {
                ListNode first = new ListNode(5);
                first = reverseBetween2(first,1,1);
                PrintUtils.printListNodes(first);

                ListNode second = new ListNode(1);second.next = new ListNode(2);
                second = reverseBetween2(second,1,2);
                PrintUtils.printListNodes(second);

                int nodeCount = 6;//控制结点数量可以检查代码的健壮性
                ListNode head = new ListNode(1);
                ListNode tail = head;
                for (int i = 2; i <= nodeCount; i++) {
                    tail.next = new ListNode(i);
                    tail = tail.next;
                }
                head = reverseBetween2(head,3,5);
                PrintUtils.printListNodes(head);
            }


        }
    }

    /**
      # 25. k个一组反转链表
     一个链表的k个结点为一组进行反转，正整数 k <= 链表的长度
     最后剩余不足k个的结点不反转
     1->2->3->4->5->6->7

     */
    static class ReverseLinkedList{
        //完全递归解法(未测试)
        static ListNode reverseKGroup2(ListNode head, int k){
            if (head == null) return null;
            //区间[a,b)包含k个待反转元素
            ListNode a,b;
            a = b = head;
            for (int i = 0; i < k; i++) {
                //不足k个不需要反转 base case
                if (b == null) return head;
                b = b.next;
            }
            //反转前k个元素,前k个元素反转也使用递归
            ListNode newHead = null; //recurseAndReverse(head, a, b);
            //递归反转后续链表并连接起来
            a.next = reverseKGroup2(b,k);
            return newHead;
        }

        //我的解法：综合使用递归+迭代 - 外层KGroup用递归+Group内部用迭代
        static ListNode reverseKGroup(ListNode head, int k){
            if(head == null || k == 1) return head;
            ListNode curr = head;
            int kcopy = k;
            //前进k个结点到达kGroup最后一个结点
            while (curr != null && kcopy > 1){
                curr = curr.next;
                kcopy--;
            }
            //寻找到一个kGroup(至少两个结点)
            if (kcopy <= 1 && curr != null){
                //先反转剩下的结点
                ListNode newHead = reverseKGroup(curr.next, k);

                //head与curr指出一个kGroup的起始和结尾  head -> n1 -> n2 -> ... -> curr
                //                                     prev  scan  next
                //三指针原地反转的方式
                ListNode prev = head;
                ListNode scan = prev.next;
                ListNode next = scan.next;
                while (prev != curr && scan != null){
                    scan.next = prev;
                    prev = scan;
                    scan = next;
                    next = next == null ? null : next.next;
                }
                head.next = newHead;
                return curr;
            }else {
                //当前head到curr(null)不足4个结点，直接返回head
                return head;
            }
        }

        public static void main(String[] args) {
            ListNode head = ListNode.getNodeList(4);
            PrintUtils.printListNodes(head);
            head = reverseKGroup(head, 3);
            PrintUtils.printListNodes(head);
        }

    }

    /**
      # 回文单链表的判断
     */
    static class PanlidromeInLinkedList{
        /**
         回顾回文子串的寻找
         核心思想是从中心向两端扩展
         因为回文串的长度可能是奇数也可能是偶数，中心点可能是1个，也可能是2个，所以需要使用两个指针left right

         而一个字符串是否是回文串的判断比较简单
         */
        String panlidrome(char[] str, int left, int right){
            //防止索引越界
            while (left >= 0 && right < str.length && str[left] == str[right]){
                //向两边展开
                left --; right ++;
            }
            return new String(str,left + 1, right - left - 1);
        }
        /**
          使用递归解法判断整个单链表是否是回文的
         */
        static ListNode assistant = null;
        static boolean isPanlidromeLinkedList(ListNode curr){
            if (curr == null || curr.next == null){//curr==null用于处理只有一个结点的情况
                return curr == null || curr.val == assistant.val;
            }
            boolean match = isPanlidromeLinkedList( curr.next);
            if (match){
                assistant = assistant.next;
                return assistant.val == curr.val;
            }
            return false;
        }

        /**
         方案：反转单链表后比较
         */
        /**
         方案：借助二叉树的后续遍历实现倒序遍历链表
         但如果仅仅是将遍历方法定义为isPanlidromeLinkedList(head,curr)是不能实现的，head应定义为全局变量，方便在后序遍历时不断指向下一个元素
         */
        //对于一个二叉树的遍历处理可以使用下面的代码模板
        void traverse(TreeNode root){
            //仅在这里添加处理代码就是前序遍历（中 左 右）
            traverse(root.left);
            //仅在这里添加处理代码就是中序遍历（左 中 右）
            traverse(root.right);
            //仅在这里添加处理代码就是后序遍历（左 右 中）
        }
        //对于一个链表同样可以有类似的前序遍历和后序遍历
        void traverse(ListNode head){
            //前序遍历
            traverse(head.next);
            //后序遍历，链表倒过来处理，head指向元素从后向前
            //链表后序遍历的解法就是上面的递归解法
        }
        /**
          链表的后续遍历实际上借助了栈，递归本质上就是栈操作，栈操作下链表元素实现了反序
         */

        /**-=-=-=-=-= 上述解法 SC = O(N),TC = O(N), 应考虑优化SC-=-=-=-=-=-*/
        /**
          上面的递归加法比较了N次，实际上比较N/2次就可以了，递归不能在发现head与curr指向同一个结点（奇数长度）或指向相邻结点（偶数长度）时停止
          要优化空间复杂度，自然的想法就是只将一半链表入栈进行倒序遍历
          链表的取半技巧就是快慢指针

         在进一步优化sc，把递归干掉，就是把后半部分的链表反转了
         */
        static ListNode realHead = null;
        static boolean halfCompare(ListNode head){
            realHead = head;
            ListNode fast = head, slow = head;
            while (fast != null && fast.next != null){
                slow = slow.next;
                fast = fast.next.next;
            }
            //1-2-3-4 偶数时slow停留在后半部分第一个结点上，fast指向null
            //1-2-3-4-5 奇数个结点时slow停留在中间结点，fast指向最后一个结点
            if (fast != null) slow = slow.next; //slow应该表示后半部分需要反转部分的起始结点

            //1. 递归方案
            //此时的递归栈只有一半深度，优化了SC
            //return halfCompareHelper(slow);

            //2. 原地反转方案（改变原链表总是不好的，可以再反转回来。。。）                                               null
            ListNode last = reverseLocally(slow), lastcopy = last;                                             //   ↑
            while (last != null && last.val == head.val){//last!=null这个条件的原因是：1->2->3->4->5 反转后是 1->2->3->4<-5<-last
                last = last.next;
                head = head.next;
            }
            reverseLocally(lastcopy); //还原掉反转操作
            return last == null;
        }
        //一个单链表反转需要使用3个指针！
        static ListNode reverseLocally(ListNode head){
            if (head == null) return null;
            //1-2-3-4-5-6
            ListNode pre = head,
                    curr = head.next,
                    next = curr == null?null:curr.next;
            pre.next = null;
            while (curr != null){
                curr.next = pre;
                pre = curr;
                curr = next;
                next = next == null?null:next.next;
            }
            return pre;
        }

        static boolean halfCompareHelper(ListNode halfBackHead){
            if (halfBackHead.next == null){
                return halfBackHead.val == realHead.val;
            }
            boolean match = halfCompareHelper(halfBackHead.next);
            if (match){
                realHead = realHead.next;
                return realHead.val == halfBackHead.val;
            }
            return false;
        }


        public static void main(String[] args) {
            ListNode head = ListNode.generatePanlidromeLinkist();
            assistant = head;
            //System.out.println(isPanlidromeLinkedList(head));
            //System.out.println(halfCompare(head));
            boolean b = halfCompare(head);
            System.out.println(b);
        }
        /**
         回文串的寻找是从中间向两端扩展，判断回文串是从两端向中间收缩
         单链表判断回文串就只好从中间结点后续遍历了
         另外还可以把后半部分反转了进行比较，但要注意链表结点数目的奇偶性
         */
    }

    /**
      上面链表处理中提到了双指针技巧，这里进行总结
     快慢指针：解决链表问题，如链表中是否有环、链表的中间结点
     左右指针：解决数组问题，如二分查找、字符串回文串
     一、快慢指针
     1. 链表中的环的处理
     含有环的链表中寻找环的起始点（环与链表的切点）：快慢指针走到相遇，slow回到head两个指针再一步一个结点前进
                        |----- k-m -----|---m---|-------- k-m ------|
     这个脑筋急转弯的证明：* > * > * > * > * > * > * > * > * > * > * > *
                                        ^___________________________|
     第一次相遇时，慢指针走了k，块指针走了2k，在交点的m处相遇

     2. 寻找倒数第k个结点
     让快指针先前进k个结点，此后两个指针一次前进一步知道块指针为null,慢指针就是倒数第k个结点

     二、左右指针
     1. 二分查找法
     2. 反转数组
     3. 滑动窗口算法，参见子串匹配问题
     */

    /**
      二叉树系列问题 - 重要，回溯、动归、分治算法的基础。。。虽然先学了动归
     快速排序 - 二叉树前序遍历；归并排序 - 二叉树后续遍历；

     //快速排序代码框架
     void sort(int[] nums, int lo, int hi){
        //构建分界点
        int p = partion(nums, lo, hi);
        sort(nums, lo, p - 1);
        sort(nums, p + 1, hi);
     }
     //归并排序
     void sort(int[] nums, int lo, int hi){
        int mid = (lo + hi) / 2;
        sort(nums, lo, mid);
        sort(nums, mid+1, hi);
        //合并两个排序好的数组
        merge(nums, lo, mid, hi);
     }
     */


}
