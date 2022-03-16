package nl.recipes.views.recipes;


import org.springframework.stereotype.Component;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import nl.recipes.domain.Ingredient;
import nl.recipes.domain.Recipe;
import nl.recipes.services.RecipeService;
import nl.recipes.views.components.pane.bootstrap.BootstrapColumn;
import nl.recipes.views.components.pane.bootstrap.BootstrapPane;
import nl.recipes.views.components.pane.bootstrap.BootstrapRow;
import nl.recipes.views.components.pane.bootstrap.Breakpoint;
import nl.recipes.views.root.RootView;

import static nl.recipes.views.ViewConstants.*;

@Component
public class RecipeSingleView {
	
	private final RecipeService recipeService;
	
	private RootView rootView;
	
	private Recipe selectedRecipe;
	
	ScrollPane scrollPane;
	
	ImageView imageView = new ImageView();
	Label recipeName;
	
	Label tagString = new Label();
	Label recipeType = new Label();
	Label preparationTime = new Label();
	Label cookTime = new Label();
	Label servings = new Label();
	Label timesServed = new Label();
	Label lastTimeServed = new Label();
	Label rating = new Label();
	
	TableView<Ingredient> ingredientTableView = new TableView<>();
	TableColumn<Ingredient, Number> amountColumn = new TableColumn<>();
	TableColumn<Ingredient, String> measureUnitColumn = new TableColumn<>();
	TableColumn<Ingredient, String> ingredientNameColumn = new TableColumn<>();
	TableColumn<Ingredient, Boolean> stockColumn = new TableColumn<>();
	
	Label notes = new Label();
	Label preparations = new Label();
	Label directions = new Label();
	
	public RecipeSingleView(RecipeService recipeService) {
		this.recipeService = recipeService;
		
		BootstrapPane root = makeView();
		
		scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
	}
	
	public void setRootView(RootView rootView) {
		this.rootView = rootView;
	}
	
	public Node getRecipeSingleViewPanel(Recipe recipe) {
		selectedRecipe = recipe;
		recipeName.setText(recipe.getName());
		
		tagString.setText(recipe.getTagString());
		recipeType.setText(recipe.getRecipeType().getDisplayName());
		preparationTime.setText(recipe.getPreparationTime() == null ? "-" : recipe.getPreparationTime().toString());
		cookTime.setText(recipe.getCookTime() == null ? "-" :recipe.getCookTime().toString());
		servings.setText(recipe.getServings() == null ? "-" : recipe.getServings().toString());
		timesServed.setText(recipe.getTimesServed() == null ? "-" : recipe.getTimesServed().toString());
		lastTimeServed.setText(recipe.getLastServed() == null ? "-" : recipe.getLastServed().toString());
		rating.setText(recipe.getRating() == null ? "-" : recipe.getRating().toString());		
		// TODO: For now hard coded, image location will be part of recipe later on
		imageView = setRoundedImage("/images/spaghetti brocolli spekjes.png");
		
		ingredientTableView.setItems(recipeService.getReadonlyIngredientList(recipe.getId()));
		ingredientTableView.setFixedCellSize(25);
		ingredientTableView.prefHeightProperty().bind(Bindings.size(ingredientTableView.getItems()).multiply(ingredientTableView.getFixedCellSize()).add(4));
		ingredientTableView.minHeightProperty().bind(ingredientTableView.prefHeightProperty());
		ingredientTableView.maxHeightProperty().bind(ingredientTableView.prefHeightProperty());
		
		notes.setText(recipe.getNotes());
		preparations.setText(recipe.getPreparations());
		directions.setText(recipe.getDirections());
		
		return scrollPane;
	}
	
	private BootstrapPane makeView() {
		BootstrapPane bootstrapPane = new BootstrapPane();
        bootstrapPane.setPadding(new Insets(15));
        bootstrapPane.getStyleClass().add("background");
        bootstrapPane.setVgap(25);
        bootstrapPane.setHgap(25);
        
        BootstrapRow row = new BootstrapRow();
        row.addColumn(createTitleColumn());
        row.addColumn(createColumn(createFeaturesWidget("Kenmerken")));
        row.addColumn(createColumn(createIngredientTableWidget("Ingrediënten")));
        row.addColumn(createColumn(createTextWidget("Notities", notes)));
        row.addColumn(createLargeColumn(createTextWidget("Voorbereiding", preparations)));
        row.addColumn(createLargeColumn(createTextWidget("Bereiding", directions)));
        
        bootstrapPane.addRow(row);
        
        return bootstrapPane;
	}

	private BootstrapColumn createTitleColumn() {
		HBox header = new HBox();
		header.setSpacing(40);
		header.setPadding(new Insets(5, 0, 0, 5));
		
		recipeName = new Label();
		recipeName.setWrapText(true);
		recipeName.getStyleClass().addAll(RECIPE_TITLE, DROP_SHADOW);
		
		VBox buttonPanel = new VBox();
		buttonPanel.setSpacing(20);
		Button edit = new Button("Recept wijzigen");
		edit.setOnAction(this::showRecipeEditView);
		edit.setMinWidth(150);
		Button plan = new Button("Recept plannen");
		plan.setMinWidth(150);
		Button list = new Button("Terug naar lijst");
		list.setOnAction(this::showRecipeListView);
		list.setMinWidth(150);
		buttonPanel.getChildren().addAll(edit, plan, list);
		
		Region buffer =  new Region();
		HBox.setHgrow(buffer, Priority.ALWAYS);
			
		header.getChildren().add(imageView);
		header.getChildren().add(recipeName);
		header.getChildren().add(buffer);
		header.getChildren().add(buttonPanel);
		
		BootstrapColumn titleColumn = new BootstrapColumn(header);
		titleColumn.setBreakpointColumnWidth(Breakpoint.XSMALL, 12);
		
		return titleColumn;
	}
	
