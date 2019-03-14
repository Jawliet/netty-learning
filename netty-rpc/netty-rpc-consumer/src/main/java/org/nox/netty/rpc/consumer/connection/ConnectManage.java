package org.nox.netty.rpc.consumer.connection;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.nox.netty.rpc.consumer.netty.NettyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jawliet on 2019/3/14
 */
@Slf4j
@Component
public class ConnectManage {
    @Autowired
    private NettyClient nettyClient;

    private AtomicInteger roundRobin = new AtomicInteger(0);
    private CopyOnWriteArrayList<Channel> channels = new CopyOnWriteArrayList<>();
    private Map<SocketAddress, Channel> channelNodes = new ConcurrentHashMap<>();


    public Channel chooseChannel() {
        if (channels.size() > 0) {
            int size = channels.size();
            int index = (roundRobin.getAndAdd(1) + size) % size;
            return channels.get(index);
        } else {
            return null;
        }
    }

    synchronized void updateConnectServer(List<String> addressList) {
        if (CollectionUtils.isEmpty(addressList)) {
            log.error("没有可用的服务器节点, 全部服务节点已关闭!");
            for (final Channel channel : channels) {
                SocketAddress remotePeer = channel.remoteAddress();
                Channel handler_node = channelNodes.get(remotePeer);
                handler_node.close();
            }
            channels.clear();
            channelNodes.clear();
            return;
        }
        Set<SocketAddress> newAllServerNodeSet = new HashSet<>();
        for (String s : addressList) {
            String[] array = s.split(":");
            if (array.length == 2) {
                String host = array[0];
                int port = Integer.parseInt(array[1]);
                final SocketAddress remotePeer = new InetSocketAddress(host, port);
                newAllServerNodeSet.add(remotePeer);
            }
        }

        for (final SocketAddress serverNodeAddress : newAllServerNodeSet) {
            Channel channel = channelNodes.get(serverNodeAddress);
            if (channel != null && channel.isOpen()) {
                log.info("当前服务节点已存在,无需重新连接.{}", serverNodeAddress);
            } else {
                connectServerNode(serverNodeAddress);
            }
        }
        for (int i = 0; i < channels.size(); ++i) {
            Channel channel = channels.get(i);
            SocketAddress remotePeer = channel.remoteAddress();
            if (!newAllServerNodeSet.contains(remotePeer)) {
                log.info("删除失效服务节点 " + remotePeer);
                Channel channel_node = channelNodes.get(remotePeer);
                if (channel_node != null) {
                    channel_node.close();
                }
                channels.remove(channel);
                channelNodes.remove(remotePeer);
            }
        }
    }

    private void connectServerNode(SocketAddress address) {
        try {
            Channel channel = nettyClient.doConnect(address);
            addChannel(channel, address);
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.info("未能成功连接到服务器:{}", address);
        }
    }

    private void addChannel(Channel channel, SocketAddress address) {
        log.info("加入Channel到连接管理器.{}", address);
        channels.add(channel);
        channelNodes.put(address, channel);
    }

    public void removeChannel(Channel channel) {
        log.info("从连接管理器中移除失效Channel.{}", channel.remoteAddress());
        SocketAddress remotePeer = channel.remoteAddress();
        channelNodes.remove(remotePeer);
        channels.remove(channel);
    }
}