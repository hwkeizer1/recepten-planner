package nl.recipes.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.springframework.format.annotation.DateTimeFormat;


@Entity
public class Planning implements Comparable<Planning> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate date;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "planning_recipe", joinColumns = @JoinColumn(name = "planning_id"),
      inverseJoinColumns = @JoinColumn(name = "recipe_id"))
  private List<Recipe> recipes = new ArrayList<>();

  private Integer servings;

  private boolean onShoppingList = true;

  protected Planning() {}

  private Planning(PlanningBuilder builder) {
    this.date = builder.date;
    this.recipes = builder.recipes;
    this.servings = builder.servings;
    this.onShoppingList = builder.onShoppingList;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public LocalDate getDate() {
    return date;
  }

  public List<Recipe> getRecipes() {
    return recipes;
  }

  public void setRecipes(List<Recipe> recipes) {
    this.recipes = recipes;
  }

  public Integer getServings() {
    return servings;
  }

  public void setServings(Integer servings) {
    this.servings = servings;
  }

  public boolean isOnShoppingList() {
    return onShoppingList;
  }

  public void setOnShoppingList(boolean onShoppingList) {
    this.onShoppingList = onShoppingList;
  }

  public void addRecipe(Recipe recipe) {
    if (recipe != null) {
      this.recipes.add(recipe);
    }
  }

  public List<Recipe> getRecipesOrderedByType() {
    List<Recipe> orderedRecipes = new ArrayList<>();
    for (RecipeType type : RecipeType.values()) {
      for (Recipe recipe : recipes) {
        if (recipe.getRecipeType().equals(type)) {
          orderedRecipes.add(recipe);
        }
      }
    }
    return orderedRecipes;
  }

  @Override
  public int compareTo(Planning other) {
    return this.getDate().compareTo(other.getDate());
  }

  public static class PlanningBuilder {
    private LocalDate date;
    private List<Recipe> recipes = new ArrayList<>();
    private Integer servings;
    private boolean onShoppingList = true;

    public PlanningBuilder withDate(LocalDate date) {
      this.date = date;
      return this;
    }

    public PlanningBuilder withRecipe(Recipe recipe) {
      if (recipe != null) {
        this.recipes.add(recipe);
      }
      return this;
    }

    public PlanningBuilder withServings(Integer servings) {
      this.servings = servings;
      return this;
    }

    public PlanningBuilder withOnShoppingList(boolean onShoppingList) {
      this.onShoppingList = onShoppingList;
      return this;
    }

    public Planning build() {
      Planning planning = new Planning(this);
      return planning;
    }

    public Planning build(Long id) {
      Planning planning = new Planning(this);
      planning.setId(id);
      return planning;
    }
  }

  /**
   * Each planning has a unique date therefore the equals/hash uses only the date field
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Planning other = (Planning) obj;
    if (date == null) {
      if (other.date != null)
        return false;
    } else if (!date.equals(other.date))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((date == null) ? 0 : date.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "Planning [id=" + id + ", date=" + date + ", servings=" + servings + ", onShoppingList="
        + onShoppingList + "]";
  }

}
