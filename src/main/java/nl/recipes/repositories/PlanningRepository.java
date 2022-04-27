package nl.recipes.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nl.recipes.domain.Planning;

@Repository
public interface PlanningRepository extends JpaRepository<Planning, Long> {

  public Optional<Planning> findByOrderByDateAsc();

}
