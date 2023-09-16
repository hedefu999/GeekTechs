package com.javalearning.leetcode.datastructure;

import com.javalearning.leetcode.components.TreeNode;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Stack;

public class _02BinaryTree {
    public TreeNode plantATree(){
        TreeNode b2 = new TreeNode(2);
        TreeNode b3 = new TreeNode(3);
        TreeNode b5 = new TreeNode(5);
        TreeNode b6 = new TreeNode(6);
        TreeNode b8 = new TreeNode(8);
        TreeNode b11 = new TreeNode(11);
        b2.right = b3;
        b3.left = b5;
        b5.left = b6; b5.right = b8;
        return b2;
    }

    /**
     @title 二叉树的遍历
     前中后序三种的递归、堆栈写法
     写遍历程序时，不要将左节点与根节点看作两个节点，二叉树里只有两种节点：根结点和它的右结点
     */
    //中序遍历 2 6 5 8 3
    //递归写法
    public void inorderTraversalRecursively(TreeNode root){
        if (root == null){
            return;
        }
        inorderTraversalRecursively(root.left);
        System.out.print(root.val);
        inorderTraversalRecursively(root.right);
    }
    //堆栈写法
    public void inorderByStack2(TreeNode root){
        Stack<TreeNode> stack = new Stack<>();
        while (root!=null){
            stack.push(root);
            root = root.left;
        }
        while (!stack.empty()){
            root = stack.pop();
            System.out.print(root.val);
            root = root.right;
            while (root != null){
                stack.push(root);
                root = root.left;
            }
        }
    }
    public void inorderByStack(TreeNode root){
        Stack<TreeNode> stack = new Stack<>();
        while (root != null || !stack.isEmpty()){
            while (root != null){
                stack.push(root);
                root = root.left;
            }
            if (!stack.isEmpty()){
                root = stack.pop();
                System.out.print(root.val);
                root = root.right;
            }
        }
    }
    //前序遍历 23568
    public void preorderTraversalRecursively(TreeNode root){
        if (root == null){
            return;
        }
        System.out.print(root.val);
        preorderTraversalRecursively(root.left);
        preorderTraversalRecursively(root.right);
    }
    public void preorderByStack2(TreeNode root){
        Stack<TreeNode> stack = new Stack<>();
        while (root != null){
            System.out.print(root.val);
            stack.push(root);
            root = root.left;
        }
        while (!stack.isEmpty()){
            root = stack.pop();
            root = root.right;
            while (root != null){
                System.out.print(root.val);
                stack.push(root);
                root = root.left;
            }
        }
    }
    public void preorderByStack3(TreeNode root){
        Stack<TreeNode> stack = new Stack<>();
        while (root != null || !stack.isEmpty()){
            while (root != null){
                System.out.print(root.val);
                stack.push(root);
                root = root.left;
            }
            if (!stack.isEmpty()){
                root = stack.pop();
                root = root.right;
            }
        }
    }

    //后序遍历 6 8 5 3 2
    public void postorderTraversalRecursively(TreeNode root){
        if (root == null){
            return;
        }
        postorderTraversalRecursively(root.left);
        postorderTraversalRecursively(root.right);
        System.out.print(root.val);
    }
    /*
    后序遍历的非递归算法是三种顺序中最复杂的，原因在于，后序遍历是先访问左、右子树,再访问根节点，而在非递归算法中，利用栈回退到时，并不知道是从左子树回退到根节点，还是从右子树回退到根节点，如果从左子树回退到根节点，此时就应该去访问右子树，而如果从右子树回退到根节点，此时就应该访问根节点。所以相比前序和后序，必须得在压栈时添加信息，以便在退栈时可以知道是从左子树返回，还是从右子树返回进而决定下一步的操作。
     */
    //错误写法记录
    public void postorderByStack2(TreeNode root){
        Stack<TreeNode> stack = new Stack<>();
        while (root != null){
            stack.push(root);
            root = root.left;
        }
        while (!stack.isEmpty()){
            root = stack.pop();
            root = root.right;
            while (root != null){
                stack.push(root);
                root = root.left;
            }
            System.out.print(root.val);
        }
    }
    /*
    使用队列进行广度优先遍历二叉树的关键是父结点的左右子树添加到队列是放在后面，这样才同级遍历
    * */
    public void postorderByStack(TreeNode root) {
        int left = 1;
        int right = 2;
        Stack<TreeNode> stack = new Stack<>();
        Stack<Integer> stack1 = new Stack<>();
        while (root != null || !stack.isEmpty()){
            while (root != null){
                stack.push(root);
                stack1.push(left);
                root = root.left;
            }
            while (!stack.isEmpty() && stack1.peek() == right){
                stack1.pop();
                System.out.print(stack.pop().val);//弹出并打印左结点，左结点与右结点应看作一类结点
            }
            if (!stack.isEmpty() && stack1.peek() == left){
                stack1.pop();stack1.push(right);//栈顶改为右结点标记
                root = stack.peek().right;//根节点在栈里不能弹掉，移到右结点继续处理
            }
        }
    }
    @Test
    public void test19(){
        TreeNode root = plantATree();
        postorderByStack(root);
    }
    /**
     * 树的遍历是DFS(Depth First Search)深度优先搜索的应用，一般DFS用栈去实现（递归本质上也是栈），BFS(Breadth First Search)使用队列去实现
     *
     */
    //层次遍历
    public void levelTraversal(TreeNode root){
        if (root ==  null){
            return;
        }
        LinkedList<TreeNode> list = new LinkedList<>();
        list.add(root);
        TreeNode current;
        while (!list.isEmpty()){
            //关于List的api：poll 剪枝 摘取首结点，peek读取首结点，offer插到队尾...就是add
            current = list.poll();
            System.out.print(current.val);
            if (current.left != null){
                list.add(current.left);
            }
            if (current.right != null){
                list.add(current.right);
            }
        }

    }
    @Test
    public void test179(){
        TreeNode root = plantATree();
        levelTraversal(root);
    }
    /**
     判断两棵树是否是同一棵树
     */
    public boolean testIsSameTree(TreeNode rootA, TreeNode rootB){
        // 都为空的话，显然相同,在遍历到叶子结点时也会有这种情况
        if (rootA == null && rootB == null) return true;
        // 一个为空，一个非空，显然不同
        if (rootA == null || rootB == null) return false;
        // 两个都非空，但 val 不一样也不行
        if (rootA.val!= rootB.val) return false;
        return testIsSameTree(rootA.left, rootB.left) && testIsSameTree(rootA.right, rootB.right);
    }
    /**
     * AVL（取自发明人名字） 平衡二叉树的旋转算法
     */


}
