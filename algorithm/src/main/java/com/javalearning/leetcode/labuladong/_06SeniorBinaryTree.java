package com.javalearning.leetcode.labuladong;

import com.javalearning.leetcode.components.TreeNode;
import com.javalearning.leetcode.utils.TestCases;

import java.util.Stack;

public class _06SeniorBinaryTree {
    /** =-=-=-=-=-=-=- BST相关问题 -=-=-=-=-=-=-= */
    /**
     # 二叉搜索数 BST 的特性
     1. 对于BST的每一个结点root，左子树比root的值小，右子树比root的值大
     2. 对于BST的每个结点root，它的左子树和右子树都是BST
     直接基于BST的有AVL树、红黑树，拥有自平衡性质，可以提供logN级别效率的增删改查

     BST的一个重要性质：中序遍历结果是升序数列
     */

    /**
     # 230 二叉搜索树中第K小的元素
        1 <= k <= BST中元素个数
     示例：[5,3,6,2,4,null,null,1], k=3, 结果 3

     */
    static class FindKthItemInBST{
        static int kthSmallest(TreeNode root, int k){
            return 0;//懒得写了
        }
        //第k大元素的查找，似乎要全部遍历完，也懒得写了
        static int kthBiggest(TreeNode root, int k){
            kthBiggestHelper(root);
            return 0;
        }
        static TreeNode kthBiggestHelper(TreeNode root){
            if (root == null) return null;
            TreeNode left = kthBiggestHelper(root.left);
            System.out.print(root.val+"- ");
            TreeNode right = kthBiggestHelper(root.right);
            return root;
        }
        /*
        上述中序遍历算法时间复杂度O(N),比较低效
        像红黑树那样实现一个O(logN)级别复杂度的算法，查找第k小元素
        BST的操作高效的原因在于其结构，自身的构造决定了在寻找元素时可以通过对比当前结点的值决定
        应该是去左子树还是右子树搜索目标值，从而避免了全树遍历，达到对数级别复杂度

        BST中的结点如果维护一个排名信息就可以做到知道当前结点的排名信息，从而可以决定去左子树还是右子树去查找
        这样一颗BST多次进行kthSmall元素查找时可以显著提高性能
        TreeNode{int val; int rank; TreeNode left; TreeNode right}
         */
        public static void main(String[] args) {
            kthBiggest(TestCases.binSearchTreeKthSmall,3);
        }
    }

    /**
     # 538/1038 转化累加树 Greater Sum Tree
     修改BST上每个结点node的值，使得新值等于整个树上大于等于原值的所有结点的值的和
     示例：
            4                               4（30）
        1       6           -->     1(36)          6(21)
     0    2    5  7             0(36)  2(35)     5(26)  7(15)
            3       8                    3(33)              8(8)
     */
    static class AccumulationBST{
        /*
        BST降序遍历就可以解决问题
        原来二叉树还存在一个右中左的遍历方式，遍历方式原本来自代码，来自实践，机械学习就会以为二叉树只能那样遍历
         */
        static int sum = 0;
        static TreeNode convertBST(TreeNode root){
            if (root == null) return null;
            convertBST(root.right);
            sum+=root.val;root.val = sum;
            convertBST(root.left);
            return root;
        }
        /*
         J.H.Morris在1979年的论文《Traversing Binary Trees Simply and Cheaply》中提出
         利用树的大量空闲指针，实现反序中序遍历，并且避免了递归处理（Base里的106题也有这种避免递归处理二叉树的改进）
         逐步调试可以看出算法的巧妙之处，难以掌握
         */
        static TreeNode morrisTraverse(TreeNode root){
            int sum = 0;
            TreeNode node = root;
            while (node != null){
                if (node.right == null){
                    sum += node.val;//先设法到达最右边的叶子上
                    node.val = sum;
                    node = node.left;//改算法利用了叶子节点许多left指针空着不用的情况，用它指向更小元素，实现回溯，也就是倒序遍历
                }else {
                    TreeNode succ = getSuccessor(node);//初始情况下 - 4的后继结点是5; 6的后继是7; 7的后继是8;
                    if (succ.left == null){
                        succ.left = node;
                        node = node.right;//5.left -> 4; 7.left -> 6; 8.left->7;
                    }else {
                        succ.left = null;//之前利用left指向下一小元素的这里要取消链接，还原BST
                        sum += node.val;
                        node.val = sum;
                        node = node.left;//总是回到left上，不光是算法修改的left指针，在处理完root时也靠这个前往左子树
                    }
                }
            }
            return root;
        }
        //寻找node的继任，node.right.left 会指向node？！
        static TreeNode getSuccessor(TreeNode node){
            TreeNode succ = node.right;
            while (succ.left != null && succ.left != node){
                succ = succ.left;
            }
            return succ;
        }


    }

