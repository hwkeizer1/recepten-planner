package nl.recipes.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import nl.recipes.domain.MeasureUnit;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.repositories.MeasureUnitRepository;

@Service
public class MeasureUnitService {

	private final MeasureUnitRepository measureUnitRepository;

	public MeasureUnitService(MeasureUnitRepository measureUnitRepository) {
		this.measureUnitRepository = measureUnitRepository;
	}
	
	public List<MeasureUnit> findAll() {
		return measureUnitRepository.findAll();
	}
	
	public MeasureUnit create(MeasureUnit measureUnit) throws AlreadyExistsException {
		if (measureUnitRepository.findByName(measureUnit.getName()).isPresent()) {	
			throw new AlreadyExistsException("Maateenheid " + measureUnit.getName() + " bestaat al");
		}
		return measureUnitRepository.save(measureUnit);
	}
	
	public Optional<MeasureUnit> findByName(String name) {
		return measureUnitRepository.findByName(name);
	}
}
