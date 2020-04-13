package com.interview.liuman;

/**
 * @Author: liuman
 * @Date: 2020-04-13 10:17
 * @Descript: 测试堆排序 (由小到大需要构建'最大堆')
 */
public class TestHeapSort {

    /**
     * 二叉堆对应数组排序变化的堆容量
     */
    private static int heapSize;

    public static void main(String[] args) {

        int array[] = new int[] {5,1,2,3,10,23,11};

        heapSort(array);

        for (int element : array) {
            System.out.print(element + " ");
        }
    }

    private static void heapSort(int array[]) {
        if (array == null || array.length <= 1) return;

        heapSize = array.length;

        //首先要将array数组构建成最大堆
        for (int i= (heapSize>>1)-1; i>=0 ;i--) {
            //自下而上的进行'下滤'
            siftDown(array,i);
        }

        //由于'最大堆'的根元素就是最大值，
        // 只要最大值和最后一个元素互换之后，然后将array的heapSize指针向前移动以为
        int rootIndex = 0;
        while (heapSize > 1) {
            int temp = array[rootIndex];
            array[rootIndex] = array[heapSize-1];
            array[heapSize-1] = temp;
            heapSize--;
            //恢复二叉树,因为root左右子树都是二叉堆
            siftDown(array,rootIndex);
        }
    }

    private static void siftDown(int array[],int index) {

        //获取第一个叶子节点的索引
        int half = heapSize >> 1;

        int element = array[index];

        //二叉树就是完全二叉树的数据结构，
        // 所以拥有的性质就是从上到下、从左到右索引值递增,
        // 所以小于half的都是非叶子节点
        while (index < half) {
            //此时 index就是 parent,判断其所在的值与左右孩子的值做比较
            //非叶子节点的左孩子必存在,所以默认的childIndex就是左孩子的索引值
            int childIndex = (index << 1) + 1;
            //默认是左孩子的元素值
            int child = array[childIndex];

            //右孩子索引值
            int rightChildIndex = childIndex + 1;

            //1、不能超过堆的容量大小；2、如果右孩子存在，并且右孩子的值大于左孩子的值
            if (rightChildIndex < heapSize && array[rightChildIndex] > child) {
                //那么需要与parent比对的孩子值就可以确定了,索引值也是右孩子的索引值
                child = array[childIndex = rightChildIndex];
            }

            //如果下滤的index元素大于两个孩子的值,则不交换
            if (element >= child) return;

            //如果index的元素值小于孩子的值，向下交换索引值,直到最后一个符合条件的非叶子节点
            array[index] = child;
            index = childIndex;

        }
        //下滤的元素如果比孩子节点下则往索引值大的地方存放,元素值大的元素向索引值小的上浮
        array[index] = element;

    }
}
