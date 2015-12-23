package demo.metamodel;

import demo.metamodel.IModel.IView;

public class View implements IView{

	
	private String schema;
	private String viewName;
	private String viewDefinition;
	private String updatable;
	private boolean checked;

	
	public View(String schema, String viewName, String viewDefinition,
			String updatable, boolean checked) {
		super();
		this.schema = schema;
		this.viewName = viewName;
		this.viewDefinition = viewDefinition;
		this.updatable = updatable;
		this.checked = checked;
	}

	@Override
	public String getSchema() {
		return schema;
	}

	@Override
	public String getViewName() {
			return viewName;
	}

	@Override
	public String getViewDefinition() {
		return viewDefinition;
	}

	@Override
	public String isUpdatable() {
		return updatable;
	}

	@Override
	public boolean isChecked() {
		return checked;
	}

	@Override
	public String toSQL() {
		// TODO Auto-generated method stub
		return null;
	}

}
