package com.interview.chengang.sort;

import com.interview.chengang.utils.ArrayUtils;

/**
 * 选择排序，只是稍微比冒泡性能好一些
 * 每次将最大的值直接放到最后
 *
 */
public class SelectSortTest {
	
	public static void main(String[] args) {
		int[] array = new int[] {1,6,3,7,2,9,4,8,5};
		sort(array);
		ArrayUtils.print(array);
	}
	
	private static void sort(int[] array) {
		for(int i=array.length-1;i>0;i--) {
			int max = i;
			for(int j=i-1;j>=0;j--) {
				if(array[max] < array[j]) {
					max = j;
				}
			}
			if(max != i) {
				ArrayUtils.swap(array, i, max);
			}
		}
	}

}
