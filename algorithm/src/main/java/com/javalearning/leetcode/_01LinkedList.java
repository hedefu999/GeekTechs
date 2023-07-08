package com.javalearning.leetcode;

import com.javalearning.element.ListNode;

public class _01LinkedList {


    //@number 2
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
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
    public ListNode addTwoNumbers2(ListNode l1, ListNode l2) {
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
}
