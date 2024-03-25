/**
 * Will serialize the complete state of a given object passed in and
 * produce a JSON object String that will be sent over a network connection.
 * The base of the JSON container is a list of objects.
 * The base object being sent will be first, and then any objects it
 * references will be stored after it within this list.
 **/

import java.util.*;
import java.lang.reflect.*;
import javax.json.*;

public class Serializer {

  /**
   * Given an Object, serialize it into Json format
   * @param source Object to be serialized
   * @return String representation of source
   */
  @SuppressWarnings("rawtypes")
  public static String serializeObject (Object source) throws Exception {
    // Store information of all objects found in object_list
    JsonArrayBuilder object_list = Json.createArrayBuilder();
    serializeHelper(source, object_list, new IdentityHashMap());

    // Put together total JsonObject to be sent
    JsonObjectBuilder json_base_object = Json.createObjectBuilder();
    json_base_object.add("objects", object_list);
    JsonObject jsonObject = json_base_object.build();

    return jsonObject.toString();
  }

  /**
   * Starting from the source object, recursively create and store JsonObjects for each Object found
   * @param source Object to be serialized
   * @param object_list Array of all encountered Objects
   * @param object_tracking_map Map used to track Object IDs and whether an Object has been encountered before
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static void serializeHelper(Object source, JsonArrayBuilder object_list, Map object_tracking_map) throws Exception {
    // Generate object ID
    String object_id = Integer.toString(object_tracking_map.size());
    object_tracking_map.put(source, object_id);

    // Build JsonObject for given source Object
    JsonObjectBuilder object_info = Json.createObjectBuilder();

    // Add source class name
    Class object_class = source.getClass();
    object_info.add("class", object_class.getName());

    // Add source ID
    object_info.add("id", object_id);

    // Array: Store the type as array, length and its entries
    if (object_class.isArray()) {
      // Type = Array
      object_info.add("type", "array");
      // Store length
      object_info.add("length", Integer.toString(Array.getLength(source)));
      // Store each entry in an array
      JsonArrayBuilder entries_array = Json.createArrayBuilder();
      Class entries_component = object_class.getComponentType();
      for (int i = 0; i < Array.getLength(source); i++) {
        // Create a JsonObject for this entry
        JsonObjectBuilder entry_json = Json.createObjectBuilder();
        serializeObjectValue(Array.get(source, i), entries_component, entry_json, object_tracking_map, object_list);
        entries_array.add(entry_json);
      }
      // Add the total found entries to the overall Json object for the array
      object_info.add("entries", entries_array);
    }

    // Object: Store the type as object and its fields
    else {
      // Type = Object
      object_info.add("type", "object");

      // Add source fields
      Field[] source_fields = getAllInstanceVariables(object_class);
      JsonArrayBuilder fields_array = Json.createArrayBuilder();

      // For Each Field: Create JsonObjectCreator and add it to fields_array
      for (int i = 0; i < source_fields.length; i++) {
        // Make sure the field is accessible
        if (!Modifier.isPublic(source_fields[i].getModifiers())) {
          source_fields[i].setAccessible(true);
        }

        // Get this field's class
        JsonObjectBuilder current_field = Json.createObjectBuilder();
        Object field_value = source_fields[i].get(source);
        Class field_class = source_fields[i].getType();

        current_field.add("name", source_fields[i].getName());
        current_field.add("declaringclass", source_fields[i].getDeclaringClass().getName());

        serializeObjectValue(field_value, field_class, current_field, object_tracking_map, object_list);
        fields_array.add(current_field);
      }
      // Add the list of the source's fields to the object_info for the source
      object_info.add("fields", fields_array);

    }
    // Add the source's Json object to the overall list of objects
    object_list.add(object_info);
  }

  /**
   * Given an Object that represents a field of another Object, store its value into its given JsonObject
   * if the given Object is not already in the comprehensive object_tracking_map, recursively call serializeHelper
   * before adding its ID to the map.
   * @param obj Object node representing a field whose value will be serialized
   * @param obj_class The Class of obj
   * @param obj_json The JsonObject of the field contained in obj
   * @param object_tracking_map Total list of objects found in the program so far
   * @param object_list Total list of object serialized/in the process of serialization thus far
   */
  @SuppressWarnings("rawtypes")
  private static void serializeObjectValue (Object obj, Class obj_class, JsonObjectBuilder obj_json, Map object_tracking_map, JsonArrayBuilder object_list) throws Exception {
    // Primitive Value: Store its value
    if (obj_class.isPrimitive()) {
      obj_json.add("value", obj.toString());
    }
    // Null Value: Store null reference
    else if (obj == null) {
      obj_json.add("reference", "null");
    }
    // Object Value: Store its reference value after making sure it is in the object_tracking_map
    else {
      if (!object_tracking_map.containsValue(obj)) {
        serializeHelper(obj, object_list, object_tracking_map);
      }
      obj_json.add("reference", object_tracking_map.get(obj).toString());
    }
  }

  /**
   * The following code is adapted from the Java Reflection in Action textbook by Ira R. Forman and Nate Forman
   * on page 38 listing 2.2 as of November 26, 2020 from: http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.116.5796&rep=rep1&type=pdf
   * @param c Class whose fields will be explored
   * @return Field[] containing all instance variables of c
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private static Field[] getAllInstanceVariables (Class c) {
    List allFields = new LinkedList();
    // Traverse inheritance hierarchy completely, collecting instance variables along the way
    while (c != null) {
      Field[] f = c.getDeclaredFields();
      for (int i = 0; i < f.length; i++) {
        int mods = f[i].getModifiers();
        // Ignore static fields
        if (!Modifier.isStatic(mods)) {
          allFields.add(f[i]);
        }
      }
      // Move up inheritance hierarchy
      c = c.getSuperclass();
    }
    // Cast LinkedList to Field[]
    Field[] actualFields = new Field[allFields.size()];
    return (Field[]) allFields.toArray(actualFields);
  }

}
