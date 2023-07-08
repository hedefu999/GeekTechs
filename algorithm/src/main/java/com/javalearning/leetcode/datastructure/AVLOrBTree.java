package com.javalearning.leetcode.datastructure;

public class AVLOrBTree {
    /**
     * AVL树的旋转  https://blog.csdn.net/weixin_36888577/article/details/87211314
     * //建立左旋转函数
     * void left_rotation(tree_node_t *node)
     * {
     *     tree_node_t *tmp;
     *     int tmp_key;
     *     tmp=node->left;
     *     tmp_key=node->key;
     *     node->left=node->right;
     *     node->key=node->right->key;
     *     node->right=node->left->right;
     *     node->left->right=node->left->left;
     *     node->left->left=tmp;
     *     node->left->key=tmp_key;
     * }
     * //建立右旋转函数
     * void right_rotation(tree_node_t *node)
     * {
     *     tree_node_t *tmp;
     *     int tmp_key;
     *     tmp=node->right;
     *     tmp_key=node->key;
     *     node->right=node->left;
     *     node->key=node->left->key;
     *     node->left=node->right->left;
     *     node->right->left=node->right->right;
     *     node->right->right=tmp;
     *     node->right->key=tmp_key;
     * }
     */



    /**
     * Java实现一个B+树
     */
}
