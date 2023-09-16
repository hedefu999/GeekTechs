package com.javalearning.leetcode;

import com.javalearning.leetcode.components.ListNode;
import com.javalearning.leetcode.utils.TestCases;

import java.util.Comparator;
import java.util.PriorityQueue;

public class _01LinkedList {
//region stage I 基本技巧
/* +LC21 合并两个有序链表
自己的解法，内存占用高点，性能100%
递归解法
***/static ListNode mergeTwoLists(ListNode list1, ListNode list2){
    ListNode head = new ListNode(-99);//虚拟头结点业内常称为 dummy
    ListNode cursor = head;
    while (list1 != null && list2 != null){
        boolean list1First = list1.val <= list2.val;
        if (list1First){
            cursor.next = list1;
            list1 = list1.next;
        }else {
            cursor.next = list2;
            list2 = list2.next;
        }
        cursor = cursor.next;
        cursor.next = null;
    }
    if (list1 != null){
        cursor.next = list1;
    }
    if (list2 != null){
        cursor.next = list2;
    }
    return head.next;
}
static ListNode mergeTwoLists2(ListNode list1, ListNode list2){
    if (list1 == null){
        return list2;
    }
    if (list2 == null){
        return list1;
    }
    boolean list1Smaller = list1.val < list2.val;
    if (list1Smaller){
        list1.next = mergeTwoLists2(list1.next, list2);
        return list1;
    }else {
        list2.next = mergeTwoLists2(list1, list2.next);
        return list2;
    }
}

/* +LC206 翻转链表
1>2>3>4
1 2>3>4
2>1 3>4
ListNode tenNodesList = TestCases.getTenNodesList();
PrintUtils.printListNodes(tenNodesList);
PrintUtils.printListNodes(reverseNodeList(tenNodesList));
***/static ListNode reverseNodeList(ListNode head){
    ListNode prev = head;
    ListNode curr = prev.next;
    if (curr == null){//只有一个元素
        return prev;
    }
    prev.next = null;
    ListNode next;
    do {
        next = curr.next;
        curr.next = prev;
        prev = curr;
        curr = next;
    }while (next != null);
    return prev;
}//+递归翻转整个链表
static ListNode reverseNodeList2(ListNode head){
    if (head.next == null){
        return head;
    }
    ListNode newHead = reverseNodeList2(head.next);
    head.next.next = head;//递归翻转链表整体时，通过考虑最后的1个节点、2个节点出发，两个节点的翻转不是 newHead.next=head，而是通过head.next.next实现尾部节点指向自己
    head.next = null;//掐断原来头部元素的指针，防止死循环
    return newHead;
}

/*
翻转每k个链表元素，保证链表节点数量n可被k整除
给定一个含有 n 个元素的链表，现在要求每 k 个节点一组进行翻转，打印翻转后的链表结果。其中，k 是一个正整数，且可被 n 整除。
例如，链表为 1 -> 2 -> 3 -> 4 -> 5 -> 6，k = 3，则打印 321654。
思路，需要拆分成多个链表反转
1>2>3>4>5>6
2><1 3>4>5>6
3>2><1 4>5>6
3>2><1 5><4 6
3>2><1 6>5><4
3>2>1>6>5><4
***/static ListNode inverseEveryKNodes(ListNode head, int k){
        ListNode nextHead = head;
        ListNode cursor = null;
        ListNode resultHead = null;
        ListNode lastTail = null;
        ListNode currHead = null;
        while (nextHead != null){
            currHead = nextHead;
            cursor = nextHead;
            for (int i = 1; i < k; i++) {
                cursor = cursor.next;
            }
            nextHead = cursor.next;
            cursor.next = null; //这里采用了截断链表翻转的做法
            ListNode newHead = reverseNodeList(currHead);
            if (resultHead == null){//只有第一次进入才需要的操作
                lastTail = currHead;
                resultHead = newHead;
            }else {
                lastTail.next = newHead;
                lastTail = currHead;
            }
        }
        return resultHead;
    }

/* LC92 反转中间部分链表
输入：head = [1,2,3,4,5], left = 2, right = 4 输出：[1,4,3,2,5]
***/static ListNode reverseBetween(ListNode head, int left, int right) {
    ListNode leftPartTail = new ListNode(-99); //left=1咋办
    leftPartTail.next = head;
    boolean changeHead = true;
    for (int i = 1; i < left; i++) {
        changeHead = false;
        leftPartTail = leftPartTail.next;
    }
    ListNode prev = leftPartTail.next;
    ListNode middleTail = prev;
    leftPartTail.next = null;
    if (prev == null) return head;
    ListNode curr = prev.next;
    ListNode next = curr == null ? null : curr.next;
    prev.next = null;
    for (int i = left; i < right; i++) {
        curr.next = prev;
        prev = curr;
        curr = next;
        next = next==null?null:next.next;
    }
    leftPartTail.next = prev;
    middleTail.next = curr;
    return changeHead?leftPartTail.next:head;
}//递归解法反转前n个节点
static ListNode rightPartHead = null;
static ListNode reverseFirstNNode(ListNode head, int n){
    if (n == 1){
        rightPartHead = head.next;
        return head;
    }
    ListNode listNode = reverseFirstNNode(head.next, n-1);
    head.next.next = head;
    head.next=rightPartHead;//这里在递归返回时会不断修改指向后半部分不翻转的list的head
    return listNode;
}
//递归解法翻转中间部分连续的节点
static ListNode reverseBetween2(ListNode head, int left, int right) {
    if (right == 1){
        rightPartHead = head.next;
        return head;
    }
    if (left <= 1 && right > 1){//看做翻转整个链表
        ListNode newHead = reverseBetween2(head.next, left-1, right-1);
        head.next.next = head;
        head.next = rightPartHead;//调试发现右侧的不翻转部分总是丢失，这里使用外部指针来解决。。。
        return newHead;
    }else {//不需要翻转，就保持原顺序 head -> newHead
        ListNode newHead = reverseBetween2(head.next, left-1, right-1);
        head.next = newHead;
        return head;
    }
}
//递归法翻转中间部分连续的节点3
static ListNode reverseBetween3(ListNode head, int left, int right){
    if (left == 1){
        return reverseFirstNNode(head,right);
    }
    head.next = reverseBetween3(head.next, left - 1, right - 1);
    return head;
}

/* LC23 合并K个升序链表
需要使用PriorityQueue这种队列对内部元素进行排序，优先队列就是小根堆，使用了java API进行建堆
时间复杂度计算：queue容量 list.length,取元素时间复杂度 O(log lists.length)，链表总元素数量N，要全部遍历，所以整体时间复杂度O(N log lists.length)
int[][] nums = {{1,4,5},{1,3,4},{2,6}};
ListNode[] nodeLists = TestCases.getNodeLists(nums);
ListNode listNode = mergeKLists(nodeLists);
PrintUtils.printListNodes(listNode);
***/public static ListNode mergeKLists(ListNode[] lists) {
    if (lists == null || lists.length == 0){
        return null;
    }
    PriorityQueue<ListNode> pq = new PriorityQueue<>(lists.length,
            Comparator.comparingInt(a -> a.val));
    ListNode dummy = new ListNode(-99);
    ListNode head = dummy;
    for (ListNode list : lists) {
        pq.add(list);
    }
    while (! pq.isEmpty()){
        ListNode min = pq.poll();
        head.next = min;
        if (min.next != null){
            pq.add(min.next);
        }
        head = head.next;
    }
    return dummy.next;
}
//原地合并K个链表，需要使用前面的合并两个有序链表的方法,时间复杂度 O(nk^2) k是链表数量，n是最大链表长度
public static ListNode mergeKLists2(ListNode[] lists){
        ListNode head = new ListNode(-99);
        for (ListNode list : lists) {
            head = mergeTwoLists(head,list);
        }
        return head;
    }

/**-=-=-=-=-=-=-=-=-=-=-=-=-=-= 链表中的双指针解法 -=-=-=-=-=-=-=-=-=-=-=-=-=-=**/
/* +LC86 小于某个阈值的链表元素集中到前部，保持原始相对位置
输入：head = [1,4,3,2,5,2], x = 3 输出：[1,2,2,4,3,5]
需要使用双指针解决
将链表中的元素视作独立的节点，收集到两个链表中，再将链表连起来
***/static ListNode partition2(ListNode head, int x) {
    ListNode lowerHead = new ListNode(-99);
    ListNode higherHead = new ListNode(-99);
    ListNode higher = higherHead;
    ListNode lower = lowerHead;
    ListNode cursor = head;
    while (cursor != null){
        if (cursor.val<x){
            lower.next = cursor;
            lower = lower.next;
        }else {
            higher.next = cursor;
            higher = higher.next;
        }
        ListNode temp = cursor;
        cursor = cursor.next;//前进到下一个
        temp.next = null;//上一个节点别再指向我，达到断开所有节点的效果
    }
    lower.next = higherHead.next;
    return lowerHead.next;
}
static ListNode partition(ListNode head, int x) {
    if (head==null)return null;
    ListNode dummy = new ListNode(-99);//如果有元素要挪到前面，就需要dummy
    dummy.next = head;
    ListNode tail = dummy;
    ListNode curr = head;
    ListNode prev = dummy;
    ListNode next = curr.next;
    while (curr != null){
        if (curr.val < x){
            prev.next = curr.next;
            curr.next = tail.next;
            tail.next = curr;
            boolean same = prev == tail;
            tail = tail.next;
            curr = next;
            if (same){//遇到了一些的奇葩的测试用例，挪走的元素又插回来了，此时prev需要前进
                prev = prev.next;
            }
        }else {
            prev = curr;
            curr = next;
        }
        next = next==null?null:next.next;
    }
    return dummy.next;
}
//上面是自己的单链表原地交换勉强写出来一个，但里面有个特判，下面是不带特判的写法,推荐
static ListNode partition3(ListNode head, int x) {
    if (head == null || head.next == null)
        return head;
    ListNode dummy = new ListNode(-99);
    dummy.next = head;
    ListNode prev = dummy;
    ListNode tail = dummy;
    ListNode curr = head;
    while (curr != null){
        if (curr.val < x){
            if (prev != tail){
                prev.next = curr.next;
                curr.next = tail.next;
                tail.next = curr;
                tail = tail.next;
                curr = prev.next;
            }else {
                prev = tail = curr;
                curr = curr.next;
            }
        }else {
            prev = curr;
            curr = curr.next;
        }
    }
    return dummy.next;
}

/* LC19 删除链表的倒数第N个节点
如果这个算法里没有dummy，会遇到 n=5 没法判断头结点有没有被删除的情况，使用val比较有漏洞
head = [1,2,3,4,5], n = 2 -> [1,2,3,5]
***/static ListNode removeNthFromEnd(ListNode head, int n) {
    ListNode dummy = new ListNode(-99);
    dummy.next = head;
    /*-=-=下面就是查到倒数第n个节点的代码=-=*/
    ListNode prev = dummy;//prev next作为两个游标使用
    ListNode next = head;
    while (n-->1){
        next = next.next;
    }
    while (next.next != null){
        prev = prev.next;
        next = next.next;
    }
    /*-=-=-=-=-=-=-=-=-=-=-=-=*/
    ListNode temp = prev.next;
    prev.next = prev.next.next;
    temp.next = null;temp=null;
    return dummy.next;
}

/* LC876 链表的中间节点
head = [1,2,3,4,5] 输出：[3,4,5] 解释：链表只有一个中间结点，值为 3 。
head = [1,2,3,4,5,6] 输出：[4,5,6] 解释：该链表有两个中间结点，值分别为 3 和 4 ，返回第二个结点。
***/static ListNode middleNode(ListNode head) {
    ListNode first=head;ListNode second=head;
    while (second != null && second.next != null){
        first = first.next;
        second = second.next.next;
    }
    return first;
}
/* LC141 环形链表 判断是否有环
***/static boolean hasCycle(ListNode head) {
    ListNode fast = head;
    ListNode slow = head;
    while (fast != null && fast.next != null){
        fast = fast.next.next;
        slow = slow.next;
// 注意不要使用val比较，允许节点的val相同
//        if (fast!=null && fast.val == slow.val){
//            return true;
//        }
        if (fast == slow){
            return true;
        }
    }
    return false;
}
/* LC142 环形链表II 返回环的起点
相关数学证明：假设相遇点距离环起点 m，慢游标走了k步，快游标走了2k步，则环起点距离head k-m，环内相遇点距离环起点也有k-m，再各一步一步走就能相遇在环起点
***/static ListNode detectCycle(ListNode head) {
    ListNode slow = head;
    ListNode fast = head;
    int index=0;
    while (fast != null && fast.next != null){
        slow = slow.next;index++;
        fast = fast.next.next;
        if (fast == slow){
            fast=head;
            while (fast != slow){
                slow = slow.next;
                fast = fast.next;
            }
            return fast;
        }
    }
    return null;
}
/* LC160 相交链表  相交链表，起点不同，构造相同长度让它们相遇
测试用例
1. a1-a2-a3-a4-a5-c1; b1-c1;
2. c1; c1;
3. a1-c1;c1;
ListNode la = TestCases.getNodesList(new int[]{1,9,1,2,4});
ListNode lb = TestCases.getNodesList(new int[]{3,2,4});
ListNode l1 = TestCases.getNodesList(new int[]{3,2});
ListNode l2 = TestCases.getNodesList(new int[]{2});
ListNode l3 = TestCases.getNodesList(new int[]{3});
ListNode l4 = TestCases.getNodesList(new int[]{3});

TestCases.buildIntersectList(la,lb,-1, -1);
System.out.println(getIntersectionNode(la,lb));
TestCases.buildIntersectList(la,lb,3, 1);
System.out.println(getIntersectionNode(la,lb));
TestCases.buildIntersectList(l1,l2,1, 0);
System.out.println(getIntersectionNode(l1,l2));
//由于java的引用传递，这种相交链表无法构建,将l3传两遍吧，没必要走构建方法
TestCases.buildIntersectList(l3,l4,0, 0);
System.out.println(getIntersectionNode(l3,l4));

证明：
a: a1-a2-a3-c1-c2
b: b1-b2-c1-c2
需要双指针解决，一个链表走完走到另一个链表上可以解决问题，但两个指针的走速不需要有差异
这样相当于两个链表连起来，这样能保证两个指针总能同时到达 c1 节点
a1-a2-a3-c1-c2-b1-b2-c1...
b1-b2-c1-c2-a1-a2-a3-c1...
a1 a2 a3 b1 b2
b1 b2 a1 a2 a3
***/static ListNode getIntersectionNode(ListNode headA, ListNode headB) {
    ListNode up = headA;
    ListNode down = headB;
    //可以设置标记位保持代码简洁，多花费内存
    boolean upSwitch = false;
    boolean downSwitch = false;
    //至多变量两轮
    while (up != null && down != null){
        if (up == down){
            return up;
        }
        up = up.next;down=down.next;
        //有人到头,就换头
        if (up == null && !upSwitch){
            up = headB;
            upSwitch=true;
        }
        if (down == null && !downSwitch){
            down = headA;
            downSwitch = true;
        }
    }
    return null;
}//推荐写法，巧妙利用 null == null ，返回任意一个null即可。将null作为不相交链表的交点（平行线相较于无穷远？）
static ListNode getIntersectionNode2(ListNode headA, ListNode headB){
    ListNode p1 = headA, p2 = headB;
    while (p1 != p2) {
        // p1 走一步，如果走到 A 链表末尾，转到 B 链表
        if (p1 == null) p1 = headB;
        else            p1 = p1.next;
        // p2 走一步，如果走到 B 链表末尾，转到 A 链表
        if (p2 == null) p2 = headA;
        else            p2 = p2.next;
    }
    return p1;
}//也可以让两个链表各自成环，各自delta=1遍历，相交链表总会走到同一节点，但运算完要改回链表原状态

//endregion

//region stage II 巩固
/* LC2 两数相加
***/static ListNode addTwoNumbers(ListNode l1, ListNode l2) {
    ListNode l3 = new ListNode(0), l3head = l3;
    int over10 = 0;
    while(l1 != null || l2 != null){
        int sum = (l1==null?0:l1.val) + (l2==null?0:l2.val) + l3.val;
        if(sum >= 10){
            l3.val = sum - 10;
            over10 = 1;
        }else{
            l3.val = sum;
            over10 = 0;
        }
        l1 = l1==null?null:l1.next;
        l2 = l2==null?null:l2.next;
        if(l1 != null || l2 != null || over10 != 0){
            //l1==null && l2 == null &&
            l3.next = new ListNode(over10);
            l3 = l3.next;
        }
    }
    return l3head;
}
/* LC1 两数之和
***/static ListNode addTwoNumbers2(ListNode l1, ListNode l2) {
        ListNode l3 = new ListNode(0), l3head = l3;
        while(l1 != null || l2 != null){
            int sum = (l1==null?0:l1.val) + (l2==null?0:l2.val) + l3.val;
            l3.val = sum % 10;
            l1 = l1==null?null:l1.next;
            l2 = l2==null?null:l2.next;
            if(l1 != null || l2 != null || sum>10){
                l3.next = new ListNode(sum/10);
                l3 = l3.next;
            }
        }
        return l3head;
    }

/* LC83 删除升序链表中的重复元素
head = [1,1,2]  输出：[1,2]
head = [1,1,2,3,3]  输出：[1,2,3]
***/static ListNode deleteDuplicates(ListNode head) {
    if (head==null)return null;
    ListNode slow = new ListNode(-99);slow.next=head;
    ListNode fast = head;
    while (fast != null){//相等和不相等时都可以移除节点，这里的写法可以保证移除的节点不会组成链，更容易被回收
        if (slow.val == fast.val){
            fast = fast.next;
            slow.next.next = null;
            slow.next = fast;
        }else {
            slow = slow.next;
            fast = fast.next;
        }
    }
    return head;
}

//endregion

//region stage III

//endregion



    public static void main(String[] args) {
        ListNode la = TestCases.getNodesList(new int[]{});
        ListNode lb = TestCases.getNodesList(new int[]{3,2,4});
        System.out.println(deleteDuplicates(la));
    }
}
