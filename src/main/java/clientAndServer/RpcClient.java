package clientAndServer;

import pojo.RpcRequestFormat;

/**
 * 客户端实现接口
 */
public interface RpcClient {

    Object sendRequest(RpcRequestFormat rpcRequestFormat);

}
