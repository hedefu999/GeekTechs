package com.javalearning.leetcode;

import com.javalearning.element.ListNode;
import com.javalearning.utils.PrintUtils;
import com.javalearning.utils.TestCases;

public class _01LinkedList {
/* LC21 合并两个有序链表
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

/*
翻转链表
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
ListNode sixNodes = TestCases.getNodesList(9);
PrintUtils.printListNodes(inverseEveryKNodes(sixNodes, 3));
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

    public static void main(String[] args) {
        ListNode list1 = TestCases.getNodesList(new int[]{1, 2, 4});
        ListNode list2 = TestCases.getNodesList(new int[]{1, 3, 4});
        PrintUtils.printListNodes(mergeTwoLists2(list1,list2));
    }
}
