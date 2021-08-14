package com.interview.重量级锁解锁过程;

import java.util.concurrent.TimeUnit;

public class SynchronizedLockAndUnLockTest {

    public static void main(String[] args) {
        Object lock = new Object();

        Thread t3 = new Thread(() -> {
            System.out.println("Thread 3 start!!!!!!");
            synchronized (lock) {
                try {
                    System.in.read();
                    lock.wait(); // 额外验证：让其加入到_WaitSet中
                } catch (Exception ignored) {
                }
                System.out.println("Thread 3 end!!!!!!");
            }
        });

        Thread t4 = new Thread(() -> {
            System.out.println("Thread 4 start!!!!!!");
            synchronized (lock) {
                System.out.println("Thread 4 end!!!!!!");
            }
        });

        Thread t5 = new Thread(() -> {
            int a = 0;
            System.out.println("Thread 5 start!!!!!!");
            synchronized (lock) {
                a++;
                System.out.println("Thread 5 end!!!!!!");
            }
        });

        Thread t6 = new Thread(() -> {
            int a = 0;
            System.out.println("Thread 6 start!!!!!!");
            synchronized (lock) {
                a++;
                // 唤醒_EntryList
                lock.notify(); // 额外验证：默认情况下被唤醒的线程会加入到_cxq队列。
                System.out.println("Thread 6 end!!!!!!");
            }
        });

        // 如果_EntryList队列不为NULL，则会优先唤醒此队列中休眠的线程。最后将_cxq队列设置为双端队列并复制到_EntryList队列，然后再依次唤醒。

        t3.start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (Exception ignored) {

        }
        t4.start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (Exception ignored) {

        }
        t5.start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (Exception ignored) {

        }
        t6.start();
    }

}
