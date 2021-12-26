package de.tiefigames.bluebrixxcrawler.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    private String name;

    private String setNumber;

    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    private String url;

    private LocalDateTime created;
}
