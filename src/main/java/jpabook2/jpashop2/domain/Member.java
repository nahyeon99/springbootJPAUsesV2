package jpabook2.jpashop2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name="member_id") // 테이블의 column 명 설정
    private Long id;

    @NotEmpty
    private String name;

    @Embedded
    private Address address;

//    @JsonIgnore
//    엔티티에 화면을 표현하기 위한 프레젠테이션 계층을 위한 로직이 엔티티에 들어오는 것을 권장하지 않음
//    엔티티가 변경되면 API 스펙이 변한다. (차후 장애 발생원인)
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

}

