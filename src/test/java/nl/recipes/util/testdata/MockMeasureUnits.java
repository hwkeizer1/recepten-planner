package nl.recipes.util.testdata;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.recipes.domain.MeasureUnit;

public class MockMeasureUnits {

  public ObservableList<MeasureUnit> getMeasureUnitList() {
    List<MeasureUnit> measureUnitList = new ArrayList<>();
    measureUnitList.add( new MeasureUnit.MeasureUnitBuilder()
        .withName("bakje")
        .withPluralName("bakjes")
        .build(1L));
    measureUnitList.add( new MeasureUnit.MeasureUnitBuilder()
        .withName("eetlepel")
        .withPluralName("eetlepels")
        .build(2L));
    measureUnitList.add( new MeasureUnit.MeasureUnitBuilder()
        .withName("pot")
        .withPluralName("potten")
        .build(3L));
    measureUnitList.add( new MeasureUnit.MeasureUnitBuilder()
        .withName("theelepel")
        .withPluralName("theelepels")
        .build(4L));
    return FXCollections.observableList(measureUnitList);
  }
}
