package nl.recipes.util;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.recipes.domain.Tag;

public class TestData {

	public Tag getTag(Long id, String name) {
		return new Tag(id, name);
	}

	public ObservableList<Tag> getTagList() {
		List<Tag> tagList = new ArrayList<>();
		tagList.add(getTag(1L, "Vegetarisch"));
		tagList.add(getTag(2L, "Makkelijk"));
		tagList.add(getTag(3L, "Feestelijk"));
		tagList.add(getTag(4L, "Pasta"));
		return FXCollections.observableList(tagList);
	}
}
