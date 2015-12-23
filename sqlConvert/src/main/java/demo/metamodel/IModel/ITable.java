package demo.metamodel.IModel;

import java.util.*;

public interface ITable extends ICommon{

	public ISchema getSchema();
		
	public String getTableType ();
	
	public List<IColumn> getColumns ();
	
	public void addColumn (IColumn col);
	
	public void addColumnsList(List<IColumn> colList);
	
	public void addConstriant(IConstraint constraint) ;

	public void addConstraintsList(List<IConstraint> constList);
	
	public List<IConstraint> getConstraints();
}
