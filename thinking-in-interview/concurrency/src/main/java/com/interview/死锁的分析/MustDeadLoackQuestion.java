package com.interview.死锁的分析;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * @Author: liuman
 * System.out.printf的应用:https://www.cnblogs.com/dalingxuan/p/9357404.html
 * @Date: 2020-04-17 12:03
 * @Descript: 必然发生死锁的问题分析
 * 1、命令方式：jps获得pid；jstack pid获得死锁信息
 * 2、代码方式检查:ThreadMXBean
 * 3、线上发生死锁应该怎么办:
 *
 */
public class MustDeadLoackQuestion implements Runnable{

    public int flag;

    static Object o1 = new Object();
    static Object o2 = new Object();

      //如果不是 static的就不会变成死锁
//    Object o1 = new Object();
//    Object o2 = new Object();

    @Override
    public void run() {
        System.out.printf("线程[%s],的flag为[%o]\n",Thread.currentThread().getName(),flag);
        System.out.println();
        if (flag == 1) {
            synchronized (o1) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (o2) {
                    System.out.printf("线程[%s]获得了两把锁",1);
                    System.out.println();
                }
            }
        }

        if (flag == 2) {
            synchronized (o2) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (o1) {
                    System.out.printf("线程[%s]获得了两把锁",2);
                    System.out.println();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MustDeadLoackQuestion r1 = new MustDeadLoackQuestion();
        MustDeadLoackQuestion r2 = new MustDeadLoackQuestion();
        r1.flag = 1;
        r2.flag = 2;
        Thread t1 = new Thread(r1,"t1");
        Thread t2 = new Thread(r2,"t2");
        t1.start();
        t2.start();

        //代码方式检查死锁
        Thread.sleep(1000);
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long[] deadLoackedThreads = threadMXBean.findDeadlockedThreads();
        if (deadLoackedThreads != null && deadLoackedThreads.length > 0) {
            for (int i=0; i < deadLoackedThreads.length; i++) {
                ThreadInfo threadInfo = threadMXBean.getThreadInfo(deadLoackedThreads[i]);
                System.out.printf("线程id为[%s],线程名称为[%s]的线程已经发生死锁,需要的锁正被线程[%s]持有\n",
                                    threadInfo.getThreadId(),threadInfo.getThreadName(),threadInfo.getLockOwnerName());
            }
        }
    }
}
