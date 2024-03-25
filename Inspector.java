/**
 * Client side of system. Will listen and wait for JSON strings to be passed
 * from an ObjectCreator instance. Then, it will deserialize the string and print
 * information about the object to terminal.
 */

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.net.UnknownHostException;
import java.util.*;

public class Inspector {

  /**
   * Connect with ObjectCreator, obtain Json Strings and display once deserialized
   * @param args Unused
   */
  public static void main (String args[]) throws Exception {
    // Network connection configuration - change this as needed
    String hostname = "LAPTOP-3AMPCPHU";
    int port = 6868;

    // Connect to ObjectCreator.java
    try (Socket clientSocket = new Socket(hostname, port)) {
      System.out.println("Inspector: Listening on port " + Integer.toString(port));

      // Ready input reader
      InputStream input = clientSocket.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(input));

      while (true) {
        // Get Json from ObjectCreator and print what has been obtained to console
        String msg = reader.readLine();
        System.out.println("Inspector: Received the following from ObjectCreator: " + msg);

        // Deserialize the String to recreate the object sent over
        Object restored_obj = Deserializer.deserializeObject(msg);

        // Print information about the object
        displayObjectInformation(restored_obj);
      }
    }
    catch (UnknownHostException ex) {
        System.out.println("Server not found: " + ex.getMessage());
    }
    catch (IOException ex) {
        System.out.println("I/O error: " + ex.getMessage());
    }

  } // End of main

  /**
   * Given an Object, display information about its Class name and contents
   * @param obj Object to be examined
   */
  @SuppressWarnings("rawtypes")
  private static void displayObjectInformation (Object obj) throws IllegalArgumentException, IllegalAccessException {
	Map object_tracking_map = new IdentityHashMap();
	displayHelper(obj, obj.getClass(), 0, object_tracking_map);
  }

  /**
   * Helper function for recursively displaying information on Objects as they are discovered.
   * @param obj Base object to explote
   * @param c Class of obj
   * @param depth Depth level of traversal
   * @param map Map containing all objects previously encountered
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private static void displayHelper (Object obj, Class c, int depth, Map map) throws IllegalArgumentException, IllegalAccessException {
	// Check if the given object has been encountered before
	if (map.containsValue(obj)) {
		printDepth(depth);
		System.out.println("-> Object " + obj + " has been encountered before");
		return;
	}
	// Else: Object not encountered before - Add it to the map
	String object_id = Integer.toString(map.size());
	map.put(obj, object_id);

	// Display information about object
	if (!c.isPrimitive()) {
		printDepth(depth); System.out.println("---- OBJECT INFORMATION ----");
	}
	printDepth(depth); System.out.println("Class Name: " + c.getName());

	// Array: Display length and entries
	if (c.isArray()) {
		printDepth(depth); System.out.println("Type: Array");
	    printDepth(depth); System.out.println("Length: " + Integer.toString(Array.getLength(obj)));
	    printDepth(depth); System.out.println("Entries: ");
	    Class comp = c.getComponentType();
	    for (int i = 0; i < Array.getLength(obj); i++) {
	    	// Object Entry: Recursively explore
	    	if (!comp.isPrimitive()) {
	    		displayHelper(Array.get(obj, i), comp, depth + 1, map);
	    	}
	    	else {
	    		printDepth(depth); System.out.println("#" + Integer.toString(i) + ": " + Array.get(obj, i));
	    	}
	    }
	  }
	// Object: Display fields
	else {
		printDepth(depth); System.out.println("Type: Object");
		printDepth(depth); System.out.println("Fields:");
		Field[] f = c.getDeclaredFields();

		for (int i = 0 ; i < f.length; i++) {
			if (!Modifier.isPublic(f[i].getModifiers())) {
				f[i].setAccessible(true);
			}
			printDepth(depth + 1); System.out.println("--- Field Name: " + f[i].getName() + " ---");
			printDepth(depth + 1); System.out.println("Field Class/Type: " + f[i].getType().getName());
			// Check if the field is primitive or not
			if (f[i].getType().isArray()) {
				printDepth(depth + 1); System.out.println("Recursively exploring Array field");
				displayHelper(f[i].get(obj), f[i].get(obj).getClass(), depth + 2, map);
			}
			else if (!f[i].getType().isPrimitive()) {
				printDepth(depth + 1); System.out.println("Recursively exploring Object field");
				displayHelper(f[i].get(obj), f[i].getType(), depth + 2, map);
			}
			else {
				printDepth(depth + 1); System.out.println("Field Value: " + f[i].get(obj));
			}
		}
	}

  }

  /**
   * Helper function to print depth level to make traversal visually easier to understand
   * @param depth indicates how many spaces to print out
   */
  private static void printDepth (int depth) {
	  for (int i = 0; i < depth; i++) {
		  System.out.print("\s\s\s");
	  }
  }

}
