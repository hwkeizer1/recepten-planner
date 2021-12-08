package nl.recipes.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties("recipe")
public class Ingredient {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Recipe recipe;
	
	private Float amount;
	
	@OneToOne(fetch = FetchType.EAGER)
	private MeasureUnit measureUnit;
	

	@OneToOne(fetch = FetchType.EAGER)
	private IngredientName ingredientName;
	
	public Ingredient(Float amount, MeasureUnit measureUnit, IngredientName ingredientName) {
		this.amount = amount;
		this.measureUnit = measureUnit;
		this.ingredientName = ingredientName;
	}	
	
	public Ingredient(Recipe recipe, Float amount, MeasureUnit measureUnit, IngredientName ingredientName) {
		this.recipe = recipe;
		this.amount = amount;
		this.measureUnit = measureUnit;
		this.ingredientName = ingredientName;
	}
}
