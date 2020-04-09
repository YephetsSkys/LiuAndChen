package com.interview.chengang.binarytree;

import com.interview.chengang.utils.ArrayUtils;

/**
 * 二叉堆（最大堆）
 *
 */
public class BinaryTreeMaxHead {
	
	/**
	 * 节点下沉
	 * @param array
	 * @param parentIndex
	 */
	public static void downJust(int[] array, int parentIndex) {
		int temp = array[parentIndex];
		int childIndex = parentIndex * 2 + 1;
		while(childIndex < array.length) {
			//这里判断左右孩子哪个大
			while(childIndex + 1 < array.length && array[childIndex + 1] > array[childIndex]) {
				childIndex++;
			}
			if(temp > array[childIndex]) {
				break;
			}
			ArrayUtils.swap(array, parentIndex, childIndex);
			parentIndex = childIndex;
			childIndex = parentIndex * 2 + 1;
		}
	}
	
	public static void buildHeap(int[] array) {
		//定位到最后父一个节点
		for(int i=(array.length-2)/2;i>=0;i--) {
			downJust(array, i);
		}
	}
	
	public static void main(String[] args) {
		int[] array = new int[] {1,6,3,7,2,9,4,8,5,0};
		buildHeap(array);
		ArrayUtils.print(array);
	}

}
