package org.nox.netty.rpc.consumer.util;

/**
 * @author Jawliet on 2019/3/14
 */
public class IdUtil {
    private final static SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);
    /**
     * 消息ID
     * @return
     */
    public static String getId(){
        return String.valueOf(idWorker.nextId());
    }
}
