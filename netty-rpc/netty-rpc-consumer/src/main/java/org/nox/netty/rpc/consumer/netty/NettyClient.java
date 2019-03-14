package org.nox.netty.rpc.consumer.netty;

import com.alibaba.fastjson.JSONArray;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.nox.netty.rpc.consumer.connection.ConnectManage;
import org.nox.netty.rpc.network.codec.JSONDecoder;
import org.nox.netty.rpc.network.codec.JSONEncoder;
import org.nox.netty.rpc.network.entity.Request;
import org.nox.netty.rpc.network.entity.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.SocketAddress;
import java.util.concurrent.SynchronousQueue;

/**
 * @author Jawliet on 2019/3/14
 */
@Component
@Slf4j
public class NettyClient {
    private EventLoopGroup group = new NioEventLoopGroup(1);
    private Bootstrap bootstrap = new Bootstrap();
    @Autowired
    private  NettyClientHandler clientHandler;
    @Autowired
    private ConnectManage connectManage;


    public NettyClient() {
        bootstrap.group(group).
                channel(NioSocketChannel.class).
                option(ChannelOption.TCP_NODELAY, true).
                option(ChannelOption.SO_KEEPALIVE, true).
                handler(new ChannelInitializer<SocketChannel>() {
                    //创建NIOSocketChannel成功后，在进行初始化时，将它的ChannelHandler设置到ChannelPipeline中，用于处理网络IO事件
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new IdleStateHandler(0, 0, 30));
                        pipeline.addLast(new JSONEncoder());
                        pipeline.addLast(new JSONDecoder());
                        pipeline.addLast("handler", clientHandler);
                    }
                });
    }

    @PreDestroy
    public void destroy() {
        log.info("RPC客户端退出,释放资源!");
        group.shutdownGracefully();
    }

    public Object send(Request request) throws InterruptedException {

        Channel channel = connectManage.chooseChannel();
        if (channel != null && channel.isActive()) {
            SynchronousQueue<Object> queue = clientHandler.sendRequest(request, channel);
            Object result = queue.take();
            return JSONArray.toJSONString(result);
        } else {
            Response res = new Response();
            res.setCode(1);
            res.setError_msg("未正确连接到服务器.请检查相关配置信息!");
            return JSONArray.toJSONString(res);
        }
    }

    public Channel doConnect(SocketAddress address) throws InterruptedException {
        ChannelFuture future = bootstrap.connect(address);
        return future.sync().channel();
    }
}

