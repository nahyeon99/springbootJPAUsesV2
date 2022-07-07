package jpabook2.jpashop2.repository;

import jpabook2.jpashop2.domain.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderSearch {

    // 동적쿼리에서 파라미터 조건에 있으면 where문으로 검색이 될 것

    private String memberName; // 회원 이름
    private OrderStatus orderStatus; // 주문 상태[ORDER, CANCEL]
}
