package demo.util;

import org.apache.logging.log4j.LogManager;


public class Logger {
	
	public static final String LOGGER_INFO = "demo.sqlConvert.logger.info";
	public static final String LOGGER_DEBUG = "demo.sqlConvert.logger.debug";
	public static final String LOGGER_WARN = "demo.sqlConvert.logger.warn";
	public static final String LOGGER_ERROR = "demo.sqlConvert.logger.err";
	public static final String LOGGER_CONSOL = "demo.sqlConvert.logger.consol";
	public static final String LOGGER_ALL = "demo.sqlConvert.logger.all";
	
	public static  org.apache.logging.log4j.Logger infoLogger ;
	public static  org.apache.logging.log4j.Logger debugLogger ;
	public static  org.apache.logging.log4j.Logger warnLogger ;
	public static  org.apache.logging.log4j.Logger errorLogger;
	
	public static void initializeLoggers ()
	{
		infoLogger = LogManager.getLogger (LOGGER_INFO);
		debugLogger = LogManager.getLogger (LOGGER_DEBUG);
		warnLogger = LogManager.getLogger (LOGGER_WARN);
		errorLogger = LogManager.getLogger (LOGGER_ERROR);
		
		Logger.debugLogger.debug ("Loggers initialized");
	}
}
