package demo.metamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import demo.metamodel.IModel.IColumn;
import demo.metamodel.IModel.IFKConstraint;
import demo.metamodel.IModel.ISchema;
import demo.metamodel.IModel.ITable;

public class FKConstraint extends Constraint implements IFKConstraint {
	private final static String NO_ACTION = "NO ACTION";

	private String refTableSchema;
	private String refTableName;
	private List<String> refColNames;

	private String MatchOption;
	private String updateRule;
	private String deleteRule;

	public FKConstraint(String consSchema, String consName, String consType,
			ISchema tableSchema, ITable table, boolean deferable,
			boolean initiallyDefered) {
		super(consSchema, consName, consType, tableSchema, table, deferable,
				initiallyDefered);
		refColNames = new ArrayList<String>();
	}

	@Override
	public List<String> getRefColNames() {
		return refColNames;
	}

	@Override
	public void setRefColNames(List<String> refColNames) {
		this.refColNames = refColNames;
	}

	@Override
	public void setRefTableSchema(String refTableSchema) {
		this.refTableSchema = refTableSchema;
	}

	@Override
	public void setRefTableName(String refTableName) {
		this.refTableName = refTableName;
	}

	@Override
	public String getRefTableSchema() {
		return refTableSchema;
	}

	@Override
	public String getRefTableName() {
		return refTableName;
	}

	@Override
	public List<String> getRefColName() {
		return refColNames;
	}

	@Override
	public void addRefColum(String colName) {
		this.refColNames.add(colName);
	}

	@Override
	public String getMatchOption() {
		return MatchOption;
	}

	@Override
	public void setMatchOption(String matchOption) {
		MatchOption = matchOption;
	}

	@Override
	public String getUpdateRule() {
		return updateRule;
	}

	@Override
	public void setUpdateRule(String updateRule) {
		this.updateRule = updateRule;
	}

	@Override
	public String getDeleteRule() {
		return deleteRule;
	}

	@Override
	public void setDeleteRule(String deleteRule) {
		this.deleteRule = deleteRule;
	}

	@Override
	public String toSQL() {
		String columns = getcolNames().stream().map(IColumn::toString)
				.collect(Collectors.joining(","));
		String refCols = getRefColName().stream().collect(
				Collectors.joining(","));

		String sql = "ALTER TABLE " + getTableSchema() + "."
				+ getTable() + " ADD CONSTRAINT " + toString() + " "
				+ getConstraintType() + "(" + columns + ") REFERENCES "
				+ getRefTableSchema() + "." + getRefTableName() + "(" 
				+ refCols + ")";

		if (updateRule != null
				&& !updateRule.trim().equalsIgnoreCase(NO_ACTION))
			sql += " ON UPDATE " + updateRule;

		if (deleteRule != null
				&& !deleteRule.trim().equalsIgnoreCase(NO_ACTION))
			sql += " ON DELETE " + deleteRule;
		return sql;
	}
}
