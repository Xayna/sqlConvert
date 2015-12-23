package demo.connections.dbConnections;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import demo.connections.IdbConnections.IConnection;

public class MySQLConnection implements IConnection {

	public static final String PROPERTIES_FILE_NAME = "mySqlDatabase.properties";
	
	public static final String DB_NAME_PROP = "database.name";
	public static final String DB_DRIVER_PROP = "jdbc.drivers";
	public static final String DB_USER_NAME_PROP = "database.user";
	public static final String DB_PASSWORD_PROP = "database.password";
	public static final String DB_SCHEMA_PROP = "database.schema";
	public static final String DB_URL = "database.url";

	private Connection conn = null;

	private Properties props;

	/*
	 * @return properties
	 */
	public Properties getProps() {
		return props;
	}

	/*
	 * @return connection
	 */
	public Connection connect() {
		try {
			// Load in Database properties from file
			props = new Properties();

			// try to load the last saved properties
			InputStream in = MySQLConnection.class
					.getResourceAsStream(PROPERTIES_FILE_NAME);

			// if no new properties exists load the default ones
			
				if (in == null) {
					System.err
							.println("Error: Failed to find the \"database.properties\" file. Note that it must be ");
					System.err
							.println("in the same directory as Main.class and that the name is case sensitive");
					System.exit(1);
				}
			
			props.load(in);
			// close stream
			in.close();

			// get the db info from properties
			String[] databaseInfo = getDBConnectionInfo(props);
			if (databaseInfo == null) {
				System.err.println("Cancelling the process");
				System.exit(1);
			}

			// load the jdbc driver
			Class.forName(databaseInfo[0]);

			// Get a database connection
			String fullDatabaseURL = null;
			try {
				if (!databaseInfo[1].endsWith("/"))
					databaseInfo[1] = databaseInfo[1].concat("/");

				fullDatabaseURL = databaseInfo[1] + databaseInfo[2];// +":"+databaseInfo[3];
				System.out.println("fullDatabaseURL : " + fullDatabaseURL);
				conn = DriverManager.getConnection(fullDatabaseURL.trim(),
						databaseInfo[4], databaseInfo[5]);
				System.out.println(fullDatabaseURL);

			} catch (Exception e) {
				System.out.println(fullDatabaseURL);
				e.printStackTrace();
				throw new Exception("Error: Attempt to connect to database \""
						+ fullDatabaseURL
						+ ((databaseInfo[4] == null) ? ""
								: (" with username \"" + databaseInfo[4]))
						+ "\" failed: " + e.getMessage());
			}

			// save the db last used db info
			//saveProperties(props, databaseInfo);
		} catch (SQLException e) {
			do {
				System.out.println(e.getMessage());
			} while ((e = e.getNextException()) != null);
			System.exit(1);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}

		return conn;

	}

