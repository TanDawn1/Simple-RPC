package clientAndServer.bioSocket.client;

import clientAndServer.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.RpcRequestFormat;
import registry.IServiceRegistry;
import registry.Impl.NacosServiceRegistryImpl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;


public class ClientService implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);

    private Socket socket;

    private IServiceRegistry iServiceRegistry;

    //指定Host和Port
    public ClientService(String host, int port) {
        try {
            socket = new Socket(host, port);
        } catch (IOException e) {
            logger.error("BIO创建异常:{}",socket);
            e.printStackTrace();
        }
    }

    //使用Nacos获取有效的服务地址
    public ClientService(){
        this.iServiceRegistry = new NacosServiceRegistryImpl();
    }

    @Override
    public Object sendRequest(RpcRequestFormat rpcRequestFormat) {
        try{
            if(socket == null) {
                InetSocketAddress inetSocketAddress = iServiceRegistry.lookUpService(rpcRequestFormat.getInterFaceName());
                socket = new Socket(inetSocketAddress.getAddress(), inetSocketAddress.getPort());
            }
            //通过序列化的方式 通过Socket的传输序列化之后的数据
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequestFormat);
            objectOutputStream.flush();
            return objectInputStream.readObject();
        }catch (Exception e){
            logger.info("发生异常: ", e);
            return null;
        }finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
