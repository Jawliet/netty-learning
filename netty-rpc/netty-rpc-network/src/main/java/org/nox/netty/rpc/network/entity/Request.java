package org.nox.netty.rpc.network.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Jawliet on 2019/3/13 18:37
 */
@Setter
@Getter
public class Request {
    private String id;
    /**
     * 类名
     */
    private String className;
    /**
     * 函数名称
     */
    private String methodName;
    /**
     * 参数类型
     */
    private Class<?>[] parameterTypes;
    /**
     * 参数列表
     */
    private Object[] parameters;
}
