package com.javalearning.element;

public class TreeNode {
    public int val;
    public TreeNode left;
    public TreeNode right;
    public boolean visited;//是否访问过 invite-邀请
    public TreeNode(int val){this.val = val;}

    @Override
    public String toString() {
        return val+"";
    }
}
