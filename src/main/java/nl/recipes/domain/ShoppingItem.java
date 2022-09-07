package nl.recipes.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import nl.recipes.domain.IngredientName.IngredientNameBuilder;

@Entity
public class ShoppingItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  
  private String pluralName;

  private Float amount;
  
  @OneToOne(fetch = FetchType.EAGER)
  private MeasureUnit measureUnit;

  private boolean isStandard;

  private boolean onList;
  
  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private ShopType shopType;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private IngredientType ingredientType;

  protected ShoppingItem() {}

  private ShoppingItem(ShoppingItemBuilder builder) {
    this.amount = builder.amount;
    this.name = builder.name;
    this.pluralName = builder.pluralName;
    this.measureUnit = builder.measureUnit;
    this.isStandard = builder.isStandard;
    this.onList = builder.onList;
    this.shopType = builder.shopType;
    this.ingredientType = builder.ingredientType;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
  public String getPluralName() {
    return pluralName;
  }

  public void setPluralName(String pluralName) {
    this.pluralName = pluralName;
  }

  public MeasureUnit getMeasureUnit() {
    return measureUnit;
  }

  public void setMeasureUnit(MeasureUnit measureUnit) {
    this.measureUnit = measureUnit;
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
  
  public ShopType getShopType() {
    return shopType;
  }

  public void setShopType(ShopType shopType) {
    this.shopType = shopType;
  }

  public IngredientType getIngredientType() {
    return ingredientType;
  }

  public void setIngredientType(IngredientType ingredientType) {
    this.ingredientType = ingredientType;
  }
  

  public static class ShoppingItemBuilder {
    private Float amount;
    private String name;
    private String pluralName;
    private MeasureUnit measureUnit;
    private boolean isStandard;
    private boolean onList;
    private ShopType shopType;
    private IngredientType ingredientType;

    public ShoppingItemBuilder withAmount(Float amount) {
      this.amount = amount;
      return this;
    }

    public ShoppingItemBuilder withName(String name) {
      this.name = name;
      return this;
    }
    
    public ShoppingItemBuilder withPluralName(String pluralName) {
      this.pluralName = pluralName;
      return this;
    }
    
    public ShoppingItemBuilder withMeasureUnit(MeasureUnit measureUnit) {
      this.measureUnit = measureUnit;
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
    
    public ShoppingItemBuilder withShopType(ShopType shopType) {
      this.shopType = shopType;
      return this;
    }
    
    public ShoppingItemBuilder withIngredientType(IngredientType ingredientType) {
      this.ingredientType = ingredientType;
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
    return "ShoppingItem [id=" + id + ", name=" + name + ", amount=" + amount + ", measureUnit=" + measureUnit
        + ", isStandard=" + isStandard + ", onList=" + onList + ", shopType=" + shopType + ", ingredientType="
        + ingredientType + "]";
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
    ShoppingItem other = (ShoppingItem) obj;
    return Objects.equals(name, other.name);
  }


}
