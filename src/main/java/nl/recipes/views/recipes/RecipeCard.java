package nl.recipes.views.recipes;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import org.girod.javafx.svgimage.SVGImage;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import nl.recipes.domain.Recipe;
import nl.recipes.views.components.utils.ToolBarFactory;

public class RecipeCard extends VBox{

  DateTimeFormatter df = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
  
  private CheckBox planningCheckBox;
 
  private final Recipe recipe;
  private boolean isPlanned;
  private VBox imageViewBox;
  
  public RecipeCard(Recipe recipe, boolean isPlanned) {
    super();
    this.recipe = recipe;
    this.isPlanned = isPlanned;
    VBox.setVgrow(this, Priority.ALWAYS);
    this.getStyleClass().add("recipe-card");
    initializeComponents();
  }
  
  
  public void setOnPlanningCheckBoxAction(EventHandler<ActionEvent> event) {
    planningCheckBox.setOnAction(event);
  }
  
  public void setOnRecipeCardClicked(EventHandler<? super MouseEvent> value) {
    this.setOnMouseClicked(value);
  }
  
  public void setImage(ImageView imageView) {
    this.imageViewBox.getChildren().add(imageView);
  }
  
  public void setCardVisible(boolean isVisible) {
    setVisible(isVisible);
  }
  
  public boolean isCardVisible() {
    return isVisible();
  }
  
  public boolean hasRecipeName(String name) {
    return name.equals(recipe.getName());
  }
  
  private void initializeComponents() {
    planningCheckBox = new CheckBox();
    planningCheckBox.setSelected(isPlanned);
    
    HBox header = new HBox();
    header.setPadding(new Insets(5));
    header.setSpacing(5);
    VBox headerLine = new VBox();
    VBox.setVgrow(headerLine, Priority.ALWAYS);
    
    Label recipeName = new Label(recipe.getName());
    recipeName.getStyleClass().add("title");
    recipeName.setWrapText(true);

    headerLine.getChildren().addAll(recipeName);

    header.getChildren().addAll(planningCheckBox, headerLine);

    HBox cardContent = new HBox();
    HBox.setHgrow(cardContent, Priority.ALWAYS);
    cardContent.getStyleClass().add("card-content");
    cardContent.setMinHeight(165);


    VBox lines = new VBox();
    VBox.setVgrow(lines, Priority.ALWAYS);
    lines.getStyleClass().add("lines");
    lines.setPadding(new Insets(5));
    lines.setSpacing(5);

    Label recipeTypeLabel = new Label(recipe.getRecipeType().getDisplayName());
    recipeTypeLabel.getStyleClass().add("type");
    lines.getChildren().add(recipeTypeLabel);
    
    Label categoryLabel = new Label(recipe.getTagString());
    categoryLabel.getStyleClass().add("categories");
    categoryLabel.setWrapText(true);
    categoryLabel.setAlignment(Pos.TOP_LEFT);
    lines.getChildren().add(categoryLabel);

    String lastServed = recipe.getLastServed() != null ? "Laatst gegeten op " + df.format(recipe.getLastServed()) : "";
    Label lastServedLabel = new Label(lastServed);
    lastServedLabel.setWrapText(true);
    if (!lastServed.isEmpty()) {
      lines.getChildren().add(lastServedLabel);
    }
    
    Integer totalTime = recipe.getPreparationTime() != null ? recipe.getPreparationTime() : 0;
    totalTime = totalTime + (recipe.getCookTime() != null ? recipe.getCookTime() : 0);
    String cooktime = "Klaar in " + totalTime + " minuten";
    Label cooktimeLabel = new Label(cooktime);
    cooktimeLabel.setWrapText(true);
    if (!totalTime.equals(0)) {
      lines.getChildren().add(cooktimeLabel);
    }
    
    String timesServed = recipe.getTimesServed() != null ? recipe.getTimesServed().toString() + " keer gegeten" : "";
    Label timesServedLabel = new Label(timesServed);
    timesServedLabel.setWrapText(true);
    if (!timesServed.isEmpty()) {
      lines.getChildren().add(timesServedLabel);
    }
    
    Region vBuffer = new Region();
    VBox.setVgrow(vBuffer, Priority.ALWAYS);
    lines.getChildren().add(vBuffer);
    
    if (recipe.getRating() != null && recipe.getRating() != 0) {
      HBox rating = new HBox();   
      for (int i = 0; i < recipe.getRating(); i++) {
        SVGImage starImage = ToolBarFactory.createImage("/icons/rating.svg", 18d);
        rating.getChildren().add(starImage);
      }
      lines.getChildren().add(rating);
    }
    
    imageViewBox = new VBox();
    VBox.setVgrow(imageViewBox, Priority.ALWAYS);
    imageViewBox.setPadding(new Insets(5));
    

    cardContent.getChildren().addAll(imageViewBox, lines);

    this.getChildren().addAll(header, new Separator(Orientation.HORIZONTAL), cardContent);
  }
}
