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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.EAGER)
  private IngredientName ingredientName;

  private Float amount;

  @OneToOne(fetch = FetchType.EAGER)
  private MeasureUnit measureUnit;

  boolean isStandard;

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  ShopType shopType = ShopType.OVERIG;

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  IngredientType ingredientType = IngredientType.OVERIG;

  boolean onList;

}