	/*
	 * close the connection with database
	 */
	public boolean close() {
		try {
			if (conn != null)
				if (!conn.isClosed())
					conn.close();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/*
	 * build the db info UI
	 * 
	 * @param props, properties contain db info
	 * 
	 * @return String
	 */
	private static String[] getDBConnectionInfo(Properties props) {
		// get the JDBC driver
		String drivers = props.getProperty(DB_DRIVER_PROP);
		// get the db url
		String databaseUrl = props.getProperty(DB_URL);
		// get the db Name
		String database = props.getProperty(DB_NAME_PROP);
		// get the schema
		String schema = props.getProperty(DB_SCHEMA_PROP);
		// get the user name
		String username = props.getProperty(DB_USER_NAME_PROP);
		// get the password
		String pass = props.getProperty(DB_PASSWORD_PROP);

		return new String[] { drivers, databaseUrl,
				database, schema, username, pass };
		/*
		 * // get cassandra host String host =
		 * props.getProperty(Helper.NDB_HOST_PROP); // get cassandra port String
		 * port = props.getProperty(Helper.NDB_PORT_PROP);
		 * 
		 * // creating UI fields JTextField dbDriver = new JTextField((drivers
		 * == null) ? "" : drivers, 50); JTextField dbURL = new
		 * JTextField((databaseUrl == null) ? "" : databaseUrl, 20); JTextField
		 * dbName = new JTextField((database == null) ? "" : database, 20);
		 * JTextField dbSchema = new JTextField((schema == null) ? "" : schema,
		 * 20); JTextField username = new JTextField((usernameP == null) ? "" :
		 * usernameP, 20); JPasswordField password = new JPasswordField( (pass
		 * == null) ? "" : pass, 20);
		 * 
		 * JTextField cHost = new JTextField((host == null) ? "" : host, 20);
		 * JTextField cPort = new JTextField((port == null) ? "" : port, 20);
		 * 
		 * final Object[] fields = { Helper.GET_DB_DRIVER, dbDriver,
		 * Helper.GET_DB_URL, dbURL, Helper.GET_DB_NAME, dbName,
		 * Helper.GET_DB_SCHEMA, dbSchema, Helper.GET_DB_USER_NAME, username,
		 * Helper.GET_DB_USER_PASS, password, Helper.GET_NDB_HOST, cHost,
		 * Helper.GET_NDB_PORT, cPort };
		 * 
		 * 
		 * // creating UI dialog JOptionPane pane = new JOptionPane(fields,
		 * JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null);
		 * JDialog d = pane.createDialog(null, "Database Access");
		 * d.setVisible(true);
		 * 
		 * // reading entered values Integer returnValue = (Integer)
		 * pane.getValue(); d.dispose();
		 * 
		 * // checking for return value or cancel option if (returnValue == null
		 * || returnValue != JOptionPane.OK_OPTION) { return null; }
		 * 
		 * // checking essential db connection parameters if (dbDriver.getText()
		 * == null) { System.err.println("Error: No JDBC driver specified");
		 * return null; }
		 * 
		 * if (dbURL.getText() == null) {
		 * System.err.println("Error: No database URL "); return null; } if
		 * (dbName.getText() == null) {
		 * System.err.println("Error: No database name is provided "); return
		 * null; } if (dbSchema.getText() == null) {
		 * System.err.println("Error: No schema is provided "); return null; }
		 * 
		 * if (username.getText() == null) {
		 * System.err.println("Error: No username is provided "); return null; }
		 * 
		 * if (password.getPassword().toString() == null) {
		 * System.err.println("Error: No password is provided "); return null; }
		 * 
		 * if (cHost.getText() == null) {
		 * System.err.println("Error: No host is provided "); return null; }
		 * 
		 * if (cPort.getText() == null) {
		 * System.err.println("Error: No port is provided "); return null; }
		 * 
		 * return new String[] { dbDriver.getText(), dbURL.getText(),
		 * dbName.getText(), dbSchema.getText(), username.getText(), new
		 * String(password.getPassword()), cHost.getText(), cPort.getText() };
		 */
	}

	@Override
	public boolean initailize() {
		// TODO Auto-generated method stub
		return true;
	}

	/*
	 * @param props, properties to be saved
	 * 
	 * @param dbInfo, array of new properties values
	 * 
	 * @return
	 */
	/*
	 * private void saveProperties(Properties props, String[] dbInfo) {
	 * 
	 * // set the JDBC driver props.setProperty(Helper.DB_DRIVER_PROP,
	 * dbInfo[0]); // set the db url props.setProperty(Helper.DB_URL,
	 * dbInfo[1]); // set the db Name props.setProperty(Helper.DB_NAME_PROP,
	 * dbInfo[2]); // set the schema props.setProperty(Helper.DB_SCHEMA_PROP,
	 * dbInfo[3]); // set the username
	 * props.setProperty(Helper.DB_USER_NAME_PROP, dbInfo[4]); // set the
	 * password props.setProperty(Helper.DB_PASSWORD_PROP, dbInfo[5]); // set
	 * the Host props.setProperty(Helper.NDB_HOST_PROP, dbInfo[6]); // set the
	 * Port props.setProperty(Helper.NDB_PORT_PROP, dbInfo[7]); // saving the
	 * properties File file = new File(Helper.TEMP_FILE_PATH +
	 * Helper.NEW_PROPERTIES_FILE_NAME); OutputStream out = null; try {
	 * 
	 * out = new FileOutputStream(file); props.store(out,
	 * "This is an optional header comment string");
	 * System.out.println(file.getAbsolutePath());
	 * 
	 * } catch (FileNotFoundException e) {
	 * 
	 * e.printStackTrace(); } catch (IOException e) { e.printStackTrace();
	 * 
	 * } finally { try { if (out != null) out.close(); } catch (IOException e) {
	 * 
	 * e.printStackTrace(); } } }
	 */

}
