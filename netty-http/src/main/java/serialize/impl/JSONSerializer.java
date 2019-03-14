package serialize.impl;

import com.alibaba.fastjson.JSON;
import serialize.Serializer;

/**
 * @author JiangJian on 2019/3/6 15:21
 */
public class JSONSerializer implements Serializer {
    @Override
    public byte[] serialize(Object object) {
        return JSON.toJSONBytes(object);
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        return JSON.parseObject(bytes,clazz);
    }
}
