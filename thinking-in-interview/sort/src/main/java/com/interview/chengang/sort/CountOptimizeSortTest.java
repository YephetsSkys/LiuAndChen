package com.interview.chengang.sort;

import com.interview.chengang.utils.ArrayUtils;

/**
 * 计数排序优化。
 * 
 * 1.因为原来的计数排序比如 90-99只有10个数，但是也会创建出100的数组出来，空间浪费，其实只需要创建10个空间大小的数据即可。
 * 2.无法记录两个值相同的谁先后的问题，只需要在计数s数组的里面每个是前面的值的总和即可。
 * 如:三个数 95 96 96，那么计数排序数组为 2, 3，循环的时候，代表95=第三名,96,96按照原始顺序展示2,1，得到96 96 95这个顺序了。
 * 这个是一种稳定排序
 *
 */
public class CountOptimizeSortTest {
	
	public static void main(String[] args) {
		int[] array = new int[] {99,90,97,94,93,95,98,99,92};
		ArrayUtils.print(sort(array));
	}
	
	private static int[] sort(int[] array) {
		//1.计算min和max
		int min = array[0], max = array[0];
		for(int i=1;i<array.length;i++) {
			if(max < array[i]) {
				max = array[i];
			} else if(min > array[i]) {
				min = array[i];
			}
		}
		int diff = max - min;
		//2.初始化计数排序数组
		int[] countArray = new int[diff+1];
		for(int i=0;i<array.length;i++) {
			countArray[array[i]-min]++;
		}
		//3.将计数排序数组中的值设置为累加前一个元素的计数值
		for(int i=1;i<countArray.length;i++) {
			countArray[i] += countArray[i-1];
		}
		//4.排序数组数组
		int[] sortArray = new int[array.length];
		int num = 0;
		for(int i=0;i<countArray.length;i++) {
			for(int j=num;j<=countArray[i];j++) {
				sortArray[num++] = min+i;
				countArray[i]--;
			}
		}
		
		return sortArray;
	}

}
