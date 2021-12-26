package de.tiefigames.bluebrixxcrawler.repository;

import de.tiefigames.bluebrixxcrawler.entity.Notify;
import de.tiefigames.bluebrixxcrawler.entity.Product;
import org.springframework.data.repository.CrudRepository;

public interface NotifyRepository extends CrudRepository<Notify, Integer> {

    Notify findByProduct(Product product);
}
