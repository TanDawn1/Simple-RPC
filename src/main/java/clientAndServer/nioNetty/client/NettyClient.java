package clientAndServer.nioNetty.client;

import clientAndServer.RpcClient;
import io.netty.channel.*;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.RpcRequestFormat;
import pojo.RpcResponse;
import registry.IServiceRegistry;
import registry.Impl.NacosServiceRegistryImpl;
import serializerType.CommonSerializer;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

/*
    NettyNIO作为客户端通信的具体实现
 */
public class NettyClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

//    private String host;
//    private int port;
//    private static final Bootstrap bootstrap;
//    private static final EventLoopGroup group;
    private final CommonSerializer serializer;
    private final IServiceRegistry iServiceRegistry;

    public NettyClient(CommonSerializer serializer){
        //注入序列化方式
        this.serializer = serializer;
        this.iServiceRegistry = new NacosServiceRegistryImpl();
    }

//    static {
//        group = new NioEventLoopGroup();
//        bootstrap = new Bootstrap();
//        bootstrap.group(group)
//                .channel(NioSocketChannel.class)
//                .option(ChannelOption.SO_KEEPALIVE, true);
//    }

    //TODO 容灾重试策略
    @Override
    public Object sendRequest(RpcRequestFormat rpcRequestFormat) {
        AtomicReference<Object> result = new AtomicReference<>();
        if(serializer == null){
            logger.info("未设置序列化器");
            throw new RuntimeException();
        }
        try {
            InetSocketAddress inetSocketAddress = iServiceRegistry.lookUpService(rpcRequestFormat.getInterFaceName());
            //获取Channel
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            if(channel == null || !channel.isActive()){
                logger.error("channel创建失败");
                return null;
            }
            //ChannelFuture future = bootstrap.connect(host, port).sync();
            logger.info("成功服务端");
            channel.writeAndFlush(rpcRequestFormat).addListener(future1 -> {
                if(future1.isSuccess()){
                    logger.info(String.format("客户端发送消息：%s", rpcRequestFormat.toString()));
                }else{
                    logger.error("消息发送失败：", future1.cause());
                }
            });
            channel.closeFuture().sync();
            //阻塞获取返回结果
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
            //获得返回结果后，将对象以key为rpcResponse放入ChannelHandlerContext中，这里就可以立即获得结果并返回
            return channel.attr(key).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.info("发送消息的时候发生错误：",e);
        }
        return null;
    }

}
