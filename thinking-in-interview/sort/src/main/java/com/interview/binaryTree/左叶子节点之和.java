package com.interview.binaryTree;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @Author: liuman
 * @Date: 2020-04-14 19:54
 * @Descript:
 * 计算给定二叉树的所有左叶子之和。
 *
 * 示例：
 *
 *     3
 *    / \
 *   9  20
 *     /  \
 *    15   7
 *
 * 在这个二叉树中，有两个左叶子，分别是 9 和 15，所以返回 24
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/sum-of-left-leaves
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class 左叶子节点之和 {

    public int sumOfLeftLeaves(TreeNode root) {
        if (root == null) return 0;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);

        int sum = 0;
        while (!queue.isEmpty()) {
            TreeNode curNode = queue.poll();
            if (curNode.left != null) {
                if (curNode.left.left == null && curNode.left.right == null)
                    sum += curNode.left.val;
                queue.add(curNode.left);
            }

            if (curNode.right != null) {
                queue.add(curNode.right);
            }
        }
        return sum;
    }
}
