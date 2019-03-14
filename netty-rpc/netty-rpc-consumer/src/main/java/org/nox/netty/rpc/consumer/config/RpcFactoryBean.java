package org.nox.netty.rpc.consumer.config;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Proxy;

/**
 * @author Jawliet on 2019/3/14
 */
public class RpcFactoryBean<T> implements FactoryBean<T> {

    private Class<T> rpcInterface;

    @Autowired
    private RpcFactory<T> factory;

    public RpcFactoryBean() {
    }

    public RpcFactoryBean(Class<T> rpcInterface) {
        this.rpcInterface = rpcInterface;
    }

    @Override
    public T getObject() throws Exception {
        return getRpc();
    }

    public Class<?> getObjectType() {
        return this.rpcInterface;
    }

    public boolean isSingleton() {
        return true;
    }

    public <T> T getRpc() {
        return (T) Proxy.newProxyInstance(rpcInterface.getClassLoader(), new Class[]{rpcInterface}, factory);
    }
}

