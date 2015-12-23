package demo.metamodel.IModel;

import java.util.List;

public interface ISchema extends ICommon{
		
	public List<ITable> getTables ();
	
	public List<IConstraint> getConstraints();
	
	public List<IColumn> getColumns();

	public List<IConstraint> getFKConstraints();
	
	public List<IView> getViews();

	public List<IConstraint> getCKConstraints();

	public List<ISequence> getSequences();
}
