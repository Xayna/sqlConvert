package demo.metamodel.IModel;

public interface ISequence extends ICommon{
	
	public void setSeqSchemaName (String schemaName);
	
	public void setSeqName (String seqName);
	
	public void setStartValue (String value);
	
	public void setMaxValue (String value);
	
	public void setMinValue (String value);
	
	public void setIncrementBy(String value);
	
	public void isCyclic(int cyclic);
	
	public void setCacheSize(int size);
	
}
