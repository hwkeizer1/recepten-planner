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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import net.rgielen.fxweaver.core.FxmlView;
import nl.recipes.domain.MeasureUnit;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.services.MeasureUnitService;

@Controller
@FxmlView("MeasureUnitTableViewPanel.fxml")
public class MeasureUnitTableViewPanelController {

	private final MeasureUnitService measureUnitService;

	@FXML AnchorPane measureUnitTableViewPanel;
	
	@FXML TableView<MeasureUnit> measureUnitTableView;
	@FXML TableColumn<MeasureUnit, String> colName;
	@FXML TableColumn<MeasureUnit, String> colPluralName;
	
	@FXML TextField nameTextField;
	@FXML Label nameError;
	
	@FXML TextField pluralNameTextField;
	@FXML Label pluralNameError;
	
	@FXML VBox changeMeasureUnitVBox;
	
	@FXML Button createButton;
	@FXML Button updateButton;
	@FXML Button removeButton;
	
	private MeasureUnit selectedMeasureUnit;
	private final BooleanProperty modifiedProperty = new SimpleBooleanProperty(false);
	ChangeListener<MeasureUnit> measureUnitChangeListener;
	
	public MeasureUnitTableViewPanelController(MeasureUnitService measureUnitService) {
		this.measureUnitService = measureUnitService;
		
		measureUnitChangeListener = (observable, oldValue, newValue) -> {
			selectedMeasureUnit = newValue;
			modifiedProperty.set(false);
			if (newValue != null) {
				nameTextField.setText(selectedMeasureUnit.getName());
				pluralNameTextField.setText(selectedMeasureUnit.getPluralName());
			} else {
				nameTextField.setText(null);
				pluralNameTextField.setText(null);
			}
		};
	}

	public AnchorPane getMeasureUnitTableViewPanel() {
		return measureUnitTableViewPanel;
	}
	
	public void initialize() {
		AnchorPane.setTopAnchor(changeMeasureUnitVBox, 0.0);
		AnchorPane.setBottomAnchor(changeMeasureUnitVBox, 0.0);
		changeMeasureUnitVBox.setBackground(new Background(new BackgroundFill(Color.web("#ffffcc"), CornerRadii.EMPTY , Insets.EMPTY)));
		
		initializeMeasureUnitTableView();
		initializeButtons();
	}
	
	private void initializeMeasureUnitTableView() {
		measureUnitTableView.setItems(measureUnitService.getReadonlyMeasureUnitList());
		
		colName.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getName()));
		colPluralName.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getPluralName()));
		
		// Remove the previous listener before adding one
		measureUnitTableView.getSelectionModel().selectedItemProperty().removeListener(measureUnitChangeListener);
		measureUnitTableView.getSelectionModel().selectedItemProperty().addListener(measureUnitChangeListener);
	}
	
	private void initializeButtons() {
		removeButton.disableProperty().bind(measureUnitTableView.getSelectionModel().selectedItemProperty().isNull());
		updateButton.disableProperty()
		.bind(measureUnitTableView.getSelectionModel().selectedItemProperty().isNull().or(modifiedProperty.not()).or(
				nameTextField.textProperty().isEmpty()));
		createButton.disableProperty().bind(measureUnitTableView.getSelectionModel().selectedItemProperty().isNotNull());
	}
	
	@FXML
	private void handleKeyReleasedAction(KeyEvent keyEvent) {
		nameError.setText(null);
		modifiedProperty.set(true);
	}
	
	@FXML
	private void createMeasureUnit(ActionEvent actionEvent) {
		MeasureUnit measureUnit = new MeasureUnit(nameTextField.getText(), pluralNameTextField.getText());
		try {
			measureUnitService.create(measureUnit);
			measureUnitTableView.getSelectionModel().select(measureUnit);
			measureUnitTableView.scrollTo(measureUnit);
		} catch (AlreadyExistsException | IllegalValueException e) {
			nameError.setText(e.getMessage());
		}
	}
	
	@FXML
	private void updateMeasureUnit(ActionEvent actionEvent) {
		MeasureUnit update = new MeasureUnit();
		update.setName(nameTextField.getText());
		update.setPluralName(pluralNameTextField.getText());
		try {
			measureUnitService.update(selectedMeasureUnit, update);
		} catch (NotFoundException | AlreadyExistsException e) {
			nameError.setText(e.getMessage());
		}
		modifiedProperty.set(false);
	}
	
	@FXML
	private void removeMeasureUnit(ActionEvent actionEvent) {
		try {
			measureUnitService.remove(selectedMeasureUnit);
		} catch (NotFoundException e) {
			nameError.setText(e.getMessage());
		}
	}
	
}
