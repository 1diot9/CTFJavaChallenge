package org.h2.engine;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.h2.util.StringUtils;
import org.h2.value.DataType;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/Mode.class */
public class Mode {
    private static final HashMap<String, Mode> MODES = new HashMap<>();
    public boolean aliasColumnName;
    public boolean convertOnlyToSmallerScale;
    public boolean indexDefinitionInCreateTable;
    public boolean squareBracketQuotedNames;
    public boolean systemColumns;
    public boolean treatEmptyStringsAsNull;
    public boolean greatestLeastIgnoreNulls;
    public boolean sysDummy1;
    public boolean allowPlusForStringConcat;
    public boolean logIsLogBase10;
    public boolean swapLogFunctionParameters;
    public boolean regexpReplaceBackslashReferences;
    public boolean swapConvertFunctionParameters;
    public boolean isolationLevelInSelectOrInsertStatement;
    public boolean onDuplicateKeyUpdate;
    public boolean replaceInto;
    public boolean insertOnConflict;
    public Pattern supportedClientInfoPropertiesRegEx;
    public boolean supportPoundSymbolForColumnNames;
    public boolean allowEmptyInPredicate;
    public boolean allowDB2TimestampFormat;
    public boolean discardWithTableHints;
    public boolean dateTimeValueWithinTransaction;
    public boolean zeroExLiteralsAreBinaryStrings;
    public boolean allowUnrelatedOrderByExpressionsInDistinctQueries;
    public boolean alterTableExtensionsMySQL;
    public boolean alterTableModifyColumn;
    public boolean alterTableModifyColumnPreserveNullability;
    public boolean mySqlTableOptions;
    public boolean deleteIdentifierFrom;
    public boolean truncateTableRestartIdentity;
    public boolean decimalSequences;
    public boolean allowEmptySchemaValuesAsDefaultSchema;
    public boolean allNumericTypesHavePrecision;
    public boolean forBitData;
    public boolean charAndByteLengthUnits;
    public boolean nextvalAndCurrvalPseudoColumns;
    public boolean nextValueReturnsDifferentValues;
    public boolean updateSequenceOnManualIdentityInsertion;
    public boolean takeInsertedIdentity;
    public boolean takeGeneratedSequenceValue;
    public boolean identityColumnsHaveDefaultOnNull;
    public boolean mergeWhere;
    public boolean allowUsingFromClauseInUpdateStatement;
    public boolean createUniqueConstraintForReferencedColumns;
    public boolean topInSelect;
    public boolean topInDML;
    public boolean limit;
    public boolean minusIsExcept;
    public boolean identityDataType;
    public boolean serialDataTypes;
    public boolean identityClause;
    public boolean autoIncrementClause;
    public boolean dateIsTimestamp0;
    public boolean numericIsDecfloat;
    public boolean groupByColumnIndex;
    public boolean numericWithBooleanComparison;
    public boolean acceptsCommaAsJsonKeyValueSeparator;
    private final String name;
    private final ModeEnum modeEnum;
    public NullsDistinct nullsDistinct = NullsDistinct.DISTINCT;
    public CharPadding charPadding = CharPadding.ALWAYS;
    public ExpressionNames expressionNames = ExpressionNames.OPTIMIZED_SQL;
    public ViewExpressionNames viewExpressionNames = ViewExpressionNames.AS_IS;
    public Set<String> disallowedTypes = Collections.emptySet();
    public HashMap<String, DataType> typeByNameMap = new HashMap<>();

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/Mode$CharPadding.class */
    public enum CharPadding {
        ALWAYS,
        IN_RESULT_SETS,
        NEVER
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/Mode$ExpressionNames.class */
    public enum ExpressionNames {
        OPTIMIZED_SQL,
        ORIGINAL_SQL,
        EMPTY,
        NUMBER,
        C_NUMBER,
        POSTGRESQL_STYLE
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/Mode$ModeEnum.class */
    public enum ModeEnum {
        REGULAR,
        STRICT,
        LEGACY,
        DB2,
        Derby,
        MariaDB,
        MSSQLServer,
        HSQLDB,
        MySQL,
        Oracle,
        PostgreSQL
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/Mode$ViewExpressionNames.class */
    public enum ViewExpressionNames {
        AS_IS,
        EXCEPTION,
        MYSQL_STYLE
    }

