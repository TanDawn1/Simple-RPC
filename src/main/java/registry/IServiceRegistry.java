package registry;

import java.net.InetSocketAddress;

/**
 * 服务注册
 * 依托nacos
 */
public interface IServiceRegistry {
    //注册
    void register(String serviceName, InetSocketAddress inetSocketAddress);
    //根据方法名从注册中心获取服务提供者地址
    InetSocketAddress lookUpService(String serviceName);

}
