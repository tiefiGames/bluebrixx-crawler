package de.tiefigames.bluebrixxcrawler.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class ChangeHistory {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    @OneToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @Enumerated(EnumType.STRING)
    private ProductStatus oldProductstatus;

    @Enumerated(EnumType.STRING)
    private ProductStatus newProductstatus;

    private LocalDateTime created;




}
