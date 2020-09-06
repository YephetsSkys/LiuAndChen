package com.interview.chengang.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RouterServerHandler extends SimpleChannelInboundHandler<ByteBuf> {
	
	static ExecutorService executorService = Executors.newSingleThreadExecutor();

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		ByteBuf reqMsg = (ByteBuf)msg;
		byte[] body = new byte[reqMsg.readableBytes()];
		
		executorService.execute(() -> {
			//其他业务代码
		});
	}
	
	public void channelActive(final ChannelHandlerContext ctx) {
		new Thread(() -> {
			try {
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
			
			ByteBuf msg = null;
			while (true) {
				msg = Unpooled.wrappedBuffer("Netty OOM Example".getBytes());
                ctx.writeAndFlush(msg);
            }
		}).start();
	}

}
