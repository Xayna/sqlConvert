package demo.connections.dbConnections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import com.google.common.base.Stopwatch;

import demo.connections.IdbConnections.IConnection;
import demo.util.Logger;

public class MSSQLConnection implements IConnection {

	public static final String PROPERTIES_FILE_NAME = "MSSqlDatabase.properties";

	public static final String DB_NAME_PROP = "database.name";
	public static final String DB_DRIVER_PROP = "jdbc.drivers";
	public static final String DB_USER_NAME_PROP = "database.user";
	public static final String DB_PASSWORD_PROP = "database.password";
	public static final String DB_SCHEMA_PROP = "database.schema";
	public static final String DB_URL = "database.url";

	private Connection conn = null;

	private Properties props;

	private boolean integratedSecurity;

	private String server = null;
	private String port = null;
	private String dbName = null;

	private String username = null;
	private String password = null;

	private final String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	public MSSQLConnection(String server, String port, String dbName,
			boolean integratedSecurity, String... param) {

		this.server = server;
		this.port = port;
		this.dbName = dbName;
		this.integratedSecurity = integratedSecurity;
		if (!this.integratedSecurity) {
			username = param[0];
			password = param[1];
		}
	}

	/*
	 * @return properties
	 */
	public Properties getProps() {
		return props;
	}

	/*
	 * initailize the database connection , load MSSQL jdbc driver
	 */
	@Override
	public boolean initailize() {

		try {
			Class.forName(driver);
		}

		catch (Exception e) {
			Logger.errorLogger.catching(e);
			System.out.println("failed to load the mssql driver");
			System.exit(1);
			return false;
		}
		return true;

	}

	/*
	 * Connect to MSSQL database
	 * 
	 * @return connection
	 */
	public Connection connect() {
		Stopwatch timer = Stopwatch.createStarted();

		try {
			// prepare connection url
			String connectionUrl = "jdbc:sqlserver://" + this.server + ":"
					+ port + ";" + "databaseName=" + this.dbName + ";";

			// check authentication method
			if (this.integratedSecurity)
				connectionUrl += "integratedSecurity=true;";
			else
				connectionUrl += "user=" + this.username + ";" + "password="
						+ this.password + ";";

			// get connection to db
			conn = DriverManager.getConnection(connectionUrl);
			Logger.debugLogger.debug(connectionUrl);
			Logger.debugLogger.debug("Connected");

		} catch (Exception e) {
			e.printStackTrace();
			Logger.errorLogger.catching(e);
			Logger.errorLogger.catching(new Exception(
					"Error: Attempt to connect to database \""
							+ dbName
							+ ((this.username == null) ? ""
									: (" with username \"" + this.username))
							+ "\" failed: " + e.getMessage()));
			System.exit(1);
		}

		finally {
			Logger.infoLogger.info("Total time for connecting to db : "
					+ timer.stop());
		}
		return conn;

	}

	/*
	 * close the connection with database
	 */
	public boolean close() {
		Logger.debugLogger.debug("Closing connection");
		try {
			if (conn != null)
				if (!conn.isClosed())
					conn.close();
			return true;
		} catch (Exception ex) {
			Logger.errorLogger.catching(ex);
			return false;
		}
	}

}
