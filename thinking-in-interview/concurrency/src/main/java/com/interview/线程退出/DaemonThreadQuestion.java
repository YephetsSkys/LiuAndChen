package com.interview.线程退出;

/**
 * 守护线程的理解: https://www.jianshu.com/p/6b68af3e5738
 * @Author: liuman
 * @Date: 2020-04-17 11:13
 * @Descript: 线程退出时,守候子线程的例子
 * 用户线程退出后，守护线程立即退出
 */
public class DaemonThreadQuestion {

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {

            try {
                //在小于主线程的休眠时间就会执行 守护线程的内容
                Thread.sleep(4000);
                //在大于主线程的休眠时间就不会执行 守护线程的内容 直接退出
//                Thread.sleep(40000);
                System.out.println("liuman test daemon");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"daemon");

        t1.setDaemon(true);
        t1.start();
//        t1.join();

        System.out.println("main 线程是用户线程,t1是为main服务的守护线程");
        Thread.sleep(5000L);
        System.out.println("main线程执行完毕");
    }

}
