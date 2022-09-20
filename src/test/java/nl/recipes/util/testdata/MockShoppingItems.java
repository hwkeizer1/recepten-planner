package nl.recipes.util.testdata;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.recipes.domain.MeasureUnit;
import nl.recipes.domain.ShoppingItem;

public class MockShoppingItems {

  public ObservableList<ShoppingItem> getShoppingItemList() {
    List<ShoppingItem> shoppingItemList = new ArrayList<>();
    shoppingItemList.add(new ShoppingItem.ShoppingItemBuilder()
        .withAmount(750F)
        .withName("kaas")
        .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("gram").build(1L))
        .withIsStandard(true)
        .withOnList(false)
        .build(1L));
    shoppingItemList.add(new ShoppingItem.ShoppingItemBuilder()
        .withAmount(250F)
        .withName("boter")
        .withMeasureUnit(new MeasureUnit.MeasureUnitBuilder().withName("gram").build(1L))
        .withIsStandard(false)
        .withOnList(true)
        .build(2L));
    shoppingItemList.add(new ShoppingItem.ShoppingItemBuilder()
        .withAmount(6F)
        .withName("eieren")
        .withIsStandard(true)
        .withOnList(true)
        .build(3L));
    
    return FXCollections.observableList(shoppingItemList);
  }
}
