package demo.metamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import demo.metamodel.IModel.IColumn;
import demo.metamodel.IModel.IConstraint;
import demo.metamodel.IModel.ISchema;
import demo.metamodel.IModel.ITable;

public class Constraint implements IConstraint {

	public static final String PK = "PRIMARY KEY";
	public static final String FK = "FOREIGN KEY";
	public static final String UNIQUE = "UNIQUE";
	public static final String CHECK = "CHECK";

	public static final String YES = "YES";
	public static final String NO = "NO";

	private String constraintSchema;
	private String constraintName;
	private String constraintType;

	private ISchema tableSchema;
	private ITable table;

	private List<IColumn> colNames;
	private boolean deferable;
	private boolean initiallyDefered;

	public Constraint(String consSchema, String consName, String consType,
			ISchema schema, ITable table, boolean deferable,
			boolean initiallyDefered) {
		this.constraintSchema = consSchema;
		this.constraintName = consName;
		this.constraintType = consType;
		this.table = table;
		this.tableSchema = schema;
		this.initiallyDefered = initiallyDefered;
		this.deferable = deferable;
		colNames = new ArrayList<IColumn>();
	}

	public String getConstraintType() {
		return constraintType;
	}

	@Override
	public String getConstraintSchema() {
		return constraintSchema;
	}

	@Override
	public ISchema getTableSchema() {
		return tableSchema;
	}

	@Override
	public ITable getTable() {
		return table;
	}

	@Override
	public String toString() {
		return constraintName;
	}

	@Override
	public List<IColumn> getcolNames() {
		return colNames;
	}

	@Override
	public void addColumn(String colName) {
		// get the column object from columns list
		List<IColumn> tempCol = table.getColumns().stream()
				.filter(col -> col.toString().equalsIgnoreCase(colName))
				.distinct().collect(Collectors.toList());
		if (tempCol != null && tempCol.size() > 0) {
			colNames.add(tempCol.get(0));
		}

	}

	/*
	 * Alter table sql String
	 */
	
	@Override
	public String toSQL() {

		String columns = "";
		String sql = "ALTER TABLE " + getTableSchema() + "." + getTable()
				+ " ADD ";

		sql += (this.constraintType.equalsIgnoreCase(PK) ? "" : "CONSTRAINT "
				+ toString() + " ");
		sql += constraintType;

		columns = colNames.stream().map(IColumn::toString)
				.collect(Collectors.joining(","));

		return sql + "(" + columns + ")";

	}

	@Override
	public boolean isDeferable() {
		return deferable;
	}

	@Override
	public boolean isInitiallyDefered() {
		return initiallyDefered;
	}

}
