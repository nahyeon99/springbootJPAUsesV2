package jpabook2.jpashop2.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;


    // CascadeType.ALL 이라는 뜻은
//    Order가 persist 될 때 자동으로 orderItems도 persist 된다는 뜻
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

//    CascadeType.ALL 기능
//    persist(orderItemA) // default는 entity 당 각자 영속화를 해주어야 함
//    persist(orderItemB)
//    persist(orderItemC)
//    persist(order)
//
//    설정시
//    persist(order) // 전부 영속화 가능


    // 마찬가지로 Cascade.ALL 이란 뜻은
    // Order가 persist 될 때 자동으로 delivery도 Persist 된다는 뜻
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id") // 연관관계의 주인
    private Delivery delivery;

    private LocalDateTime orderDate; // 주문 시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상세 [ORDER, CANCEL]


    //==연관관계 메서드==//
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);

    }


    // 핵심 비지니스 로직을 만들건데, 주문이기 때문에 가장 중요한게 생성 메서드!

    // 주문 생성은 복잡함. Order, OrderItem, Delivery 그리고 연관관계도 있어야 하므로 복잡해진다.
    // 그래서 이런 복잡한 생성은 별도의 생성메서드가 있으면 좋다.
    // 앞으로 생성하는 지점을 변경해야 한다면 createOrder만 변경해줘야하기 떄문에 좋다.
    //== 생성 메서드==//
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) { // 주문 아이템이 여러 개 있을 수 있으므로 ... 문법 사용
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);

        for(OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER); // 처음 생성됐을 때 order 상태를 ORDER로 강제해 놓을 것
        order.setOrderDate(LocalDateTime.now());

        return order;
    }

    //==비지니스 로직==//
    /**
     * 주문 취소
     */
    public void cancel() {
        // 이미 배송이 완료된 주문은 취소 불가능하다는 로직
        if(delivery.getStatus() == DeliveryStatus.COMP) { // 배송 완료 돼버리면
            // IllegalStateException: RuntimeException으로 메소드가 요구된 처리를 하기에 적합한 상태에 있지 않을 때
            throw new IllegalStateException("이미 완료된 상품은 취소가 불가능합니다.");
        }

// status를 변경하기만하고 db에 따로 업데이트 해놓은 것이 없는데, 어떻게 db까지 변경되었나면 dirty checking(변경 감지)이 일어났기 때문이다.
        // flush 할 때 dirty checking이 일어난다.
        this.setStatus(OrderStatus.CANCEL);

        for(OrderItem orderItem : orderItems) {
            orderItem.cancel(); // OrderItem에서 재고을 원복시켜준다.
        }
    }

    //==조회 로직==//
    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice() {
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

}
