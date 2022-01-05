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
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import nl.recipes.domain.Tag;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.services.TagService;

@Slf4j
@Controller
@FxmlView("TagListView.fxml")
public class TagListViewController {
	
	private final TagService tagService;
	
	private RootController rootController;
	
	public TagListViewController(TagService tagService) {
		this.tagService = tagService;
	}

	@FXML AnchorPane tagListViewPanel;
	
	@FXML ListView<Tag> tagListView;
	
	@FXML TextField tagNameTextField;
	@FXML Label tagNameError;
	
	@FXML VBox changeTagVBox;
	
	@FXML Button createButton;
	@FXML Button updateButton;
	@FXML Button removeButton;
	@FXML Button closeButton;
	
	private Tag selectedTag;
	private final BooleanProperty modifiedProperty = new SimpleBooleanProperty(false);
	private ChangeListener<Tag> tagChangeListener;
	
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
				tagNameTextField.setText(selectedTag.getName());
			} else {
				tagNameTextField.setText(null);
			}
		};

		tagListView.getSelectionModel().selectedItemProperty().addListener(tagChangeListener);
	}
	
	private void initializeButtons() {
		log.debug("{}", tagListView.getSelectionModel().selectedItemProperty());
		removeButton.disableProperty().bind(tagListView.getSelectionModel().selectedItemProperty().isNull());
		updateButton.disableProperty()
		.bind(tagListView.getSelectionModel().selectedItemProperty().isNull().or(modifiedProperty.not()).or(
				tagNameTextField.textProperty().isEmpty()));
		createButton.disableProperty().bind(tagListView.getSelectionModel().selectedItemProperty().isNotNull());
	}

	@FXML
	public void closeTagListViewPanel(ActionEvent actionEvent) {
		tagListView.getSelectionModel().selectedItemProperty().removeListener(tagChangeListener);
		rootController.closeLeftSidePanel();
	}
	
	@FXML
	private void handleKeyReleasedAction(KeyEvent keyEvent) {
		tagNameError.setText(null);
		modifiedProperty.set(true);
	}
	
	@FXML
	private void createTag(ActionEvent actionEvent) {
		Tag tag = new Tag(tagNameTextField.getText());
		log.debug("Create tag: {}", tag);
		try {
			tagService.create(tag);
			tagListView.getSelectionModel().select(tag);
		} catch (AlreadyExistsException | IllegalValueException e) {
			tagNameError.setText(e.getMessage());
			log.debug(e.getMessage());
		}
	}
	
	@FXML
	private void updateTag(ActionEvent actionEvent) {
		Tag tag = selectedTag;
		log.debug("Tag to update: {}", tag);
		try {
			tagService.update(tag, tagNameTextField.getText());
		} catch (NotFoundException | AlreadyExistsException e) {
			tagNameError.setText(e.getMessage());
			log.debug(e.getMessage());
		}
		modifiedProperty.set(false);
	}
	
	@FXML
	private void removeTag(ActionEvent actionEvent) {
		Tag tag = selectedTag;
		log.debug("Remove tag: {}", tag);
		try {
			tagService.remove(tag);
		} catch (NotFoundException e) {
			tagNameError.setText(e.getMessage());
			log.debug(e.getMessage());
		}
	}
}
