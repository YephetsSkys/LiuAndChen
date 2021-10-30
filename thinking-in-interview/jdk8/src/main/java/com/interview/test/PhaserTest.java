package com.interview.test;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Phaser;

public class PhaserTest {

    @Test
    public void phaserAndCountDownTest() throws Exception {
        int num = 6;
        Phaser phaser = new Phaser(num);
        Random random = new Random();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(random.nextInt(1000));
                    System.out.println(Thread.currentThread().getName() + " exit,time->" + System.currentTimeMillis());
                    //表示当前线程已到达
                    phaser.arrive();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        for (int i = 0; i < num; i++) {
            new Thread(task).start();
        }
        System.out.println("main thread start await,time->" + System.currentTimeMillis());
        //等待其他线程都到达
        phaser.awaitAdvance(phaser.getPhase());
        System.out.println("main thread end,time->" + System.currentTimeMillis());
    }

    @Test
    public void countDownTest2() throws Exception {
        int num = 6;
        CountDownLatch countDownLatch = new CountDownLatch(num);
        Random random = new Random();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(random.nextInt(1000));
                    System.out.println(Thread.currentThread().getName() + " exit,time->" + System.currentTimeMillis());
                    countDownLatch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        for (int i = 0; i < num; i++) {
            new Thread(task).start();
        }
        System.out.println("main thread start await,time->" + System.currentTimeMillis());
        countDownLatch.await();
        System.out.println("main thread end,time->" + System.currentTimeMillis());
    }

    @Test
    public void haserAndCyclicBarrierTest5() throws Exception {
        int num = 5;
        Phaser phaser = new Phaser(num);
        Random random = new Random();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    //到达并等待其他线程到达
                    phaser.arriveAndAwaitAdvance();
                    System.out.println(Thread.currentThread().getName() + " start,time->" + System.currentTimeMillis());
                    Thread.sleep(random.nextInt(1000));
                    System.out.println(Thread.currentThread().getName() + " exit,time->" + System.currentTimeMillis());
                    //通知当前线程已到达
                    phaser.arrive();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        for (int i = 0; i < num; i++) {
            new Thread(task).start();
        }
        System.out.println("main thread start await,time->" + System.currentTimeMillis());
        phaser.awaitAdvance(phaser.getPhase());
        System.out.println("all thread start,time->" + System.currentTimeMillis());
        phaser.awaitAdvance(phaser.getPhase());
        System.out.println("main thread end,time->" + System.currentTimeMillis());
    }

    @Test
    public void cyclicBarrierTest6() throws Exception {
        int num = 6;
        CyclicBarrier cyclicBarrier = new CyclicBarrier(num);
        Random random = new Random();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    cyclicBarrier.await();
                    System.out.println(Thread.currentThread().getName() + " start,time->" + System.currentTimeMillis());
                    Thread.sleep(random.nextInt(1000));
                    System.out.println(Thread.currentThread().getName() + " exit,time->" + System.currentTimeMillis());
                    cyclicBarrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        for (int i = 0; i < num - 1; i++) {
            new Thread(task).start();
        }
        System.out.println("main thread start await,time->" + System.currentTimeMillis());
        cyclicBarrier.await();
        System.out.println("all thread start,time->" + System.currentTimeMillis());
        cyclicBarrier.await();
        System.out.println("main thread end,time->" + System.currentTimeMillis());
    }

