package com.interview.线程执行;

/**
 * @author manliu
 * @description 通过设置每个Runnable实力的执行时间然后join方法也是一起执行，可以达到M2-M1-M3的这种线程执行顺序
 */
public class ThreadJoin3 {

    public static void main(String[] args) throws InterruptedException {
        long startTimestamp = System.currentTimeMillis();
        Thread t1 = new Thread(new CaptureRunnable("M1", 10000L));
        Thread t2 = new Thread(new CaptureRunnable("M2", 5000L));
        Thread t3 = new Thread(new CaptureRunnable("M3", 15000L));

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        long endTimestamp = System.currentTimeMillis();
        System.out.printf("Save data begin timestamp is:%s, end timestamp is:%s\n", startTimestamp, endTimestamp);
    }

}

class CaptureRunnable implements Runnable {

    private String machineName;

    private long spendTime;

    public CaptureRunnable(String machineName, long spendTime) {
        this.machineName = machineName;
        this.spendTime = spendTime;
    }

    @Override
    public void run() {
        //do the really capture data.
        try {
            Thread.sleep(spendTime);
            System.out.printf(machineName + " completed data capture at timestamp [%s] and successfully.\n", System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getResult() {
        return machineName + " finish.";
    }
}