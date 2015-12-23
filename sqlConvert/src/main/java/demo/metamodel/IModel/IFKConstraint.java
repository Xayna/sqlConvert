package demo.metamodel.IModel;

import java.util.List;

public interface IFKConstraint extends IConstraint{

	public List<String> getRefColNames();

	public void setRefColNames(List<String> refColNames) ;

	public void setRefTableSchema(String refTableSchema) ;

	public void setRefTableName(String refTableName);

	public void setMatchOption(String matchOption) ;
	
	public void setUpdateRule(String updateRule) ;
	
	public void setDeleteRule(String deleteRule) ;
	
	public String getMatchOption() ;

	public String getUpdateRule();
	
	public String getDeleteRule();

	public String getRefTableSchema() ;

	public String getRefTableName() ;

	public List<String> getRefColName() ;

	public void addRefColum(String colName);
	
	
}
