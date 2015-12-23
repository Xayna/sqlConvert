package demo.sqlConvert;

import java.sql.Connection;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

import demo.connections.dbConnections.MSSQLConnection;
import demo.metamodel.IModel.IDatabase;
import demo.util.Helper;
import demo.util.Json;
import demo.util.Logger;

public class Main {

	static IDatabase myDB;
	static Connection myConn;

	private static String psServer, psPort, psDBname, psUser, psPass;

	private static String msServer, msPort, msDBname, msUser, msPass;
	private static boolean security;

	public static void main(String[] args) {

		if (args.length == 9 || args.length == 10) {

			psServer = args[0];
			psPort = args[1];
			psDBname = args[2];
			psUser = args[3];
			psPass = args[4];

			msServer = args[5];
			msPort = args[6];
			msDBname = args[7];
			if (args[8].equals("-t"))
				security = true;
			else {
				security = false;
				msUser = args[8];
				msPass = args[9];
			}

		} else {
			System.out
					.println("Usage : <postgres server> <port> <default db> <username> <password> <ms server> <port> -t|[<user_name> <password>] ");
			System.exit(0);
		}
		System.out.println("Process started");
		Stopwatch timer = Stopwatch.createStarted();
		//initialize loggers
		Logger.initializeLoggers();

		//set hashmaps and list
		Json.readFromJsonFile();
		
		// connect to mssql server
		MSSQLConnection sqlCon = new MSSQLConnection(msServer, msPort,
				msDBname, security, msUser, msPass);
		if (sqlCon.initailize()) {
			if ((myConn = sqlCon.connect()) != null) {
				System.out.println("Connected to MS SQL Server\n");
				// initialze and start extraction process
				MSSQLFunctions myFunctions = new MSSQLFunctions(myConn);
				Stopwatch dbtimer = Stopwatch.createStarted();
				myDB = myFunctions.ExtractDB(msDBname);
				Logger.infoLogger.info("Total Time to extract Schemas :"
						+ dbtimer.stop());

				//Transfer to postgres db object
				Transfer.startTransfer(myDB, myConn, psServer, psPort,
						psDBname, psUser, psPass);
				System.out.println("Trasfer completed");

				sqlCon.close();

			} else {
				System.out.println("Connecting to MS SQL Server failed");

			}

		}

		String stop = timer.stop().toString();
		// format time into hh:mm:ss 
		Long time = timer.elapsed(TimeUnit.MILLISECONDS);

		Double sec = (double) time / 1000;

		Double millisec = (sec - sec.intValue()) * 1000;

		Double minute = (double) sec.intValue() / 60;
		sec = (minute - minute.intValue()) * 60;

		System.out.println("total time for the whole process :"
				+ stop
				+ (" (" + minute.intValue() + ":" + sec.intValue() + ":"
						+ millisec.intValue() + ")"));
		Logger.infoLogger.info("total time for the whole process :"
				+ stop
				+ (" (" + minute.intValue() + ":" + sec.intValue() + ":"
						+ millisec.intValue() + ")"));

	}

}
