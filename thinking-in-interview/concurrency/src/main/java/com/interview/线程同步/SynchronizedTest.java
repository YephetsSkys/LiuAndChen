package com.interview.线程同步;

public class SynchronizedTest {

    private final static Object LOCK = new Object();

    public static void main(String[] args) {

        Runnable runnable = () -> {
            synchronized (LOCK) {
                try {
                    System.out.println(Thread.currentThread().getName());
                    Thread.sleep(2_000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread t1 = new Thread(runnable);
        Thread t2 = new Thread(runnable);
        Thread t3 = new Thread(runnable);
        t1.start();
        t2.start();
        t3.start();

    }
}
