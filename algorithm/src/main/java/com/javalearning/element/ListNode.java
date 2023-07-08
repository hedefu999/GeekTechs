package com.javalearning.element;

public class ListNode {
    public int val;
    public ListNode next;
    public ListNode(int x) { val = x; }

    @Override
    public String toString() {
        return val+"";
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

}
