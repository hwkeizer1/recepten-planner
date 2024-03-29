package nl.recipes.domain;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Entity
public class ShoppingItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private String pluralName;

  @NotNull
  private Float amount;

  @OneToOne(fetch = FetchType.EAGER)
  private MeasureUnit measureUnit;

  private boolean isStandard;

  private Boolean onList;
  
  @Transient
  private boolean highlight = false;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private ShopType shopType;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private IngredientType ingredientType;

  @Transient
  private PropertyChangeSupport support;

  protected ShoppingItem() {
    if (support == null) {
      support = new PropertyChangeSupport(this);
    }
  }

  private ShoppingItem(ShoppingItemBuilder builder) {
    if (support == null) {
      support = new PropertyChangeSupport(this);
    }
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
    Float previous = this.amount;
    this.amount = amount;
    if (support == null) {
      support = new PropertyChangeSupport(this);
    }
    support.firePropertyChange("amount", previous, amount);
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

  public Boolean isOnList() {
    return onList;
  }

  public void setOnList(Boolean onList) {
    Boolean previous = this.onList;
    this.onList = onList;
    if (support == null) {
      support = new PropertyChangeSupport(this);
    }
    support.firePropertyChange("onList", previous, onList);
  }
  
  public boolean highlight() {
    return highlight;
  }
  
  public void setHighlight(boolean highlight) {
    this.highlight = highlight;
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

  public String getLabel() {
    if (amount == null)
      return getName();
    if (amount > 1.0)
      return getPluralName();
    return getName();
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    support.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    support.removePropertyChangeListener(listener);
  }

  public static class ShoppingItemBuilder {
    private Float amount;
    private String name;
    private String pluralName;
    private MeasureUnit measureUnit;
    private boolean isStandard;
    private Boolean onList;
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

    public ShoppingItemBuilder withOnList(Boolean onList) {
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
    return "ShoppingItem [id=" + id + ", name=" + name + ", amount=" + amount + ", measureUnit="
        + measureUnit + ", isStandard=" + isStandard + ", onList=" + onList + ", shopType="
        + shopType + ", ingredientType=" + ingredientType + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
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
    return Objects.equals(id, other.id);
  }

}
