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
import nl.recipes.util.testdata.MockMeasureUnits;

class MeasureUnitServiceTest {

  private static final String NEW_MEASUREUNIT = "NewMeasureUnit";

  private static final String NEW_MEASUREUNITS = "NewMeasureUnits";

  @Mock
  MeasureUnitRepository measureUnitRepository;

  @InjectMocks
  MeasureUnitService measureUnitService;

  MockMeasureUnits mockMeasureUnits;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    mockMeasureUnits = new MockMeasureUnits();
    measureUnitService.setObservableList(mockMeasureUnits.getMeasureUnitList());
  }

  @Test
  void testGetReadonlyMeasureUnitList() {
    List<MeasureUnit> expectedList = mockMeasureUnits.getMeasureUnitList();

    assertEquals(expectedList, measureUnitService.getList());
  }

  @Test
  void testFindByName_HappyPath() {
    Optional<MeasureUnit> expectedMeasureUnit = Optional.of(
        new MeasureUnit.MeasureUnitBuilder().withName("pot").withPluralName("potten").build(3L));

    assertEquals(expectedMeasureUnit, measureUnitService.findByName("pot"));
  }

  @Test
  void testFindByName_NotFound() {
    Optional<MeasureUnit> expectedMeasureUnit = Optional.empty();

    assertEquals(expectedMeasureUnit, measureUnitService.findByName("pott"));
  }

  @Test
  void testFindById_HappyPath() {
    Optional<MeasureUnit> expectedMeasureUnit = Optional.of(
        new MeasureUnit.MeasureUnitBuilder().withName("pot").withPluralName("potten").build(3L));

    assertEquals(expectedMeasureUnit, measureUnitService.findById(3L));
  }

  @Test
  void testFindById_NotFound() {
    Optional<MeasureUnit> expectedMeasureUnit = Optional.empty();

    assertEquals(expectedMeasureUnit, measureUnitService.findById(3000L));
  }

  @Test
  void testCreate_HappyPath() throws Exception {
    MeasureUnit measureUnit = new MeasureUnit.MeasureUnitBuilder().withName(NEW_MEASUREUNIT)
        .withPluralName(NEW_MEASUREUNITS).build();

    MeasureUnit savedMeasureUnit = new MeasureUnit.MeasureUnitBuilder().withName(NEW_MEASUREUNIT)
        .withPluralName(NEW_MEASUREUNITS).build(5L);

    when(measureUnitRepository.save(measureUnit)).thenReturn(savedMeasureUnit);

    assertEquals(savedMeasureUnit, measureUnitService.create(measureUnit));
    assertEquals(5, measureUnitService.getList().size());
    assertEquals(Optional.of(savedMeasureUnit), measureUnitService.findByName(NEW_MEASUREUNIT));
  }

  @Test
  void testCreate_AlreadyExistsException() throws Exception {
    MeasureUnit measureUnit = new MeasureUnit.MeasureUnitBuilder().withName("theelepel")
        .withPluralName("theelepels").build();

    AlreadyExistsException exception = Assertions.assertThrows(AlreadyExistsException.class, () -> {
      measureUnitService.create(measureUnit);
    });

    Assertions.assertEquals("Maateenheid theelepel bestaat al", exception.getMessage());
    assertEquals(4, measureUnitService.getList().size());
  }

  @Test
  void testCreate_IllegalValueException() throws Exception {
    MeasureUnit measureUnit = new MeasureUnit.MeasureUnitBuilder().withName("").build();

    IllegalValueException exception = Assertions.assertThrows(IllegalValueException.class, () -> {
      measureUnitService.create(measureUnit);
    });

    Assertions.assertEquals("Maateenheid mag niet leeg zijn", exception.getMessage());
    assertEquals(4, measureUnitService.getList().size());
  }

  @Test
  void testUpdate_HappyPath() throws Exception {
    MeasureUnit originalMeasureUnit = measureUnitService.findByName("eetlepel").get();
    MeasureUnit update = new MeasureUnit.MeasureUnitBuilder().withName("mespuntje")
        .withPluralName("mespuntjes").build();

    MeasureUnit expectedMeasureUnit = new MeasureUnit.MeasureUnitBuilder().withName("mespuntje")
        .withPluralName("mespuntjes").build(2L);

    when(measureUnitRepository.save(expectedMeasureUnit)).thenReturn(expectedMeasureUnit);

    assertEquals(expectedMeasureUnit, measureUnitService.update(originalMeasureUnit, update));
    assertEquals(4, measureUnitService.getList().size());
    assertEquals(Optional.of(expectedMeasureUnit), measureUnitService.findById(2L));
  }

  @Test
  void testUpdate_NotFoundException() throws Exception {
    MeasureUnit originalMeasureUnit = new MeasureUnit.MeasureUnitBuilder().withName("onbekend")
        .withPluralName("onbekenden").build(3000L);

    MeasureUnit update =
        new MeasureUnit.MeasureUnitBuilder().withName("bekend").withPluralName("bekenden").build();

    NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> {
      measureUnitService.update(originalMeasureUnit, update);
    });

    Assertions.assertEquals("Maateenheid onbekend niet gevonden", exception.getMessage());
    assertEquals(4, measureUnitService.getList().size());
  }

  @Test
  void testUpdate_AlreadyExistsException() throws Exception {
    MeasureUnit originalMeasureUnit =
        new MeasureUnit.MeasureUnitBuilder().withName("bakje").withPluralName("bakjes").build(1L);

    MeasureUnit update = new MeasureUnit.MeasureUnitBuilder().withName("theelepel")
        .withPluralName("theelepels").build();

    AlreadyExistsException exception = Assertions.assertThrows(AlreadyExistsException.class, () -> {
      measureUnitService.update(originalMeasureUnit, update);
    });

    Assertions.assertEquals("Maateenheid theelepel bestaat al", exception.getMessage());
    assertEquals(4, measureUnitService.getList().size());
  }

  @Test
  void testRemove_HappyPath() throws Exception {
    MeasureUnit originalMeasureUnit = measureUnitService.findByName("eetlepel").get();

    assertEquals(4, measureUnitService.getList().size());
    measureUnitService.remove(originalMeasureUnit);
    assertEquals(3, measureUnitService.getList().size());
    assertEquals(Optional.empty(), measureUnitService.findByName(originalMeasureUnit.getName()));
  }

  @Test
  void testRemove_NotFound() throws Exception {
    MeasureUnit originalMeasureUnit = new MeasureUnit.MeasureUnitBuilder().withName("onbekend")
        .withPluralName("onbekenden").build(3000L);

    NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> {
      measureUnitService.remove(originalMeasureUnit);
    });

    Assertions.assertEquals("Maateenheid onbekend niet gevonden", exception.getMessage());
    assertEquals(4, measureUnitService.getList().size());
  }

}
