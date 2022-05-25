package nl.recipes.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import lombok.extern.slf4j.Slf4j;
import nl.recipes.domain.IngredientName;
import nl.recipes.domain.IngredientType;
import nl.recipes.domain.ShopType;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.repositories.IngredientNameRepository;
import nl.recipes.util.testdata.MockIngredientNames;

@Slf4j
class IngredientNameServiceTest {

  private static final String NEW_INGREDIENTNAME = "NewIngredientName";

  private static final String NEW_INGREDIENTNAMES = "NewIngredientNames";

  @Mock
  IngredientNameRepository ingredientNameRepository;

  @InjectMocks
  IngredientNameService ingredientNameService;

  MockIngredientNames mockIngredientNames;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    mockIngredientNames = new MockIngredientNames();
    ingredientNameService.setObservableIngredientNameList(mockIngredientNames.getIngredientNameList());
  }

  @Test
  void testGetReadonlyIngredientNameList() {
    List<IngredientName> expectedList = mockIngredientNames.getIngredientNameList();

    assertEquals(expectedList, ingredientNameService.getReadonlyIngredientNameList());
  }

  @Test
  void testFindByName_HappyPath() {
    Optional<IngredientName> expectedIngredientName = Optional.of(mockIngredientNames.getIngredientName(3L, null,
        "prei", "preien", false, ShopType.DEKA, IngredientType.GROENTE));

    assertEquals(expectedIngredientName, ingredientNameService.findByName("prei"));
  }

  @Test
  void testFindByName_NotFound() {
    Optional<IngredientName> expectedIngredientName = Optional.empty();

    assertEquals(expectedIngredientName, ingredientNameService.findByName("prij"));
  }

  @Test
  void testFindById_HappyPath() {
    Optional<IngredientName> expectedIngredientName = Optional.of(mockIngredientNames.getIngredientName(3L, null,
        "prei", "preien", false, ShopType.DEKA, IngredientType.GROENTE));

    assertEquals(expectedIngredientName, ingredientNameService.findById(3L));
  }

  @Test
  void testFindById_NotFound() {
    Optional<IngredientName> expectedIngredientName = Optional.empty();

    assertEquals(expectedIngredientName, ingredientNameService.findById(3000L));
  }

  @Test
  void testCreate_HappyPath() throws Exception {
    IngredientName ingredientName = new IngredientName.IngredientNameBuilder()
        .withName(NEW_INGREDIENTNAME)
        .withPluralName(NEW_INGREDIENTNAMES)
        .withStock(true)
        .withShopType(ShopType.MARKT)
        .withIngredientType(IngredientType.OVERIG)
        .build();

    IngredientName savedIngredientName = mockIngredientNames.getIngredientName(5L, null, NEW_INGREDIENTNAME,
        NEW_INGREDIENTNAMES, true, ShopType.MARKT, IngredientType.OVERIG);
    when(ingredientNameRepository.save(ingredientName)).thenReturn(savedIngredientName);

    assertEquals(savedIngredientName, ingredientNameService.create(ingredientName));
    assertEquals(5, ingredientNameService.getReadonlyIngredientNameList().size());
    assertEquals(Optional.of(savedIngredientName),
        ingredientNameService.findByName(NEW_INGREDIENTNAME));
  }

  @Test
  void testCreate_AlreadyExistsException() throws Exception {
    IngredientName ingredientName = new IngredientName.IngredientNameBuilder().withName("water").build();

    AlreadyExistsException exception = Assertions.assertThrows(AlreadyExistsException.class, () -> {
      ingredientNameService.create(ingredientName);
    });

    Assertions.assertEquals("Ingrediënt naam water bestaat al", exception.getMessage());
    assertEquals(4, ingredientNameService.getReadonlyIngredientNameList().size());
  }

  @Test
  void testCreate_IllegalValueException() throws Exception {
    IngredientName ingredientName = new IngredientName.IngredientNameBuilder().withName("").build();

    IllegalValueException exception = Assertions.assertThrows(IllegalValueException.class, () -> {
      ingredientNameService.create(ingredientName);
    });

    Assertions.assertEquals("Ingrediënt naam mag niet leeg zijn", exception.getMessage());
    assertEquals(4, ingredientNameService.getReadonlyIngredientNameList().size());
  }

  @Test
  void testUpdate_HappyPath() throws Exception {
    IngredientName originalIngredientName = ingredientNameService.findByName("ui").get();
    IngredientName update = new IngredientName.IngredientNameBuilder()
        .withName("wortel")
        .withPluralName("wortels")
        .withStock(false)
        .withShopType(ShopType.EKO)
        .withIngredientType(IngredientType.GROENTE)
        .build();

    IngredientName expectedIngredientName = mockIngredientNames.getIngredientName(2L, null, "wortel", "wortels",
        false, ShopType.EKO, IngredientType.GROENTE);
    when(ingredientNameRepository.save(expectedIngredientName)).thenReturn(expectedIngredientName);

    log.debug("expectedIngredientName: {}", expectedIngredientName);
    log.debug("update: {}", ingredientNameService.update(originalIngredientName, update));
    assertEquals(expectedIngredientName,
        ingredientNameService.update(originalIngredientName, update));
    assertEquals(4, ingredientNameService.getReadonlyIngredientNameList().size());
    assertEquals(Optional.of(expectedIngredientName), ingredientNameService.findById(2L));
  }

  @Test
  void testUpdate_NotFoundException() throws Exception {
    IngredientName originalIngredientName = mockIngredientNames.getIngredientName(3000L, null, "onbekend",
        "onbekenden", true, ShopType.DEKA, IngredientType.GROENTE);
    IngredientName update = new IngredientName.IngredientNameBuilder()
        .withName("bekend")
        .withPluralName("bekenden")
        .build();

    NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> {
      ingredientNameService.update(originalIngredientName, update);
    });

    Assertions.assertEquals("Ingrediënt naam onbekend niet gevonden", exception.getMessage());
    assertEquals(4, ingredientNameService.getReadonlyIngredientNameList().size());
  }

  @Test
  void testUpdate_AlreadyExistsException() throws Exception {
    IngredientName originalIngredientName =
        mockIngredientNames.getIngredientName(1L, null, "water", "water", true, null, IngredientType.OVERIG);
    IngredientName update = new IngredientName.IngredientNameBuilder()
        .withName("mozzarella")
        .withPluralName("mozzarella")
        .build();

    AlreadyExistsException exception = Assertions.assertThrows(AlreadyExistsException.class, () -> {
      ingredientNameService.update(originalIngredientName, update);
    });

    Assertions.assertEquals("Ingrediënt naam mozzarella bestaat al", exception.getMessage());
    assertEquals(4, ingredientNameService.getReadonlyIngredientNameList().size());
  }

  @Test
  void testRemove_HappyPath() throws Exception {
    IngredientName originalIngredientName = ingredientNameService.findByName("ui").get();

    assertEquals(4, ingredientNameService.getReadonlyIngredientNameList().size());
    ingredientNameService.remove(originalIngredientName);
    assertEquals(3, ingredientNameService.getReadonlyIngredientNameList().size());
    assertEquals(Optional.empty(),
        ingredientNameService.findByName(originalIngredientName.getName()));
  }

  @Test
  void testRemove_NotFound() throws Exception {
    IngredientName originalIngredientName = mockIngredientNames.getIngredientName(3000L, null, "onbekend",
        "onbekenden", true, null, IngredientType.OVERIG);

    NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> {
      ingredientNameService.remove(originalIngredientName);
    });

    Assertions.assertEquals("Ingrediënt naam onbekend niet gevonden", exception.getMessage());
    assertEquals(4, ingredientNameService.getReadonlyIngredientNameList().size());
  }

}
