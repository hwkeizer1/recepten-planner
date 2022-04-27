package nl.recipes.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import nl.recipes.domain.MeasureUnit;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.repositories.MeasureUnitRepository;
import nl.recipes.util.TestData;

class MeasureUnitServiceTest {

  private static final String NEW_MEASUREUNIT = "NewMeasureUnit";

  private static final String NEW_MEASUREUNITS = "NewMeasureUnits";

  @Mock
  MeasureUnitRepository measureUnitRepository;

  @InjectMocks
  MeasureUnitService measureUnitService;

  TestData testData;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    testData = new TestData();
    measureUnitService.setObservableMeasureUnitList(testData.getMeasureUnitList());
  }

  @Test
  void testGetReadonlyMeasureUnitList() {
    List<MeasureUnit> expectedList = testData.getMeasureUnitList();

    assertEquals(expectedList, measureUnitService.getReadonlyMeasureUnitList());
  }

  @Test
  void testFindByName_HappyPath() {
    Optional<MeasureUnit> expectedMeasureUnit =
        Optional.of(testData.getMeasureUnit(3L, "pot", "potten"));

    assertEquals(expectedMeasureUnit, measureUnitService.findByName("pot"));
  }

  @Test
  void testFindByName_NotFound() {
    Optional<MeasureUnit> expectedMeasureUnit = Optional.empty();

    assertEquals(expectedMeasureUnit, measureUnitService.findByName("pott"));
  }

  @Test
  void testFindById_HappyPath() {
    Optional<MeasureUnit> expectedMeasureUnit =
        Optional.of(testData.getMeasureUnit(3L, "pot", "potten"));

    assertEquals(expectedMeasureUnit, measureUnitService.findById(3L));
  }

  @Test
  void testFindById_NotFound() {
    Optional<MeasureUnit> expectedMeasureUnit = Optional.empty();

    assertEquals(expectedMeasureUnit, measureUnitService.findById(3000L));
  }

  @Test
  void testCreate_HappyPath() throws Exception {
    MeasureUnit measureUnit = new MeasureUnit();
    measureUnit.setName(NEW_MEASUREUNIT);
    measureUnit.setPluralName(NEW_MEASUREUNITS);
    MeasureUnit savedMeasureUnit = testData.getMeasureUnit(5L, NEW_MEASUREUNIT, NEW_MEASUREUNITS);
    when(measureUnitRepository.save(measureUnit)).thenReturn(savedMeasureUnit);

    assertEquals(savedMeasureUnit, measureUnitService.create(measureUnit));
    assertEquals(5, measureUnitService.getReadonlyMeasureUnitList().size());
    assertEquals(Optional.of(savedMeasureUnit), measureUnitService.findByName(NEW_MEASUREUNIT));
  }

  @Test
  void testCreate_AlreadyExistsException() throws Exception {
    MeasureUnit measureUnit = new MeasureUnit();
    measureUnit.setName("theelepel");
    measureUnit.setPluralName("theelepels");

    AlreadyExistsException exception = Assertions.assertThrows(AlreadyExistsException.class, () -> {
      measureUnitService.create(measureUnit);
    });

    Assertions.assertEquals("Maateenheid theelepel bestaat al", exception.getMessage());
    assertEquals(4, measureUnitService.getReadonlyMeasureUnitList().size());
  }

  @Test
  void testCreate_IllegalValueException() throws Exception {
    MeasureUnit measureUnit = new MeasureUnit();
    measureUnit.setName("");

    IllegalValueException exception = Assertions.assertThrows(IllegalValueException.class, () -> {
      measureUnitService.create(measureUnit);
    });

    Assertions.assertEquals("Maateenheid naam mag niet leeg zijn", exception.getMessage());
    assertEquals(4, measureUnitService.getReadonlyMeasureUnitList().size());
  }

  @Test
  void testUpdate_HappyPath() throws Exception {
    MeasureUnit originalMeasureUnit = measureUnitService.findByName("eetlepel").get();
    MeasureUnit update = new MeasureUnit();
    update.setName("mespuntje");
    update.setPluralName("mespuntjes");

    MeasureUnit expectedMeasureUnit = testData.getMeasureUnit(2L, "mespuntje", "mespuntjes");
    when(measureUnitRepository.save(expectedMeasureUnit)).thenReturn(expectedMeasureUnit);

    assertEquals(expectedMeasureUnit, measureUnitService.update(originalMeasureUnit, update));
    assertEquals(4, measureUnitService.getReadonlyMeasureUnitList().size());
    assertEquals(Optional.of(expectedMeasureUnit), measureUnitService.findById(2L));
  }

  @Test
  void testUpdate_NotFoundException() throws Exception {
    MeasureUnit originalMeasureUnit = testData.getMeasureUnit(3000L, "onbekend", "onbekenden");
    MeasureUnit update = new MeasureUnit();
    update.setName("bekend");
    update.setPluralName("bekenden");

    NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> {
      measureUnitService.update(originalMeasureUnit, update);
    });

    Assertions.assertEquals("Maateenheid onbekend niet gevonden", exception.getMessage());
    assertEquals(4, measureUnitService.getReadonlyMeasureUnitList().size());
  }

  @Test
  void testUpdate_AlreadyExistsException() throws Exception {
    MeasureUnit originalMeasureUnit = testData.getMeasureUnit(1L, "bakje", "bakjes");
    MeasureUnit update = new MeasureUnit();
    update.setName("theelepel");
    update.setPluralName("theelepels");

    AlreadyExistsException exception = Assertions.assertThrows(AlreadyExistsException.class, () -> {
      measureUnitService.update(originalMeasureUnit, update);
    });

    Assertions.assertEquals("Maateenheid theelepel bestaat al", exception.getMessage());
    assertEquals(4, measureUnitService.getReadonlyMeasureUnitList().size());
  }

  @Test
  void testRemove_HappyPath() throws Exception {
    MeasureUnit originalMeasureUnit = measureUnitService.findByName("eetlepel").get();

    assertEquals(4, measureUnitService.getReadonlyMeasureUnitList().size());
    measureUnitService.remove(originalMeasureUnit);
    assertEquals(3, measureUnitService.getReadonlyMeasureUnitList().size());
    assertEquals(Optional.empty(), measureUnitService.findByName(originalMeasureUnit.getName()));
  }

  @Test
  void testRemove_NotFound() throws Exception {
    MeasureUnit originalMeasureUnit = testData.getMeasureUnit(3000L, "onbekend", "onbekenden");

    NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> {
      measureUnitService.remove(originalMeasureUnit);
    });

    Assertions.assertEquals("Maateenheid onbekend niet gevonden", exception.getMessage());
    assertEquals(4, measureUnitService.getReadonlyMeasureUnitList().size());
  }

}
