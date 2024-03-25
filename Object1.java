/**
 * Contains a simple object with only primitives for instance variables
 **/

public class Object1 {

  private int x;
  private float y;

  /**
   * Set variables to 0 as default
   */
  public Object1() {
    x = 0;
    y = 0;
  }

  /**
   * Setter methods for x and y
   **/
  public void setX (int newVal) { x = newVal; }

  public void setY (float newVal) { y = newVal; }

  /**
   * Getter methods for x and y
   **/
  public int getX () { return x; }

  public float getY () { return y; }

}
