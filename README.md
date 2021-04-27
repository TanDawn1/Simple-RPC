# Simple-RPC
以Nacos为注册中心，支持Java原生Socket的BIO和以Netty为基础的NIO，并自定义了通信数据格式，对外可支持多种的序列化方式

- JDK版本: 1.8
- Netty版本：4.0.43
- Nacos版本：1.4.1

# 架构相关

<div align=center><img src="https://github.com/TanDawn1/Simple-RPC/blob/main/RPC%E6%9E%B6%E6%9E%84.jpg" /></div>

客户端调用服务端的方式取决于客户端的实现：如果使用原生Sokcet则通过线程池实现伪异步BIO，如果选用Netty则调用NIO

<div align=center><img src="https://github.com/TanDawn1/Simple-RPC/blob/main/%E8%B0%83%E7%94%A8%E6%B5%81%E7%A8%8B.jpg" /></div>

自定义了数据包格式，自定义编码和译码

数据格式：

```
  +---------------+---------------+-----------------+-------------+
  |  Package Type    |     Serializer Type |      Data Length     |
  |       4 bytes    |        4 bytes      |        4 bytes       |
  +---------------+---------------+-----------------+-------------+
  |                          Data Bytes                           |
  +---------------------------------------------------------------+
```
- Package Type： 包类型，标明是请求还是响应
- Serializer Type： 序列化类型，标明包使用的序列化方式
- Data Length：数据字节的长度
- Data Bytes：数据

# Future
- 实现NIO和BIO的两种网络传输方式 √

- 实现自定义的通信协议   √

- 使用Nacos为注册中心，管理服务提供者的信息 √

- 实现自定义序列化  √

- 支持以Redis作为注册中心 ×

- 实现负载均衡算法 

- 实现容灾可靠性控制

- 支持注解定义



# 代码模块
```
  clientAndServer
    - bioSocket 原生BIO网络通信的实现
    - nioNetty  Netty的NIO网络通信的配置
   customizeProtocol 编码解码的实现
   pojo  简单实体类
   poxy  基于JDK的动态代理的实现
   registry
    - deal 方法调用的处理
    - Impl 服务提供者的注册
   serializerType 
    - SerializeHandler 序列化方式的实现
   Test 测试类
   
```

# LICENSE
Simple-RPC is under the MIT license.
