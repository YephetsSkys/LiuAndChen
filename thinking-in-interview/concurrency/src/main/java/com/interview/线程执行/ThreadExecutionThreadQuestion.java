package com.interview.线程执行;

/**
 * @Author: liuman
 * @Date: 2020-04-17 10:22
 * @Descript: 当有线程 T1、T2 以及 T3，如何实现 T1 -> T2 -> T3 的执行顺序？
 */
public class ThreadExecutionThreadQuestion {

    public static void main(String[] args) throws InterruptedException {
        joinOneByOne();
    }

    private static void joinOneByOne() throws InterruptedException {
        //lambda表达式的方式创建Runnable
        Thread t1 = new Thread(ThreadExecutionThreadQuestion::action,"t1");
        Thread t2 = new Thread(ThreadExecutionThreadQuestion::action,"t2");
        Thread t3 = new Thread(ThreadExecutionThreadQuestion::action,"t3");

        //start() 仅是通知线程启动 Thread内部枚举类State public enum State
        // NEW -> RUNNABLE
        t1.start();
        //        if (millis == 0) {
        //            while (isAlive()) {
        //                wait(0);
        //            }
        //        }
        //Thread类中join默认 join(0); 就是millis == 0；通过while自旋转的方式直到线程不是
        //public final native boolean isAlive();
        //Object public final native void wait(long timeout) throws InterruptedException;
        t1.join();

        t2.start();
        t2.join();

        t3.start();
        t3.join();
    }

    private static void action() {
        System.out.printf("线程[%s] 正在执行",Thread.currentThread().getName());
        System.out.println();
    }
}
