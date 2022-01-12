package nl.recipes.util;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.recipes.domain.MeasureUnit;
import nl.recipes.domain.Tag;

public class TestData {

	public MeasureUnit getMeasureUnit(Long id, String name, String pluralName) {
		return new MeasureUnit(id, name, pluralName);
	}
	
	public Tag getTag(Long id, String name) {
		return new Tag(id, name);
	}

	public ObservableList<MeasureUnit> getMeasureUnitList() {
		List<MeasureUnit> measureUnitList = new ArrayList<>();
		measureUnitList.add(getMeasureUnit(1L, "bakje", "bakjes"));
		measureUnitList.add(getMeasureUnit(2L, "eetlepel", "eetlepels"));
		measureUnitList.add(getMeasureUnit(3L, "pot", "potten"));
		measureUnitList.add(getMeasureUnit(4L, "theelepel", "theelepels"));
		return FXCollections.observableList(measureUnitList);
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
