package nl.recipes.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;
import nl.recipes.domain.IngredientName;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.repositories.ShoppingItemRepository;

@Slf4j
@Service
public class StockShoppingItemService implements ListChangeListener<IngredientName> {

  private final IngredientNameService ingredientNameService;
  private final ShoppingItemRepository shoppingItemRepository;

  private ObservableList<ShoppingItem> stockShoppingItemList;

  public StockShoppingItemService(ShoppingItemRepository shoppingItemRepository,
      IngredientNameService ingredientNameService) {
    this.ingredientNameService = ingredientNameService;
    this.shoppingItemRepository = shoppingItemRepository;
    
    this.ingredientNameService.addListener(this);
    loadShoppingItemList();
    synchronizeWithIngredientNameStockItems();
  }

  public ObservableList<ShoppingItem> getReadonlyShoppingItemList() {
    return FXCollections.unmodifiableObservableList(stockShoppingItemList);
  }
  
  protected ShoppingItem create(IngredientName ingredientName) throws AlreadyExistsException {
    if (findByName(ingredientName.getName()).isPresent()) {
      throw new AlreadyExistsException(
          "Naam " + ingredientName.getName() + " bestaat al");
    }
    ShoppingItem shoppingItem = new ShoppingItem.ShoppingItemBuilder()
        .withName(ingredientName.getName())
        .withPluralName(ingredientName.getPluralName())
        .withIngredientType(ingredientName.getIngredientType())
        .withShopType(ingredientName.getShopType())
        .withIsStandard(false)
        .build();
    
    ShoppingItem createdShoppingItem = shoppingItemRepository.save(shoppingItem);
    stockShoppingItemList.add(createdShoppingItem);
    
    return createdShoppingItem;
  }
  
  protected ShoppingItem create(ShoppingItem shoppingItem) throws AlreadyExistsException {
    if (findByName(shoppingItem.getName()).isPresent()) {
      throw new AlreadyExistsException(
          "Naam " + shoppingItem.getName() + " bestaat al");
    }
    
    ShoppingItem createdShoppingItem = shoppingItemRepository.save(shoppingItem);
    stockShoppingItemList.add(createdShoppingItem);
    
    return createdShoppingItem;
  }
  
  public void update(ShoppingItem shoppingItem) {
    Optional<ShoppingItem> opt = findById(shoppingItem.getId());
    if (opt.isPresent()) {
      ShoppingItem updatedShoppingItem = shoppingItemRepository.save(shoppingItem);
      stockShoppingItemList.set(stockShoppingItemList.lastIndexOf(shoppingItem),
          updatedShoppingItem);
    } else {
      throw new NotFoundException(
          "Boodschap artikel met id " + shoppingItem.getId() + " niet gevonden");
    }
  }
  
  protected void removeByName(String name) throws NotFoundException {
    if (!findByName(name).isPresent()) {
      throw new NotFoundException(
          "Naam " + name + " niet gevonden");
    }
    shoppingItemRepository.delete(findByName(name).get());
    stockShoppingItemList.remove(findByName(name).get());
  }

  public Optional<ShoppingItem> findByName(String name) {
    return stockShoppingItemList.stream()
        .filter(shoppingItem -> name.equals(shoppingItem.getName())).findAny();
  }

  public Optional<ShoppingItem> findById(Long id) {
    return stockShoppingItemList.stream()
        .filter(shoppingItem -> id.equals(shoppingItem.getId())).findAny();
  }

  public void addListener(ListChangeListener<ShoppingItem> listener) {
    stockShoppingItemList.addListener(listener);
  }

  public void removeChangeListener(ListChangeListener<ShoppingItem> listener) {
    stockShoppingItemList.removeListener(listener);
  }

  private void loadShoppingItemList() {
    stockShoppingItemList = FXCollections.observableList(shoppingItemRepository.findByOrderByNameAsc().stream()
        .filter(s -> !s.isStandard())
        .collect(Collectors.toList()));
  }

  /*
   * We keep track of the IngredientName list to update the stockShoppingItemList if required
   */
  @Override
  public void onChanged(Change<? extends IngredientName> c) {
    while (c.next()) {
      if (c.wasPermutated()) {
        /* Should not happen */
        log.debug("IngredientNameList was permutated unexpectedly on element: " + c);
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
              update(s);
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
            findByName(added.getName()).ifPresent(s -> {
              try {
                create(added);
              } catch (AlreadyExistsException e) {
                log.debug("To be created shoppingitem already exists: ", added);
              }
            });
          }
        }
      }
    }
  }
  
  private void synchronizeWithIngredientNameStockItems() {
    List<IngredientName> ingredientNameStockItems = ingredientNameService.findAllStockItems();
    if (ingredientNameStockItems.size() != stockShoppingItemList.size()) {
      log.debug("Het aantal voorraad boodschappen (" + stockShoppingItemList.size()
          + ") komt niet overeen met het aantal voorraad ingredienten (" + ingredientNameStockItems.size() + ")");
      for (IngredientName ingredientName : ingredientNameStockItems) {
        if (findByName(ingredientName.getName()).isEmpty()) {
          try {
            create(ingredientName);
          } catch (AlreadyExistsException e) {
            log.debug("De te creeeren voorraad boodschap " + ingredientName.getName() + " bestaat al");
          }
        }
      }
    } else {
      log.info("Het aantal voorraad boodschappen komt overeen met het aantal voorraad ingredienten");
    }
  }
  
  // Setter for JUnit testing only
  void setStockShoppingItemList(ObservableList<ShoppingItem> standardShoppingItemList) {
    this.stockShoppingItemList = standardShoppingItemList;
  }

}
