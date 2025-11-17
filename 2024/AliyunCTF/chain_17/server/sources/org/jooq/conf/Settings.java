package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.jooq.SQLDialect;
import org.jooq.util.jaxb.tools.XMLAppendable;
import org.jooq.util.jaxb.tools.XMLBuilder;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Settings", propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/Settings.class */
public class Settings extends SettingsBase implements Serializable, Cloneable, XMLAppendable {
    private static final long serialVersionUID = 31900;
    protected RenderMapping renderMapping;

    @XmlJavaTypeAdapter(LocaleAdapter.class)
    @XmlElement(type = String.class)
    protected Locale renderLocale;
    protected RenderFormatting renderFormatting;

    @XmlJavaTypeAdapter(SQLDialectAdapter.class)
    @XmlElement(type = String.class, defaultValue = "DEFAULT")
    protected SQLDialect interpreterDialect;

    @XmlJavaTypeAdapter(LocaleAdapter.class)
    @XmlElement(type = String.class)
    protected Locale interpreterLocale;
    protected MigrationSchema migrationHistorySchema;
    protected MigrationSchema migrationDefaultSchema;

    @XmlJavaTypeAdapter(LocaleAdapter.class)
    @XmlElement(type = String.class)
    protected Locale locale;

    @XmlJavaTypeAdapter(SQLDialectAdapter.class)
    @XmlElement(type = String.class, defaultValue = "DEFAULT")
    protected SQLDialect parseDialect;

    @XmlJavaTypeAdapter(LocaleAdapter.class)
    @XmlElement(type = String.class)
    protected Locale parseLocale;

    @XmlElementWrapper(name = "interpreterSearchPath")
    @XmlElement(name = "schema")
    protected List<InterpreterSearchSchema> interpreterSearchPath;

    @XmlElementWrapper(name = "migrationSchemata")
    @XmlElement(name = "schema")
    protected List<MigrationSchema> migrationSchemata;

    @XmlElementWrapper(name = "parseSearchPath")
    @XmlElement(name = "schema")
    protected List<ParseSearchSchema> parseSearchPath;

    @XmlElement(defaultValue = "true")
    protected Boolean forceIntegerTypesOnZeroScaleDecimals = true;

    @XmlElement(defaultValue = "true")
    protected Boolean renderCatalog = true;

