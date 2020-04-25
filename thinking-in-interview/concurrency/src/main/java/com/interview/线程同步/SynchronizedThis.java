package com.interview.线程同步;

public class SynchronizedThis {

    public static void main(String[] args) {

        ThisLock thisLock = new ThisLock();
        new Thread("T1") {
            @Override
            public void run() {
                thisLock.m1();
            }
        }.start();

        new Thread("T2") {
            @Override
            public void run() {
                thisLock.m2();
            }
        }.start();
    }
}

class ThisLock {

    private final Object LOCK = new Object();

    public void m1() {
        synchronized (LOCK) {
            try {
                System.out.println(Thread.currentThread().getName());
                Thread.sleep(3_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void m2() {
        synchronized (LOCK) {
            try {
                System.out.println(Thread.currentThread().getName());
                Thread.sleep(3_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
