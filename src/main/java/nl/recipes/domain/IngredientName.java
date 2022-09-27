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
import javax.persistence.Transient;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class IngredientName {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.EAGER)
  private MeasureUnit measureUnit;
  
  @Column(length = 50)
  private String name;

  @Column(length = 50)
  private String pluralName;

  private boolean stock = false;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private ShopType shopType;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private IngredientType ingredientType;
  
  protected IngredientName() {}
  
  private IngredientName(IngredientNameBuilder builder) {
    this.measureUnit = builder.measureUnit;
    this.name = builder.name;
    this.pluralName = builder.pluralName;
    this.stock = builder.stock;
    this.shopType = builder.shopType;
    this.ingredientType = builder.ingredientType;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public MeasureUnit getMeasureUnit() {
    return measureUnit;
  }

  public void setMeasureUnit(MeasureUnit measureUnit) {
    this.measureUnit = measureUnit;
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

  public boolean isStock() {
    return stock;
  }

  public void setStock(boolean stock) {
    this.stock = stock;
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

  @Transient
  @JsonIgnore
  public String getListLabel() {
    return name + (measureUnit == null ? "" : " (" + measureUnit.getName() + ")");
  }
  
  
  public static class IngredientNameBuilder {
    private MeasureUnit measureUnit;
    private String name;
    private String pluralName;
    private boolean stock;
    private ShopType shopType;
    private IngredientType ingredientType;
    
    public IngredientNameBuilder withMeasureUnit(MeasureUnit measureUnit) {
      this.measureUnit = measureUnit;
      return this;
    }
    
    public IngredientNameBuilder withName(String name) {
      this.name = name;
      return this;
    }
    
    public IngredientNameBuilder withPluralName(String pluralName) {
      this.pluralName = pluralName;
      return this;
    }
    
    public IngredientNameBuilder withStock(boolean stock) {
      this.stock = stock;
      return this;
    }
    
    public IngredientNameBuilder withShopType(ShopType shopType) {
      this.shopType = shopType;
      return this;
    }
    
    public IngredientNameBuilder withIngredientType(IngredientType ingredientType) {
      this.ingredientType = ingredientType;
      return this;
    }
    
    public IngredientName build() {
      IngredientName ingredientName = new IngredientName(this);
      return ingredientName;
    }
    
    public IngredientName build(Long id) {
      IngredientName ingredientName = new IngredientName(this);
      ingredientName.setId(id);
      return ingredientName;
    }
  }

  @Override
  public String toString() {
    return "IngredientName [id=" + id + ", measureUnit=" + measureUnit + ", name=" + name
        + ", pluralName=" + pluralName + ", stock=" + stock + ", shopType=" + shopType
        + ", ingredientType=" + ingredientType + "]";
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
    IngredientName other = (IngredientName) obj;
    return Objects.equals(id, other.id);
  }
}
