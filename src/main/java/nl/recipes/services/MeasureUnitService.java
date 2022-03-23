package nl.recipes.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import nl.recipes.domain.MeasureUnit;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.repositories.MeasureUnitRepository;

@Service
public class MeasureUnitService {

	private final MeasureUnitRepository measureUnitRepository;
	
	private ObservableList<MeasureUnit> observableMeasureUnitList;

	public MeasureUnitService(MeasureUnitRepository measureUnitRepository) {
		this.measureUnitRepository = measureUnitRepository;
		observableMeasureUnitList = FXCollections.observableList(measureUnitRepository.findByOrderByNameAsc());
	}
	
	public ObservableList<MeasureUnit> getReadonlyMeasureUnitList() {
		return FXCollections.unmodifiableObservableList(observableMeasureUnitList);
	}
	
	public MeasureUnit create(MeasureUnit measureUnit) throws AlreadyExistsException, IllegalValueException {
		if (measureUnit == null || measureUnit.getName() == null || measureUnit.getName().isEmpty()) {
			throw new IllegalValueException("Maateenheid naam mag niet leeg zijn");
		}
		if (findByName(measureUnit.getName()).isPresent()) {	
			throw new AlreadyExistsException("Maateenheid " + measureUnit.getName() + " bestaat al");
		}
		MeasureUnit createdMeasureUnit = measureUnitRepository.save(measureUnit);
		observableMeasureUnitList.add(createdMeasureUnit);
		return createdMeasureUnit;
	}
	
	public MeasureUnit update(MeasureUnit measureUnit, MeasureUnit update) throws NotFoundException, AlreadyExistsException {
		if (!findById(measureUnit.getId()).isPresent()) {
			throw new NotFoundException("Maateenheid " + measureUnit.getName() + " niet gevonden");
		}
		if (!measureUnit.getName().equals(update.getName()) && findByName(update.getName()).isPresent()) {
			throw new AlreadyExistsException("Maateenheid " + update.getName() + " bestaat al");
		}
		measureUnit.setName(update.getName());
		measureUnit.setPluralName(update.getPluralName());
		
		MeasureUnit updatedMeasureUnit = measureUnitRepository.save(measureUnit);
		observableMeasureUnitList.set(observableMeasureUnitList.lastIndexOf(measureUnit), updatedMeasureUnit);
		return updatedMeasureUnit;
	}
	
	public void remove(MeasureUnit measureUnit) throws NotFoundException {
		// TODO add check for removing measureUnits that are in use
		if (!findById(measureUnit.getId()).isPresent()) {
			throw new NotFoundException("Maateenheid " + measureUnit.getName() + " niet gevonden");
		}
		measureUnitRepository.delete(measureUnit);
		observableMeasureUnitList.remove(measureUnit);
	}
	
	public Optional<MeasureUnit> findByName(String name) {
		return observableMeasureUnitList.stream()
				.filter(measureUnit -> name.equals(measureUnit.getName()))
				.findAny();
	}
	
	public Optional<MeasureUnit> findById(Long id) {
		return observableMeasureUnitList.stream()
				.filter(measureUnit -> id.equals(measureUnit.getId()))
				.findAny();
	}
	
	public void addListener(ListChangeListener<MeasureUnit> listener) {
		observableMeasureUnitList.addListener(listener);
	}
	
	public void removeChangeListener(ListChangeListener<MeasureUnit> listener) {
		observableMeasureUnitList.removeListener(listener);
	}
	
	// Setter for JUnit testing only
	void setObservableMeasureUnitList(ObservableList<MeasureUnit> observableMeasureUnitList) {
		this.observableMeasureUnitList = observableMeasureUnitList;
	}
}
