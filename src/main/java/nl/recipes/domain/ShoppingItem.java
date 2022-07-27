package nl.recipes.domain;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class ShoppingItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.EAGER)
  private IngredientName ingredientName;

  private Float amount;

  private boolean isStandard;

  private boolean onList;

  protected ShoppingItem() {}

  private ShoppingItem(ShoppingItemBuilder builder) {
    this.amount = builder.amount;
    this.ingredientName = builder.ingredientName;
    this.isStandard = builder.isStandard;
    this.onList = builder.onList;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Float getAmount() {
    return amount;
  }

  public void setAmount(Float amount) {
    this.amount = amount;
  }

  public IngredientName getIngredientName() {
    return ingredientName;
  }

  public void setIngredientName(IngredientName ingredientName) {
    this.ingredientName = ingredientName;
  }

  public boolean isStandard() {
    return isStandard;
  }

  public void setStandard(boolean isStandard) {
    this.isStandard = isStandard;
  }

  public boolean isOnList() {
    return onList;
  }

  public void setOnList(boolean onList) {
    this.onList = onList;
  }

  public static class ShoppingItemBuilder {
    private Float amount;
    private IngredientName ingredientName;
    private boolean isStandard;
    private boolean onList;

    public ShoppingItemBuilder withAmount(Float amount) {
      this.amount = amount;
      return this;
    }

    public ShoppingItemBuilder withIngredientName(IngredientName ingredientName) {
      this.ingredientName = ingredientName;
      return this;
    }

    public ShoppingItemBuilder withIsStandard(boolean isStandard) {
      this.isStandard = isStandard;
      return this;
    }

    public ShoppingItemBuilder withOnList(boolean onList) {
      this.onList = onList;
      return this;
    }

    public ShoppingItem build() {
      return new ShoppingItem(this);
    }

    public ShoppingItem build(Long id) {
      ShoppingItem shoppingItem = new ShoppingItem(this);
      shoppingItem.setId(id);
      return shoppingItem;
    }
  }

  @Override
  public String toString() {
    return "ShoppingItem [id=" + id + ", ingredientName=" + ingredientName + ", amount=" + amount
        + ", isStandard=" + isStandard + ", onList=" + onList + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(ingredientName);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ShoppingItem other = (ShoppingItem) obj;
    return Objects.equals(ingredientName, other.ingredientName);
  }
}
