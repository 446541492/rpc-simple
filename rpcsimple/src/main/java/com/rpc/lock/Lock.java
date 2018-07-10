package com.rpc.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Demo class
 *
 * @author wanglei
 * @date create in 14:31 2018/7/10
 */
public class Lock {
    static  ReentrantLock reentrantLock = new ReentrantLock();
    static  Condition condition = reentrantLock.newCondition();
    public static boolean lock() throws Exception{
        reentrantLock.lock();
        condition.await();
        return true;
    }

    public static boolean unLock(){
        condition.signalAll();
        reentrantLock.unlock();
        return true;
    }
}
