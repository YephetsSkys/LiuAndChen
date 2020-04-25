package com.interview.线程执行;

import java.util.Optional;

/**
 * @author manliu
 * @description: 线程优先级理解 https://blog.csdn.net/qq_35400008/article/details/80219947
 *
 * Java线程的优先级从1到10级别，值越大优先级越高 但是根据机器不同也不一定
 */
public class ThreadSimpleAPI2 {

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                Optional.of(Thread.currentThread().getName() + "-Index" + i).ifPresent(System.out::println);
            }
        },"t1");
        t1.setPriority(Thread.MAX_PRIORITY);

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                Optional.of(Thread.currentThread().getName() + "-Index" + i).ifPresent(System.out::println);
            }
        },"t2");

        t2.setPriority(Thread.NORM_PRIORITY);

        Thread t3 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                Optional.of(Thread.currentThread().getName() + "-Index" + i).ifPresent(System.out::println);
            }
        },"t3");

        t3.setPriority(Thread.MIN_PRIORITY);

        t1.start();
        t2.start();
        t3.start();
    }
}
