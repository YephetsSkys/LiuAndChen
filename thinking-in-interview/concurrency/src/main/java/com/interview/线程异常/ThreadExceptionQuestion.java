package com.interview.线程异常;

/**
 * join的详细解释：https://blog.csdn.net/mikechenliang/article/details/100808005
 * @Author: liuman
 * @Date: 2020-04-17 10:51
 * @Descript: 获取线程结束时的异常信息
 */
public class ThreadExceptionQuestion {

    public static void main(String[] args) throws InterruptedException {

        //Thread类内部的接口
        // public interface UncaughtExceptionHandler
        // void uncaughtException(Thread t, Throwable e);
        Thread.setDefaultUncaughtExceptionHandler((thread,throwable) ->{
            System.out.printf("线程[%s] 遇到了异常,详细信息: %s\n",thread.getName(),throwable.getMessage());
        });

        Thread t1 = new Thread(() -> {
            throw new RuntimeException("liuman测试异常");
        },"t1");

        t1.start();
        //直到 t1线程执行完毕 才释放锁
        t1.join();

        //如果t1.join执行的话，这一行代码要在异常打印信息之后,
        // 如果注释 t1.join() 的话 则先打印
        System.out.println("alive of t1 :" + t1.isAlive());
    }
}
