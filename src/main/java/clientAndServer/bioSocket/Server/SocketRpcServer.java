package clientAndServer.bioSocket.Server;

import clientAndServer.RpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import registry.IServiceProvider;
import registry.IServiceRegistry;
import registry.deal.RequestHandler;
import registry.deal.RequestHandlerThread;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 *  核心服务端
 *  伪异步IO -> BIO + 线程池
 */
public class SocketRpcServer implements RpcServer {
    //线程池的配置数据
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 50;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;

    private final ExecutorService threadPool;

    private RequestHandler requestHandler = new RequestHandler();
    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

    //注册对象
    private final IServiceProvider iserviceProvider;

    private final String host;
    private final int port;
    private final IServiceRegistry iServiceRegistry;

    //控制反转的思想，采用注入的方式为RPCServer注入注册服务对象
    public SocketRpcServer(String host, int port, IServiceProvider serviceProvider, IServiceRegistry iServiceRegistry) {
        this.host = host;
        this.port = port;
        //线程阻塞队列
        BlockingQueue<Runnable> workingBlockingQueue = new ArrayBlockingQueue<Runnable>(BLOCKING_QUEUE_CAPACITY);
        //暂时使用默认的factory
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TIME_UNIT,workingBlockingQueue,threadFactory);
        this.iserviceProvider =serviceProvider;
        this.iServiceRegistry = iServiceRegistry;
    }

    //暂时只对外提供一个接口的调用服务
    public void start(int port){
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            logger.info("服务端启动");
            Socket socket;
            while((socket = serverSocket.accept()) != null){
                logger.info("客户端IP为： " + socket.getInetAddress());
                //每个请求线程都由专门的类进行处理
                threadPool.execute(new RequestHandlerThread(socket, requestHandler, iserviceProvider));
            }
        }catch (Exception e){
            logger.info("发生错误：",e);
        }
    }

    @Override
    public <T> void publisService(Object service, Class<T> serviceClass) {
        //添加服务提供
        iserviceProvider.register(service);

        iServiceRegistry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
        start(port);
    }
}
