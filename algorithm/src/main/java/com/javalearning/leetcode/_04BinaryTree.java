package com.javalearning.leetcode;

import com.javalearning.leetcode.components.ListNode;
import com.javalearning.leetcode.components.TreeNode;
import com.javalearning.leetcode.utils.TestCases;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class _04BinaryTree {
//region 二叉树总结 labuladong纲领篇
/** 二叉树总结 labuladong纲领篇
- 快速排序就是个二叉树的前序遍历，归并排序就是个二叉树的后序遍历

*/
//二叉树遍历函数
static void traverse(TreeNode root){
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
    traverse(arr, ++i);
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
/** 二叉树基础遍历
前序遍历 1234567
中序遍历 3241765
后序遍历 3427651
 */@Test void recall3WaysTreeTraverse(){
    TreeNode root = TreeNode.plantATree(TestCases.bt1);
    traverse(root);
}
/*
不使用二叉树遍历框架进行前序遍历
直接使用下述函数递归，不允许再调用其他函数
使用 分解问题 的思路：前序遍历就是 根节点+左子树遍历+右子树遍历
 */static List<Integer> preorderTraverse(TreeNode root){
     List<Integer> list = new ArrayList<>();
     if (root == null) return list;
     list.add(root.val);
     list.addAll(preorderTraverse(root.left));
     list.addAll(preorderTraverse(root.right));
     return list;
}
/** 前序位置只可以看到从父节点传递来的数据，后序位置还可以额外得到子树遍历处理的结果*/
/* 将二叉树根节点视作层1，打印每一个节点所在的层数
 */static int level = 1;
static void traverseWithLevel(TreeNode root){
     if (root == null) return;
    System.out.println(root.val + "所在层级：" + level);
    level++;
    traverseWithLevel(root.left);
    traverseWithLevel(root.right);
    level--;
}//也可以定义递归函数签名为 traverseWithLevel(TreeNode root，int level)
static void traverseWithLevel2(TreeNode root, int level){
    if (root == null) return;
    System.out.println(root.val + "所在层级：" + level);
    int nextLevel = level + 1;
    traverseWithLevel2(root.left, nextLevel);
    traverseWithLevel2(root.right, nextLevel);
}/*
打印每个节点的左右子树各有多少节点
* */static int traverseWithCount(TreeNode root){
    if (root == null) return 0;
    int leftCount = traverseWithCount(root.left);
    int rightCount = traverseWithCount(root.right);
    System.out.printf("根节点%d的左子树有%d个，右子树有%d个\n", root.val, leftCount, rightCount);
    return leftCount + rightCount + 1;
}

//endregion

//region junior
/* 二叉树解题的思维模式：
1. 通过遍历一遍二叉树得到答案：使用一个traverse哈数配合外部变量实现
2. 定义递归函数，通过子问题（子树）的答案推导出原问题的答案。分解问题的思维模式
相关思维模式可以应用在 动态规划、回溯算法、分治算法、图论算法
 */
/** LC104 二叉树最大深度
*/static int maxDepth(TreeNode root) {
    if (root == null) return 0;
    int leftDepth = maxDepth(root.left);
    int rightDepth = maxDepth(root.right);
    int maxDepth = Math.max(leftDepth, rightDepth);
    return maxDepth + 1;
}/*
前序遍历的代码是自顶向下的，后序遍历的代码自底向上，离开节点时才会执行，下述解法说明这一特性
*/static int maxDepth = 0;static int currentDepth = 0;
static int maxDepth2(TreeNode root) {
    traverse4MaxDepth(root);
    return maxDepth;
}
static void traverse4MaxDepth(TreeNode root){
    if (root == null) return;
    currentDepth++;
    if (root.left == null && root.right == null){//到达叶子节点，更新最大深度
        maxDepth = Math.max(maxDepth, currentDepth);
    }//叶子节点的判断和比较当前深度的逻辑可以放在前中后序的位置
    traverse4MaxDepth(root.left);
    traverse4MaxDepth(root.right);
    currentDepth --;
}
/** LC543 二叉树的直径
关键测试用例 TestCases.bt3 根节点不一定在最大直径上
 二叉树的「直径」长度，就是一个节点的左右子树的最大深度之和，需要比较所有节点
 时间复杂度 O(N) 空间复杂度 O(bt_height)
 * */static int maxDiameter = 0;
static int diameterOfBinaryTree(TreeNode root) {
    if (root == null) return 0;
    maxDepth3(root);
    return maxDiameter;
}
static int maxDepth3(TreeNode root){
    if (root == null) return 0;
    int leftDepth = maxDepth3(root.left);
    int rightDepth = maxDepth3(root.right);
    maxDiameter = Math.max(maxDiameter, leftDepth+rightDepth);
    return Math.max(leftDepth,rightDepth)+1;
}
/** ++LC124 二叉树最大路径和
hard
 *///maxRes 初始值不可以是0，二叉树 {-3} 会出错
static int maxRes = Integer.MIN_VALUE;
static int maxPathSum(TreeNode root) {
    if (root == null) return 0;
    //这一行可以减少递归深度，但是二叉树 {1} 无法正常计算
    //if (root.left == null && root.right == null) return root.val;
    int L = maxPathSum(root.left);
    int R = maxPathSum(root.right);
    //负值置零
    int adL = Math.max(L,0);
    int adR = Math.max(R,0);
    //最大值比较时只需考虑当前根节点的下的情况，不要看上层的比较，递归会来解决
    //直接对负值置零后的值累加，根节点+左右子树 的最大正值
    maxRes = Math.max(maxRes, root.val + adL + adR);
    //如果要递归到上一层就表示根节点参与路径，此时可以仅root（左右子节点都是负节点）、单边路径（root和最大的子节点），但不可以左中右连起来
    return root.val + Math.max(adL, adR);
}

/** LC366 收集并删除二叉树叶子节点
会员题，通过博客获得
收集叶子节点再移除掉，回到根节点，典型的后续遍历
List塞元素的index可以根据节点到叶子节点的距离来确定，所有这些在一次递归中解决
 */
static List<List<Integer>> collectRMLFNode(TreeNode root){
    tellMeDistance(root);
    return resLC366;
}
static List<List<Integer>> resLC366 = new ArrayList<>();
static int tellMeDistance(TreeNode root){
    if (root == null) return 0;
    int LDepth = tellMeDistance(root.left);
    int RDepth = tellMeDistance(root.right);
    int distance = Math.max(LDepth, RDepth);
    List<Integer> list = resLC366.size()>=(distance+1)?resLC366.get(distance):null;
    if (list == null){
        list = new ArrayList<>();
        resLC366.add(list);
    }
    list.add(root.val);
    if (distance > 0){
        root.left = null;root.right = null;
    }
    return distance + 1;
}

/**
讲解过于跳跃，先记下来 todo 上周进度 打通网络
动归/DFS/回溯算法都可以看做二叉树问题的扩展，只是它们的关注点不同：
动态规划算法属于分解问题的思路，它的关注点在整棵「子树」。
回溯算法属于遍历的思路，它的关注点在节点间的「树枝」。
DFS 算法属于遍历的思路，它的关注点在单个「节点」。
*/
    public static void main(String[] args) {
        Integer[] i1 = {1, 2, 3};
        Integer[] i2 = {-10, 9, 20, null, null, 15, 7};
        Integer[] i3 = {-1,-2, 3};
        Integer[] i4 = {1,2,3,4,5,null,null};
        TreeNode treeNode = TreeNode.plantATree(i4);
        int result = tellMeDistance(treeNode);
        System.out.println(resLC366);
    }

//endregion
}
