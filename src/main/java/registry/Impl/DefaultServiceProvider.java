package registry.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import registry.IServiceProvider;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务端将相关的服务保存到Map中
 * 便于外部调用
 */
public class DefaultServiceProvider implements IServiceProvider {

    private static final Logger logger = LoggerFactory.getLogger(DefaultServiceProvider.class);
    //key为接口的完整类名，value为service实现对象
    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<String, Object>();
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    public <T> void register(T service) {
        //返回class表示
        String serviceName = service.getClass().getCanonicalName();
        if(registeredService.contains(serviceName)) return;
        //添加到注册列表
        registeredService.add(serviceName);
        //类实现的接口
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if(interfaces.length == 0){
            logger.info("异常，实现接口数为0");
            throw new RuntimeException();
        }
        //遍历
        for(Class<?> i : interfaces){
            //默认采用对象实现的接口完整类名作为服务名
            //TODO 某个接口只能有一个对象提供服务
            serviceMap.put(i.getCanonicalName(), service);
        }
        logger.info("接口：{} 提供服务：{}", interfaces, serviceName);
    }

    public Object getService(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if(service == null){
            logger.info("未在注册列表中找到服务");
            throw new RuntimeException();
        }
        return service;
    }

}
