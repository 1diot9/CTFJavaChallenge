package org.h2.command;

import cn.hutool.core.text.StrPool;
import com.fasterxml.jackson.core.JsonFactory;
import java.nio.charset.Charset;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.TreeSet;
import org.h2.api.ErrorCode;
import org.h2.api.IntervalQualifier;
import org.h2.command.Token;
import org.h2.command.ddl.AlterDomainAddConstraint;
import org.h2.command.ddl.AlterDomainDropConstraint;
import org.h2.command.ddl.AlterDomainExpressions;
import org.h2.command.ddl.AlterDomainRename;
import org.h2.command.ddl.AlterDomainRenameConstraint;
import org.h2.command.ddl.AlterIndexRename;
import org.h2.command.ddl.AlterSchemaRename;
import org.h2.command.ddl.AlterSequence;
import org.h2.command.ddl.AlterTableAddConstraint;
import org.h2.command.ddl.AlterTableAlterColumn;
import org.h2.command.ddl.AlterTableDropConstraint;
import org.h2.command.ddl.AlterTableRename;
import org.h2.command.ddl.AlterTableRenameColumn;
import org.h2.command.ddl.AlterTableRenameConstraint;
import org.h2.command.ddl.AlterUser;
import org.h2.command.ddl.AlterView;
import org.h2.command.ddl.Analyze;
import org.h2.command.ddl.CommandWithColumns;
import org.h2.command.ddl.CreateAggregate;
import org.h2.command.ddl.CreateConstant;
import org.h2.command.ddl.CreateDomain;
import org.h2.command.ddl.CreateFunctionAlias;
import org.h2.command.ddl.CreateIndex;
import org.h2.command.ddl.CreateLinkedTable;
import org.h2.command.ddl.CreateMaterializedView;
import org.h2.command.ddl.CreateRole;
import org.h2.command.ddl.CreateSchema;
import org.h2.command.ddl.CreateSequence;
import org.h2.command.ddl.CreateSynonym;
import org.h2.command.ddl.CreateUser;
import org.h2.command.ddl.CreateView;
import org.h2.command.ddl.DeallocateProcedure;
import org.h2.command.ddl.DefineCommand;
import org.h2.command.ddl.DropAggregate;
import org.h2.command.ddl.DropConstant;
import org.h2.command.ddl.DropDatabase;
import org.h2.command.ddl.DropDomain;
import org.h2.command.ddl.DropFunctionAlias;
import org.h2.command.ddl.DropIndex;
import org.h2.command.ddl.DropMaterializedView;
import org.h2.command.ddl.DropRole;
import org.h2.command.ddl.DropSchema;
import org.h2.command.ddl.DropSequence;
import org.h2.command.ddl.DropSynonym;
import org.h2.command.ddl.DropTable;
import org.h2.command.ddl.DropTrigger;
import org.h2.command.ddl.DropUser;
import org.h2.command.ddl.DropView;
import org.h2.command.ddl.GrantRevoke;
import org.h2.command.ddl.PrepareProcedure;
import org.h2.command.ddl.RefreshMaterializedView;
import org.h2.command.ddl.SequenceOptions;
import org.h2.command.ddl.SetComment;
import org.h2.command.ddl.TruncateTable;
import org.h2.command.dml.AlterTableSet;
import org.h2.command.dml.BackupCommand;
import org.h2.command.dml.Call;
import org.h2.command.dml.DataChangeStatement;
import org.h2.command.dml.Delete;
import org.h2.command.dml.ExecuteProcedure;
import org.h2.command.dml.Explain;
import org.h2.command.dml.Help;
import org.h2.command.dml.Insert;
import org.h2.command.dml.Merge;
import org.h2.command.dml.MergeUsing;
import org.h2.command.dml.NoOperation;
import org.h2.command.dml.RunScriptCommand;
import org.h2.command.dml.ScriptCommand;
import org.h2.command.dml.Set;
import org.h2.command.dml.SetClauseList;
import org.h2.command.dml.SetSessionCharacteristics;
import org.h2.command.dml.TransactionCommand;
import org.h2.command.dml.Update;
import org.h2.command.query.Query;
import org.h2.command.query.QueryOrderBy;
import org.h2.command.query.Select;
import org.h2.command.query.SelectUnion;
import org.h2.command.query.TableValueConstructor;
import org.h2.constraint.ConstraintActionType;
import org.h2.engine.Constants;
import org.h2.engine.Database;
import org.h2.engine.IsolationLevel;
import org.h2.engine.Mode;
import org.h2.engine.NullsDistinct;
import org.h2.engine.Procedure;
import org.h2.engine.SessionLocal;
import org.h2.expression.Alias;
import org.h2.expression.ArrayElementReference;
import org.h2.expression.BinaryOperation;
import org.h2.expression.ConcatenationOperation;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionWithFlags;
import org.h2.expression.FieldReference;
import org.h2.expression.Format;
import org.h2.expression.Parameter;
import org.h2.expression.SearchedCase;
import org.h2.expression.SequenceValue;
import org.h2.expression.SimpleCase;
import org.h2.expression.Subquery;
import org.h2.expression.TimeZoneOperation;
import org.h2.expression.TypedValueExpression;
import org.h2.expression.ValueExpression;
import org.h2.expression.Wildcard;
import org.h2.expression.aggregate.AbstractAggregate;
import org.h2.expression.aggregate.Aggregate;
import org.h2.expression.aggregate.AggregateType;
import org.h2.expression.aggregate.ListaggArguments;
import org.h2.expression.analysis.DataAnalysisOperation;
import org.h2.expression.analysis.Window;
import org.h2.expression.analysis.WindowFrame;
import org.h2.expression.analysis.WindowFrameBound;
import org.h2.expression.analysis.WindowFrameBoundType;
import org.h2.expression.analysis.WindowFrameExclusion;
import org.h2.expression.analysis.WindowFrameUnits;
import org.h2.expression.analysis.WindowFunction;
import org.h2.expression.analysis.WindowFunctionType;
import org.h2.expression.condition.BetweenPredicate;
import org.h2.expression.condition.BooleanTest;
import org.h2.expression.condition.CompareLike;
import org.h2.expression.condition.Comparison;
import org.h2.expression.condition.ConditionAndOr;
import org.h2.expression.condition.ConditionAndOrN;
import org.h2.expression.condition.ConditionIn;
import org.h2.expression.condition.ConditionInArray;
import org.h2.expression.condition.ConditionInQuery;
import org.h2.expression.condition.ConditionLocalAndGlobal;
import org.h2.expression.condition.ConditionNot;
import org.h2.expression.condition.ExistsPredicate;
import org.h2.expression.condition.IsJsonPredicate;
import org.h2.expression.condition.NullPredicate;
import org.h2.expression.condition.TypePredicate;
import org.h2.expression.condition.UniquePredicate;
import org.h2.expression.function.BuiltinFunctions;
import org.h2.expression.function.CardinalityExpression;
import org.h2.expression.function.CastSpecification;
import org.h2.expression.function.CoalesceFunction;
import org.h2.expression.function.CompatibilitySequenceValueFunction;
import org.h2.expression.function.ConcatFunction;
import org.h2.expression.function.CurrentDateTimeValueFunction;
import org.h2.expression.function.CurrentGeneralValueSpecification;
import org.h2.expression.function.DateTimeFormatFunction;
import org.h2.expression.function.DateTimeFunction;
import org.h2.expression.function.JavaFunction;
import org.h2.expression.function.MathFunction;
import org.h2.expression.function.SetFunction;
import org.h2.expression.function.StringFunction;
import org.h2.expression.function.StringFunction1;
import org.h2.expression.function.SubstringFunction;
import org.h2.expression.function.TrimFunction;
import org.h2.expression.function.table.ArrayTableFunction;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.mode.FunctionsPostgreSQL;
import org.h2.mode.ModeFunction;
import org.h2.mode.OnDuplicateKeyValues;
import org.h2.mode.Regclass;
import org.h2.schema.Domain;
import org.h2.schema.FunctionAlias;
import org.h2.schema.Schema;
import org.h2.schema.Sequence;
import org.h2.schema.UserDefinedFunction;
import org.h2.table.Column;
import org.h2.table.DualTable;
import org.h2.table.FunctionTable;
import org.h2.table.IndexColumn;
import org.h2.table.IndexHints;
import org.h2.table.MaterializedView;
import org.h2.table.QueryExpressionTable;
import org.h2.table.RangeTable;
import org.h2.table.Table;
import org.h2.table.TableFilter;
import org.h2.table.TableView;
import org.h2.util.IntervalUtils;
import org.h2.util.ParserUtil;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.util.geometry.EWKTUtils;
import org.h2.util.json.JSONItemType;
import org.h2.value.CompareMode;
import org.h2.value.DataType;
import org.h2.value.ExtTypeInfoEnum;
import org.h2.value.ExtTypeInfoGeometry;
import org.h2.value.ExtTypeInfoNumeric;
import org.h2.value.ExtTypeInfoRow;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueDate;
import org.h2.value.ValueGeometry;
import org.h2.value.ValueInteger;
import org.h2.value.ValueInterval;
import org.h2.value.ValueJson;
import org.h2.value.ValueTime;
import org.h2.value.ValueTimeTimeZone;
import org.h2.value.ValueTimestamp;
import org.h2.value.ValueTimestampTimeZone;
import org.h2.value.ValueUuid;
import org.h2.value.ValueVarchar;
import org.springframework.asm.Opcodes;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.web.server.session.HeaderWebSessionIdResolver;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/Parser.class */
public final class Parser extends ParserBase {
    private static final String WITH_STATEMENT_SUPPORTS_LIMITED_SUB_STATEMENTS = "WITH statement supports only SELECT, TABLE, VALUES, CREATE TABLE, INSERT, UPDATE, MERGE or DELETE statements";
    private CreateView createView;
    private Prepared currentPrepared;
    private Select currentSelect;
    private List<TableView> cteCleanups;
    private String schemaName;
    private boolean rightsChecked;
    private boolean recompileAlways;
    private int orderInFrom;
    private boolean parseDomainConstraint;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/Parser$NullConstraintType.class */
    public enum NullConstraintType {
        NULL_IS_ALLOWED,
        NULL_IS_NOT_ALLOWED,
        NO_NULL_CONSTRAINT_FOUND
    }

    public Parser(SessionLocal sessionLocal) {
        super(sessionLocal);
    }

    public Prepared prepare(String str) {
        Prepared parse = parse(str, null);
        parse.prepare();
        if (this.currentTokenType != 93) {
            throw getSyntaxError();
        }
        return parse;
    }

    public Query prepareQueryExpression(String str) {
        Query query = (Query) parse(str, null);
        query.prepareExpressions();
        if (this.currentTokenType != 93) {
            throw getSyntaxError();
        }
        return query;
    }

    public Command prepareCommand(String str) {
        try {
            Prepared parse = parse(str, null);
            if (this.currentTokenType != 115 && this.currentTokenType != 93) {
                addExpected(115);
                throw getSyntaxError();
            }
            try {
                parse.prepare();
                int start = this.token.start();
                if (start < str.length()) {
                    str = str.substring(0, start);
                }
                CommandContainer commandContainer = new CommandContainer(this.session, str, parse);
                while (this.currentTokenType == 115) {
                    read();
                }
                if (this.currentTokenType != 93) {
                    int start2 = this.token.start();
                    return prepareCommandList(commandContainer, parse, str, this.sqlCommand.substring(start2), getRemainingTokens(start2));
                }
                return commandContainer;
            } catch (Throwable th) {
                CommandContainer.clearCTE(this.session, parse);
                throw th;
            }
        } catch (DbException e) {
            throw e.addSQL(this.sqlCommand);
        }
    }

