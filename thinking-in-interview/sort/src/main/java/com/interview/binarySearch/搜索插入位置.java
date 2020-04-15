package com.interview.binarySearch;

/**
 * @Author: liuman
 * @Date: 2020-04-15 11:01
 * @Descript:
 *
给定一个排序数组和一个目标值，在数组中找到目标值，并返回其索引。如果目标值不存在于数组中，返回它将会被按顺序插入的位置。

你可以假设数组中无重复元素。

示例 1:

输入: [1,3,5,6], 5
输出: 2
示例 2:

输入: [1,3,5,6], 2
输出: 1
示例 3:

输入: [1,3,5,6], 7
输出: 4
示例 4:

输入: [1,3,5,6], 0
输出: 0
 */
public class 搜索插入位置 {

    public int searchInsert(int[] nums, int target) {
        int begin = 0;
        int end = nums.length;

        int result = 0;
        while (begin < end) {
            int mid = (begin+end) >> 1;
            if (target < nums[mid]) {
                result = end = mid;
            } else if (target > nums[mid]) {
                result = begin = mid+1;
            } else {
                result = mid;
                break;
            }
        }
        return result;
    }
}
