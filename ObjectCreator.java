/**
 * Server side of system. Creates objects under the direction of the user.
 * Allows the user to create one object at a time from a given selection
 * using a text-based menu. Then, it will print the JSON form of the
 * object and serialize it. Finally, it will send the serialized string
 * over the socket connection to the awaiting Inspector.java.
 */

import java.util.Scanner;
import java.io.*;
import java.net.*;
import java.net.InetAddress;

public class ObjectCreator {

  private static Object1 object1;
  private static Object2 object2;
  private static Object3 object3;
  private static Object4 object4;
  private static Object5 object5;
  private static Scanner keyboard;

  public static void main (String[] args) throws Exception {

    keyboard = new Scanner (System.in);

    // Socket connection setup
    int port = 6868;
    String hostname = null;

    try (ServerSocket serverSocket = new ServerSocket(port)) {

      InetAddress address;
      address = InetAddress.getLocalHost();
      hostname = address.getHostName();
      System.out.println("ObjectCreator: Starting connection set-up. Hostname is " + hostname);

      System.out.println("ObjectCreator: Server listening on port " + Integer.toString(port));
      Socket socket = serverSocket.accept();
      System.out.println("ObjectCreator: Successfully connected with Inspector");

      // Get input/output streams for connection ready
      OutputStream output = socket.getOutputStream();
      PrintWriter writer = new PrintWriter(output, true);

      // Menu Loop: Keep asking the user which objects to create until they quit
      int choice;
      boolean menuLoop = true;

      while (menuLoop) {

        // Display first menu: Create object or Exit program
        choice = menuDisplayCreateObjectOrExit();

        // Option 1 = Create object, Option 2 = Exit program
        if (choice == 2) {
          System.out.println("--- Exiting Program ---");
          menuLoop = false;
          continue;
        }

        // Display second menu: Which object to create from option 1-5
        choice = menuDisplayObjectChoices();

        // Create jsonString according to user choice
        String jsonString = null;

        if (choice == 1) {
          createObject1();
          jsonString = Serializer.serializeObject(object1);
        }
        else if (choice == 2) {
          createObject2();
          jsonString = Serializer.serializeObject(object2);
        }
        else if (choice == 3) {
          createObject3();
          jsonString = Serializer.serializeObject(object3);
        }
        else if (choice == 4) {
          createObject4();
          jsonString = Serializer.serializeObject(object4);
        }
        else if (choice == 5) {
          createObject5();
          jsonString = Serializer.serializeObject(object5);
        }
        else {
          System.out.println("--- Invalid Input Given - Try Again ---");
          continue;
        }

        // Display the JSON string
        System.out.println("ObjectCreator: Object #" + Integer.toString(choice) + "'s JSON String is: " + jsonString);

        // Send data over socket connection
        writer.println(jsonString);
        System.out.println("--- ObjectCreator: Have sent JSON String of object #" + Integer.toString(choice) + " to Inspector ---");

      } // End of menu loop

    }

    catch (IOException e) {
      System.out.println("ObjectCreator: Server exception: " + e.getMessage());
      e.printStackTrace();
    }

  }

  /**
   * Display Menu Prompt #1: The user will decide whether they want to create an Object or exit program
   * @return int representing the user's choice. 1 = Create object and 2 = Exit
   */
  private static int menuDisplayCreateObjectOrExit () {
    System.out.println("-----------------------------------------");
    System.out.println("        Object Serializer Server         ");
    System.out.println("-----------------------------------------");
    System.out.println("--- What would you like to do? ---");
    System.out.println("\t1) Create an object\n\t2) Exit");
    System.out.print("Your Choice: ");
    String choice = keyboard.nextLine();
    System.out.println();

    return Integer.parseInt(choice);
  }

  /**
   * Display Menu Prompt #2: Show user available Object choices alongside their descriptions
   * @return int representing user's choice. 1 = Object1, 2 = Object2, 3 = Object3, 4 = Object4 and 5 = Object5
   */
  private static int menuDisplayObjectChoices () {
    System.out.println("--- Which object would you like to create? ---");
    System.out.println("\t1) Object1 - A simple object with only primitives for instance variables");
    System.out.println("\t2) Object2 - An object that contains references to other objects");
    System.out.println("\t3) Object3 - An object that contains an array of primitives");
    System.out.println("\t4) Object4 - An object that contains an array of object references");
    System.out.println("\t5) Object5 - An object that uses an instance of one of Java's collection classes to refer to several other objects");

    System.out.print("Your Choice: ");
    String choice = keyboard.nextLine();
    System.out.println();

    return Integer.parseInt(choice);
  }

