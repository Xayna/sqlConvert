package demo.metamodel.IModel;

import java.util.List;

public interface IDatabase extends ICommon{
	
	
	public List<ISchema> getSchemas();

}
