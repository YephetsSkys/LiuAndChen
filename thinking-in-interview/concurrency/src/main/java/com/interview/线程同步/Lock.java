package com.interview.线程同步;

import java.util.Collection;

public interface Lock {

    class TimeOutException extends Exception {

        public TimeOutException(String message) {
            super(message);
        }
    }

    void lock() throws InterruptedException;

    void lock(long mills) throws InterruptedException, TimeOutException;

    void unlock();

    Collection<Thread> getBlockedThread();

    int getBlockedSize();

}