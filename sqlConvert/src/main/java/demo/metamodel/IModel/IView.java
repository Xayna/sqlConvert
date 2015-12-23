package demo.metamodel.IModel;

public interface IView extends ICommon{

	public String getSchema ();
	
	public String getViewName ();
	
	public String getViewDefinition ();
	
	public String isUpdatable ();
	
	public boolean isChecked ();
}
