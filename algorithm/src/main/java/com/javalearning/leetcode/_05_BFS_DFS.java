package com.javalearning.leetcode;

import com.javalearning.leetcode.components.TreeNode;
import com.javalearning.leetcode.utils.PrintUtils;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**-=-=-=-=-=-= BFS（Breadth First Search,宽度优先搜索） =-=-=-=-=-=-*/
//region
/**
【参考资料】
 回溯算法解题套路框架 https://labuladong.github.io/algo/di-ling-zh-bfe1b/hui-su-sua-c26da/
 回溯算法秒杀排列组合子集的九种题型 https://labuladong.github.io/algo/di-ling-zh-bfe1b/hui-su-sua-56e11/


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
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(_05_BFS_DFS.class);
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
//region
/* +LC46 数组数字的全排列
可以很容易地想到多叉树，但不是真的构建一棵多叉树
人脑解决这个问题就是先确定第一位，三种可能
数字不允许重复，数字使用过就不能使用,维护一个集合添加移除数字很麻烦，但通过标记位维护就方便些
确定当前数字存在很多可能，这就可以通过递归实现，数字是否使用过可以通过递归进入和退出进行维护
*/
static void backtrack(int[] nums, List<Integer> oneOrder, boolean[] used){
    if (oneOrder.size() == nums.length){
        backtrackRes.add(new LinkedList<>(oneOrder));
        return;
    }//DFS回溯的一个典型特征：在for循环内部调递归函数，前后会分别进行写入擦除操作
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

/* LC51 N皇后问题
*/static List<List<String>> solveNQueens(int n) {
    resLC51.clear();
    int[][] flags = new int[n][n];
    backtrackLC51(flags,0);
    return resLC51;
}
static final List<List<String>> resLC51 = new ArrayList<>();
//使用 1 0 -1 -2 ...标记一个棋盘位置状态，1-已放置一个皇后；0-可放置一个皇后；-N <= -1 被N个皇后禁止；
static void backtrackLC51(int[][] flags, int currentLine){
    if (currentLine == flags.length){ //收集最终解
        convertFlagsToList(flags, resLC51);
        return;
    }
    //一开始设想的是两层for循环，再进入递归，后来发现递归本身就有一个遍历的效果，所以按行递归就能解决问题了
    //而且一行放置了一个皇后，直接处理下一行，不必再forj遍历，所以不是两层for循环
    for (int i = 0; i < flags.length; i++) {
        //不过要先看看是否能放置
        if (flags[currentLine][i] < 0) continue;
        //第i行第j列放置一个Q，则能确定很多位置无法放置
        flags[currentLine][i]=1;
        adjustBanCount(flags,currentLine,i,-1);
        //进入递归回溯
        backtrackLC51(flags, currentLine+1);
        //恢复现场
        flags[currentLine][i]=0;
        adjustBanCount(flags,currentLine,i,1);
    }
}
static void convertFlagsToList(int[][] flags,List<List<String>> res){
    List<String> line = new ArrayList<>();
    for (int i = 0; i < flags.length; i++) {
        char[] chars = new char[flags.length];
        for (int j = 0; j < flags.length; j++) {
            if (flags[i][j]==1){
                chars[j] = 'Q';
            }else {
                chars[j] = '.';
            }
        }
        line.add(new String(chars));
    }
    res.add(line);
}
static void adjustBanCount(int[][] flags, int i, int j, int delta){
    //固定x轴i横向来一波
    for (int l = 0; l < flags.length; l++) {
        if (l==j) continue;
        flags[i][l] += delta;
    }
    //纵向来一波
    for (int k = 0; k < flags.length; k++) {
        if (k==i) continue;
        flags[k][j] += delta;
    }
    //从 i,j出发 四个方向来一波
    adjustDiagonalBanCount(flags,i,j,delta,1,-1);
    adjustDiagonalBanCount(flags,i,j,delta,1,1);
    adjustDiagonalBanCount(flags,i,j,delta,-1,1);
    adjustDiagonalBanCount(flags,i,j,delta,-1,-1);

}
static void adjustDiagonalBanCount(int[][] flags, int i, int j, int delta, int delta_i, int delta_j){
    for (int x=i+delta_i,y=j+delta_j; 0<=x && x<flags.length && 0<=y && y<flags.length; x+=delta_i,y+=delta_j) {
        flags[x][y] += delta;
    }
}
static void printNQueuesRes(List<List<String>> res){
    for (List<String> list : res) {
        for (String s : list) {
            for (int i = 0; i < s.length(); i++) {
                System.out.printf("%s    ", s.charAt(i));
            }
            System.out.println();

        }
        System.out.println("-=-=-=-=-=-=-=-=-=-=-");
    }
}
/*
上述算法性能很弱，可以优化：
- 判断一个皇后能不能放置，不一定要记录状态到一个数组，可以从当前皇后出发，直接判断攻击路线上是否有其他皇后，这样根本不需要flags状态二维数组
- 攻击路线的检查可以只往上看，忽略掉 左下 正下 右下 路线的检查
*/static List<List<String>> solveNQueens2(int n) {
    List<List<String>> resLC51 = new ArrayList<>();
    List<String> res = new ArrayList<>();
    char[] chars = new char[n];
    for (int i = 0; i < n; i++) {
        chars[i] = '.';
    }
    String primary = new String(chars);
    for (int i = 0; i < n; i++) {
        res.add(primary);
    }
    backtrack2LC51(n, 0, res, resLC51);
    return resLC51;
}
static void backtrack2LC51(int n, int row, List<String> res, List<List<String>> resLC51){
    if (row >= n){
        resLC51.add(new ArrayList<>(res));
        return;
    }
    for (int column = 0; column < n; column++) {
        if (!checkSafety(res, row, column)){
            continue;
        }
        String line = replaceCharAt(res.get(row), column, 'Q');res.set(row, line);
        backtrack2LC51(n, row+1, res, resLC51);
        line = replaceCharAt(res.get(row), column, '.');res.set(row, line);
    }
}
static boolean checkSafety(List<String> res, int x, int y){
    //查看上方是否有皇后
    for (int i = x; i >= 0; i--) {
        char info = res.get(i).charAt(y);
        if (info == 'Q') return false;
    }
    //查看左上方是否有皇后
    for (int i=x,j=y; i>=0 && j>=0; i--,j--) {
        char info = res.get(i).charAt(j);
        if (info == 'Q') return false;
    }
    //查看右上方是否有皇后
    for (int i=x,j=y; i>=0 && j<res.size(); i--,j++) {
        char info = res.get(i).charAt(j);
        if (info == 'Q') return false;
    }
    return true;
}
static String replaceCharAt(String input, int index, char replacement){
    char[] chars = input.toCharArray();
    chars[index] = replacement;
    return new String(chars);
}
/* LC52
*/
static Integer solveNQueens3(int n) {
    AtomicInteger ai = new AtomicInteger(0);
    List<String> res = new ArrayList<>();
    char[] chars = new char[n];
    for (int i = 0; i < n; i++) {
        chars[i] = '.';
    }
    String primary = new String(chars);
    for (int i = 0; i < n; i++) {
        res.add(primary);
    }
    backtrack2LC51(n, 0, res, ai);
    return ai.get();
}
static void backtrack2LC51(int n, int row, List<String> res, AtomicInteger count){
    if (row >= n){
        count.incrementAndGet();
        return;
    }
    for (int column = 0; column < n; column++) {
        if (!checkSafety(res, row, column)){
            continue;
        }
        String line = replaceCharAt(res.get(row), column, 'Q');res.set(row, line);
        backtrack2LC51(n, row+1, res, count);
        line = replaceCharAt(res.get(row), column, '.');res.set(row, line);
    }
}
/**
 BFS 广度优先，需要借助集合收集每层的节点，这样可以避免不必要的深度递归，及早返回
 DFS 深度优先，需要明确走到二叉树叶子节点才能结束，就像上面的求解，就需要借助递归堆栈向深处完成一个符合要求的解的遍历
 还有一类算法叫 回溯算法，与DFS很类似，但有细微差别
  回溯算法是在遍历树枝，DFS算法是在遍历节点
全排列 和 N皇后问题 是两个经典的回溯算法问题，本质上都是一棵多叉树的遍历
 回溯算法框架
 resutl = []
 def backtrack(路径，选择列表):
    if 满足结束条件：
        result.add(路径)
        return
    for 选择 in 选择列表：
        （进入此节点）做选择
        backtrack(路径，选择列表)
        （离开此节点）撤销选择
 */
//endregion

//region 回溯算法 阶段二 排列组合、子集问题
/**
通常都是从序列 nums 中以给定规则取若干元素，规则通常有下述几种
 - 元素无重不可复选 如2，3，6，7中和为7的组合只有7；LC46 全排列问题
 - 元素可重不可复选 如2，5，2，1，2中和为7的组合有[2,2,2,1][5,2]
 - 元素无重可复选   如2，3，6，7 和为7的组合有 [2,2,3] [7]
 解决这类问题的关键是：先把多叉树画出来，想好怎么通过递归回溯遍历树枝/树叶出结果
 最后你会发现，testcase给出的答案的子集顺序可以拿来验证自己花的树是否正确！
 */
/* LC78 subsets 给定元素唯一，返回所有可能的子集，子集中元素不讲究顺序
子集之间不可重复，[1,2,3]中选2个元素，人的思路是：
    2
1<
    3
2 < 3 -- 这不是排列组合，不能重复，所以一个起始元素只能向后选，不可以向前看；
3 - X -- 最后一个元素无法向后凑齐两个，所以不算了
通过保证元素之间的相对顺序不变来防止出现重复的子集
这种只能向后取元素的思路，跟排列组合有一定不同
*/static List<List<Integer>> subsets(int[] nums) {
    LinkedList<Integer> currentItems = new LinkedList<>();
    List<List<Integer>> res = new ArrayList<>();
    for (int itemCount = 0; itemCount <= nums.length; itemCount++) {
        backtrackLC78(nums, currentItems, res, itemCount, 0);
    }
    return res;
}
/*
子集问题涉及一个典型的 C(n,m) 问题 = n!/m!  这样上面的子集问题就是寻找m从 0到n 的子集结果
int[] nums = {1,2,3,4};
System.out.println(soluteCnm(nums, 3));
*/
static List<List<Integer>> soluteCnm(int[] nums, int m){
    LinkedList<Integer> currentItems = new LinkedList<>();
    List<List<Integer>> res = new ArrayList<>();
    backtrackLC78(nums, currentItems, res, m, 0);
    return res;
}
static void backtrackLC78(int[] nums, LinkedList<Integer> currentItems, List<List<Integer>> res, int itemCount, int startIndex){
    if (itemCount == 0) {
        res.add(new ArrayList<>(currentItems));
        return;
    }
//    if (startIndex >= nums.length){ for循环有判断效果
//        return;
//    }
    for (int i = startIndex; i < nums.length; i++) {
        currentItems.offer(nums[i]);
        backtrackLC78(nums, currentItems,res,itemCount-1, i+1);
        currentItems.pollLast();
    }
}
/* LC78 子集问题的简化思路
可以抽象成对一棵多叉树的遍历，求解的过程就是收集这棵多叉树的所有节点
            []
     1      2     3
   12 13  23
123
*/
static LinkedList<Integer> trackLC78 = new LinkedList<>();
static List<List<Integer>> subsets2(int[] nums) {
    trackLC78.clear();
    List<List<Integer>> res = new ArrayList<>();
    backtrack2LC78(nums, 0, trackLC78, res);
    return res;
}
static void backtrack2LC78(int[] nums, int startIndex, LinkedList<Integer> currentSet, List<List<Integer>> res){
    res.add(new LinkedList<>(currentSet));
    //if (startIndex == nums.length){ 不需要这个if，for循环里面会判断后退出的
    //    return;
    //}
    for (int i = startIndex; i < nums.length; i++) {
        currentSet.addLast(nums[i]);
        //注意这里是i+1，不是startIndex+1
        backtrack2LC78(nums, i+1, currentSet, res);
        currentSet.removeLast();
    }
}
/* LC77 组合 1-n 的自然数中选择 k 个，返回所有可能的子集/组合
借助前面写的求解 C(n,m) 的方法
简化思路 - 还是上面 LC78 那棵多叉树，只要第 k 层的节点集合
*/
static List<List<Integer>> combine(int n, int k) {
    int[] nums = new int[n];
    for (int i = 0; i < n; i++) {
        nums[i] = i+1;
    }
    return soluteCnm(nums, k);
}
static List<List<Integer>> combine2(int n, int k) {
    LinkedList<Integer> currentSet = new LinkedList<>();
    List<List<Integer>> res = new ArrayList<>();
    backtrack2LC77(n+1,k,1,currentSet,res);
    return res;
}
static void backtrack2LC77(int n, int k, int startIndex, LinkedList<Integer> currentSet, List<List<Integer>> res){
    if (currentSet.size() == k){
        res.add(new ArrayList<Integer>(currentSet));
        return;
    }
    //剪枝优化性能 i<n 改成 i <= (n+currentSet.size()-k)
    for (int i = startIndex; i < n; i++) {
        currentSet.addLast(i);
        backtrack2LC77(n, k, i+1, currentSet, res);
        currentSet.removeLast();
    }
}
//元素可重不可复选，nums中存在重复元素
/* LC90 子集II
nums=[1,2,2] [[],[1],[1,2],[1,2,2],[2],[2,2]]
据说计算机的很多问题可以抽象成一棵树
   [] -- 向后，用过的元素不能再用，累积一路遇到的节点到集合里作为子集
  1  2
 2    2
2
[1,1,3,4] 有3个distinct元素，所以是三叉树，每一层要伸出剩下的distinct元素数量的树枝
        []    --[1][1,1][1,1,3][1,1,3,4][1,1,4][1,3][1,3,4][1,4][3][3,4][4]
    1        3    4
  1   3  4   4
 3  4 4
4
[4,4,1,4]
       []
     4     1
   4   1   4
 1  4  4
4
[[], [1], [1, 1], [1, 1, 1], [1, 1, 1, 3], [1, 1, 1, 3, 4], [1, 1, 1, 4], [1, 1, 3], [1, 1, 3, 4], [1, 1, 4], [1, 3], [1, 3, 4], [1, 4], [3], [3, 4], [4]]
System.out.println(subsetsWithDup(new int[]{1,1,1,3,4}));
[[], [1], [1, 4], [1, 4, 4], [1, 4, 4, 4], [1, 4, 4, 4, 4], [4], [4, 4], [4, 4, 4], [4, 4, 4, 4]]
System.out.println(subsetsWithDup(new int[]{4,4,4,1,4}));
*/
static List<List<Integer>> subsetsWithDup(int[] nums) {
    LinkedList<Integer> curr = new LinkedList<>();
    List<List<Integer>> res = new ArrayList<>();
    Arrays.sort(nums);
//    backtrackLC90(nums, 0, curr, res,new HashSet<>());
    backtrack2LC90(nums, 0, curr, res);
    return res;
}
//Set解法无法在不排序的情况下处理 4,4,4,1,4 的子集问题
//static void backtrackLC90(int[] nums, int startIndex, LinkedList<Integer> currentSet, List<List<Integer>> res, Set<Integer> distinctNums){
//    res.add(new ArrayList<>(currentSet));
//    //int preSameLevelItem = -99; [4,4,1,4] 可以推翻这种去重方案,要保证在同一层元素不重复，所以进入递归要清除set，返回递归又要恢复，干脆new一个进去吧
//    for (int i = startIndex; i < nums.length; i++) {
//        if (distinctNums.contains(nums[i])) continue;
//        distinctNums.add(nums[i]);
//        currentSet.addLast(nums[i]);//出现无端元素重复了就请注意这里的i+1是不是写成startIndex+1了
//        backtrackLC90(nums, i+1, currentSet, res, new HashSet<>());
//        currentSet.removeLast();
//    }
//}
static void backtrack2LC90(int[] nums, int startIndex, LinkedList<Integer> currentSet, List<List<Integer>> res){
    res.add(new ArrayList<>(currentSet));
    int preSameLevelItem = -99; //[4,4,1,4] 可以推翻这种去重方案,但既然1混在中间干扰判断，那就先排个序吧
    for (int i = startIndex; i < nums.length; i++) {
        //preSameLevelItem==... 改成 i > start && nums[i] == nums[i - 1] 比较，可以节约内存
        if (preSameLevelItem == nums[i]) continue;
        preSameLevelItem = nums[i];
        currentSet.addLast(nums[i]);//出现无端元素重复了就请注意这里的i+1是不是写成startIndex+1了
        backtrack2LC90(nums, i+1, currentSet, res);
        currentSet.removeLast();
    }
}
//endregion
/* LC40 Combination Sum II
[10,1,2,7,6,1,5] = 8: [[1,1,6],[1,2,5],[1,7],[2,6]]
[1,1,2,5,6,7,10]
    1                 2  5  6  7 10
 1     2    5  6 [7]
25[6] [5]  6   7
System.out.println(combinationSum2(new int[]{10,1,2,7,6,1,5},8));
System.out.println(combinationSum2(new int[]{2,5,2,1,2},5));
*/
static List<List<Integer>> combinationSum2(int[] candidates, int target) {
    Arrays.sort(candidates);
    LinkedList<Integer> curr = new LinkedList<>();
    List<List<Integer>> res = new ArrayList<>();
    backtrackLC40(candidates, target, 0, 0, curr, res);
    return res;
}
static void backtrackLC40(int[] nums, int target, int startIndex, int currentSum, LinkedList<Integer> currentItems, List<List<Integer>> res){
    if (currentSum == target){
        res.add(new ArrayList<>(currentItems));
        return;
    }
    for (int i = startIndex; i < nums.length; i++) {
        //同一层的不能重复
        if (i>startIndex && nums[i] == nums[i-1]) continue;
        //走到和超过target的树枝上，可以剪枝了，直接return，由于是在递归里，不会全退出去
        if (currentSum + nums[i] > target) return;
        //纳入这个节点
        currentItems.addLast(nums[i]);
        currentSum+=nums[i];
        backtrackLC40(nums, target, i+1,currentSum, currentItems, res);
        //恢复现场，回到上一层，removeLast返回的就是nums[i] 可以拿来用
        currentItems.removeLast();
        currentSum-=nums[i];
    }
}
/* LC47 全排列II 元素有重复的
[1,1,2] - [[1,1,2], [1,2,1], [2,1,1]]
[1,1,2,3]
横向画一颗多叉树比较方便，也接近人的思路
1 - 1 - 2 - 3
      - 3 - 2
  - 2 - 1 - 3
      - 3 - 1
  - 3 - 1 - 2
      - 2 - 1
2 - 1 - 1 - 3
      - 3 - 1
  - 3 - 1 - 1
3 - 1 - 1 - 2
      - 2 - 1
  - 2 - 1 - 1
多叉树规律：每层总是在剩下的元素中按不重复（需要排序）原则开支，直到用完所有元素，收集树叶
注意前面的元素也要参与，而不是仅从剩下的元素中取
[1,2,3] - [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
*/
static List<List<Integer>> permuteUnique(int[] nums) {
    List<List<Integer>> res = new ArrayList<>();
    LinkedList<Integer> current = new LinkedList<>();
    boolean[] used = new boolean[nums.length];
    Arrays.sort(nums);
    backtrackLC47(nums,used,res,current);
    return res;
}
static void backtrackLC47(int[] nums, boolean[] used, List<List<Integer>> res, LinkedList<Integer> current){
    if (current.size() == nums.length){
        res.add(new ArrayList<>(current));
    }
    for (int i = 0; i < nums.length; i++) {
        if (used[i]) continue;
        //重复元素找全排列的麻烦在于：既要保证每层不重复（nums[i]!=nums[i-1]），又要从剩下的没用过的元素中选择（!used[i-1] 第二个相同元素要在下一轮使用上）
        //这里每次从0开始有点性能弱，但要使用链表存储剩余可用元素，又要保证元素有序性，比较麻烦
        //可重复元素的全排列的剪枝方法
        // 教程中提到 !used[i-1]（前面的要用过才往下走）改成 used[i-1]（前面的没用过才能走当前元素的开支）也能得到正确结果，但效率会下降很多，是因为剪枝效率降低了，有些分支走了一部分放弃了
        //去重的方法也可以采用前面写过的 记录previousItemValue的方法避免同层重复
        if (i>0 && nums[i]==nums[i-1] && !used[i-1]) continue;
        current.addLast(nums[i]);
        used[i]=true;
        backtrackLC47(nums, used, res, current);
        current.removeLast();
        used[i]=false;
    }
}
/* LC39 Combination Sum 唯一元素求和得到目标值，元素可以无限重复使用
[2,3,6,7] - 7 下面是一颗完整的回溯树，横向画法是人的思路，转置这棵树就是代码遍历出的一颗多叉树
2 - 2 - 2 - 2
        3+
        6
    3 - 3
    6
    7
3 - 3 - 3
    6
6 - 6
7+
子集不可重复，所以元素向后选，不可向前选（223与232）
收集叶子节点
*/
static List<List<Integer>> combinationSum(int[] candidates, int target) {
    List<List<Integer>> res = new ArrayList<>();
    LinkedList<Integer> current = new LinkedList<>();
    int[] count = new int[candidates.length];
    //这里限制了元素重复使用的最大次数 3 ，原题没有这个要求
    backtrackLC39(candidates,target,0,0,res,current,count, 3);
    return res;
}
static void backtrackLC39(int[] nums, int target,int currentSum, int startIndex, List<List<Integer>> res, LinkedList<Integer> current, int[] count, int maxCount){
    if (currentSum == target){
        res.add(new ArrayList<>(current));
        return;
    }
    for (int i = startIndex; i < nums.length && currentSum < target; i++) {
        if (count[i]>=3) continue;
        count[i] += 1;
        currentSum += nums[i];
        current.addLast(nums[i]);
        backtrackLC39(nums,target,currentSum,i,res,current,count,maxCount);
        current.removeLast();
        currentSum -= nums[i];
        count[i] -= 1;
    }
}
/* 第9中 排列组合问题 由于过于简单，LC无题
唯一元素集合可重复选择得到全排列
[1,2,3] 的可重复全排列共 3^3=27种
实现上无需剪枝，不需要判重，不需要向后选择，代码过于简单，无考察价值，但适合回溯框架入门
*/
    public static void main(String[] args) {
        System.out.println(combinationSum(new int[]{5,2,3}, 8));
    }
}
