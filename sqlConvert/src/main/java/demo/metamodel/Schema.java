package demo.metamodel;

import java.util.ArrayList;
import java.util.List;

import demo.metamodel.IModel.IColumn;
import demo.metamodel.IModel.IConstraint;
import demo.metamodel.IModel.ISchema;
import demo.metamodel.IModel.ISequence;
import demo.metamodel.IModel.ITable;
import demo.metamodel.IModel.IView;

public class Schema implements ISchema 
{

	
	private String schemaName;
	private List<ITable> tables;
	private List<IConstraint> constraints;
	private List<IConstraint> fkConstraints;
	private List<IColumn> columns ;
	private List<IView> views;
	private List<IConstraint> ckConstraints;
	private List<ISequence> sequences;
	
	public Schema (String schemaName)
	{
		super ();
		this.schemaName = schemaName ;
		tables = new ArrayList<ITable>();
		constraints = new ArrayList<IConstraint>();
		fkConstraints = new ArrayList<IConstraint>();
		ckConstraints = new ArrayList<IConstraint>();
		columns = new ArrayList<IColumn>();
		views = new ArrayList<IView>();
		sequences = new ArrayList<ISequence>();
	}
	
	@Override
	public List<ITable> getTables() {
		return tables;
	}
	
	@Override
	public String toString ()
	{
		return schemaName ;
	}
	
	@Override
	public List<IConstraint> getConstraints() {
	
		return constraints;
	}

	@Override
	public String toSQL() {
		return "DROP SCHEMA IF EXISTS "+ schemaName + " CASCADE  ;"  + "CREATE SCHEMA " +  schemaName ;
	}

	@Override
	public List<IColumn> getColumns() {
		return columns;
	}

	@Override
	public List<IConstraint> getFKConstraints() {
		return fkConstraints;
	}

	@Override
	public List<IView> getViews() {
			return views;
	}

	@Override
	public List<IConstraint> getCKConstraints() {
		return ckConstraints;
	}

	@Override
	public List<ISequence> getSequences() {
			return sequences;
	}

	
	

}