    static {
        Mode mode = new Mode(ModeEnum.REGULAR);
        mode.allowEmptyInPredicate = true;
        mode.dateTimeValueWithinTransaction = true;
        mode.topInSelect = true;
        mode.limit = true;
        mode.minusIsExcept = true;
        mode.identityDataType = true;
        mode.serialDataTypes = true;
        mode.autoIncrementClause = true;
        add(mode);
        Mode mode2 = new Mode(ModeEnum.STRICT);
        mode2.dateTimeValueWithinTransaction = true;
        add(mode2);
        Mode mode3 = new Mode(ModeEnum.LEGACY);
        mode3.allowEmptyInPredicate = true;
        mode3.dateTimeValueWithinTransaction = true;
        mode3.topInSelect = true;
        mode3.limit = true;
        mode3.minusIsExcept = true;
        mode3.identityDataType = true;
        mode3.serialDataTypes = true;
        mode3.autoIncrementClause = true;
        mode3.identityClause = true;
        mode3.updateSequenceOnManualIdentityInsertion = true;
        mode3.takeInsertedIdentity = true;
        mode3.identityColumnsHaveDefaultOnNull = true;
        mode3.nextvalAndCurrvalPseudoColumns = true;
        mode3.topInDML = true;
        mode3.mergeWhere = true;
        mode3.createUniqueConstraintForReferencedColumns = true;
        mode3.numericWithBooleanComparison = true;
        mode3.greatestLeastIgnoreNulls = true;
        add(mode3);
        Mode mode4 = new Mode(ModeEnum.DB2);
        mode4.aliasColumnName = true;
        mode4.sysDummy1 = true;
        mode4.isolationLevelInSelectOrInsertStatement = true;
        mode4.supportedClientInfoPropertiesRegEx = Pattern.compile("ApplicationName|ClientAccountingInformation|ClientUser|ClientCorrelationToken");
        mode4.allowDB2TimestampFormat = true;
        mode4.forBitData = true;
        mode4.takeInsertedIdentity = true;
        mode4.nextvalAndCurrvalPseudoColumns = true;
        mode4.expressionNames = ExpressionNames.NUMBER;
        mode4.viewExpressionNames = ViewExpressionNames.EXCEPTION;
        mode4.limit = true;
        mode4.minusIsExcept = true;
        mode4.numericWithBooleanComparison = true;
        add(mode4);
        Mode mode5 = new Mode(ModeEnum.Derby);
        mode5.aliasColumnName = true;
        mode5.nullsDistinct = NullsDistinct.NOT_DISTINCT;
        mode5.sysDummy1 = true;
        mode5.isolationLevelInSelectOrInsertStatement = true;
        mode5.supportedClientInfoPropertiesRegEx = null;
        mode5.forBitData = true;
        mode5.takeInsertedIdentity = true;
        mode5.expressionNames = ExpressionNames.NUMBER;
        mode5.viewExpressionNames = ViewExpressionNames.EXCEPTION;
        add(mode5);
        Mode mode6 = new Mode(ModeEnum.HSQLDB);
        mode6.allowPlusForStringConcat = true;
        mode6.identityColumnsHaveDefaultOnNull = true;
        mode6.supportedClientInfoPropertiesRegEx = null;
        mode6.expressionNames = ExpressionNames.C_NUMBER;
        mode6.topInSelect = true;
        mode6.limit = true;
        mode6.minusIsExcept = true;
        mode6.numericWithBooleanComparison = true;
        add(mode6);
        Mode mode7 = new Mode(ModeEnum.MSSQLServer);
        mode7.aliasColumnName = true;
        mode7.squareBracketQuotedNames = true;
        mode7.nullsDistinct = NullsDistinct.NOT_DISTINCT;
        mode7.greatestLeastIgnoreNulls = true;
        mode7.allowPlusForStringConcat = true;
        mode7.swapLogFunctionParameters = true;
        mode7.swapConvertFunctionParameters = true;
        mode7.supportPoundSymbolForColumnNames = true;
        mode7.discardWithTableHints = true;
        mode7.supportedClientInfoPropertiesRegEx = null;
        mode7.zeroExLiteralsAreBinaryStrings = true;
        mode7.truncateTableRestartIdentity = true;
        mode7.takeInsertedIdentity = true;
        DataType createNumeric = DataType.createNumeric(19, 4);
        createNumeric.type = 13;
        createNumeric.sqlType = 2;
        createNumeric.specialPrecisionScale = true;
        mode7.typeByNameMap.put("MONEY", createNumeric);
        DataType createNumeric2 = DataType.createNumeric(10, 4);
        createNumeric2.type = 13;
        createNumeric2.sqlType = 2;
        createNumeric2.specialPrecisionScale = true;
        mode7.typeByNameMap.put("SMALLMONEY", createNumeric2);
        mode7.typeByNameMap.put("UNIQUEIDENTIFIER", DataType.getDataType(39));
        mode7.allowEmptySchemaValuesAsDefaultSchema = true;
        mode7.expressionNames = ExpressionNames.EMPTY;
        mode7.viewExpressionNames = ViewExpressionNames.EXCEPTION;
        mode7.topInSelect = true;
        mode7.topInDML = true;
        mode7.identityClause = true;
        mode7.numericWithBooleanComparison = true;
        add(mode7);
        Mode mode8 = new Mode(ModeEnum.MariaDB);
        mode8.indexDefinitionInCreateTable = true;
        mode8.regexpReplaceBackslashReferences = true;
        mode8.onDuplicateKeyUpdate = true;
        mode8.replaceInto = true;
        mode8.charPadding = CharPadding.NEVER;
        mode8.supportedClientInfoPropertiesRegEx = Pattern.compile(".*");
        mode8.zeroExLiteralsAreBinaryStrings = true;
        mode8.allowUnrelatedOrderByExpressionsInDistinctQueries = true;
        mode8.alterTableExtensionsMySQL = true;
        mode8.alterTableModifyColumn = true;
        mode8.mySqlTableOptions = true;
        mode8.deleteIdentifierFrom = true;
        mode8.truncateTableRestartIdentity = true;
        mode8.allNumericTypesHavePrecision = true;
        mode8.nextValueReturnsDifferentValues = true;
        mode8.updateSequenceOnManualIdentityInsertion = true;
        mode8.takeInsertedIdentity = true;
        mode8.identityColumnsHaveDefaultOnNull = true;
        mode8.expressionNames = ExpressionNames.ORIGINAL_SQL;
        mode8.viewExpressionNames = ViewExpressionNames.MYSQL_STYLE;
        mode8.limit = true;
        mode8.autoIncrementClause = true;
        mode8.typeByNameMap.put("YEAR", DataType.getDataType(10));
        mode8.groupByColumnIndex = true;
        mode8.numericWithBooleanComparison = true;
        mode8.acceptsCommaAsJsonKeyValueSeparator = true;
        add(mode8);
        Mode mode9 = new Mode(ModeEnum.MySQL);
        mode9.indexDefinitionInCreateTable = true;
        mode9.regexpReplaceBackslashReferences = true;
        mode9.onDuplicateKeyUpdate = true;
        mode9.replaceInto = true;
        mode9.charPadding = CharPadding.NEVER;
        mode9.supportedClientInfoPropertiesRegEx = Pattern.compile(".*");
        mode9.zeroExLiteralsAreBinaryStrings = true;
        mode9.allowUnrelatedOrderByExpressionsInDistinctQueries = true;
        mode9.alterTableExtensionsMySQL = true;
        mode9.alterTableModifyColumn = true;
        mode9.mySqlTableOptions = true;
        mode9.deleteIdentifierFrom = true;
        mode9.truncateTableRestartIdentity = true;
        mode9.allNumericTypesHavePrecision = true;
        mode9.updateSequenceOnManualIdentityInsertion = true;
        mode9.takeInsertedIdentity = true;
        mode9.identityColumnsHaveDefaultOnNull = true;
        mode9.createUniqueConstraintForReferencedColumns = true;
        mode9.expressionNames = ExpressionNames.ORIGINAL_SQL;
        mode9.viewExpressionNames = ViewExpressionNames.MYSQL_STYLE;
        mode9.limit = true;
        mode9.autoIncrementClause = true;
        mode9.typeByNameMap.put("YEAR", DataType.getDataType(10));
        mode9.groupByColumnIndex = true;
        mode9.numericWithBooleanComparison = true;
        mode9.acceptsCommaAsJsonKeyValueSeparator = true;
        add(mode9);
        Mode mode10 = new Mode(ModeEnum.Oracle);
        mode10.aliasColumnName = true;
        mode10.convertOnlyToSmallerScale = true;
        mode10.nullsDistinct = NullsDistinct.ALL_DISTINCT;
        mode10.treatEmptyStringsAsNull = true;
        mode10.regexpReplaceBackslashReferences = true;
        mode10.supportPoundSymbolForColumnNames = true;
        mode10.supportedClientInfoPropertiesRegEx = Pattern.compile(".*\\..*");
        mode10.alterTableModifyColumn = true;
        mode10.alterTableModifyColumnPreserveNullability = true;
        mode10.decimalSequences = true;
        mode10.charAndByteLengthUnits = true;
        mode10.nextvalAndCurrvalPseudoColumns = true;
        mode10.mergeWhere = true;
        mode10.minusIsExcept = true;
        mode10.expressionNames = ExpressionNames.ORIGINAL_SQL;
        mode10.viewExpressionNames = ViewExpressionNames.EXCEPTION;
        mode10.dateIsTimestamp0 = true;
        mode10.typeByNameMap.put("BINARY_FLOAT", DataType.getDataType(14));
        mode10.typeByNameMap.put("BINARY_DOUBLE", DataType.getDataType(15));
        add(mode10);
        Mode mode11 = new Mode(ModeEnum.PostgreSQL);
        mode11.aliasColumnName = true;
        mode11.systemColumns = true;
        mode11.greatestLeastIgnoreNulls = true;
        mode11.logIsLogBase10 = true;
        mode11.regexpReplaceBackslashReferences = true;
        mode11.insertOnConflict = true;
        mode11.supportedClientInfoPropertiesRegEx = Pattern.compile("ApplicationName");
        mode11.charPadding = CharPadding.IN_RESULT_SETS;
        mode11.nextValueReturnsDifferentValues = true;
        mode11.takeGeneratedSequenceValue = true;
        mode11.expressionNames = ExpressionNames.POSTGRESQL_STYLE;
        mode11.allowUsingFromClauseInUpdateStatement = true;
        mode11.limit = true;
        mode11.serialDataTypes = true;
        mode11.numericIsDecfloat = true;
        HashSet hashSet = new HashSet();
        hashSet.add("NUMBER");
        hashSet.add("TINYINT");
        hashSet.add("BLOB");
        hashSet.add("VARCHAR_IGNORECASE");
        mode11.disallowedTypes = hashSet;
        mode11.typeByNameMap.put("JSONB", DataType.getDataType(38));
        DataType createNumeric3 = DataType.createNumeric(19, 2);
        createNumeric3.type = 13;
        createNumeric3.sqlType = 2;
        createNumeric3.specialPrecisionScale = true;
        mode11.typeByNameMap.put("MONEY", createNumeric3);
        mode11.typeByNameMap.put("OID", DataType.getDataType(11));
        mode11.dateTimeValueWithinTransaction = true;
        mode11.groupByColumnIndex = true;
        add(mode11);
    }

    private Mode(ModeEnum modeEnum) {
        this.name = modeEnum.name();
        this.modeEnum = modeEnum;
    }

    private static void add(Mode mode) {
        MODES.put(StringUtils.toUpperEnglish(mode.name), mode);
    }

    public static Mode getInstance(String str) {
        return MODES.get(StringUtils.toUpperEnglish(str));
    }

    public static Mode getRegular() {
        return getInstance(ModeEnum.REGULAR.name());
    }

    public String getName() {
        return this.name;
    }

    public ModeEnum getEnum() {
        return this.modeEnum;
    }

    public String toString() {
        return this.name;
    }
}
