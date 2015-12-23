package demo.sqlConvert;

import java.sql.Connection;

import demo.metamodel.IModel.ICKConstraint;
import demo.metamodel.IModel.IConstraint;
import demo.metamodel.IModel.IDatabase;
import demo.metamodel.IModel.IFKConstraint;
import demo.metamodel.IModel.ISchema;
import demo.metamodel.IModel.ITable;

public interface SQLFunctions {

	public IDatabase ExtractDB(String dbName) ;

	public void ExractSchema() ;

	ISchema ExractSchema(ISchema schema);

	ISchema extractTables(ISchema schema) ;

	ITable extractTableColumns(Connection conn, ISchema schema,
			ITable table) ;

	ISchema extractConstraints(ISchema schema) ;

	ICKConstraint getCKContraintDetails(ICKConstraint cons) ;
	
	IConstraint getContraintDetails(IConstraint cons) ;

	IFKConstraint getFKContraintDetails(IFKConstraint cons) ;

	ISchema extractViews(ISchema schema);

	ISchema extractSequences (ISchema schema);

}
