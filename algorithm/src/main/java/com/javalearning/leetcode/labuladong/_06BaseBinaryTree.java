package com.javalearning.leetcode.labuladong;

import com.javalearning.leetcode.components.TreeNode;
import com.javalearning.leetcode.utils.TestCases;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
  此处二叉树问题的求解需要明确二叉树的每个结点需要做什么，才能写出代码
 */
public class _06BaseBinaryTree {
    static class BinaryTreePractice1{
        /**
         # 236 二叉树最近公共祖先
         任意给定的两个二叉树的结点，找到最近公共祖先，一个结点的最近公共祖先可以是其自身
         [3,5,1,6,2,0,8,null,null,7,4]
         p=5 q=1 result=3
         p=5 q=4 result=5

         二叉树问题常用二叉树遍历递归解决
         递归思路通常需要考虑 函数的作用；函数的入参变量；递归函数的返回结果的处理；
         */
        static TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q){
            //base case 1.树遍历完了，p q不在某个分支下 2.终于找到p/q，此时p/q一定作为根节点root出现，直接返回自身作为父结点
            if(root == null) return null;
            if (root == p || root == q) return root;
            /**
             二叉树遍历的代码模板，分别遍历左右子树
             在左右子树中分别找p q
             （1）如果p、q都不在root.left下，则对root.left遍历最终返回null
             （2）如果p、q都在root.left下，则对root.left查找返回的结果就是答案
             （3）如果p在root.left下，而q在root.right下，则对左右两侧的遍历得到就是p、q两个结点，
             这种情形似乎没有找到公共祖先，但观察当前的递归函数中的变量，root.left是p的上级结点，root.right是q的上级结点,
             目的是求p q的最近公共父结点，则当前的root很完美的满足这个要求
             在左右子树遍历完成后处理结果，这是一个后序遍历
             上述三个如果的情形 就是递归返回结果的处理方法
             */
            TreeNode left = lowestCommonAncestor(root.left, p, q);
            TreeNode right = lowestCommonAncestor(root.right, p, q);
            //对应上面第三种情形，找到了答案
            if (left != null && right != null){
                return root;
            }
            //对应上面的（1）（2）情形，表示某一分支包含p、q，该分支遍历已经得到了最终答案
            if (left != null || right != null){
                return left == null ? right : left;//返回不为null的那个结点
            }
            //当前左右子结点下都没有p q，显然这是遍历过程的中间环节，往上才有p q，直接返回null即可
            if (left == null && right == null){
                return null;
            }
            return null;//为体现各种情形，这里重复写返回情形
        }

        /**
         递归是一个强定义的算法思路，不需要太多繁复的计算
         所以定义、思路清晰是正确解决这类问题的关键
         树相关的算法的一个思路：先确定当前root结点该做什么，然后根据函数定义递归调用子结点，让子结点也做相同的事
         安排好遍历的次序：前序遍历、中序遍历 还是 后序遍历
         */
        /**
         计算一棵二叉树共多少个结点
         */
        static int countTreeNode(TreeNode root){
            if (root == null) return 0;
            int left = countTreeNode(root.left);
            int right = countTreeNode(root.right);
            return 1+left+right;
        }
        /**
          # 226 翻转二叉树
         输入一个二叉树跟结点root，将整棵树镜像翻转
         TC = O(N) 遍历所有结点
         SC 空间复杂度就是树的深度，通常情况下应为O(lgN)，如果树变成链状，就是O(N)
         */
        static TreeNode invertTree(TreeNode root){
            if (root == null) return root;
            TreeNode temp = root.left;
            root.left = root.right;
            root.right = temp;
            invertTree(root.left);
            invertTree(root.right);
            return root;
            /*
             前序遍历和后续遍历就是代码挪挪位置，中序遍历翻转时需要注意是两个invertTree(root.left);
            * */
        }
        /**
          # 116 填充二叉树结点的下一个右侧结点指针
         一个完美二叉树，结点定义如下,填充其next指针，让这个指针指向其下一个右侧结点，找不到就指向null
         就是说next指向同级的右侧结点，指向弟弟结点
               1 -> null
           2  -->  3 -> null
         4-> 5-> 6-> 7 -> null
        8 9 0 1 2 3 4 5

         注意5的next指向6，在当前结点root下处理没法办到，因为5 6是跨了父结点的
         而且随着树的深度增加，跨父结点连接的深度也在增加,这个问题如何解决呢？

         通常的思路都是这样写的
         left.next = right;
         connect(left);
         connect(right); 这种写法不能跨父结点

         要跨父结点，connect函数传一个root肯定做不到，那就传两个root
         left1.next = right1;right1.next=left2;left2.next=right2;
         connect(left1,right1)
         connect(left2,right2)

         */
        static class Node{
            int val;
            Node left;Node right;Node next;

