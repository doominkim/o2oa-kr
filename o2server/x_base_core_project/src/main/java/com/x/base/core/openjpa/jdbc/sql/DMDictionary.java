package com.x.base.core.openjpa.jdbc.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import org.apache.openjpa.jdbc.identifier.DBIdentifier;
import org.apache.openjpa.jdbc.identifier.DBIdentifier.DBIdentifierType;
import org.apache.openjpa.jdbc.schema.Column;
import org.apache.openjpa.jdbc.sql.DBDictionary;
import org.apache.openjpa.jdbc.sql.SQLBuffer;
import org.apache.openjpa.lib.util.Localizer;

public class DMDictionary extends DBDictionary {

	public static final String VENDOR_DAMENG = "dameng";

	private static final Localizer _loc = Localizer.forPackage(DMDictionary.class);

	private String schemaCase = "preserve";

	public boolean uniqueIdentifierAsVarbinary = true;

	/**
	 * 다몽 INTEGER/INT JDBC 메타데이터는 보통 COLUMN_SIZE = 10을 반환하지만 OpenJPA 매핑 측 고정 정수 열은 보통 size = 0.
	 *
	 * 활성화하면 데이터베이스 메타데이터를 읽은 후 고정 정수 열 size를 0으로 정규화.
	 */
	public boolean normalizeFixedIntegerSize = true;

	/**
	 * 다몽 JDBC 메타데이터는 열 이름을 "xorderColumn"으로 반환할 수 있지만 OpenJPA 매핑 측은 xorderColumn.
	 *
	 * 활성화하면 schema metadata 리플렉션 경로에서만 바깥 구분자를 제거.
	 */
	public boolean normalizeMetadataIdentifierDelimiters = true;

	public void setNormalizeFixedIntegerSize(boolean normalizeFixedIntegerSize) {
		this.normalizeFixedIntegerSize = normalizeFixedIntegerSize;
	}

	public void setNormalizeMetadataIdentifierDelimiters(boolean normalizeMetadataIdentifierDelimiters) {
		this.normalizeMetadataIdentifierDelimiters = normalizeMetadataIdentifierDelimiters;
	}