    @Test
    public void cyclicBarrierActionTest7() throws Exception {
        int num = 6;
        CyclicBarrier cyclicBarrier = new CyclicBarrier(num, new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " last arrive,time->" + System.currentTimeMillis());
            }
        });
        Random random = new Random();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    cyclicBarrier.await();
                    System.out.println(Thread.currentThread().getName() + " start,time->" + System.currentTimeMillis());
                    Thread.sleep(random.nextInt(1000));
                    System.out.println(Thread.currentThread().getName() + " exit,time->" + System.currentTimeMillis());
                    cyclicBarrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        for (int i = 0; i < num - 1; i++) {
            new Thread(task).start();
        }
        System.out.println("main thread start await,time->" + System.currentTimeMillis());
        cyclicBarrier.await();
        System.out.println("all thread start,time->" + System.currentTimeMillis());
        cyclicBarrier.await();
        System.out.println("main thread end,time->" + System.currentTimeMillis());
    }

    @Test
    public void phaserUnarrivedPartiesTest8() throws Exception {
        int num = 6;
        Phaser phaser = new Phaser(num);
        Random random = new Random();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    //getUnarrivedParties等于1时，当前线程就是最后一个达到的线程
                    if (phaser.getUnarrivedParties() == 1) {
                        System.out.println(Thread.currentThread().getName() + " last arrive,time->" + System.currentTimeMillis());
                    }
                    phaser.arriveAndAwaitAdvance();
                    System.out.println(Thread.currentThread().getName() + " start,time->" + System.currentTimeMillis());
                    Thread.sleep(random.nextInt(1000));
                    System.out.println(Thread.currentThread().getName() + " exit,time->" + System.currentTimeMillis());
                    if (phaser.getUnarrivedParties() == 1) {
                        System.out.println(Thread.currentThread().getName() + " last arrive,time->" + System.currentTimeMillis());
                    }
                    phaser.arrive();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        for (int i = 0; i < num; i++) {
            new Thread(task).start();
        }
        System.out.println("main thread start await,time->" + System.currentTimeMillis());
        phaser.awaitAdvance(phaser.getPhase());
        System.out.println("all thread start,time->" + System.currentTimeMillis());
        phaser.awaitAdvance(phaser.getPhase());
        System.out.println("main thread end,time->" + System.currentTimeMillis());
    }

    /**
     * 上述用例中CountDownLatch、CyclicBarrier和Phaser在构造时都指定了等待的线程数，如果需要等待的线程数发生变更，
     * 则需要重新创建一个新的CountDownLatch或者CyclicBarrier实例，但是如果使用Phaser，则可以通过register方法将等待的线程数加1，
     * 通过bulkRegister方法将等待的线程数加上指定的值，通过arriveAndDeregister方法将等待的线程数减1。
     * @throws Exception
     */
    @Test
    public void test4() throws Exception {
        int num = 6;
        Phaser phaser = new Phaser(num);
        Random random = new Random();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(random.nextInt(1000));
                    System.out.println(Thread.currentThread().getName() + " exit,time->" + System.currentTimeMillis());
                    phaser.arrive();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        for (int i = 0; i < num; i++) {
            new Thread(task).start();
        }
        System.out.println("main thread start Job1 await,time->" + System.currentTimeMillis());
        phaser.awaitAdvance(phaser.getPhase());
        System.out.println("main thread doJob1 end,time->" + System.currentTimeMillis());

        //增加两个等待的线程数
        phaser.bulkRegister(2);
        for (int i = 0; i < num + 2; i++) {
            new Thread(task).start();
        }
        System.out.println("main thread start Job2 await,time->" + System.currentTimeMillis());
        phaser.awaitAdvance(phaser.getPhase());
        System.out.println("main thread doJob2 end,time->" + System.currentTimeMillis());

        //减少四个等待的线程数
        phaser.arriveAndDeregister();
        phaser.arriveAndDeregister();
        phaser.arriveAndDeregister();
        phaser.arriveAndDeregister();
        for (int i = 0; i < num + 2 - 4; i++) {
            new Thread(task).start();
        }
        System.out.println("main thread start Job3 await,time->" + System.currentTimeMillis());
        phaser.awaitAdvance(phaser.getPhase());
        System.out.println("main thread doJob3 end,time->" + System.currentTimeMillis());
    }

    static class Task implements Runnable {

        Random random = new Random();

        Phaser phaser;

        public Task(Phaser phaser) {
            this.phaser = phaser;
            phaser.register();
        }

        @Override
        public void run() {
            try {
                //到达并等待其他线程到达
                phaser.arriveAndAwaitAdvance();
                System.out.println(Thread.currentThread().getName() + " start,time->" + System.currentTimeMillis());
                Thread.sleep(random.nextInt(1000));
                System.out.println(Thread.currentThread().getName() + " exit,time->" + System.currentTimeMillis());
                //到达然后解除注册
                phaser.arriveAndDeregister();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 还可以在构造函数中不指定等待的线程数，根据实际执行任务的线程数动态调整
     */
    @Test
    public void test3() throws Exception {
        Phaser phaser = new Phaser() {
            @Override
            //改写此方法避免parties变成0后被终止了
            protected boolean onAdvance(int phase, int registeredParties) {
                return false;
            }
        };
        //线程数可以是任意个
        for (int i = 0; i < 6; i++) {
            Thread thread = new Thread(new Task(phaser));
            thread.start();
        }
        System.out.println("main thread start await,time->" + System.currentTimeMillis());
        phaser.awaitAdvance(phaser.getPhase());
        System.out.println("all thread start,time->" + System.currentTimeMillis());
        phaser.awaitAdvance(phaser.getPhase());
        System.out.println("main thread end,time->" + System.currentTimeMillis());

        System.out.println("=================");
        //前后的线程数可以不一致，不需要重新创建Phaser实例
        for (int i = 0; i < 4; i++) {
            Thread thread = new Thread(new Task(phaser));
            thread.start();
        }
        System.out.println("main thread start await,time->" + System.currentTimeMillis());
        phaser.awaitAdvance(phaser.getPhase());
        System.out.println("all thread start,time->" + System.currentTimeMillis());
        phaser.awaitAdvance(phaser.getPhase());
    }

    static class Task2 implements Runnable{

        Random random=new Random();

        Phaser phaser;

        public Task2(Phaser phaser) {
            this.phaser = phaser;
            phaser.register();
        }

        @Override
        public void run() {
            try {
                for(int i=0;i<5;i++) {
                    phaser.arriveAndAwaitAdvance();
                    if(phaser.isTerminated()){
                        return;
                    }
                    System.out.println(Thread.currentThread().getName() + " start,time->" + System.currentTimeMillis());
                    Thread.sleep(random.nextInt(1000));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * onAdvance方法是protected方法，子类可以覆写，该方法是最后一个到达的线程执行的，如果返回true表示需要终止Phaser，
     * 否则继续下一轮的phase，因此可以借助该方法实现CyclicBarrier的回调函数功能，也可以控制Phaser的阶段数
     * @throws Exception
     */
    @Test
    public void test9() throws Exception {
        Phaser phaser = new Phaser() {
            @Override
            protected boolean onAdvance(int phase, int registeredParties) {
                System.out.println(Thread.currentThread().getName() + " last arrive,time->" + System.currentTimeMillis());
                // phase为3时，最后一个到达的线程将phase改成4，再下一次循环时，最后一个到达的线程发现phase为4了，终止Phaser，同时唤醒等待的所有线程，判断Phaser终止后就直接退出了。
                if (phase > 3) {
                    return true;
                }
                return false;
            }
        };
        //线程数可以是任意个
        for (int i = 0; i < 3; i++) {
            Thread thread = new Thread(new Task2(phaser));
            thread.start();
        }
        while (!phaser.isTerminated()) {
            int phase = phaser.getPhase();
            System.out.println("main thread start await,time->" + System.currentTimeMillis() + ",phase->" + phase);
            phaser.awaitAdvance(phase);

        }
        System.out.println("main thread exit");
    }

    class Job implements Runnable {

        Random random = new Random();

        Phaser phaser;

        String name;

        public Job(Phaser phaser, String name) {
            this.phaser = phaser;
            this.name = name;
            phaser.register();
        }

        @Override
        public void run() {
            try {
                System.out.println(Thread.currentThread().getName() + " start,time->" + System.currentTimeMillis() + "，do job:" + name + ",phaser->" + phaser.hashCode());
                Thread.sleep(random.nextInt(1000));
                phaser.arrive();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void test10() throws Exception {
        Phaser root = new Phaser();
        Phaser phaser = null;
        for (int i = 0; i < 13; i++) {
            if (i % 4 == 0) { //每4个线程共用一个Phaser实例
                phaser = new Phaser(root);
            }
            new Thread(new Job(phaser, "Job_" + i)).start();
        }
        // 必须等待所有的子Phaser任务都执行完成
        root.awaitAdvance(root.getPhase());
        System.out.println("main thread exit");
    }

}
