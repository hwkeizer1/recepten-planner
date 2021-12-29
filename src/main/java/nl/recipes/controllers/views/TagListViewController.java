package nl.recipes.controllers.views;


import org.springframework.stereotype.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import net.rgielen.fxweaver.core.FxmlView;
import nl.recipes.domain.Tag;
import nl.recipes.services.TagService;

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
	
	@FXML VBox changeTagVBox;
	
	@FXML Button closeButton;
	
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
		tagListView.setItems(tagService.getReadonlyTagList());
	}
	
	@FXML
	public void closeTagListViewPanel(ActionEvent actionEvent) {
		rootController.closeLeftSidePanel();
	}
}
