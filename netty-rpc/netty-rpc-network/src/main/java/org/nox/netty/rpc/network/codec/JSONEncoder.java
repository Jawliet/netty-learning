package org.nox.netty.rpc.network.codec;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @author Jawliet on 2019/3/13 18:48
 */
public class JSONEncoder extends MessageToMessageEncoder {

    @Override
    @SuppressWarnings("unchecked")
    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, List out){
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.ioBuffer();
        byte[] bytes = JSON.toJSONBytes(msg);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
        out.add(byteBuf);
    }
}
