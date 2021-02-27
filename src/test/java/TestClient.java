import clientAndServer.RpcClient;
import Test.HelloObject;
import Test.IHelloService;
import clientAndServer.bioSocket.client.ClientService;
import poxy.RpcClientProxy;

public class TestClient {

    public static void main(String[] args) {
        RpcClient rpcClient = new ClientService();
        RpcClientProxy proxy = new RpcClientProxy(rpcClient);
        IHelloService iHelloService = proxy.getProxy(IHelloService.class);
        HelloObject object = new HelloObject(12, "client message");
        String res = iHelloService.hello(object);
        System.out.println(res);
    }

}
