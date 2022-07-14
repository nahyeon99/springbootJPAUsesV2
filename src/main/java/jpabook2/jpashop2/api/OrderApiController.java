package jpabook2.jpashop2.api;

import jpabook2.jpashop2.domain.Address;
import jpabook2.jpashop2.domain.Order;
import jpabook2.jpashop2.domain.OrderItem;
import jpabook2.jpashop2.domain.OrderStatus;
import jpabook2.jpashop2.repository.OrderRepository;
import jpabook2.jpashop2.repository.OrderSearch;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

        for (Order order : all) {
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

    @GetMapping("/api/v2/orders")
    public List<OrderDto> orderV2() {

        List<Order> orders = orderRepository.findAll(new OrderSearch());

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return result;
    }

    @Getter
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems; // 엔티티라 반환이 안됨
        // Address 정도의 value entity는 반환해도 괜찮지만
        // OrderItem type의 List의 경우는 OrderItem entity대해서도 dto로 돌려줘야 한다.

        // dto 반환
        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }
    }


            // 아래 코드로 wrapping을 해서 엔티티를 dto로 반환하는데는 성공했지만
            // 엔티티를 dto로 wrapping 해서도 반환해선 안된다.
            // (엔티티 외부 노출 금지, 엔티티에 대한 의존 X)
            // -> orderItem도 dto로 변환해서 반환해줘야한다.
    //            order.getOrderItems().stream().forEach(o -> o.getItem().getName());
    //            orderItems = order.getOrderItems();



    @Getter
    static class OrderItemDto {

        private String itemName; // 상품 명
        private int orderPrice; // 주문 가격
        private int count; // 주문 수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}







