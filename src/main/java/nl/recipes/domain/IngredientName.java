package nl.recipes.domain;

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
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientName {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.EAGER)
  private MeasureUnit measureUnit;
  
  @NotNull
  @Column(unique = true, length = 50)
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

  public IngredientName(String name, String pluralName) {
    this(name, pluralName, false, ShopType.OVERIG, IngredientType.OVERIG);
  }

  public IngredientName(String name, String pluralName, boolean stock) {
    this(name, pluralName, stock, ShopType.OVERIG, IngredientType.OVERIG);
  }

  public IngredientName(String name, String pluralName, boolean stock, ShopType shopType,
      IngredientType ingredientType) {
    this.name = name;
    this.pluralName = pluralName;
    this.stock = stock;
    this.shopType = shopType;
    this.ingredientType = ingredientType;
  }

  @Transient
  @JsonIgnore
  public String getListLabel() {
    return name + (measureUnit == null ? "" : " (" + measureUnit.getName() + ")");
  }
}
