package com.javalearning.leetcode.labuladong;

import com.javalearning.element.TreeNode;
import com.javalearning.utils.PrintUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class _02DynamicProgramme {
    static final Logger log = LoggerFactory.getLogger(_02DynamicProgramme.class);

/*-=-=-=-=-=-=-=-=- 系列背包问题 -=-=-=-=-*/
    /**
     # 0-1背包问题
     一个可装载重量为W的背包和N个物品，每个物品有重量和价值两个属性。
     其中第i个物品的重量 wt[i]，价值为val[i]，现在让你用这个背包装物品，最多能装的价值是多少？
     0-1的含义就是物品不可分割，要么放进背包，要么不放
     */
    /*
    ## 分析
    一维动态规划就是：第i个物品的可放入的总价值，在没有重量限制的情况下就是前一个i-1判断结果加上当前
    二维动态规划就是：第i个物品要放，看第i-1个物品在总容量为减去当前物品后余量的情况下的最大价值，加上当前价值。
                    第i个物品不放，取第i-1个物品的最大价值
                    比较上述两种情况，作为当前最大价值

     */
    static class ZeroOnePackageProblem{
        /*
        第i个物品放如就是总价值增加第i个物品的价值，不放就是总价值保持不变
        那如何确定这第i个物品放入后不会超出容量呢？答案是增加「第i个物品」之外的一个容量维度
        取总价值的最大值，这样构成了一个二维数组

        DP分析
        「状态」有两个：背包容量、可选择物品，所以dp数组是一个二维数组。
        「选择」有两个：装进背包和不装
            当前物品装入的价值是 这个物品的价值+剩余容量是背包最大容量时的最大价值
            当前物品不装的价值就是上一个物品同等最大容量的价值
            当前物品的最大价值就是上面两个的max比较

            dp[i][j]定义：对当前第i个物品，背包总容量为j，可以装入的最大价值。
         「base case」：dp[0][...] = dp[...][0] = 0
         「STF」
         将第i个物品装入背包：dp[i][j] = dp[i-1][j-wt[i-1]] + val[i-1]
         第i个物品不放入背包：dp[i][j] = dp[i-1][j]
         dp[i][j] = max(..上面两个..)
         这样先写出dpTable
           0 1 2 3 ←第i个物品
         0 0 0 0 0
         1 0 0 2 2 二维数组表示在试验到第i个物品，总容量是j的情况下的最大价值
         2 0 4 4 4
         3 0 4 6 6
         4 0 4 6 6
         ↑背包总容量
         穷举不是穷举2^N种放置物品可能性，而是将剩余重量也作为状态进行规划
        * */
        static int packageLoadDPSolution(int W, int[] wt, int[] val){
            int[][] dp = new int[wt.length][W+1];
            for (int i = 1; i < wt.length; i++) {
                for (int j = 1; j <= W; j++) {
                    int preVolume = j - wt[i];
                    if (preVolume < 0){//不能放入此物品
                        dp[i][j] = dp[i-1][j];
                    }else {//可以放入此物品
                        dp[i][j] = Math.max(dp[i-1][preVolume]+val[i], dp[i-1][j]);
                    }
                    //dp[i][j] = Math.max(dp[i-1][preVolume<0?0:preVolume] + val[i], dp[i-1][j]);
                }
            }
            return dp[wt.length-1][W];
        }
        /*
        其他思路
        2^N种情况的穷举：填充数组，取出最大值
        * */
        static int maxWgtPackageLoad(int[] wt, int[] val){
            int size = 2 ^ wt.length;
            int[] valsums = new int[size];
            /*  以3个物品为例，要进行8轮计算，复杂度都是2^N,使用二进制位标记是否放入包中，难以计算重量
                0 000 000-0
              0
            0   1 001 100-1
                0 010 010-2
              1
                1 011 110-3
                0 100 001-4:
              0
                1 101 101-5
            1
                0 110 011-6
              1
                1 111 111-7
            * */
            valsums[0]=0;
            for (int i = 1; i < valsums.length; i++) {
                //过于复杂
            }
            return 0;
        }

        public static void main(String[] args) {
            int N = 3,W = 4;
            int[] wt = {2,1,3};
            int[] val = {4,2,3};
            //dp[i] 第i个物品
            System.out.println(packageLoadDPSolution(W,wt,val));
        }
    }

    /**
     # 416 分割等和子集   子集背包问题
     - 二维动态规划如何压缩成一维的

     一个只包含正整数的非空数组，是否可分割成 两 个子集，使得两个子集的元素和相等
     限制：每个数组的元素个数 <= 100; 数组大小 <= 200
     测试用例：[1 5 11 5] true; [1 2 3 5]
     */
    static class CanPartitionProblemAdvanced{
        /*
        ERROR:排好序一分为二，左右的和是否相等
        计算总和获得half，按背包思想组合出half
        * */
        static boolean canParitition(int[] nums){
            int sum = 0;
            for (int i = 0; i < nums.length; i++) {
                sum += nums[i];
            }
            if (sum%2==1) return false;
            int half = sum/2;
            //下面就变成背包问题了
            return canSumToByPackageProblem(half,nums);
        }
        /*
        博客解法
        对于dp数组 boolean dp[i][j]: 对于前i个物品，当前背包的容量为j，若能恰好装满，则dp[i][j]=true;
        x表示物品index，y表示背包容量
        * */
        static boolean canSumToByPackageProblem(int half, int[] nums){
            boolean[][] dp = new boolean[nums.length+1][half+1];
            //背包容量为0能否组成，由于要用来判断当前容量-物品质量是否恰好为0，所以被置为true。dp[0][...]采用默认的false，dp[...][0]置为true
            for (int i = 0; i <= nums.length; i++) {
                dp[i][0] = true;
            }
            //外层对x-物品index遍历还是对容量进行遍历都是可以的(AB行可以对调顺序)
            for (int i = 1; i <= nums.length; i++) { //A
            for (int j = 1; j <= half; j++) { //B
                    if (j - nums[i-1] < 0){
                        //放不下就不放，看前i-1件物品能否组成此背包容量（背包容量不变）
                        dp[i][j] = dp[i-1][j];
                    }else {
                        /*
                        背包容量大于当前物品质量，但不确定背包是否已经满了
                        所以要取前i-1个物品能否组成背包容量（这是防止物品重复放入的关键），再看放入当前物品，前i-1个物品能否组成剩余背包容量
                        * */
                        dp[i][j] = dp[i-1][j] || dp[i-1][j-nums[i-1]];
                        //dp[i-1][j]是防止物品重复放入的关键
                    }
                }
            }
            //打印出dp二维数组，横向是背包容量，纵向是物品index
            PrintUtils.printBoolMatrix(dp,6);
            return dp[nums.length][half];
        }
        /*
        可能dp矩阵声明的形式是转置的，或者容量、物品的遍历顺序也是不一样的，这些都不影响dp矩阵的计算和最终结果
        下面的解法与上面仅仅是dp矩阵是转置的
        */
        static boolean canSumToByPackageProblem2(int half, int[] nums){
            boolean[][] dp = new boolean[half+1][nums.length+1];
            //dp[0][...]采用默认的false，dp[...][0]置为true
            for (int i = 0; i <= nums.length; i++) {
                dp[0][i] = true;
            }
            for (int i = 1; i <= half; i++) {
                for (int j = 1; j <= nums.length; j++) {
                    if (i < nums[j-1]){//第j个物品无法放入,就取同样的容量，前j-1个物品能否组成容量
                        dp[i][j] = dp[i][j-1];
                    }else{
                        dp[i][j] = dp[i][j-1] || dp[i-nums[j-1]][j-1];
                    }
                }
            }
            PrintUtils.printBoolMatrix(dp,6);
            return dp[half][nums.length];
        }
        //自己的解法，无法解决nums被重用的问题
        static boolean canSumTo(int half, int[] nums){
            boolean[] dp = new boolean[half+1];
            dp[0] = true;
            for (int i = 1; i <= half; i++) {
                for (int j = 0; j < nums.length; j++) {
                    if (i == nums[j]){
                        dp[i] = true;
                        nums[j]=0;
                        break;
                    }
                    if (i-nums[j] < 0){
                        dp[i] = false;
                    }else {
                        dp[i] = dp[i-nums[j]];
                        nums[j]=0;
                        break;
                    }
                }
            }
            return dp[half];
        }

        //对dp数组进行降维，压缩状态，减少SC
        static boolean canSumTo3(int half, int[] nums){
            boolean[] dp = new boolean[half+1];
            dp[0] = true;
            //此时对于half的遍历必须放在nums的遍历内部
            //如果half的遍历放在外层，观察{1,5,11,5}在i=2,j=0的计算，显然数字1重复使用了 在第一个5进行half遍历时，10被判断为false，但5可以判断为true，第二个5进入时，10才会判断为true
            for (int j = 0; j < nums.length; j++) {
                //这里要倒过来遍历，而且还要放在物品遍历的里面，如果放在外面，在{1,5,11,5}的情况下，sum=10时由于sum=5还未处理到，会错误地计算为false
                //而仅仅放在内层，half正序遍历时，对于一个数字5，sum=5时判断可以组成，sum=10时因为要取sum=5的状态，计算出true，相当于把这个数字用了两次
                for (int i = half; i > 0; i--) {
                    if (i-nums[j]>=0){
                        //dp[i]一旦为true了,不管在来几个nums[j],就不修改了
                        //在{1,5,11,5}情况下，half=11在11时判断为true，在下一个5时要使用dp[i]进行或计算，防止被改掉
                        dp[i]=dp[i]||dp[i-nums[j]];
                    }
                    //dp[i] =i>0 && i>=nums[j] && dp[i-nums[j]];
                }
            }
            return dp[half];
            /**
             half正序遍历会导致物品重复放入
             half遍历放在外层会导致在倒序遍历时小容量的情况没有先判断
             所以half的遍历要放在物品遍历内层，并且要倒序遍历，否则算法出错

             上面方法是经典的判断一组整数能否在不重复使用的情况下凑成某个和的判断  LeCo494
             如果数字允许重复使用，就变成了硬币问题
             */
        }

        public static void main(String[] args) {
            int[] ns1 = {1,5,11,5};
            int[] ns2 = {1,2,3,5};
            int[] ns3 = {1,2,3,4,5,6,7};
            int[] ns4 = {1,5,11,3};
            Assert.assertTrue(canParitition(ns1));
            Assert.assertTrue(!canParitition(ns2));
            Assert.assertTrue(canParitition(ns3));
            Assert.assertTrue(!canParitition(ns4));
        }
    }

    /**
     518 零钱问题 2
     另一种典型背包问题的变体

     给定不同面额的硬币和一个总金额，可以重复使用硬币面额（每种面额的硬币有无数个）计算凑成某个总金额的组合数

     完全背包问题：容量为amount的背包装入一系列重量为coins的物品，每个物品数量不限（完全名头的由来），多少种方法可以将背包装满
     */
    static class ChangeProblem2{
        /*
         状态：可选择的物品、背包容量
         若只使用前i个物品，恰好装满背包容量j有dp[i][j]种方法
         base case: dp[0][...]=0; dp[...][0]=1;(表示恰好装进去，通常在缩写子问题时j=0表示上一步恰好用硬币凑成了/恰好将背包装满)
         结果：dp[coins.length][amount]
         */
        static int change(int amount, int[] coins) {
            int[][] dp = new int[amount+1][coins.length+1];
            //初始情况
            // dp[...][0]=0;(一种硬币都不使用，无法凑出任意正值) 1.这种初始情况无需显式初始化，2.由于这种情形要考虑，所以数组声明和遍历时都要额外+1
            // dp[0][...]=1;(使用所有的硬币凑出0，表明使用当前硬币恰好凑出了amount)
            for (int i = 0; i <= coins.length; i++) {
                dp[0][i] = 1;
            }
            //谁放在外层遍历不影响结果
            for (int i = 1; i <= amount; i++) {
                for (int j = 1; j <= coins.length; j++) {
                    if (i>=coins[j-1]){
                        dp[i][j] =
                                dp[i-coins[j-1]][j] //第j种（j从1开始）硬币使用，就取减去这种硬币一个面值后的amount在包括当前硬币的组合方案个数，注意coins的index(j)不需要减1，表示第j种硬币会使用多次
                                        + dp[i][j-1]; //第j种硬币不使用，此种硬币有和没有一样，取同样amount但硬币种类倒退1的值：dp[i][j-1]
                    }else {
                        dp[i][j] = dp[i][j-1];//与上面不使用第j种硬币的情况一致
                    }
                }
            }
            return dp[amount][coins.length];
        }
        //简化思路：注意上面的状态转移方程 dp数组只和 dp[...][j]和dp[...][j-1] i维度的跨度大，不好压缩，但j维度只跨越了1，相当于当前维度的计算依赖同样维度的上一次的计算结果，这就是代码中覆盖局部变量以达到降维降低sc的技巧
        static int changeSimplify(int amount, int[] coins){
            int[] dp = new int[amount+1];
            dp[0]=1;//使用所有硬币凑出0元
            //降维时遍历的先后顺序有讲究，被去掉的维度应该先遍历
            //放在此场景的解释是：如果先对amount遍历，如amount=10考虑[1,2,5]三种硬币的[1,2]的组合方案数时，10-2=8, dp[8]是已经考虑了所有类型硬币的组合，是不能拿来使用的
            //也就是被降维的维度如果在dp数组生成时应该是固定的，如果还在变就不是不再考虑这一维度的问题（降维基于不变）
            for (int j = 0; j < coins.length; j++) {
                for (int i = 1; i <= amount; i++) {
                    if (i>=coins[j]){
                        dp[i] = dp[i-coins[j]] + dp[i];
                    }
                }
            }
            //上面的解法还可以再优化
            //for (int coin : coins){
            //    for (int i = coin; i <= amount; i++) {//amount小于此新coin的可以不考虑，反正用不上
            //        dp[i] = dp[i-coin]+dp[i];
            //    }
            //}
            return dp[amount];
        }
        public static void main(String[] args) {
            int[] coins = {1,2,5}, coins2 = {2}, coins3 = {2,4,6};
            int amount = 5, amount2 = 3, amount3 = 10;
            Assert.assertTrue(change(amount, coins) == 4);
            Assert.assertTrue(changeSimplify(amount2,coins2) == 0);
            Assert.assertTrue(changeSimplify(amount3,coins3) == 5);//5＊2;2224;244;46;226;
        }
    }

    /**
      系列打家劫舍问题
     # 198
     光顾一个数组内的房子，每个房子都有一定现金，相邻房屋装有防盗系统：如果两间相邻的房子在同一晚上被盗，将报警
     房屋现金 money = {1,2,3,1} 计算不触动警报的情况下，一次收集到的最高金额

     case 1: {1,2,3,1} - 4, 去1号和3号房子
     case 2: {2,7,9,3,1} - 12,去1 3 5号房
        纵向为房子数量
          2  7  9  3  1
     1-2  2  -  -  -  -
     2-7  2  7  -  -  -
     3-9  2  7 11  -  -
     4-3  2  7 11  10 -
     5-1  2  7 11  10 12
    解法类似 最长递增子序列
     */
    static class HouseRobber{
        /**
          光顾的最后一个房子，房子数量 两个状态
          思考顺序正过来是递归，倒过来是动态规划
          递归思路：profits[i] = max(profit[i+1], money[i]+profit[i+2])
         */
        static int dpSolutionForI(int[] money){
            int length = money.length;
            int[] dp = new int[length];
            //i表示房子数量，j表示最后光顾的房子，dp[i][j]表示收入
            int result = 0;
            for (int i = 0; i < length; i++) {
                int max = 0;
                for (int k = i-2; k >=0 ; k--) {
                    max = Math.max(max, dp[k]);
                }
                dp[i] = money[i] + max;
                result = Math.max(result, dp[i]);
            }
            return result;
        }
        //上述思路是最后一个房子一定盗，但如果仅考虑房子数量，最后一个不一定盗就可以少一层遍历
        //根本的不同是对于dp数组的定义：dp[i]有前i个房子考虑如何采花
        static int dpSolution2ForI(int[] money){
            int length = money.length;
            int[] dp = new int[length];
            for (int i = 0; i < length; i++) {
                int stealLast = money[i],stealPre = 0;
                if (i>=1){
                    stealPre = dp[i-1];
                }
                if (i>=2){
                    stealLast += dp[i-2];
                }
                dp[i] = Math.max(stealPre,stealLast);//dp[i]依赖dp[i-1] dp[i-2]可以优化SC
            }
            return dp[length-1];
        }
        /**
         窃格瓦拉 发现房屋数组首尾相连成了一个环，作何解？
         case 0: {} 0
         case 1: {2,3,2} 3
         case 2: {1,2,3,1} 4
         case 3: {1,2,3,4,2,1} 7
         ERROR - 第i个房子一定盗窃，则从i-2...到2判断最大的profit 这种算法无法发现第end个房子不盗窃有更大收入的机会

         关键思路：数组里最后一个房子到底去不去 不去-变成问题1 [0,length-2] 去-变成问题1 [1,length-3]
         */
        static int dpSolutionForII(int[] money){
            int length = money.length;
            if (length < 4){
                int max = 0;
                for (int i = 0; i < length; i++) {
                    max = Math.max(max, money[i]);
                }
                return max;
            }
            int stealLast = dpSolutionForIIHelper(money,1,length-3);
            int notstealLast = dpSolutionForIIHelper(money,0,length-2);
            return Math.max(stealLast+money[length-1],notstealLast);
        }
        static int dpSolutionForIIHelper(int[] money, int start, int end){
            int length = end - start +1;
            int[] dp = new int[length];
            for (int i = start; i <= end; i++) {
                int stealLast = money[i], stealPre = 0;
                if (i-1 >= start){
                    stealPre = dp[i-start-1];
                }
                if (i-2 >= start){
                    stealLast += dp[i-start-2];
                }
                dp[i-start] = Math.max(stealPre, stealLast);
            }
            return dp[length-1];
        }
        /**
          时间来到2020年，窃格瓦拉出狱了，然而狱中修炼的他这次决定出国做高技术觅香工作，毕竟人往高处走
         北欧的一个富人区，房子呈二叉树排列，同样两个相连的房屋被盗会报警
         case1: {3,2,3,null,3,null,1}   3+3+1=7
             3
            / \
           2   3
            \   \
             3   1
         case2: {3,4,5,1,3,null,1}    4+5=9
             3
            / \
           4   5
          / \   \
         1   3   1
                   0
             1           2
          3    4      5    6
         7 8  9 10  11 12      一个房子下面的序号是2n+1,2n+2这意味着
         一个房子上面的房子是 ceil(i/2)-1
         似乎不好动态规划
         case3 {4,1,null,2,null,null,null,3}
              4
             1
           2
         3
         既然树相邻的房子不能偷，那就像石头游戏，我隔层偷求和是否可以，比较偶数层与奇数层的和的大小
         ERROR: case3就是对这种思路的致命一击，偷1 4 两层比 其他类型的两层收获多的多。。。
         */
        static void buildBinaryTree(int[] money, TreeNode root, int rootIndex){
            int length = money.length;
            int leftIndex = rootIndex * 2 + 1;
            if (leftIndex < length && money[leftIndex] != 0){
                TreeNode left = new TreeNode(money[leftIndex]);
                root.left = left;
                buildBinaryTree(money, left, leftIndex);
            }
            int rightIndex = rootIndex * 2 + 2;
            if (rightIndex < length && money[rightIndex] != 0){
                TreeNode right = new TreeNode(money[rightIndex]);
                root.right = right;
                buildBinaryTree(money, right, rightIndex);
            }
        }
        static int recursiveSolutionForIIIHelper(TreeNode root){
            if (root == null){
                return 0;
            }
            int stealRoot = root.val +
                    (root.left == null?0: recursiveSolutionForIIIHelper(root.left.left)) +
                    (root.left == null?0: recursiveSolutionForIIIHelper(root.left.right)) +
                    (root.right == null?0: recursiveSolutionForIIIHelper(root.right.left)) +
                    (root.right == null?0: recursiveSolutionForIIIHelper(root.right.right));
            int keepRoot = recursiveSolutionForIIIHelper(root.left) + recursiveSolutionForIIIHelper(root.right);
            return Math.max(stealRoot,keepRoot);
        }
        static int recursiveSolutionForIII(int[] money){
            TreeNode root = new TreeNode(money[0]);
            buildBinaryTree(money,root,0);
            return recursiveSolutionForIIIHelper(root);
        }
        //先让递归进到树的树叶上
        //递归返回的结果是一个元组
        /**
         * 这其实是一个树的后续遍历
         */
        static Pair<Integer,Integer> recursiveSolutionForIII2Helper(TreeNode root){
            if (root == null){
                return Pair.of(0,0);
            }
            Pair<Integer,Integer> left = recursiveSolutionForIII2Helper(root.left);
            Pair<Integer,Integer> right = recursiveSolutionForIII2Helper(root.right);

            int stealRoot = root.val + left.getLeft() + right.getLeft();
            //root不偷，考虑偷left不偷right的组合大小？应该是left偷和不偷两种情况作为左侧分支的考虑结果，这里容易出错
            int keepRoot = Math.max(left.getLeft(), left.getRight())+
                            Math.max(right.getLeft(), right.getRight());
            return Pair.of(keepRoot, stealRoot);//左侧数据是不偷，右侧是偷
        }
        static int recursiveSolutionForIII2(int[] money){
            TreeNode root = new TreeNode(money[0]);
            buildBinaryTree(money,root,0);
            Pair<Integer, Integer> result = recursiveSolutionForIII2Helper(root);
            return Math.max(result.getLeft(), result.getRight());
        }
        //除了递归遍历树，暂未发现有啥更好的算法
        public static void main(String[] args) {
            int[] case1 = {1,2,3,1};
            int[] case2 = {2,7,9,3,1};
            //Assert.assertTrue(dpSolution2ForI(case1) == 4);
            //Assert.assertTrue(dpSolution2ForI(case2) == 12);
            int[] case20 = {};
            int[] case21 = {2,3,2};
            int[] case22 = {1,2,3,1};
            int[] case23 = {1,2,3,4,2,1};
            Assert.assertTrue(dpSolutionForII(case20) == 0);
            Assert.assertTrue(dpSolutionForII(case21) == 3);
            Assert.assertTrue(dpSolutionForII(case22) == 4);
            Assert.assertTrue(dpSolutionForII(case23) == 7);
            int[] case30 = {3,2,3,0,3,0,1};//7
            int[] case31 = {3,4,5,1,3,0,1};//9
            System.out.println(recursiveSolutionForIII2(case31));
        }
    }

    /**
     * dp问题解法 - 状态设计增加一维消除后效性
     * 无后效性：某阶段之后的发展变化仅与当前阶段的状态有关，而与此阶段前的状态无关。
     * 利用动态规划求解多阶段决策问题时，过程的状态必须具备无后效性
     * 动态规划通常不关系过程，只关心"阶段结果"，这个阶段结果就是dp问题中设计的状态。回溯算法才会关系过程，因此通常复杂度也较高
     */
    static class BackEffectProblem{
        //打家劫舍问题I如此，偷与不偷是不同的结果，那就使用元组（增加维度）记录这两种情况

        /*
         # 152 乘积最大子数组,连续的几个元素
         类似 #53 最大子序和  #300 最长上升子序列
         case1 {2,3,-2,4} 6=2*3
         case2 {-2,0,-1} 0
         */
        static class MaxProcuct{
            public static int maxProductFierceSolution(int[] nums){
                int length = nums.length;
                int[][] dp = new int[length][length];
                int max = Integer.MIN_VALUE;
                for (int i = 0; i < length; i++) {
                    for (int j = i; j < length; j++) {
                        //计算[i,j]个数字的乘积，记录最大值
                        int temp = 1;
                        for (int k = i; k <= j; k++) {
                            temp*=nums[k];
                        }
                        max = Math.max(max,temp);
                    }
                }
                return max;
            }

            //状态：开始i结束j 暴力解法
        /*
         负数的存在使得乘积可以在最大值和最小值之间转化，可以增加维度解决这一问题
         状态：数组长度i+乘还是不乘都保存下来

         状态：数组长度i， <no>最后一个元素永远作为子数组最后一个，最大乘积</no> pre元素上的最大值能和
         ⬆之前考虑过以数组长度作为dp状态，以nums[i]作为子数组最后一个元素，但最大乘积可能并不包含最后一个元素
           这种问题的解决办法是，最后一个元素放入子数组，对一整轮的遍历取一次最大值就可以了
         */
            public static int maxProductDpSolution(int[] nums){
                int length = nums.length;
                //扩展维度同时存储 left-0.最小值 - right-1.最大值，因为最小值负值越小在遇到负数时会变成最大值
                int[][] dp = new int[length][2];
                //此i用于遍历nums数组，不是数组长度作为状态
                //以nums[i]强行作为子数组的最后一个元素时，可能当前子数组的最大乘积就是这一个元素
                //当nums[i]为正，那么乘以dp[pre]就是最大值，nums[i]为负，乘以dp[pre]的记录的最小值才能转变成最大值，nums[i]为0，dp[i]就 = 0
                dp[0][0] = nums[0]; dp[0][1] = nums[0];
                for (int i = 1; i < length; i++) {
                    int current = nums[i];
                    int currentMax = 0;
                    int currentMin = 0;
                    //是否乘上前面的子数组的乘积，这是一个问题，类似打家劫舍的第三题
                    if (current > 0){
                        //难以理解的话可以通过举例改进当前写法得到正确的转移方程
                        currentMax = Math.max(current, current * dp[i-1][1]);//如果前一个数字是0，right就是0，那么应该取current
                        currentMin = Math.min(current, current * dp[i-1][0]);// 2 2 2<2,8> 2
                    }else if (current < 0){
                        currentMax = Math.max(current, current * dp[i-1][0]);//负值*以前一个元素为最末元素的子数组的乘积的最小值，如果前面都是正数，此时应抛弃前面的子数组
                        currentMin = Math.min(current, current * dp[i-1][1]);//-2 -2 -2<-8,-2> -2,最后一个-2作为current求解
                    }else {
                        currentMax = currentMin = 0;//这个可以省略
                    }
                    //无后效性在这个状态转移方程的反映：dp[i] 与 当前num[i]和有限个dp[pre] 有关，也就是更早前的dp[pre]对dp[i]没有影响
                    dp[i][0] = currentMin; dp[i][1] = currentMax;
                }
                int result = Integer.MIN_VALUE;
                for (int i = 0; i < length; i++) {
                    result = Math.max(result, dp[i][1]);
                }
                return result;
            }
            /*
             使用 滚动数组 或 滚动变量 优化上述算法的空间复杂度
             */
            public static int maxProductDpRollVariableSolution(int[] nums){
                int length = nums.length;
                int dp_pre_min = nums[0], dp_pre_max = nums[0];
                int dp_cur_min, dp_cur_max = Integer.MIN_VALUE;
                int result = Integer.MIN_VALUE;
                for (int i = 1; i < length; i++) {
                    int current = nums[i];
                    if (current > 0){
                        dp_cur_max = Math.max(current, current * dp_pre_max);
                        dp_cur_min = Math.min(current, current * dp_pre_min);
                    }else if (current < 0){
                        dp_cur_max = Math.max(current, current * dp_pre_min);
                        dp_cur_min = Math.min(current, current * dp_pre_max);
                    }else {
                        dp_cur_max = dp_cur_min = 0;
                    }
                    result = Math.max(result, dp_cur_max);
                    dp_pre_max = dp_cur_max;
                    dp_pre_min = dp_cur_min;
                }
                return result;
            }


            public static void main(String[] args) {
                int[] case1 = {2,3,-2,4};//6
                int[] case2 = {-2,0,-1};//0
                int[] case3 = {2,3,-2,4,-1,0,-2};//48
                log.info(""+maxProductFierceSolution(case1) + " = " + maxProductDpRollVariableSolution(case1));
                log.info(""+maxProductFierceSolution(case2) + " = " + maxProductDpRollVariableSolution(case2));
                log.info(""+maxProductFierceSolution(case3) + " = " + maxProductDpRollVariableSolution(case3));
            }
        }

        /*
         # 面试题 17.16 按摩师  就是 打家劫舍 I
         按摩师不断收到预约，但每个预约的时长不同，收益也就不同，按摩师需要休息，所以不会接连续的预约
         */
        static class LazyMassage{
            static int dpSolution(int[] nums){
                int length = nums.length;
                if (length == 0) return 0;
                if (length == 1) return nums[0];
                int[] dp = new int[length];
                dp[0] = nums[0];dp[1] = Math.max(nums[0],nums[1]);
                for (int i = 2; i < length; i++) {
                    dp[i] = Math.max(nums[i]+dp[i-2], dp[i-1]);
                }
                return dp[length-1];
            }
            public static void main(String[] args) {
                int[] case4 = {};
                int[] case5 = {1};
                int[] case6 = {1,2};
                int[] case7 = {3,5,2};
                int[] case1 = {1,2,3,1};
                int[] case2 = {2,7,9,3,1};
            }
        }
        //无后效性似乎是dp思想的一个基本特征，状态转移方程就是此特征的具体表现
    }


    /*-=-=-=-=-= 系列高楼扔鸡蛋问题 =-=-=-=--=-=*/

    /**
      # 887 鸡蛋掉落
     K个鸡蛋，可以从1到N层的建筑里测试，测试出让鸡蛋恰好摔不碎的最高楼层F
     case1: K=1,N=2  2
        鸡蛋从1楼掉落，如果碎了，F = 0，如果没碎再从2楼掉落，如果此时碎了，则F = 1,如果没碎F = 2
        所以至少要移动2次确定F的值
     case:  K=1,N=7  7
     case2: K=2,N=6  3
     case3: K=3,N=14  4

     ## 分析
     在不考虑鸡蛋个数限制的情况下，对于N层楼，寻找F层
     最坏的情况就是从1楼试到N层才碎（线性扫描），则F=N
     最好的情况就是第一层楼就碎了，则F=1
     对上述做法进行优化，最好的策略是使用二分查找法，在N/2层进行一次尝试，如果碎了就向下尝试，如果没碎就向上尝试，这种策略的最坏情形就是在1楼就碎和在N楼也不碎，需要尝试的次数：至少是log(2,N)

     但现在鸡蛋的数量限制了，就不能贸然使用二分法尝试高楼层了，需要结合二分法和逐层扫描法

     题目最终就是要去求 最优策略在最坏情况下试验的次数
     这个次数是有极限的，如100层2个鸡蛋最优策略最坏也要试验14次，而且这个最优策略也不是唯一的
     这个14怎么求？

     ## 动态规划解法
     【状态】：当前拥有的鸡蛋数K 和 需要测试的楼层数N
     【选择】选择去哪层楼扔鸡蛋
     【状态转移】在第i层楼扔了鸡蛋之后，可能出现两种情形：蛋碎了/蛋没碎
        如果鸡蛋碎了：K-1，搜索楼层从 [1..N] 变为 [1..i-1] 共i-1层
        如果鸡蛋没碎：K不变，搜索的楼层从 [1..N] 变为 [i+1..N] 共N-i层  （这里第二轮没有包含第i层，在试验1-N层时，F可以取0，这样i+1 - N对应F取零的就是第i层）
     fori 1~N
        int res = 0;
        res = min(res, max(dp(K-1,i-1), dp(K,N-1))+1 )
        return res
     【base case】楼层N=0时 返回0; 鸡蛋数K=1时，返回总楼层数N
     */
    static class EggDropProblem{

        static int recursiveSolution(int K, int N){
            int[][] dp = new int[K+1][N+1];
            return recursiveHelper(K,N,dp);
        }
        /*
        递归的思路就是 可以回到过去，鸡蛋可以复原，每个楼层都试一次，找到所有楼层的result的最小值
        一次计算就是 蛋碎了和蛋不碎情况下的递归结果的较大值 + 这次试验消耗的试验次数:1
        蛋碎了如何递归：K-1,去试验低楼层1~i-1
            递归入参楼层数为i-1,相当于忽略掉i以上的楼层
        蛋没碎如何递归：K不变，继续拿着这颗蛋去试验高楼层
            递归入参楼层变为N-i,相当于上一步的低楼层埋掉重新给楼层编号(可见楼层的高低并不是算法的考虑)
        递归自动处理了楼层号为 i-j 的情况。。。
         */
        static int recursiveHelper(int K, int N, int[][] dp){
            if (K == 1) return N;//递归的一个重点在于base case，而base case直接反映K N的值
            if (N == 0) return 0;
            if (dp[K][N] != 0) return dp[K][N];
            int result = Integer.MAX_VALUE;
            //2个鸡蛋试验100层楼先去哪个楼层比较合适呢？干脆就都试一遍比较下大小了
            for (int i = 1; i < N + 1; i++) {
                result = Math.min(result,
                        Math.max(recursiveHelper(K,N-i, dp), recursiveHelper(K-1,i-1,dp)) + 1
                        //由递归的传参表明：N表示剩余楼层的数量，而这剩余50层楼是高处50层还是低处50层，算法是不考虑的，因为对于鸡蛋不同楼层都只有一个特性，让鸡蛋碎还是不碎，楼层高低只是一个编号而已
                        //此N随着递归的进行一直在变化，似乎研究的楼层在变化，N-i总是可以得到后部分的楼层数
                        //楼层高低又对算法有影响，鸡蛋碎了要去后部编号尝试，没碎要去前部编号尝试
                        );//好神奇
            }
            dp[K][N] = result;
            return result;
        }//TC = O(N*max(K,N)) = O(KN) ????

        /*
           上述递归解法是下述状态转移方程的一个应用：
           dp(K,N) = min{ max{dp(K-1,i-1), dp(K,N-i)} + 1}
           固定K N，则dp(K-1,i-1)随i单调递增，dp(K,N-i)随i单调递减，两者max的min在dp(K-1,i-1) = dp(K,N-i)时取得，这样dp(K,N)就是最小值
            关键思路：二分查找法在找一个mid，似乎是在让 broken = survive，这就是上面两个dp一个递增一个递减求两者较大值的最小情况的一个关键思路
         */
        static int binarySearchSolution(int K, int N){
            int[][] dp = new int[K+1][N+1];
            return binSearchHelper(K,N,dp);
        }
        static int binSearchHelper(int K, int N, int[][] dp){
            if (K == 1) return N;
            if (N == 0) return 0;
            if (dp[K][N] != 0) return dp[K][N];
            int result = Integer.MAX_VALUE;
            //使用二分搜索替代线性搜索
            int low = 1, high = N;
            while (low <= high){
                int mid = (low + high)/2;
                //mid层上扔鸡蛋会不会碎,注意调用的是helper，写成binarySearchSolution也能得出结果，但很慢
                int broken = binSearchHelper(K-1,mid - 1,dp);//如果碎了，低层需要的试验次数
                int survive = binSearchHelper(K,N - mid,dp);//如果没有碎，高层需要的试验次数
                //到这里可以看出二分搜索与线性搜索的区别：线性搜索相当于是把这里的mid从1到N都尝试了一遍得到最小值
                if (broken > survive){ //当前试验楼层，鸡蛋碎了情况下剩余试验次数 如果大于 鸡蛋完好情况下剩余试验次数，说明楼层挑地太高了，鸡蛋容易损失掉，导致后面要逐层尝试了，所以楼层选矮点
                    high = mid - 1;
                }else {
                    low = mid + 1;
                }
                result = Math.min(result, Math.max(broken,survive) + 1);//因为求的是至少要试验多少次，所以按最坏情况考虑，应取broken survice的最大值
            }
            dp[K][N] = result;
            return result;
        }/*求解 K=2,N=100 时二分查找算法性能很差，while在遇到1~较大N时无谓计算太多*/
        //TC = O(KNlogN)

        /*
        # 反转状态转移
        dp[k][n] 当前状态为k个鸡蛋、面对n层楼，这个状态下最少的扔鸡蛋次数为m
        反向思考原问题，求楼层F。那如果已知F求N呢？
        dp[k][m] 当前状态为k个鸡蛋，可以尝试扔m次鸡蛋，最坏情况下能确切测试出的最高楼层n
            dp[1][7]=7 表示有1个鸡蛋，允许扔7次，这个状态下最多可以给7层楼使得测试人员确定楼层F（恰好不碎的楼层）

        上述反向思路对应的代码结构应该是这样的：
        while(dp[K][m] < N){
           m++; //如果dp[K][m]小于N就让m++，啥时候达到N，m就停下，这样就找到答案了
        }
        反向思路的一些分析结论：
        1. 无论你在哪层楼扔鸡蛋，鸡蛋只可能摔碎或者没摔碎，碎了的话就测楼下，没碎就测楼上
        2. 无论上楼测试还是下楼测试，总的楼层数 = 楼上的层数（N-i） + 楼下的层数(i-1) + 1（i这一层）
        对第二条结论进行转化
        k个鸡蛋m次测试可以确定F的最小N = 鸡蛋没碎时依然有k个鸡蛋，但剩下m-1次测试次数最坏所能测试出的楼层数 + 鸡蛋碎了少了一个剩下k-1个鸡蛋同时剩下m-1次测试次数所能测试出的楼层数 + 1（中间刚刚测过的一个楼层）
        dp[k][m] = dp[k][m-1] + dp[k-1][m-1] + 1
        这样可以考虑填写dp数组了
        case: K=2 N=4
            0   1   2   3   4   <--m是试验的次数
        0   -   -   -   -   -
        1   0   1   2   3   4
        2   0   1   3   6   10
        K=2,变化的是i
        填充dp数组时，dp[k][m] = 左侧一个 + 左上角一个元素 + 1
        这样问题就变得极度简单了
         */
        static int classicDpSolution(int K, int N){
            if (K == 1) return N;
            if (N == 0) return 0;
            int[][] dp = new int[K+1][N+1];//m再大也不超过N
            for (int i = 0; i <= N; i++) {
                dp[1][i] = i;
            }
            for (int i = 0; i <= K; i++) {
                dp[i][0] = 0;
            }
            for (int k = 2; k <= K; k++) {
                for (int m = 1; m <= N; m++) {
                    dp[k][m] = dp[k][m-1] + dp[k-1][m-1] + 1;
                }
            }
            int result = 0;
            for (int i = 0; i <= N; i++) {
                if (dp[K][i] >= N){
                    result = i;
                    break;
                }
            }
            return result;
        }
        //可以进一步压缩dp数组
        /*
        附加问题：
        如果有不限鸡蛋数量，K=+infinity, N=100, 则至少要试验多少次？
        如果有两个鸡蛋，最坏的情况需要扔多少次一定能把F找出（算法给出的答案是14，那这种策略具体是怎么扔的？）
            第一次在n层测试，第二次在n+(n-1)层测试，第三次在n+(n-1)+(n-2)层测试，，，以此类推，n>= 13.65 取n=14
            这样第一次测试的楼层列表是 [14,27,39,50,60,69,77,...]共12次
            第二次+第一次发现总是14次

         */



        public static void main(String[] args) {
            int K = 1, N = 2;
            int K1 = 1, N1 = 7;
            int K2 = 2, N2 = 6;//3
            int K3 = 3, N3 = 14;//4
            int K4 = 2, N4 = 100;//14
            int K5 = 5, N5 = 100;//7
            //log.info(""+ binarySearchSolution(K,N));
            //log.info(""+ binarySearchSolution(K1,N1));
            //log.info(""+ binarySearchSolution(K2,N2));
            //log.info(""+ binarySearchSolution(K3,N3));
            //log.info(""+ recursiveSolution(K4,N4));
            //log.info(""+ binarySearchSolution(K5,N5));
        }
    }





























}
