package com.interview.线程依次输出;

import java.io.IOException;

public class ThreadOneCi {

    private static final Object WAIT_OBJECT = new Object();

    private static int count = 0;

    public static void main(String[] args) throws IOException {
        Thread s1 = new Thread(new ThreadAble());
        Thread s2 = new Thread(new ThreadAble());

        s1.start();
        s2.start();
    }

    private static class ThreadAble implements Runnable {

        public ThreadAble() {

        }

        @Override
        public void run() {
            while(true) {
                // 如果被唤醒，超过100不要往下走了
                if(count >= 100) {
                    break;
                }
                synchronized (WAIT_OBJECT) {
                    System.out.println(Thread.currentThread().getName() + "-" + ++count);
                    try {
                        WAIT_OBJECT.notify();
                        // 如果超过100则不要再等待了
                        if(count >= 100) {
                            break;
                        }
                        WAIT_OBJECT.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
