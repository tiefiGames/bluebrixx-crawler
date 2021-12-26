package de.tiefigames.bluebrixxcrawler.repository;

import de.tiefigames.bluebrixxcrawler.entity.ChangeHistory;
import org.springframework.data.repository.CrudRepository;

public interface ChangeHistoryRepository extends CrudRepository<ChangeHistory, Integer> {
}