	public DMDictionary() {
		this.platform = "dameng";
		this.validationSQL = "SELECT GETDATE()";
		this.supportsAutoAssign = true;
		this.autoAssignClause = "IDENTITY";
		this.lastGeneratedKeyQuery = "SELECT @@IDENTITY";
		this.nextSequenceQuery = "SELECT {0}.NEXTVAL";
		this.integerTypeName = "INT";
		this.substringFunctionName = "SUBSTR";

		this.reservedWordSet.addAll(Arrays.asList(new String[] { "ABORT", "ABSOLUTE", "ABSTRACT", "ACROSS", "ACTION",
				"ADD", "AUDIT", "ADMIN", "AFTER", "ALL", "ALLOW_DATETIME", "ALLOW_IP", "ALTER", "ANALYZE", "AND", "ANY",
				"ARCHIVEDIR", "ARCHIVELOG", "ARCHIVESTYLE", "ARRAY", "ARRAYLEN", "AS", "ASC", "ASSIGN", "AT", "ATTACH",
				"AUTHORIZATION", "AUTO", "AUTOEXTEND", "AVG", "BACKUP", "BACKUPDIR", "BACKUPINFO", "BAKFILE", "BASE",
				"BEFORE", "BEGIN", "BETWEEN", "BIGDATEDIFF", "BIGINT", "BINARY", "BIT", "BITMAP", "BLOB", "BLOCK",
				"BOOL", "BOOLEAN", "BOTH", "BOUNDARY", "BRANCH", "BREAK", "BSTRING", "BTREE", "BY", "BYTE", "CACHE",
				"CALL", "CASCADE", "CASCADED", "CASE", "CAST", "CATALOG", "CATCH", "CHAIN", "CHAR", "CHARACTER",
				"CHECK", "CIPHER", "CLASS", "CLOB", "CLOSE", "CLUSTER", "CLUSTERBTR", "COLUMN", "COMMENT", "COMMIT",
				"COMMITTED", "COMMITWORK", "COMPILE", "COMPRESS", "COMPRESSED", "CONNECT", "CONNECT_BY_IS_CYCLE",
				"CONNECT_BY_IS_LEAF", "CONNECT_BY_ROOT", "CONNECT_IDLE_TIME", "CONST", "CONSTANT", "CONSER_OP",
				"CONSTRAINT", "CONTAINS", "CONTEXT", "CONTINUE", "CONVERT", "COUNT", "CPU_REF_CALL", "CPU_REF_SESSION",
				"CREATE", "CROSS", "CRYPTO", "CTLFILE", "CUBE", "CURRENT", "CURSOR", "CYCLE", "DANGLING", "DATABASE",
				"DATAFILE", "DATE", "DATEADD", "DATEDIFF", "DATEPART", "DATETIME", "DAY", "DBFILE", "DEBUG", "DEC",
				"DECIMAL", "DECLARE", "DECODE", "DEFAULT", "DEFERRABLE", "DELETE", "DELETING", "DEREF", "DESC",
				"DETACH", "DISABLE", "DISCONNECT", "DISKSPACE", "DISTINCT", "DISTRIBUTED", "DO", "DOUBLE", "DOWN",
				"DROP", "EACH", "ELSE", "ELSEIF", "ENABLE", "ENCRYPT", "ENCRYPTION", "END", "EQU", "ERROR", "ESCAPE",
				"EVENTINFO", "EXCEPT", "EXCEPTION", "EXCHANGE", "EXCLUSIVE", "EXECUTE", "EXISTS", "EXIT", "EXPLAIN",
				"EXTERN", "EXTERNAL", "EXTERNALLY", "EXTRACT", "FAILED_LOGIN_ATTEMPS", "FALSE", "FETCH", "FILEGROUP",
				"FILLFACTOR", "FINALLY", "FIRST", "FLOAT", "FOR", "FORCE", "FOREIGN", "FREQUENCE", "FROM", "FULL",
				"FUNCTION", "FOLLOWING", "GET", "GLOBAL", "GOTO", "GRANT", "GROUP GROUPING", "HASH", "HAVING",
				"HEXTORAW", "HOUR", "IDENTIFIED", "IDENTITY", "IDENTITY_INSERT", "IF", "IMAGE", "IMMEDIATE", "IN",
				"INCREASE", "INCREMENT", "INDEX", "INITIAL", "INITIALLY", "INNER", "INNERID", "INSERT", "INSERTING",
				"INSTEAD", "INT", "INTEGER", "INTENT", "INTERNAL", "INTERSECT", "INTERVAL", "INTO", "IS", "ISOLATION",
				"JAVA", "JOIN", "KEY", "LABEL", "LAST", "LEAD", "LEFT", "LESS", "LEVEL", "LEXER", "LIKE", "LIMIT",
				"LINK", "LIST", "LOB", "LOCAL", "LOCK", "LOG", "LOGFILE", "LOGIN", "LOGOUT", "LONG", "LONGVARBINARY",
				"LONGVARCHAR", "LOOP", "LP_OP", "LT_BINTEGER", "LT_BIGINTEGER", "LT_BITSTRING", "LT_DECIMAL",
				"LT_GLOBAL_VAR", "LT_IDENTIFIER", "LT_INTEGER", "LP_REAL", "LT_STRING", "MANUAL", "MAP", "MATCH",
				"MATCHED", "MAX", "MAXSIZE", "MAXVALUE", "MEMBER", "MEN_SPACE", "MERGE", "MIN", "MINEXTENTS", "MINUS",
				"MINUTE", "MINVALUE", "MODE", "MODIFY", "MONEY", "MONTH", "MOUNT", "NATURAL", "NEW", "NEXT", "NO",
				"NOARCHIVELOG", "NOAUDIT", "NOBRANCH", "NOCACHE", "NOCYCLE", "NOMAXVALUE", "NOMINVALUE", "NONE",
				"NOORDER", "NORMAL", "NOSALT", "NOT", "NOT_ALLOW_DATETIME", "NOT_ALLOW_IP", "NOWAIT", "NULL", "",
				"NUMBER", "NUMERIC", "OBJECT", "OF", "OFF", "OFFLINE", "OFFSET", "OLD", "ON", "ONCE", "ONLINE", "ONLY",
				"OP_SHIFT_LERT", "OP_SHIFT_RIGHT", " OPEN", "OPTION", "OR", "ORDER", "OUT", "OUTER", "OVER ",
				"OVERLAPS", "OVERRIDE", "PACKAGE", "PACKAGE_BODY", "PAGE", "PARTIAL", "PARTITION", "PARTITIONS",
				"PASSWORD_GRACE_TIME", "PASSWORD_LIFE_TIME", "PASSWORD_LOCK_TIME", "PASSWORD_POLICY",
				"PASSWORD_REUSE_MAX", "PASSWORD_REUSE_TIME", "PENDANT", "PERCENT", "PRECEDING", "PRECISION", "PRESERVE",
				"PRIMARY", "PRINT", "PRIOR", "PRIVATE", "PRIVILEGES", "PROCEDURE", "PROTECTED", "PT_FOUND", "PT_ISOPEN",
				"PT_NOFOUND", "PT_ROWCOUNT", "PT_ROWTYPE", "PUBLIC", "PUT", "RAISE", "RANGE", "RAWTOHEX", "READ",
				"READ_PER_CALL", "READ_PER_SESSION", "READONLY", "REAL", "REBUILD", "RECORD", "REF", "REFERENCES",
				"REFERENCING", "RELATED", "RELATIVE", "RENAME", "REPEAT", "REPEATABLE", "REPLACE", "REPLICATE",
				"RESIZE", "RESTORE", "RESTRICT", "RETURN", "RETURNING", "REVERSE", "REVOKE", "RIGHT", "ROLE",
				"ROLLBACK", "ROLLFILE", "ROLLUP", "ROOT", "ROW", "ROWCOUNT", "ROWID", "ROWNUM", "ROWS", "RULE", "SALT",
				"SAVEPOINT", "SBYTE", "SCHEMA", "SCOPE", "SEALED", "SECTION", "SECOND", "SELECT", "SELSTAR", "SEQUENCE",
				"SERERR", "SERIALIZABLE", "SERVER", "SESSION_PER_USER", "SET", "SETS", "SHARE", "SHORT", "SHUTDOWN",
				"SIBLINGS", "SIZE", "SIZEOF", "SMALLINT", "SNAPSHOT", "SOME", "SOUND", "SPLIT", "SQL", "STANDBY",
				"START_WITH", "STARTUP", "STATEMENT", "STATIC", "STAT", "STDDEV", "STORAGE", "STORE", "STRING",
				"STRUCT", "STYLE", "SUBSTRING", "SUCCESSFUL", "SUM", "SUSPEND", "SWITCH", "SYNC", "SYNONYM",
				"SYS_CONNECT_BY_PATH", "TABLE", "TABLESPACE", "TEMPORARY", "TEXT", "THAN", "THEN", "THROW", "TIES",
				"TIME", "TIMER", "TIMES", "TIMESTAMP", "TIMESTAMPADD", "TIMESTAMPDIFF", "TINYINT", "TO",
				"TOO_MANY_ROWS", "TOP", "TRAIL", "TRANSACTION", "TRANSACTIONAL", "TRIGGER", "TRIGGERS", "TRIM", "TRUE",
				"TRUNCATE", "TRUNCSIZE", "TRY", "TYPE", "TYPE_BODY", "TYPEOF", "UINT", "ULONG", "UNBOUNDED",
				"UNCOMMITTED", "UNDER", "UNION", "UNIQUE", "UNLIMITED", "UNSAFE", "UNTIL", "UP", "UPDATE", "UPDATING",
				"USER", "USHORT", "USING", "VALUE", "VALUES", "VARBINARY", "VARCHAR", "VARCHAR2", "VARIANCE", "VARYING",
				"VERIFY", "VERTICAL", "VIEW", "VIRTUAL", "VOID", "VOLATILE", "VSIZE", "WEEK", "WHEN", "WHENEVER",
				"WHERE", "WHILE", "WITH", "WORK", "WRAPPED", "WRITE", "YEAR", "ZONE" }));

		this.systemSchemaSet.addAll(Arrays.asList(new String[] { "CTISYS", "SYS", "SYSDBA", "SYSSSO", "SYSAUDITOR" }));

		this.fixedSizeTypeNameSet
				.addAll(Arrays.asList(new String[] { "IMAGE", "TEXT", "DATETIME", "LONGVARBINARY", "LONGVARCHAR" }));

		/*
		 * 다몽 고정 정수/불리언 유형: 1. DDL 생성 시 길이를 붙이지 않음. 2. newColumn(...)과 함께 Existing size 10 vs
		 * Given size 0의 가짜 차이 제거.
		 */
		this.fixedSizeTypeNameSet.addAll(Arrays.asList(
				new String[] { "INT", "INTEGER", "BIGINT", "SMALLINT", "TINYINT", "BYTE", "BIT", "BOOL", "BOOLEAN" }));

		this.supportsDeferredConstraints = true;
		this.supportsSelectEndIndex = true;
	}

