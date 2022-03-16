package nl.recipes.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Recipe {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique=true, length = 60)
	@NotBlank(message="Naam mag niet leeg zijn")
	@Size(max = 60, message="Naam mag niet langer zijn dan 60 karakters")
	private String name;
	
	private String servingTips;

	private String notes;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "recipe", orphanRemoval = true)
	private Set<Ingredient> ingredients = new HashSet<>();

	private String image;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 20, nullable = false)
	private RecipeType recipeType;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "recipe_tag", joinColumns = @JoinColumn(name = "recipe_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private Set<Tag> tags = new HashSet<>();

	private Integer preparationTime;
	private Integer cookTime;
	private Integer servings;
	private Integer timesServed;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate lastServed;


	private String preparations;
	
	private String directions;

	private Integer rating;
	
	public Recipe(String name) {
		this.name = name;
	}
	
	public void setIngredients(Set<Ingredient> ingredients) {
		this.ingredients.clear();
		for (Ingredient ingredient : ingredients) {
			addIngredient(ingredient);
		}
	}
	
	public void addIngredient(Ingredient ingredient) {
		ingredient.setRecipe(this);
		ingredients.add(ingredient);
	}
	
	public void removeIngredient(Ingredient ingredient) {
		ingredients.remove(ingredient);
	}
	
	public boolean hasIngredientWithName(Long id) {
		return ingredients.stream().anyMatch(i -> i.getIngredientName().getId().equals(id));
	}
	
	public String getTagString() {
		String tagString = "";
		for (Tag tag : tags) {
			if (tagString.isEmpty()) {
				tagString = tagString.concat(tag.getName() + ", ");
			} else {
				tagString = tagString.concat(tag.getName().toLowerCase() + ", ");
			}
		}
		if (tagString.length() != 0) {
			return tagString.substring(0, tagString.length() - 2);
		} else {
			return "";
		}
	}

	@Override
	public String toString() {
		return "Recipe [id=" + id + ", name=" + name + ", servingTips=" + servingTips + ", notes=" + notes
				+ ", recipeType=" + recipeType + ", tags=" + tags
				+ ", preparationTime=" + preparationTime + ", cookTime=" + cookTime + ", servings=" + servings
				+ ", timesServed=" + timesServed + ", lastServed=" + lastServed + ", preparations=" + preparations
				+ ", directions=" + directions + ", rating=" + rating + "]";
	}
}