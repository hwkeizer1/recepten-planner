package nl.recipes.util.testdata;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;
import nl.recipes.domain.IngredientName;
import nl.recipes.domain.MeasureUnit;
import nl.recipes.domain.ShoppingItem;
@Slf4j
public class MockShoppingItems {

  public ObservableList<ShoppingItem> getShoppingItemList() {
    List<ShoppingItem> shoppingItemList = new ArrayList<>();
    shoppingItemList.add(new ShoppingItem.ShoppingItemBuilder()
        .withAmount(750F)
        .withIngredientName(new IngredientName.IngredientNameBuilder()
            .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("gram").build(1L))
            .withName("kaas").build(1L))
        .withIsStandard(true)
        .withOnList(false)
        .build(1L));
    shoppingItemList.add(new ShoppingItem.ShoppingItemBuilder()
        .withAmount(250F)
        .withIngredientName(new IngredientName.IngredientNameBuilder()
            .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("gram").build(1L))
            .withName("boter").build(2L))
        .withIsStandard(false)
        .withOnList(true)
        .build(2L));
    shoppingItemList.add(new ShoppingItem.ShoppingItemBuilder()
        .withAmount(6F)
        .withIngredientName(new IngredientName.IngredientNameBuilder().withName("eieren").build(3L))
        .withIsStandard(true)
        .withOnList(true)
        .build(3L));
    
    return FXCollections.observableList(shoppingItemList);
  }
}
