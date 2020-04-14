package com.interview.binaryTree;

import java.util.ArrayList;
import java.util.List;

/**
 * 参考思路:
 * https://blog.csdn.net/coder__666/article/details/80349039
 * https://leetcode-cn.com/problems/binary-tree-inorder-traversal/solution/dong-hua-yan-shi-94-er-cha-shu-de-zhong-xu-bian-li/
 * @Author: liuman
 * @Date: 2020-04-14 19:59
 * @Descript:
 * 给定一个二叉树，返回它的中序 遍历。
 *
 * 示例:
 *
 * 输入: [1,null,2,3]
 *    1
 *     \
 *      2
 *     /
 *    3
 *
 * 输出: [1,3,2]
 * 进阶: 递归算法很简单，你可以通过迭代算法完成吗？
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/binary-tree-inorder-traversal
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class 二叉树的中序遍历 {

    private List<Integer> result = new ArrayList<>();

    public List<Integer> inorderTraversal(TreeNode root) {
        if (root == null) return result;
        if (root.left != null) {
            inorderTraversal(root.left);
        }

        if (root != null) {
            result.add(root.val);
        }

        if (root.right != null) {
            inorderTraversal(root.right);
        }

        return result;
    }
}


class TreeNode//节点结构
{
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int value) {
        this.val = value;
    }
}
