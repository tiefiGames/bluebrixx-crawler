package de.tiefigames.bluebrixxcrawler.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Notify {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    @OneToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    private LocalDateTime created;
}
