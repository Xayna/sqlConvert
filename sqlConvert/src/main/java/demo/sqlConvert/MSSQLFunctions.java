package demo.sqlConvert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

import demo.metamodel.CKConstraint;
import demo.metamodel.Column;
import demo.metamodel.Constraint;
import demo.metamodel.Database;
import demo.metamodel.FKConstraint;
import demo.metamodel.Schema;
import demo.metamodel.Sequence;
import demo.metamodel.Table;
import demo.metamodel.View;
import demo.metamodel.IModel.ICKConstraint;
import demo.metamodel.IModel.IColumn;
import demo.metamodel.IModel.IConstraint;
import demo.metamodel.IModel.IDatabase;
import demo.metamodel.IModel.IFKConstraint;
import demo.metamodel.IModel.ISchema;
import demo.metamodel.IModel.ISequence;
import demo.metamodel.IModel.ITable;
import demo.metamodel.IModel.IView;
import demo.util.Logger;
import demo.util.TableType;

public class MSSQLFunctions implements SQLFunctions {

	IDatabase myDB;
	Connection myConn;

	public MSSQLFunctions(Connection conn) {
		super();
		myConn = conn;

	}

	/*
	 * DB Extract process
	 * 
	 * @dbName , database name to be extracted
	 * 
	 * @return, database object
	 */
	public IDatabase ExtractDB(String dbName) {
		Stopwatch timer = Stopwatch.createStarted();

		myDB = new Database(dbName);
		ExractSchema();
		Logger.infoLogger
				.info("Total Time to extract Schemas :" + timer.stop());
		return myDB;
	}

	/*
	 * extract schemas related to db
	 */
	public void ExractSchema() {

		// get all related schemas
		String sqlStr = "SELECT SCHEMA_NAME FROM " + myDB
				+ ".INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_OWNER = ?";

		try (PreparedStatement st = myConn.prepareStatement(sqlStr)) {
			st.setString(1, "dbo");
			ExecutorService executor = Executors.newWorkStealingPool();
			try (ResultSet rs = st.executeQuery()) {
				while (rs.next()) {
					// for each schema initiate new object.
					ISchema schema = new Schema(rs.getString("SCHEMA_NAME"));
					myDB.getSchemas().add(schema);

					// start a thread to extract schema elements
					Thread process = new Thread(new Runnable() {

						@Override
						public void run() {
							ExractSchema(schema);

						}
					});

					executor.execute(process);

				}
			}

			executor.shutdown();
			while (!executor.awaitTermination(1, TimeUnit.MICROSECONDS))
				;

			System.out.println("Schemas extracted\n");

		} catch (SQLException e) {
			e.printStackTrace();
			Logger.errorLogger.catching(e);
		} catch (Exception ex) {
			ex.printStackTrace();
			Logger.errorLogger.catching(ex);
		} finally {
			Logger.infoLogger.info("Number of extracted schemas : "
					+ myDB.getSchemas().size());
		}

	}

	/*
	 * Extract schema elements ( tables, constratins , ...)
	 * 
	 * @schema , schema object to be extracted
	 * 
	 * @retrun extracted schema object
	 */
	public ISchema ExractSchema(ISchema schema) {
		System.out.println("Extracting schema : " + schema);
		ISchema mySchema = null;
		if ((mySchema = extractTables(schema)) != null) {
			mySchema = extractConstraints(mySchema);
			mySchema = extractSequences(mySchema);
		}
		return mySchema;
	}

	/*
	 * Extract tables related to the passed schema
	 * 
	 * @schema , schema object to be extracted
	 * 
	 * @retrun extracted schema object with tables
	 */
	public ISchema extractTables(ISchema schema) {
		Stopwatch timer = Stopwatch.createStarted();

		// tables
		String sqlStr = "SELECT table_schema, table_name, table_type "
				+ "FROM " + myDB + ".information_schema.tables "
				+ "WHERE table_schema = ? AND table_type != ?";

		try (PreparedStatement st = myConn.prepareStatement(sqlStr)) {
			st.setString(1, schema.toString());
			st.setString(2, TableType.VIEW.toString());

			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				ITable table = new Table(schema, rs.getString("table_name"),
						rs.getString("table_type"));

				table = extractTableColumns(myConn, schema, table);
				schema.getTables().add(table);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Logger.errorLogger.catching(e);
			return null;
		} finally {
			Logger.infoLogger.info("Total time for extracting "
					+ schema.getTables().size() + "tables  from " + schema
					+ ": " + timer.stop());
		}
		return schema;
	}

