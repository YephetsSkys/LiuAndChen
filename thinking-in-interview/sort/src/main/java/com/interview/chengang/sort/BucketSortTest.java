package com.interview.chengang.sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import com.interview.chengang.utils.ArrayUtils;

/**
 * 桶排序，可以排序小数的数字。。是将一组数字除最后一个数外，1 到 n-1数字分成N个桶，然后对N个桶进行排序，最后输出即是有序的数组.
 * 最后一个数放在最后一个桶中。
 */
public class BucketSortTest {
	
	public static void main(String[] args) {
		double[] array = new double[] {99.9,90,97,94,93,95,98,99.1,92};
		ArrayUtils.print(sort(array));
	}
	
	private static double[] sort(double[] array) {
		//1.计算出最大值和最小值
		double min = array[0], max = array[0];
		for(int i=0;i<array.length;i++) {
			if(array[i] > max) {
				max = array[i];
			} else if(array[i] < min) {
				min = array[i];
			}
		}
		//2. 计算差值，求出桶的数量。并初始化桶
		double d = max - min;
		int backetNum = array.length/4;//这里的代码感觉桶的计算还可以优化
		ArrayList<LinkedList<Double>> bucketList = new ArrayList<>(0);
		for(int i=0;i<backetNum;i++) {
			bucketList.add(new LinkedList<>());
		}
		//3. 将数据放入各自的桶中
		for(int i=0;i<array.length;i++) {
			// 这里如果是最后一个数字的话 array[i]-min = d
			// 否则计算出差值根据一定比例，放入到桶中
			int num = (int)((array[i]-min) * (backetNum - 1)/d);
			bucketList.get(num).add(array[i]);
		}
		//4. 将每个桶中的数据进行排序
		for(LinkedList<Double> list : bucketList) {
			Collections.sort(list);
		}
		//5. 将数据组成新的数据
		double[] sortArray = new double[array.length];
		int index = 0;
		for(LinkedList<Double> list : bucketList) {
			for(double element : list) {
				sortArray[index++] = element;
			}
		}
		return sortArray;
	}

}
