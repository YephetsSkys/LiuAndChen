package com.interview.chengang.sort;

import com.interview.chengang.utils.ArrayUtils;

/**
 * 快排双边循环排序。
 * 理论是，一个从左往右循环，一个从右往前循环，记录两个标志位，left,right，直到两个不满足，则交换left和right的值
 *
 */
public class QuickSortTest2 {
	
	public static void main(String[] args) {
		int[] array = new int[] {1,6,3,7,2,9,4,8,5,0};
		quick(array, 0, array.length - 1);
		ArrayUtils.print(array);
	}
	
	private static void quick(int[] array, int startIndex, int endIndex) {
		if(startIndex >= endIndex) {
			return;
		}
		int poviteIndex = partition(array, startIndex, endIndex);
		quick(array, startIndex, poviteIndex - 1);
		quick(array, poviteIndex + 1, endIndex);
	}
	
	/**
	 * 排序并获取到分割的坐标值
	 * @param array
	 * @param startIndex
	 * @param endIndex
	 * @return int
	 */
	private static int partition(int[] array, int startIndex, int endIndex) {
		//默认获取第一个元素为基准元素
		int povite = array[startIndex];
		int left = startIndex,right=endIndex;
		while(left != right) {
			//先右侧循环
			while(left < right && array[right] > povite) {
				right--;
			}
			//再左侧循环
			while(left < right && array[left] <= povite) {
				left++;
			}
			if(left < right) {
				ArrayUtils.swap(array, left, right);
			}
		}
		
		ArrayUtils.swap(array, startIndex, left);
		
		return left;
	}

}
