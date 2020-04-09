package com.interview.chengang.sort;

import com.interview.chengang.utils.ArrayUtils;

/**
 * 冒泡排序3，优化版本，可能在排序的时候，我们比如
 * 9 1 2 3 4 5 6 7 8，第一次循环9已经在后面了变更为 1 2 3 4 5 6 7 8 9，后面再次循环的时候1-8已经是有序的了，没必要再循环了。。可以直接输出排序结果了
 * 原理就在于我们需要每次记录住排序交换的最后的边界值即可。
 *
 */
public class BubbleSortTest3 {

	public static void main(String[] args) {
		int[] array = new int[] {1,6,3,7,2,9,4,8,5};
		bubbleSort(array);
		ArrayUtils.print(array);
	}
	
	public static void bubbleSort(int[] array) {
		//最后一次交换的位置
		int lastExchangeIndex = 0;
		//无序的边界
		int borderSort = array.length-1;
		for(int i=0;i<array.length-1;i++) {
			boolean flag = true;
			for(int j=0;j<borderSort;j++) {
				if(array[j] > array[j+1]) {
					flag = false;
					ArrayUtils.swap(array, j, j+1);
					lastExchangeIndex = j;
				}
			}
			
			borderSort = lastExchangeIndex;
			if(flag) {
				break;
			}
		}
	}

}
