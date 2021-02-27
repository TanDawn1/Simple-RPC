package clientAndServer.nioNetty.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.RpcRequestFormat;
import pojo.RpcResponse;
import registry.IServiceProvider;
import registry.Impl.DefaultServiceProvider;
import registry.deal.RequestHandler;


/**
 * 用于接收RpcRequest，并执行调用
 * 将调用结果返回封装成RpcResonse发送出去
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequestFormat> {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    private static RequestHandler requestHandler;

    private static IServiceProvider serviceRegistry;

    static {
        requestHandler = new RequestHandler();
        serviceRegistry = new DefaultServiceProvider();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestFormat msg) throws Exception {
        try {
            logger.info("服务端接收到请求：{}", msg);
            String interfaceName = msg.getInterFaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object result = requestHandler.handle(msg, service);
            ChannelFuture future = ctx.writeAndFlush(RpcResponse.success(result));
            future.addListener(ChannelFutureListener.CLOSE);
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }
}
