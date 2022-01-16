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
@FxmlView("IngredientNamePanel.fxml")
public class IngredientNamePanelController {

	private final IngredientNameService ingredientNameService;

	@FXML AnchorPane ingredientNamePanel;
	
	@FXML TableView<IngredientName> ingredientNameTableView;
	@FXML TableColumn<IngredientName, String> nameColumn;
	@FXML TableColumn<IngredientName, String> pluralNameColumn;
	@FXML TableColumn<IngredientName, Boolean> stockColumn;
	@FXML TableColumn<IngredientName, ShopType> shopTypeColumn;
	@FXML TableColumn<IngredientName, IngredientType> ingredientTypeColumn;
	
	@FXML VBox ingredientNameVBox;
	
	@FXML TextField nameTextField;
	@FXML Label nameError;
	
	@FXML TextField pluralNameTextField;
	@FXML Label pluralNameError;
	
	@FXML CheckBox stockCheckBox;
	
	@FXML ComboBox<ShopType> shopTypeComboBox;
	@FXML ComboBox<IngredientType> ingredientTypeComboBox;
	
	@FXML Button createButton;
	@FXML Button updateButton;
	@FXML Button removeButton;
	@FXML Button closeButton;
	
	private IngredientName selectedIngredientName;
	private final BooleanProperty modifiedProperty = new SimpleBooleanProperty(false);
	ChangeListener<IngredientName> ingredientNameChangeListener;
	
	public IngredientNamePanelController(IngredientNameService ingredientNameService) {
		this.ingredientNameService = ingredientNameService;

		ingredientNameChangeListener = (observable, oldValue, newValue) -> {
			selectedIngredientName = newValue;
			if (newValue != null) {
				nameTextField.setText(selectedIngredientName.getName());
				pluralNameTextField.setText(selectedIngredientName.getPluralName());
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
	}

	public AnchorPane getIngredientNamePanel() {
		return ingredientNamePanel;
	}
	
	public void initialize() {
		AnchorPane.setTopAnchor(ingredientNameVBox, 0.0);
		AnchorPane.setBottomAnchor(ingredientNameVBox, 0.0);
		ingredientNameVBox.setBackground(new Background(new BackgroundFill(Color.web("#ffffcc"), CornerRadii.EMPTY , Insets.EMPTY)));
		
		initializeIngredientNameTableView();
		initializeControls();
		initializeButtons();
	}
	
	private void initializeIngredientNameTableView() {
		ingredientNameTableView.setItems(ingredientNameService.getReadonlyIngredientNameList());
		
		nameColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getName()));
		pluralNameColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getPluralName()));
		
		stockColumn.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isStock()));
		stockColumn.setCellFactory(c -> new CheckBoxTableCell<>());
		
		shopTypeColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getShopType()));
		ingredientTypeColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getIngredientType()));

		// Remove the previous listener before adding one
		ingredientNameTableView.getSelectionModel().selectedItemProperty().removeListener(ingredientNameChangeListener);
		ingredientNameTableView.getSelectionModel().selectedItemProperty().addListener(ingredientNameChangeListener);
	}
	
	private void initializeControls() {
		shopTypeComboBox.getItems().setAll(ShopType.values());
		shopTypeComboBox.setValue(ShopType.OVERIG);
		shopTypeComboBox.setOnAction(e -> modifiedProperty.set(true));
		
		ingredientTypeComboBox.getItems().setAll(IngredientType.values());
		ingredientTypeComboBox.setValue(IngredientType.OVERIG);
		ingredientTypeComboBox.setOnAction(e -> modifiedProperty.set(true));
		
		stockCheckBox.setOnAction(e -> modifiedProperty.set(true));
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
	
	@FXML
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
	
	@FXML
	private void removeIngredientName(ActionEvent actionEvent) {
		try {
			ingredientNameService.remove(selectedIngredientName);
		} catch (NotFoundException e) {
			nameError.setText(e.getMessage());
		}
	}
	
}
