package serializerType;

import serializerType.serializerHandler.JsonSerializer;

/**
 * 序列化方式接口
 */
public interface CommonSerializer {

    //序列化
    byte[] serialized(Object obj);

    //反序列化
    Object deSerialized(byte[] bytes, Class<?> clazz);

    int getCode();

    //根据数据头中的type序列化器
    //TODO 增加序列化方式
    static CommonSerializer getSerializerContainer(int code){
        switch (code){
            case 1:
                return new JsonSerializer();
            default:
                return null;
        }
    }

}
