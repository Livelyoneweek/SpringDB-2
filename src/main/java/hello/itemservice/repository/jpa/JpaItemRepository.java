package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Transactional //jpa 모든 데이터 변경은 트랜잭셔널 안에서 이뤄저야함
public class JpaItemRepository implements ItemRepository {

    private final EntityManager em; // 스프링에서 자동으로 만들어줌 (데이터소스 넣어주거나 하는등)

    public JpaItemRepository(EntityManager em) {
        this.em=em;
    }

    @Override
    public Item save(Item item) {
        em.persist(item);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        Item findItem = em.find(Item.class, itemId);
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
        //영속성 컨텍스트에 초기 스냅샷을 가지고있기에 변경만 하면 트랜잭셔널 커밋된 순간에 변경감지해서 업데이트 쿼리 자동으로 날라감
    }

    @Override
    public Optional<Item> findById(Long id) {
        Item item = em.find(Item.class, id);

        return Optional.ofNullable(item);
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {

        String jpql = "select i from Item i";

//        List<Item> result = em.createQuery(jpql, Item.class).getResultList()

        Integer maxPrice = cond.getMaxPrice();
        String itemName = cond.getItemName();

        if (StringUtils.hasText(itemName) || maxPrice != null) {
            jpql += " where";
        }

        boolean andFlag = false;
        if (StringUtils.hasText(itemName)) {
            jpql += " i.itemName like concat('%',:itemName,'%')";
            andFlag = true;
        }
        if (maxPrice != null) {
            if (andFlag) {
                jpql += " and";
            }
            jpql += " i.price <= :maxPrice";
        }

        log.info("jpql={}", jpql);

        TypedQuery<Item> query = em.createQuery(jpql, Item.class);

        if (StringUtils.hasText(itemName)) {
            query.setParameter("itemName", itemName);
        }

        if (maxPrice != null) {
            query.setParameter("maxPrice", maxPrice);
        }

        return query.getResultList();
    }
}
