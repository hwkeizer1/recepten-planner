package nl.recipes.util.testdata;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.recipes.domain.Tag;

public class MockTags {
  
  public static final String NEWTAG = "Newtag";

  public ObservableList<Tag> getTagList() {
    List<Tag> tagList = new ArrayList<>();
    tagList.add(getTag(1L, "Vegetarisch"));
    tagList.add(getTag(2L, "Makkelijk"));
    tagList.add(getTag(3L, "Feestelijk"));
    tagList.add(getTag(4L, "Pasta"));
    return FXCollections.observableList(tagList);
  }
  
  public ObservableList<Tag> getOrderedTagList() {
    ObservableList<Tag> tagList = getTagList();
    tagList.sort((t1, t2)-> t1.getName().compareTo(t2.getName()));
    return FXCollections.observableList(tagList);
  }
  
  public Tag getTag(Long id, String name) {
    return new Tag(id, name);
  }
}
