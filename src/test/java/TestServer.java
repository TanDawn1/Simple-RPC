import clientAndServer.bioSocket.Server.SocketRpcServer;
import clientAndServer.RpcServer;
import registry.IServiceProvider;
import registry.IServiceRegistry;
import registry.Impl.DefaultServiceProvider;
import Test.IHelloService;
import Test.Impl.HelloServiceImpl;
import registry.Impl.NacosServiceRegistryImpl;


public class TestServer {

    public static void main(String[] args) {
        IHelloService helloService = new HelloServiceImpl();
        IServiceProvider serviceProvider = new DefaultServiceProvider();
        IServiceRegistry serviceRegistry = new NacosServiceRegistryImpl();
//        serviceRegistry.register(helloService);
        RpcServer rpcServer = new SocketRpcServer("127.0.0.1", 9000, serviceProvider, serviceRegistry);
        rpcServer.publisService(helloService, IHelloService.class);
    }

}
