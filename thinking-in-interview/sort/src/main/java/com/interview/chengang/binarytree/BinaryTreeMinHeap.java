package com.interview.chengang.binarytree;

import com.interview.chengang.utils.ArrayUtils;

/**
 * 二叉堆（最小堆）
 *
 */
public class BinaryTreeMinHeap {
	
	/**
	 * 父节点
	 * @param array - int[]
	 * @param parentIndex - 父节点
	 */
	public static void downJust(int[] array, int parentIndex) {
		//比较父节点与左右孩子哪个最小，更最小的那已给互换
		int temp = array[parentIndex];
		int childIndex = parentIndex * 2 + 1;
		while(childIndex < array.length) {
			//判断是否有右孩子，并且判断左孩子和右孩子哪个小
			if(childIndex + 1 < array.length && array[childIndex] > array[childIndex + 1]) {
				childIndex++;
			}
			//这里判断孩子是否小于父亲
			if(temp < array[childIndex]) {
				break;
			}
			ArrayUtils.swap(array, parentIndex, childIndex);
			parentIndex = childIndex;
			childIndex = parentIndex * 2 + 1;
		}
	}
	
	public static void buildHeap(int[] array) {
		// 从最后一个非叶子节点开始
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