    /**
      # 98 验证一棵树是不是二叉搜索树
        10
     5      15
          6   20 //由于6的存在，这个不是BST,所以不能在当前递归中简单地检查root跟其left、right的大小关系
     */
    static class BSTValidation{
        //BST中root跟left或right不可以相等，没有重复元素
        static boolean errorValidBST(TreeNode root){
            if (root == null) return true;
            //注意left/right为空的情况
            if (root.left != null && root.left.val >= root.val) return false;
            if (root.right != null && root.right.val <= root.val) return false;
            boolean left = errorValidBST(root.left);
            boolean right = errorValidBST(root.right);
            return left && right;
        }//这个解法是错的
        /**
          提供额外信息min max, root.left应该大于之前的root。。。 min\max都是前面使用的root递归时带进来的
         难以描述，只能背下来
         */
        static boolean isValidBST(TreeNode root, TreeNode min, TreeNode max){
            if (root == null) return true;
            if (min != null && root.val <= min.val) return false;
            if (max != null && root.val >= max.val) return false;
            boolean validLeft = isValidBST(root.left, min, root);
            boolean validRight = isValidBST(root.right, root, max);
            return validLeft && validRight;
        }
        /*
         中序遍历也可以，不用完全遍历完,只比较前后两个元素，不写了 5,10,6,15,20
         中序遍历使用stack改成非递归写法也可以
         */
        static boolean validBSTNonRecursion(TreeNode root){
            Stack<TreeNode> stack = new Stack<>();
            int preVal = Integer.MIN_VALUE;
            while (root != null){
                while (root.left != null && !root.left.visited){
                    stack.push(root);
                    root = root.left;
                }
                if (preVal > root.val) return false;
                preVal = root.val;
                root.visited = true;
                if (root.right == null){
                    if (!stack.isEmpty())
                        root = stack.pop();
                    else
                        root = null;
                }else {
                    root = root.right;
                }
            }
            return true;
        }
        static boolean validBSTNonRecursion2(TreeNode root){
            Stack<TreeNode> stack = new Stack<>();
            TreeNode preVal = null; //Integer.MIN_VALUE在leetcode里作为了测试用例，这里要用null标记
            while (root != null || !stack.isEmpty()){
                while (root != null){
                    stack.push(root);
                    root = root.left;
                }
                root = stack.pop();
                if (preVal != null && preVal.val >= root.val) return false;
                preVal = root;
                root = root.right;
            }
            return true;
            //性能（25.57）比不上上面的方法
        }
        /**
         中序遍历的非递归写法（要用stack）
         先到最左树叶上，一路把root压入stack
            while (root.left != null){ //ERROR 没有visited标记这种写法是错误的
                stack.push(root);
                root = root.left;
            }
            //可以输出root的值，并且切到right上
            System.out.println(root.val);
            root = root.right;
         首次走到右侧子树上，就需要把上面的流程再走一遍，所以要再套一个while
         */
        static void printBTInOrderNoRecursion(TreeNode root){
            Stack<TreeNode> stack = new Stack<>();
            while (root != null || !stack.isEmpty()){
                while (root != null){ //root要允许进来，死循环的原因就是这里的root不知道是否已经遍历过，如果TreeNode支持已访问标记就可以
                    stack.push(root);
                    root = root.left;
                }
                root = stack.pop();
                System.out.println(root.val);
                root = root.right;//root = root.right == null ? stack.pop() : root.right; 这样写会死循环
            }
        }
        //引入visited字段代码也很难看
        static void printBTInOrderNoRecursion3(TreeNode root){
            Stack<TreeNode> stack = new Stack<>();
            while (root != null){
                while (root.left != null && !root.left.visited){
                    stack.push(root);
                    root = root.left;
                }
                System.out.println(root.val);
                root.visited = true;
                if (root.right == null){
                    if (!stack.isEmpty())
                        root = stack.pop();
                    else
                        root = null;
                }else {
                    root = root.right;
                }
            }
        }
        public static void main(String[] args) {
            //boolean validBST = isValidBST(TestCasesAndUtils.validBSTree, null, null);
            //boolean validRes = validBSTNonRecursion2(TestCasesAndUtils.validBSTree);
            //printBTInOrderNoRecursion3(TestCasesAndUtils.validBSTree);
            System.out.println(Integer.MIN_VALUE);
        }
    }
    /**
      # 700 在二叉搜索树中搜索元素
     */
    static class SearchInBST{
        //简单的元素是否存在于二叉树的判断
        static boolean existInBinaryTree(TreeNode root, int nodeVal){
            if (root == null) return false;
            //爱咋遍历咋遍历
            if (root.val == nodeVal) return true;
            return existInBinaryTree(root.left, nodeVal) && existInBinaryTree(root.right, nodeVal);
        }
        //针对BST进行查询优化,这是BST搜索元素的模板代码
        static boolean existInBinarySearchTree(TreeNode root, int target){
            if (root == null) return false;
            if (target > root.val)
                return existInBinarySearchTree(root.right,target);
            if (target < root.val)
                return existInBinarySearchTree(root.left, target);
            return true;
        }
    }
    /**
      # 701 BST中插入元素
     总是可以找到一个位置，不需要考虑旋转等复杂操作
     */
    static class InsertIntoBST{
        static TreeNode insertIntoBST(TreeNode root, int val){
            if (root == null) return new TreeNode(val);
            TreeNode res = root;
            TreeNode preRoot = root;
            while (root != null){
                if (val > root.val){
                    preRoot = root;
                    root = root.right;
                }else if (val < root.val){ //这里要if else, 写成两个if出错了
                    preRoot = root;
                    root = root.left;
                }
            }
            if (val > preRoot.val){
                preRoot.right = new TreeNode(val);
            }else
            if (val < preRoot.val){
                preRoot.left = new TreeNode(val);
            }
            return res;
        }
        //递归写法不需要对root做局部变量保存
        static TreeNode insertIntoBSTRecursively(TreeNode root, int val){
            if (root == null) return new TreeNode(val);
            if (val < root.val){
                TreeNode left = insertIntoBSTRecursively(root.left, val);
                if (left.left == null) root.left = left;
            }else if (val > root.val){
                TreeNode right = insertIntoBSTRecursively(root.right, val);
                root.right = right;//上面那个if判断可以去掉，就是有许多结点会重复赋值一次
            }
            return root;
        }

