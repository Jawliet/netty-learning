package server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;

/**
 * @author JiangJian on 2019/3/6 15:23
 */
public class HttpHelloWorldServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        /**
         * 或者使用HttpRequestDecoder & HttpResponseEncoder
         * HttpRequestDecoder 即把 ByteBuf 解码到 HttpRequest 和 HttpContent。
         *
         * HttpResponseEncoder 即把 HttpResponse 或 HttpContent 编码到 ByteBuf。
         *
         * HttpServerCodec 即 HttpRequestDecoder 和 HttpResponseEncoder 的结合。
         */
        p.addLast(new HttpServerCodec());
        /**
         * 在处理POST消息体时需要加上
         * aggregator，消息聚合器（重要）。为什么能有FullHttpRequest这个东西，就是因为有他，
         * HttpObjectAggregator，如果没有他，就不会有那个消息是FullHttpRequest的那段Channel，同样也不会有FullHttpResponse。
         * 如果我们将z'h
         * HttpObjectAggregator(1024 * 1024)的参数含义是消息合并的数据大小，如此代表聚合的消息内容长度不超过1M。
         * 通过它可以把 HttpMessage 和 HttpContent 聚合成一个 FullHttpRequest 或者 FullHttpResponse （取决于是处理请求还是响应），而且它还可以帮助你在解码时忽略是否为“块”传输方式。
         */
        p.addLast(new HttpObjectAggregator(1024*1024));
        p.addLast(new HttpServerExpectContinueHandler());
        p.addLast(new HttpHelloWorldServerHandler());
    }
}
