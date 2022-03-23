package toools.text.json;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtils {

	public static String beautify(String json) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Object obj = mapper.readValue(json, Object.class);
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
}
