package com.zk.demo.queue;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class BarrierQueue extends AbstractQueue{

    private static final String PATH = "/barrierQueue";

    private CountDownLatch countDownLatch = null;

    private Integer barrierNum=0;

    public BarrierQueue(Integer barrierNum) {
        boolean exists = zkClient.exists(PATH);
        if (!exists) {
            this.barrierNum=barrierNum;
            zkClient.createPersistent(PATH,barrierNum);
        }
    }

    @Override
    public void waitQueue() {
        IZkChildListener iZkChildListener = new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                   if(!CollectionUtils.isEmpty(currentChilds)){
                       int size = currentChilds.size();
                       if(size==barrierNum){
                           System.out.println("子节点数量达到"+barrierNum);
                       }
                   }
            }
        };
        //监听前一个节点的变化
        zkClient.subscribeChildChanges(PATH, iZkChildListener);
        if (zkClient.exists(PATH)) {
            countDownLatch = new CountDownLatch(1);
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        zkClient.subscribeChildChanges(PATH, iZkChildListener);
    }

    @Override
    public void inQueue() {
        List<String> childrens = zkClient.getChildren(PATH);
        if(!CollectionUtils.isEmpty(childrens)&&childrens.size()<=barrierNum){
            zkClient.createEphemeralSequential(PATH + "/", "queue");
        }else{
            System.out.println("拒绝入队");
        }
    }

    @Override
    public void outQueue() {

    }
}
