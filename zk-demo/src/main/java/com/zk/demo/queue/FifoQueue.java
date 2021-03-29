package com.zk.demo.queue;

import org.I0Itec.zkclient.IZkDataListener;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class FifoQueue extends AbstractQueue{

    private static final String PATH = "/fifo";
    //当前节点路径
    private String currentPath;
    //前一个节点的路径
    private String beforePath;

    private CountDownLatch countDownLatch = null;

    public FifoQueue() {
        //如果不存在这个节点，则创建持久节点
        try {
            boolean exists = zkClient.exists(PATH);
            if (!exists) {
                zkClient.createPersistent(PATH);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void waitQueue() {
        IZkDataListener lIZkDataListener = new IZkDataListener() {

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println("删除的路径:"+dataPath);
                if (null != countDownLatch) {
                    countDownLatch.countDown();
                }
            }

            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {

            }
        };
        //监听前一个节点的变化
        zkClient.subscribeDataChanges(beforePath, lIZkDataListener);
        if (zkClient.exists(beforePath)) {
            countDownLatch = new CountDownLatch(1);
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        zkClient.unsubscribeDataChanges(beforePath, lIZkDataListener);
    }

    @Override
    public void inQueue() {
        //如果currentPath为空则为第一次尝试加锁，第一次加锁赋值currentPath
        if (null == currentPath || "".equals(currentPath)) {
            //在path下创建一个临时的顺序节点
            currentPath = zkClient.createEphemeralSequential(PATH + "/", "queue");
        }
        //获取所有的临时节点，并排序
        List<String> childrens = zkClient.getChildren(PATH);
        Collections.sort(childrens);
        if (!currentPath.equals(PATH+"/"+childrens.get(0))){//如果当前节点不是排名第一，则获取它前面的节点名称，并赋值给beforePath
            int pathLength = PATH.length();
            int wz = Collections.binarySearch(childrens, currentPath.substring(pathLength+1));
            beforePath = PATH+"/"+childrens.get(wz-1);
        }
    }

    @Override
    public void outQueue() {
        if (null != zkClient) {
            try {
                zkClient.delete(currentPath);
                zkClient.close();
            } catch (Exception e) {
            }
        }
    }
}
