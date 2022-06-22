package nl.recipes.views.converters;

import javafx.util.StringConverter;
import nl.recipes.domain.MeasureUnit;

public class MeasureUnitStringConverter extends StringConverter<MeasureUnit> {

  @Override
  public String toString(MeasureUnit object) {
    if (object != null) {
      return object.getName();
    } else {
      return null;
    }
  }

  @Override
  public MeasureUnit fromString(String string) {
    return null;
  }
}
