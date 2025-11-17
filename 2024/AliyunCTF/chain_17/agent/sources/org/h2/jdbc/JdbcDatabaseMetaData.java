package org.h2.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import org.h2.command.CommandInterface;
import org.h2.engine.Constants;
import org.h2.engine.Session;
import org.h2.jdbc.meta.DatabaseMeta;
import org.h2.jdbc.meta.DatabaseMetaLegacy;
import org.h2.message.DbException;
import org.h2.message.Trace;
import org.h2.message.TraceObject;
import org.h2.mode.DefaultNullOrdering;
import org.h2.result.ResultInterface;
import org.h2.result.SimpleResult;
import org.h2.security.auth.DefaultAuthenticator;
import org.h2.value.TypeInfo;
import org.h2.value.ValueInteger;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbc/JdbcDatabaseMetaData.class */
public final class JdbcDatabaseMetaData extends TraceObject implements DatabaseMetaData, JdbcDatabaseMetaDataBackwardsCompat {
    private final JdbcConnection conn;
    private final DatabaseMeta meta;

    /* JADX INFO: Access modifiers changed from: package-private */
    public JdbcDatabaseMetaData(JdbcConnection jdbcConnection, Trace trace, int i) {
        setTrace(trace, 2, i);
        this.conn = jdbcConnection;
        Session session = jdbcConnection.getSession();
        this.meta = session.isOldInformationSchema() ? new DatabaseMetaLegacy(session) : jdbcConnection.getSession().getDatabaseMeta();
    }

    @Override // java.sql.DatabaseMetaData
    public int getDriverMajorVersion() {
        debugCodeCall("getDriverMajorVersion");
        return 2;
    }

    @Override // java.sql.DatabaseMetaData
    public int getDriverMinorVersion() {
        debugCodeCall("getDriverMinorVersion");
        return 2;
    }

    @Override // java.sql.DatabaseMetaData
    public String getDatabaseProductName() {
        debugCodeCall("getDatabaseProductName");
        return DefaultAuthenticator.DEFAULT_REALMNAME;
    }

