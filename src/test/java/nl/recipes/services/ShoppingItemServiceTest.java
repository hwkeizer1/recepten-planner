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
import lombok.extern.slf4j.Slf4j;
import nl.recipes.domain.IngredientName;
import nl.recipes.domain.IngredientType;
import nl.recipes.domain.MeasureUnit;
import nl.recipes.domain.ShopType;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.repositories.ShoppingItemRepository;
import nl.recipes.util.testdata.MockShoppingItems;

@Slf4j
class ShoppingItemServiceTest {

  @Mock
  ShoppingItemRepository shoppingItemRepository;
  
  @InjectMocks
  ShoppingItemService shoppingItemService;
  
  MockShoppingItems mockShoppingItems;
  

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    mockShoppingItems = new MockShoppingItems();
    shoppingItemService.setObservableShoppingItemList(mockShoppingItems.getShoppingItemList());
  }
  
  @Test
  void testGetReadonlyShoppingItemList() {
    List<ShoppingItem> expectedList = mockShoppingItems.getShoppingItemList();

    assertEquals(expectedList, shoppingItemService.getReadonlyShoppingItemList());
  }


  @Test
  void testFindByName_HappyPath() {
    IngredientName ingredientName = new IngredientName.IngredientNameBuilder()
        .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("gram").build(1L))
        .withName("kaas").build(1L);
    
    Optional<ShoppingItem> expectedShoppingItem = Optional.of(mockShoppingItems.getShoppingItemList().get(0));

    assertEquals(expectedShoppingItem, shoppingItemService.findByName(ingredientName));
  }
  
  @Test
  void testFindByName_NotFound() {
    IngredientName ingredientName = new IngredientName.IngredientNameBuilder()
        .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("kilo").build(1L))
        .withName("kaas").build(1L);
    
    Optional<ShoppingItem> expectedShoppingItem = Optional.empty();

    assertEquals(expectedShoppingItem, shoppingItemService.findByName(ingredientName));
  }
  
  @Test
  void testFindById_HappyPath() {
    Optional<ShoppingItem> expectedShoppingItem = Optional.of(mockShoppingItems.getShoppingItemList().get(2));

    assertEquals(expectedShoppingItem, shoppingItemService.findById(3L));
  }
  
  @Test
  void testFindById_NotFound() {
    Optional<ShoppingItem> expectedShoppingItem = Optional.empty();

    assertEquals(expectedShoppingItem, shoppingItemService.findById(3000L));
  }
  
  @Test
  void testCreate_HappyPath() throws Exception {
    ShoppingItem shoppingItem = new ShoppingItem.ShoppingItemBuilder()
        .withAmount(1F)
        .withIngredientName(new IngredientName.IngredientNameBuilder()
            .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("zak").build(1L))
            .withName("chips").build(1L))
        .withIsStandard(true)
        .withOnList(false)
        .build();

    ShoppingItem savedShoppingItem = new ShoppingItem.ShoppingItemBuilder()
        .withAmount(1F)
        .withIngredientName(new IngredientName.IngredientNameBuilder()
            .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("zak").build(1L))
            .withName("chips").build(1L))
        .withIsStandard(true)
        .withOnList(false)
        .build(4L);
        
    when(shoppingItemRepository.save(shoppingItem)).thenReturn(savedShoppingItem);

    assertEquals(savedShoppingItem, shoppingItemService.create(shoppingItem));
    assertEquals(4, shoppingItemService.getReadonlyShoppingItemList().size());
    assertEquals(Optional.of(savedShoppingItem),
        shoppingItemService.findByName(new IngredientName.IngredientNameBuilder()
            .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("zak").build(1L))
            .withName("chips").build(1L)));
  }
  
  @Test
  void testCreate_AlreadyExistsException() throws Exception {
    ShoppingItem shoppingItem = new ShoppingItem.ShoppingItemBuilder()
        .withIngredientName(new IngredientName.IngredientNameBuilder()
            .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder()
                .withName("gram").build(1L))
            .withName("boter")
            .build(2L))
        .build();

    AlreadyExistsException exception = Assertions.assertThrows(AlreadyExistsException.class, () -> {
      shoppingItemService.create(shoppingItem);
    });

    Assertions.assertEquals("Artikel boter bestaat al", exception.getMessage());
    assertEquals(3, shoppingItemService.getReadonlyShoppingItemList().size());
  }
  
  @Test
  void testCreate_IllegalValueException() throws Exception {
    ShoppingItem shoppingItem = new ShoppingItem.ShoppingItemBuilder().withIngredientName(null).build();

    IllegalValueException exception = Assertions.assertThrows(IllegalValueException.class, () -> {
      shoppingItemService.create(shoppingItem);
    });

    Assertions.assertEquals("Artikel naam mag niet leeg zijn", exception.getMessage());
    assertEquals(3, shoppingItemService.getReadonlyShoppingItemList().size());
  }
  
  @Test
  void testUpdate_HappyPath() throws Exception {
    ShoppingItem originalShoppingItem = shoppingItemService.findById(3L).get();
    ShoppingItem update = new ShoppingItem.ShoppingItemBuilder()
        .withAmount(4F)
        .withIngredientName(new IngredientName.IngredientNameBuilder()
            .withName("paaseieren").build(5L))
        .build();

    ShoppingItem expectedShoppingItem = new ShoppingItem.ShoppingItemBuilder()
        .withAmount(4F)
        .withIngredientName(new IngredientName.IngredientNameBuilder().withName("paaseieren").build(5L))
        .withIsStandard(true)
        .withOnList(true)
        .build(3L);

    when(shoppingItemRepository.save(expectedShoppingItem)).thenReturn(expectedShoppingItem);

    assertEquals(expectedShoppingItem,
        shoppingItemService.update(originalShoppingItem, update));
    assertEquals(3, shoppingItemService.getReadonlyShoppingItemList().size());
    assertEquals(Optional.of(expectedShoppingItem), shoppingItemService.findById(3L));
  }
  
  @Test
  void testUpdate_NotFoundException() throws Exception {
    ShoppingItem originalShoppingItem = new ShoppingItem.ShoppingItemBuilder()
        .withAmount(650F)
        .withIngredientName(new IngredientName.IngredientNameBuilder()
            .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("druppels").build(1L))
            .withName("druiven").build(9L))
        .withIsStandard(false)
        .withOnList(false)
        .build(1000L);

    ShoppingItem update = new ShoppingItem.ShoppingItemBuilder()
        .withAmount(6F)
        .withIngredientName(new IngredientName.IngredientNameBuilder().withName("druiven").build(1L))
        .withIsStandard(true)
        .withOnList(true)
        .build(3L);

    NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> {
      shoppingItemService.update(originalShoppingItem, update);
    });

    Assertions.assertEquals("Artikel druiven niet gevonden", exception.getMessage());
    assertEquals(3, shoppingItemService.getReadonlyShoppingItemList().size());
  }
  
  @Test
  void testUpdate_AlreadyExistsException() throws Exception {
    ShoppingItem originalShoppingItem = new ShoppingItem.ShoppingItemBuilder()
        .withAmount(750F)
        .withIngredientName(new IngredientName.IngredientNameBuilder()
            .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("gram").build(1L))
            .withName("kaas").build(1L))
        .withIsStandard(true)
        .withOnList(false)
        .build(1L);

    ShoppingItem update = new ShoppingItem.ShoppingItemBuilder()
        .withAmount(4F)
        .withIngredientName(new IngredientName.IngredientNameBuilder().withName("eieren").build(3L))
        .withIsStandard(true)
        .withOnList(true)
        .build();
    
    AlreadyExistsException exception = Assertions.assertThrows(AlreadyExistsException.class, () -> {
      shoppingItemService.update(originalShoppingItem, update);
    });

    Assertions.assertEquals("Artikel eieren bestaat al", exception.getMessage());
    assertEquals(3, shoppingItemService.getReadonlyShoppingItemList().size());
  }
  
  @Test
  void testRemove_HappyPath() throws Exception {
    ShoppingItem originalShoppingItem = shoppingItemService.findById(2L).get();

    assertEquals(3, shoppingItemService.getReadonlyShoppingItemList().size());
    shoppingItemService.remove(originalShoppingItem);
    assertEquals(2, shoppingItemService.getReadonlyShoppingItemList().size());
    assertEquals(Optional.empty(),
        shoppingItemService.findByName(originalShoppingItem.getIngredientName()));
  }
  
  @Test
  void testRemove_NotFound() throws Exception {
    ShoppingItem originalIngredientName = new ShoppingItem.ShoppingItemBuilder()
        .withAmount(650F)
        .withIngredientName(new IngredientName.IngredientNameBuilder()
            .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("druppels").build(1L))
            .withName("druiven").build(9L))
        .withIsStandard(false)
        .withOnList(false)
        .build(1000L);

    NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> {
      shoppingItemService.remove(originalIngredientName);
    });

    Assertions.assertEquals("Artikel druiven niet gevonden", exception.getMessage());
    assertEquals(3, shoppingItemService.getReadonlyShoppingItemList().size());
  }
}
