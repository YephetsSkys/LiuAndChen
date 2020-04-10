package com.interview.liuman;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: liuman
 * @Date: 2020-04-10 17:42
 * @Descript: 测试希尔排序
 */
public class TestShellSort {

    public static void main(String[] args) {
        int array[] = {13,2,56,7,2,10,1,6,7,8};
        shellSort(array);
        for (int i=0; i<array.length; i++) {
            System.out.print(array[i] + " ");
        }
    }

    private static void shellSort(int array[]) {
        if (array == null || array.length <= 1) return;

        //首先需要计算该数组拥有的步长
        List<Integer> steps = new ArrayList<>();
        int currentStep = array.length;
        //除以2的k次就是为了获得步长
        while((currentStep >>= 1) > 0) {
            steps.add(currentStep);
        }

        if (steps.size() == 0) return;

        for (Integer step : steps) {
            //根据每一个步长构建一个矩阵，row、col都是以0为开始的
            //实际的排序就是比对每一列的元素进行插入排序
            for (int col=0; col<step; col++) {
                //插入排序的原理就是以第一个元素为基准有序的序列,然后从下一个索引为begin元素开始迭代比较
                for (int begin=col+step; begin<array.length; begin+=step) {
                    //cur指针首先指向无序子序列的第一个元素索引,然后对列元素数组的进行比对，
                    //从而完成列的插入排序
                    int cur = begin;
                    while (cur>col && array[cur] <= array[cur-step]) {
                        //交换元素
                        int temp = array[cur];
                        array[cur] = array[cur-step];
                        array[cur-step] = temp;
                        cur -= step;
                    }
                }
            }
        }
    }
}
