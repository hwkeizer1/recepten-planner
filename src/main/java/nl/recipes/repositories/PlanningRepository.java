package nl.recipes.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import nl.recipes.domain.Planning;

public interface PlanningRepository extends JpaRepository<Planning, Long> {

  public Optional<Planning> findByOrderByDateAsc();

}
