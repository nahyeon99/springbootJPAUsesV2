package jpabook2.jpashop2.repository;

import jpabook2.jpashop2.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
        if(item.getId() == null) {
            // item은 JPA에 저장되기 전까지 id값이 없으므로 id 값이 없다는 것은 완전히 새로 생성하는 객체라는 뜻
            em.persist(item);

        } else {
            // 반면에 Id 값이 있다는 것은 이미 db에 등록되있고 어디선가 가져온다는 뜻.
            // 여기서 save는 Update는 아니지만 update 와 비슷한 것이라 보면 된다.
            em.merge(item); // update 비슷한 것
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
