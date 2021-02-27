import clientAndServer.RpcClient;
import clientAndServer.nioNetty.client.NettyClient;
import Test.HelloObject;
import serializerType.serializerHandler.JsonSerializer;
import Test.IHelloService;
import poxy.RpcClientProxy;

public class NettyTestClient {

    public static void main(String[] args) {
        RpcClient client = new NettyClient(new JsonSerializer());
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        IHelloService helloService = rpcClientProxy.getProxy(IHelloService.class);
        HelloObject helloObject = new HelloObject(12,"This is a message");
        String res = helloService.hello(helloObject);
        System.out.println(res);
    }

}