	protected void appendSelectRange(SQLBuffer buf, long start, long end, boolean subselect) {
		buf.append(" LIMIT ").appendValue(start).append(", ").appendValue(end - start);
	}

	public void connectedConfiguration(Connection conn) throws SQLException {
		super.connectedConfiguration(conn);

		boolean requiresWarnings = true;
		DatabaseMetaData meta = conn.getMetaData();
		String driverName = meta.getDriverName();
		String url = meta.getURL();

		if (this.driverVendor == null) {
			if ((driverName != null) && (driverName.equalsIgnoreCase("dm.jdbc.driver.DmDriver"))) {
				this.driverVendor = "Dm7JdbcDriver";
				if ((url != null) && (url.startsWith("jdbc:dm://"))) {
					requiresWarnings = false;
				}
			} else {
				this.driverVendor = "other";
			}
		}

		if (("Dm7JdbcDriver".equalsIgnoreCase(this.driverVendor)) && (requiresWarnings)) {
			this.log.warn(_loc.get("Dm Jdbc connection", url));
		}
	}

	/**
	 * OpenJPA가 데이터베이스 metadata 이름을 DBIdentifier로 변환할 때 여기를 거침.
	 *
	 * 다몽 JDBC metadata는 때때로 "xorderColumn" ""xorderColumn"" 반환
	 *
	 * OpenJPA 매핑 측은 보통 xorderColumn
	 *
	 * 정리하지 않으면 schema compare에서 둘이 호환되지 않는다고 판단함.
	 */
	@Override
	public DBIdentifier fromDBName(String name, DBIdentifierType type) {
		if (this.normalizeMetadataIdentifierDelimiters) {
			String cleaned = stripMetadataIdentifierDelimiters(name);
			if (cleaned != null && name != null && !cleaned.equals(name)) {
				DBIdentifier id = DBIdentifier.newIdentifier(cleaned, type, false);
				id.setIgnoreCase(true);
				return id;
			}
		}
		return super.fromDBName(name, type);
	}

