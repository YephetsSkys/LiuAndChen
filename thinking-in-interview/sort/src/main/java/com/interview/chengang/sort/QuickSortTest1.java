package com.interview.chengang.sort;

import com.interview.chengang.utils.ArrayUtils;

/**
 * 快排单边循环排序。
 * 就是每次找基准元素后，单循环到startIndex,endIndex之间来判断。最后将标志位赋值为基准元素即可。
 *
 */
public class QuickSortTest1 {
	
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
		int mark = startIndex;
		for(int i=startIndex+1;i<=endIndex;i++) {
			if(povite > array[i]) {
				++mark;
				ArrayUtils.swap(array, i, mark);
			}
		}
		//最后将mark指向的元素与povite交换
		ArrayUtils.swap(array, mark, startIndex);
		return mark;
	}

}
