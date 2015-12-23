package demo.metamodel;

import demo.metamodel.IModel.ISequence;

public class Sequence implements ISequence {

	private String schemaName;
	private String seqName;
	private String startValue;
	private String maxValue;
	private String minValue;
	private String incrementBy;
	private boolean cyclic;
	private int cacheSize ;

	public Sequence(String schemaName, String seqName) {
		this.schemaName = schemaName;
		this.seqName = seqName;
	}

	@Override
	public void setSeqSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	@Override
	public void setSeqName(String seqName) {
		this.schemaName = seqName;
	}

	@Override
	public void setStartValue(String value) {
		this.startValue = value;

	}

	@Override
	public void setMaxValue(String value) {
		this.maxValue = value;
	}

	@Override
	public void setMinValue(String value) {
		this.minValue = value;
	}

	@Override
	public void setIncrementBy(String value) {
		this.incrementBy = value;
	}

	@Override
	public void isCyclic(int cyclic) {
		this.cyclic = (cyclic == 0 ? false : true);
	}
	
	@Override
	public void setCacheSize(int size) {
		this.cacheSize = size;
		}

	@Override
	public String toSQL() {
		String sql = "CREATE SEQUENCE " + schemaName+ "." + seqName ;
		sql += " INCREMENT BY " + incrementBy ;
		sql += " MINVALUE " + minValue;
		sql += " MAXVALUE " + maxValue;
		sql += " START WITH " + startValue ;
		sql += ( cacheSize >0 ? " CACHE " + cacheSize : "");
		sql += (cyclic ? " CYCLE " : " NO CYCLE");
		return sql;
	}

}
