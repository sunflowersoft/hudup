package net.hudup.core.parser;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.Map;

import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.io.output.WriterOutputStream;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;


/**
 * This utility class is used to process (read, write, etc.) JSON string, JSON archive (file).
 * JSON (JavaScript Object Notation) is a human-read format used for interchange between many protocols, available at <a href="http://www.json.org/">http://www.json.org</a>.
 * In this framework, JSON is often used to represent a Java object as a text, which means that JSON text is converted into Java object and vice versa.
 * This {@link JsonUtil} class uses the GSON library developed by Google for processing JSON format. GSON is available at <a href="https://github.com/google/gson">https://github.com/google/gson</a>.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class JsonUtil {
	
	
	/**
	 * Converting Java object into JSON string.
	 * @param object Specified Java object.
	 * @return JSON string.
	 */
	public static String toJson(Object object) {
		try {
			return JsonWriter.objectToJson(object);
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/**
	 * Writing Java object to storage according JSON format by {@link Writer}.
	 * @param object Java object.
	 * @param writer {@link Writer} for writing.
	 * @return whether writing successfully
	 */
	public static boolean toJson(Object object, Writer writer) {
		boolean result = true;
		JsonWriter jsonWriter = null;
		try {
			jsonWriter = new JsonWriter(new WriterOutputStream(writer));
			jsonWriter.write(object);
			jsonWriter.flush();
		}
		catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		finally {
			
		}
		
		return result;
	}

	
	/**
	 * Converting specified JSON string into Java object.
	 * @param json Specified JSON string.
	 * @return Converted Java object.
	 */
	public static Object parseJson(String json) {
		try {
			return JsonReader.jsonToJava(json);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/**
	 * Parsing JSON-format content into Java object by {@link Reader}.
	 * @param reader {@link Reader} of JSON-format content.
	 * @return Parsed Java object.
	 */
	public static Object parseJson(Reader reader) {
		Object parsedObject = null;
		JsonReader jsonReader = null;
		try {
			jsonReader = new JsonReader(new ReaderInputStream(reader));
			parsedObject = jsonReader.readObject();
		}
		catch (Exception e) {
			e.printStackTrace();
			parsedObject = null;
		}
		finally {
			
		}
		
		return parsedObject;
	}

	
	/**
	 * Converting specified Java object into JSON string.
	 * @param object Specified Java object.
	 * @return JSON string
	 */
	public static String toPlainJson(Object object) {
		Gson gson = createBuilder().create();
		return gson.toJson(object);
	}
	
	
	/**
	 * Converting specified Java object into JSON content via {@link Writer}.
	 * @param object Specified Java object
	 * @param writer {@link Writer} to write JSON content.
	 * @return whether writing successfully
	 */
	public static boolean toPlainJson(Object object, Writer writer) {
		Gson gson = createBuilder().create();
		gson.toJson(object, writer);
		return true;
	}

	
	/**
	 * Converting JSON string into Java object according to specified class type.
	 * @param json Specified JSON string.
	 * @param type Specified class type.
	 * @return Java object according specified class type.
	 */
	public static Object parsePlainJson(String json, Type type) {
		try {
			Gson gson = createBuilder().create();
			return gson.fromJson(json, type);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * Converting JSON content into Java object by {@link Reader} according to specified class type.
	 * @param reader {@link Reader} to read JSON content.
	 * @param type Specified class type.
	 * @return Parsed Java object according class type.
	 */
	public static Object parsePlainJson(Reader reader, Type type) {
		try {
			Gson gson = createBuilder().create();
			return gson.fromJson(reader, type);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * Creating Google JSON builder used for all operations (converting JSON content to Java object and vice versa).
	 * Such builder is represented by {@link GsonBuilder} class.
	 * @return Google JSON builder represented by {@link GsonBuilder} class, used for JSON parsing.
	 */
	private static GsonBuilder createBuilder() {
		return new GsonBuilder().registerTypeAdapter(
				Map.class,
				new JsonDeserializer<Map<?, ?>>() {

					@Override
					public Map<?, ?> deserialize(JsonElement json, Type typeOfT,
							JsonDeserializationContext context)
							throws JsonParseException {
						// TODO Auto-generated method stub
						
						Type type = new TypeToken<Map<?, ?>>() { }.getType();
						Gson gson = new Gson();
						return gson.fromJson(json, type);
					}
					
				});
	}
	
	
	
}