    private CommandList prepareCommandList(CommandContainer commandContainer, Prepared prepared, String str, String str2, ArrayList<Token> arrayList) {
        try {
            ArrayList newSmallArrayList = Utils.newSmallArrayList();
            while (!(prepared instanceof DefineCommand)) {
                try {
                    prepared = parse(str2, arrayList);
                    newSmallArrayList.add(prepared);
                    if (this.currentTokenType != 115 && this.currentTokenType != 93) {
                        addExpected(115);
                        throw getSyntaxError();
                    }
                    while (this.currentTokenType == 115) {
                        read();
                    }
                    if (this.currentTokenType != 93) {
                        int start = this.token.start();
                        str2 = this.sqlCommand.substring(start);
                        arrayList = getRemainingTokens(start);
                    } else {
                        return new CommandList(this.session, str, commandContainer, newSmallArrayList, this.parameters, null);
                    }
                } catch (DbException e) {
                    if (e.getErrorCode() == 90123) {
                        throw e;
                    }
                    return new CommandList(this.session, str, commandContainer, newSmallArrayList, this.parameters, str2);
                }
            }
            return new CommandList(this.session, str, commandContainer, newSmallArrayList, this.parameters, str2);
        } catch (Throwable th) {
            commandContainer.clearCTE();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Prepared parse(String str, ArrayList<Token> arrayList) {
        Prepared parse;
        initialize(str, arrayList, false);
        try {
            parse = parse(false);
        } catch (DbException e) {
            if (e.getErrorCode() == 42000) {
                resetTokenIndex();
                parse = parse(true);
            } else {
                throw e.addSQL(str);
            }
        }
        return parse;
    }

    private Prepared parse(boolean z) {
        if (z) {
            this.expectedList = new ArrayList<>();
        } else {
            this.expectedList = null;
        }
        this.currentSelect = null;
        this.currentPrepared = null;
        this.createView = null;
        this.cteCleanups = null;
        this.recompileAlways = false;
        this.usedParameters.clear();
        read();
        try {
            Prepared parsePrepared = parsePrepared();
            parsePrepared.setCteCleanups(this.cteCleanups);
            parsePrepared.setPrepareAlways(this.recompileAlways);
            parsePrepared.setParameterList(this.parameters);
            return parsePrepared;
        } catch (Throwable th) {
            if (this.cteCleanups != null) {
                CommandContainer.clearCTE(this.session, this.cteCleanups);
            }
            throw th;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:133:0x03f3, code lost:            if (r0 != false) goto L135;     */
    /* JADX WARN: Code restructure failed: missing block: B:134:0x03f6, code lost:            r0 = ((int) readLong()) - 1;     */
    /* JADX WARN: Code restructure failed: missing block: B:135:0x0401, code lost:            if (r0 < 0) goto L159;     */
    /* JADX WARN: Code restructure failed: missing block: B:137:0x040d, code lost:            if (r0 < r5.parameters.size()) goto L141;     */
    /* JADX WARN: Code restructure failed: missing block: B:138:0x0415, code lost:            r0 = r5.parameters.get(r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:139:0x0425, code lost:            if (r0 != null) goto L145;     */
    /* JADX WARN: Code restructure failed: missing block: B:140:0x042d, code lost:            read(116);        r0.setValue(readExpression().optimize(r5.session).getValue(r5.session));     */
    /* JADX WARN: Code restructure failed: missing block: B:141:0x0458, code lost:            if (readIf(org.springframework.asm.Opcodes.LDIV) != false) goto L163;     */
    /* JADX WARN: Code restructure failed: missing block: B:143:0x045b, code lost:            read(112);        r0 = r5.parameters.iterator();     */
    /* JADX WARN: Code restructure failed: missing block: B:145:0x0471, code lost:            if (r0.hasNext() == false) goto L164;     */
    /* JADX WARN: Code restructure failed: missing block: B:146:0x0474, code lost:            r0.next().checkSet();     */
    /* JADX WARN: Code restructure failed: missing block: B:148:0x0488, code lost:            r7.setWithParamValues(true);     */
    /* JADX WARN: Code restructure failed: missing block: B:152:0x042c, code lost:            throw getSyntaxError();     */
    /* JADX WARN: Code restructure failed: missing block: B:155:0x0414, code lost:            throw getSyntaxError();     */
    /* JADX WARN: Code restructure failed: missing block: B:158:0x048e, code lost:            if (r0 != false) goto L156;     */
    /* JADX WARN: Code restructure failed: missing block: B:160:0x0495, code lost:            if (r7.getSQL() != null) goto L157;     */
    /* JADX WARN: Code restructure failed: missing block: B:162:0x049f, code lost:            return r7;     */
    /* JADX WARN: Code restructure failed: missing block: B:163:0x0498, code lost:            setSQL(r7, r6);     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private org.h2.command.Prepared parsePrepared() {
        /*
            Method dump skipped, instructions count: 1184
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.Parser.parsePrepared():org.h2.command.Prepared");
    }

    private Prepared parseBackup() {
        BackupCommand backupCommand = new BackupCommand(this.session);
        read(76);
        backupCommand.setFileName(readExpression());
        return backupCommand;
    }

    private Prepared parseAnalyze() {
        Analyze analyze = new Analyze(this.session);
        if (readIf(75)) {
            analyze.setTable(readTableOrView());
        }
        if (readIf("SAMPLE_SIZE")) {
            analyze.setTop(readNonNegativeInt());
        }
        return analyze;
    }

    private TransactionCommand parseBegin() {
        if (!readIf("WORK")) {
            readIf("TRANSACTION");
        }
        return new TransactionCommand(this.session, 83);
    }

    private TransactionCommand parseCommit() {
        if (readIf("TRANSACTION")) {
            TransactionCommand transactionCommand = new TransactionCommand(this.session, 78);
            transactionCommand.setTransactionName(readIdentifier());
            return transactionCommand;
        }
        TransactionCommand transactionCommand2 = new TransactionCommand(this.session, 71);
        readIf("WORK");
        return transactionCommand2;
    }

    private TransactionCommand parseShutdown() {
        int i = 80;
        if (readIf("IMMEDIATELY")) {
            i = 81;
        } else if (readIf("COMPACT")) {
            i = 82;
        } else if (readIf("DEFRAG")) {
            i = 84;
        } else {
            readIf("SCRIPT");
        }
        return new TransactionCommand(this.session, i);
    }

    private TransactionCommand parseRollback() {
        TransactionCommand transactionCommand;
        if (readIf("TRANSACTION")) {
            TransactionCommand transactionCommand2 = new TransactionCommand(this.session, 79);
            transactionCommand2.setTransactionName(readIdentifier());
            return transactionCommand2;
        }
        readIf("WORK");
        if (readIf(76, "SAVEPOINT")) {
            transactionCommand = new TransactionCommand(this.session, 75);
            transactionCommand.setSavepointName(readIdentifier());
        } else {
            transactionCommand = new TransactionCommand(this.session, 72);
        }
        return transactionCommand;
    }

    private Prepared parsePrepare() {
        if (readIf("COMMIT")) {
            TransactionCommand transactionCommand = new TransactionCommand(this.session, 77);
            transactionCommand.setTransactionName(readIdentifier());
            return transactionCommand;
        }
        return parsePrepareProcedure();
    }

    private Prepared parsePrepareProcedure() {
        if (this.database.getMode().getEnum() == Mode.ModeEnum.MSSQLServer) {
            throw getSyntaxError();
        }
        String readIdentifier = readIdentifier();
        if (readIf(105)) {
            ArrayList newSmallArrayList = Utils.newSmallArrayList();
            int i = 0;
            while (true) {
                newSmallArrayList.add(parseColumnForTable("C" + i, true));
                if (!readIfMore()) {
                    break;
                }
                i++;
            }
        }
        read(7);
        Prepared parsePrepared = parsePrepared();
        PrepareProcedure prepareProcedure = new PrepareProcedure(this.session);
        prepareProcedure.setProcedureName(readIdentifier);
        prepareProcedure.setPrepared(parsePrepared);
        return prepareProcedure;
    }

    private TransactionCommand parseSavepoint() {
        TransactionCommand transactionCommand = new TransactionCommand(this.session, 74);
        transactionCommand.setSavepointName(readIdentifier());
        return transactionCommand;
    }

    private Prepared parseReleaseSavepoint() {
        NoOperation noOperation = new NoOperation(this.session);
        readIf("SAVEPOINT");
        readIdentifier();
        return noOperation;
    }

    private Schema findSchema(String str) {
        if (str == null) {
            return null;
        }
        Schema findSchema = this.database.findSchema(str);
        if (findSchema == null && equalsToken(HeaderWebSessionIdResolver.DEFAULT_HEADER_NAME, str)) {
            findSchema = this.database.getSchema(this.session.getCurrentSchemaName());
        }
        return findSchema;
    }

    private Schema getSchema(String str) {
        if (str == null) {
            return null;
        }
        Schema findSchema = findSchema(str);
        if (findSchema == null) {
            throw DbException.get(ErrorCode.SCHEMA_NOT_FOUND_1, str);
        }
        return findSchema;
    }

    private Schema getSchema() {
        return getSchema(this.schemaName);
    }

    private Schema getSchemaWithDefault() {
        if (this.schemaName == null) {
            this.schemaName = this.session.getCurrentSchemaName();
        }
        return getSchema(this.schemaName);
    }

    private Column readTableColumn(TableFilter tableFilter) {
        String readIdentifier = readIdentifier();
        if (readIf(Opcodes.FDIV)) {
            readIdentifier = readTableColumn(tableFilter, readIdentifier);
        }
        return tableFilter.getTable().getColumn(readIdentifier);
    }

    private String readTableColumn(TableFilter tableFilter, String str) {
        String readIdentifier = readIdentifier();
        if (readIf(Opcodes.FDIV)) {
            String str2 = str;
            str = readIdentifier;
            readIdentifier = readIdentifier();
            if (readIf(Opcodes.FDIV)) {
                checkDatabaseName(str2);
                str2 = str;
                str = readIdentifier;
                readIdentifier = readIdentifier();
            }
            if (!equalsToken(str2, tableFilter.getTable().getSchema().getName())) {
                throw DbException.get(ErrorCode.SCHEMA_NOT_FOUND_1, str2);
            }
        }
        if (!equalsToken(str, tableFilter.getTableAlias())) {
            throw DbException.get(ErrorCode.TABLE_OR_VIEW_NOT_FOUND_1, str);
        }
        return readIdentifier;
    }

    private DataChangeStatement parseUpdate(int i) {
        Update update = new Update(this.session);
        this.currentPrepared = update;
        Expression expression = null;
        if (this.database.getMode().topInDML && readIfCompat("TOP")) {
            read(105);
            expression = readTerm().optimize(this.session);
            read(106);
        }
        TableFilter readSimpleTableFilter = readSimpleTableFilter();
        update.setTableFilter(readSimpleTableFilter);
        int i2 = this.tokenIndex;
        if (this.database.getMode().discardWithTableHints) {
            discardWithTableHints();
        }
        update.setSetClauseList(readUpdateSetClause(readSimpleTableFilter));
        if (this.database.getMode().allowUsingFromClauseInUpdateStatement && readIfCompat(35)) {
            setTokenIndex(i2);
            return parseUpdateFrom(readSimpleTableFilter, i);
        }
        if (readIf(87)) {
            update.setCondition(readExpression());
        }
        if (expression == null) {
            readIfOrderBy();
            expression = readFetchOrLimit();
        }
        update.setFetch(expression);
        setSQL(update, i);
        return update;
    }

    private MergeUsing parseUpdateFrom(TableFilter tableFilter, int i) {
        MergeUsing mergeUsing = new MergeUsing(this.session, tableFilter);
        this.currentPrepared = mergeUsing;
        SetClauseList readUpdateSetClause = readUpdateSetClause(tableFilter);
        read(35);
        mergeUsing.setSourceTableFilter(readTableReference());
        mergeUsing.setOnCondition(readIf(87) ? readExpression() : ValueExpression.TRUE);
        mergeUsing.getClass();
        MergeUsing.WhenMatchedThenUpdate whenMatchedThenUpdate = new MergeUsing.WhenMatchedThenUpdate();
        whenMatchedThenUpdate.setSetClauseList(readUpdateSetClause);
        mergeUsing.addWhen(whenMatchedThenUpdate);
        setSQL(mergeUsing, i);
        return mergeUsing;
    }

    private SetClauseList readUpdateSetClause(TableFilter tableFilter) {
        read(71);
        SetClauseList setClauseList = new SetClauseList(tableFilter.getTable());
        do {
            if (readIf(105)) {
                ArrayList<Column> newSmallArrayList = Utils.newSmallArrayList();
                ArrayList<Expression[]> newSmallArrayList2 = Utils.newSmallArrayList();
                do {
                    newSmallArrayList.add(readTableColumn(tableFilter));
                    newSmallArrayList2.add(readUpdateSetClauseArrayIndexes());
                } while (readIfMore());
                read(95);
                setClauseList.addMultiple(newSmallArrayList, newSmallArrayList2, readExpression());
            } else {
                Column readTableColumn = readTableColumn(tableFilter);
                Expression[] readUpdateSetClauseArrayIndexes = readUpdateSetClauseArrayIndexes();
                read(95);
                setClauseList.addSingle(readTableColumn, readUpdateSetClauseArrayIndexes, readUpdateSetClauseArrayIndexes == null ? readExpressionOrDefault() : readExpression());
            }
        } while (readIf(Opcodes.LDIV));
        return setClauseList;
    }

    private Expression[] readUpdateSetClauseArrayIndexes() {
        if (readIf(Opcodes.LNEG)) {
            ArrayList newSmallArrayList = Utils.newSmallArrayList();
            do {
                newSmallArrayList.add(readExpression());
                read(Opcodes.FNEG);
            } while (readIf(Opcodes.LNEG));
            return (Expression[]) newSmallArrayList.toArray(new Expression[0]);
        }
        return null;
    }

    private TableFilter readSimpleTableFilter() {
        return new TableFilter(this.session, readTableOrView(), readFromAlias(null), this.rightsChecked, this.currentSelect, 0, null);
    }

    private Delete parseDelete(int i) {
        Delete delete = new Delete(this.session);
        Expression expression = null;
        if (this.database.getMode().topInDML && readIfCompat("TOP")) {
            expression = readTerm().optimize(this.session);
        }
        this.currentPrepared = delete;
        if (!readIf(35) && this.database.getMode().deleteIdentifierFrom) {
            readIdentifierWithSchema();
            read(35);
        }
        delete.setTableFilter(readSimpleTableFilter());
        if (readIf(87)) {
            delete.setCondition(readExpression());
        }
        if (expression == null) {
            expression = readFetchOrLimit();
        }
        delete.setFetch(expression);
        setSQL(delete, i);
        return delete;
    }

    private Expression readFetchOrLimit() {
        Expression expression = null;
        if (readIf(32)) {
            if (!readIf("FIRST")) {
                read("NEXT");
            }
            if (readIf(66) || readIf("ROWS")) {
                expression = ValueExpression.get(ValueInteger.get(1));
            } else {
                expression = readExpression().optimize(this.session);
                if (!readIf(66)) {
                    read("ROWS");
                }
            }
            read("ONLY");
        } else if (this.database.getMode().limit && readIfCompat(50)) {
            expression = readTerm().optimize(this.session);
        }
        return expression;
    }

    private IndexColumn[] parseIndexColumnList() {
        ArrayList newSmallArrayList = Utils.newSmallArrayList();
        do {
            newSmallArrayList.add(new IndexColumn(readIdentifier(), parseSortType()));
        } while (readIfMore());
        return (IndexColumn[]) newSmallArrayList.toArray(new IndexColumn[0]);
    }

    private int parseSortType() {
        int i = (readIf("ASC") || !readIf("DESC")) ? 0 : 1;
        if (readIf("NULLS")) {
            if (readIf("FIRST")) {
                i |= 2;
            } else {
                read("LAST");
                i |= 4;
            }
        }
        return i;
    }

    private String[] parseColumnList() {
        ArrayList newSmallArrayList = Utils.newSmallArrayList();
        do {
            newSmallArrayList.add(readIdentifier());
        } while (readIfMore());
        return (String[]) newSmallArrayList.toArray(new String[0]);
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x0030, code lost:            throw org.h2.message.DbException.get(org.h2.api.ErrorCode.DUPLICATE_COLUMN_NAME_1, r0.getTraceSQL());     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x004a, code lost:            return (org.h2.table.Column[]) r0.toArray(new org.h2.table.Column[0]);     */
    /* JADX WARN: Code restructure failed: missing block: B:2:0x0012, code lost:            if (readIf(106) == false) goto L4;     */
    /* JADX WARN: Code restructure failed: missing block: B:3:0x0015, code lost:            r0 = parseColumn(r4);     */
    /* JADX WARN: Code restructure failed: missing block: B:4:0x0022, code lost:            if (r0.add(r0) != false) goto L8;     */
    /* JADX WARN: Code restructure failed: missing block: B:5:0x0031, code lost:            r0.add(r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x003c, code lost:            if (readIfMore() != false) goto L14;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private org.h2.table.Column[] parseColumnList(org.h2.table.Table r4) {
        /*
            r3 = this;
            java.util.ArrayList r0 = org.h2.util.Utils.newSmallArrayList()
            r5 = r0
            java.util.HashSet r0 = new java.util.HashSet
            r1 = r0
            r1.<init>()
            r6 = r0
            r0 = r3
            r1 = 106(0x6a, float:1.49E-43)
            boolean r0 = r0.readIf(r1)
            if (r0 != 0) goto L3f
        L15:
            r0 = r3
            r1 = r4
            org.h2.table.Column r0 = r0.parseColumn(r1)
            r7 = r0
            r0 = r6
            r1 = r7
            boolean r0 = r0.add(r1)
            if (r0 != 0) goto L31
            r0 = 42121(0xa489, float:5.9024E-41)
            r1 = r7
            java.lang.String r1 = r1.getTraceSQL()
            org.h2.message.DbException r0 = org.h2.message.DbException.get(r0, r1)
            throw r0
        L31:
            r0 = r5
            r1 = r7
            boolean r0 = r0.add(r1)
            r0 = r3
            boolean r0 = r0.readIfMore()
            if (r0 != 0) goto L15
        L3f:
            r0 = r5
            r1 = 0
            org.h2.table.Column[] r1 = new org.h2.table.Column[r1]
            java.lang.Object[] r0 = r0.toArray(r1)
            org.h2.table.Column[] r0 = (org.h2.table.Column[]) r0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.Parser.parseColumnList(org.h2.table.Table):org.h2.table.Column[]");
    }

    private Column parseColumn(Table table) {
        if (this.currentTokenType == 91) {
            read();
            return table.getRowIdColumn();
        }
        return table.getColumn(readIdentifier());
    }

    private Prepared parseHelp() {
        HashSet hashSet = new HashSet();
        while (this.currentTokenType != 93) {
            hashSet.add(StringUtils.toUpperEnglish(readIdentifierOrKeyword()));
        }
        return new Help(this.session, (String[]) hashSet.toArray(new String[0]));
    }

    private Prepared parseShow() {
        StringBuilder sb = new StringBuilder("SELECT ");
        if (readIf("CLIENT_ENCODING")) {
            sb.append("'UNICODE' CLIENT_ENCODING");
        } else if (readIf("DEFAULT_TRANSACTION_ISOLATION")) {
            sb.append("'read committed' DEFAULT_TRANSACTION_ISOLATION");
        } else if (readIf("TRANSACTION")) {
            read("ISOLATION");
            read("LEVEL");
            sb.append("LOWER(ISOLATION_LEVEL) TRANSACTION_ISOLATION FROM INFORMATION_SCHEMA.SESSIONS WHERE SESSION_ID = SESSION_ID()");
        } else if (readIf("DATESTYLE")) {
            sb.append("'ISO' DATESTYLE");
        } else if (readIf("SEARCH_PATH")) {
            String[] schemaSearchPath = this.session.getSchemaSearchPath();
            StringBuilder sb2 = new StringBuilder();
            if (schemaSearchPath != null) {
                for (int i = 0; i < schemaSearchPath.length; i++) {
                    if (i > 0) {
                        sb2.append(", ");
                    }
                    ParserUtil.quoteIdentifier(sb2, schemaSearchPath[i], 1);
                }
            }
            StringUtils.quoteStringSQL(sb, sb2.toString());
            sb.append(" SEARCH_PATH");
        } else if (readIf("SERVER_VERSION")) {
            sb.append("'8.2.23' SERVER_VERSION");
        } else if (readIf("SERVER_ENCODING")) {
            sb.append("'UTF8' SERVER_ENCODING");
        } else if (readIf("SSL")) {
            sb.append("'off' SSL");
        } else if (readIf("TABLES")) {
            String name = this.database.getMainSchema().getName();
            if (readIf(35)) {
                name = readIdentifier();
            }
            sb.append("TABLE_NAME, TABLE_SCHEMA FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA=");
            StringUtils.quoteStringSQL(sb, name).append(" ORDER BY TABLE_NAME");
        } else if (readIf("COLUMNS")) {
            read(35);
            String readIdentifierWithSchema = readIdentifierWithSchema();
            String name2 = getSchema().getName();
            if (readIf(35)) {
                name2 = readIdentifier();
            }
            sb.append("C.COLUMN_NAME FIELD, ");
            boolean isOldInformationSchema = this.session.isOldInformationSchema();
            if (isOldInformationSchema) {
                sb.append("C.COLUMN_TYPE");
            } else {
                sb.append("DATA_TYPE_SQL(");
                StringUtils.quoteStringSQL(sb, name2).append(", ");
                StringUtils.quoteStringSQL(sb, readIdentifierWithSchema).append(", 'TABLE', C.DTD_IDENTIFIER)");
            }
            sb.append(" TYPE, C.IS_NULLABLE \"NULL\", CASE (SELECT MAX(I.INDEX_TYPE_NAME) FROM INFORMATION_SCHEMA.INDEXES I ");
            if (!isOldInformationSchema) {
                sb.append("JOIN INFORMATION_SCHEMA.INDEX_COLUMNS IC ");
            }
            sb.append("WHERE I.TABLE_SCHEMA=C.TABLE_SCHEMA AND I.TABLE_NAME=C.TABLE_NAME ");
            if (isOldInformationSchema) {
                sb.append("AND I.COLUMN_NAME=C.COLUMN_NAME");
            } else {
                sb.append("AND IC.TABLE_SCHEMA=C.TABLE_SCHEMA AND IC.TABLE_NAME=C.TABLE_NAME AND IC.INDEX_SCHEMA=I.INDEX_SCHEMA AND IC.INDEX_NAME=I.INDEX_NAME AND IC.COLUMN_NAME=C.COLUMN_NAME");
            }
            sb.append(")WHEN 'PRIMARY KEY' THEN 'PRI' WHEN 'UNIQUE INDEX' THEN 'UNI' ELSE '' END `KEY`, COALESCE(COLUMN_DEFAULT, 'NULL') `DEFAULT` FROM INFORMATION_SCHEMA.COLUMNS C WHERE C.TABLE_SCHEMA=");
            StringUtils.quoteStringSQL(sb, name2).append(" AND C.TABLE_NAME=");
            StringUtils.quoteStringSQL(sb, readIdentifierWithSchema).append(" ORDER BY C.ORDINAL_POSITION");
        } else if (readIf("DATABASES") || readIf("SCHEMAS")) {
            sb.append("SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA");
        } else if (this.database.getMode().getEnum() == Mode.ModeEnum.PostgreSQL && readIf(3)) {
            sb.append("NAME, SETTING FROM PG_CATALOG.PG_SETTINGS");
        }
        boolean allowLiterals = this.session.getAllowLiterals();
        try {
            this.session.setAllowLiterals(true);
            Prepared prepare = this.session.prepare(sb.toString());
            this.session.setAllowLiterals(allowLiterals);
            return prepare;
        } catch (Throwable th) {
            this.session.setAllowLiterals(allowLiterals);
            throw th;
        }
    }

    private boolean isDerivedTable() {
        int i = this.tokenIndex;
        int i2 = 0;
        while (this.tokens.get(i).tokenType() == 105) {
            i2++;
            i++;
        }
        boolean isDirectQuery = isDirectQuery(i);
        if (isDirectQuery && i2 > 0) {
            int scanToCloseParen = scanToCloseParen(i + 1);
            if (scanToCloseParen >= 0) {
                while (true) {
                    switch (this.tokens.get(scanToCloseParen).tokenType()) {
                        case 46:
                            isDirectQuery = false;
                            break;
                        case 93:
                        case 115:
                            isDirectQuery = false;
                            break;
                        case 105:
                            scanToCloseParen = scanToCloseParen(scanToCloseParen + 1);
                            if (scanToCloseParen >= 0) {
                                break;
                            } else {
                                isDirectQuery = false;
                                break;
                            }
                        case 106:
                            i2--;
                            if (i2 != 0) {
                                scanToCloseParen++;
                                break;
                            } else {
                                break;
                            }
                        default:
                            scanToCloseParen++;
                            break;
                    }
                }
            } else {
                isDirectQuery = false;
            }
        }
        return isDirectQuery;
    }

    private boolean isQuery() {
        int i = this.tokenIndex;
        int i2 = 0;
        while (this.tokens.get(i).tokenType() == 105) {
            i2++;
            i++;
        }
        boolean isDirectQuery = isDirectQuery(i);
        if (isDirectQuery && i2 > 0) {
            int i3 = i + 1;
            while (true) {
                i3 = scanToCloseParen(i3);
                if (i3 < 0) {
                    isDirectQuery = false;
                } else {
                    switch (this.tokens.get(i3).tokenType()) {
                        case 29:
                        case 32:
                        case 43:
                        case 50:
                        case 53:
                        case 59:
                        case 62:
                        case 79:
                        case 93:
                        case 106:
                        case 115:
                            i2--;
                            if (i2 <= 0) {
                                break;
                            }
                        default:
                            isDirectQuery = false;
                            break;
                    }
                }
            }
        }
        return isDirectQuery;
    }

    private int scanToCloseParen(int i) {
        int i2 = 0;
        while (true) {
            switch (this.tokens.get(i).tokenType()) {
                case 93:
                case 115:
                    return -1;
                case 105:
                    i2++;
                    break;
                case 106:
                    i2--;
                    if (i2 >= 0) {
                        break;
                    } else {
                        return i + 1;
                    }
            }
            i++;
        }
    }

    private boolean isQueryQuick() {
        int i = this.tokenIndex;
        while (this.tokens.get(i).tokenType() == 105) {
            i++;
        }
        return isDirectQuery(i);
    }

    private boolean isDirectQuery(int i) {
        boolean z;
        switch (this.tokens.get(i).tokenType()) {
            case 69:
            case 85:
            case 89:
                z = true;
                break;
            case 75:
                z = this.tokens.get(i + 1).tokenType() != 105;
                break;
            default:
                z = false;
                break;
        }
        return z;
    }

    private Prepared parseMerge(int i) {
        read("INTO");
        TableFilter readSimpleTableFilter = readSimpleTableFilter();
        if (readIf(83)) {
            return parseMergeUsing(readSimpleTableFilter, i);
        }
        return parseMergeInto(readSimpleTableFilter, i);
    }

    private Prepared parseMergeInto(TableFilter tableFilter, int i) {
        Merge merge = new Merge(this.session, false);
        this.currentPrepared = merge;
        merge.setTable(tableFilter.getTable());
        Table table = merge.getTable();
        if (readIf(105)) {
            if (isQueryQuick()) {
                merge.setQuery(parseQuery());
                read(106);
                return merge;
            }
            merge.setColumns(parseColumnList(table));
        }
        if (readIf(47, 105)) {
            merge.setKeys(parseColumnList(table));
        }
        if (readIf(85)) {
            parseValuesForCommand(merge);
        } else {
            merge.setQuery(parseQuery());
        }
        setSQL(merge, i);
        return merge;
    }

    private MergeUsing parseMergeUsing(TableFilter tableFilter, int i) {
        MergeUsing mergeUsing = new MergeUsing(this.session, tableFilter);
        this.currentPrepared = mergeUsing;
        mergeUsing.setSourceTableFilter(readTableReference());
        read(60);
        mergeUsing.setOnCondition(readExpression());
        read(86);
        do {
            if (readIf("MATCHED")) {
                parseWhenMatched(mergeUsing);
            } else {
                parseWhenNotMatched(mergeUsing);
            }
        } while (readIf(86));
        setSQL(mergeUsing, i);
        return mergeUsing;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void parseWhenMatched(MergeUsing mergeUsing) {
        MergeUsing.WhenMatchedThenDelete whenMatchedThenDelete;
        Expression readExpression = readIf(4) ? readExpression() : null;
        read("THEN");
        if (readIf("UPDATE")) {
            mergeUsing.getClass();
            MergeUsing.WhenMatchedThenUpdate whenMatchedThenUpdate = new MergeUsing.WhenMatchedThenUpdate();
            whenMatchedThenUpdate.setSetClauseList(readUpdateSetClause(mergeUsing.getTargetTableFilter()));
            whenMatchedThenDelete = whenMatchedThenUpdate;
        } else {
            read("DELETE");
            mergeUsing.getClass();
            whenMatchedThenDelete = new MergeUsing.WhenMatchedThenDelete();
        }
        if (readExpression == null && this.database.getMode().mergeWhere && readIf(87)) {
            readExpression = readExpression();
        }
        whenMatchedThenDelete.setAndCondition(readExpression);
        mergeUsing.addWhen(whenMatchedThenDelete);
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0072, code lost:            if (readIfMore() != false) goto L17;     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0075, code lost:            r9.getClass();        r0 = new org.h2.command.dml.MergeUsing.WhenNotMatched(r9, r11, r0, (org.h2.expression.Expression[]) r0.toArray(new org.h2.expression.Expression[0]));        r0.setAndCondition(r10);        r9.addWhen(r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x009f, code lost:            return;     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x0061, code lost:            if (readIf(106) == false) goto L12;     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0064, code lost:            r0.add(readExpressionOrDefault());     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void parseWhenNotMatched(org.h2.command.dml.MergeUsing r9) {
        /*
            r8 = this;
            r0 = r8
            r1 = 57
            r0.read(r1)
            r0 = r8
            java.lang.String r1 = "MATCHED"
            r0.read(r1)
            r0 = r8
            r1 = 4
            boolean r0 = r0.readIf(r1)
            if (r0 == 0) goto L1c
            r0 = r8
            org.h2.expression.Expression r0 = r0.readExpression()
            goto L1d
        L1c:
            r0 = 0
        L1d:
            r10 = r0
            r0 = r8
            java.lang.String r1 = "THEN"
            r0.read(r1)
            r0 = r8
            java.lang.String r1 = "INSERT"
            r0.read(r1)
            r0 = r8
            r1 = 105(0x69, float:1.47E-43)
            boolean r0 = r0.readIf(r1)
            if (r0 == 0) goto L42
            r0 = r8
            r1 = r9
            org.h2.table.TableFilter r1 = r1.getTargetTableFilter()
            org.h2.table.Table r1 = r1.getTable()
            org.h2.table.Column[] r0 = r0.parseColumnList(r1)
            goto L43
        L42:
            r0 = 0
        L43:
            r11 = r0
            r0 = r8
            java.lang.Boolean r0 = r0.readIfOverriding()
            r12 = r0
            r0 = r8
            r1 = 85
            r0.read(r1)
            r0 = r8
            r1 = 105(0x69, float:1.47E-43)
            r0.read(r1)
            java.util.ArrayList r0 = org.h2.util.Utils.newSmallArrayList()
            r13 = r0
            r0 = r8
            r1 = 106(0x6a, float:1.49E-43)
            boolean r0 = r0.readIf(r1)
            if (r0 != 0) goto L75
        L64:
            r0 = r13
            r1 = r8
            org.h2.expression.Expression r1 = r1.readExpressionOrDefault()
            boolean r0 = r0.add(r1)
            r0 = r8
            boolean r0 = r0.readIfMore()
            if (r0 != 0) goto L64
        L75:
            org.h2.command.dml.MergeUsing$WhenNotMatched r0 = new org.h2.command.dml.MergeUsing$WhenNotMatched
            r1 = r0
            r2 = r9
            r3 = r2
            java.lang.Class r3 = r3.getClass()
            r3 = r11
            r4 = r12
            r5 = r13
            r6 = 0
            org.h2.expression.Expression[] r6 = new org.h2.expression.Expression[r6]
            java.lang.Object[] r5 = r5.toArray(r6)
            org.h2.expression.Expression[] r5 = (org.h2.expression.Expression[]) r5
            r1.<init>(r3, r4, r5)
            r14 = r0
            r0 = r14
            r1 = r10
            r0.setAndCondition(r1)
            r0 = r9
            r1 = r14
            r0.addWhen(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.Parser.parseWhenNotMatched(org.h2.command.dml.MergeUsing):void");
    }

    private Insert parseInsert(int i) {
        Insert insert = new Insert(this.session);
        this.currentPrepared = insert;
        Mode mode = this.database.getMode();
        if (mode.onDuplicateKeyUpdate && readIfCompat("IGNORE")) {
            insert.setIgnore(true);
        }
        read("INTO");
        Table readTableOrView = readTableOrView();
        insert.setTable(readTableOrView);
        Column[] columnArr = null;
        if (readIf(105)) {
            if (isQueryQuick()) {
                insert.setQuery(parseQuery());
                read(106);
                return insert;
            }
            columnArr = parseColumnList(readTableOrView);
            insert.setColumns(columnArr);
        }
        Boolean readIfOverriding = readIfOverriding();
        insert.setOverridingSystem(readIfOverriding);
        boolean z = false;
        if (readIf("DIRECT")) {
            z = true;
            insert.setInsertFromSelect(true);
        }
        if (readIfCompat("SORTED")) {
            z = true;
        }
        if (!z) {
            if (readIfOverriding == null && readIf(25, 85)) {
                insert.addRow(new Expression[0]);
            } else if (readIf(85)) {
                parseValuesForCommand(insert);
            } else if (readIf(71)) {
                parseInsertSet(insert, readTableOrView, columnArr);
            }
            if (!mode.onDuplicateKeyUpdate || mode.insertOnConflict || mode.isolationLevelInSelectOrInsertStatement) {
                parseInsertCompatibility(insert, readTableOrView, mode);
            }
            setSQL(insert, i);
            return insert;
        }
        insert.setQuery(parseQuery());
        if (!mode.onDuplicateKeyUpdate) {
        }
        parseInsertCompatibility(insert, readTableOrView, mode);
        setSQL(insert, i);
        return insert;
    }

    private Boolean readIfOverriding() {
        Boolean bool = null;
        if (readIf("OVERRIDING", 82, 84)) {
            bool = Boolean.FALSE;
        } else if (readIf("OVERRIDING", "SYSTEM", 84)) {
            bool = Boolean.TRUE;
        }
        return bool;
    }

    private void parseInsertSet(Insert insert, Table table, Column[] columnArr) {
        if (columnArr != null) {
            throw getSyntaxError();
        }
        ArrayList newSmallArrayList = Utils.newSmallArrayList();
        ArrayList newSmallArrayList2 = Utils.newSmallArrayList();
        do {
            newSmallArrayList.add(parseColumn(table));
            read(95);
            newSmallArrayList2.add(readExpressionOrDefault());
        } while (readIf(Opcodes.LDIV));
        insert.setColumns((Column[]) newSmallArrayList.toArray(new Column[0]));
        insert.addRow((Expression[]) newSmallArrayList2.toArray(new Expression[0]));
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x005b, code lost:            if (r8.getSchema().getName().equals(r10) != false) goto L14;     */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x0065, code lost:            r10 = readIdentifier();     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x007f, code lost:            if (r8.getName().equals(r12) != false) goto L20;     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0089, code lost:            throw org.h2.message.DbException.get(org.h2.api.ErrorCode.TABLE_OR_VIEW_NOT_FOUND_1, r12);     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0064, code lost:            throw org.h2.message.DbException.get(org.h2.api.ErrorCode.SCHEMA_NAME_MUST_MATCH);     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x006e, code lost:            r10 = r12;        r12 = r10;     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x008a, code lost:            r0 = r8.getColumn(r10);        read(95);        r7.addAssignmentForDuplicate(r0, readExpressionOrDefault());     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x00a8, code lost:            if (readIf(org.springframework.asm.Opcodes.LDIV) != false) goto L34;     */
    /* JADX WARN: Code restructure failed: missing block: B:4:0x002a, code lost:            if (readIfCompat(60, "DUPLICATE", 47, "UPDATE") != false) goto L6;     */
    /* JADX WARN: Code restructure failed: missing block: B:5:0x002d, code lost:            r10 = readIdentifier();     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x0039, code lost:            if (readIf(org.springframework.asm.Opcodes.FDIV) == false) goto L20;     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x003c, code lost:            r12 = readIdentifier();     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x004c, code lost:            if (readIf(org.springframework.asm.Opcodes.FDIV) == false) goto L15;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void parseInsertCompatibility(org.h2.command.dml.Insert r7, org.h2.table.Table r8, org.h2.engine.Mode r9) {
        /*
            Method dump skipped, instructions count: 232
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.Parser.parseInsertCompatibility(org.h2.command.dml.Insert, org.h2.table.Table, org.h2.engine.Mode):void");
    }

    private Merge parseReplace(int i) {
        Merge merge = new Merge(this.session, true);
        this.currentPrepared = merge;
        read("INTO");
        Table readTableOrView = readTableOrView();
        merge.setTable(readTableOrView);
        if (readIf(105)) {
            if (isQueryQuick()) {
                merge.setQuery(parseQuery());
                read(106);
                return merge;
            }
            merge.setColumns(parseColumnList(readTableOrView));
        }
        if (readIf(85)) {
            parseValuesForCommand(merge);
        } else {
            merge.setQuery(parseQuery());
        }
        setSQL(merge, i);
        return merge;
    }

    private RefreshMaterializedView parseRefresh(int i) {
        read("MATERIALIZED");
        read("VIEW");
        Table readTableOrView = readTableOrView(false);
        if (!(readTableOrView instanceof MaterializedView)) {
            throw DbException.get(ErrorCode.VIEW_NOT_FOUND_1, readTableOrView.getName());
        }
        RefreshMaterializedView refreshMaterializedView = new RefreshMaterializedView(this.session, getSchema());
        this.currentPrepared = refreshMaterializedView;
        refreshMaterializedView.setView((MaterializedView) readTableOrView);
        setSQL(refreshMaterializedView, i);
        return refreshMaterializedView;
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0039, code lost:            if (readIfMore() != false) goto L21;     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x0029, code lost:            if (readIf(106) == false) goto L11;     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x002c, code lost:            r0.add(readExpressionOrDefault());     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void parseValuesForCommand(org.h2.command.dml.CommandWithValues r5) {
        /*
            r4 = this;
            java.util.ArrayList r0 = org.h2.util.Utils.newSmallArrayList()
            r6 = r0
        L4:
            r0 = r6
            r0.clear()
            r0 = r4
            r1 = 66
            r2 = 105(0x69, float:1.47E-43)
            boolean r0 = r0.readIf(r1, r2)
            if (r0 == 0) goto L18
            r0 = 1
            r7 = r0
            goto L1f
        L18:
            r0 = r4
            r1 = 105(0x69, float:1.47E-43)
            boolean r0 = r0.readIf(r1)
            r7 = r0
        L1f:
            r0 = r7
            if (r0 == 0) goto L3f
            r0 = r4
            r1 = 106(0x6a, float:1.49E-43)
            boolean r0 = r0.readIf(r1)
            if (r0 != 0) goto L48
        L2c:
            r0 = r6
            r1 = r4
            org.h2.expression.Expression r1 = r1.readExpressionOrDefault()
            boolean r0 = r0.add(r1)
            r0 = r4
            boolean r0 = r0.readIfMore()
            if (r0 != 0) goto L2c
            goto L48
        L3f:
            r0 = r6
            r1 = r4
            org.h2.expression.Expression r1 = r1.readExpressionOrDefault()
            boolean r0 = r0.add(r1)
        L48:
            r0 = r5
            r1 = r6
            r2 = 0
            org.h2.expression.Expression[] r2 = new org.h2.expression.Expression[r2]
            java.lang.Object[] r1 = r1.toArray(r2)
            org.h2.expression.Expression[] r1 = (org.h2.expression.Expression[]) r1
            r0.addRow(r1)
            r0 = r4
            r1 = 109(0x6d, float:1.53E-43)
            boolean r0 = r0.readIf(r1)
            if (r0 != 0) goto L4
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.Parser.parseValuesForCommand(org.h2.command.dml.CommandWithValues):void");
    }

    private TableFilter readTablePrimary() {
        Table readDataChangeDeltaTable;
        Schema findSchema;
        String str = null;
        if (readIf(105)) {
            if (isDerivedTable()) {
                return readDerivedTableWithCorrelation();
            }
            TableFilter readTableReference = readTableReference();
            read(106);
            return readCorrelation(readTableReference);
        }
        if (readIf(85)) {
            BitSet openParametersScope = openParametersScope();
            TableValueConstructor parseValues = parseValues();
            str = this.session.getNextSystemIdentifier(this.sqlCommand);
            readDataChangeDeltaTable = parseValues.toTable(str, null, closeParametersScope(openParametersScope), this.createView != null, this.currentSelect);
        } else if (readIf(75, 105)) {
            readDataChangeDeltaTable = new FunctionTable(this.database.getMainSchema(), this.session, readTableFunction(1));
        } else {
            boolean isQuoted = this.token.isQuoted();
            String readIdentifier = readIdentifier();
            int i = this.tokenIndex;
            this.schemaName = null;
            if (readIf(Opcodes.FDIV)) {
                readIdentifier = readIdentifierWithSchema2(readIdentifier);
            } else if (!isQuoted && readIf(75, 105)) {
                readDataChangeDeltaTable = readDataChangeDeltaTable(upperName(readIdentifier), i);
            }
            if (this.schemaName == null) {
                findSchema = null;
            } else {
                findSchema = findSchema(this.schemaName);
                if (findSchema == null) {
                    if (isDualTable(readIdentifier)) {
                        readDataChangeDeltaTable = new DualTable(this.database);
                    } else {
                        throw DbException.get(ErrorCode.SCHEMA_NOT_FOUND_1, this.schemaName);
                    }
                }
            }
            boolean readIf = readIf(105);
            if (readIf && readIfCompat("INDEX")) {
                readIdentifierWithSchema(null);
                read(106);
                readIf = false;
            }
            if (readIf) {
                Schema mainSchema = this.database.getMainSchema();
                if (equalsToken(readIdentifier, RangeTable.NAME) || equalsToken(readIdentifier, RangeTable.ALIAS)) {
                    Expression readExpression = readExpression();
                    read(Opcodes.LDIV);
                    Expression readExpression2 = readExpression();
                    if (readIf(Opcodes.LDIV)) {
                        Expression readExpression3 = readExpression();
                        read(106);
                        readDataChangeDeltaTable = new RangeTable(mainSchema, readExpression, readExpression2, readExpression3);
                    } else {
                        read(106);
                        readDataChangeDeltaTable = new RangeTable(mainSchema, readExpression, readExpression2);
                    }
                } else {
                    readDataChangeDeltaTable = new FunctionTable(mainSchema, this.session, readTableFunction(readIdentifier, findSchema));
                }
            } else {
                readDataChangeDeltaTable = readTableOrView(readIdentifier, true);
            }
        }
        ArrayList<String> arrayList = null;
        IndexHints indexHints = null;
        if (readIfUseIndex()) {
            indexHints = parseIndexHints(readDataChangeDeltaTable);
        } else {
            str = readFromAlias(str);
            if (str != null) {
                arrayList = readDerivedColumnNames();
                if (readIfUseIndex()) {
                    indexHints = parseIndexHints(readDataChangeDeltaTable);
                }
            }
        }
        return buildTableFilter(readDataChangeDeltaTable, str, arrayList, indexHints);
    }

    private TableFilter readCorrelation(TableFilter tableFilter) {
        String readFromAlias = readFromAlias(null);
        if (readFromAlias != null) {
            tableFilter.setAlias(readFromAlias);
            ArrayList<String> readDerivedColumnNames = readDerivedColumnNames();
            if (readDerivedColumnNames != null) {
                tableFilter.setDerivedColumns(readDerivedColumnNames);
            }
        }
        return tableFilter;
    }

    private TableFilter readDerivedTableWithCorrelation() {
        String readFromAlias;
        Table table;
        BitSet openParametersScope = openParametersScope();
        Query parseQueryExpression = parseQueryExpression();
        ArrayList<Parameter> closeParametersScope = closeParametersScope(openParametersScope);
        read(106);
        ArrayList<String> arrayList = null;
        IndexHints indexHints = null;
        if (readIfUseIndex()) {
            readFromAlias = this.session.getNextSystemIdentifier(this.sqlCommand);
            table = parseQueryExpression.toTable(readFromAlias, null, closeParametersScope, this.createView != null, this.currentSelect);
            indexHints = parseIndexHints(table);
        } else {
            readFromAlias = readFromAlias(null);
            if (readFromAlias != null) {
                arrayList = readDerivedColumnNames();
                Column[] columnArr = null;
                if (arrayList != null) {
                    parseQueryExpression.init();
                    columnArr = (Column[]) QueryExpressionTable.createQueryColumnTemplateList((String[]) arrayList.toArray(new String[0]), parseQueryExpression, new String[1]).toArray(new Column[0]);
                }
                table = parseQueryExpression.toTable(readFromAlias, columnArr, closeParametersScope, this.createView != null, this.currentSelect);
                if (readIfUseIndex()) {
                    indexHints = parseIndexHints(table);
                }
            } else {
                readFromAlias = this.session.getNextSystemIdentifier(this.sqlCommand);
                table = parseQueryExpression.toTable(readFromAlias, null, closeParametersScope, this.createView != null, this.currentSelect);
            }
        }
        return buildTableFilter(table, readFromAlias, arrayList, indexHints);
    }

    private TableFilter buildTableFilter(Table table, String str, ArrayList<String> arrayList, IndexHints indexHints) {
        if (this.database.getMode().discardWithTableHints) {
            discardWithTableHints();
        }
        if (str == null && table.isView() && table.isTableExpression()) {
            str = table.getName();
        }
        boolean z = this.rightsChecked;
        Select select = this.currentSelect;
        int i = this.orderInFrom;
        this.orderInFrom = i + 1;
        TableFilter tableFilter = new TableFilter(this.session, table, str, z, select, i, indexHints);
        if (arrayList != null) {
            tableFilter.setDerivedColumns(arrayList);
        }
        return tableFilter;
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:13:0x006a. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:19:0x017c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private org.h2.table.Table readDataChangeDeltaTable(java.lang.String r8, int r9) {
        /*
            Method dump skipped, instructions count: 408
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.Parser.readDataChangeDeltaTable(java.lang.String, int):org.h2.table.Table");
    }

    /* JADX WARN: Code restructure failed: missing block: B:32:0x00e9, code lost:            if (readIf(106) == false) goto L33;     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x00ec, code lost:            r0.add(readExpression());     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x00fa, code lost:            if (readIfMore() != false) goto L38;     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x0111, code lost:            return new org.h2.expression.function.table.JavaTableFunction(r0, (org.h2.expression.Expression[]) r0.toArray(new org.h2.expression.Expression[0]));     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private org.h2.expression.function.table.TableFunction readTableFunction(java.lang.String r7, org.h2.schema.Schema r8) {
        /*
            Method dump skipped, instructions count: 274
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.Parser.readTableFunction(java.lang.String, org.h2.schema.Schema):org.h2.expression.function.table.TableFunction");
    }

    private boolean readIfUseIndex() {
        int i = this.tokenIndex;
        if (!readIf("USE")) {
            return false;
        }
        if (!readIf("INDEX")) {
            setTokenIndex(i);
            return false;
        }
        return true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:2:0x0014, code lost:            if (readIf(106) == false) goto L4;     */
    /* JADX WARN: Code restructure failed: missing block: B:3:0x0017, code lost:            r0.add(r4.getIndex(readIdentifierWithSchema()).getName());     */
    /* JADX WARN: Code restructure failed: missing block: B:4:0x0031, code lost:            if (readIfMore() != false) goto L9;     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x0038, code lost:            return org.h2.table.IndexHints.createUseIndexHints(r0);     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private org.h2.table.IndexHints parseIndexHints(org.h2.table.Table r4) {
        /*
            r3 = this;
            r0 = r3
            r1 = 105(0x69, float:1.47E-43)
            r0.read(r1)
            java.util.LinkedHashSet r0 = new java.util.LinkedHashSet
            r1 = r0
            r1.<init>()
            r5 = r0
            r0 = r3
            r1 = 106(0x6a, float:1.49E-43)
            boolean r0 = r0.readIf(r1)
            if (r0 != 0) goto L34
        L17:
            r0 = r3
            java.lang.String r0 = r0.readIdentifierWithSchema()
            r6 = r0
            r0 = r4
            r1 = r6
            org.h2.index.Index r0 = r0.getIndex(r1)
            r7 = r0
            r0 = r5
            r1 = r7
            java.lang.String r1 = r1.getName()
            boolean r0 = r0.add(r1)
            r0 = r3
            boolean r0 = r0.readIfMore()
            if (r0 != 0) goto L17
        L34:
            r0 = r5
            org.h2.table.IndexHints r0 = org.h2.table.IndexHints.createUseIndexHints(r0)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.Parser.parseIndexHints(org.h2.table.Table):org.h2.table.IndexHints");
    }

    private String readFromAlias(String str) {
        if (readIf(7) || isIdentifier()) {
            str = readIdentifier();
        }
        return str;
    }

    private ArrayList<String> readDerivedColumnNames() {
        if (readIf(105)) {
            ArrayList<String> arrayList = new ArrayList<>();
            do {
                arrayList.add(readIdentifier());
            } while (readIfMore());
            return arrayList;
        }
        return null;
    }

    private void discardWithTableHints() {
        if (!readIfCompat(89, 105)) {
            return;
        }
        do {
            discardTableHint();
        } while (readIfMore());
    }

    private void discardTableHint() {
        if (readIfCompat("INDEX")) {
            if (!readIf(105)) {
                read(95);
                readExpression();
                return;
            }
            do {
                readExpression();
            } while (readIfMore());
            return;
        }
        readExpression();
    }

    private Prepared parseTruncate() {
        read(75);
        Table readTableOrView = readTableOrView();
        boolean z = this.database.getMode().truncateTableRestartIdentity;
        if (readIf("CONTINUE", "IDENTITY")) {
            z = false;
        } else if (readIf("RESTART", "IDENTITY")) {
            z = true;
        }
        TruncateTable truncateTable = new TruncateTable(this.session);
        truncateTable.setTable(readTableOrView);
        truncateTable.setRestart(z);
        return truncateTable;
    }

    private boolean readIfExists(boolean z) {
        if (readIf(40, 30)) {
            z = true;
        }
        return z;
    }

    private Prepared parseComment() {
        int i;
        String readIdentifierWithSchema;
        read(60);
        boolean z = false;
        if (readIf(75) || readIf("VIEW")) {
            i = 0;
        } else if (readIf("COLUMN")) {
            z = true;
            i = 0;
        } else if (readIf("CONSTANT")) {
            i = 11;
        } else if (readIf(14)) {
            i = 5;
        } else if (readIf("ALIAS")) {
            i = 9;
        } else if (readIf("INDEX")) {
            i = 1;
        } else if (readIf("ROLE")) {
            i = 7;
        } else if (readIf("SCHEMA")) {
            i = 10;
        } else if (readIf("SEQUENCE")) {
            i = 3;
        } else if (readIf("TRIGGER")) {
            i = 4;
        } else if (readIf(82)) {
            i = 2;
        } else if (readIf("DOMAIN")) {
            i = 12;
        } else {
            throw getSyntaxError();
        }
        SetComment setComment = new SetComment(this.session);
        if (z) {
            readIdentifierWithSchema = readIdentifier();
            String str = null;
            read(Opcodes.FDIV);
            boolean z2 = this.database.getMode().allowEmptySchemaValuesAsDefaultSchema;
            String readIdentifier = (z2 && this.currentTokenType == 110) ? null : readIdentifier();
            if (readIf(Opcodes.FDIV)) {
                str = readIdentifierWithSchema;
                readIdentifierWithSchema = readIdentifier;
                readIdentifier = (z2 && this.currentTokenType == 110) ? null : readIdentifier();
                if (readIf(Opcodes.FDIV)) {
                    checkDatabaseName(str);
                    str = readIdentifierWithSchema;
                    readIdentifierWithSchema = readIdentifier;
                    readIdentifier = readIdentifier();
                }
            }
            if (readIdentifier == null || readIdentifierWithSchema == null) {
                throw DbException.getSyntaxError(this.sqlCommand, this.token.start(), "table.column");
            }
            this.schemaName = str != null ? str : this.session.getCurrentSchemaName();
            setComment.setColumn(true);
            setComment.setColumnName(readIdentifier);
        } else {
            readIdentifierWithSchema = readIdentifierWithSchema();
        }
        setComment.setSchemaName(this.schemaName);
        setComment.setObjectName(readIdentifierWithSchema);
        setComment.setObjectType(i);
        read(45);
        setComment.setCommentExpression(readExpression());
        return setComment;
    }

    private Prepared parseDrop() {
        if (readIf(75)) {
            boolean readIfExists = readIfExists(false);
            DropTable dropTable = new DropTable(this.session);
            do {
                dropTable.addTable(getSchema(), readIdentifierWithSchema());
            } while (readIf(Opcodes.LDIV));
            dropTable.setIfExists(readIfExists(readIfExists));
            if (readIf("CASCADE")) {
                dropTable.setDropAction(ConstraintActionType.CASCADE);
                readIf("CONSTRAINTS");
            } else if (readIf("RESTRICT")) {
                dropTable.setDropAction(ConstraintActionType.RESTRICT);
            } else if (readIf("IGNORE")) {
                dropTable.setDropAction(ConstraintActionType.SET_DEFAULT);
            }
            return dropTable;
        }
        if (readIf("INDEX")) {
            boolean readIfExists2 = readIfExists(false);
            String readIdentifierWithSchema = readIdentifierWithSchema();
            DropIndex dropIndex = new DropIndex(this.session, getSchema());
            dropIndex.setIndexName(readIdentifierWithSchema);
            dropIndex.setIfExists(readIfExists(readIfExists2));
            if (readIf(60)) {
                readIdentifierWithSchema();
            }
            return dropIndex;
        }
        if (readIf(82)) {
            boolean readIfExists3 = readIfExists(false);
            DropUser dropUser = new DropUser(this.session);
            dropUser.setUserName(readIdentifier());
            boolean readIfExists4 = readIfExists(readIfExists3);
            readIf("CASCADE");
            dropUser.setIfExists(readIfExists4);
            return dropUser;
        }
        if (readIf("SEQUENCE")) {
            boolean readIfExists5 = readIfExists(false);
            String readIdentifierWithSchema2 = readIdentifierWithSchema();
            DropSequence dropSequence = new DropSequence(this.session, getSchema());
            dropSequence.setSequenceName(readIdentifierWithSchema2);
            dropSequence.setIfExists(readIfExists(readIfExists5));
            return dropSequence;
        }
        if (readIf("CONSTANT")) {
            boolean readIfExists6 = readIfExists(false);
            String readIdentifierWithSchema3 = readIdentifierWithSchema();
            DropConstant dropConstant = new DropConstant(this.session, getSchema());
            dropConstant.setConstantName(readIdentifierWithSchema3);
            dropConstant.setIfExists(readIfExists(readIfExists6));
            return dropConstant;
        }
        if (readIf("TRIGGER")) {
            boolean readIfExists7 = readIfExists(false);
            String readIdentifierWithSchema4 = readIdentifierWithSchema();
            DropTrigger dropTrigger = new DropTrigger(this.session, getSchema());
            dropTrigger.setTriggerName(readIdentifierWithSchema4);
            dropTrigger.setIfExists(readIfExists(readIfExists7));
            return dropTrigger;
        }
        if (readIf("MATERIALIZED")) {
            read("VIEW");
            boolean readIfExists8 = readIfExists(false);
            String readIdentifierWithSchema5 = readIdentifierWithSchema();
            DropMaterializedView dropMaterializedView = new DropMaterializedView(this.session, getSchema());
            dropMaterializedView.setViewName(readIdentifierWithSchema5);
            dropMaterializedView.setIfExists(readIfExists(readIfExists8));
            return dropMaterializedView;
        }
        if (readIf("VIEW")) {
            boolean readIfExists9 = readIfExists(false);
            String readIdentifierWithSchema6 = readIdentifierWithSchema();
            DropView dropView = new DropView(this.session, getSchema());
            dropView.setViewName(readIdentifierWithSchema6);
            dropView.setIfExists(readIfExists(readIfExists9));
            ConstraintActionType parseCascadeOrRestrict = parseCascadeOrRestrict();
            if (parseCascadeOrRestrict != null) {
                dropView.setDropAction(parseCascadeOrRestrict);
            }
            return dropView;
        }
        if (readIf("ROLE")) {
            boolean readIfExists10 = readIfExists(false);
            DropRole dropRole = new DropRole(this.session);
            dropRole.setRoleName(readIdentifier());
            dropRole.setIfExists(readIfExists(readIfExists10));
            return dropRole;
        }
        if (readIf("ALIAS")) {
            boolean readIfExists11 = readIfExists(false);
            String readIdentifierWithSchema7 = readIdentifierWithSchema();
            DropFunctionAlias dropFunctionAlias = new DropFunctionAlias(this.session, getSchema());
            dropFunctionAlias.setAliasName(readIdentifierWithSchema7);
            dropFunctionAlias.setIfExists(readIfExists(readIfExists11));
            return dropFunctionAlias;
        }
        if (readIf("SCHEMA")) {
            boolean readIfExists12 = readIfExists(false);
            DropSchema dropSchema = new DropSchema(this.session);
            dropSchema.setSchemaName(readIdentifier());
            dropSchema.setIfExists(readIfExists(readIfExists12));
            ConstraintActionType parseCascadeOrRestrict2 = parseCascadeOrRestrict();
            if (parseCascadeOrRestrict2 != null) {
                dropSchema.setDropAction(parseCascadeOrRestrict2);
            }
            return dropSchema;
        }
        if (readIf(3, "OBJECTS")) {
            DropDatabase dropDatabase = new DropDatabase(this.session);
            dropDatabase.setDropAllObjects(true);
            if (readIf("DELETE", "FILES")) {
                dropDatabase.setDeleteFiles(true);
            }
            return dropDatabase;
        }
        if (readIf("DOMAIN") || readIf("TYPE") || readIfCompat("DATATYPE")) {
            return parseDropDomain();
        }
        if (readIf("AGGREGATE")) {
            return parseDropAggregate();
        }
        if (readIf("SYNONYM")) {
            boolean readIfExists13 = readIfExists(false);
            String readIdentifierWithSchema8 = readIdentifierWithSchema();
            DropSynonym dropSynonym = new DropSynonym(this.session, getSchema());
            dropSynonym.setSynonymName(readIdentifierWithSchema8);
            dropSynonym.setIfExists(readIfExists(readIfExists13));
            return dropSynonym;
        }
        throw getSyntaxError();
    }

    private DropDomain parseDropDomain() {
        boolean readIfExists = readIfExists(false);
        String readIdentifierWithSchema = readIdentifierWithSchema();
        DropDomain dropDomain = new DropDomain(this.session, getSchema());
        dropDomain.setDomainName(readIdentifierWithSchema);
        dropDomain.setIfDomainExists(readIfExists(readIfExists));
        ConstraintActionType parseCascadeOrRestrict = parseCascadeOrRestrict();
        if (parseCascadeOrRestrict != null) {
            dropDomain.setDropAction(parseCascadeOrRestrict);
        }
        return dropDomain;
    }

    private DropAggregate parseDropAggregate() {
        boolean readIfExists = readIfExists(false);
        String readIdentifierWithSchema = readIdentifierWithSchema();
        DropAggregate dropAggregate = new DropAggregate(this.session, getSchema());
        dropAggregate.setName(readIdentifierWithSchema);
        dropAggregate.setIfExists(readIfExists(readIfExists));
        return dropAggregate;
    }

    private TableFilter readTableReference() {
        TableFilter readTablePrimary;
        TableFilter readTablePrimary2 = readTablePrimary();
        TableFilter tableFilter = readTablePrimary2;
        while (true) {
            TableFilter tableFilter2 = readTablePrimary2;
            switch (this.currentTokenType) {
                case 15:
                    read();
                    read(46);
                    readTablePrimary = readTablePrimary();
                    addJoin(tableFilter, readTablePrimary, false, null);
                    break;
                case 36:
                    read();
                    throw getSyntaxError();
                case 42:
                    read();
                    read(46);
                    readTablePrimary = readTableReference();
                    addJoin(tableFilter, readTablePrimary, false, readJoinSpecification(tableFilter, readTablePrimary, false));
                    break;
                case 46:
                    read();
                    readTablePrimary = readTableReference();
                    addJoin(tableFilter, readTablePrimary, false, readJoinSpecification(tableFilter, readTablePrimary, false));
                    break;
                case 48:
                    read();
                    readIf("OUTER");
                    read(46);
                    readTablePrimary = readTableReference();
                    addJoin(tableFilter, readTablePrimary, true, readJoinSpecification(tableFilter, readTablePrimary, false));
                    break;
                case 56:
                    read();
                    read(46);
                    readTablePrimary = readTablePrimary();
                    Expression expression = null;
                    for (Column column : tableFilter2.getTable().getColumns()) {
                        Column column2 = readTablePrimary.getColumn(tableFilter2.getColumnName(column), true);
                        if (column2 != null) {
                            expression = addJoinColumn(expression, tableFilter2, readTablePrimary, column, column2, false);
                        }
                    }
                    addJoin(tableFilter, readTablePrimary, false, expression);
                    break;
                case 65:
                    read();
                    readIf("OUTER");
                    read(46);
                    readTablePrimary = readTableReference();
                    addJoin(readTablePrimary, tableFilter, true, readJoinSpecification(tableFilter, readTablePrimary, true));
                    tableFilter = readTablePrimary;
                    break;
                default:
                    if (this.expectedList != null) {
                        addMultipleExpected(65, 48, 42, 46, 15, 56);
                    }
                    return tableFilter;
            }
            readTablePrimary2 = readTablePrimary;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0043, code lost:            if (readIfMore() != false) goto L12;     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x001d, code lost:            if (readIf(83, 105) != false) goto L7;     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0020, code lost:            r0 = readIdentifier();        r13 = addJoinColumn(r13, r10, r11, r10.getColumn(r0, false), r11.getColumn(r0, false), r12);     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private org.h2.expression.Expression readJoinSpecification(org.h2.table.TableFilter r10, org.h2.table.TableFilter r11, boolean r12) {
        /*
            r9 = this;
            r0 = 0
            r13 = r0
            r0 = r9
            r1 = 60
            boolean r0 = r0.readIf(r1)
            if (r0 == 0) goto L15
            r0 = r9
            org.h2.expression.Expression r0 = r0.readExpression()
            r13 = r0
            goto L46
        L15:
            r0 = r9
            r1 = 83
            r2 = 105(0x69, float:1.47E-43)
            boolean r0 = r0.readIf(r1, r2)
            if (r0 == 0) goto L46
        L20:
            r0 = r9
            java.lang.String r0 = r0.readIdentifier()
            r14 = r0
            r0 = r9
            r1 = r13
            r2 = r10
            r3 = r11
            r4 = r10
            r5 = r14
            r6 = 0
            org.h2.table.Column r4 = r4.getColumn(r5, r6)
            r5 = r11
            r6 = r14
            r7 = 0
            org.h2.table.Column r5 = r5.getColumn(r6, r7)
            r6 = r12
            org.h2.expression.Expression r0 = r0.addJoinColumn(r1, r2, r3, r4, r5, r6)
            r13 = r0
            r0 = r9
            boolean r0 = r0.readIfMore()
            if (r0 != 0) goto L20
        L46:
            r0 = r13
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.Parser.readJoinSpecification(org.h2.table.TableFilter, org.h2.table.TableFilter, boolean):org.h2.expression.Expression");
    }

    private Expression addJoinColumn(Expression expression, TableFilter tableFilter, TableFilter tableFilter2, Column column, Column column2, boolean z) {
        Expression conditionAndOr;
        if (z) {
            tableFilter.addCommonJoinColumns(column, column2, tableFilter2);
            tableFilter2.addCommonJoinColumnToExclude(column2);
        } else {
            tableFilter.addCommonJoinColumns(column, column, tableFilter);
            tableFilter2.addCommonJoinColumnToExclude(column2);
        }
        Expression comparison = new Comparison(0, new ExpressionColumn(this.database, tableFilter.getSchemaName(), tableFilter.getTableAlias(), tableFilter.getColumnName(column)), new ExpressionColumn(this.database, tableFilter2.getSchemaName(), tableFilter2.getTableAlias(), tableFilter2.getColumnName(column2)), false);
        if (expression == null) {
            conditionAndOr = comparison;
        } else {
            conditionAndOr = new ConditionAndOr(0, expression, comparison);
        }
        return conditionAndOr;
    }

    private void addJoin(TableFilter tableFilter, TableFilter tableFilter2, boolean z, Expression expression) {
        if (tableFilter2.getJoin() != null) {
            TableFilter tableFilter3 = new TableFilter(this.session, new DualTable(this.database), Constants.PREFIX_JOIN + this.token.start(), this.rightsChecked, this.currentSelect, tableFilter2.getOrderInFrom(), null);
            tableFilter3.setNestedJoin(tableFilter2);
            tableFilter2 = tableFilter3;
        }
        tableFilter.addJoin(tableFilter2, z, expression);
    }

    private Prepared parseExecutePostgre() {
        ExecuteProcedure executeProcedure = new ExecuteProcedure(this.session);
        String readIdentifier = readIdentifier();
        Procedure procedure = this.session.getProcedure(readIdentifier);
        if (procedure == null) {
            throw DbException.get(ErrorCode.FUNCTION_ALIAS_NOT_FOUND_1, readIdentifier);
        }
        executeProcedure.setProcedure(procedure);
        if (readIf(105)) {
            int i = 0;
            while (true) {
                executeProcedure.setExpression(i, readExpression());
                if (!readIfMore()) {
                    break;
                }
                i++;
            }
        }
        return executeProcedure;
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x0068, code lost:            if (r6.currentTokenType != 93) goto L15;     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x006b, code lost:            r0.add(readExpression());     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x007b, code lost:            if (readIf(org.springframework.asm.Opcodes.LDIV) != false) goto L20;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private org.h2.command.Prepared parseExecuteSQLServer() {
        /*
            r6 = this;
            org.h2.command.dml.Call r0 = new org.h2.command.dml.Call
            r1 = r0
            r2 = r6
            org.h2.engine.SessionLocal r2 = r2.session
            r1.<init>(r2)
            r7 = r0
            r0 = r6
            r1 = r7
            r0.currentPrepared = r1
            r0 = 0
            r8 = r0
            r0 = r6
            java.lang.String r0 = r0.readIdentifier()
            r9 = r0
            r0 = r6
            r1 = 110(0x6e, float:1.54E-43)
            boolean r0 = r0.readIf(r1)
            if (r0 == 0) goto L3d
            r0 = r9
            r8 = r0
            r0 = r6
            java.lang.String r0 = r0.readIdentifier()
            r9 = r0
            r0 = r6
            r1 = 110(0x6e, float:1.54E-43)
            boolean r0 = r0.readIf(r1)
            if (r0 == 0) goto L3d
            r0 = r6
            r1 = r8
            r0.checkDatabaseName(r1)
            r0 = r9
            r8 = r0
            r0 = r6
            java.lang.String r0 = r0.readIdentifier()
            r9 = r0
        L3d:
            r0 = r6
            r1 = r9
            r2 = r8
            if (r2 == 0) goto L4e
            r2 = r6
            org.h2.engine.Database r2 = r2.database
            r3 = r8
            org.h2.schema.Schema r2 = r2.getSchema(r3)
            goto L4f
        L4e:
            r2 = 0
        L4f:
            org.h2.schema.FunctionAlias r0 = r0.getFunctionAliasWithinPath(r1, r2)
            r10 = r0
            java.util.ArrayList r0 = org.h2.util.Utils.newSmallArrayList()
            r12 = r0
            r0 = r6
            int r0 = r0.currentTokenType
            r1 = 115(0x73, float:1.61E-43)
            if (r0 == r1) goto L7e
            r0 = r6
            int r0 = r0.currentTokenType
            r1 = 93
            if (r0 == r1) goto L7e
        L6b:
            r0 = r12
            r1 = r6
            org.h2.expression.Expression r1 = r1.readExpression()
            boolean r0 = r0.add(r1)
            r0 = r6
            r1 = 109(0x6d, float:1.53E-43)
            boolean r0 = r0.readIf(r1)
            if (r0 != 0) goto L6b
        L7e:
            r0 = r12
            r1 = 0
            org.h2.expression.Expression[] r1 = new org.h2.expression.Expression[r1]
            java.lang.Object[] r0 = r0.toArray(r1)
            org.h2.expression.Expression[] r0 = (org.h2.expression.Expression[]) r0
            r11 = r0
            r0 = r7
            org.h2.expression.function.JavaFunction r1 = new org.h2.expression.function.JavaFunction
            r2 = r1
            r3 = r10
            r4 = r11
            r2.<init>(r3, r4)
            r0.setExpression(r1)
            r0 = r7
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.Parser.parseExecuteSQLServer():org.h2.command.Prepared");
    }

    private FunctionAlias getFunctionAliasWithinPath(String str, Schema schema) {
        UserDefinedFunction findUserDefinedFunctionWithinPath = findUserDefinedFunctionWithinPath(schema, str);
        if (findUserDefinedFunctionWithinPath instanceof FunctionAlias) {
            return (FunctionAlias) findUserDefinedFunctionWithinPath;
        }
        throw DbException.get(ErrorCode.FUNCTION_NOT_FOUND_1, str);
    }

    private DeallocateProcedure parseDeallocate() {
        readIf("PLAN");
        DeallocateProcedure deallocateProcedure = new DeallocateProcedure(this.session);
        deallocateProcedure.setProcedureName(readIdentifier());
        return deallocateProcedure;
    }

    private Explain parseExplain() {
        Explain explain = new Explain(this.session);
        if (readIf("ANALYZE")) {
            explain.setExecuteCommand(true);
        } else if (readIfCompat("PLAN")) {
            readIf(33);
        }
        switch (this.currentTokenType) {
            case 69:
            case 75:
            case 85:
            case 89:
            case 105:
                Query parseQuery = parseQuery();
                parseQuery.setNeverLazy(true);
                explain.setCommand(parseQuery);
                break;
            default:
                int i = this.tokenIndex;
                if (readIf("DELETE")) {
                    explain.setCommand(parseDelete(i));
                    break;
                } else if (readIf("UPDATE")) {
                    explain.setCommand(parseUpdate(i));
                    break;
                } else if (readIf("INSERT")) {
                    explain.setCommand(parseInsert(i));
                    break;
                } else if (readIf("MERGE")) {
                    explain.setCommand(parseMerge(i));
                    break;
                } else {
                    throw getSyntaxError();
                }
        }
        return explain;
    }

    private Query parseQuery() {
        BitSet openParametersScope = openParametersScope();
        Query parseQueryExpression = parseQueryExpression();
        parseQueryExpression.setParameterList(closeParametersScope(openParametersScope));
        parseQueryExpression.init();
        return parseQueryExpression;
    }

    private Prepared parseWithStatementOrQuery(int i) {
        BitSet openParametersScope = openParametersScope();
        Prepared parseWith = parseWith();
        parseWith.setParameterList(closeParametersScope(openParametersScope));
        if (parseWith instanceof Query) {
            ((Query) parseWith).init();
        }
        setSQL(parseWith, i);
        return parseWith;
    }

    private Query parseQueryExpression() {
        Query query;
        if (readIf(89)) {
            try {
                query = (Query) parseWith();
                query.setNeverLazy(true);
            } catch (ClassCastException e) {
                throw DbException.get(ErrorCode.SYNTAX_ERROR_1, "WITH statement supports only query in this context");
            }
        } else {
            query = parseQueryExpressionBodyAndEndOfQuery();
        }
        return query;
    }

    private Query parseQueryExpressionBodyAndEndOfQuery() {
        int i = this.tokenIndex;
        Query parseQueryExpressionBody = parseQueryExpressionBody();
        parseEndOfQuery(parseQueryExpressionBody);
        setSQL(parseQueryExpressionBody, i);
        return parseQueryExpressionBody;
    }

    private Query parseQueryExpressionBody() {
        SelectUnion.UnionType unionType;
        Query parseQueryTerm = parseQueryTerm();
        while (true) {
            Query query = parseQueryTerm;
            if (readIf(79)) {
                if (readIf(3)) {
                    unionType = SelectUnion.UnionType.UNION_ALL;
                } else {
                    readIf(26);
                    unionType = SelectUnion.UnionType.UNION;
                }
            } else if (readIf(29) || readIfCompat(53)) {
                unionType = SelectUnion.UnionType.EXCEPT;
            } else {
                return query;
            }
            parseQueryTerm = new SelectUnion(this.session, unionType, query, parseQueryTerm());
        }
    }

    private Query parseQueryTerm() {
        Query parseQueryPrimary = parseQueryPrimary();
        while (true) {
            Query query = parseQueryPrimary;
            if (readIf(43)) {
                parseQueryPrimary = new SelectUnion(this.session, SelectUnion.UnionType.INTERSECT, query, parseQueryPrimary());
            } else {
                return query;
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:73:0x01f2, code lost:            if (readIfCompat("OF") != false) goto L73;     */
    /* JADX WARN: Code restructure failed: missing block: B:74:0x01f5, code lost:            readIdentifierWithSchema();     */
    /* JADX WARN: Code restructure failed: missing block: B:75:0x0200, code lost:            if (readIf(org.springframework.asm.Opcodes.LDIV) != false) goto L108;     */
    /* JADX WARN: Code restructure failed: missing block: B:79:0x020a, code lost:            if (readIf("NOWAIT") == false) goto L78;     */
    /* JADX WARN: Code restructure failed: missing block: B:80:0x020d, code lost:            r7 = org.h2.command.query.ForUpdate.NOWAIT;     */
    /* JADX WARN: Code restructure failed: missing block: B:81:0x028c, code lost:            r6.setForUpdate(r7);     */
    /* JADX WARN: Code restructure failed: missing block: B:83:0x021b, code lost:            if (readIf("WAIT") == false) goto L91;     */
    /* JADX WARN: Code restructure failed: missing block: B:85:0x0224, code lost:            if (r5.currentTokenType != 94) goto L88;     */
    /* JADX WARN: Code restructure failed: missing block: B:86:0x0227, code lost:            r0 = r5.token.value(r5.session).getBigDecimal();     */
    /* JADX WARN: Code restructure failed: missing block: B:87:0x0237, code lost:            if (r0 == null) goto L88;     */
    /* JADX WARN: Code restructure failed: missing block: B:89:0x023e, code lost:            if (r0.signum() < 0) goto L88;     */
    /* JADX WARN: Code restructure failed: missing block: B:91:0x024c, code lost:            if (r0.compareTo(java.math.BigDecimal.valueOf(2147483647L, 3)) <= 0) goto L90;     */
    /* JADX WARN: Code restructure failed: missing block: B:92:0x0261, code lost:            read();        r7 = org.h2.command.query.ForUpdate.wait(r0.movePointRight(3).intValue());     */
    /* JADX WARN: Code restructure failed: missing block: B:94:0x0260, code lost:            throw org.h2.message.DbException.getSyntaxError(r5.sqlCommand, r5.token.start(), "timeout (0..2147483.647)");     */
    /* JADX WARN: Code restructure failed: missing block: B:97:0x027e, code lost:            if (readIf("SKIP", "LOCKED") == false) goto L94;     */
    /* JADX WARN: Code restructure failed: missing block: B:98:0x0281, code lost:            r7 = org.h2.command.query.ForUpdate.SKIP_LOCKED;     */
    /* JADX WARN: Code restructure failed: missing block: B:99:0x0288, code lost:            r7 = org.h2.command.query.ForUpdate.DEFAULT;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void parseEndOfQuery(org.h2.command.query.Query r6) {
        /*
            Method dump skipped, instructions count: 704
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.Parser.parseEndOfQuery(org.h2.command.query.Query):void");
    }

    private void parseIsolationClause() {
        if (readIfCompat(89)) {
            if (readIf("RR") || readIf("RS")) {
                if (readIf("USE", 4, "KEEP")) {
                    if (readIf("SHARE") || readIf("UPDATE") || readIf("EXCLUSIVE")) {
                    }
                    read("LOCKS");
                    return;
                }
                return;
            }
            if (readIf("CS") || !readIf("UR")) {
            }
        }
    }

    private Query parseQueryPrimary() {
        if (readIf(105)) {
            Query parseQueryExpressionBodyAndEndOfQuery = parseQueryExpressionBodyAndEndOfQuery();
            read(106);
            return parseQueryExpressionBodyAndEndOfQuery;
        }
        int i = this.tokenIndex;
        if (readIf(69)) {
            return parseSelect(i);
        }
        if (readIf(75)) {
            return parseExplicitTable(i);
        }
        read(85);
        return parseValues();
    }

    private void parseSelectFromPart(Select select) {
        do {
            TableFilter readTableReference = readTableReference();
            select.addTableFilter(readTableReference, true);
            boolean z = false;
            while (true) {
                TableFilter nestedJoin = readTableReference.getNestedJoin();
                if (nestedJoin != null) {
                    nestedJoin.visit(tableFilter -> {
                        select.addTableFilter(tableFilter, false);
                    });
                }
                TableFilter join = readTableReference.getJoin();
                if (join == null) {
                    break;
                }
                z |= join.isJoinOuter();
                if (z) {
                    select.addTableFilter(join, false);
                } else {
                    Expression joinCondition = join.getJoinCondition();
                    if (joinCondition != null) {
                        select.addCondition(joinCondition);
                    }
                    join.removeJoinCondition();
                    readTableReference.removeJoin();
                    select.addTableFilter(join, true);
                }
                readTableReference = join;
            }
        } while (readIf(Opcodes.LDIV));
    }

    private void parseSelectExpressions(Select select) {
        if (this.database.getMode().topInSelect && readIfCompat("TOP")) {
            Select select2 = this.currentSelect;
            this.currentSelect = null;
            select.setFetch(readTerm().optimize(this.session));
            if (readIf("PERCENT")) {
                select.setFetchPercent(true);
            }
            if (readIf(89, "TIES")) {
                select.setWithTies(true);
            }
            this.currentSelect = select2;
        }
        if (readIf(26)) {
            if (readIf(60, 105)) {
                ArrayList newSmallArrayList = Utils.newSmallArrayList();
                do {
                    newSmallArrayList.add(readExpression());
                } while (readIfMore());
                select.setDistinct((Expression[]) newSmallArrayList.toArray(new Expression[0]));
            } else {
                select.setDistinct();
            }
        } else {
            readIf(3);
        }
        ArrayList<Expression> newSmallArrayList2 = Utils.newSmallArrayList();
        do {
            if (readIf(108)) {
                newSmallArrayList2.add(parseWildcard(null, null));
            } else {
                switch (this.currentTokenType) {
                    case 32:
                    case 35:
                    case 37:
                    case 38:
                    case 59:
                    case 62:
                    case 64:
                    case 87:
                    case 88:
                    case 93:
                    case 106:
                    case 115:
                        break;
                    default:
                        Expression readExpression = readExpression();
                        if (readIf(7) || isIdentifier()) {
                            readExpression = new Alias(readExpression, readIdentifier(), this.database.getMode().aliasColumnName);
                        }
                        newSmallArrayList2.add(readExpression);
                        break;
                }
            }
        } while (readIf(Opcodes.LDIV));
        select.setExpressions(newSmallArrayList2);
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x00ac, code lost:            if (readIf(106) == false) goto L18;     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x00af, code lost:            r0.add(readExpression());     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x00bd, code lost:            if (readIfMore() != false) goto L70;     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x01ab, code lost:            if (readIf(88) != false) goto L53;     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x01ae, code lost:            r0 = r10.token.start();        r0 = readIdentifier();        read(7);     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x01d4, code lost:            if (r10.currentSelect.addWindow(r0, readWindowSpecification()) != false) goto L57;     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x01ea, code lost:            if (readIf(org.springframework.asm.Opcodes.LDIV) != false) goto L75;     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x01e3, code lost:            throw org.h2.message.DbException.getSyntaxError(r10.sqlCommand, r0, "unique identifier");     */
    /* JADX WARN: Code restructure failed: missing block: B:69:0x01f3, code lost:            if (readIf(64) == false) goto L62;     */
    /* JADX WARN: Code restructure failed: missing block: B:70:0x01f6, code lost:            r0.setWindowQuery();        r0.setQualify(readExpressionWithGlobalConditions());     */
    /* JADX WARN: Code restructure failed: missing block: B:71:0x0202, code lost:            r0.setParameterList(closeParametersScope(r0));        r10.currentSelect = r0;        r10.currentPrepared = r0;        setSQL(r0, r11);     */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x021e, code lost:            return r0;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private org.h2.command.query.Select parseSelect(int r11) {
        /*
            Method dump skipped, instructions count: 543
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.Parser.parseSelect(int):org.h2.command.query.Select");
    }

    private boolean isOrdinaryGroupingSet() {
        int scanToCloseParen = scanToCloseParen(this.tokenIndex + 1);
        if (scanToCloseParen < 0) {
            return false;
        }
        switch (this.tokens.get(scanToCloseParen).tokenType()) {
            case 29:
            case 32:
            case 33:
            case 38:
            case 43:
            case 50:
            case 53:
            case 59:
            case 62:
            case 64:
            case 79:
            case 88:
            case 93:
            case 106:
            case Opcodes.LDIV /* 109 */:
            case 115:
                setTokenIndex(this.tokenIndex + 1);
                return true;
            default:
                return false;
        }
    }

    private Query parseExplicitTable(int i) {
        Table readTableOrView = readTableOrView();
        Select select = new Select(this.session, this.currentSelect);
        SessionLocal sessionLocal = this.session;
        boolean z = this.rightsChecked;
        int i2 = this.orderInFrom;
        this.orderInFrom = i2 + 1;
        select.addTableFilter(new TableFilter(sessionLocal, readTableOrView, null, z, select, i2, null), true);
        select.setExplicitTable();
        setSQL(select, i);
        return select;
    }

    private void setSQL(Prepared prepared, int i) {
        ArrayList<Token> arrayList;
        String str = this.sqlCommand;
        int start = this.tokens.get(i).start();
        int start2 = this.token.start();
        while (start < start2 && str.charAt(start) <= ' ') {
            start++;
        }
        while (start < start2 && str.charAt(start2 - 1) <= ' ') {
            start2--;
        }
        String substring = str.substring(start, start2);
        if (i == 0 && this.currentTokenType == 93) {
            arrayList = this.tokens;
            if (start != 0) {
                int size = arrayList.size() - 1;
                for (int i2 = 0; i2 < size; i2++) {
                    arrayList.get(i2).subtractFromStart(start);
                }
            }
            this.token.setStart(substring.length());
            this.sqlCommand = substring;
        } else {
            arrayList = new ArrayList<>(this.tokens.subList(i, this.tokenIndex).size() + 1);
            for (int i3 = i; i3 < this.tokenIndex; i3++) {
                Token mo1443clone = this.tokens.get(i3).mo1443clone();
                mo1443clone.subtractFromStart(start);
                arrayList.add(mo1443clone);
            }
            arrayList.add(new Token.EndOfInputToken(substring.length()));
        }
        prepared.setSQL(substring, arrayList);
    }

    private Expression readExpressionOrDefault() {
        if (readIf(25)) {
            return ValueExpression.DEFAULT;
        }
        return readExpression();
    }

    private Expression readExpressionWithGlobalConditions() {
        Expression readCondition = readCondition();
        if (readIf(4)) {
            readCondition = readAnd(new ConditionAndOr(0, readCondition, readCondition()));
        } else if (readIf("_LOCAL_AND_GLOBAL_")) {
            readCondition = readAnd(new ConditionLocalAndGlobal(readCondition, readCondition()));
        }
        return readExpressionPart2(readCondition);
    }

    private Expression readExpression() {
        return readExpressionPart2(readAnd(readCondition()));
    }

    private Expression readExpressionPart2(Expression expression) {
        if (!readIf(61)) {
            return expression;
        }
        Expression readAnd = readAnd(readCondition());
        if (!readIf(61)) {
            return new ConditionAndOr(1, expression, readAnd);
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(expression);
        arrayList.add(readAnd);
        do {
            arrayList.add(readAnd(readCondition()));
        } while (readIf(61));
        return new ConditionAndOrN(1, arrayList);
    }

    private Expression readAnd(Expression expression) {
        if (!readIf(4)) {
            return expression;
        }
        Expression readCondition = readCondition();
        if (!readIf(4)) {
            return new ConditionAndOr(0, expression, readCondition);
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(expression);
        arrayList.add(readCondition);
        do {
            arrayList.add(readCondition());
        } while (readIf(4));
        return new ConditionAndOrN(0, arrayList);
    }

    private Expression readCondition() {
        Expression expression;
        switch (this.currentTokenType) {
            case 30:
                read();
                read(105);
                Query parseQuery = parseQuery();
                read(106);
                return new ExistsPredicate(parseQuery);
            case 57:
                read();
                return new ConditionNot(readCondition());
            case 80:
                read();
                NullsDistinct readNullsDistinct = readNullsDistinct(NullsDistinct.DISTINCT);
                read(105);
                Query parseQuery2 = parseQuery();
                read(106);
                return new UniquePredicate(parseQuery2, readNullsDistinct);
            default:
                if (readIf("INTERSECTS", 105)) {
                    Expression readConcat = readConcat();
                    read(Opcodes.LDIV);
                    Expression readConcat2 = readConcat();
                    read(106);
                    return new Comparison(8, readConcat, readConcat2, false);
                }
                if (this.expectedList != null) {
                    addMultipleExpected(57, 30, 80);
                    addExpected("INTERSECTS");
                }
                Expression readConcat3 = readConcat();
                while (true) {
                    expression = readConcat3;
                    int i = this.tokenIndex;
                    boolean readIf = readIf(57);
                    if (readIf && this.currentTokenType == 58) {
                        setTokenIndex(i);
                    } else {
                        readConcat3 = readConditionRightHandSide(expression, readIf, false);
                        if (readConcat3 == null) {
                        }
                    }
                }
                return expression;
        }
    }

    private Expression readConditionRightHandSide(Expression expression, boolean z, boolean z2) {
        Expression readComparison;
        if (!z && readIf(45)) {
            readComparison = readConditionIs(expression, z2);
        } else {
            switch (this.currentTokenType) {
                case 10:
                    read();
                    boolean readIf = readIf(73);
                    if (!readIf) {
                        readIf(8);
                    }
                    Expression readConcat = readConcat();
                    read(4);
                    readComparison = new BetweenPredicate(expression, z, z2, readIf, readConcat, readConcat());
                    break;
                case 41:
                    read();
                    readComparison = readInPredicate(expression, z, z2);
                    break;
                case 49:
                    read();
                    readComparison = readLikePredicate(expression, CompareLike.LikeType.LIKE, z, z2);
                    break;
                default:
                    if (readIf("ILIKE")) {
                        readComparison = readLikePredicate(expression, CompareLike.LikeType.ILIKE, z, z2);
                        break;
                    } else if (!readIf("REGEXP")) {
                        if (z) {
                            if (z2) {
                                return null;
                            }
                            if (this.expectedList != null) {
                                addMultipleExpected(10, 41, 49);
                            }
                            throw getSyntaxError();
                        }
                        int compareType = getCompareType(this.currentTokenType);
                        if (compareType < 0) {
                            return null;
                        }
                        read();
                        readComparison = readComparison(expression, compareType, z2);
                        break;
                    } else {
                        Expression readConcat2 = readConcat();
                        this.recompileAlways = true;
                        readComparison = new CompareLike(this.database, expression, z, z2, readConcat2, null, CompareLike.LikeType.REGEXP);
                        break;
                    }
            }
        }
        return readComparison;
    }

    private Expression readConditionIs(Expression expression, boolean z) {
        Expression comparison;
        boolean readIf = readIf(57);
        switch (this.currentTokenType) {
            case 26:
                read();
                read(35);
                comparison = readComparison(expression, readIf ? 6 : 7, z);
                break;
            case 31:
                read();
                comparison = new BooleanTest(expression, readIf, z, false);
                break;
            case 58:
                read();
                comparison = new NullPredicate(expression, readIf, z);
                break;
            case 77:
                read();
                comparison = new BooleanTest(expression, readIf, z, true);
                break;
            case 81:
                read();
                comparison = new BooleanTest(expression, readIf, z, null);
                break;
            default:
                if (readIf("OF")) {
                    comparison = readTypePredicate(expression, readIf, z);
                    break;
                } else if (readIf(JsonFactory.FORMAT_NAME_JSON)) {
                    comparison = readJsonPredicate(expression, readIf, z);
                    break;
                } else {
                    if (this.expectedList != null) {
                        addMultipleExpected(58, 26, 77, 31, 81);
                    }
                    if (z || !this.session.isQuirksMode()) {
                        throw getSyntaxError();
                    }
                    comparison = new Comparison(readIf ? 7 : 6, expression, readConcat(), false);
                    break;
                }
                break;
        }
        return comparison;
    }

    private TypePredicate readTypePredicate(Expression expression, boolean z, boolean z2) {
        read(105);
        ArrayList newSmallArrayList = Utils.newSmallArrayList();
        do {
            newSmallArrayList.add(parseDataType());
        } while (readIfMore());
        return new TypePredicate(expression, z, z2, (TypeInfo[]) newSmallArrayList.toArray(new TypeInfo[0]));
    }

    private Expression readInPredicate(Expression expression, boolean z, boolean z2) {
        ArrayList newSmallArrayList;
        read(105);
        if (!z2 && this.database.getMode().allowEmptyInPredicate && readIf(106)) {
            return ValueExpression.getBoolean(z);
        }
        if (isQuery()) {
            Query parseQuery = parseQuery();
            if (!readIfMore()) {
                return new ConditionInQuery(expression, z, z2, parseQuery, false, 0);
            }
            newSmallArrayList = Utils.newSmallArrayList();
            newSmallArrayList.add(new Subquery(parseQuery));
        } else {
            newSmallArrayList = Utils.newSmallArrayList();
        }
        do {
            newSmallArrayList.add(readExpression());
        } while (readIfMore());
        return new ConditionIn(expression, z, z2, newSmallArrayList);
    }

    private IsJsonPredicate readJsonPredicate(Expression expression, boolean z, boolean z2) {
        JSONItemType jSONItemType;
        if (readIf(84)) {
            jSONItemType = JSONItemType.VALUE;
        } else if (readIf(6)) {
            jSONItemType = JSONItemType.ARRAY;
        } else if (readIf("OBJECT")) {
            jSONItemType = JSONItemType.OBJECT;
        } else if (readIf("SCALAR")) {
            jSONItemType = JSONItemType.SCALAR;
        } else {
            jSONItemType = JSONItemType.VALUE;
        }
        boolean z3 = false;
        if (readIf(89, 80)) {
            readIf("KEYS");
            z3 = true;
        } else if (readIf("WITHOUT", 80)) {
            readIf("KEYS");
        }
        return new IsJsonPredicate(expression, z, z2, z3, jSONItemType);
    }

    private Expression readLikePredicate(Expression expression, CompareLike.LikeType likeType, boolean z, boolean z2) {
        Expression readConcat = readConcat();
        Expression readConcat2 = readIf("ESCAPE") ? readConcat() : null;
        this.recompileAlways = true;
        return new CompareLike(this.database, expression, z, z2, readConcat, readConcat2, likeType);
    }

    private Expression readComparison(Expression expression, int i, boolean z) {
        Expression comparison;
        int i2 = this.tokenIndex;
        if (readIf(3, 105)) {
            if (isQuery()) {
                comparison = new ConditionInQuery(expression, false, z, parseQuery(), true, i);
            } else {
                comparison = new ConditionInArray(expression, z, readExpression(), true, i);
            }
            read(106);
        } else if (readIf(5, 105)) {
            comparison = readAnyComparison(expression, i, z, i2);
        } else if (readIf(72, 105)) {
            comparison = readAnyComparison(expression, i, z, i2);
        } else {
            comparison = new Comparison(i, expression, readConcat(), z);
        }
        return comparison;
    }

    private Expression readAnyComparison(Expression expression, int i, boolean z, int i2) {
        Expression conditionInArray;
        if (isQuery()) {
            conditionInArray = new ConditionInQuery(expression, false, z, parseQuery(), false, i);
        } else {
            conditionInArray = new ConditionInArray(expression, z, readExpression(), false, i);
        }
        read(106);
        return conditionInArray;
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x008b, code lost:            addExpected(104);     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0092, code lost:            return r6;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private org.h2.expression.Expression readConcat() {
        /*
            r5 = this;
            r0 = r5
            org.h2.expression.Expression r0 = r0.readSum()
            r6 = r0
        L5:
            r0 = r5
            int r0 = r0.currentTokenType
            switch(r0) {
                case 104: goto L2c;
                case 119: goto L77;
                case 122: goto L81;
                default: goto L8b;
            }
        L2c:
            r0 = r5
            r0.read()
            r0 = r5
            org.h2.expression.Expression r0 = r0.readSum()
            r7 = r0
            r0 = r5
            r1 = 104(0x68, float:1.46E-43)
            boolean r0 = r0.readIf(r1)
            if (r0 == 0) goto L6a
            org.h2.expression.ConcatenationOperation r0 = new org.h2.expression.ConcatenationOperation
            r1 = r0
            r1.<init>()
            r8 = r0
            r0 = r8
            r1 = r6
            r0.addParameter(r1)
            r0 = r8
            r1 = r7
            r0.addParameter(r1)
        L50:
            r0 = r8
            r1 = r5
            org.h2.expression.Expression r1 = r1.readSum()
            r0.addParameter(r1)
            r0 = r5
            r1 = 104(0x68, float:1.46E-43)
            boolean r0 = r0.readIf(r1)
            if (r0 != 0) goto L50
            r0 = r8
            r0.doneWithParameters()
            r0 = r8
            r6 = r0
            goto L5
        L6a:
            org.h2.expression.ConcatenationOperation r0 = new org.h2.expression.ConcatenationOperation
            r1 = r0
            r2 = r6
            r3 = r7
            r1.<init>(r2, r3)
            r6 = r0
            goto L5
        L77:
            r0 = r5
            r1 = r6
            r2 = 0
            org.h2.expression.Expression r0 = r0.readTildeCondition(r1, r2)
            r6 = r0
            goto L5
        L81:
            r0 = r5
            r1 = r6
            r2 = 1
            org.h2.expression.Expression r0 = r0.readTildeCondition(r1, r2)
            r6 = r0
            goto L5
        L8b:
            r0 = r5
            r1 = 104(0x68, float:1.46E-43)
            r0.addExpected(r1)
            r0 = r6
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.Parser.readConcat():org.h2.expression.Expression");
    }

    private Expression readSum() {
        Expression readFactor = readFactor();
        while (true) {
            Expression expression = readFactor;
            if (readIf(103)) {
                readFactor = new BinaryOperation(BinaryOperation.OpType.PLUS, expression, readFactor());
            } else if (readIf(102)) {
                readFactor = new BinaryOperation(BinaryOperation.OpType.MINUS, expression, readFactor());
            } else {
                return expression;
            }
        }
    }

    private Expression readFactor() {
        Expression readTerm = readTerm();
        while (true) {
            Expression expression = readTerm;
            if (readIf(108)) {
                readTerm = new BinaryOperation(BinaryOperation.OpType.MULTIPLY, expression, readTerm());
            } else if (readIf(Opcodes.LREM)) {
                readTerm = new BinaryOperation(BinaryOperation.OpType.DIVIDE, expression, readTerm());
            } else if (readIf(Opcodes.FREM)) {
                readTerm = new MathFunction(expression, readTerm(), 1);
            } else {
                return expression;
            }
        }
    }

    private Expression readTildeCondition(Expression expression, boolean z) {
        read();
        if (readIf(108)) {
            expression = new CastSpecification(expression, TypeInfo.TYPE_VARCHAR_IGNORECASE);
        }
        return new CompareLike(this.database, expression, z, false, readSum(), null, CompareLike.LikeType.REGEXP);
    }

    private Expression readAggregate(AggregateType aggregateType, String str) {
        Aggregate aggregate;
        ArrayList<QueryOrderBy> arrayList;
        if (this.currentSelect == null) {
            this.expectedList = null;
            throw getSyntaxError();
        }
        switch (aggregateType) {
            case COUNT:
                if (readIf(108)) {
                    aggregate = new Aggregate(AggregateType.COUNT_ALL, new Expression[0], this.currentSelect, false);
                    break;
                } else {
                    boolean readDistinctAgg = readDistinctAgg();
                    Expression readExpression = readExpression();
                    if ((readExpression instanceof Wildcard) && !readDistinctAgg) {
                        aggregate = new Aggregate(AggregateType.COUNT_ALL, new Expression[0], this.currentSelect, false);
                        break;
                    } else {
                        aggregate = new Aggregate(AggregateType.COUNT, new Expression[]{readExpression}, this.currentSelect, readDistinctAgg);
                        break;
                    }
                }
            case COVAR_POP:
            case COVAR_SAMP:
            case CORR:
            case REGR_SLOPE:
            case REGR_INTERCEPT:
            case REGR_COUNT:
            case REGR_R2:
            case REGR_AVGX:
            case REGR_AVGY:
            case REGR_SXX:
            case REGR_SYY:
            case REGR_SXY:
                aggregate = new Aggregate(aggregateType, new Expression[]{readExpression(), readNextArgument()}, this.currentSelect, false);
                break;
            case HISTOGRAM:
                aggregate = new Aggregate(aggregateType, new Expression[]{readExpression()}, this.currentSelect, false);
                break;
            case LISTAGG:
                boolean readDistinctAgg2 = readDistinctAgg();
                Expression readExpression2 = readExpression();
                ListaggArguments listaggArguments = new ListaggArguments();
                if ("STRING_AGG".equals(str)) {
                    read(Opcodes.LDIV);
                    listaggArguments.setSeparator(readStringOrParameter());
                    arrayList = readIfOrderBy();
                } else if ("GROUP_CONCAT".equals(str)) {
                    arrayList = readIfOrderBy();
                    if (readIf("SEPARATOR")) {
                        listaggArguments.setSeparator(readStringOrParameter());
                    }
                } else {
                    if (readIf(Opcodes.LDIV)) {
                        listaggArguments.setSeparator(readStringOrParameter());
                    }
                    if (readIf(60)) {
                        read("OVERFLOW");
                        if (readIf("TRUNCATE")) {
                            listaggArguments.setOnOverflowTruncate(true);
                            if (this.currentTokenType == 94) {
                                listaggArguments.setFilter(readStringOrParameter());
                            }
                            if (!readIf(89)) {
                                read("WITHOUT");
                                listaggArguments.setWithoutCount(true);
                            }
                            read("COUNT");
                        } else {
                            read("ERROR");
                        }
                    }
                    arrayList = null;
                }
                Expression[] expressionArr = {readExpression2};
                int i = this.tokenIndex;
                read(106);
                if (arrayList == null && isToken("WITHIN")) {
                    aggregate = readWithinGroup(aggregateType, expressionArr, readDistinctAgg2, listaggArguments, false, false);
                    break;
                } else {
                    setTokenIndex(i);
                    aggregate = new Aggregate(AggregateType.LISTAGG, expressionArr, this.currentSelect, readDistinctAgg2);
                    aggregate.setExtraArguments(listaggArguments);
                    if (arrayList != null) {
                        aggregate.setOrderByList(arrayList);
                        break;
                    }
                }
                break;
            case ARRAY_AGG:
                aggregate = new Aggregate(AggregateType.ARRAY_AGG, new Expression[]{readExpression()}, this.currentSelect, readDistinctAgg());
                aggregate.setOrderByList(readIfOrderBy());
                break;
            case RANK:
            case DENSE_RANK:
            case PERCENT_RANK:
            case CUME_DIST:
                if (isToken(106)) {
                    return readWindowFunction(str);
                }
                ArrayList newSmallArrayList = Utils.newSmallArrayList();
                do {
                    newSmallArrayList.add(readExpression());
                } while (readIfMore());
                aggregate = readWithinGroup(aggregateType, (Expression[]) newSmallArrayList.toArray(new Expression[0]), false, null, true, false);
                break;
            case PERCENTILE_CONT:
            case PERCENTILE_DISC:
                Expression readExpression3 = readExpression();
                read(106);
                aggregate = readWithinGroup(aggregateType, new Expression[]{readExpression3}, false, null, false, true);
                break;
            case MODE:
                if (readIf(106)) {
                    aggregate = readWithinGroup(AggregateType.MODE, new Expression[0], false, null, false, true);
                    break;
                } else {
                    Expression readExpression4 = readExpression();
                    aggregate = new Aggregate(AggregateType.MODE, new Expression[0], this.currentSelect, false);
                    if (readIf(62)) {
                        read("BY");
                        Expression readExpression5 = readExpression();
                        String sql = readExpression4.getSQL(0);
                        Object sql2 = readExpression5.getSQL(0);
                        if (!sql.equals(sql2)) {
                            throw DbException.getSyntaxError(ErrorCode.IDENTICAL_EXPRESSIONS_SHOULD_BE_USED, this.sqlCommand, this.token.start(), sql, sql2);
                        }
                        readAggregateOrder(aggregate, readExpression4, true);
                        break;
                    } else {
                        readAggregateOrder(aggregate, readExpression4, false);
                        break;
                    }
                }
            case JSON_OBJECTAGG:
                boolean readIf = readIf(47);
                Expression readExpression6 = readExpression();
                if (readIf) {
                    read(84);
                } else if (!readIf(84) && (!this.database.getMode().acceptsCommaAsJsonKeyValueSeparator || !readIf(Opcodes.LDIV))) {
                    read(116);
                }
                aggregate = new Aggregate(AggregateType.JSON_OBJECTAGG, new Expression[]{readExpression6, readExpression()}, this.currentSelect, false);
                readJsonObjectFunctionFlags(aggregate, false);
                break;
            case JSON_ARRAYAGG:
                aggregate = new Aggregate(AggregateType.JSON_ARRAYAGG, new Expression[]{readExpression()}, this.currentSelect, readDistinctAgg());
                aggregate.setOrderByList(readIfOrderBy());
                aggregate.setFlags(1);
                readJsonObjectFunctionFlags(aggregate, true);
                break;
            default:
                aggregate = new Aggregate(aggregateType, new Expression[]{readExpression()}, this.currentSelect, readDistinctAgg());
                break;
        }
        read(106);
        readFilterAndOver(aggregate);
        return aggregate;
    }

    private Aggregate readWithinGroup(AggregateType aggregateType, Expression[] expressionArr, boolean z, Object obj, boolean z2, boolean z3) {
        read("WITHIN");
        read(37);
        read(105);
        read(62);
        read("BY");
        Aggregate aggregate = new Aggregate(aggregateType, expressionArr, this.currentSelect, z);
        aggregate.setExtraArguments(obj);
        if (z2) {
            int length = expressionArr.length;
            ArrayList<QueryOrderBy> arrayList = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                if (i > 0) {
                    read(Opcodes.LDIV);
                }
                arrayList.add(parseSortSpecification());
            }
            aggregate.setOrderByList(arrayList);
        } else if (z3) {
            readAggregateOrder(aggregate, readExpression(), true);
        } else {
            aggregate.setOrderByList(parseSortSpecificationList());
        }
        return aggregate;
    }

    private void readAggregateOrder(Aggregate aggregate, Expression expression, boolean z) {
        ArrayList<QueryOrderBy> arrayList = new ArrayList<>(1);
        QueryOrderBy queryOrderBy = new QueryOrderBy();
        queryOrderBy.expression = expression;
        if (z) {
            queryOrderBy.sortType = parseSortType();
        }
        arrayList.add(queryOrderBy);
        aggregate.setOrderByList(arrayList);
    }

    private ArrayList<QueryOrderBy> readIfOrderBy() {
        if (readIf(62, "BY")) {
            return parseSortSpecificationList();
        }
        return null;
    }

    private ArrayList<QueryOrderBy> parseSortSpecificationList() {
        ArrayList<QueryOrderBy> newSmallArrayList = Utils.newSmallArrayList();
        do {
            newSmallArrayList.add(parseSortSpecification());
        } while (readIf(Opcodes.LDIV));
        return newSmallArrayList;
    }

    private QueryOrderBy parseSortSpecification() {
        QueryOrderBy queryOrderBy = new QueryOrderBy();
        queryOrderBy.expression = readExpression();
        queryOrderBy.sortType = parseSortType();
        return queryOrderBy;
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0036, code lost:            if (readIfMore() != false) goto L20;     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x004e, code lost:            return new org.h2.expression.function.JavaFunction(r0, (org.h2.expression.Expression[]) r0.toArray(new org.h2.expression.Expression[0]));     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x0025, code lost:            if (readIf(106) == false) goto L10;     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0028, code lost:            r0.add(readExpression());     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private org.h2.expression.Expression readUserDefinedFunctionIf(org.h2.schema.Schema r8, java.lang.String r9) {
        /*
            r7 = this;
            r0 = r7
            r1 = r8
            r2 = r9
            org.h2.schema.UserDefinedFunction r0 = r0.findUserDefinedFunctionWithinPath(r1, r2)
            r10 = r0
            r0 = r10
            if (r0 != 0) goto Ld
            r0 = 0
            return r0
        Ld:
            r0 = r10
            boolean r0 = r0 instanceof org.h2.schema.FunctionAlias
            if (r0 == 0) goto L4f
            r0 = r10
            org.h2.schema.FunctionAlias r0 = (org.h2.schema.FunctionAlias) r0
            r11 = r0
            java.util.ArrayList r0 = org.h2.util.Utils.newSmallArrayList()
            r12 = r0
            r0 = r7
            r1 = 106(0x6a, float:1.49E-43)
            boolean r0 = r0.readIf(r1)
            if (r0 != 0) goto L39
        L28:
            r0 = r12
            r1 = r7
            org.h2.expression.Expression r1 = r1.readExpression()
            boolean r0 = r0.add(r1)
            r0 = r7
            boolean r0 = r0.readIfMore()
            if (r0 != 0) goto L28
        L39:
            org.h2.expression.function.JavaFunction r0 = new org.h2.expression.function.JavaFunction
            r1 = r0
            r2 = r11
            r3 = r12
            r4 = 0
            org.h2.expression.Expression[] r4 = new org.h2.expression.Expression[r4]
            java.lang.Object[] r3 = r3.toArray(r4)
            org.h2.expression.Expression[] r3 = (org.h2.expression.Expression[]) r3
            r1.<init>(r2, r3)
            return r0
        L4f:
            r0 = r10
            org.h2.schema.UserAggregate r0 = (org.h2.schema.UserAggregate) r0
            r11 = r0
            r0 = r7
            boolean r0 = r0.readDistinctAgg()
            r12 = r0
            java.util.ArrayList r0 = org.h2.util.Utils.newSmallArrayList()
            r13 = r0
        L60:
            r0 = r13
            r1 = r7
            org.h2.expression.Expression r1 = r1.readExpression()
            boolean r0 = r0.add(r1)
            r0 = r7
            boolean r0 = r0.readIfMore()
            if (r0 != 0) goto L60
            r0 = r13
            r1 = 0
            org.h2.expression.Expression[] r1 = new org.h2.expression.Expression[r1]
            java.lang.Object[] r0 = r0.toArray(r1)
            org.h2.expression.Expression[] r0 = (org.h2.expression.Expression[]) r0
            r14 = r0
            org.h2.expression.aggregate.JavaAggregate r0 = new org.h2.expression.aggregate.JavaAggregate
            r1 = r0
            r2 = r11
            r3 = r14
            r4 = r7
            org.h2.command.query.Select r4 = r4.currentSelect
            r5 = r12
            r1.<init>(r2, r3, r4, r5)
            r15 = r0
            r0 = r7
            r1 = r15
            r0.readFilterAndOver(r1)
            r0 = r15
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.Parser.readUserDefinedFunctionIf(org.h2.schema.Schema, java.lang.String):org.h2.expression.Expression");
    }

    private boolean readDistinctAgg() {
        if (readIf(26)) {
            return true;
        }
        readIf(3);
        return false;
    }

    private void readFilterAndOver(AbstractAggregate abstractAggregate) {
        if (readIf("FILTER", 105, 87)) {
            Expression readExpression = readExpression();
            read(106);
            abstractAggregate.setFilterCondition(readExpression);
        }
        readOver(abstractAggregate);
    }

    private void readOver(DataAnalysisOperation dataAnalysisOperation) {
        if (readIf("OVER")) {
            dataAnalysisOperation.setOverCondition(readWindowNameOrSpecification());
            this.currentSelect.setWindowQuery();
        } else {
            if (dataAnalysisOperation.isAggregate()) {
                this.currentSelect.setGroupQuery();
                return;
            }
            throw getSyntaxError();
        }
    }

    private Window readWindowNameOrSpecification() {
        return isToken(105) ? readWindowSpecification() : new Window(readIdentifier(), null, null, null);
    }

    private Window readWindowSpecification() {
        read(105);
        String str = null;
        if (this.currentTokenType == 2) {
            String str2 = this.currentToken;
            if (this.token.isQuoted() || (!equalsToken(str2, "PARTITION") && !equalsToken(str2, "ROWS") && !equalsToken(str2, "RANGE") && !equalsToken(str2, "GROUPS"))) {
                str = str2;
                read();
            }
        }
        ArrayList arrayList = null;
        if (readIf("PARTITION", "BY")) {
            arrayList = Utils.newSmallArrayList();
            do {
                arrayList.add(readExpression());
            } while (readIf(Opcodes.LDIV));
        }
        ArrayList<QueryOrderBy> readIfOrderBy = readIfOrderBy();
        WindowFrame readWindowFrame = readWindowFrame();
        read(106);
        return new Window(str, arrayList, readIfOrderBy, readWindowFrame);
    }

    private WindowFrame readWindowFrame() {
        WindowFrameUnits windowFrameUnits;
        WindowFrameBound readWindowFrameStarting;
        WindowFrameBound windowFrameBound;
        if (readIf("ROWS")) {
            windowFrameUnits = WindowFrameUnits.ROWS;
        } else if (readIf("RANGE")) {
            windowFrameUnits = WindowFrameUnits.RANGE;
        } else if (readIf("GROUPS")) {
            windowFrameUnits = WindowFrameUnits.GROUPS;
        } else {
            return null;
        }
        if (readIf(10)) {
            readWindowFrameStarting = readWindowFrameRange();
            read(4);
            windowFrameBound = readWindowFrameRange();
        } else {
            readWindowFrameStarting = readWindowFrameStarting();
            windowFrameBound = null;
        }
        int start = this.token.start();
        WindowFrameExclusion windowFrameExclusion = WindowFrameExclusion.EXCLUDE_NO_OTHERS;
        if (readIf("EXCLUDE")) {
            if (readIf("CURRENT", 66)) {
                windowFrameExclusion = WindowFrameExclusion.EXCLUDE_CURRENT_ROW;
            } else if (readIf(37)) {
                windowFrameExclusion = WindowFrameExclusion.EXCLUDE_GROUP;
            } else if (readIf("TIES")) {
                windowFrameExclusion = WindowFrameExclusion.EXCLUDE_TIES;
            } else {
                read("NO");
                read("OTHERS");
            }
        }
        WindowFrame windowFrame = new WindowFrame(windowFrameUnits, readWindowFrameStarting, windowFrameBound, windowFrameExclusion);
        if (!windowFrame.isValid()) {
            throw DbException.getSyntaxError(this.sqlCommand, start);
        }
        return windowFrame;
    }

    private WindowFrameBound readWindowFrameStarting() {
        if (readIf("UNBOUNDED")) {
            read("PRECEDING");
            return new WindowFrameBound(WindowFrameBoundType.UNBOUNDED_PRECEDING, null);
        }
        if (readIf("CURRENT")) {
            read(66);
            return new WindowFrameBound(WindowFrameBoundType.CURRENT_ROW, null);
        }
        Expression readExpression = readExpression();
        read("PRECEDING");
        return new WindowFrameBound(WindowFrameBoundType.PRECEDING, readExpression);
    }

    private WindowFrameBound readWindowFrameRange() {
        if (readIf("UNBOUNDED")) {
            if (readIf("PRECEDING")) {
                return new WindowFrameBound(WindowFrameBoundType.UNBOUNDED_PRECEDING, null);
            }
            read("FOLLOWING");
            return new WindowFrameBound(WindowFrameBoundType.UNBOUNDED_FOLLOWING, null);
        }
        if (readIf("CURRENT")) {
            read(66);
            return new WindowFrameBound(WindowFrameBoundType.CURRENT_ROW, null);
        }
        Expression readExpression = readExpression();
        if (readIf("PRECEDING")) {
            return new WindowFrameBound(WindowFrameBoundType.PRECEDING, readExpression);
        }
        read("FOLLOWING");
        return new WindowFrameBound(WindowFrameBoundType.FOLLOWING, readExpression);
    }

    private Expression readFunction(Schema schema, String str) {
        Expression readUserDefinedFunctionIf;
        Expression readUserDefinedFunctionIf2;
        String upperName = upperName(str);
        if (schema != null) {
            return readFunctionWithSchema(schema, str, upperName);
        }
        boolean isAllowBuiltinAliasOverride = this.database.isAllowBuiltinAliasOverride();
        if (isAllowBuiltinAliasOverride && (readUserDefinedFunctionIf2 = readUserDefinedFunctionIf(null, str)) != null) {
            return readUserDefinedFunctionIf2;
        }
        AggregateType aggregateType = Aggregate.getAggregateType(upperName);
        if (aggregateType != null) {
            return readAggregate(aggregateType, upperName);
        }
        Expression readBuiltinFunctionIf = readBuiltinFunctionIf(upperName);
        if (readBuiltinFunctionIf != null) {
            return readBuiltinFunctionIf;
        }
        WindowFunction readWindowFunction = readWindowFunction(upperName);
        if (readWindowFunction != null) {
            return readWindowFunction;
        }
        Expression readCompatibilityFunction = readCompatibilityFunction(upperName);
        if (readCompatibilityFunction != null) {
            return readCompatibilityFunction;
        }
        if (!isAllowBuiltinAliasOverride && (readUserDefinedFunctionIf = readUserDefinedFunctionIf(null, str)) != null) {
            return readUserDefinedFunctionIf;
        }
        throw DbException.get(ErrorCode.FUNCTION_NOT_FOUND_1, str);
    }

    private Expression readFunctionWithSchema(Schema schema, String str, String str2) {
        FunctionsPostgreSQL function;
        if (this.database.getMode().getEnum() == Mode.ModeEnum.PostgreSQL && schema.getName().equals(this.database.sysIdentifier(Constants.SCHEMA_PG_CATALOG)) && (function = FunctionsPostgreSQL.getFunction(str2)) != null) {
            return (Expression) readParameters(function);
        }
        Expression readUserDefinedFunctionIf = readUserDefinedFunctionIf(schema, str);
        if (readUserDefinedFunctionIf != null) {
            return readUserDefinedFunctionIf;
        }
        throw DbException.get(ErrorCode.FUNCTION_NOT_FOUND_1, str);
    }

    private Expression readCompatibilityFunction(String str) {
        Expression readExpression;
        Column parseColumnWithType;
        boolean z = -1;
        switch (str.hashCode()) {
            case -2137984988:
                if (str.equals("IFNULL")) {
                    z = 8;
                    break;
                }
                break;
            case -2020697580:
                if (str.equals("MINUTE")) {
                    z = 26;
                    break;
                }
                break;
            case -1854658143:
                if (str.equals("SCHEMA")) {
                    z = 13;
                    break;
                }
                break;
            case -1852950412:
                if (str.equals("SECOND")) {
                    z = 29;
                    break;
                }
                break;
            case -1838199823:
                if (str.equals("SUBSTR")) {
                    z = 38;
                    break;
                }
                break;
            case -1729888466:
                if (str.equals("NEXTVAL")) {
                    z = 41;
                    break;
                }
                break;
            case -1722875525:
                if (str.equals("DATABASE")) {
                    z = 10;
                    break;
                }
                break;
            case -1321838393:
                if (str.equals("DAYOFWEEK")) {
                    z = 19;
                    break;
                }
                break;
            case -1321778928:
                if (str.equals("DAYOFYEAR")) {
                    z = 21;
                    break;
                }
                break;
            case -1133313056:
                if (str.equals("ARRAY_APPEND")) {
                    z = false;
                    break;
                }
                break;
            case -1019868197:
                if (str.equals("SYSDATE")) {
                    z = 34;
                    break;
                }
                break;
            case -881372481:
                if (str.equals("ISO_DAY_OF_WEEK")) {
                    z = 23;
                    break;
                }
                break;
            case -828608596:
                if (str.equals("ARRAY_LENGTH")) {
                    z = 3;
                    break;
                }
                break;
            case -132223141:
                if (str.equals("DAY_OF_MONTH")) {
                    z = 16;
                    break;
                }
                break;
            case 67452:
                if (str.equals("DAY")) {
                    z = 15;
                    break;
                }
                break;
            case 77494:
                if (str.equals("NOW")) {
                    z = 33;
                    break;
                }
                break;
            case 77700:
                if (str.equals("NVL")) {
                    z = 9;
                    break;
                }
                break;
            case 2223588:
                if (str.equals("HOUR")) {
                    z = 22;
                    break;
                }
                break;
            case 2408750:
                if (str.equals("NVL2")) {
                    z = 6;
                    break;
                }
                break;
            case 2660340:
                if (str.equals("WEEK")) {
                    z = 30;
                    break;
                }
                break;
            case 2719805:
                if (str.equals("YEAR")) {
                    z = 31;
                    break;
                }
                break;
            case 69823180:
                if (str.equals("INSTR")) {
                    z = 35;
                    break;
                }
                break;
            case 72248700:
                if (str.equals("LCASE")) {
                    z = 37;
                    break;
                }
                break;
            case 73542240:
                if (str.equals("MONTH")) {
                    z = 27;
                    break;
                }
                break;
            case 79996705:
                if (str.equals("TODAY")) {
                    z = 12;
                    break;
                }
                break;
            case 80560389:
                if (str.equals("UCASE")) {
                    z = 39;
                    break;
                }
                break;
            case 525052137:
                if (str.equals("SYSTIMESTAMP")) {
                    z = 14;
                    break;
                }
                break;
            case 804978282:
                if (str.equals("CASEWHEN")) {
                    z = 5;
                    break;
                }
                break;
            case 1362946224:
                if (str.equals("ARRAY_CAT")) {
                    z = true;
                    break;
                }
                break;
            case 1362950192:
                if (str.equals("ARRAY_GET")) {
                    z = 2;
                    break;
                }
                break;
            case 1369386636:
                if (str.equals("QUARTER")) {
                    z = 28;
                    break;
                }
                break;
            case 1376594830:
                if (str.equals("ISO_WEEK")) {
                    z = 24;
                    break;
                }
                break;
            case 1376654295:
                if (str.equals("ISO_YEAR")) {
                    z = 25;
                    break;
                }
                break;
            case 1530431785:
                if (str.equals("POSITION")) {
                    z = 36;
                    break;
                }
                break;
            case 1669573011:
                if (str.equals("CONVERT")) {
                    z = 7;
                    break;
                }
                break;
            case 1844501966:
                if (str.equals("CURDATE")) {
                    z = 11;
                    break;
                }
                break;
            case 1844938639:
                if (str.equals("CURRVAL")) {
                    z = 40;
                    break;
                }
                break;
            case 1844986093:
                if (str.equals("CURTIME")) {
                    z = 32;
                    break;
                }
                break;
            case 1963754477:
                if (str.equals("DAYOFMONTH")) {
                    z = 17;
                    break;
                }
                break;
            case 2012579310:
                if (str.equals("DECODE")) {
                    z = 4;
                    break;
                }
                break;
            case 2074232729:
                if (str.equals("DAY_OF_WEEK")) {
                    z = 18;
                    break;
                }
                break;
            case 2074292194:
                if (str.equals("DAY_OF_YEAR")) {
                    z = 20;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
            case true:
                return new ConcatenationOperation(readExpression(), readLastArgument());
            case true:
                return new ArrayElementReference(readExpression(), readLastArgument());
            case true:
                return new CardinalityExpression(readSingleArgument(), false);
            case true:
                Expression readExpression2 = readExpression();
                boolean z2 = readExpression2.isConstant() && !readExpression2.getValue(this.session).containsNull();
                SimpleCase.SimpleWhen decodeToWhen = decodeToWhen(readExpression2, z2, readNextArgument(), readNextArgument());
                SimpleCase.SimpleWhen simpleWhen = decodeToWhen;
                Expression expression = null;
                while (true) {
                    if (readIf(Opcodes.LDIV)) {
                        Expression readExpression3 = readExpression();
                        if (readIf(Opcodes.LDIV)) {
                            SimpleCase.SimpleWhen decodeToWhen2 = decodeToWhen(readExpression2, z2, readExpression3, readExpression());
                            simpleWhen.setWhen(decodeToWhen2);
                            simpleWhen = decodeToWhen2;
                        } else {
                            expression = readExpression3;
                        }
                    }
                }
                read(106);
                return new SimpleCase(readExpression2, decodeToWhen, expression);
            case true:
                return readCompatibilityCase(readExpression());
            case true:
                return readCompatibilityCase(new NullPredicate(readExpression(), true, false));
            case true:
                if (this.database.getMode().swapConvertFunctionParameters) {
                    parseColumnWithType = parseColumnWithType(null);
                    readExpression = readNextArgument();
                } else {
                    readExpression = readExpression();
                    read(Opcodes.LDIV);
                    parseColumnWithType = parseColumnWithType(null);
                }
                read(106);
                return new CastSpecification(readExpression, parseColumnWithType);
            case true:
                return new CoalesceFunction(0, readExpression(), readLastArgument());
            case true:
                return readCoalesceFunction(0);
            case true:
                read(106);
                return new CurrentGeneralValueSpecification(0);
            case true:
                return readCurrentDateTimeValueFunction(0, true, str);
            case true:
                read(106);
                return ModeFunction.getCompatibilityDateTimeValueFunction(this.database, "TODAY", -1);
            case true:
                read(106);
                return new CurrentGeneralValueSpecification(3);
            case true:
                int i = -1;
                if (!readIf(106)) {
                    i = readInt();
                    if (i < 0 || i > 9) {
                        throw DbException.get(ErrorCode.INVALID_VALUE_SCALE, Integer.toString(i), CustomBooleanEditor.VALUE_0, "9");
                    }
                    read(106);
                }
                return ModeFunction.getCompatibilityDateTimeValueFunction(this.database, "SYSTIMESTAMP", i);
            case true:
            case true:
            case true:
                return new DateTimeFunction(0, 2, readSingleArgument(), null);
            case true:
            case true:
                return new DateTimeFunction(0, 20, readSingleArgument(), null);
            case true:
            case true:
                return new DateTimeFunction(0, 16, readSingleArgument(), null);
            case true:
                return new DateTimeFunction(0, 3, readSingleArgument(), null);
            case true:
                return new DateTimeFunction(0, 17, readSingleArgument(), null);
            case true:
                return new DateTimeFunction(0, 18, readSingleArgument(), null);
            case true:
                return new DateTimeFunction(0, 19, readSingleArgument(), null);
            case true:
                return new DateTimeFunction(0, 4, readSingleArgument(), null);
            case true:
                return new DateTimeFunction(0, 1, readSingleArgument(), null);
            case true:
                return new DateTimeFunction(0, 12, readSingleArgument(), null);
            case true:
                return new DateTimeFunction(0, 5, readSingleArgument(), null);
            case true:
                return new DateTimeFunction(0, 21, readSingleArgument(), null);
            case true:
                return new DateTimeFunction(0, 0, readSingleArgument(), null);
            case true:
                return readCurrentDateTimeValueFunction(2, true, "CURTIME");
            case true:
                return readCurrentDateTimeValueFunction(4, true, "NOW");
            case true:
                read(106);
                return ModeFunction.getCompatibilityDateTimeValueFunction(this.database, "SYSDATE", -1);
            case true:
                return new StringFunction(readNextArgument(), readExpression(), readIfArgument(), 0);
            case true:
                Expression readConcat = readConcat();
                if (!readIf(Opcodes.LDIV)) {
                    read(41);
                }
                return new StringFunction(readConcat, readSingleArgument(), null, 0);
            case true:
                return new StringFunction1(readSingleArgument(), 1);
            case true:
                return readSubstringFunction();
            case true:
                return new StringFunction1(readSingleArgument(), 0);
            case true:
                return readCompatibilitySequenceValueFunction(true);
            case true:
                return readCompatibilitySequenceValueFunction(false);
            default:
                return null;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:2:0x0006, code lost:            if (readIf(106) == false) goto L4;     */
    /* JADX WARN: Code restructure failed: missing block: B:3:0x0009, code lost:            r4.addParameter(readExpression());     */
    /* JADX WARN: Code restructure failed: missing block: B:4:0x0017, code lost:            if (readIfMore() != false) goto L9;     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x001a, code lost:            r4.doneWithParameters();     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x0021, code lost:            return r4;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private <T extends org.h2.expression.ExpressionWithVariableParameters> T readParameters(T r4) {
        /*
            r3 = this;
            r0 = r3
            r1 = 106(0x6a, float:1.49E-43)
            boolean r0 = r0.readIf(r1)
            if (r0 != 0) goto L1a
        L9:
            r0 = r4
            r1 = r3
            org.h2.expression.Expression r1 = r1.readExpression()
            r0.addParameter(r1)
            r0 = r3
            boolean r0 = r0.readIfMore()
            if (r0 != 0) goto L9
        L1a:
            r0 = r4
            r0.doneWithParameters()
            r0 = r4
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.Parser.readParameters(org.h2.expression.ExpressionWithVariableParameters):org.h2.expression.ExpressionWithVariableParameters");
    }

    private SimpleCase.SimpleWhen decodeToWhen(Expression expression, boolean z, Expression expression2, Expression expression3) {
        if (!z && (!expression2.isConstant() || expression2.getValue(this.session).containsNull())) {
            expression2 = new Comparison(6, expression, expression2, true);
        }
        return new SimpleCase.SimpleWhen(expression2, expression3);
    }

    private Expression readCompatibilityCase(Expression expression) {
        return new SearchedCase(new Expression[]{expression, readNextArgument(), readLastArgument()});
    }

    private Expression readCompatibilitySequenceValueFunction(boolean z) {
        Expression readExpression = readExpression();
        Expression readExpression2 = readIf(Opcodes.LDIV) ? readExpression() : null;
        read(106);
        return new CompatibilitySequenceValueFunction(readExpression, readExpression2, z);
    }

    /* JADX WARN: Code restructure failed: missing block: B:654:0x1667, code lost:            if (readJsonObjectFunctionFlags(r0, false) == false) goto L655;     */
    /* JADX WARN: Code restructure failed: missing block: B:655:0x166a, code lost:            r0 = readIf(47);        r0.addParameter(readExpression());     */
    /* JADX WARN: Code restructure failed: missing block: B:656:0x167d, code lost:            if (r0 == false) goto L658;     */
    /* JADX WARN: Code restructure failed: missing block: B:657:0x1680, code lost:            read(84);     */
    /* JADX WARN: Code restructure failed: missing block: B:658:0x16ae, code lost:            r0.addParameter(readExpression());     */
    /* JADX WARN: Code restructure failed: missing block: B:659:0x16bd, code lost:            if (readIf(org.springframework.asm.Opcodes.LDIV) != false) goto L763;     */
    /* JADX WARN: Code restructure failed: missing block: B:661:0x16c0, code lost:            readJsonObjectFunctionFlags(r0, false);     */
    /* JADX WARN: Code restructure failed: missing block: B:664:0x168f, code lost:            if (readIf(84) != false) goto L665;     */
    /* JADX WARN: Code restructure failed: missing block: B:666:0x169c, code lost:            if (r8.database.getMode().acceptsCommaAsJsonKeyValueSeparator == false) goto L664;     */
    /* JADX WARN: Code restructure failed: missing block: B:668:0x16a5, code lost:            if (readIf(org.springframework.asm.Opcodes.LDIV) != false) goto L665;     */
    /* JADX WARN: Code restructure failed: missing block: B:669:0x16a8, code lost:            read(116);     */
    /* JADX WARN: Code restructure failed: missing block: B:675:0x16f6, code lost:            if (readJsonObjectFunctionFlags(r0, true) == false) goto L674;     */
    /* JADX WARN: Code restructure failed: missing block: B:676:0x16f9, code lost:            r0.addParameter(readExpression());     */
    /* JADX WARN: Code restructure failed: missing block: B:677:0x1708, code lost:            if (readIf(org.springframework.asm.Opcodes.LDIV) != false) goto L765;     */
    /* JADX WARN: Code restructure failed: missing block: B:679:0x170b, code lost:            readJsonObjectFunctionFlags(r0, true);     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private org.h2.expression.Expression readBuiltinFunctionIf(java.lang.String r9) {
        /*
            Method dump skipped, instructions count: 6533
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.Parser.readBuiltinFunctionIf(java.lang.String):org.h2.expression.Expression");
    }

    private Expression readDateTimeFormatFunction(int i) {
        DateTimeFormatFunction dateTimeFormatFunction = new DateTimeFormatFunction(i);
        dateTimeFormatFunction.addParameter(readExpression());
        read(Opcodes.LDIV);
        dateTimeFormatFunction.addParameter(readExpression());
        if (readIf(Opcodes.LDIV)) {
            dateTimeFormatFunction.addParameter(readExpression());
            if (readIf(Opcodes.LDIV)) {
                dateTimeFormatFunction.addParameter(readExpression());
            }
        }
        read(106);
        dateTimeFormatFunction.doneWithParameters();
        return dateTimeFormatFunction;
    }

    private Expression readTrimFunction() {
        boolean readIf;
        int i;
        Expression readExpression;
        if (readIf("LEADING")) {
            i = 1;
            readIf = true;
        } else if (readIf("TRAILING")) {
            i = 2;
            readIf = true;
        } else {
            readIf = readIf("BOTH");
            i = 3;
        }
        Expression expression = null;
        if (readIf) {
            if (!readIf(35)) {
                expression = readExpression();
                read(35);
            }
            readExpression = readExpression();
        } else if (readIf(35)) {
            readExpression = readExpression();
        } else {
            readExpression = readExpression();
            if (readIf(35)) {
                expression = readExpression;
                readExpression = readExpression();
            } else if (readIfCompat(Opcodes.LDIV)) {
                expression = readExpression();
            }
        }
        read(106);
        return new TrimFunction(readExpression, expression, i);
    }

    private ArrayTableFunction readUnnestFunction() {
        ArrayTableFunction arrayTableFunction = new ArrayTableFunction(0);
        ArrayList<Column> newSmallArrayList = Utils.newSmallArrayList();
        if (!readIf(106)) {
            int i = 0;
            do {
                Expression readExpression = readExpression();
                TypeInfo typeInfo = TypeInfo.TYPE_NULL;
                boolean isConstant = readExpression.isConstant();
                if (isConstant || (readExpression instanceof CastSpecification)) {
                    if (isConstant) {
                        readExpression = readExpression.optimize(this.session);
                    }
                    TypeInfo type = readExpression.getType();
                    switch (type.getValueType()) {
                        case 38:
                            typeInfo = TypeInfo.TYPE_JSON;
                            break;
                        case 40:
                            typeInfo = (TypeInfo) type.getExtTypeInfo();
                            break;
                    }
                }
                arrayTableFunction.addParameter(readExpression);
                i++;
                newSmallArrayList.add(new Column("C" + i, typeInfo));
            } while (readIfMore());
        }
        if (readIf(89, "ORDINALITY")) {
            newSmallArrayList.add(new Column("NORD", TypeInfo.TYPE_INTEGER));
        }
        arrayTableFunction.setColumns(newSmallArrayList);
        arrayTableFunction.doneWithParameters();
        return arrayTableFunction;
    }

    private ArrayTableFunction readTableFunction(int i) {
        ArrayTableFunction arrayTableFunction = new ArrayTableFunction(i);
        ArrayList<Column> newSmallArrayList = Utils.newSmallArrayList();
        do {
            newSmallArrayList.add(parseColumnWithType(readIdentifier()));
            read(95);
            arrayTableFunction.addParameter(readExpression());
        } while (readIfMore());
        arrayTableFunction.setColumns(newSmallArrayList);
        arrayTableFunction.doneWithParameters();
        return arrayTableFunction;
    }

    private Expression readSingleArgument() {
        Expression readExpression = readExpression();
        read(106);
        return readExpression;
    }

    private Expression readNextArgument() {
        read(Opcodes.LDIV);
        return readExpression();
    }

    private Expression readLastArgument() {
        read(Opcodes.LDIV);
        Expression readExpression = readExpression();
        read(106);
        return readExpression;
    }

    private Expression readIfSingleArgument() {
        Expression readExpression;
        if (readIf(106)) {
            readExpression = null;
        } else {
            readExpression = readExpression();
            read(106);
        }
        return readExpression;
    }

    private Expression readIfArgument() {
        Expression readExpression = readIf(Opcodes.LDIV) ? readExpression() : null;
        read(106);
        return readExpression;
    }

    private Expression readCoalesceFunction(int i) {
        CoalesceFunction coalesceFunction = new CoalesceFunction(i);
        coalesceFunction.addParameter(readExpression());
        while (readIfMore()) {
            coalesceFunction.addParameter(readExpression());
        }
        if (i == 1 || i == 2) {
            coalesceFunction.setIgnoreNulls(readIgnoreNulls(this.database.getMode().greatestLeastIgnoreNulls));
        }
        coalesceFunction.doneWithParameters();
        return coalesceFunction;
    }

    private Expression readConcatFunction(int i) {
        ConcatFunction concatFunction = new ConcatFunction(i);
        concatFunction.addParameter(readExpression());
        concatFunction.addParameter(readNextArgument());
        if (i == 1) {
            concatFunction.addParameter(readNextArgument());
        }
        while (readIfMore()) {
            concatFunction.addParameter(readExpression());
        }
        concatFunction.doneWithParameters();
        return concatFunction;
    }

    private Expression readSubstringFunction() {
        SubstringFunction substringFunction = new SubstringFunction();
        substringFunction.addParameter(readExpression());
        if (readIf(35)) {
            substringFunction.addParameter(readExpression());
            if (readIf(33)) {
                substringFunction.addParameter(readExpression());
            }
        } else if (readIf(33)) {
            substringFunction.addParameter(ValueExpression.get(ValueInteger.get(1)));
            substringFunction.addParameter(readExpression());
        } else {
            readCompat(Opcodes.LDIV);
            substringFunction.addParameter(readExpression());
            if (readIf(Opcodes.LDIV)) {
                substringFunction.addParameter(readExpression());
            }
        }
        read(106);
        substringFunction.doneWithParameters();
        return substringFunction;
    }

    private int readDateTimeField() {
        int i = -1;
        switch (this.currentTokenType) {
            case 2:
                if (!this.token.isQuoted()) {
                    i = DateTimeFunction.getField(this.currentToken);
                    break;
                }
                break;
            case 24:
                i = 2;
                break;
            case 39:
                i = 3;
                break;
            case 54:
                i = 4;
                break;
            case 55:
                i = 1;
                break;
            case 68:
                i = 5;
                break;
            case 90:
                i = 0;
                break;
            case 94:
                if (this.token.value(this.session).getValueType() == 2) {
                    i = DateTimeFunction.getField(this.token.value(this.session).getString());
                    break;
                }
                break;
        }
        if (i < 0) {
            addExpected("date-time field");
            throw getSyntaxError();
        }
        read();
        return i;
    }

    private WindowFunction readWindowFunction(String str) {
        WindowFunctionType windowFunctionType = WindowFunctionType.get(str);
        if (windowFunctionType == null) {
            return null;
        }
        if (this.currentSelect == null) {
            throw getSyntaxError();
        }
        int minArgumentCount = WindowFunction.getMinArgumentCount(windowFunctionType);
        Expression[] expressionArr = null;
        if (minArgumentCount > 0) {
            int maxArgumentCount = WindowFunction.getMaxArgumentCount(windowFunctionType);
            expressionArr = new Expression[maxArgumentCount];
            if (minArgumentCount == maxArgumentCount) {
                for (int i = 0; i < minArgumentCount; i++) {
                    if (i > 0) {
                        read(Opcodes.LDIV);
                    }
                    expressionArr[i] = readExpression();
                }
            } else {
                int i2 = 0;
                while (i2 < maxArgumentCount && (i2 <= 0 || readIf(Opcodes.LDIV))) {
                    expressionArr[i2] = readExpression();
                    i2++;
                }
                if (i2 < minArgumentCount) {
                    throw getSyntaxError();
                }
                if (i2 != maxArgumentCount) {
                    expressionArr = (Expression[]) Arrays.copyOf(expressionArr, i2);
                }
            }
        }
        read(106);
        WindowFunction windowFunction = new WindowFunction(windowFunctionType, this.currentSelect, expressionArr);
        switch (windowFunctionType) {
            case NTH_VALUE:
                readFromFirstOrLast(windowFunction);
            case LEAD:
            case LAG:
            case FIRST_VALUE:
            case LAST_VALUE:
                windowFunction.setIgnoreNulls(readIgnoreNulls(false));
                break;
        }
        readOver(windowFunction);
        return windowFunction;
    }

    private void readFromFirstOrLast(WindowFunction windowFunction) {
        if (readIf(35, "LAST")) {
            windowFunction.setFromLast(true);
        } else {
            readIf(35, "FIRST");
        }
    }

    private boolean readIgnoreNulls(boolean z) {
        if (readIf("IGNORE", "NULLS")) {
            return true;
        }
        if (readIf("RESPECT", "NULLS")) {
            return false;
        }
        return z;
    }

    private boolean readJsonObjectFunctionFlags(ExpressionWithFlags expressionWithFlags, boolean z) {
        boolean z2 = false;
        int flags = expressionWithFlags.getFlags();
        if (readIf(58, 60, 58)) {
            flags &= -2;
            z2 = true;
        } else if (readIf("ABSENT", 60, 58)) {
            flags |= 1;
            z2 = true;
        }
        if (!z) {
            if (readIf(89, 80, "KEYS")) {
                flags |= 2;
                z2 = true;
            } else if (readIf("WITHOUT", 80, "KEYS")) {
                flags &= -3;
                z2 = true;
            }
        }
        if (z2) {
            expressionWithFlags.setFlags(flags);
        }
        return z2;
    }

    private Expression readKeywordCompatibilityFunctionOrColumn() {
        boolean z = this.nonKeywords != null && this.nonKeywords.get(this.currentTokenType);
        String str = this.currentToken;
        read();
        if (readIf(105)) {
            return readCompatibilityFunction(upperName(str));
        }
        if (z) {
            return readIf(Opcodes.FDIV) ? readTermObjectDot(str) : new ExpressionColumn(this.database, null, null, str);
        }
        throw getSyntaxError();
    }

    private Expression readCurrentDateTimeValueFunction(int i, boolean z, String str) {
        int i2 = -1;
        if (z) {
            if (i != 0 && this.currentTokenType != 106) {
                i2 = readInt();
                if (i2 < 0 || i2 > 9) {
                    throw DbException.get(ErrorCode.INVALID_VALUE_SCALE, Integer.toString(i2), CustomBooleanEditor.VALUE_0, "9");
                }
            }
            read(106);
        }
        if (this.database.isAllowBuiltinAliasOverride()) {
            FunctionAlias findFunction = this.database.getSchema(this.session.getCurrentSchemaName()).findFunction(str != null ? str : CurrentDateTimeValueFunction.getName(i));
            if (findFunction != null) {
                return new JavaFunction(findFunction, i2 >= 0 ? new Expression[]{ValueExpression.get(ValueInteger.get(i2))} : new Expression[0]);
            }
        }
        return new CurrentDateTimeValueFunction(i, i2);
    }

    private Expression readIfWildcardRowidOrSequencePseudoColumn(String str, String str2) {
        if (readIf(108)) {
            return parseWildcard(str, str2);
        }
        if (readIf(91)) {
            return new ExpressionColumn(this.database, str, str2);
        }
        if (this.database.getMode().nextvalAndCurrvalPseudoColumns) {
            return readIfSequencePseudoColumn(str, str2);
        }
        return null;
    }

    private Wildcard parseWildcard(String str, String str2) {
        Wildcard wildcard = new Wildcard(str, str2);
        if (readIf(29, 105)) {
            ArrayList<ExpressionColumn> newSmallArrayList = Utils.newSmallArrayList();
            do {
                String str3 = null;
                String str4 = null;
                String readIdentifier = readIdentifier();
                if (readIf(Opcodes.FDIV)) {
                    str4 = readIdentifier;
                    readIdentifier = readIdentifier();
                    if (readIf(Opcodes.FDIV)) {
                        str3 = str4;
                        str4 = readIdentifier;
                        readIdentifier = readIdentifier();
                        if (readIf(Opcodes.FDIV)) {
                            checkDatabaseName(str3);
                            str3 = str4;
                            str4 = readIdentifier;
                            readIdentifier = readIdentifier();
                        }
                    }
                }
                newSmallArrayList.add(new ExpressionColumn(this.database, str3, str4, readIdentifier));
            } while (readIfMore());
            wildcard.setExceptColumns(newSmallArrayList);
        }
        return wildcard;
    }

    private SequenceValue readIfSequencePseudoColumn(String str, String str2) {
        Sequence findSequence;
        if (str == null) {
            str = this.session.getCurrentSchemaName();
        }
        if (isTokenCompat("NEXTVAL")) {
            Sequence findSequence2 = findSequence(str, str2);
            if (findSequence2 != null) {
                read();
                return new SequenceValue(findSequence2, getCurrentPreparedOrSelect());
            }
            return null;
        }
        if (isTokenCompat("CURRVAL") && (findSequence = findSequence(str, str2)) != null) {
            read();
            return new SequenceValue(findSequence);
        }
        return null;
    }

    private Expression readTermObjectDot(String str) {
        Expression readIfWildcardRowidOrSequencePseudoColumn = readIfWildcardRowidOrSequencePseudoColumn(null, str);
        if (readIfWildcardRowidOrSequencePseudoColumn != null) {
            return readIfWildcardRowidOrSequencePseudoColumn;
        }
        String readIdentifier = readIdentifier();
        if (readIf(105)) {
            return readFunction(this.database.getSchema(str), readIdentifier);
        }
        if (readIf(Opcodes.FDIV)) {
            String str2 = str;
            String str3 = readIdentifier;
            Expression readIfWildcardRowidOrSequencePseudoColumn2 = readIfWildcardRowidOrSequencePseudoColumn(str2, str3);
            if (readIfWildcardRowidOrSequencePseudoColumn2 != null) {
                return readIfWildcardRowidOrSequencePseudoColumn2;
            }
            String readIdentifier2 = readIdentifier();
            if (readIf(105)) {
                checkDatabaseName(str2);
                return readFunction(this.database.getSchema(str3), readIdentifier2);
            }
            if (readIf(Opcodes.FDIV)) {
                checkDatabaseName(str2);
                str2 = str3;
                str3 = readIdentifier2;
                Expression readIfWildcardRowidOrSequencePseudoColumn3 = readIfWildcardRowidOrSequencePseudoColumn(str2, str3);
                if (readIfWildcardRowidOrSequencePseudoColumn3 != null) {
                    return readIfWildcardRowidOrSequencePseudoColumn3;
                }
                readIdentifier2 = readIdentifier();
            }
            return new ExpressionColumn(this.database, str2, str3, readIdentifier2);
        }
        return new ExpressionColumn(this.database, null, str, readIdentifier);
    }

    private void checkDatabaseName(String str) {
        if (!this.database.getIgnoreCatalogs() && !equalsToken(this.database.getShortName(), str)) {
            throw DbException.get(ErrorCode.DATABASE_NOT_FOUND_1, str);
        }
    }

    private Expression readTerm() {
        Expression readTermWithIdentifier = this.currentTokenType == 2 ? readTermWithIdentifier() : readTermWithoutIdentifier();
        while (true) {
            if (readIf(Opcodes.LNEG)) {
                readTermWithIdentifier = new ArrayElementReference(readTermWithIdentifier, readExpression());
                read(Opcodes.FNEG);
            } else if (readIf(Opcodes.FDIV)) {
                readTermWithIdentifier = new FieldReference(readTermWithIdentifier, readIdentifier());
            } else if (readIf(120)) {
                readTermWithIdentifier = readColonColonAfterTerm(readTermWithIdentifier);
            } else {
                TypeInfo readIntervalQualifier = readIntervalQualifier();
                if (readIntervalQualifier != null) {
                    readTermWithIdentifier = new CastSpecification(readTermWithIdentifier, readIntervalQualifier);
                } else {
                    int i = this.tokenIndex;
                    if (readIf("AT")) {
                        if (readIf("TIME", "ZONE")) {
                            readTermWithIdentifier = new TimeZoneOperation(readTermWithIdentifier, readExpression());
                        } else if (readIf("LOCAL")) {
                            readTermWithIdentifier = new TimeZoneOperation(readTermWithIdentifier, null);
                        } else {
                            setTokenIndex(i);
                            break;
                        }
                    } else {
                        if (!readIf("FORMAT")) {
                            break;
                        }
                        if (readIf(JsonFactory.FORMAT_NAME_JSON)) {
                            readTermWithIdentifier = new Format(readTermWithIdentifier, Format.FormatEnum.JSON);
                        } else {
                            setTokenIndex(i);
                            break;
                        }
                    }
                }
            }
        }
        return readTermWithIdentifier;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0004. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:131:0x063f  */
    /* JADX WARN: Removed duplicated region for block: B:133:0x0644  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private org.h2.expression.Expression readTermWithoutIdentifier() {
        /*
            Method dump skipped, instructions count: 1611
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.Parser.readTermWithoutIdentifier():org.h2.expression.Expression");
    }

    private Expression readTermWithIdentifier() {
        Expression readTermWithIdentifier;
        String str = this.currentToken;
        boolean isQuoted = this.token.isQuoted();
        read();
        if (readIf(105)) {
            readTermWithIdentifier = readFunction(null, str);
        } else if (readIf(Opcodes.FDIV)) {
            readTermWithIdentifier = readTermObjectDot(str);
        } else if (isQuoted) {
            readTermWithIdentifier = new ExpressionColumn(this.database, null, null, str);
        } else {
            readTermWithIdentifier = readTermWithIdentifier(str, isQuoted);
        }
        return readTermWithIdentifier;
    }

    private Expression readColonColonAfterTerm(Expression expression) {
        if (this.database.getMode().getEnum() == Mode.ModeEnum.PostgreSQL) {
            if (readIfCompat(Constants.SCHEMA_PG_CATALOG)) {
                read(Opcodes.FDIV);
            }
            if (readIfCompat("REGCLASS")) {
                return new Regclass(expression);
            }
        }
        return new CastSpecification(expression, parseColumnWithType(null));
    }

    private Expression readCurrentGeneralValueSpecification(int i) {
        read();
        if (readIf(105)) {
            read(106);
        }
        return new CurrentGeneralValueSpecification(i);
    }

    private Expression readColumnIfNotFunction() {
        boolean z = this.nonKeywords != null && this.nonKeywords.get(this.currentTokenType);
        String str = this.currentToken;
        read();
        if (readIf(105)) {
            return null;
        }
        if (z) {
            return readIf(Opcodes.FDIV) ? readTermObjectDot(str) : new ExpressionColumn(this.database, null, null, str);
        }
        throw getSyntaxError();
    }

    private Expression readSetFunction() {
        FunctionAlias findFunction;
        SetFunction setFunction = new SetFunction(readExpression(), readLastArgument());
        if (this.database.isAllowBuiltinAliasOverride() && (findFunction = this.database.getSchema(this.session.getCurrentSchemaName()).findFunction(setFunction.getName())) != null) {
            return new JavaFunction(findFunction, new Expression[]{setFunction.getSubexpression(0), setFunction.getSubexpression(1)});
        }
        return setFunction;
    }

    private Expression readOnDuplicateKeyValues(Table table, Update update) {
        read();
        read(105);
        Column readTableColumn = readTableColumn(new TableFilter(this.session, table, null, this.rightsChecked, null, 0, null));
        read(106);
        return new OnDuplicateKeyValues(readTableColumn, update);
    }

    private Expression readTermWithIdentifier(String str, boolean z) {
        switch (str.charAt(0) & 65503) {
            case 67:
                if (equalsToken("CURRENT", str)) {
                    if (readIf(84, 33)) {
                        return new SequenceValue(readSequence());
                    }
                    if (this.database.getMode().getEnum() == Mode.ModeEnum.DB2) {
                        return parseDB2SpecialRegisters(str);
                    }
                }
                break;
            case 68:
                if (this.currentTokenType == 94 && this.token.value(this.session).getValueType() == 2 && (equalsToken("DATE", str) || equalsToken("D", str))) {
                    String string = this.token.value(this.session).getString();
                    read();
                    return ValueExpression.get(ValueDate.parse(string));
                }
                break;
            case 69:
                if (this.currentTokenType == 94 && this.token.value(this.session).getValueType() == 2 && equalsToken("E", str)) {
                    String replaceAll = StringUtils.replaceAll(this.token.value(this.session).getString(), "\\\\", StrPool.BACKSLASH);
                    read();
                    return ValueExpression.get(ValueVarchar.get(replaceAll));
                }
                break;
            case 71:
                if (this.currentTokenType == 94) {
                    int valueType = this.token.value(this.session).getValueType();
                    if (valueType == 2 && equalsToken("GEOMETRY", str)) {
                        ValueExpression valueExpression = ValueExpression.get(ValueGeometry.get(this.token.value(this.session).getString()));
                        read();
                        return valueExpression;
                    }
                    if (valueType == 6 && equalsToken("GEOMETRY", str)) {
                        ValueExpression valueExpression2 = ValueExpression.get(ValueGeometry.getFromEWKB(this.token.value(this.session).getBytesNoCopy()));
                        read();
                        return valueExpression2;
                    }
                }
                break;
            case 74:
                if (this.currentTokenType == 94) {
                    int valueType2 = this.token.value(this.session).getValueType();
                    if (valueType2 == 2 && equalsToken(JsonFactory.FORMAT_NAME_JSON, str)) {
                        ValueExpression valueExpression3 = ValueExpression.get(ValueJson.fromJson(this.token.value(this.session).getString()));
                        read();
                        return valueExpression3;
                    }
                    if (valueType2 == 6 && equalsToken(JsonFactory.FORMAT_NAME_JSON, str)) {
                        ValueExpression valueExpression4 = ValueExpression.get(ValueJson.fromJson(this.token.value(this.session).getBytesNoCopy()));
                        read();
                        return valueExpression4;
                    }
                }
                break;
            case 78:
                if (equalsToken("NEXT", str) && readIf(84, 33)) {
                    return new SequenceValue(readSequence(), getCurrentPreparedOrSelect());
                }
                break;
            case 84:
                if (equalsToken("TIME", str)) {
                    if (readIf(89, "TIME", "ZONE")) {
                        if (this.currentTokenType != 94 || this.token.value(this.session).getValueType() != 2) {
                            throw getSyntaxError();
                        }
                        String string2 = this.token.value(this.session).getString();
                        read();
                        return ValueExpression.get(ValueTimeTimeZone.parse(string2, this.session));
                    }
                    boolean readIf = readIf("WITHOUT", "TIME", "ZONE");
                    if (this.currentTokenType == 94 && this.token.value(this.session).getValueType() == 2) {
                        String string3 = this.token.value(this.session).getString();
                        read();
                        return ValueExpression.get(ValueTime.parse(string3, this.session));
                    }
                    if (readIf) {
                        throw getSyntaxError();
                    }
                } else if (equalsToken("TIMESTAMP", str)) {
                    if (readIf(89, "TIME", "ZONE")) {
                        if (this.currentTokenType != 94 || this.token.value(this.session).getValueType() != 2) {
                            throw getSyntaxError();
                        }
                        String string4 = this.token.value(this.session).getString();
                        read();
                        return ValueExpression.get(ValueTimestampTimeZone.parse(string4, this.session));
                    }
                    boolean readIf2 = readIf("WITHOUT", "TIME", "ZONE");
                    if (this.currentTokenType == 94 && this.token.value(this.session).getValueType() == 2) {
                        String string5 = this.token.value(this.session).getString();
                        read();
                        return ValueExpression.get(ValueTimestamp.parse(string5, this.session));
                    }
                    if (readIf2) {
                        throw getSyntaxError();
                    }
                } else if (this.currentTokenType == 94 && this.token.value(this.session).getValueType() == 2) {
                    if (equalsToken("T", str)) {
                        String string6 = this.token.value(this.session).getString();
                        read();
                        return ValueExpression.get(ValueTime.parse(string6, this.session));
                    }
                    if (equalsToken("TS", str)) {
                        String string7 = this.token.value(this.session).getString();
                        read();
                        return ValueExpression.get(ValueTimestamp.parse(string7, this.session));
                    }
                }
                break;
            case 85:
                if (this.currentTokenType == 94 && this.token.value(this.session).getValueType() == 2 && equalsToken("UUID", str)) {
                    String string8 = this.token.value(this.session).getString();
                    read();
                    return ValueExpression.get(ValueUuid.get(string8));
                }
                break;
        }
        return new ExpressionColumn(this.database, null, null, str, z);
    }

    private Prepared getCurrentPreparedOrSelect() {
        Prepared prepared = this.currentPrepared;
        return prepared != null ? prepared : this.currentSelect;
    }

    private Expression readInterval() {
        boolean readIf = readIf(102);
        if (!readIf) {
            readIf(103);
        }
        if (this.currentTokenType != 94 || this.token.value(this.session).getValueType() != 2) {
            addExpected("string");
            throw getSyntaxError();
        }
        String string = this.token.value(this.session).getString();
        read();
        TypeInfo readIntervalQualifier = readIntervalQualifier();
        try {
            ValueInterval parseInterval = IntervalUtils.parseInterval(IntervalQualifier.valueOf(readIntervalQualifier.getValueType() - 22), readIf, string);
            if (readIntervalQualifier.getDeclaredPrecision() != -1 || readIntervalQualifier.getDeclaredScale() != -1) {
                return TypedValueExpression.get(parseInterval.castTo(readIntervalQualifier, this.session), readIntervalQualifier);
            }
            return ValueExpression.get(parseInterval);
        } catch (Exception e) {
            throw DbException.get(ErrorCode.INVALID_DATETIME_CONSTANT_2, e, "INTERVAL", string);
        }
    }

    private Expression parseDB2SpecialRegisters(String str) {
        if (readIfCompat("TIMESTAMP")) {
            if (readIf(89, "TIME", "ZONE")) {
                return readCurrentDateTimeValueFunction(3, readIf(105), null);
            }
            return readCurrentDateTimeValueFunction(4, readIf(105), null);
        }
        if (readIfCompat("TIME")) {
            return readCurrentDateTimeValueFunction(2, false, null);
        }
        if (readIfCompat("DATE")) {
            return readCurrentDateTimeValueFunction(0, false, null);
        }
        return new ExpressionColumn(this.database, null, null, str);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v18, types: [org.h2.expression.SearchedCase] */
    private Expression readCase() {
        SimpleCase simpleCase;
        if (readIf(86)) {
            ?? searchedCase = new SearchedCase();
            do {
                Expression readExpression = readExpression();
                read("THEN");
                searchedCase.addParameter(readExpression);
                searchedCase.addParameter(readExpression());
            } while (readIf(86));
            if (readIf(27)) {
                searchedCase.addParameter(readExpression());
            }
            searchedCase.doneWithParameters();
            simpleCase = searchedCase;
        } else {
            Expression readExpression2 = readExpression();
            read(86);
            SimpleCase.SimpleWhen readSimpleWhenClause = readSimpleWhenClause(readExpression2);
            SimpleCase.SimpleWhen simpleWhen = readSimpleWhenClause;
            while (true) {
                SimpleCase.SimpleWhen simpleWhen2 = simpleWhen;
                if (!readIf(86)) {
                    break;
                }
                SimpleCase.SimpleWhen readSimpleWhenClause2 = readSimpleWhenClause(readExpression2);
                simpleWhen2.setWhen(readSimpleWhenClause2);
                simpleWhen = readSimpleWhenClause2;
            }
            simpleCase = new SimpleCase(readExpression2, readSimpleWhenClause, readIf(27) ? readExpression() : null);
        }
        read(28);
        return simpleCase;
    }

    private SimpleCase.SimpleWhen readSimpleWhenClause(Expression expression) {
        Expression readWhenOperand = readWhenOperand(expression);
        if (readIf(Opcodes.LDIV)) {
            ArrayList newSmallArrayList = Utils.newSmallArrayList();
            newSmallArrayList.add(readWhenOperand);
            do {
                newSmallArrayList.add(readWhenOperand(expression));
            } while (readIf(Opcodes.LDIV));
            read("THEN");
            return new SimpleCase.SimpleWhen((Expression[]) newSmallArrayList.toArray(new Expression[0]), readExpression());
        }
        read("THEN");
        return new SimpleCase.SimpleWhen(readWhenOperand, readExpression());
    }

    private Expression readWhenOperand(Expression expression) {
        int i = this.tokenIndex;
        boolean readIf = readIf(57);
        Expression readConditionRightHandSide = readConditionRightHandSide(expression, readIf, true);
        if (readConditionRightHandSide == null) {
            if (readIf) {
                setTokenIndex(i);
            }
            readConditionRightHandSide = readExpression();
        }
        return readConditionRightHandSide;
    }

    /* JADX WARN: Code restructure failed: missing block: B:6:0x002b, code lost:            if (r0.length() <= 1000000000) goto L7;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private java.lang.String readString() {
        /*
            r4 = this;
            r0 = r4
            org.h2.command.Token r0 = r0.token
            int r0 = r0.start()
            r5 = r0
            r0 = r4
            org.h2.expression.Expression r0 = r0.readExpression()
            r6 = r0
            r0 = r6
            r1 = r4
            org.h2.engine.SessionLocal r1 = r1.session     // Catch: org.h2.message.DbException -> L33
            org.h2.expression.Expression r0 = r0.optimize(r1)     // Catch: org.h2.message.DbException -> L33
            r1 = r4
            org.h2.engine.SessionLocal r1 = r1.session     // Catch: org.h2.message.DbException -> L33
            org.h2.value.Value r0 = r0.getValue(r1)     // Catch: org.h2.message.DbException -> L33
            java.lang.String r0 = r0.getString()     // Catch: org.h2.message.DbException -> L33
            r7 = r0
            r0 = r7
            if (r0 == 0) goto L2e
            r0 = r7
            int r0 = r0.length()     // Catch: org.h2.message.DbException -> L33
            r1 = 1000000000(0x3b9aca00, float:0.0047237873)
            if (r0 > r1) goto L30
        L2e:
            r0 = r7
            return r0
        L30:
            goto L34
        L33:
            r7 = move-exception
        L34:
            r0 = r4
            java.lang.String r0 = r0.sqlCommand
            r1 = r5
            java.lang.String r2 = "character string"
            org.h2.message.DbException r0 = org.h2.message.DbException.getSyntaxError(r0, r1, r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.Parser.readString():java.lang.String");
    }

    private Expression readStringOrParameter() {
        Expression optimize;
        int start = this.token.start();
        try {
            optimize = readExpression().optimize(this.session);
        } catch (DbException e) {
        }
        if (optimize instanceof Parameter) {
            return optimize;
        }
        Value value = optimize.getValue(this.session);
        int valueType = value.getValueType();
        if ((valueType == 58 || valueType == 2) && (optimize instanceof ValueExpression)) {
            return optimize;
        }
        String string = value.getString();
        if (string == null || string.length() <= 1000000000) {
            return string == null ? ValueExpression.NULL : ValueExpression.get(ValueVarchar.get(string, this.database));
        }
        throw DbException.getSyntaxError(this.sqlCommand, start, "character string");
    }

    private String readIdentifierWithSchema(String str) {
        String readIdentifier = readIdentifier();
        this.schemaName = str;
        if (readIf(Opcodes.FDIV)) {
            readIdentifier = readIdentifierWithSchema2(readIdentifier);
        }
        return readIdentifier;
    }

    private String readIdentifierWithSchema2(String str) {
        this.schemaName = str;
        if (this.database.getMode().allowEmptySchemaValuesAsDefaultSchema && readIf(Opcodes.FDIV)) {
            if (equalsToken(this.schemaName, this.database.getShortName()) || this.database.getIgnoreCatalogs()) {
                this.schemaName = this.session.getCurrentSchemaName();
                str = readIdentifier();
            }
        } else {
            str = readIdentifier();
            if (this.currentTokenType == 110 && (equalsToken(this.schemaName, this.database.getShortName()) || this.database.getIgnoreCatalogs())) {
                read();
                this.schemaName = str;
                str = readIdentifier();
            }
        }
        return str;
    }

    private String readIdentifierWithSchema() {
        return readIdentifierWithSchema(this.session.getCurrentSchemaName());
    }

    private String readIdentifier() {
        if (!isIdentifier() && (!this.session.isQuirksMode() || !isKeyword(this.currentTokenType))) {
            throw DbException.getSyntaxError(this.sqlCommand, this.token.start(), "identifier");
        }
        String str = this.currentToken;
        read();
        return str;
    }

    private String readIdentifierOrKeyword() {
        if (this.currentTokenType < 2 || this.currentTokenType > 91) {
            addExpected("identifier or keyword");
            throw getSyntaxError();
        }
        String str = this.currentToken;
        read();
        return str;
    }

    private Column parseColumnForTable(String str, boolean z) {
        Column parseColumnWithType;
        Mode mode = this.database.getMode();
        if (mode.identityDataType && readIfCompat("IDENTITY")) {
            parseColumnWithType = new Column(str, TypeInfo.TYPE_BIGINT);
            parseCompatibilityIdentityOptions(parseColumnWithType);
            parseColumnWithType.setPrimaryKey(true);
        } else if (mode.serialDataTypes && readIfCompat("BIGSERIAL")) {
            parseColumnWithType = new Column(str, TypeInfo.TYPE_BIGINT);
            parseColumnWithType.setIdentityOptions(new SequenceOptions(), false);
        } else if (mode.serialDataTypes && readIfCompat("SERIAL")) {
            parseColumnWithType = new Column(str, TypeInfo.TYPE_INTEGER);
            parseColumnWithType.setIdentityOptions(new SequenceOptions(), false);
        } else {
            parseColumnWithType = parseColumnWithType(str);
        }
        if (readIf("INVISIBLE")) {
            parseColumnWithType.setVisible(false);
        } else if (readIf("VISIBLE")) {
            parseColumnWithType.setVisible(true);
        }
        boolean z2 = false;
        NullConstraintType parseNotNullConstraint = parseNotNullConstraint();
        if (!parseColumnWithType.isIdentity()) {
            if (readIfCompat(7)) {
                parseColumnWithType.setGeneratedExpression(readExpression());
            } else if (readIf(25)) {
                if (readIf(60, 58)) {
                    z2 = true;
                } else {
                    parseColumnWithType.setDefaultExpression(this.session, readExpression());
                }
            } else if (readIf("GENERATED")) {
                boolean readIf = readIf("ALWAYS");
                if (!readIf) {
                    read("BY");
                    read(25);
                }
                read(7);
                if (readIf("IDENTITY")) {
                    SequenceOptions sequenceOptions = new SequenceOptions();
                    if (readIf(105)) {
                        parseSequenceOptions(sequenceOptions, null, false, false);
                        read(106);
                    }
                    parseColumnWithType.setIdentityOptions(sequenceOptions, readIf);
                } else {
                    if (!readIf) {
                        throw getSyntaxError();
                    }
                    parseColumnWithType.setGeneratedExpression(readExpression());
                }
            }
            if (!parseColumnWithType.isGenerated() && readIf(60, "UPDATE")) {
                parseColumnWithType.setOnUpdateExpression(this.session, readExpression());
            }
            parseNotNullConstraint = parseNotNullConstraint(parseNotNullConstraint);
            if (parseCompatibilityIdentity(parseColumnWithType, mode)) {
                parseNotNullConstraint = parseNotNullConstraint(parseNotNullConstraint);
            }
        }
        switch (parseNotNullConstraint) {
            case NULL_IS_ALLOWED:
                if (parseColumnWithType.isIdentity()) {
                    throw DbException.get(ErrorCode.COLUMN_MUST_NOT_BE_NULLABLE_1, parseColumnWithType.getName());
                }
                parseColumnWithType.setNullable(true);
                break;
            case NULL_IS_NOT_ALLOWED:
                parseColumnWithType.setNullable(false);
                break;
            case NO_NULL_CONSTRAINT_FOUND:
                if (!parseColumnWithType.isIdentity()) {
                    parseColumnWithType.setNullable(z);
                    break;
                }
                break;
            default:
                throw DbException.get(ErrorCode.UNKNOWN_MODE_1, "Internal Error - unhandled case: " + parseNotNullConstraint.name());
        }
        if (!z2) {
            if (readIf(25, 60, 58)) {
                z2 = true;
            } else if (readIfCompat("NULL_TO_DEFAULT")) {
                z2 = true;
            }
        }
        if (z2) {
            parseColumnWithType.setDefaultOnNull(true);
        }
        if (!parseColumnWithType.isGenerated() && readIf("SEQUENCE")) {
            parseColumnWithType.setSequence(readSequence(), parseColumnWithType.isGeneratedAlways());
        }
        if (readIf("SELECTIVITY")) {
            parseColumnWithType.setSelectivity(readNonNegativeInt());
        }
        if (mode.mySqlTableOptions) {
            if (readIfCompat("CHARACTER")) {
                readIf(71);
                readMySQLCharset();
            }
            if (readIfCompat("COLLATE")) {
                readMySQLCharset();
            }
        }
        String readCommentIf = readCommentIf();
        if (readCommentIf != null) {
            parseColumnWithType.setComment(readCommentIf);
        }
        return parseColumnWithType;
    }

    private void parseCompatibilityIdentityOptions(Column column) {
        SequenceOptions sequenceOptions = new SequenceOptions();
        if (readIf(105)) {
            sequenceOptions.setStartValue(ValueExpression.get(ValueBigint.get(readLong())));
            if (readIf(Opcodes.LDIV)) {
                sequenceOptions.setIncrement(ValueExpression.get(ValueBigint.get(readLong())));
            }
            read(106);
        }
        column.setIdentityOptions(sequenceOptions, false);
    }

    private String readCommentIf() {
        if (readIf("COMMENT")) {
            readIf(45);
            return readString();
        }
        return null;
    }

    private Column parseColumnWithType(String str) {
        TypeInfo readIfDataType = readIfDataType();
        if (readIfDataType == null) {
            return getColumnWithDomain(str, getSchema().getDomain(readIdentifierWithSchema()));
        }
        return new Column(str, readIfDataType);
    }

    private TypeInfo parseDataType() {
        TypeInfo readIfDataType = readIfDataType();
        if (readIfDataType == null) {
            addExpected("data type");
            throw getSyntaxError();
        }
        return readIfDataType;
    }

    private TypeInfo readIfDataType() {
        TypeInfo readIfDataType1 = readIfDataType1();
        if (readIfDataType1 != null) {
            while (readIf(6)) {
                readIfDataType1 = parseArrayType(readIfDataType1);
            }
        }
        return readIfDataType1;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:77:0x02f7. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:160:0x0517  */
    /* JADX WARN: Removed duplicated region for block: B:166:0x054d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private org.h2.value.TypeInfo readIfDataType1() {
        /*
            Method dump skipped, instructions count: 1928
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.Parser.readIfDataType1():org.h2.value.TypeInfo");
    }

    private static DbException getInvalidPrecisionException(DataType dataType, long j) {
        return DbException.get(ErrorCode.INVALID_VALUE_PRECISION, Long.toString(j), Long.toString(dataType.minPrecision), Long.toString(dataType.maxPrecision));
    }

    private static Column getColumnWithDomain(String str, Domain domain) {
        Column column = new Column(str, domain.getDataType());
        column.setComment(domain.getComment());
        column.setDomain(domain);
        return column;
    }

    private TypeInfo parseFloatType() {
        int i;
        int i2 = 15;
        if (readIf(105)) {
            i = readNonNegativeInt();
            read(106);
            if (i < 1 || i > 53) {
                throw DbException.get(ErrorCode.INVALID_VALUE_PRECISION, Integer.toString(i), CustomBooleanEditor.VALUE_1, "53");
            }
            if (i <= 24) {
                i2 = 14;
            }
        } else {
            i = 0;
        }
        return TypeInfo.getTypeInfo(i2, i, -1, null);
    }

    private TypeInfo parseNumericType(boolean z) {
        long j = -1;
        int i = -1;
        if (readIf(105)) {
            j = readPrecision(13);
            if (j < 1) {
                throw getInvalidNumericPrecisionException(j);
            }
            if (j > 100000) {
                if (this.session.isQuirksMode() || this.session.isTruncateLargeLength()) {
                    j = 100000;
                } else {
                    throw getInvalidNumericPrecisionException(j);
                }
            }
            if (readIf(Opcodes.LDIV)) {
                i = readInt();
                if (i < 0 || i > 100000) {
                    throw DbException.get(ErrorCode.INVALID_VALUE_SCALE, Integer.toString(i), CustomBooleanEditor.VALUE_0, "100000");
                }
            }
            read(106);
        } else if (this.database.getMode().numericIsDecfloat) {
            return TypeInfo.TYPE_DECFLOAT;
        }
        return TypeInfo.getTypeInfo(13, j, i, z ? ExtTypeInfoNumeric.DECIMAL : null);
    }

    private TypeInfo parseDecfloatType() {
        long j = -1;
        if (readIf(105)) {
            j = readPrecision(16);
            if (j < 1 || j > 100000) {
                throw getInvalidNumericPrecisionException(j);
            }
            read(106);
        }
        return TypeInfo.getTypeInfo(16, j, -1, null);
    }

    private static DbException getInvalidNumericPrecisionException(long j) {
        return DbException.get(ErrorCode.INVALID_VALUE_PRECISION, Long.toString(j), CustomBooleanEditor.VALUE_1, "100000");
    }

    private TypeInfo parseTimeType() {
        int i = -1;
        if (readIf(105)) {
            i = readNonNegativeInt();
            if (i > 9) {
                throw DbException.get(ErrorCode.INVALID_VALUE_SCALE, Integer.toString(i), CustomBooleanEditor.VALUE_0, "9");
            }
            read(106);
        }
        int i2 = 18;
        if (readIf(89, "TIME", "ZONE")) {
            i2 = 19;
        } else {
            readIf("WITHOUT", "TIME", "ZONE");
        }
        return TypeInfo.getTypeInfo(i2, -1L, i, null);
    }

    private TypeInfo parseTimestampType() {
        int i = -1;
        if (readIf(105)) {
            i = readNonNegativeInt();
            if (readIf(Opcodes.LDIV)) {
                i = readNonNegativeInt();
            }
            if (i > 9) {
                throw DbException.get(ErrorCode.INVALID_VALUE_SCALE, Integer.toString(i), CustomBooleanEditor.VALUE_0, "9");
            }
            read(106);
        }
        int i2 = 20;
        if (readIf(89, "TIME", "ZONE")) {
            i2 = 21;
        } else {
            readIf("WITHOUT", "TIME", "ZONE");
        }
        return TypeInfo.getTypeInfo(i2, -1L, i, null);
    }

    private TypeInfo parseDateTimeType(boolean z) {
        int i;
        if (z) {
            i = 0;
        } else {
            i = -1;
            if (readIf(105)) {
                i = readNonNegativeInt();
                if (i > 9) {
                    throw DbException.get(ErrorCode.INVALID_VALUE_SCALE, Integer.toString(i), CustomBooleanEditor.VALUE_0, "9");
                }
                read(106);
            }
        }
        return TypeInfo.getTypeInfo(20, -1L, i, null);
    }

    private TypeInfo readIntervalQualifier() {
        IntervalQualifier intervalQualifier;
        int i = -1;
        int i2 = -1;
        switch (this.currentTokenType) {
            case 24:
                read();
                if (readIf(105)) {
                    i = readNonNegativeInt();
                    read(106);
                }
                if (readIf(76)) {
                    switch (this.currentTokenType) {
                        case 39:
                            read();
                            intervalQualifier = IntervalQualifier.DAY_TO_HOUR;
                            break;
                        case 54:
                            read();
                            intervalQualifier = IntervalQualifier.DAY_TO_MINUTE;
                            break;
                        case 68:
                            read();
                            if (readIf(105)) {
                                i2 = readNonNegativeInt();
                                read(106);
                            }
                            intervalQualifier = IntervalQualifier.DAY_TO_SECOND;
                            break;
                        default:
                            throw intervalDayError();
                    }
                } else {
                    intervalQualifier = IntervalQualifier.DAY;
                    break;
                }
            case 39:
                read();
                if (readIf(105)) {
                    i = readNonNegativeInt();
                    read(106);
                }
                if (readIf(76)) {
                    switch (this.currentTokenType) {
                        case 54:
                            read();
                            intervalQualifier = IntervalQualifier.HOUR_TO_MINUTE;
                            break;
                        case 68:
                            read();
                            if (readIf(105)) {
                                i2 = readNonNegativeInt();
                                read(106);
                            }
                            intervalQualifier = IntervalQualifier.HOUR_TO_SECOND;
                            break;
                        default:
                            throw intervalHourError();
                    }
                } else {
                    intervalQualifier = IntervalQualifier.HOUR;
                    break;
                }
            case 54:
                read();
                if (readIf(105)) {
                    i = readNonNegativeInt();
                    read(106);
                }
                if (readIf(76, 68)) {
                    if (readIf(105)) {
                        i2 = readNonNegativeInt();
                        read(106);
                    }
                    intervalQualifier = IntervalQualifier.MINUTE_TO_SECOND;
                    break;
                } else {
                    intervalQualifier = IntervalQualifier.MINUTE;
                    break;
                }
            case 55:
                read();
                if (readIf(105)) {
                    i = readNonNegativeInt();
                    read(106);
                }
                intervalQualifier = IntervalQualifier.MONTH;
                break;
            case 68:
                read();
                if (readIf(105)) {
                    i = readNonNegativeInt();
                    if (readIf(Opcodes.LDIV)) {
                        i2 = readNonNegativeInt();
                    }
                    read(106);
                }
                intervalQualifier = IntervalQualifier.SECOND;
                break;
            case 90:
                read();
                if (readIf(105)) {
                    i = readNonNegativeInt();
                    read(106);
                }
                if (readIf(76, 55)) {
                    intervalQualifier = IntervalQualifier.YEAR_TO_MONTH;
                    break;
                } else {
                    intervalQualifier = IntervalQualifier.YEAR;
                    break;
                }
            default:
                return null;
        }
        if (i >= 0 && (i == 0 || i > 18)) {
            throw DbException.get(ErrorCode.INVALID_VALUE_PRECISION, Integer.toString(i), CustomBooleanEditor.VALUE_1, "18");
        }
        if (i2 >= 0 && i2 > 9) {
            throw DbException.get(ErrorCode.INVALID_VALUE_SCALE, Integer.toString(i2), CustomBooleanEditor.VALUE_0, "9");
        }
        return TypeInfo.getTypeInfo(intervalQualifier.ordinal() + 22, i, i2, null);
    }

    private DbException intervalQualifierError() {
        if (this.expectedList != null) {
            addMultipleExpected(90, 55, 24, 39, 54, 68);
        }
        return getSyntaxError();
    }

    private DbException intervalDayError() {
        if (this.expectedList != null) {
            addMultipleExpected(39, 54, 68);
        }
        return getSyntaxError();
    }

    private DbException intervalHourError() {
        if (this.expectedList != null) {
            addMultipleExpected(54, 68);
        }
        return getSyntaxError();
    }

    private TypeInfo parseArrayType(TypeInfo typeInfo) {
        int i = -1;
        if (readIf(Opcodes.LNEG)) {
            i = readNonNegativeInt();
            if (i > 65536) {
                throw DbException.get(ErrorCode.INVALID_VALUE_PRECISION, Integer.toString(i), CustomBooleanEditor.VALUE_0, "65536");
            }
            read(Opcodes.FNEG);
        }
        return TypeInfo.getTypeInfo(40, i, -1, typeInfo);
    }

    private TypeInfo parseEnumType() {
        read(105);
        ArrayList arrayList = new ArrayList();
        do {
            arrayList.add(readString());
        } while (readIfMore());
        return TypeInfo.getTypeInfo(36, -1L, -1, new ExtTypeInfoEnum((String[]) arrayList.toArray(new String[0])));
    }

    private TypeInfo parseGeometryType() {
        ExtTypeInfoGeometry extTypeInfoGeometry;
        if (readIf(105)) {
            int i = 0;
            if (this.currentTokenType != 2 || this.token.isQuoted()) {
                throw getSyntaxError();
            }
            if (!readIf("GEOMETRY")) {
                try {
                    i = EWKTUtils.parseGeometryType(this.currentToken);
                    read();
                    if (i / 1000 == 0 && this.currentTokenType == 2 && !this.token.isQuoted()) {
                        i += EWKTUtils.parseDimensionSystem(this.currentToken) * 1000;
                        read();
                    }
                } catch (IllegalArgumentException e) {
                    throw getSyntaxError();
                }
            }
            Integer num = null;
            if (readIf(Opcodes.LDIV)) {
                num = Integer.valueOf(readInt());
            }
            read(106);
            extTypeInfoGeometry = new ExtTypeInfoGeometry(i, num);
        } else {
            extTypeInfoGeometry = null;
        }
        return TypeInfo.getTypeInfo(37, -1L, -1, extTypeInfoGeometry);
    }

    private TypeInfo parseRowType() {
        read(105);
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        do {
            String readIdentifier = readIdentifier();
            if (linkedHashMap.putIfAbsent(readIdentifier, parseDataType()) != null) {
                throw DbException.get(ErrorCode.DUPLICATE_COLUMN_NAME_1, readIdentifier);
            }
        } while (readIfMore());
        return TypeInfo.getTypeInfo(41, -1L, -1, new ExtTypeInfoRow((LinkedHashMap<String, TypeInfo>) linkedHashMap));
    }

    private long readPrecision(int i) {
        long j;
        long readPositiveLong = readPositiveLong();
        if (this.currentTokenType != 2 || this.token.isQuoted()) {
            return readPositiveLong;
        }
        if ((i == 7 || i == 3) && this.currentToken.length() == 1) {
            switch (this.currentToken.charAt(0) & 65503) {
                case 71:
                    j = 1073741824;
                    break;
                case 72:
                case 73:
                case 74:
                case 76:
                case 78:
                case 79:
                case 81:
                case 82:
                case 83:
                default:
                    throw getSyntaxError();
                case 75:
                    j = 1024;
                    break;
                case 77:
                    j = 1048576;
                    break;
                case 80:
                    j = 1125899906842624L;
                    break;
                case 84:
                    j = 1099511627776L;
                    break;
            }
            if (readPositiveLong > Long.MAX_VALUE / j) {
                throw DbException.getInvalidValueException("precision", readPositiveLong + this.currentToken);
            }
            readPositiveLong *= j;
            read();
            if (this.currentTokenType != 2 || this.token.isQuoted()) {
                return readPositiveLong;
            }
        }
        switch (i) {
            case 1:
            case 2:
            case 3:
            case 4:
                if (!readIf("CHARACTERS") && !readIf("OCTETS") && this.database.getMode().charAndByteLengthUnits && !readIfCompat("CHAR")) {
                    readIfCompat("BYTE");
                    break;
                }
                break;
        }
        return readPositiveLong;
    }

    private Prepared parseCreate() {
        IndexColumn[] parseIndexColumnList;
        boolean z = false;
        if (readIf(61, "REPLACE")) {
            z = true;
        }
        boolean readIf = readIf("FORCE");
        if (readIf("VIEW")) {
            return parseCreateView(readIf, z);
        }
        if (readIf("MATERIALIZED")) {
            read("VIEW");
            return parseCreateMaterializedView(readIf, z);
        }
        if (readIf("ALIAS")) {
            return parseCreateFunctionAlias(readIf);
        }
        if (readIf("SEQUENCE")) {
            return parseCreateSequence();
        }
        if (readIf(82)) {
            return parseCreateUser();
        }
        if (readIf("TRIGGER")) {
            return parseCreateTrigger(readIf);
        }
        if (readIf("ROLE")) {
            return parseCreateRole();
        }
        if (readIf("SCHEMA")) {
            return parseCreateSchema();
        }
        if (readIf("CONSTANT")) {
            return parseCreateConstant();
        }
        if (readIf("DOMAIN") || readIf("TYPE") || readIfCompat("DATATYPE")) {
            return parseCreateDomain();
        }
        if (readIf("AGGREGATE")) {
            return parseCreateAggregate(readIf);
        }
        if (readIf("LINKED")) {
            return parseCreateLinkedTable(false, false, readIf);
        }
        boolean z2 = false;
        boolean z3 = false;
        if (readIf("MEMORY")) {
            z2 = true;
        } else if (readIf("CACHED")) {
            z3 = true;
        }
        if (readIf("LOCAL", "TEMPORARY")) {
            if (readIf("LINKED")) {
                return parseCreateLinkedTable(true, false, readIf);
            }
            read(75);
            return parseCreateTable(true, false, z3);
        }
        if (readIf("GLOBAL", "TEMPORARY")) {
            if (readIf("LINKED")) {
                return parseCreateLinkedTable(true, true, readIf);
            }
            read(75);
            return parseCreateTable(true, true, z3);
        }
        if (readIfCompat("TEMP") || readIf("TEMPORARY")) {
            if (readIf("LINKED")) {
                return parseCreateLinkedTable(true, true, readIf);
            }
            read(75);
            return parseCreateTable(true, true, z3);
        }
        if (readIf(75)) {
            if (!z3 && !z2) {
                z3 = this.database.getDefaultTableType() == 0;
            }
            return parseCreateTable(false, false, z3);
        }
        if (readIf("SYNONYM")) {
            return parseCreateSynonym(z);
        }
        boolean z4 = false;
        boolean z5 = false;
        NullsDistinct nullsDistinct = null;
        boolean z6 = false;
        String str = null;
        Schema schema = null;
        boolean z7 = false;
        if (this.session.isQuirksMode() && readIf(63, 47)) {
            if (readIf("HASH")) {
                z4 = true;
            }
            z5 = true;
            if (!isToken(60)) {
                z7 = readIfNotExists();
                str = readIdentifierWithSchema(null);
                schema = getSchema();
            }
        } else {
            if (readIf(80)) {
                nullsDistinct = readNullsDistinct(this.database.getMode().nullsDistinct);
            }
            if (readIfCompat("HASH")) {
                z4 = true;
            } else if (nullsDistinct == null && readIf("SPATIAL")) {
                z6 = true;
            }
            read("INDEX");
            if (!isToken(60)) {
                z7 = readIfNotExists();
                str = readIdentifierWithSchema(null);
                schema = getSchema();
            }
        }
        read(60);
        String readIdentifierWithSchema = readIdentifierWithSchema();
        checkSchema(schema);
        String readCommentIf = readCommentIf();
        if (!readIf(105)) {
            if (z4 || z6) {
                throw getSyntaxError();
            }
            readCompat(83);
            if (!readIf("BTREE")) {
                if (readIf("HASH")) {
                    z4 = true;
                } else {
                    read("RTREE");
                    z6 = true;
                }
            }
            read(105);
        }
        CreateIndex createIndex = new CreateIndex(this.session, getSchema());
        createIndex.setIfNotExists(z7);
        createIndex.setPrimaryKey(z5);
        createIndex.setTableName(readIdentifierWithSchema);
        createIndex.setHash(z4);
        createIndex.setSpatial(z6);
        createIndex.setIndexName(str);
        createIndex.setComment(readCommentIf);
        int i = 0;
        if (z6) {
            parseIndexColumnList = new IndexColumn[]{new IndexColumn(readIdentifier())};
            if (nullsDistinct != null) {
                i = 1;
            }
            read(106);
        } else {
            parseIndexColumnList = parseIndexColumnList();
            if (nullsDistinct != null) {
                i = parseIndexColumnList.length;
                if (readIf("INCLUDE")) {
                    read(105);
                    IndexColumn[] parseIndexColumnList2 = parseIndexColumnList();
                    int length = parseIndexColumnList2.length;
                    parseIndexColumnList = (IndexColumn[]) Arrays.copyOf(parseIndexColumnList, i + length);
                    System.arraycopy(parseIndexColumnList2, 0, parseIndexColumnList, i, length);
                }
            } else if (z5) {
                i = parseIndexColumnList.length;
            }
        }
        createIndex.setIndexColumns(parseIndexColumnList);
        createIndex.setUnique(nullsDistinct, i);
        return createIndex;
    }

    private NullsDistinct readNullsDistinct(NullsDistinct nullsDistinct) {
        if (readIf("NULLS")) {
            if (readIf(26)) {
                return NullsDistinct.DISTINCT;
            }
            if (readIf(57, 26)) {
                return NullsDistinct.NOT_DISTINCT;
            }
            if (readIf(3, 26)) {
                return NullsDistinct.ALL_DISTINCT;
            }
            throw getSyntaxError();
        }
        return nullsDistinct;
    }

    private boolean addRoleOrRight(GrantRevoke grantRevoke) {
        if (readIf(69)) {
            grantRevoke.addRight(1);
            return true;
        }
        if (readIf("DELETE")) {
            grantRevoke.addRight(2);
            return true;
        }
        if (readIf("INSERT")) {
            grantRevoke.addRight(4);
            return true;
        }
        if (readIf("UPDATE")) {
            grantRevoke.addRight(8);
            return true;
        }
        if (readIfCompat("CONNECT") || readIfCompat("RESOURCE")) {
            return true;
        }
        grantRevoke.addRoleName(readIdentifier());
        return false;
    }

    private GrantRevoke parseGrantRevoke(int i) {
        boolean addRoleOrRight;
        GrantRevoke grantRevoke = new GrantRevoke(this.session);
        grantRevoke.setOperationType(i);
        if (readIf(3)) {
            readIf("PRIVILEGES");
            grantRevoke.addRight(15);
            addRoleOrRight = true;
        } else if (readIf("ALTER")) {
            read(5);
            read("SCHEMA");
            grantRevoke.addRight(16);
            grantRevoke.addTable(null);
            addRoleOrRight = false;
        } else {
            addRoleOrRight = addRoleOrRight(grantRevoke);
            while (readIf(Opcodes.LDIV)) {
                if (addRoleOrRight(grantRevoke) != addRoleOrRight) {
                    throw DbException.get(ErrorCode.ROLES_AND_RIGHT_CANNOT_BE_MIXED);
                }
            }
        }
        if (addRoleOrRight && readIf(60)) {
            if (readIf("SCHEMA")) {
                grantRevoke.setSchema(this.database.getSchema(readIdentifier()));
            } else {
                readIf(75);
                do {
                    grantRevoke.addTable(readTableOrView());
                } while (readIf(Opcodes.LDIV));
            }
        }
        read(i == 49 ? 76 : 35);
        grantRevoke.setGranteeName(readIdentifier());
        return grantRevoke;
    }

    private TableValueConstructor parseValues() {
        ArrayList newSmallArrayList = Utils.newSmallArrayList();
        ArrayList<Expression> parseValuesRow = parseValuesRow(Utils.newSmallArrayList());
        newSmallArrayList.add(parseValuesRow);
        int size = parseValuesRow.size();
        while (readIf(Opcodes.LDIV)) {
            ArrayList<Expression> parseValuesRow2 = parseValuesRow(new ArrayList<>(size));
            if (parseValuesRow2.size() != size) {
                throw DbException.get(ErrorCode.COLUMN_COUNT_DOES_NOT_MATCH);
            }
            newSmallArrayList.add(parseValuesRow2);
        }
        return new TableValueConstructor(this.session, newSmallArrayList);
    }

    private ArrayList<Expression> parseValuesRow(ArrayList<Expression> arrayList) {
        if (!readIf(66, 105) && !readIf(105)) {
            arrayList.add(readExpression());
            return arrayList;
        }
        do {
            arrayList.add(readExpression());
        } while (readIfMore());
        return arrayList;
    }

    private Call parseCall() {
        Call call = new Call(this.session);
        this.currentPrepared = call;
        if (readIf(75, 105)) {
            call.setTableFunction(readTableFunction(1));
            return call;
        }
        int i = this.tokenIndex;
        boolean isIdentifier = isIdentifier();
        try {
            call.setExpression(readExpression());
            return call;
        } catch (DbException e) {
            if (isIdentifier && e.getErrorCode() == 90022) {
                setTokenIndex(i);
                String str = null;
                String readIdentifier = readIdentifier();
                if (readIf(Opcodes.FDIV)) {
                    str = readIdentifier;
                    readIdentifier = readIdentifier();
                    if (readIf(Opcodes.FDIV)) {
                        checkDatabaseName(str);
                        str = readIdentifier;
                        readIdentifier = readIdentifier();
                    }
                }
                read(105);
                call.setTableFunction(readTableFunction(readIdentifier, str != null ? this.database.getSchema(str) : null));
                return call;
            }
            throw e;
        }
    }

    private CreateRole parseCreateRole() {
        CreateRole createRole = new CreateRole(this.session);
        createRole.setIfNotExists(readIfNotExists());
        createRole.setRoleName(readIdentifier());
        return createRole;
    }

    private CreateSchema parseCreateSchema() {
        String name;
        CreateSchema createSchema = new CreateSchema(this.session);
        createSchema.setIfNotExists(readIfNotExists());
        if (readIf(9)) {
            name = readIdentifier();
            createSchema.setSchemaName(name);
            createSchema.setAuthorization(name);
        } else {
            createSchema.setSchemaName(readIdentifier());
            if (readIf(9)) {
                name = readIdentifier();
            } else {
                name = this.session.getUser().getName();
            }
        }
        createSchema.setAuthorization(name);
        if (readIf(89)) {
            createSchema.setTableEngineParams(readTableEngineParams());
        }
        return createSchema;
    }

    private ArrayList<String> readTableEngineParams() {
        ArrayList<String> newSmallArrayList = Utils.newSmallArrayList();
        do {
            newSmallArrayList.add(readIdentifier());
        } while (readIf(Opcodes.LDIV));
        return newSmallArrayList;
    }

    private CreateSequence parseCreateSequence() {
        boolean readIfNotExists = readIfNotExists();
        String readIdentifierWithSchema = readIdentifierWithSchema();
        CreateSequence createSequence = new CreateSequence(this.session, getSchema());
        createSequence.setIfNotExists(readIfNotExists);
        createSequence.setSequenceName(readIdentifierWithSchema);
        SequenceOptions sequenceOptions = new SequenceOptions();
        parseSequenceOptions(sequenceOptions, createSequence, true, false);
        createSequence.setOptions(sequenceOptions);
        return createSequence;
    }

    private boolean readIfNotExists() {
        if (readIf(40, 57, 30)) {
            return true;
        }
        return false;
    }

    private CreateConstant parseCreateConstant() {
        boolean readIfNotExists = readIfNotExists();
        String readIdentifierWithSchema = readIdentifierWithSchema();
        Schema schema = getSchema();
        if (isKeyword(readIdentifierWithSchema)) {
            throw DbException.get(ErrorCode.CONSTANT_ALREADY_EXISTS_1, readIdentifierWithSchema);
        }
        read(84);
        Expression readExpression = readExpression();
        CreateConstant createConstant = new CreateConstant(this.session, schema);
        createConstant.setConstantName(readIdentifierWithSchema);
        createConstant.setExpression(readExpression);
        createConstant.setIfNotExists(readIfNotExists);
        return createConstant;
    }

    private CreateAggregate parseCreateAggregate(boolean z) {
        boolean readIfNotExists = readIfNotExists();
        String readIdentifierWithSchema = readIdentifierWithSchema();
        if (!isKeyword(readIdentifierWithSchema)) {
            Database database = this.database;
            String upperName = upperName(readIdentifierWithSchema);
            if (!BuiltinFunctions.isBuiltinFunction(database, upperName) && Aggregate.getAggregateType(upperName) == null) {
                CreateAggregate createAggregate = new CreateAggregate(this.session, getSchema());
                createAggregate.setForce(z);
                createAggregate.setName(readIdentifierWithSchema);
                createAggregate.setIfNotExists(readIfNotExists);
                read(33);
                createAggregate.setJavaClassMethod(readStringOrIdentifier());
                return createAggregate;
            }
        }
        throw DbException.get(ErrorCode.FUNCTION_ALIAS_ALREADY_EXISTS_1, readIdentifierWithSchema);
    }

    private CreateDomain parseCreateDomain() {
        String str;
        boolean readIfNotExists = readIfNotExists();
        String readIdentifierWithSchema = readIdentifierWithSchema();
        Schema schema = getSchema();
        CreateDomain createDomain = new CreateDomain(this.session, schema);
        createDomain.setIfNotExists(readIfNotExists);
        createDomain.setTypeName(readIdentifierWithSchema);
        readIf(7);
        TypeInfo readIfDataType = readIfDataType();
        if (readIfDataType != null) {
            createDomain.setDataType(readIfDataType);
        } else {
            createDomain.setParentDomain(getSchema().getDomain(readIdentifierWithSchema()));
        }
        if (readIf(25)) {
            createDomain.setDefaultExpression(readExpression());
        }
        if (readIf(60, "UPDATE")) {
            createDomain.setOnUpdateExpression(readExpression());
        }
        if (readIfCompat("SELECTIVITY")) {
            readNonNegativeInt();
        }
        String readCommentIf = readCommentIf();
        if (readCommentIf != null) {
            createDomain.setComment(readCommentIf);
        }
        while (true) {
            if (readIf(14)) {
                str = readIdentifier();
                read(13);
            } else if (readIf(13)) {
                str = null;
            } else {
                return createDomain;
            }
            AlterDomainAddConstraint alterDomainAddConstraint = new AlterDomainAddConstraint(this.session, schema, readIfNotExists);
            alterDomainAddConstraint.setConstraintName(str);
            alterDomainAddConstraint.setDomainName(readIdentifierWithSchema);
            this.parseDomainConstraint = true;
            try {
                alterDomainAddConstraint.setCheckExpression(readExpression());
                this.parseDomainConstraint = false;
                createDomain.addConstraintCommand(alterDomainAddConstraint);
            } catch (Throwable th) {
                this.parseDomainConstraint = false;
                throw th;
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:19:0x00d8, code lost:            read(60);        r0 = readIdentifierWithSchema();        checkSchema(r0);        r0 = new org.h2.command.ddl.CreateTrigger(r5.session, getSchema());        r0.setForce(r6);        r0.setTriggerName(r0);        r0.setIfNotExists(r0);        r0.setInsteadOf(r10);        r0.setBefore(r11);        r0.setOnRollback(r13);        r0.setTypeMask(r12);        r0.setTableName(r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0139, code lost:            if (readIf(33, "EACH") == false) goto L43;     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x0142, code lost:            if (readIf(66) == false) goto L42;     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0145, code lost:            r0.setRowBased(true);     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x014e, code lost:            read("STATEMENT");     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x015c, code lost:            if (readIf("QUEUE") == false) goto L46;     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x015f, code lost:            r0.setQueueSize(readNonNegativeInt());     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0168, code lost:            r0.setNoWait(readIf("NOWAIT"));     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x017a, code lost:            if (readIf(7) == false) goto L49;     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x017d, code lost:            r0.setTriggerSource(readString());     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x019a, code lost:            return r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x0189, code lost:            read("CALL");        r0.setTriggerClassName(readStringOrIdentifier());     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private org.h2.command.ddl.CreateTrigger parseCreateTrigger(boolean r6) {
        /*
            Method dump skipped, instructions count: 411
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.Parser.parseCreateTrigger(boolean):org.h2.command.ddl.CreateTrigger");
    }

    private CreateUser parseCreateUser() {
        CreateUser createUser = new CreateUser(this.session);
        createUser.setIfNotExists(readIfNotExists());
        createUser.setUserName(readIdentifier());
        createUser.setComment(readCommentIf());
        if (readIf("PASSWORD")) {
            createUser.setPassword(readExpression());
        } else if (readIf("SALT")) {
            createUser.setSalt(readExpression());
            read("HASH");
            createUser.setHash(readExpression());
        } else if (readIf("IDENTIFIED")) {
            read("BY");
            createUser.setPassword(ValueExpression.get(ValueVarchar.get(readIdentifier())));
        } else {
            throw getSyntaxError();
        }
        if (readIf("ADMIN")) {
            createUser.setAdmin(true);
        }
        return createUser;
    }

    private CreateFunctionAlias parseCreateFunctionAlias(boolean z) {
        String str;
        boolean readIfNotExists = readIfNotExists();
        if (this.currentTokenType == 2) {
            str = readIdentifierWithSchema();
        } else if (isKeyword(this.currentTokenType)) {
            str = this.currentToken;
            read();
            this.schemaName = this.session.getCurrentSchemaName();
        } else {
            addExpected("identifier");
            throw getSyntaxError();
        }
        if (isReservedFunctionName(upperName(str))) {
            throw DbException.get(ErrorCode.FUNCTION_ALIAS_ALREADY_EXISTS_1, str);
        }
        CreateFunctionAlias createFunctionAlias = new CreateFunctionAlias(this.session, getSchema());
        createFunctionAlias.setForce(z);
        createFunctionAlias.setAliasName(str);
        createFunctionAlias.setIfNotExists(readIfNotExists);
        createFunctionAlias.setDeterministic(readIf("DETERMINISTIC"));
        readIfCompat("NOBUFFER");
        if (readIf(7)) {
            createFunctionAlias.setSource(readString());
        } else {
            read(33);
            createFunctionAlias.setJavaClassMethod(readStringOrIdentifier());
        }
        return createFunctionAlias;
    }

    private String readStringOrIdentifier() {
        return this.currentTokenType != 2 ? readString() : readIdentifier();
    }

    private boolean isReservedFunctionName(String str) {
        int tokenType = ParserUtil.getTokenType(str, false, false);
        if (tokenType == 2) {
            return Aggregate.getAggregateType(str) != null || (BuiltinFunctions.isBuiltinFunction(this.database, str) && !this.database.isAllowBuiltinAliasOverride());
        }
        if (this.database.isAllowBuiltinAliasOverride()) {
            switch (tokenType) {
                case 17:
                case 21:
                case 22:
                case 24:
                case 39:
                case 51:
                case 52:
                case 54:
                case 55:
                case 68:
                case 90:
                    return false;
                default:
                    return true;
            }
        }
        return true;
    }

    private Prepared parseWith() {
        ArrayList arrayList = new ArrayList();
        try {
            return parseWith1(arrayList);
        } catch (Throwable th) {
            CommandContainer.clearCTE(this.session, arrayList);
            throw th;
        }
    }

    private Prepared parseWith1(List<TableView> list) {
        Prepared parseCreate;
        readIf("RECURSIVE");
        boolean z = !this.session.isParsingCreateView();
        do {
            list.add(parseSingleCommonTableExpression(z));
        } while (readIf(Opcodes.LDIV));
        Collections.reverse(list);
        int i = this.tokenIndex;
        if (isQueryQuick()) {
            parseCreate = parseWithQuery();
        } else if (readIfCompat("INSERT")) {
            parseCreate = parseInsert(i);
            parseCreate.setPrepareAlways(true);
        } else if (readIfCompat("UPDATE")) {
            parseCreate = parseUpdate(i);
            parseCreate.setPrepareAlways(true);
        } else if (readIfCompat("MERGE")) {
            parseCreate = parseMerge(i);
            parseCreate.setPrepareAlways(true);
        } else if (readIfCompat("DELETE")) {
            parseCreate = parseDelete(i);
            parseCreate.setPrepareAlways(true);
        } else if (readIfCompat("CREATE")) {
            if (!isToken(75)) {
                throw DbException.get(ErrorCode.SYNTAX_ERROR_1, WITH_STATEMENT_SUPPORTS_LIMITED_SUB_STATEMENTS);
            }
            parseCreate = parseCreate();
            parseCreate.setPrepareAlways(true);
        } else {
            throw DbException.get(ErrorCode.SYNTAX_ERROR_1, WITH_STATEMENT_SUPPORTS_LIMITED_SUB_STATEMENTS);
        }
        if (z) {
            if (this.cteCleanups == null) {
                this.cteCleanups = new ArrayList(list.size());
            }
            this.cteCleanups.addAll(list);
        }
        return parseCreate;
    }

    private Prepared parseWithQuery() {
        Query parseQueryExpressionBodyAndEndOfQuery = parseQueryExpressionBodyAndEndOfQuery();
        parseQueryExpressionBodyAndEndOfQuery.setPrepareAlways(true);
        parseQueryExpressionBodyAndEndOfQuery.setNeverLazy(true);
        return parseQueryExpressionBodyAndEndOfQuery;
    }

    private TableView parseSingleCommonTableExpression(boolean z) {
        Table findLocalTempTable;
        String readIdentifierWithSchema = readIdentifierWithSchema();
        Schema schema = getSchema();
        ArrayList newSmallArrayList = Utils.newSmallArrayList();
        String[] strArr = null;
        if (readIf(105)) {
            strArr = parseColumnList();
            for (String str : strArr) {
                newSmallArrayList.add(new Column(str, TypeInfo.TYPE_VARCHAR));
            }
        }
        if (!z) {
            findLocalTempTable = getSchema().findTableOrView(this.session, readIdentifierWithSchema);
        } else {
            findLocalTempTable = this.session.findLocalTempTable(readIdentifierWithSchema);
        }
        if (findLocalTempTable != null) {
            if (!(findLocalTempTable instanceof TableView)) {
                throw DbException.get(ErrorCode.TABLE_OR_VIEW_ALREADY_EXISTS_1, readIdentifierWithSchema);
            }
            if (!((TableView) findLocalTempTable).isTableExpression()) {
                throw DbException.get(ErrorCode.TABLE_OR_VIEW_ALREADY_EXISTS_1, readIdentifierWithSchema);
            }
            if (!z) {
                findLocalTempTable.lock(this.session, 2);
                this.database.removeSchemaObject(this.session, findLocalTempTable);
            } else {
                this.session.removeLocalTempTable(findLocalTempTable);
            }
        }
        Table createShadowTableForRecursiveTableExpression = TableView.createShadowTableForRecursiveTableExpression(z, this.session, readIdentifierWithSchema, schema, newSmallArrayList, this.database);
        String[] strArr2 = new String[1];
        BitSet openParametersScope = openParametersScope();
        try {
            read(7);
            read(105);
            Query parseQuery = parseQuery();
            if (!z) {
                parseQuery.session = this.session;
            }
            read(106);
            List<Column> createQueryColumnTemplateList = QueryExpressionTable.createQueryColumnTemplateList(strArr, parseQuery, strArr2);
            ArrayList<Parameter> closeParametersScope = closeParametersScope(openParametersScope);
            TableView.destroyShadowTableForRecursiveExpression(z, this.session, createShadowTableForRecursiveTableExpression);
            return createCTEView(readIdentifierWithSchema, strArr2[0], closeParametersScope, createQueryColumnTemplateList, z);
        } catch (Throwable th) {
            closeParametersScope(openParametersScope);
            TableView.destroyShadowTableForRecursiveExpression(z, this.session, createShadowTableForRecursiveTableExpression);
            throw th;
        }
    }

    private TableView createCTEView(String str, String str2, ArrayList<Parameter> arrayList, List<Column> list, boolean z) {
        Schema schemaWithDefault = getSchemaWithDefault();
        int allocateObjectId = this.database.allocateObjectId();
        Column[] columnArr = (Column[]) list.toArray(new Column[0]);
        this.session.lock();
        try {
            TableView tableView = new TableView(schemaWithDefault, allocateObjectId, str, str2, arrayList, columnArr, this.session, true, false, true, z);
            if (!tableView.isRecursiveQueryDetected()) {
                if (!z) {
                    this.database.addSchemaObject(this.session, tableView);
                    tableView.lock(this.session, 2);
                    this.database.removeSchemaObject(this.session, tableView);
                } else {
                    this.session.addLocalTempTable(tableView);
                    this.session.removeLocalTempTable(tableView);
                }
                tableView = new TableView(schemaWithDefault, this.database.allocateObjectId(), str, str2, arrayList, columnArr, this.session, false, false, true, z);
            }
            this.database.unlockMeta(this.session);
            this.session.unlock();
            tableView.setTableExpression(true);
            tableView.setTemporary(z);
            tableView.setHidden(true);
            tableView.setOnCommitDrop(false);
            if (!z) {
                this.database.addSchemaObject(this.session, tableView);
                tableView.unlock(this.session);
                this.database.unlockMeta(this.session);
            } else {
                this.session.addLocalTempTable(tableView);
            }
            return tableView;
        } catch (Throwable th) {
            this.session.unlock();
            throw th;
        }
    }

    private CreateView parseCreateView(boolean z, boolean z2) {
        boolean readIfNotExists = readIfNotExists();
        boolean readIf = readIf("TABLE_EXPRESSION");
        String readIdentifierWithSchema = readIdentifierWithSchema();
        CreateView createView = new CreateView(this.session, getSchema());
        this.createView = createView;
        createView.setViewName(readIdentifierWithSchema);
        createView.setIfNotExists(readIfNotExists);
        createView.setComment(readCommentIf());
        createView.setOrReplace(z2);
        createView.setForce(z);
        createView.setTableExpression(readIf);
        if (readIf(105)) {
            createView.setColumnNames(parseColumnList());
        }
        read(7);
        String cache = StringUtils.cache(this.sqlCommand.substring(this.token.start()));
        try {
            this.session.setParsingCreateView(true);
            try {
                Query parseQuery = parseQuery();
                parseQuery.prepare();
                this.session.setParsingCreateView(false);
                createView.setSelect(parseQuery);
            } catch (Throwable th) {
                this.session.setParsingCreateView(false);
                throw th;
            }
        } catch (DbException e) {
            if (z) {
                createView.setSelectSQL(cache);
                while (this.currentTokenType != 93) {
                    read();
                }
            } else {
                throw e;
            }
        }
        return createView;
    }

    private CreateMaterializedView parseCreateMaterializedView(boolean z, boolean z2) {
        boolean readIfNotExists = readIfNotExists();
        String readIdentifierWithSchema = readIdentifierWithSchema();
        read(7);
        CreateMaterializedView createMaterializedView = new CreateMaterializedView(this.session, getSchema());
        createMaterializedView.setViewName(readIdentifierWithSchema);
        createMaterializedView.setIfNotExists(readIfNotExists);
        createMaterializedView.setComment(readCommentIf());
        createMaterializedView.setOrReplace(z2);
        if (z) {
            throw new UnsupportedOperationException("not yet implemented");
        }
        String cache = StringUtils.cache(this.sqlCommand.substring(this.token.start()));
        this.session.setParsingCreateView(true);
        try {
            Query parseQuery = parseQuery();
            this.session.setParsingCreateView(false);
            createMaterializedView.setSelect(parseQuery);
            createMaterializedView.setSelectSQL(cache);
            return createMaterializedView;
        } catch (Throwable th) {
            this.session.setParsingCreateView(false);
            throw th;
        }
    }

    private TransactionCommand parseCheckpoint() {
        TransactionCommand transactionCommand;
        if (readIf("SYNC")) {
            transactionCommand = new TransactionCommand(this.session, 76);
        } else {
            transactionCommand = new TransactionCommand(this.session, 73);
        }
        return transactionCommand;
    }

    private Prepared parseAlter() {
        if (readIf(75)) {
            return parseAlterTable();
        }
        if (readIf(82)) {
            return parseAlterUser();
        }
        if (readIf("INDEX")) {
            return parseAlterIndex();
        }
        if (readIf("SCHEMA")) {
            return parseAlterSchema();
        }
        if (readIf("SEQUENCE")) {
            return parseAlterSequence();
        }
        if (readIf("VIEW")) {
            return parseAlterView();
        }
        if (readIf("DOMAIN")) {
            return parseAlterDomain();
        }
        throw getSyntaxError();
    }

    private void checkSchema(Schema schema) {
        if (schema != null && getSchema() != schema) {
            throw DbException.get(ErrorCode.SCHEMA_NAME_MUST_MATCH);
        }
    }

    private AlterIndexRename parseAlterIndex() {
        boolean readIfExists = readIfExists(false);
        String readIdentifierWithSchema = readIdentifierWithSchema();
        Schema schema = getSchema();
        AlterIndexRename alterIndexRename = new AlterIndexRename(this.session);
        alterIndexRename.setOldSchema(schema);
        alterIndexRename.setOldName(readIdentifierWithSchema);
        alterIndexRename.setIfExists(readIfExists);
        read("RENAME");
        read(76);
        String readIdentifierWithSchema2 = readIdentifierWithSchema(schema.getName());
        checkSchema(schema);
        alterIndexRename.setNewName(readIdentifierWithSchema2);
        return alterIndexRename;
    }

    private DefineCommand parseAlterDomain() {
        boolean readIfExists = readIfExists(false);
        String readIdentifierWithSchema = readIdentifierWithSchema();
        Schema schema = getSchema();
        if (readIf("ADD")) {
            boolean z = false;
            String str = null;
            String str2 = null;
            if (readIf(14)) {
                z = readIfNotExists();
                str = readIdentifierWithSchema(schema.getName());
                checkSchema(schema);
                str2 = readCommentIf();
            }
            read(13);
            AlterDomainAddConstraint alterDomainAddConstraint = new AlterDomainAddConstraint(this.session, schema, z);
            alterDomainAddConstraint.setDomainName(readIdentifierWithSchema);
            alterDomainAddConstraint.setConstraintName(str);
            this.parseDomainConstraint = true;
            try {
                alterDomainAddConstraint.setCheckExpression(readExpression());
                this.parseDomainConstraint = false;
                alterDomainAddConstraint.setIfDomainExists(readIfExists);
                alterDomainAddConstraint.setComment(str2);
                if (readIf("NOCHECK")) {
                    alterDomainAddConstraint.setCheckExisting(false);
                } else {
                    readIf(13);
                    alterDomainAddConstraint.setCheckExisting(true);
                }
                return alterDomainAddConstraint;
            } catch (Throwable th) {
                this.parseDomainConstraint = false;
                throw th;
            }
        }
        if (readIf("DROP")) {
            if (readIf(14)) {
                boolean readIfExists2 = readIfExists(false);
                String readIdentifierWithSchema2 = readIdentifierWithSchema(schema.getName());
                checkSchema(schema);
                AlterDomainDropConstraint alterDomainDropConstraint = new AlterDomainDropConstraint(this.session, getSchema(), readIfExists2);
                alterDomainDropConstraint.setConstraintName(readIdentifierWithSchema2);
                alterDomainDropConstraint.setDomainName(readIdentifierWithSchema);
                alterDomainDropConstraint.setIfDomainExists(readIfExists);
                return alterDomainDropConstraint;
            }
            if (readIf(25)) {
                AlterDomainExpressions alterDomainExpressions = new AlterDomainExpressions(this.session, schema, 94);
                alterDomainExpressions.setDomainName(readIdentifierWithSchema);
                alterDomainExpressions.setIfDomainExists(readIfExists);
                alterDomainExpressions.setExpression(null);
                return alterDomainExpressions;
            }
            if (readIf(60, "UPDATE")) {
                AlterDomainExpressions alterDomainExpressions2 = new AlterDomainExpressions(this.session, schema, 95);
                alterDomainExpressions2.setDomainName(readIdentifierWithSchema);
                alterDomainExpressions2.setIfDomainExists(readIfExists);
                alterDomainExpressions2.setExpression(null);
                return alterDomainExpressions2;
            }
        } else {
            if (readIf("RENAME")) {
                if (readIf(14)) {
                    String readIdentifierWithSchema3 = readIdentifierWithSchema(schema.getName());
                    checkSchema(schema);
                    read(76);
                    AlterDomainRenameConstraint alterDomainRenameConstraint = new AlterDomainRenameConstraint(this.session, schema);
                    alterDomainRenameConstraint.setDomainName(readIdentifierWithSchema);
                    alterDomainRenameConstraint.setIfDomainExists(readIfExists);
                    alterDomainRenameConstraint.setConstraintName(readIdentifierWithSchema3);
                    alterDomainRenameConstraint.setNewConstraintName(readIdentifier());
                    return alterDomainRenameConstraint;
                }
                read(76);
                String readIdentifierWithSchema4 = readIdentifierWithSchema(schema.getName());
                checkSchema(schema);
                AlterDomainRename alterDomainRename = new AlterDomainRename(this.session, getSchema());
                alterDomainRename.setDomainName(readIdentifierWithSchema);
                alterDomainRename.setIfDomainExists(readIfExists);
                alterDomainRename.setNewDomainName(readIdentifierWithSchema4);
                return alterDomainRename;
            }
            read(71);
            if (readIf(25)) {
                AlterDomainExpressions alterDomainExpressions3 = new AlterDomainExpressions(this.session, schema, 94);
                alterDomainExpressions3.setDomainName(readIdentifierWithSchema);
                alterDomainExpressions3.setIfDomainExists(readIfExists);
                alterDomainExpressions3.setExpression(readExpression());
                return alterDomainExpressions3;
            }
            if (readIf(60, "UPDATE")) {
                AlterDomainExpressions alterDomainExpressions4 = new AlterDomainExpressions(this.session, schema, 95);
                alterDomainExpressions4.setDomainName(readIdentifierWithSchema);
                alterDomainExpressions4.setIfDomainExists(readIfExists);
                alterDomainExpressions4.setExpression(readExpression());
                return alterDomainExpressions4;
            }
        }
        throw getSyntaxError();
    }

    private DefineCommand parseAlterView() {
        boolean readIfExists = readIfExists(false);
        String readIdentifierWithSchema = readIdentifierWithSchema();
        Schema schema = getSchema();
        Table findTableOrView = schema.findTableOrView(this.session, readIdentifierWithSchema);
        if (!(findTableOrView instanceof TableView) && !readIfExists) {
            throw DbException.get(ErrorCode.VIEW_NOT_FOUND_1, readIdentifierWithSchema);
        }
        if (readIf("RENAME", 76)) {
            String readIdentifierWithSchema2 = readIdentifierWithSchema(schema.getName());
            checkSchema(schema);
            AlterTableRename alterTableRename = new AlterTableRename(this.session, getSchema());
            alterTableRename.setTableName(readIdentifierWithSchema);
            alterTableRename.setNewTableName(readIdentifierWithSchema2);
            alterTableRename.setIfTableExists(readIfExists);
            return alterTableRename;
        }
        read("RECOMPILE");
        TableView tableView = (TableView) findTableOrView;
        AlterView alterView = new AlterView(this.session);
        alterView.setIfExists(readIfExists);
        alterView.setView(tableView);
        return alterView;
    }

    private Prepared parseAlterSchema() {
        boolean readIfExists = readIfExists(false);
        String readIdentifierWithSchema = readIdentifierWithSchema();
        Schema schema = getSchema();
        read("RENAME");
        read(76);
        String readIdentifierWithSchema2 = readIdentifierWithSchema(schema.getName());
        Schema findSchema = findSchema(readIdentifierWithSchema);
        if (findSchema == null) {
            if (readIfExists) {
                return new NoOperation(this.session);
            }
            throw DbException.get(ErrorCode.SCHEMA_NOT_FOUND_1, readIdentifierWithSchema);
        }
        AlterSchemaRename alterSchemaRename = new AlterSchemaRename(this.session);
        alterSchemaRename.setOldSchema(findSchema);
        checkSchema(schema);
        alterSchemaRename.setNewName(readIdentifierWithSchema2);
        return alterSchemaRename;
    }

    private AlterSequence parseAlterSequence() {
        boolean readIfExists = readIfExists(false);
        String readIdentifierWithSchema = readIdentifierWithSchema();
        AlterSequence alterSequence = new AlterSequence(this.session, getSchema());
        alterSequence.setSequenceName(readIdentifierWithSchema);
        alterSequence.setIfExists(readIfExists);
        SequenceOptions sequenceOptions = new SequenceOptions();
        parseSequenceOptions(sequenceOptions, null, false, false);
        alterSequence.setOptions(sequenceOptions);
        return alterSequence;
    }

    private boolean parseSequenceOptions(SequenceOptions sequenceOptions, CreateSequence createSequence, boolean z, boolean z2) {
        boolean z3;
        boolean z4 = false;
        while (true) {
            z3 = z4;
            if (z && readIf(7)) {
                TypeInfo parseDataType = parseDataType();
                if (!DataType.isNumericType(parseDataType.getValueType())) {
                    throw DbException.getUnsupportedException(parseDataType.getSQL(new StringBuilder("CREATE SEQUENCE AS "), 3).toString());
                }
                sequenceOptions.setDataType(parseDataType);
            } else if (readIf("START", 89) || (this.database.getMode().getEnum() == Mode.ModeEnum.PostgreSQL && readIfCompat("START"))) {
                sequenceOptions.setStartValue(readExpression());
            } else if (readIf("RESTART")) {
                sequenceOptions.setRestartValue(readIf(89) ? readExpression() : ValueExpression.DEFAULT);
            } else if (createSequence == null || !parseCreateSequenceOption(createSequence)) {
                if (z2) {
                    int i = this.tokenIndex;
                    if (!readIf(71)) {
                        break;
                    }
                    if (!parseBasicSequenceOption(sequenceOptions)) {
                        setTokenIndex(i);
                        break;
                    }
                } else if (!parseBasicSequenceOption(sequenceOptions)) {
                    break;
                }
            }
            z4 = true;
        }
        return z3;
    }

    private boolean parseCreateSequenceOption(CreateSequence createSequence) {
        if (readIf("BELONGS_TO_TABLE")) {
            createSequence.setBelongsToTable(true);
            return true;
        }
        if (!readIfCompat(62)) {
            return false;
        }
        return true;
    }

    private boolean parseBasicSequenceOption(SequenceOptions sequenceOptions) {
        if (readIf("INCREMENT")) {
            readIf("BY");
            sequenceOptions.setIncrement(readExpression());
            return true;
        }
        if (readIf("MINVALUE")) {
            sequenceOptions.setMinValue(readExpression());
            return true;
        }
        if (readIf("MAXVALUE")) {
            sequenceOptions.setMaxValue(readExpression());
            return true;
        }
        if (readIf("CYCLE")) {
            sequenceOptions.setCycle(Sequence.Cycle.CYCLE);
            return true;
        }
        if (readIf("NO")) {
            if (readIf("MINVALUE")) {
                sequenceOptions.setMinValue(ValueExpression.NULL);
                return true;
            }
            if (readIf("MAXVALUE")) {
                sequenceOptions.setMaxValue(ValueExpression.NULL);
                return true;
            }
            if (readIf("CYCLE")) {
                sequenceOptions.setCycle(Sequence.Cycle.NO_CYCLE);
                return true;
            }
            if (readIf("CACHE")) {
                sequenceOptions.setCacheSize(ValueExpression.get(ValueBigint.get(1L)));
                return true;
            }
            throw getSyntaxError();
        }
        if (readIf("EXHAUSTED")) {
            sequenceOptions.setCycle(Sequence.Cycle.EXHAUSTED);
            return true;
        }
        if (readIf("CACHE")) {
            sequenceOptions.setCacheSize(readExpression());
            return true;
        }
        if (readIfCompat("NOMINVALUE")) {
            sequenceOptions.setMinValue(ValueExpression.NULL);
            return true;
        }
        if (readIfCompat("NOMAXVALUE")) {
            sequenceOptions.setMaxValue(ValueExpression.NULL);
            return true;
        }
        if (readIfCompat("NOCYCLE")) {
            sequenceOptions.setCycle(Sequence.Cycle.NO_CYCLE);
            return true;
        }
        if (readIfCompat("NOCACHE")) {
            sequenceOptions.setCacheSize(ValueExpression.get(ValueBigint.get(1L)));
            return true;
        }
        return false;
    }

    private AlterUser parseAlterUser() {
        String readIdentifier = readIdentifier();
        if (readIf(71)) {
            AlterUser alterUser = new AlterUser(this.session);
            alterUser.setType(19);
            alterUser.setUser(this.database.getUser(readIdentifier));
            if (readIf("PASSWORD")) {
                alterUser.setPassword(readExpression());
            } else if (readIf("SALT")) {
                alterUser.setSalt(readExpression());
                read("HASH");
                alterUser.setHash(readExpression());
            } else {
                throw getSyntaxError();
            }
            return alterUser;
        }
        if (readIf("RENAME", 76)) {
            AlterUser alterUser2 = new AlterUser(this.session);
            alterUser2.setType(18);
            alterUser2.setUser(this.database.getUser(readIdentifier));
            alterUser2.setNewName(readIdentifier());
            return alterUser2;
        }
        if (readIf("ADMIN")) {
            AlterUser alterUser3 = new AlterUser(this.session);
            alterUser3.setType(17);
            alterUser3.setUser(this.database.getUser(readIdentifier));
            if (readIf(77)) {
                alterUser3.setAdmin(true);
            } else if (readIf(31)) {
                alterUser3.setAdmin(false);
            } else {
                throw getSyntaxError();
            }
            return alterUser3;
        }
        throw getSyntaxError();
    }

    private void readIfEqualOrTo() {
        if (!readIf(95)) {
            readIf(76);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:128:0x03ce, code lost:            if (r5.currentTokenType != 115) goto L127;     */
    /* JADX WARN: Code restructure failed: missing block: B:129:0x03d1, code lost:            r0.add(org.h2.util.StringUtils.toUpperEnglish(readIdentifierOrKeyword()));     */
    /* JADX WARN: Code restructure failed: missing block: B:130:0x03e3, code lost:            if (readIf(org.springframework.asm.Opcodes.LDIV) != false) goto L172;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private org.h2.command.Prepared parseSet() {
        /*
            Method dump skipped, instructions count: 1322
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.Parser.parseSet():org.h2.command.Prepared");
    }

    private Prepared parseSetTransactionMode() {
        IsolationLevel isolationLevel;
        read("ISOLATION");
        read("LEVEL");
        if (readIf("READ")) {
            if (readIf("UNCOMMITTED")) {
                isolationLevel = IsolationLevel.READ_UNCOMMITTED;
            } else {
                read("COMMITTED");
                isolationLevel = IsolationLevel.READ_COMMITTED;
            }
        } else if (readIf("REPEATABLE")) {
            read("READ");
            isolationLevel = IsolationLevel.REPEATABLE_READ;
        } else if (readIf("SNAPSHOT")) {
            isolationLevel = IsolationLevel.SNAPSHOT;
        } else {
            read("SERIALIZABLE");
            isolationLevel = IsolationLevel.SERIALIZABLE;
        }
        return new SetSessionCharacteristics(this.session, isolationLevel);
    }

    private Expression readExpressionOrIdentifier() {
        if (isIdentifier()) {
            return ValueExpression.get(ValueVarchar.get(readIdentifier()));
        }
        return readExpression();
    }

    private Prepared parseUse() {
        readIfEqualOrTo();
        Set set = new Set(this.session, 22);
        set.setExpression(ValueExpression.get(ValueVarchar.get(readIdentifier())));
        return set;
    }

    private Set parseSetCollation() {
        Set set = new Set(this.session, 11);
        String readIdentifier = readIdentifier();
        set.setString(readIdentifier);
        if (equalsToken(readIdentifier, CompareMode.OFF)) {
            return set;
        }
        Collator collator = CompareMode.getCollator(readIdentifier);
        if (collator == null) {
            throw DbException.getInvalidValueException("collation", readIdentifier);
        }
        if (readIf("STRENGTH")) {
            if (readIf(63)) {
                set.setInt(0);
            } else if (readIf("SECONDARY")) {
                set.setInt(1);
            } else if (readIf("TERTIARY")) {
                set.setInt(2);
            } else if (readIf("IDENTICAL")) {
                set.setInt(3);
            }
        } else {
            set.setInt(collator.getStrength());
        }
        return set;
    }

    private Prepared readSetCompatibility(Mode.ModeEnum modeEnum) {
        switch (modeEnum) {
            case Derby:
                if (readIfCompat("CREATE")) {
                    readIfEqualOrTo();
                    read();
                    return new NoOperation(this.session);
                }
                return null;
            case HSQLDB:
                if (readIfCompat("LOGSIZE")) {
                    readIfEqualOrTo();
                    Set set = new Set(this.session, 1);
                    set.setExpression(readExpression());
                    return set;
                }
                return null;
            case MariaDB:
            case MySQL:
                if (readIfCompat("FOREIGN_KEY_CHECKS")) {
                    readIfEqualOrTo();
                    Set set2 = new Set(this.session, 25);
                    set2.setExpression(readExpression());
                    return set2;
                }
                if (readIfCompat("NAMES")) {
                    readIfEqualOrTo();
                    read();
                    return new NoOperation(this.session);
                }
                return null;
            case PostgreSQL:
                if (readIfCompat("STATEMENT_TIMEOUT")) {
                    readIfEqualOrTo();
                    Set set3 = new Set(this.session, 30);
                    set3.setInt(readNonNegativeInt());
                    return set3;
                }
                if (readIfCompat("CLIENT_ENCODING") || readIfCompat("CLIENT_MIN_MESSAGES") || readIfCompat("JOIN_COLLAPSE_LIMIT")) {
                    readIfEqualOrTo();
                    read();
                    return new NoOperation(this.session);
                }
                if (readIfCompat("DATESTYLE")) {
                    readIfEqualOrTo();
                    if (!readIf("ISO") && !equalsToken(readString(), "ISO")) {
                        throw getSyntaxError();
                    }
                    return new NoOperation(this.session);
                }
                if (readIfCompat("SEARCH_PATH")) {
                    readIfEqualOrTo();
                    Set set4 = new Set(this.session, 24);
                    ArrayList newSmallArrayList = Utils.newSmallArrayList();
                    String sysIdentifier = this.database.sysIdentifier(Constants.SCHEMA_PG_CATALOG);
                    boolean z = false;
                    do {
                        String readString = this.currentTokenType == 94 ? readString() : readIdentifier();
                        if (!"$user".equals(readString)) {
                            if (sysIdentifier.equals(readString)) {
                                z = true;
                            }
                            newSmallArrayList.add(readString);
                        }
                    } while (readIf(Opcodes.LDIV));
                    if (!z && this.database.findSchema(sysIdentifier) != null) {
                        newSmallArrayList.add(0, sysIdentifier);
                    }
                    set4.setStringArray((String[]) newSmallArrayList.toArray(new String[0]));
                    return set4;
                }
                return null;
            default:
                return null;
        }
    }

    private RunScriptCommand parseRunScript() {
        RunScriptCommand runScriptCommand = new RunScriptCommand(this.session);
        read(35);
        runScriptCommand.setFileNameExpr(readExpression());
        if (readIf("COMPRESSION")) {
            runScriptCommand.setCompressionAlgorithm(readIdentifier());
        }
        if (readIf("CIPHER")) {
            runScriptCommand.setCipher(readIdentifier());
            if (readIf("PASSWORD")) {
                runScriptCommand.setPassword(readExpression());
            }
        }
        if (readIf("CHARSET")) {
            runScriptCommand.setCharset(Charset.forName(readString()));
        }
        if (readIf("FROM_1X")) {
            runScriptCommand.setFrom1X();
        } else {
            if (readIf("QUIRKS_MODE")) {
                runScriptCommand.setQuirksMode(true);
            }
            if (readIf("VARIABLE_BINARY")) {
                runScriptCommand.setVariableBinary(true);
            }
        }
        return runScriptCommand;
    }

    private ScriptCommand parseScript() {
        ScriptCommand scriptCommand = new ScriptCommand(this.session);
        boolean z = true;
        boolean z2 = true;
        boolean z3 = true;
        boolean z4 = true;
        boolean z5 = false;
        boolean z6 = false;
        boolean z7 = false;
        if (readIf("NODATA")) {
            z = false;
        } else {
            if (readIf("SIMPLE")) {
                z6 = true;
            }
            if (readIf("COLUMNS")) {
                z7 = true;
            }
        }
        if (readIf("NOPASSWORDS")) {
            z2 = false;
        }
        if (readIf("NOSETTINGS")) {
            z3 = false;
        }
        if (readIf("NOVERSION")) {
            z4 = false;
        }
        if (readIf("DROP")) {
            z5 = true;
        }
        if (readIf("BLOCKSIZE")) {
            scriptCommand.setLobBlockSize(readLong());
        }
        scriptCommand.setData(z);
        scriptCommand.setPasswords(z2);
        scriptCommand.setSettings(z3);
        scriptCommand.setVersion(z4);
        scriptCommand.setDrop(z5);
        scriptCommand.setSimple(z6);
        scriptCommand.setWithColumns(z7);
        if (readIf(76)) {
            scriptCommand.setFileNameExpr(readExpression());
            if (readIf("COMPRESSION")) {
                scriptCommand.setCompressionAlgorithm(readIdentifier());
            }
            if (readIf("CIPHER")) {
                scriptCommand.setCipher(readIdentifier());
                if (readIf("PASSWORD")) {
                    scriptCommand.setPassword(readExpression());
                }
            }
            if (readIf("CHARSET")) {
                scriptCommand.setCharset(Charset.forName(readString()));
            }
        }
        if (readIf("SCHEMA")) {
            HashSet hashSet = new HashSet();
            do {
                hashSet.add(readIdentifier());
            } while (readIf(Opcodes.LDIV));
            scriptCommand.setSchemaNames(hashSet);
        } else if (readIf(75)) {
            ArrayList newSmallArrayList = Utils.newSmallArrayList();
            do {
                newSmallArrayList.add(readTableOrView());
            } while (readIf(Opcodes.LDIV));
            scriptCommand.setTables(newSmallArrayList);
        }
        return scriptCommand;
    }

    private boolean isDualTable(String str) {
        return ((this.schemaName == null || equalsToken(this.schemaName, "SYS")) && equalsToken(DualTable.NAME, str)) || (this.database.getMode().sysDummy1 && ((this.schemaName == null || equalsToken(this.schemaName, "SYSIBM")) && equalsToken("SYSDUMMY1", str)));
    }

    private Table readTableOrView() {
        return readTableOrView(readIdentifierWithSchema(null), true);
    }

    private Table readTableOrView(boolean z) {
        return readTableOrView(readIdentifierWithSchema(null), z);
    }

    private Table readTableOrView(String str, boolean z) {
        if (this.schemaName != null) {
            Table resolveTableOrView = getSchema().resolveTableOrView(this.session, str, z);
            if (resolveTableOrView != null) {
                return resolveTableOrView;
            }
        } else {
            Table resolveTableOrView2 = this.database.getSchema(this.session.getCurrentSchemaName()).resolveTableOrView(this.session, str, z);
            if (resolveTableOrView2 != null) {
                return resolveTableOrView2;
            }
            String[] schemaSearchPath = this.session.getSchemaSearchPath();
            if (schemaSearchPath != null) {
                for (String str2 : schemaSearchPath) {
                    Table resolveTableOrView3 = this.database.getSchema(str2).resolveTableOrView(this.session, str, z);
                    if (resolveTableOrView3 != null) {
                        return resolveTableOrView3;
                    }
                }
            }
        }
        if (isDualTable(str)) {
            return new DualTable(this.database);
        }
        throw getTableOrViewNotFoundDbException(str);
    }

    private DbException getTableOrViewNotFoundDbException(String str) {
        if (this.schemaName != null) {
            return getTableOrViewNotFoundDbException(this.schemaName, str);
        }
        String currentSchemaName = this.session.getCurrentSchemaName();
        String[] schemaSearchPath = this.session.getSchemaSearchPath();
        if (schemaSearchPath == null) {
            return getTableOrViewNotFoundDbException(Collections.singleton(currentSchemaName), str);
        }
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        linkedHashSet.add(currentSchemaName);
        linkedHashSet.addAll(Arrays.asList(schemaSearchPath));
        return getTableOrViewNotFoundDbException(linkedHashSet, str);
    }

    private DbException getTableOrViewNotFoundDbException(String str, String str2) {
        return getTableOrViewNotFoundDbException(Collections.singleton(str), str2);
    }

    private DbException getTableOrViewNotFoundDbException(java.util.Set<String> set, String str) {
        if (this.database == null || this.database.getFirstUserTable() == null) {
            return DbException.get(ErrorCode.TABLE_OR_VIEW_NOT_FOUND_DATABASE_EMPTY_1, str);
        }
        if (this.database.getSettings().caseInsensitiveIdentifiers) {
            return DbException.get(ErrorCode.TABLE_OR_VIEW_NOT_FOUND_1, str);
        }
        TreeSet treeSet = new TreeSet();
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            findTableNameCandidates(it.next(), str, treeSet);
        }
        if (treeSet.isEmpty()) {
            return DbException.get(ErrorCode.TABLE_OR_VIEW_NOT_FOUND_1, str);
        }
        return DbException.get(ErrorCode.TABLE_OR_VIEW_NOT_FOUND_WITH_CANDIDATES_2, str, String.join(", ", treeSet));
    }

    private void findTableNameCandidates(String str, String str2, java.util.Set<String> set) {
        Schema schema = this.database.getSchema(str);
        String upperEnglish = StringUtils.toUpperEnglish(str2);
        Iterator<Table> it = schema.getAllTablesAndViews(this.session).iterator();
        while (it.hasNext()) {
            String name = it.next().getName();
            if (upperEnglish.equals(StringUtils.toUpperEnglish(name))) {
                set.add(name);
            }
        }
    }

    private UserDefinedFunction findUserDefinedFunctionWithinPath(Schema schema, String str) {
        UserDefinedFunction findFunctionOrAggregate;
        if (schema != null) {
            return schema.findFunctionOrAggregate(str);
        }
        Schema schema2 = this.database.getSchema(this.session.getCurrentSchemaName());
        UserDefinedFunction findFunctionOrAggregate2 = schema2.findFunctionOrAggregate(str);
        if (findFunctionOrAggregate2 != null) {
            return findFunctionOrAggregate2;
        }
        String[] schemaSearchPath = this.session.getSchemaSearchPath();
        if (schemaSearchPath != null) {
            for (String str2 : schemaSearchPath) {
                Schema schema3 = this.database.getSchema(str2);
                if (schema3 != schema2 && (findFunctionOrAggregate = schema3.findFunctionOrAggregate(str)) != null) {
                    return findFunctionOrAggregate;
                }
            }
            return null;
        }
        return null;
    }

    private Sequence findSequence(String str, String str2) {
        Sequence findSequence = this.database.getSchema(str).findSequence(str2);
        if (findSequence != null) {
            return findSequence;
        }
        String[] schemaSearchPath = this.session.getSchemaSearchPath();
        if (schemaSearchPath != null) {
            for (String str3 : schemaSearchPath) {
                Sequence findSequence2 = this.database.getSchema(str3).findSequence(str2);
                if (findSequence2 != null) {
                    return findSequence2;
                }
            }
            return null;
        }
        return null;
    }

    private Sequence readSequence() {
        String readIdentifierWithSchema = readIdentifierWithSchema(null);
        if (this.schemaName != null) {
            return getSchema().getSequence(readIdentifierWithSchema);
        }
        Sequence findSequence = findSequence(this.session.getCurrentSchemaName(), readIdentifierWithSchema);
        if (findSequence != null) {
            return findSequence;
        }
        throw DbException.get(ErrorCode.SEQUENCE_NOT_FOUND_1, readIdentifierWithSchema);
    }

    private Prepared parseAlterTable() {
        boolean readIfExists = readIfExists(false);
        String readIdentifierWithSchema = readIdentifierWithSchema();
        Schema schema = getSchema();
        if (readIf("ADD")) {
            DefineCommand parseTableConstraintIf = parseTableConstraintIf(readIdentifierWithSchema, schema, readIfExists);
            if (parseTableConstraintIf != null) {
                return parseTableConstraintIf;
            }
            return parseAlterTableAddColumn(readIdentifierWithSchema, schema, readIfExists);
        }
        if (readIf(71)) {
            return parseAlterTableSet(schema, readIdentifierWithSchema, readIfExists);
        }
        if (readIf("RENAME")) {
            return parseAlterTableRename(schema, readIdentifierWithSchema, readIfExists);
        }
        if (readIf("DROP")) {
            return parseAlterTableDrop(schema, readIdentifierWithSchema, readIfExists);
        }
        if (readIf("ALTER")) {
            return parseAlterTableAlter(schema, readIdentifierWithSchema, readIfExists);
        }
        Mode mode = this.database.getMode();
        if (mode.alterTableExtensionsMySQL || mode.alterTableModifyColumn) {
            return parseAlterTableCompatibility(schema, readIdentifierWithSchema, readIfExists, mode);
        }
        throw getSyntaxError();
    }

    private Prepared parseAlterTableAlter(Schema schema, String str, boolean z) {
        readIf("COLUMN");
        boolean readIfExists = readIfExists(false);
        String readIdentifier = readIdentifier();
        Column columnIfTableExists = columnIfTableExists(schema, str, readIdentifier, z, readIfExists);
        if (readIf("RENAME")) {
            read(76);
            AlterTableRenameColumn alterTableRenameColumn = new AlterTableRenameColumn(this.session, schema);
            alterTableRenameColumn.setTableName(str);
            alterTableRenameColumn.setIfTableExists(z);
            alterTableRenameColumn.setIfExists(readIfExists);
            alterTableRenameColumn.setOldColumnName(readIdentifier);
            alterTableRenameColumn.setNewColumnName(readIdentifier());
            return alterTableRenameColumn;
        }
        if (readIf("DROP")) {
            if (readIf(25)) {
                if (readIf(60, 58)) {
                    AlterTableAlterColumn alterTableAlterColumn = new AlterTableAlterColumn(this.session, schema);
                    alterTableAlterColumn.setTableName(str);
                    alterTableAlterColumn.setIfTableExists(z);
                    alterTableAlterColumn.setOldColumn(columnIfTableExists);
                    alterTableAlterColumn.setType(100);
                    alterTableAlterColumn.setBooleanFlag(false);
                    return alterTableAlterColumn;
                }
                return getAlterTableAlterColumnDropDefaultExpression(schema, str, z, columnIfTableExists, 10);
            }
            if (readIf("EXPRESSION")) {
                return getAlterTableAlterColumnDropDefaultExpression(schema, str, z, columnIfTableExists, 98);
            }
            if (readIf("IDENTITY")) {
                return getAlterTableAlterColumnDropDefaultExpression(schema, str, z, columnIfTableExists, 99);
            }
            if (readIf(60, "UPDATE")) {
                AlterTableAlterColumn alterTableAlterColumn2 = new AlterTableAlterColumn(this.session, schema);
                alterTableAlterColumn2.setTableName(str);
                alterTableAlterColumn2.setIfTableExists(z);
                alterTableAlterColumn2.setOldColumn(columnIfTableExists);
                alterTableAlterColumn2.setType(90);
                alterTableAlterColumn2.setDefaultExpression(null);
                return alterTableAlterColumn2;
            }
            read(57);
            read(58);
            AlterTableAlterColumn alterTableAlterColumn3 = new AlterTableAlterColumn(this.session, schema);
            alterTableAlterColumn3.setTableName(str);
            alterTableAlterColumn3.setIfTableExists(z);
            alterTableAlterColumn3.setOldColumn(columnIfTableExists);
            alterTableAlterColumn3.setType(9);
            return alterTableAlterColumn3;
        }
        if (readIfCompat("TYPE")) {
            return parseAlterTableAlterColumnDataType(schema, str, readIdentifier, z, readIfExists);
        }
        if (readIf("SELECTIVITY")) {
            AlterTableAlterColumn alterTableAlterColumn4 = new AlterTableAlterColumn(this.session, schema);
            alterTableAlterColumn4.setTableName(str);
            alterTableAlterColumn4.setIfTableExists(z);
            alterTableAlterColumn4.setType(13);
            alterTableAlterColumn4.setOldColumn(columnIfTableExists);
            alterTableAlterColumn4.setSelectivity(readExpression());
            return alterTableAlterColumn4;
        }
        Prepared parseAlterTableAlterColumnIdentity = parseAlterTableAlterColumnIdentity(schema, str, z, columnIfTableExists);
        if (parseAlterTableAlterColumnIdentity != null) {
            return parseAlterTableAlterColumnIdentity;
        }
        if (readIf(71)) {
            return parseAlterTableAlterColumnSet(schema, str, z, readIfExists, readIdentifier, columnIfTableExists);
        }
        return parseAlterTableAlterColumnType(schema, str, readIdentifier, z, readIfExists, true);
    }

    private Prepared getAlterTableAlterColumnDropDefaultExpression(Schema schema, String str, boolean z, Column column, int i) {
        AlterTableAlterColumn alterTableAlterColumn = new AlterTableAlterColumn(this.session, schema);
        alterTableAlterColumn.setTableName(str);
        alterTableAlterColumn.setIfTableExists(z);
        alterTableAlterColumn.setOldColumn(column);
        alterTableAlterColumn.setType(i);
        alterTableAlterColumn.setDefaultExpression(null);
        return alterTableAlterColumn;
    }

    private Prepared parseAlterTableAlterColumnIdentity(Schema schema, String str, boolean z, Column column) {
        Boolean bool = null;
        if (readIf(71, "GENERATED")) {
            if (readIf("ALWAYS")) {
                bool = true;
            } else {
                read("BY");
                read(25);
                bool = false;
            }
        }
        SequenceOptions sequenceOptions = new SequenceOptions();
        if (!parseSequenceOptions(sequenceOptions, null, false, true) && bool == null) {
            return null;
        }
        if (column == null) {
            return new NoOperation(this.session);
        }
        if (!column.isIdentity()) {
            AlterTableAlterColumn alterTableAlterColumn = new AlterTableAlterColumn(this.session, schema);
            parseAlterColumnUsingIf(alterTableAlterColumn);
            alterTableAlterColumn.setTableName(str);
            alterTableAlterColumn.setIfTableExists(z);
            alterTableAlterColumn.setType(11);
            alterTableAlterColumn.setOldColumn(column);
            Column clone = column.getClone();
            clone.setIdentityOptions(sequenceOptions, bool != null && bool.booleanValue());
            alterTableAlterColumn.setNewColumn(clone);
            return alterTableAlterColumn;
        }
        AlterSequence alterSequence = new AlterSequence(this.session, schema);
        alterSequence.setColumn(column, bool);
        alterSequence.setOptions(sequenceOptions);
        return commandIfTableExists(schema, str, z, alterSequence);
    }

    private Prepared parseAlterTableAlterColumnSet(Schema schema, String str, boolean z, boolean z2, String str2, Column column) {
        if (readIf("DATA", "TYPE")) {
            return parseAlterTableAlterColumnDataType(schema, str, str2, z, z2);
        }
        AlterTableAlterColumn alterTableAlterColumn = new AlterTableAlterColumn(this.session, schema);
        alterTableAlterColumn.setTableName(str);
        alterTableAlterColumn.setIfTableExists(z);
        alterTableAlterColumn.setOldColumn(column);
        NullConstraintType parseNotNullConstraint = parseNotNullConstraint();
        switch (parseNotNullConstraint) {
            case NULL_IS_ALLOWED:
                alterTableAlterColumn.setType(9);
                break;
            case NULL_IS_NOT_ALLOWED:
                alterTableAlterColumn.setType(8);
                break;
            case NO_NULL_CONSTRAINT_FOUND:
                if (!readIf(25)) {
                    if (!readIf(60, "UPDATE")) {
                        if (readIf("INVISIBLE")) {
                            alterTableAlterColumn.setType(87);
                            alterTableAlterColumn.setBooleanFlag(false);
                            break;
                        } else if (readIf("VISIBLE")) {
                            alterTableAlterColumn.setType(87);
                            alterTableAlterColumn.setBooleanFlag(true);
                            break;
                        }
                    } else {
                        Expression readExpression = readExpression();
                        alterTableAlterColumn.setType(90);
                        alterTableAlterColumn.setDefaultExpression(readExpression);
                        break;
                    }
                } else if (readIf(60, 58)) {
                    alterTableAlterColumn.setType(100);
                    alterTableAlterColumn.setBooleanFlag(true);
                    break;
                } else {
                    Expression readExpression2 = readExpression();
                    alterTableAlterColumn.setType(10);
                    alterTableAlterColumn.setDefaultExpression(readExpression2);
                    break;
                }
                break;
            default:
                throw DbException.get(ErrorCode.UNKNOWN_MODE_1, "Internal Error - unhandled case: " + parseNotNullConstraint.name());
        }
        return alterTableAlterColumn;
    }

    private Prepared parseAlterTableDrop(Schema schema, String str, boolean z) {
        Column column;
        Prepared parseAlterTableDropCompatibility;
        if (readIf(14)) {
            boolean readIfExists = readIfExists(false);
            String readIdentifierWithSchema = readIdentifierWithSchema(schema.getName());
            boolean readIfExists2 = readIfExists(readIfExists);
            checkSchema(schema);
            AlterTableDropConstraint alterTableDropConstraint = new AlterTableDropConstraint(this.session, getSchema(), readIfExists2);
            alterTableDropConstraint.setTableName(str);
            alterTableDropConstraint.setIfTableExists(z);
            alterTableDropConstraint.setConstraintName(readIdentifierWithSchema);
            ConstraintActionType parseCascadeOrRestrict = parseCascadeOrRestrict();
            if (parseCascadeOrRestrict != null) {
                alterTableDropConstraint.setDropAction(parseCascadeOrRestrict);
            }
            return alterTableDropConstraint;
        }
        if (readIf(63, 47)) {
            Table tableIfTableExists = tableIfTableExists(schema, str, z);
            if (tableIfTableExists == null) {
                return new NoOperation(this.session);
            }
            Index primaryKey = tableIfTableExists.getPrimaryKey();
            DropIndex dropIndex = new DropIndex(this.session, schema);
            dropIndex.setIndexName(primaryKey.getName());
            return dropIndex;
        }
        if (this.database.getMode().alterTableExtensionsMySQL && (parseAlterTableDropCompatibility = parseAlterTableDropCompatibility(schema, str, z)) != null) {
            return parseAlterTableDropCompatibility;
        }
        readIf("COLUMN");
        boolean readIfExists3 = readIfExists(false);
        ArrayList<Column> arrayList = new ArrayList<>();
        Table tableIfTableExists2 = tableIfTableExists(schema, str, z);
        boolean readIf = readIf(105);
        do {
            String readIdentifier = readIdentifier();
            if (tableIfTableExists2 != null && (column = tableIfTableExists2.getColumn(readIdentifier, readIfExists3)) != null) {
                arrayList.add(column);
            }
        } while (readIf(Opcodes.LDIV));
        if (readIf) {
            read(106);
        }
        if (tableIfTableExists2 == null || arrayList.isEmpty()) {
            return new NoOperation(this.session);
        }
        AlterTableAlterColumn alterTableAlterColumn = new AlterTableAlterColumn(this.session, schema);
        alterTableAlterColumn.setType(12);
        alterTableAlterColumn.setTableName(str);
        alterTableAlterColumn.setIfTableExists(z);
        alterTableAlterColumn.setColumnsToRemove(arrayList);
        return alterTableAlterColumn;
    }

    private Prepared parseAlterTableDropCompatibility(Schema schema, String str, boolean z) {
        if (readIfCompat(34, 47)) {
            boolean readIfExists = readIfExists(false);
            String readIdentifierWithSchema = readIdentifierWithSchema(schema.getName());
            checkSchema(schema);
            AlterTableDropConstraint alterTableDropConstraint = new AlterTableDropConstraint(this.session, getSchema(), readIfExists);
            alterTableDropConstraint.setTableName(str);
            alterTableDropConstraint.setIfTableExists(z);
            alterTableDropConstraint.setConstraintName(readIdentifierWithSchema);
            return alterTableDropConstraint;
        }
        if (readIfCompat("INDEX")) {
            boolean readIfExists2 = readIfExists(false);
            String readIdentifierWithSchema2 = readIdentifierWithSchema(schema.getName());
            if (schema.findIndex(this.session, readIdentifierWithSchema2) != null) {
                DropIndex dropIndex = new DropIndex(this.session, getSchema());
                dropIndex.setIndexName(readIdentifierWithSchema2);
                return commandIfTableExists(schema, str, z, dropIndex);
            }
            AlterTableDropConstraint alterTableDropConstraint2 = new AlterTableDropConstraint(this.session, getSchema(), readIfExists2);
            alterTableDropConstraint2.setTableName(str);
            alterTableDropConstraint2.setIfTableExists(z);
            alterTableDropConstraint2.setConstraintName(readIdentifierWithSchema2);
            return alterTableDropConstraint2;
        }
        return null;
    }

    private Prepared parseAlterTableRename(Schema schema, String str, boolean z) {
        if (readIf("COLUMN")) {
            String readIdentifier = readIdentifier();
            read(76);
            AlterTableRenameColumn alterTableRenameColumn = new AlterTableRenameColumn(this.session, schema);
            alterTableRenameColumn.setTableName(str);
            alterTableRenameColumn.setIfTableExists(z);
            alterTableRenameColumn.setOldColumnName(readIdentifier);
            alterTableRenameColumn.setNewColumnName(readIdentifier());
            return alterTableRenameColumn;
        }
        if (readIf(14)) {
            String readIdentifierWithSchema = readIdentifierWithSchema(schema.getName());
            checkSchema(schema);
            read(76);
            AlterTableRenameConstraint alterTableRenameConstraint = new AlterTableRenameConstraint(this.session, schema);
            alterTableRenameConstraint.setTableName(str);
            alterTableRenameConstraint.setIfTableExists(z);
            alterTableRenameConstraint.setConstraintName(readIdentifierWithSchema);
            alterTableRenameConstraint.setNewConstraintName(readIdentifier());
            return alterTableRenameConstraint;
        }
        read(76);
        String readIdentifierWithSchema2 = readIdentifierWithSchema(schema.getName());
        checkSchema(schema);
        AlterTableRename alterTableRename = new AlterTableRename(this.session, getSchema());
        alterTableRename.setTableName(str);
        alterTableRename.setNewTableName(readIdentifierWithSchema2);
        alterTableRename.setIfTableExists(z);
        alterTableRename.setHidden(readIf("HIDDEN"));
        return alterTableRename;
    }

    private Prepared parseAlterTableSet(Schema schema, String str, boolean z) {
        read("REFERENTIAL_INTEGRITY");
        AlterTableSet alterTableSet = new AlterTableSet(this.session, schema, 55, readBooleanSetting());
        alterTableSet.setTableName(str);
        alterTableSet.setIfTableExists(z);
        if (readIf(13)) {
            alterTableSet.setCheckExisting(true);
        } else if (readIf("NOCHECK")) {
            alterTableSet.setCheckExisting(false);
        }
        return alterTableSet;
    }

    private Prepared parseAlterTableCompatibility(Schema schema, String str, boolean z, Mode mode) {
        AlterTableAlterColumn parseAlterTableAlterColumnType;
        if (mode.alterTableExtensionsMySQL) {
            if (readIfCompat("AUTO_INCREMENT")) {
                readIf(95);
                Expression readExpression = readExpression();
                Table tableIfTableExists = tableIfTableExists(schema, str, z);
                if (tableIfTableExists == null) {
                    return new NoOperation(this.session);
                }
                Index findPrimaryKey = tableIfTableExists.findPrimaryKey();
                if (findPrimaryKey != null) {
                    for (IndexColumn indexColumn : findPrimaryKey.getIndexColumns()) {
                        Column column = indexColumn.column;
                        if (column.isIdentity()) {
                            AlterSequence alterSequence = new AlterSequence(this.session, schema);
                            alterSequence.setColumn(column, null);
                            SequenceOptions sequenceOptions = new SequenceOptions();
                            sequenceOptions.setRestartValue(readExpression);
                            alterSequence.setOptions(sequenceOptions);
                            return alterSequence;
                        }
                    }
                }
                throw DbException.get(ErrorCode.COLUMN_NOT_FOUND_1, "AUTO_INCREMENT PRIMARY KEY");
            }
            if (readIfCompat("CHANGE")) {
                readIf("COLUMN");
                String readIdentifier = readIdentifier();
                String readIdentifier2 = readIdentifier();
                Column columnIfTableExists = columnIfTableExists(schema, str, readIdentifier, z, false);
                parseColumnForTable(readIdentifier2, columnIfTableExists == null ? true : columnIfTableExists.isNullable());
                AlterTableRenameColumn alterTableRenameColumn = new AlterTableRenameColumn(this.session, schema);
                alterTableRenameColumn.setTableName(str);
                alterTableRenameColumn.setIfTableExists(z);
                alterTableRenameColumn.setOldColumnName(readIdentifier);
                alterTableRenameColumn.setNewColumnName(readIdentifier2);
                return alterTableRenameColumn;
            }
            if (readIfCompat("CONVERT")) {
                readIf(76);
                readIf("CHARACTER");
                readIf(71);
                readMySQLCharset();
                if (readIf("COLLATE")) {
                    readMySQLCharset();
                }
                return new NoOperation(this.session);
            }
        }
        if (mode.alterTableModifyColumn && readIfCompat("MODIFY")) {
            readIf("COLUMN");
            boolean readIf = readIf(105);
            String readIdentifier3 = readIdentifier();
            NullConstraintType parseNotNullConstraint = parseNotNullConstraint();
            switch (parseNotNullConstraint) {
                case NULL_IS_ALLOWED:
                case NULL_IS_NOT_ALLOWED:
                    parseAlterTableAlterColumnType = new AlterTableAlterColumn(this.session, schema);
                    parseAlterTableAlterColumnType.setTableName(str);
                    parseAlterTableAlterColumnType.setIfTableExists(z);
                    parseAlterTableAlterColumnType.setOldColumn(columnIfTableExists(schema, str, readIdentifier3, z, false));
                    if (parseNotNullConstraint == NullConstraintType.NULL_IS_ALLOWED) {
                        parseAlterTableAlterColumnType.setType(9);
                        break;
                    } else {
                        parseAlterTableAlterColumnType.setType(8);
                        break;
                    }
                case NO_NULL_CONSTRAINT_FOUND:
                    parseAlterTableAlterColumnType = parseAlterTableAlterColumnType(schema, str, readIdentifier3, z, false, mode.alterTableModifyColumnPreserveNullability);
                    break;
                default:
                    throw DbException.get(ErrorCode.UNKNOWN_MODE_1, "Internal Error - unhandled case: " + parseNotNullConstraint.name());
            }
            if (readIf) {
                read(106);
            }
            return parseAlterTableAlterColumnType;
        }
        throw getSyntaxError();
    }

    private Table tableIfTableExists(Schema schema, String str, boolean z) {
        Table resolveTableOrView = schema.resolveTableOrView(this.session, str);
        if (resolveTableOrView == null && !z) {
            throw getTableOrViewNotFoundDbException(schema.getName(), str);
        }
        return resolveTableOrView;
    }

    private Column columnIfTableExists(Schema schema, String str, String str2, boolean z, boolean z2) {
        Table tableIfTableExists = tableIfTableExists(schema, str, z);
        if (tableIfTableExists == null) {
            return null;
        }
        return tableIfTableExists.getColumn(str2, z2);
    }

    private Prepared commandIfTableExists(Schema schema, String str, boolean z, Prepared prepared) {
        return tableIfTableExists(schema, str, z) == null ? new NoOperation(this.session) : prepared;
    }

    private AlterTableAlterColumn parseAlterTableAlterColumnType(Schema schema, String str, String str2, boolean z, boolean z2, boolean z3) {
        Column columnIfTableExists = columnIfTableExists(schema, str, str2, z, z2);
        Column parseColumnForTable = parseColumnForTable(str2, !z3 || columnIfTableExists == null || columnIfTableExists.isNullable());
        AlterTableAlterColumn alterTableAlterColumn = new AlterTableAlterColumn(this.session, schema);
        parseAlterColumnUsingIf(alterTableAlterColumn);
        alterTableAlterColumn.setTableName(str);
        alterTableAlterColumn.setIfTableExists(z);
        alterTableAlterColumn.setType(11);
        alterTableAlterColumn.setOldColumn(columnIfTableExists);
        alterTableAlterColumn.setNewColumn(parseColumnForTable);
        return alterTableAlterColumn;
    }

    private AlterTableAlterColumn parseAlterTableAlterColumnDataType(Schema schema, String str, String str2, boolean z, boolean z2) {
        Column columnIfTableExists = columnIfTableExists(schema, str, str2, z, z2);
        Column parseColumnWithType = parseColumnWithType(str2);
        if (columnIfTableExists != null) {
            if (!columnIfTableExists.isNullable()) {
                parseColumnWithType.setNullable(false);
            }
            if (!columnIfTableExists.getVisible()) {
                parseColumnWithType.setVisible(false);
            }
            Expression defaultExpression = columnIfTableExists.getDefaultExpression();
            if (defaultExpression != null) {
                if (columnIfTableExists.isGenerated()) {
                    parseColumnWithType.setGeneratedExpression(defaultExpression);
                } else {
                    parseColumnWithType.setDefaultExpression(this.session, defaultExpression);
                }
            }
            Expression onUpdateExpression = columnIfTableExists.getOnUpdateExpression();
            if (onUpdateExpression != null) {
                parseColumnWithType.setOnUpdateExpression(this.session, onUpdateExpression);
            }
            Sequence sequence = columnIfTableExists.getSequence();
            if (sequence != null) {
                parseColumnWithType.setIdentityOptions(new SequenceOptions(sequence, parseColumnWithType.getType()), columnIfTableExists.isGeneratedAlways());
            }
            String comment = columnIfTableExists.getComment();
            if (comment != null) {
                parseColumnWithType.setComment(comment);
            }
        }
        AlterTableAlterColumn alterTableAlterColumn = new AlterTableAlterColumn(this.session, schema);
        parseAlterColumnUsingIf(alterTableAlterColumn);
        alterTableAlterColumn.setTableName(str);
        alterTableAlterColumn.setIfTableExists(z);
        alterTableAlterColumn.setType(11);
        alterTableAlterColumn.setOldColumn(columnIfTableExists);
        alterTableAlterColumn.setNewColumn(parseColumnWithType);
        return alterTableAlterColumn;
    }

    private AlterTableAlterColumn parseAlterTableAddColumn(String str, Schema schema, boolean z) {
        readIf("COLUMN");
        AlterTableAlterColumn alterTableAlterColumn = new AlterTableAlterColumn(this.session, schema);
        alterTableAlterColumn.setType(7);
        alterTableAlterColumn.setTableName(str);
        alterTableAlterColumn.setIfTableExists(z);
        if (readIf(105)) {
            alterTableAlterColumn.setIfNotExists(false);
            do {
                parseTableColumnDefinition(alterTableAlterColumn, schema, str, false);
            } while (readIfMore());
        } else {
            alterTableAlterColumn.setIfNotExists(readIfNotExists());
            parseTableColumnDefinition(alterTableAlterColumn, schema, str, false);
            parseAlterColumnUsingIf(alterTableAlterColumn);
        }
        if (readIf("BEFORE")) {
            alterTableAlterColumn.setAddBefore(readIdentifier());
        } else if (readIf("AFTER")) {
            alterTableAlterColumn.setAddAfter(readIdentifier());
        } else if (readIf("FIRST")) {
            alterTableAlterColumn.setAddFirst();
        }
        return alterTableAlterColumn;
    }

    private void parseAlterColumnUsingIf(AlterTableAlterColumn alterTableAlterColumn) {
        if (readIf(83)) {
            alterTableAlterColumn.setUsingExpression(readExpression());
        }
    }

    private ConstraintActionType parseAction() {
        ConstraintActionType parseCascadeOrRestrict = parseCascadeOrRestrict();
        if (parseCascadeOrRestrict != null) {
            return parseCascadeOrRestrict;
        }
        if (readIf("NO", "ACTION")) {
            return ConstraintActionType.RESTRICT;
        }
        read(71);
        if (readIf(58)) {
            return ConstraintActionType.SET_NULL;
        }
        read(25);
        return ConstraintActionType.SET_DEFAULT;
    }

    private ConstraintActionType parseCascadeOrRestrict() {
        if (readIf("CASCADE")) {
            return ConstraintActionType.CASCADE;
        }
        if (readIf("RESTRICT")) {
            return ConstraintActionType.RESTRICT;
        }
        return null;
    }

    private DefineCommand parseTableConstraintIf(String str, Schema schema, boolean z) {
        AlterTableAddConstraint alterTableAddConstraint;
        String str2 = null;
        String str3 = null;
        boolean z2 = false;
        if (readIf(14)) {
            z2 = readIfNotExists();
            str2 = readIdentifierWithSchema(schema.getName());
            checkSchema(schema);
            str3 = readCommentIf();
        }
        switch (this.currentTokenType) {
            case 13:
                read();
                alterTableAddConstraint = new AlterTableAddConstraint(this.session, schema, 3, z2);
                alterTableAddConstraint.setCheckExpression(readExpression());
                break;
            case 34:
                read();
                read(47);
                read(105);
                alterTableAddConstraint = new AlterTableAddConstraint(this.session, schema, 5, z2);
                alterTableAddConstraint.setIndexColumns(parseIndexColumnList());
                if (readIf("INDEX")) {
                    alterTableAddConstraint.setIndex(schema.findIndex(this.session, readIdentifierWithSchema()));
                }
                read("REFERENCES");
                parseReferences(alterTableAddConstraint, schema, str);
                break;
            case 63:
                read();
                read(47);
                alterTableAddConstraint = new AlterTableAddConstraint(this.session, schema, 6, z2);
                if (readIf("HASH")) {
                    alterTableAddConstraint.setPrimaryKeyHash(true);
                }
                read(105);
                alterTableAddConstraint.setIndexColumns(parseIndexColumnList());
                if (readIf("INDEX")) {
                    alterTableAddConstraint.setIndex(getSchema().findIndex(this.session, readIdentifierWithSchema()));
                    break;
                }
                break;
            case 80:
                read();
                NullsDistinct readNullsDistinct = readNullsDistinct(this.database.getMode().nullsDistinct);
                boolean z3 = this.database.getMode().indexDefinitionInCreateTable;
                if (z3) {
                    if (!readIfCompat(47)) {
                        readIfCompat("INDEX");
                    }
                    if (!isToken(105)) {
                        str2 = readIdentifier();
                    }
                }
                read(105);
                alterTableAddConstraint = new AlterTableAddConstraint(this.session, schema, 4, z2);
                alterTableAddConstraint.setNullsDistinct(readNullsDistinct);
                if (readIf(84, 106)) {
                    alterTableAddConstraint.setIndexColumns(null);
                } else {
                    alterTableAddConstraint.setIndexColumns(parseIndexColumnList());
                }
                if (readIf("INDEX")) {
                    alterTableAddConstraint.setIndex(getSchema().findIndex(this.session, readIdentifierWithSchema()));
                }
                if (z3) {
                    readIfCompat(83, "BTREE");
                    break;
                }
                break;
            default:
                if (str2 == null) {
                    Mode mode = this.database.getMode();
                    if (mode.indexDefinitionInCreateTable) {
                        int i = this.tokenIndex;
                        if (readIfCompat(47) || readIfCompat("INDEX")) {
                            if (DataType.getTypeByName(this.currentToken, mode) == null) {
                                CreateIndex createIndex = new CreateIndex(this.session, schema);
                                createIndex.setComment(str3);
                                createIndex.setTableName(str);
                                createIndex.setIfTableExists(z);
                                if (!readIf(105)) {
                                    createIndex.setIndexName(readIdentifier());
                                    read(105);
                                }
                                createIndex.setIndexColumns(parseIndexColumnList());
                                if (readIf(83)) {
                                    read("BTREE");
                                }
                                return createIndex;
                            }
                            setTokenIndex(i);
                            return null;
                        }
                        return null;
                    }
                    return null;
                }
                if (this.expectedList != null) {
                    addMultipleExpected(63, 80, 34, 13);
                }
                throw getSyntaxError();
        }
        if (alterTableAddConstraint.getType() != 6) {
            if (readIf("NOCHECK")) {
                alterTableAddConstraint.setCheckExisting(false);
            } else {
                readIf(13);
                alterTableAddConstraint.setCheckExisting(true);
            }
        }
        alterTableAddConstraint.setTableName(str);
        alterTableAddConstraint.setIfTableExists(z);
        alterTableAddConstraint.setConstraintName(str2);
        alterTableAddConstraint.setComment(str3);
        return alterTableAddConstraint;
    }

    private void parseReferences(AlterTableAddConstraint alterTableAddConstraint, Schema schema, String str) {
        if (readIf(105)) {
            alterTableAddConstraint.setRefTableName(schema, str);
            alterTableAddConstraint.setRefIndexColumns(parseIndexColumnList());
        } else {
            alterTableAddConstraint.setRefTableName(getSchema(), readIdentifierWithSchema(schema.getName()));
            if (readIf(105)) {
                alterTableAddConstraint.setRefIndexColumns(parseIndexColumnList());
            }
        }
        if (readIf("INDEX")) {
            alterTableAddConstraint.setRefIndex(getSchema().findIndex(this.session, readIdentifierWithSchema()));
        }
        while (readIf(60)) {
            if (readIf("DELETE")) {
                alterTableAddConstraint.setDeleteAction(parseAction());
            } else {
                read("UPDATE");
                alterTableAddConstraint.setUpdateAction(parseAction());
            }
        }
        if (!readIf(57, "DEFERRABLE")) {
            readIf("DEFERRABLE");
        }
    }

    private CreateLinkedTable parseCreateLinkedTable(boolean z, boolean z2, boolean z3) {
        read(75);
        boolean readIfNotExists = readIfNotExists();
        String readIdentifierWithSchema = readIdentifierWithSchema();
        CreateLinkedTable createLinkedTable = new CreateLinkedTable(this.session, getSchema());
        createLinkedTable.setTemporary(z);
        createLinkedTable.setGlobalTemporary(z2);
        createLinkedTable.setForce(z3);
        createLinkedTable.setIfNotExists(readIfNotExists);
        createLinkedTable.setTableName(readIdentifierWithSchema);
        createLinkedTable.setComment(readCommentIf());
        read(105);
        createLinkedTable.setDriver(readString());
        read(Opcodes.LDIV);
        createLinkedTable.setUrl(readString());
        read(Opcodes.LDIV);
        createLinkedTable.setUser(readString());
        read(Opcodes.LDIV);
        createLinkedTable.setPassword(readString());
        read(Opcodes.LDIV);
        String readString = readString();
        if (readIf(Opcodes.LDIV)) {
            createLinkedTable.setOriginalSchema(readString);
            readString = readString();
        }
        createLinkedTable.setOriginalTable(readString);
        read(106);
        if (readIf("EMIT", "UPDATES")) {
            createLinkedTable.setEmitUpdates(true);
        } else if (readIf("READONLY")) {
            createLinkedTable.setReadOnly(true);
        }
        if (readIf("FETCH_SIZE")) {
            createLinkedTable.setFetchSize(readNonNegativeInt());
        }
        if (readIf("AUTOCOMMIT")) {
            if (readIf("ON")) {
                createLinkedTable.setAutoCommit(true);
            } else if (readIf(CompareMode.OFF)) {
                createLinkedTable.setAutoCommit(false);
            }
        }
        return createLinkedTable;
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x007b, code lost:            if (readIf(106) == false) goto L13;     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x007e, code lost:            parseTableColumnDefinition(r0, r0, r0, true);     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x008d, code lost:            if (readIfMore() != false) goto L59;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private org.h2.command.ddl.CreateTable parseCreateTable(boolean r7, boolean r8, boolean r9) {
        /*
            Method dump skipped, instructions count: 405
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.Parser.parseCreateTable(boolean, boolean, boolean):org.h2.command.ddl.CreateTable");
    }

    private void parseTableColumnDefinition(CommandWithColumns commandWithColumns, Schema schema, String str, boolean z) {
        DefineCommand parseTableConstraintIf = parseTableConstraintIf(str, schema, false);
        if (parseTableConstraintIf != null) {
            commandWithColumns.addConstraintCommand(parseTableConstraintIf);
            return;
        }
        String readIdentifier = readIdentifier();
        if (z && (this.currentTokenType == 109 || this.currentTokenType == 106)) {
            commandWithColumns.addColumn(new Column(readIdentifier, TypeInfo.TYPE_UNKNOWN));
            return;
        }
        Column parseColumnForTable = parseColumnForTable(readIdentifier, true);
        if (parseColumnForTable.hasIdentityOptions() && parseColumnForTable.isPrimaryKey()) {
            commandWithColumns.addConstraintCommand(newPrimaryKeyConstraintCommand(this.session, schema, str, parseColumnForTable));
        }
        commandWithColumns.addColumn(parseColumnForTable);
        readColumnConstraints(commandWithColumns, schema, str, parseColumnForTable);
    }

    public static AlterTableAddConstraint newPrimaryKeyConstraintCommand(SessionLocal sessionLocal, Schema schema, String str, Column column) {
        column.setPrimaryKey(false);
        AlterTableAddConstraint alterTableAddConstraint = new AlterTableAddConstraint(sessionLocal, schema, 6, false);
        alterTableAddConstraint.setTableName(str);
        alterTableAddConstraint.setIndexColumns(new IndexColumn[]{new IndexColumn(column.getName())});
        return alterTableAddConstraint;
    }

    private void readColumnConstraints(CommandWithColumns commandWithColumns, Schema schema, String str, Column column) {
        String readIdentifier;
        NullConstraintType parseNotNullConstraint;
        String comment = column.getComment();
        boolean z = false;
        boolean z2 = false;
        Mode mode = this.database.getMode();
        while (true) {
            if (readIf(14)) {
                readIdentifier = readIdentifier();
            } else {
                if (comment == null) {
                    String readCommentIf = readCommentIf();
                    comment = readCommentIf;
                    if (readCommentIf != null) {
                        column.setComment(comment);
                    }
                }
                readIdentifier = null;
            }
            if (!z && readIf(63, 47)) {
                z = true;
                boolean readIf = readIf("HASH");
                AlterTableAddConstraint alterTableAddConstraint = new AlterTableAddConstraint(this.session, schema, 6, false);
                alterTableAddConstraint.setConstraintName(readIdentifier);
                alterTableAddConstraint.setPrimaryKeyHash(readIf);
                alterTableAddConstraint.setTableName(str);
                alterTableAddConstraint.setIndexColumns(new IndexColumn[]{new IndexColumn(column.getName())});
                commandWithColumns.addConstraintCommand(alterTableAddConstraint);
            } else if (readIf(80)) {
                NullsDistinct readNullsDistinct = readNullsDistinct(this.database.getMode().nullsDistinct);
                AlterTableAddConstraint alterTableAddConstraint2 = new AlterTableAddConstraint(this.session, schema, 4, false);
                alterTableAddConstraint2.setConstraintName(readIdentifier);
                alterTableAddConstraint2.setNullsDistinct(readNullsDistinct);
                alterTableAddConstraint2.setIndexColumns(new IndexColumn[]{new IndexColumn(column.getName())});
                alterTableAddConstraint2.setTableName(str);
                commandWithColumns.addConstraintCommand(alterTableAddConstraint2);
            } else if (!z2 && (parseNotNullConstraint = parseNotNullConstraint()) != NullConstraintType.NO_NULL_CONSTRAINT_FOUND) {
                z2 = true;
                if (parseNotNullConstraint == NullConstraintType.NULL_IS_NOT_ALLOWED) {
                    column.setNullable(false);
                } else if (parseNotNullConstraint != NullConstraintType.NULL_IS_ALLOWED) {
                    continue;
                } else {
                    if (column.isIdentity()) {
                        throw DbException.get(ErrorCode.COLUMN_MUST_NOT_BE_NULLABLE_1, column.getName());
                    }
                    column.setNullable(true);
                }
            } else if (readIf(13)) {
                AlterTableAddConstraint alterTableAddConstraint3 = new AlterTableAddConstraint(this.session, schema, 3, false);
                alterTableAddConstraint3.setConstraintName(readIdentifier);
                alterTableAddConstraint3.setTableName(str);
                alterTableAddConstraint3.setCheckExpression(readExpression());
                commandWithColumns.addConstraintCommand(alterTableAddConstraint3);
            } else if (readIf("REFERENCES")) {
                AlterTableAddConstraint alterTableAddConstraint4 = new AlterTableAddConstraint(this.session, schema, 5, false);
                alterTableAddConstraint4.setConstraintName(readIdentifier);
                alterTableAddConstraint4.setIndexColumns(new IndexColumn[]{new IndexColumn(column.getName())});
                alterTableAddConstraint4.setTableName(str);
                parseReferences(alterTableAddConstraint4, schema, str);
                commandWithColumns.addConstraintCommand(alterTableAddConstraint4);
            } else if (readIdentifier == null) {
                if (column.getIdentityOptions() != null || !parseCompatibilityIdentity(column, mode)) {
                    return;
                }
            } else {
                throw getSyntaxError();
            }
        }
    }

    private boolean parseCompatibilityIdentity(Column column, Mode mode) {
        if (mode.autoIncrementClause && readIfCompat("AUTO_INCREMENT")) {
            parseCompatibilityIdentityOptions(column);
            return true;
        }
        if (mode.identityClause && readIfCompat("IDENTITY")) {
            parseCompatibilityIdentityOptions(column);
            return true;
        }
        return false;
    }

    /* JADX WARN: Code restructure failed: missing block: B:28:0x009e, code lost:            throw org.h2.message.DbException.get(org.h2.api.ErrorCode.COLUMN_NOT_FOUND_1, "AUTO_INCREMENT PRIMARY KEY");     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void parseCreateTableMySQLTableOptions(org.h2.command.ddl.CreateTable r5) {
        /*
            Method dump skipped, instructions count: 360
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.Parser.parseCreateTableMySQLTableOptions(org.h2.command.ddl.CreateTable):void");
    }

    private void readMySQLCharset() {
        readIf(95);
        readIdentifier();
    }

    private NullConstraintType parseNotNullConstraint(NullConstraintType nullConstraintType) {
        if (nullConstraintType == NullConstraintType.NO_NULL_CONSTRAINT_FOUND) {
            nullConstraintType = parseNotNullConstraint();
        }
        return nullConstraintType;
    }

    private NullConstraintType parseNotNullConstraint() {
        NullConstraintType nullConstraintType;
        if (readIf(57, 58)) {
            nullConstraintType = NullConstraintType.NULL_IS_NOT_ALLOWED;
        } else if (readIfCompat(58)) {
            nullConstraintType = NullConstraintType.NULL_IS_ALLOWED;
        } else {
            return NullConstraintType.NO_NULL_CONSTRAINT_FOUND;
        }
        if (this.database.getMode().getEnum() == Mode.ModeEnum.Oracle) {
            nullConstraintType = parseNotNullCompatibility(nullConstraintType);
        }
        return nullConstraintType;
    }

    private NullConstraintType parseNotNullCompatibility(NullConstraintType nullConstraintType) {
        if (readIfCompat("ENABLE")) {
            if (!readIf("VALIDATE") && readIf("NOVALIDATE")) {
                nullConstraintType = NullConstraintType.NULL_IS_ALLOWED;
            }
        } else if (readIfCompat("DISABLE")) {
            nullConstraintType = NullConstraintType.NULL_IS_ALLOWED;
            if (!readIf("VALIDATE")) {
                readIf("NOVALIDATE");
            }
        }
        return nullConstraintType;
    }

    private CreateSynonym parseCreateSynonym(boolean z) {
        boolean readIfNotExists = readIfNotExists();
        String readIdentifierWithSchema = readIdentifierWithSchema();
        Schema schema = getSchema();
        read(33);
        String readIdentifierWithSchema2 = readIdentifierWithSchema();
        Schema schema2 = getSchema();
        CreateSynonym createSynonym = new CreateSynonym(this.session, schema);
        createSynonym.setName(readIdentifierWithSchema);
        createSynonym.setSynonymFor(readIdentifierWithSchema2);
        createSynonym.setSynonymForSchema(schema2);
        createSynonym.setComment(readCommentIf());
        createSynonym.setIfNotExists(readIfNotExists);
        createSynonym.setOrReplace(z);
        return createSynonym;
    }

    private static int getCompareType(int i) {
        switch (i) {
            case 95:
                return 0;
            case 96:
                return 5;
            case 97:
                return 3;
            case 98:
                return 2;
            case 99:
                return 4;
            case 100:
                return 1;
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            default:
                return -1;
            case Opcodes.DMUL /* 107 */:
                return 8;
        }
    }

    public void setRightsChecked(boolean z) {
        this.rightsChecked = z;
    }

    public Expression parseExpression(String str) {
        initialize(str, null, false);
        read();
        return readExpression();
    }

    public Expression parseDomainConstraintExpression(String str) {
        initialize(str, null, false);
        read();
        try {
            this.parseDomainConstraint = true;
            return readExpression();
        } finally {
            this.parseDomainConstraint = false;
        }
    }

    public Table parseTableName(String str) {
        initialize(str, null, false);
        read();
        return readTableOrView();
    }
}
