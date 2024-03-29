package nl.recipes.views.recipes;

import static nl.recipes.views.ViewConstants.CSS_DROP_SHADOW;
import static nl.recipes.views.ViewConstants.CSS_EMPTY_INGREDIENT_TABLE;
import static nl.recipes.views.ViewConstants.CSS_INGREDIENT_EDIT_TABLE;
import static nl.recipes.views.ViewConstants.CSS_BASIC_TABLE;
import static nl.recipes.views.ViewConstants.CSS_WIDGET;
import java.util.ArrayList;
import org.controlsfx.control.SearchableComboBox;
import org.controlsfx.control.textfield.TextFields;
import org.springframework.stereotype.Component;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import nl.recipes.domain.Ingredient;
import nl.recipes.domain.IngredientName;
import nl.recipes.services.IngredientNameService;
import nl.recipes.views.components.utils.Utils;
import nl.recipes.views.converters.IngredientNameStringConverter;

@Component
public class IngredientEditView implements ListChangeListener<IngredientName> {

  private final IngredientNameService ingredientNameService;

  ObservableList<Ingredient> ingredientList;

  VBox ingredientPanel;

  TableView<Ingredient> ingredientTable;

  TextField amountTextField;

  SearchableComboBox<IngredientName> ingredientNameComboBox;

  private Ingredient selectedIngredient;

  private final BooleanProperty modifiedProperty = new SimpleBooleanProperty(false);

  public IngredientEditView(IngredientNameService ingredientNameService) {
    this.ingredientNameService = ingredientNameService;

    this.ingredientNameService.addListener(this);

    ingredientPanel = new VBox();
    ingredientPanel.setPadding(new Insets(10, 0, 0, 0));
    ingredientPanel.getStyleClass().addAll(CSS_WIDGET, CSS_DROP_SHADOW);
    amountTextField = new TextField();
    ingredientNameComboBox = new SearchableComboBox<>();
    ingredientNameComboBox.setConverter(new IngredientNameStringConverter());
    TextFields.bindAutoCompletion(ingredientNameComboBox.getEditor(),
        ingredientNameComboBox.getItems(), ingredientNameComboBox.getConverter());
    ingredientNameComboBox.getItems().setAll(this.ingredientNameService.getList());

    ingredientPanel.getChildren().addAll(getIngredientTableViewPanel(), getIngredientEditPanel());
  }

  public Node getIngredientPanel() {
    return ingredientPanel;
  }

  public void setIngredientList(ObservableList<Ingredient> ingredients) {
    if (ingredients.isEmpty()) {
      ingredientList = FXCollections.observableList(new ArrayList<Ingredient>());
    } else {
      ingredientList = ingredients;
    }

    ingredientTable.setItems(ingredientList);
    Label ingredientTableEmpty = new Label("Nog geen ingredienten toegevoegd");
    ingredientTableEmpty.getStyleClass().add(CSS_EMPTY_INGREDIENT_TABLE);
    ingredientTable.setPlaceholder(ingredientTableEmpty);
    ingredientTable.setFixedCellSize(25);

    ingredientTable.prefHeightProperty().bind(
        Bindings.size(ingredientTable.getItems()).multiply(ingredientTable.getFixedCellSize()));
    ingredientTable.setMinHeight(25);
    ingredientTable.maxHeightProperty().bind(ingredientTable.prefHeightProperty());
  }

  public ObservableList<Ingredient> getIngredientList() {
    return ingredientList;
  }

