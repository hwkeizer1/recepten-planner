package nl.recipes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import nl.recipes.domain.Ingredient;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

}
