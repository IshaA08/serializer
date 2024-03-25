/**
 * Contains an object that contains an array of primitives
 **/

public class Object3 {
   private int[] arr;

   public Object3() {
	 arr = new int[3];
     for (int i = 0; i < arr.length; i++) {
       arr[i] = 0;
     }
   }

   /**
    * Setter methods for each array entry
    */
   public void setArr0 (int a) { arr[0] = a; }

   public void setArr1 (int a) { arr[1] = a; }

   public void setArr2 (int a) { arr[2] = a; }

   /**
    * Getter method for the array
    */
   public int[] getArray () { return arr; }

}