  private Node getIngredientTableViewPanel() {
    VBox ingredientTableViewPanel = new VBox();

    ingredientTable = new TableView<>();
    ingredientTable.getStyleClass().addAll(CSS_BASIC_TABLE, CSS_INGREDIENT_EDIT_TABLE);
    TableColumn<Ingredient, Number> amountColumn = new TableColumn<>();
    TableColumn<Ingredient, String> measureUnitColumn = new TableColumn<>();
    TableColumn<Ingredient, String> ingredientNameColumn = new TableColumn<>();

    ingredientTable.getColumns().add(amountColumn);
    ingredientTable.getColumns().add(measureUnitColumn);
    ingredientTable.getColumns().add(ingredientNameColumn);

    ChangeListener<Ingredient> ingredientChangeListener = (observable, oldValue, newValue) -> {
      selectedIngredient = newValue;
      if (newValue != null) {
        amountTextField.setText(selectedIngredient.getAmount() == null ? null
            : Utils.format(selectedIngredient.getAmount()));
        ingredientNameComboBox.setValue(selectedIngredient.getIngredientName());
      } else {
        amountTextField.setText(null);
        ingredientNameComboBox.setValue(null);
      }
      modifiedProperty.set(false);
    };

    amountColumn.prefWidthProperty().bind(ingredientTable.widthProperty().multiply(0.1));
    measureUnitColumn.prefWidthProperty().bind(ingredientTable.widthProperty().multiply(0.40));
    ingredientNameColumn.prefWidthProperty().bind(ingredientTable.widthProperty().multiply(0.50));

    amountColumn.setCellValueFactory(
        c -> (c.getValue().getAmount() != null && (10 * c.getValue().getAmount() % 10) == 0)
            ? new ReadOnlyObjectWrapper<>(Math.round(c.getValue().getAmount()))
            : new ReadOnlyObjectWrapper<>(c.getValue().getAmount()));

    measureUnitColumn.setCellValueFactory(c -> {
      if (c.getValue().getIngredientName().getMeasureUnit() == null) {
        return new ReadOnlyObjectWrapper<>();
      } else {
        return new ReadOnlyObjectWrapper<>(
            (c.getValue().getAmount() == null || c.getValue().getAmount() <= 1)
                ? c.getValue().getIngredientName().getMeasureUnit().getName()
                : c.getValue().getIngredientName().getMeasureUnit().getPluralName());
      }
    });

    ingredientNameColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
        (c.getValue().getAmount() == null || c.getValue().getAmount() <= 1)
            ? c.getValue().getIngredientName().getName()
            : c.getValue().getIngredientName().getPluralName()));

    ingredientTable.getSelectionModel().selectedItemProperty()
        .addListener(ingredientChangeListener);

    ingredientTableViewPanel.getChildren().add(ingredientTable);

    return ingredientTableViewPanel;
  }

  private Node getIngredientEditPanel() {
    VBox ingredientEditPanel = new VBox();

    ingredientEditPanel.setPadding(new Insets(10));
    ingredientEditPanel.setSpacing(20);
    ingredientEditPanel.getChildren().addAll(createInputForm(), createButtonBar());
    return ingredientEditPanel;
  }

  private Node createButtonBar() {
    ButtonBar buttonBar = new ButtonBar();
    Button createButton = new Button("Toevoegen");
    Button updateButton = new Button("Wijzigen");
    Button removeButton = new Button("Verwijderen");

    buttonBar.setPadding(new Insets(0, 30, 0, 0));
    buttonBar.getButtons().addAll(removeButton, updateButton, createButton);

    removeButton.disableProperty()
        .bind(ingredientTable.getSelectionModel().selectedItemProperty().isNull());
    removeButton.setOnAction(this::removeIngredient);

    updateButton.disableProperty().bind(ingredientTable.getSelectionModel().selectedItemProperty()
        .isNull().or(modifiedProperty.not()));
    updateButton.setOnAction(this::updateIngredient);

    createButton.disableProperty()
        .bind(ingredientTable.getSelectionModel().selectedItemProperty().isNotNull());
    createButton.setOnAction(this::createIngredient);

    return buttonBar;
  }

  private Node createInputForm() {

    GridPane inputForm = new GridPane();
    inputForm.setPadding(new Insets(10, 0, 0, 0));
    inputForm.setHgap(20);
    inputForm.setVgap(10);

    ColumnConstraints column0 = new ColumnConstraints();
    column0.setPercentWidth(35);
    column0.setHalignment(HPos.RIGHT);
    ColumnConstraints column1 = new ColumnConstraints();
    column1.setPercentWidth(65);
    inputForm.getColumnConstraints().addAll(column0, column1);

    Label amountLabel = new Label("Hoeveelheid:");
    inputForm.add(amountLabel, 0, 0);
    amountTextField.setMaxWidth(80);
    inputForm.add(amountTextField, 1, 0);

    Label ingredientNameLabel = new Label("Ingrediënt:");
    inputForm.add(ingredientNameLabel, 0, 1);
    ingredientNameComboBox.setMinWidth(250);
    inputForm.add(ingredientNameComboBox, 1, 1);

    amountTextField.setOnKeyReleased(this::handleKeyReleasedAction);
    ingredientNameComboBox.setOnAction(e -> modifiedProperty.set(true));

    return inputForm;
  }

  private void handleKeyReleasedAction(KeyEvent keyEvent) {
    modifiedProperty.set(true);
  }

  private void createIngredient(ActionEvent actionEvent) {
    Ingredient ingredient = new Ingredient.IngredientBuilder()
        .withAmount(
            (amountTextField.getText() == null || amountTextField.getText().isEmpty()) ? 0.0f
                : Float.valueOf(amountTextField.getText()))
        .withIngredientName(ingredientNameComboBox.getValue()).build();

    ingredientList.add(ingredient);
    ingredientTable.getSelectionModel().clearSelection();
    amountTextField.setText(null);
    ingredientNameComboBox.setValue(null);
  }

  private void updateIngredient(ActionEvent actionEvent) {
    int index = ingredientList.indexOf(selectedIngredient);
    selectedIngredient
        .setAmount((amountTextField.getText() == null || amountTextField.getText().isEmpty()) ? 0.0f
            : Float.valueOf(amountTextField.getText()));
    selectedIngredient.setIngredientName(ingredientNameComboBox.getValue());
    ingredientList.set(index, selectedIngredient);
    ingredientTable.getSelectionModel().clearSelection();
  }

  private void removeIngredient(ActionEvent actionEvent) {
    ingredientList.remove(selectedIngredient);
  }

  @Override
  public void onChanged(Change<? extends IngredientName> c) {
    ingredientNameComboBox.getItems().setAll(this.ingredientNameService.getList());
  }
}
