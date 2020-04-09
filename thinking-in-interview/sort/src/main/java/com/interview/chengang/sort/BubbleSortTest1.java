package com.interview.chengang.sort;

import com.interview.chengang.utils.ArrayUtils;

/**
 * 冒泡排序1
 *
 */
public class BubbleSortTest1 {
	
	public static void main(String[] args) {
		int[] array = new int[] {1,6,3,7,2,9,4,8,5};
		bubbleSort(array);
		ArrayUtils.print(array);
	}
	
	public static void bubbleSort(int[] array) {
		for(int i=0;i<array.length-1;i++) {
			for(int j=0;j<array.length-i-1;j++) {
				if(array[j] > array[j+1]) {
					ArrayUtils.swap(array, j, j+1);
				}
			}
		}
	}

}
