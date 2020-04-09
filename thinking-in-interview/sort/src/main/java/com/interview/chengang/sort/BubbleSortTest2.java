package com.interview.chengang.sort;

import com.interview.chengang.utils.ArrayUtils;

/**
 * 冒泡排序2（优化版本）
 *
 */
public class BubbleSortTest2 {
	
	public static void main(String[] args) {
		int[] array = new int[] {1,6,3,7,2,9,4,8,5};
		bubbleSort(array);
		ArrayUtils.print(array);
	}
	
	public static void bubbleSort(int[] array) {
		for(int i=0;i<array.length-1;i++) {
			//如果已经有序了，则不需要再进行任何排序了，减少了无用的循环。
			boolean flag = true;
			for(int j=0;j<array.length-i-1;j++) {
				if(array[j] > array[j+1]) {
					ArrayUtils.swap(array, j, j+1);
					flag = false;
				}
			}
			if(flag) {
				break;
			}
		}
	}

}
