package com.javalearning.leetcode.components;

public class ListNode {
    public int val;
    public ListNode next;
    public ListNode(int x) { val = x; }

    @Override
    public String toString() {
        return val+"->"+next;
    }

    public static ListNode getNodeList(int count){
        if (count == 0) return null;
        ListNode head = new ListNode(1), middle = head;
        if (count == 1) return head;
        for (int i = 2; i <= count; i++) {
            //这样生成链表很舒服
            middle.next = middle = new ListNode(i);
        }
        return head;
    }

    public static ListNode generatePanlidromeLinkist(){
        ListNode head = new ListNode(1), middle = head;
        middle.next = middle = new ListNode(2);
        middle.next = middle = new ListNode(3);
        middle.next = middle = new ListNode(4);
        middle.next = middle = new ListNode(3);
        middle.next = middle = new ListNode(2);
        middle.next = middle = new ListNode(1);
        return head;
    }

    //链表
    public static ListNode getTwoNodes(){
        ListNode head = new ListNode(2);
        head.next = new ListNode(3);
        return head;
    }
    public static ListNode getNodesList(int nodesCount){
        ListNode tenNodes = new ListNode(0);
        ListNode tail = tenNodes;
        for (int i = 1; i < nodesCount; i++) {
            tail.next = new ListNode(i);
            tail = tail.next;
        }
        return tenNodes;
    }
    public static ListNode getNodesList(int[] nums){
        if (nums == null || nums.length == 0){
            return null;
        }
        ListNode head = new ListNode(nums[0]);
        ListNode cursor = head;
        for (int i = 1; i < nums.length; i++) {
            cursor.next = new ListNode(nums[i]);
            cursor = cursor.next;
        }
        return head;
    }

}
