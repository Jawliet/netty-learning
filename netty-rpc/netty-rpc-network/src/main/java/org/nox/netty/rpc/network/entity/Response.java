package org.nox.netty.rpc.network.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author JiangJian on 2019/3/13 18:38
 */
@Getter
@Setter
public class Response {
    private String requestId;
    private int code;
    private String error_msg;
    private Object data;
}
