package com.javalearning.leetcode;

import org.junit.Test;

import java.util.*;

public class StackTagSimple {
    /**
     * #1021 括号原语，去除最外层括号
     */
    //11ms 40.3MB
    public String getPrimativeStr(String S){
        //关于长度的获取：集合叫size() 数组的是length String的是length()
        char[] chars = S.toCharArray();
        int flag = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '('){
                flag++;
                if (flag == 1) chars[i]=' ';
            }
            if (chars[i] == ')'){
                flag--;
                if (flag == 0) chars[i] = ' ';
            }
        }
        return String.valueOf(chars).replaceAll(" ", "");
    }
    //3ms 39.6MB
    public String getPrimativeStrUpgarade(String s){
        char[] chars = s.toCharArray();
        int flag = 0;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '(' && ++flag != 1){// && flag++ > 0
                builder.append(chars[i]);
                continue;
            }
            if (chars[i] == ')' && --flag != 0){// && --flag > 0
                builder.append(chars[i]);
            }
        }
        return builder.toString();
    }
    //8ms 39.4MB
    public String getPrimativeStrByStack(String s){
        //使用堆栈处理括号原语问题
        char[] chars = s.toCharArray();
        Stack<Character> stack = new Stack<>();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == ')'){
                stack.pop();
            }
            if (!stack.isEmpty()){
                builder.append(chars[i]);
            }
            if (chars[i] == '('){
                stack.push('(');
            }
        }
        return builder.toString();
    }

    @Test
    public void test1021(){
        String s1 = "(()())(())";
        String s2 = "(()())(())(()(()))";
        String s3 = "()()";
        String s4 = "";
        System.out.println(getPrimativeStrByStack(s1));
        System.out.println(getPrimativeStrByStack(s2));
        System.out.println(getPrimativeStrByStack(s3));
    }
    /**
     * #1047 删除相邻重复字符，类似寿司消除
     */
    public String removeSameConjection(String s){
        char[] chars = s.toCharArray();
        Stack<Character> stack = new Stack<>();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (!stack.isEmpty() && stack.peek().equals(chars[i])){
                stack.pop();
            }else {
                stack.push(chars[i]);
            }
        }
        Stack<Character> stack2 = new Stack<>();
        //需要再new一个stack倒腾一番。。。
        return "";
    }
    public String removeSameChar(String s){
        char[] charSrc = s.toCharArray();
        char[] charDst = new char[charSrc.length];
        int dstIndex = 0;
        for (int i = 0; i < charSrc.length; i++) {
            if (dstIndex != 0 && charDst[dstIndex-1] == charSrc[i]){
                dstIndex--;//char转成字符串时赋值0也会打印出来，赋值32是空格，所以要彻底不显示就需要截取
            }else {
                charDst[dstIndex++] = charSrc[i];
            }
        }
        return String.valueOf(charDst).substring(0,dstIndex);
    }
    @Test
    public void test79(){
        String input = "abbaca";
        System.out.println(removeSameChar(input));
    }
    /**
     * #20 有效的括号
     * isValidBrankets 使用堆栈进行处理
     */
    public boolean isValidBrankets(String s){
        HashMap<Character,Character> branketPairs = new HashMap<>();
        branketPairs.put('(',')');
        branketPairs.put('[',']');
        branketPairs.put('{','}');
        char[] chars = s.toCharArray();
        Stack<Character> stack = new Stack<>();
        for (char c : chars){
            if (!stack.empty()){
                Character peek = stack.peek();
                if (branketPairs.containsKey(c)){
                    stack.push(c);
                }else if (branketPairs.get(peek).equals(c)){
                    stack.pop();
                }else {
                    return false;
                }
            }else if (branketPairs.containsKey(c)){
                stack.push(c);
            }else {
                return false;
            }
        }
        if (stack.empty()){
            return true;
        }else {
            return false;
        }
    }
    //这种方式代码简单，性能很差，内存占用也高，不推荐
    public boolean isValidBranket2(String s){
        while (s.contains("{}")||s.contains("()")||s.contains("[]")){
            s = s.replaceAll("\\{\\}","");
            s = s.replaceAll("\\[\\]","");
            s = s.replaceAll("\\(\\)","");
        }
        if (s.length() != 0)
            return false;
        else
            return true;
    }

    @Test
    public void test20(){
        String[] testCases = {"()","()[]{}","(]","","([)]","{[]}","{[]",")}{({))[{{[}"};
        for (String str : testCases){
            System.out.print(isValidBrankets(str));
        }
        /**
         * truetruefalsetruefalsetruefalsefalse
         * truetruefalsetruefalsetruefalsefalse
         */
    }
    /**
     * 最小栈问题
     */
    //用两个栈处理
    static class MinStack{
        Stack<Integer> data = null;
        Stack<Integer> assist = null;
        public MinStack(){
            data = new Stack<>();
            assist = new Stack<>();
        }
        public void push(Integer x){
            if (assist.isEmpty()){
                assist.push(x);
            }else {
                Integer peek = assist.peek();
                if (x<=peek){
                    assist.push(x);
                }
            }
            data.push(x);
        }
        public Integer pop(){
            Integer pop = data.pop();
            if (pop == assist.peek()){
                assist.pop();
            }
            return pop;
        }
        public Integer top(){
            return data.peek();
        }
        public Integer getMin(){
            if (assist.isEmpty()){
                return 0;
            }else {
                return assist.peek();
            }

        }
    }
    //方案2 标准解法
    static class Node{
        private int value;
        private int min;
        private Node next;

        public Node(int value, int min, Node next) {
            this.value = value;
            this.min = min;
            this.next = next;
        }
    }
    static class MinStack2 {
        private Node head;
        /** initialize your data structure here. */
        public MinStack2() {

        }
        public void push(int x) {
            if (head == null){
                head = new Node(x,x,null);
            }else {
                head = new Node(x,Math.min(x,head.min),head);
            }
        }
        public void pop() {
            head = head.next;
        }
        public int top() {
            return head.value;
        }
        public int getMin() {
            return head.min;
        }
    }
    //方案3:-2 0 -3 依次进栈后 -2 -2 0 -3 -3,getMin是一次pop，pop是两次pop
    @Test
    public void test155(){
        MinStack2 minStack = new MinStack2();
        minStack.push(512);
        minStack.push(-1024);
        minStack.push(-1024);
        minStack.push(512);
        minStack.pop();
        System.out.println(minStack.getMin());
        minStack.pop();
        System.out.println(minStack.getMin());
        minStack.pop();
        System.out.println(minStack.getMin());
        minStack.pop();
        minStack.pop();
    }

    /**
     * 使用单向对列实现栈（只允许使用peek pop push2Back size isEmpty）
     *
     * LinkList（双向队列） apis:
     * pop、poll、pollFirst 出头结点,pollLast出尾结点
     * peek、peekFirst取头结点，peekLast取尾结点
     * offer、offerLast放入尾部，offerFirst 放入头部,带返回结果boolean
     * add、addLast、linkLast 放入尾部，addFirst、push、linkFirst 放入头部
     * remove(Index)移除第Index个元素（从0计数）removeFirst移除头部,removeLast移除尾部，都返回移除的元素
     */
     /**
     * 方案1：pop比push复杂,pop时把元素都移到辅助队列中只剩一个元素，剩下的就是栈顶，再互换两个队列的身份 数据队列和辅助队列
     */
    static class StackByLinkedList1{
        private LinkedList<Integer> q1 = new LinkedList<>();
        private LinkedList<Integer> q2 = new LinkedList<>();
        public void push(int x){//时空复杂度都是O(1)
            q1.add(x);
        }
        public int pop(){//时间复杂度O(n)
            while (q1.size()>1){
                Integer pop = q1.pop();
                q2.add(pop);
            }
            Integer result = q1.pop();
            LinkedList<Integer> tmp = q1;
            q1 = q2;
            q2 = tmp;
            return result;
        }
        public int top(){
            while (q1.size()>1){
                Integer pop = q1.pop();
                q2.add(pop);
            }
            Integer result = q1.pop();
            q2.add(result);
            LinkedList<Integer> tmp = q1;
            q1 = q2;
            q2 = tmp;
            return result;
        }
        public boolean empty(){
            return q1.isEmpty();
        }
    }

    /**
     * 方案2：在push的时候就转成真正的stack,两个队列倒腾
     */
    static class StackByLinkedList2{
        private LinkedList<Integer> q1 = new LinkedList<>();
        private LinkedList<Integer> q2 = new LinkedList<>();
        public void push(int x){//时间复杂度O(n) 空间复杂度O(1)
            q2.add(x);
            while (!q1.isEmpty()){
                q2.add(q1.pop());
            }
            LinkedList<Integer> tmp = q1;
            q1 = q2;
            q2 = tmp;
        }
        public int pop(){//时空复杂度O(1)
            return q1.pop();
        }
        public int top(){//时空复杂度O(1)
            return q1.peek();
        }
        public boolean empty(){
            return q1.isEmpty();
        }
    }

    /**
     * 方案3：用一个队列实现stack
     */
    static class StackByLinkedList3{
        LinkedList<Integer> q = new LinkedList<>();
        public void push(int x){//时间复杂度O(n) 空间复杂度O(1)
            int size = q.size();
            q.add(x);
            for (int i = 0; i < size; i++) {
                q.add(q.pop());
            }
        }
        //下述三个方法时空复杂度都是O(1)
        public int pop(){
            return q.pop();
        }
        public int top(){
            return q.peek();
        }
        public boolean empty(){
            return q.isEmpty();
        }
    }
    @Test
    public void test255(){
        StackByLinkedList3 stack = new StackByLinkedList3();
        stack.push(1);
        stack.push(4);
        System.out.println(stack.top());//4
        stack.push(3);
        System.out.println(stack.pop());//3
        stack.push(7);
        System.out.println(stack.top());//1 4 7,7
        System.out.println(stack.pop());//14,7
        System.out.println(stack.top());//4
        System.out.println(stack.empty());//false
        System.out.println(stack.pop());//4
        System.out.println(stack.top());//1
        System.out.println(stack.pop());//1
        System.out.println(stack.empty());//true
    }
    static class QueueByStack{
        Stack<Integer> s1 = new Stack<>();
        Stack<Integer> s2 = new Stack<>();
        public void push(int x) {//时间复杂度O(n)
            if (s1.isEmpty()){
                s1.push(x);
            }else {
                while (!s1.isEmpty()){
                    s2.push(s1.pop());
                }
                s2.push(x);
                while (!s2.isEmpty()){
                    s1.push(s2.pop());
                }
            }
        }
        public int pop() {
            return s1.pop();
        }
        public int peek() {
            return s1.peek();
        }
        public boolean empty() {
            return s1.isEmpty();
        }
    }
    //进阶版 摊还复杂度为O(1),最坏情况下为O(n)
    static class QueueByStack2{
        int head = 0;
        Stack<Integer> s1 = new Stack<>();
        Stack<Integer> s2 = new Stack<>();
        public void push(int x) {
            if (s1.isEmpty()){
                head = x;
            }
            while (!s2.isEmpty()){
                s1.push(s2.pop());
            }
            s1.push(x);
        }
        public int pop() {
            if (s1.isEmpty()){
                Integer top = s2.pop();
                head =s2.isEmpty()?0:s2.peek();
                return top;
            }else {
                while (!s1.isEmpty()){
                    s2.push(s1.pop());
                }
                int result = s2.pop();
                head =s2.isEmpty()?0:s2.peek();
                return result;
            }
        }
        public int peek() {
            return head;
        }
        public boolean empty() {
            return s1.isEmpty() && s2.isEmpty();
        }
    }
    @Test
    public void test232(){
        QueueByStack2 que = new QueueByStack2();
        System.out.println(que.empty());//false
        que.push(2);
        que.push(5);
        System.out.println(que.peek());//2
        que.push(6);
        System.out.println(que.pop());//2
        que.push(7);
        System.out.println(que.pop());//5
        System.out.println(que.empty());//false
        //测试用例1
        //System.out.println(que.empty());//false
        //que.push(2);
        //que.push(5);
        //System.out.println(que.peek());//2
        //que.push(6);
        //System.out.println(que.pop());//2
        //que.push(7);
        //System.out.println(que.pop());//5
        //System.out.println(que.empty());//false

        //测试用例2
        //que.push(1);
        //que.pop();
        //que.empty();
        que.push(1);
        que.push(2);
        que.peek();
        que.pop();
        que.pop();
        que.empty();
    }

    //巧妙使用stack避免两层for循环！
    public int[] nextGreaterElement(int[] sub, int[] total){
        Stack<Integer> stack = new Stack<>();
        HashMap<Integer,Integer> map = new HashMap<>();
        int[] res = new int[sub.length];
        for (int i = 0; i < total.length; i++) {
            while (!stack.empty() && total[i] > stack.peek())
                map.put(stack.pop(),total[i]);
            stack.push(total[i]);
        }
        while (!stack.empty()){
            map.put(stack.pop(), -1);
        }
        for (int i = 0; i < sub.length; i++) {
            res[i] = map.get(sub[i]);
        }
        return res;
    }
    //先建立value-index的hash表，比较复杂
    public int[] nextGreaterElement2(int[] nums1, int[] nums2) {
        HashMap<Integer, Integer> map = new HashMap<>();
        // 遍历 nums2 将 nums2 建立哈希表 (nums2[i], i)，时间复杂度 O(n)
        for (int i = 0; i < nums2.length; i++)
            map.put(nums2[i], i);
        int[] arr = new int[nums1.length];
        // 遍历 nums1，时间复杂度 O(n)
        for (int i = 0; i < nums1.length; i++) {
            if (map.get(nums1[i]) == nums2.length-1)
                arr[i] = -1;
            else
                // nums2 中若后一个比当前大，arr成功接收赋值，时间复杂度 O(1)，概率 1/2
                if (nums2[map.get(nums1[i])+1] > nums1[i])
                    arr[i] = nums2[map.get(nums1[i])+1];
                else {
                    int j;
                    // nums2 中若后一个小于等于当前，循环找到后面第一个最大的，时间复杂度计算介于 1-1/2^n 到 (n-1)/2 之间，概率 1/2
                    for (j = map.get(nums1[i]) + 1; j < nums2.length; j++)
                        if (nums2[j] > nums1[i])
                            break;
                    if (j == nums2.length)
                        arr[i] = -1;
                    else
                        arr[i] = nums2[j];
                }
        }
        return arr;
    }
    public int[] nextGreaterInt(int[] nums1, int[] nums2){
        int[] result = new int[nums1.length];
//        int[] dict = new int[nums2.length];
        Map<Integer,Integer> map = new HashMap<>();
        for (int i = 0; i < nums2.length; i++) {
            boolean exist = false;
            for (int j = i+1; j < nums2.length; j++) {
                if (nums2[j] > nums2[i]){
//                    dict[i] = nums2[j];
                    map.put(nums2[i],nums2[j]);
                    exist = true;
                    break;
                }
            }
            if (!exist){
//                dict[i] = -1;
                map.put(nums2[i],-1);
            }
        }
        for (int i = 0; i < nums1.length; i++) {
            result[i] = map.get(nums1[i]);
        }
        return result;
    }
    @Test
    public void test496(){
        int[] nums1 = {4,1,2};
        int[] nums2 = {1,3,4,2};
        int[] nums11 = {2,4};
        int[] nums22 = {1,2,3,4};
        int[] result1 = nextGreaterElement(nums1, nums2);//-1 3 -1
//        int[] result2 = nextGreaterElement(nums11, nums22);
//        System.out.println(Arrays.toString(result2));//3 -1
        int[] ints = nextGreaterInt(nums11, nums22);
        System.out.println(Arrays.toString(ints));
    }
    public int[] twoSum(int[] nums, int target) {
        Map<Integer,Integer> valueIndexMap = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            //valueIndexMap.put(nums[i],i); 这个放前面纠错了
            int other = target - nums[i];
            if (valueIndexMap.containsKey(other)){
                return new int[]{valueIndexMap.get(other),i};
            }
            valueIndexMap.put(nums[i],i);
        }
        return null;
    }
    @Test
    public void test1(){
        //使用hashMap
        int[] nums = {3,2,4};
        System.out.println(Arrays.toString(twoSum(nums,6)));
    }
    //使用stack
    public int calPoints(String[] ops) {
        Stack<Integer> stack = new Stack<>();
        int sum = 0;
        for (int i = 0; i < ops.length; i++) {
            String op = ops[i];
            Integer score = 0;
            switch (op){
                case "C":
                    score = stack.pop()*-1;
                    break;
                case "D":
                    score = stack.peek() * 2;
                    stack.push(score);
                    break;
                case "+":
                    Integer score1 = stack.pop();
                    score = score1 + stack.peek();
                    stack.push(score1);
                    stack.push(score);
                    break;
                default:
                    score = Integer.parseInt(op);
                    stack.push(score);

                    break;
            }
            sum+=score;
        }
        return sum;
    }
    //使用数组
    public int calPoints2(String[] ops) {
        int[] arr = new int[ops.length];
        int i=0;
        for(String s:ops){
            switch (s){
                case "+":arr[i]=arr[i-1]+arr[i-2];i++;break;
                case "D":arr[i]=2*arr[i-1];i++;break;
                case "C":arr[i-1]=0;i--;break;
                default:
                    arr[i]=Integer.valueOf(s);
                    i++;
            }
        }
        int sum=0;
        for (int j = 0; j <arr.length ; j++) {
            sum+=arr[j];
        }
        return sum;
    }
    @Test
    public void test682(){
        String[] ops = {"5","-2","4","C","D","9","+","+"};
        String[] ops2 = {"5","2","C","D","+"};
        System.out.println(calPoints(ops));
    }
    public boolean backspaceCompare(String S, String T) {
        Stack<Character> stackS = new Stack<>();
        Stack<Character> stackT = new Stack<>();
        for (Character s: S.toCharArray()){
            if (s == '#'){
                if (!stackS.empty())
                stackS.pop();
            }else {
                stackS.push(s);
            }
        }
        for (Character t : T.toCharArray()){
            if (t == '#'){
                if (!stackT.empty())
                stackT.pop();
            }else {
                stackT.push(t);
            }
        }
        while (!stackS.empty() && !stackT.empty()){
            if (stackS.pop() != stackT.pop()){
                return false;
            }
        }
        if (!stackS.empty() || !stackT.empty()){
            return false;
        }
        return true;
    }
    @Test
    public void test844(){
        String S = "ab#c", T = "ad#c";
        String S1 = "ab##", T1 = "c#d#";
        String S2 = "a##c", T2 = "#a#c";
        String S3="y#fo##f",T3= "y#f#o##f";
        System.out.println(backspaceCompare(S3,T3));
    }












//简单未做的tag - stack
//https://leetcode-cn.com/problemset/algorithms/?topicSlugs=stack&difficulty=%E7%AE%80%E5%8D%95&status=%E6%9C%AA%E5%81%9A
















}
