package demo.metamodel.IModel;

public interface IColumn extends ICommon {
	
	public ISchema getSchema();

	public ITable getTable();

	public int getPosition();

	public Object getDefualtValue();

	public String getDataType();

	public String getColumnType();

	public boolean isNullable();
	
	public boolean isIdentity ();

	public Integer getMaxCharSize();

	public Integer getNumaricPrecision();

	public Integer getNumaricScale();

	public String getExtra() ;
	
	// I need to know what is this exactly and see how to use it
	public Integer getDateTimePrecision();

	public String getColumnComment();

	public void setSchema(ISchema schema);

	public void setTable(ITable table);

	public void setColumnName(String columnName);

	public void setPosition(int position);

	public void setDefualtValue(Object defualtValue);

	public void setDataType(String dataType);

	public void setColumnType(String columnType);

	public void setNullable(boolean nullable);

	public void setIdentity (boolean identity);
	
	public void setMaxCharSize(Integer columnSize);

	public void setNumaricPrecision(Integer numaricPrecision);

	public void setNumaricScale(Integer numaricScale);

	public void setDateTimePrecision(Integer dateTimePrecision);

	public void setColumnComment(String columnComment);
	
	public void setExtra(String extra);
		
	public String dropTypeSQL ();
	public String createTypeSQL ();
	
}
