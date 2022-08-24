package nl.recipes.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import nl.recipes.domain.ShoppingItem;

public interface ShoppingItemRepository extends JpaRepository<ShoppingItem, Long> {

  public List<ShoppingItem> findByOrderByNameAsc();
}
