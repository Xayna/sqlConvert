package demo.metamodel.IModel;

import java.util.List;

public interface IConstraint extends ICommon{
	
	public String getConstraintSchema();

	public ISchema getTableSchema();

	public ITable getTable();

	public String getConstraintType();

	public boolean isDeferable ();
	
	public boolean isInitiallyDefered ();
	
	public List<IColumn> getcolNames();

	public void addColumn(String colName);

}
