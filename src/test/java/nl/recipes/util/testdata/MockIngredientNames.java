package nl.recipes.util.testdata;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.recipes.domain.IngredientName;
import nl.recipes.domain.IngredientType;
import nl.recipes.domain.MeasureUnit;
import nl.recipes.domain.ShopType;

public class MockIngredientNames {

  
  public ObservableList<IngredientName> getIngredientNameList() {
    List<IngredientName> ingredientnameList = new ArrayList<>();
    ingredientnameList
        .add(getIngredientName(1L, null, "water", "water", true, null, IngredientType.OVERIG));
    ingredientnameList
        .add(getIngredientName(2L, null, "ui", "uien", true, ShopType.EKO, IngredientType.GROENTE));
    ingredientnameList
        .add(getIngredientName(3L, null, "prei", "preien", false, ShopType.DEKA, IngredientType.GROENTE));
    ingredientnameList.add(getIngredientName(4L, null, "mozzarella", "mozzarella", false, ShopType.DEKA,
        IngredientType.ZUIVEL));
    return FXCollections.observableList(ingredientnameList);
  }
  
  public IngredientName getIngredientName(Long id, MeasureUnit measureUnit, String name, String pluralName, boolean stock,
      ShopType shopType, IngredientType ingredientType) {
    return new IngredientName(id, measureUnit, name, pluralName, stock, shopType, ingredientType);
  }
}
