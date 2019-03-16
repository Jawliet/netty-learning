package org.nox.netty.http.sample.serialize;

/**
 * @author Jawliet on 2019/3/6 15:20
 */
public interface Serializer {
    /**
     * java 对象转换成二进制
     */
    byte[] serialize(Object object);

    /**
     * 二进制转换成 java 对象
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);
}
