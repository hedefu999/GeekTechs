package com.javalearning.leetcode.labuladong;

import com.javalearning.leetcode.components.TreeNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class _00Recursion {
    /**
     有效括号序列
     递归的拆分：基本情况base case, 构造器constructor
     运用数学归纳法求解递归问题

     BaseCase: 规定空串 "" ∈ S
     Constructor：S中的任意两个串s和t这样组合得到v = "("+ s +")" + t,则v ∈ S   <--
     对于正整数n=3,返回序列集合：["((()))","(()())","(())()","()(())","()()()"]
     */
    static class ValidBranketSequence{
        //递归解法，构造器难以理解
        static List<String> generateAllValidBrankets(int n){
            if (n == 0) return Arrays.asList("");
            List<String> ans = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                for (String left : generateAllValidBrankets(i)){
                    for (String right : generateAllValidBrankets(n-i-1)){
                        ans.add("("+left+")"+right);
                    }
                }
            }
            return ans;
        }
        /*
        类似动态规划的解法
         上一轮的字符串作为一个整体，新括号要么包围、要么放在前面或后面，再加点防重复
         () -> ()() -> ()()() / (()())
            -> (()) -> ((())) / (())() / ()(())
        * */
        static List<String> generateAllValidBrankets2(int n){
            List<String> start = Arrays.asList("");
            for (int i = 0; i < n; i++) {
                List<String> thisRound = new ArrayList<>();
                for (String sta : start){
                    thisRound.addAll(helper(sta));
                }
                start = thisRound;
            }
            return start;
        }
        static List<String> helper(String start){
            if (start.equals("")){
                return Arrays.asList("()");
            }else if (!start.contains("((")){
                return Arrays.asList('('+start+')',"()"+start);
            }else {
                return Arrays.asList('('+start+')',"()"+start,start+"()");
            }
        }
        public static void main(String[] args) {
            System.out.println(generateAllValidBrankets2(3));
        }
    }
    /**
     # 二叉树生成
     给定一个正整数n，返回所有包含n个结点的合法二叉树
     */
    static class BinaryTreeGeneration{
        /*
        ## 递归解法
        由顶自下，逐层递归
        n表示结点数目，那么给定一个一维结点列表：① ② ③ ④ ⑤ ⑥ ⑦ ⑧ ⑨ ⑩ ⑪ ⑫
        从左到右逐个取根结点，根节点左侧的作为左子树的结点集合，右侧的作为右子树结点集合
        结点集合再向下分配，作为子问题递归求解
        * */
        static List<TreeNode>  generateBinaryTree(int n){
            if (n == 0) return Collections.emptyList();
            return helper(1,n);
        }
        //helper返回的是所有可能的root结点集合
        static List<TreeNode> helper(int low, int high){
            if (low == high) return Collections.singletonList(new TreeNode(low));
            if (low > high) return Collections.emptyList();
            List<TreeNode> res = new ArrayList<>();
            for (int i = low; i < high; i++) {
                for(TreeNode left : helper(low,i-1)){
                    for (TreeNode right : helper(i+1,high)){
                        TreeNode curr = new TreeNode(i);
                        curr.left = left;
                        curr.right = right;
                        res.add(curr);
                    }
                }
            }
            return res;
        }
        /*
        打印一棵二叉树
        如何打印一棵二叉树？？？？能看就行, 未能实现，todo 待研究
            1    空4 = 2^(l-1)
         2     3  空
        x  x 4   5
        xxx xx x x 6
        * */
        static void printBinTree(TreeNode root){
            int depth = calcBinTreeDepth(root);
            printBinTreeHelper2(root,depth,false);
        }
        static int calcBinTreeDepth(TreeNode root){
            if (root == null) return 0;
            int depth = 0;
            depth = 1 + Math.max(calcBinTreeDepth(root.left),calcBinTreeDepth(root.right));
            return depth;
        }
        static void printBinTreeHelper2(TreeNode root, int depth, boolean hasBrother){
            int pre = (int) Math.pow(2,depth-1);
            for (int i = 0; i < pre; i++) {
                System.out.print(" ");
            }
            if (depth < 0) return;
            System.out.print(root==null?" ":root.val);
            //if (!hasBrother) System.out.println("");
            printBinTreeHelper2(root == null?null:root.left, depth-1,true);
            printBinTreeHelper2(root == null?null:root.right, depth-1,false);
            System.out.println(" ");
        }
        static void printBinaryTreeHelper(List<TreeNode> nodes, int depth){
            if (nodes == null) return;
            int empty = 2^depth/nodes.size();
            for (TreeNode node : nodes){
                System.out.print(node.val);
                for (int i = 0; i < empty; i++) {
                    System.out.print(" ");
                }
            }
        }
        /*
         分层逐步求解. 失败
        * */
        public static void main(String[] args) {
            List<TreeNode> binTreeNodes = generateBinaryTree(6);
            TreeNode binNode = new TreeNode(1);
            TreeNode binNode2 = new TreeNode(2);
            TreeNode binNode3 = new TreeNode(3);
            TreeNode binNode4 = new TreeNode(4);
            TreeNode binNode5 = new TreeNode(5);
            TreeNode binNode6 = new TreeNode(6);
            binNode.left = binNode2;
            binNode.right = binNode3;
            binNode3.left = binNode4;
            binNode3.right = binNode5;
            binNode5.right = binNode6;
            //System.out.println(calcBinTreeDepth(binNode));
            printBinTree(binNode);
        }
    }
}
