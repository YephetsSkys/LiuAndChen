package com.interview.chengang.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.netty.buffer.ByteBuf;
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

}
