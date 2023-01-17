package cn.itcast.order.service;

import cn.itcast.order.client.UserClient;
import cn.itcast.order.pojo.Order;
import cn.itcast.order.pojo.User;
import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderService {

    @Autowired
    private UserClient userClient;

    public Order queryOrderById(Long orderId) {
        // 创建资源并进行保护
        try (Entry entry = SphU.entry("resource1")) {
            // 1.查询订单，这里是假数据
            Order order = Order.build(101L, 4999L, "小米 MIX4", 1, 1L, null);
            // 2.查询用户，基于Feign的远程调用
            User user = userClient.findById(order.getUserId());
            // 3.设置
            order.setUser(user);
            // 4.返回
            return order;
        } catch (BlockException blockException) {
            // 资源访问出现异常
            log.error("资源resource1出现异常, 被限流或降级!");
            return new Order();
        }
    }
}
