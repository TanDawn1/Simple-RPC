package registry.deal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.RpcRequestFormat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 通过反射进行方法调用
 */
public class RequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    public Object handle(RpcRequestFormat rpcRequestFormat, Object service){
        Object res = null;
        try{
            res = invokeTargetMethod(rpcRequestFormat, service);
            logger.info("服务：{} 方法调用成功：{}", rpcRequestFormat.getInterFaceName(), rpcRequestFormat.getMethodName());
        }catch (Exception e){
            logger.error("调用时发生错误：",e);
        }
        return res;
    }

    public Object invokeTargetMethod(RpcRequestFormat rpcRequestFormat, Object service) throws InvocationTargetException, IllegalAccessException {
        Method method = null;
        try{
            method = service.getClass().getMethod(rpcRequestFormat.getMethodName(), rpcRequestFormat.getParamType());
        }catch (Exception e){
            logger.error("发生错误，方法调用失败",e);
        }
        return method.invoke(service, rpcRequestFormat.getParameters());
    }

}
