package com.interview.线程创建;

/**
 * @Author: liuman
 * @Date: 2020-04-17 10:12
 * @Descript: 线程创建方式
 */
public class ThreadCreationQuestion {

    public static void main(String[] args) {
        //Runnable方式
        Thread thread = new Thread(() -> {},"子线程1");
    }

    //继承Thread类
    private static class MyThread extends Thread {
        @Override
        public void run() {
            super.run();
        }
    }
}
