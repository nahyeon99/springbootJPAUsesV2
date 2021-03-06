package jpabook2.jpashop2.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class MemberForm {

    @NotEmpty(message = "회원 이름은 필수 입니다.") // javax.validation을 통해서 스프링이 validation을 해준다.
    private String name;

    private String city;
    private String street;
    private String zipcode;
}
