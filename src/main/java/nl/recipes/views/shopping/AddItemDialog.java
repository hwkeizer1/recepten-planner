package nl.recipes.views.shopping;

import java.util.Optional;
import org.controlsfx.control.SearchableComboBox;
import org.controlsfx.control.textfield.TextFields;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import nl.recipes.domain.IngredientType;
import nl.recipes.domain.MeasureUnit;
import nl.recipes.domain.ShopType;
import nl.recipes.domain.ShoppingItem;
import nl.recipes.views.converters.MeasureUnitStringConverter;

public class AddItemDialog {
  Dialog<ShoppingItem> dialog = new Dialog<>();

  TextField amountTextField;
  TextField nameField;
  TextField pluralNameField;

  SearchableComboBox<MeasureUnit> measureUnitComboBox;
  ComboBox<ShopType> shopTypeComboBox;
  ComboBox<IngredientType> ingredientTypeComboBox;


  public AddItemDialog(ObservableList<MeasureUnit> measureUnits) {
    dialog.setTitle("Eenmalige boodschap toevoegen");
    dialog.initModality(Modality.WINDOW_MODAL);
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == ButtonType.OK) {
        return new ShoppingItem.ShoppingItemBuilder()
            .withAmount(
                (amountTextField.getText() == null || amountTextField.getText().isEmpty()) ? null
                    : Float.valueOf(amountTextField.getText()))
            .withMeasureUnit(measureUnitComboBox.getValue()).withName(nameField.getText())
            .withOnList(true)
            .withShopType(shopTypeComboBox.getValue())
            .withIngredientType(ingredientTypeComboBox.getValue()).build();
      }
      return null;
    });

    measureUnitComboBox = new SearchableComboBox<>();
    measureUnitComboBox.setConverter(new MeasureUnitStringConverter());
    TextFields.bindAutoCompletion(measureUnitComboBox.getEditor(), measureUnitComboBox.getItems(),
        measureUnitComboBox.getConverter());

    measureUnitComboBox.setItems(measureUnits);
    dialog.getDialogPane().setPadding(new Insets(20, 20, 20, 20));
    dialog.getDialogPane().setContent(createDialogForm());
  }

  public Optional<ShoppingItem> getDialogResult() {
    return dialog.showAndWait();
  }

  private GridPane createDialogForm() {
    GridPane form = new GridPane();
    form.setPadding(new Insets(10, 0, 0, 0));
    form.setHgap(20);
    form.setVgap(10);

    ColumnConstraints column0 = new ColumnConstraints();
    column0.setPercentWidth(20);
    column0.setHalignment(HPos.RIGHT);
    ColumnConstraints column1 = new ColumnConstraints();
    column1.setPercentWidth(30);
    ColumnConstraints column2 = new ColumnConstraints();
    column2.setPercentWidth(20);
    column2.setHalignment(HPos.RIGHT);
    ColumnConstraints column3 = new ColumnConstraints();
    column3.setPercentWidth(30);

    form.getColumnConstraints().addAll(column0, column1, column2, column3);

    Label amountLabel = new Label("Hoeveelheid:");
    amountTextField = new TextField();
    form.add(amountLabel, 0, 0);
    form.add(amountTextField, 1, 0);

    Label measureUnitLabel = new Label("Maateenheid:");
    form.add(measureUnitLabel, 0, 1);
    form.add(measureUnitComboBox, 1, 1);

    Label nameLabel = new Label("Naam:");
    nameField = new TextField();
    GridPane.setValignment(nameLabel, VPos.TOP);
    form.add(nameLabel, 0, 2);
    form.add(nameField, 1, 2);

    Label shopTypeLabel = new Label("Winkel:");
    form.add(shopTypeLabel, 2, 0);
    shopTypeComboBox = new ComboBox<>();
    shopTypeComboBox.getItems().setAll(ShopType.values());
    form.add(shopTypeComboBox, 3, 0);

    Label ingredientTypeLabel = new Label("Ingrediënt type:");
    form.add(ingredientTypeLabel, 2, 1);
    ingredientTypeComboBox = new ComboBox<>();
    ingredientTypeComboBox.getItems().setAll(IngredientType.values());
    ingredientTypeComboBox.setMinWidth(150);
    form.add(ingredientTypeComboBox, 3, 1);

    return form;
  }
}
