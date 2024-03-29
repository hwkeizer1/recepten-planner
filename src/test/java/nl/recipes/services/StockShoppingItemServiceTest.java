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
import nl.recipes.domain.MeasureUnit;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.repositories.ShoppingItemRepository;
import nl.recipes.util.testdata.MockShoppingItems;

class StockShoppingItemServiceTest {

  @Mock
  ShoppingItemRepository shoppingItemRepository;
  @Mock
  IngredientNameService ingredientNameService;

  @InjectMocks
  StockShoppingItemService stockShoppingItemService;

  MockShoppingItems mockShoppingItems;


  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    mockShoppingItems = new MockShoppingItems();
    stockShoppingItemService.setObservableList(mockShoppingItems.getShoppingItemList());
  }

  @Test
  void testGetReadonlyShoppingItemList() {
    List<ShoppingItem> expectedList = mockShoppingItems.getShoppingItemList();

    assertEquals(expectedList, stockShoppingItemService.getList());
  }


  @Test
  void testFindByName_HappyPath() {
    String name = "kaas";

    Optional<ShoppingItem> expectedShoppingItem =
        Optional.of(mockShoppingItems.getShoppingItemList().get(0));

    assertEquals(expectedShoppingItem, stockShoppingItemService.findByName(name));
  }

  @Test
  void testFindByName_NotFound() {
    String name = "kaaz";

    Optional<ShoppingItem> expectedShoppingItem = Optional.empty();

    assertEquals(expectedShoppingItem, stockShoppingItemService.findByName(name));
  }

  @Test
  void testFindById_HappyPath() {
    Optional<ShoppingItem> expectedShoppingItem =
        Optional.of(mockShoppingItems.getShoppingItemList().get(2));

    assertEquals(expectedShoppingItem, stockShoppingItemService.findById(3L));
  }

  @Test
  void testFindById_NotFound() {
    Optional<ShoppingItem> expectedShoppingItem = Optional.empty();

    assertEquals(expectedShoppingItem, stockShoppingItemService.findById(3000L));
  }

  @Test
  void testCreate_HappyPath() throws Exception {
    ShoppingItem shoppingItem =
        new ShoppingItem.ShoppingItemBuilder().withAmount(1F).withName("chips")
            .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("zak").build(1L))
            .withIsStandard(true).withOnList(false).build();

    ShoppingItem savedShoppingItem =
        new ShoppingItem.ShoppingItemBuilder().withAmount(1F).withName("chips")
            .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("zak").build(1L))
            .withIsStandard(true).withOnList(false).build(4L);

    when(shoppingItemRepository.save(shoppingItem)).thenReturn(savedShoppingItem);

    assertEquals(savedShoppingItem, stockShoppingItemService.create(shoppingItem));
    assertEquals(4, stockShoppingItemService.getList().size());
    assertEquals(Optional.of(savedShoppingItem), stockShoppingItemService.findByName("chips"));
  }

  @Test
  void testCreate_AlreadyExistsException() throws Exception {
    ShoppingItem shoppingItem = new ShoppingItem.ShoppingItemBuilder().withName("boter")
        .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("gram").build(1L)).build();

    AlreadyExistsException exception = Assertions.assertThrows(AlreadyExistsException.class, () -> {
      stockShoppingItemService.create(shoppingItem);
    });

    Assertions.assertEquals("Boodschap boter bestaat al", exception.getMessage());
    assertEquals(3, stockShoppingItemService.getList().size());
  }

  @Test
  void testCreate_IllegalValueException() throws Exception {
    ShoppingItem shoppingItem = new ShoppingItem.ShoppingItemBuilder().withName(null).build();

    IllegalValueException exception = Assertions.assertThrows(IllegalValueException.class, () -> {
      stockShoppingItemService.create(shoppingItem);
    });

    Assertions.assertEquals("Boodschap naam mag niet leeg zijn", exception.getMessage());
    assertEquals(3, stockShoppingItemService.getList().size());
  }

  @Test
  void testUpdate_HappyPath() throws Exception {
    ShoppingItem originalShoppingItem = stockShoppingItemService.findById(3L).get();
    ShoppingItem update =
        new ShoppingItem.ShoppingItemBuilder().withAmount(4F).withName("paaseieren").build();

    ShoppingItem expectedShoppingItem = new ShoppingItem.ShoppingItemBuilder().withAmount(4F)
        .withName("paaseieren").withIsStandard(true).withOnList(true).build(3L);

    when(shoppingItemRepository.save(expectedShoppingItem)).thenReturn(expectedShoppingItem);

    assertEquals(expectedShoppingItem, stockShoppingItemService.edit(expectedShoppingItem));
    assertEquals(3, stockShoppingItemService.getList().size());
    assertEquals(Optional.of(expectedShoppingItem), stockShoppingItemService.findById(3L));
  }

  @Test
  void testUpdate_NotFoundException() throws Exception {
    ShoppingItem originalShoppingItem =
        new ShoppingItem.ShoppingItemBuilder().withAmount(650F).withName("druifen")
            .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("druppels").build(1L))
            .withIsStandard(false).withOnList(false).build(1000L);

    ShoppingItem update = new ShoppingItem.ShoppingItemBuilder().withAmount(6F).withName("druiven")
        .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("druppels").build(1L))
        .withIsStandard(true).withOnList(true).build(3L);

    NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> {
      stockShoppingItemService.edit(originalShoppingItem);
    });

    Assertions.assertEquals("Boodschap druifen niet gevonden", exception.getMessage());
    assertEquals(3, stockShoppingItemService.getList().size());
  }

  // @Test
  // void testUpdate_AlreadyExistsException() throws Exception {
  // ShoppingItem originalShoppingItem = new ShoppingItem.ShoppingItemBuilder()
  // .withAmount(750F)
  // .withName("kaas")
  // .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("gram").build(1L))
  // .withIsStandard(true)
  // .withOnList(false)
  // .build(1L);
  //
  // ShoppingItem update = new ShoppingItem.ShoppingItemBuilder()
  // .withAmount(4F)
  // .withName("eieren")
  // .withIsStandard(true)
  // .withOnList(true)
  // .build();
  //
  // AlreadyExistsException exception = Assertions.assertThrows(AlreadyExistsException.class, () ->
  // {
  // stockShoppingItemService.edit(originalShoppingItem, update);
  // });
  //
  // Assertions.assertEquals("Naam eieren bestaat al", exception.getMessage());
  // assertEquals(3, stockShoppingItemService.getList().size());
  // }

  @Test
  void testRemove_HappyPath() throws Exception {
    ShoppingItem originalShoppingItem = stockShoppingItemService.findById(2L).get();

    assertEquals(3, stockShoppingItemService.getList().size());
    stockShoppingItemService.delete(originalShoppingItem);
    assertEquals(2, stockShoppingItemService.getList().size());
    assertEquals(Optional.empty(),
        stockShoppingItemService.findByName(originalShoppingItem.getName()));
  }

  @Test
  void testRemove_NotFound() throws Exception {
    ShoppingItem originalIngredientName =
        new ShoppingItem.ShoppingItemBuilder().withAmount(650F).withName("druifen")
            .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("druppels").build(1L))
            .withIsStandard(false).withOnList(false).build(1000L);

    NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> {
      stockShoppingItemService.removeByName(originalIngredientName.getName());
    });

    Assertions.assertEquals("Boodschap druifen niet gevonden", exception.getMessage());
    assertEquals(3, stockShoppingItemService.getList().size());
  }

}
