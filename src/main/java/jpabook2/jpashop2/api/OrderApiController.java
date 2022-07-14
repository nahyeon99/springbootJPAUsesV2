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

    /**
     * V2. 엔티티를 DTO로 변환
     */
    @GetMapping("/api/v2/orders")
    public List<OrderDto> orderV2() {

        List<Order> orders = orderRepository.findAll(new OrderSearch());

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return result;
    }

    /**
     * V3. 엔티티를 DTO로 변환 - 페치 조인 최적화
     */
    /*
        fetch join으로 SQL이 1번만 실행된다.
        distinct를 사용함으로써 1대다 조인의 경우 데이터베이스 row가 증가하게 되는데,
        그 결과 order 엔티티의 조회 수도 증가하게 된다.
        JPA의 distinct는 SQL의 distinct를 추가하고,
        더해서 같은 엔티티가 조회되면, 애플리케이션에서 중복을 걸러준다.

        단점은 컬렉션 패치조인을 사용하면 페이징 불가능하다. (사용 금지)
        1대다 fetch join에서는 hibernate는 경고 로그를 남기면서
        모든 데이터를 DB에서 읽어오고, 메모리에서 페이징 해버린다. (매우 위험)

        1대다가 아닌 member, delivery은 가능하지만
        orderItems를 fetch join한 이상 1대다가 되므로 페이징이 불가능하다.

        + 컬렉션 패치 조인은 1개만 사용할 수 있다.(1대다는 한 건만 페치 조인 가능)
        둘 이상 사용시 데이터가 부정합하게 조회될 수 있음
     */
    @GetMapping("/api/v3/orders")
    public List<OrderDto> orderV3() {
        List<Order> orders = orderRepository.findAllWithItem();
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







