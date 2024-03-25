/**
 * Contains an object that contains an array of object references
 **/

public class Object4 {
  private Object1[] obj1Arr;

  public Object4 () {
	obj1Arr = new Object1[2];
    for (int i = 0; i < obj1Arr.length; i++) {
      obj1Arr[i] = new Object1();
      obj1Arr[i].setX(0);
      obj1Arr[i].setY(0);
    }
  }

  public void setObj1Arr0 (int a, float b) {
    obj1Arr[0].setX(a);
    obj1Arr[0].setY(b);
  }

  public void setObj1Arr1 (int a, float b) {
    obj1Arr[1].setX(a);
    obj1Arr[1].setY(b);
  }
}
