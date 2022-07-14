package jpabook2.jpashop2.repository;

import jpabook2.jpashop2.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);

    }

    // 주문 단건조회
    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    // 주문 전체조회
    // 동적쿼리에서 파라미터 조건에 있으면 where문으로 검색이 될 것
    // orderSearch에 memberName과 orderStatus parameter가 있고
    // 만약 검색어가 없다면 전체 조회가 될거고, 검색어가 있다면 해당 조건에 맞춰서 검색이 될텐데
    // 이런 동적쿼리를 어떻게 처리해줄 것인가, 만만치가 않음(JPA에서 해결해야할 질문_ 마이바티스에선 이런 동적쿼리를 xml로 처리할 수 있는 방법이 있음)


    public List<Order> findAll(OrderSearch orderSearch) {

        String jpql = "select o from Order o join o.member";

        return em.createQuery(jpql, Order.class)
                .getResultList();

//        return em.createQuery("select o from Order o join o.member m" +
//                " where o.status = :status " +
//                " and m.name like : name", Order.class)
//                .setParameter("status", orderSearch.getOrderStatus())
//                .setParameter("name", orderSearch.getMemberName())
//                .setMaxResults(1000) // 최대 1000건 페이징
//                .getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        // JPA에만 있는 문법인 fetch join
        // 를 이용하여 order, member, delivery를 join해서 select로 한 번에 끌고 오는 JPQL 문

        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
        ).getResultList();
    }

    public List<OrderSimpleQueryDTO> findOrderDtos() {
        return em.createQuery(
                        "select new jpabook2.jpashop2.repository.OrderSimpleQueryDTO(o.id, m.name, o.orderDate, o.status, d.address)" +
                                " from Order o" +
                                " join o.member m" +
                    " join o.delivery d", OrderSimpleQueryDTO.class)
                    .getResultList();
    }


    public List<Order> findAllWithItem() {
        // fetch join 하면서 데이터가 뻥튀기 됨
        // distinct keyword를 사용함으로써 orderId의 중복 시 중복된 row를 날려준다.
        // 1. db의 distinct 키워드를 날려준다.
        // 2. root entity(아래 경우 Order)가 중복인 경우 중복을 걸러서 collection에 담아준다.
        return em.createQuery(
                "select distinct o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d" +
                        " join fetch o.orderItems oi" +
                        " join fetch oi.item i", Order.class)
                .getResultList();
    }
}
