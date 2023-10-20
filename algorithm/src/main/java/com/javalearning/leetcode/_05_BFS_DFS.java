package com.javalearning.leetcode;

import com.javalearning.leetcode.components.TreeNode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**-=-=-=-=-=-= BFS（Breadth First Search,宽度优先搜索） =-=-=-=-=-=-*/
//region
/**
BFS的思路：将问题抽象成图，从一个点开始，向四周扩散，通常BFS算法会使用队列保存一个节点周围的所有节点
BFS相对DFS的最主要区别：BFS找到的路径是最短的，但代价就是SC比DFS大很多

BFS问题的本质就是在一幅“图”中找到从起点start到终点target的最近距离
实际的问题并不会明显看出来，举例如下：
走迷宫，有的格子是围墙不能走，求起点到终点的最短距离
最小编辑距离：两个单词，通过某些替换把一个变成另一个，每次只能替换一个字符，最少要替换几次
连连看游戏：两个方块消除的条件不仅仅是图案相同，还得保证两个方块之间最短连线不能多于两个拐点
BFS算法解题框架
//计算从start到target的最近距离
int BFS(Node start, Node target){
    Queue<Node> q;
    Set<Node> visited; //避免走回头路
    q.offer(start);
    visited.add(start);
    while(q not empty){
        int sz = q.size();
        //将当前队列中的所有节点向四周扩散
        for(int i=0;i<sz;i++){
            Node cur = q.poll();
            //判断是否到达终点
            if(cur is target) return step;
            for(Node x : cur.adj()){ //加入相邻节点
                if(x not in visited){
                    q.offer(x);
                    visited.add(x);
    }  }    }   }

 此算法框架与二叉树层序遍历非常类似，多一个防止走回头路的visited集合
 二叉树不存在子节点回到父节点的指针，不需要visited集合避免走回头路
*/
public class _05_BFS_DFS {
/** LC111 二叉树的最小深度
这一问题适合采用广度优先搜索算法BFS，深度优先搜索DFS也可以，但要遍历完毕所有路径再比较大小才能出结果
 BFS层序遍历，齐头并进式的搜索可以节省不必要深度搜索
 DFS使用堆栈，空间复杂度更低，与树的高度有关，O(logN),而BFS则与底部的叶子节点数量有关，SC=O(N/2)
*/static int minDepth(TreeNode root) {
    if (root == null) return 0;
    int depth = 1;
    LinkedList<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    while (!queue.isEmpty()){
        int size = queue.size();
        for (int i = 0; i < size; i++) {
            TreeNode current = queue.poll();
            if (current.left == null && current.right == null){
                return depth;
            }
            if (current.left != null){
                queue.offer(current.left);
            }
            if (current.right != null){
                queue.offer(current.right);
            }
        }//这一层遍历完增加深度计数depth
        depth++;
    }
    return depth;
}
/** ++LC752 打开转盘锁
注意：是8叉树，不是level=4的10叉树
 String[] deadends1 = {"0201","0101","0102","1212","2002"};
 String target1 = "0202";
 String[] deadends2 = {"8888"};
 String target2 = "0009";
 String[] deadends3 = {"8887","8889","8878","8898","8788","8988","7888","9888"};
 String target3 = "8888";
 System.out.println(openLock(deadends3, target3));
*/static int openLock(String[] deadends, String target) {
    Set<String> deadSet = new HashSet<>(Arrays.asList(deadends));
    Set<String> visitedLocks = new HashSet<>();//deadends可以直接初始化到visitedLocks中
    Queue<String> queue = new LinkedList<>();
    queue.offer("0000");
    int depth = 0;
    while (!queue.isEmpty()){
        int size = queue.size();
        for (int i = 0; i < size; i++) {
            String lock = queue.poll();
            //1.不同锁可能拨到同一个锁上，要去重，否则死循环
            if (visitedLocks.contains(lock)){
                continue;
            }
            //2.防止 0000 就是 deadend
            if (deadSet.contains(lock)){
                continue;
            }//3.这样看来干脆在roll之后操作visited，这里只看deadSet，还能简化代码
            visitedLocks.add(lock);
            if (target.equals(lock)){
                return depth;
            }
            for (int j = 0; j < 4; j++) {
                String down = roll(lock,j,-1);
                if (!visitedLocks.contains(down) && !deadSet.contains(down)){
                    queue.offer(down);//是不是也可以再这里判断==target？
                }
                String up = roll(lock,j,1);
                if (!visitedLocks.contains(up) && !deadSet.contains(up)){
                    queue.offer(up);
                }
            }
        }
        depth++;
    }
    return -1;
}
static String roll(String lock, int index, int updown){
    char[] chars = lock.toCharArray();
    if (updown > 0 && chars[index] == '9'){
        chars[index] = '0';
    }else if (updown < 0 && chars[index] == '0'){
        chars[index] = '9';
    }else {
        //chars[index] = (char) (chars[index] + updown);
        //chars[index]=++chars[index];
        chars[index] += updown;
    }
    return new String(chars);
}
/*
使用双向BFS优化上述算法
传统的BFS框架从起点开始向四周扩散，遇到终点时停止。双向BFS从起点和终点同时扩散，当两边有交集时停止
双向BFS应用的前提是 必须知道终点在哪里。对于二叉树最小深度的问题不能应用
 在调试前面的程序时发现，从root逐渐扩散出来的集合先变大后变小，中途会遇到target元素结束
 如果分别从root和target按相同方式扩散，当两者出现交集就表明可达。这样从先前的队列变成了两个集合
*/static int openlock(String[] deadends, String target){
    Set<String> deads = new HashSet<>(Arrays.asList(deadends));
    Set<String> q1 = new HashSet<>();q1.add("0000");
    Set<String> q2 = new HashSet<>();q2.add(target);
    Set<String> visited = new HashSet<>();
    //说是从两边扩散，其实并不是同时扩散，只是先后依次扩散，所以这个step正常++
    //这也说明这种双向BFS并不能真正地降低TC
    int step = 0;
    while (!q1.isEmpty() && !q2.isEmpty()){ //这里可以继续优化，每次捡更小的集合扩散，可能节省点SC
        Set<String> temp = new HashSet<>();
        //对q1中的节点进行扩散
        for (String cur : q1) {
            if (deads.contains(cur)) continue;
            //判断两个集合 q1 q2是否有交集
            if (q2.contains(cur)) return step;
            visited.add(cur);

            for (int i = 0; i < 4; i++) {
                String down = roll(cur, i, -1);
                if (!visited.contains(down)) temp.add(down);
                String up = roll(cur, i, 1);
                if (!visited.contains(up)) temp.add(up);
            }
        }
        step++;
        //交换 q1 q2，root扩展完，再从target扩展试试
        q1 = q2; q2 = temp;
    }
    return -1;
}

//endregion

/**-=-=-=-=--=-= DFS算法，深度优先搜索/回溯算法 =--=-=-=-=-=-=-=*/
/**
todo 手把手刷二叉树（纲领篇） https://labuladong.github.io/algo/di-ling-zh-bfe1b/dong-ge-da-334dd/
todo 图论算法基础 https://labuladong.github.io/algo/di-yi-zhan-da78c/shou-ba-sh-03a72/tu-lun-ji--d55b2/
todo 回溯算法秒杀排列组合子集的九种题型 https://labuladong.github.io/algo/di-ling-zh-bfe1b/hui-su-sua-56e11/
todo 多叉树DFS遍历框架的前序位置和后序位置应该在 for 循环外面，但回溯算法却是在for循环里面，原因？ https://labuladong.github.io/algo/di-yi-zhan-da78c/shou-ba-sh-03a72/tu-lun-ji--d55b2/
区别：DFS算法遍历节点，回溯算法遍历树枝；
回溯算法框架
 resutl = []
 def backtrack(路径，选择列表):
    if 满足结束条件：
        result.add(路径)
        return
    for 选择 in 选择列表：
        做选择
        backtrack(路径，选择列表)
        撤销选择
*/
//region
/* +LC46 数组数字的全排列
可以很容易地想到多叉树，但不是真的构建一棵多叉树
人脑解决这个问题就是先确定第一位，三种可能
数字不允许重复，数字使用过就不能使用,维护一个集合添加移除数字很麻烦，但通过标记位维护就方便些
确定当前数字存在很多可能，这就可以通过递归实现，数字是否使用过可以通过递归进入和退出进行维护
*/static void backtrack(int[] nums, List<Integer> oneOrder, boolean[] used){
    if (oneOrder.size() == nums.length){
        backtrackRes.add(new LinkedList<>(oneOrder));
        return;
    }
    for (int i = 0; i < nums.length; i++) {
        if (used[i]) continue;
        oneOrder.add(nums[i]);
        used[i] = true;
        backtrack(nums, oneOrder, used);
        oneOrder.remove(oneOrder.size()-1);
        used[i] = false;
    }
}
static List<List<Integer>> backtrackRes = new LinkedList<>();
static List<List<Integer>> permute(int[] nums) {
    List<Integer> oneOrder = new LinkedList<>();
    boolean[] used = new boolean[nums.length];
    backtrack(nums, oneOrder, used);
    return backtrackRes;
}

/* LC51

*/

/* LC52

*/
//endregion

    public static void main(String[] args) {
        Integer[] t1 = {2,
                null,3,
                null,null,null,4,
                null,null,null,null,null,null,null,5,
                null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,6};
        Integer[] t2 = {3,9,20,null,null,15,7};

        int[] lc46_1 = {1,2,3};
        int[] lc46_2 = {0,1};
        int[] lc46_3 = {};
        System.out.println(permute(lc46_3));
    }
}
