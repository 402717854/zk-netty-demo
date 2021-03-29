package com.zk.demo.queue;

import org.I0Itec.zkclient.ZkClient;

public abstract class AbstractQueue {
    //zk地址和端口
    public static final String ZK_ADDR = "127.0.0.1:2181";
    //超时时间
    public static final int SESSION_TIMEOUT = 10000;
    //创建zk
    protected ZkClient zkClient = new ZkClient(ZK_ADDR, SESSION_TIMEOUT);

    /**
     * 监控队列
     */
    public abstract void waitQueue();

    /**
     * 入队
     */
    public abstract void inQueue();

    /**
     * 出队
     */
    public abstract void outQueue();
}
