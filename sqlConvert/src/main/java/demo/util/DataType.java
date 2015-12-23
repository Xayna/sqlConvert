package demo.util;

public enum DataType {

	// return boolean if jdbc used for datatransfer
	// return bit if other tools used
	BIT {
		@Override
		public String toString() {
			//return "bit";
			return "boolean";
		}
	},

	TINYINT {
		@Override
		public String toString() {
			return "smallint";
		}
	},

	TINYINT_AUTO_INCREMENT {
		@Override
		public String toString() {
			return "SERIAL";
		}
	},

	TINYINT_IS_IDENTITY {
		@Override
		public String toString() {
			return "SERIAL";
		}
	},

	SMALLINT {
		@Override
		public String toString() {
			return "smallint";
		}
	},

	SMALLINT_AUTO_INCREMENT {
		@Override
		public String toString() {
			return "SERIAL";
		}
	},

	SMALLINT_IS_IDENTITY {
		@Override
		public String toString() {
			return "SERIAL";
		}
	},
	MEDIUMINT {
		@Override
		public String toString() {
			return "SERIAL";
		}
	},

	MEDIUMINT_AUTO_INCREMENT {
		@Override
		public String toString() {
			return "SERIAL";
		}
	},
	INT {
		@Override
		public String toString() {
			return "integer";
		}
	},

	INT_AUTO_INCREMENT {
		@Override
		public String toString() {
			return "SERIAL";
		}
	},

	INT_IS_IDENTITY {
		@Override
		public String toString() {
			return "SERIAL";
		}
	},

	BIGINT {
		@Override
		public String toString() {
			return "bigint";
		}
	},

	BIGINT_AUTO_INCREMENT {
		@Override
		public String toString() {
			return "BIGSERIAL";
		}
	},

	BIGINT_IS_IDENTITY {
		@Override
		public String toString() {
			return "BIGSERIAL";
		}
	},
	DECIMAL {
		@Override
		public String toString() {
			return "decimal";
		}
	},

	NUMERIC {
		@Override
		public String toString() {
			return "numeric";
		}
	},

	MONEY {
		@Override
		public String toString() {
			return "money";
		}
	},
	
	SMALLMONEY{
		@Override
		public String toString() {
			return "money";
		}
	},
	REAL {
		@Override
		public String toString() {
			return "real";
		}
	},

	FLOAT {
		@Override
		public String toString() {
			return "float";
		}
	},
	DOUBLE {
		@Override
		public String toString() {
			return "double precision";
		}
	},

	DOUBLEPRECISION {
		@Override
		public String toString() {
			return "double precision";
		}
	},

	DATETIME {
		@Override
		public String toString() {
			return "timestamp";
		}
	},

	DATETIME2 {
		@Override
		public String toString() {
			return "timestamp";
		}
	},

	SMALLDATETIME {
		@Override
		public String toString() {
			return "timestamp";
		}
	},

	DATETIMEOFFSET {
		@Override
		public String toString() {
			return "timestampz";
		}
	},

	TIMESTAMP {
		@Override
		public String toString() {
			return "timestamp";
		}
	},

	TIME {
		@Override
		public String toString() {
			return "time";
		}
	},

	DATE {
		@Override
		public String toString() {
			return "date";
		}
	},

	YEAR {
		@Override
		public String toString() {
			return "integer";
		}
	},

	SET {
		@Override
		public String toString() {
			return " text array";
		}
	},

	ENUM {
		@Override
		public String toString() {
			return "enum"; // just temporarly to see what I will do with this
							// type conversion
			// return " text array";
		}
	},

	CHAR {
		@Override
		public String toString() {
			return "char";
		}
	},

	NCHAR {
		@Override
		public String toString() {
			return "char";
		}
	},
	VARCHAR {
		@Override
		public String toString() {
			return "varchar";
		}
	},

	NVARCHAR {
		@Override
		public String toString() {
			return "varchar";
		}
	},
	MEDIUMTEXT {
		@Override
		public String toString() {
			return "text";
		}
	},

	TEXT {
		@Override
		public String toString() {
			return "text";
		}
	},

	NTEXT {
		@Override
		public String toString() {
			return "text";
		}
	},
	LONGTEXT {
		@Override
		public String toString() {
			return "text";
		}
	},

	BLOB {
		@Override
		public String toString() {
			return "text";
		}
	},

	LONGBLOB {
		@Override
		public String toString() {
			return "text";
		}
	},

	BINARY {
		@Override
		public String toString() {
			return "bytea";
		}
	},

	VARBINARY {
		@Override
		public String toString() {
			return "bytea";
		}
	},

	IMAGE {
		@Override
		public String toString() {
			return "bytea";
		}
	},

	HIERARCHYID {
		@Override
		public String toString() {
			return "text";
		}
	},

	UNIQUEIDENTIFIER {
		@Override
		public String toString() {
			return "uuid";
		}
	},

	XML {
		@Override
		public String toString() {
			return "xml";
			//return "text";
		}
	},
	
	GEOGRAPHY{
		@Override
		public String toString() {
			return "text";
		}
	}
	,

}
