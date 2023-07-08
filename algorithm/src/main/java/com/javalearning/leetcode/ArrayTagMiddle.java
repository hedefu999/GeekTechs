package com.javalearning.leetcode;

import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ArrayTagMiddle {
    /**
     * 421 数组中两个数的最大异或值
     * 利用哈希集合存储按位前缀。
     * 利用字典树存储按位前缀
     * XOR的基本特性：x XOR 0 = x, x XOR x = x;
     * 二进制数转十进制过程中的一个特性 2^n > Sum(2^0,...,2^(n-1)) = 2^n - 1; 所以高位为1抵得上余下所有位都为1
     * O(N) 不是指只能遍历一遍。。。
     */
    /*
     * 解法一
     * 算法思路
     * 一堆二进制数两两异或，怎样为最大的？
     * 自然是从最高位开始看，哪两个可以异或成1
     * 所以就是将所有数右移过i位后看看能否有两个数XOR成1，*2+1，i是从最大长度-1到0
     *
     *
     * 代码思路
     * 先找到最大的数，这样高位会为1，计算二进制长度 L。
     * 注意：此算法并不依赖最大数进行异或运算，最大数仅用于确定最长bit数，像{10,8,2}这样就确定L=4
     * 初始化 max_xor = 0。
     * 从最左侧的比特位遍历到最右侧的比特位（将数字右移不同位数）：
     * max_xor * 2
     * 初始化 curr_xor = max_xor | 1（max_xor想要更大自然是最右位置为1了）。
     * 遍历 nums，数字右移当前i位，计算出长度为 L - i 的所有可能的按位前缀，放到hashset中（p集合）。
     * 遍历所有可能的按位前缀，检查是否存在 p1，p2 使得 p1^p2 == curr_xor（显然这里是每次都重新计算了逐步增长bit位的两个数）。
     * 集合p中是否存在p1^p2=curr_xor --> p1^p2^p2=curr_xor^p2 --> p1=curr_xor^p2(curr_xor不为0) ==> 对于所有pi curr_xor^pi是否存在于集合p中。
     * 如果存在，就将 max_xor 改为 curr_xor
     *
     * 附：
     * 1. curr_xor = max_xor | 1（max_xor想要更大自然是最右位置为1了）
     * 2. 集合p中是否存在 p1^p2=curr_xor
     *       因为 p1^p2^p2=curr_xor^p2  也就是  p1=curr_xor^p2
     *       由于curr_xor不为0，所以p1 p2不会是同一个数
     *    所以等效为：对于所有pi，curr_xor^pi是否存在于集合p中。这样就转成遍历了
     */
    public int findMaximumXOR(int[] nums) {
        int maxNum = nums[0];
        for(int num : nums) maxNum = Math.max(maxNum, num);
        //最大数字的二进制长度
        int L = (Integer.toBinaryString(maxNum)).length();
        int maxXOR = 0, currXOR;
        Set<Integer> prefixes = new HashSet<>();
        for(int i = L - 1; i > -1; --i) {
            // go to the next bit by the left shift
            maxXOR <<= 1;
            // set 1 in the smallest bit
            currXOR = maxXOR | 1;
            prefixes.clear();
            // compute all possible prefixes
            // of length (L - i) in binary representation
            for(int num: nums) prefixes.add(num >> i);
            // Update maxXor, if two of these prefixes could result in currXor.
            // Check if p1^p2 == currXor, i.e. p1 == currXor^p2.
            for(int p: prefixes) {
                if (prefixes.contains(currXOR^p)) {//maxXor|1)^p - p末位取反替换maxXOR的末位
                    maxXOR = currXOR;
                    break;
                }
            }
        }
        return maxXOR;
    }
    public int findMaximumXORError(int[] nums) {
        int maxnum = 0;
        for (int num : nums) {
            if (maxnum < num) maxnum = num;
        }
        int max_result = 0;
        //似乎有捷径：最大的数跟剩下的数逐个异或
        //1010 1000 10 最大的两个数异或是8 2，这个做法是错误的，找的不是最大数，而是二进制位数最长的数
        //但如果用 8 10 分别去异或，TC不符合
        for (int num : nums){
            if (num!=maxnum && ((maxnum^num) > max_result)){
                max_result = maxnum^num;
            }
        }
        return max_result;
    }
    public int findMaximumXORCopy(int[] nums) {
        int maxnum = 0;
        for (int num : nums) {
            if (maxnum < num) maxnum = num;
        }
        int length = 0;
        while (maxnum > 0){
            maxnum = maxnum >> 1;
            length++;
        }
        int max_result=0,current = 0;
        Set<Integer> allNumsInMoving = new HashSet<>();
        for (int i = length - 1; i > -1; i--) {
            max_result*=2;
            current = max_result+1;
            allNumsInMoving.clear();
            for (int num : nums) allNumsInMoving.add(num>>i);
            for (int numInMoving : allNumsInMoving){
                if (allNumsInMoving.contains(current ^ numInMoving)){
                    max_result = current;
                    break;//跳出最近的循环
                }
            }
        }
        return max_result;
    }

    /**
     * 算法二 逐位字典树
     * hash集合存储按位前缀无法做剪枝优化
     * 构造按位字典树，树的深度是L(最大数的二进制长度)
     * 走完所有数字，使用数字的二进制位逐步构造一棵深度为L的二叉树（子节点只能是0 1），每来一组新bit位就从根结点（非bit位）走一遍，向下补上缺少的二进制位
     * 构建树的遍历由node完成，xorNode负责在同一深度的树上找与node相反的结点，如果有就x2+1,否则仅x2，xor只会在一条分支上走下去，表示选中了一个已知的最大数进行了异或运算
     * 这样构建树的操作也符合从n个数中选两个最大的数的效果
     */
    static class OfficialSolution2{
        static class TrieNode {
            HashMap<Character, TrieNode> children = new HashMap<Character, TrieNode>();
            public TrieNode() {}
        }
        static class Solution {
            public static int findMaximumXOR(int[] nums) {
                // Compute length L of max number in a binary representation
                int maxNum = nums[0];
                for(int num : nums) maxNum = Math.max(maxNum, num);
                int L = (Integer.toBinaryString(maxNum)).length();

                // zero left-padding to ensure L bits for each number
                int n = nums.length, bitmask = 1 << L;
                String [] strNums = new String[n];
                for(int i = 0; i < n; ++i) {
                    strNums[i] = Integer.toBinaryString(bitmask | nums[i]).substring(1);
                }
                /* strNums是
                 0 = "00011"
                 1 = "01010"
                 2 = "00101"
                 3 = "11001"
                 4 = "00010"
                 5 = "01000"
                 */
                TrieNode trie = new TrieNode();
                int maxXor = 0;
                for (String num : strNums) {
                    TrieNode node = trie, xorNode = trie;
                    int currXor = 0;
                    for (Character bit : num.toCharArray()) {
                        // insert new number in trie
                        if (node.children.containsKey(bit)) {
                            node = node.children.get(bit);
                        } else {
                            TrieNode newNode = new TrieNode();
                            node.children.put(bit, newNode);
                            node = newNode;
                        }
                        // compute max xor of that new number
                        // with all previously inserted
                        Character toggledBit = bit == '1' ? '0' : '1';
                        if (xorNode.children.containsKey(toggledBit)) {
                            currXor = (currXor << 1) | 1;//左移加一，期望最大值
                            xorNode = xorNode.children.get(toggledBit);
                        } else {
                            currXor = currXor << 1;
                            xorNode = xorNode.children.get(bit);
                        }
                    }
                    maxXor = Math.max(maxXor, currXor);
                }
                return maxXor;
            }
        }
    }

    /**
     时间复杂度：O(N)O(N)。在字典树插入一个数的时间复杂度为 O(L)O(L)，找到一个数的最大异或值时间复杂度也为 O(L)O(L)。其中 L = 1 + [\log_2 M]L=1+[log
     2
     M]，M 为数组中的最大数值，这里可以当做一个常量。因此最终时间复杂度为 O(N)O(N)。

     空间复杂度：O(1)O(1)。维护字典树最多需要 O(2^L) = O(M)O(2
     L
     )=O(M) 的空间，但由于输入的限制，这里的 L 和 M 可以当做常数。
     */


    @Test
    public void test421() {
        int[] array = {3,10,5,25,2,8};
        int[] array2 = {8,10,2};
        // System.out.println(findMaximumXOR(array));
        // System.out.println(findMaximumXORCopy(array));
        System.out.println(OfficialSolution2.Solution.findMaximumXOR(array));
    }
}
