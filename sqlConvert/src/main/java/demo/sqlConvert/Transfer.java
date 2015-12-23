package demo.sqlConvert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

import demo.connections.dbConnections.PostgresConnection;
import demo.metamodel.IModel.IConstraint;
import demo.metamodel.IModel.IDatabase;
import demo.metamodel.IModel.ISchema;
import demo.metamodel.IModel.ISequence;
import demo.metamodel.IModel.ITable;
import demo.util.Logger;

public class Transfer {

	static IDatabase myDB;

	static Connection postConn;
	static PostgresConnection postgresCon;

	static void startTransfer(IDatabase db, Connection msConn,
			String... postgresParam) {
		System.out.println("Trasfer started");
		Stopwatch timer = Stopwatch.createStarted();
		// create new db in postgres
		if (CreateDB(db, postgresParam)) {
			// connect to the new db;
			postConn = null;
			postgresCon = new PostgresConnection(postgresParam[0],
					postgresParam[1], db.toString(), postgresParam[3],
					postgresParam[4]);
			timer = Stopwatch.createStarted();
			try {
				if (postgresCon.initailize()) {
					if ((postConn = postgresCon.connect()) != null) {
						System.out.println("Connected to " + db);
						// create extentions
						CreateExtention();
						System.out.println("Extenctions created");
						Logger.infoLogger.info("Extenctions created");

						// create schemas & tables
						for (ISchema schema : db.getSchemas()) {
							System.out.println("Creating Schema " + schema);
							Logger.infoLogger.info("Schema " + schema + " :");
							CreateScemasAndTables(schema);
						}

						Logger.infoLogger.info("Schemas & tables created");

						Stopwatch datatimmer = Stopwatch.createStarted();
						ExecutorService executor = Executors
								.newWorkStealingPool();

						System.out.println("\n Transferring data ... ");
						// Transferring data
						for (ISchema schema : db.getSchemas()) {

							for (ITable table : schema.getTables()) {

								Thread process = new Thread(new Runnable() {

									@Override
									public void run() {
										try {
											System.out
													.println("Transfaring table "
															+ table);
											testData.processData3(
													db.toString(), msConn,
													postConn, table);
										} catch (Exception e) {
											Logger.errorLogger
													.error("tarsferting data ,error at table :"
															+ table);
											Logger.errorLogger.catching(e);
										}
									}
								});
								executor.execute(process);

							}

						}

						executor.shutdown();

						while (!executor.awaitTermination(1,
								TimeUnit.NANOSECONDS))
							;
						Logger.infoLogger.info("total inserting time : "
								+ datatimmer.stop());
						System.out.println("Data transfered");

						// add pk & create sequences
						executor = Executors.newCachedThreadPool();

						for (ISchema schema : db.getSchemas()) {
							Logger.infoLogger.info("Schema " + schema + " :");

							Thread process = new Thread(new Runnable() {

								@Override
								public void run() {

									try {
										System.out
												.println("Adding PK & Unique constraints for "
														+ schema + " schema");
										AlterTablePKandUnique(schema);
									} catch (Exception e) {
										Logger.errorLogger.catching(e);
									}
								}
							});

							executor.execute(process);
						}
						executor.shutdownNow();
						while (!executor.awaitTermination(1,
								TimeUnit.NANOSECONDS))
							;

						Logger.infoLogger.info("Pk, Unique ");

						// add constraints -- FK & CK

						executor = Executors.newCachedThreadPool();

						for (ISchema schema : db.getSchemas()) {
							Logger.infoLogger.info("Schema " + schema + " :");

							Thread fkProcess = new Thread(new Runnable() {

								@Override
								public void run() {

									try {
										System.out
												.println("Adding FK constraints for "
														+ schema + " schema");
										AlterTablesFK(schema);
									} catch (Exception e) {
										Logger.errorLogger.catching(e);
									}
								}
							});

							Thread ckProcess = new Thread(new Runnable() {

								@Override
								public void run() {

									try {
										System.out
												.println("Adding Ckeck constraints for "
														+ schema + " schema");
										AlterTablesCK(schema);
									} catch (Exception e) {
										Logger.errorLogger.catching(e);
									}
								}
							});

							Thread seqProcess = new Thread(new Runnable() {

								@Override
								public void run() {

									try {
										System.out
												.println("Adding Secquences for "
														+ schema + " schema");
										CreateSequences(schema);
									} catch (Exception e) {
										Logger.errorLogger.catching(e);
									}
								}
							});

							executor.execute(fkProcess);
							executor.execute(ckProcess);
							executor.execute(seqProcess);
						}

						executor.shutdown();
						while (!executor.awaitTermination(1,
								TimeUnit.NANOSECONDS))
							;
						Logger.infoLogger
								.info("FKs, CKs & Sequences created created");

					}

					postgresCon.close();
				} else {
					System.out.println("Connecting to PostgesSQL Server faild");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				Logger.errorLogger.catching(ex);
			} finally {
				Logger.infoLogger.info("total time for the transfer process :"
						+ timer.stop());

			}

		} else {
			System.out.println("DB creation failed");
			System.exit(1);
		}
	}

	private static boolean CreateDB(IDatabase db, String... param) {
		Stopwatch timer = Stopwatch.createUnstarted();
		Stopwatch totalTime = Stopwatch.createStarted();
		postConn = null;
		postgresCon = new PostgresConnection(param[0], param[1], param[2],
				param[3], param[4]);

		try {
			timer.start();
			// open connection to existing database
			if (postgresCon.initailize()) {
				if ((postConn = postgresCon.connect()) != null) {
					Logger.infoLogger.info("Total time to connect to "
							+ postConn.getCatalog() + " db :" + timer.stop());
					System.out.println("Connected to PostgresSQL Server");
					System.out.println("Creating db : " + db);
					// timing for db creation
					timer.reset().start();
					// create database
					Logger.debugLogger.debug(db.toSQL());
					PreparedStatement ps = postConn
							.prepareStatement(db.toSQL());
					ps.execute();
					Logger.infoLogger.info("Total time to create " + db
							+ " db :" + timer.stop());
					// close the connection

					System.out.println("DB " + db
							+ " created\nDisconnecting ...");
					ps.close();
					postgresCon.close();
					return true;

				}

			}
			return false;

		} catch (Exception ex) {
			ex.printStackTrace();
			Logger.errorLogger.catching(ex);
			return false;
		} finally {
			Logger.infoLogger.info("Total time to execute createDB () process "
					+ totalTime.stop());

		}

		// connect to the new database
		// continue in transfer
	}

	private static void CreateExtention() throws Exception {
		// extention for uuid function
		System.out.println("Creating extenctions ...");
		Logger.debugLogger.debug("CREATE EXTENSION \"uuid-ossp\";");
		PreparedStatement ps = postConn
				.prepareStatement("CREATE EXTENSION \"uuid-ossp\";");
		ps.execute();
		ps.close();

	}

	private static void CreateScemasAndTables(ISchema schema) throws Exception {
		Stopwatch timer = Stopwatch.createStarted();
		int totalTable = 0;
		Logger.debugLogger.debug(schema.toSQL());
		PreparedStatement ps = postConn.prepareStatement(schema.toSQL());
		ps.execute();

		// create tables
		for (ITable table : schema.getTables()) {

			totalTable += schema.getTables().size();
			Logger.debugLogger.debug(table.toSQL());
			ps = postConn.prepareStatement(table.toSQL());
			ps.execute();

		}
		if (ps != null)
			ps.close();

		Logger.infoLogger.info(totalTable + " tables created in "
				+ timer.stop());

	}

	private static void AlterTablePKandUnique(ISchema schema) throws Exception {
		// add constraints -- PK & UNIQUE
		Stopwatch timer = Stopwatch.createStarted();
		int totalConst = schema.getConstraints().size();

		// ExecutorService executor = Executors.newCachedThreadPool();
		for (IConstraint constraint : schema.getConstraints()) {
			Logger.debugLogger.debug(constraint.toSQL());
			try (PreparedStatement ps = postConn.prepareStatement(constraint
					.toSQL())) {
				ps.execute();
			}

			catch (Exception e) {
				Logger.errorLogger.error("PK&Unique ,error at :" + constraint);
				Logger.errorLogger.catching(e);
			}

		}

		Logger.infoLogger.info(totalConst
				+ " PK & Unique constraints created in " + timer.stop());

	}

	private static void AlterTablesFK(ISchema schema) throws Exception {

		// add constraints -- FK

		Stopwatch timer = Stopwatch.createStarted();
		int totalConst = schema.getFKConstraints().size();

		for (IConstraint constraint : schema.getFKConstraints()) {
			Logger.debugLogger.debug(constraint.toSQL());
			try (PreparedStatement ps = postConn.prepareStatement(constraint
					.toSQL())) {
				ps.execute();
			}

			catch (Exception e) {
				Logger.errorLogger.error("FK ,error at :" + constraint);
				Logger.errorLogger.catching(e);
			}

		}

		Logger.infoLogger.info(totalConst + " FK constraints created in "
				+ timer.stop());

	}

	private static void AlterTablesCK(ISchema schema) throws Exception {

		// add constraints -- CK
		Stopwatch timer = Stopwatch.createStarted();
		int totalConst = schema.getCKConstraints().size();
		// ExecutorService executor = Executors.newCachedThreadPool();

		for (IConstraint constraint : schema.getCKConstraints()) {
			Logger.debugLogger.debug(constraint.toSQL());
			try (PreparedStatement ps = postConn.prepareStatement(constraint
					.toSQL())) {
				ps.execute();
			}

			catch (Exception e) {
				Logger.errorLogger.error("ck,error at :" + constraint);
				Logger.errorLogger.catching(e);
			}

		}

		Logger.infoLogger.info(totalConst + " Check constraints created in "
				+ timer.stop());

	}

	private static void CreateSequences(ISchema schema) throws Exception {
		Stopwatch timer = Stopwatch.createStarted();

		int totalSeqNum = schema.getSequences().size();

		for (ISequence seq : schema.getSequences()) {

			Logger.debugLogger.debug(seq.toSQL());
			try (PreparedStatement ps = postConn.prepareStatement(seq.toSQL())) {
				ps.execute();
			}

			catch (Exception e) {
				Logger.errorLogger.catching(e);
			}

		}

		Logger.infoLogger.info(totalSeqNum + " Sequence created in "
				+ timer.stop());

	}
}
