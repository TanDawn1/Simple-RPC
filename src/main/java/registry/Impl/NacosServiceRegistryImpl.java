package registry.Impl;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import registry.IServiceRegistry;
import registry.balancing.IBalancing;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Nacos注册
 */
public class NacosServiceRegistryImpl implements IServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistryImpl.class);

    //负载均衡策略 默认为随机方式
    private static int strategy = 0;

    //读取配置文件
    //TODO
    static {
        strategy = 0;
    }

    //TODO 后序支持自定义
    private static final String SERVER_ADDR = "123.56.160.202:8848";
    private static NamingService namingService = null;

    static {
        try {
            namingService = NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e) {
            logger.error("连接到Nacos失败：",e);
            e.printStackTrace();
        }
    }

    /**
     * 服务端将服务注册进Nacos
     * @param serviceName
     * @param inetSocketAddress
     */
    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            namingService.registerInstance(serviceName, inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        } catch (NacosException e) {
            logger.error("注册服务时错误:",e);
            e.printStackTrace();
        }
    }

    /**
     * 客户端从Nacos获取可用服务
     * @param serviceName
     * @return
     */
    @Override
    public InetSocketAddress lookUpService(String serviceName) {
        try {
            //logger.info("serviceName:{}",serviceName);
            List<Instance> instances = namingService.getAllInstances(serviceName);
            if(instances.size() == 0){
                logger.error("Nacos异常，instances数目为0");
                throw new RuntimeException();
            }
            //TODO 负载均衡策略
            //TODO 现在只获取第一个使用，后面可以根据具体情况进行选择
            IBalancing iBalancing = IBalancing.getIbancing(strategy);
            if(iBalancing == null){
                logger.error("负载均衡配置异常");
                throw new RuntimeException();
            }
            Instance instance = iBalancing.getInstance(instances);
            //Instance instance = instances.get(0);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            logger.info("获取服务时错误:",e);
            e.printStackTrace();
        }
        return null;
    }
}
