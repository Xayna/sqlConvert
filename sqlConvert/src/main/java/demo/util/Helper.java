package demo.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Helper {


	static final String DEFAULT_VALUE = "text";

	static List<String> myKeywordsList = null;

	static HashMap<String, String> myFunctionsMap = null;
	static HashMap<String, String> myNumbericTypesMap = null;
	static HashMap<String, String> myDataTypesMap = null;

	
	public static void initialize ()
	{
		myKeywordsList = new ArrayList<String>();
		myFunctionsMap = new HashMap<String, String>();
		myNumbericTypesMap = new HashMap<String, String>();
		myDataTypesMap = new HashMap<String, String>();
	}
	
	public static boolean isKeyword(String word) {
		return myKeywordsList.contains(word.toUpperCase());
	}

	public static boolean hasSpace(String word) {
		return word.trim().contains(" ");
	}

	public static boolean isMSSQLFunction(String key) {
		return myFunctionsMap.containsKey(key.toUpperCase().trim());
	}

	public static String getMappedFunction(String key) {
		return myFunctionsMap.get(key.toUpperCase().trim());
	}

	public static String getMappedDataType(String key) {
		return myDataTypesMap.getOrDefault(key, DEFAULT_VALUE);
	}

	public static boolean isNumericType(String dataType) {

		return myNumbericTypesMap.containsValue(dataType.toUpperCase().trim());
	}

}
