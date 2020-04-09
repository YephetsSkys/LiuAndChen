package com.interview.chengang.sort;

import com.interview.chengang.utils.ArrayUtils;

/**
 * 鸡尾酒排序原理就是
 * 从左往右排序，再从右向左排序，需要每次记录左/右的标识位
 *
 */
public class CocktailSortTest {

	public static void main(String[] args) {
		int[] array = new int[] {1,6,3,7,2,9,4,8,5,0};
		cocktailSort(array);
		ArrayUtils.print(array);
	}
	
	private static void cocktailSort(int[] array) {
		for(int i=0;i<array.length/2;i++) {
			boolean flag = true;
			//从左向右排序
			for(int j=0;j<array.length-i-1;j++) {
				if(array[j] > array[j+1]) {
					ArrayUtils.swap(array, j, j+1);
					flag = false;
				}
			}
			//从右向左排序
			for(int j=array.length-i-1;j>i;j--) {
				if(array[j] < array[j-1]) {
					ArrayUtils.swap(array, j, j-1);
					flag = false;
				}
			}
			
			if(flag) {
				break;
			}
		}
	}

}
