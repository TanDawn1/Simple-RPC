package customizeProtocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.RpcRequestFormat;
import pojo.RpcResponse;
import serializerType.CommonSerializer;

import java.util.List;

/**
 * 解码，把Encoder编码的进行解码
 *
 *+---------------+---------------+-----------------+-------------+
 * |  Package Type    |     Serializer Type |      Data Length     |
 * |       4 bytes    |        4 bytes      |        4 bytes       |
 * +---------------+---------------+-----------------+-------------+
 * |                          Data Bytes                           |
 * |                   Length: ${Data Length}                      |
 * +---------------------------------------------------------------+
 */
public class CommonDecoder extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(CommonDecoder.class);


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        //解码
//        logger.info("解码数据包:{}",byteBuf);
        //int占4个字节
        int packageCode = byteBuf.readInt();
        Class<?> packageClass;
        //标明class的类型
        if(packageCode == PackageType.REQUEST_PACK.code){
            packageClass = RpcRequestFormat.class;
        }else if(packageCode == PackageType.RESPONSE_PACK.code){
            packageClass = RpcResponse.class;
        }else{
            logger.error("不能识别的数据包：{}",packageCode);
            throw new RuntimeException();
        }
        //根据数据包中的序列化表示选择序列化器
        int serializerCode = byteBuf.readInt();
        CommonSerializer serializer = CommonSerializer.getSerializerContainer(serializerCode);
        if(serializer == null){
            logger.error("不能识别的反序列化器：{}",serializer);
            throw new RuntimeException();
        }
        //获取数据长度  防止沾包
        int length = byteBuf.readInt();
        //创建接收数组
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        //反序列化
        Object obj = serializer.deSerialized(bytes, packageClass);
        list.add(obj);
    }
}
