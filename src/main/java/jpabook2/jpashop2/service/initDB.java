package jpabook2.jpashop2.service;

import jpabook2.jpashop2.domain.*;
import jpabook2.jpashop2.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;


/**
 * 총 주문 2개
* userA
 *      * JPA1 BOOK
 *      * JPA2 BOOK
*  userB
 *      * SPRING1 BOOK
 *      * SPRING2 BOOK
 */
@Component
@RequiredArgsConstructor
public class initDB {

    private final InitService initService;

    @PostConstruct // 스프링빈이 다 올라오면 스프링이 호출해준다는 애노테이션
    public void init() {
        // 여기에 InitService를 넣어줘도 되지만 굳이 transaction을 따로 먹여서 호출하는 이유는
        // spring life cycle이 있어서 transaction 등이 잘 작동이 안됨. 그래서 별도의 bean으로 등록하고 호출하는 방식으로 사용하는 것을 권장
        initService.dbInit1();
        initService.dbInit2();

    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        // sample data 바로 넣는 서비스

        private final EntityManager em;
        public void dbInit1() {
            Member member = createMember("userA", "서울", "1", "1111");
            em.persist(member);

            Book book1 = createBook("JPA1 Book", 10000, 1);
            em.persist(book1);

            Book book2 = createBook("JPA2 Book", 20000, 2);
            em.persist(book2);


            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 10000, 2);


            Delivery delivery = createDelivery(member);

            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        public void dbInit2() {
            Member member = createMember("userB", "진주", "2", "2222");
            em.persist(member);

            Book book1 = createBook("SPRING1 BOOK", 20000, 200);
            em.persist(book1);

            Book book2 = createBook("SPRING2 BOOK", 40000, 300);
            em.persist(book2);


            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 20000, 3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);


            Delivery delivery = createDelivery(member);

            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        private Delivery createDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return delivery;
        }

        private Book createBook(String name, int price, int stockQuantity) {
            Book book1 = new Book();
            book1.setName(name);
            book1.setPrice(price);
            book1.setStockQuantity(stockQuantity);
            return book1;
        }

        private Member createMember(String name, String city, String street, String zipcode) {
            Member member = new Member();
            member.setName(name);
            member.setAddress(new Address(city, street, zipcode));
            return member;
        }
    }


}

