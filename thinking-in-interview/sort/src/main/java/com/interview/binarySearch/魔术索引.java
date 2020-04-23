package com.interview.binarySearch;

/**
 * @Author: liuman
 * @Date: 2020-04-23 18:46
 * @Descript:
 * 魔术索引。 在数组A[0...n-1]中，有所谓的魔术索引，满足条件A[i] = i。
 * 给定一个有序整数数组，编写一种方法找出魔术索引，若有的话，在数组A中找出一个魔术索引，如果没有，则返回-1。若有多个魔术索引，返回索引值最小的一个。
 *
 * 示例1:
 *
 *  输入：nums = [0, 2, 3, 4, 5]
 *  输出：0
 *  说明: 0下标的元素为0
 * 示例2:
 *
 *  输入：nums = [1, 1, 1]
 *  输出：1
 * 提示:
 *
 * nums长度在[1, 1000000]之间
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/magic-index-lcci
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class 魔术索引 {

    public static void main(String[] args) {
        //array的前提是必须有序
//        int array[] = {0,2,4,5};
        int array[] = {1,1,2,3};
        int magicIndex = findMagicIndex(array);
        System.out.println("magicIndex:" + magicIndex);
    }

    public static int findMagicIndex(int[] nums) {
        int left = 0, right = nums.length;

        while (left < right) {

            //两行代码 都是获取一段数组的中间位置索引
//            int mid = left + (right - left) / 2;
            int mid = (right + left) >> 1;
            //如果当前中位索引的值不等于下标索引值,
            if (nums[mid] != mid) {
                //
                if (nums[mid] >= 0) {
                    right = mid;
                } else {
                    left = mid + 1;
                }
            } else {
                left = mid + 1;
            }
        }
        return left - 1;
    }
}
