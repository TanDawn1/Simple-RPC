package poxy;

import clientAndServer.RpcClient;
import pojo.RpcRequestFormat;
import pojo.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 *   JDK动态代理类
 *     在客户端并没有接口的实现类，没有办法直接生成实例对象，可以通过动态代理的方式生成实例
 *     调用方法时生成RpcRequest对象并且发送给服务端
 */
public class RpcClientProxy implements InvocationHandler {

    private RpcClient rpcClient;

    public RpcClientProxy(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    //通过传递port和host来指明服务端的位置，使用getProxy方法生成代理对象
    public <T> T getProxy(Class<T> clazz){
        //创建代理对象实例
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    //代理对象的方法被调用时的动作
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //使用builder模式生成对象
        RpcRequestFormat rpcRequestFormat = RpcRequestFormat.builder()
                .interFaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .paramType(method.getParameterTypes())
                .build();
        //ClientService testClient = new ClientService();
        return ((RpcResponse)rpcClient.sendRequest(rpcRequestFormat)).getData();
    }
}