	/**
	 * OpenJPA가 DatabaseMetaData.getColumns(...) 결과를 읽을 때 Column을 생성함.
	 *
	 * 여기서 일괄 수정: 1. 열 이름/테이블 이름/schema/typeName 바깥 큰따옴표. 2. 다몽 INT/INTEGER/BIGINT 등 고정 정수 유형
	 * size。
	 */
	@Override
	protected Column newColumn(ResultSet colMeta) throws SQLException {
		Column col = super.newColumn(colMeta);
		normalizeReflectedColumn(col);
		return col;
	}

	private void normalizeReflectedColumn(Column col) {
		if (col == null) {
			return;
		}

		if (this.normalizeMetadataIdentifierDelimiters) {
			col.setSchemaIdentifier(stripIdentifier(col.getSchemaIdentifier()));
			col.setTableIdentifier(stripIdentifier(col.getTableIdentifier()));
			col.setIdentifier(stripIdentifier(col.getIdentifier()));
			col.setTypeIdentifier(stripIdentifier(col.getTypeIdentifier()));
		}

		String typeName = normalizedTypeName(col);

		/*
		 * 다몽 드라이버가 INT/BOOL 등을 다른 호환 유형으로 보고하면 여기서 JDBC 표준 유형으로 통일하여 OpenJPA의 유형 비교를 쉽게 함.
		 */
		if ("INT".equals(typeName) || "INTEGER".equals(typeName)) {
			col.setType(Types.INTEGER);
		} else if ("BIGINT".equals(typeName)) {
			col.setType(Types.BIGINT);
		} else if ("SMALLINT".equals(typeName)) {
			col.setType(Types.SMALLINT);
		} else if ("TINYINT".equals(typeName) || "BYTE".equals(typeName)) {
			col.setType(Types.TINYINT);
		} else if ("BIT".equals(typeName) || "BOOL".equals(typeName) || "BOOLEAN".equals(typeName)) {
			col.setType(Types.BIT);
		}

		if (this.normalizeFixedIntegerSize && isFixedIntegerColumn(col, typeName)) {
			/*
			 * 핵심 수정:
			 *
			 * 다몽 metadata: INTEGER / INT Size 10
			 *
			 * OpenJPA mapping: INTEGER / INT Size 0
			 *
			 * 정규화 후 방지: Existing column Size: 10 Given column Size: 0
			 */
			col.setSize(0);
			col.setDecimalDigits(0);
		}
	}

