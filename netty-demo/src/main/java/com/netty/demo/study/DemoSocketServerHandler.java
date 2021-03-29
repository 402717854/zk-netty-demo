package com.netty.demo.study;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName DemoSocketServerHandler
 * @Description: TODO
 * @Author wys
 * @Date 2021/1/25-13:58
 * @Version V1.0
 **/
public class DemoSocketServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        System.out.println(msg);
        System.out.println("Client Address ====== " + ctx.channel().remoteAddress());
        ctx.channel().writeAndFlush("from server:" + UUID.randomUUID());
        TimeUnit.MILLISECONDS.sleep(5000);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
