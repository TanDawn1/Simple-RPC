package clientAndServer.nioNetty.server;

import clientAndServer.RpcServer;
import customizeProtocol.CommonDecoder;
import customizeProtocol.CommonEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import registry.IServiceProvider;
import registry.IServiceRegistry;
import registry.Impl.DefaultServiceProvider;
import registry.Impl.NacosServiceRegistryImpl;
import serializerType.CommonSerializer;

import java.net.InetSocketAddress;

public class NettyServer implements RpcServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private final String host;
    private final int port;
    private final IServiceRegistry iServiceRegistry;
    private final IServiceProvider iServiceProvider;
    private final CommonSerializer serializer;

    //默认注册中心，默认服务提供实现
    public NettyServer(String host, int port, CommonSerializer serializer){
        this.host = host;
        this.port = port;
        this.serializer = serializer;
        this.iServiceRegistry = new NacosServiceRegistryImpl();
        //服务提供
        this.iServiceProvider = new DefaultServiceProvider();
    }

    //高度自定义
    public NettyServer(String host, int port, CommonSerializer serializer, IServiceRegistry iServiceRegistry, IServiceProvider iServiceProvider){
        this.host = host;
        this.port = port;
        this.serializer = serializer;
        this.iServiceRegistry = new NacosServiceRegistryImpl();
        //服务提供
        this.iServiceProvider = new DefaultServiceProvider();
    }

    public void start(int port){
        if(serializer == null){
            logger.info("未设置序列化器");
            throw new RuntimeException();
        }
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    //通道实现类型
                    .channel(NioServerSocketChannel.class)
                    //主通道参数
                    .handler(new LoggingHandler(LogLevel.INFO))
                    //通道选项参数,线程队列连接个数
                    .option(ChannelOption.SO_BACKLOG, 256)
                    //保持活动连接状态
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    //匿名内部类的形式初始化通道对象
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            //解码和编码
                            pipeline.addLast(new CommonEncoder(serializer));
                            pipeline.addLast(new CommonDecoder());
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });
            //绑定端口号
            ChannelFuture future = serverBootstrap.bind(port).sync();
            //对关闭通道进行监听: 直到channel关闭才退出
            future.channel().closeFuture().sync();
        }catch (Exception e){
            logger.error("发生异常：",e);
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * pushilService将服务保存在本地的注册表，同时注册到Nacos中，注册完成之后调用start()方法
     * 缺点：一个服务端只能注册一个服务
     * @param service
     * @param serviceClass
     * @param <T>
     */
    @Override
    public <T> void publisService(Object service, Class<T> serviceClass) {
        //添加服务提供
        iServiceProvider.register(service);

        iServiceRegistry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
        //测试使用
        start(port);
    }

}
