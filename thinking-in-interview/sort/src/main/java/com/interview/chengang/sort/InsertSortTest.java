package com.interview.chengang.sort;

import com.interview.chengang.utils.ArrayUtils;

/**
 * 插入排序
 *
 */
public class InsertSortTest {
	
	public static void main(String[] args) {
		int[] array = new int[] {1,6,3,7,2,9,4,8,5};
		sort(array);
		ArrayUtils.print(array);
	}
	
	private static void sort(int[] array) {
		for(int i=1;i<array.length;i++) {
			if(array[i] < array[i-1]) {
				int temp = array[i];
				for(int j=i-1;j>=0 && array[j]>temp;j--) {
					ArrayUtils.swap(array, j, j+1);
				}
			}
		}
	}

}
