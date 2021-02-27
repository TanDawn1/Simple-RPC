package registry.deal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.RpcRequestFormat;
import pojo.RpcResponse;
import registry.IServiceProvider;
import registry.Impl.DefaultServiceProvider;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * 线程池任务对象
 */
public class RequestHandlerThread implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerThread.class);

    private Socket socket;
    private RequestHandler requestHandler;
    private IServiceProvider serviceRegistry;

    public RequestHandlerThread(Socket socket, RequestHandler requestHandler, IServiceProvider serviceRegistry) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serviceRegistry = serviceRegistry;
    }

    public void run() {
        //接收RPCRequest对象，解析调用，生成Resonse对象返回
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            RpcRequestFormat requestFormat = (RpcRequestFormat) objectInputStream.readObject();
//            //获取Method对象
//            Method method = service.getClass().getMethod(requestFormat.getMethodName(), requestFormat.getParamType());
//            //反射调用
//            Object returnObject = method.invoke(service, requestFormat.getParameters());
            String interfaceName = requestFormat.getInterFaceName();
            //解耦，由专用对象进行调用
            Object service = serviceRegistry.getService(interfaceName);
            //方法调用
            Object res = requestHandler.handle(requestFormat, service);
            //封装结果
            objectOutputStream.writeObject(RpcResponse.success(res));
            objectOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
