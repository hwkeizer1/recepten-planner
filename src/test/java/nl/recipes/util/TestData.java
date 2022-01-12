package nl.recipes.util;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.recipes.domain.IngredientName;
import nl.recipes.domain.IngredientType;
import nl.recipes.domain.MeasureUnit;
import nl.recipes.domain.ShopType;
import nl.recipes.domain.Tag;

public class TestData {
	
	public Tag getTag(Long id, String name) {
		return new Tag(id, name);
	}
	
	public MeasureUnit getMeasureUnit(Long id, String name, String pluralName) {
		return new MeasureUnit(id, name, pluralName);
	}
	
	public IngredientName getIngredientName(Long id, String name, String pluralName, boolean stock, ShopType shopType, IngredientType ingredientType) {
		return new IngredientName(id, name, pluralName, stock, shopType, ingredientType);
	}
	
	public ObservableList<Tag> getTagList() {
		List<Tag> tagList = new ArrayList<>();
		tagList.add(getTag(1L, "Vegetarisch"));
		tagList.add(getTag(2L, "Makkelijk"));
		tagList.add(getTag(3L, "Feestelijk"));
		tagList.add(getTag(4L, "Pasta"));
		return FXCollections.observableList(tagList);
	}
	
	public ObservableList<MeasureUnit> getMeasureUnitList() {
		List<MeasureUnit> measureUnitList = new ArrayList<>();
		measureUnitList.add(getMeasureUnit(1L, "bakje", "bakjes"));
		measureUnitList.add(getMeasureUnit(2L, "eetlepel", "eetlepels"));
		measureUnitList.add(getMeasureUnit(3L, "pot", "potten"));
		measureUnitList.add(getMeasureUnit(4L, "theelepel", "theelepels"));
		return FXCollections.observableList(measureUnitList);
	}
	
	public ObservableList<IngredientName> getIngredientNameList() {
		List<IngredientName> ingredientnameList = new ArrayList<>();
		ingredientnameList.add(getIngredientName(1L, "water", "water", true, null, IngredientType.OVERIG));
		ingredientnameList.add(getIngredientName(2L, "ui", "uien", true, ShopType.EKO, IngredientType.GROENTE));
		ingredientnameList.add(getIngredientName(3L, "prei", "preien", false, ShopType.DEKA, IngredientType.GROENTE));
		ingredientnameList.add(getIngredientName(4L, "mozzarella", "mozzarella", false, ShopType.DEKA, IngredientType.ZUIVEL));	
		return FXCollections.observableList(ingredientnameList);
	}
}