  /**
   * Facilitate menu for Object1 creation
   */
  private static void createObject1() {
    object1 = new Object1();

    System.out.println("--- Object1 Creation ---");
    System.out.println("Set Values for the Following Variables:");

    // Set value for integer variable x
    System.out.print("\tint x: ");
    String intVar = keyboard.nextLine();
    System.out.println();
    object1.setX(Integer.parseInt(intVar));

    // Set value for float variable y
    System.out.print("\tfloat y: ");
    String floatVar = keyboard.nextLine();
    System.out.println();
    object1.setY(Float.parseFloat(floatVar));

    System.out.println("--- Finished Object1 Creation ---");
  }

  /**
   * Facilitate menu for Object2 creation
   */
  private static void createObject2() {
    object2 = new Object2();

    System.out.println("--- Object2 Creation ---");
    System.out.println("Object2 has an instance of Object1 and you will set variable values for it as well.");
    System.out.println("Set Values for the Following Variables:");

    // Set value for boolean variable b
    System.out.print("\tboolean b: ");
    String bool = keyboard.nextLine();
    System.out.println();
    object2.setB(Boolean.parseBoolean(bool));

    // Set value for obj1 int variable
    System.out.print("\tint x in obj1: ");
    String intVal = keyboard.nextLine();
    System.out.println();
    object2.setObj1X(Integer.parseInt(intVal));

    // Set value for obj1 float variable
    System.out.print("\tfloat y in obj1: ");
    String floatVal = keyboard.nextLine();
    System.out.println();
    object2.setObj1Y(Float.parseFloat(floatVal));

    System.out.println("--- Finished Object2 Creation ---");
  }

  /**
   * Facilitate menu for Object3 creation
   */
  private static void createObject3() {
    object3 = new Object3();
    String arr = null;

    System.out.println("--- Object3 Creation ---");
    System.out.println("Set Values for the Following Variables:");

    // Set int array value 0
    System.out.print("\tint for arr[0]: ");
    arr = keyboard.nextLine();
    System.out.println();
    object3.setArr0(Integer.parseInt(arr));

    // Set int array value 1
    System.out.print("\tint for arr[1]: ");
    arr = keyboard.nextLine();
    System.out.println();
    object3.setArr1(Integer.parseInt(arr));

    // Set int array value 2
    System.out.print("\tint for arr[2]: ");
    arr = keyboard.nextLine();
    System.out.println();
    object3.setArr2(Integer.parseInt(arr));

    System.out.println("--- Finished Object3 Creation ---");
  }

  /**
   * Facilitate menu for Object4 creation
   */
  private static void createObject4() {
    object4 = new Object4();

    System.out.println("--- Object4 Creation ---");
    System.out.println("Object4 contains an array of Object1 instances. You will set values for each\nentry's x and y values.");
    System.out.println("Set Values for the Following Variables:");

    String intVal = null;
    String floatVal = null;

    // Get int and float values for obj1Arr element 0
    System.out.print("\tint for Object1 x in obj1Arr[0]: ");
    intVal = keyboard.nextLine();
    System.out.println();

    System.out.print("\tfloat for Object1 y in obj1Arr[0]: ");
    floatVal = keyboard.nextLine();
    System.out.println();

    object4.setObj1Arr0(Integer.parseInt(intVal), Float.parseFloat(floatVal));

    // Get int and float values for obj1Arr element 1
    System.out.print("\tint for Object1 x in obj1Arr[1]: ");
    intVal = keyboard.nextLine();
    System.out.println();

    System.out.print("\tfloat for Object1 y in obj1Arr[1]: ");
    floatVal = keyboard.nextLine();
    System.out.println();

    object4.setObj1Arr1(Integer.parseInt(intVal), Float.parseFloat(floatVal));

    System.out.println("--- Finished Object4 Creation ---");
  }

  /**
   * Facilitate menu for Object5 creation
   */
  private static void createObject5() {
    object5 = new Object5();

    System.out.println("--- Object5 Creation ---");
    System.out.println("Object5 contains an ArrayList<String>. You will add two Strings to it.");
    System.out.println("Set Values for the Following Variables:");
    String item = null;

    // Set String variable 1
    System.out.print("\tString for item entry 0: ");
    item = keyboard.nextLine();
    System.out.println();
    object5.addItem(item);

    // Set String variable 2
    System.out.print("\tString for item entry 1: ");
    item = keyboard.nextLine();
    System.out.println();
    object5.addItem(item);

    System.out.println("--- Finished Object5 Creation ---");
  }

}
