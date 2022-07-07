package jpabook2.jpashop2.service;

import jpabook2.jpashop2.domain.item.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemUpdateTest {

    @PersistenceContext
    EntityManager em;

    @Test
    public void updateTest() throws Exception {

        Book book = em.find(Book.class, 1L);

        book.setName("adadad");

        // 변경감지 == dirty checking
        // 이 메커니즘으로 JPA의 엔티티를 기본적으로 데이터를 update 할 수 있다.

    }
}
