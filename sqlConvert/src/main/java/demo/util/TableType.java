package demo.util;

public enum TableType {

	SYSTEMVIEW {
		@Override
		public String toString ()
		{ 
			return 	"SYSTEM VIEW";
		}
	}
	,
	BASETABLE 
	{
		@Override
		public String toString ()
		{ 
			return 	"BASE TABLE";
		}
	},
	
	VIEW;
	
	
}

