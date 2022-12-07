package nl.recipes.services;

import static nl.recipes.views.ViewMessages.SHOPPING_ITEM_NAME_;
import static nl.recipes.views.ViewMessages.SHOPPING_ITEM_NAME_CANNOT_BE_EMPTY;
import static nl.recipes.views.ViewMessages._ALREADY_EXISTS;
import static nl.recipes.views.ViewMessages._NOT_FOUND;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;
import nl.recipes.domain.IngredientName;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.repositories.ShoppingItemRepository;

@Slf4j
@Service
public class StockShoppingItemService extends ListService<ShoppingItem> implements ListChangeListener<IngredientName> {

  private final IngredientNameService ingredientNameService;

  public StockShoppingItemService(ShoppingItemRepository shoppingItemRepository,
      IngredientNameService ingredientNameService) {
    this.ingredientNameService = ingredientNameService;
    repository = shoppingItemRepository;
    
    this.ingredientNameService.addListener(this);
    
    Sort sort = Sort.by("name").ascending();
    observableList = FXCollections.observableList(repository.findAll(sort).stream()
            .filter(s -> !s.isStandard()).collect(Collectors.toList()));
    comparator = (t1, t2)-> t1.getName().compareTo(t2.getName());
  }
  
  public Optional<ShoppingItem> findByName(String name) {
    return observableList.stream().filter(shoppingItem -> name.equals(shoppingItem.getName()))
        .findAny();
  }

  public Optional<ShoppingItem> findById(Long id) {
    return observableList.stream().filter(shoppingItem -> id.equals(shoppingItem.getId()))
        .findAny();
  }
  
  protected ShoppingItem create(IngredientName ingredientName) throws AlreadyExistsException {
    if (findByName(ingredientName.getName()).isPresent()) {
      throw new AlreadyExistsException(
          SHOPPING_ITEM_NAME_ + ingredientName.getName() + _ALREADY_EXISTS);
    }
    ShoppingItem shoppingItem = new ShoppingItem.ShoppingItemBuilder()
        .withName(ingredientName.getName())
        .withPluralName(ingredientName.getPluralName())
        .withIngredientType(ingredientName.getIngredientType())
        .withShopType(ingredientName.getShopType())
        .withIsStandard(false)
        .build();
    
    return save(shoppingItem);
  }
  
  protected ShoppingItem create(ShoppingItem shoppingItem) throws AlreadyExistsException, IllegalValueException {
    if (shoppingItem == null || shoppingItem.getName() == null) {
      throw new IllegalValueException(SHOPPING_ITEM_NAME_CANNOT_BE_EMPTY);
    }
    if (findByName(shoppingItem.getName()).isPresent()) {
      throw new AlreadyExistsException(
          SHOPPING_ITEM_NAME_+ shoppingItem.getName() + _ALREADY_EXISTS);
    }
    
    if (shoppingItem.getAmount() == null) shoppingItem.setAmount(0.0f);
    return save(shoppingItem);
  }
  
  public ShoppingItem edit(ShoppingItem shoppingItem) {
    if (!findById(shoppingItem.getId()).isPresent()) {
      throw new NotFoundException(SHOPPING_ITEM_NAME_ + shoppingItem.getName() + _NOT_FOUND);
    }
    shoppingItem.setOnList(false);
    if (shoppingItem.getAmount() == null) shoppingItem.setAmount(0.0f);
    return update(shoppingItem);
  }
  
  protected void removeByName(String name) throws NotFoundException {
    if (findByName(name).isPresent()) {
      delete(findByName(name).get());
      
    } else {
      throw new NotFoundException(
          SHOPPING_ITEM_NAME_ + name + _NOT_FOUND);
    }
  }

  /*
   * We keep track of the IngredientName list to update the stockShoppingItemList if required
   */
  @Override
  public void onChanged(Change<? extends IngredientName> c) {
    while (c.next()) {
      if (c.wasPermutated()) {
      } else if (c.wasUpdated()) {
        /* Should not happen */
        log.debug("IngredientNameList was updated unexpectedly on element: " + c);
      } else if (c.wasReplaced()) {
        if (c.getRemovedSize() != 1 || c.getAddedSize() != 1) {
          /* Should not happen */
          log.debug("Unexpected mutations on IngredientNameList");
          return;
        }
        // Single replacement
        if (c.getRemoved().get(0).isStock() == c.getAddedSubList().get(0).isStock()) {
          /* No mutation on stock field but some other fields might have changed */
          if (c.getAddedSubList().get(0).isStock()) {
            findByName(c.getRemoved().get(0).getName()).ifPresent((s) -> {
              s.setName(c.getAddedSubList().get(0).getName());
              s.setPluralName(c.getAddedSubList().get(0).getPluralName());
              s.setShopType(c.getAddedSubList().get(0).getShopType());
              s.setIngredientType(c.getAddedSubList().get(0).getIngredientType());
              edit(s);
            });
          }
          return;
        }
        
        for (IngredientName added : c.getAddedSubList()) {
          if (added.isStock()) {
            try {
              create(added);
            } catch (AlreadyExistsException e) {
              log.debug("To be created shoppingitem already exists: ", added);
            }
          }
          if (!added.isStock()) {
            removeByName(added.getName());
          }
        }

      } else {
        for (IngredientName removed : c.getRemoved()) {
          if (removed.isStock()) {
            log.debug("{}", c);
            findByName(removed.getName()).ifPresent(s -> removeByName(removed.getName()));
          }

        }
        for (IngredientName added : c.getAddedSubList()) {
          if (added.isStock()) {
            if (findByName(added.getName()).isEmpty())
              try {
                create(added);
              } catch (AlreadyExistsException e) {
                log.debug("To be created shoppingitem already exists: ", added);
              }
          }
        }
      }
    }
  }
  
  /**
   * Setter for JUnit testing only!
   * 
   * @param observableList
   */
  void setObservableList(ObservableList<ShoppingItem> observableList) {
    this.observableList = observableList;
  }
}