	/*
	 * Extract columns related to the passed schema and table
	 * @schema , schema object to be extracted
	 * @table , table object to get its columns  
	 * @retrun table object with columns
	 */
	public ITable extractTableColumns(Connection conn, ISchema schema,
			ITable table) {
		Stopwatch timer = Stopwatch.createStarted();
		// rows for the table
		String sqlStr = "SELECT "
				+ "a.COLUMN_NAME, a.ORDINAL_POSITION, a.COLUMN_DEFAULT, a.IS_NULLABLE ,"
				+ "a.DATA_TYPE, a.CHARACTER_MAXIMUM_LENGTH, a.NUMERIC_PRECISION, a.NUMERIC_SCALE, "
				+ "a.DATETIME_PRECISION , b.is_identity "
				+ "FROM "
				+ myDB
				+ ".information_schema.columns a,"
				+ myDB
				+ ".sys.columns b "
				+ "WHERE a.TABLE_NAME = ? "
				+ "AND a.table_schema = ? "
				+ "AND a.COLUMN_NAME = b.name "
				+ "AND OBJECT_ID(a.TABLE_CATALOG +'.'+ a.TABLE_SCHEMA + '.'+ a.TABLE_NAME) = b.object_id";

		// + "AND table_catalog = ? "
		try (PreparedStatement st = conn.prepareStatement(sqlStr)) {
			st.setString(1, table.toString());
			st.setString(2, schema.toString());

			try (ResultSet columnsRS = st.executeQuery()) {
				while (columnsRS.next()) {
					IColumn col = new Column();
					col.setTable(table); 
					col.setColumnName(columnsRS.getString("COLUMN_NAME"));
					col.setPosition(columnsRS.getInt("ORDINAL_POSITION"));
					col.setDefualtValue(columnsRS.getObject("COLUMN_DEFAULT"));
					col.setNullable(columnsRS.getString("IS_NULLABLE")
							.toUpperCase().equals("NO") ? false : true);
					col.setIdentity(columnsRS.getInt("is_identity") == 1 ? true
							: false);
					// col.setExtra(columnsRS.getString("EXTRA"));
					col.setDataType(columnsRS.getString("DATA_TYPE"));
					col.setMaxCharSize(columnsRS
							.getInt("CHARACTER_MAXIMUM_LENGTH"));
					col.setNumaricPrecision(columnsRS
							.getInt("NUMERIC_PRECISION"));
					col.setNumaricScale(columnsRS.getInt("NUMERIC_SCALE"));
					col.setDateTimePrecision(columnsRS
							.getInt("DATETIME_PRECISION"));
					// col.setColumnType(columnsRS.getString("COLUMN_TYPE"));
					// col.setColumnComment(columnsRS.getString("COLUMN_COMMENT"));

					table.addColumn(col);
				

				}// while
			}// try
		}// try
		catch (SQLException e) {
			e.printStackTrace();
			Logger.errorLogger.catching(e);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.errorLogger.catching(e);
		} finally {
			Logger.infoLogger.info("Total time for extracting "
					+ table.getColumns().size() + " columns  from " + table
					+ ": " + timer.stop());
		}
		return table;
	}

