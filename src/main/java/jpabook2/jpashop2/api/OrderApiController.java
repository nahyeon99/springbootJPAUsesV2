package jpabook2.jpashop2.api;

import jpabook2.jpashop2.domain.Order;
import jpabook2.jpashop2.domain.OrderItem;
import jpabook2.jpashop2.repository.OrderRepository;
import jpabook2.jpashop2.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    /**
     * V1. 엔티티를 직접 노출
     */
    @GetMapping("/api/v1/orders")
    private List<Order> orderV1() {
        List<Order> all = orderRepository.findAll(new OrderSearch());

        for(Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
//            for (OrderItem orderItem : orderItems) {
//                orderItem.getItem().getName();
//            }

            // 주문과 관련된 아이템들을 가져와서 item도 전부 초기화
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

}
