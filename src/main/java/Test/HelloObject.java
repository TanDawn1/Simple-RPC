package Test;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

//必须实现序列化接口，需要通过网络传输由C->S
@Data
@AllArgsConstructor
public class HelloObject implements Serializable {

    private Integer id;

    private String message;

}
