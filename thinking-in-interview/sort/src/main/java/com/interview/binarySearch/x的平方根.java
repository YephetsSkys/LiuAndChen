package com.interview.binarySearch;

/**
 * 题解:https://blog.csdn.net/reuxfhc/article/details/80302071
 * @Author: liuman
 * @Date: 2020-04-15 11:11
 * @Descript:
 * 实现 int sqrt(int x) 函数。
 *
 * 计算并返回 x 的平方根，其中 x 是非负整数。
 *
 * 由于返回类型是整数，结果只保留整数的部分，小数部分将被舍去。
 *
 * 示例 1:
 *
 * 输入: 4
 * 输出: 2
 * 示例 2:
 *
 * 输入: 8
 * 输出: 2
 * 说明: 8 的平方根是 2.82842...,
 *      由于返回类型是整数，小数部分将被舍去。
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/sqrtx
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class x的平方根 {

    public int mySqrt(int x) {
        int low = 0, high = x;
        int ans = 0;
        while(low <= high) {
            int mid = (low + high) >> 1;
            if (mid * mid <= x) {
                ans = mid;
                low = mid + 1;
            } else if(mid * mid > x) {
                high = mid - 1;
            }
        }

        return ans;
    }
}
