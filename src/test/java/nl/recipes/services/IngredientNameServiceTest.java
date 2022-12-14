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
import nl.recipes.domain.IngredientName;
import nl.recipes.domain.IngredientType;
import nl.recipes.domain.MeasureUnit;
import nl.recipes.domain.ShopType;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.repositories.IngredientNameRepository;
import nl.recipes.util.testdata.MockIngredientNames;

class IngredientNameServiceTest {

  private static final String NEW_INGREDIENTNAME = "new ingredient name";

  private static final String NEW_INGREDIENTNAMES = "new ingredient names";

  @Mock
  IngredientNameRepository ingredientNameRepository;

  @InjectMocks
  IngredientNameService ingredientNameService;

  MockIngredientNames mockIngredientNames;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    mockIngredientNames = new MockIngredientNames();
    ingredientNameService.setObservableList(mockIngredientNames.getIngredientNameList());
  }

  @Test
  void testGetReadonlyIngredientNameList() {
    List<IngredientName> expectedList = mockIngredientNames.getIngredientNameList();

    assertEquals(expectedList, ingredientNameService.getList());
  }

  @Test
  void testFindByName_HappyPath() {
    Optional<IngredientName> expectedIngredientName =
        Optional.of(new IngredientName.IngredientNameBuilder().withMeasureUnit(null)
            .withName("prei").withPluralName("preien").withStock(false).withShopType(ShopType.DEKA)
            .withIngredientType(IngredientType.GROENTE).build(3L));

    when(ingredientNameRepository.findByName("prei")).thenReturn(expectedIngredientName);

    assertEquals(expectedIngredientName, ingredientNameService.findByName("prei"));
  }

  @Test
  void testFindByName_NotFound() {
    Optional<IngredientName> expectedIngredientName = Optional.empty();

    assertEquals(expectedIngredientName, ingredientNameService.findByName("prij"));
  }

  @Test
  void testFindById_HappyPath() {
    Optional<IngredientName> expectedIngredientName =
        Optional.of(new IngredientName.IngredientNameBuilder().withMeasureUnit(null)
            .withName("prei").withPluralName("preien").withStock(false).withShopType(ShopType.DEKA)
            .withIngredientType(IngredientType.GROENTE).build(3L));

    when(ingredientNameRepository.findById(3L)).thenReturn(expectedIngredientName);

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
        .withName(NEW_INGREDIENTNAME).withPluralName(NEW_INGREDIENTNAMES).withStock(true)
        .withShopType(ShopType.MARKT).withIngredientType(IngredientType.OVERIG).build();

    IngredientName savedIngredientName =
        new IngredientName.IngredientNameBuilder().withMeasureUnit(null)
            .withName(NEW_INGREDIENTNAME).withPluralName(NEW_INGREDIENTNAMES).withStock(true)
            .withShopType(ShopType.MARKT).withIngredientType(IngredientType.OVERIG).build(5L);

    when(ingredientNameRepository.save(ingredientName)).thenReturn(savedIngredientName);

    assertEquals(savedIngredientName, ingredientNameService.create(ingredientName));
    assertEquals(5, ingredientNameService.getList().size());
    assertEquals(savedIngredientName, ingredientNameService.getList().get(1));
  }

  @Test
  void testCreate_HappyPath_SameNameDifferentMeasureUnit() throws Exception {
    IngredientName ingredientName = new IngredientName.IngredientNameBuilder()
        .withName("mozzarella").withPluralName("mozzarella").withStock(true)
        .withShopType(ShopType.DEKA).withIngredientType(IngredientType.ZUIVEL).build();

    IngredientName savedIngredientName = new IngredientName.IngredientNameBuilder()
        .withName("mozzarella").withPluralName("mozzarella").withMeasureUnit(null).withStock(true)
        .withShopType(ShopType.DEKA).withIngredientType(IngredientType.ZUIVEL).build(5L);

    when(ingredientNameRepository.save(ingredientName)).thenReturn(savedIngredientName);

    assertEquals(savedIngredientName, ingredientNameService.create(ingredientName));
    assertEquals(5, ingredientNameService.getList().size());
    assertEquals(savedIngredientName, ingredientNameService.getList().get(1));
  }

  @Test
  void testCreate_AlreadyExistsException() throws Exception {
    IngredientName ingredientName =
        new IngredientName.IngredientNameBuilder().withName("water").build();

    AlreadyExistsException exception = Assertions.assertThrows(AlreadyExistsException.class, () -> {
      ingredientNameService.create(ingredientName);
    });

    Assertions.assertEquals("Ingrediënt water bestaat al", exception.getMessage());
    assertEquals(4, ingredientNameService.getList().size());
  }

  @Test
  void testCreate_IllegalValueException() throws Exception {
    IngredientName ingredientName = new IngredientName.IngredientNameBuilder().withName("").build();

    IllegalValueException exception = Assertions.assertThrows(IllegalValueException.class, () -> {
      ingredientNameService.create(ingredientName);
    });

    Assertions.assertEquals("Ingrediënt naam mag niet leeg zijn", exception.getMessage());
    assertEquals(4, ingredientNameService.getList().size());
  }

  @Test
  void testUpdate_HappyPath() throws Exception {
    IngredientName originalIngredientName = mockIngredientNames.getIngredientNameList().get(1);
    IngredientName update = new IngredientName.IngredientNameBuilder().withMeasureUnit(null)
        .withName("wortel").withPluralName("wortels").withStock(false).withShopType(ShopType.EKO)
        .withIngredientType(IngredientType.GROENTE).build();

    IngredientName expectedIngredientName = new IngredientName.IngredientNameBuilder()
        .withMeasureUnit(null).withName("wortel").withPluralName("wortels").withStock(false)
        .withShopType(ShopType.EKO).withIngredientType(IngredientType.GROENTE).build(2L);

    when(ingredientNameRepository.save(expectedIngredientName)).thenReturn(expectedIngredientName);

    assertEquals(expectedIngredientName,
        ingredientNameService.update(originalIngredientName, update));
  }

  @Test
  void testUpdate_HappyPathOnlyUpdateMeasureUnit() throws Exception {
    when(ingredientNameRepository.findByName("ui")).thenReturn(Optional.of(mockIngredientNames.getIngredientNameList().get(1)));
    when(ingredientNameRepository.findById(2L)).thenReturn(Optional.of(mockIngredientNames.getIngredientNameList().get(1)));
    
    IngredientName originalIngredientName = ingredientNameService.findByName("ui").get();
    IngredientName update = new IngredientName.IngredientNameBuilder()
        .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("netje"). build(60L))
        .withName("ui")
        .withPluralName("uien")
        .withStock(true)
        .withShopType(ShopType.EKO)
        .withIngredientType(IngredientType.GROENTE)
        .build();

    IngredientName expectedIngredientName = new IngredientName.IngredientNameBuilder()
        .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("netje"). build(60L))
        .withName("ui")
        .withPluralName("uien")
        .withStock(true)
        .withShopType(ShopType.EKO)
        .withIngredientType(IngredientType.GROENTE)
        .build(2L);

    when(ingredientNameRepository.save(expectedIngredientName)).thenReturn(expectedIngredientName);

    assertEquals(expectedIngredientName,
        ingredientNameService.update(originalIngredientName, update));
    assertEquals(4, ingredientNameService.getList().size());
    assertEquals(expectedIngredientName, ingredientNameService.getList().get(2));
  }

  @Test
  void testUpdate_HappyPathOnlyUpdateStock() throws Exception {
    when(ingredientNameRepository.findByName("ui")).thenReturn(Optional.of(mockIngredientNames.getIngredientNameList().get(1)));
    when(ingredientNameRepository.findById(2L)).thenReturn(Optional.of(mockIngredientNames.getIngredientNameList().get(1)));
    
    IngredientName originalIngredientName = ingredientNameService.findByName("ui").get();
    IngredientName update = new IngredientName.IngredientNameBuilder()
        .withName("ui")
        .withPluralName("uien")
        .withStock(false)
        .withShopType(ShopType.EKO)
        .withIngredientType(IngredientType.GROENTE)
        .build();

    IngredientName expectedIngredientName = new IngredientName.IngredientNameBuilder()
        .withName("ui")
        .withPluralName("uien")
        .withStock(false)
        .withShopType(ShopType.EKO)
        .withIngredientType(IngredientType.GROENTE)
        .build(2L);

    when(ingredientNameRepository.save(expectedIngredientName)).thenReturn(expectedIngredientName);

    assertEquals(expectedIngredientName,
        ingredientNameService.update(originalIngredientName, update));
    assertEquals(4, ingredientNameService.getList().size());
    assertEquals(expectedIngredientName, ingredientNameService.getList().get(2));
  }

  @Test
  void testUpdate_NotFoundException() throws Exception {
    IngredientName originalIngredientName = new IngredientName.IngredientNameBuilder()
        .withMeasureUnit(null).withName("onbekend").withPluralName("onbekenden").withStock(true)
        .withShopType(ShopType.DEKA).withIngredientType(IngredientType.GROENTE).build(3000L);

    IngredientName update = new IngredientName.IngredientNameBuilder().withName("bekend")
        .withPluralName("bekenden").build();

    NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> {
      ingredientNameService.update(originalIngredientName, update);
    });

    Assertions.assertEquals("Ingrediënt onbekend niet gevonden", exception.getMessage());
    assertEquals(4, ingredientNameService.getList().size());
  }

  @Test
  void testUpdate_AlreadyExistsException() throws Exception {
    IngredientName originalIngredientName = new IngredientName.IngredientNameBuilder()
        .withMeasureUnit(null).withName("water").withPluralName("water").withStock(true)
        .withShopType(null).withIngredientType(IngredientType.OVERIG).build(1L);

    IngredientName update = new IngredientName.IngredientNameBuilder().withName("prei")
        .withPluralName("preien").build();

    when(ingredientNameRepository.findById(1L))
        .thenReturn(Optional.of(ingredientNameService.getList().get(0)));

    AlreadyExistsException exception = Assertions.assertThrows(AlreadyExistsException.class, () -> {
      ingredientNameService.update(originalIngredientName, update);
    });

    Assertions.assertEquals("Ingrediënt prei bestaat al", exception.getMessage());
    assertEquals(4, ingredientNameService.getList().size());
  }

  @Test
  void testRemove_HappyPath() throws Exception {
    when (ingredientNameRepository.findByName("ui")).thenReturn(Optional.of(ingredientNameService.getList().get(1)));
    when (ingredientNameRepository.findById(2L)).thenReturn(Optional.of(ingredientNameService.getList().get(1)));
    
    IngredientName originalIngredientName = ingredientNameService.findByName("ui").get();
    IngredientName secondListItemAfterRemove = ingredientNameService.getList().get(2);
    
    assertEquals(4, ingredientNameService.getList().size());
    ingredientNameService.remove(originalIngredientName);
    assertEquals(3, ingredientNameService.getList().size());
    assertEquals(secondListItemAfterRemove,
        ingredientNameService.getList().get(1));
  }

  @Test
  void testRemove_NotFound() throws Exception {
    IngredientName originalIngredientName = new IngredientName.IngredientNameBuilder()
        .withMeasureUnit(null).withName("onbekend").withPluralName("onbekenden").withStock(true)
        .withShopType(null).withIngredientType(IngredientType.OVERIG).build(3000L);

    NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> {
      ingredientNameService.remove(originalIngredientName);
    });

    Assertions.assertEquals("Ingrediënt onbekend niet gevonden", exception.getMessage());
    assertEquals(4, ingredientNameService.getList().size());
  }

  @Test
  void testIngredientNameExists() {

    assertEquals(true,
        ingredientNameService.ingredientNameExists(new IngredientName.IngredientNameBuilder()
            .withMeasureUnit(null).withName("water").withPluralName("water").withStock(true)
            .withShopType(null).withIngredientType(IngredientType.OVERIG).build(1L)));

    assertEquals(true,
        ingredientNameService.ingredientNameExists(new IngredientName.IngredientNameBuilder()
            .withMeasureUnit(null).withName("water").withPluralName("wateren").withStock(false)
            .withShopType(ShopType.EKO).withIngredientType(IngredientType.ZUIVEL).build(1000L)));

    assertEquals(false,
        ingredientNameService.ingredientNameExists(new IngredientName.IngredientNameBuilder()
            .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("liter").build(1L))
            .withName("water").withPluralName("water").withStock(true).withShopType(null)
            .withIngredientType(IngredientType.OVERIG).build(1L)));

    assertEquals(false,
        ingredientNameService.ingredientNameExists(new IngredientName.IngredientNameBuilder()
            .withMeasureUnit(null).withName("wwater").withPluralName("water").withStock(true)
            .withShopType(null).withIngredientType(IngredientType.OVERIG).build(1L)));

    assertEquals(false,
        ingredientNameService
            .ingredientNameExists(new IngredientName.IngredientNameBuilder().withMeasureUnit(null)
                .withName("mozzarella").withPluralName("mozzarella").withStock(true)
                .withShopType(null).withIngredientType(IngredientType.OVERIG).build(1L)));
  }

  @Test
  void testFindAllStockItems() {
    List<IngredientName> list = ingredientNameService.findAllStockItems();
    assertEquals(3, list.size());
    assertEquals(false, list.stream().anyMatch(i -> i.getName().equals("prei")));
  }
}