	private DBIdentifier stripIdentifier(DBIdentifier id) {
		if (DBIdentifier.isNull(id)) {
			return id;
		}

		String name = id.getName();
		String cleaned = stripMetadataIdentifierDelimiters(name);

		if (cleaned == null || cleaned.length() == 0) {
			return id;
		}

		/*
		 * 텍스트가 같아 보여도 DBIdentifier 자체가 delimited 상태이면 비 delimited로 재구성하여 schema compare
		 * 시 "x"와 x를 다르게 취급하는 것을 방지.
		 */
		if (!cleaned.equals(name) || id.isDelimited()) {
			DBIdentifier normalized = DBIdentifier.newIdentifier(cleaned, id.getType(), false);
			normalized.setIgnoreCase(true);
			return normalized;
		}

		return id;
	}

	/**
	 * JDBC metadata가 반환한 식별자의 바깥 구분자 정리.
	 *
	 * 지원: "x" -> x ""x"" -> x "X"."T" -> X.T `x` -> x [x] -> x
	 */
	private String stripMetadataIdentifierDelimiters(String name) {
		if (name == null) {
			return null;
		}

		String trimmed = name.trim();
		if (trimmed.length() == 0) {
			return trimmed;
		}

		String[] parts = trimmed.split("\\.");
		StringBuilder result = new StringBuilder();

		for (int i = 0; i < parts.length; i++) {
			if (i > 0) {
				result.append('.');
			}
			result.append(stripOneIdentifierSegment(parts[i]));
		}

		return result.toString();
	}

	private String stripOneIdentifierSegment(String segment) {
		String s = segment == null ? null : segment.trim();

		if (s == null) {
			return null;
		}

		boolean changed;

		do {
			changed = false;

			if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
				s = s.substring(1, s.length() - 1);
				changed = true;
			} else if (s.length() >= 2 && s.startsWith("`") && s.endsWith("`")) {
				s = s.substring(1, s.length() - 1);
				changed = true;
			} else if (s.length() >= 2 && s.startsWith("[") && s.endsWith("]")) {
				s = s.substring(1, s.length() - 1);
				changed = true;
			}
		} while (changed && s.length() >= 2);

		return s;
	}

	private boolean isFixedIntegerColumn(Column col, String typeName) {
		switch (col.getType()) {
		case Types.INTEGER:
		case Types.BIGINT:
		case Types.SMALLINT:
		case Types.TINYINT:
		case Types.BIT:
		case Types.BOOLEAN:
			return true;
		default:
			return "INT".equals(typeName) || "INTEGER".equals(typeName) || "BIGINT".equals(typeName)
					|| "SMALLINT".equals(typeName) || "TINYINT".equals(typeName) || "BYTE".equals(typeName)
					|| "BIT".equals(typeName) || "BOOL".equals(typeName) || "BOOLEAN".equals(typeName);
		}
	}

	private String normalizedTypeName(Column col) {
		String name = null;

		if (col != null && !DBIdentifier.isNull(col.getTypeIdentifier())) {
			name = col.getTypeIdentifier().getName();
		}

		if (name == null && col != null) {
			/*
			 * getTypeName()은 OpenJPA 3.x에서 deprecated로 표시되었지만 fallback으로 사용하면 호환성을 높일 수 있음.
			 */
			name = col.getTypeName();
		}

		if (name == null) {
			return "";
		}

		name = stripMetadataIdentifierDelimiters(name);

		if (name == null) {
			return "";
		}

		int paren = name.indexOf('(');
		if (paren >= 0) {
			name = name.substring(0, paren);
		}

		return name.trim().toUpperCase(Locale.ENGLISH);
	}

	public Date getDate(ResultSet rs, int column) throws SQLException {
		return rs.getDate(column);
	}
}