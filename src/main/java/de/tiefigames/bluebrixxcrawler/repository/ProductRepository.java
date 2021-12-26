package de.tiefigames.bluebrixxcrawler.repository;

import de.tiefigames.bluebrixxcrawler.entity.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Integer> {

    Product findProductByUrl(String url);

}
