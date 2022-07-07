package jpabook2.jpashop2.domain.item;

import jpabook2.jpashop2.domain.Category;
import jpabook2.jpashop2.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public abstract class Item {

    @Id @GeneratedValue
    @Column(name="item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    // 핵심 비지니스 로직
    // 데이터를 가지고 있는 쪽에 핵심 비지니스 로직을 가지고 있는 것이 응집력이 좋고, 관리하기 좋다.
    // 핵심비지니스 로직을 엔티티에 직접 넣음

    /**
     * stock 증가
     */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    /**
     * stock 감소
     */
    public void removeStock(int orderQuantity) {
        int restStock = this.stockQuantity - orderQuantity;
        if(restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;

    }

}
