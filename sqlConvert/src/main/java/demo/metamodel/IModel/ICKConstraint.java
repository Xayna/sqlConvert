package demo.metamodel.IModel;

public interface ICKConstraint extends IConstraint {

	public String getConstraintClause () ;
	
	public void setConstraintClause (String clause);
	
	public String getConstraintColumn();
	
	public void setConstraintColumn(String columnName);
}