            public Node(int val) {
                this.val = val;
            }

            //
            static Node connect(Node root){
                if (root == null) return root;
                //root.left.next = root.right;
                //connect(root.left);
                //connect(root.right);
                connectHelper(root.left, root.right);
                return root;
            }
            static void connectHelper(Node root1, Node root2){
                if (root1 == null || root2 == null){
                    return;
                }
                root1.next = root2;//为子结点考虑next，自己的可不能漏了
                if(root1.left == null || root1.right == null || root2.left == null){
                    return;
                }
                root1.left.next = root1.right;
                root1.right.next = root2.left;
                root2.left.next = root2.right;
                connectHelper(root1.left, root1.right);
                connectHelper(root2.left, root2.right);
                connectHelper(root1.right, root2.left);//A
                /*
                 这种写法比较罗嗦还容易出错
                 这种写法的思路是考虑当前两个兄弟结点下的4个结点如何处理next指针
                 需要注意A行不能漏掉，A行是处理跨父结点的两个表兄弟结点，在二叉树达到4层时可以看出
                 */
            }
            static void connectHelper2(Node root1, Node root2){
                if (root1 == null || root2 == null){
                    return;
                }
                root1.next = root2;
                connectHelper2(root1.left, root1.right);
                connectHelper2(root2.left, root2.right);
                connectHelper2(root1.right, root2.left);
            }

            public static void main(String[] args) {
                Node node1 = new Node(1);
                Node node2 = new Node(2);
                Node node3 = new Node(3);
                Node node4 = new Node(4);
                Node node5 = new Node(5);
                Node node6 = new Node(6);
                Node node7 = new Node(7);
                node1.left = node2;node1.right = node3;
                node2.left = node4;node2.right = node5;
                node3.left = node6;node3.right = node7;
                connect(node1);//注意，只考虑完美二叉树
            }
        }

