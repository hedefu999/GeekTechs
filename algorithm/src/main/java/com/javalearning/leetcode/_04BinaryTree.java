package com.javalearning.leetcode;

import com.javalearning.leetcode.components.ListNode;
import com.javalearning.leetcode.components.TreeNode;

public class _04BinaryTree {
//region 二叉树总结 labuladong纲领篇
/** 二叉树总结 labuladong纲领篇
- 快速排序就是个二叉树的前序遍历，归并排序就是个二叉树的后序遍历

*/
//二叉树遍历函数
void traverse(TreeNode root){
    if (root == null){
        return;
    }
    //前序位置
    traverse(root.left);
    //中序处理位置
    traverse(root.right);
    //后序处理位置
}
//使用二叉树遍历的思路遍历数组和链表，递归方式
void traverse(int[] arr, int i){
    if (i == arr.length){
        return;
    }
    //前序位置
    traverse(arr, i++);
    //后序位置（可以实现倒序遍历）
}
void traverse(ListNode head){
    if (head == null){
        return;
    }
    //前序位置
    traverse(head.next);
    //后序位置（可以实现倒序遍历）
}
//正常的都是采用迭代的方式遍历数组和链表
void traverse(int[] arr){
    for (int i = 0; i < arr.length; i++) {
    }
}
void traverse2(ListNode head){
    for (ListNode cursor=head; cursor != null; cursor=cursor.next) {
    }
}
//todo 上周进度
//endregion


/* 二叉树解题的思维模式：
1. 通过遍历一遍二叉树得到答案：使用一个traverse哈数配合外部变量实现
2. 定义递归函数，通过子问题（子树）的答案推导出原问题的答案。分解问题的思维模式
相关思维模式可以应用在 动态规划、回溯算法、分治算法、图论算法
 */
//region stage I

/*
***/


}
