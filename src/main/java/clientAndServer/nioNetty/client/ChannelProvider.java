package clientAndServer.nioNetty.client;

import customizeProtocol.CommonDecoder;
import customizeProtocol.CommonEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serializerType.CommonSerializer;

import java.net.InetSocketAddress;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 获取Channel对象
 */
public class ChannelProvider {

    private static final Logger logger = LoggerFactory.getLogger(ChannelProvider.class);
    private static EventLoopGroup eventLoopGroup;
    private static Bootstrap bootstrap = new Bootstrap();
    //Key: Socket地址+编码方式 Value: Channel
    private static Map<String, Channel> channels = new ConcurrentHashMap<>();

    public static Channel get(InetSocketAddress inetSocketAddress, CommonSerializer serializer) throws InterruptedException {
        //暂时只有一种
        String key = inetSocketAddress.toString() + serializer.getCode();
        if (channels.containsKey(key)) {
            Channel channel = channels.get(key);
            if(channels != null && channel.isActive()) {
                return channel;
            } else {
                if (channels != null) {
                    channels.remove(key);
                }else{
                    //一般不会走到这块代码，单纯的因为被标黄看起来不舒服加的
                    logger.info("channels为null");
                    throw new RuntimeException();
                }
            }
        }
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                //超时时间
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)
                .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                /*自定义序列化编解码器*/
                // RpcResponse -> ByteBuf
                ch.pipeline()
                        .addLast(new CommonEncoder(CommonSerializer.getSerializerContainer(serializer.getCode())))
                        .addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS))
                        .addLast(new CommonDecoder())
                        .addLast(new NettyClientHandler());
            }
        });

        Channel channel;
        try {
            channel = connect(bootstrap, inetSocketAddress);
        } catch (ExecutionException e) {
            logger.error("连接客户端时有错误发生", e);
            return null;
        }
        channels.put(key, channel);
        return channel;
    }

    private static Channel connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress) throws ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                logger.info("客户端连接成功!");
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }
}
