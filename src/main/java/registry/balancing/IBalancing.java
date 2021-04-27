package registry.balancing;

import com.alibaba.nacos.api.naming.pojo.Instance;
import registry.balancing.impl.PollingBalancingImpl;

import java.util.List;

/**
 * 负载均衡接口
 */
public interface IBalancing {

    Instance getInstance(List<Instance> instance);

    static IBalancing getIbancing(int type){
        switch (type){
            //轮询
            case 0 : new PollingBalancingImpl();

            default:
                return null;
        }

    }
}
