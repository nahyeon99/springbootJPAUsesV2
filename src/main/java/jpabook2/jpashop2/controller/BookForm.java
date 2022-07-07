package jpabook2.jpashop2.controller;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BookForm {


    private Long id; // 상품 수정을 위한 id값
    private String name;
    private int price; // 가격
    private int stockQuantity; // 재고
    private String author;
    private String isbn;




}
