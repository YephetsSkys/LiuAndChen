package com.interview.chengang.sort;

import com.interview.chengang.utils.ArrayUtils;

/**
 * 计数排序，不适合数字比较大的数据排序
 *
 */
public class CountSortTest {
	
	public static void main(String[] args) {
		int[] array = new int[] {1,6,3,7,2,9,4,8,5};
		ArrayUtils.print(sort(array));
	}
	
	private static int[] sort(int[] array) {
		// 1.首先查询出最大的值
		int max = array[0];
		for(int i=1;i<array.length;i++) {
			if(max < array[i]) {
				max = array[i];
			}
		}
		// 2.初始化一个max+1长度的数组
		int[] a = new int[max+1];
		// 3.循环将下标的值赋值
		for(int i=0;i<array.length;i++) {
			a[array[i]]++;
		}
		// 4.生成新的数组
		int[] newArray = new int[array.length];
		int index = 0;
		for(int i=0;i<a.length;i++) {
			for(int j=0;j<a[i];j++) {
				newArray[index++] = i;
			}
		}
		return newArray;
	}

}
