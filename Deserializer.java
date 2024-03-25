/**
 * Deserialize a given JSON object string passed in as parameter,
 * returning the re-constructed object alongside any objects it refers to.
*/

import javax.json.*;
import java.io.StringReader;
import java.lang.reflect.*;
import java.util.*;

public class Deserializer {

	/**
	 * Given a String containing a Json object, deserialize and return its object
	 * @param source String containing Json to be deserialized
	 * @return Object instance of data found within source
	 * @throws Exception
	 */
   @SuppressWarnings("rawtypes")
   public static Object deserializeObject(String source) throws Exception {

     // Convert String to JsonObject
     JsonReader reader = Json.createReader(new StringReader(source));
     JsonObject obj = reader.readObject();
     reader.close();

     // Extract list of objects from "object" value
     JsonArray object_list = obj.getJsonArray("objects");

     // Create and populate HashMap of all object instances in JsonObject
     Map object_tracking_map = new HashMap();
     createInstances(object_tracking_map, object_list);

     // Assign values to the object according to JsonObject
     assignFieldValues(object_tracking_map, object_list);

     // HashMap ID 0 = Source object
     return object_tracking_map.get("0");

   }

   /**
    * Reflectively populate a Map instance with instances of every object in the given object_list
    * @param object_tracking_map Map instance to be populated. Both key and instance are derived from object_list
    * @param object_list JsonArray containing a list of objects to be instantiated
    * @throws Exception
    */
   @SuppressWarnings({ "rawtypes", "unchecked" })
   private static void createInstances(Map object_tracking_map, JsonArray object_list) throws Exception {

     // For Each JsonObject in object_list: Create an instance and store it in object_tracking_map
     for (int i = 0; i < object_list.size(); i++) {
       // Get the JsonObject, id and class
       JsonObject object_info = object_list.getJsonObject(i);
       String id = object_info.getString("id");
       Class object_class = Class.forName(object_info.getString("class"));

       // Create object instance
       Object object_instance = null;

       // Array Class: Use Array to instantiate
       if (object_class.isArray()) {
    	   int arrLength = Integer.parseInt(object_info.getString("length"));
    	   object_instance = Array.newInstance(object_class.getComponentType(), arrLength);
       }

       // Object Class: Use no-argument constructor to instantiate
       else {
    	   Constructor constructor = object_class.getDeclaredConstructor();
    	   if (!Modifier.isPublic(constructor.getModifiers())) {
    		   constructor.setAccessible(true);
    	   }
    	   object_instance = constructor.newInstance();
       }

       // Add the Array or Object instance into the map
       object_tracking_map.put(id, object_instance);
     }

   }

   /**
    * Reflectively assign field values for each object instance in object_tracking_map using information from object_list
    * @param object_tracking_map Map containing the object instances containing fields to be populated
    * @param object_list JsonArray containing field values of every object instance
    */
   @SuppressWarnings("rawtypes")
   private static void assignFieldValues (Map object_tracking_map, JsonArray object_list) throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException, NoSuchFieldException, SecurityException {

     // For Each Object in object_tracking_map: Set its fields using its corresponding JsonObject from object_list
     for (int i = 0; i < object_list.size(); i++) {

       // Get the JsonObject, Class and instance of the current map index/id entry
       JsonObject object_info = object_list.getJsonObject(i);
       Object object_instance = object_tracking_map.get(object_info.getString("id"));
       Class object_class = object_instance.getClass();

       // Array Object: Retrieve its entries and store each corresponding value from JsonObject
       if (object_class.isArray()) {
         JsonArray field_array = object_info.getJsonArray("entries");
         Class component_class = object_class.getComponentType();

         for (int j = 0; j < field_array.size(); j++) {
           JsonObject field_obj = field_array.getJsonObject(j);
           Array.set(object_instance, j, deserializeJsonValue(field_obj, object_tracking_map, component_class));
         }
       }

       // Object: Populate fields using "field" entry in JsonObject
       else {
         JsonArray object_fields = object_info.getJsonArray("fields");

         for (int j = 0; j < object_fields.size(); j++) {
           JsonObject object_field = (JsonObject) object_fields.get(j);
           String field_name = object_field.getString("name");
           String field_class_name = object_field.getString("declaringclass");

           Class field_class = Class.forName(field_class_name);
           Field field = field_class.getDeclaredField(field_name);

           if (!Modifier.isPublic(field.getModifiers())) {
             field.setAccessible(true);
           }
           // Set the field properly
           field.set(object_instance, deserializeJsonValue(object_field, object_tracking_map, field.getType()));
         }
       }

     }

   }


   /**
    * Given a JsonObject, return the stored value/reference Object if it contains one
    * Used when deserializing an array entry or Object field
    * @param f JsonObject containing a value or reference
    * @param object_tracking_map Map containing all object instances. Used to retrieve object from references
    * @param f_type Type of the value contained in f
    * @return Object representing the value or reference found in f
    */
   @SuppressWarnings("rawtypes")
   private static Object deserializeJsonValue (JsonObject f, Map object_tracking_map, Class f_type) {

	 // No Reference or Value Key Found: Return null
     if (!(f.containsKey("value")) && !(f.containsKey("reference"))) {
       return null;
     }
     // Reference Key: Retrieve the object from object_tracking_map using its id
     else if (f.containsKey("reference")) {
       return object_tracking_map.get(f.getString("reference"));
     }
     // Value Key: Parse and return the primitive value contained
     else {
       if (f_type.equals(boolean.class)) {
         return Boolean.parseBoolean(f.getString("value"));
       }
       else if (f_type.equals(byte.class)) {
         return Byte.parseByte(f.getString("value"));
       }
       else if (f_type.equals(char.class)) {
    	 return Character.valueOf(f.getString("value").charAt(0));
       }
       else if (f_type.equals(double.class)) {
         return Double.parseDouble(f.getString("value"));
       }
       else if (f_type.equals(float.class)) {
         return Float.parseFloat(f.getString("value"));
       }
       else if (f_type.equals(int.class)) {
         return Integer.parseInt(f.getString("value"));
       }
       else if (f_type.equals(long.class)) {
         return Long.parseLong(f.getString("value"));
       }
       else {
         return null;
       }
     }

  }

}
