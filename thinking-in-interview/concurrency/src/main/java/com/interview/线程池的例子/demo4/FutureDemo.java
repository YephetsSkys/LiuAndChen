package com.interview.线程池的例子.demo4;

import java.util.concurrent.*;

/*
    练习异步计算结果
 */
public class FutureDemo {
    public static void main(String[] args) throws Exception {
        //1:获取线程池对象
        ExecutorService es = Executors.newCachedThreadPool();
        //2:创建Callable类型的任务对象
        Future<Integer> f = es.submit(new MyCall(1, 1));
        //3:判断任务是否已经完成
        //test1(f);
        boolean b = f.cancel(true);
        //System.out.println("取消任务执行的结果:"+b);
        //Integer v = f.get(1, TimeUnit.SECONDS);//由于等待时间过短,任务来不及执行完成,会报异常
        //System.out.println("任务执行的结果是:"+v);
    }
    //正常测试流程
    private static void test1(Future<Integer> f) throws InterruptedException, ExecutionException {
        boolean done = f.isDone();
        System.out.println("第一次判断任务是否完成:"+done);
        boolean cancelled = f.isCancelled();
        System.out.println("第一次判断任务是否取消:"+cancelled);
        Integer v = f.get();//一直等待任务的执行,直到完成为止
        System.out.println("任务执行的结果是:"+v);
        boolean done2 = f.isDone();
        System.out.println("第二次判断任务是否完成:"+done2);
        boolean cancelled2 = f.isCancelled();
        System.out.println("第二次判断任务是否取消:"+cancelled2);
    }
}
class MyCall implements Callable<Integer>{
    private int a;
    private int b;
    //通过构造方法传递两个参数

    public MyCall(int a, int b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public Integer call() throws Exception {
        String name = Thread.currentThread().getName();
        System.out.println(name+"准备开始计算...");
        Thread.sleep(2000);
        System.out.println(name+"计算完成...");
        return a+b;
    }
}