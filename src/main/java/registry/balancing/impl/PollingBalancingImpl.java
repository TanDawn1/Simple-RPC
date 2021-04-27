package registry.balancing.impl;

import com.alibaba.nacos.api.naming.pojo.Instance;
import registry.balancing.IBalancing;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询的负载均衡
 *
 */
public class PollingBalancingImpl implements IBalancing {

    //原子类实现保证线程安全
    private static final AtomicInteger index = new AtomicInteger(-1);

    @Override
    public Instance getInstance(List<Instance> instances) {
        int num = instances.size();
        //确保每个线程都有一个私有的变量，不会因为其他线程的修改而修改
        int prIndex = index.incrementAndGet();
        if(prIndex >= num){
            index.set(0);
        }
        return instances.get(prIndex);
    }
}
