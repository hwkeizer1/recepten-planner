package nl.recipes.views.components.utils;

/**
 * Based on https://edencoding.com/responsive-layouts/
 */
public class Utils {

  /**
   * Return an integer between the stated max and min values
   * 
   * @param value the value to be clamped
   * @param max the maximum allowed integer value to be returned
   * @param min the minimum allowed integer value to be returned
   * @return the clamped value
   */
  public static int clamp(int value, int min, int max) {

    if (max < min)
      throw new IllegalArgumentException("Cannot clamp when max is greater than min");

    if (value > max) {
      return max;
    } else if (value < min) {
      return min;
    } else {
      return value;
    }
  }
  
  public static String format(float d)
  {
      if(d == (long) d)
          return String.format("%d",(long)d);
      else
          return String.format("%s",d);
  }

}
