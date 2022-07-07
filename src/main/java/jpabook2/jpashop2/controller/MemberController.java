package jpabook2.jpashop2.controller;

import jpabook2.jpashop2.domain.Address;
import jpabook2.jpashop2.domain.Member;
import jpabook2.jpashop2.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm()); // Controller에게 이 데이터(빈껍데기 멤버 객체)를 실어서 넘기게 된다.
        // 엔티티를 파라미터로 바로 넘기기엔 안맞는 부분들 이 있고, 유효성 검사 등 실무에선 거의 불가능하다.
        // form 데이터를 넘겨받아서 controller에서 한번 정제하는 것이 좋다.
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result) { // javax.validation이 @NotEmpty 등 다양한 기능을 사용할 수 있게 해준다.

        if (result.hasErrors()) {
            return "members/createMemberForm"; // BindingResult 덕분에 이 파일까지 스프링이 에러사항을 끌고 가서 뿌려줄 수 있음
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";
    }

    /**
     * 회원목록
     */
    @GetMapping("/members")
    public String list(Model model) { // model이란 객체를 통해 회원 목록을 전달

        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }
}
