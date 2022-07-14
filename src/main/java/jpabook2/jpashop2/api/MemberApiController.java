package jpabook2.jpashop2.api;

import jpabook2.jpashop2.domain.Member;
import jpabook2.jpashop2.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    /**
     * 회원 조회
     */
    @GetMapping("/api/v1/members")
    public List<Member> memberV1() {
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result memberV2() {
        List<Member> findMembers = memberService.findMembers();
        // MemberDto가 object로 감싸서 내보냄
        List<MemberDto> collect = findMembers.stream() // List<Member>를 List<MemberDto>로 변경
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());
        return new Result(collect);
//        return new Result(collect.size(), collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
//        private int count;
        private T data; // data field의 값은 List가 나가게 될 것
        // List(컬렉션)를 바로 나가게 하면 json의 배열 타입으로 나가게 되기 때문에 제네릭 타입으로 한 번 감싸줘야 한다.
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name; // api spec으로 노출할 것만 dto에 작성
        // 엔티티의 전체적인 것을 노출하는 것이 아닌 보여줘야할 필요가 있는 것들만 보여주게끔
    }

    /**
     * 회원 생성
     */

    /*
         쿼리 방식 선택 권장 순서
         1. 우선 엔티티를 DTO로 변환하는 방법을 선택
         2. 필요하면 패치 조인으로 성능을 최적화 한다. -> 대부분의 성능 이슈가 해결
         3. 그래도 안되면 DTO로 직접 조회하는 방법을 사용
         4. 최후의 방법은 JPA가 제공하는 네이티브 SQL이나 스프링 JDBC Template을 사용해서 SQL을 직접 사용한다.
     */

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {

        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request) {

        // 수정할 때는 변경감지를 사용 권장
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    static class CreateMemberRequest {
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }


    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }
}
