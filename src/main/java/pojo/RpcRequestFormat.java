package pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/*
   请求传输的格式
   服务端需要相关的信息确定调用哪个接口方法
 */
@Data
@AllArgsConstructor
@Builder
public class RpcRequestFormat implements Serializable {

    //待调用的接口名
    private String interFaceName;

    //待调用的方法名
    private String methodName;

    //调用的方法参数
    private Object[] parameters;
    //因为在序列化的时候，Object[]数组中的数据会序列化失败，所以需要一个方法参数类型辅助序列化

    //方法参数类型
    private Class<?>[] paramType;

}
