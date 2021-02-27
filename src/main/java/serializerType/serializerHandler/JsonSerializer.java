package serializerType.serializerHandler;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.RpcRequestFormat;
import serializerType.CommonSerializer;
import serializerType.SerializerCode;

import java.io.IOException;

/**
 * json序列化器
 *
 *性能对比，选择fastJson
 * 类别	1000次	5000次	10000次	100000次	1000000次
 * byte数组	82ms	147ms	213ms	827ms	5707ms
 * fastJson	114ms	157ms	146ms	174ms	412ms
 *
 */
public class JsonSerializer implements CommonSerializer {

    private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);

    @Override
    public byte[] serialized(Object obj) {
        try {
            return JSON.toJSONBytes(obj);
        }catch (Exception e){
            logger.error("序列化发生错误：", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object deSerialized(byte[] bytes, Class<?> clazz) {
        try{
            Object obj = JSON.parseObject(bytes, clazz);
            if(obj instanceof RpcRequestFormat){
                obj = handleRequest(obj);
            }
            return obj;
        }catch (Exception e){
            logger.error("反序列的时候发生错误：",e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.JSON.code;
    }

    /*
       这里由于使用JSON序列化和反序列化Object数组，无法保证反序列化后仍然为原实例类型
       需要重新判断处理
    */
    private Object handleRequest(Object obj) throws IOException {
        RpcRequestFormat rpcRequest = (RpcRequestFormat) obj;
        for(int i = 0; i < rpcRequest.getParameters().length; i ++) {
            Class<?> clazz = rpcRequest.getParamType()[i];
            //判断是否为本身或者父类，不为则说明实例类型出现了问题
            if(!clazz.isAssignableFrom(rpcRequest.getParameters()[i].getClass())) {
                byte[] bytes = JSON.toJSONBytes(rpcRequest.getParameters()[i]);
                rpcRequest.getParameters()[i] = JSON.parseObject(bytes, clazz);
            }
        }
        return rpcRequest;
    }

}
