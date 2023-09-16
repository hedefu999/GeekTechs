package com.javalearning.leetcode.utils;

import com.javalearning.leetcode.components.ListNode;
import com.javalearning.leetcode.components.TreeNode;

import java.util.Arrays;

public class PrintUtils {
    public static void printListNodes(ListNode l1){
        while (l1!=null){
            System.out.print(l1.val+"->");
            l1 = l1.next;
        }
        System.out.println();
    }

    public static void inorderPrintTree(TreeNode root){
        inorderPrintTreeHelper(root);
        System.out.println();
    }
    static TreeNode inorderPrintTreeHelper(TreeNode root){
        if (root == null) return null;
        inorderPrintTreeHelper(root.left);
        System.out.print(root.val + "-");
        inorderPrintTreeHelper(root.right);
        return root;
    }

    //纵向x-i轴，向下递增，横向y-j轴，向右递增，打印二维数组
    public static void printBoolMatrix(boolean[][] input, int itemlength){
        System.out.print("  ");
        for (int i = 0; input[0] != null && i < input[0].length; i++) {
            int spaces = i==0?1:(int) Math.log10(i);
            int before = (itemlength - (spaces+1))/2;
            int after = itemlength - before - spaces - 1;
            for (int j = 0; j < before; j++) {
                System.out.print(" ");
            }
            System.out.print(i);
            for (int j = 0; j < after; j++) {
                System.out.print(" ");
            }
        }
        System.out.println();
        for (int i = 0; i < input.length; i++) {
            System.out.print(i+" ");
            for (int j = 0; j < input[i].length; j++) {
                boolean b = input[i][j];
                if (b){
                    System.out.print(b+"  ");
                }else {
                    System.out.print(b+" ");
                }

            }
            System.out.println();
        }
    }

    public static void printIntArray(int[] nums){
        System.out.println(Arrays.toString(nums));
    }

    public static void main(String[] args) {
        boolean[][] input = {{true,  true,  true,true},
                {false, true,  false,true},
                {true,  false, false,true}};
        System.out.println(input.length);
        printBoolMatrix(input,6);
    }
}
