package jpabook2.jpashop2.service;

import jpabook2.jpashop2.domain.item.Book;
import jpabook2.jpashop2.domain.item.Item;
import jpabook2.jpashop2.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // 저장이 안된다.
@RequiredArgsConstructor
public class ItemService {

    // ItemService는 itemRepository에 위임만 하는 클래스
    // 개발이 단순하게 끝나버린다. 그러나 경우에 따라서 위임만 하는 클래스를 만들어야 할필요가 있는지 생각해볼 필요가 있다.
    // controller에서 itemRepository에 직접 접근해서 사용할 수도 있기 때문에.

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity) {
        Item findItem = itemRepository.findOne(itemId); // id로 영속성 컨텍스트에서 실제 아이템을 찾아옴(영속상황)
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity);

        itemRepository.save(findItem);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