    @XmlElement(defaultValue = "true")
    protected Boolean renderSchema = true;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "ALWAYS")
    protected RenderTable renderTable = RenderTable.ALWAYS;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "EXPLICIT_DEFAULT_QUOTED")
    protected RenderQuotedNames renderQuotedNames = RenderQuotedNames.EXPLICIT_DEFAULT_QUOTED;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "AS_IS")
    protected RenderNameCase renderNameCase = RenderNameCase.AS_IS;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "QUOTED")
    protected RenderNameStyle renderNameStyle = RenderNameStyle.QUOTED;

    @XmlElement(defaultValue = ":")
    protected String renderNamedParamPrefix = ":";

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "AS_IS")
    protected RenderKeywordCase renderKeywordCase = RenderKeywordCase.AS_IS;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "AS_IS")
    protected RenderKeywordStyle renderKeywordStyle = RenderKeywordStyle.AS_IS;

    @XmlElement(defaultValue = "false")
    protected Boolean renderFormatted = false;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "DEFAULT")
    protected RenderOptionalKeyword renderOptionalAssociativityParentheses = RenderOptionalKeyword.DEFAULT;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "DEFAULT")
    protected RenderOptionalKeyword renderOptionalAsKeywordForTableAliases = RenderOptionalKeyword.DEFAULT;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "DEFAULT")
    protected RenderOptionalKeyword renderOptionalAsKeywordForFieldAliases = RenderOptionalKeyword.DEFAULT;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "DEFAULT")
    protected RenderOptionalKeyword renderOptionalInnerKeyword = RenderOptionalKeyword.DEFAULT;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "DEFAULT")
    protected RenderOptionalKeyword renderOptionalOuterKeyword = RenderOptionalKeyword.DEFAULT;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "OFF")
    protected RenderImplicitWindowRange renderImplicitWindowRange = RenderImplicitWindowRange.OFF;

    @XmlElement(defaultValue = "false")
    protected Boolean renderScalarSubqueriesForStoredFunctions = false;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "DEFAULT")
    protected RenderImplicitJoinType renderImplicitJoinType = RenderImplicitJoinType.DEFAULT;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "DEFAULT")
    protected RenderImplicitJoinType renderImplicitJoinToManyType = RenderImplicitJoinType.DEFAULT;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "IMPLICIT_NULL")
    protected RenderDefaultNullability renderDefaultNullability = RenderDefaultNullability.IMPLICIT_NULL;

    @XmlElement(defaultValue = "false")
    protected Boolean renderCoalesceToEmptyStringInConcat = false;

    @XmlElement(defaultValue = "true")
    protected Boolean renderOrderByRownumberForEmulatedPagination = true;

    @XmlElement(defaultValue = "true")
    protected Boolean renderOutputForSQLServerReturningClause = true;

    @XmlElement(defaultValue = "true")
    protected Boolean renderGroupConcatMaxLenSessionVariable = true;

    @XmlElement(defaultValue = "false")
    protected Boolean renderParenthesisAroundSetOperationQueries = false;

    @XmlElement(defaultValue = "true")
    protected Boolean renderVariablesInDerivedTablesForEmulations = true;

    @XmlElement(defaultValue = "true")
    protected Boolean renderRowConditionForSeekClause = true;

    @XmlElement(defaultValue = "false")
    protected Boolean renderRedundantConditionForSeekClause = false;

    @XmlElement(defaultValue = "false")
    protected Boolean renderPlainSQLTemplatesAsRaw = false;

    @XmlElement(defaultValue = "")
    protected String renderDollarQuotedStringToken = "";

    @XmlElement(defaultValue = ".")
    protected String namePathSeparator = ".";

    @XmlElement(defaultValue = "false")
    protected Boolean bindOffsetDateTimeType = false;

    @XmlElement(defaultValue = "false")
    protected Boolean bindOffsetTimeType = false;

    @XmlElement(defaultValue = "true")
    protected Boolean fetchTriggerValuesAfterSQLServerOutput = true;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "WHEN_NEEDED")
    protected FetchTriggerValuesAfterReturning fetchTriggerValuesAfterReturning = FetchTriggerValuesAfterReturning.WHEN_NEEDED;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "WHEN_RESULT_REQUESTED")
    protected FetchIntermediateResult fetchIntermediateResult = FetchIntermediateResult.WHEN_RESULT_REQUESTED;

    @XmlElement(defaultValue = "true")
    protected Boolean diagnosticsDuplicateStatements = true;

    @XmlElement(defaultValue = "true")
    protected Boolean diagnosticsDuplicateStatementsUsingTransformPatterns = true;

    @XmlElement(defaultValue = "true")
    protected Boolean diagnosticsMissingWasNullCall = true;

    @XmlElement(defaultValue = "true")
    protected Boolean diagnosticsRepeatedStatements = true;

    @XmlElement(defaultValue = "true")
    protected Boolean diagnosticsConsecutiveAggregation = true;

    @XmlElement(defaultValue = "true")
    protected Boolean diagnosticsConcatenationInPredicate = true;

    @XmlElement(defaultValue = "true")
    protected Boolean diagnosticsPossiblyWrongExpression = true;

    @XmlElement(defaultValue = "true")
    protected Boolean diagnosticsTooManyColumnsFetched = true;

    @XmlElement(defaultValue = "true")
    protected Boolean diagnosticsTooManyRowsFetched = true;

    @XmlElement(defaultValue = "true")
    protected Boolean diagnosticsUnnecessaryWasNullCall = true;

    @XmlElement(defaultValue = "true")
    protected Boolean diagnosticsPatterns = true;

    @XmlElement(defaultValue = "true")
    protected Boolean diagnosticsTrivialCondition = true;

    @XmlElement(defaultValue = "true")
    protected Boolean diagnosticsNullCondition = true;

    @XmlElement(defaultValue = "false")
    protected Boolean transformPatterns = false;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsLogging = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsUnnecessaryDistinct = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsUnnecessaryScalarSubquery = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsUnnecessaryInnerJoin = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsUnnecessaryGroupByExpressions = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsUnnecessaryOrderByExpressions = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsUnnecessaryExistsSubqueryClauses = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsCountConstant = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsTrim = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsNotAnd = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsNotOr = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsNotNot = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsNotComparison = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsNotNotDistinct = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsDistinctFromNull = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsNormaliseAssociativeOps = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsNormaliseInListSingleElementToComparison = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsNormaliseFieldCompareValue = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsNormaliseCoalesceToNvl = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsOrEqToIn = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsAndNeToNotIn = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsMergeOrComparison = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsMergeAndComparison = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsMergeInLists = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsMergeRangePredicates = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsMergeBetweenSymmetricPredicates = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsCaseSearchedToCaseSimple = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsCaseElseNull = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsUnreachableCaseClauses = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsUnreachableDecodeClauses = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsCaseDistinctToDecode = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsCaseMergeWhenWhen = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsCaseMergeWhenElse = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsCaseToCaseAbbreviation = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsSimplifyCaseAbbreviation = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsFlattenCaseAbbreviation = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsFlattenDecode = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsFlattenCase = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsTrivialCaseAbbreviation = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsTrivialPredicates = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsTrivialBitwiseOperations = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsBitSet = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsBitGet = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsScalarSubqueryCountAsteriskGtZero = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsScalarSubqueryCountExpressionGtZero = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsEmptyScalarSubquery = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsNegNeg = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsBitNotBitNot = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsBitNotBitNand = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsBitNotBitNor = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsBitNotBitXNor = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsNullOnNullInput = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsIdempotentFunctionRepetition = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsArithmeticComparisons = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsArithmeticExpressions = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsTrigonometricFunctions = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsLogarithmicFunctions = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsHyperbolicFunctions = true;

    @XmlElement(defaultValue = "true")
    protected Boolean transformPatternsInverseHyperbolicFunctions = true;

    @XmlElement(defaultValue = "false")
    protected Boolean transformInlineBindValuesForFieldComparisons = false;

    @XmlElement(defaultValue = "false")
    protected Boolean transformAnsiJoinToTableLists = false;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "WHEN_NEEDED")
    protected Transformation transformInConditionSubqueryWithLimitToDerivedTable = Transformation.WHEN_NEEDED;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "WHEN_NEEDED")
    protected Transformation transformQualify = Transformation.WHEN_NEEDED;

    @XmlElement(defaultValue = "false")
    protected Boolean transformTableListsToAnsiJoin = false;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "NEVER")
    protected Transformation transformRownum = Transformation.NEVER;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "NEVER")
    protected TransformUnneededArithmeticExpressions transformUnneededArithmeticExpressions = TransformUnneededArithmeticExpressions.NEVER;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "WHEN_NEEDED")
    protected Transformation transformGroupByColumnIndex = Transformation.WHEN_NEEDED;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "WHEN_NEEDED")
    protected Transformation transformInlineCTE = Transformation.WHEN_NEEDED;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "DEFAULT")
    protected BackslashEscaping backslashEscaping = BackslashEscaping.DEFAULT;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "INDEXED")
    protected ParamType paramType = ParamType.INDEXED;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "DEFAULT")
    protected ParamCastMode paramCastMode = ParamCastMode.DEFAULT;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "PREPARED_STATEMENT")
    protected StatementType statementType = StatementType.PREPARED_STATEMENT;

    @XmlElement(defaultValue = CustomBooleanEditor.VALUE_0)
    protected Integer inlineThreshold = 0;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "DEFAULT")
    protected InvocationOrder transactionListenerStartInvocationOrder = InvocationOrder.DEFAULT;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "DEFAULT")
    protected InvocationOrder transactionListenerEndInvocationOrder = InvocationOrder.DEFAULT;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "DEFAULT")
    protected InvocationOrder migrationListenerStartInvocationOrder = InvocationOrder.DEFAULT;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "DEFAULT")
    protected InvocationOrder migrationListenerEndInvocationOrder = InvocationOrder.DEFAULT;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "DEFAULT")
    protected InvocationOrder visitListenerStartInvocationOrder = InvocationOrder.DEFAULT;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "DEFAULT")
    protected InvocationOrder visitListenerEndInvocationOrder = InvocationOrder.DEFAULT;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "DEFAULT")
    protected InvocationOrder recordListenerStartInvocationOrder = InvocationOrder.DEFAULT;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "DEFAULT")
    protected InvocationOrder recordListenerEndInvocationOrder = InvocationOrder.DEFAULT;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "DEFAULT")
    protected InvocationOrder executeListenerStartInvocationOrder = InvocationOrder.DEFAULT;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "DEFAULT")
    protected InvocationOrder executeListenerEndInvocationOrder = InvocationOrder.DEFAULT;

    @XmlElement(defaultValue = "true")
    protected Boolean executeLogging = true;

    @XmlElement(defaultValue = "true")
    protected Boolean executeLoggingSQLExceptions = true;

    @XmlElement(defaultValue = "true")
    protected Boolean diagnosticsLogging = true;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "DEFAULT")
    protected DiagnosticsConnection diagnosticsConnection = DiagnosticsConnection.DEFAULT;

    @XmlElement(defaultValue = "true")
    protected Boolean updateRecordVersion = true;

    @XmlElement(defaultValue = "true")
    protected Boolean updateRecordTimestamp = true;

    @XmlElement(defaultValue = "false")
    protected Boolean executeWithOptimisticLocking = false;

    @XmlElement(defaultValue = "false")
    protected Boolean executeWithOptimisticLockingExcludeUnversioned = false;

    @XmlElement(defaultValue = "true")
    protected Boolean attachRecords = true;

    @XmlElement(defaultValue = "true")
    protected Boolean insertUnchangedRecords = true;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "NEVER")
    protected UpdateUnchangedRecords updateUnchangedRecords = UpdateUnchangedRecords.NEVER;

    @XmlElement(defaultValue = "false")
    protected Boolean updatablePrimaryKeys = false;

    @XmlElement(defaultValue = "true")
    protected Boolean reflectionCaching = true;

    @XmlElement(defaultValue = "true")
    protected Boolean cacheRecordMappers = true;

    @XmlElement(defaultValue = "true")
    protected Boolean cacheParsingConnection = true;

    @XmlElement(defaultValue = "8192")
    protected Integer cacheParsingConnectionLRUCacheSize = 8192;

    @XmlElement(defaultValue = "true")
    protected Boolean cachePreparedStatementInLoader = true;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "THROW_ALL")
    protected ThrowExceptions throwExceptions = ThrowExceptions.THROW_ALL;

    @XmlElement(defaultValue = "true")
    protected Boolean fetchWarnings = true;

    @XmlElement(defaultValue = CustomBooleanEditor.VALUE_0)
    protected Integer fetchServerOutputSize = 0;

    @XmlElement(defaultValue = "true")
    protected Boolean returnIdentityOnUpdatableRecord = true;

    @XmlElement(defaultValue = "false")
    protected Boolean returnDefaultOnUpdatableRecord = false;

    @XmlElement(defaultValue = "false")
    protected Boolean returnComputedOnUpdatableRecord = false;

    @XmlElement(defaultValue = "false")
    protected Boolean returnAllOnUpdatableRecord = false;

    @XmlElement(defaultValue = "true")
    protected Boolean returnRecordToPojo = true;

    @XmlElement(defaultValue = "true")
    protected Boolean mapJPAAnnotations = true;

    @XmlElement(defaultValue = "false")
    protected Boolean mapRecordComponentParameterNames = false;

    @XmlElement(defaultValue = "true")
    protected Boolean mapConstructorPropertiesParameterNames = true;

    @XmlElement(defaultValue = "false")
    protected Boolean mapConstructorParameterNames = false;

    @XmlElement(defaultValue = "true")
    protected Boolean mapConstructorParameterNamesInKotlin = true;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "DEFAULT")
    protected QueryPoolable queryPoolable = QueryPoolable.DEFAULT;

    @XmlElement(defaultValue = CustomBooleanEditor.VALUE_0)
    protected Integer queryTimeout = 0;

    @XmlElement(defaultValue = CustomBooleanEditor.VALUE_0)
    protected Integer maxRows = 0;

    @XmlElement(defaultValue = CustomBooleanEditor.VALUE_0)
    protected Integer fetchSize = 0;

    @XmlElement(defaultValue = "2147483647")
    protected Integer batchSize = Integer.MAX_VALUE;

    @XmlElement(defaultValue = "true")
    protected Boolean debugInfoOnStackTrace = true;

    @XmlElement(defaultValue = "false")
    protected Boolean inListPadding = false;

    @XmlElement(defaultValue = "2")
    protected Integer inListPadBase = 2;

    @XmlElement(defaultValue = ";")
    protected String delimiter = ";";

    @XmlElement(defaultValue = "false")
    protected Boolean emulateOnDuplicateKeyUpdateOnPrimaryKeyOnly = false;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "DEFAULT")
    protected NestedCollectionEmulation emulateMultiset = NestedCollectionEmulation.DEFAULT;

    @XmlElement(defaultValue = "false")
    protected Boolean emulateComputedColumns = false;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "LOG_DEBUG")
    protected ExecuteWithoutWhere executeUpdateWithoutWhere = ExecuteWithoutWhere.LOG_DEBUG;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "LOG_DEBUG")
    protected ExecuteWithoutWhere executeDeleteWithoutWhere = ExecuteWithoutWhere.LOG_DEBUG;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "DEFAULT")
    protected InterpreterNameLookupCaseSensitivity interpreterNameLookupCaseSensitivity = InterpreterNameLookupCaseSensitivity.DEFAULT;

    @XmlElement(defaultValue = "false")
    protected Boolean interpreterDelayForeignKeyDeclarations = false;

    @XmlElement(defaultValue = "false")
    protected Boolean metaIncludeSystemIndexes = false;

    @XmlElement(defaultValue = "false")
    protected Boolean metaIncludeSystemSequences = false;

    @XmlElement(defaultValue = "false")
    protected Boolean migrationHistorySchemaCreateSchemaIfNotExists = false;

    @XmlElement(defaultValue = "false")
    protected Boolean migrationSchemataCreateSchemaIfNotExists = false;

    @XmlElement(defaultValue = "false")
    protected Boolean migrationAllowsUndo = false;

    @XmlElement(defaultValue = "false")
    protected Boolean migrationRevertUntracked = false;

    @XmlElement(defaultValue = "false")
    protected Boolean migrationAutoBaseline = false;

    @XmlElement(defaultValue = "true")
    protected Boolean migrationAutoVerification = true;

    @XmlElement(defaultValue = "true")
    protected Boolean migrationIgnoreDefaultTimestampPrecisionDiffs = true;

    @XmlElement(defaultValue = "YYYY-MM-DD")
    protected String parseDateFormat = "YYYY-MM-DD";

    @XmlElement(defaultValue = "YYYY-MM-DD HH24:MI:SS.FF")
    protected String parseTimestampFormat = "YYYY-MM-DD HH24:MI:SS.FF";

    @XmlElement(defaultValue = ":")
    protected String parseNamedParamPrefix = ":";

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "DEFAULT")
    protected ParseNameCase parseNameCase = ParseNameCase.DEFAULT;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "OFF")
    protected ParseWithMetaLookups parseWithMetaLookups = ParseWithMetaLookups.OFF;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "WHEN_NEEDED")
    protected Transformation parseAppendMissingTableReferences = Transformation.WHEN_NEEDED;

    @XmlElement(defaultValue = "false")
    protected Boolean parseSetCommands = false;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "IGNORE")
    protected ParseUnsupportedSyntax parseUnsupportedSyntax = ParseUnsupportedSyntax.IGNORE;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "FAIL")
    protected ParseUnknownFunctions parseUnknownFunctions = ParseUnknownFunctions.FAIL;

    @XmlElement(defaultValue = "false")
    protected Boolean parseIgnoreCommercialOnlyFeatures = false;

    @XmlElement(defaultValue = "false")
    protected Boolean parseIgnoreComments = false;

    @XmlElement(defaultValue = "[jooq ignore start]")
    protected String parseIgnoreCommentStart = "[jooq ignore start]";

    @XmlElement(defaultValue = "[jooq ignore stop]")
    protected String parseIgnoreCommentStop = "[jooq ignore stop]";

    @XmlElement(defaultValue = "false")
    protected Boolean parseRetainCommentsBetweenQueries = false;

    @XmlElement(defaultValue = "true")
    protected Boolean parseMetaDefaultExpressions = true;

    @XmlElement(defaultValue = "true")
    protected Boolean parseMetaViewSources = true;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "IGNORE")
    protected WriteIfReadonly readonlyTableRecordInsert = WriteIfReadonly.IGNORE;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "IGNORE")
    protected WriteIfReadonly readonlyUpdatableRecordUpdate = WriteIfReadonly.IGNORE;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "IGNORE")
    protected WriteIfReadonly readonlyInsert = WriteIfReadonly.IGNORE;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "IGNORE")
    protected WriteIfReadonly readonlyUpdate = WriteIfReadonly.IGNORE;

    @XmlElement(defaultValue = "true")
    protected Boolean applyWorkaroundFor7962 = true;

    @XmlSchemaType(name = "string")
    @XmlElement(defaultValue = "LOG_WARN")
    protected Warning warnOnStaticTypeRegistryAccess = Warning.LOG_WARN;

    @Override // org.jooq.conf.SettingsBase
    public /* bridge */ /* synthetic */ Object clone() {
        return super.clone();
    }

    public Boolean isForceIntegerTypesOnZeroScaleDecimals() {
        return this.forceIntegerTypesOnZeroScaleDecimals;
    }

    public void setForceIntegerTypesOnZeroScaleDecimals(Boolean value) {
        this.forceIntegerTypesOnZeroScaleDecimals = value;
    }

    public Boolean isRenderCatalog() {
        return this.renderCatalog;
    }

    public void setRenderCatalog(Boolean value) {
        this.renderCatalog = value;
    }

    public Boolean isRenderSchema() {
        return this.renderSchema;
    }

    public void setRenderSchema(Boolean value) {
        this.renderSchema = value;
    }

    public RenderTable getRenderTable() {
        return this.renderTable;
    }

    public void setRenderTable(RenderTable value) {
        this.renderTable = value;
    }

    public RenderMapping getRenderMapping() {
        return this.renderMapping;
    }

    public void setRenderMapping(RenderMapping value) {
        this.renderMapping = value;
    }

    public RenderQuotedNames getRenderQuotedNames() {
        return this.renderQuotedNames;
    }

    public void setRenderQuotedNames(RenderQuotedNames value) {
        this.renderQuotedNames = value;
    }

    public RenderNameCase getRenderNameCase() {
        return this.renderNameCase;
    }

    public void setRenderNameCase(RenderNameCase value) {
        this.renderNameCase = value;
    }

    @Deprecated
    public RenderNameStyle getRenderNameStyle() {
        return this.renderNameStyle;
    }

    @Deprecated
    public void setRenderNameStyle(RenderNameStyle value) {
        this.renderNameStyle = value;
    }

    public String getRenderNamedParamPrefix() {
        return this.renderNamedParamPrefix;
    }

    public void setRenderNamedParamPrefix(String value) {
        this.renderNamedParamPrefix = value;
    }

    public RenderKeywordCase getRenderKeywordCase() {
        return this.renderKeywordCase;
    }

    public void setRenderKeywordCase(RenderKeywordCase value) {
        this.renderKeywordCase = value;
    }

    @Deprecated
    public RenderKeywordStyle getRenderKeywordStyle() {
        return this.renderKeywordStyle;
    }

    @Deprecated
    public void setRenderKeywordStyle(RenderKeywordStyle value) {
        this.renderKeywordStyle = value;
    }

    public Locale getRenderLocale() {
        return this.renderLocale;
    }

    public void setRenderLocale(Locale value) {
        this.renderLocale = value;
    }

    public Boolean isRenderFormatted() {
        return this.renderFormatted;
    }

    public void setRenderFormatted(Boolean value) {
        this.renderFormatted = value;
    }

    public RenderFormatting getRenderFormatting() {
        return this.renderFormatting;
    }

    public void setRenderFormatting(RenderFormatting value) {
        this.renderFormatting = value;
    }

    public RenderOptionalKeyword getRenderOptionalAssociativityParentheses() {
        return this.renderOptionalAssociativityParentheses;
    }

    public void setRenderOptionalAssociativityParentheses(RenderOptionalKeyword value) {
        this.renderOptionalAssociativityParentheses = value;
    }

    public RenderOptionalKeyword getRenderOptionalAsKeywordForTableAliases() {
        return this.renderOptionalAsKeywordForTableAliases;
    }

    public void setRenderOptionalAsKeywordForTableAliases(RenderOptionalKeyword value) {
        this.renderOptionalAsKeywordForTableAliases = value;
    }

    public RenderOptionalKeyword getRenderOptionalAsKeywordForFieldAliases() {
        return this.renderOptionalAsKeywordForFieldAliases;
    }

    public void setRenderOptionalAsKeywordForFieldAliases(RenderOptionalKeyword value) {
        this.renderOptionalAsKeywordForFieldAliases = value;
    }

    public RenderOptionalKeyword getRenderOptionalInnerKeyword() {
        return this.renderOptionalInnerKeyword;
    }

    public void setRenderOptionalInnerKeyword(RenderOptionalKeyword value) {
        this.renderOptionalInnerKeyword = value;
    }

    public RenderOptionalKeyword getRenderOptionalOuterKeyword() {
        return this.renderOptionalOuterKeyword;
    }

    public void setRenderOptionalOuterKeyword(RenderOptionalKeyword value) {
        this.renderOptionalOuterKeyword = value;
    }

    public RenderImplicitWindowRange getRenderImplicitWindowRange() {
        return this.renderImplicitWindowRange;
    }

    public void setRenderImplicitWindowRange(RenderImplicitWindowRange value) {
        this.renderImplicitWindowRange = value;
    }

    public Boolean isRenderScalarSubqueriesForStoredFunctions() {
        return this.renderScalarSubqueriesForStoredFunctions;
    }

    public void setRenderScalarSubqueriesForStoredFunctions(Boolean value) {
        this.renderScalarSubqueriesForStoredFunctions = value;
    }

    public RenderImplicitJoinType getRenderImplicitJoinType() {
        return this.renderImplicitJoinType;
    }

    public void setRenderImplicitJoinType(RenderImplicitJoinType value) {
        this.renderImplicitJoinType = value;
    }

    public RenderImplicitJoinType getRenderImplicitJoinToManyType() {
        return this.renderImplicitJoinToManyType;
    }

    public void setRenderImplicitJoinToManyType(RenderImplicitJoinType value) {
        this.renderImplicitJoinToManyType = value;
    }

    public RenderDefaultNullability getRenderDefaultNullability() {
        return this.renderDefaultNullability;
    }

    public void setRenderDefaultNullability(RenderDefaultNullability value) {
        this.renderDefaultNullability = value;
    }

    public Boolean isRenderCoalesceToEmptyStringInConcat() {
        return this.renderCoalesceToEmptyStringInConcat;
    }

    public void setRenderCoalesceToEmptyStringInConcat(Boolean value) {
        this.renderCoalesceToEmptyStringInConcat = value;
    }

    public Boolean isRenderOrderByRownumberForEmulatedPagination() {
        return this.renderOrderByRownumberForEmulatedPagination;
    }

    public void setRenderOrderByRownumberForEmulatedPagination(Boolean value) {
        this.renderOrderByRownumberForEmulatedPagination = value;
    }

    public Boolean isRenderOutputForSQLServerReturningClause() {
        return this.renderOutputForSQLServerReturningClause;
    }

    public void setRenderOutputForSQLServerReturningClause(Boolean value) {
        this.renderOutputForSQLServerReturningClause = value;
    }

    public Boolean isRenderGroupConcatMaxLenSessionVariable() {
        return this.renderGroupConcatMaxLenSessionVariable;
    }

    public void setRenderGroupConcatMaxLenSessionVariable(Boolean value) {
        this.renderGroupConcatMaxLenSessionVariable = value;
    }

    public Boolean isRenderParenthesisAroundSetOperationQueries() {
        return this.renderParenthesisAroundSetOperationQueries;
    }

    public void setRenderParenthesisAroundSetOperationQueries(Boolean value) {
        this.renderParenthesisAroundSetOperationQueries = value;
    }

    public Boolean isRenderVariablesInDerivedTablesForEmulations() {
        return this.renderVariablesInDerivedTablesForEmulations;
    }

    public void setRenderVariablesInDerivedTablesForEmulations(Boolean value) {
        this.renderVariablesInDerivedTablesForEmulations = value;
    }

    public Boolean isRenderRowConditionForSeekClause() {
        return this.renderRowConditionForSeekClause;
    }

    public void setRenderRowConditionForSeekClause(Boolean value) {
        this.renderRowConditionForSeekClause = value;
    }

    public Boolean isRenderRedundantConditionForSeekClause() {
        return this.renderRedundantConditionForSeekClause;
    }

    public void setRenderRedundantConditionForSeekClause(Boolean value) {
        this.renderRedundantConditionForSeekClause = value;
    }

    public Boolean isRenderPlainSQLTemplatesAsRaw() {
        return this.renderPlainSQLTemplatesAsRaw;
    }

    public void setRenderPlainSQLTemplatesAsRaw(Boolean value) {
        this.renderPlainSQLTemplatesAsRaw = value;
    }

    public String getRenderDollarQuotedStringToken() {
        return this.renderDollarQuotedStringToken;
    }

    public void setRenderDollarQuotedStringToken(String value) {
        this.renderDollarQuotedStringToken = value;
    }

    public String getNamePathSeparator() {
        return this.namePathSeparator;
    }

    public void setNamePathSeparator(String value) {
        this.namePathSeparator = value;
    }

    public Boolean isBindOffsetDateTimeType() {
        return this.bindOffsetDateTimeType;
    }

    public void setBindOffsetDateTimeType(Boolean value) {
        this.bindOffsetDateTimeType = value;
    }

    public Boolean isBindOffsetTimeType() {
        return this.bindOffsetTimeType;
    }

    public void setBindOffsetTimeType(Boolean value) {
        this.bindOffsetTimeType = value;
    }

    @Deprecated
    public Boolean isFetchTriggerValuesAfterSQLServerOutput() {
        return this.fetchTriggerValuesAfterSQLServerOutput;
    }

    @Deprecated
    public void setFetchTriggerValuesAfterSQLServerOutput(Boolean value) {
        this.fetchTriggerValuesAfterSQLServerOutput = value;
    }

    public FetchTriggerValuesAfterReturning getFetchTriggerValuesAfterReturning() {
        return this.fetchTriggerValuesAfterReturning;
    }

    public void setFetchTriggerValuesAfterReturning(FetchTriggerValuesAfterReturning value) {
        this.fetchTriggerValuesAfterReturning = value;
    }

    public FetchIntermediateResult getFetchIntermediateResult() {
        return this.fetchIntermediateResult;
    }

    public void setFetchIntermediateResult(FetchIntermediateResult value) {
        this.fetchIntermediateResult = value;
    }

    public Boolean isDiagnosticsDuplicateStatements() {
        return this.diagnosticsDuplicateStatements;
    }

    public void setDiagnosticsDuplicateStatements(Boolean value) {
        this.diagnosticsDuplicateStatements = value;
    }

    public Boolean isDiagnosticsDuplicateStatementsUsingTransformPatterns() {
        return this.diagnosticsDuplicateStatementsUsingTransformPatterns;
    }

    public void setDiagnosticsDuplicateStatementsUsingTransformPatterns(Boolean value) {
        this.diagnosticsDuplicateStatementsUsingTransformPatterns = value;
    }

    public Boolean isDiagnosticsMissingWasNullCall() {
        return this.diagnosticsMissingWasNullCall;
    }

    public void setDiagnosticsMissingWasNullCall(Boolean value) {
        this.diagnosticsMissingWasNullCall = value;
    }

    public Boolean isDiagnosticsRepeatedStatements() {
        return this.diagnosticsRepeatedStatements;
    }

    public void setDiagnosticsRepeatedStatements(Boolean value) {
        this.diagnosticsRepeatedStatements = value;
    }

    public Boolean isDiagnosticsConsecutiveAggregation() {
        return this.diagnosticsConsecutiveAggregation;
    }

    public void setDiagnosticsConsecutiveAggregation(Boolean value) {
        this.diagnosticsConsecutiveAggregation = value;
    }

    public Boolean isDiagnosticsConcatenationInPredicate() {
        return this.diagnosticsConcatenationInPredicate;
    }

    public void setDiagnosticsConcatenationInPredicate(Boolean value) {
        this.diagnosticsConcatenationInPredicate = value;
    }

    public Boolean isDiagnosticsPossiblyWrongExpression() {
        return this.diagnosticsPossiblyWrongExpression;
    }

    public void setDiagnosticsPossiblyWrongExpression(Boolean value) {
        this.diagnosticsPossiblyWrongExpression = value;
    }

    public Boolean isDiagnosticsTooManyColumnsFetched() {
        return this.diagnosticsTooManyColumnsFetched;
    }

    public void setDiagnosticsTooManyColumnsFetched(Boolean value) {
        this.diagnosticsTooManyColumnsFetched = value;
    }

    public Boolean isDiagnosticsTooManyRowsFetched() {
        return this.diagnosticsTooManyRowsFetched;
    }

    public void setDiagnosticsTooManyRowsFetched(Boolean value) {
        this.diagnosticsTooManyRowsFetched = value;
    }

    public Boolean isDiagnosticsUnnecessaryWasNullCall() {
        return this.diagnosticsUnnecessaryWasNullCall;
    }

    public void setDiagnosticsUnnecessaryWasNullCall(Boolean value) {
        this.diagnosticsUnnecessaryWasNullCall = value;
    }

    public Boolean isDiagnosticsPatterns() {
        return this.diagnosticsPatterns;
    }

    public void setDiagnosticsPatterns(Boolean value) {
        this.diagnosticsPatterns = value;
    }

    public Boolean isDiagnosticsTrivialCondition() {
        return this.diagnosticsTrivialCondition;
    }

    public void setDiagnosticsTrivialCondition(Boolean value) {
        this.diagnosticsTrivialCondition = value;
    }

    public Boolean isDiagnosticsNullCondition() {
        return this.diagnosticsNullCondition;
    }

    public void setDiagnosticsNullCondition(Boolean value) {
        this.diagnosticsNullCondition = value;
    }

    public Boolean isTransformPatterns() {
        return this.transformPatterns;
    }

    public void setTransformPatterns(Boolean value) {
        this.transformPatterns = value;
    }

    public Boolean isTransformPatternsLogging() {
        return this.transformPatternsLogging;
    }

    public void setTransformPatternsLogging(Boolean value) {
        this.transformPatternsLogging = value;
    }

    public Boolean isTransformPatternsUnnecessaryDistinct() {
        return this.transformPatternsUnnecessaryDistinct;
    }

    public void setTransformPatternsUnnecessaryDistinct(Boolean value) {
        this.transformPatternsUnnecessaryDistinct = value;
    }

    public Boolean isTransformPatternsUnnecessaryScalarSubquery() {
        return this.transformPatternsUnnecessaryScalarSubquery;
    }

    public void setTransformPatternsUnnecessaryScalarSubquery(Boolean value) {
        this.transformPatternsUnnecessaryScalarSubquery = value;
    }

    public Boolean isTransformPatternsUnnecessaryInnerJoin() {
        return this.transformPatternsUnnecessaryInnerJoin;
    }

    public void setTransformPatternsUnnecessaryInnerJoin(Boolean value) {
        this.transformPatternsUnnecessaryInnerJoin = value;
    }

    public Boolean isTransformPatternsUnnecessaryGroupByExpressions() {
        return this.transformPatternsUnnecessaryGroupByExpressions;
    }

    public void setTransformPatternsUnnecessaryGroupByExpressions(Boolean value) {
        this.transformPatternsUnnecessaryGroupByExpressions = value;
    }

    public Boolean isTransformPatternsUnnecessaryOrderByExpressions() {
        return this.transformPatternsUnnecessaryOrderByExpressions;
    }

    public void setTransformPatternsUnnecessaryOrderByExpressions(Boolean value) {
        this.transformPatternsUnnecessaryOrderByExpressions = value;
    }

    public Boolean isTransformPatternsUnnecessaryExistsSubqueryClauses() {
        return this.transformPatternsUnnecessaryExistsSubqueryClauses;
    }

    public void setTransformPatternsUnnecessaryExistsSubqueryClauses(Boolean value) {
        this.transformPatternsUnnecessaryExistsSubqueryClauses = value;
    }

    public Boolean isTransformPatternsCountConstant() {
        return this.transformPatternsCountConstant;
    }

    public void setTransformPatternsCountConstant(Boolean value) {
        this.transformPatternsCountConstant = value;
    }

    public Boolean isTransformPatternsTrim() {
        return this.transformPatternsTrim;
    }

    public void setTransformPatternsTrim(Boolean value) {
        this.transformPatternsTrim = value;
    }

    public Boolean isTransformPatternsNotAnd() {
        return this.transformPatternsNotAnd;
    }

    public void setTransformPatternsNotAnd(Boolean value) {
        this.transformPatternsNotAnd = value;
    }

    public Boolean isTransformPatternsNotOr() {
        return this.transformPatternsNotOr;
    }

    public void setTransformPatternsNotOr(Boolean value) {
        this.transformPatternsNotOr = value;
    }

    public Boolean isTransformPatternsNotNot() {
        return this.transformPatternsNotNot;
    }

    public void setTransformPatternsNotNot(Boolean value) {
        this.transformPatternsNotNot = value;
    }

    public Boolean isTransformPatternsNotComparison() {
        return this.transformPatternsNotComparison;
    }

    public void setTransformPatternsNotComparison(Boolean value) {
        this.transformPatternsNotComparison = value;
    }

    public Boolean isTransformPatternsNotNotDistinct() {
        return this.transformPatternsNotNotDistinct;
    }

    public void setTransformPatternsNotNotDistinct(Boolean value) {
        this.transformPatternsNotNotDistinct = value;
    }

    public Boolean isTransformPatternsDistinctFromNull() {
        return this.transformPatternsDistinctFromNull;
    }

    public void setTransformPatternsDistinctFromNull(Boolean value) {
        this.transformPatternsDistinctFromNull = value;
    }

    public Boolean isTransformPatternsNormaliseAssociativeOps() {
        return this.transformPatternsNormaliseAssociativeOps;
    }

    public void setTransformPatternsNormaliseAssociativeOps(Boolean value) {
        this.transformPatternsNormaliseAssociativeOps = value;
    }

    public Boolean isTransformPatternsNormaliseInListSingleElementToComparison() {
        return this.transformPatternsNormaliseInListSingleElementToComparison;
    }

    public void setTransformPatternsNormaliseInListSingleElementToComparison(Boolean value) {
        this.transformPatternsNormaliseInListSingleElementToComparison = value;
    }

    public Boolean isTransformPatternsNormaliseFieldCompareValue() {
        return this.transformPatternsNormaliseFieldCompareValue;
    }

    public void setTransformPatternsNormaliseFieldCompareValue(Boolean value) {
        this.transformPatternsNormaliseFieldCompareValue = value;
    }

    public Boolean isTransformPatternsNormaliseCoalesceToNvl() {
        return this.transformPatternsNormaliseCoalesceToNvl;
    }

    public void setTransformPatternsNormaliseCoalesceToNvl(Boolean value) {
        this.transformPatternsNormaliseCoalesceToNvl = value;
    }

    public Boolean isTransformPatternsOrEqToIn() {
        return this.transformPatternsOrEqToIn;
    }

    public void setTransformPatternsOrEqToIn(Boolean value) {
        this.transformPatternsOrEqToIn = value;
    }

    public Boolean isTransformPatternsAndNeToNotIn() {
        return this.transformPatternsAndNeToNotIn;
    }

    public void setTransformPatternsAndNeToNotIn(Boolean value) {
        this.transformPatternsAndNeToNotIn = value;
    }

    public Boolean isTransformPatternsMergeOrComparison() {
        return this.transformPatternsMergeOrComparison;
    }

    public void setTransformPatternsMergeOrComparison(Boolean value) {
        this.transformPatternsMergeOrComparison = value;
    }

    public Boolean isTransformPatternsMergeAndComparison() {
        return this.transformPatternsMergeAndComparison;
    }

    public void setTransformPatternsMergeAndComparison(Boolean value) {
        this.transformPatternsMergeAndComparison = value;
    }

    public Boolean isTransformPatternsMergeInLists() {
        return this.transformPatternsMergeInLists;
    }

    public void setTransformPatternsMergeInLists(Boolean value) {
        this.transformPatternsMergeInLists = value;
    }

    public Boolean isTransformPatternsMergeRangePredicates() {
        return this.transformPatternsMergeRangePredicates;
    }

    public void setTransformPatternsMergeRangePredicates(Boolean value) {
        this.transformPatternsMergeRangePredicates = value;
    }

    public Boolean isTransformPatternsMergeBetweenSymmetricPredicates() {
        return this.transformPatternsMergeBetweenSymmetricPredicates;
    }

    public void setTransformPatternsMergeBetweenSymmetricPredicates(Boolean value) {
        this.transformPatternsMergeBetweenSymmetricPredicates = value;
    }

    public Boolean isTransformPatternsCaseSearchedToCaseSimple() {
        return this.transformPatternsCaseSearchedToCaseSimple;
    }

    public void setTransformPatternsCaseSearchedToCaseSimple(Boolean value) {
        this.transformPatternsCaseSearchedToCaseSimple = value;
    }

    public Boolean isTransformPatternsCaseElseNull() {
        return this.transformPatternsCaseElseNull;
    }

    public void setTransformPatternsCaseElseNull(Boolean value) {
        this.transformPatternsCaseElseNull = value;
    }

    public Boolean isTransformPatternsUnreachableCaseClauses() {
        return this.transformPatternsUnreachableCaseClauses;
    }

    public void setTransformPatternsUnreachableCaseClauses(Boolean value) {
        this.transformPatternsUnreachableCaseClauses = value;
    }

    public Boolean isTransformPatternsUnreachableDecodeClauses() {
        return this.transformPatternsUnreachableDecodeClauses;
    }

    public void setTransformPatternsUnreachableDecodeClauses(Boolean value) {
        this.transformPatternsUnreachableDecodeClauses = value;
    }

    public Boolean isTransformPatternsCaseDistinctToDecode() {
        return this.transformPatternsCaseDistinctToDecode;
    }

    public void setTransformPatternsCaseDistinctToDecode(Boolean value) {
        this.transformPatternsCaseDistinctToDecode = value;
    }

    public Boolean isTransformPatternsCaseMergeWhenWhen() {
        return this.transformPatternsCaseMergeWhenWhen;
    }

    public void setTransformPatternsCaseMergeWhenWhen(Boolean value) {
        this.transformPatternsCaseMergeWhenWhen = value;
    }

    public Boolean isTransformPatternsCaseMergeWhenElse() {
        return this.transformPatternsCaseMergeWhenElse;
    }

    public void setTransformPatternsCaseMergeWhenElse(Boolean value) {
        this.transformPatternsCaseMergeWhenElse = value;
    }

    public Boolean isTransformPatternsCaseToCaseAbbreviation() {
        return this.transformPatternsCaseToCaseAbbreviation;
    }

    public void setTransformPatternsCaseToCaseAbbreviation(Boolean value) {
        this.transformPatternsCaseToCaseAbbreviation = value;
    }

    public Boolean isTransformPatternsSimplifyCaseAbbreviation() {
        return this.transformPatternsSimplifyCaseAbbreviation;
    }

    public void setTransformPatternsSimplifyCaseAbbreviation(Boolean value) {
        this.transformPatternsSimplifyCaseAbbreviation = value;
    }

    public Boolean isTransformPatternsFlattenCaseAbbreviation() {
        return this.transformPatternsFlattenCaseAbbreviation;
    }

    public void setTransformPatternsFlattenCaseAbbreviation(Boolean value) {
        this.transformPatternsFlattenCaseAbbreviation = value;
    }

    public Boolean isTransformPatternsFlattenDecode() {
        return this.transformPatternsFlattenDecode;
    }

    public void setTransformPatternsFlattenDecode(Boolean value) {
        this.transformPatternsFlattenDecode = value;
    }

    public Boolean isTransformPatternsFlattenCase() {
        return this.transformPatternsFlattenCase;
    }

    public void setTransformPatternsFlattenCase(Boolean value) {
        this.transformPatternsFlattenCase = value;
    }

    public Boolean isTransformPatternsTrivialCaseAbbreviation() {
        return this.transformPatternsTrivialCaseAbbreviation;
    }

    public void setTransformPatternsTrivialCaseAbbreviation(Boolean value) {
        this.transformPatternsTrivialCaseAbbreviation = value;
    }

    public Boolean isTransformPatternsTrivialPredicates() {
        return this.transformPatternsTrivialPredicates;
    }

    public void setTransformPatternsTrivialPredicates(Boolean value) {
        this.transformPatternsTrivialPredicates = value;
    }

    public Boolean isTransformPatternsTrivialBitwiseOperations() {
        return this.transformPatternsTrivialBitwiseOperations;
    }

    public void setTransformPatternsTrivialBitwiseOperations(Boolean value) {
        this.transformPatternsTrivialBitwiseOperations = value;
    }

    public Boolean isTransformPatternsBitSet() {
        return this.transformPatternsBitSet;
    }

    public void setTransformPatternsBitSet(Boolean value) {
        this.transformPatternsBitSet = value;
    }

    public Boolean isTransformPatternsBitGet() {
        return this.transformPatternsBitGet;
    }

    public void setTransformPatternsBitGet(Boolean value) {
        this.transformPatternsBitGet = value;
    }

    public Boolean isTransformPatternsScalarSubqueryCountAsteriskGtZero() {
        return this.transformPatternsScalarSubqueryCountAsteriskGtZero;
    }

    public void setTransformPatternsScalarSubqueryCountAsteriskGtZero(Boolean value) {
        this.transformPatternsScalarSubqueryCountAsteriskGtZero = value;
    }

    public Boolean isTransformPatternsScalarSubqueryCountExpressionGtZero() {
        return this.transformPatternsScalarSubqueryCountExpressionGtZero;
    }

    public void setTransformPatternsScalarSubqueryCountExpressionGtZero(Boolean value) {
        this.transformPatternsScalarSubqueryCountExpressionGtZero = value;
    }

    public Boolean isTransformPatternsEmptyScalarSubquery() {
        return this.transformPatternsEmptyScalarSubquery;
    }

    public void setTransformPatternsEmptyScalarSubquery(Boolean value) {
        this.transformPatternsEmptyScalarSubquery = value;
    }

    public Boolean isTransformPatternsNegNeg() {
        return this.transformPatternsNegNeg;
    }

    public void setTransformPatternsNegNeg(Boolean value) {
        this.transformPatternsNegNeg = value;
    }

    public Boolean isTransformPatternsBitNotBitNot() {
        return this.transformPatternsBitNotBitNot;
    }

    public void setTransformPatternsBitNotBitNot(Boolean value) {
        this.transformPatternsBitNotBitNot = value;
    }

    public Boolean isTransformPatternsBitNotBitNand() {
        return this.transformPatternsBitNotBitNand;
    }

    public void setTransformPatternsBitNotBitNand(Boolean value) {
        this.transformPatternsBitNotBitNand = value;
    }

    public Boolean isTransformPatternsBitNotBitNor() {
        return this.transformPatternsBitNotBitNor;
    }

    public void setTransformPatternsBitNotBitNor(Boolean value) {
        this.transformPatternsBitNotBitNor = value;
    }

    public Boolean isTransformPatternsBitNotBitXNor() {
        return this.transformPatternsBitNotBitXNor;
    }

    public void setTransformPatternsBitNotBitXNor(Boolean value) {
        this.transformPatternsBitNotBitXNor = value;
    }

    public Boolean isTransformPatternsNullOnNullInput() {
        return this.transformPatternsNullOnNullInput;
    }

    public void setTransformPatternsNullOnNullInput(Boolean value) {
        this.transformPatternsNullOnNullInput = value;
    }

    public Boolean isTransformPatternsIdempotentFunctionRepetition() {
        return this.transformPatternsIdempotentFunctionRepetition;
    }

    public void setTransformPatternsIdempotentFunctionRepetition(Boolean value) {
        this.transformPatternsIdempotentFunctionRepetition = value;
    }

    public Boolean isTransformPatternsArithmeticComparisons() {
        return this.transformPatternsArithmeticComparisons;
    }

    public void setTransformPatternsArithmeticComparisons(Boolean value) {
        this.transformPatternsArithmeticComparisons = value;
    }

    public Boolean isTransformPatternsArithmeticExpressions() {
        return this.transformPatternsArithmeticExpressions;
    }

    public void setTransformPatternsArithmeticExpressions(Boolean value) {
        this.transformPatternsArithmeticExpressions = value;
    }

    public Boolean isTransformPatternsTrigonometricFunctions() {
        return this.transformPatternsTrigonometricFunctions;
    }

    public void setTransformPatternsTrigonometricFunctions(Boolean value) {
        this.transformPatternsTrigonometricFunctions = value;
    }

    public Boolean isTransformPatternsLogarithmicFunctions() {
        return this.transformPatternsLogarithmicFunctions;
    }

    public void setTransformPatternsLogarithmicFunctions(Boolean value) {
        this.transformPatternsLogarithmicFunctions = value;
    }

    public Boolean isTransformPatternsHyperbolicFunctions() {
        return this.transformPatternsHyperbolicFunctions;
    }

    public void setTransformPatternsHyperbolicFunctions(Boolean value) {
        this.transformPatternsHyperbolicFunctions = value;
    }

    public Boolean isTransformPatternsInverseHyperbolicFunctions() {
        return this.transformPatternsInverseHyperbolicFunctions;
    }

    public void setTransformPatternsInverseHyperbolicFunctions(Boolean value) {
        this.transformPatternsInverseHyperbolicFunctions = value;
    }

    public Boolean isTransformInlineBindValuesForFieldComparisons() {
        return this.transformInlineBindValuesForFieldComparisons;
    }

    public void setTransformInlineBindValuesForFieldComparisons(Boolean value) {
        this.transformInlineBindValuesForFieldComparisons = value;
    }

    public Boolean isTransformAnsiJoinToTableLists() {
        return this.transformAnsiJoinToTableLists;
    }

    public void setTransformAnsiJoinToTableLists(Boolean value) {
        this.transformAnsiJoinToTableLists = value;
    }

    @Deprecated
    public Transformation getTransformInConditionSubqueryWithLimitToDerivedTable() {
        return this.transformInConditionSubqueryWithLimitToDerivedTable;
    }

    @Deprecated
    public void setTransformInConditionSubqueryWithLimitToDerivedTable(Transformation value) {
        this.transformInConditionSubqueryWithLimitToDerivedTable = value;
    }

    public Transformation getTransformQualify() {
        return this.transformQualify;
    }

    public void setTransformQualify(Transformation value) {
        this.transformQualify = value;
    }

    public Boolean isTransformTableListsToAnsiJoin() {
        return this.transformTableListsToAnsiJoin;
    }

    public void setTransformTableListsToAnsiJoin(Boolean value) {
        this.transformTableListsToAnsiJoin = value;
    }

    public Transformation getTransformRownum() {
        return this.transformRownum;
    }

    public void setTransformRownum(Transformation value) {
        this.transformRownum = value;
    }

    public TransformUnneededArithmeticExpressions getTransformUnneededArithmeticExpressions() {
        return this.transformUnneededArithmeticExpressions;
    }

    public void setTransformUnneededArithmeticExpressions(TransformUnneededArithmeticExpressions value) {
        this.transformUnneededArithmeticExpressions = value;
    }

    public Transformation getTransformGroupByColumnIndex() {
        return this.transformGroupByColumnIndex;
    }

    public void setTransformGroupByColumnIndex(Transformation value) {
        this.transformGroupByColumnIndex = value;
    }

    public Transformation getTransformInlineCTE() {
        return this.transformInlineCTE;
    }

    public void setTransformInlineCTE(Transformation value) {
        this.transformInlineCTE = value;
    }

    public BackslashEscaping getBackslashEscaping() {
        return this.backslashEscaping;
    }

    public void setBackslashEscaping(BackslashEscaping value) {
        this.backslashEscaping = value;
    }

    public ParamType getParamType() {
        return this.paramType;
    }

    public void setParamType(ParamType value) {
        this.paramType = value;
    }

    public ParamCastMode getParamCastMode() {
        return this.paramCastMode;
    }

    public void setParamCastMode(ParamCastMode value) {
        this.paramCastMode = value;
    }

    public StatementType getStatementType() {
        return this.statementType;
    }

    public void setStatementType(StatementType value) {
        this.statementType = value;
    }

    public Integer getInlineThreshold() {
        return this.inlineThreshold;
    }

    public void setInlineThreshold(Integer value) {
        this.inlineThreshold = value;
    }

    public InvocationOrder getTransactionListenerStartInvocationOrder() {
        return this.transactionListenerStartInvocationOrder;
    }

    public void setTransactionListenerStartInvocationOrder(InvocationOrder value) {
        this.transactionListenerStartInvocationOrder = value;
    }

    public InvocationOrder getTransactionListenerEndInvocationOrder() {
        return this.transactionListenerEndInvocationOrder;
    }

    public void setTransactionListenerEndInvocationOrder(InvocationOrder value) {
        this.transactionListenerEndInvocationOrder = value;
    }

    public InvocationOrder getMigrationListenerStartInvocationOrder() {
        return this.migrationListenerStartInvocationOrder;
    }

    public void setMigrationListenerStartInvocationOrder(InvocationOrder value) {
        this.migrationListenerStartInvocationOrder = value;
    }

    public InvocationOrder getMigrationListenerEndInvocationOrder() {
        return this.migrationListenerEndInvocationOrder;
    }

    public void setMigrationListenerEndInvocationOrder(InvocationOrder value) {
        this.migrationListenerEndInvocationOrder = value;
    }

    public InvocationOrder getVisitListenerStartInvocationOrder() {
        return this.visitListenerStartInvocationOrder;
    }

    public void setVisitListenerStartInvocationOrder(InvocationOrder value) {
        this.visitListenerStartInvocationOrder = value;
    }

    public InvocationOrder getVisitListenerEndInvocationOrder() {
        return this.visitListenerEndInvocationOrder;
    }

    public void setVisitListenerEndInvocationOrder(InvocationOrder value) {
        this.visitListenerEndInvocationOrder = value;
    }

    public InvocationOrder getRecordListenerStartInvocationOrder() {
        return this.recordListenerStartInvocationOrder;
    }

    public void setRecordListenerStartInvocationOrder(InvocationOrder value) {
        this.recordListenerStartInvocationOrder = value;
    }

    public InvocationOrder getRecordListenerEndInvocationOrder() {
        return this.recordListenerEndInvocationOrder;
    }

    public void setRecordListenerEndInvocationOrder(InvocationOrder value) {
        this.recordListenerEndInvocationOrder = value;
    }

    public InvocationOrder getExecuteListenerStartInvocationOrder() {
        return this.executeListenerStartInvocationOrder;
    }

    public void setExecuteListenerStartInvocationOrder(InvocationOrder value) {
        this.executeListenerStartInvocationOrder = value;
    }

    public InvocationOrder getExecuteListenerEndInvocationOrder() {
        return this.executeListenerEndInvocationOrder;
    }

    public void setExecuteListenerEndInvocationOrder(InvocationOrder value) {
        this.executeListenerEndInvocationOrder = value;
    }

    public Boolean isExecuteLogging() {
        return this.executeLogging;
    }

    public void setExecuteLogging(Boolean value) {
        this.executeLogging = value;
    }

    public Boolean isExecuteLoggingSQLExceptions() {
        return this.executeLoggingSQLExceptions;
    }

    public void setExecuteLoggingSQLExceptions(Boolean value) {
        this.executeLoggingSQLExceptions = value;
    }

    public Boolean isDiagnosticsLogging() {
        return this.diagnosticsLogging;
    }

    public void setDiagnosticsLogging(Boolean value) {
        this.diagnosticsLogging = value;
    }

    public DiagnosticsConnection getDiagnosticsConnection() {
        return this.diagnosticsConnection;
    }

    public void setDiagnosticsConnection(DiagnosticsConnection value) {
        this.diagnosticsConnection = value;
    }

    public Boolean isUpdateRecordVersion() {
        return this.updateRecordVersion;
    }

    public void setUpdateRecordVersion(Boolean value) {
        this.updateRecordVersion = value;
    }

    public Boolean isUpdateRecordTimestamp() {
        return this.updateRecordTimestamp;
    }

    public void setUpdateRecordTimestamp(Boolean value) {
        this.updateRecordTimestamp = value;
    }

    public Boolean isExecuteWithOptimisticLocking() {
        return this.executeWithOptimisticLocking;
    }

    public void setExecuteWithOptimisticLocking(Boolean value) {
        this.executeWithOptimisticLocking = value;
    }

    public Boolean isExecuteWithOptimisticLockingExcludeUnversioned() {
        return this.executeWithOptimisticLockingExcludeUnversioned;
    }

    public void setExecuteWithOptimisticLockingExcludeUnversioned(Boolean value) {
        this.executeWithOptimisticLockingExcludeUnversioned = value;
    }

    public Boolean isAttachRecords() {
        return this.attachRecords;
    }

    public void setAttachRecords(Boolean value) {
        this.attachRecords = value;
    }

    public Boolean isInsertUnchangedRecords() {
        return this.insertUnchangedRecords;
    }

    public void setInsertUnchangedRecords(Boolean value) {
        this.insertUnchangedRecords = value;
    }

    public UpdateUnchangedRecords getUpdateUnchangedRecords() {
        return this.updateUnchangedRecords;
    }

    public void setUpdateUnchangedRecords(UpdateUnchangedRecords value) {
        this.updateUnchangedRecords = value;
    }

    public Boolean isUpdatablePrimaryKeys() {
        return this.updatablePrimaryKeys;
    }

    public void setUpdatablePrimaryKeys(Boolean value) {
        this.updatablePrimaryKeys = value;
    }

    public Boolean isReflectionCaching() {
        return this.reflectionCaching;
    }

    public void setReflectionCaching(Boolean value) {
        this.reflectionCaching = value;
    }

    public Boolean isCacheRecordMappers() {
        return this.cacheRecordMappers;
    }

    public void setCacheRecordMappers(Boolean value) {
        this.cacheRecordMappers = value;
    }

    public Boolean isCacheParsingConnection() {
        return this.cacheParsingConnection;
    }

    public void setCacheParsingConnection(Boolean value) {
        this.cacheParsingConnection = value;
    }

    public Integer getCacheParsingConnectionLRUCacheSize() {
        return this.cacheParsingConnectionLRUCacheSize;
    }

    public void setCacheParsingConnectionLRUCacheSize(Integer value) {
        this.cacheParsingConnectionLRUCacheSize = value;
    }

    public Boolean isCachePreparedStatementInLoader() {
        return this.cachePreparedStatementInLoader;
    }

    public void setCachePreparedStatementInLoader(Boolean value) {
        this.cachePreparedStatementInLoader = value;
    }

    public ThrowExceptions getThrowExceptions() {
        return this.throwExceptions;
    }

    public void setThrowExceptions(ThrowExceptions value) {
        this.throwExceptions = value;
    }

    public Boolean isFetchWarnings() {
        return this.fetchWarnings;
    }

    public void setFetchWarnings(Boolean value) {
        this.fetchWarnings = value;
    }

    public Integer getFetchServerOutputSize() {
        return this.fetchServerOutputSize;
    }

    public void setFetchServerOutputSize(Integer value) {
        this.fetchServerOutputSize = value;
    }

    public Boolean isReturnIdentityOnUpdatableRecord() {
        return this.returnIdentityOnUpdatableRecord;
    }

    public void setReturnIdentityOnUpdatableRecord(Boolean value) {
        this.returnIdentityOnUpdatableRecord = value;
    }

    public Boolean isReturnDefaultOnUpdatableRecord() {
        return this.returnDefaultOnUpdatableRecord;
    }

    public void setReturnDefaultOnUpdatableRecord(Boolean value) {
        this.returnDefaultOnUpdatableRecord = value;
    }

    public Boolean isReturnComputedOnUpdatableRecord() {
        return this.returnComputedOnUpdatableRecord;
    }

    public void setReturnComputedOnUpdatableRecord(Boolean value) {
        this.returnComputedOnUpdatableRecord = value;
    }

    public Boolean isReturnAllOnUpdatableRecord() {
        return this.returnAllOnUpdatableRecord;
    }

    public void setReturnAllOnUpdatableRecord(Boolean value) {
        this.returnAllOnUpdatableRecord = value;
    }

    public Boolean isReturnRecordToPojo() {
        return this.returnRecordToPojo;
    }

    public void setReturnRecordToPojo(Boolean value) {
        this.returnRecordToPojo = value;
    }

    public Boolean isMapJPAAnnotations() {
        return this.mapJPAAnnotations;
    }

    public void setMapJPAAnnotations(Boolean value) {
        this.mapJPAAnnotations = value;
    }

    public Boolean isMapRecordComponentParameterNames() {
        return this.mapRecordComponentParameterNames;
    }

    public void setMapRecordComponentParameterNames(Boolean value) {
        this.mapRecordComponentParameterNames = value;
    }

    public Boolean isMapConstructorPropertiesParameterNames() {
        return this.mapConstructorPropertiesParameterNames;
    }

    public void setMapConstructorPropertiesParameterNames(Boolean value) {
        this.mapConstructorPropertiesParameterNames = value;
    }

    public Boolean isMapConstructorParameterNames() {
        return this.mapConstructorParameterNames;
    }

    public void setMapConstructorParameterNames(Boolean value) {
        this.mapConstructorParameterNames = value;
    }

    public Boolean isMapConstructorParameterNamesInKotlin() {
        return this.mapConstructorParameterNamesInKotlin;
    }

    public void setMapConstructorParameterNamesInKotlin(Boolean value) {
        this.mapConstructorParameterNamesInKotlin = value;
    }

    public QueryPoolable getQueryPoolable() {
        return this.queryPoolable;
    }

    public void setQueryPoolable(QueryPoolable value) {
        this.queryPoolable = value;
    }

    public Integer getQueryTimeout() {
        return this.queryTimeout;
    }

    public void setQueryTimeout(Integer value) {
        this.queryTimeout = value;
    }

    public Integer getMaxRows() {
        return this.maxRows;
    }

    public void setMaxRows(Integer value) {
        this.maxRows = value;
    }

    public Integer getFetchSize() {
        return this.fetchSize;
    }

    public void setFetchSize(Integer value) {
        this.fetchSize = value;
    }

    public Integer getBatchSize() {
        return this.batchSize;
    }

    public void setBatchSize(Integer value) {
        this.batchSize = value;
    }

    public Boolean isDebugInfoOnStackTrace() {
        return this.debugInfoOnStackTrace;
    }

    public void setDebugInfoOnStackTrace(Boolean value) {
        this.debugInfoOnStackTrace = value;
    }

    public Boolean isInListPadding() {
        return this.inListPadding;
    }

    public void setInListPadding(Boolean value) {
        this.inListPadding = value;
    }

    public Integer getInListPadBase() {
        return this.inListPadBase;
    }

    public void setInListPadBase(Integer value) {
        this.inListPadBase = value;
    }

    public String getDelimiter() {
        return this.delimiter;
    }

    public void setDelimiter(String value) {
        this.delimiter = value;
    }

    public Boolean isEmulateOnDuplicateKeyUpdateOnPrimaryKeyOnly() {
        return this.emulateOnDuplicateKeyUpdateOnPrimaryKeyOnly;
    }

    public void setEmulateOnDuplicateKeyUpdateOnPrimaryKeyOnly(Boolean value) {
        this.emulateOnDuplicateKeyUpdateOnPrimaryKeyOnly = value;
    }

    public NestedCollectionEmulation getEmulateMultiset() {
        return this.emulateMultiset;
    }

    public void setEmulateMultiset(NestedCollectionEmulation value) {
        this.emulateMultiset = value;
    }

    public Boolean isEmulateComputedColumns() {
        return this.emulateComputedColumns;
    }

    public void setEmulateComputedColumns(Boolean value) {
        this.emulateComputedColumns = value;
    }

    public ExecuteWithoutWhere getExecuteUpdateWithoutWhere() {
        return this.executeUpdateWithoutWhere;
    }

    public void setExecuteUpdateWithoutWhere(ExecuteWithoutWhere value) {
        this.executeUpdateWithoutWhere = value;
    }

    public ExecuteWithoutWhere getExecuteDeleteWithoutWhere() {
        return this.executeDeleteWithoutWhere;
    }

    public void setExecuteDeleteWithoutWhere(ExecuteWithoutWhere value) {
        this.executeDeleteWithoutWhere = value;
    }

    public SQLDialect getInterpreterDialect() {
        return this.interpreterDialect;
    }

    public void setInterpreterDialect(SQLDialect value) {
        this.interpreterDialect = value;
    }

    public InterpreterNameLookupCaseSensitivity getInterpreterNameLookupCaseSensitivity() {
        return this.interpreterNameLookupCaseSensitivity;
    }

    public void setInterpreterNameLookupCaseSensitivity(InterpreterNameLookupCaseSensitivity value) {
        this.interpreterNameLookupCaseSensitivity = value;
    }

    public Locale getInterpreterLocale() {
        return this.interpreterLocale;
    }

    public void setInterpreterLocale(Locale value) {
        this.interpreterLocale = value;
    }

    public Boolean isInterpreterDelayForeignKeyDeclarations() {
        return this.interpreterDelayForeignKeyDeclarations;
    }

    public void setInterpreterDelayForeignKeyDeclarations(Boolean value) {
        this.interpreterDelayForeignKeyDeclarations = value;
    }

    public Boolean isMetaIncludeSystemIndexes() {
        return this.metaIncludeSystemIndexes;
    }

    public void setMetaIncludeSystemIndexes(Boolean value) {
        this.metaIncludeSystemIndexes = value;
    }

    public Boolean isMetaIncludeSystemSequences() {
        return this.metaIncludeSystemSequences;
    }

    public void setMetaIncludeSystemSequences(Boolean value) {
        this.metaIncludeSystemSequences = value;
    }

    public MigrationSchema getMigrationHistorySchema() {
        return this.migrationHistorySchema;
    }

    public void setMigrationHistorySchema(MigrationSchema value) {
        this.migrationHistorySchema = value;
    }

    public Boolean isMigrationHistorySchemaCreateSchemaIfNotExists() {
        return this.migrationHistorySchemaCreateSchemaIfNotExists;
    }

    public void setMigrationHistorySchemaCreateSchemaIfNotExists(Boolean value) {
        this.migrationHistorySchemaCreateSchemaIfNotExists = value;
    }

    public MigrationSchema getMigrationDefaultSchema() {
        return this.migrationDefaultSchema;
    }

    public void setMigrationDefaultSchema(MigrationSchema value) {
        this.migrationDefaultSchema = value;
    }

    public Boolean isMigrationSchemataCreateSchemaIfNotExists() {
        return this.migrationSchemataCreateSchemaIfNotExists;
    }

    public void setMigrationSchemataCreateSchemaIfNotExists(Boolean value) {
        this.migrationSchemataCreateSchemaIfNotExists = value;
    }

    public Boolean isMigrationAllowsUndo() {
        return this.migrationAllowsUndo;
    }

    public void setMigrationAllowsUndo(Boolean value) {
        this.migrationAllowsUndo = value;
    }

    public Boolean isMigrationRevertUntracked() {
        return this.migrationRevertUntracked;
    }

    public void setMigrationRevertUntracked(Boolean value) {
        this.migrationRevertUntracked = value;
    }

    public Boolean isMigrationAutoBaseline() {
        return this.migrationAutoBaseline;
    }

    public void setMigrationAutoBaseline(Boolean value) {
        this.migrationAutoBaseline = value;
    }

    public Boolean isMigrationAutoVerification() {
        return this.migrationAutoVerification;
    }

    public void setMigrationAutoVerification(Boolean value) {
        this.migrationAutoVerification = value;
    }

    public Boolean isMigrationIgnoreDefaultTimestampPrecisionDiffs() {
        return this.migrationIgnoreDefaultTimestampPrecisionDiffs;
    }

    public void setMigrationIgnoreDefaultTimestampPrecisionDiffs(Boolean value) {
        this.migrationIgnoreDefaultTimestampPrecisionDiffs = value;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale value) {
        this.locale = value;
    }

    public SQLDialect getParseDialect() {
        return this.parseDialect;
    }

    public void setParseDialect(SQLDialect value) {
        this.parseDialect = value;
    }

    public Locale getParseLocale() {
        return this.parseLocale;
    }

    public void setParseLocale(Locale value) {
        this.parseLocale = value;
    }

    public String getParseDateFormat() {
        return this.parseDateFormat;
    }

    public void setParseDateFormat(String value) {
        this.parseDateFormat = value;
    }

    public String getParseTimestampFormat() {
        return this.parseTimestampFormat;
    }

    public void setParseTimestampFormat(String value) {
        this.parseTimestampFormat = value;
    }

    public String getParseNamedParamPrefix() {
        return this.parseNamedParamPrefix;
    }

    public void setParseNamedParamPrefix(String value) {
        this.parseNamedParamPrefix = value;
    }

    public ParseNameCase getParseNameCase() {
        return this.parseNameCase;
    }

    public void setParseNameCase(ParseNameCase value) {
        this.parseNameCase = value;
    }

    public ParseWithMetaLookups getParseWithMetaLookups() {
        return this.parseWithMetaLookups;
    }

    public void setParseWithMetaLookups(ParseWithMetaLookups value) {
        this.parseWithMetaLookups = value;
    }

    public Transformation getParseAppendMissingTableReferences() {
        return this.parseAppendMissingTableReferences;
    }

    public void setParseAppendMissingTableReferences(Transformation value) {
        this.parseAppendMissingTableReferences = value;
    }

    public Boolean isParseSetCommands() {
        return this.parseSetCommands;
    }

    public void setParseSetCommands(Boolean value) {
        this.parseSetCommands = value;
    }

    public ParseUnsupportedSyntax getParseUnsupportedSyntax() {
        return this.parseUnsupportedSyntax;
    }

    public void setParseUnsupportedSyntax(ParseUnsupportedSyntax value) {
        this.parseUnsupportedSyntax = value;
    }

    public ParseUnknownFunctions getParseUnknownFunctions() {
        return this.parseUnknownFunctions;
    }

    public void setParseUnknownFunctions(ParseUnknownFunctions value) {
        this.parseUnknownFunctions = value;
    }

    public Boolean isParseIgnoreCommercialOnlyFeatures() {
        return this.parseIgnoreCommercialOnlyFeatures;
    }

    public void setParseIgnoreCommercialOnlyFeatures(Boolean value) {
        this.parseIgnoreCommercialOnlyFeatures = value;
    }

    public Boolean isParseIgnoreComments() {
        return this.parseIgnoreComments;
    }

    public void setParseIgnoreComments(Boolean value) {
        this.parseIgnoreComments = value;
    }

    public String getParseIgnoreCommentStart() {
        return this.parseIgnoreCommentStart;
    }

    public void setParseIgnoreCommentStart(String value) {
        this.parseIgnoreCommentStart = value;
    }

    public String getParseIgnoreCommentStop() {
        return this.parseIgnoreCommentStop;
    }

    public void setParseIgnoreCommentStop(String value) {
        this.parseIgnoreCommentStop = value;
    }

    public Boolean isParseRetainCommentsBetweenQueries() {
        return this.parseRetainCommentsBetweenQueries;
    }

    public void setParseRetainCommentsBetweenQueries(Boolean value) {
        this.parseRetainCommentsBetweenQueries = value;
    }

    public Boolean isParseMetaDefaultExpressions() {
        return this.parseMetaDefaultExpressions;
    }

    public void setParseMetaDefaultExpressions(Boolean value) {
        this.parseMetaDefaultExpressions = value;
    }

    public Boolean isParseMetaViewSources() {
        return this.parseMetaViewSources;
    }

    public void setParseMetaViewSources(Boolean value) {
        this.parseMetaViewSources = value;
    }

    public WriteIfReadonly getReadonlyTableRecordInsert() {
        return this.readonlyTableRecordInsert;
    }

    public void setReadonlyTableRecordInsert(WriteIfReadonly value) {
        this.readonlyTableRecordInsert = value;
    }

    public WriteIfReadonly getReadonlyUpdatableRecordUpdate() {
        return this.readonlyUpdatableRecordUpdate;
    }

    public void setReadonlyUpdatableRecordUpdate(WriteIfReadonly value) {
        this.readonlyUpdatableRecordUpdate = value;
    }

    public WriteIfReadonly getReadonlyInsert() {
        return this.readonlyInsert;
    }

    public void setReadonlyInsert(WriteIfReadonly value) {
        this.readonlyInsert = value;
    }

    public WriteIfReadonly getReadonlyUpdate() {
        return this.readonlyUpdate;
    }

    public void setReadonlyUpdate(WriteIfReadonly value) {
        this.readonlyUpdate = value;
    }

    public Boolean isApplyWorkaroundFor7962() {
        return this.applyWorkaroundFor7962;
    }

    public void setApplyWorkaroundFor7962(Boolean value) {
        this.applyWorkaroundFor7962 = value;
    }

    public Warning getWarnOnStaticTypeRegistryAccess() {
        return this.warnOnStaticTypeRegistryAccess;
    }

    public void setWarnOnStaticTypeRegistryAccess(Warning value) {
        this.warnOnStaticTypeRegistryAccess = value;
    }

    public List<InterpreterSearchSchema> getInterpreterSearchPath() {
        if (this.interpreterSearchPath == null) {
            this.interpreterSearchPath = new ArrayList();
        }
        return this.interpreterSearchPath;
    }

    public void setInterpreterSearchPath(List<InterpreterSearchSchema> interpreterSearchPath) {
        this.interpreterSearchPath = interpreterSearchPath;
    }

    public List<MigrationSchema> getMigrationSchemata() {
        if (this.migrationSchemata == null) {
            this.migrationSchemata = new ArrayList();
        }
        return this.migrationSchemata;
    }

    public void setMigrationSchemata(List<MigrationSchema> migrationSchemata) {
        this.migrationSchemata = migrationSchemata;
    }

    public List<ParseSearchSchema> getParseSearchPath() {
        if (this.parseSearchPath == null) {
            this.parseSearchPath = new ArrayList();
        }
        return this.parseSearchPath;
    }

    public void setParseSearchPath(List<ParseSearchSchema> parseSearchPath) {
        this.parseSearchPath = parseSearchPath;
    }

    public Settings withForceIntegerTypesOnZeroScaleDecimals(Boolean value) {
        setForceIntegerTypesOnZeroScaleDecimals(value);
        return this;
    }

    public Settings withRenderCatalog(Boolean value) {
        setRenderCatalog(value);
        return this;
    }

    public Settings withRenderSchema(Boolean value) {
        setRenderSchema(value);
        return this;
    }

    public Settings withRenderTable(RenderTable value) {
        setRenderTable(value);
        return this;
    }

    public Settings withRenderMapping(RenderMapping value) {
        setRenderMapping(value);
        return this;
    }

    public Settings withRenderQuotedNames(RenderQuotedNames value) {
        setRenderQuotedNames(value);
        return this;
    }

    public Settings withRenderNameCase(RenderNameCase value) {
        setRenderNameCase(value);
        return this;
    }

    @Deprecated
    public Settings withRenderNameStyle(RenderNameStyle value) {
        setRenderNameStyle(value);
        return this;
    }

    public Settings withRenderNamedParamPrefix(String value) {
        setRenderNamedParamPrefix(value);
        return this;
    }

    public Settings withRenderKeywordCase(RenderKeywordCase value) {
        setRenderKeywordCase(value);
        return this;
    }

    @Deprecated
    public Settings withRenderKeywordStyle(RenderKeywordStyle value) {
        setRenderKeywordStyle(value);
        return this;
    }

    public Settings withRenderLocale(Locale value) {
        setRenderLocale(value);
        return this;
    }

    public Settings withRenderFormatted(Boolean value) {
        setRenderFormatted(value);
        return this;
    }

    public Settings withRenderFormatting(RenderFormatting value) {
        setRenderFormatting(value);
        return this;
    }

    public Settings withRenderOptionalAssociativityParentheses(RenderOptionalKeyword value) {
        setRenderOptionalAssociativityParentheses(value);
        return this;
    }

    public Settings withRenderOptionalAsKeywordForTableAliases(RenderOptionalKeyword value) {
        setRenderOptionalAsKeywordForTableAliases(value);
        return this;
    }

    public Settings withRenderOptionalAsKeywordForFieldAliases(RenderOptionalKeyword value) {
        setRenderOptionalAsKeywordForFieldAliases(value);
        return this;
    }

    public Settings withRenderOptionalInnerKeyword(RenderOptionalKeyword value) {
        setRenderOptionalInnerKeyword(value);
        return this;
    }

    public Settings withRenderOptionalOuterKeyword(RenderOptionalKeyword value) {
        setRenderOptionalOuterKeyword(value);
        return this;
    }

    public Settings withRenderImplicitWindowRange(RenderImplicitWindowRange value) {
        setRenderImplicitWindowRange(value);
        return this;
    }

    public Settings withRenderScalarSubqueriesForStoredFunctions(Boolean value) {
        setRenderScalarSubqueriesForStoredFunctions(value);
        return this;
    }

    public Settings withRenderImplicitJoinType(RenderImplicitJoinType value) {
        setRenderImplicitJoinType(value);
        return this;
    }

    public Settings withRenderImplicitJoinToManyType(RenderImplicitJoinType value) {
        setRenderImplicitJoinToManyType(value);
        return this;
    }

    public Settings withRenderDefaultNullability(RenderDefaultNullability value) {
        setRenderDefaultNullability(value);
        return this;
    }

    public Settings withRenderCoalesceToEmptyStringInConcat(Boolean value) {
        setRenderCoalesceToEmptyStringInConcat(value);
        return this;
    }

    public Settings withRenderOrderByRownumberForEmulatedPagination(Boolean value) {
        setRenderOrderByRownumberForEmulatedPagination(value);
        return this;
    }

    public Settings withRenderOutputForSQLServerReturningClause(Boolean value) {
        setRenderOutputForSQLServerReturningClause(value);
        return this;
    }

    public Settings withRenderGroupConcatMaxLenSessionVariable(Boolean value) {
        setRenderGroupConcatMaxLenSessionVariable(value);
        return this;
    }

    public Settings withRenderParenthesisAroundSetOperationQueries(Boolean value) {
        setRenderParenthesisAroundSetOperationQueries(value);
        return this;
    }

    public Settings withRenderVariablesInDerivedTablesForEmulations(Boolean value) {
        setRenderVariablesInDerivedTablesForEmulations(value);
        return this;
    }

    public Settings withRenderRowConditionForSeekClause(Boolean value) {
        setRenderRowConditionForSeekClause(value);
        return this;
    }

    public Settings withRenderRedundantConditionForSeekClause(Boolean value) {
        setRenderRedundantConditionForSeekClause(value);
        return this;
    }

    public Settings withRenderPlainSQLTemplatesAsRaw(Boolean value) {
        setRenderPlainSQLTemplatesAsRaw(value);
        return this;
    }

    public Settings withRenderDollarQuotedStringToken(String value) {
        setRenderDollarQuotedStringToken(value);
        return this;
    }

    public Settings withNamePathSeparator(String value) {
        setNamePathSeparator(value);
        return this;
    }

    public Settings withBindOffsetDateTimeType(Boolean value) {
        setBindOffsetDateTimeType(value);
        return this;
    }

    public Settings withBindOffsetTimeType(Boolean value) {
        setBindOffsetTimeType(value);
        return this;
    }

    public Settings withFetchTriggerValuesAfterSQLServerOutput(Boolean value) {
        setFetchTriggerValuesAfterSQLServerOutput(value);
        return this;
    }

    public Settings withFetchTriggerValuesAfterReturning(FetchTriggerValuesAfterReturning value) {
        setFetchTriggerValuesAfterReturning(value);
        return this;
    }

    public Settings withFetchIntermediateResult(FetchIntermediateResult value) {
        setFetchIntermediateResult(value);
        return this;
    }

    public Settings withDiagnosticsDuplicateStatements(Boolean value) {
        setDiagnosticsDuplicateStatements(value);
        return this;
    }

    public Settings withDiagnosticsDuplicateStatementsUsingTransformPatterns(Boolean value) {
        setDiagnosticsDuplicateStatementsUsingTransformPatterns(value);
        return this;
    }

    public Settings withDiagnosticsMissingWasNullCall(Boolean value) {
        setDiagnosticsMissingWasNullCall(value);
        return this;
    }

    public Settings withDiagnosticsRepeatedStatements(Boolean value) {
        setDiagnosticsRepeatedStatements(value);
        return this;
    }

    public Settings withDiagnosticsConsecutiveAggregation(Boolean value) {
        setDiagnosticsConsecutiveAggregation(value);
        return this;
    }

    public Settings withDiagnosticsConcatenationInPredicate(Boolean value) {
        setDiagnosticsConcatenationInPredicate(value);
        return this;
    }

    public Settings withDiagnosticsPossiblyWrongExpression(Boolean value) {
        setDiagnosticsPossiblyWrongExpression(value);
        return this;
    }

    public Settings withDiagnosticsTooManyColumnsFetched(Boolean value) {
        setDiagnosticsTooManyColumnsFetched(value);
        return this;
    }

    public Settings withDiagnosticsTooManyRowsFetched(Boolean value) {
        setDiagnosticsTooManyRowsFetched(value);
        return this;
    }

    public Settings withDiagnosticsUnnecessaryWasNullCall(Boolean value) {
        setDiagnosticsUnnecessaryWasNullCall(value);
        return this;
    }

    public Settings withDiagnosticsPatterns(Boolean value) {
        setDiagnosticsPatterns(value);
        return this;
    }

    public Settings withDiagnosticsTrivialCondition(Boolean value) {
        setDiagnosticsTrivialCondition(value);
        return this;
    }

    public Settings withDiagnosticsNullCondition(Boolean value) {
        setDiagnosticsNullCondition(value);
        return this;
    }

    public Settings withTransformPatterns(Boolean value) {
        setTransformPatterns(value);
        return this;
    }

    public Settings withTransformPatternsLogging(Boolean value) {
        setTransformPatternsLogging(value);
        return this;
    }

    public Settings withTransformPatternsUnnecessaryDistinct(Boolean value) {
        setTransformPatternsUnnecessaryDistinct(value);
        return this;
    }

    public Settings withTransformPatternsUnnecessaryScalarSubquery(Boolean value) {
        setTransformPatternsUnnecessaryScalarSubquery(value);
        return this;
    }

    public Settings withTransformPatternsUnnecessaryInnerJoin(Boolean value) {
        setTransformPatternsUnnecessaryInnerJoin(value);
        return this;
    }

    public Settings withTransformPatternsUnnecessaryGroupByExpressions(Boolean value) {
        setTransformPatternsUnnecessaryGroupByExpressions(value);
        return this;
    }

    public Settings withTransformPatternsUnnecessaryOrderByExpressions(Boolean value) {
        setTransformPatternsUnnecessaryOrderByExpressions(value);
        return this;
    }

    public Settings withTransformPatternsUnnecessaryExistsSubqueryClauses(Boolean value) {
        setTransformPatternsUnnecessaryExistsSubqueryClauses(value);
        return this;
    }

    public Settings withTransformPatternsCountConstant(Boolean value) {
        setTransformPatternsCountConstant(value);
        return this;
    }

    public Settings withTransformPatternsTrim(Boolean value) {
        setTransformPatternsTrim(value);
        return this;
    }

    public Settings withTransformPatternsNotAnd(Boolean value) {
        setTransformPatternsNotAnd(value);
        return this;
    }

    public Settings withTransformPatternsNotOr(Boolean value) {
        setTransformPatternsNotOr(value);
        return this;
    }

    public Settings withTransformPatternsNotNot(Boolean value) {
        setTransformPatternsNotNot(value);
        return this;
    }

    public Settings withTransformPatternsNotComparison(Boolean value) {
        setTransformPatternsNotComparison(value);
        return this;
    }

    public Settings withTransformPatternsNotNotDistinct(Boolean value) {
        setTransformPatternsNotNotDistinct(value);
        return this;
    }

    public Settings withTransformPatternsDistinctFromNull(Boolean value) {
        setTransformPatternsDistinctFromNull(value);
        return this;
    }

    public Settings withTransformPatternsNormaliseAssociativeOps(Boolean value) {
        setTransformPatternsNormaliseAssociativeOps(value);
        return this;
    }

    public Settings withTransformPatternsNormaliseInListSingleElementToComparison(Boolean value) {
        setTransformPatternsNormaliseInListSingleElementToComparison(value);
        return this;
    }

    public Settings withTransformPatternsNormaliseFieldCompareValue(Boolean value) {
        setTransformPatternsNormaliseFieldCompareValue(value);
        return this;
    }

    public Settings withTransformPatternsNormaliseCoalesceToNvl(Boolean value) {
        setTransformPatternsNormaliseCoalesceToNvl(value);
        return this;
    }

    public Settings withTransformPatternsOrEqToIn(Boolean value) {
        setTransformPatternsOrEqToIn(value);
        return this;
    }

    public Settings withTransformPatternsAndNeToNotIn(Boolean value) {
        setTransformPatternsAndNeToNotIn(value);
        return this;
    }

    public Settings withTransformPatternsMergeOrComparison(Boolean value) {
        setTransformPatternsMergeOrComparison(value);
        return this;
    }

    public Settings withTransformPatternsMergeAndComparison(Boolean value) {
        setTransformPatternsMergeAndComparison(value);
        return this;
    }

    public Settings withTransformPatternsMergeInLists(Boolean value) {
        setTransformPatternsMergeInLists(value);
        return this;
    }

    public Settings withTransformPatternsMergeRangePredicates(Boolean value) {
        setTransformPatternsMergeRangePredicates(value);
        return this;
    }

    public Settings withTransformPatternsMergeBetweenSymmetricPredicates(Boolean value) {
        setTransformPatternsMergeBetweenSymmetricPredicates(value);
        return this;
    }

    public Settings withTransformPatternsCaseSearchedToCaseSimple(Boolean value) {
        setTransformPatternsCaseSearchedToCaseSimple(value);
        return this;
    }

    public Settings withTransformPatternsCaseElseNull(Boolean value) {
        setTransformPatternsCaseElseNull(value);
        return this;
    }

    public Settings withTransformPatternsUnreachableCaseClauses(Boolean value) {
        setTransformPatternsUnreachableCaseClauses(value);
        return this;
    }

    public Settings withTransformPatternsUnreachableDecodeClauses(Boolean value) {
        setTransformPatternsUnreachableDecodeClauses(value);
        return this;
    }

    public Settings withTransformPatternsCaseDistinctToDecode(Boolean value) {
        setTransformPatternsCaseDistinctToDecode(value);
        return this;
    }

    public Settings withTransformPatternsCaseMergeWhenWhen(Boolean value) {
        setTransformPatternsCaseMergeWhenWhen(value);
        return this;
    }

    public Settings withTransformPatternsCaseMergeWhenElse(Boolean value) {
        setTransformPatternsCaseMergeWhenElse(value);
        return this;
    }

    public Settings withTransformPatternsCaseToCaseAbbreviation(Boolean value) {
        setTransformPatternsCaseToCaseAbbreviation(value);
        return this;
    }

    public Settings withTransformPatternsSimplifyCaseAbbreviation(Boolean value) {
        setTransformPatternsSimplifyCaseAbbreviation(value);
        return this;
    }

    public Settings withTransformPatternsFlattenCaseAbbreviation(Boolean value) {
        setTransformPatternsFlattenCaseAbbreviation(value);
        return this;
    }

    public Settings withTransformPatternsFlattenDecode(Boolean value) {
        setTransformPatternsFlattenDecode(value);
        return this;
    }

    public Settings withTransformPatternsFlattenCase(Boolean value) {
        setTransformPatternsFlattenCase(value);
        return this;
    }

    public Settings withTransformPatternsTrivialCaseAbbreviation(Boolean value) {
        setTransformPatternsTrivialCaseAbbreviation(value);
        return this;
    }

    public Settings withTransformPatternsTrivialPredicates(Boolean value) {
        setTransformPatternsTrivialPredicates(value);
        return this;
    }

    public Settings withTransformPatternsTrivialBitwiseOperations(Boolean value) {
        setTransformPatternsTrivialBitwiseOperations(value);
        return this;
    }

    public Settings withTransformPatternsBitSet(Boolean value) {
        setTransformPatternsBitSet(value);
        return this;
    }

    public Settings withTransformPatternsBitGet(Boolean value) {
        setTransformPatternsBitGet(value);
        return this;
    }

    public Settings withTransformPatternsScalarSubqueryCountAsteriskGtZero(Boolean value) {
        setTransformPatternsScalarSubqueryCountAsteriskGtZero(value);
        return this;
    }

    public Settings withTransformPatternsScalarSubqueryCountExpressionGtZero(Boolean value) {
        setTransformPatternsScalarSubqueryCountExpressionGtZero(value);
        return this;
    }

    public Settings withTransformPatternsEmptyScalarSubquery(Boolean value) {
        setTransformPatternsEmptyScalarSubquery(value);
        return this;
    }

    public Settings withTransformPatternsNegNeg(Boolean value) {
        setTransformPatternsNegNeg(value);
        return this;
    }

    public Settings withTransformPatternsBitNotBitNot(Boolean value) {
        setTransformPatternsBitNotBitNot(value);
        return this;
    }

    public Settings withTransformPatternsBitNotBitNand(Boolean value) {
        setTransformPatternsBitNotBitNand(value);
        return this;
    }

    public Settings withTransformPatternsBitNotBitNor(Boolean value) {
        setTransformPatternsBitNotBitNor(value);
        return this;
    }

    public Settings withTransformPatternsBitNotBitXNor(Boolean value) {
        setTransformPatternsBitNotBitXNor(value);
        return this;
    }

    public Settings withTransformPatternsNullOnNullInput(Boolean value) {
        setTransformPatternsNullOnNullInput(value);
        return this;
    }

    public Settings withTransformPatternsIdempotentFunctionRepetition(Boolean value) {
        setTransformPatternsIdempotentFunctionRepetition(value);
        return this;
    }

    public Settings withTransformPatternsArithmeticComparisons(Boolean value) {
        setTransformPatternsArithmeticComparisons(value);
        return this;
    }

    public Settings withTransformPatternsArithmeticExpressions(Boolean value) {
        setTransformPatternsArithmeticExpressions(value);
        return this;
    }

    public Settings withTransformPatternsTrigonometricFunctions(Boolean value) {
        setTransformPatternsTrigonometricFunctions(value);
        return this;
    }

    public Settings withTransformPatternsLogarithmicFunctions(Boolean value) {
        setTransformPatternsLogarithmicFunctions(value);
        return this;
    }

    public Settings withTransformPatternsHyperbolicFunctions(Boolean value) {
        setTransformPatternsHyperbolicFunctions(value);
        return this;
    }

    public Settings withTransformPatternsInverseHyperbolicFunctions(Boolean value) {
        setTransformPatternsInverseHyperbolicFunctions(value);
        return this;
    }

    public Settings withTransformInlineBindValuesForFieldComparisons(Boolean value) {
        setTransformInlineBindValuesForFieldComparisons(value);
        return this;
    }

    public Settings withTransformAnsiJoinToTableLists(Boolean value) {
        setTransformAnsiJoinToTableLists(value);
        return this;
    }

    @Deprecated
    public Settings withTransformInConditionSubqueryWithLimitToDerivedTable(Transformation value) {
        setTransformInConditionSubqueryWithLimitToDerivedTable(value);
        return this;
    }

    public Settings withTransformQualify(Transformation value) {
        setTransformQualify(value);
        return this;
    }

    public Settings withTransformTableListsToAnsiJoin(Boolean value) {
        setTransformTableListsToAnsiJoin(value);
        return this;
    }

    public Settings withTransformRownum(Transformation value) {
        setTransformRownum(value);
        return this;
    }

    public Settings withTransformUnneededArithmeticExpressions(TransformUnneededArithmeticExpressions value) {
        setTransformUnneededArithmeticExpressions(value);
        return this;
    }

    public Settings withTransformGroupByColumnIndex(Transformation value) {
        setTransformGroupByColumnIndex(value);
        return this;
    }

    public Settings withTransformInlineCTE(Transformation value) {
        setTransformInlineCTE(value);
        return this;
    }

    public Settings withBackslashEscaping(BackslashEscaping value) {
        setBackslashEscaping(value);
        return this;
    }

    public Settings withParamType(ParamType value) {
        setParamType(value);
        return this;
    }

    public Settings withParamCastMode(ParamCastMode value) {
        setParamCastMode(value);
        return this;
    }

    public Settings withStatementType(StatementType value) {
        setStatementType(value);
        return this;
    }

    public Settings withInlineThreshold(Integer value) {
        setInlineThreshold(value);
        return this;
    }

    public Settings withTransactionListenerStartInvocationOrder(InvocationOrder value) {
        setTransactionListenerStartInvocationOrder(value);
        return this;
    }

    public Settings withTransactionListenerEndInvocationOrder(InvocationOrder value) {
        setTransactionListenerEndInvocationOrder(value);
        return this;
    }

    public Settings withMigrationListenerStartInvocationOrder(InvocationOrder value) {
        setMigrationListenerStartInvocationOrder(value);
        return this;
    }

    public Settings withMigrationListenerEndInvocationOrder(InvocationOrder value) {
        setMigrationListenerEndInvocationOrder(value);
        return this;
    }

    public Settings withVisitListenerStartInvocationOrder(InvocationOrder value) {
        setVisitListenerStartInvocationOrder(value);
        return this;
    }

    public Settings withVisitListenerEndInvocationOrder(InvocationOrder value) {
        setVisitListenerEndInvocationOrder(value);
        return this;
    }

    public Settings withRecordListenerStartInvocationOrder(InvocationOrder value) {
        setRecordListenerStartInvocationOrder(value);
        return this;
    }

    public Settings withRecordListenerEndInvocationOrder(InvocationOrder value) {
        setRecordListenerEndInvocationOrder(value);
        return this;
    }

    public Settings withExecuteListenerStartInvocationOrder(InvocationOrder value) {
        setExecuteListenerStartInvocationOrder(value);
        return this;
    }

    public Settings withExecuteListenerEndInvocationOrder(InvocationOrder value) {
        setExecuteListenerEndInvocationOrder(value);
        return this;
    }

    public Settings withExecuteLogging(Boolean value) {
        setExecuteLogging(value);
        return this;
    }

    public Settings withExecuteLoggingSQLExceptions(Boolean value) {
        setExecuteLoggingSQLExceptions(value);
        return this;
    }

    public Settings withDiagnosticsLogging(Boolean value) {
        setDiagnosticsLogging(value);
        return this;
    }

    public Settings withDiagnosticsConnection(DiagnosticsConnection value) {
        setDiagnosticsConnection(value);
        return this;
    }

    public Settings withUpdateRecordVersion(Boolean value) {
        setUpdateRecordVersion(value);
        return this;
    }

    public Settings withUpdateRecordTimestamp(Boolean value) {
        setUpdateRecordTimestamp(value);
        return this;
    }

    public Settings withExecuteWithOptimisticLocking(Boolean value) {
        setExecuteWithOptimisticLocking(value);
        return this;
    }

    public Settings withExecuteWithOptimisticLockingExcludeUnversioned(Boolean value) {
        setExecuteWithOptimisticLockingExcludeUnversioned(value);
        return this;
    }

    public Settings withAttachRecords(Boolean value) {
        setAttachRecords(value);
        return this;
    }

    public Settings withInsertUnchangedRecords(Boolean value) {
        setInsertUnchangedRecords(value);
        return this;
    }

    public Settings withUpdateUnchangedRecords(UpdateUnchangedRecords value) {
        setUpdateUnchangedRecords(value);
        return this;
    }

    public Settings withUpdatablePrimaryKeys(Boolean value) {
        setUpdatablePrimaryKeys(value);
        return this;
    }

    public Settings withReflectionCaching(Boolean value) {
        setReflectionCaching(value);
        return this;
    }

    public Settings withCacheRecordMappers(Boolean value) {
        setCacheRecordMappers(value);
        return this;
    }

    public Settings withCacheParsingConnection(Boolean value) {
        setCacheParsingConnection(value);
        return this;
    }

    public Settings withCacheParsingConnectionLRUCacheSize(Integer value) {
        setCacheParsingConnectionLRUCacheSize(value);
        return this;
    }

    public Settings withCachePreparedStatementInLoader(Boolean value) {
        setCachePreparedStatementInLoader(value);
        return this;
    }

    public Settings withThrowExceptions(ThrowExceptions value) {
        setThrowExceptions(value);
        return this;
    }

    public Settings withFetchWarnings(Boolean value) {
        setFetchWarnings(value);
        return this;
    }

    public Settings withFetchServerOutputSize(Integer value) {
        setFetchServerOutputSize(value);
        return this;
    }

    public Settings withReturnIdentityOnUpdatableRecord(Boolean value) {
        setReturnIdentityOnUpdatableRecord(value);
        return this;
    }

    public Settings withReturnDefaultOnUpdatableRecord(Boolean value) {
        setReturnDefaultOnUpdatableRecord(value);
        return this;
    }

    public Settings withReturnComputedOnUpdatableRecord(Boolean value) {
        setReturnComputedOnUpdatableRecord(value);
        return this;
    }

    public Settings withReturnAllOnUpdatableRecord(Boolean value) {
        setReturnAllOnUpdatableRecord(value);
        return this;
    }

    public Settings withReturnRecordToPojo(Boolean value) {
        setReturnRecordToPojo(value);
        return this;
    }

    public Settings withMapJPAAnnotations(Boolean value) {
        setMapJPAAnnotations(value);
        return this;
    }

    public Settings withMapRecordComponentParameterNames(Boolean value) {
        setMapRecordComponentParameterNames(value);
        return this;
    }

    public Settings withMapConstructorPropertiesParameterNames(Boolean value) {
        setMapConstructorPropertiesParameterNames(value);
        return this;
    }

    public Settings withMapConstructorParameterNames(Boolean value) {
        setMapConstructorParameterNames(value);
        return this;
    }

    public Settings withMapConstructorParameterNamesInKotlin(Boolean value) {
        setMapConstructorParameterNamesInKotlin(value);
        return this;
    }

    public Settings withQueryPoolable(QueryPoolable value) {
        setQueryPoolable(value);
        return this;
    }

    public Settings withQueryTimeout(Integer value) {
        setQueryTimeout(value);
        return this;
    }

    public Settings withMaxRows(Integer value) {
        setMaxRows(value);
        return this;
    }

    public Settings withFetchSize(Integer value) {
        setFetchSize(value);
        return this;
    }

    public Settings withBatchSize(Integer value) {
        setBatchSize(value);
        return this;
    }

    public Settings withDebugInfoOnStackTrace(Boolean value) {
        setDebugInfoOnStackTrace(value);
        return this;
    }

    public Settings withInListPadding(Boolean value) {
        setInListPadding(value);
        return this;
    }

    public Settings withInListPadBase(Integer value) {
        setInListPadBase(value);
        return this;
    }

    public Settings withDelimiter(String value) {
        setDelimiter(value);
        return this;
    }

    public Settings withEmulateOnDuplicateKeyUpdateOnPrimaryKeyOnly(Boolean value) {
        setEmulateOnDuplicateKeyUpdateOnPrimaryKeyOnly(value);
        return this;
    }

    public Settings withEmulateMultiset(NestedCollectionEmulation value) {
        setEmulateMultiset(value);
        return this;
    }

    public Settings withEmulateComputedColumns(Boolean value) {
        setEmulateComputedColumns(value);
        return this;
    }

    public Settings withExecuteUpdateWithoutWhere(ExecuteWithoutWhere value) {
        setExecuteUpdateWithoutWhere(value);
        return this;
    }

    public Settings withExecuteDeleteWithoutWhere(ExecuteWithoutWhere value) {
        setExecuteDeleteWithoutWhere(value);
        return this;
    }

    public Settings withInterpreterDialect(SQLDialect value) {
        setInterpreterDialect(value);
        return this;
    }

    public Settings withInterpreterNameLookupCaseSensitivity(InterpreterNameLookupCaseSensitivity value) {
        setInterpreterNameLookupCaseSensitivity(value);
        return this;
    }

    public Settings withInterpreterLocale(Locale value) {
        setInterpreterLocale(value);
        return this;
    }

    public Settings withInterpreterDelayForeignKeyDeclarations(Boolean value) {
        setInterpreterDelayForeignKeyDeclarations(value);
        return this;
    }

    public Settings withMetaIncludeSystemIndexes(Boolean value) {
        setMetaIncludeSystemIndexes(value);
        return this;
    }

    public Settings withMetaIncludeSystemSequences(Boolean value) {
        setMetaIncludeSystemSequences(value);
        return this;
    }

    public Settings withMigrationHistorySchema(MigrationSchema value) {
        setMigrationHistorySchema(value);
        return this;
    }

    public Settings withMigrationHistorySchemaCreateSchemaIfNotExists(Boolean value) {
        setMigrationHistorySchemaCreateSchemaIfNotExists(value);
        return this;
    }

    public Settings withMigrationDefaultSchema(MigrationSchema value) {
        setMigrationDefaultSchema(value);
        return this;
    }

    public Settings withMigrationSchemataCreateSchemaIfNotExists(Boolean value) {
        setMigrationSchemataCreateSchemaIfNotExists(value);
        return this;
    }

    public Settings withMigrationAllowsUndo(Boolean value) {
        setMigrationAllowsUndo(value);
        return this;
    }

    public Settings withMigrationRevertUntracked(Boolean value) {
        setMigrationRevertUntracked(value);
        return this;
    }

    public Settings withMigrationAutoBaseline(Boolean value) {
        setMigrationAutoBaseline(value);
        return this;
    }

    public Settings withMigrationAutoVerification(Boolean value) {
        setMigrationAutoVerification(value);
        return this;
    }

    public Settings withMigrationIgnoreDefaultTimestampPrecisionDiffs(Boolean value) {
        setMigrationIgnoreDefaultTimestampPrecisionDiffs(value);
        return this;
    }

    public Settings withLocale(Locale value) {
        setLocale(value);
        return this;
    }

    public Settings withParseDialect(SQLDialect value) {
        setParseDialect(value);
        return this;
    }

    public Settings withParseLocale(Locale value) {
        setParseLocale(value);
        return this;
    }

    public Settings withParseDateFormat(String value) {
        setParseDateFormat(value);
        return this;
    }

    public Settings withParseTimestampFormat(String value) {
        setParseTimestampFormat(value);
        return this;
    }

    public Settings withParseNamedParamPrefix(String value) {
        setParseNamedParamPrefix(value);
        return this;
    }

    public Settings withParseNameCase(ParseNameCase value) {
        setParseNameCase(value);
        return this;
    }

    public Settings withParseWithMetaLookups(ParseWithMetaLookups value) {
        setParseWithMetaLookups(value);
        return this;
    }

    public Settings withParseAppendMissingTableReferences(Transformation value) {
        setParseAppendMissingTableReferences(value);
        return this;
    }

    public Settings withParseSetCommands(Boolean value) {
        setParseSetCommands(value);
        return this;
    }

    public Settings withParseUnsupportedSyntax(ParseUnsupportedSyntax value) {
        setParseUnsupportedSyntax(value);
        return this;
    }

    public Settings withParseUnknownFunctions(ParseUnknownFunctions value) {
        setParseUnknownFunctions(value);
        return this;
    }

    public Settings withParseIgnoreCommercialOnlyFeatures(Boolean value) {
        setParseIgnoreCommercialOnlyFeatures(value);
        return this;
    }

    public Settings withParseIgnoreComments(Boolean value) {
        setParseIgnoreComments(value);
        return this;
    }

    public Settings withParseIgnoreCommentStart(String value) {
        setParseIgnoreCommentStart(value);
        return this;
    }

    public Settings withParseIgnoreCommentStop(String value) {
        setParseIgnoreCommentStop(value);
        return this;
    }

    public Settings withParseRetainCommentsBetweenQueries(Boolean value) {
        setParseRetainCommentsBetweenQueries(value);
        return this;
    }

    public Settings withParseMetaDefaultExpressions(Boolean value) {
        setParseMetaDefaultExpressions(value);
        return this;
    }

    public Settings withParseMetaViewSources(Boolean value) {
        setParseMetaViewSources(value);
        return this;
    }

    public Settings withReadonlyTableRecordInsert(WriteIfReadonly value) {
        setReadonlyTableRecordInsert(value);
        return this;
    }

    public Settings withReadonlyUpdatableRecordUpdate(WriteIfReadonly value) {
        setReadonlyUpdatableRecordUpdate(value);
        return this;
    }

    public Settings withReadonlyInsert(WriteIfReadonly value) {
        setReadonlyInsert(value);
        return this;
    }

    public Settings withReadonlyUpdate(WriteIfReadonly value) {
        setReadonlyUpdate(value);
        return this;
    }

    public Settings withApplyWorkaroundFor7962(Boolean value) {
        setApplyWorkaroundFor7962(value);
        return this;
    }

    public Settings withWarnOnStaticTypeRegistryAccess(Warning value) {
        setWarnOnStaticTypeRegistryAccess(value);
        return this;
    }

    public Settings withInterpreterSearchPath(InterpreterSearchSchema... values) {
        if (values != null) {
            for (InterpreterSearchSchema value : values) {
                getInterpreterSearchPath().add(value);
            }
        }
        return this;
    }

    public Settings withInterpreterSearchPath(Collection<InterpreterSearchSchema> values) {
        if (values != null) {
            getInterpreterSearchPath().addAll(values);
        }
        return this;
    }

    public Settings withInterpreterSearchPath(List<InterpreterSearchSchema> interpreterSearchPath) {
        setInterpreterSearchPath(interpreterSearchPath);
        return this;
    }

    public Settings withMigrationSchemata(MigrationSchema... values) {
        if (values != null) {
            for (MigrationSchema value : values) {
                getMigrationSchemata().add(value);
            }
        }
        return this;
    }

    public Settings withMigrationSchemata(Collection<MigrationSchema> values) {
        if (values != null) {
            getMigrationSchemata().addAll(values);
        }
        return this;
    }

    public Settings withMigrationSchemata(List<MigrationSchema> migrationSchemata) {
        setMigrationSchemata(migrationSchemata);
        return this;
    }

    public Settings withParseSearchPath(ParseSearchSchema... values) {
        if (values != null) {
            for (ParseSearchSchema value : values) {
                getParseSearchPath().add(value);
            }
        }
        return this;
    }

    public Settings withParseSearchPath(Collection<ParseSearchSchema> values) {
        if (values != null) {
            getParseSearchPath().addAll(values);
        }
        return this;
    }

    public Settings withParseSearchPath(List<ParseSearchSchema> parseSearchPath) {
        setParseSearchPath(parseSearchPath);
        return this;
    }

    @Override // org.jooq.util.jaxb.tools.XMLAppendable
    public final void appendTo(XMLBuilder builder) {
        builder.append("forceIntegerTypesOnZeroScaleDecimals", this.forceIntegerTypesOnZeroScaleDecimals);
        builder.append("renderCatalog", this.renderCatalog);
        builder.append("renderSchema", this.renderSchema);
        builder.append("renderTable", this.renderTable);
        builder.append("renderMapping", (XMLAppendable) this.renderMapping);
        builder.append("renderQuotedNames", this.renderQuotedNames);
        builder.append("renderNameCase", this.renderNameCase);
        builder.append("renderNameStyle", this.renderNameStyle);
        builder.append("renderNamedParamPrefix", this.renderNamedParamPrefix);
        builder.append("renderKeywordCase", this.renderKeywordCase);
        builder.append("renderKeywordStyle", this.renderKeywordStyle);
        builder.append("renderLocale", this.renderLocale);
        builder.append("renderFormatted", this.renderFormatted);
        builder.append("renderFormatting", (XMLAppendable) this.renderFormatting);
        builder.append("renderOptionalAssociativityParentheses", this.renderOptionalAssociativityParentheses);
        builder.append("renderOptionalAsKeywordForTableAliases", this.renderOptionalAsKeywordForTableAliases);
        builder.append("renderOptionalAsKeywordForFieldAliases", this.renderOptionalAsKeywordForFieldAliases);
        builder.append("renderOptionalInnerKeyword", this.renderOptionalInnerKeyword);
        builder.append("renderOptionalOuterKeyword", this.renderOptionalOuterKeyword);
        builder.append("renderImplicitWindowRange", this.renderImplicitWindowRange);
        builder.append("renderScalarSubqueriesForStoredFunctions", this.renderScalarSubqueriesForStoredFunctions);
        builder.append("renderImplicitJoinType", this.renderImplicitJoinType);
        builder.append("renderImplicitJoinToManyType", this.renderImplicitJoinToManyType);
        builder.append("renderDefaultNullability", this.renderDefaultNullability);
        builder.append("renderCoalesceToEmptyStringInConcat", this.renderCoalesceToEmptyStringInConcat);
        builder.append("renderOrderByRownumberForEmulatedPagination", this.renderOrderByRownumberForEmulatedPagination);
        builder.append("renderOutputForSQLServerReturningClause", this.renderOutputForSQLServerReturningClause);
        builder.append("renderGroupConcatMaxLenSessionVariable", this.renderGroupConcatMaxLenSessionVariable);
        builder.append("renderParenthesisAroundSetOperationQueries", this.renderParenthesisAroundSetOperationQueries);
        builder.append("renderVariablesInDerivedTablesForEmulations", this.renderVariablesInDerivedTablesForEmulations);
        builder.append("renderRowConditionForSeekClause", this.renderRowConditionForSeekClause);
        builder.append("renderRedundantConditionForSeekClause", this.renderRedundantConditionForSeekClause);
        builder.append("renderPlainSQLTemplatesAsRaw", this.renderPlainSQLTemplatesAsRaw);
        builder.append("renderDollarQuotedStringToken", this.renderDollarQuotedStringToken);
        builder.append("namePathSeparator", this.namePathSeparator);
        builder.append("bindOffsetDateTimeType", this.bindOffsetDateTimeType);
        builder.append("bindOffsetTimeType", this.bindOffsetTimeType);
        builder.append("fetchTriggerValuesAfterSQLServerOutput", this.fetchTriggerValuesAfterSQLServerOutput);
        builder.append("fetchTriggerValuesAfterReturning", this.fetchTriggerValuesAfterReturning);
        builder.append("fetchIntermediateResult", this.fetchIntermediateResult);
        builder.append("diagnosticsDuplicateStatements", this.diagnosticsDuplicateStatements);
        builder.append("diagnosticsDuplicateStatementsUsingTransformPatterns", this.diagnosticsDuplicateStatementsUsingTransformPatterns);
        builder.append("diagnosticsMissingWasNullCall", this.diagnosticsMissingWasNullCall);
        builder.append("diagnosticsRepeatedStatements", this.diagnosticsRepeatedStatements);
        builder.append("diagnosticsConsecutiveAggregation", this.diagnosticsConsecutiveAggregation);
        builder.append("diagnosticsConcatenationInPredicate", this.diagnosticsConcatenationInPredicate);
        builder.append("diagnosticsPossiblyWrongExpression", this.diagnosticsPossiblyWrongExpression);
        builder.append("diagnosticsTooManyColumnsFetched", this.diagnosticsTooManyColumnsFetched);
        builder.append("diagnosticsTooManyRowsFetched", this.diagnosticsTooManyRowsFetched);
        builder.append("diagnosticsUnnecessaryWasNullCall", this.diagnosticsUnnecessaryWasNullCall);
        builder.append("diagnosticsPatterns", this.diagnosticsPatterns);
        builder.append("diagnosticsTrivialCondition", this.diagnosticsTrivialCondition);
        builder.append("diagnosticsNullCondition", this.diagnosticsNullCondition);
        builder.append("transformPatterns", this.transformPatterns);
        builder.append("transformPatternsLogging", this.transformPatternsLogging);
        builder.append("transformPatternsUnnecessaryDistinct", this.transformPatternsUnnecessaryDistinct);
        builder.append("transformPatternsUnnecessaryScalarSubquery", this.transformPatternsUnnecessaryScalarSubquery);
        builder.append("transformPatternsUnnecessaryInnerJoin", this.transformPatternsUnnecessaryInnerJoin);
        builder.append("transformPatternsUnnecessaryGroupByExpressions", this.transformPatternsUnnecessaryGroupByExpressions);
        builder.append("transformPatternsUnnecessaryOrderByExpressions", this.transformPatternsUnnecessaryOrderByExpressions);
        builder.append("transformPatternsUnnecessaryExistsSubqueryClauses", this.transformPatternsUnnecessaryExistsSubqueryClauses);
        builder.append("transformPatternsCountConstant", this.transformPatternsCountConstant);
        builder.append("transformPatternsTrim", this.transformPatternsTrim);
        builder.append("transformPatternsNotAnd", this.transformPatternsNotAnd);
        builder.append("transformPatternsNotOr", this.transformPatternsNotOr);
        builder.append("transformPatternsNotNot", this.transformPatternsNotNot);
        builder.append("transformPatternsNotComparison", this.transformPatternsNotComparison);
        builder.append("transformPatternsNotNotDistinct", this.transformPatternsNotNotDistinct);
        builder.append("transformPatternsDistinctFromNull", this.transformPatternsDistinctFromNull);
        builder.append("transformPatternsNormaliseAssociativeOps", this.transformPatternsNormaliseAssociativeOps);
        builder.append("transformPatternsNormaliseInListSingleElementToComparison", this.transformPatternsNormaliseInListSingleElementToComparison);
        builder.append("transformPatternsNormaliseFieldCompareValue", this.transformPatternsNormaliseFieldCompareValue);
        builder.append("transformPatternsNormaliseCoalesceToNvl", this.transformPatternsNormaliseCoalesceToNvl);
        builder.append("transformPatternsOrEqToIn", this.transformPatternsOrEqToIn);
        builder.append("transformPatternsAndNeToNotIn", this.transformPatternsAndNeToNotIn);
        builder.append("transformPatternsMergeOrComparison", this.transformPatternsMergeOrComparison);
        builder.append("transformPatternsMergeAndComparison", this.transformPatternsMergeAndComparison);
        builder.append("transformPatternsMergeInLists", this.transformPatternsMergeInLists);
        builder.append("transformPatternsMergeRangePredicates", this.transformPatternsMergeRangePredicates);
        builder.append("transformPatternsMergeBetweenSymmetricPredicates", this.transformPatternsMergeBetweenSymmetricPredicates);
        builder.append("transformPatternsCaseSearchedToCaseSimple", this.transformPatternsCaseSearchedToCaseSimple);
        builder.append("transformPatternsCaseElseNull", this.transformPatternsCaseElseNull);
        builder.append("transformPatternsUnreachableCaseClauses", this.transformPatternsUnreachableCaseClauses);
        builder.append("transformPatternsUnreachableDecodeClauses", this.transformPatternsUnreachableDecodeClauses);
        builder.append("transformPatternsCaseDistinctToDecode", this.transformPatternsCaseDistinctToDecode);
        builder.append("transformPatternsCaseMergeWhenWhen", this.transformPatternsCaseMergeWhenWhen);
        builder.append("transformPatternsCaseMergeWhenElse", this.transformPatternsCaseMergeWhenElse);
        builder.append("transformPatternsCaseToCaseAbbreviation", this.transformPatternsCaseToCaseAbbreviation);
        builder.append("transformPatternsSimplifyCaseAbbreviation", this.transformPatternsSimplifyCaseAbbreviation);
        builder.append("transformPatternsFlattenCaseAbbreviation", this.transformPatternsFlattenCaseAbbreviation);
        builder.append("transformPatternsFlattenDecode", this.transformPatternsFlattenDecode);
        builder.append("transformPatternsFlattenCase", this.transformPatternsFlattenCase);
        builder.append("transformPatternsTrivialCaseAbbreviation", this.transformPatternsTrivialCaseAbbreviation);
        builder.append("transformPatternsTrivialPredicates", this.transformPatternsTrivialPredicates);
        builder.append("transformPatternsTrivialBitwiseOperations", this.transformPatternsTrivialBitwiseOperations);
        builder.append("transformPatternsBitSet", this.transformPatternsBitSet);
        builder.append("transformPatternsBitGet", this.transformPatternsBitGet);
        builder.append("transformPatternsScalarSubqueryCountAsteriskGtZero", this.transformPatternsScalarSubqueryCountAsteriskGtZero);
        builder.append("transformPatternsScalarSubqueryCountExpressionGtZero", this.transformPatternsScalarSubqueryCountExpressionGtZero);
        builder.append("transformPatternsEmptyScalarSubquery", this.transformPatternsEmptyScalarSubquery);
        builder.append("transformPatternsNegNeg", this.transformPatternsNegNeg);
        builder.append("transformPatternsBitNotBitNot", this.transformPatternsBitNotBitNot);
        builder.append("transformPatternsBitNotBitNand", this.transformPatternsBitNotBitNand);
        builder.append("transformPatternsBitNotBitNor", this.transformPatternsBitNotBitNor);
        builder.append("transformPatternsBitNotBitXNor", this.transformPatternsBitNotBitXNor);
        builder.append("transformPatternsNullOnNullInput", this.transformPatternsNullOnNullInput);
        builder.append("transformPatternsIdempotentFunctionRepetition", this.transformPatternsIdempotentFunctionRepetition);
        builder.append("transformPatternsArithmeticComparisons", this.transformPatternsArithmeticComparisons);
        builder.append("transformPatternsArithmeticExpressions", this.transformPatternsArithmeticExpressions);
        builder.append("transformPatternsTrigonometricFunctions", this.transformPatternsTrigonometricFunctions);
        builder.append("transformPatternsLogarithmicFunctions", this.transformPatternsLogarithmicFunctions);
        builder.append("transformPatternsHyperbolicFunctions", this.transformPatternsHyperbolicFunctions);
        builder.append("transformPatternsInverseHyperbolicFunctions", this.transformPatternsInverseHyperbolicFunctions);
        builder.append("transformInlineBindValuesForFieldComparisons", this.transformInlineBindValuesForFieldComparisons);
        builder.append("transformAnsiJoinToTableLists", this.transformAnsiJoinToTableLists);
        builder.append("transformInConditionSubqueryWithLimitToDerivedTable", this.transformInConditionSubqueryWithLimitToDerivedTable);
        builder.append("transformQualify", this.transformQualify);
        builder.append("transformTableListsToAnsiJoin", this.transformTableListsToAnsiJoin);
        builder.append("transformRownum", this.transformRownum);
        builder.append("transformUnneededArithmeticExpressions", this.transformUnneededArithmeticExpressions);
        builder.append("transformGroupByColumnIndex", this.transformGroupByColumnIndex);
        builder.append("transformInlineCTE", this.transformInlineCTE);
        builder.append("backslashEscaping", this.backslashEscaping);
        builder.append("paramType", this.paramType);
        builder.append("paramCastMode", this.paramCastMode);
        builder.append("statementType", this.statementType);
        builder.append("inlineThreshold", this.inlineThreshold);
        builder.append("transactionListenerStartInvocationOrder", this.transactionListenerStartInvocationOrder);
        builder.append("transactionListenerEndInvocationOrder", this.transactionListenerEndInvocationOrder);
        builder.append("migrationListenerStartInvocationOrder", this.migrationListenerStartInvocationOrder);
        builder.append("migrationListenerEndInvocationOrder", this.migrationListenerEndInvocationOrder);
        builder.append("visitListenerStartInvocationOrder", this.visitListenerStartInvocationOrder);
        builder.append("visitListenerEndInvocationOrder", this.visitListenerEndInvocationOrder);
        builder.append("recordListenerStartInvocationOrder", this.recordListenerStartInvocationOrder);
        builder.append("recordListenerEndInvocationOrder", this.recordListenerEndInvocationOrder);
        builder.append("executeListenerStartInvocationOrder", this.executeListenerStartInvocationOrder);
        builder.append("executeListenerEndInvocationOrder", this.executeListenerEndInvocationOrder);
        builder.append("executeLogging", this.executeLogging);
        builder.append("executeLoggingSQLExceptions", this.executeLoggingSQLExceptions);
        builder.append("diagnosticsLogging", this.diagnosticsLogging);
        builder.append("diagnosticsConnection", this.diagnosticsConnection);
        builder.append("updateRecordVersion", this.updateRecordVersion);
        builder.append("updateRecordTimestamp", this.updateRecordTimestamp);
        builder.append("executeWithOptimisticLocking", this.executeWithOptimisticLocking);
        builder.append("executeWithOptimisticLockingExcludeUnversioned", this.executeWithOptimisticLockingExcludeUnversioned);
        builder.append("attachRecords", this.attachRecords);
        builder.append("insertUnchangedRecords", this.insertUnchangedRecords);
        builder.append("updateUnchangedRecords", this.updateUnchangedRecords);
        builder.append("updatablePrimaryKeys", this.updatablePrimaryKeys);
        builder.append("reflectionCaching", this.reflectionCaching);
        builder.append("cacheRecordMappers", this.cacheRecordMappers);
        builder.append("cacheParsingConnection", this.cacheParsingConnection);
        builder.append("cacheParsingConnectionLRUCacheSize", this.cacheParsingConnectionLRUCacheSize);
        builder.append("cachePreparedStatementInLoader", this.cachePreparedStatementInLoader);
        builder.append("throwExceptions", this.throwExceptions);
        builder.append("fetchWarnings", this.fetchWarnings);
        builder.append("fetchServerOutputSize", this.fetchServerOutputSize);
        builder.append("returnIdentityOnUpdatableRecord", this.returnIdentityOnUpdatableRecord);
        builder.append("returnDefaultOnUpdatableRecord", this.returnDefaultOnUpdatableRecord);
        builder.append("returnComputedOnUpdatableRecord", this.returnComputedOnUpdatableRecord);
        builder.append("returnAllOnUpdatableRecord", this.returnAllOnUpdatableRecord);
        builder.append("returnRecordToPojo", this.returnRecordToPojo);
        builder.append("mapJPAAnnotations", this.mapJPAAnnotations);
        builder.append("mapRecordComponentParameterNames", this.mapRecordComponentParameterNames);
        builder.append("mapConstructorPropertiesParameterNames", this.mapConstructorPropertiesParameterNames);
        builder.append("mapConstructorParameterNames", this.mapConstructorParameterNames);
        builder.append("mapConstructorParameterNamesInKotlin", this.mapConstructorParameterNamesInKotlin);
        builder.append("queryPoolable", this.queryPoolable);
        builder.append("queryTimeout", this.queryTimeout);
        builder.append("maxRows", this.maxRows);
        builder.append("fetchSize", this.fetchSize);
        builder.append("batchSize", this.batchSize);
        builder.append("debugInfoOnStackTrace", this.debugInfoOnStackTrace);
        builder.append("inListPadding", this.inListPadding);
        builder.append("inListPadBase", this.inListPadBase);
        builder.append("delimiter", this.delimiter);
        builder.append("emulateOnDuplicateKeyUpdateOnPrimaryKeyOnly", this.emulateOnDuplicateKeyUpdateOnPrimaryKeyOnly);
        builder.append("emulateMultiset", this.emulateMultiset);
        builder.append("emulateComputedColumns", this.emulateComputedColumns);
        builder.append("executeUpdateWithoutWhere", this.executeUpdateWithoutWhere);
        builder.append("executeDeleteWithoutWhere", this.executeDeleteWithoutWhere);
        builder.append("interpreterDialect", this.interpreterDialect);
        builder.append("interpreterNameLookupCaseSensitivity", this.interpreterNameLookupCaseSensitivity);
        builder.append("interpreterLocale", this.interpreterLocale);
        builder.append("interpreterDelayForeignKeyDeclarations", this.interpreterDelayForeignKeyDeclarations);
        builder.append("metaIncludeSystemIndexes", this.metaIncludeSystemIndexes);
        builder.append("metaIncludeSystemSequences", this.metaIncludeSystemSequences);
        builder.append("migrationHistorySchema", (XMLAppendable) this.migrationHistorySchema);
        builder.append("migrationHistorySchemaCreateSchemaIfNotExists", this.migrationHistorySchemaCreateSchemaIfNotExists);
        builder.append("migrationDefaultSchema", (XMLAppendable) this.migrationDefaultSchema);
        builder.append("migrationSchemataCreateSchemaIfNotExists", this.migrationSchemataCreateSchemaIfNotExists);
        builder.append("migrationAllowsUndo", this.migrationAllowsUndo);
        builder.append("migrationRevertUntracked", this.migrationRevertUntracked);
        builder.append("migrationAutoBaseline", this.migrationAutoBaseline);
        builder.append("migrationAutoVerification", this.migrationAutoVerification);
        builder.append("migrationIgnoreDefaultTimestampPrecisionDiffs", this.migrationIgnoreDefaultTimestampPrecisionDiffs);
        builder.append(LocaleChangeInterceptor.DEFAULT_PARAM_NAME, this.locale);
        builder.append("parseDialect", this.parseDialect);
        builder.append("parseLocale", this.parseLocale);
        builder.append("parseDateFormat", this.parseDateFormat);
        builder.append("parseTimestampFormat", this.parseTimestampFormat);
        builder.append("parseNamedParamPrefix", this.parseNamedParamPrefix);
        builder.append("parseNameCase", this.parseNameCase);
        builder.append("parseWithMetaLookups", this.parseWithMetaLookups);
        builder.append("parseAppendMissingTableReferences", this.parseAppendMissingTableReferences);
        builder.append("parseSetCommands", this.parseSetCommands);
        builder.append("parseUnsupportedSyntax", this.parseUnsupportedSyntax);
        builder.append("parseUnknownFunctions", this.parseUnknownFunctions);
        builder.append("parseIgnoreCommercialOnlyFeatures", this.parseIgnoreCommercialOnlyFeatures);
        builder.append("parseIgnoreComments", this.parseIgnoreComments);
        builder.append("parseIgnoreCommentStart", this.parseIgnoreCommentStart);
        builder.append("parseIgnoreCommentStop", this.parseIgnoreCommentStop);
        builder.append("parseRetainCommentsBetweenQueries", this.parseRetainCommentsBetweenQueries);
        builder.append("parseMetaDefaultExpressions", this.parseMetaDefaultExpressions);
        builder.append("parseMetaViewSources", this.parseMetaViewSources);
        builder.append("readonlyTableRecordInsert", this.readonlyTableRecordInsert);
        builder.append("readonlyUpdatableRecordUpdate", this.readonlyUpdatableRecordUpdate);
        builder.append("readonlyInsert", this.readonlyInsert);
        builder.append("readonlyUpdate", this.readonlyUpdate);
        builder.append("applyWorkaroundFor7962", this.applyWorkaroundFor7962);
        builder.append("warnOnStaticTypeRegistryAccess", this.warnOnStaticTypeRegistryAccess);
        builder.append("interpreterSearchPath", "schema", this.interpreterSearchPath);
        builder.append("migrationSchemata", "schema", this.migrationSchemata);
        builder.append("parseSearchPath", "schema", this.parseSearchPath);
    }

    public String toString() {
        XMLBuilder builder = XMLBuilder.nonFormatting();
        appendTo(builder);
        return builder.toString();
    }

    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null || getClass() != that.getClass()) {
            return false;
        }
        Settings other = (Settings) that;
        if (this.forceIntegerTypesOnZeroScaleDecimals == null) {
            if (other.forceIntegerTypesOnZeroScaleDecimals != null) {
                return false;
            }
        } else if (!this.forceIntegerTypesOnZeroScaleDecimals.equals(other.forceIntegerTypesOnZeroScaleDecimals)) {
            return false;
        }
        if (this.renderCatalog == null) {
            if (other.renderCatalog != null) {
                return false;
            }
        } else if (!this.renderCatalog.equals(other.renderCatalog)) {
            return false;
        }
        if (this.renderSchema == null) {
            if (other.renderSchema != null) {
                return false;
            }
        } else if (!this.renderSchema.equals(other.renderSchema)) {
            return false;
        }
        if (this.renderTable == null) {
            if (other.renderTable != null) {
                return false;
            }
        } else if (!this.renderTable.equals(other.renderTable)) {
            return false;
        }
        if (this.renderMapping == null) {
            if (other.renderMapping != null) {
                return false;
            }
        } else if (!this.renderMapping.equals(other.renderMapping)) {
            return false;
        }
        if (this.renderQuotedNames == null) {
            if (other.renderQuotedNames != null) {
                return false;
            }
        } else if (!this.renderQuotedNames.equals(other.renderQuotedNames)) {
            return false;
        }
        if (this.renderNameCase == null) {
            if (other.renderNameCase != null) {
                return false;
            }
        } else if (!this.renderNameCase.equals(other.renderNameCase)) {
            return false;
        }
        if (this.renderNameStyle == null) {
            if (other.renderNameStyle != null) {
                return false;
            }
        } else if (!this.renderNameStyle.equals(other.renderNameStyle)) {
            return false;
        }
        if (this.renderNamedParamPrefix == null) {
            if (other.renderNamedParamPrefix != null) {
                return false;
            }
        } else if (!this.renderNamedParamPrefix.equals(other.renderNamedParamPrefix)) {
            return false;
        }
        if (this.renderKeywordCase == null) {
            if (other.renderKeywordCase != null) {
                return false;
            }
        } else if (!this.renderKeywordCase.equals(other.renderKeywordCase)) {
            return false;
        }
        if (this.renderKeywordStyle == null) {
            if (other.renderKeywordStyle != null) {
                return false;
            }
        } else if (!this.renderKeywordStyle.equals(other.renderKeywordStyle)) {
            return false;
        }
        if (this.renderLocale == null) {
            if (other.renderLocale != null) {
                return false;
            }
        } else if (!this.renderLocale.equals(other.renderLocale)) {
            return false;
        }
        if (this.renderFormatted == null) {
            if (other.renderFormatted != null) {
                return false;
            }
        } else if (!this.renderFormatted.equals(other.renderFormatted)) {
            return false;
        }
        if (this.renderFormatting == null) {
            if (other.renderFormatting != null) {
                return false;
            }
        } else if (!this.renderFormatting.equals(other.renderFormatting)) {
            return false;
        }
        if (this.renderOptionalAssociativityParentheses == null) {
            if (other.renderOptionalAssociativityParentheses != null) {
                return false;
            }
        } else if (!this.renderOptionalAssociativityParentheses.equals(other.renderOptionalAssociativityParentheses)) {
            return false;
        }
        if (this.renderOptionalAsKeywordForTableAliases == null) {
            if (other.renderOptionalAsKeywordForTableAliases != null) {
                return false;
            }
        } else if (!this.renderOptionalAsKeywordForTableAliases.equals(other.renderOptionalAsKeywordForTableAliases)) {
            return false;
        }
        if (this.renderOptionalAsKeywordForFieldAliases == null) {
            if (other.renderOptionalAsKeywordForFieldAliases != null) {
                return false;
            }
        } else if (!this.renderOptionalAsKeywordForFieldAliases.equals(other.renderOptionalAsKeywordForFieldAliases)) {
            return false;
        }
        if (this.renderOptionalInnerKeyword == null) {
            if (other.renderOptionalInnerKeyword != null) {
                return false;
            }
        } else if (!this.renderOptionalInnerKeyword.equals(other.renderOptionalInnerKeyword)) {
            return false;
        }
        if (this.renderOptionalOuterKeyword == null) {
            if (other.renderOptionalOuterKeyword != null) {
                return false;
            }
        } else if (!this.renderOptionalOuterKeyword.equals(other.renderOptionalOuterKeyword)) {
            return false;
        }
        if (this.renderImplicitWindowRange == null) {
            if (other.renderImplicitWindowRange != null) {
                return false;
            }
        } else if (!this.renderImplicitWindowRange.equals(other.renderImplicitWindowRange)) {
            return false;
        }
        if (this.renderScalarSubqueriesForStoredFunctions == null) {
            if (other.renderScalarSubqueriesForStoredFunctions != null) {
                return false;
            }
        } else if (!this.renderScalarSubqueriesForStoredFunctions.equals(other.renderScalarSubqueriesForStoredFunctions)) {
            return false;
        }
        if (this.renderImplicitJoinType == null) {
            if (other.renderImplicitJoinType != null) {
                return false;
            }
        } else if (!this.renderImplicitJoinType.equals(other.renderImplicitJoinType)) {
            return false;
        }
        if (this.renderImplicitJoinToManyType == null) {
            if (other.renderImplicitJoinToManyType != null) {
                return false;
            }
        } else if (!this.renderImplicitJoinToManyType.equals(other.renderImplicitJoinToManyType)) {
            return false;
        }
        if (this.renderDefaultNullability == null) {
            if (other.renderDefaultNullability != null) {
                return false;
            }
        } else if (!this.renderDefaultNullability.equals(other.renderDefaultNullability)) {
            return false;
        }
        if (this.renderCoalesceToEmptyStringInConcat == null) {
            if (other.renderCoalesceToEmptyStringInConcat != null) {
                return false;
            }
        } else if (!this.renderCoalesceToEmptyStringInConcat.equals(other.renderCoalesceToEmptyStringInConcat)) {
            return false;
        }
        if (this.renderOrderByRownumberForEmulatedPagination == null) {
            if (other.renderOrderByRownumberForEmulatedPagination != null) {
                return false;
            }
        } else if (!this.renderOrderByRownumberForEmulatedPagination.equals(other.renderOrderByRownumberForEmulatedPagination)) {
            return false;
        }
        if (this.renderOutputForSQLServerReturningClause == null) {
            if (other.renderOutputForSQLServerReturningClause != null) {
                return false;
            }
        } else if (!this.renderOutputForSQLServerReturningClause.equals(other.renderOutputForSQLServerReturningClause)) {
            return false;
        }
        if (this.renderGroupConcatMaxLenSessionVariable == null) {
            if (other.renderGroupConcatMaxLenSessionVariable != null) {
                return false;
            }
        } else if (!this.renderGroupConcatMaxLenSessionVariable.equals(other.renderGroupConcatMaxLenSessionVariable)) {
            return false;
        }
        if (this.renderParenthesisAroundSetOperationQueries == null) {
            if (other.renderParenthesisAroundSetOperationQueries != null) {
                return false;
            }
        } else if (!this.renderParenthesisAroundSetOperationQueries.equals(other.renderParenthesisAroundSetOperationQueries)) {
            return false;
        }
        if (this.renderVariablesInDerivedTablesForEmulations == null) {
            if (other.renderVariablesInDerivedTablesForEmulations != null) {
                return false;
            }
        } else if (!this.renderVariablesInDerivedTablesForEmulations.equals(other.renderVariablesInDerivedTablesForEmulations)) {
            return false;
        }
        if (this.renderRowConditionForSeekClause == null) {
            if (other.renderRowConditionForSeekClause != null) {
                return false;
            }
        } else if (!this.renderRowConditionForSeekClause.equals(other.renderRowConditionForSeekClause)) {
            return false;
        }
        if (this.renderRedundantConditionForSeekClause == null) {
            if (other.renderRedundantConditionForSeekClause != null) {
                return false;
            }
        } else if (!this.renderRedundantConditionForSeekClause.equals(other.renderRedundantConditionForSeekClause)) {
            return false;
        }
        if (this.renderPlainSQLTemplatesAsRaw == null) {
            if (other.renderPlainSQLTemplatesAsRaw != null) {
                return false;
            }
        } else if (!this.renderPlainSQLTemplatesAsRaw.equals(other.renderPlainSQLTemplatesAsRaw)) {
            return false;
        }
        if (this.renderDollarQuotedStringToken == null) {
            if (other.renderDollarQuotedStringToken != null) {
                return false;
            }
        } else if (!this.renderDollarQuotedStringToken.equals(other.renderDollarQuotedStringToken)) {
            return false;
        }
        if (this.namePathSeparator == null) {
            if (other.namePathSeparator != null) {
                return false;
            }
        } else if (!this.namePathSeparator.equals(other.namePathSeparator)) {
            return false;
        }
        if (this.bindOffsetDateTimeType == null) {
            if (other.bindOffsetDateTimeType != null) {
                return false;
            }
        } else if (!this.bindOffsetDateTimeType.equals(other.bindOffsetDateTimeType)) {
            return false;
        }
        if (this.bindOffsetTimeType == null) {
            if (other.bindOffsetTimeType != null) {
                return false;
            }
        } else if (!this.bindOffsetTimeType.equals(other.bindOffsetTimeType)) {
            return false;
        }
        if (this.fetchTriggerValuesAfterSQLServerOutput == null) {
            if (other.fetchTriggerValuesAfterSQLServerOutput != null) {
                return false;
            }
        } else if (!this.fetchTriggerValuesAfterSQLServerOutput.equals(other.fetchTriggerValuesAfterSQLServerOutput)) {
            return false;
        }
        if (this.fetchTriggerValuesAfterReturning == null) {
            if (other.fetchTriggerValuesAfterReturning != null) {
                return false;
            }
        } else if (!this.fetchTriggerValuesAfterReturning.equals(other.fetchTriggerValuesAfterReturning)) {
            return false;
        }
        if (this.fetchIntermediateResult == null) {
            if (other.fetchIntermediateResult != null) {
                return false;
            }
        } else if (!this.fetchIntermediateResult.equals(other.fetchIntermediateResult)) {
            return false;
        }
        if (this.diagnosticsDuplicateStatements == null) {
            if (other.diagnosticsDuplicateStatements != null) {
                return false;
            }
        } else if (!this.diagnosticsDuplicateStatements.equals(other.diagnosticsDuplicateStatements)) {
            return false;
        }
        if (this.diagnosticsDuplicateStatementsUsingTransformPatterns == null) {
            if (other.diagnosticsDuplicateStatementsUsingTransformPatterns != null) {
                return false;
            }
        } else if (!this.diagnosticsDuplicateStatementsUsingTransformPatterns.equals(other.diagnosticsDuplicateStatementsUsingTransformPatterns)) {
            return false;
        }
        if (this.diagnosticsMissingWasNullCall == null) {
            if (other.diagnosticsMissingWasNullCall != null) {
                return false;
            }
        } else if (!this.diagnosticsMissingWasNullCall.equals(other.diagnosticsMissingWasNullCall)) {
            return false;
        }
        if (this.diagnosticsRepeatedStatements == null) {
            if (other.diagnosticsRepeatedStatements != null) {
                return false;
            }
        } else if (!this.diagnosticsRepeatedStatements.equals(other.diagnosticsRepeatedStatements)) {
            return false;
        }
        if (this.diagnosticsConsecutiveAggregation == null) {
            if (other.diagnosticsConsecutiveAggregation != null) {
                return false;
            }
        } else if (!this.diagnosticsConsecutiveAggregation.equals(other.diagnosticsConsecutiveAggregation)) {
            return false;
        }
        if (this.diagnosticsConcatenationInPredicate == null) {
            if (other.diagnosticsConcatenationInPredicate != null) {
                return false;
            }
        } else if (!this.diagnosticsConcatenationInPredicate.equals(other.diagnosticsConcatenationInPredicate)) {
            return false;
        }
        if (this.diagnosticsPossiblyWrongExpression == null) {
            if (other.diagnosticsPossiblyWrongExpression != null) {
                return false;
            }
        } else if (!this.diagnosticsPossiblyWrongExpression.equals(other.diagnosticsPossiblyWrongExpression)) {
            return false;
        }
        if (this.diagnosticsTooManyColumnsFetched == null) {
            if (other.diagnosticsTooManyColumnsFetched != null) {
                return false;
            }
        } else if (!this.diagnosticsTooManyColumnsFetched.equals(other.diagnosticsTooManyColumnsFetched)) {
            return false;
        }
        if (this.diagnosticsTooManyRowsFetched == null) {
            if (other.diagnosticsTooManyRowsFetched != null) {
                return false;
            }
        } else if (!this.diagnosticsTooManyRowsFetched.equals(other.diagnosticsTooManyRowsFetched)) {
            return false;
        }
        if (this.diagnosticsUnnecessaryWasNullCall == null) {
            if (other.diagnosticsUnnecessaryWasNullCall != null) {
                return false;
            }
        } else if (!this.diagnosticsUnnecessaryWasNullCall.equals(other.diagnosticsUnnecessaryWasNullCall)) {
            return false;
        }
        if (this.diagnosticsPatterns == null) {
            if (other.diagnosticsPatterns != null) {
                return false;
            }
        } else if (!this.diagnosticsPatterns.equals(other.diagnosticsPatterns)) {
            return false;
        }
        if (this.diagnosticsTrivialCondition == null) {
            if (other.diagnosticsTrivialCondition != null) {
                return false;
            }
        } else if (!this.diagnosticsTrivialCondition.equals(other.diagnosticsTrivialCondition)) {
            return false;
        }
        if (this.diagnosticsNullCondition == null) {
            if (other.diagnosticsNullCondition != null) {
                return false;
            }
        } else if (!this.diagnosticsNullCondition.equals(other.diagnosticsNullCondition)) {
            return false;
        }
        if (this.transformPatterns == null) {
            if (other.transformPatterns != null) {
                return false;
            }
        } else if (!this.transformPatterns.equals(other.transformPatterns)) {
            return false;
        }
        if (this.transformPatternsLogging == null) {
            if (other.transformPatternsLogging != null) {
                return false;
            }
        } else if (!this.transformPatternsLogging.equals(other.transformPatternsLogging)) {
            return false;
        }
        if (this.transformPatternsUnnecessaryDistinct == null) {
            if (other.transformPatternsUnnecessaryDistinct != null) {
                return false;
            }
        } else if (!this.transformPatternsUnnecessaryDistinct.equals(other.transformPatternsUnnecessaryDistinct)) {
            return false;
        }
        if (this.transformPatternsUnnecessaryScalarSubquery == null) {
            if (other.transformPatternsUnnecessaryScalarSubquery != null) {
                return false;
            }
        } else if (!this.transformPatternsUnnecessaryScalarSubquery.equals(other.transformPatternsUnnecessaryScalarSubquery)) {
            return false;
        }
        if (this.transformPatternsUnnecessaryInnerJoin == null) {
            if (other.transformPatternsUnnecessaryInnerJoin != null) {
                return false;
            }
        } else if (!this.transformPatternsUnnecessaryInnerJoin.equals(other.transformPatternsUnnecessaryInnerJoin)) {
            return false;
        }
        if (this.transformPatternsUnnecessaryGroupByExpressions == null) {
            if (other.transformPatternsUnnecessaryGroupByExpressions != null) {
                return false;
            }
        } else if (!this.transformPatternsUnnecessaryGroupByExpressions.equals(other.transformPatternsUnnecessaryGroupByExpressions)) {
            return false;
        }
        if (this.transformPatternsUnnecessaryOrderByExpressions == null) {
            if (other.transformPatternsUnnecessaryOrderByExpressions != null) {
                return false;
            }
        } else if (!this.transformPatternsUnnecessaryOrderByExpressions.equals(other.transformPatternsUnnecessaryOrderByExpressions)) {
            return false;
        }
        if (this.transformPatternsUnnecessaryExistsSubqueryClauses == null) {
            if (other.transformPatternsUnnecessaryExistsSubqueryClauses != null) {
                return false;
            }
        } else if (!this.transformPatternsUnnecessaryExistsSubqueryClauses.equals(other.transformPatternsUnnecessaryExistsSubqueryClauses)) {
            return false;
        }
        if (this.transformPatternsCountConstant == null) {
            if (other.transformPatternsCountConstant != null) {
                return false;
            }
        } else if (!this.transformPatternsCountConstant.equals(other.transformPatternsCountConstant)) {
            return false;
        }
        if (this.transformPatternsTrim == null) {
            if (other.transformPatternsTrim != null) {
                return false;
            }
        } else if (!this.transformPatternsTrim.equals(other.transformPatternsTrim)) {
            return false;
        }
        if (this.transformPatternsNotAnd == null) {
            if (other.transformPatternsNotAnd != null) {
                return false;
            }
        } else if (!this.transformPatternsNotAnd.equals(other.transformPatternsNotAnd)) {
            return false;
        }
        if (this.transformPatternsNotOr == null) {
            if (other.transformPatternsNotOr != null) {
                return false;
            }
        } else if (!this.transformPatternsNotOr.equals(other.transformPatternsNotOr)) {
            return false;
        }
        if (this.transformPatternsNotNot == null) {
            if (other.transformPatternsNotNot != null) {
                return false;
            }
        } else if (!this.transformPatternsNotNot.equals(other.transformPatternsNotNot)) {
            return false;
        }
        if (this.transformPatternsNotComparison == null) {
            if (other.transformPatternsNotComparison != null) {
                return false;
            }
        } else if (!this.transformPatternsNotComparison.equals(other.transformPatternsNotComparison)) {
            return false;
        }
        if (this.transformPatternsNotNotDistinct == null) {
            if (other.transformPatternsNotNotDistinct != null) {
                return false;
            }
        } else if (!this.transformPatternsNotNotDistinct.equals(other.transformPatternsNotNotDistinct)) {
            return false;
        }
        if (this.transformPatternsDistinctFromNull == null) {
            if (other.transformPatternsDistinctFromNull != null) {
                return false;
            }
        } else if (!this.transformPatternsDistinctFromNull.equals(other.transformPatternsDistinctFromNull)) {
            return false;
        }
        if (this.transformPatternsNormaliseAssociativeOps == null) {
            if (other.transformPatternsNormaliseAssociativeOps != null) {
                return false;
            }
        } else if (!this.transformPatternsNormaliseAssociativeOps.equals(other.transformPatternsNormaliseAssociativeOps)) {
            return false;
        }
        if (this.transformPatternsNormaliseInListSingleElementToComparison == null) {
            if (other.transformPatternsNormaliseInListSingleElementToComparison != null) {
                return false;
            }
        } else if (!this.transformPatternsNormaliseInListSingleElementToComparison.equals(other.transformPatternsNormaliseInListSingleElementToComparison)) {
            return false;
        }
        if (this.transformPatternsNormaliseFieldCompareValue == null) {
            if (other.transformPatternsNormaliseFieldCompareValue != null) {
                return false;
            }
        } else if (!this.transformPatternsNormaliseFieldCompareValue.equals(other.transformPatternsNormaliseFieldCompareValue)) {
            return false;
        }
        if (this.transformPatternsNormaliseCoalesceToNvl == null) {
            if (other.transformPatternsNormaliseCoalesceToNvl != null) {
                return false;
            }
        } else if (!this.transformPatternsNormaliseCoalesceToNvl.equals(other.transformPatternsNormaliseCoalesceToNvl)) {
            return false;
        }
        if (this.transformPatternsOrEqToIn == null) {
            if (other.transformPatternsOrEqToIn != null) {
                return false;
            }
        } else if (!this.transformPatternsOrEqToIn.equals(other.transformPatternsOrEqToIn)) {
            return false;
        }
        if (this.transformPatternsAndNeToNotIn == null) {
            if (other.transformPatternsAndNeToNotIn != null) {
                return false;
            }
        } else if (!this.transformPatternsAndNeToNotIn.equals(other.transformPatternsAndNeToNotIn)) {
            return false;
        }
        if (this.transformPatternsMergeOrComparison == null) {
            if (other.transformPatternsMergeOrComparison != null) {
                return false;
            }
        } else if (!this.transformPatternsMergeOrComparison.equals(other.transformPatternsMergeOrComparison)) {
            return false;
        }
        if (this.transformPatternsMergeAndComparison == null) {
            if (other.transformPatternsMergeAndComparison != null) {
                return false;
            }
        } else if (!this.transformPatternsMergeAndComparison.equals(other.transformPatternsMergeAndComparison)) {
            return false;
        }
        if (this.transformPatternsMergeInLists == null) {
            if (other.transformPatternsMergeInLists != null) {
                return false;
            }
        } else if (!this.transformPatternsMergeInLists.equals(other.transformPatternsMergeInLists)) {
            return false;
        }
        if (this.transformPatternsMergeRangePredicates == null) {
            if (other.transformPatternsMergeRangePredicates != null) {
                return false;
            }
        } else if (!this.transformPatternsMergeRangePredicates.equals(other.transformPatternsMergeRangePredicates)) {
            return false;
        }
        if (this.transformPatternsMergeBetweenSymmetricPredicates == null) {
            if (other.transformPatternsMergeBetweenSymmetricPredicates != null) {
                return false;
            }
        } else if (!this.transformPatternsMergeBetweenSymmetricPredicates.equals(other.transformPatternsMergeBetweenSymmetricPredicates)) {
            return false;
        }
        if (this.transformPatternsCaseSearchedToCaseSimple == null) {
            if (other.transformPatternsCaseSearchedToCaseSimple != null) {
                return false;
            }
        } else if (!this.transformPatternsCaseSearchedToCaseSimple.equals(other.transformPatternsCaseSearchedToCaseSimple)) {
            return false;
        }
        if (this.transformPatternsCaseElseNull == null) {
            if (other.transformPatternsCaseElseNull != null) {
                return false;
            }
        } else if (!this.transformPatternsCaseElseNull.equals(other.transformPatternsCaseElseNull)) {
            return false;
        }
        if (this.transformPatternsUnreachableCaseClauses == null) {
            if (other.transformPatternsUnreachableCaseClauses != null) {
                return false;
            }
        } else if (!this.transformPatternsUnreachableCaseClauses.equals(other.transformPatternsUnreachableCaseClauses)) {
            return false;
        }
        if (this.transformPatternsUnreachableDecodeClauses == null) {
            if (other.transformPatternsUnreachableDecodeClauses != null) {
                return false;
            }
        } else if (!this.transformPatternsUnreachableDecodeClauses.equals(other.transformPatternsUnreachableDecodeClauses)) {
            return false;
        }
        if (this.transformPatternsCaseDistinctToDecode == null) {
            if (other.transformPatternsCaseDistinctToDecode != null) {
                return false;
            }
        } else if (!this.transformPatternsCaseDistinctToDecode.equals(other.transformPatternsCaseDistinctToDecode)) {
            return false;
        }
        if (this.transformPatternsCaseMergeWhenWhen == null) {
            if (other.transformPatternsCaseMergeWhenWhen != null) {
                return false;
            }
        } else if (!this.transformPatternsCaseMergeWhenWhen.equals(other.transformPatternsCaseMergeWhenWhen)) {
            return false;
        }
        if (this.transformPatternsCaseMergeWhenElse == null) {
            if (other.transformPatternsCaseMergeWhenElse != null) {
                return false;
            }
        } else if (!this.transformPatternsCaseMergeWhenElse.equals(other.transformPatternsCaseMergeWhenElse)) {
            return false;
        }
        if (this.transformPatternsCaseToCaseAbbreviation == null) {
            if (other.transformPatternsCaseToCaseAbbreviation != null) {
                return false;
            }
        } else if (!this.transformPatternsCaseToCaseAbbreviation.equals(other.transformPatternsCaseToCaseAbbreviation)) {
            return false;
        }
        if (this.transformPatternsSimplifyCaseAbbreviation == null) {
            if (other.transformPatternsSimplifyCaseAbbreviation != null) {
                return false;
            }
        } else if (!this.transformPatternsSimplifyCaseAbbreviation.equals(other.transformPatternsSimplifyCaseAbbreviation)) {
            return false;
        }
        if (this.transformPatternsFlattenCaseAbbreviation == null) {
            if (other.transformPatternsFlattenCaseAbbreviation != null) {
                return false;
            }
        } else if (!this.transformPatternsFlattenCaseAbbreviation.equals(other.transformPatternsFlattenCaseAbbreviation)) {
            return false;
        }
        if (this.transformPatternsFlattenDecode == null) {
            if (other.transformPatternsFlattenDecode != null) {
                return false;
            }
        } else if (!this.transformPatternsFlattenDecode.equals(other.transformPatternsFlattenDecode)) {
            return false;
        }
        if (this.transformPatternsFlattenCase == null) {
            if (other.transformPatternsFlattenCase != null) {
                return false;
            }
        } else if (!this.transformPatternsFlattenCase.equals(other.transformPatternsFlattenCase)) {
            return false;
        }
        if (this.transformPatternsTrivialCaseAbbreviation == null) {
            if (other.transformPatternsTrivialCaseAbbreviation != null) {
                return false;
            }
        } else if (!this.transformPatternsTrivialCaseAbbreviation.equals(other.transformPatternsTrivialCaseAbbreviation)) {
            return false;
        }
        if (this.transformPatternsTrivialPredicates == null) {
            if (other.transformPatternsTrivialPredicates != null) {
                return false;
            }
        } else if (!this.transformPatternsTrivialPredicates.equals(other.transformPatternsTrivialPredicates)) {
            return false;
        }
        if (this.transformPatternsTrivialBitwiseOperations == null) {
            if (other.transformPatternsTrivialBitwiseOperations != null) {
                return false;
            }
        } else if (!this.transformPatternsTrivialBitwiseOperations.equals(other.transformPatternsTrivialBitwiseOperations)) {
            return false;
        }
        if (this.transformPatternsBitSet == null) {
            if (other.transformPatternsBitSet != null) {
                return false;
            }
        } else if (!this.transformPatternsBitSet.equals(other.transformPatternsBitSet)) {
            return false;
        }
        if (this.transformPatternsBitGet == null) {
            if (other.transformPatternsBitGet != null) {
                return false;
            }
        } else if (!this.transformPatternsBitGet.equals(other.transformPatternsBitGet)) {
            return false;
        }
        if (this.transformPatternsScalarSubqueryCountAsteriskGtZero == null) {
            if (other.transformPatternsScalarSubqueryCountAsteriskGtZero != null) {
                return false;
            }
        } else if (!this.transformPatternsScalarSubqueryCountAsteriskGtZero.equals(other.transformPatternsScalarSubqueryCountAsteriskGtZero)) {
            return false;
        }
        if (this.transformPatternsScalarSubqueryCountExpressionGtZero == null) {
            if (other.transformPatternsScalarSubqueryCountExpressionGtZero != null) {
                return false;
            }
        } else if (!this.transformPatternsScalarSubqueryCountExpressionGtZero.equals(other.transformPatternsScalarSubqueryCountExpressionGtZero)) {
            return false;
        }
        if (this.transformPatternsEmptyScalarSubquery == null) {
            if (other.transformPatternsEmptyScalarSubquery != null) {
                return false;
            }
        } else if (!this.transformPatternsEmptyScalarSubquery.equals(other.transformPatternsEmptyScalarSubquery)) {
            return false;
        }
        if (this.transformPatternsNegNeg == null) {
            if (other.transformPatternsNegNeg != null) {
                return false;
            }
        } else if (!this.transformPatternsNegNeg.equals(other.transformPatternsNegNeg)) {
            return false;
        }
        if (this.transformPatternsBitNotBitNot == null) {
            if (other.transformPatternsBitNotBitNot != null) {
                return false;
            }
        } else if (!this.transformPatternsBitNotBitNot.equals(other.transformPatternsBitNotBitNot)) {
            return false;
        }
        if (this.transformPatternsBitNotBitNand == null) {
            if (other.transformPatternsBitNotBitNand != null) {
                return false;
            }
        } else if (!this.transformPatternsBitNotBitNand.equals(other.transformPatternsBitNotBitNand)) {
            return false;
        }
        if (this.transformPatternsBitNotBitNor == null) {
            if (other.transformPatternsBitNotBitNor != null) {
                return false;
            }
        } else if (!this.transformPatternsBitNotBitNor.equals(other.transformPatternsBitNotBitNor)) {
            return false;
        }
        if (this.transformPatternsBitNotBitXNor == null) {
            if (other.transformPatternsBitNotBitXNor != null) {
                return false;
            }
        } else if (!this.transformPatternsBitNotBitXNor.equals(other.transformPatternsBitNotBitXNor)) {
            return false;
        }
        if (this.transformPatternsNullOnNullInput == null) {
            if (other.transformPatternsNullOnNullInput != null) {
                return false;
            }
        } else if (!this.transformPatternsNullOnNullInput.equals(other.transformPatternsNullOnNullInput)) {
            return false;
        }
        if (this.transformPatternsIdempotentFunctionRepetition == null) {
            if (other.transformPatternsIdempotentFunctionRepetition != null) {
                return false;
            }
        } else if (!this.transformPatternsIdempotentFunctionRepetition.equals(other.transformPatternsIdempotentFunctionRepetition)) {
            return false;
        }
        if (this.transformPatternsArithmeticComparisons == null) {
            if (other.transformPatternsArithmeticComparisons != null) {
                return false;
            }
        } else if (!this.transformPatternsArithmeticComparisons.equals(other.transformPatternsArithmeticComparisons)) {
            return false;
        }
        if (this.transformPatternsArithmeticExpressions == null) {
            if (other.transformPatternsArithmeticExpressions != null) {
                return false;
            }
        } else if (!this.transformPatternsArithmeticExpressions.equals(other.transformPatternsArithmeticExpressions)) {
            return false;
        }
        if (this.transformPatternsTrigonometricFunctions == null) {
            if (other.transformPatternsTrigonometricFunctions != null) {
                return false;
            }
        } else if (!this.transformPatternsTrigonometricFunctions.equals(other.transformPatternsTrigonometricFunctions)) {
            return false;
        }
        if (this.transformPatternsLogarithmicFunctions == null) {
            if (other.transformPatternsLogarithmicFunctions != null) {
                return false;
            }
        } else if (!this.transformPatternsLogarithmicFunctions.equals(other.transformPatternsLogarithmicFunctions)) {
            return false;
        }
        if (this.transformPatternsHyperbolicFunctions == null) {
            if (other.transformPatternsHyperbolicFunctions != null) {
                return false;
            }
        } else if (!this.transformPatternsHyperbolicFunctions.equals(other.transformPatternsHyperbolicFunctions)) {
            return false;
        }
        if (this.transformPatternsInverseHyperbolicFunctions == null) {
            if (other.transformPatternsInverseHyperbolicFunctions != null) {
                return false;
            }
        } else if (!this.transformPatternsInverseHyperbolicFunctions.equals(other.transformPatternsInverseHyperbolicFunctions)) {
            return false;
        }
        if (this.transformInlineBindValuesForFieldComparisons == null) {
            if (other.transformInlineBindValuesForFieldComparisons != null) {
                return false;
            }
        } else if (!this.transformInlineBindValuesForFieldComparisons.equals(other.transformInlineBindValuesForFieldComparisons)) {
            return false;
        }
        if (this.transformAnsiJoinToTableLists == null) {
            if (other.transformAnsiJoinToTableLists != null) {
                return false;
            }
        } else if (!this.transformAnsiJoinToTableLists.equals(other.transformAnsiJoinToTableLists)) {
            return false;
        }
        if (this.transformInConditionSubqueryWithLimitToDerivedTable == null) {
            if (other.transformInConditionSubqueryWithLimitToDerivedTable != null) {
                return false;
            }
        } else if (!this.transformInConditionSubqueryWithLimitToDerivedTable.equals(other.transformInConditionSubqueryWithLimitToDerivedTable)) {
            return false;
        }
        if (this.transformQualify == null) {
            if (other.transformQualify != null) {
                return false;
            }
        } else if (!this.transformQualify.equals(other.transformQualify)) {
            return false;
        }
        if (this.transformTableListsToAnsiJoin == null) {
            if (other.transformTableListsToAnsiJoin != null) {
                return false;
            }
        } else if (!this.transformTableListsToAnsiJoin.equals(other.transformTableListsToAnsiJoin)) {
            return false;
        }
        if (this.transformRownum == null) {
            if (other.transformRownum != null) {
                return false;
            }
        } else if (!this.transformRownum.equals(other.transformRownum)) {
            return false;
        }
        if (this.transformUnneededArithmeticExpressions == null) {
            if (other.transformUnneededArithmeticExpressions != null) {
                return false;
            }
        } else if (!this.transformUnneededArithmeticExpressions.equals(other.transformUnneededArithmeticExpressions)) {
            return false;
        }
        if (this.transformGroupByColumnIndex == null) {
            if (other.transformGroupByColumnIndex != null) {
                return false;
            }
        } else if (!this.transformGroupByColumnIndex.equals(other.transformGroupByColumnIndex)) {
            return false;
        }
        if (this.transformInlineCTE == null) {
            if (other.transformInlineCTE != null) {
                return false;
            }
        } else if (!this.transformInlineCTE.equals(other.transformInlineCTE)) {
            return false;
        }
        if (this.backslashEscaping == null) {
            if (other.backslashEscaping != null) {
                return false;
            }
        } else if (!this.backslashEscaping.equals(other.backslashEscaping)) {
            return false;
        }
        if (this.paramType == null) {
            if (other.paramType != null) {
                return false;
            }
        } else if (!this.paramType.equals(other.paramType)) {
            return false;
        }
        if (this.paramCastMode == null) {
            if (other.paramCastMode != null) {
                return false;
            }
        } else if (!this.paramCastMode.equals(other.paramCastMode)) {
            return false;
        }
        if (this.statementType == null) {
            if (other.statementType != null) {
                return false;
            }
        } else if (!this.statementType.equals(other.statementType)) {
            return false;
        }
        if (this.inlineThreshold == null) {
            if (other.inlineThreshold != null) {
                return false;
            }
        } else if (!this.inlineThreshold.equals(other.inlineThreshold)) {
            return false;
        }
        if (this.transactionListenerStartInvocationOrder == null) {
            if (other.transactionListenerStartInvocationOrder != null) {
                return false;
            }
        } else if (!this.transactionListenerStartInvocationOrder.equals(other.transactionListenerStartInvocationOrder)) {
            return false;
        }
        if (this.transactionListenerEndInvocationOrder == null) {
            if (other.transactionListenerEndInvocationOrder != null) {
                return false;
            }
        } else if (!this.transactionListenerEndInvocationOrder.equals(other.transactionListenerEndInvocationOrder)) {
            return false;
        }
        if (this.migrationListenerStartInvocationOrder == null) {
            if (other.migrationListenerStartInvocationOrder != null) {
                return false;
            }
        } else if (!this.migrationListenerStartInvocationOrder.equals(other.migrationListenerStartInvocationOrder)) {
            return false;
        }
        if (this.migrationListenerEndInvocationOrder == null) {
            if (other.migrationListenerEndInvocationOrder != null) {
                return false;
            }
        } else if (!this.migrationListenerEndInvocationOrder.equals(other.migrationListenerEndInvocationOrder)) {
            return false;
        }
        if (this.visitListenerStartInvocationOrder == null) {
            if (other.visitListenerStartInvocationOrder != null) {
                return false;
            }
        } else if (!this.visitListenerStartInvocationOrder.equals(other.visitListenerStartInvocationOrder)) {
            return false;
        }
        if (this.visitListenerEndInvocationOrder == null) {
            if (other.visitListenerEndInvocationOrder != null) {
                return false;
            }
        } else if (!this.visitListenerEndInvocationOrder.equals(other.visitListenerEndInvocationOrder)) {
            return false;
        }
        if (this.recordListenerStartInvocationOrder == null) {
            if (other.recordListenerStartInvocationOrder != null) {
                return false;
            }
        } else if (!this.recordListenerStartInvocationOrder.equals(other.recordListenerStartInvocationOrder)) {
            return false;
        }
        if (this.recordListenerEndInvocationOrder == null) {
            if (other.recordListenerEndInvocationOrder != null) {
                return false;
            }
        } else if (!this.recordListenerEndInvocationOrder.equals(other.recordListenerEndInvocationOrder)) {
            return false;
        }
        if (this.executeListenerStartInvocationOrder == null) {
            if (other.executeListenerStartInvocationOrder != null) {
                return false;
            }
        } else if (!this.executeListenerStartInvocationOrder.equals(other.executeListenerStartInvocationOrder)) {
            return false;
        }
        if (this.executeListenerEndInvocationOrder == null) {
            if (other.executeListenerEndInvocationOrder != null) {
                return false;
            }
        } else if (!this.executeListenerEndInvocationOrder.equals(other.executeListenerEndInvocationOrder)) {
            return false;
        }
        if (this.executeLogging == null) {
            if (other.executeLogging != null) {
                return false;
            }
        } else if (!this.executeLogging.equals(other.executeLogging)) {
            return false;
        }
        if (this.executeLoggingSQLExceptions == null) {
            if (other.executeLoggingSQLExceptions != null) {
                return false;
            }
        } else if (!this.executeLoggingSQLExceptions.equals(other.executeLoggingSQLExceptions)) {
            return false;
        }
        if (this.diagnosticsLogging == null) {
            if (other.diagnosticsLogging != null) {
                return false;
            }
        } else if (!this.diagnosticsLogging.equals(other.diagnosticsLogging)) {
            return false;
        }
        if (this.diagnosticsConnection == null) {
            if (other.diagnosticsConnection != null) {
                return false;
            }
        } else if (!this.diagnosticsConnection.equals(other.diagnosticsConnection)) {
            return false;
        }
        if (this.updateRecordVersion == null) {
            if (other.updateRecordVersion != null) {
                return false;
            }
        } else if (!this.updateRecordVersion.equals(other.updateRecordVersion)) {
            return false;
        }
        if (this.updateRecordTimestamp == null) {
            if (other.updateRecordTimestamp != null) {
                return false;
            }
        } else if (!this.updateRecordTimestamp.equals(other.updateRecordTimestamp)) {
            return false;
        }
        if (this.executeWithOptimisticLocking == null) {
            if (other.executeWithOptimisticLocking != null) {
                return false;
            }
        } else if (!this.executeWithOptimisticLocking.equals(other.executeWithOptimisticLocking)) {
            return false;
        }
        if (this.executeWithOptimisticLockingExcludeUnversioned == null) {
            if (other.executeWithOptimisticLockingExcludeUnversioned != null) {
                return false;
            }
        } else if (!this.executeWithOptimisticLockingExcludeUnversioned.equals(other.executeWithOptimisticLockingExcludeUnversioned)) {
            return false;
        }
        if (this.attachRecords == null) {
            if (other.attachRecords != null) {
                return false;
            }
        } else if (!this.attachRecords.equals(other.attachRecords)) {
            return false;
        }
        if (this.insertUnchangedRecords == null) {
            if (other.insertUnchangedRecords != null) {
                return false;
            }
        } else if (!this.insertUnchangedRecords.equals(other.insertUnchangedRecords)) {
            return false;
        }
        if (this.updateUnchangedRecords == null) {
            if (other.updateUnchangedRecords != null) {
                return false;
            }
        } else if (!this.updateUnchangedRecords.equals(other.updateUnchangedRecords)) {
            return false;
        }
        if (this.updatablePrimaryKeys == null) {
            if (other.updatablePrimaryKeys != null) {
                return false;
            }
        } else if (!this.updatablePrimaryKeys.equals(other.updatablePrimaryKeys)) {
            return false;
        }
        if (this.reflectionCaching == null) {
            if (other.reflectionCaching != null) {
                return false;
            }
        } else if (!this.reflectionCaching.equals(other.reflectionCaching)) {
            return false;
        }
        if (this.cacheRecordMappers == null) {
            if (other.cacheRecordMappers != null) {
                return false;
            }
        } else if (!this.cacheRecordMappers.equals(other.cacheRecordMappers)) {
            return false;
        }
        if (this.cacheParsingConnection == null) {
            if (other.cacheParsingConnection != null) {
                return false;
            }
        } else if (!this.cacheParsingConnection.equals(other.cacheParsingConnection)) {
            return false;
        }
        if (this.cacheParsingConnectionLRUCacheSize == null) {
            if (other.cacheParsingConnectionLRUCacheSize != null) {
                return false;
            }
        } else if (!this.cacheParsingConnectionLRUCacheSize.equals(other.cacheParsingConnectionLRUCacheSize)) {
            return false;
        }
        if (this.cachePreparedStatementInLoader == null) {
            if (other.cachePreparedStatementInLoader != null) {
                return false;
            }
        } else if (!this.cachePreparedStatementInLoader.equals(other.cachePreparedStatementInLoader)) {
            return false;
        }
        if (this.throwExceptions == null) {
            if (other.throwExceptions != null) {
                return false;
            }
        } else if (!this.throwExceptions.equals(other.throwExceptions)) {
            return false;
        }
        if (this.fetchWarnings == null) {
            if (other.fetchWarnings != null) {
                return false;
            }
        } else if (!this.fetchWarnings.equals(other.fetchWarnings)) {
            return false;
        }
        if (this.fetchServerOutputSize == null) {
            if (other.fetchServerOutputSize != null) {
                return false;
            }
        } else if (!this.fetchServerOutputSize.equals(other.fetchServerOutputSize)) {
            return false;
        }
        if (this.returnIdentityOnUpdatableRecord == null) {
            if (other.returnIdentityOnUpdatableRecord != null) {
                return false;
            }
        } else if (!this.returnIdentityOnUpdatableRecord.equals(other.returnIdentityOnUpdatableRecord)) {
            return false;
        }
        if (this.returnDefaultOnUpdatableRecord == null) {
            if (other.returnDefaultOnUpdatableRecord != null) {
                return false;
            }
        } else if (!this.returnDefaultOnUpdatableRecord.equals(other.returnDefaultOnUpdatableRecord)) {
            return false;
        }
        if (this.returnComputedOnUpdatableRecord == null) {
            if (other.returnComputedOnUpdatableRecord != null) {
                return false;
            }
        } else if (!this.returnComputedOnUpdatableRecord.equals(other.returnComputedOnUpdatableRecord)) {
            return false;
        }
        if (this.returnAllOnUpdatableRecord == null) {
            if (other.returnAllOnUpdatableRecord != null) {
                return false;
            }
        } else if (!this.returnAllOnUpdatableRecord.equals(other.returnAllOnUpdatableRecord)) {
            return false;
        }
        if (this.returnRecordToPojo == null) {
            if (other.returnRecordToPojo != null) {
                return false;
            }
        } else if (!this.returnRecordToPojo.equals(other.returnRecordToPojo)) {
            return false;
        }
        if (this.mapJPAAnnotations == null) {
            if (other.mapJPAAnnotations != null) {
                return false;
            }
        } else if (!this.mapJPAAnnotations.equals(other.mapJPAAnnotations)) {
            return false;
        }
        if (this.mapRecordComponentParameterNames == null) {
            if (other.mapRecordComponentParameterNames != null) {
                return false;
            }
        } else if (!this.mapRecordComponentParameterNames.equals(other.mapRecordComponentParameterNames)) {
            return false;
        }
        if (this.mapConstructorPropertiesParameterNames == null) {
            if (other.mapConstructorPropertiesParameterNames != null) {
                return false;
            }
        } else if (!this.mapConstructorPropertiesParameterNames.equals(other.mapConstructorPropertiesParameterNames)) {
            return false;
        }
        if (this.mapConstructorParameterNames == null) {
            if (other.mapConstructorParameterNames != null) {
                return false;
            }
        } else if (!this.mapConstructorParameterNames.equals(other.mapConstructorParameterNames)) {
            return false;
        }
        if (this.mapConstructorParameterNamesInKotlin == null) {
            if (other.mapConstructorParameterNamesInKotlin != null) {
                return false;
            }
        } else if (!this.mapConstructorParameterNamesInKotlin.equals(other.mapConstructorParameterNamesInKotlin)) {
            return false;
        }
        if (this.queryPoolable == null) {
            if (other.queryPoolable != null) {
                return false;
            }
        } else if (!this.queryPoolable.equals(other.queryPoolable)) {
            return false;
        }
        if (this.queryTimeout == null) {
            if (other.queryTimeout != null) {
                return false;
            }
        } else if (!this.queryTimeout.equals(other.queryTimeout)) {
            return false;
        }
        if (this.maxRows == null) {
            if (other.maxRows != null) {
                return false;
            }
        } else if (!this.maxRows.equals(other.maxRows)) {
            return false;
        }
        if (this.fetchSize == null) {
            if (other.fetchSize != null) {
                return false;
            }
        } else if (!this.fetchSize.equals(other.fetchSize)) {
            return false;
        }
        if (this.batchSize == null) {
            if (other.batchSize != null) {
                return false;
            }
        } else if (!this.batchSize.equals(other.batchSize)) {
            return false;
        }
        if (this.debugInfoOnStackTrace == null) {
            if (other.debugInfoOnStackTrace != null) {
                return false;
            }
        } else if (!this.debugInfoOnStackTrace.equals(other.debugInfoOnStackTrace)) {
            return false;
        }
        if (this.inListPadding == null) {
            if (other.inListPadding != null) {
                return false;
            }
        } else if (!this.inListPadding.equals(other.inListPadding)) {
            return false;
        }
        if (this.inListPadBase == null) {
            if (other.inListPadBase != null) {
                return false;
            }
        } else if (!this.inListPadBase.equals(other.inListPadBase)) {
            return false;
        }
        if (this.delimiter == null) {
            if (other.delimiter != null) {
                return false;
            }
        } else if (!this.delimiter.equals(other.delimiter)) {
            return false;
        }
        if (this.emulateOnDuplicateKeyUpdateOnPrimaryKeyOnly == null) {
            if (other.emulateOnDuplicateKeyUpdateOnPrimaryKeyOnly != null) {
                return false;
            }
        } else if (!this.emulateOnDuplicateKeyUpdateOnPrimaryKeyOnly.equals(other.emulateOnDuplicateKeyUpdateOnPrimaryKeyOnly)) {
            return false;
        }
        if (this.emulateMultiset == null) {
            if (other.emulateMultiset != null) {
                return false;
            }
        } else if (!this.emulateMultiset.equals(other.emulateMultiset)) {
            return false;
        }
        if (this.emulateComputedColumns == null) {
            if (other.emulateComputedColumns != null) {
                return false;
            }
        } else if (!this.emulateComputedColumns.equals(other.emulateComputedColumns)) {
            return false;
        }
        if (this.executeUpdateWithoutWhere == null) {
            if (other.executeUpdateWithoutWhere != null) {
                return false;
            }
        } else if (!this.executeUpdateWithoutWhere.equals(other.executeUpdateWithoutWhere)) {
            return false;
        }
        if (this.executeDeleteWithoutWhere == null) {
            if (other.executeDeleteWithoutWhere != null) {
                return false;
            }
        } else if (!this.executeDeleteWithoutWhere.equals(other.executeDeleteWithoutWhere)) {
            return false;
        }
        if (this.interpreterDialect == null) {
            if (other.interpreterDialect != null) {
                return false;
            }
        } else if (!this.interpreterDialect.equals(other.interpreterDialect)) {
            return false;
        }
        if (this.interpreterNameLookupCaseSensitivity == null) {
            if (other.interpreterNameLookupCaseSensitivity != null) {
                return false;
            }
        } else if (!this.interpreterNameLookupCaseSensitivity.equals(other.interpreterNameLookupCaseSensitivity)) {
            return false;
        }
        if (this.interpreterLocale == null) {
            if (other.interpreterLocale != null) {
                return false;
            }
        } else if (!this.interpreterLocale.equals(other.interpreterLocale)) {
            return false;
        }
        if (this.interpreterDelayForeignKeyDeclarations == null) {
            if (other.interpreterDelayForeignKeyDeclarations != null) {
                return false;
            }
        } else if (!this.interpreterDelayForeignKeyDeclarations.equals(other.interpreterDelayForeignKeyDeclarations)) {
            return false;
        }
        if (this.metaIncludeSystemIndexes == null) {
            if (other.metaIncludeSystemIndexes != null) {
                return false;
            }
        } else if (!this.metaIncludeSystemIndexes.equals(other.metaIncludeSystemIndexes)) {
            return false;
        }
        if (this.metaIncludeSystemSequences == null) {
            if (other.metaIncludeSystemSequences != null) {
                return false;
            }
        } else if (!this.metaIncludeSystemSequences.equals(other.metaIncludeSystemSequences)) {
            return false;
        }
        if (this.migrationHistorySchema == null) {
            if (other.migrationHistorySchema != null) {
                return false;
            }
        } else if (!this.migrationHistorySchema.equals(other.migrationHistorySchema)) {
            return false;
        }
        if (this.migrationHistorySchemaCreateSchemaIfNotExists == null) {
            if (other.migrationHistorySchemaCreateSchemaIfNotExists != null) {
                return false;
            }
        } else if (!this.migrationHistorySchemaCreateSchemaIfNotExists.equals(other.migrationHistorySchemaCreateSchemaIfNotExists)) {
            return false;
        }
        if (this.migrationDefaultSchema == null) {
            if (other.migrationDefaultSchema != null) {
                return false;
            }
        } else if (!this.migrationDefaultSchema.equals(other.migrationDefaultSchema)) {
            return false;
        }
        if (this.migrationSchemataCreateSchemaIfNotExists == null) {
            if (other.migrationSchemataCreateSchemaIfNotExists != null) {
                return false;
            }
        } else if (!this.migrationSchemataCreateSchemaIfNotExists.equals(other.migrationSchemataCreateSchemaIfNotExists)) {
            return false;
        }
        if (this.migrationAllowsUndo == null) {
            if (other.migrationAllowsUndo != null) {
                return false;
            }
        } else if (!this.migrationAllowsUndo.equals(other.migrationAllowsUndo)) {
            return false;
        }
        if (this.migrationRevertUntracked == null) {
            if (other.migrationRevertUntracked != null) {
                return false;
            }
        } else if (!this.migrationRevertUntracked.equals(other.migrationRevertUntracked)) {
            return false;
        }
        if (this.migrationAutoBaseline == null) {
            if (other.migrationAutoBaseline != null) {
                return false;
            }
        } else if (!this.migrationAutoBaseline.equals(other.migrationAutoBaseline)) {
            return false;
        }
        if (this.migrationAutoVerification == null) {
            if (other.migrationAutoVerification != null) {
                return false;
            }
        } else if (!this.migrationAutoVerification.equals(other.migrationAutoVerification)) {
            return false;
        }
        if (this.migrationIgnoreDefaultTimestampPrecisionDiffs == null) {
            if (other.migrationIgnoreDefaultTimestampPrecisionDiffs != null) {
                return false;
            }
        } else if (!this.migrationIgnoreDefaultTimestampPrecisionDiffs.equals(other.migrationIgnoreDefaultTimestampPrecisionDiffs)) {
            return false;
        }
        if (this.locale == null) {
            if (other.locale != null) {
                return false;
            }
        } else if (!this.locale.equals(other.locale)) {
            return false;
        }
        if (this.parseDialect == null) {
            if (other.parseDialect != null) {
                return false;
            }
        } else if (!this.parseDialect.equals(other.parseDialect)) {
            return false;
        }
        if (this.parseLocale == null) {
            if (other.parseLocale != null) {
                return false;
            }
        } else if (!this.parseLocale.equals(other.parseLocale)) {
            return false;
        }
        if (this.parseDateFormat == null) {
            if (other.parseDateFormat != null) {
                return false;
            }
        } else if (!this.parseDateFormat.equals(other.parseDateFormat)) {
            return false;
        }
        if (this.parseTimestampFormat == null) {
            if (other.parseTimestampFormat != null) {
                return false;
            }
        } else if (!this.parseTimestampFormat.equals(other.parseTimestampFormat)) {
            return false;
        }
        if (this.parseNamedParamPrefix == null) {
            if (other.parseNamedParamPrefix != null) {
                return false;
            }
        } else if (!this.parseNamedParamPrefix.equals(other.parseNamedParamPrefix)) {
            return false;
        }
        if (this.parseNameCase == null) {
            if (other.parseNameCase != null) {
                return false;
            }
        } else if (!this.parseNameCase.equals(other.parseNameCase)) {
            return false;
        }
        if (this.parseWithMetaLookups == null) {
            if (other.parseWithMetaLookups != null) {
                return false;
            }
        } else if (!this.parseWithMetaLookups.equals(other.parseWithMetaLookups)) {
            return false;
        }
        if (this.parseAppendMissingTableReferences == null) {
            if (other.parseAppendMissingTableReferences != null) {
                return false;
            }
        } else if (!this.parseAppendMissingTableReferences.equals(other.parseAppendMissingTableReferences)) {
            return false;
        }
        if (this.parseSetCommands == null) {
            if (other.parseSetCommands != null) {
                return false;
            }
        } else if (!this.parseSetCommands.equals(other.parseSetCommands)) {
            return false;
        }
        if (this.parseUnsupportedSyntax == null) {
            if (other.parseUnsupportedSyntax != null) {
                return false;
            }
        } else if (!this.parseUnsupportedSyntax.equals(other.parseUnsupportedSyntax)) {
            return false;
        }
        if (this.parseUnknownFunctions == null) {
            if (other.parseUnknownFunctions != null) {
                return false;
            }
        } else if (!this.parseUnknownFunctions.equals(other.parseUnknownFunctions)) {
            return false;
        }
        if (this.parseIgnoreCommercialOnlyFeatures == null) {
            if (other.parseIgnoreCommercialOnlyFeatures != null) {
                return false;
            }
        } else if (!this.parseIgnoreCommercialOnlyFeatures.equals(other.parseIgnoreCommercialOnlyFeatures)) {
            return false;
        }
        if (this.parseIgnoreComments == null) {
            if (other.parseIgnoreComments != null) {
                return false;
            }
        } else if (!this.parseIgnoreComments.equals(other.parseIgnoreComments)) {
            return false;
        }
        if (this.parseIgnoreCommentStart == null) {
            if (other.parseIgnoreCommentStart != null) {
                return false;
            }
        } else if (!this.parseIgnoreCommentStart.equals(other.parseIgnoreCommentStart)) {
            return false;
        }
        if (this.parseIgnoreCommentStop == null) {
            if (other.parseIgnoreCommentStop != null) {
                return false;
            }
        } else if (!this.parseIgnoreCommentStop.equals(other.parseIgnoreCommentStop)) {
            return false;
        }
        if (this.parseRetainCommentsBetweenQueries == null) {
            if (other.parseRetainCommentsBetweenQueries != null) {
                return false;
            }
        } else if (!this.parseRetainCommentsBetweenQueries.equals(other.parseRetainCommentsBetweenQueries)) {
            return false;
        }
        if (this.parseMetaDefaultExpressions == null) {
            if (other.parseMetaDefaultExpressions != null) {
                return false;
            }
        } else if (!this.parseMetaDefaultExpressions.equals(other.parseMetaDefaultExpressions)) {
            return false;
        }
        if (this.parseMetaViewSources == null) {
            if (other.parseMetaViewSources != null) {
                return false;
            }
        } else if (!this.parseMetaViewSources.equals(other.parseMetaViewSources)) {
            return false;
        }
        if (this.readonlyTableRecordInsert == null) {
            if (other.readonlyTableRecordInsert != null) {
                return false;
            }
        } else if (!this.readonlyTableRecordInsert.equals(other.readonlyTableRecordInsert)) {
            return false;
        }
        if (this.readonlyUpdatableRecordUpdate == null) {
            if (other.readonlyUpdatableRecordUpdate != null) {
                return false;
            }
        } else if (!this.readonlyUpdatableRecordUpdate.equals(other.readonlyUpdatableRecordUpdate)) {
            return false;
        }
        if (this.readonlyInsert == null) {
            if (other.readonlyInsert != null) {
                return false;
            }
        } else if (!this.readonlyInsert.equals(other.readonlyInsert)) {
            return false;
        }
        if (this.readonlyUpdate == null) {
            if (other.readonlyUpdate != null) {
                return false;
            }
        } else if (!this.readonlyUpdate.equals(other.readonlyUpdate)) {
            return false;
        }
        if (this.applyWorkaroundFor7962 == null) {
            if (other.applyWorkaroundFor7962 != null) {
                return false;
            }
        } else if (!this.applyWorkaroundFor7962.equals(other.applyWorkaroundFor7962)) {
            return false;
        }
        if (this.warnOnStaticTypeRegistryAccess == null) {
            if (other.warnOnStaticTypeRegistryAccess != null) {
                return false;
            }
        } else if (!this.warnOnStaticTypeRegistryAccess.equals(other.warnOnStaticTypeRegistryAccess)) {
            return false;
        }
        if (this.interpreterSearchPath == null) {
            if (other.interpreterSearchPath != null) {
                return false;
            }
        } else if (!this.interpreterSearchPath.equals(other.interpreterSearchPath)) {
            return false;
        }
        if (this.migrationSchemata == null) {
            if (other.migrationSchemata != null) {
                return false;
            }
        } else if (!this.migrationSchemata.equals(other.migrationSchemata)) {
            return false;
        }
        if (this.parseSearchPath == null) {
            if (other.parseSearchPath != null) {
                return false;
            }
            return true;
        }
        if (!this.parseSearchPath.equals(other.parseSearchPath)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.forceIntegerTypesOnZeroScaleDecimals == null ? 0 : this.forceIntegerTypesOnZeroScaleDecimals.hashCode());
        return (31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * result) + (this.renderCatalog == null ? 0 : this.renderCatalog.hashCode()))) + (this.renderSchema == null ? 0 : this.renderSchema.hashCode()))) + (this.renderTable == null ? 0 : this.renderTable.hashCode()))) + (this.renderMapping == null ? 0 : this.renderMapping.hashCode()))) + (this.renderQuotedNames == null ? 0 : this.renderQuotedNames.hashCode()))) + (this.renderNameCase == null ? 0 : this.renderNameCase.hashCode()))) + (this.renderNameStyle == null ? 0 : this.renderNameStyle.hashCode()))) + (this.renderNamedParamPrefix == null ? 0 : this.renderNamedParamPrefix.hashCode()))) + (this.renderKeywordCase == null ? 0 : this.renderKeywordCase.hashCode()))) + (this.renderKeywordStyle == null ? 0 : this.renderKeywordStyle.hashCode()))) + (this.renderLocale == null ? 0 : this.renderLocale.hashCode()))) + (this.renderFormatted == null ? 0 : this.renderFormatted.hashCode()))) + (this.renderFormatting == null ? 0 : this.renderFormatting.hashCode()))) + (this.renderOptionalAssociativityParentheses == null ? 0 : this.renderOptionalAssociativityParentheses.hashCode()))) + (this.renderOptionalAsKeywordForTableAliases == null ? 0 : this.renderOptionalAsKeywordForTableAliases.hashCode()))) + (this.renderOptionalAsKeywordForFieldAliases == null ? 0 : this.renderOptionalAsKeywordForFieldAliases.hashCode()))) + (this.renderOptionalInnerKeyword == null ? 0 : this.renderOptionalInnerKeyword.hashCode()))) + (this.renderOptionalOuterKeyword == null ? 0 : this.renderOptionalOuterKeyword.hashCode()))) + (this.renderImplicitWindowRange == null ? 0 : this.renderImplicitWindowRange.hashCode()))) + (this.renderScalarSubqueriesForStoredFunctions == null ? 0 : this.renderScalarSubqueriesForStoredFunctions.hashCode()))) + (this.renderImplicitJoinType == null ? 0 : this.renderImplicitJoinType.hashCode()))) + (this.renderImplicitJoinToManyType == null ? 0 : this.renderImplicitJoinToManyType.hashCode()))) + (this.renderDefaultNullability == null ? 0 : this.renderDefaultNullability.hashCode()))) + (this.renderCoalesceToEmptyStringInConcat == null ? 0 : this.renderCoalesceToEmptyStringInConcat.hashCode()))) + (this.renderOrderByRownumberForEmulatedPagination == null ? 0 : this.renderOrderByRownumberForEmulatedPagination.hashCode()))) + (this.renderOutputForSQLServerReturningClause == null ? 0 : this.renderOutputForSQLServerReturningClause.hashCode()))) + (this.renderGroupConcatMaxLenSessionVariable == null ? 0 : this.renderGroupConcatMaxLenSessionVariable.hashCode()))) + (this.renderParenthesisAroundSetOperationQueries == null ? 0 : this.renderParenthesisAroundSetOperationQueries.hashCode()))) + (this.renderVariablesInDerivedTablesForEmulations == null ? 0 : this.renderVariablesInDerivedTablesForEmulations.hashCode()))) + (this.renderRowConditionForSeekClause == null ? 0 : this.renderRowConditionForSeekClause.hashCode()))) + (this.renderRedundantConditionForSeekClause == null ? 0 : this.renderRedundantConditionForSeekClause.hashCode()))) + (this.renderPlainSQLTemplatesAsRaw == null ? 0 : this.renderPlainSQLTemplatesAsRaw.hashCode()))) + (this.renderDollarQuotedStringToken == null ? 0 : this.renderDollarQuotedStringToken.hashCode()))) + (this.namePathSeparator == null ? 0 : this.namePathSeparator.hashCode()))) + (this.bindOffsetDateTimeType == null ? 0 : this.bindOffsetDateTimeType.hashCode()))) + (this.bindOffsetTimeType == null ? 0 : this.bindOffsetTimeType.hashCode()))) + (this.fetchTriggerValuesAfterSQLServerOutput == null ? 0 : this.fetchTriggerValuesAfterSQLServerOutput.hashCode()))) + (this.fetchTriggerValuesAfterReturning == null ? 0 : this.fetchTriggerValuesAfterReturning.hashCode()))) + (this.fetchIntermediateResult == null ? 0 : this.fetchIntermediateResult.hashCode()))) + (this.diagnosticsDuplicateStatements == null ? 0 : this.diagnosticsDuplicateStatements.hashCode()))) + (this.diagnosticsDuplicateStatementsUsingTransformPatterns == null ? 0 : this.diagnosticsDuplicateStatementsUsingTransformPatterns.hashCode()))) + (this.diagnosticsMissingWasNullCall == null ? 0 : this.diagnosticsMissingWasNullCall.hashCode()))) + (this.diagnosticsRepeatedStatements == null ? 0 : this.diagnosticsRepeatedStatements.hashCode()))) + (this.diagnosticsConsecutiveAggregation == null ? 0 : this.diagnosticsConsecutiveAggregation.hashCode()))) + (this.diagnosticsConcatenationInPredicate == null ? 0 : this.diagnosticsConcatenationInPredicate.hashCode()))) + (this.diagnosticsPossiblyWrongExpression == null ? 0 : this.diagnosticsPossiblyWrongExpression.hashCode()))) + (this.diagnosticsTooManyColumnsFetched == null ? 0 : this.diagnosticsTooManyColumnsFetched.hashCode()))) + (this.diagnosticsTooManyRowsFetched == null ? 0 : this.diagnosticsTooManyRowsFetched.hashCode()))) + (this.diagnosticsUnnecessaryWasNullCall == null ? 0 : this.diagnosticsUnnecessaryWasNullCall.hashCode()))) + (this.diagnosticsPatterns == null ? 0 : this.diagnosticsPatterns.hashCode()))) + (this.diagnosticsTrivialCondition == null ? 0 : this.diagnosticsTrivialCondition.hashCode()))) + (this.diagnosticsNullCondition == null ? 0 : this.diagnosticsNullCondition.hashCode()))) + (this.transformPatterns == null ? 0 : this.transformPatterns.hashCode()))) + (this.transformPatternsLogging == null ? 0 : this.transformPatternsLogging.hashCode()))) + (this.transformPatternsUnnecessaryDistinct == null ? 0 : this.transformPatternsUnnecessaryDistinct.hashCode()))) + (this.transformPatternsUnnecessaryScalarSubquery == null ? 0 : this.transformPatternsUnnecessaryScalarSubquery.hashCode()))) + (this.transformPatternsUnnecessaryInnerJoin == null ? 0 : this.transformPatternsUnnecessaryInnerJoin.hashCode()))) + (this.transformPatternsUnnecessaryGroupByExpressions == null ? 0 : this.transformPatternsUnnecessaryGroupByExpressions.hashCode()))) + (this.transformPatternsUnnecessaryOrderByExpressions == null ? 0 : this.transformPatternsUnnecessaryOrderByExpressions.hashCode()))) + (this.transformPatternsUnnecessaryExistsSubqueryClauses == null ? 0 : this.transformPatternsUnnecessaryExistsSubqueryClauses.hashCode()))) + (this.transformPatternsCountConstant == null ? 0 : this.transformPatternsCountConstant.hashCode()))) + (this.transformPatternsTrim == null ? 0 : this.transformPatternsTrim.hashCode()))) + (this.transformPatternsNotAnd == null ? 0 : this.transformPatternsNotAnd.hashCode()))) + (this.transformPatternsNotOr == null ? 0 : this.transformPatternsNotOr.hashCode()))) + (this.transformPatternsNotNot == null ? 0 : this.transformPatternsNotNot.hashCode()))) + (this.transformPatternsNotComparison == null ? 0 : this.transformPatternsNotComparison.hashCode()))) + (this.transformPatternsNotNotDistinct == null ? 0 : this.transformPatternsNotNotDistinct.hashCode()))) + (this.transformPatternsDistinctFromNull == null ? 0 : this.transformPatternsDistinctFromNull.hashCode()))) + (this.transformPatternsNormaliseAssociativeOps == null ? 0 : this.transformPatternsNormaliseAssociativeOps.hashCode()))) + (this.transformPatternsNormaliseInListSingleElementToComparison == null ? 0 : this.transformPatternsNormaliseInListSingleElementToComparison.hashCode()))) + (this.transformPatternsNormaliseFieldCompareValue == null ? 0 : this.transformPatternsNormaliseFieldCompareValue.hashCode()))) + (this.transformPatternsNormaliseCoalesceToNvl == null ? 0 : this.transformPatternsNormaliseCoalesceToNvl.hashCode()))) + (this.transformPatternsOrEqToIn == null ? 0 : this.transformPatternsOrEqToIn.hashCode()))) + (this.transformPatternsAndNeToNotIn == null ? 0 : this.transformPatternsAndNeToNotIn.hashCode()))) + (this.transformPatternsMergeOrComparison == null ? 0 : this.transformPatternsMergeOrComparison.hashCode()))) + (this.transformPatternsMergeAndComparison == null ? 0 : this.transformPatternsMergeAndComparison.hashCode()))) + (this.transformPatternsMergeInLists == null ? 0 : this.transformPatternsMergeInLists.hashCode()))) + (this.transformPatternsMergeRangePredicates == null ? 0 : this.transformPatternsMergeRangePredicates.hashCode()))) + (this.transformPatternsMergeBetweenSymmetricPredicates == null ? 0 : this.transformPatternsMergeBetweenSymmetricPredicates.hashCode()))) + (this.transformPatternsCaseSearchedToCaseSimple == null ? 0 : this.transformPatternsCaseSearchedToCaseSimple.hashCode()))) + (this.transformPatternsCaseElseNull == null ? 0 : this.transformPatternsCaseElseNull.hashCode()))) + (this.transformPatternsUnreachableCaseClauses == null ? 0 : this.transformPatternsUnreachableCaseClauses.hashCode()))) + (this.transformPatternsUnreachableDecodeClauses == null ? 0 : this.transformPatternsUnreachableDecodeClauses.hashCode()))) + (this.transformPatternsCaseDistinctToDecode == null ? 0 : this.transformPatternsCaseDistinctToDecode.hashCode()))) + (this.transformPatternsCaseMergeWhenWhen == null ? 0 : this.transformPatternsCaseMergeWhenWhen.hashCode()))) + (this.transformPatternsCaseMergeWhenElse == null ? 0 : this.transformPatternsCaseMergeWhenElse.hashCode()))) + (this.transformPatternsCaseToCaseAbbreviation == null ? 0 : this.transformPatternsCaseToCaseAbbreviation.hashCode()))) + (this.transformPatternsSimplifyCaseAbbreviation == null ? 0 : this.transformPatternsSimplifyCaseAbbreviation.hashCode()))) + (this.transformPatternsFlattenCaseAbbreviation == null ? 0 : this.transformPatternsFlattenCaseAbbreviation.hashCode()))) + (this.transformPatternsFlattenDecode == null ? 0 : this.transformPatternsFlattenDecode.hashCode()))) + (this.transformPatternsFlattenCase == null ? 0 : this.transformPatternsFlattenCase.hashCode()))) + (this.transformPatternsTrivialCaseAbbreviation == null ? 0 : this.transformPatternsTrivialCaseAbbreviation.hashCode()))) + (this.transformPatternsTrivialPredicates == null ? 0 : this.transformPatternsTrivialPredicates.hashCode()))) + (this.transformPatternsTrivialBitwiseOperations == null ? 0 : this.transformPatternsTrivialBitwiseOperations.hashCode()))) + (this.transformPatternsBitSet == null ? 0 : this.transformPatternsBitSet.hashCode()))) + (this.transformPatternsBitGet == null ? 0 : this.transformPatternsBitGet.hashCode()))) + (this.transformPatternsScalarSubqueryCountAsteriskGtZero == null ? 0 : this.transformPatternsScalarSubqueryCountAsteriskGtZero.hashCode()))) + (this.transformPatternsScalarSubqueryCountExpressionGtZero == null ? 0 : this.transformPatternsScalarSubqueryCountExpressionGtZero.hashCode()))) + (this.transformPatternsEmptyScalarSubquery == null ? 0 : this.transformPatternsEmptyScalarSubquery.hashCode()))) + (this.transformPatternsNegNeg == null ? 0 : this.transformPatternsNegNeg.hashCode()))) + (this.transformPatternsBitNotBitNot == null ? 0 : this.transformPatternsBitNotBitNot.hashCode()))) + (this.transformPatternsBitNotBitNand == null ? 0 : this.transformPatternsBitNotBitNand.hashCode()))) + (this.transformPatternsBitNotBitNor == null ? 0 : this.transformPatternsBitNotBitNor.hashCode()))) + (this.transformPatternsBitNotBitXNor == null ? 0 : this.transformPatternsBitNotBitXNor.hashCode()))) + (this.transformPatternsNullOnNullInput == null ? 0 : this.transformPatternsNullOnNullInput.hashCode()))) + (this.transformPatternsIdempotentFunctionRepetition == null ? 0 : this.transformPatternsIdempotentFunctionRepetition.hashCode()))) + (this.transformPatternsArithmeticComparisons == null ? 0 : this.transformPatternsArithmeticComparisons.hashCode()))) + (this.transformPatternsArithmeticExpressions == null ? 0 : this.transformPatternsArithmeticExpressions.hashCode()))) + (this.transformPatternsTrigonometricFunctions == null ? 0 : this.transformPatternsTrigonometricFunctions.hashCode()))) + (this.transformPatternsLogarithmicFunctions == null ? 0 : this.transformPatternsLogarithmicFunctions.hashCode()))) + (this.transformPatternsHyperbolicFunctions == null ? 0 : this.transformPatternsHyperbolicFunctions.hashCode()))) + (this.transformPatternsInverseHyperbolicFunctions == null ? 0 : this.transformPatternsInverseHyperbolicFunctions.hashCode()))) + (this.transformInlineBindValuesForFieldComparisons == null ? 0 : this.transformInlineBindValuesForFieldComparisons.hashCode()))) + (this.transformAnsiJoinToTableLists == null ? 0 : this.transformAnsiJoinToTableLists.hashCode()))) + (this.transformInConditionSubqueryWithLimitToDerivedTable == null ? 0 : this.transformInConditionSubqueryWithLimitToDerivedTable.hashCode()))) + (this.transformQualify == null ? 0 : this.transformQualify.hashCode()))) + (this.transformTableListsToAnsiJoin == null ? 0 : this.transformTableListsToAnsiJoin.hashCode()))) + (this.transformRownum == null ? 0 : this.transformRownum.hashCode()))) + (this.transformUnneededArithmeticExpressions == null ? 0 : this.transformUnneededArithmeticExpressions.hashCode()))) + (this.transformGroupByColumnIndex == null ? 0 : this.transformGroupByColumnIndex.hashCode()))) + (this.transformInlineCTE == null ? 0 : this.transformInlineCTE.hashCode()))) + (this.backslashEscaping == null ? 0 : this.backslashEscaping.hashCode()))) + (this.paramType == null ? 0 : this.paramType.hashCode()))) + (this.paramCastMode == null ? 0 : this.paramCastMode.hashCode()))) + (this.statementType == null ? 0 : this.statementType.hashCode()))) + (this.inlineThreshold == null ? 0 : this.inlineThreshold.hashCode()))) + (this.transactionListenerStartInvocationOrder == null ? 0 : this.transactionListenerStartInvocationOrder.hashCode()))) + (this.transactionListenerEndInvocationOrder == null ? 0 : this.transactionListenerEndInvocationOrder.hashCode()))) + (this.migrationListenerStartInvocationOrder == null ? 0 : this.migrationListenerStartInvocationOrder.hashCode()))) + (this.migrationListenerEndInvocationOrder == null ? 0 : this.migrationListenerEndInvocationOrder.hashCode()))) + (this.visitListenerStartInvocationOrder == null ? 0 : this.visitListenerStartInvocationOrder.hashCode()))) + (this.visitListenerEndInvocationOrder == null ? 0 : this.visitListenerEndInvocationOrder.hashCode()))) + (this.recordListenerStartInvocationOrder == null ? 0 : this.recordListenerStartInvocationOrder.hashCode()))) + (this.recordListenerEndInvocationOrder == null ? 0 : this.recordListenerEndInvocationOrder.hashCode()))) + (this.executeListenerStartInvocationOrder == null ? 0 : this.executeListenerStartInvocationOrder.hashCode()))) + (this.executeListenerEndInvocationOrder == null ? 0 : this.executeListenerEndInvocationOrder.hashCode()))) + (this.executeLogging == null ? 0 : this.executeLogging.hashCode()))) + (this.executeLoggingSQLExceptions == null ? 0 : this.executeLoggingSQLExceptions.hashCode()))) + (this.diagnosticsLogging == null ? 0 : this.diagnosticsLogging.hashCode()))) + (this.diagnosticsConnection == null ? 0 : this.diagnosticsConnection.hashCode()))) + (this.updateRecordVersion == null ? 0 : this.updateRecordVersion.hashCode()))) + (this.updateRecordTimestamp == null ? 0 : this.updateRecordTimestamp.hashCode()))) + (this.executeWithOptimisticLocking == null ? 0 : this.executeWithOptimisticLocking.hashCode()))) + (this.executeWithOptimisticLockingExcludeUnversioned == null ? 0 : this.executeWithOptimisticLockingExcludeUnversioned.hashCode()))) + (this.attachRecords == null ? 0 : this.attachRecords.hashCode()))) + (this.insertUnchangedRecords == null ? 0 : this.insertUnchangedRecords.hashCode()))) + (this.updateUnchangedRecords == null ? 0 : this.updateUnchangedRecords.hashCode()))) + (this.updatablePrimaryKeys == null ? 0 : this.updatablePrimaryKeys.hashCode()))) + (this.reflectionCaching == null ? 0 : this.reflectionCaching.hashCode()))) + (this.cacheRecordMappers == null ? 0 : this.cacheRecordMappers.hashCode()))) + (this.cacheParsingConnection == null ? 0 : this.cacheParsingConnection.hashCode()))) + (this.cacheParsingConnectionLRUCacheSize == null ? 0 : this.cacheParsingConnectionLRUCacheSize.hashCode()))) + (this.cachePreparedStatementInLoader == null ? 0 : this.cachePreparedStatementInLoader.hashCode()))) + (this.throwExceptions == null ? 0 : this.throwExceptions.hashCode()))) + (this.fetchWarnings == null ? 0 : this.fetchWarnings.hashCode()))) + (this.fetchServerOutputSize == null ? 0 : this.fetchServerOutputSize.hashCode()))) + (this.returnIdentityOnUpdatableRecord == null ? 0 : this.returnIdentityOnUpdatableRecord.hashCode()))) + (this.returnDefaultOnUpdatableRecord == null ? 0 : this.returnDefaultOnUpdatableRecord.hashCode()))) + (this.returnComputedOnUpdatableRecord == null ? 0 : this.returnComputedOnUpdatableRecord.hashCode()))) + (this.returnAllOnUpdatableRecord == null ? 0 : this.returnAllOnUpdatableRecord.hashCode()))) + (this.returnRecordToPojo == null ? 0 : this.returnRecordToPojo.hashCode()))) + (this.mapJPAAnnotations == null ? 0 : this.mapJPAAnnotations.hashCode()))) + (this.mapRecordComponentParameterNames == null ? 0 : this.mapRecordComponentParameterNames.hashCode()))) + (this.mapConstructorPropertiesParameterNames == null ? 0 : this.mapConstructorPropertiesParameterNames.hashCode()))) + (this.mapConstructorParameterNames == null ? 0 : this.mapConstructorParameterNames.hashCode()))) + (this.mapConstructorParameterNamesInKotlin == null ? 0 : this.mapConstructorParameterNamesInKotlin.hashCode()))) + (this.queryPoolable == null ? 0 : this.queryPoolable.hashCode()))) + (this.queryTimeout == null ? 0 : this.queryTimeout.hashCode()))) + (this.maxRows == null ? 0 : this.maxRows.hashCode()))) + (this.fetchSize == null ? 0 : this.fetchSize.hashCode()))) + (this.batchSize == null ? 0 : this.batchSize.hashCode()))) + (this.debugInfoOnStackTrace == null ? 0 : this.debugInfoOnStackTrace.hashCode()))) + (this.inListPadding == null ? 0 : this.inListPadding.hashCode()))) + (this.inListPadBase == null ? 0 : this.inListPadBase.hashCode()))) + (this.delimiter == null ? 0 : this.delimiter.hashCode()))) + (this.emulateOnDuplicateKeyUpdateOnPrimaryKeyOnly == null ? 0 : this.emulateOnDuplicateKeyUpdateOnPrimaryKeyOnly.hashCode()))) + (this.emulateMultiset == null ? 0 : this.emulateMultiset.hashCode()))) + (this.emulateComputedColumns == null ? 0 : this.emulateComputedColumns.hashCode()))) + (this.executeUpdateWithoutWhere == null ? 0 : this.executeUpdateWithoutWhere.hashCode()))) + (this.executeDeleteWithoutWhere == null ? 0 : this.executeDeleteWithoutWhere.hashCode()))) + (this.interpreterDialect == null ? 0 : this.interpreterDialect.hashCode()))) + (this.interpreterNameLookupCaseSensitivity == null ? 0 : this.interpreterNameLookupCaseSensitivity.hashCode()))) + (this.interpreterLocale == null ? 0 : this.interpreterLocale.hashCode()))) + (this.interpreterDelayForeignKeyDeclarations == null ? 0 : this.interpreterDelayForeignKeyDeclarations.hashCode()))) + (this.metaIncludeSystemIndexes == null ? 0 : this.metaIncludeSystemIndexes.hashCode()))) + (this.metaIncludeSystemSequences == null ? 0 : this.metaIncludeSystemSequences.hashCode()))) + (this.migrationHistorySchema == null ? 0 : this.migrationHistorySchema.hashCode()))) + (this.migrationHistorySchemaCreateSchemaIfNotExists == null ? 0 : this.migrationHistorySchemaCreateSchemaIfNotExists.hashCode()))) + (this.migrationDefaultSchema == null ? 0 : this.migrationDefaultSchema.hashCode()))) + (this.migrationSchemataCreateSchemaIfNotExists == null ? 0 : this.migrationSchemataCreateSchemaIfNotExists.hashCode()))) + (this.migrationAllowsUndo == null ? 0 : this.migrationAllowsUndo.hashCode()))) + (this.migrationRevertUntracked == null ? 0 : this.migrationRevertUntracked.hashCode()))) + (this.migrationAutoBaseline == null ? 0 : this.migrationAutoBaseline.hashCode()))) + (this.migrationAutoVerification == null ? 0 : this.migrationAutoVerification.hashCode()))) + (this.migrationIgnoreDefaultTimestampPrecisionDiffs == null ? 0 : this.migrationIgnoreDefaultTimestampPrecisionDiffs.hashCode()))) + (this.locale == null ? 0 : this.locale.hashCode()))) + (this.parseDialect == null ? 0 : this.parseDialect.hashCode()))) + (this.parseLocale == null ? 0 : this.parseLocale.hashCode()))) + (this.parseDateFormat == null ? 0 : this.parseDateFormat.hashCode()))) + (this.parseTimestampFormat == null ? 0 : this.parseTimestampFormat.hashCode()))) + (this.parseNamedParamPrefix == null ? 0 : this.parseNamedParamPrefix.hashCode()))) + (this.parseNameCase == null ? 0 : this.parseNameCase.hashCode()))) + (this.parseWithMetaLookups == null ? 0 : this.parseWithMetaLookups.hashCode()))) + (this.parseAppendMissingTableReferences == null ? 0 : this.parseAppendMissingTableReferences.hashCode()))) + (this.parseSetCommands == null ? 0 : this.parseSetCommands.hashCode()))) + (this.parseUnsupportedSyntax == null ? 0 : this.parseUnsupportedSyntax.hashCode()))) + (this.parseUnknownFunctions == null ? 0 : this.parseUnknownFunctions.hashCode()))) + (this.parseIgnoreCommercialOnlyFeatures == null ? 0 : this.parseIgnoreCommercialOnlyFeatures.hashCode()))) + (this.parseIgnoreComments == null ? 0 : this.parseIgnoreComments.hashCode()))) + (this.parseIgnoreCommentStart == null ? 0 : this.parseIgnoreCommentStart.hashCode()))) + (this.parseIgnoreCommentStop == null ? 0 : this.parseIgnoreCommentStop.hashCode()))) + (this.parseRetainCommentsBetweenQueries == null ? 0 : this.parseRetainCommentsBetweenQueries.hashCode()))) + (this.parseMetaDefaultExpressions == null ? 0 : this.parseMetaDefaultExpressions.hashCode()))) + (this.parseMetaViewSources == null ? 0 : this.parseMetaViewSources.hashCode()))) + (this.readonlyTableRecordInsert == null ? 0 : this.readonlyTableRecordInsert.hashCode()))) + (this.readonlyUpdatableRecordUpdate == null ? 0 : this.readonlyUpdatableRecordUpdate.hashCode()))) + (this.readonlyInsert == null ? 0 : this.readonlyInsert.hashCode()))) + (this.readonlyUpdate == null ? 0 : this.readonlyUpdate.hashCode()))) + (this.applyWorkaroundFor7962 == null ? 0 : this.applyWorkaroundFor7962.hashCode()))) + (this.warnOnStaticTypeRegistryAccess == null ? 0 : this.warnOnStaticTypeRegistryAccess.hashCode()))) + (this.interpreterSearchPath == null ? 0 : this.interpreterSearchPath.hashCode()))) + (this.migrationSchemata == null ? 0 : this.migrationSchemata.hashCode()))) + (this.parseSearchPath == null ? 0 : this.parseSearchPath.hashCode());
    }
}
