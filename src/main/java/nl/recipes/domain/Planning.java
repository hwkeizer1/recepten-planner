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

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The planning class contains one or more recipes (or '- no recipe planned -') for a single day. 
 * There can only be one planning for each day on the planBoard so date is a unique field.
 * equals, hashCode and compareTo methods will all only use the date field.
 */
@Entity
@Data
@NoArgsConstructor
public class Planning implements Comparable<Planning> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "planning_recipe", joinColumns = @JoinColumn(name = "planning_id"), inverseJoinColumns = @JoinColumn(name = "recipe_id"))
	private List<Recipe> recipes = new ArrayList<>();
	
	private Integer servings;	
	private boolean onShoppingList = true;
	
	public Planning(LocalDate date, Recipe recipe, boolean onShoppingList) {
		this.date = date;
		this.recipes.add(recipe);
		this.servings = recipe.getServings();
		this.onShoppingList = onShoppingList;
	}
	
	public Planning(LocalDate date) {
		this.date = date;
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
		return "Planning [id=" + id + ", date=" + date + ", servings=" + servings + ", onShoppingList=" + onShoppingList
				+ "]";
	}

	
}