	/*
	 * Extract constraints related to the passed schema 
	 * @schema , schema object to be extracted 
	 * @retrun extracted schema object with constraints
	 */
	public ISchema extractConstraints(ISchema schema) {
		Stopwatch timer = Stopwatch.createStarted();
		String sqlStr = " SELECT "
				+ "CONSTRAINT_SCHEMA, CONSTRAINT_NAME, CONSTRAINT_TYPE,"
				+ "IS_DEFERRABLE , INITIALLY_DEFERRED " + "FROM " + myDB
				+ ".information_schema.Table_Constraints "
				+ "WHERE TABLE_SCHEMA = ? and TABLE_NAME = ? ";

		try (PreparedStatement ps = myConn.prepareStatement(sqlStr)) {
			for (ITable table : schema.getTables()) {
				ps.setString(1, table.getSchema().toString());
				ps.setString(2, table.toString());

				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						IConstraint constraint = null;
						//fk
						if (rs.getString("CONSTRAINT_TYPE").equalsIgnoreCase(
								Constraint.FK)) {
							constraint = new FKConstraint(
									rs.getString("CONSTRAINT_SCHEMA"),
									rs.getString("CONSTRAINT_NAME"),
									rs.getString("CONSTRAINT_TYPE"),
									table.getSchema(),
									table,
									(rs.getString("IS_DEFERRABLE")
											.equalsIgnoreCase(Constraint.YES) ? true
											: false),
									(rs.getString("INITIALLY_DEFERRED")
											.equalsIgnoreCase(Constraint.YES) ? true
											: false));
							
							constraint = getFKContraintDetails((IFKConstraint) constraint);
							schema.getFKConstraints().add(constraint);

						}
						//check
						else if (rs.getString("CONSTRAINT_TYPE")
								.equalsIgnoreCase(Constraint.CHECK)) {
							constraint = new CKConstraint(
									rs.getString("CONSTRAINT_SCHEMA"),
									rs.getString("CONSTRAINT_NAME"),
									rs.getString("CONSTRAINT_TYPE"),
									table.getSchema(),
									table,
									(rs.getString("IS_DEFERRABLE")
											.equalsIgnoreCase(Constraint.YES) ? true
											: false),
									(rs.getString("INITIALLY_DEFERRED")
											.equalsIgnoreCase(Constraint.YES) ? true
											: false));

							constraint = getCKContraintDetails((ICKConstraint) constraint);
							schema.getCKConstraints().add(constraint);
						}
						//pk & unique
						else {
							constraint = new Constraint(
									rs.getString("CONSTRAINT_SCHEMA"),
									rs.getString("CONSTRAINT_NAME"),
									rs.getString("CONSTRAINT_TYPE"),
									table.getSchema(),
									table,
									(rs.getString("IS_DEFERRABLE")
											.equalsIgnoreCase(Constraint.YES) ? true
											: false),
									(rs.getString("INITIALLY_DEFERRED")
											.equalsIgnoreCase(Constraint.YES) ? true
											: false));
							constraint = getContraintDetails(constraint);
							schema.getConstraints().add(constraint);
						}
						table.addConstriant(constraint);
					} // while
				}// try
			}// for
			return schema;
		} // try
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			int num = schema.getCKConstraints().size()
					+ schema.getFKConstraints().size()
					+ schema.getConstraints().size();
			Logger.infoLogger.info("Total time for extracting " + num
					+ " constraints  from " + schema + ": " + timer.stop());
		}

	}

	/*
	 * Extract constraints related to the passed schema 
	 * @schema , schema object to be extracted 
	 * @retrun extracted schema object with constraints
	 */
	public ICKConstraint getCKContraintDetails(ICKConstraint cons) {
		ICKConstraint constraint = cons;
		// this query is taken  from stack overflow website
		String sqlStr = ""
				+ "SELECT  obj.name AS CONSTRAINT_NAME, "
				+ "    sch.name AS [SCHEMA_NAME], "
				+ "    tab1.name AS [CONSTRAINT_TABLE], "
				+ "    col1.name AS [CONSTRAINT_COLUMN], "
				+ "	chc.[definition] AS [CHECK_CLAUSE] "
				+ "FROM "
				+ myDB
				+ ".sys.check_constraints chc "
				+ "INNER JOIN "
				+ myDB
				+ ".sys.objects obj "
				+ "    ON obj.object_id = chc.object_id "
				+ "INNER JOIN "
				+ myDB
				+ ".sys.tables tab1 "
				+ "    ON tab1.object_id = chc.parent_object_id "
				+ "INNER JOIN "
				+ myDB
				+ ".sys.schemas sch "
				+ "    ON tab1.schema_id = sch.schema_id "
				+ "INNER JOIN "
				+ myDB
				+ ".sys.columns col1 "
				+ "    ON col1.column_id = parent_column_id AND col1.object_id = tab1.object_id "
				+ "WHERE obj.name = ?";

		try (PreparedStatement ps = myConn.prepareStatement(sqlStr,
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
			// ps.setString(1, constraint.getConstraintSchema());
			ps.setString(1, constraint.toString());

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.first()) {
					constraint.setConstraintColumn(rs
							.getString("CONSTRAINT_COLUMN"));

					constraint
							.setConstraintClause(rs.getString("CHECK_CLAUSE"));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Logger.debugLogger.debug(ex);
		}

		return constraint;

	}

	/*
	 * Extract constraints related to the passed schema 
	 * @schema , schema object to be extracted 
	 * @retrun extracted schema object with constraints
	 */
	public IConstraint getContraintDetails(IConstraint cons) {

		IConstraint constraint = cons;

		String sqlStr = "SELECT " + "COLUMN_NAME "
				+ "FROM information_schema.CONSTRAINT_COLUMN_USAGE "
				+ "WHERE TABLE_SCHEMA = ? " + "AND TABLE_NAME = ? "
				+ "AND CONSTRAINT_SCHEMA = ? " + "AND CONSTRAINT_NAME = ? ";

		try (PreparedStatement ps = myConn.prepareStatement(sqlStr)) {
			ps.setString(1, constraint.getTableSchema().toString());
			ps.setString(2, constraint.getTable().toString());
			ps.setString(3, constraint.getConstraintSchema());
			ps.setString(4, constraint.toString());

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					constraint.addColumn(rs.getString("COLUMN_NAME"));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Logger.errorLogger.catching(ex);
		}

		return constraint;

	}

	/*
	 * Extract constraints related to the passed schema 
	 * @schema , schema object to be extracted 
	 * @retrun extracted schema object with constraints
	 */
	public IFKConstraint getFKContraintDetails(IFKConstraint cons) {
		IFKConstraint constraint = cons;
		// this query is taken form
		// http://stackoverflow.com/questions/483193/how-can-i-list-all-foreign-keys-referencing-a-given-table-in-sql-server
		String sqlStr = "SELECT  col1.name AS [COLUMN_NAME], "
				+ " tab2.name AS [REF_TABLE_NAME],"
				+ " col2.name AS [REF_COL_NAME],"
				+ " c.MATCH_OPTION , c.UPDATE_RULE , c.DELETE_RULE , c.UNIQUE_CONSTRAINT_SCHEMA as [REF_TABLE_SCHEMA]  "
				+ " FROM "
				+ myDB
				+ ".sys.foreign_key_columns fkc"
				+ " INNER JOIN "
				+ myDB
				+ ".sys.objects obj"
				+ " ON obj.object_id = fkc.constraint_object_id and obj.name = ?"
				+ " INNER JOIN "
				+ myDB
				+ ".sys.tables tab1"
				+ " ON tab1.object_id = fkc.parent_object_id"
				+ " INNER JOIN "
				+ myDB
				+ ".sys.schemas sch"
				+ " ON tab1.schema_id = sch.schema_id"
				+ " INNER JOIN "
				+ myDB
				+ ".sys.columns col1"
				+ " ON col1.column_id = parent_column_id AND col1.object_id = tab1.object_id"
				+ " INNER JOIN "
				+ myDB
				+ ".sys.tables tab2"
				+ " ON tab2.object_id = fkc.referenced_object_id"
				+ " INNER JOIN "
				+ myDB
				+ ".sys.columns col2"
				+ " ON col2.column_id = referenced_column_id AND col2.object_id = tab2.object_id"
				+ " Inner join "
				+ myDB
				+ ".INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS c  "
				+ " on c.CONSTRAINT_NAME = ?";

		try (PreparedStatement ps = myConn.prepareStatement(sqlStr,
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
			ps.setString(1, constraint.toString());
			ps.setString(2, constraint.toString());

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.first()) {
					constraint.addColumn(rs.getString("COLUMN_NAME"));
					constraint.setRefTableSchema(rs
							.getString("REF_TABLE_SCHEMA"));
					constraint.setRefTableName(rs.getString("REF_TABLE_NAME"));
					constraint.addRefColum(rs.getString("REF_COL_NAME"));
					constraint.setMatchOption(rs.getString("MATCH_OPTION"));
					constraint.setUpdateRule(rs.getString("UPDATE_RULE"));
					constraint.setDeleteRule(rs.getString("DELETE_RULE"));
				}
				while (rs.next()) {
					constraint.addColumn(rs.getString("COLUMN_NAME"));
					((IFKConstraint) constraint).addRefColum(rs
							.getString("REF_COL_NAME"));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Logger.errorLogger.catching(ex);
		}

		return constraint;
	}

	
	public ISchema extractViews(ISchema schema) {
		Stopwatch timer = Stopwatch.createStarted();
		// views
		String sqlStr = "SELECT  TABLE_NAME, VIEW_DEFINITION,"
				+ "CHECK_OPTION,IS_UPDATABLE "
				+ "FROM information_schema.views " + "WHERE TABLE_SCHEMA = ?";

		try (PreparedStatement st = myConn.prepareStatement(sqlStr)) {
			st.setString(1, schema.toString());

			try (ResultSet rs = st.executeQuery()) {
				while (rs.next()) {
					IView view = new View(schema.toString(),
							rs.getString("TABLE_NAME"),
							rs.getString("VIEW_DEFINITION"),
							rs.getString("IS_UPDATABLE"),
							(rs.getString("CHECK_OPTION").equalsIgnoreCase(
									"none") ? false : true));
					schema.getViews().add(view);

				}

				// rs.close();
			}
			// st.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Logger.errorLogger.catching(e);
		} finally {
			Logger.infoLogger.info("Total time for extracting "
					+ schema.getViews().size() + " views  from " + schema
					+ ": " + timer.stop());
		}
		return schema;
	}

	/*
	 * Extract sequences related to the passed schema 
	 * @schema , schema object to be extracted 
	 * @retrun extracted schema object with sequences
	 */
	public ISchema extractSequences(ISchema schema) {
		Stopwatch timer = Stopwatch.createStarted();
		// sequences
		String sqlStr = "SELECT" + " a.SEQUENCE_SCHEMA, a.SEQUENCE_NAME,"
				+ " TRY_CONVERT(varchar, a.START_VALUE) AS START_VALUE ,"
				+ " TRY_CONVERT(varchar ,a.MAXIMUM_VALUE) AS MAXIMUM_VALUE,"
				+ " TRY_CONVERT(varchar,a.MINIMUM_VALUE) AS MINIMUM_VALUE,"
				+ " TRY_CONVERT(varchar, a.INCREMENT) AS INCREMENT, "
				+ " a.CYCLE_OPTION ,b.cache_size " + " FROM " + myDB
				+ ".INFORMATION_SCHEMA.SEQUENCES a " + " INNER JOIN " + myDB
				+ ".sys.sequences b " + " ON a.SEQUENCE_NAME = b.name "
				+ " AND SCHEMA_NAME(b.schema_id) = a.SEQUENCE_SCHEMA "
				+ " AND a.SEQUENCE_SCHEMA = ? ";

		try (PreparedStatement st = myConn.prepareStatement(sqlStr)) {
			st.setString(1, schema.toString());

			try (ResultSet rs = st.executeQuery()) {
				while (rs.next()) {
					ISequence seq = new Sequence(
							rs.getString("SEQUENCE_SCHEMA"),
							rs.getString("SEQUENCE_NAME"));
					seq.setCacheSize(rs.getInt("cache_size"));
					seq.setMaxValue(rs.getString("MAXIMUM_VALUE"));
					seq.setMinValue(rs.getString("MINIMUM_VALUE"));
					seq.setSeqSchemaName(rs.getString("SEQUENCE_SCHEMA"));
					seq.setStartValue(rs.getString("START_VALUE"));
					seq.isCyclic(rs.getInt("CYCLE_OPTION"));
					seq.setIncrementBy(rs.getString("INCREMENT"));

					schema.getSequences().add(seq);

				}
				
			}
		
		} catch (SQLException e) {
			Logger.errorLogger.catching(e);
			e.printStackTrace();
		} finally {
			Logger.infoLogger.info("Total time for extracting "
					+ schema.getSequences().size() + " sequences  from "
					+ schema + ": " + timer.stop());
		}
		return schema;
	}
}
