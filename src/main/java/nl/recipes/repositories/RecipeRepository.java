package nl.recipes.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nl.recipes.domain.Recipe;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

  public Optional<Recipe> findByName(String name);

  public List<Recipe> findByOrderByNameAsc();

}
