package client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 注解 {@link io.netty.channel.ChannelHandler.Sharable} 主要是为了多个handler可以被多个channel安全地共享，也就是保证线程安全
 * @author JiangJian on 2019/3/5 18:26
 */
@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.err.println(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
