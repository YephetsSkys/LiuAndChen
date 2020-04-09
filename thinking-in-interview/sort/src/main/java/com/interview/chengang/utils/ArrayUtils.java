package com.interview.chengang.utils;

import java.util.Arrays;

public class ArrayUtils {
	
	/**
	 * 数据交换
	 */
	public static void swap(int[] array, int i, int j) {
		int temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}
	
	/**
	 * 打印数组元素信息
	 */
	public static void print(int[] array) {
		System.out.println(Arrays.toString(array));
	}

}
