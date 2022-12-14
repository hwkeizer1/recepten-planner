package nl.recipes.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import nl.recipes.domain.IngredientName;

public interface IngredientNameRepository extends JpaRepository<IngredientName, Long> {

  public Optional<IngredientName> findByName(String name);

  public List<IngredientName> findByOrderByNameAsc();

}
