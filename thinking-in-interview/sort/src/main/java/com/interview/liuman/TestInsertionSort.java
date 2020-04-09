package com.interview.liuman;

/**
 * @Author: liuman
 * @Date: 2020-04-09 09:29
 * @Descript: 测试插入排序
 */
public class TestInsertionSort {

    public static void main(String[] args) {
        int array[] = new int[]{19,12,3,6,9,10,3};
        insertionSort(array);

        for (int i=0; i<array.length; i++) {
            System.out.print(array[i] + " ");
        }
    }

    private static void insertionSort(int array[]) {
        //两个序列,左1是有序序列
        for (int begin=1; begin<array.length; begin++) {
            int cur = begin;
            while (cur>0 && array[cur]<array[cur-1]) {
                int temp = array[cur];
                array[cur] = array[cur-1];
                array[cur-1] = temp;
                cur--;
            }
        }
    }
}
