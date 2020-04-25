package com.interview.线程退出;

public class DaemonThread {

    public static void main(String[] args) throws InterruptedException {

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    System.out.println(Thread.currentThread().getName() + " running");
                    Thread.sleep(100000);
                    System.out.println(Thread.currentThread().getName() + " done.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }; //new
        t.start();
        t.setDaemon(true);
        //runnable  ->running| ->dead| ->blocked


        Thread.sleep(5_000);   //JDK1.7
        System.out.println(Thread.currentThread().getName());
    }
}

/**
 * A<---------------------------------->B
 *  ->daemonThread(health check)
 * */