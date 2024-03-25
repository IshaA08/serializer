/**
 * Contains an object that uses an instance of one of Java's collection classes to refer to several other objects
 **/
import java.util.ArrayList;

public class Object5 {

  private ArrayList<String> items;

  public Object5 () { items = new ArrayList<String>(); }

  public void addItem (String a) { items.add(a); }

  public ArrayList<String> getItems () { return items; }

}
