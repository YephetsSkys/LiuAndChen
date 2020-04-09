package com.interview.liuman;

/**
 * @Author: liuman
 * @Date: 2020-04-09 09:15
 * @Descript: 测试选择排序
 */
public class TestSelectionSort {

    public static void main(String[] args) {
        int array[] = {10,8,7,6,15,30,1,2,3,2};
        selectionSort(array);

        for (int i=0; i<array.length; i++) {
            System.out.print(array[i] + " ");
        }
    }

    private static void selectionSort(int array[]) {
        for (int end=array.length-1; end>0; end--) {
            int maxIndex = 0;
            for (int begin=1; begin<=end; begin++) {
                if (array[maxIndex] <= array[begin]) {
                    //找到最大值的索引位置
                    maxIndex = begin;
                }
            }
            int temp = array[end];
            array[end] = array[maxIndex];
            array[maxIndex] = temp;
        }
    }
}