        /**
          # 117 二叉树展开为链表
         原地展开为一个单链表(原地展开不可以使用List单独收集)
              1
           2    5
         3  4     6
         => 1 2 3 4 5 6

         */
        static class FlatBinaryTree{
            static void flatten(TreeNode root){
                if (root == null) return;
                //结点都挂在左结点上
                //flatenHelper(root, root.left, root.right);
                //root.right = null;

                //结点都挂在右结点上
                flatenHelper2(root, root.left, root.right);
                root.left = null;

            }
            /*
             右子树始终挂在左子树上，另一个root与另一棵子树断开要单独处理
             不好写就把所有可能要用到的变量放到递归里
            【自有解】
            **/
            static TreeNode flatenHelper(TreeNode root, TreeNode left, TreeNode right){
                if (left == null){
                    if (right == null){
                        return root;
                    }
                    root.left = right; root.right = null;
                    return flatenHelper(right, right.left, right.right);
                }
                root.right = null;
                //处理 left 得到最底下的一个结点
                TreeNode leftBottom = flatenHelper(left, left.left, left.right);
                //把 right 挂在leftBottom上
                leftBottom.left = right; leftBottom.right = null;
                //继续处理 right
                return flatenHelper(right, right.left, right.right);
            }
            //左子树始终挂在右子树上,前序遍历
            static TreeNode flatenHelper2(TreeNode root, TreeNode left, TreeNode right){
                if (left == null){
                    if (right == null){
                        return root;
                    }
                    return flatenHelper2(right, right.left, right.right);
                }
                root.right = left;root.left = null;
                TreeNode leftBottom = flatenHelper2(left, left.left, left.right);
                if(right == null){
                    return leftBottom;//纯左树的情况下需要加上这一行
                }
                leftBottom.right = right; leftBottom.left = null;
                return flatenHelper2(right, right.left, right.right);
            }
            //只使用root作为入参 【学习解】 。。。这个很简洁，性能也很高
            static void flatten2(TreeNode root){
                if (root == null) return;
                flatten2(root.left);
                flatten2(root.right);
                //后序遍历位置 左->右->中
                //后续遍历时应认为左右子树已经拉平成一条链表，所以将右root挂到左叶子结点即可
                TreeNode left = root.left;
                TreeNode right = root.right;
                root.left = null;
                root.right = left;
                //找到左叶子结点挂右root
                TreeNode temp = root; //要从root往下走，使用left有可能此时已经是null了
                while (temp.right != null){
                    temp = temp.right;
                }
                temp.right = right;
            }
            //走中序遍历
            static void flatten3(TreeNode root){
                if (root == null) return;
                flatten3(root.left);

                TreeNode right = root.right;//right指针备份一下
                root.right = root.left;
                root.left = null;
                TreeNode temp = root;
                while (temp.right != null){
                    temp = temp.right;
                }
                temp.right = right;//？？要不要放最后一行？

                flatten3(root.right);
            }
            /**
             后续遍历、中序遍历代码都简洁
             自有解 偏偏走了个前序遍历，还是三个入参，代码调试非常麻烦
             据说此题与 k个一组翻转链表 思路类似？
             */
            public static void main(String[] args) {
                TreeNode node1 = new TreeNode(1);
                TreeNode node2 = new TreeNode(2);
                TreeNode node3 = new TreeNode(3);
                TreeNode node4 = new TreeNode(4);
                TreeNode node5 = new TreeNode(5);
                TreeNode node6 = new TreeNode(6);
                node1.left = node2;
                node1.right = node5;
                node2.left = node3;
                node2.right = node4;
                node5.right = node6;
                flatten2(node1);
                TreeNode root = node1;
                while (root != null){
                    System.out.print(root.val + " - ");
                    root = root.right;
                }
            }
        }

        /**
         # 654 最大二叉树
         用一个不含重复元素的整数数组，构建最大二叉树：
         1. 二叉树的跟是数组中的最大元素
         2. 左子树是通过数组中最大值左边部分构造出的最大二叉树
         3. 右子树是通过数组中最大值右边部分构造出的最大二叉树
         构造这个二叉树并输出根结点
         示例：3 2 1 6 0 5
         结果：      6
                3       5
                 2    0
                   1
         */
        static class BuildBiggestBinaryTree{
            static TreeNode constructMaximumBinaryTree(int[] nums){
                if (nums == null || nums.length == 0){
                    return null;
                }
                int maxIndex = 0;
                for (int i = 0; i < nums.length; i++) {
                    if (nums[maxIndex] < nums[i])
                        maxIndex = i;
                }
                TreeNode root = new TreeNode(nums[maxIndex]);
                if (nums.length == 1) return root;
                int[] leftNums = Arrays.copyOfRange(nums, 0, maxIndex);
                int[] rightNums = Arrays.copyOfRange(nums, maxIndex+1,nums.length);
                TreeNode left = constructMaximumBinaryTree(leftNums);
                TreeNode right = constructMaximumBinaryTree(rightNums);
                root.left = left;root.right = right;
                return root;
            }
            static TreeNode contrustMaxmiumBinaryTreeHelper(int[] nums, int low, int high){
                if (low > high) return null;
                if (low == high) return new TreeNode(nums[low]);
                int maxIndex = low;
                for (int i = low; i <= high; i++) {
                    if (nums[maxIndex] < nums[i]) maxIndex = i;
                }
                TreeNode root = new TreeNode(nums[maxIndex]);
                TreeNode left = contrustMaxmiumBinaryTreeHelper(nums, low, maxIndex - 1);
                TreeNode right = contrustMaxmiumBinaryTreeHelper(nums, maxIndex+1, high);
                root.left = left; root.right = right;
                return root;
            }
            public static void main(String[] args) {
                int[] nums = {3,2,1,6,0,5};
                int[] nums2 = {3,2,1};
                TreeNode root = contrustMaxmiumBinaryTreeHelper(nums2, 0, nums2.length-1);
            }
        }

