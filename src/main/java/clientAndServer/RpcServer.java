package clientAndServer;

/**
 * 服务端实现接口
 */
public interface RpcServer {

    void start(int port);

    <T> void publisService(Object service, Class<T> serviceClass);

}
