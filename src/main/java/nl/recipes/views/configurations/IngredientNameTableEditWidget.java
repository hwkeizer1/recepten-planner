package nl.recipes.views.configurations;

import org.springframework.stereotype.Component;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import nl.recipes.domain.IngredientName;
import nl.recipes.domain.IngredientType;
import nl.recipes.domain.ShopType;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.services.IngredientNameService;

import static nl.recipes.views.ViewConstants.*;

@Component
public class IngredientNameTableEditWidget {
	
	private IngredientNameService ingredientNameService;
	
	VBox rpWidget = new VBox();
	VBox ingredientNameTableBox = new VBox();
	VBox ingredientNameEditBox = new VBox();
	
	TableView<IngredientName> ingredientNameTableView = new TableView<>();
	TableColumn<IngredientName, String> nameColumn = new TableColumn<>("Naam");
	TableColumn<IngredientName, String> pluralNameColumn = new TableColumn<>("Meervoud");
	TableColumn<IngredientName, Boolean> stockColumn = new TableColumn<>("Voorraad");
	TableColumn<IngredientName, ShopType> shopTypeColumn = new TableColumn<>("Winkel");
	TableColumn<IngredientName, IngredientType> ingredientTypeColumn = new TableColumn<>("Type");
	
	TextField nameTextField = new TextField();
	Label nameError = new Label();
	
	TextField pluralNameTextField = new TextField();
	Label pluralNameError = new Label();
	
	CheckBox stockCheckBox = new CheckBox();
	
	ComboBox<ShopType> shopTypeComboBox = new ComboBox<>();
	ComboBox<IngredientType> ingredientTypeComboBox = new ComboBox<>();
	
	Button createButton = new Button("Toevoegen");
	Button updateButton = new Button("Wijzigen");
	Button removeButton = new Button("Verwijderen");
	
	private IngredientName selectedIngredientName;
	private final BooleanProperty modifiedProperty = new SimpleBooleanProperty(false);
	ChangeListener<IngredientName> ingredientNameChangeListener;
	
	public IngredientNameTableEditWidget(IngredientNameService ingredientNameService) {
		this.ingredientNameService = ingredientNameService;
		
		ingredientNameChangeListener = (observable, oldValue, newValue) -> {
			selectedIngredientName = newValue;
			if (newValue != null) {
				nameTextField.setText(selectedIngredientName.getName());
				nameError.setText("");
				pluralNameTextField.setText(selectedIngredientName.getPluralName());
				pluralNameError.setText("");
				stockCheckBox.setSelected(selectedIngredientName.isStock());
				shopTypeComboBox.setValue(selectedIngredientName.getShopType());
				ingredientTypeComboBox.setValue(selectedIngredientName.getIngredientType());
			} else {
				nameTextField.setText(null);
				pluralNameTextField.setText(null);
				stockCheckBox.setSelected(false);
				shopTypeComboBox.setValue(null);
				ingredientTypeComboBox.setValue(null);
			}
			modifiedProperty.set(false);
		};
		
		initializeIngredientNameTableBox();
		initializeIngredientNameEditBox();
		
		Label title = new Label("Ingrediënt bewerken");
		title.getStyleClass().add(TITLE);
		
		rpWidget.getStyleClass().addAll(DROP_SHADOW, WIDGET);
		rpWidget.getChildren().addAll(title, ingredientNameTableBox,ingredientNameEditBox);
		rpWidget.setPadding(new Insets(20));
	}
	
	public Node getIngredientNameTableEditWidget() {
		return rpWidget;
	}
	
