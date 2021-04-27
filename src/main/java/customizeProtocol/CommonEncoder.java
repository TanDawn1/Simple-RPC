package customizeProtocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.RpcRequestFormat;
import serializerType.CommonSerializer;
import serializerType.SerializerCode;

/**
 * 编码
 * 为数据加上相应的头部
 * 自定义协议
 *+---------------+---------------+-----------------+-------------+
 * |  Package Type    |     Serializer Type |      Data Length     |
 * |       4 bytes    |        4 bytes      |        4 bytes       |
 * +---------------+---------------+-----------------+-------------+
 * |                          Data Bytes                           |
 * |                   Length: ${Data Length}                      |
 * +---------------------------------------------------------------+
 * 添加了序列化类型和 数据大小防止沾包
 */
public class CommonEncoder extends MessageToByteEncoder<Object> {

    private final CommonSerializer serializer;

    private static final Logger logger = LoggerFactory.getLogger(CommonEncoder.class);

    //注入序列化方式
    public CommonEncoder(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    //依托于Netty提供的封装方法
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf byteBuf) throws Exception {
        //编码封装数据
       // logger.info("编码数据包:{}",byteBuf);
        //包类型
        if(msg instanceof RpcRequestFormat){
            byteBuf.writeInt(PackageType.REQUEST_PACK.code);
        }else{
            byteBuf.writeInt(PackageType.RESPONSE_PACK.code);
        }
        //序列化类型
        byteBuf.writeInt(serializer.getCode());
        //序列化数据
        byte[] bytes = serializer.serialized(msg);
        //数据大小 -> 防止沾包
        byteBuf.writeInt(bytes.length);
        //封装数据
        byteBuf.writeBytes(bytes);
    }
}
