package nl.recipes.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nl.recipes.domain.MeasureUnit;

@Repository
public interface MeasureUnitRepository extends JpaRepository<MeasureUnit, Long>{

	public Optional<MeasureUnit> findByName(String name);
	
	public List<MeasureUnit> findByOrderByNameAsc();
}
