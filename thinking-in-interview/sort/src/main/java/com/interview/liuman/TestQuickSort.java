package com.interview.liuman;

/**
 * @Author: liuman
 * @Date: 2020-04-09 20:28
 * @Descript: 测试快速排序
 */
public class TestQuickSort {

    public static void main(String[] args) {
        int array[] = {7,2,3,4,10,11,7,4};
        quickSort(array);

        for (int i=0; i<array.length; i++) {
            System.out.print(array[i] + " ");
        }
    }

    private static void quickSort(int array[]) {

        //如果数组为空，就要返回
        if (array == null) return;

        int begin = 0;
        int end = array.length;
        quickSort(begin,end,array);
    }

    private static void quickSort(int begin, int end, int array[]) {
        //子序列至少要有两个元素
        if (end - begin < 2) return;

        //获得轴心点所在索引值
        int mid = pivotIndex(begin,end,array);
        quickSort(begin,mid,array);
        quickSort(mid+1,end,array);
    }

    private static int pivotIndex(int begin,int end, int array[]) {
        //end是开区间:因为每个子序列的长度都是从0开始,length-1
        end--;
        //记录轴心点的元素值
        int pivotValue = array[begin];

        //当begin和end不相等的时候
        while (begin < end) {

            //end指针向左移动 end--
            while (begin < end) {
                if (pivotValue < array[end]) {
                    end--;
                } else {//当前的end指针的值小于轴心点的值的时候需要开始移动begin指针了,并且将当前end指针对应的元素放到轴心点的左边
                    array[begin++] = array[end];
                    //跳出当前循环，暂停end指针的移动,开始begin指针的移动
                    break;
                }
            }

            //begin指针向右移动begin++
            while (begin < end) {
                //begin指针的元素小于轴心点元素的值
                if (array[begin] < pivotValue) {
                    begin++;
                } else {//当前的begin指针的值大于轴心点元素的值,则需要将当前begin指针对应的元素值移动到子序列右边
                    array[end--] = array[begin];
                    //跳出begin指针的移动,开始end指针的移动
                    break;
                }
            }
        }

        //当begin=end的时候,此时begin就是轴心点元素值放置的索引位置
        array[begin] = pivotValue;

        return begin;
    }
}
