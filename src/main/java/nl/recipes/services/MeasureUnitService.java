package nl.recipes.services;

import static nl.recipes.views.ViewMessages.MEASURE_UNIT_;
import static nl.recipes.views.ViewMessages.MEASURE_UNIT_NAME_CANNOT_BE_EMPTY;
import static nl.recipes.views.ViewMessages._ALREADY_EXISTS;
import static nl.recipes.views.ViewMessages._NOT_FOUND;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.recipes.domain.MeasureUnit;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.repositories.MeasureUnitRepository;

@Service
public class MeasureUnitService extends ListService<MeasureUnit> {


  public MeasureUnitService(MeasureUnitRepository measureUnitRepository) {
    repository = measureUnitRepository;
    Sort sort = Sort.by("name").ascending();
    observableList = FXCollections.observableList(repository.findAll(sort));
    comparator = (m1, m2) -> m1.getName().compareTo(m2.getName());
  }

  public Optional<MeasureUnit> findByName(String name) {
    return observableList.stream().filter(measureUnit -> name.equals(measureUnit.getName())).findAny();
  }

  public Optional<MeasureUnit> findById(Long id) {
    return observableList.stream().filter(measureUnit -> id.equals(measureUnit.getId())).findAny();
  }

  public MeasureUnit create(MeasureUnit measureUnit) throws AlreadyExistsException, IllegalValueException {
    if (measureUnit == null || measureUnit.getName() == null || measureUnit.getName().isEmpty()) {
      throw new IllegalValueException(MEASURE_UNIT_NAME_CANNOT_BE_EMPTY);
    }
    if (findByName(measureUnit.getName()).isPresent()) {
      throw new AlreadyExistsException(MEASURE_UNIT_ + measureUnit.getName() + _ALREADY_EXISTS);
    }
    return save(measureUnit);
  }

  public MeasureUnit update(MeasureUnit measureUnit, MeasureUnit update)
      throws NotFoundException, AlreadyExistsException {
    if (!findById(measureUnit.getId()).isPresent()) {
      throw new NotFoundException(MEASURE_UNIT_ + measureUnit.getName() + _NOT_FOUND);
    }
    if (!measureUnit.getName().equals(update.getName()) && findByName(update.getName()).isPresent()) {
      throw new AlreadyExistsException(MEASURE_UNIT_ + update.getName() + _ALREADY_EXISTS);
    }

    measureUnit.setName(update.getName());
    measureUnit.setPluralName(update.getPluralName());
    return update(measureUnit);
  }

  public void remove(MeasureUnit measureUnit) throws NotFoundException {
    if (!findById(measureUnit.getId()).isPresent()) {
      throw new NotFoundException(MEASURE_UNIT_ + measureUnit.getName() + _NOT_FOUND);
    }
    delete(measureUnit);
  }

  /**
   * Setter for JUnit testing only!
   * 
   * @param observableList
   */
  void setObservableList(ObservableList<MeasureUnit> observableList) {
    this.observableList = observableList;
  }

}
