/**
 * Tests Deserializer.java functionality
 */

import static org.junit.Assert.*;
import org.junit.Test;

public class DeserializerTest {

	@SuppressWarnings("rawtypes")
	@Test
	public void testDeserializeObject () throws Exception {
		Object1 original = new Object1();
		original.setX(0);
		original.setY(1);
		
		String origSerial = Serializer.serializeObject(original);
		Object1 after = (Object1) Deserializer.deserializeObject(origSerial);
		
		// Compare class names and fields
		Class originalClass = original.getClass();
		Class afterClass = after.getClass();
		
		assertTrue(afterClass.equals(originalClass));
		assertTrue(after.getX() == original.getX());
		assertTrue(after.getY() == original.getY());
		
	}

}
