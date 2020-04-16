package com.interview.LinkedList;

import java.util.Stack;

/**
 * @Author: liuman
 * @Date: 2020-04-16 17:45
 * @Descript:
 * 输入一个链表的头节点，从尾到头反过来返回每个节点的值（用数组返回）。
 *
 *  
 *
 * 示例 1：
 *
 * 输入：head = [1,3,2]
 * 输出：[2,3,1]
 *  
 *
 * 限制：
 *
 * 0 <= 链表长度 <= 10000
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/cong-wei-dao-tou-da-yin-lian-biao-lcof
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class 从尾到头打印链表 {

    public int[] reversePrint(ListNode head) {
        Stack<ListNode> stack = new Stack<>();
        ListNode cur = head;
        while (cur != null) {
            stack.push(cur);
            cur = cur.next;
        }
        int size = stack.size();
        int result[] = new int[size];
        for (int i = 0; i<size; i++) {
            result[i] = stack.pop().val;
        }

        return result;
    }
}
