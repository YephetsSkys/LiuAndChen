package com.interview.线程状态;

/**
 * @Author: liuman
 * @Date: 2020-04-17 10:24
 * @Descript: 线程状态问题
 */
public class ThreadStateQuestion {

    public static void main(String[] args) {
        //NEW状态
        Thread thread = new Thread(()-> {
            System.out.printf("线程[%s] 正在执行...",Thread.currentThread().getName());
        },"子线程1");

        //NEW -> Runnable
        thread.start();

        //这行代码先执行 isAlive是native方法,需要JVM底层才能销毁java线程，
        // java代码没办法销毁线程
        System.out.printf("线程[%s] 是否还活着: %s\n",thread.getName(),thread.isAlive());
    }
}
