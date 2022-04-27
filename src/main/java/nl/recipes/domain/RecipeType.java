package nl.recipes.domain;

/**
 * Entries in this enumeration must not be changed Entries can be reordered Entries can be added
 * Entries can contain max 20 characters
 */
public enum RecipeType {

  AMUSE("Amuse"), VOORGERECHT("Voorgerecht"), TUSSENGERECHT("Tussengerecht"), HOOFDGERECHT(
      "Hoofdgerecht"), BIJGERECHT("Bijgerecht"), NAGERECHT("Nagerecht");

  private String displayName;

  private RecipeType(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

}
