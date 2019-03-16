package org.nox.netty.im.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Jawliet on 2019/3/15
 */
@Slf4j
public class NettyClient {
    private final static int MAX_RETRY = 5;

    public static void main(String[] args) {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) {
                                System.out.println(new Date() + ": 客户端写出数据");

                                // 1. 获取数据
                                ByteBuf buffer = getByteBuf(ctx);

                                // 2. 写数据
                                ctx.channel().writeAndFlush(buffer);
                            }

                            private ByteBuf getByteBuf(ChannelHandlerContext ctx) {
                                // 1. 获取二进制抽象 ByteBuf
                                ByteBuf buffer = ctx.alloc().buffer();

                                // 2. 准备数据，指定字符串的字符集为 utf-8
                                byte[] bytes = "hello, netty".getBytes(StandardCharsets.UTF_8);

                                // 3. 填充数据到 ByteBuf
                                buffer.writeBytes(bytes);

                                return buffer;
                            }
                        });
                    }
                });
        connect(bootstrap, "127.0.0.1", 8000, MAX_RETRY);
    }

    private static void connect(Bootstrap bootstrap, String host, int port, int retry) {
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess())
                log.info("connected server: " + host + ":" + port);
            else if (retry == 0)
                log.error("connect failed");
            else {
                int order = MAX_RETRY - retry;
                int delay = 1 << order;
                log.warn("retry connected times: " + retry + ", retrying " + order + "connect");
                bootstrap.config().group().schedule(() -> connect(bootstrap, host, port, retry - 1), delay, TimeUnit.SECONDS);
            }
        });
    }
}
