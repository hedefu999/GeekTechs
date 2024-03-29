package com.javalearning.leetcode.utils;

import com.javalearning.leetcode.components.ListNode;
import com.javalearning.leetcode.components.TreeNode;

public class TestCases {
/*  1
 2    5
3 4  6
    7
* */public static Integer[] bt1 = {1,2,5,3,4,6,null,null,null,null,null,7,null};
/*    1 15个节点，log2(length+1) =4,2^n-lenght-1==0,不需要加1层。n层
  2       3
5   4   8   9
   6 7
* */public static Integer[] bt2 = {1,2,3,5,4,8,9,null,null,6,7,null,null,null,null};
/* 注意LeetCode的树表示数组 TreeNode#plantATree 不支持
        1
    2       3
        4        5
      6   7    8   9
   10        11
* */public static Integer[] bt3 = {
                                     1,
                                    2,3,
                                null,null,4,5,
                        null,null,null,null,6,7,8,9,
null,null,null,null,null,null,null,null,10,null,null,null,11,null,null,null};
    //股票最大利润问题
    public static int[] prices0 = {3,2,5,6,10,9,7,11,14};//15:
    public static int[] prices1 = {7,1,5,3,6,4};//7:1-5,3-6
    public static int[] prices2 = {1,2,3,4};//4:1-5
    public static int[] prices3 = {7,6,4,3,1};//0
    public static int[] prices4 = {1,2,3,0,2};//3
    public static int[] prices5 = {2,1};//0
    public static int[] prices55 = {1,2};//1
    public static int[] prices6 = {2,1,4};//3
    public static int[] prices7 = {6,1,6,4,3,0,2};//7
    public static int[] prices8 = {1, 3, 2, 8, 4, 9};
    public static int[] prices9 = {1,2,4,2,5,7,2,4,9,0};

    public static TreeNode tree4FindDuplication;
    public static TreeNode tree4FindDuplicationAllZero;//逐行扫描代码是 [0,0,0,0,null,null,0,null,null,null,0]
    public static TreeNode tree4FindDuplicationPreOrder;//逐行扫描代码 [2,1,11,11,null,1]
    static {
        tree4FindDuplication = new TreeNode(1);
        TreeNode node2 = tree4FindDuplication.left = new TreeNode(2);
        TreeNode node3 = tree4FindDuplication.right = new TreeNode(3);
        node2.left = new TreeNode(4);
        TreeNode node21 = node3.left = new TreeNode(2);
        node3.right = new TreeNode(4);
        node21.left = new TreeNode(4);

        tree4FindDuplicationAllZero = new TreeNode(0);
        TreeNode nodeZero1 = tree4FindDuplicationAllZero.left = new TreeNode(0);
        TreeNode nodeZero2 = tree4FindDuplicationAllZero.right = new TreeNode(0);
        nodeZero1.left = new TreeNode(0);
        TreeNode nodeZero3 = nodeZero2.right = new TreeNode(0);
        nodeZero3.right = new TreeNode(0);

        tree4FindDuplicationPreOrder = new TreeNode(2);
        TreeNode nodePre1 = tree4FindDuplicationPreOrder.left = new TreeNode(1);
        TreeNode nodePre2 = tree4FindDuplicationPreOrder.right = new TreeNode(11);
        nodePre1.left = new TreeNode(11);
        nodePre2.left = new TreeNode(1);
    }
    public static TreeNode binSearchTreeKthSmall;
    public static TreeNode convertBSTTree;
    public static TreeNode validBSTree;
    public static TreeNode insertIntoBSTree;
    public static TreeNode deleteFromBSTree;
    static {
        binSearchTreeKthSmall = new TreeNode(5);
        TreeNode node21 = binSearchTreeKthSmall.left = new TreeNode(3);
        binSearchTreeKthSmall.right = new TreeNode(6);
        TreeNode node31 = node21.left = new TreeNode(2);
        node21.right = new TreeNode(4);
        node31.left = new TreeNode(1);

        convertBSTTree = new TreeNode(4);
        TreeNode nodecb21 = convertBSTTree.left = new TreeNode(1);
        TreeNode nodecb22 = convertBSTTree.right = new TreeNode(6);
        nodecb21.left = new TreeNode(0);
        TreeNode nodecb32 = nodecb21.right = new TreeNode(2);
        nodecb32.right = new TreeNode(3);
        nodecb22.left = new TreeNode(5);
        TreeNode nodecb34 = nodecb22.right = new TreeNode(7);
        nodecb34.right = new TreeNode(8);

        validBSTree = new TreeNode(10);
        validBSTree.left = new TreeNode(5);
        TreeNode nodev22 = validBSTree.right = new TreeNode(15);
        nodev22.left = new TreeNode(6);
        nodev22.right = new TreeNode(20);
        /*
            61
          46  66
        43
      39
        */
        insertIntoBSTree = new TreeNode(61);
        TreeNode insertInto11 = insertIntoBSTree.left = new TreeNode(46);
        insertIntoBSTree.right = new TreeNode(66);
        TreeNode insertInto21 = insertInto11.left = new TreeNode(43);
        insertInto21.left = new TreeNode(39);

        /*   5
           3    7
            4  6 8
         */
        TreeNode det11 = deleteFromBSTree = new TreeNode(5);
        TreeNode det21 = det11.left = new TreeNode(3);
        det21.right = new TreeNode(4);
        TreeNode det22 = det11.right = new TreeNode(7);
        det22.left = new TreeNode(6);
        det22.right = new TreeNode(8);
    }

}