package registry.Impl;

import com.alibaba.nacos.api.naming.NamingService;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import registry.IServiceRegistry;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Redis做注册中心
 */
public class RedisServiceRegistryImpl implements IServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(RedisServiceRegistryImpl.class);

    //TODO 读取pro文件 高度自定义
    private static String SERVER_ADDR = "123.56.160.202:8848";
    private static String SERVER_PASSWORD = "***";

    private static RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;
    private static NamingService namingService = null;

    public RedisServiceRegistryImpl(){
        RedisURI redisURI = RedisURI.builder()
                .withHost(SERVER_ADDR)
                .withPort(6379)
                .withPassword(SERVER_PASSWORD)
                .withTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();
        if(redisURI == null){
            logger.error("Redis连接失败");
            throw new RuntimeException();
        }
        redisClient = RedisClient.create(redisURI);
        connection = redisClient.connect();
    }

    public RedisServiceRegistryImpl(String SERVER_ADDR, String SERVER_PASSWORD){
        //TODO 提供自定义的Redis连接地址
        RedisURI redisURI = RedisURI.builder()
                .withHost(SERVER_ADDR)
                .withPort(6379)
                .withPassword(SERVER_PASSWORD)
                .withTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();
        redisClient = RedisClient.create(redisURI);
        connection = redisClient.connect();
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        //注册
        //Redis使用Hash类型进行存储
    }

    @Override
    public InetSocketAddress lookUpService(String serviceName) {
        return null;
    }

    public void closeConnection(){
        connection.close();
    }

}