        /**
         # 105 从前序遍历和中序遍历序列构造二叉树
            给出前序遍历 preorder = {3,9,20,15,7}
            和  中序遍历 inorder = {9,3,15,20,7}
            返回二叉树
                3
            9       20
                  15  7
         */
        static class PreInOrderGenerateBinTree{
            //递归解法，计算index稍加优化，一步到位
            static TreeNode buildTree(int[] preorder, int[] inorder){
                return buildTreeHelper(preorder,0,preorder.length-1,inorder,0,inorder.length-1);
            }
            static TreeNode buildTreeHelper(int[] pres, int preLow, int preHigh, int[] ins, int inLow, int inHigh){
                if (preLow > preHigh) return null;
                if (preLow == preHigh) return new TreeNode(pres[preLow]);
                TreeNode root = new TreeNode(pres[preLow]);
                int rootInOrder = inLow;
                for (; rootInOrder < inHigh; rootInOrder++) {
                    if (ins[rootInOrder] == pres[preLow])
                        break;
                }
                //从preorder的剩下元素分出左右的前序遍历，rootIndex前的元素数量就是preorder前左子树的结点数量
                /*
                新的位置 3前面有 rootInOrder - inLow 个元素，
                preOrder新位置 [preLow+1,preLow+rootInOrder-inLow] [preLow+rootInOrder-inLow+1,preHigh]
                inOrder新位置 [inLow, rootInOrder-1] [rootInOrder+1,inHigh]
                 */
                int preLeftStart = preLow + 1, preLeftEnd = preLow + rootInOrder - inLow;
                int preRightStart = preLow + rootInOrder - inLow + 1, preRightEnd = preHigh;
                int inLeftStart = inLow, inLeftEnd = rootInOrder - 1;
                int inRightStart = rootInOrder + 1, inRightEnd = inHigh;
                TreeNode left = buildTreeHelper(pres,preLeftStart,preLeftEnd, ins,inLeftStart,inLeftEnd);
                TreeNode right = buildTreeHelper(pres,preRightStart,preRightEnd, ins,inRightStart,inRightEnd);
                root.left = left; root.right = right;
                return root;
            }
            /*
            每次递归都要找一次根节点在inorder的哪里很麻烦
            这种解法(骚操作)难以掌握，出自discuss用户StefanPochmann
             */
            static int pre = 0, in = 0;
            static TreeNode buildTreeWithHashMap(int[] preorder, int[] inorder, long stop){
                if (pre == preorder.length) return null;
                if (inorder[in] == stop){
                    in++;
                    return null;
                }
                int rootVal = preorder[pre++];
                TreeNode root = new TreeNode(rootVal);
                root.left = buildTreeWithHashMap(preorder, inorder, rootVal);
                root.right = buildTreeWithHashMap(preorder, inorder, stop);
                return root;
            }
            /*
            上述解法性能差，可以再提升
            使用堆栈解决 - 迭代法
            性能是递归方案的2倍，但难以看懂
             */
            static TreeNode buildTreeStackSolution(int[] preorder, int[] inorder){
                if (preorder == null || preorder.length == 0) return null;
                TreeNode root = new TreeNode(preorder[0]);
                Stack<TreeNode> stack = new Stack<>();
                stack.push(root);
                int inorderIndex = 0;
                for (int i = 1; i < preorder.length; i++) {
                    int preorderVal = preorder[i];
                    TreeNode node = stack.peek();
                    if (node.val != inorder[inorderIndex]) {
                        node.left = new TreeNode(preorderVal);
                        stack.push(node.left);
                    } else {
                        //stack彻底为空发生在经过inorder的根节点root的情况下
                        while (!stack.isEmpty() && stack.peek().val == inorder[inorderIndex]) {
                            node = stack.pop();
                            inorderIndex++;
                        }
                        //堆栈弹出的最后一个node总是当前的根结点，而此时inorderIndex停留的元素如果与栈顶元素相等就是右子结点
                        node.right = new TreeNode(preorderVal);
                        stack.push(node.right);
                    }
                }
                return root;
                /*
                关键内容：对preorder的结点进行入栈stack操作，直到inorder当前的index指向的元素与栈顶元素相等
                相等了就认为左子树处理完了，要清理左子树直到把当前的root拿到，准备挂右子树
                 */
            }
            static TreeNode buildTreeWithStack(int[] preorder, int[] inorder){
                if (preorder == null || preorder.length == 0) return null;
                TreeNode root = new TreeNode(preorder[0]);
                //stack还是要以TreeNode为元素的
                Stack<TreeNode> stack = new Stack<>();
                TreeNode currNode = root;
                //root结点先入栈，后面遍历preorder就直接从1开始
                stack.push(root);
                int inorderIndex = 0;
                for (int i = 1; i < preorder.length; i++) {
                    //inorderIndex指向的元素不是直接跟preorder[i]比较，否则右结点需要挂上preorder[i+1]
                    currNode = stack.peek();
                    if (inorder[inorderIndex] != currNode.val){
                        TreeNode left = new TreeNode(preorder[i]);
                        stack.push(left);
                        currNode.left = left;
                    }else {
                        while (!stack.isEmpty() && inorder[inorderIndex] == stack.peek().val){
                            currNode = stack.pop();//currRoot要记录最后一次弹出的栈顶元素
                            inorderIndex++;
                        }
                        //inorderIndex指向的元素与preorder中的元素比较有滞后性，使用上一次入栈的栈顶元素，而不是preorder[i],否则这里挂右子树很麻烦
                        currNode.right = new TreeNode(preorder[i]);
                        stack.push(currNode.right);
                    }
                }//整个计算完毕后stack中总是剩下一个右子树的一个或多个（多代右子树）
                return root;
            }
            public static void main(String[] args) {
                int[] preorder = {3,9,20,15,7};
                int[] inorder = {9,3,15,20,7};
                int[] preorder1 = {1,2,5,4,6,7,3,8,9};
                int[] inorder1 = {5,2,6,4,7,1,8,3,9};
                TreeNode root = buildTreeWithHashMap(preorder1, inorder1, Long.MAX_VALUE);
            }
        }

