//package jpabook2.jpashop2;
//
//import jpabook2.jpashop2.domain.Member;
//import jpabook2.jpashop2.repository.MemberRepository;
//import org.assertj.core.api.Assertions;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class MemberRepositoryTest {
//
//    @Autowired
//    MemberRepository memberRepository;
//
//    @PersistenceContext
//    EntityManager em;
//
//    @Test
//    @Transactional
//    @Rollback(value = false)
//    public void testMember() {
//        Member member = new Member();
//        member.setUsername("nahyeon");
//        Long saveId = memberRepository.save(member);
//
//        Member findMember = memberRepository.find(saveId);
//
//        Assertions.assertThat(findMember).isEqualTo(member);
//    }
//
//}