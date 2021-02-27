import clientAndServer.nioNetty.server.NettyServer;
import serializerType.serializerHandler.JsonSerializer;
import Test.IHelloService;
import Test.Impl.HelloServiceImpl;

public class NettyTestServer {

    public static void main(String[] args) {
        IHelloService helloService = new HelloServiceImpl();
//        IServiceProvider registry = new DefaultServiceProvider();
//        registry.register(helloService);
        NettyServer server = new NettyServer("127.0.0.1", 9999, new JsonSerializer());
        server.publisService(helloService, IHelloService.class);
    }

}
