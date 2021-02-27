package clientAndServer.bioSocket.Server;

import pojo.RpcRequestFormat;
import pojo.RpcResponse;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;

/*
    最初工作线程
    单一的方法调用
 */
public class WorkerThread implements Runnable{

    Socket socket;

    Object service;

    public WorkerThread(Socket socket, Object service) {
        this.socket = socket;
        this.service = service;
    }

    public void run() {
        //接收RPCRequest对象，解析调用，生成Resonse对象返回
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            RpcRequestFormat requestFormat = (RpcRequestFormat) objectInputStream.readObject();
            //获取Method对象
            Method method = service.getClass().getMethod(requestFormat.getMethodName(), requestFormat.getParamType());
            //反射调用
            Object returnObject = method.invoke(service, requestFormat.getParameters());
            objectOutputStream.writeObject(RpcResponse.success(returnObject));
            objectOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
