package com.x.query.core.express.statement;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 * 데이터 센터 Statement 네이티브 SQL 보안 검사.
 *
 * <p>
 * Statement의 sql과 sqlScript는 최종적으로 그대로 EntityManager.createNativeQuery()에 전달되어 실행되며, 데이터베이스 내장 파일 함수는
 * 서버의 임의 파일을 읽는 데 쓰일 수 있음. 예: H2의 FILE_READ, FILE_WRITE, CSVREAD, CSVWRITE, RUNSCRIPT, CREATE ALIAS,
 * MySQL의 LOAD_FILE, LOAD DATA, PostgreSQL의 pg_read_file, lo_import, Oracle의 UTL_FILE, SQLServer의
 * xp_cmdshell. 데이터베이스 계정이 관리자이면 이 함수들을 모두 쓸 수 있어 데이터베이스 권한만으로는 막을 수 없음.
 *
 * <p>
 * 이 클래스는 실행 전에 "기본 거부" 검사를 한 번 수행하며, 규칙은 다음과 같음.
 * <ol>
 * <li>문은 SELECT, WITH, INSERT, UPDATE, DELETE로만 시작할 수 있으며 DDL, DCL, CALL, SET, SHOW 등은 모두 거부.</li>
 * <li>세미콜론으로 여러 문을 한 번에 제출할 수 없음.</li>
 * <li>쿼리 문에 INTO가 나올 수 없음. H2의 SELECT INTO는 결과를 파일이나 새 테이블에 씀.</li>
 * <li>SQL에 나오는 모든 함수는 {@link #ALLOWED_FUNCTIONS} 화이트리스트에 있어야 하며, 화이트리스트에 없는 함수는 알 수 없는
 * 함수를 포함해 모두 거부하므로 계속 늘어나는 위험 함수 목록을 관리할 필요가 없음.</li>
 * <li>함수 호출 형태가 아닌 위험한 구문(예: LOAD DATA, OPENROWSET, LINK SCHEMA, RUNSCRIPT)에 대해 한 번 더
 * 키워드 차단, 화이트리스트 외 두 번째 방어선.</li>
 * </ol>
 *
 * <p>
 * 검사는 주석, 문자열 상수, 식별자 인용을 제거한 후 수행해 주석이나 상수로 키워드를 쪼개 우회하는 것을 방지하며, 닫히지 않은 주석과
 * 따옴표는 바로 거부.
 *
 * <p>
 * 화이트리스트는 설정 파일이 아닌 코드에 둠. 설정 누락은 무방비와 같기 때문. 업무상 새 데이터베이스 함수가 필요하면 해당
 * 함수가 파일 시스템과 명령 실행에 관여하지 않음을 확인한 후 {@link #ALLOWED_FUNCTIONS}에 추가해야 함.
 */
public final class SqlSafetyChecker {

    /** 문 시작으로 허용되는 키워드. 나머지 DDL, DCL, CALL, SET, SHOW 등은 모두 거부. */
    private static final Set<String> ALLOWED_STATEMENT_STARTS = words("SELECT", "WITH", "INSERT", "UPDATE", "DELETE");

    /**
     * 호출 허용 함수 화이트리스트. 집계, 빈 값과 조건, 문자열, 정규식, 형 변환, 날짜 시간, 수학, 윈도, JSON,
     * 인코딩과 해시 같은 표시용 함수만 포함하며 H2, MySQL, Oracle, PostgreSQL, SQLServer, Dameng, Kingbase의 일반적인 용법을 포괄.
     */
    private static final Set<String> ALLOWED_FUNCTIONS = words(//
            // 집계
            "COUNT", "SUM", "AVG", "MIN", "MAX", "MEDIAN", "GROUPING", "STDDEV", "STDDEV_POP", "STDDEV_SAMP",
            "VARIANCE", "VAR_POP", "VAR_SAMP", "BIT_AND", "BIT_OR", "BIT_XOR", "GROUP_CONCAT", "STRING_AGG",
            "LISTAGG", "WM_CONCAT", "ARRAY_AGG", "ARRAY_TO_STRING", "UNNEST", "JSON_ARRAYAGG", "JSON_OBJECTAGG",
            "XMLAGG", "PERCENTILE_CONT", "PERCENTILE_DISC", "APPROX_COUNT_DISTINCT", //
            // 빈 값과 조건
            "COALESCE", "NVL", "NVL2", "IFNULL", "ISNULL", "NULLIF", "DECODE", "IF", "GREATEST", "LEAST", //
            // 문자열
            "CONCAT", "CONCAT_WS", "SUBSTRING", "SUBSTR", "SUBSTRING_INDEX", "LEFT", "RIGHT", "LENGTH",
            "CHAR_LENGTH", "CHARACTER_LENGTH", "OCTET_LENGTH", "BIT_LENGTH", "DATA_LENGTH", "LOWER", "UPPER",
            "LCASE", "UCASE", "TRIM", "LTRIM", "RTRIM", "BTRIM", "LPAD", "RPAD", "REPLACE", "REVERSE", "INSTR",
            "LOCATE", "POSITION", "ASCII", "CHR", "UNICODE", "SPACE", "REPEAT", "REPLICATE", "ELT", "FIELD",
            "FORMAT", "QUOTE", "SOUNDEX", "DIFFERENCE", "TRANSLATE", "LEVENSHTEIN", "TO_BASE64", "FROM_BASE64", //
            // 정규식
            "REGEXP_LIKE", "REGEXP_REPLACE", "REGEXP_SUBSTR", "REGEXP_INSTR", "REGEXP_COUNT", //
            // 형 변환
            "CAST", "CONVERT", "TRY_CAST", "TRY_CONVERT", "TRY_TO_NUMBER", "TRY_TO_DATE", "TRY_TO_TIMESTAMP",
            "VALIDATE_CONVERSION", "TO_CHAR", "TO_NUMBER", "TO_DATE", "TO_TIMESTAMP", "TO_TIMESTAMP_TZ",
            "TO_CLOB", "TO_BLOB", "TO_BINARY_FLOAT", "TO_BINARY_DOUBLE", "FORMATDATETIME", "PARSEDATETIME", //
            // 날짜와 시간
            "EXTRACT", "YEAR", "QUARTER", "MONTH", "MONTHNAME", "WEEK", "WEEKDAY", "DAY", "DAYNAME", "DAYOFMONTH",
            "DAYOFWEEK", "DAYOFYEAR", "HOUR", "MINUTE", "SECOND", "MILLISECOND", "MICROSECOND", "NANOSECOND",
            "EPOCH", "LAST_DAY", "MAKEDATE", "MAKETIME", "PERIOD_ADD", "PERIOD_DIFF", "TO_DAYS", "FROM_DAYS",
            "FROM_UNIXTIME", "UNIX_TIMESTAMP", "DATE", "TIME", "TIMESTAMP", "DATETIME", "DATEDIFF", "DATEADD",
            "DATESUB", "DATE_ADD", "DATE_SUB", "DATE_FORMAT", "TIME_FORMAT", "DATETIME_FORMAT", "DATETIMEFROMPARTS",
            "TIMESTAMPDIFF", "TIMESTAMPADD", "ADD_MONTHS", "MONTHS_BETWEEN", "NEXT_DAY", "EOMONTH", "TRUNC",
            "TRUNCATE", "NOW", "TODAY", "SYSDATE", "SYSTIMESTAMP", "CURRENT_DATE", "CURRENT_TIME",
            "CURRENT_TIMESTAMP", "LOCALTIME", "LOCALTIMESTAMP", "UTC_DATE", "UTC_TIME", "UTC_TIMESTAMP",
            "FROMTODATETIMEZONE", //
            // 수학
            "ABS", "CEIL", "CEILING", "FLOOR", "ROUND", "MOD", "POW", "POWER", "SQRT", "CBRT", "SIGN", "RAND",
            "RANDOM", "EXP", "LN", "LOG", "LOG2", "LOG10", "SIN", "COS", "TAN", "COT", "ASIN", "ACOS", "ATAN",
            "ATAN2", "SINH", "COSH", "TANH", "DEGREES", "RADIANS", "WIDTH_BUCKET", //
            // 윈도
            "ROW_NUMBER", "RANK", "DENSE_RANK", "PERCENT_RANK", "CUME_DIST", "NTILE", "LAG", "LEAD", "FIRST_VALUE",
            "LAST_VALUE", "NTH_VALUE", //
            // JSON
            "JSON_VALUE", "JSON_QUERY", "JSON_EXTRACT", "JSON_EXISTS", "JSON_ARRAY", "JSON_OBJECT", //
            // 인코딩과 해시, 파일 시스템에 관여하지 않음
            "HEX", "UNHEX", "BIN", "OCT", "MD5", "SHA", "SHA1", "SHA256", "SHA512", "HASH", "BASE64",
            "BASE64_BINARY", "RAWTOHEX", "HEXTORAW", "UID", "UUID", "GEN_RANDOM_UUID", "RANDOM_UUID", "NEWID",
            "SYS_GUID", "ORA_HASH", "TYPEOF", "IDENTITY", "LAST_INSERT_ID", "SCOPE_IDENTITY");

    /** 왼쪽 괄호가 바로 뒤에 오지만 함수 호출이 아닌 단어. SQL 키워드와 CAST(X AS VARCHAR(20)), DECIMAL(10,2)의 형 이름 포함. */
    private static final Set<String> NOT_FUNCTION_WORDS = words(//
            // 구조와 키워드
            "SELECT", "FROM", "WHERE", "GROUP", "GROUPING", "ORDER", "BY", "HAVING", "LIMIT", "OFFSET", "FETCH",
            "UNION", "EXCEPT", "MINUS", "INTERSECT", "AND", "OR", "NOT", "IN", "EXISTS", "ANY", "ALL", "SOME", "AS",
            "ON", "USING", "JOIN", "INNER", "LEFT", "RIGHT", "FULL", "OUTER", "CROSS", "NATURAL", "CASE", "WHEN",
            "THEN", "ELSE", "END", "IS", "NULL", "LIKE", "ILIKE", "RLIKE", "BETWEEN", "DISTINCT", "VALUES", "INTO",
            "SET", "ASC", "DESC", "WITH", "FILTER", "OVER", "PARTITION", "ROWS", "RANGE", "UNBOUNDED", "PRECEDING",
            "FOLLOWING", "CURRENT", "INTERVAL", "ESCAPE", "SIMILAR", "COLLATE", "DEFAULT", "FOR", "NULLS", "FIRST",
            "LAST", "SEPARATOR", "ROLLUP", "CUBE", "LATERAL", "RECURSIVE", "RETURNING", "TOP", "TIES", "WINDOW", //
            // 데이터 유형
            "VARCHAR", "VARCHAR2", "CHAR", "NCHAR", "NVARCHAR", "NVARCHAR2", "CLOB", "NCLOB", "BLOB", "BINARY",
            "VARBINARY", "BINARY_FLOAT", "BINARY_DOUBLE", "INT", "INTEGER", "TINYINT", "SMALLINT", "MEDIUMINT",
            "BIGINT", "DECIMAL", "DEC", "NUMERIC", "FIXED", "FLOAT", "DOUBLE", "REAL", "BOOLEAN", "BOOL", "BIT",
            "DATE", "TIME", "DATETIME", "SMALLDATETIME", "DATETIME2", "TIMESTAMP", "TIMESTAMPTZ", "TIMESTAMP_LTZ",
            "TIMESTAMP_TZ", "TEXT", "TINYTEXT", "MEDIUMTEXT", "LONGTEXT", "TINYBLOB", "MEDIUMBLOB",
            "LONGBLOB", "BYTEA", "YEAR", "SERIAL", "BIGSERIAL", "UUID", "JSON", "JSONB", "XML", "STRING", "NUMBER",
            "RAW", "LONG", "IMAGE", "MONEY", "SMALLMONEY", "SQL_VARIANT", "UNIQUEIDENTIFIER", "ROWVERSION", "ARRAY");

    /** 함수 호출 형태가 아닌 위험한 구문, 화이트리스트 외 두 번째 방어선. */
    private static final Pattern[] DENIED_PATTERNS = new Pattern[] { //
            Pattern.compile("\\bINTO\\s+(OUTFILE|DUMPFILE)\\b"), //
            Pattern.compile("\\bLOAD\\s+(LOCAL\\s+)?DATA\\b"), //
            Pattern.compile("\\bLOAD\\s+INFILE\\b"), //
            Pattern.compile("\\bBULK\\s+INSERT\\b"), //
            Pattern.compile("\\b(OPENROWSET|OPENQUERY|OPENDATASOURCE)\\b"), //
            Pattern.compile("\\b(RUNSCRIPT|SCRIPTFILE)\\b"), //
            Pattern.compile("\\bLINK\\s+SCHEMA\\b"), //
            Pattern.compile("\\b(FILE_READ|FILE_WRITE|FILE_LENGTH|FILE_EXISTS|CSVREAD|CSVWRITE|LOAD_FILE)\\b"), //
            Pattern.compile("\\b(DBLINK|DBLINK_[A-Z0-9_]+)\\b"), //
            Pattern.compile("\\b(PG_[A-Z0-9_]+)\\b"), //
            Pattern.compile("\\bLO_(IMPORT|EXPORT)\\b"), //
            Pattern.compile("\\b(UTL_[A-Z0-9_]+|DBMS_[A-Z0-9_]+)\\b"), //
            Pattern.compile("\\b(XP_[A-Z0-9_]+|SP_OA[A-Z0-9_]+)\\b"), //
            Pattern.compile("\\b(SLEEP|BENCHMARK)\\b"), //
            Pattern.compile("\\bWAITFOR\\s+DELAY\\b"), //
            Pattern.compile("\\b(CREATE|ALTER|DROP)\\s+(ALIAS|AGGREGATE|ROLE|USER|SCHEMA|LINKED|FUNCTION|PROCEDURE|TRIGGER|SEQUENCE|VIEW|MATERIALIZED|TABLE|INDEX)\\b"), //
            Pattern.compile("\\b(GRANT|REVOKE|SHUTDOWN)\\b"), //
            Pattern.compile("\\b(CALL|EXEC|EXECUTE)\\s+[A-Z_(]"), //
            Pattern.compile("\\bSET\\s+@"), //
            Pattern.compile("\\bINTO\\s+@") };

    /** 식별자 바로 뒤에 왼쪽 괄호가 오면 함수 호출로 간주. */
    private static final Pattern FUNCTION_CALL_PATTERN = Pattern.compile("([A-Z_][A-Z0-9_$]*)\\s*\\(");

    private static final Pattern INSERT_TABLE_PATTERN = Pattern.compile("(\\bINTO\\s+)[A-Z0-9_$.]+(?=\\s*\\()");

    private static final Pattern STATEMENT_START_PATTERN = Pattern.compile("^[A-Z_][A-Z0-9_$]*");

    private static final Pattern QUERY_PATTERN = Pattern.compile("^SELECT\\b|^WITH\\b");

    private static final Pattern INTO_PATTERN = Pattern.compile("\\bINTO\\b");

    private static final Pattern WHITE_SPACE_PATTERN = Pattern.compile("\\s+");

    private SqlSafetyChecker() {
        // nothing
    }

    /**
     * createNativeQuery로 실행될 네이티브 SQL 한 건 검사.
     *
     * @param sql 매개변수를 ?N 자리표시자로 치환한 후의 최종 SQL 텍스트
     * @throws Exception 검사 실패
     */
    public static void check(String sql) throws Exception {
        if (StringUtils.isBlank(sql)) {
            return;
        }
        String normalized = normalize(sql);
        if (normalized.isEmpty()) {
            return;
        }
        checkStatementType(normalized);
        checkDeniedClause(normalized);
        checkFunction(normalized);
    }

    private static void checkStatementType(String normalized) throws Exception {
        String text = normalized;
        while (text.startsWith("(")) {
            text = text.substring(1).trim();
        }
        Matcher matcher = STATEMENT_START_PATTERN.matcher(text);
        if (!matcher.find()) {
            throw new ExceptionSqlUnsafe("문장 유형을 식별할 수 없습니다.");
        }
        String start = matcher.group();
        if (!ALLOWED_STATEMENT_STARTS.contains(start)) {
            throw new ExceptionSqlUnsafe("허용되지 않는 키워드 \"" + start + "\".");
        }
        // H2의 SELECT INTO는 쿼리 결과를 파일이나 새 테이블에 쓰므로 INSERT INTO만 허용
        if (QUERY_PATTERN.matcher(start).find() && INTO_PATTERN.matcher(normalized).find()) {
            throw new ExceptionSqlUnsafe("SELECT INTO is not allowed.");
        }
    }

    private static void checkDeniedClause(String normalized) throws Exception {
        for (Pattern pattern : DENIED_PATTERNS) {
            Matcher matcher = pattern.matcher(normalized);
            if (matcher.find()) {
                throw new ExceptionSqlUnsafe("허용되지 않는 키워드 \"" + matcher.group().trim() + "\".");
            }
        }
    }

    private static void checkFunction(String normalized) throws Exception {
        // INSERT INTO T (XID, XNAME)의 테이블명은 왼쪽 괄호가 바로 뒤에 오지만 함수 호출이 아니므로 먼저 자리표시자로 치환
        String text = INSERT_TABLE_PATTERN.matcher(normalized).replaceAll("$1#");
        Matcher matcher = FUNCTION_CALL_PATTERN.matcher(text);
        while (matcher.find()) {
            String function = matcher.group(1);
            if (NOT_FUNCTION_WORDS.contains(function)) {
                continue;
            }
            if (!ALLOWED_FUNCTIONS.contains(function)) {
                throw new ExceptionSqlUnsafe("function " + function + " is not allowed.");
            }
        }
    }

    /**
     * 주석과 문자열 상수를 제거하고 대문자로 변환해 주석·상수로 키워드를 쪼개 검사를 우회하는 것을 방지. 큰따옴표와 백틱은 식별자
     * 인용이지 상수가 아니므로 내용을 남겨 검사에 포함.
     *
     * @param sql 원본 SQL
     * @return 정규화된 SQL, 앞뒤 공백을 제거하고 연속 공백을 공백 하나로 합침
     * @throws Exception 닫히지 않은 주석 또는 따옴표 존재
     */
    private static String normalize(String sql) throws Exception {
        char[] chars = sql.toCharArray();
        StringBuilder buffer = new StringBuilder(chars.length);
        int index = 0;
        while (index < chars.length) {
            char c = chars[index];
            if (('/' == c) && ((index + 1) < chars.length) && ('*' == chars[index + 1])) {
                // MySQL의 /*!...*/는 주석 안의 SQL을 일반 문장으로 실행하는 실행 가능 주석이므로 바로 거부
                if (((index + 2) < chars.length) && ('!' == chars[index + 2])) {
                    throw new ExceptionSqlUnsafe("executable comment is not allowed.");
                }
                int end = indexOfSequence(chars, "*/", index + 2);
                if (-1 == end) {
                    throw new ExceptionSqlUnsafe("unterminated block comment.");
                }
                buffer.append(' ');
                index = end + 2;
                continue;
            }
            if (('-' == c) && ((index + 1) < chars.length) && ('-' == chars[index + 1])) {
                buffer.append(' ');
                index = skipToLineEnd(chars, index);
                continue;
            }
            if ('\'' == c) {
                // 문자열 상수는 코드가 아닌 데이터이므로 통째로 공백으로 치환하고, 상수 안의 키워드는 검사하지 않음
                buffer.append(' ');
                index = indexOfClosingQuote(chars, c, index) + 1;
                continue;
            }
            if (('"' == c) || ('`' == c)) {
                // 큰따옴표와 백틱은 식별자 인용임. H2에서 SELECT "FILE_READ"('/etc/passwd', NULL)
                // 은 따옴표 없는 FILE_READ 호출과 효과가 완전히 같으므로 따옴표 안의 내용도 남겨 함께 검사해야 함
                int end = indexOfClosingQuote(chars, c, index);
                buffer.append(chars, index + 1, end - index - 1);
                index = end + 1;
                continue;
            }
            buffer.append(c);
            index++;
        }
        return WHITE_SPACE_PATTERN.matcher(buffer.toString().toUpperCase(Locale.ROOT).trim()).replaceAll(" ").trim();
    }

    private static int skipToLineEnd(char[] chars, int from) {
        for (int i = from; i < chars.length; i++) {
            if (('\n' == chars[i]) || ('\r' == chars[i])) {
                return i;
            }
        }
        return chars.length;
    }

    private static int indexOfSequence(char[] chars, String target, int from) {
        int offset = new String(chars, from, chars.length - from).indexOf(target);
        return (-1 == offset) ? -1 : offset + from;
    }

    /**
     * 짝을 이룬 따옴표에서 닫는 따옴표의 인덱스 반환. 따옴표 두 개 연속으로 따옴표 자체를 나타내는 방식과 MySQL의 백슬래시 이스케이프 지원.
     *
     * @param chars  문자 배열
     * @param quote  따옴표
     * @param from   여는 따옴표의 인덱스
     * @return 닫는 따옴표의 인덱스
     * @throws Exception 따옴표가 닫히지 않음
     */
    private static int indexOfClosingQuote(char[] chars, char quote, int from) throws Exception {
        for (int i = from + 1; i < chars.length; i++) {
            char c = chars[i];
            if ((('\'' == quote) && ('\\' == c))) {
                i++;
                continue;
            }
            if (c == quote) {
                if (((i + 1) < chars.length) && (chars[i + 1] == quote)) {
                    i++;
                    continue;
                }
                return i + 1;
            }
        }
        throw new ExceptionSqlUnsafe("unterminated quoted string.");
    }

    private static Set<String> words(String... values) {
        return new LinkedHashSet<>(Arrays.asList(values));
    }
}