        public static void main(String[] args) {
            TreeNode node = insertIntoBSTRecursively(TestCases.insertIntoBSTree, 88);
        }
    }

    /**
      # 450 删除二叉搜索树中的结点
     删除操作同时要维持BST的结构特性，分下述情况：
     1. 删除的是叶子节点
     2. 只有一个子结点，直接让子节点替代自己
     3. 有左右两个子结点，A - 找左子树中的最大节点代替自己； B - 找右子树中的最小节点代替自己。
     先找到节点再操作，可以将寻找节点的代码作为框架
     */
    static class DeleteNodeInBST{
        /*
         递归解法
         可能会想持有parent节点，当前root被删除时可以看情况把left或right挂到parent上
         在递归的环境下持有parent很麻烦，代码也不好看，应主动返回当前root的合适替换节点到上一层遍历，parent不需要传递
         */
        static TreeNode deleteNodeRecursive(TreeNode root, int val){
            if (root == null) return null;
            if (val < root.val){
                TreeNode newRoot = deleteNodeRecursive(root.left, val);
                if (newRoot == null || root.left.val != newRoot.val)//加点判断防止重复让 right.left = right.left, 也可以不加（由于是拷贝数据的，加了判断也还是会有一些重复的赋值）
                    root.left = newRoot;//root.left就是要删除的节点的话，直接接上；子结点的更深节点要删除，不关当前的root什么事
                return root;
            }else if (val > root.val){
                root.right = deleteNodeRecursive(root.right, val);
                return root;//要返回root，上一级会重复挂一次right/left
            }else {
                //root是待删除节点
                //处理第1种情形
                if (root.left == null && root.right == null){
                    return null;//上一层遍历的root拿到这个null挂到left/right，相当于删除节点
                }
                //处理第二种情形
                if (root.left == null) return root.right;
                if (root.right == null) return root.left;
                //处理第三种情形
                //寻找左子树中的最右/大节点代替自己
                TreeNode parent = root, successor = root.right;//[A->B]
                while (successor.left != null){//[A->B]
                    parent = successor;
                    successor = successor.left;//[A->B]
                }
                root.val = successor.val;//拷贝数据，而不是修改引用
                deleteNodeRecursive(parent, successor.val);//被拷贝的节点删除,递归调用
                return root;//新root交给上层遍历
            }
            //发现root可以在这里统一返回
            //上述代码中[A->B]标记的行，将left/right置换可实现切换AB两种方案
            /*
                5 --parent
              3    6 --successor
            2   4    7 //parent.right = successor.right

                5
              3   7 --parent
               4 6 8 --6-successor //parent.left = null

               5
                  7 --parent
                6   8 --6-successor //parent.left = ??? 这一案例表明successor的删除是同样的问题，要使用递归
                 6.5

             5
                  7
                6    8
             6.2 6.5
             */
        }

