package nl.recipes.domain;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Recipe {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, length = 60)
  @NotBlank(message = "Naam mag niet leeg zijn")
  @Size(max = 60, message = "Naam mag niet langer zijn dan 60 karakters")
  private String name;

  private String servingTips;

  private String notes;

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "recipe",
      orphanRemoval = true)
  private Set<Ingredient> ingredients = new HashSet<>();

  private String image;

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private RecipeType recipeType;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "recipe_tag", joinColumns = @JoinColumn(name = "recipe_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  private Set<Tag> tags = new HashSet<>();

  private Integer preparationTime;

  private Integer cookTime;

  private Integer servings;

  private Integer timesServed;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate lastServed;

  private String preparations;

  private String directions;

  private Integer rating;

  protected Recipe() {}
  
  private Recipe(RecipeBuilder builder) {
    this.name = builder.name;
    this.servingTips = builder.servingTips;
    this.notes = builder.notes;
    for (Ingredient ingredient : builder.ingredients) {
      addIngredient(ingredient);
    }
    this.image = builder.image;
    this.recipeType = builder.recipeType;
    this.tags = builder.tags;
    this.preparationTime = builder.preparationTime;
    this.cookTime = builder.cookTime;
    this.servings = builder.servings;
    this.timesServed = builder.timesServed;
    this.lastServed = builder.lastServed;
    this.preparations = builder.preparations;
    this.directions = builder.directions;
    this.rating = builder.rating;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getServingTips() {
    return servingTips;
  }

  public void setServingTips(String servingTips) {
    this.servingTips = servingTips;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public Set<Ingredient> getIngredients() {
    return ingredients;
  }
  
  public void setIngredients(Set<Ingredient> ingredients) {
    this.ingredients.clear();
    for (Ingredient ingredient : ingredients) {
      addIngredient(ingredient);
    }
  }
  
  public void addIngredient(Ingredient ingredient) {
    ingredient.setRecipe(this);
    ingredients.add(ingredient);
  }

  public void removeIngredient(Ingredient ingredient) {
    ingredients.remove(ingredient);
  }


  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public RecipeType getRecipeType() {
    return recipeType;
  }

  public void setRecipeType(RecipeType recipeType) {
    this.recipeType = recipeType;
  }

  public Set<Tag> getTags() {
    return tags;
  }

  public void setTags(Set<Tag> tags) {
    this.tags = tags;
  }

  public Integer getPreparationTime() {
    return preparationTime;
  }

  public void setPreparationTime(Integer preparationTime) {
    this.preparationTime = preparationTime;
  }

  public Integer getCookTime() {
    return cookTime;
  }

  public void setCookTime(Integer cookTime) {
    this.cookTime = cookTime;
  }

  public Integer getServings() {
    return servings;
  }

  public void setServings(Integer servings) {
    this.servings = servings;
  }

  public Integer getTimesServed() {
    return timesServed;
  }

  public void setTimesServed(Integer timesServed) {
    this.timesServed = timesServed;
  }

  public LocalDate getLastServed() {
    return lastServed;
  }

  public void setLastServed(LocalDate lastServed) {
    this.lastServed = lastServed;
  }

  public String getPreparations() {
    return preparations;
  }

  public void setPreparations(String preparations) {
    this.preparations = preparations;
  }

  public String getDirections() {
    return directions;
  }

  public void setDirections(String directions) {
    this.directions = directions;
  }

  public Integer getRating() {
    return rating;
  }

  public void setRating(Integer rating) {
    this.rating = rating;
  }

  public boolean hasIngredientWithName(Long id) {
    return ingredients.stream().anyMatch(i -> i.getIngredientName().getId().equals(id));
  }

  public String getTagString() {
    String tagString = "";
    for (Tag tag : tags) {
      if (tagString.isEmpty()) {
        tagString = tagString.concat(tag.getName() + ", ");
      } else {
        tagString = tagString.concat(tag.getName().toLowerCase() + ", ");
      }
    }
    if (tagString.length() != 0) {
      return tagString.substring(0, tagString.length() - 2);
    } else {
      return "";
    }
  }
  
  public static class RecipeBuilder {
    private String name;
    private String servingTips;
    private String notes;
    private Set<Ingredient> ingredients = new HashSet<>();
    private String image;
    private RecipeType recipeType;
    private Set<Tag> tags = new HashSet<>();
    private Integer preparationTime;
    private Integer cookTime;
    private Integer servings;
    private Integer timesServed;
    private LocalDate lastServed;
    private String preparations;
    private String directions;
    private Integer rating;
    
    public RecipeBuilder withName(String name) {
      this.name = name;
      return this;
    }
    
    public RecipeBuilder withServingTips(String servingTips) {
      this.servingTips = servingTips;
      return this;
    }
    
    public RecipeBuilder withNotes(String notes) {
      this.notes = notes;
      return this;
    }
    
    public RecipeBuilder withIngredients(Set<Ingredient> ingredients) {
      this.ingredients = ingredients;
      return this;
    }
    
    public RecipeBuilder withImage(String image) {
      this.image = image;
      return this;
    }
    
    public RecipeBuilder withRecipeType(RecipeType recipeType) {
      this.recipeType = recipeType;
      return this;
    }
    
    public RecipeBuilder withTags(Set<Tag>  tags) {
      this.tags = tags;
      return this;
    }
    
    public RecipeBuilder withPreparationTime(Integer preparationTime) {
      this.preparationTime = preparationTime;
      return this;
    }
    
    public RecipeBuilder withCookTime(Integer cookTime) {
      this.cookTime = cookTime;
      return this;
    }
    
    public RecipeBuilder withServings(Integer servings) {
      this.servings = servings;
      return this;
    }
    
    public RecipeBuilder withTimesServed(Integer timesServed) {
      this.timesServed = timesServed;
      return this;
    }
    
    public RecipeBuilder withLastServed(LocalDate lastServed) {
      this.lastServed = lastServed;
      return this;
    }
    
    public RecipeBuilder withPreparations(String preparations) {
      this.preparations = preparations;
      return this;
    }
    
    public RecipeBuilder withDirections(String directions) {
      this.directions = directions;
      return this;
    }
    
    public RecipeBuilder withRating(Integer rating) {
      this.rating = rating;
      return this;
    }
    
    public Recipe build() {
      return new Recipe(this);
    }
    
    public Recipe build(Long id) {
      Recipe recipe = new Recipe(this);
      recipe.setId(id);
      return recipe;
    }
  }

  @Override
  public String toString() {
    return "Recipe [id=" + id + ", name=" + name + ", servingTips=" + servingTips + ", notes="
        + notes + ", ingredients=" + ingredients + ", image=" + image + ", recipeType=" + recipeType
        + ", tags=" + tags + ", preparationTime=" + preparationTime + ", cookTime=" + cookTime
        + ", servings=" + servings + ", timesServed=" + timesServed + ", lastServed=" + lastServed
        + ", preparations=" + preparations + ", directions=" + directions + ", rating=" + rating
        + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Recipe other = (Recipe) obj;
    return Objects.equals(name, other.name);
  }
}
