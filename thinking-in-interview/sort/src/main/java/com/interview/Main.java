package com.interview;

/**
 * @Author: liuman
 * @Date: 2020-04-08 17:45
 * @Descript:
 */
public class Main {

    public static void main(String[] args) {
        //测试形参
        int a =1;
        int b =2;

        System.out.println("a:" + a);
        System.out.println("b:" + b);
    }

    private static void add(int a, int b) {
        a = 10;
        b = 20;
    }
}
