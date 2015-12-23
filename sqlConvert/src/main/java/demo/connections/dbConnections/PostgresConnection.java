package demo.connections.dbConnections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import com.google.common.base.Stopwatch;

import demo.connections.IdbConnections.IConnection;
import demo.util.Logger;

public class PostgresConnection implements IConnection {

	private Connection conn = null;

	private String server = null;
	private String port = null;
	private String dbName = null;

	private String username = null;
	private String password = null;

	public static final String PROPERTIES_FILE_NAME = "database.properties";

	private final String driver = "org.postgresql.Driver";

	public PostgresConnection() {
		super();
	}

	public PostgresConnection(String server, String port, String dbName,
			String username, String password) {
		super();
		this.server = server;
		this.port = port;
		this.dbName = dbName;
		this.username = username;
		this.password = password;
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
	@Override
	public Connection connect() {

		Stopwatch timer = Stopwatch.createStarted();
		try {

			// prepare connection url
			String url = "jdbc:postgresql://" + server + ":" + port + "/"
					+ dbName.toLowerCase();

			// create properties object to pass connection info
			Properties dbProb = new Properties();
			dbProb.setProperty("allowEncodingChanges", "true");
			dbProb.setProperty("user", username);
			dbProb.setProperty("password", password);
			try {
				// get connection to db
				conn = DriverManager.getConnection(url, dbProb);

			} catch (Exception e) {
				Logger.errorLogger.catching(new Exception(
						"Error: Attempt to connect to database \""
								+ dbName
								+ ((username == null) ? ""
										: (" with username \"" + username))
								+ "\" failed: " + e.getMessage()));
				System.exit(1);
			}
		} catch (Exception e) {
			Logger.errorLogger.catching(e);
			e.printStackTrace();
			System.exit(1);
		} finally {
			Logger.infoLogger.info("Total time for connecting to db : "
					+ timer.stop());
		}
		return conn;
	}

	/*
	 * close the connection with database
	 */
	@Override
	public boolean close() {
		Logger.debugLogger.debug("Closing connection");
		try {
			if (!conn.isClosed())
				conn.close();
			return true;

		} catch (Exception ex) {
			Logger.errorLogger.catching(ex);
			ex.printStackTrace();
			return false;
		}
	}

}
