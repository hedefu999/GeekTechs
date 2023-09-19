package com.javalearning.leetcode.components;

public class TreeNode {
    public int val;
    public TreeNode left;
    public TreeNode right;
    public boolean visited;//是否访问过
    public TreeNode(int val){this.val = val;}

    @Override
    public String toString() {
        return val+"";
    }

    //使用数组构建一棵二叉树，数组形如 [3,9,20,null,null,15,7]
    public static TreeNode plantATree(Integer[] nodes){
        TreeNode root = new TreeNode(nodes[0]);
        buildTree(root, 0, nodes);
        return root;
    }
    public static void buildTree(TreeNode root, int index, Integer[] nodes){
        int leftIndex = 2 * index + 1;
        if (leftIndex < nodes.length){
            Integer leftValue = nodes[leftIndex];
            if (leftValue != null){
                TreeNode leftNode = new TreeNode(leftValue);
                root.left = leftNode;
                buildTree(leftNode, leftIndex, nodes);
            }
        }
        int rightIndex = 2 * index + 2;
        if (rightIndex < nodes.length){
            Integer rightValue = nodes[rightIndex];
            if (rightValue != null){
                TreeNode rightNode = new TreeNode(rightValue);
                root.right = rightNode;
                buildTree(rightNode, rightIndex, nodes);
            }
        }
    }

    public static void main(String[] args) {

    }
}
