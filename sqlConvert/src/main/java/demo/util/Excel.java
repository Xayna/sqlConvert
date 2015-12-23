package demo.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.common.base.Stopwatch;

public class Excel {


	static final String STATUS_RESERVED = "reserved";
	static final String STATUS_NON_RESERVED = "non-reserved";

	static final String KEYWORDS_FILE = "demo/util/keywords.xlsx";
	static final String MAP_FUNCTION_FILE = "demo/util/mappedFunctions.xlsx";
	static final String MAP_DATATYPE_FILE = "demo/util/mappedDatatypes.xlsx";

	static final String SHEET_NAME_NUMERIC = "numeric";
	static final String SHEET_NAME_TIME = "time";
	static final String SHEET_NAME_BINARY = "binary";
	static final String SHEET_NAME_TEXT = "text";
	static final String SHEET_NAME_OTHER = "other";
	
	public static void getReservedKeywordsFromExcel() {

		Stopwatch timer = Stopwatch.createStarted();
		String path = Helper.class.getClassLoader().getResource("").getPath()
				+ KEYWORDS_FILE;
		Logger.debugLogger.debug("current path :" + path);
		Helper.myKeywordsList = new ArrayList<String>();
		try (XSSFWorkbook myBook = new XSSFWorkbook(new FileInputStream(path))) {
			XSSFSheet mySheet = myBook.getSheetAt(0);
			mySheet.forEach(myrow -> {
				if (myrow.getCell(1) != null
						&& myrow.getCell(1).getStringCellValue().trim()
								.equalsIgnoreCase(STATUS_RESERVED)) {
					Helper.myKeywordsList.add(myrow.getCell(0).getStringCellValue()
							.toUpperCase().trim());
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
			Logger.errorLogger.catching(e);

		} finally {
			Logger.debugLogger.debug("Reserved keyword loaded");
			Logger.infoLogger.info("Total time to load reserved keywords :"
					+ timer.stop());
		}

	}

	public static void getMappedFunctionsFromExcel() {

		Stopwatch timer = Stopwatch.createStarted();
		String path = Helper.class.getClassLoader().getResource("").getPath()
				+ MAP_FUNCTION_FILE;
		Logger.debugLogger.debug("current path :" + path);
		Helper.myFunctionsMap = new HashMap<String, String>();

		try (XSSFWorkbook myBook = new XSSFWorkbook(new FileInputStream(path))) {
			XSSFSheet mySheet = myBook.getSheetAt(0);
mySheet.forEach(myrow -> {if (myrow.getCell(0) != null && myrow.getCell(1) != null) {
					String key = myrow.getCell(0).getStringCellValue()
							.toUpperCase().trim();
					Helper.myFunctionsMap.put(key, myrow.getCell(1)
							.getStringCellValue().toUpperCase().trim());

				}});

		} catch (Exception ex) {
			ex.printStackTrace();
			Logger.debugLogger.catching(ex);
		} finally {
			Logger.debugLogger.debug("Mapped functions keywords loaded");
			Logger.infoLogger.info("Total time to load mapped functions :"
					+ timer.stop());
		}
	}

	public static void getMappedDataTypesFromExcel() {
		Stopwatch timer = Stopwatch.createStarted();
		String path = Helper.class.getClassLoader().getResource("").getPath()
				+ MAP_DATATYPE_FILE;

		Logger.debugLogger.debug("current path :" + path);

		Helper.myNumbericTypesMap = new HashMap<String, String>();
		Helper.myDataTypesMap = new HashMap<String, String>();

		try (XSSFWorkbook myBook = new XSSFWorkbook(new FileInputStream(path))) {
			Helper.myNumbericTypesMap.putAll(getDataTypesFromSheet(myBook,
					SHEET_NAME_NUMERIC));

			Helper.myDataTypesMap.putAll(Helper.myNumbericTypesMap);
			// myDataTypesMap.putAll(getDataTypesFromSheet(SHEET_NAME_BINARY));
			// myDataTypesMap.putAll(getDataTypesFromSheet(SHEET_NAME_TEXT));
			// myDataTypesMap.putAll(getDataTypesFromSheet(SHEET_NAME_TIME));
			// myDataTypesMap.putAll(getDataTypesFromSheet(SHEET_NAME_OTHER));
		} catch (IOException ex) {
			ex.printStackTrace();
			Logger.debugLogger.catching(ex);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.debugLogger.catching(e);
		} finally {
			Logger.debugLogger.debug("Mapped datatypes keywords loaded");
			Logger.infoLogger.info("Total time to load mapped datatypes :"
					+ timer.stop());
		}

	}

	private static Map<String, String> getDataTypesFromSheet(
			XSSFWorkbook myBook, String sheetName) {
		Map<String, String> temp = new HashMap<String, String>();
		XSSFSheet mySheet = myBook.getSheet(sheetName);

		mySheet.forEach(myrow ->{if (myrow.getCell(0) != null && myrow.getCell(1) != null)
				temp.put(myrow.getCell(0).getStringCellValue().toUpperCase()
						.trim(), myrow.getCell(1).getStringCellValue()
						.toUpperCase().trim());

		});

		return temp;
	}

}
