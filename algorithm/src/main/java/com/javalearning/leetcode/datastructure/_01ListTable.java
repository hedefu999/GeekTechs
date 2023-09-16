package com.javalearning.leetcode.datastructure;

import com.javalearning.leetcode.components.ListNode;
import com.javalearning.leetcode.utils.PrintUtils;
import org.junit.Test;

public class _01ListTable {
    public ListNode buildList(){
        int[] ints = {1,3,5,6,2,8,9};
        ListNode cursor = new ListNode(ints[0]), head = cursor;
        for (int i = 1; i < ints.length; i++) {
            cursor.next = new ListNode(ints[i]);
            cursor = cursor.next;
        }
        return head;
    }
    public ListNode buildListWithCircle(){
        int[] ints = {1,3,5,6,2,8,9};
        ListNode cursor = new ListNode(ints[0]), head = cursor;
        ListNode conjunction = null;
        for (int i = 1; i < ints.length; i++) {
            cursor.next = new ListNode(ints[i]);
            cursor = cursor.next;
            if (i == 2) conjunction = cursor;
        }
        System.out.printf("tail = %d, connection = %d\n", cursor.val,conjunction.val);
        cursor.next = conjunction;
        return head;
    }
    /**
    @title 反转单向链表
    使用常规方法+递归方法
    * */
    public ListNode reverseList(ListNode l1){
        ListNode l2 = null;
        while (l1 != null){
            ListNode temp = l1;
            l1 = l1.next;
            temp.next = l2;
            l2 = temp;
        }
        return l2;
    }
    public ListNode reverseByRecursion(ListNode head){
        if (head == null || head.next == null){
            return head;
        }
        ListNode temp = head;
        head = head.next;
        ListNode newHead = reverseByRecursion(head);
        head.next = temp;
        temp.next = null;//少这一步会在最后变成死循环
        return newHead;
    }
    public ListNode reverse(ListNode head){
        if (head == null || head.next == null)
            return head;
        ListNode temp = head.next;//保存下一个节点
        ListNode newHead = reverse(head.next);//整体思维，宏观语义
        temp.next = head;//连上头与递归部分
        head.next = null;//调整尾部
        return newHead;//返回头节点
    }
    @Test
    public void test94(){
        int[] ints = {1,3,5,6,2};
        ListNode cursor = new ListNode(ints[0]), head = cursor;
        for (int i = 1; i < ints.length; i++) {
            cursor.next = new ListNode(ints[i]);
            cursor = cursor.next;
        }
        PrintUtils.printListNodes(head);
        ListNode newHead = reverseByRecursion(head);
        PrintUtils.printListNodes(newHead);
    }
    /**
     * 单链表判断是否有环,有环把连接点打印出来
     */
    public void checkCircleInSingleLinkedList(ListNode head){
        //自己的代码总是又臭又长
        ListNode cursorA = head;
        ListNode cursorB = head;
        ListNode circleJoint = null;
        while ((cursorA != null || cursorB != null) && circleJoint == null){
            cursorA = cursorA == null?null:cursorA.next;
            cursorB = cursorB == null?null:(cursorB.next == null?null:cursorB.next.next);
            if (cursorA == cursorB){
                circleJoint = cursorA;
            }
        }
        if (circleJoint != null){
            System.out.printf("有环：%d",circleJoint.val);
        }
    }
    public void checkCircle(ListNode head){
        ListNode fast = head;
        ListNode slow = head;
        while (fast != null && fast.next != null){
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast){
                break;
            }
        }
        if (fast != null){
            System.out.println("有环，相遇在："+slow.val);
            slow = head;
            ListNode pre = null;
            while (slow != fast){
                slow = slow.next;
                pre = fast;
                fast = fast.next;
            }
            System.out.printf("连结点叫做：%d,故障结点是：%d\n",slow.val,pre.val);
            pre.next = null;
            System.out.println("解环完成");
            PrintUtils.printListNodes(head);
        }
    }
    @Test
    public void test69(){
        ListNode head = buildListWithCircle();
        checkCircle(head);
    }

}
