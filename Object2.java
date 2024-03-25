/**
 * Contains an object that contains references to other objects
 **/

public class Object2 {

  private boolean b;
  private Object1 obj1;

  /**
   * Set defaults
   */
  public Object2() {
    b = false;
    obj1 = new Object1();
  }

  /**
   * Setter methods for b and obj1
   */
  public void setB (boolean newVal) { b = newVal; }

  public void setObj1X (int intVal) { obj1.setX(intVal); }

  public void setObj1Y (float floatVal) { obj1.setY(floatVal); }

  /**
   * Getter methods for b and obj1
   */
  public boolean getB () { return b; }

  public Object1 getObj1 () { return obj1; }

}
