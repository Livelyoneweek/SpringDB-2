package hello.itemservice.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor //jpa는 기본생성자 필수
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //IDENTITY는 H2
    private Long id;

    @Column(name= "item_name", length = 10)
    private String itemName;
    private Integer price;
    private Integer quantity;
    

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
