package com.interview.liuman;

/**
 * @Author: liuman
 * @Date: 2020-04-09 09:04
 * @Descript: 测试冒泡排序
 */
public class TestBubbleSort {

    public static void main(String[] args) {
        int array[] = {10,4,6,8,10,21};
        bubbleSort(array);

        for (int i=0; i<array.length; i++) {
            System.out.print(array[i] + " ");
        }
    }

    private static void bubbleSort(int array[]) {
        for (int end=array.length-1; end>0; end--) {
            for (int begin=1; begin<=end; begin++) {
                if (array[begin] < array[begin-1]) {
                    int temp = array[begin-1];
                    array[begin-1] = array[begin];
                    array[begin] = temp;
                }
            }
        }
    }
}
