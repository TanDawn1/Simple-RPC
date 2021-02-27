package registry;

public interface IServiceProvider {

    //注册接口
    <T> void register(T service);

    //获取服务信息接口
    Object getService(String serviceName);


}
