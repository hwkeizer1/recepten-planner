package nl.recipes.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import nl.recipes.domain.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

  public Optional<Tag> findByName(String name);

  public List<Tag> findByOrderByNameAsc();

}
