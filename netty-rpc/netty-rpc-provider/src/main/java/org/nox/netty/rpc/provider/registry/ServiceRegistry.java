package org.nox.netty.rpc.provider.registry;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Jawliet on 2019/3/13 18:55
 */
@Component
@Slf4j
public class ServiceRegistry {


    @Value("${registry.address}")
    private String registryAddress;

    private static final String ZK_REGISTRY_PATH = "/rpc";

    public void register(String data) {
        if (data != null) {
            ZkClient client = connectServer();
            AddRootNode(client);
            createNode(client, data);
        }
    }
    private ZkClient connectServer() {
        return new ZkClient(registryAddress,20000,20000);
    }

    private void AddRootNode(ZkClient client){
        boolean exists = client.exists(ZK_REGISTRY_PATH);
        if (!exists){
            client.createPersistent(ZK_REGISTRY_PATH);
            log.info("创建zookeeper主节点 {}",ZK_REGISTRY_PATH);
        }
    }

    private void createNode(ZkClient client, String data) {
        String path = client.create(ZK_REGISTRY_PATH + "/provider", data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        log.info("创建zookeeper数据节点 ({} => {})", path, data);
    }
}

