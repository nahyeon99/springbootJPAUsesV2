package jpabook2.jpashop2.service;

import jpabook2.jpashop2.domain.Member;
import jpabook2.jpashop2.repository.MemberRepository;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

// 완전히 spring과 integration을 해서 테스트를 진행할 것
@RunWith(SpringRunner.class) // Junit 실행할 때 Spring이랑 엮어서 실행할래 in Junit4
@SpringBootTest // spring boot를 띄운 상태에서 테스트 실행할거야, 이거 있어야 @Autowired 등 사용할 수 있다.
@Transactional // JPA에서 같은 transaction 안에서 같은 entity(PK 값이 같다면)라면, 같은 영속성 컨텍스트에서 하나로 관리된다.
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @PersistenceContext EntityManager em;

    @Test
//    @Rollback(value = false) // @Transactional 이 기본 true로 rollback이 default인데 insert문을 확인하고 싶다면 false로 설정하고 확인
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("kim");

        //when
        Long savedId = memberService.join(member);

        //then
        em.flush(); // 영속성 컨텍스트에 member 객체가 들어가있는 상황인데 flush()를 함으로써 영속성 컨텍스트에 있는 변경사항을 db에 반영되게 된다., db에 쿼리가 강제로 나가는 것
        Assertions.assertEquals(member, memberRepository.findOne(savedId)); // 고로 true가 나온다.
    }

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_검증() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        //when
        memberService.join(member1);
        memberService.join(member2); // 예외가 발생해야 한다.
//        try {
//            memberService.join(member2); // 예외가 발생해야 한다.
//        } catch(IllegalStateException e) {
//            return;
//        }

        //then
        Assertions.fail("예외가 발생해야 한다."); // 이전 코드에서 필터링 됐어야 했는데 여기 오면 문제가 있는 것
    }
}