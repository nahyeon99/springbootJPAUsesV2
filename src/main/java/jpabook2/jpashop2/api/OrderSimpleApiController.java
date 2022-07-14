package jpabook2.jpashop2.api;

import jpabook2.jpashop2.domain.Address;
import jpabook2.jpashop2.domain.Order;
import jpabook2.jpashop2.domain.OrderStatus;
import jpabook2.jpashop2.repository.OrderRepository;
import jpabook2.jpashop2.repository.OrderSearch;
import jpabook2.jpashop2.repository.OrderSimpleQueryDTO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne(ManyToOne, OneToOne) 관계 최적화
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    /**
     * V1. 엔티티 직접 노출
     * - Hibernate5Module 모듈 등록, Lazy=null 처리
     * - 양방향 관게 문제 발생 -> @JsonIgnore
     */

    @GetMapping("/api/v1/simple-orders")
    public List<Order> orderV1() {
        List<Order> all = orderRepository.findAll(new OrderSearch());
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderQueryDTO> orderV2() {
        List<Order> orders = orderRepository.findAll(new OrderSearch());
        // 바로 orders를 return 하면 X, 엔티티의 노출을 막기 위해서 api spec에 맞춰서 최적화해서 개발
        // dto로 변환해서 리턴

        // ORDER 2개
        // N + 1 -> 1 + N(order 개수: 2 -> 회원 N + 배송 N)
        // 쿼리가 총 1+N+N 번 실행된다.
        // (order 조회 1번, order->member 지연 로딩 조회 N번, order->delivery 지연 로딩 조회 N번)

        List<SimpleOrderQueryDTO> result = orders.stream()
                .map(o -> new SimpleOrderQueryDTO(o))
                .collect(Collectors.toList());

        return result;
    }

    // fetch join 이용해서 query가 5번->1번으로 나옴
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderQueryDTO> orderV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderQueryDTO> result = orders.stream()
                .map(o -> new SimpleOrderQueryDTO(o))
                .collect(Collectors.toList());

        return result;
    }

    // query를 직접 작성해서 fetch join 보다 일반적인 SQL문을 사용할 때처럼 쓸 수 있기 때문에
    // select절에서의 데이터를 줄임으로써 네트워크 사용량을 줄임
    // v3보다 재사용성이 낮음. 해당 dto를 사용할 때만 쓸 수 있기 떄문에.
    // 애플리케이션 네트웍 용량 최적화가 생각보다 미비
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDTO> orderV4() {
        return orderRepository.findOrderDtos();
    }

    // api spec을 명확하게 규정하기 위해 사용하는 dto
    @Data
    public static class SimpleOrderQueryDTO { // 고객 주소가 아닌 배송지 정보
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        // dto는 entity를 참조해도 ok
        public SimpleOrderQueryDTO(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // LAZY 초기화

        }
    }
}

