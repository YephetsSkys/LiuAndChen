package com.interview.liuman;

/**
 * @Author: liuman
 * @Date: 2020-04-11 15:26
 * @Descript: 测试归并排序
 */
public class TestMergeSort {

    public static void main(String[] args) {
        int array[] = new int[]{10,3,5,7,10,12,21};
        mergeSort(array);

        for (int element:array) {
            System.out.print(element + " ");
        }
    }

    private static void mergeSort(int array[]) {
        if (array == null || array.length <= 2) return;

        //定义一个拥有一半容量的左数组序列
        int leftArray[] = new int[array.length >> 1];
        mergeSort(leftArray,array,0,array.length);
    }

    /**
     * 二分分裂array,然后最后合并array
     * @param leftArray 存储根据二分后的左半部分子序列元素
     * @param array 整个待排序序列数组
     * @param begin merge的序列起始索引
     * @param end merge的序列结束索引
     */
    private static void mergeSort(int leftArray[],int array[],int begin,int end) {
        //该判断证明当前的范围标示的子序列至少要有两个元素才可以
        if (end - begin < 2) return;

        int mid = (begin+end) >> 1;
        mergeSort(leftArray,array,begin,mid);
        mergeSort(leftArray,array,mid,end);

        mergeArray(leftArray,array,begin,mid,end);
    }

    /**
     * 合并子序列，并比较数组元素
     * @param leftArray 复制待合并子序列左半部分
     * @param array 整个待排序序列
     * @param begin array合并是的起始元素索引值
     * @param mid   array合并是的二分索引值
     * @param end   array合并是的起始元素索引值
     */
    private static void mergeArray(int leftArray[],int array[],int begin,int mid,int end) {
        int li = 0;
        int le = mid - begin;
        int ri = mid;
        int re = end;
        int ai = begin;

        //备份每个子序列的左半部分序列的元素
        for (int i=li; i<le;i++) {
            leftArray[i] = array[begin+i];
        }

        //判断越界
        while (li < le) {
            if (ri < re && array[ri] < leftArray[li]) {
                array[ai++] = array[ri++];
            } else {
                array[ai++] = leftArray[li++];
            }

        }
    }

}