        /**
         * 上述代码比较乱，简化一下
         *
         * 算法难点在于，需要明确每次遍历传入和返回的TreeNode是什么
         * 传入的总是子结点left/right，返回的总是传入的TreeNode,只不过val可能被改了
         * 所以需要将子结点传入再赋值给子结点
         */
        static TreeNode deleteNodeRecursively(TreeNode root, int key){
            if (root == null) return null;
            if (key > root.val){
                root.right = deleteNodeRecursively(root.right, key);
            }else if (key < root.val){
                root.left = deleteNodeRecursively(root.left, key);
            }else {
                if (root.left == null) return root.right;
                if (root.right == null) return root.left;
                //被删除节点存在左右子节点,上面的parent可以去掉
                TreeNode successor = root.right;
                while (successor.left != null) successor = successor.left;
                root.val = successor.val;
                root.right = deleteNodeRecursively(root.right, successor.val);
            }
            return root;
        }

    }

    /*
    BST相关问题的目前总结：利用左小右大的特性提升算法效率/利用中序遍历的特性满足题目要求
    */
    /* =-=-=-=-=-=-=- BST相关问题 -=-=-=-=-=-=-= */

    /**
      # 297 二叉树的序列化与反序列化
     共4种实现方式： 3种递归遍历 + 逐层遍历
     单独的递归遍历是无法还原原始的二叉树的，那是因为空指针信息都丢失了，所以序列化时带上null叶子节点信息才可以反序列化
     依然是递归的应用
     */
    static class BinaryTreeSerialization{
        public static final String NULL_LEAF = "#";
        //像leetcode那样来个逐行扫描
        static class CodecLineScan {
            // 把一棵二叉树序列化成字符串
            static String serialize(TreeNode root) {
                return null;
            }
            // 把字符串反序列化成二叉树
            public TreeNode deserialize(String data) {
                return null;
            }
            static String deserializeHelper(TreeNode root, String data){
                return null;
            }
            public static void main(String[] args) {

            }
        }

    }
}
