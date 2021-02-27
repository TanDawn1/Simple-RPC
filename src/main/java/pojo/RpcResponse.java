package pojo;

import lombok.Data;

import java.io.Serializable;

/*
    服务器调用完方法之后，需要给客户端返回信息
    同样的需要响应数据的封装
 */
@Data
public class RpcResponse<T> implements Serializable {

    //状态码
    private Integer statusCode;

    //响应状态补充信息
    private String message;

    //响应数据
    private T data;

    //成功调用
    public static <T> RpcResponse<T> success(T data){
         RpcResponse<T> response = new RpcResponse<T>();
         response.setStatusCode(Response.SUCCESS.getValue());
         response.setData(data);
         return response;
    }

    //失败调用
    public static <T> RpcResponse<T> fail(Response code){
        RpcResponse<T> response = new RpcResponse<T>();
        response.setStatusCode(code.getValue());
        response.setMessage(code.getReason());
        return response;
    }

}
