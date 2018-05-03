/**
 * 
 */
package net.hudup.core.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;




/**
 * This final utility class provides constants and static methods for parsing, converting, encrypting, decrypting, splitting a text.
 * This class is used more by classes that implement {@code TextParsable} interface.
 * In the future, this utility class is replaced by support of JSON format.
 * JSON (JavaScript Object Notation) is a human-read format used for interchange between many protocols, available at <a href="http://www.json.org/">http://www.json.org</a>.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public final class TextParserUtil {

	
	/**
	 * Default Java regular expression for splitting a sentence into many words (tokens), including space.
	 * Note that regular expression is a string pattern for editing and finding texts (words, characters) in string of texts.
	 * Please see <a href="https://docs.oracle.com/javase/tutorial/essential/regex">https://docs.oracle.com/javase/tutorial/essential/regex</a> for more details about regular expression.
	 */
	public final static String DEFAULT_SEP         = "[[\\s][::][\\|][,][;]]";
	
	/**
	 * Default Java regular expression for splitting a sentence into many words (tokens), including space.
	 * Note that regular expression is a string pattern for editing and finding texts (words, characters) in string of texts.
	 * Please see <a href="https://docs.oracle.com/javase/tutorial/essential/regex">https://docs.oracle.com/javase/tutorial/essential/regex</a> for more details about regular expression.
	 */
	public final static String NOSPACE_DEFAULT_SEP = "[[::][\\|][,][;]]";
	
	
	/**
	 * There are many special characters called reserved characters for other purposes, for example, &quot;\\n&quot;, &quot;,&quot;, &quot;=&quot;, &quot;{&quot;, &quot;}&quot;.
	 * There is a mapping a reserved character to a special string. Such special string uses this constant to encode itself.
	 * For example, the character &quot;\\n&quot; is mapped to the string &quot;xreturnx&quot;.
	 * Some static methods of this class use such mapping for their own tasks such as {@link #encryptReservedChars(String)} method and {@link #decryptReservedChars(String)} method.
	 */
	public final static String SPEC_CHAR           = "x";
	
	
	/**
	 * There are many special characters called reserved characters for other purposes, for example, &quot;\\n&quot;, &quot;,&quot;, &quot;=&quot;, &quot;{&quot;, &quot;}&quot;.
	 * There is a mapping a reserved character to a special string.
	 * This constant &quot;xnull&quot; is the special string of {@code null} character.
	 */
	public final static String NULL_STRING         = SPEC_CHAR + "null" + SPEC_CHAR;
	
	/**
	 * There are many special characters called reserved characters for other purposes, for example, &quot;\\n&quot;, &quot;,&quot;, &quot;=&quot;, &quot;{&quot;, &quot;}&quot;.
	 * There is a mapping a reserved character to a special string.
	 * This constant &quot;xnline&quot; is the special string of {@code new line} character.
	 */
	public final static String NEW_LINE            = SPEC_CHAR + "nline" + SPEC_CHAR;
	
	
	/**
	 * Main separating character to split a string into many words (tokens). This character is a reserved character, &quot;:&quot;.
	 * For example, the string &quot;apple:orange&quot; is split into two words &quot;apple&quot; and &quot;orange&quot;. 
	 */
	public final static String MAIN_SEP            = ":";
	
	/**
	 * The special string of reserved character {@link #MAIN_SEP}. In current implementation, it is &quot;xcolonx&quot;.
	 */
	public final static String MAIN_SEP_REPLACE    = SPEC_CHAR + "colon" + SPEC_CHAR;
	
	/**
	 * The character is used to link many words in a string. This character is a reserved character, &quot;~&quot;.
	 * For example, we have a linked string &quot;abc~def~ghj&quot;.
	 */
	public final static String LINK_SEP            = "~";
	
	/**
	 * The special string of reserved character {@link #LINK_SEP}. In current implementation, it is &quot;xtildex&quot;.
	 */
	public final static String LINK_SEP_REPLACE    = SPEC_CHAR + "tilde" + SPEC_CHAR;
	
	/**
	 * The character is used to connect many words of a string or connect many parts of a word.
	 * For example, we have a connected word &quot;a_b_c&quot;.
	 */
	public final static String CONNECT_SEP         = "_";
	
	/**
	 * The special string of reserved character {@link #CONNECT_SEP}. In current implementation, it is &quot;xconnx&quot;.
	 */
	public final static String CONNECT_SEP_REPLACE = SPEC_CHAR + "conn" + SPEC_CHAR;
	
	/**
	 * The character is used to specify an extra part of a word or of a string.
	 * For example, we have string having a extra part &quot;user_id#1&quot;.
	 */
	public final static String EXTRA_SEP           = "#";
	
	/**
	 * The special string of reserved character {@link #EXTRA_SEP}. In current implementation, it is &quot;xsharpx&quot;.
	 */
	public final static String EXTRA_SEP_REPLACE   = SPEC_CHAR + "sharp" + SPEC_CHAR;

	
	/**
	 * There are many special characters called reserved characters for other purposes, for example, &quot;\\n&quot;, &quot;,&quot;, &quot;=&quot;, &quot;{&quot;, &quot;}&quot;.
	 * This constant makes a lookup table between every reserved character and a special string.
	 * Concretely, this constant is an array of many entries. Each entry has two elements in which the first element is a reserved character and the second element is a respective special string.
	 * Methods {@link #encryptReservedChars(String)} and {@link #decryptReservedChars(String)} uses this lookup table for encryption and decryption in which a reserved character is replaced by a special string (encryption) and a reserved character is recovered from a special string (decryption).
	 * In current implementation, following is the lookup table represented by this constant:
	 * <ul>
	 * <li>&quot;:&quot; &lt;-&gt; &quot;xcolonx&quot;</li>
	 * <li>&quot;~&quot; &lt;-&gt; &quot;xtildex&quot;</li>
	 * <li>&quot;_&quot; &lt;-&gt; &quot;xconnx&quot;</li>
	 * <li>&quot;#&quot; &lt;-&gt; &quot;xsharpx&quot;</li>
	 * <li>&quot;\\n&quot; &lt;-&gt; &quot;xreturnx&quot;</li>
	 * <li>&quot;,&quot; &lt;-&gt; &quot;xcommax&quot;</li>
	 * <li>&quot;=&quot; &lt;-&gt; &quot;xequalx&quot;</li>
	 * <li>&quot;{&quot; &lt;-&gt; &quot;xopen_bracketx&quot;</li>
	 * <li>&quot;}&quot; &lt;-&gt; &quot;xclose_bracketx&quot;</li>
	 * </ul>
	 */
	public final static String RESERVED_CHARS[][] = {
		{MAIN_SEP, MAIN_SEP_REPLACE}, 
		{LINK_SEP, LINK_SEP_REPLACE}, 
		{CONNECT_SEP, CONNECT_SEP_REPLACE}, 
		{EXTRA_SEP, EXTRA_SEP_REPLACE}, 
		{"\n", SPEC_CHAR + "return" + SPEC_CHAR}, 
		{",", SPEC_CHAR + "comma" + SPEC_CHAR}, 
		{"=", SPEC_CHAR + "equal" + SPEC_CHAR}, 
		{"{", SPEC_CHAR + "open_bracket" + SPEC_CHAR}, 
		{"}", SPEC_CHAR + "close_bracket" + SPEC_CHAR}};
	

	/**
	 * There are many special characters called reserved characters for other purposes, for example, &quot;\\n&quot;, &quot;,&quot;, &quot;=&quot;, &quot;{&quot;, &quot;}&quot;.
	 * This method replaces every reserved characters by a special string, for example, &quot;\\n&quot; is replaced by &quot;xreturnx&quot;.
	 * Such replacement is called encryption. This method looks the constant {@link #RESERVED_CHARS} for encryption.
	 * @param text Specified text may contain reserved characters.
	 * @return The encrypted text derived from the special text in which every reserved character is replaced by a special string. 
	 */
	public static String encryptReservedChars(String text) {
		int n = RESERVED_CHARS.length;
		for (int i = 0; i < n; i++) {
			text = text.replace(
					RESERVED_CHARS[i][0], 
					RESERVED_CHARS[i][1]);
		}
		
		return text;
	}


	/**
	 * This method applies replacements (encryptions) of reserved characters into every text element of the specified collection.
	 * In other words, this method calls {@link #encryptReservedChars(String)} many times.
	 * @param textList The specified collection of texts.
	 * @return {@link List} of encrypted texts. In each encrypted text, every reserved character is replaced by a special string.
	 */
	public static List<String> encryptReservedChars(Collection<String> textList) {
		List<String> result = Util.newList();
		
		for (String text : textList) {
			text = encryptReservedChars(text);
			result.add(text);
		}
		
		return result;
	}


	/**
	 * This method is opposite to the {@link #encryptReservedChars(String)} method.
	 * There are many special characters called reserved characters for other purposes, for example, &quot;\\n&quot;, &quot;,&quot;, &quot;=&quot;, &quot;{&quot;, &quot;}&quot;.
	 * When the specified text was encrypted by replacing every reserved character a special string by calling {@link #encryptReservedChars(String)} before,
	 * This method will recover all reserved characters from special strings. For example, the special string &quot;xreturnx&quot; is recovered as the reserved character &quot;\\n&quot;.
	 * This method looks the constant {@link #RESERVED_CHARS} for decryption. 
	 * @param text the specified text was encrypted by replacing every reserved character a special string by calling {@link #encryptReservedChars(String)} before.
	 * @return plain text is decrypted by recovering all reserved characters from special strings.
	 */
	public static String decryptReservedChars(String text) {
		int n = RESERVED_CHARS.length;
		for (int i = 0; i < n; i++) {
			text = text.replace(
					RESERVED_CHARS[i][1], 
					RESERVED_CHARS[i][0]);
		}
		
		return text;
	}


	/**
	 * This method applies recoveries (decryptions) of reserved characters from special strings into every encrypted text element of the specified collection.
	 * In other words, this method calls {@link #decryptReservedChars(String)} many times.
	 * @param textList Specified collection of encrypted texts.
	 * @return {@link List} of plain texts. Each plain text is decrypted by recovering all reserved characters from special strings.
	 */
	public static List<String> decryptReservedChars(Collection<String> textList) {
		List<String> result = Util.newList();
		
		for (String text : textList) {
			text = decryptReservedChars(text);
			result.add(text);
		}
		
		return result;
	}
	
	
	/**
	 * Splitting a specified string into many words (tokens). The character (string) that is used for separation is specified by the parameter {@code sep} which follows Java regular expression.
	 * For each returned word (token), the string that specified by the parameter {@code remove} is removed from such word.
	 * @param source Specified string.
	 * @param sep The character (string) that is used for separation.
	 * @param remove the string which is removed from each tokens.
	 * @return {@link List} of words (tokens) from splitting the specified string.
	 */
	public final static List<String> split(String source, String sep, String remove) {
		String[] array = source.split(sep);
		
		List<String> result = Util.newList();
		for (String str : array) {
			if (str == null) continue;
			
			if (remove != null && remove.length() > 0)
				str = str.replaceAll(remove, "");
			str = str.trim();
			if (str.length() > 0)
				result.add(str);
		}
		
		return result;
	}

	
	/**
	 * Converting a specified bit set represented by {@link BitSet} class into text form like &quot;01101001111&quot;. 
	 * Note that bit set is an array of binary elements. Essentially, any integer is specified by a bit set but the built-in {@link BitSet} class expresses an unlimited-length bit set.
	 * @param bs Specified bit set.
	 * @return Text form of the specified bit set.
	 */
	public static String toText(BitSet bs) {
		
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < bs.length(); i++) {
			if (bs.get(i))
				buffer.append('1');
			else
				buffer.append('0');
		}
		
		return buffer.toString();
	}

	
	/**
	 * Converting a specified text form like &quot;01101001111&quot; into a bit set represented by {@link BitSet} class.
	 * Note that bit set is an array of binary elements. Essentially, any integer is specified by a bit set but the built-in {@link BitSet} class expresses an unlimited-length bit set.
	 * This method is opposite to the {@link #toText(BitSet)} method.
	 * @param spec Specified text form of a bit set.
	 * @return Bit set of the specified text form.
	 */
	public static BitSet parseBitSet(String spec) {
		BitSet bs = new BitSet(spec.length());
		
		for (int i = 0; i < spec.length(); i++) {
			char c = spec.charAt(i);
			if (c == '1')
				bs.set(i);
			else
				bs.clear(i);
		}
		
		return bs;
	}

	
	/**
	 * Converting a specified array of objects (any type) into a string in which each object is converted as a word in such string.
	 * Words in such returned string are connected by the character specified by the parameter {@code sep}. 
	 * This is template static method and so the type of object is specified by the template &lt;{@code T}&gt;.
	 * @param <T> type of each object in the specified array.
	 * @param array Specified array of objects.
	 * @param sep The character that is used to connect words in the returned string. As usual, it is a comma &quot;,&quot;.
	 * @return Text form (string) of the specified array of objects, in which each object is converted as a word in such text form.
	 */
	public static <T extends Object> String toText(T[] array, String sep) {
		StringBuffer buffer = new StringBuffer();
		
		for (int i = 0; i < array.length; i++) {
			if ( i > 0)
				buffer.append(sep + " ");

			T value = array[i];
			if (value instanceof TextParsable)
				buffer.append(((TextParsable)value).toText());
			else
				buffer.append(value);
		}
		
		return buffer.toString();
		
	}

	
	/**
	 * Converting a specified array of float numbers into a string.
	 * Words in such returned string are connected by the character specified by the parameter {@code sep}. 
	 * @param array Specified array of objects.
	 * @param sep The character that is used to connect words in the returned string. As usual, it is a comma &quot;,&quot;.
	 * @return Text form (string) of the specified array of float numbers. 
	 */
	public static String toText(float[] array, String sep) {
		StringBuffer buffer = new StringBuffer();
		
		for (int i = 0; i < array.length; i++) {
			if ( i > 0)
				buffer.append(sep + " ");

			float value = array[i];
			buffer.append(value);
		}
		
		return buffer.toString();
		
	}

	
	/**
	 * Converting a specified collection of objects (any type) into a string in which each object is converted as a word in such string.
	 * Words in such returned string are connected by the character specified by the parameter {@code sep}. 
	 * This is template static method and so the type of object is specified by the template &lt;{@code T}&gt;.
	 * @param <T> type of each object in the specified collection.
	 * @param list Specified collection of objects.
	 * @param sep The character that is used to connect words in the returned string. As usual, it is a comma &quot;,&quot;.
	 * @return Text form (string) of the specified collection of objects, in which each object is converted as a word in such text form.
	 */
	public static <T extends Object> String toText(Collection<T> list, String sep) {
		StringBuffer buffer = new StringBuffer();
		
		int i = 0;
		for (T value : list) {
			if (i > 0)
				buffer.append(sep + " ");
			
			if (value instanceof TextParsable)
				buffer.append(((TextParsable)value).toText());
			else
				buffer.append(value);
			
			i++;
		}
			
		return buffer.toString();
	}
	
	
	/**
	 * Splitting (parsing) a specified string into many words (tokens). The character (string) that is used for separation is specified by the parameter {@code sep} which follows Java regular expression.
	 * @param text specified text string.
	 * @param sep The character (string) that is used for separation.
	 * @return {@link List} of words (tokens) from splitting the specified string.
	 */
	public static List<String> parseTextList(String text, String sep) {
		return TextParserUtil.split(text, sep, null);
	}
	
	
	/**
	 * Converting a specified map of key-value pairs (entries) into a string in which each key-value pair is converted as a part in form &quot;key=value&quot; of such string.
	 * Parts in such returned string are connected by the character specified by the parameter {@code sep}. 
	 * This is template static method and so the types of key and value is specified by template &lt;{@code K}&gt; and template &lt;{@code V}&gt;, respectively.
	 * @param <K> type of key.
	 * @param <V> type of value.
	 * @param map specified map of key-value pairs (entries).
	 * @param sep character that is used to connect parts in the returned string. As usual, it is a comma &quot;,&quot;.
	 * @return string of the specified map of entries, in which each entry is converted as a part in form &quot;key=value&quot; of such string. An example of this returned string is &quot;key1=value1,key2=value2,key3=value3&quot;
	 */
	public static <K extends Object, V extends Object> String toText(Map<K, V> map, String sep) {
		StringBuffer buffer = new StringBuffer();
		
		List<K> keys = Util.newList();
		keys.addAll(map.keySet());
		for (int i = 0; i < keys.size(); i++) {
			if (i > 0)
				buffer.append(sep + " ");
			
			K key = keys.get(i);
			
			Object value = map.get(key);
			String line = 
				(
					key instanceof TextParsable ? ((TextParsable)key).toText() : key.toString()
				) +
				"=" + 
				(
					value instanceof TextParsable ? ((TextParsable)value).toText() : value.toString()
				);
			buffer.append(line);
		}
		
		return buffer.toString();
	}
	
	
	/**
	 * Parsing (splitting) a specified string into many entries of the returned map. The character (string) that is used for separation is specified by the parameter {@code sep} which follows Java regular expression.
	 * Each entry is a pair of key and value. So the original specified string has a form &quot;key1=value1,key2=value2,key3=value3&quot;.
	 * This method is opposite to {@link #toText(Map, String)} method.
	 * @param text specified text string.
	 * @param sep character (string) that is used for separation.
	 * @return {@link Map} of entries which are key-value pairs, parsed from the specified string.
	 */
	public static Map<String, String> parseTextMap(String text, String sep) {
		Map<String, String> map = Util.newMap();
		
		List<String> list = parseTextList(text, sep);
		for (String string : list) {
			List<String> pair = TextParserUtil.split(string, "=", null);
			if (pair.size() < 2)
				continue;
			
			map.put(pair.get(0), pair.get(1));
		}
		
		return map;
	}
	
	
	/**
	 * Writing a specified map of key-value pairs (entries) via {@link Writer} in which each key-value pair is converted as a part in form &quot;key=value&quot;.
	 * These parts are connected by the character specified by the parameter {@code sep}. 
	 * This is template static method and so the types of key and value is specified by template &lt;{@code K}&gt; and template &lt;{@code V}&gt;, respectively.
	 * @param <K> type of key.
	 * @param <V> type of value.
	 * @param map specified map of key-value pairs (entries).
	 * @param writer {@link Writer} for writing the specified map of key-value pairs (entries).
	 */
	public static <K extends Object, V extends TextParsable> void toText(Map<K, V> map, Writer writer) {
		try {
			Set<K> keys = map.keySet();
			int i = 0;
			for (K key : keys) {
				if (i > 0)
					writer.write("\n");
				
				TextParsable value = map.get(key);
				
				String line = 
					(
						key instanceof TextParsable ? ((TextParsable)key).toText() : key.toString()
					) +
					"=" + value.toText();
				
				writer.write(line);
				
				i++;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Parsing from an archive (file) by {@link Reader} line by line and then conversing such lines into many entries of the returned map.
	 * Each entry is a pair of key and value. So each line read by {@link Reader} has a form &quot;key1=value1,key2=value2,key3=value3&quot;.
	 * @param <K> type of key.
	 * @param <V> type of value.
	 * @param reader {@link Reader} for reading an archive (file) line by line.
	 * @param k class of key.
	 * @param v class of value.
	 * @return {@link Map} of entries which are key-value pairs.
	 */
	@SuppressWarnings("unchecked")
	public static <K extends Object, V extends TextParsable> Map<K, V> parseTextParsableMap(
			Reader reader, Class<K> k, Class<V> v) {
		
		Map<K, V> map = Util.newMap();
		
		BufferedReader buffered = new BufferedReader(reader);
		
		String line = null;
		try {
			while ( (line = buffered.readLine()) != null ) {
				
				List<String> list = TextParserUtil.split(line, "=", null);
				if (list.size() < 2)
					continue;
				
				K key = (K) parseObjectByClass(list.get(0), k);
				V value = v.newInstance();
				value.parseText(list.get(1));
				
				map.put(key, value);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return map;
		
	}
	
	
	/**
	 * Splitting and parsing a specified string into many objects. The character (string) that is used for separation is specified by the parameter {@code sep} which follows Java regular expression.
	 * A returned objects must implement {@link TextParsable} directly or indirectly, called <i>text parsable</i> objects.
	 * @param <T> type of returned objects, which must implement {@link TextParsable} directly or indirectly.
	 * @param string specified string.
	 * @param classType class of returned objects, which plays the role of template for parsing. This class must be the class of <i>text-parsable</i> object. 
	 * @param sep character (string) that is used for separation.
	 * @return {@link List} of text-parsable objects parsed from specified string.
	 */
	public static <T extends TextParsable> List<T> parseTextParsableList(String string, Class<T> classType, String sep) {
		string = string.trim();
		List<String> array = TextParserUtil.split(string, sep, null);
		
		List<T> list = Util.newList();
		for (String el : array) {
			
			try {
				T t = classType.newInstance();
				t.parseText(el);
				
				list.add(t);
			} 
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return list;
	}
	
	
	/**
	 * Splitting and parsing a specified string into many objects. The character (string) that is used for separation is specified by the parameter {@code sep} which follows Java regular expression.
	 * The class of returned objects are arbitrary. This method is more general that {@link #parseTextParsableList(String, Class, String)} method.
	 * @param <T> type of returned objects.
	 * @param string specified string.
	 * @param type class of returned objects, which plays the role of template for parsing.
	 * @param sep character (string) that is used for separation.
	 * @return {@link List} of objects parsed from specified string.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Object> List<T> parseListByClass(String string, Class<T> type, String sep) {
		string = string.trim();
		List<String> array = TextParserUtil.split(string, sep, null);
		
		List<T> list = Util.newList();
		for (String el : array) {
			T v = (T) parseObjectByClass(el, type);
			if (v != null)
				list.add(v);
		}
		
		return list;
	}

	
	/**
	 * Parsing (converting) a specified string into an object according to a class.
	 * @param string specified string.
	 * @param type class of the parsed object.
	 * @return parsed object.
	 */
	public static Object parseObjectByClass(String string, Class<?> type) {
		
		Object v = null;
		try {
			if (Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type))
				v = Boolean.parseBoolean(string); 
			else if (Byte.class.isAssignableFrom(type) || byte.class.isAssignableFrom(type))
				v = Byte.parseByte(string); 
			else if (Short.class.isAssignableFrom(type) || short.class.isAssignableFrom(type))
				v = Short.parseShort(string); 
			else if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type))
				v = Integer.parseInt(string); 
			else if (Long.class.isAssignableFrom(type) || long.class.isAssignableFrom(type))
				v = Long.parseLong(string); 
			else if (Float.class.isAssignableFrom(type) || float.class.isAssignableFrom(type))
				v = Float.parseFloat(string);
			else if (Double.class.isAssignableFrom(type) || double.class.isAssignableFrom(type))
				v = Double.parseDouble(string);
			else if (Character.class.isAssignableFrom(type) || char.class.isAssignableFrom(type))
				v = string.charAt(0); 
			else if (String.class.isAssignableFrom(type))
				v = string; 
			else if (Date.class.isAssignableFrom(type))
				v = new SimpleDateFormat(Constants.DATE_FORMAT).parse(string); 
			else if (File.class.isAssignableFrom(type))
				v = new File(string);
			else if (xURI.class.isAssignableFrom(type))
				v = xURI.create(string); 
			else if (Path.class.isAssignableFrom(type)) {
				UriAdapter adapter = new UriAdapter();
				v = adapter.newPath(string);
				adapter.close();
			}
			else if (TextParsable.class.isAssignableFrom(type)){
				TextParsable obj = (TextParsable) type.newInstance();
				obj.parseText(string);
				v = obj;
			}
			else
				v = null;
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			v = null;
		} 
	
		return v;
	}

	
	/**
	 * Parsing (converting) a specified string into an object according to a full name of the specified class. An example of the full name of a class is &quot;net.hudup.core.parser.TextParsable&quot;.
	 * @param string specified string.
	 * @param className full name of the specified class.
	 * @return object parsed from the specified string.
	 */
	public static Object parseObjectByClass(String string, String className) {
		try {
			return parseObjectByClass(string, Class.forName(className));
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}


}
