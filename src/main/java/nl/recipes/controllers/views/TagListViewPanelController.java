package nl.recipes.controllers.views;


import org.springframework.stereotype.Controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import net.rgielen.fxweaver.core.FxmlView;
import nl.recipes.domain.Tag;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.services.TagService;

@Controller
@FxmlView("TagListViewPanel.fxml")
public class TagListViewPanelController {
	
	private final TagService tagService;
	
	private RootController rootController;
	
	@FXML AnchorPane tagListViewPanel;
	
	@FXML ListView<Tag> tagListView;
	
	@FXML TextField nameTextField;
	@FXML Label nameError;
	
	@FXML VBox changeTagVBox;
	
	@FXML Button createButton;
	@FXML Button updateButton;
	@FXML Button removeButton;
	@FXML Button closeButton;
	
	private Tag selectedTag;
	private final BooleanProperty modifiedProperty = new SimpleBooleanProperty(false);
	private ChangeListener<Tag> tagChangeListener;
	
	public TagListViewPanelController(TagService tagService) {
		this.tagService = tagService;
	}

	public AnchorPane getTagListView() {
		return tagListViewPanel;
	}
	
	public void setRootController(RootController rootController) {
		this.rootController = rootController;
	}

	public void initialize() {
		AnchorPane.setTopAnchor(changeTagVBox, 0.0);
		AnchorPane.setBottomAnchor(changeTagVBox, 0.0);
		changeTagVBox.setBackground(new Background(new BackgroundFill(Color.web("#ffffcc"), CornerRadii.EMPTY , Insets.EMPTY)));
		
		initializeTagListView();
		initializeButtons();
	}
	
	private void initializeTagListView() {
		tagListView.setItems(tagService.getReadonlyTagList());

		tagChangeListener = (observable, oldValue, newValue) -> {
			selectedTag = newValue;
			modifiedProperty.set(false);
			if (newValue != null) {
				nameTextField.setText(selectedTag.getName());
			} else {
				nameTextField.setText("");
			}
		};

		tagListView.getSelectionModel().selectedItemProperty().addListener(tagChangeListener);
	}
	
	private void initializeButtons() {
		removeButton.disableProperty().bind(tagListView.getSelectionModel().selectedItemProperty().isNull());
		updateButton.disableProperty()
		.bind(tagListView.getSelectionModel().selectedItemProperty().isNull().or(modifiedProperty.not()).or(
				nameTextField.textProperty().isEmpty()));
		createButton.disableProperty().bind(tagListView.getSelectionModel().selectedItemProperty().isNotNull());
	}

	@FXML
	public void closeTagListViewPanel(ActionEvent actionEvent) {
		tagListView.getSelectionModel().selectedItemProperty().removeListener(tagChangeListener);
		rootController.closeLeftSidePanel();
	}
	
	@FXML
	private void handleKeyReleasedAction(KeyEvent keyEvent) {
		nameError.setText(null);
		modifiedProperty.set(true);
	}
	
	@FXML
	private void createTag(ActionEvent actionEvent) {
		Tag tag = new Tag(nameTextField.getText());
		try {
			tagService.create(tag);
			tagListView.getSelectionModel().select(tag);
		} catch (AlreadyExistsException | IllegalValueException e) {
			nameError.setText(e.getMessage());
		}
	}
	
	@FXML
	private void updateTag(ActionEvent actionEvent) {
//		Tag tag = selectedTag;
		try {
			tagService.update(selectedTag, nameTextField.getText());
		} catch (NotFoundException | AlreadyExistsException e) {
			nameError.setText(e.getMessage());
		}
		modifiedProperty.set(false);
	}
	
	@FXML
	private void removeTag(ActionEvent actionEvent) {
//		Tag tag = selectedTag;
		try {
			tagService.remove(selectedTag);
		} catch (NotFoundException e) {
			nameError.setText(e.getMessage());
		}
	}
}
