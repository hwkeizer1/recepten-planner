package nl.recipes.controllers.views;

import org.springframework.stereotype.Controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import net.rgielen.fxweaver.core.FxmlView;
import nl.recipes.domain.IngredientName;
import nl.recipes.domain.IngredientType;
import nl.recipes.domain.ShopType;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.services.IngredientNameService;

@Controller
@FxmlView("IngredientNameTableViewPanel.fxml")
public class IngredientNameTableViewPanelController {

	private final IngredientNameService ingredientNameService;

	@FXML AnchorPane ingredientNameTableViewPanel;
	
	@FXML TableView<IngredientName> ingredientNameTableView;
	@FXML TableColumn<IngredientName, String> colName;
	@FXML TableColumn<IngredientName, String> colPluralName;
	@FXML TableColumn<IngredientName, Boolean> colStock;
	@FXML TableColumn<IngredientName, ShopType> colShopType;
	@FXML TableColumn<IngredientName, IngredientType> colIngredientType;
	
	@FXML VBox changeIngredientNameVBox;
	
	@FXML TextField nameTextField;
	@FXML Label nameError;
	
	@FXML TextField pluralNameTextField;
	@FXML Label pluralNameError;
	
	@FXML CheckBox checkStock;
	
	@FXML ComboBox<ShopType> comboShopType;
	@FXML ComboBox<IngredientType> comboIngredientType;
	
	@FXML Button createButton;
	@FXML Button updateButton;
	@FXML Button removeButton;
	@FXML Button closeButton;
	
	private IngredientName selectedIngredientName;
	private final BooleanProperty modifiedProperty = new SimpleBooleanProperty(false);
	ChangeListener<IngredientName> ingredientNameChangeListener;
	
	public IngredientNameTableViewPanelController(IngredientNameService ingredientNameService) {
		this.ingredientNameService = ingredientNameService;

		ingredientNameChangeListener = (observable, oldValue, newValue) -> {
			selectedIngredientName = newValue;
			if (newValue != null) {
				nameTextField.setText(selectedIngredientName.getName());
				pluralNameTextField.setText(selectedIngredientName.getPluralName());
				checkStock.setSelected(selectedIngredientName.isStock());
				comboShopType.setValue(selectedIngredientName.getShopType());
				comboIngredientType.setValue(selectedIngredientName.getIngredientType());
			} else {
				nameTextField.setText(null);
				pluralNameTextField.setText(null);
				checkStock.setSelected(false);
				comboShopType.setValue(null);
				comboIngredientType.setValue(null);
			}
			modifiedProperty.set(false);
		};
	}

	public AnchorPane getIngredientNameTableViewPanel() {
		return ingredientNameTableViewPanel;
	}
	
	public void initialize() {
		AnchorPane.setTopAnchor(changeIngredientNameVBox, 0.0);
		AnchorPane.setBottomAnchor(changeIngredientNameVBox, 0.0);
		changeIngredientNameVBox.setBackground(new Background(new BackgroundFill(Color.web("#ffffcc"), CornerRadii.EMPTY , Insets.EMPTY)));
		
		initializeIngredientNameTableView();
		initializeControls();
		initializeButtons();
	}
	
	private void initializeIngredientNameTableView() {
		ingredientNameTableView.setItems(ingredientNameService.getReadonlyIngredientNameList());
		
		colName.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getName()));
		colPluralName.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getPluralName()));
		
		colStock.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isStock()));
		colStock.setCellFactory(c -> new CheckBoxTableCell<>());
		
		colShopType.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getShopType()));
		colIngredientType.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getIngredientType()));

		// Remove the previous listener before adding one
		ingredientNameTableView.getSelectionModel().selectedItemProperty().removeListener(ingredientNameChangeListener);
		ingredientNameTableView.getSelectionModel().selectedItemProperty().addListener(ingredientNameChangeListener);
	}
	
	private void initializeControls() {
		comboShopType.getItems().setAll(ShopType.values());
		comboShopType.setValue(ShopType.OVERIG);
		comboShopType.setOnAction(e -> modifiedProperty.set(true));
		
		comboIngredientType.getItems().setAll(IngredientType.values());
		comboIngredientType.setValue(IngredientType.OVERIG);
		comboIngredientType.setOnAction(e -> modifiedProperty.set(true));
		
		checkStock.setOnAction(e -> modifiedProperty.set(true));
	}
	
	private void initializeButtons() {
		removeButton.disableProperty().bind(ingredientNameTableView.getSelectionModel().selectedItemProperty().isNull());
		updateButton.disableProperty()
		.bind(ingredientNameTableView.getSelectionModel().selectedItemProperty().isNull().or(modifiedProperty.not()).or(
				nameTextField.textProperty().isEmpty()));
		createButton.disableProperty().bind(ingredientNameTableView.getSelectionModel().selectedItemProperty().isNotNull());
	}
	
	@FXML
	private void handleKeyReleasedAction(KeyEvent keyEvent) {
		nameError.setText(null);
		modifiedProperty.set(true);
	}
	
	@FXML
	private void createIngredientName(ActionEvent actionEvent) {
		IngredientName ingredientName = new IngredientName();
		ingredientName.setName(nameTextField.getText());
		ingredientName.setPluralName(pluralNameTextField.getText());
		ingredientName.setStock(checkStock.isSelected());
		ingredientName.setShopType(comboShopType.getValue());
		ingredientName.setIngredientType(comboIngredientType.getValue());
		try {
			ingredientNameService.create(ingredientName);
			ingredientNameTableView.getSelectionModel().select(ingredientName);
			ingredientNameTableView.scrollTo(ingredientName);
		} catch (AlreadyExistsException | IllegalValueException e) {
			nameError.setText(e.getMessage());
		}
	}
	
	@FXML
	private void updateIngredientName(ActionEvent actionEvent) {
		IngredientName update = new IngredientName();
		update.setName(nameTextField.getText());
		update.setPluralName(pluralNameTextField.getText());
		update.setStock(checkStock.isSelected());
		update.setShopType(comboShopType.getValue());
		update.setIngredientType(comboIngredientType.getValue());
		try {
			ingredientNameService.update(selectedIngredientName, update);
		} catch (NotFoundException | AlreadyExistsException e) {
			nameError.setText(e.getMessage());
		}
		modifiedProperty.set(false);
	}
	
	@FXML
	private void removeIngredientName(ActionEvent actionEvent) {
		try {
			ingredientNameService.remove(selectedIngredientName);
		} catch (NotFoundException e) {
			nameError.setText(e.getMessage());
		}
	}
	
}
