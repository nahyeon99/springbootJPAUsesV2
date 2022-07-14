package jpabook2.jpashop2.repository;

import jpabook2.jpashop2.domain.Address;
import jpabook2.jpashop2.domain.Order;
import jpabook2.jpashop2.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class OrderSimpleQueryDTO {
    // api spec을 명확하게 규정하기 위해 사용하는 dto

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        // dto는 entity를 참조해도 ok
        // jpql은 엔티티를 파라미터로 못 받기 때문에 (테이블로 인식해버림)
        // 직접 하나하나 써줘야 한다.
        public OrderSimpleQueryDTO(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address) {
            this.orderId = orderId;
            this.name = name;
            this.orderDate = orderDate;
            this.orderStatus = orderStatus;
            this.address = address;

        }
}