	private void initializeIngredientNameTableBox() {
		ingredientNameTableView.setItems(ingredientNameService.getReadonlyIngredientNameList());
		ingredientNameTableView.setMinHeight(200); // prevent table from collapsing
		
		nameColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getName()));
		nameColumn.prefWidthProperty().bind(ingredientNameTableView.widthProperty().multiply(0.3));
		pluralNameColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getPluralName()));
		pluralNameColumn.prefWidthProperty().bind(ingredientNameTableView.widthProperty().multiply(0.3));
		
		stockColumn.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isStock()));
		stockColumn.setCellFactory(c -> new CheckBoxTableCell<>());
		stockColumn.prefWidthProperty().bind(ingredientNameTableView.widthProperty().multiply(0.1));
		
		shopTypeColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getShopType()));
		shopTypeColumn.prefWidthProperty().bind(ingredientNameTableView.widthProperty().multiply(0.15));
		ingredientTypeColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getIngredientType()));
		ingredientTypeColumn.prefWidthProperty().bind(ingredientNameTableView.widthProperty().multiply(0.15));
		
		ingredientNameTableView.getColumns().add(nameColumn);
		ingredientNameTableView.getColumns().add(pluralNameColumn);
		ingredientNameTableView.getColumns().add(stockColumn);
		ingredientNameTableView.getColumns().add(shopTypeColumn);
		ingredientNameTableView.getColumns().add(ingredientTypeColumn);
		ingredientNameTableView.getSelectionModel().selectedItemProperty().addListener(ingredientNameChangeListener);
		
		ingredientNameTableBox.getChildren().add(ingredientNameTableView);
		ingredientNameTableBox.getStyleClass().add(RP_TABLE);
	}
	
	private void initializeIngredientNameEditBox() {
		initializeButtons();
		
		ButtonBar buttonBar = new ButtonBar();
		buttonBar.setPadding(new Insets(0, 30, 0, 0));
		buttonBar.getButtons().addAll(removeButton, updateButton, createButton);
		
		ingredientNameEditBox.setPadding(new Insets(10));
		ingredientNameEditBox.setSpacing(20);
		ingredientNameEditBox.getChildren().addAll(createInputForm(), buttonBar);
	}
	
	private void initializeButtons() {
		removeButton.disableProperty().bind(ingredientNameTableView.getSelectionModel().selectedItemProperty().isNull());
		removeButton.setOnAction(this::removeIngredientName);
		
		updateButton.disableProperty()
		.bind(ingredientNameTableView.getSelectionModel().selectedItemProperty().isNull().or(modifiedProperty.not()).or(
				nameTextField.textProperty().isEmpty()));
		updateButton.setOnAction(this::updateIngredientName);
		
		createButton.disableProperty().bind(ingredientNameTableView.getSelectionModel().selectedItemProperty().isNotNull());
		createButton.setOnAction(this::createIngredientName);
	}
	
	private Node createInputForm() {
		
		GridPane inputForm = new GridPane();
		inputForm.setPadding(new Insets(10, 0, 0, 0));
		inputForm.setHgap(20);
				
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
		inputForm.getColumnConstraints().addAll(column0, column1, column2, column3);
		
		Label nameLabel = new Label("Ingrediënt naam:");
		inputForm.add(nameLabel, 0, 0);
		inputForm.add(nameTextField, 1, 0);
		inputForm.add(nameError,1 ,1 );
		
		Label pluralNameLabel = new Label("Meervoud:");
		inputForm.add(pluralNameLabel, 0, 2);
		inputForm.add(pluralNameTextField, 1, 2);
		inputForm.add(pluralNameError,1 ,3 );
		
		Label stockLabel = new Label("Voorraad:");
		inputForm.add(stockLabel, 0, 4);
		inputForm.add(stockCheckBox, 1, 4);
		
		Label shopTypeLabel = new Label("Winkel:");
		inputForm.add(shopTypeLabel,2, 0);
		shopTypeComboBox.getItems().setAll(ShopType.values());
		shopTypeComboBox.setMinWidth(150);
		inputForm.add(shopTypeComboBox, 3, 0);
		
		Label ingredientTypeLabel = new Label("Ingrediënt type:");
		inputForm.add(ingredientTypeLabel,2, 2);
		ingredientTypeComboBox.getItems().setAll(IngredientType.values());
		ingredientTypeComboBox.setMinWidth(150);
		inputForm.add(ingredientTypeComboBox, 3, 2);
		
		nameTextField.setOnKeyReleased(this::handleKeyReleasedAction);
		nameError.getStyleClass().add(VALIDATION);
		pluralNameTextField.setOnKeyReleased(this::handleKeyReleasedAction);
		pluralNameError.getStyleClass().add(VALIDATION);
		shopTypeComboBox.setOnAction(e -> modifiedProperty.set(true));
		ingredientTypeComboBox.setOnAction(e -> modifiedProperty.set(true));
		stockCheckBox.setOnAction(e -> modifiedProperty.set(true));
		
		return inputForm;
	}
	
	private void handleKeyReleasedAction(KeyEvent keyEvent) {
		nameError.setText(null);
		modifiedProperty.set(true);
	}
	
	private void createIngredientName(ActionEvent actionEvent) {
		IngredientName ingredientName = new IngredientName();
		ingredientName.setName(nameTextField.getText());
		ingredientName.setPluralName(pluralNameTextField.getText());
		ingredientName.setStock(stockCheckBox.isSelected());
		ingredientName.setShopType(shopTypeComboBox.getValue());
		ingredientName.setIngredientType(ingredientTypeComboBox.getValue());
		try {
			ingredientNameService.create(ingredientName);
			ingredientNameTableView.getSelectionModel().select(ingredientName);
			ingredientNameTableView.scrollTo(ingredientName);
		} catch (AlreadyExistsException | IllegalValueException e) {
			nameError.setText(e.getMessage());
		}
	}
	
	private void updateIngredientName(ActionEvent actionEvent) {
		IngredientName update = new IngredientName();
		update.setName(nameTextField.getText());
		update.setPluralName(pluralNameTextField.getText());
		update.setStock(stockCheckBox.isSelected());
		update.setShopType(shopTypeComboBox.getValue());
		update.setIngredientType(ingredientTypeComboBox.getValue());
		try {
			ingredientNameService.update(selectedIngredientName, update);
		} catch (NotFoundException | AlreadyExistsException e) {
			nameError.setText(e.getMessage());
		}
		modifiedProperty.set(false);
	}
	
	private void removeIngredientName(ActionEvent actionEvent) {
		try {
			ingredientNameService.remove(selectedIngredientName);
		} catch (NotFoundException e) {
			nameError.setText(e.getMessage());
		}
	}
}
