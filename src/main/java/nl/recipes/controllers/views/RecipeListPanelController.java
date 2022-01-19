package nl.recipes.controllers.views;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import net.rgielen.fxweaver.core.FxmlView;
import nl.recipes.domain.Recipe;
import nl.recipes.domain.RecipeType;
import nl.recipes.services.RecipeService;

@Controller
@FxmlView("RecipeListPanel.fxml")
public class RecipeListPanelController {

	private final RecipeService recipeService;
	
	@FXML AnchorPane recipeListPanel;
	
	@FXML TableView<Recipe> recipeListTableView;
	@FXML TableColumn<Recipe, String> nameColumn;
	@FXML TableColumn<Recipe, RecipeType> typeColumn;
	@FXML TableColumn<Recipe, String> tagColumn;
	@FXML TableColumn<Recipe, LocalDate> lastServedColumn;
	@FXML TableColumn<Recipe, Integer> timesServedColumn;

	@FXML VBox recipeListVBox;
	
	public RecipeListPanelController(RecipeService recipeService) {
		this.recipeService = recipeService;
	}

	public AnchorPane getRecipeListPanel() {
		return recipeListPanel;
	}
	
	public void initialize() {
		AnchorPane.setTopAnchor(recipeListVBox, 0.0);
		AnchorPane.setBottomAnchor(recipeListVBox, 0.0);
		recipeListVBox.setBackground(new Background(new BackgroundFill(Color.web("#ffffcc"), CornerRadii.EMPTY , Insets.EMPTY)));
		
		initializeRecipeListTableView();
	}
	
	private void initializeRecipeListTableView() {
		recipeListTableView.setItems(recipeService.getReadonlyRecipeList());
		
		nameColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getName()));
		typeColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getRecipeType()));
		tagColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTagString()));
		lastServedColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getLastServed()));
		timesServedColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTimesServed()));
		
//		 Remove the previous listener before adding one
//		recipeListTableView.getSelectionModel().selectedItemProperty().removeListener(recipeChangeListener);
//		recipeListTableView.getSelectionModel().selectedItemProperty().addListener(recipeChangeListener);
	}
}
