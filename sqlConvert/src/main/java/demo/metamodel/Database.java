package demo.metamodel;

import java.util.ArrayList;
import java.util.List;

import demo.metamodel.IModel.IDatabase;
import demo.metamodel.IModel.ISchema;

public class Database implements IDatabase {

	private String dbName;
	private List<ISchema> schemas;

	public Database(String dbName) {
		this.dbName = dbName;
		schemas = new ArrayList<ISchema>();
	}

	@Override
	public String toString() {
		return dbName;
	}

	@Override
	public List<ISchema> getSchemas() {
		return schemas;
	}

	@Override
	public String toSQL() {
		return "DROP DATABASE IF EXISTS " + dbName +   ";"
				+ "CREATE DATABASE " + dbName;
	}

}