	private BootstrapColumn createColumn(Node widget) {
		BootstrapColumn column = new BootstrapColumn(widget);
		column.setBreakpointColumnWidth(Breakpoint.XSMALL, 12);
		column.setBreakpointColumnWidth(Breakpoint.SMALL, 9);
		column.setBreakpointColumnWidth(Breakpoint.LARGE, 6);
		column.setBreakpointColumnWidth(Breakpoint.XLARGE, 4);
		return column;
	}
	
	private BootstrapColumn createLargeColumn(Node widget) {
		BootstrapColumn column = new BootstrapColumn(widget);
		column.setBreakpointColumnWidth(Breakpoint.XSMALL, 12);
		column.setBreakpointColumnWidth(Breakpoint.SMALL, 10);
		column.setBreakpointColumnWidth(Breakpoint.LARGE, 8);
		column.setBreakpointColumnWidth(Breakpoint.XLARGE, 6);
		return column;
	}
	
	private Node createFeaturesWidget(String title) {
		VBox widget = new VBox();
		widget.setSpacing(10);
		widget.getStyleClass().addAll(WIDGET, DROP_SHADOW);
		
		Label titleLabel = new Label(title);
		titleLabel.getStyleClass().add(TITLE);
		widget.getChildren().add(titleLabel);
		
		widget.getChildren().add(new Separator(Orientation.HORIZONTAL));
		widget.getChildren().add(createFeatureItem("Categorieën:", tagString));
		widget.getChildren().add(createFeatureItem("Recept type:", recipeType));
		widget.getChildren().add(createFeatureItem("Voorbereidingstijd:", preparationTime));
		widget.getChildren().add(createFeatureItem("Bereidingstijd:", cookTime));
		widget.getChildren().add(createFeatureItem("Aantal personen:", servings));
		widget.getChildren().add(createFeatureItem("Aantal keren gegeten:", timesServed));
		widget.getChildren().add(createFeatureItem("Laatste keer gegeten:", lastTimeServed));
		widget.getChildren().add(createFeatureItem("Waardering:", rating));
		return widget;
	}
	
	private Node createIngredientTableWidget(String title) {
		VBox widget = new VBox();
		widget.setSpacing(10);
		widget.getStyleClass().addAll(WIDGET, DROP_SHADOW);
		
		Label titleLabel = new Label(title);
		titleLabel.getStyleClass().add(TITLE);
		widget.getChildren().add(titleLabel);
		
		widget.getChildren().add(new Separator(Orientation.HORIZONTAL));
		widget.getChildren().add(ingredientTableView);
		
		ingredientTableView.getColumns().add(amountColumn);
		ingredientTableView.getColumns().add(measureUnitColumn);
		ingredientTableView.getColumns().add(ingredientNameColumn);
		ingredientTableView.getColumns().add(stockColumn);
		
		amountColumn.prefWidthProperty().bind(ingredientTableView.widthProperty().multiply(0.1));
		measureUnitColumn.prefWidthProperty().bind(ingredientTableView.widthProperty().multiply(0.35));
		ingredientNameColumn.prefWidthProperty().bind(ingredientTableView.widthProperty().multiply(0.45));
		stockColumn.prefWidthProperty().bind(ingredientTableView.widthProperty().multiply(0.1));
		
		ingredientTableView.getStyleClass().add(INGREDIENT_TABLE);
		
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
		
		return widget;
	}
	
	private Node createTextWidget(String title, Label text) {
		VBox widget = new VBox();
		VBox.setVgrow(widget, Priority.ALWAYS);
		widget.setSpacing(10);
		widget.getStyleClass().addAll(WIDGET, DROP_SHADOW);
		
		Label titleLabel = new Label(title);
		titleLabel.getStyleClass().add(TITLE);
		widget.getChildren().add(titleLabel);
		
		widget.getChildren().add(new Separator(Orientation.HORIZONTAL));
		
		text.setWrapText(true);
		text.setMinHeight(Region.USE_PREF_SIZE);
		widget.getChildren().add(text);
		return widget;
	}
	
	private Node createFeatureItem(String featureLabel, Label text) {
		HBox feature = new HBox();
		HBox.setHgrow(feature, Priority.ALWAYS);

        HBox left = new HBox();
        left.setMinWidth(200);
        HBox.setHgrow(left, Priority.NEVER);
        left.getChildren().add(new Label(featureLabel));

        HBox right = new HBox();
        HBox.setHgrow(right, Priority.ALWAYS);
        text.setWrapText(true);
        right.getChildren().add(text);

        feature.getChildren().addAll(left, right);
        return feature;
	}
	
	private ImageView setRoundedImage(String imageLocation) {
		Image image = new Image(imageLocation, 300, 300, true, false);
		imageView.setImage(image);
		imageView.getStyleClass().add(DROP_SHADOW);
		
		Rectangle clip = new Rectangle();
		clip.setWidth(300.0);
		clip.setHeight(300.0);
		
		clip.setArcHeight(20);
		clip.setArcWidth(20);
		clip.setStroke(Color.BLACK);
		imageView.setClip(clip);
		
		SnapshotParameters parameters = new SnapshotParameters();
		parameters.setFill(Color.TRANSPARENT);
		WritableImage writeableImage = imageView.snapshot(parameters, null);
		
		imageView.setClip(null);
		imageView.setImage(writeableImage);
		return imageView;
	}
	
	private void showRecipeListView(ActionEvent event) {
		if (rootView != null) {
			rootView.handleRecipeListPanel(event);
		}
	}
	
	private void showRecipeEditView(ActionEvent event) {
		if (rootView != null) {
			rootView.showRecipeEditViewPanel(selectedRecipe);
		}
	}
}
