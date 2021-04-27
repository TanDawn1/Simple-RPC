package Test.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Test.HelloObject;
import Test.IHelloService;

public class HelloServiceImpl implements IHelloService {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    public String hello(HelloObject helloObject, int data) {
        logger.info("接收到：{}", helloObject.getMessage());
        return "成功的返回值：id = " + helloObject.getId();
    }

}
