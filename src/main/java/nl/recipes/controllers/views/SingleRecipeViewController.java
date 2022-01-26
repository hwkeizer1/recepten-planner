package nl.recipes.controllers.views;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import nl.recipes.domain.Ingredient;
import nl.recipes.domain.IngredientName;
import nl.recipes.domain.MeasureUnit;
import nl.recipes.domain.Recipe;
import nl.recipes.domain.RecipeType;
import nl.recipes.services.RecipeService;

@Slf4j
@Controller
@FxmlView("SingleRecipeView.fxml")
public class SingleRecipeViewController implements Initializable {

	private final RecipeService recipeService;
	
	@FXML AnchorPane singleRecipeViewPanel;
	
	@FXML Label nameLabel;

	@FXML Label tagStringLabel;
	@FXML Label preparationTimeLabel;
	@FXML Label cookTimeLabel;
	@FXML Label servingsLabel;
	@FXML Label timesServedLabel;
	@FXML Label lastTimeServedLabel;
	@FXML Label ratingLabel;
	
	@FXML ImageView imageView;
	
	@FXML TableView<Ingredient> ingredientTableView;
	@FXML TableColumn<Ingredient, Number> amountColumn;
	@FXML TableColumn<Ingredient, String> measureUnitColumn;
	@FXML TableColumn<Ingredient, String> ingredientNameColumn;
	@FXML TableColumn<Ingredient, Boolean> stockColumn;

	@FXML TextArea preparationsTextArea;
	@FXML TextArea directionsTextArea;
	
	public SingleRecipeViewController(RecipeService recipeService) {
		this.recipeService = recipeService;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeControls();
		initializeIngredientTableView();
	}
	
	
	public AnchorPane getSingleRecipeViewPanel(Recipe recipe) {
		nameLabel.setText(recipe.getName());
		
		tagStringLabel.setText(recipe.getTagString());
		preparationTimeLabel.setText(recipe.getPreparationTime().toString());
		cookTimeLabel.setText(recipe.getCookTime().toString());
		servingsLabel.setText(recipe.getServings().toString());
		timesServedLabel.setText(recipe.getTimesServed().toString());
		if (recipe.getLastServed() == null) {
			lastTimeServedLabel.setText("-");
		} else {
			lastTimeServedLabel.setText(recipe.getLastServed().toString());
		}
		ratingLabel.setText(recipe.getRating().toString());
		
		ingredientTableView.setItems(recipeService.getReadonlyIngredientList(recipe.getId()));
		ingredientTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		ingredientTableView.setFixedCellSize(25);
		ingredientTableView.prefHeightProperty().bind(Bindings.size(ingredientTableView.getItems()).multiply(ingredientTableView.getFixedCellSize()).add(4));
		
		preparationsTextArea.setText(recipe.getPreparations());
		directionsTextArea.setText(recipe.getDirections());
		
		return singleRecipeViewPanel;
	}
	
	private void initializeControls() {
		Font nameFont = Font.font("Courier New", FontWeight.BOLD, 35);
		Font controlFont = Font.font("Courier New", FontWeight.BOLD, 16);
		
		nameLabel.setFont(nameFont);
		
		tagStringLabel.setFont(controlFont);
		preparationTimeLabel.setFont(controlFont);
		cookTimeLabel.setFont(controlFont);
		servingsLabel.setFont(controlFont);
		timesServedLabel.setFont(controlFont);
		lastTimeServedLabel.setFont(controlFont);
		ratingLabel.setFont(controlFont);
		
		imageView.setImage(new Image("/images/spaghetti brocolli spekjes.png", 400, 400, true, false));
	}
	
	private void initializeIngredientTableView() {
		ingredientTableView.getStyleClass().add("ingredient_table");
	
		amountColumn.setCellValueFactory(
				c -> (c.getValue().getAmount() != null && (10 * c.getValue().getAmount() % 10) == 0)
						? new ReadOnlyObjectWrapper<>(Math.round(c.getValue().getAmount()))
						: new ReadOnlyObjectWrapper<>(c.getValue().getAmount()));

		measureUnitColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
				(c.getValue().getAmount() == null || c.getValue().getAmount() <= 1) 
				? c.getValue().getMeasureUnit().getName() : c.getValue().getMeasureUnit().getPluralName()));
				
		ingredientNameColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
				(c.getValue().getAmount() == null || c.getValue().getAmount() <= 1) 
				? c.getValue().getIngredientName().getName() : c.getValue().getIngredientName().getPluralName()));
		
		stockColumn.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().getIngredientName().isStock()));
		stockColumn.setCellFactory(c -> new CheckBoxTableCell<>());
		
		// Listener needed????
	}
}
