package nl.recipes.util.testdata;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.recipes.domain.IngredientName;
import nl.recipes.domain.IngredientType;
import nl.recipes.domain.ShopType;

public class MockIngredientNames {

  
  public ObservableList<IngredientName> getIngredientNameList() {
    List<IngredientName> ingredientNameList = new ArrayList<>();
    ingredientNameList.add(new IngredientName.IngredientNameBuilder()
        .withMeasureUnit(null)
        .withName("water")
        .withPluralName("water")
        .withStock(true)
        .withShopType(null)
        .withIngredientType(IngredientType.OVERIG)
        .build(1L));
    ingredientNameList.add(new IngredientName.IngredientNameBuilder()
        .withMeasureUnit(null)
        .withName("ui")
        .withPluralName("uien")
        .withStock(true)
        .withShopType(ShopType.EKO)
        .withIngredientType(IngredientType.GROENTE)
        .build(2L));
    ingredientNameList.add(new IngredientName.IngredientNameBuilder()
        .withMeasureUnit(null)
        .withName("prei")
        .withPluralName("preien")
        .withStock(false)
        .withShopType(ShopType.DEKA)
        .withIngredientType(IngredientType.GROENTE)
        .build(3L));
    ingredientNameList.add(new IngredientName.IngredientNameBuilder()
        .withMeasureUnit(null)
        .withName("mozzarella")
        .withPluralName("mozzarella")
        .withStock(false)
        .withShopType(ShopType.DEKA)
        .withIngredientType(IngredientType.ZUIVEL)
        .build(4L));
    return FXCollections.observableList(ingredientNameList);
  }
}
