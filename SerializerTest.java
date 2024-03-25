import static org.junit.Assert.*;
import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.junit.Test;

public class SerializerTest {

	@Test
	public void testSerializeObject() throws Exception {
		Object1 obj1Original = new Object1();
		obj1Original.setX(10);
		obj1Original.setY(9);
		
		String obj1Json = Serializer.serializeObject(obj1Original);
		JsonReader reader = Json.createReader(new StringReader(obj1Json));
	    JsonObject obj = reader.readObject();
	    reader.close();
		
	    JsonArray object_list = obj.getJsonArray("objects");
	    JsonObject obj1New = object_list.getJsonObject(0);
	    String obj1NewClass = obj1New.getString("class");

	    assertTrue(obj1Original.getClass().equals(Class.forName(obj1NewClass)));
	}

}
