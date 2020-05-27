package com.interview.线程池的例子.demo6;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class MyTest {
    public static void main(String[] args) {
        //1:创建线程池对象
        ExecutorService pool = Executors.newFixedThreadPool(2, new ThreadFactory() {
            int id = 1;

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "ATM" + id++);
            }
        });
        //2:创建两个任务并提交
        for (int i = 1; i <=2 ; i++) {
            MyTask myTask = new MyTask("客户" + i, 800);
            pool.submit(myTask);
        }
        //3:关闭线程池
        pool.shutdown();
    }
}
