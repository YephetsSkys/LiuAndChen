package com.interview.线程池的例子.demo6;

public class MyTask implements Runnable {
    //用户姓名
    private String userName;
    //取款金额
    private double money;
    //总金额
    private static double total = 1000;

    public MyTask(String userName, double money) {
        this.userName = userName;
        this.money = money;
    }

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        System.out.println(userName+"正在准备使用"+name+"取款:"+money+"元");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (MyTask.class){
            if(total-money>0){
                System.out.println(userName+"使用"+name+"取款:"+money+"元成功,余额:"+(total-money));
                total-=money;
            }else {
                System.out.println(userName+"使用"+name+"取款:"+money+"元失败,余额:"+total);
            }
        }
    }
}