        /**
         # 106 从中序遍历和后序遍历序列构造二叉树
         */
        static class InPostOrderGenerateBinTree{
            //递归解法
            static TreeNode buildTreeRecursiveSolution(int[] inorder, int[] postorder){
                return null;//懒得写了
            }
            //非递归一次迭代解法,数组索引需要完全倒过来,写过一次这种解法也能成模板代码
            static TreeNode buildTreeIterationSolution(int[] inorder, int[] postorder){
                if (postorder == null || postorder.length == 0) return null;
                TreeNode root = new TreeNode(postorder[postorder.length - 1]);
                Stack<TreeNode> stack = new Stack<>();
                stack.push(root);
                int inorderIndex = inorder.length - 1;
                TreeNode currNode = root;
                for (int i = postorder.length - 2; i > -1; i--) {
                    currNode = stack.peek();
                    TreeNode node = new TreeNode(postorder[i]);
                    if (inorder[inorderIndex] != stack.peek().val){
                        stack.push(node);
                        currNode.right = node;
                    }else {
                        while (!stack.isEmpty() && inorder[inorderIndex] == stack.peek().val){
                            inorderIndex--;
                            currNode = stack.pop();
                        }
                        currNode.left = node;
                        stack.push(node);
                    }
                }
                return root;
            }
            public static void main(String[] args) {
                int[] inorder = {9,3,15,20,7};
                int[] postorder = {9,15,7,20,3};
                int[] inorder1 = {5,2,6,4,7,1,8,3,9};
                int[] postorder1 = {5,6,7,4,2,8,9,3,1};
                TreeNode root = buildTreeIterationSolution(inorder1, postorder1);
            }
        }

        /**
         # ??? 从前序遍历和后序遍历序列构造二叉树
         拒绝解答，没有中序遍历无法唯一确定一棵二叉树
         */

