package nl.recipes.domain;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@JsonIgnoreProperties("recipe")
public class Ingredient {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JsonIgnore
  private Recipe recipe;

  private Float amount;

  @OneToOne(fetch = FetchType.EAGER)
  private MeasureUnit measureUnit;

  @OneToOne(fetch = FetchType.EAGER)
  private IngredientName ingredientName;
  
  @Transient
  private boolean onList;
  
  protected Ingredient() {}
  
  private Ingredient(IngredientBuilder builder) {
    this.recipe = builder.recipe;
    this.amount = builder.amount;
    this.measureUnit = builder.measureUnit;
    this.ingredientName = builder.ingredientName;
    this.onList = builder.onList;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Recipe getRecipe() {
    return recipe;
  }

  public void setRecipe(Recipe recipe) {
    this.recipe = recipe;
  }

  public Float getAmount() {
    return amount;
  }

  public void setAmount(Float amount) {
    this.amount = amount;
  }

  public MeasureUnit getMeasureUnit() {
    return measureUnit;
  }

  public void setMeasureUnit(MeasureUnit measureUnit) {
    this.measureUnit = measureUnit;
  }

  public IngredientName getIngredientName() {
    return ingredientName;
  }

  public void setIngredientName(IngredientName ingredientName) {
    this.ingredientName = ingredientName;
  }

  public boolean isOnList() {
    return onList;
  }

  public void setOnList(boolean onList) {
    this.onList = onList;
  }
  
  public static class IngredientBuilder {
    private Recipe recipe;
    private Float amount;
    private MeasureUnit measureUnit;
    private IngredientName ingredientName;
    private boolean onList;
    
    public IngredientBuilder withRecipe(Recipe recipe) {
      this.recipe = recipe;
      return this;
    }
    
    public IngredientBuilder withAmount(Float amount) {
      this.amount = amount;
      return this;
    }
    
    public IngredientBuilder withMeasureUnit(MeasureUnit measureUnit) {
      this.measureUnit = measureUnit;
      return this;
    }
    
    public IngredientBuilder withIngredientName(IngredientName ingredientName) {
      this.ingredientName = ingredientName;
      return this;
    }
    
    public IngredientBuilder withOnList(boolean onList) {
      this.onList = onList;
      return this;
    }
    
    public Ingredient build() {
      Ingredient ingredient = new Ingredient(this);
      return ingredient;
    }
    
    public Ingredient build(Long id) {
      Ingredient ingredient = new Ingredient(this);
      ingredient.setId(id);
      return ingredient;
    }
  }

  @Override
  public String toString() {
    return "Ingredient [id=" + id + ", amount=" + amount + ", measureUnit=" + measureUnit
        + ", ingredientName=" + ingredientName + ", onList=" + onList + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount, id, ingredientName, measureUnit, onList);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Ingredient other = (Ingredient) obj;
    return Objects.equals(amount, other.amount) && Objects.equals(id, other.id)
        && Objects.equals(ingredientName, other.ingredientName)
        && Objects.equals(measureUnit, other.measureUnit) && onList == other.onList;
  }
}
