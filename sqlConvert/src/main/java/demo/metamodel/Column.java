package demo.metamodel;

import demo.metamodel.IModel.IColumn;
import demo.metamodel.IModel.ISchema;
import demo.metamodel.IModel.ITable;
import demo.util.DataType;
import demo.util.Helper;

public class Column implements IColumn {

	// MAX column size is represented as -1 in MSSQL which is not valid in
	// Postgres,
	// MAX is relpaced with the maximun column size in postgres
	final static int MAX = 10485760;

	private ISchema schema;
	private ITable table;
	private String columnName;
	private Integer position;
	private Object defualtValue;
	private String dataType;
	private String columnType;
	private boolean nullable;
	private Integer maxCharSize;
	private Integer numaricPrecision;
	private Integer numaricScale;
	private Integer dateTimePrecision;
	private String columnComment;
	private String extra;
	private boolean isIdentity;

	public Column() {
		super();

	}

	@Override
	public ISchema getSchema() {

		return schema;
	}

	@Override
	public ITable getTable() {
		return table;
	}

	@Override
	public String toString() {
		return columnName;
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public Object getDefualtValue() {
		return defualtValue;
	}

	@Override
	public String getDataType() {
		return dataType;
	}

	@Override
	public String getColumnType() {
		return columnType;
	}

	@Override
	public boolean isNullable() {
		return nullable;
	}

	@Override
	public Integer getMaxCharSize() {
		return maxCharSize;
	}

	@Override
	public Integer getNumaricPrecision() {
		return numaricPrecision;
	}

	@Override
	public Integer getNumaricScale() {
		return numaricScale;
	}

	@Override
	public Integer getDateTimePrecision() {
		return dateTimePrecision;
	}

	@Override
	public String getColumnComment() {
		return columnComment;
	}

	@Override
	public String getExtra() {
		return extra;
	}

	@Override
	public void setSchema(ISchema schema) {
		this.schema = schema;
	}

	@Override
	public void setTable(ITable table) {
		this.table = table;
	}

	@Override
	public void setColumnName(String columnName) {
		//quotations are needed if the colum name is a keyword or contains a space
		if (Helper.hasSpace(columnName) || Helper.isKeyword(columnName)) {
			columnName = "\"" + columnName + "\"";
		}
		this.columnName = columnName;
	}

	@Override
	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public void setDefualtValue(Object defualtValue) {
		//default values is surrounded by () in MSSQL but not in postgres 
		this.defualtValue = (defualtValue != null ? defualtValue.toString()
				.replace("(", "").replace(")", "").trim() : defualtValue);
	}

	@Override
	public void setDataType(String dataType) {
		// if the column isIdentitiy column in MSSQL appened it to _IS_IDENTITY
		String type = dataType.toUpperCase().replace(" ", "")
				+ (isIdentity ? "_IS_IDENTITY" : "");
		//map datatype to its equivalent
		this.dataType = (type.equalsIgnoreCase(DataType.ENUM.toString()) ? "zm_"
				+ table
				: DataType.valueOf(type).toString());
		
	}

	@Override
	public void setColumnType(String columnType) {
		// used for create enum type
		this.columnType = columnType;
	}

	@Override
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	@Override
	public void setMaxCharSize(Integer columnSize) {
		this.maxCharSize = (columnSize == -1 ? MAX : columnSize);
	}

	@Override
	public void setNumaricPrecision(Integer numaricPrecision) {
		this.numaricPrecision = numaricPrecision;
	}

	@Override
	public void setNumaricScale(Integer numaricScale) {
		this.numaricScale = numaricScale;
	}

	@Override
	public void setDateTimePrecision(Integer dateTimePrecision) {
		this.dateTimePrecision = dateTimePrecision;
	}

	@Override
	public void setColumnComment(String columnComment) {
		this.columnComment = columnComment;
	}

	@Override
	public void setExtra(String extra) {
		this.extra = extra;
		isIdentity = (extra.equalsIgnoreCase("AUTO_INCREMEANT") ? true : false);
	}

	@Override
	public boolean isIdentity() {
		return isIdentity;
	}

	@Override
	public void setIdentity(boolean identity) {
		this.isIdentity = identity;
	}

	/*
	 * return column SQL string
	 */
	@Override
	public String toSQL() {
		String sql = columnName + " " + dataType;
		if (!dataType.startsWith("zm")) {
			if (maxCharSize != null
					&& maxCharSize != 0
					&& !compare(dataType, DataType.TEXT.toString(),
							DataType.XML.toString(), DataType.BINARY.toString())) {
				sql += "(" + maxCharSize + ")";
			} else if (numaricScale != 0 && numaricPrecision != 0
					&& !compare(dataType, DataType.MONEY.toString())) {
				sql += "(" + numaricPrecision + "," + numaricScale + ")";
			}
		}

		if (!isNullable()) {
			sql += " NOT NULL";
		}
		sql += getDefaultValue(dataType, defualtValue);

		return sql;
	}

	private String getDefaultValue(String dataType, Object value) {
		if (value == null)
			return "";
		if (Helper.isMSSQLFunction(value.toString()))
			return " DEFAULT " + Helper.getMappedFunction(value.toString());
		if (Helper.isNumericType(dataType))
			return " DEFAULT " + value;
		return " DEFAULT '" + value + "'";
	}

	//this to be moved from here and used in mysql implementation for the mapping
	public String dropTypeSQL() {
		return "DROP TYPE  IF EXISTS " + "zm_" + table + " CASCADE";
	}

	public String createTypeSQL() {
		return "CREATE TYPE " + "zm_" + table + " AS " + columnType;
	}

	/*
	 * method to compare a string with  values in a list of strings
	 */
	private boolean compare(String value, String... values) {
		for (String val : values) {
			if (value.equalsIgnoreCase(val))
				return true;
		}
		return false;
	}

}