        /**
         # 652 寻找重复的子树
         给定一棵二叉树，返回所有重复的子树，对于同一类的重复子树，只需返回其中任意一棵的根节点即可
         两个树重复指的是具有相同的结构及相同的结点值
                1
            2       3
         4        2  4
                4
         应返回[4,2] 单独的结点4和 子树 2-4 都存在重复
         后序遍历 ##4#2##4#2##431
         中序遍历 #4#2#1#4#2#3#4# 中序遍历与后序遍历判断方法一致？
         前序遍历 124###324###4##
                2
            1       11
         11       1
         后序遍历 ##(11)#1##1#(11)2 数字还是要加符号分割的！不然11跟1混在一起就出错了
         前序遍历 2111###111### 即 2-1-11-#-#-#-11-1-#-#-#

                0
            0       0
         0             0
                         0
         后序遍历 ##0#0####0000
         中序遍历 #0#0#0#0#0#0#
         中序遍历不能用于此题的子树判断，上述是一个反例，0   0 两棵子树的中序遍历都是#0#0#,从遍历序列无法进行区分
                                          0     0
         此题可以使用前序、后续遍历进行解答，中序遍历会出错，原因是单左子树与单右子树在中序遍历里序列是相同的，会被当作相同子树
         */
        static class FindDuplicatedBinaryTree{
            /*
            对树进行后序遍历：4242431
             */
            static List<TreeNode> findDuplicatedSubtree(TreeNode root){
                HashMap<String, Integer> memo = new HashMap<>();
                LinkedList<TreeNode> result = new LinkedList<>();
                String temp = traverse(root, memo, result);
                System.out.println(temp);
                return result;
            }
            static String traverse(TreeNode root, HashMap<String, Integer> memo, LinkedList<TreeNode> result){
                if (root == null) return "#";
                String left = traverse(root.left, memo, result);
                String right = traverse(root.right, memo, result);
                String subTree = left+","+right+","+root.val;//后续遍历，先走到最左子树，此时left right都是null，组成的subTree就是 #,#,rootVal
                //后序遍历到每个结点时，都看看当前根节点的子树的后续遍历序列subTree是否在memo中出现过
                int freq = memo.getOrDefault(subTree,0);
                if (freq == 1){
                    result.add(root);
                }
                memo.put(subTree, freq + 1);
                return subTree;//最终返回结果 #,#,4,#,2,#,#,4,#,2,#,#,4,3,1
            }
            //ERROR - 中序遍历被TestCasesAndUtils.tree4FindDuplicationAllZero这样的树推翻
            static List<TreeNode> findDupSubTreeWithInOrder(TreeNode root){
                HashMap<String, Integer> memo = new HashMap<>();
                LinkedList<TreeNode> result = new LinkedList<>();
                String temp = traverseInOrder(root, memo, result);
                System.out.println(temp);
                return result;
            }
            static String traverseInOrder(TreeNode root, HashMap<String, Integer> memo, LinkedList<TreeNode> result){
                if (root == null) return "#";
                String left = traverseInOrder(root.left, memo, result);
                String right = traverseInOrder(root.right, memo, result);
                String subTree = left+root.val+right;
                Integer count = memo.getOrDefault(subTree, 0);
                if (count == 1){
                    result.add(root);
                }
                memo.put(subTree,count+1);
                return subTree;
            }
            //前序遍历可以
            static List<TreeNode> findDupSubTreeWithPreOrder(TreeNode root){
                HashMap<String, Integer> memo = new HashMap<>();
                LinkedList<TreeNode> result = new LinkedList<>();
                String temp = traversePreOrder(root, memo, result);
                System.out.println(temp);
                return result;
            }
            static String traversePreOrder(TreeNode root, HashMap<String, Integer> memo, LinkedList<TreeNode> result){
                if (root == null) return "#";
                String left = traversePreOrder(root.left, memo, result);
                String right = traversePreOrder(root.right, memo, result);
                String subTree = root.val +"-"+ left+"-" + right;
                Integer count = memo.getOrDefault(subTree, 0);
                if (count == 1){
                    result.add(root);
                }
                memo.put(subTree, count + 1);
                return subTree;
            }

            public static void main(String[] args) {
                //可以使用 tree4FindDuplication tree4FindDuplicationAllZero树检查算法
                List<TreeNode> duplicatedSubtree = findDuplicatedSubtree(TestCases.tree4FindDuplicationAllZero);
                List<TreeNode> dupSubTreeWithInOrder = findDupSubTreeWithPreOrder(TestCases.tree4FindDuplicationAllZero);
                duplicatedSubtree = findDuplicatedSubtree(TestCases.tree4FindDuplication);
                dupSubTreeWithInOrder = findDupSubTreeWithPreOrder(TestCases.tree4FindDuplication);

            }
        }

    }
















}
