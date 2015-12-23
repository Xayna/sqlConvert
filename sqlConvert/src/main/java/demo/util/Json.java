package demo.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public class Json {

	final static String FILE = "../resources/data.json";

	
	
	public static JSONObject createJson(List<String> mySet,
			Map<String, String> myFunctionsMap,
			Map<String, String> myNumbericTypesMap,
			Map<String, String> myDataTypesMap) {
		JSONObject root = new JSONObject();
		JSONArray myArray = new JSONArray();

		
		myArray.addAll(mySet);
		
		root.put("reservedKeyWord", myArray);

		JSONObject mappingObject = new JSONObject(myFunctionsMap);
		root.put("functionsMap", mappingObject);

		mappingObject = new JSONObject(myNumbericTypesMap);
		root.put("numerictypesMap", mappingObject);

		mappingObject = new JSONObject(myDataTypesMap);
		root.put("datatypesMap", mappingObject);

		return root;

	}

	public static void printToTerminal(JSONObject object) {
		StringWriter out = new StringWriter();
		try {
			object.writeJSONString(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String jsonText = out.toString();
		System.out.println(jsonText);
	}

	public static void printToFile(JSONObject object) {
		try (FileWriter file = new FileWriter(FILE)) {
			file.write(object.toJSONString());
			file.flush();
	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void readFromJsonFile() {
		try {
			
			//FileSystems.getDefault().getRootDirectories().forEach(dir -> System.out.println(dir.getFileName()));
			FileSystems.getDefault().getPath("resources", "data.json");
			//final ClassLoader loader = Json.class.getClassLoader();
	String path = FileSystems.getDefault().getPath("resources", "data.json").toString(); //loader.getResource(FILE).getPath();
	Logger.debugLogger.debug("Json file path :" + path );
		JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
		
		Helper.initialize ();
		

			Object obj = parser.parse(new FileReader(path));
			
			// getting the root object
			JSONObject root = (JSONObject) obj;

			// get key words
			JSONArray msg = (JSONArray) root.get("reservedKeyWord");
					
			//Stopwatch timer = Stopwatch.createStarted();
			Iterator<Object> iterator = msg.iterator();
			while (iterator.hasNext()) {
				Helper.myKeywordsList.add(iterator.next().toString());}
					
			//get numerictypes Map
			fillHashMap((JSONObject)root.get("numerictypesMap") ,Helper. myNumbericTypesMap);
			
			//get functions map
			fillHashMap((JSONObject)root.get("functionsMap") , Helper.myFunctionsMap);
			
			//get datatype map	
			fillHashMap((JSONObject)root.get("datatypesMap") , Helper.myDataTypesMap);
						
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private static void fillHashMap (JSONObject object , HashMap<String, String> map)
	{
		//Stopwatch timer = Stopwatch.createStarted();
		Iterator<String> objIterator = object.keySet().iterator();
		while (objIterator.hasNext()) {
			String key = objIterator.next();
			map.put(key, object.get(key).toString());
			 }
		//System.out.println("time to add items using iterator" + timer.stop());

	//	System.out.println(map.keySet().stream().collect(Collectors.joining(",")));
		
	}
}