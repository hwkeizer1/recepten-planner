package nl.recipes.services;

import org.springframework.stereotype.Service;
import nl.recipes.domain.Recipe;

@Service
public class ImageService {

  public void addImageToRecipe(Recipe recipe, String image)  {
    // if recipe has image {
    //    delete current image from image folder (with warning)
    // }
    // copy selected image to image folder
    // add imagename to recipe.image field
  }
  
  public void removeImageFromRecipe(Recipe recipe) {
    // delete current image from image folder (with warning)
  }
}
