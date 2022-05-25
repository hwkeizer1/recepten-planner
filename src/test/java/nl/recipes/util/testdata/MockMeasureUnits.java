package nl.recipes.util.testdata;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.recipes.domain.MeasureUnit;

public class MockMeasureUnits {

  public ObservableList<MeasureUnit> getMeasureUnitList() {
    List<MeasureUnit> measureUnitList = new ArrayList<>();
    measureUnitList.add(getMeasureUnit(1L, "bakje", "bakjes"));
    measureUnitList.add(getMeasureUnit(2L, "eetlepel", "eetlepels"));
    measureUnitList.add(getMeasureUnit(3L, "pot", "potten"));
    measureUnitList.add(getMeasureUnit(4L, "theelepel", "theelepels"));
    return FXCollections.observableList(measureUnitList);
  }
  
  public MeasureUnit getMeasureUnit(Long id, String name, String pluralName) {
    return new MeasureUnit(id, name, pluralName);
  }
}
