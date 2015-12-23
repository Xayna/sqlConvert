package demo.metamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import demo.metamodel.IModel.IColumn;
import demo.metamodel.IModel.IConstraint;
import demo.metamodel.IModel.ISchema;
import demo.metamodel.IModel.ITable;
import demo.util.Helper;
import demo.util.TableType;

public class Table implements ITable {

	private ISchema schema;
	private String tableName;
	private String tableType;
	private List<IColumn> columns;
	private List<IConstraint> constraints;

	public Table() {
		super();
		columns = new ArrayList<IColumn>();
	}

	public Table(ISchema schema, String tableName, String tableType) {
		if (Helper.hasSpace(tableName) || Helper.isKeyword(tableName)) {
			tableName = "\"" + tableName + "\"";
		}
		this.schema = schema;
		this.tableName = tableName;
		this.tableType = TableType.valueOf(
				tableType.toUpperCase().replace(" ", "")).toString();

		columns = new ArrayList<IColumn>();
		constraints = new ArrayList<IConstraint>();
	}

	@Override
	public ISchema getSchema() {
		return schema;
	}

	@Override
	public String toString() {
		return tableName;
	}

	@Override
	public String getTableType() {
		return tableType;
	}

	@Override
	public List<IColumn> getColumns() {
		return columns;
	}

	@Override
	public void addColumn(IColumn col) {
		columns.add(col);

	}

	@Override
	public void addColumnsList(List<IColumn> colList) {
		columns.addAll(colList);

	}

	@Override
	public void addConstriant(IConstraint constraint) {
		constraints.add(constraint);

	}

	@Override
	public void addConstraintsList(List<IConstraint> constList) {
		constraints.addAll(constList);

	}

	public String toSQL() {
		String columnsNames = columns.stream().map(IColumn::toSQL)
				.collect(Collectors.joining(","));
		String sql = "CREATE TABLE " + getSchema() + "." + tableName
				+ "(" + columnsNames + ")";
		return sql;

	}

	@Override
	public List<IConstraint> getConstraints() {
		return constraints;
	}

}
