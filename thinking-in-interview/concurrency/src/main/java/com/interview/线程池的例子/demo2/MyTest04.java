package com.interview.线程池的例子.demo2;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/*
    练习Executors获取ExecutorService,测试关闭线程池的方法;
 */
public class MyTest04 {
    public static void main(String[] args) {
        //test1();
        test2();
    }
    //练习方法newFixedThreadPool
    private static void test1() {
        //1:使用工厂类获取线程池对象
        ExecutorService es = Executors.newSingleThreadExecutor();
        //2:提交任务;
        for (int i = 1; i <=10 ; i++) {
            es.submit(new MyRunnable4(i));
        }
        //3:关闭线程池,仅仅是不再接受新的任务,以前的任务还会继续执行
        es.shutdown();
        //es.submit(new MyRunnable4(888));//不能再提交新的任务了
    }
    private static void test2() {
        //1:使用工厂类获取线程池对象
        ExecutorService es = Executors.newSingleThreadExecutor(new ThreadFactory() {
            int n=1;
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r,"自定义的线程名称"+n++);
            }
        });
        //2:提交任务;
        for (int i = 1; i <=10 ; i++) {
            es.submit(new MyRunnable4(i));
        }
        //3:立刻关闭线程池,如果线程池中还有缓存的任务,没有执行,则取消执行,并返回这些任务
        List<Runnable> list = es.shutdownNow();
        System.out.println(list);
    }
}

/*
    任务类,包含一个任务编号,在任务中,打印出是哪一个线程正在执行任务
 */
class MyRunnable4 implements Runnable{
    private  int id;
    public MyRunnable4(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        //获取线程的名称,打印一句话
        String name = Thread.currentThread().getName();
        System.out.println(name+"执行了任务..."+id);
    }

    @Override
    public String toString() {
        return "MyRunnable4{" +
                "id=" + id +
                '}';
    }
}