    @Override // java.sql.DatabaseMetaData
    public String getDatabaseProductVersion() throws SQLException {
        try {
            debugCodeCall("getDatabaseProductVersion");
            return this.meta.getDatabaseProductVersion();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public String getDriverName() {
        debugCodeCall("getDriverName");
        return "H2 JDBC Driver";
    }

    @Override // java.sql.DatabaseMetaData
    public String getDriverVersion() {
        debugCodeCall("getDriverVersion");
        return Constants.FULL_VERSION;
    }

    @Override // java.sql.DatabaseMetaData
    public ResultSet getTables(String str, String str2, String str3, String[] strArr) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getTables(" + quote(str) + ", " + quote(str2) + ", " + quote(str3) + ", " + quoteArray(strArr) + ')');
            }
            return getResultSet(this.meta.getTables(str, str2, str3, strArr));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public ResultSet getColumns(String str, String str2, String str3, String str4) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getColumns(" + quote(str) + ", " + quote(str2) + ", " + quote(str3) + ", " + quote(str4) + ')');
            }
            return getResultSet(this.meta.getColumns(str, str2, str3, str4));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public ResultSet getIndexInfo(String str, String str2, String str3, boolean z, boolean z2) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getIndexInfo(" + quote(str) + ", " + quote(str2) + ", " + quote(str3) + ", " + z + ", " + z2 + ')');
            }
            return getResultSet(this.meta.getIndexInfo(str, str2, str3, z, z2));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public ResultSet getPrimaryKeys(String str, String str2, String str3) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getPrimaryKeys(" + quote(str) + ", " + quote(str2) + ", " + quote(str3) + ')');
            }
            return getResultSet(this.meta.getPrimaryKeys(str, str2, str3));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public boolean allProceduresAreCallable() {
        debugCodeCall("allProceduresAreCallable");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean allTablesAreSelectable() {
        debugCodeCall("allTablesAreSelectable");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public String getURL() throws SQLException {
        try {
            debugCodeCall("getURL");
            return this.conn.getURL();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public String getUserName() throws SQLException {
        try {
            debugCodeCall("getUserName");
            return this.conn.getUser();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public boolean isReadOnly() throws SQLException {
        try {
            debugCodeCall("isReadOnly");
            return this.conn.isReadOnly();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public boolean nullsAreSortedHigh() throws SQLException {
        try {
            debugCodeCall("nullsAreSortedHigh");
            return this.meta.defaultNullOrdering() == DefaultNullOrdering.HIGH;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public boolean nullsAreSortedLow() throws SQLException {
        try {
            debugCodeCall("nullsAreSortedLow");
            return this.meta.defaultNullOrdering() == DefaultNullOrdering.LOW;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public boolean nullsAreSortedAtStart() throws SQLException {
        try {
            debugCodeCall("nullsAreSortedAtStart");
            return this.meta.defaultNullOrdering() == DefaultNullOrdering.FIRST;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public boolean nullsAreSortedAtEnd() throws SQLException {
        try {
            debugCodeCall("nullsAreSortedAtEnd");
            return this.meta.defaultNullOrdering() == DefaultNullOrdering.LAST;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public Connection getConnection() {
        debugCodeCall("getConnection");
        return this.conn;
    }

    @Override // java.sql.DatabaseMetaData
    public ResultSet getProcedures(String str, String str2, String str3) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getProcedures(" + quote(str) + ", " + quote(str2) + ", " + quote(str3) + ')');
            }
            return getResultSet(this.meta.getProcedures(str, str2, str3));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public ResultSet getProcedureColumns(String str, String str2, String str3, String str4) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getProcedureColumns(" + quote(str) + ", " + quote(str2) + ", " + quote(str3) + ", " + quote(str4) + ')');
            }
            checkClosed();
            return getResultSet(this.meta.getProcedureColumns(str, str2, str3, str4));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public ResultSet getSchemas() throws SQLException {
        try {
            debugCodeCall("getSchemas");
            return getResultSet(this.meta.getSchemas());
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public ResultSet getCatalogs() throws SQLException {
        try {
            debugCodeCall("getCatalogs");
            return getResultSet(this.meta.getCatalogs());
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public ResultSet getTableTypes() throws SQLException {
        try {
            debugCodeCall("getTableTypes");
            return getResultSet(this.meta.getTableTypes());
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public ResultSet getColumnPrivileges(String str, String str2, String str3, String str4) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getColumnPrivileges(" + quote(str) + ", " + quote(str2) + ", " + quote(str3) + ", " + quote(str4) + ')');
            }
            return getResultSet(this.meta.getColumnPrivileges(str, str2, str3, str4));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public ResultSet getTablePrivileges(String str, String str2, String str3) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getTablePrivileges(" + quote(str) + ", " + quote(str2) + ", " + quote(str3) + ')');
            }
            checkClosed();
            return getResultSet(this.meta.getTablePrivileges(str, str2, str3));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public ResultSet getBestRowIdentifier(String str, String str2, String str3, int i, boolean z) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getBestRowIdentifier(" + quote(str) + ", " + quote(str2) + ", " + quote(str3) + ", " + i + ", " + z + ')');
            }
            return getResultSet(this.meta.getBestRowIdentifier(str, str2, str3, i, z));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public ResultSet getVersionColumns(String str, String str2, String str3) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getVersionColumns(" + quote(str) + ", " + quote(str2) + ", " + quote(str3) + ')');
            }
            return getResultSet(this.meta.getVersionColumns(str, str2, str3));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public ResultSet getImportedKeys(String str, String str2, String str3) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getImportedKeys(" + quote(str) + ", " + quote(str2) + ", " + quote(str3) + ')');
            }
            return getResultSet(this.meta.getImportedKeys(str, str2, str3));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public ResultSet getExportedKeys(String str, String str2, String str3) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getExportedKeys(" + quote(str) + ", " + quote(str2) + ", " + quote(str3) + ')');
            }
            return getResultSet(this.meta.getExportedKeys(str, str2, str3));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public ResultSet getCrossReference(String str, String str2, String str3, String str4, String str5, String str6) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getCrossReference(" + quote(str) + ", " + quote(str2) + ", " + quote(str3) + ", " + quote(str4) + ", " + quote(str5) + ", " + quote(str6) + ')');
            }
            return getResultSet(this.meta.getCrossReference(str, str2, str3, str4, str5, str6));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public ResultSet getUDTs(String str, String str2, String str3, int[] iArr) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getUDTs(" + quote(str) + ", " + quote(str2) + ", " + quote(str3) + ", " + quoteIntArray(iArr) + ')');
            }
            return getResultSet(this.meta.getUDTs(str, str2, str3, iArr));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public ResultSet getTypeInfo() throws SQLException {
        try {
            debugCodeCall("getTypeInfo");
            return getResultSet(this.meta.getTypeInfo());
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public boolean usesLocalFiles() {
        debugCodeCall("usesLocalFiles");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean usesLocalFilePerTable() {
        debugCodeCall("usesLocalFilePerTable");
        return false;
    }

    @Override // java.sql.DatabaseMetaData
    public String getIdentifierQuoteString() {
        debugCodeCall("getIdentifierQuoteString");
        return "\"";
    }

    @Override // java.sql.DatabaseMetaData
    public String getSQLKeywords() throws SQLException {
        try {
            debugCodeCall("getSQLKeywords");
            return this.meta.getSQLKeywords();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public String getNumericFunctions() throws SQLException {
        try {
            debugCodeCall("getNumericFunctions");
            return this.meta.getNumericFunctions();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public String getStringFunctions() throws SQLException {
        try {
            debugCodeCall("getStringFunctions");
            return this.meta.getStringFunctions();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public String getSystemFunctions() throws SQLException {
        try {
            debugCodeCall("getSystemFunctions");
            return this.meta.getSystemFunctions();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public String getTimeDateFunctions() throws SQLException {
        try {
            debugCodeCall("getTimeDateFunctions");
            return this.meta.getTimeDateFunctions();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public String getSearchStringEscape() throws SQLException {
        try {
            debugCodeCall("getSearchStringEscape");
            return this.meta.getSearchStringEscape();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public String getExtraNameCharacters() {
        debugCodeCall("getExtraNameCharacters");
        return "";
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsAlterTableWithAddColumn() {
        debugCodeCall("supportsAlterTableWithAddColumn");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsAlterTableWithDropColumn() {
        debugCodeCall("supportsAlterTableWithDropColumn");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsColumnAliasing() {
        debugCodeCall("supportsColumnAliasing");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean nullPlusNonNullIsNull() {
        debugCodeCall("nullPlusNonNullIsNull");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsConvert() {
        debugCodeCall("supportsConvert");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsConvert(int i, int i2) {
        if (isDebugEnabled()) {
            debugCode("supportsConvert(" + i + ", " + i2 + ')');
            return true;
        }
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsTableCorrelationNames() {
        debugCodeCall("supportsTableCorrelationNames");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsDifferentTableCorrelationNames() {
        debugCodeCall("supportsDifferentTableCorrelationNames");
        return false;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsExpressionsInOrderBy() {
        debugCodeCall("supportsExpressionsInOrderBy");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsOrderByUnrelated() {
        debugCodeCall("supportsOrderByUnrelated");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsGroupBy() {
        debugCodeCall("supportsGroupBy");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsGroupByUnrelated() {
        debugCodeCall("supportsGroupByUnrelated");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsGroupByBeyondSelect() {
        debugCodeCall("supportsGroupByBeyondSelect");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsLikeEscapeClause() {
        debugCodeCall("supportsLikeEscapeClause");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsMultipleResultSets() {
        debugCodeCall("supportsMultipleResultSets");
        return false;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsMultipleTransactions() {
        debugCodeCall("supportsMultipleTransactions");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsNonNullableColumns() {
        debugCodeCall("supportsNonNullableColumns");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsMinimumSQLGrammar() {
        debugCodeCall("supportsMinimumSQLGrammar");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsCoreSQLGrammar() {
        debugCodeCall("supportsCoreSQLGrammar");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsExtendedSQLGrammar() {
        debugCodeCall("supportsExtendedSQLGrammar");
        return false;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsANSI92EntryLevelSQL() {
        debugCodeCall("supportsANSI92EntryLevelSQL");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsANSI92IntermediateSQL() {
        debugCodeCall("supportsANSI92IntermediateSQL");
        return false;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsANSI92FullSQL() {
        debugCodeCall("supportsANSI92FullSQL");
        return false;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsIntegrityEnhancementFacility() {
        debugCodeCall("supportsIntegrityEnhancementFacility");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsOuterJoins() {
        debugCodeCall("supportsOuterJoins");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsFullOuterJoins() {
        debugCodeCall("supportsFullOuterJoins");
        return false;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsLimitedOuterJoins() {
        debugCodeCall("supportsLimitedOuterJoins");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public String getSchemaTerm() {
        debugCodeCall("getSchemaTerm");
        return "schema";
    }

    @Override // java.sql.DatabaseMetaData
    public String getProcedureTerm() {
        debugCodeCall("getProcedureTerm");
        return "procedure";
    }

    @Override // java.sql.DatabaseMetaData
    public String getCatalogTerm() {
        debugCodeCall("getCatalogTerm");
        return "catalog";
    }

    @Override // java.sql.DatabaseMetaData
    public boolean isCatalogAtStart() {
        debugCodeCall("isCatalogAtStart");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public String getCatalogSeparator() {
        debugCodeCall("getCatalogSeparator");
        return ".";
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsSchemasInDataManipulation() {
        debugCodeCall("supportsSchemasInDataManipulation");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsSchemasInProcedureCalls() {
        debugCodeCall("supportsSchemasInProcedureCalls");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsSchemasInTableDefinitions() {
        debugCodeCall("supportsSchemasInTableDefinitions");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsSchemasInIndexDefinitions() {
        debugCodeCall("supportsSchemasInIndexDefinitions");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsSchemasInPrivilegeDefinitions() {
        debugCodeCall("supportsSchemasInPrivilegeDefinitions");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsCatalogsInDataManipulation() {
        debugCodeCall("supportsCatalogsInDataManipulation");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsCatalogsInProcedureCalls() {
        debugCodeCall("supportsCatalogsInProcedureCalls");
        return false;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsCatalogsInTableDefinitions() {
        debugCodeCall("supportsCatalogsInTableDefinitions");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsCatalogsInIndexDefinitions() {
        debugCodeCall("supportsCatalogsInIndexDefinitions");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsCatalogsInPrivilegeDefinitions() {
        debugCodeCall("supportsCatalogsInPrivilegeDefinitions");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsPositionedDelete() {
        debugCodeCall("supportsPositionedDelete");
        return false;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsPositionedUpdate() {
        debugCodeCall("supportsPositionedUpdate");
        return false;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsSelectForUpdate() {
        debugCodeCall("supportsSelectForUpdate");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsStoredProcedures() {
        debugCodeCall("supportsStoredProcedures");
        return false;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsSubqueriesInComparisons() {
        debugCodeCall("supportsSubqueriesInComparisons");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsSubqueriesInExists() {
        debugCodeCall("supportsSubqueriesInExists");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsSubqueriesInIns() {
        debugCodeCall("supportsSubqueriesInIns");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsSubqueriesInQuantifieds() {
        debugCodeCall("supportsSubqueriesInQuantifieds");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsCorrelatedSubqueries() {
        debugCodeCall("supportsCorrelatedSubqueries");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsUnion() {
        debugCodeCall("supportsUnion");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsUnionAll() {
        debugCodeCall("supportsUnionAll");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsOpenCursorsAcrossCommit() {
        debugCodeCall("supportsOpenCursorsAcrossCommit");
        return false;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsOpenCursorsAcrossRollback() {
        debugCodeCall("supportsOpenCursorsAcrossRollback");
        return false;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsOpenStatementsAcrossCommit() {
        debugCodeCall("supportsOpenStatementsAcrossCommit");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsOpenStatementsAcrossRollback() {
        debugCodeCall("supportsOpenStatementsAcrossRollback");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsTransactions() {
        debugCodeCall("supportsTransactions");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsTransactionIsolationLevel(int i) throws SQLException {
        debugCodeCall("supportsTransactionIsolationLevel");
        switch (i) {
            case 1:
            case 2:
            case 4:
            case 6:
            case 8:
                return true;
            case 3:
            case 5:
            case 7:
            default:
                return false;
        }
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsDataDefinitionAndDataManipulationTransactions() {
        debugCodeCall("supportsDataDefinitionAndDataManipulationTransactions");
        return false;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsDataManipulationTransactionsOnly() {
        debugCodeCall("supportsDataManipulationTransactionsOnly");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean dataDefinitionCausesTransactionCommit() {
        debugCodeCall("dataDefinitionCausesTransactionCommit");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean dataDefinitionIgnoredInTransactions() {
        debugCodeCall("dataDefinitionIgnoredInTransactions");
        return false;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsResultSetType(int i) {
        debugCodeCall("supportsResultSetType", i);
        return i != 1005;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsResultSetConcurrency(int i, int i2) {
        if (isDebugEnabled()) {
            debugCode("supportsResultSetConcurrency(" + i + ", " + i2 + ')');
        }
        return i != 1005;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean ownUpdatesAreVisible(int i) {
        debugCodeCall("ownUpdatesAreVisible", i);
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean ownDeletesAreVisible(int i) {
        debugCodeCall("ownDeletesAreVisible", i);
        return false;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean ownInsertsAreVisible(int i) {
        debugCodeCall("ownInsertsAreVisible", i);
        return false;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean othersUpdatesAreVisible(int i) {
        debugCodeCall("othersUpdatesAreVisible", i);
        return false;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean othersDeletesAreVisible(int i) {
        debugCodeCall("othersDeletesAreVisible", i);
        return false;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean othersInsertsAreVisible(int i) {
        debugCodeCall("othersInsertsAreVisible", i);
        return false;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean updatesAreDetected(int i) {
        debugCodeCall("updatesAreDetected", i);
        return false;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean deletesAreDetected(int i) {
        debugCodeCall("deletesAreDetected", i);
        return false;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean insertsAreDetected(int i) {
        debugCodeCall("insertsAreDetected", i);
        return false;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsBatchUpdates() {
        debugCodeCall("supportsBatchUpdates");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean doesMaxRowSizeIncludeBlobs() {
        debugCodeCall("doesMaxRowSizeIncludeBlobs");
        return false;
    }

    @Override // java.sql.DatabaseMetaData
    public int getDefaultTransactionIsolation() {
        debugCodeCall("getDefaultTransactionIsolation");
        return 2;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        debugCodeCall("supportsMixedCaseIdentifiers");
        Session.StaticSettings staticSettings = this.conn.getStaticSettings();
        return (staticSettings.databaseToUpper || staticSettings.databaseToLower || staticSettings.caseInsensitiveIdentifiers) ? false : true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean storesUpperCaseIdentifiers() throws SQLException {
        debugCodeCall("storesUpperCaseIdentifiers");
        return this.conn.getStaticSettings().databaseToUpper;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean storesLowerCaseIdentifiers() throws SQLException {
        debugCodeCall("storesLowerCaseIdentifiers");
        return this.conn.getStaticSettings().databaseToLower;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean storesMixedCaseIdentifiers() throws SQLException {
        debugCodeCall("storesMixedCaseIdentifiers");
        Session.StaticSettings staticSettings = this.conn.getStaticSettings();
        return (staticSettings.databaseToUpper || staticSettings.databaseToLower || !staticSettings.caseInsensitiveIdentifiers) ? false : true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        debugCodeCall("supportsMixedCaseQuotedIdentifiers");
        return !this.conn.getStaticSettings().caseInsensitiveIdentifiers;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        debugCodeCall("storesUpperCaseQuotedIdentifiers");
        return false;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        debugCodeCall("storesLowerCaseQuotedIdentifiers");
        return false;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        debugCodeCall("storesMixedCaseQuotedIdentifiers");
        return this.conn.getStaticSettings().caseInsensitiveIdentifiers;
    }

    @Override // java.sql.DatabaseMetaData
    public int getMaxBinaryLiteralLength() {
        debugCodeCall("getMaxBinaryLiteralLength");
        return 0;
    }

    @Override // java.sql.DatabaseMetaData
    public int getMaxCharLiteralLength() {
        debugCodeCall("getMaxCharLiteralLength");
        return 0;
    }

    @Override // java.sql.DatabaseMetaData
    public int getMaxColumnNameLength() {
        debugCodeCall("getMaxColumnNameLength");
        return 0;
    }

    @Override // java.sql.DatabaseMetaData
    public int getMaxColumnsInGroupBy() {
        debugCodeCall("getMaxColumnsInGroupBy");
        return 0;
    }

    @Override // java.sql.DatabaseMetaData
    public int getMaxColumnsInIndex() {
        debugCodeCall("getMaxColumnsInIndex");
        return 0;
    }

    @Override // java.sql.DatabaseMetaData
    public int getMaxColumnsInOrderBy() {
        debugCodeCall("getMaxColumnsInOrderBy");
        return 0;
    }

    @Override // java.sql.DatabaseMetaData
    public int getMaxColumnsInSelect() {
        debugCodeCall("getMaxColumnsInSelect");
        return 0;
    }

    @Override // java.sql.DatabaseMetaData
    public int getMaxColumnsInTable() {
        debugCodeCall("getMaxColumnsInTable");
        return 0;
    }

    @Override // java.sql.DatabaseMetaData
    public int getMaxConnections() {
        debugCodeCall("getMaxConnections");
        return 0;
    }

    @Override // java.sql.DatabaseMetaData
    public int getMaxCursorNameLength() {
        debugCodeCall("getMaxCursorNameLength");
        return 0;
    }

    @Override // java.sql.DatabaseMetaData
    public int getMaxIndexLength() {
        debugCodeCall("getMaxIndexLength");
        return 0;
    }

    @Override // java.sql.DatabaseMetaData
    public int getMaxSchemaNameLength() {
        debugCodeCall("getMaxSchemaNameLength");
        return 0;
    }

    @Override // java.sql.DatabaseMetaData
    public int getMaxProcedureNameLength() {
        debugCodeCall("getMaxProcedureNameLength");
        return 0;
    }

    @Override // java.sql.DatabaseMetaData
    public int getMaxCatalogNameLength() {
        debugCodeCall("getMaxCatalogNameLength");
        return 0;
    }

    @Override // java.sql.DatabaseMetaData
    public int getMaxRowSize() {
        debugCodeCall("getMaxRowSize");
        return 0;
    }

    @Override // java.sql.DatabaseMetaData
    public int getMaxStatementLength() {
        debugCodeCall("getMaxStatementLength");
        return 0;
    }

    @Override // java.sql.DatabaseMetaData
    public int getMaxStatements() {
        debugCodeCall("getMaxStatements");
        return 0;
    }

    @Override // java.sql.DatabaseMetaData
    public int getMaxTableNameLength() {
        debugCodeCall("getMaxTableNameLength");
        return 0;
    }

    @Override // java.sql.DatabaseMetaData
    public int getMaxTablesInSelect() {
        debugCodeCall("getMaxTablesInSelect");
        return 0;
    }

    @Override // java.sql.DatabaseMetaData
    public int getMaxUserNameLength() {
        debugCodeCall("getMaxUserNameLength");
        return 0;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsSavepoints() {
        debugCodeCall("supportsSavepoints");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsNamedParameters() {
        debugCodeCall("supportsNamedParameters");
        return false;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsMultipleOpenResults() {
        debugCodeCall("supportsMultipleOpenResults");
        return false;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsGetGeneratedKeys() {
        debugCodeCall("supportsGetGeneratedKeys");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public ResultSet getSuperTypes(String str, String str2, String str3) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getSuperTypes(" + quote(str) + ", " + quote(str2) + ", " + quote(str3) + ')');
            }
            return getResultSet(this.meta.getSuperTypes(str, str2, str3));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public ResultSet getSuperTables(String str, String str2, String str3) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getSuperTables(" + quote(str) + ", " + quote(str2) + ", " + quote(str3) + ')');
            }
            return getResultSet(this.meta.getSuperTables(str, str2, str3));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public ResultSet getAttributes(String str, String str2, String str3, String str4) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getAttributes(" + quote(str) + ", " + quote(str2) + ", " + quote(str3) + ", " + quote(str4) + ')');
            }
            return getResultSet(this.meta.getAttributes(str, str2, str3, str4));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsResultSetHoldability(int i) {
        debugCodeCall("supportsResultSetHoldability", i);
        return i == 2;
    }

    @Override // java.sql.DatabaseMetaData
    public int getResultSetHoldability() {
        debugCodeCall("getResultSetHoldability");
        return 2;
    }

    @Override // java.sql.DatabaseMetaData
    public int getDatabaseMajorVersion() throws SQLException {
        try {
            debugCodeCall("getDatabaseMajorVersion");
            return this.meta.getDatabaseMajorVersion();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public int getDatabaseMinorVersion() throws SQLException {
        try {
            debugCodeCall("getDatabaseMinorVersion");
            return this.meta.getDatabaseMinorVersion();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public int getJDBCMajorVersion() {
        debugCodeCall("getJDBCMajorVersion");
        return 4;
    }

    @Override // java.sql.DatabaseMetaData
    public int getJDBCMinorVersion() {
        debugCodeCall("getJDBCMinorVersion");
        return 2;
    }

    @Override // java.sql.DatabaseMetaData
    public int getSQLStateType() {
        debugCodeCall("getSQLStateType");
        return 2;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean locatorsUpdateCopy() {
        debugCodeCall("locatorsUpdateCopy");
        return false;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsStatementPooling() {
        debugCodeCall("supportsStatementPooling");
        return false;
    }

    private void checkClosed() {
        this.conn.checkClosed();
    }

    @Override // java.sql.DatabaseMetaData
    public RowIdLifetime getRowIdLifetime() {
        debugCodeCall("getRowIdLifetime");
        return RowIdLifetime.ROWID_UNSUPPORTED;
    }

    @Override // java.sql.DatabaseMetaData
    public ResultSet getSchemas(String str, String str2) throws SQLException {
        try {
            debugCodeCall("getSchemas(String,String)");
            return getResultSet(this.meta.getSchemas(str, str2));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public boolean supportsStoredFunctionsUsingCallSyntax() {
        debugCodeCall("supportsStoredFunctionsUsingCallSyntax");
        return true;
    }

    @Override // java.sql.DatabaseMetaData
    public boolean autoCommitFailureClosesAllResultSets() {
        debugCodeCall("autoCommitFailureClosesAllResultSets");
        return false;
    }

    @Override // java.sql.DatabaseMetaData
    public ResultSet getClientInfoProperties() throws SQLException {
        Properties clientInfo = this.conn.getClientInfo();
        SimpleResult simpleResult = new SimpleResult();
        simpleResult.addColumn("NAME", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("MAX_LEN", TypeInfo.TYPE_INTEGER);
        simpleResult.addColumn("DEFAULT_VALUE", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("DESCRIPTION", TypeInfo.TYPE_VARCHAR);
        simpleResult.addColumn("VALUE", TypeInfo.TYPE_VARCHAR);
        for (Map.Entry entry : clientInfo.entrySet()) {
            simpleResult.addRow(ValueVarchar.get((String) entry.getKey()), ValueInteger.get(Integer.MAX_VALUE), ValueVarchar.EMPTY, ValueVarchar.EMPTY, ValueVarchar.get((String) entry.getValue()));
        }
        int nextId = getNextId(4);
        debugCodeAssign("ResultSet", 4, nextId, "getClientInfoProperties()");
        return new JdbcResultSet(this.conn, (JdbcStatement) null, (CommandInterface) null, (ResultInterface) simpleResult, nextId, true, false, false);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.sql.Wrapper
    public <T> T unwrap(Class<T> cls) throws SQLException {
        try {
            if (isWrapperFor(cls)) {
                return this;
            }
            throw DbException.getInvalidValueException("iface", cls);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Wrapper
    public boolean isWrapperFor(Class<?> cls) throws SQLException {
        return cls != null && cls.isAssignableFrom(getClass());
    }

    @Override // java.sql.DatabaseMetaData
    public ResultSet getFunctionColumns(String str, String str2, String str3, String str4) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getFunctionColumns(" + quote(str) + ", " + quote(str2) + ", " + quote(str3) + ", " + quote(str4) + ')');
            }
            return getResultSet(this.meta.getFunctionColumns(str, str2, str3, str4));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.DatabaseMetaData
    public ResultSet getFunctions(String str, String str2, String str3) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getFunctions(" + quote(str) + ", " + quote(str2) + ", " + quote(str3) + ')');
            }
            return getResultSet(this.meta.getFunctions(str, str2, str3));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public boolean generatedKeyAlwaysReturned() {
        return true;
    }

    public ResultSet getPseudoColumns(String str, String str2, String str3, String str4) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getPseudoColumns(" + quote(str) + ", " + quote(str2) + ", " + quote(str3) + ", " + quote(str4) + ')');
            }
            return getResultSet(this.meta.getPseudoColumns(str, str2, str3, str4));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public String toString() {
        return getTraceObjectName() + ": " + this.conn;
    }

    private JdbcResultSet getResultSet(ResultInterface resultInterface) {
        return new JdbcResultSet(this.conn, (JdbcStatement) null, (CommandInterface) null, resultInterface, getNextId(4), true, false, false);
    }
}
