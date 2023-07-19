package nl.recipes.util.testdata;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import nl.recipes.domain.Planning;

public class MockPlannings {

  public List<Planning> getPlanningList() {
    List<Planning> planningList = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      planningList
          .add(new Planning.PlanningBuilder().withDate(LocalDate.now().plusDays(i)).withServings(2).build());
    }
    return planningList;
  }

  public List<Planning> getPlanningListFrom2DaysAgo() {
    List<Planning> planningList = new ArrayList<>();
    for (int i = -2; i < 8; i++) {
      planningList
          .add(new Planning.PlanningBuilder().withDate(LocalDate.now().plusDays(i)).withServings(2).build());
    }
    return planningList;
  }
}
