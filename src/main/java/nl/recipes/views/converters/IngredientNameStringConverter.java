package nl.recipes.views.converters;

import javafx.util.StringConverter;
import nl.recipes.domain.IngredientName;

public class IngredientNameStringConverter extends StringConverter<IngredientName> {

  @Override
  public String toString(IngredientName object) {
    if (object != null) {
      return object.getListLabel();
    } else {
      return null;
    }
  }

  @Override
  public IngredientName fromString(String string) {
    return null;
  }
}
