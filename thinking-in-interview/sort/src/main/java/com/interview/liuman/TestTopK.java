package com.interview.liuman;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * @Author: liuman
 * @Date: 2020-04-19 18:31
 * @Descript: Top K问题
 */
public class TestTopK {

    public static void main(String[] args) {
        int input[] = {10,12,9,11,23,51,8,89,10,21,1,3,4};
        int k = 5;
        List<Integer> result = solutionByHeap(input,k);
        for (Integer element : result) {
            System.out.print(element + " ");
        }
    }

    public static List<Integer> solutionByHeap(int[] input, int k) {
        List<Integer> list = new ArrayList<>();
        if (k > input.length || k == 0) {
            return list;
        }
        Queue<Integer> queue = new PriorityQueue<>();
        for (int num : input) {
            if (queue.size() < k) {
                System.out.println("1");
                queue.add(num);
            } else if (queue.peek() > num){//大于num是top k小的，小于num是top k大的
                System.out.println("2");
                queue.poll();
                queue.add(num);
            }
        }
        while (k-- > 0) {
            list.add(queue.poll());
        }
        return list;
    }
}
