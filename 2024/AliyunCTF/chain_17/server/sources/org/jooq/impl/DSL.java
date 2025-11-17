package org.jooq.impl;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Consumer;
import javax.sql.DataSource;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jooq.AggregateFunction;
import org.jooq.AlterDatabaseStep;
import org.jooq.AlterDomainStep;
import org.jooq.AlterIndexOnStep;
import org.jooq.AlterSchemaStep;
import org.jooq.AlterSequenceStep;
import org.jooq.AlterTableStep;
import org.jooq.AlterTypeStep;
import org.jooq.AlterViewStep;
import org.jooq.ArrayAggOrderByStep;
import org.jooq.Asterisk;
import org.jooq.Block;
import org.jooq.Case;
import org.jooq.CaseConditionStep;
import org.jooq.CaseValueStep;
import org.jooq.Catalog;
import org.jooq.CharacterSet;
import org.jooq.Check;
import org.jooq.CheckReturnValue;
import org.jooq.CloseableDSLContext;
import org.jooq.Collation;
import org.jooq.Comment;
import org.jooq.CommentOnIsStep;
import org.jooq.CommonTableExpression;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.ConnectionProvider;
import org.jooq.Constants;
import org.jooq.ConstraintEnforcementStep;
import org.jooq.ConstraintForeignKeyReferencesStep1;
import org.jooq.ConstraintForeignKeyReferencesStep10;
import org.jooq.ConstraintForeignKeyReferencesStep11;
import org.jooq.ConstraintForeignKeyReferencesStep12;
import org.jooq.ConstraintForeignKeyReferencesStep13;
import org.jooq.ConstraintForeignKeyReferencesStep14;
import org.jooq.ConstraintForeignKeyReferencesStep15;
import org.jooq.ConstraintForeignKeyReferencesStep16;
import org.jooq.ConstraintForeignKeyReferencesStep17;
import org.jooq.ConstraintForeignKeyReferencesStep18;
import org.jooq.ConstraintForeignKeyReferencesStep19;
import org.jooq.ConstraintForeignKeyReferencesStep2;
import org.jooq.ConstraintForeignKeyReferencesStep20;
import org.jooq.ConstraintForeignKeyReferencesStep21;
import org.jooq.ConstraintForeignKeyReferencesStep22;
import org.jooq.ConstraintForeignKeyReferencesStep3;
import org.jooq.ConstraintForeignKeyReferencesStep4;
import org.jooq.ConstraintForeignKeyReferencesStep5;
import org.jooq.ConstraintForeignKeyReferencesStep6;
import org.jooq.ConstraintForeignKeyReferencesStep7;
import org.jooq.ConstraintForeignKeyReferencesStep8;
import org.jooq.ConstraintForeignKeyReferencesStep9;
import org.jooq.ConstraintForeignKeyReferencesStepN;
import org.jooq.ConstraintTypeStep;
import org.jooq.Context;
import org.jooq.CreateDatabaseFinalStep;
import org.jooq.CreateDomainAsStep;
import org.jooq.CreateIndexStep;
import org.jooq.CreateSchemaFinalStep;
import org.jooq.CreateSequenceFlagsStep;
import org.jooq.CreateTableElementListStep;
import org.jooq.CreateTypeStep;
import org.jooq.CreateViewAsStep;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.DatePart;
import org.jooq.Delete;
import org.jooq.DeleteUsingStep;
import org.jooq.Domain;
import org.jooq.DropDatabaseFinalStep;
import org.jooq.DropDomainCascadeStep;
import org.jooq.DropIndexOnStep;
import org.jooq.DropSchemaStep;
import org.jooq.DropSequenceFinalStep;
import org.jooq.DropTableStep;
import org.jooq.DropTypeStep;
import org.jooq.DropViewFinalStep;
import org.jooq.False;
import org.jooq.Field;
import org.jooq.FieldOrRow;
import org.jooq.GrantOnStep;
import org.jooq.GroupConcatOrderByStep;
import org.jooq.GroupField;
import org.jooq.Index;
import org.jooq.Insert;
import org.jooq.InsertSetStep;
import org.jooq.InsertValuesStep1;
import org.jooq.InsertValuesStep10;
import org.jooq.InsertValuesStep11;
import org.jooq.InsertValuesStep12;
import org.jooq.InsertValuesStep13;
import org.jooq.InsertValuesStep14;
import org.jooq.InsertValuesStep15;
import org.jooq.InsertValuesStep16;
import org.jooq.InsertValuesStep17;
import org.jooq.InsertValuesStep18;
import org.jooq.InsertValuesStep19;
import org.jooq.InsertValuesStep2;
import org.jooq.InsertValuesStep20;
import org.jooq.InsertValuesStep21;
import org.jooq.InsertValuesStep22;
import org.jooq.InsertValuesStep3;
import org.jooq.InsertValuesStep4;
import org.jooq.InsertValuesStep5;
import org.jooq.InsertValuesStep6;
import org.jooq.InsertValuesStep7;
import org.jooq.InsertValuesStep8;
import org.jooq.InsertValuesStep9;
import org.jooq.InsertValuesStepN;
import org.jooq.JSON;
import org.jooq.JSONArrayAggOrderByStep;
import org.jooq.JSONArrayNullStep;
import org.jooq.JSONB;
import org.jooq.JSONEntry;
import org.jooq.JSONEntryValueStep;
import org.jooq.JSONExistsOnStep;
import org.jooq.JSONObjectAggNullStep;
import org.jooq.JSONObjectNullStep;
import org.jooq.JSONTableColumnsFirstStep;
import org.jooq.JSONValueOnStep;
import org.jooq.Keyword;
import org.jooq.Merge;
import org.jooq.MergeKeyStep1;
import org.jooq.MergeKeyStep10;
import org.jooq.MergeKeyStep11;
import org.jooq.MergeKeyStep12;
import org.jooq.MergeKeyStep13;
import org.jooq.MergeKeyStep14;
import org.jooq.MergeKeyStep15;
import org.jooq.MergeKeyStep16;
import org.jooq.MergeKeyStep17;
import org.jooq.MergeKeyStep18;
import org.jooq.MergeKeyStep19;
import org.jooq.MergeKeyStep2;
import org.jooq.MergeKeyStep20;
import org.jooq.MergeKeyStep21;
import org.jooq.MergeKeyStep22;
import org.jooq.MergeKeyStep3;
import org.jooq.MergeKeyStep4;
import org.jooq.MergeKeyStep5;
import org.jooq.MergeKeyStep6;
import org.jooq.MergeKeyStep7;
import org.jooq.MergeKeyStep8;
import org.jooq.MergeKeyStep9;
import org.jooq.MergeKeyStepN;
import org.jooq.MergeUsingStep;
import org.jooq.Name;
import org.jooq.Null;
import org.jooq.Operator;
import org.jooq.OrderField;
import org.jooq.OrderedAggregateFunction;
import org.jooq.OrderedAggregateFunctionOfDeferredType;
import org.jooq.Param;
import org.jooq.ParamMode;
import org.jooq.Parameter;
import org.jooq.PlainSQL;
import org.jooq.Privilege;
import org.jooq.QualifiedRecord;
import org.jooq.QuantifiedSelect;
import org.jooq.Queries;
import org.jooq.Query;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Record11;
import org.jooq.Record12;
import org.jooq.Record13;
import org.jooq.Record14;
import org.jooq.Record15;
import org.jooq.Record16;
import org.jooq.Record17;
import org.jooq.Record18;
import org.jooq.Record19;
import org.jooq.Record2;
import org.jooq.Record20;
import org.jooq.Record21;
import org.jooq.Record22;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Record5;
import org.jooq.Record6;
import org.jooq.Record7;
import org.jooq.Record8;
import org.jooq.Record9;
import org.jooq.RecordType;
import org.jooq.Result;
import org.jooq.ResultQuery;
import org.jooq.RevokeOnStep;
import org.jooq.Role;
import org.jooq.RollbackToSavepointStep;
import org.jooq.Row;
import org.jooq.Row1;
import org.jooq.Row10;
import org.jooq.Row11;
import org.jooq.Row12;
import org.jooq.Row13;
import org.jooq.Row14;
import org.jooq.Row15;
import org.jooq.Row16;
import org.jooq.Row17;
import org.jooq.Row18;
import org.jooq.Row19;
import org.jooq.Row2;
import org.jooq.Row20;
import org.jooq.Row21;
import org.jooq.Row22;
import org.jooq.Row3;
import org.jooq.Row4;
import org.jooq.Row5;
import org.jooq.Row6;
import org.jooq.Row7;
import org.jooq.Row8;
import org.jooq.Row9;
import org.jooq.RowCountQuery;
import org.jooq.RowN;
import org.jooq.SQL;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.Select;
import org.jooq.SelectField;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SelectSelectStep;
import org.jooq.SelectWhereStep;
import org.jooq.Sequence;
import org.jooq.Statement;
import org.jooq.Support;
import org.jooq.Table;
import org.jooq.TableLike;
import org.jooq.True;
import org.jooq.TruncateIdentityStep;
import org.jooq.Type;
import org.jooq.Update;
import org.jooq.UpdateSetFirstStep;
import org.jooq.User;
import org.jooq.WindowFromFirstLastStep;
import org.jooq.WindowIgnoreNullsStep;
import org.jooq.WindowOverStep;
import org.jooq.WindowSpecificationExcludeStep;
import org.jooq.WindowSpecificationOrderByStep;
import org.jooq.WindowSpecificationRowsAndStep;
import org.jooq.WindowSpecificationRowsStep;
import org.jooq.WithAsStep;
import org.jooq.WithAsStep1;
import org.jooq.WithAsStep10;
import org.jooq.WithAsStep11;
import org.jooq.WithAsStep12;
import org.jooq.WithAsStep13;
import org.jooq.WithAsStep14;
import org.jooq.WithAsStep15;
import org.jooq.WithAsStep16;
import org.jooq.WithAsStep17;
import org.jooq.WithAsStep18;
import org.jooq.WithAsStep19;
import org.jooq.WithAsStep2;
import org.jooq.WithAsStep20;
import org.jooq.WithAsStep21;
import org.jooq.WithAsStep22;
import org.jooq.WithAsStep3;
import org.jooq.WithAsStep4;
import org.jooq.WithAsStep5;
import org.jooq.WithAsStep6;
import org.jooq.WithAsStep7;
import org.jooq.WithAsStep8;
import org.jooq.WithAsStep9;
import org.jooq.WithStep;
import org.jooq.XML;
import org.jooq.XMLAggOrderByStep;
import org.jooq.XMLAttributes;
import org.jooq.XMLExistsPassingStep;
import org.jooq.XMLQueryPassingStep;
import org.jooq.XMLTablePassingStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.exception.SQLDialectNotSupportedException;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;
import org.jooq.tools.jdbc.JDBCUtils;
import org.jooq.types.DayToSecond;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.jooq.types.UShort;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DSL.class */
public class DSL {
    @NotNull
    public static DSLContext using(SQLDialect dialect) {
        return new DefaultDSLContext(dialect, (Settings) null);
    }

    @NotNull
    public static DSLContext using(SQLDialect dialect, Settings settings) {
        return new DefaultDSLContext(dialect, settings);
    }

    @Blocking
    @NotNull
    public static CloseableDSLContext using(String url) {
        if (url.startsWith("r2dbc")) {
            Connection connection = R2DBC.getConnection(url);
            return new DefaultCloseableDSLContext(new DefaultConnectionFactory(connection, true, false), JDBCUtils.dialect(connection));
        }
        try {
            java.sql.Connection connection2 = DriverManager.getConnection(url);
            return new DefaultCloseableDSLContext(new DefaultCloseableConnectionProvider(connection2), JDBCUtils.dialect(connection2));
        } catch (SQLException e) {
            throw Tools.translate("Error when initialising Connection", e);
        }
    }

    @Blocking
    @NotNull
    public static CloseableDSLContext using(String url, String username, String password) {
        if (url.startsWith("r2dbc")) {
            Connection connection = R2DBC.getConnection(url, username, password);
            return new DefaultCloseableDSLContext(new DefaultConnectionFactory(connection, true, false), JDBCUtils.dialect(connection));
        }
        try {
            java.sql.Connection connection2 = DriverManager.getConnection(url, username, password);
            return new DefaultCloseableDSLContext(new DefaultCloseableConnectionProvider(connection2), JDBCUtils.dialect(connection2));
        } catch (SQLException e) {
            throw Tools.translate("Error when initialising Connection", e);
        }
    }

    @Blocking
    @NotNull
    public static CloseableDSLContext using(String url, Properties properties) {
        if (url.startsWith("r2dbc")) {
            Connection connection = R2DBC.getConnection(url, properties);
            return new DefaultCloseableDSLContext(new DefaultConnectionFactory(connection, true, false), JDBCUtils.dialect(connection));
        }
        try {
            java.sql.Connection connection2 = DriverManager.getConnection(url, properties);
            return new DefaultCloseableDSLContext(new DefaultCloseableConnectionProvider(connection2), JDBCUtils.dialect(connection2));
        } catch (SQLException e) {
            throw Tools.translate("Error when initialising Connection", e);
        }
    }

    @NotNull
    public static DSLContext using(java.sql.Connection connection) {
        return new DefaultDSLContext(connection, JDBCUtils.dialect(connection), (Settings) null);
    }

    @NotNull
    public static DSLContext using(java.sql.Connection connection, SQLDialect dialect) {
        return new DefaultDSLContext(connection, dialect, (Settings) null);
    }

    @NotNull
    public static DSLContext using(java.sql.Connection connection, Settings settings) {
        return new DefaultDSLContext(connection, JDBCUtils.dialect(connection), settings);
    }

    @NotNull
    public static DSLContext using(java.sql.Connection connection, SQLDialect dialect, Settings settings) {
        return new DefaultDSLContext(connection, dialect, settings);
    }

    @NotNull
    public static DSLContext using(DataSource datasource, SQLDialect dialect) {
        return new DefaultDSLContext(datasource, dialect);
    }

    @NotNull
    public static DSLContext using(DataSource datasource, SQLDialect dialect, Settings settings) {
        return new DefaultDSLContext(datasource, dialect, settings);
    }

    @NotNull
    public static DSLContext using(ConnectionProvider connectionProvider, SQLDialect dialect) {
        return new DefaultDSLContext(connectionProvider, dialect);
    }

    @NotNull
    public static DSLContext using(ConnectionProvider connectionProvider, SQLDialect dialect, Settings settings) {
        return new DefaultDSLContext(connectionProvider, dialect, settings);
    }

    @NotNull
    public static DSLContext using(ConnectionFactory connectionFactory) {
        return new DefaultDSLContext(connectionFactory, JDBCUtils.dialect(connectionFactory));
    }

    @NotNull
    public static DSLContext using(ConnectionFactory connectionFactory, SQLDialect dialect) {
        return new DefaultDSLContext(connectionFactory, dialect);
    }

    @NotNull
    public static DSLContext using(ConnectionFactory connectionFactory, SQLDialect dialect, Settings settings) {
        return new DefaultDSLContext(connectionFactory, dialect, settings);
    }

    @NotNull
    public static DSLContext using(Connection connection) {
        return new DefaultDSLContext(new DefaultConnectionFactory(connection), JDBCUtils.dialect(connection));
    }

    @NotNull
    public static DSLContext using(Connection connection, SQLDialect dialect) {
        return new DefaultDSLContext(new DefaultConnectionFactory(connection), dialect);
    }

    @NotNull
    public static DSLContext using(Connection connection, SQLDialect dialect, Settings settings) {
        return new DefaultDSLContext(new DefaultConnectionFactory(connection), dialect, settings);
    }

    @NotNull
    public static DSLContext using(Configuration configuration) {
        return new DefaultDSLContext(configuration);
    }

    @Support
    @NotNull
    public static WithAsStep with(String alias) {
        return new WithImpl(null, false).with(alias);
    }

    @Support
    @NotNull
    public static WithAsStep with(String alias, String... fieldAliases) {
        return new WithImpl(null, false).with(alias, fieldAliases);
    }

    @Support
    @NotNull
    public static WithAsStep with(String alias, Collection<String> fieldAliases) {
        return new WithImpl(null, false).with(alias, fieldAliases);
    }

    @Support
    @NotNull
    public static WithAsStep with(Name alias) {
        return new WithImpl(null, false).with(alias);
    }

    @Support
    @NotNull
    public static WithAsStep with(Name alias, Name... fieldAliases) {
        return new WithImpl(null, false).with(alias, fieldAliases);
    }

    @Support
    @NotNull
    public static WithAsStep with(Name alias, Collection<? extends Name> fieldAliases) {
        return new WithImpl(null, false).with(alias, fieldAliases);
    }

    @Support
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static WithAsStep with(String alias, java.util.function.Function<? super Field<?>, ? extends String> fieldNameFunction) {
        return new WithImpl(null, false).with(alias, fieldNameFunction);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep1 with(String alias, String fieldAlias1) {
        return new WithImpl(null, false).with(alias, fieldAlias1);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep2 with(String alias, String fieldAlias1, String fieldAlias2) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep3 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep4 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep5 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep6 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep7 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep8 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep9 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep10 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep11 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep12 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep13 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep14 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep15 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep16 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep17 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep18 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep19 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18, String fieldAlias19) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep20 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18, String fieldAlias19, String fieldAlias20) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep21 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18, String fieldAlias19, String fieldAlias20, String fieldAlias21) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20, fieldAlias21);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep22 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18, String fieldAlias19, String fieldAlias20, String fieldAlias21, String fieldAlias22) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20, fieldAlias21, fieldAlias22);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep1 with(Name alias, Name fieldAlias1) {
        return new WithImpl(null, false).with(alias, fieldAlias1);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep2 with(Name alias, Name fieldAlias1, Name fieldAlias2) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep3 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep4 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep5 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep6 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep7 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep8 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep9 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep10 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep11 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep12 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep13 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep14 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep15 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep16 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep17 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep18 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep19 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18, Name fieldAlias19) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep20 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18, Name fieldAlias19, Name fieldAlias20) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep21 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18, Name fieldAlias19, Name fieldAlias20, Name fieldAlias21) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20, fieldAlias21);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static WithAsStep22 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18, Name fieldAlias19, Name fieldAlias20, Name fieldAlias21, Name fieldAlias22) {
        return new WithImpl(null, false).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20, fieldAlias21, fieldAlias22);
    }

    @Support
    @NotNull
    public static WithStep with(CommonTableExpression<?>... tables) {
        return new WithImpl(null, false).with(tables);
    }

    @Support
    @NotNull
    public static WithStep with(Collection<? extends CommonTableExpression<?>> tables) {
        return new WithImpl(null, false).with(tables);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep withRecursive(String alias) {
        return new WithImpl(null, true).with(alias);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep withRecursive(String alias, String... fieldAliases) {
        return new WithImpl(null, true).with(alias, fieldAliases);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep withRecursive(String alias, Collection<String> fieldAliases) {
        return new WithImpl(null, true).with(alias, fieldAliases);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep withRecursive(Name alias) {
        return new WithImpl(null, true).with(alias);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep withRecursive(Name alias, Name... fieldAliases) {
        return new WithImpl(null, true).with(alias, fieldAliases);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep withRecursive(Name alias, Collection<? extends Name> fieldAliases) {
        return new WithImpl(null, true).with(alias, fieldAliases);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static WithAsStep withRecursive(String alias, java.util.function.Function<? super Field<?>, ? extends String> fieldNameFunction) {
        return new WithImpl(null, true).with(alias, fieldNameFunction);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep1 withRecursive(String alias, String fieldAlias1) {
        return new WithImpl(null, true).with(alias, fieldAlias1);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep2 withRecursive(String alias, String fieldAlias1, String fieldAlias2) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep3 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep4 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep5 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep6 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep7 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep8 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep9 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep10 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep11 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep12 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep13 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep14 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep15 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep16 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep17 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep18 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep19 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18, String fieldAlias19) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep20 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18, String fieldAlias19, String fieldAlias20) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep21 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18, String fieldAlias19, String fieldAlias20, String fieldAlias21) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20, fieldAlias21);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep22 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18, String fieldAlias19, String fieldAlias20, String fieldAlias21, String fieldAlias22) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20, fieldAlias21, fieldAlias22);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep1 withRecursive(Name alias, Name fieldAlias1) {
        return new WithImpl(null, true).with(alias, fieldAlias1);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep2 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep3 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep4 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep5 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep6 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep7 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep8 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep9 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep10 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep11 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep12 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep13 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep14 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep15 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep16 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep17 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep18 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep19 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18, Name fieldAlias19) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep20 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18, Name fieldAlias19, Name fieldAlias20) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep21 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18, Name fieldAlias19, Name fieldAlias20, Name fieldAlias21) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20, fieldAlias21);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithAsStep22 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18, Name fieldAlias19, Name fieldAlias20, Name fieldAlias21, Name fieldAlias22) {
        return new WithImpl(null, true).with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20, fieldAlias21, fieldAlias22);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithStep withRecursive(CommonTableExpression<?>... tables) {
        return new WithImpl(null, true).with(tables);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WithStep withRecursive(Collection<? extends CommonTableExpression<?>> tables) {
        return new WithImpl(null, true).with(tables);
    }

    @Support
    @NotNull
    public static <R extends Record> SelectWhereStep<R> selectFrom(TableLike<R> table) {
        return dsl().selectFrom(table);
    }

    @Support
    @NotNull
    public static SelectWhereStep<Record> selectFrom(Name table) {
        return dsl().selectFrom(table);
    }

    @PlainSQL
    @Support
    @NotNull
    public static SelectWhereStep<Record> selectFrom(SQL sql) {
        return dsl().selectFrom(sql);
    }

    @PlainSQL
    @Support
    @NotNull
    public static SelectWhereStep<Record> selectFrom(String sql) {
        return dsl().selectFrom(sql);
    }

    @PlainSQL
    @Support
    @NotNull
    public static SelectWhereStep<Record> selectFrom(String sql, Object... bindings) {
        return dsl().selectFrom(sql, bindings);
    }

    @PlainSQL
    @Support
    @NotNull
    public static SelectWhereStep<Record> selectFrom(String sql, QueryPart... parts) {
        return dsl().selectFrom(sql, parts);
    }

    @Support
    @NotNull
    public static SelectSelectStep<Record> select(Collection<? extends SelectFieldOrAsterisk> fields) {
        return dsl().select(fields);
    }

    @Support
    @NotNull
    public static SelectSelectStep<Record> select(SelectFieldOrAsterisk... fields) {
        return dsl().select(fields);
    }

    @Support
    @NotNull
    public static <T1> SelectSelectStep<Record1<T1>> select(SelectField<T1> selectField) {
        return (SelectSelectStep<Record1<T1>>) select(selectField);
    }

    @Support
    @NotNull
    public static <T1, T2> SelectSelectStep<Record2<T1, T2>> select(SelectField<T1> selectField, SelectField<T2> selectField2) {
        return (SelectSelectStep<Record2<T1, T2>>) select(selectField, selectField2);
    }

    @Support
    @NotNull
    public static <T1, T2, T3> SelectSelectStep<Record3<T1, T2, T3>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3) {
        return (SelectSelectStep<Record3<T1, T2, T3>>) select(selectField, selectField2, selectField3);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4> SelectSelectStep<Record4<T1, T2, T3, T4>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4) {
        return (SelectSelectStep<Record4<T1, T2, T3, T4>>) select(selectField, selectField2, selectField3, selectField4);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5> SelectSelectStep<Record5<T1, T2, T3, T4, T5>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5) {
        return (SelectSelectStep<Record5<T1, T2, T3, T4, T5>>) select(selectField, selectField2, selectField3, selectField4, selectField5);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6> SelectSelectStep<Record6<T1, T2, T3, T4, T5, T6>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6) {
        return (SelectSelectStep<Record6<T1, T2, T3, T4, T5, T6>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7> SelectSelectStep<Record7<T1, T2, T3, T4, T5, T6, T7>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7) {
        return (SelectSelectStep<Record7<T1, T2, T3, T4, T5, T6, T7>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8> SelectSelectStep<Record8<T1, T2, T3, T4, T5, T6, T7, T8>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8) {
        return (SelectSelectStep<Record8<T1, T2, T3, T4, T5, T6, T7, T8>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> SelectSelectStep<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9) {
        return (SelectSelectStep<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> SelectSelectStep<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10) {
        return (SelectSelectStep<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> SelectSelectStep<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11) {
        return (SelectSelectStep<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> SelectSelectStep<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12) {
        return (SelectSelectStep<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> SelectSelectStep<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13) {
        return (SelectSelectStep<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> SelectSelectStep<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14) {
        return (SelectSelectStep<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> SelectSelectStep<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15) {
        return (SelectSelectStep<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> SelectSelectStep<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16) {
        return (SelectSelectStep<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> SelectSelectStep<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17) {
        return (SelectSelectStep<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> SelectSelectStep<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18) {
        return (SelectSelectStep<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> SelectSelectStep<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19) {
        return (SelectSelectStep<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> SelectSelectStep<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20) {
        return (SelectSelectStep<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19, selectField20);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> SelectSelectStep<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20, SelectField<T21> selectField21) {
        return (SelectSelectStep<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19, selectField20, selectField21);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> SelectSelectStep<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20, SelectField<T21> selectField21, SelectField<T22> selectField22) {
        return (SelectSelectStep<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19, selectField20, selectField21, selectField22);
    }

    @Support
    @NotNull
    public static SelectSelectStep<Record> selectDistinct(Collection<? extends SelectFieldOrAsterisk> fields) {
        return dsl().selectDistinct(fields);
    }

    @Support
    @NotNull
    public static SelectSelectStep<Record> selectDistinct(SelectFieldOrAsterisk... fields) {
        return dsl().selectDistinct(fields);
    }

    @Support
    @NotNull
    public static <T1> SelectSelectStep<Record1<T1>> selectDistinct(SelectField<T1> selectField) {
        return (SelectSelectStep<Record1<T1>>) selectDistinct(selectField);
    }

    @Support
    @NotNull
    public static <T1, T2> SelectSelectStep<Record2<T1, T2>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2) {
        return (SelectSelectStep<Record2<T1, T2>>) selectDistinct(selectField, selectField2);
    }

    @Support
    @NotNull
    public static <T1, T2, T3> SelectSelectStep<Record3<T1, T2, T3>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3) {
        return (SelectSelectStep<Record3<T1, T2, T3>>) selectDistinct(selectField, selectField2, selectField3);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4> SelectSelectStep<Record4<T1, T2, T3, T4>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4) {
        return (SelectSelectStep<Record4<T1, T2, T3, T4>>) selectDistinct(selectField, selectField2, selectField3, selectField4);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5> SelectSelectStep<Record5<T1, T2, T3, T4, T5>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5) {
        return (SelectSelectStep<Record5<T1, T2, T3, T4, T5>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6> SelectSelectStep<Record6<T1, T2, T3, T4, T5, T6>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6) {
        return (SelectSelectStep<Record6<T1, T2, T3, T4, T5, T6>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7> SelectSelectStep<Record7<T1, T2, T3, T4, T5, T6, T7>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7) {
        return (SelectSelectStep<Record7<T1, T2, T3, T4, T5, T6, T7>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8> SelectSelectStep<Record8<T1, T2, T3, T4, T5, T6, T7, T8>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8) {
        return (SelectSelectStep<Record8<T1, T2, T3, T4, T5, T6, T7, T8>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> SelectSelectStep<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9) {
        return (SelectSelectStep<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> SelectSelectStep<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10) {
        return (SelectSelectStep<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> SelectSelectStep<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11) {
        return (SelectSelectStep<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> SelectSelectStep<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12) {
        return (SelectSelectStep<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> SelectSelectStep<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13) {
        return (SelectSelectStep<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> SelectSelectStep<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14) {
        return (SelectSelectStep<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> SelectSelectStep<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15) {
        return (SelectSelectStep<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> SelectSelectStep<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16) {
        return (SelectSelectStep<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> SelectSelectStep<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17) {
        return (SelectSelectStep<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> SelectSelectStep<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18) {
        return (SelectSelectStep<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> SelectSelectStep<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19) {
        return (SelectSelectStep<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> SelectSelectStep<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20) {
        return (SelectSelectStep<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19, selectField20);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> SelectSelectStep<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20, SelectField<T21> selectField21) {
        return (SelectSelectStep<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19, selectField20, selectField21);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> SelectSelectStep<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20, SelectField<T21> selectField21, SelectField<T22> selectField22) {
        return (SelectSelectStep<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19, selectField20, selectField21, selectField22);
    }

    @Support
    @NotNull
    public static SelectSelectStep<Record1<Integer>> selectZero() {
        return dsl().selectZero();
    }

    @Support
    @NotNull
    public static SelectSelectStep<Record1<Integer>> selectOne() {
        return dsl().selectOne();
    }

    @Support
    @NotNull
    public static SelectSelectStep<Record1<Integer>> selectCount() {
        return dsl().selectCount();
    }

    @Support
    @NotNull
    public static <R extends Record> InsertSetStep<R> insertInto(Table<R> into) {
        return dsl().insertInto(into);
    }

    @Support
    @NotNull
    public static <R extends Record, T1> InsertValuesStep1<R, T1> insertInto(Table<R> into, Field<T1> field1) {
        return (InsertValuesStep1) insertInto(into, (Field<?>[]) new Field[]{field1});
    }

    @Support
    @NotNull
    public static <R extends Record, T1, T2> InsertValuesStep2<R, T1, T2> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2) {
        return (InsertValuesStep2) insertInto(into, (Field<?>[]) new Field[]{field1, field2});
    }

    @Support
    @NotNull
    public static <R extends Record, T1, T2, T3> InsertValuesStep3<R, T1, T2, T3> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3) {
        return (InsertValuesStep3) insertInto(into, (Field<?>[]) new Field[]{field1, field2, field3});
    }

    @Support
    @NotNull
    public static <R extends Record, T1, T2, T3, T4> InsertValuesStep4<R, T1, T2, T3, T4> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4) {
        return (InsertValuesStep4) insertInto(into, (Field<?>[]) new Field[]{field1, field2, field3, field4});
    }

    @Support
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5> InsertValuesStep5<R, T1, T2, T3, T4, T5> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5) {
        return (InsertValuesStep5) insertInto(into, (Field<?>[]) new Field[]{field1, field2, field3, field4, field5});
    }

    @Support
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6> InsertValuesStep6<R, T1, T2, T3, T4, T5, T6> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6) {
        return (InsertValuesStep6) insertInto(into, (Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6});
    }

    @Support
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7> InsertValuesStep7<R, T1, T2, T3, T4, T5, T6, T7> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7) {
        return (InsertValuesStep7) insertInto(into, (Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7});
    }

    @Support
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8> InsertValuesStep8<R, T1, T2, T3, T4, T5, T6, T7, T8> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8) {
        return (InsertValuesStep8) insertInto(into, (Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8});
    }

    @Support
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9> InsertValuesStep9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9) {
        return (InsertValuesStep9) insertInto(into, (Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9});
    }

    @Support
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> InsertValuesStep10<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10) {
        return (InsertValuesStep10) insertInto(into, (Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10});
    }

    @Support
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> InsertValuesStep11<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11) {
        return (InsertValuesStep11) insertInto(into, (Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11});
    }

    @Support
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> InsertValuesStep12<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12) {
        return (InsertValuesStep12) insertInto(into, (Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12});
    }

    @Support
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> InsertValuesStep13<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13) {
        return (InsertValuesStep13) insertInto(into, (Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13});
    }

    @Support
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> InsertValuesStep14<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14) {
        return (InsertValuesStep14) insertInto(into, (Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14});
    }

    @Support
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> InsertValuesStep15<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15) {
        return (InsertValuesStep15) insertInto(into, (Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15});
    }

    @Support
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> InsertValuesStep16<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16) {
        return (InsertValuesStep16) insertInto(into, (Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16});
    }

    @Support
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> InsertValuesStep17<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17) {
        return (InsertValuesStep17) insertInto(into, (Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17});
    }

    @Support
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> InsertValuesStep18<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18) {
        return (InsertValuesStep18) insertInto(into, (Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18});
    }

    @Support
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> InsertValuesStep19<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19) {
        return (InsertValuesStep19) insertInto(into, (Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19});
    }

    @Support
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> InsertValuesStep20<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20) {
        return (InsertValuesStep20) insertInto(into, (Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20});
    }

    @Support
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> InsertValuesStep21<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21) {
        return (InsertValuesStep21) insertInto(into, (Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21});
    }

    @Support
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> InsertValuesStep22<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22) {
        return (InsertValuesStep22) insertInto(into, (Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22});
    }

    @Support
    @NotNull
    public static <R extends Record> InsertValuesStepN<R> insertInto(Table<R> into, Field<?>... fields) {
        return dsl().insertInto(into, fields);
    }

    @Support
    @NotNull
    public static <R extends Record> InsertValuesStepN<R> insertInto(Table<R> into, Collection<? extends Field<?>> fields) {
        return dsl().insertInto(into, fields);
    }

    @Support
    @NotNull
    public static <R extends Record> UpdateSetFirstStep<R> update(Table<R> table) {
        return dsl().update(table);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <R extends Record> MergeUsingStep<R> mergeInto(Table<R> table) {
        return dsl().mergeInto(table);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static <R extends Record, T1> MergeKeyStep1<R, T1> mergeInto(Table<R> table, Field<T1> field1) {
        return using(new DefaultConfiguration()).mergeInto(table, field1);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static <R extends Record, T1, T2> MergeKeyStep2<R, T1, T2> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2) {
        return using(new DefaultConfiguration()).mergeInto(table, field1, field2);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static <R extends Record, T1, T2, T3> MergeKeyStep3<R, T1, T2, T3> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3) {
        return using(new DefaultConfiguration()).mergeInto(table, field1, field2, field3);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static <R extends Record, T1, T2, T3, T4> MergeKeyStep4<R, T1, T2, T3, T4> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4) {
        return using(new DefaultConfiguration()).mergeInto(table, field1, field2, field3, field4);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5> MergeKeyStep5<R, T1, T2, T3, T4, T5> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5) {
        return using(new DefaultConfiguration()).mergeInto(table, field1, field2, field3, field4, field5);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6> MergeKeyStep6<R, T1, T2, T3, T4, T5, T6> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6) {
        return using(new DefaultConfiguration()).mergeInto(table, field1, field2, field3, field4, field5, field6);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7> MergeKeyStep7<R, T1, T2, T3, T4, T5, T6, T7> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7) {
        return using(new DefaultConfiguration()).mergeInto(table, field1, field2, field3, field4, field5, field6, field7);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8> MergeKeyStep8<R, T1, T2, T3, T4, T5, T6, T7, T8> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8) {
        return using(new DefaultConfiguration()).mergeInto(table, field1, field2, field3, field4, field5, field6, field7, field8);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9> MergeKeyStep9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9) {
        return using(new DefaultConfiguration()).mergeInto(table, field1, field2, field3, field4, field5, field6, field7, field8, field9);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> MergeKeyStep10<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10) {
        return using(new DefaultConfiguration()).mergeInto(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> MergeKeyStep11<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11) {
        return using(new DefaultConfiguration()).mergeInto(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> MergeKeyStep12<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12) {
        return using(new DefaultConfiguration()).mergeInto(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> MergeKeyStep13<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13) {
        return using(new DefaultConfiguration()).mergeInto(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> MergeKeyStep14<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14) {
        return using(new DefaultConfiguration()).mergeInto(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> MergeKeyStep15<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15) {
        return using(new DefaultConfiguration()).mergeInto(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> MergeKeyStep16<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16) {
        return using(new DefaultConfiguration()).mergeInto(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> MergeKeyStep17<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17) {
        return using(new DefaultConfiguration()).mergeInto(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> MergeKeyStep18<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18) {
        return using(new DefaultConfiguration()).mergeInto(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> MergeKeyStep19<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19) {
        return using(new DefaultConfiguration()).mergeInto(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> MergeKeyStep20<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20) {
        return using(new DefaultConfiguration()).mergeInto(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> MergeKeyStep21<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21) {
        return using(new DefaultConfiguration()).mergeInto(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> MergeKeyStep22<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22) {
        return using(new DefaultConfiguration()).mergeInto(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static <R extends Record> MergeKeyStepN<R> mergeInto(Table<R> table, Field<?>... fields) {
        return dsl().mergeInto(table, fields);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static <R extends Record> MergeKeyStepN<R> mergeInto(Table<R> table, Collection<? extends Field<?>> fields) {
        return dsl().mergeInto(table, fields);
    }

    @Support
    @NotNull
    public static <R extends Record> DeleteUsingStep<R> deleteFrom(Table<R> table) {
        return dsl().deleteFrom(table);
    }

    @Support
    @NotNull
    public static <R extends Record> DeleteUsingStep<R> delete(Table<R> table) {
        return dsl().deleteFrom(table);
    }

    @Support
    @NotNull
    public static Comment comment(String comment) {
        return StringUtils.isEmpty(comment) ? CommentImpl.NO_COMMENT : new CommentImpl(comment);
    }

    @Support
    @NotNull
    public static ConstraintTypeStep constraint() {
        return new ConstraintImpl();
    }

    @Support
    @NotNull
    public static ConstraintTypeStep constraint(Name name) {
        return new ConstraintImpl(name);
    }

    @Support
    @NotNull
    public static ConstraintTypeStep constraint(String name) {
        return constraint(name(name));
    }

    @Support
    @NotNull
    public static ConstraintEnforcementStep primaryKey(String... fields) {
        return constraint().primaryKey(fields);
    }

    @Support
    @NotNull
    public static ConstraintEnforcementStep primaryKey(Name... fields) {
        return constraint().primaryKey(fields);
    }

    @Support
    @NotNull
    public static ConstraintEnforcementStep primaryKey(Field<?>... fields) {
        return constraint().primaryKey(fields);
    }

    @Support
    @NotNull
    public static ConstraintEnforcementStep primaryKey(Collection<? extends Field<?>> fields) {
        return constraint().primaryKey(fields);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStepN foreignKey(String... fields) {
        return constraint().foreignKey(fields);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStepN foreignKey(Name... fields) {
        return constraint().foreignKey(fields);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStepN foreignKey(Field<?>... fields) {
        return constraint().foreignKey(fields);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStepN foreignKey(Collection<? extends Field<?>> fields) {
        return constraint().foreignKey(fields);
    }

    @Support
    @NotNull
    public static <T1> ConstraintForeignKeyReferencesStep1<T1> foreignKey(Field<T1> field1) {
        return constraint().foreignKey(field1);
    }

    @Support
    @NotNull
    public static <T1, T2> ConstraintForeignKeyReferencesStep2<T1, T2> foreignKey(Field<T1> field1, Field<T2> field2) {
        return constraint().foreignKey(field1, field2);
    }

    @Support
    @NotNull
    public static <T1, T2, T3> ConstraintForeignKeyReferencesStep3<T1, T2, T3> foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3) {
        return constraint().foreignKey(field1, field2, field3);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4> ConstraintForeignKeyReferencesStep4<T1, T2, T3, T4> foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4) {
        return constraint().foreignKey(field1, field2, field3, field4);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5> ConstraintForeignKeyReferencesStep5<T1, T2, T3, T4, T5> foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5) {
        return constraint().foreignKey(field1, field2, field3, field4, field5);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6> ConstraintForeignKeyReferencesStep6<T1, T2, T3, T4, T5, T6> foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7> ConstraintForeignKeyReferencesStep7<T1, T2, T3, T4, T5, T6, T7> foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8> ConstraintForeignKeyReferencesStep8<T1, T2, T3, T4, T5, T6, T7, T8> foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> ConstraintForeignKeyReferencesStep9<T1, T2, T3, T4, T5, T6, T7, T8, T9> foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> ConstraintForeignKeyReferencesStep10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> ConstraintForeignKeyReferencesStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> ConstraintForeignKeyReferencesStep12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> ConstraintForeignKeyReferencesStep13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> ConstraintForeignKeyReferencesStep14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> ConstraintForeignKeyReferencesStep15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> ConstraintForeignKeyReferencesStep16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> ConstraintForeignKeyReferencesStep17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> ConstraintForeignKeyReferencesStep18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> ConstraintForeignKeyReferencesStep19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> ConstraintForeignKeyReferencesStep20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> ConstraintForeignKeyReferencesStep21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> ConstraintForeignKeyReferencesStep22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep1<?> foreignKey(Name field1) {
        return constraint().foreignKey(field1);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep2<?, ?> foreignKey(Name field1, Name field2) {
        return constraint().foreignKey(field1, field2);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep3<?, ?, ?> foreignKey(Name field1, Name field2, Name field3) {
        return constraint().foreignKey(field1, field2, field3);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep4<?, ?, ?, ?> foreignKey(Name field1, Name field2, Name field3, Name field4) {
        return constraint().foreignKey(field1, field2, field3, field4);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep5<?, ?, ?, ?, ?> foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5) {
        return constraint().foreignKey(field1, field2, field3, field4, field5);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep6<?, ?, ?, ?, ?, ?> foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep7<?, ?, ?, ?, ?, ?, ?> foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep8<?, ?, ?, ?, ?, ?, ?, ?> foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep9<?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep10<?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep11<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep12<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep13<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep14<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13, Name field14) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep15<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13, Name field14, Name field15) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep16<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13, Name field14, Name field15, Name field16) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep17<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13, Name field14, Name field15, Name field16, Name field17) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep18<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13, Name field14, Name field15, Name field16, Name field17, Name field18) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep19<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13, Name field14, Name field15, Name field16, Name field17, Name field18, Name field19) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep20<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13, Name field14, Name field15, Name field16, Name field17, Name field18, Name field19, Name field20) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep21<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13, Name field14, Name field15, Name field16, Name field17, Name field18, Name field19, Name field20, Name field21) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep22<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13, Name field14, Name field15, Name field16, Name field17, Name field18, Name field19, Name field20, Name field21, Name field22) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep1<?> foreignKey(String field1) {
        return constraint().foreignKey(field1);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep2<?, ?> foreignKey(String field1, String field2) {
        return constraint().foreignKey(field1, field2);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep3<?, ?, ?> foreignKey(String field1, String field2, String field3) {
        return constraint().foreignKey(field1, field2, field3);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep4<?, ?, ?, ?> foreignKey(String field1, String field2, String field3, String field4) {
        return constraint().foreignKey(field1, field2, field3, field4);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep5<?, ?, ?, ?, ?> foreignKey(String field1, String field2, String field3, String field4, String field5) {
        return constraint().foreignKey(field1, field2, field3, field4, field5);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep6<?, ?, ?, ?, ?, ?> foreignKey(String field1, String field2, String field3, String field4, String field5, String field6) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep7<?, ?, ?, ?, ?, ?, ?> foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep8<?, ?, ?, ?, ?, ?, ?, ?> foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep9<?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep10<?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep11<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep12<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep13<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep14<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13, String field14) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep15<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13, String field14, String field15) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep16<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13, String field14, String field15, String field16) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep17<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13, String field14, String field15, String field16, String field17) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep18<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13, String field14, String field15, String field16, String field17, String field18) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep19<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13, String field14, String field15, String field16, String field17, String field18, String field19) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep20<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13, String field14, String field15, String field16, String field17, String field18, String field19, String field20) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep21<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13, String field14, String field15, String field16, String field17, String field18, String field19, String field20, String field21) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21);
    }

    @Support
    @NotNull
    public static ConstraintForeignKeyReferencesStep22<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13, String field14, String field15, String field16, String field17, String field18, String field19, String field20, String field21, String field22) {
        return constraint().foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22);
    }

    @Support
    @NotNull
    public static ConstraintEnforcementStep unique(String... fields) {
        return constraint().unique(fields);
    }

    @Support
    @NotNull
    public static ConstraintEnforcementStep unique(Name... fields) {
        return constraint().unique(fields);
    }

    @Support
    @NotNull
    public static ConstraintEnforcementStep unique(Field<?>... fields) {
        return constraint().unique(fields);
    }

    @Support
    @NotNull
    public static ConstraintEnforcementStep unique(Collection<? extends Field<?>> fields) {
        return constraint().unique(fields);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static ConstraintEnforcementStep check(Condition condition) {
        return constraint().check(condition);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static AlterDatabaseStep alterDatabase(String database) {
        return dsl().alterDatabase(database);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static AlterDatabaseStep alterDatabase(Name database) {
        return dsl().alterDatabase(database);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static AlterDatabaseStep alterDatabase(Catalog database) {
        return dsl().alterDatabase(database);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static AlterDatabaseStep alterDatabaseIfExists(String database) {
        return dsl().alterDatabaseIfExists(database);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static AlterDatabaseStep alterDatabaseIfExists(Name database) {
        return dsl().alterDatabaseIfExists(database);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static AlterDatabaseStep alterDatabaseIfExists(Catalog database) {
        return dsl().alterDatabaseIfExists(database);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static <T> AlterDomainStep<T> alterDomain(String domain) {
        return dsl().alterDomain(domain);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static <T> AlterDomainStep<T> alterDomain(Name domain) {
        return dsl().alterDomain(domain);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static <T> AlterDomainStep<T> alterDomain(Domain<T> domain) {
        return dsl().alterDomain(domain);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static <T> AlterDomainStep<T> alterDomainIfExists(String domain) {
        return dsl().alterDomainIfExists(domain);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static <T> AlterDomainStep<T> alterDomainIfExists(Name domain) {
        return dsl().alterDomainIfExists(domain);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static <T> AlterDomainStep<T> alterDomainIfExists(Domain<T> domain) {
        return dsl().alterDomainIfExists(domain);
    }

    @Support({SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    public static AlterIndexOnStep alterIndex(String index) {
        return dsl().alterIndex(index);
    }

    @Support({SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    public static AlterIndexOnStep alterIndex(Name index) {
        return dsl().alterIndex(index);
    }

    @Support({SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    public static AlterIndexOnStep alterIndex(Index index) {
        return dsl().alterIndex(index);
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    public static AlterIndexOnStep alterIndexIfExists(String index) {
        return dsl().alterIndexIfExists(index);
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    public static AlterIndexOnStep alterIndexIfExists(Name index) {
        return dsl().alterIndexIfExists(index);
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    public static AlterIndexOnStep alterIndexIfExists(Index index) {
        return dsl().alterIndexIfExists(index);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    public static AlterSchemaStep alterSchema(String schema) {
        return dsl().alterSchema(schema);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    public static AlterSchemaStep alterSchema(Name schema) {
        return dsl().alterSchema(schema);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    public static AlterSchemaStep alterSchema(Schema schema) {
        return dsl().alterSchema(schema);
    }

    @Support({SQLDialect.H2})
    @CheckReturnValue
    @NotNull
    public static AlterSchemaStep alterSchemaIfExists(String schema) {
        return dsl().alterSchemaIfExists(schema);
    }

    @Support({SQLDialect.H2})
    @CheckReturnValue
    @NotNull
    public static AlterSchemaStep alterSchemaIfExists(Name schema) {
        return dsl().alterSchemaIfExists(schema);
    }

    @Support({SQLDialect.H2})
    @CheckReturnValue
    @NotNull
    public static AlterSchemaStep alterSchemaIfExists(Schema schema) {
        return dsl().alterSchemaIfExists(schema);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static AlterSequenceStep<Number> alterSequence(String sequence) {
        return dsl().alterSequence(sequence);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static AlterSequenceStep<Number> alterSequence(Name sequence) {
        return dsl().alterSequence(sequence);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static <T extends Number> AlterSequenceStep<T> alterSequence(Sequence<T> sequence) {
        return dsl().alterSequence(sequence);
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static AlterSequenceStep<Number> alterSequenceIfExists(String sequence) {
        return dsl().alterSequenceIfExists(sequence);
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static AlterSequenceStep<Number> alterSequenceIfExists(Name sequence) {
        return dsl().alterSequenceIfExists(sequence);
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static <T extends Number> AlterSequenceStep<T> alterSequenceIfExists(Sequence<T> sequence) {
        return dsl().alterSequenceIfExists(sequence);
    }

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    public static AlterTypeStep alterType(String type) {
        return dsl().alterType(type);
    }

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    public static AlterTypeStep alterType(Name type) {
        return dsl().alterType(type);
    }

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    public static AlterTypeStep alterTypeIfExists(String type) {
        return dsl().alterTypeIfExists(type);
    }

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    public static AlterTypeStep alterTypeIfExists(Name type) {
        return dsl().alterTypeIfExists(type);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static AlterViewStep alterView(String view) {
        return dsl().alterView(view);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static AlterViewStep alterView(Name view) {
        return dsl().alterView(view);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static AlterViewStep alterView(Table<?> view) {
        return dsl().alterView(view);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static AlterViewStep alterViewIfExists(String view) {
        return dsl().alterViewIfExists(view);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static AlterViewStep alterViewIfExists(Name view) {
        return dsl().alterViewIfExists(view);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static AlterViewStep alterViewIfExists(Table<?> view) {
        return dsl().alterViewIfExists(view);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static AlterViewStep alterMaterializedView(String view) {
        return dsl().alterMaterializedView(view);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static AlterViewStep alterMaterializedView(Name view) {
        return dsl().alterMaterializedView(view);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static AlterViewStep alterMaterializedView(Table<?> view) {
        return dsl().alterMaterializedView(view);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static AlterViewStep alterMaterializedViewIfExists(String view) {
        return dsl().alterMaterializedViewIfExists(view);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static AlterViewStep alterMaterializedViewIfExists(Name view) {
        return dsl().alterMaterializedViewIfExists(view);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static AlterViewStep alterMaterializedViewIfExists(Table<?> view) {
        return dsl().alterMaterializedViewIfExists(view);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static AlterViewStep alterView(Table<?> view, Field<?>... fields) {
        return dsl().alterView(view, fields);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static AlterViewStep alterView(Table<?> view, Collection<? extends Field<?>> fields) {
        return dsl().alterView(view, fields);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CommentOnIsStep commentOnTable(String table) {
        return dsl().commentOnTable(table);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CommentOnIsStep commentOnTable(Name table) {
        return dsl().commentOnTable(table);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CommentOnIsStep commentOnTable(Table<?> table) {
        return dsl().commentOnTable(table);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CommentOnIsStep commentOnView(String view) {
        return dsl().commentOnView(view);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CommentOnIsStep commentOnView(Name view) {
        return dsl().commentOnView(view);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CommentOnIsStep commentOnView(Table<?> view) {
        return dsl().commentOnView(view);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CommentOnIsStep commentOnMaterializedView(String view) {
        return dsl().commentOnMaterializedView(view);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CommentOnIsStep commentOnMaterializedView(Name view) {
        return dsl().commentOnMaterializedView(view);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CommentOnIsStep commentOnMaterializedView(Table<?> view) {
        return dsl().commentOnMaterializedView(view);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CommentOnIsStep commentOnColumn(String field) {
        return dsl().commentOnColumn(field);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CommentOnIsStep commentOnColumn(Name field) {
        return dsl().commentOnColumn(field);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CommentOnIsStep commentOnColumn(Field<?> field) {
        return dsl().commentOnColumn(field);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateDatabaseFinalStep createDatabase(String database) {
        return dsl().createDatabase(database);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateDatabaseFinalStep createDatabase(Name database) {
        return dsl().createDatabase(database);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateDatabaseFinalStep createDatabase(Catalog database) {
        return dsl().createDatabase(database);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateDatabaseFinalStep createDatabaseIfNotExists(String database) {
        return dsl().createDatabaseIfNotExists(database);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateDatabaseFinalStep createDatabaseIfNotExists(Name database) {
        return dsl().createDatabaseIfNotExists(database);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateDatabaseFinalStep createDatabaseIfNotExists(Catalog database) {
        return dsl().createDatabaseIfNotExists(database);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateDomainAsStep createDomain(String domain) {
        return dsl().createDomain(domain);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateDomainAsStep createDomain(Name domain) {
        return dsl().createDomain(domain);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateDomainAsStep createDomain(Domain<?> domain) {
        return dsl().createDomain(domain);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateDomainAsStep createDomainIfNotExists(String domain) {
        return dsl().createDomainIfNotExists(domain);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateDomainAsStep createDomainIfNotExists(Name domain) {
        return dsl().createDomainIfNotExists(domain);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateDomainAsStep createDomainIfNotExists(Domain<?> domain) {
        return dsl().createDomainIfNotExists(domain);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateIndexStep createIndex(String index) {
        return dsl().createIndex(index);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateIndexStep createIndex(Name index) {
        return dsl().createIndex(index);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateIndexStep createIndex(Index index) {
        return dsl().createIndex(index);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateIndexStep createIndex() {
        return dsl().createIndex();
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateIndexStep createIndexIfNotExists(String index) {
        return dsl().createIndexIfNotExists(index);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateIndexStep createIndexIfNotExists(Name index) {
        return dsl().createIndexIfNotExists(index);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateIndexStep createIndexIfNotExists(Index index) {
        return dsl().createIndexIfNotExists(index);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateIndexStep createIndexIfNotExists() {
        return dsl().createIndexIfNotExists();
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateIndexStep createUniqueIndex(String index) {
        return dsl().createUniqueIndex(index);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateIndexStep createUniqueIndex(Name index) {
        return dsl().createUniqueIndex(index);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateIndexStep createUniqueIndex(Index index) {
        return dsl().createUniqueIndex(index);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateIndexStep createUniqueIndex() {
        return dsl().createUniqueIndex();
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateIndexStep createUniqueIndexIfNotExists(String index) {
        return dsl().createUniqueIndexIfNotExists(index);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateIndexStep createUniqueIndexIfNotExists(Name index) {
        return dsl().createUniqueIndexIfNotExists(index);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateIndexStep createUniqueIndexIfNotExists(Index index) {
        return dsl().createUniqueIndexIfNotExists(index);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateIndexStep createUniqueIndexIfNotExists() {
        return dsl().createUniqueIndexIfNotExists();
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static CreateTableElementListStep createTable(String table) {
        return dsl().createTable(table);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static CreateTableElementListStep createTable(Name table) {
        return dsl().createTable(table);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static CreateTableElementListStep createTable(Table<?> table) {
        return dsl().createTable(table);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateTableElementListStep createTableIfNotExists(String table) {
        return dsl().createTableIfNotExists(table);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateTableElementListStep createTableIfNotExists(Name table) {
        return dsl().createTableIfNotExists(table);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateTableElementListStep createTableIfNotExists(Table<?> table) {
        return dsl().createTableIfNotExists(table);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateTableElementListStep createTemporaryTable(String table) {
        return dsl().createTemporaryTable(table);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateTableElementListStep createTemporaryTable(Name table) {
        return dsl().createTemporaryTable(table);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateTableElementListStep createTemporaryTable(Table<?> table) {
        return dsl().createTemporaryTable(table);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateTableElementListStep createTemporaryTableIfNotExists(String table) {
        return dsl().createTemporaryTableIfNotExists(table);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateTableElementListStep createTemporaryTableIfNotExists(Name table) {
        return dsl().createTemporaryTableIfNotExists(table);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateTableElementListStep createTemporaryTableIfNotExists(Table<?> table) {
        return dsl().createTemporaryTableIfNotExists(table);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateTableElementListStep createGlobalTemporaryTable(String table) {
        return dsl().createGlobalTemporaryTable(table);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateTableElementListStep createGlobalTemporaryTable(Name table) {
        return dsl().createGlobalTemporaryTable(table);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateTableElementListStep createGlobalTemporaryTable(Table<?> table) {
        return dsl().createGlobalTemporaryTable(table);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateTableElementListStep createGlobalTemporaryTableIfNotExists(String table) {
        return dsl().createGlobalTemporaryTableIfNotExists(table);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateTableElementListStep createGlobalTemporaryTableIfNotExists(Name table) {
        return dsl().createGlobalTemporaryTableIfNotExists(table);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateTableElementListStep createGlobalTemporaryTableIfNotExists(Table<?> table) {
        return dsl().createGlobalTemporaryTableIfNotExists(table);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createView(String view, String... fields) {
        return dsl().createView(view, fields);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createView(Name view, Name... fields) {
        return dsl().createView(view, fields);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createView(Table<?> view, Field<?>... fields) {
        return dsl().createView(view, fields);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createView(String view, Collection<? extends String> fields) {
        return dsl().createView(view, fields);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createView(Name view, Collection<? extends Name> fields) {
        return dsl().createView(view, fields);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createView(Table<?> view, Collection<? extends Field<?>> fields) {
        return dsl().createView(view, fields);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createViewIfNotExists(String view, String... fields) {
        return dsl().createViewIfNotExists(view, fields);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createViewIfNotExists(Name view, Name... fields) {
        return dsl().createViewIfNotExists(view, fields);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createViewIfNotExists(Table<?> view, Field<?>... fields) {
        return dsl().createViewIfNotExists(view, fields);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createViewIfNotExists(String view, Collection<? extends String> fields) {
        return dsl().createViewIfNotExists(view, fields);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createViewIfNotExists(Name view, Collection<? extends Name> fields) {
        return dsl().createViewIfNotExists(view, fields);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createViewIfNotExists(Table<?> view, Collection<? extends Field<?>> fields) {
        return dsl().createViewIfNotExists(view, fields);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createOrReplaceView(String view, String... fields) {
        return dsl().createOrReplaceView(view, fields);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createOrReplaceView(Name view, Name... fields) {
        return dsl().createOrReplaceView(view, fields);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createOrReplaceView(Table<?> view, Field<?>... fields) {
        return dsl().createOrReplaceView(view, fields);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createOrReplaceView(String view, Collection<? extends String> fields) {
        return dsl().createOrReplaceView(view, fields);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createOrReplaceView(Name view, Collection<? extends Name> fields) {
        return dsl().createOrReplaceView(view, fields);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createOrReplaceView(Table<?> view, Collection<? extends Field<?>> fields) {
        return dsl().createOrReplaceView(view, fields);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createMaterializedView(String view, String... fields) {
        return dsl().createMaterializedView(view, fields);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createMaterializedView(Name view, Name... fields) {
        return dsl().createMaterializedView(view, fields);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createMaterializedView(Table<?> view, Field<?>... fields) {
        return dsl().createMaterializedView(view, fields);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createMaterializedView(String view, Collection<? extends String> fields) {
        return dsl().createMaterializedView(view, fields);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createMaterializedView(Name view, Collection<? extends Name> fields) {
        return dsl().createMaterializedView(view, fields);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createMaterializedView(Table<?> view, Collection<? extends Field<?>> fields) {
        return dsl().createMaterializedView(view, fields);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createMaterializedViewIfNotExists(String view, String... fields) {
        return dsl().createMaterializedViewIfNotExists(view, fields);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createMaterializedViewIfNotExists(Name view, Name... fields) {
        return dsl().createMaterializedViewIfNotExists(view, fields);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createMaterializedViewIfNotExists(Table<?> view, Field<?>... fields) {
        return dsl().createMaterializedViewIfNotExists(view, fields);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createMaterializedViewIfNotExists(String view, Collection<? extends String> fields) {
        return dsl().createMaterializedViewIfNotExists(view, fields);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createMaterializedViewIfNotExists(Name view, Collection<? extends Name> fields) {
        return dsl().createMaterializedViewIfNotExists(view, fields);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createMaterializedViewIfNotExists(Table<?> view, Collection<? extends Field<?>> fields) {
        return dsl().createMaterializedViewIfNotExists(view, fields);
    }

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createOrReplaceMaterializedView(String view, String... fields) {
        return dsl().createOrReplaceMaterializedView(view, fields);
    }

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createOrReplaceMaterializedView(Name view, Name... fields) {
        return dsl().createOrReplaceMaterializedView(view, fields);
    }

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createOrReplaceMaterializedView(Table<?> view, Field<?>... fields) {
        return dsl().createOrReplaceMaterializedView(view, fields);
    }

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createOrReplaceMaterializedView(String view, Collection<? extends String> fields) {
        return dsl().createOrReplaceMaterializedView(view, fields);
    }

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createOrReplaceMaterializedView(Name view, Collection<? extends Name> fields) {
        return dsl().createOrReplaceMaterializedView(view, fields);
    }

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    public static CreateViewAsStep<Record> createOrReplaceMaterializedView(Table<?> view, Collection<? extends Field<?>> fields) {
        return dsl().createOrReplaceMaterializedView(view, fields);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateTypeStep createType(String type) {
        return dsl().createType(type);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateTypeStep createType(Name type) {
        return dsl().createType(type);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateTypeStep createType(Type<?> type) {
        return dsl().createType(type);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateTypeStep createTypeIfNotExists(String type) {
        return dsl().createTypeIfNotExists(type);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateTypeStep createTypeIfNotExists(Name type) {
        return dsl().createTypeIfNotExists(type);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateTypeStep createTypeIfNotExists(Type<?> type) {
        return dsl().createTypeIfNotExists(type);
    }

    @Support({SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateSchemaFinalStep createSchema(String schema) {
        return dsl().createSchema(schema);
    }

    @Support({SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateSchemaFinalStep createSchema(Name schema) {
        return dsl().createSchema(schema);
    }

    @Support({SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateSchemaFinalStep createSchema(Schema schema) {
        return dsl().createSchema(schema);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateSchemaFinalStep createSchemaIfNotExists(String schema) {
        return dsl().createSchemaIfNotExists(schema);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateSchemaFinalStep createSchemaIfNotExists(Name schema) {
        return dsl().createSchemaIfNotExists(schema);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateSchemaFinalStep createSchemaIfNotExists(Schema schema) {
        return dsl().createSchemaIfNotExists(schema);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateSequenceFlagsStep createSequence(String sequence) {
        return dsl().createSequence(sequence);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateSequenceFlagsStep createSequence(Name sequence) {
        return dsl().createSequence(sequence);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateSequenceFlagsStep createSequence(Sequence<?> sequence) {
        return dsl().createSequence(sequence);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateSequenceFlagsStep createSequenceIfNotExists(String sequence) {
        return dsl().createSequenceIfNotExists(sequence);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateSequenceFlagsStep createSequenceIfNotExists(Name sequence) {
        return dsl().createSequenceIfNotExists(sequence);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static CreateSequenceFlagsStep createSequenceIfNotExists(Sequence<?> sequence) {
        return dsl().createSequenceIfNotExists(sequence);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropDatabaseFinalStep dropDatabase(String database) {
        return dsl().dropDatabase(database);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropDatabaseFinalStep dropDatabase(Name database) {
        return dsl().dropDatabase(database);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropDatabaseFinalStep dropDatabase(Catalog database) {
        return dsl().dropDatabase(database);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropDatabaseFinalStep dropDatabaseIfExists(String database) {
        return dsl().dropDatabaseIfExists(database);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropDatabaseFinalStep dropDatabaseIfExists(Name database) {
        return dsl().dropDatabaseIfExists(database);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropDatabaseFinalStep dropDatabaseIfExists(Catalog database) {
        return dsl().dropDatabaseIfExists(database);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropDomainCascadeStep dropDomain(String domain) {
        return dsl().dropDomain(domain);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropDomainCascadeStep dropDomain(Name domain) {
        return dsl().dropDomain(domain);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropDomainCascadeStep dropDomain(Domain<?> domain) {
        return dsl().dropDomain(domain);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropDomainCascadeStep dropDomainIfExists(String domain) {
        return dsl().dropDomainIfExists(domain);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropDomainCascadeStep dropDomainIfExists(Name domain) {
        return dsl().dropDomainIfExists(domain);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropDomainCascadeStep dropDomainIfExists(Domain<?> domain) {
        return dsl().dropDomainIfExists(domain);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropIndexOnStep dropIndex(String index) {
        return dsl().dropIndex(index);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropIndexOnStep dropIndex(Name index) {
        return dsl().dropIndex(index);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropIndexOnStep dropIndex(Index index) {
        return dsl().dropIndex(index);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropIndexOnStep dropIndexIfExists(String index) {
        return dsl().dropIndexIfExists(index);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropIndexOnStep dropIndexIfExists(Name index) {
        return dsl().dropIndexIfExists(index);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropIndexOnStep dropIndexIfExists(Index index) {
        return dsl().dropIndexIfExists(index);
    }

    @Support({SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropSchemaStep dropSchema(String schema) {
        return dsl().dropSchema(schema);
    }

    @Support({SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropSchemaStep dropSchema(Name schema) {
        return dsl().dropSchema(schema);
    }

    @Support({SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropSchemaStep dropSchema(Schema schema) {
        return dsl().dropSchema(schema);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropSchemaStep dropSchemaIfExists(String schema) {
        return dsl().dropSchemaIfExists(schema);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropSchemaStep dropSchemaIfExists(Name schema) {
        return dsl().dropSchemaIfExists(schema);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropSchemaStep dropSchemaIfExists(Schema schema) {
        return dsl().dropSchemaIfExists(schema);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropSequenceFinalStep dropSequence(String sequence) {
        return dsl().dropSequence(sequence);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropSequenceFinalStep dropSequence(Name sequence) {
        return dsl().dropSequence(sequence);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropSequenceFinalStep dropSequence(Sequence<?> sequence) {
        return dsl().dropSequence(sequence);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropSequenceFinalStep dropSequenceIfExists(String sequence) {
        return dsl().dropSequenceIfExists(sequence);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropSequenceFinalStep dropSequenceIfExists(Name sequence) {
        return dsl().dropSequenceIfExists(sequence);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropSequenceFinalStep dropSequenceIfExists(Sequence<?> sequence) {
        return dsl().dropSequenceIfExists(sequence);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static DropTableStep dropTable(String table) {
        return dsl().dropTable(table);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static DropTableStep dropTable(Name table) {
        return dsl().dropTable(table);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static DropTableStep dropTable(Table<?> table) {
        return dsl().dropTable(table);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropTableStep dropTableIfExists(String table) {
        return dsl().dropTableIfExists(table);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropTableStep dropTableIfExists(Name table) {
        return dsl().dropTableIfExists(table);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropTableStep dropTableIfExists(Table<?> table) {
        return dsl().dropTableIfExists(table);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropTableStep dropTemporaryTable(String table) {
        return dsl().dropTemporaryTable(table);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropTableStep dropTemporaryTable(Name table) {
        return dsl().dropTemporaryTable(table);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropTableStep dropTemporaryTable(Table<?> table) {
        return dsl().dropTemporaryTable(table);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropTableStep dropTemporaryTableIfExists(String table) {
        return dsl().dropTemporaryTableIfExists(table);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropTableStep dropTemporaryTableIfExists(Name table) {
        return dsl().dropTemporaryTableIfExists(table);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropTableStep dropTemporaryTableIfExists(Table<?> table) {
        return dsl().dropTemporaryTableIfExists(table);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropTypeStep dropType(String types) {
        return dsl().dropType(types);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropTypeStep dropType(Name types) {
        return dsl().dropType(types);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropTypeStep dropType(Type<?> types) {
        return dsl().dropType(types);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropTypeStep dropType(String... types) {
        return dsl().dropType(types);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropTypeStep dropType(Name... types) {
        return dsl().dropType(types);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropTypeStep dropType(Type<?>... types) {
        return dsl().dropType(types);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropTypeStep dropType(Collection<? extends Type<?>> types) {
        return dsl().dropType(types);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropTypeStep dropTypeIfExists(String types) {
        return dsl().dropTypeIfExists(types);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropTypeStep dropTypeIfExists(Name types) {
        return dsl().dropTypeIfExists(types);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropTypeStep dropTypeIfExists(Type<?> types) {
        return dsl().dropTypeIfExists(types);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropTypeStep dropTypeIfExists(String... types) {
        return dsl().dropTypeIfExists(types);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropTypeStep dropTypeIfExists(Name... types) {
        return dsl().dropTypeIfExists(types);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropTypeStep dropTypeIfExists(Type<?>... types) {
        return dsl().dropTypeIfExists(types);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropTypeStep dropTypeIfExists(Collection<? extends Type<?>> types) {
        return dsl().dropTypeIfExists(types);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropViewFinalStep dropView(String view) {
        return dsl().dropView(view);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropViewFinalStep dropView(Name view) {
        return dsl().dropView(view);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropViewFinalStep dropView(Table<?> view) {
        return dsl().dropView(view);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropViewFinalStep dropViewIfExists(String view) {
        return dsl().dropViewIfExists(view);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropViewFinalStep dropViewIfExists(Name view) {
        return dsl().dropViewIfExists(view);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropViewFinalStep dropViewIfExists(Table<?> view) {
        return dsl().dropViewIfExists(view);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropViewFinalStep dropMaterializedView(String view) {
        return dsl().dropMaterializedView(view);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropViewFinalStep dropMaterializedView(Name view) {
        return dsl().dropMaterializedView(view);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropViewFinalStep dropMaterializedView(Table<?> view) {
        return dsl().dropMaterializedView(view);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropViewFinalStep dropMaterializedViewIfExists(String view) {
        return dsl().dropMaterializedViewIfExists(view);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropViewFinalStep dropMaterializedViewIfExists(Name view) {
        return dsl().dropMaterializedViewIfExists(view);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static DropViewFinalStep dropMaterializedViewIfExists(Table<?> view) {
        return dsl().dropMaterializedViewIfExists(view);
    }

    @Support({SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static GrantOnStep grant(Privilege privileges) {
        return dsl().grant(privileges);
    }

    @Support({SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static GrantOnStep grant(Privilege... privileges) {
        return dsl().grant(privileges);
    }

    @Support({SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static GrantOnStep grant(Collection<? extends Privilege> privileges) {
        return dsl().grant(privileges);
    }

    @Support({SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static RevokeOnStep revoke(Privilege privileges) {
        return dsl().revoke(privileges);
    }

    @Support({SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static RevokeOnStep revoke(Privilege... privileges) {
        return dsl().revoke(privileges);
    }

    @Support({SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static RevokeOnStep revoke(Collection<? extends Privilege> privileges) {
        return dsl().revoke(privileges);
    }

    @Support({SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static RevokeOnStep revokeGrantOptionFor(Privilege privileges) {
        return dsl().revokeGrantOptionFor(privileges);
    }

    @Support({SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static RevokeOnStep revokeGrantOptionFor(Privilege... privileges) {
        return dsl().revokeGrantOptionFor(privileges);
    }

    @Support({SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static RevokeOnStep revokeGrantOptionFor(Collection<? extends Privilege> privileges) {
        return dsl().revokeGrantOptionFor(privileges);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static RowCountQuery set(String name, Param<?> value) {
        return dsl().set(name, value);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static RowCountQuery set(Name name, Param<?> value) {
        return dsl().set(name, value);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static RowCountQuery setLocal(String name, Param<?> value) {
        return dsl().setLocal(name, value);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static RowCountQuery setLocal(Name name, Param<?> value) {
        return dsl().setLocal(name, value);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.MARIADB, SQLDialect.MYSQL})
    @CheckReturnValue
    @NotNull
    public static RowCountQuery setCatalog(String catalog) {
        return dsl().setCatalog(catalog);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.MARIADB, SQLDialect.MYSQL})
    @CheckReturnValue
    @NotNull
    public static RowCountQuery setCatalog(Name catalog) {
        return dsl().setCatalog(catalog);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.MARIADB, SQLDialect.MYSQL})
    @CheckReturnValue
    @NotNull
    public static RowCountQuery setCatalog(Catalog catalog) {
        return dsl().setCatalog(catalog);
    }

    @Support({SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static RowCountQuery setSchema(String schema) {
        return dsl().setSchema(schema);
    }

    @Support({SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static RowCountQuery setSchema(Name schema) {
        return dsl().setSchema(schema);
    }

    @Support({SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static RowCountQuery setSchema(Schema schema) {
        return dsl().setSchema(schema);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static TruncateIdentityStep<Record> truncate(String table) {
        return dsl().truncate(table);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static TruncateIdentityStep<Record> truncate(Name table) {
        return dsl().truncate(table);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static <R extends Record> TruncateIdentityStep<R> truncate(Table<R> table) {
        return dsl().truncate(table);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static TruncateIdentityStep<Record> truncate(String... table) {
        return dsl().truncate(table);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static TruncateIdentityStep<Record> truncate(Name... table) {
        return dsl().truncate(table);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static TruncateIdentityStep<Record> truncate(Table<?>... table) {
        return dsl().truncate(table);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static TruncateIdentityStep<Record> truncate(Collection<? extends Table<?>> table) {
        return dsl().truncate(table);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static TruncateIdentityStep<Record> truncateTable(String table) {
        return dsl().truncateTable(table);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static TruncateIdentityStep<Record> truncateTable(Name table) {
        return dsl().truncateTable(table);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static <R extends Record> TruncateIdentityStep<R> truncateTable(Table<R> table) {
        return dsl().truncateTable(table);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static TruncateIdentityStep<Record> truncateTable(String... table) {
        return dsl().truncateTable(table);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static TruncateIdentityStep<Record> truncateTable(Name... table) {
        return dsl().truncateTable(table);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static TruncateIdentityStep<Record> truncateTable(Table<?>... table) {
        return dsl().truncateTable(table);
    }

    @Support
    @CheckReturnValue
    @NotNull
    public static TruncateIdentityStep<Record> truncateTable(Collection<? extends Table<?>> table) {
        return dsl().truncateTable(table);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static Query startTransaction() {
        return dsl().startTransaction();
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static Query savepoint(String name) {
        return dsl().savepoint(name);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static Query savepoint(Name name) {
        return dsl().savepoint(name);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static Query releaseSavepoint(String name) {
        return dsl().releaseSavepoint(name);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static Query releaseSavepoint(Name name) {
        return dsl().releaseSavepoint(name);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static Query commit() {
        return dsl().commit();
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    public static RollbackToSavepointStep rollback() {
        return dsl().rollback();
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static CreateViewAsStep<Record> createView(String view, java.util.function.Function<? super Field<?>, ? extends String> fieldNameFunction) {
        return dsl().createView(view, fieldNameFunction);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static CreateViewAsStep<Record> createView(Name view, java.util.function.Function<? super Field<?>, ? extends Name> fieldNameFunction) {
        return dsl().createView(view, fieldNameFunction);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static CreateViewAsStep<Record> createView(Table<?> view, java.util.function.Function<? super Field<?>, ? extends Field<?>> fieldNameFunction) {
        return dsl().createView(view, fieldNameFunction);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static CreateViewAsStep<Record> createOrReplaceView(String view, java.util.function.Function<? super Field<?>, ? extends String> fieldNameFunction) {
        return dsl().createOrReplaceView(view, fieldNameFunction);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static CreateViewAsStep<Record> createOrReplaceView(Name view, java.util.function.Function<? super Field<?>, ? extends Name> fieldNameFunction) {
        return dsl().createOrReplaceView(view, fieldNameFunction);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static CreateViewAsStep<Record> createOrReplaceView(Table<?> view, java.util.function.Function<? super Field<?>, ? extends Field<?>> fieldNameFunction) {
        return dsl().createOrReplaceView(view, fieldNameFunction);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static CreateViewAsStep<Record> createViewIfNotExists(String view, java.util.function.Function<? super Field<?>, ? extends String> fieldNameFunction) {
        return dsl().createViewIfNotExists(view, fieldNameFunction);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static CreateViewAsStep<Record> createViewIfNotExists(Name view, java.util.function.Function<? super Field<?>, ? extends Name> fieldNameFunction) {
        return dsl().createViewIfNotExists(view, fieldNameFunction);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static CreateViewAsStep<Record> createViewIfNotExists(Table<?> view, java.util.function.Function<? super Field<?>, ? extends Field<?>> fieldNameFunction) {
        return dsl().createViewIfNotExists(view, fieldNameFunction);
    }

    @Support
    @NotNull
    public static AlterTableStep alterTable(String table) {
        return dsl().alterTable(table);
    }

    @Support
    @NotNull
    public static AlterTableStep alterTable(Name table) {
        return dsl().alterTable(table);
    }

    @Support
    @NotNull
    public static AlterTableStep alterTable(Table<?> table) {
        return dsl().alterTable(table);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static AlterTableStep alterTableIfExists(String table) {
        return dsl().alterTableIfExists(table);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static AlterTableStep alterTableIfExists(Name table) {
        return dsl().alterTableIfExists(table);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static AlterTableStep alterTableIfExists(Table<?> table) {
        return dsl().alterTableIfExists(table);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <R extends Record> QuantifiedSelect<R> all(Select<R> select) {
        return new QuantifiedSelectImpl(QOM.Quantifier.ALL, select);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> QuantifiedSelect<Record1<T>> all(T... tArr) {
        return tArr instanceof Field[] ? all((Field[]) tArr) : new QuantifiedArray(QOM.Quantifier.ALL, val(tArr));
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> QuantifiedSelect<Record1<T>> all(Field<T[]> array) {
        return new QuantifiedArray(QOM.Quantifier.ALL, array);
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T> QuantifiedSelect<Record1<T>> all(Field<T>... fields) {
        return new QuantifiedArray(QOM.Quantifier.ALL, new Array(Arrays.asList(fields)));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <R extends Record> QuantifiedSelect<R> any(Select<R> select) {
        return new QuantifiedSelectImpl(QOM.Quantifier.ANY, select);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> QuantifiedSelect<Record1<T>> any(T... tArr) {
        return tArr instanceof Field[] ? any((Field[]) tArr) : new QuantifiedArray(QOM.Quantifier.ANY, val(tArr));
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> QuantifiedSelect<Record1<T>> any(Field<T[]> array) {
        return new QuantifiedArray(QOM.Quantifier.ANY, array);
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T> QuantifiedSelect<Record1<T>> any(Field<T>... fields) {
        return new QuantifiedArray(QOM.Quantifier.ANY, new Array(Arrays.asList(fields)));
    }

    @Support({SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Collation collation(String collation) {
        return collation(name(collation));
    }

    @Support({SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Collation collation(Name collation) {
        return new CollationImpl(collation);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @NotNull
    public static CharacterSet characterSet(String characterSet) {
        return characterSet(name(characterSet));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @NotNull
    public static CharacterSet characterSet(Name characterSet) {
        return new CharacterSetImpl(characterSet);
    }

    @PlainSQL
    @Support
    @NotNull
    public static Privilege privilege(String privilege) {
        return privilege(keyword(privilege));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Support
    @NotNull
    public static Privilege privilege(Keyword privilege) {
        return new PrivilegeImpl(privilege);
    }

    @Support
    @NotNull
    public static User user(String name) {
        return user(name(name));
    }

    @Support
    @NotNull
    public static User user(Name name) {
        return new UserImpl(name);
    }

    @Support
    @NotNull
    public static Role role(String name) {
        return role(name(name));
    }

    @Support
    @NotNull
    public static Role role(Name name) {
        return new RoleImpl(name);
    }

    @Support({SQLDialect.H2})
    @NotNull
    public static <R extends Record> Table<R> oldTable(Update<R> query) {
        return new DataChangeDeltaTable(QOM.ResultOption.OLD, query);
    }

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @NotNull
    public static <R extends Record> Table<R> oldTable(Delete<R> query) {
        return new DataChangeDeltaTable(QOM.ResultOption.OLD, query);
    }

    @Support({SQLDialect.H2})
    @NotNull
    public static <R extends Record> Table<R> oldTable(Merge<R> query) {
        return new DataChangeDeltaTable(QOM.ResultOption.OLD, query);
    }

    @Support({SQLDialect.H2})
    @NotNull
    public static <R extends Record> Table<R> newTable(Insert<R> query) {
        return new DataChangeDeltaTable(QOM.ResultOption.NEW, query);
    }

    @Support({SQLDialect.H2})
    @NotNull
    public static <R extends Record> Table<R> newTable(Update<R> query) {
        return new DataChangeDeltaTable(QOM.ResultOption.NEW, query);
    }

    @Support({SQLDialect.H2})
    @NotNull
    public static <R extends Record> Table<R> newTable(Merge<R> query) {
        return new DataChangeDeltaTable(QOM.ResultOption.NEW, query);
    }

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @NotNull
    public static <R extends Record> Table<R> finalTable(Insert<R> query) {
        return new DataChangeDeltaTable(QOM.ResultOption.FINAL, query);
    }

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @NotNull
    public static <R extends Record> Table<R> finalTable(Update<R> query) {
        return new DataChangeDeltaTable(QOM.ResultOption.FINAL, query);
    }

    @Support({SQLDialect.H2})
    @NotNull
    public static <R extends Record> Table<R> finalTable(Merge<R> query) {
        return new DataChangeDeltaTable(QOM.ResultOption.FINAL, query);
    }

    @Support
    @NotNull
    public static <R extends Record> Table<R> table(Select<R> select) {
        return select.asTable();
    }

    @Support
    @NotNull
    public static <R extends Record> Table<R> table(Result<R> result) {
        return (Table<R>) values0((Row[]) Tools.map(result, r -> {
            return r.valuesRow();
        }, x$0 -> {
            return new Row[x$0];
        })).as(name("v"), (Name[]) Tools.map(result.fields(), f -> {
            return f.getUnqualifiedName();
        }, x$02 -> {
            return new Name[x$02];
        }));
    }

    @Support
    @NotNull
    public static <R extends Record> Table<R> table(R record) {
        return table(record);
    }

    @Support
    @NotNull
    public static <R extends Record> Table<R> table(R... records) {
        if (records == null || records.length == 0) {
            return new Dual();
        }
        Result<R> result = new ResultImpl<>(Tools.configuration(records[0]), (AbstractRow) records[0].fieldsRow());
        result.addAll(Arrays.asList(records));
        return table((Result) result);
    }

    @Support
    @NotNull
    public static Table<?> table(Collection<?> list) {
        return unnest(list);
    }

    @Support
    @NotNull
    public static Table<?> table(Object[] array) {
        return unnest(array);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static Table<?> table(Field<?> cursor) {
        return unnest(cursor);
    }

    @Support
    @NotNull
    public static Table<?> unnest(Collection<?> list) {
        return unnest(list.toArray());
    }

    @Support
    @NotNull
    public static Table<?> unnest(Object[] array) {
        boolean notEmpty = !Tools.isEmpty(array);
        if (notEmpty && (array[0] instanceof Field)) {
            return new ArrayOfValues(Tools.fieldsArray(array));
        }
        if (notEmpty && array.getClass() == Object[].class) {
            return unnest0(val(Tools.mostSpecificArray(array)));
        }
        return unnest0(val(array));
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static Table<?> unnest(Field<?> cursor) {
        return unnest0(cursor);
    }

    private static Table<?> unnest0(Field<?> cursor) {
        if (cursor == null) {
            throw new IllegalArgumentException();
        }
        if (cursor.getType() == Result.class) {
            return new FunctionTable(cursor);
        }
        if (cursor.getType().isArray() && cursor.getType() != byte[].class) {
            return new ArrayTable(cursor);
        }
        throw new SQLDialectNotSupportedException("Converting arbitrary types into array tables is currently not supported");
    }

    @Support
    @NotNull
    public static Table<Record> dual() {
        return new Dual(true);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Table<Record1<Integer>> generateSeries(int from, int to) {
        return generateSeries(val(from), val(to));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Table<Record1<Integer>> generateSeries(int from, Field<Integer> to) {
        return generateSeries(val(from), (Field<Integer>) Tools.nullSafe(to));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Table<Record1<Integer>> generateSeries(Field<Integer> from, int to) {
        return new GenerateSeries(Tools.nullSafe(from), val(to));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Table<Record1<Integer>> generateSeries(Field<Integer> from, Field<Integer> to) {
        return new GenerateSeries(Tools.nullSafe(from), Tools.nullSafe(to));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Table<Record1<Integer>> generateSeries(int from, int to, int step) {
        return generateSeries(val(from), val(to), val(step));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Table<Record1<Integer>> generateSeries(int from, Field<Integer> to, int step) {
        return generateSeries(val(from), (Field<Integer>) Tools.nullSafe(to), val(step));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Table<Record1<Integer>> generateSeries(Field<Integer> from, int to, int step) {
        return new GenerateSeries(Tools.nullSafe(from), val(to), val(step));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Table<Record1<Integer>> generateSeries(Field<Integer> from, Field<Integer> to, int step) {
        return new GenerateSeries(Tools.nullSafe(from), Tools.nullSafe(to), val(step));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Table<Record1<Integer>> generateSeries(int from, int to, Field<Integer> step) {
        return generateSeries(val(from), val(to), (Field<Integer>) Tools.nullSafe(step));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Table<Record1<Integer>> generateSeries(int from, Field<Integer> to, Field<Integer> step) {
        return generateSeries(val(from), (Field<Integer>) Tools.nullSafe(to), (Field<Integer>) Tools.nullSafe(step));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Table<Record1<Integer>> generateSeries(Field<Integer> from, int to, Field<Integer> step) {
        return new GenerateSeries(Tools.nullSafe(from), val(to), Tools.nullSafe(step));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Table<Record1<Integer>> generateSeries(Field<Integer> from, Field<Integer> to, Field<Integer> step) {
        return new GenerateSeries(Tools.nullSafe(from), Tools.nullSafe(to), Tools.nullSafe(step));
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <R extends Record> Table<R> lateral(TableLike<R> table) {
        if ((table instanceof TableImpl) || (table instanceof JoinTable)) {
            return (Table) table;
        }
        return new Lateral(table.asTable());
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static Table<Record> rowsFrom(Table<?>... tables) {
        return new RowsFrom(tables);
    }

    @Support
    @NotNull
    public static Keyword keyword(String keyword) {
        return new KeywordImpl(keyword);
    }

    @Support
    @NotNull
    public static Name name(String unqualifiedName) {
        return new UnqualifiedName(unqualifiedName);
    }

    @Support
    @NotNull
    public static Name name(String... qualifiedName) {
        if (qualifiedName == null || qualifiedName.length != 1) {
            return new QualifiedName(qualifiedName);
        }
        return new UnqualifiedName(qualifiedName[0]);
    }

    @Support
    @NotNull
    public static Name name(Name... nameParts) {
        return new QualifiedName(nameParts);
    }

    @Support
    @NotNull
    public static Name name(Collection<String> qualifiedName) {
        return name((String[]) qualifiedName.toArray(Tools.EMPTY_STRING));
    }

    @Support
    @NotNull
    public static Name quotedName(String unqualifiedName) {
        return new UnqualifiedName(unqualifiedName, Name.Quoted.QUOTED);
    }

    @Support
    @NotNull
    public static Name quotedName(String... qualifiedName) {
        if (qualifiedName == null || qualifiedName.length != 1) {
            return new QualifiedName(qualifiedName, Name.Quoted.QUOTED);
        }
        return new UnqualifiedName(qualifiedName[0], Name.Quoted.QUOTED);
    }

    @Support
    @NotNull
    public static Name quotedName(Collection<String> qualifiedName) {
        return quotedName((String[]) qualifiedName.toArray(Tools.EMPTY_STRING));
    }

    @Support
    @NotNull
    public static Name unquotedName(String unqualifiedName) {
        return new UnqualifiedName(unqualifiedName, Name.Quoted.UNQUOTED);
    }

    @Support
    @NotNull
    public static Name unquotedName(String... qualifiedName) {
        if (qualifiedName == null || qualifiedName.length != 1) {
            return new QualifiedName(qualifiedName, Name.Quoted.UNQUOTED);
        }
        return new UnqualifiedName(qualifiedName[0], Name.Quoted.UNQUOTED);
    }

    @Support
    @NotNull
    public static Name unquotedName(Collection<String> qualifiedName) {
        return unquotedName((String[]) qualifiedName.toArray(Tools.EMPTY_STRING));
    }

    @Support
    @NotNull
    public static Name systemName(String unqualifiedName) {
        return new UnqualifiedName(unqualifiedName, Name.Quoted.SYSTEM);
    }

    @Support
    @NotNull
    public static Name systemName(String... qualifiedName) {
        if (qualifiedName == null || qualifiedName.length != 1) {
            return new QualifiedName(qualifiedName, Name.Quoted.SYSTEM);
        }
        return new UnqualifiedName(qualifiedName[0], Name.Quoted.SYSTEM);
    }

    @Support
    @NotNull
    public static Name systemName(Collection<String> qualifiedName) {
        return systemName((String[]) qualifiedName.toArray(Tools.EMPTY_STRING));
    }

    @Support
    @NotNull
    public static QueryPart list(QueryPart... parts) {
        return list(Arrays.asList(parts));
    }

    @Support
    @NotNull
    public static QueryPart list(Collection<? extends QueryPart> parts) {
        return new QueryPartList(parts);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<Object> defaultValue() {
        return default_();
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T> defaultValue(Class<T> type) {
        return default_(type);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T> defaultValue(DataType<T> type) {
        return default_(type);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T> defaultValue(Field<T> field) {
        return default_(field);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<Object> default_() {
        return default_(Object.class);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T> default_(Class<T> type) {
        return default_(getDataType(type));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T> default_(DataType<T> type) {
        return new Default(type);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T> default_(Field<T> field) {
        return default_(field.getDataType());
    }

    @Support
    @NotNull
    public static Table<?> noTable() {
        return NoTable.INSTANCE;
    }

    @Support
    @NotNull
    public static Field<?> noField() {
        return NoField.INSTANCE;
    }

    @Support
    @NotNull
    public static <T> Field<T> noField(Class<T> type) {
        return noField(getDataType(type));
    }

    @Support
    @NotNull
    public static <T> Field<T> noField(DataType<T> type) {
        return new NoField(type);
    }

    @Support
    @NotNull
    public static <T> Field<T> noField(Field<T> field) {
        return noField(field.getDataType());
    }

    @Support
    @NotNull
    public static Catalog catalog(String name) {
        return catalog(name(name));
    }

    @Support
    @NotNull
    public static Catalog catalog(Name name) {
        return new CatalogImpl(name);
    }

    @Support
    @NotNull
    public static Schema schema(String name) {
        return schema(name(name));
    }

    @Support
    @NotNull
    public static Schema schema(Name name) {
        return new SchemaImpl(name);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Sequence<BigInteger> sequence(Name name) {
        return sequence(name, BigInteger.class);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Sequence<T> sequence(Name name, Class<T> type) {
        return sequence(name, getDataType(type));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Sequence<T> sequence(Name name, DataType<T> type) {
        return new SequenceImpl(name.unqualifiedName(), name.qualified() ? schema(name.qualifier()) : null, (DataType) type, false);
    }

    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Type<?> type(String name) {
        return type(name(name));
    }

    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Type<?> type(Name name) {
        return type(name, SQLDataType.OTHER);
    }

    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Type<T> type(String name, DataType<T> type) {
        return type(name(name), type);
    }

    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Type<T> type(Name name, DataType<T> type) {
        return new TypeImpl(name, CommentImpl.NO_COMMENT, type);
    }

    @Support
    @NotNull
    public static Table<Record> table(Name name) {
        return new TableImpl(name);
    }

    @Support
    @NotNull
    public static Table<Record> table(Name name, Comment comment) {
        return new TableImpl(name, (Schema) null, (Table) null, (Field<?>[]) null, comment);
    }

    @Support
    @NotNull
    public static Field<Object> field(Name name) {
        return field(name, Object.class);
    }

    @Support
    @NotNull
    public static <T> Field<T> field(Name name, Class<T> type) {
        return field(name, getDataType(type));
    }

    @Support
    @NotNull
    public static <T> Field<T> field(Name name, DataType<T> type) {
        return field(name, type, (Comment) null);
    }

    @Support
    @NotNull
    public static <T> Field<T> field(Name name, DataType<T> type, Comment comment) {
        return new TableFieldImpl(name, type, comment);
    }

    @Support
    @NotNull
    public static Index index(Name name) {
        return new IndexImpl(name);
    }

    @Support
    @NotNull
    public static <T> Parameter<T> in(String name, DataType<T> type) {
        return in(name(name), type);
    }

    @Support
    @NotNull
    public static <T> Parameter<T> in(Name name, DataType<T> type) {
        return new ParameterImpl(ParamMode.IN, name, type);
    }

    @Support
    @NotNull
    public static <T> Parameter<T> inOut(String name, DataType<T> type) {
        return inOut(name(name), type);
    }

    @Support
    @NotNull
    public static <T> Parameter<T> inOut(Name name, DataType<T> type) {
        return new ParameterImpl(ParamMode.INOUT, name, type);
    }

    @Support
    @NotNull
    public static <T> Parameter<T> out(String name, DataType<T> type) {
        return out(name(name), type);
    }

    @Support
    @NotNull
    public static <T> Parameter<T> out(Name name, DataType<T> type) {
        return new ParameterImpl(ParamMode.OUT, name, type);
    }

    @Support
    @NotNull
    public static Queries queries(Query... queries) {
        return queries(Arrays.asList(queries));
    }

    @Support
    @NotNull
    public static Queries queries(Collection<? extends Query> queries) {
        return using(new DefaultConfiguration()).queries(queries);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Block begin(Statement... statements) {
        return begin(Arrays.asList(statements));
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Block begin(Collection<? extends Statement> statements) {
        return using(new DefaultConfiguration()).begin(statements);
    }

    @PlainSQL
    @Support
    @NotNull
    public static SQL raw(String sql) {
        return new SQLImpl(sql, true, new Object[0]);
    }

    @PlainSQL
    @Support
    @NotNull
    public static SQL sql(String sql) {
        return sql(sql, new Object[0]);
    }

    @PlainSQL
    @Support
    @NotNull
    public static SQL sql(String sql, QueryPart... parts) {
        return sql(sql, (Object[]) parts);
    }

    @PlainSQL
    @Support
    @NotNull
    public static SQL sql(String sql, Object... bindings) {
        return new SQLImpl(sql, false, bindings);
    }

    @PlainSQL
    @Support
    @NotNull
    public static RowCountQuery query(SQL sql) {
        return dsl().query(sql);
    }

    @PlainSQL
    @Support
    @NotNull
    public static RowCountQuery query(String sql) {
        return dsl().query(sql);
    }

    @PlainSQL
    @Support
    @NotNull
    public static RowCountQuery query(String sql, Object... bindings) {
        return dsl().query(sql, bindings);
    }

    @PlainSQL
    @Support
    @NotNull
    public static RowCountQuery query(String sql, QueryPart... parts) {
        return dsl().query(sql, parts);
    }

    @PlainSQL
    @Support
    @NotNull
    public static ResultQuery<Record> resultQuery(SQL sql) {
        return dsl().resultQuery(sql);
    }

    @PlainSQL
    @Support
    @NotNull
    public static ResultQuery<Record> resultQuery(String sql) {
        return dsl().resultQuery(sql);
    }

    @PlainSQL
    @Support
    @NotNull
    public static ResultQuery<Record> resultQuery(String sql, Object... bindings) {
        return dsl().resultQuery(sql, bindings);
    }

    @PlainSQL
    @Support
    @NotNull
    public static ResultQuery<Record> resultQuery(String sql, QueryPart... parts) {
        return dsl().resultQuery(sql, parts);
    }

    @PlainSQL
    @Support
    @NotNull
    public static Table<Record> table(SQL sql) {
        return new SQLTable(sql);
    }

    @PlainSQL
    @Support
    @NotNull
    public static Table<Record> table(String sql) {
        return table(sql, new Object[0]);
    }

    @PlainSQL
    @Support
    @NotNull
    public static Table<Record> table(String sql, Object... bindings) {
        return table(sql(sql, bindings));
    }

    @PlainSQL
    @Support
    @NotNull
    public static Table<Record> table(String sql, QueryPart... parts) {
        return table(sql, (Object[]) parts);
    }

    @PlainSQL
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.10")
    @NotNull
    public static Sequence<BigInteger> sequence(String sql) {
        return sequence(sql, BigInteger.class);
    }

    @PlainSQL
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.10")
    @NotNull
    public static <T extends Number> Sequence<T> sequence(String sql, Class<T> type) {
        return sequence(sql, getDataType(type));
    }

    @PlainSQL
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.10")
    @NotNull
    public static <T extends Number> Sequence<T> sequence(String sql, DataType<T> type) {
        return new SequenceImpl(sql, (Schema) null, (DataType) type, true);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T> value(Class<T> type) {
        return value(DefaultDataType.getDataType((SQLDialect) null, type));
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T> value(DataType<T> type) {
        return field(Names.N_VALUE, type);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Domain<?> domain(String name) {
        return domain(name(name));
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Domain<?> domain(Name name) {
        return domain(name, SQLDataType.OTHER);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Domain<T> domain(String name, Class<T> type) {
        return domain(name(name), getDataType(type));
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Domain<T> domain(Name name, Class<T> type) {
        return domain(name, getDataType(type));
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Domain<T> domain(String name, DataType<T> type) {
        return domain(name(name), type);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Domain<T> domain(Name name, DataType<T> type) {
        return new DomainImpl(name.qualified() ? schema(name.qualifier()) : null, name.unqualifiedName(), new DefaultDataType((SQLDialect) null, type.getSQLDataType(), name), new Check[0]);
    }

    @PlainSQL
    @Support
    @NotNull
    public static Field<Object> field(SQL sql) {
        return field(sql, Object.class);
    }

    @PlainSQL
    @Support
    @NotNull
    public static Field<Object> field(String sql) {
        return field(sql, new Object[0]);
    }

    @PlainSQL
    @Support
    @NotNull
    public static Field<Object> field(String sql, Object... bindings) {
        return field(sql, Object.class, bindings);
    }

    @PlainSQL
    @Support
    @NotNull
    public static <T> Field<T> field(SQL sql, Class<T> type) {
        return field(sql, getDataType(type));
    }

    @PlainSQL
    @Support
    @NotNull
    public static <T> Field<T> field(String sql, Class<T> type) {
        return field(sql, type, new Object[0]);
    }

    @PlainSQL
    @Support
    @NotNull
    public static <T> Field<T> field(String sql, Class<T> type, Object... bindings) {
        return field(sql, getDataType(type), bindings);
    }

    @PlainSQL
    @Support
    @NotNull
    public static <T> Field<T> field(SQL sql, DataType<T> type) {
        return new SQLField(type, sql);
    }

    @PlainSQL
    @Support
    @NotNull
    public static <T> Field<T> field(String sql, DataType<T> type) {
        return field(sql, type, new Object[0]);
    }

    @PlainSQL
    @Support
    @NotNull
    public static <T> Field<T> field(String sql, DataType<T> type, Object... bindings) {
        return field(sql(sql, bindings), type);
    }

    @PlainSQL
    @Support
    @NotNull
    public static <T> Field<T> field(String sql, DataType<T> type, QueryPart... parts) {
        return field(sql(sql, parts), type);
    }

    @PlainSQL
    @Support
    @NotNull
    public static Field<Object> field(String sql, QueryPart... parts) {
        return field(sql, (Object[]) parts);
    }

    @PlainSQL
    @Support
    @NotNull
    public static <T> Field<T> field(String sql, Class<T> type, QueryPart... parts) {
        return field(sql, getDataType(type), (Object[]) parts);
    }

    @PlainSQL
    @Support
    @NotNull
    public static <T> Field<T> function(String name, Class<T> type, Field<?>... arguments) {
        return function(name, getDataType(type), Tools.nullSafe(arguments));
    }

    @PlainSQL
    @Support
    @NotNull
    public static <T> Field<T> function(String name, DataType<T> type, Field<?>... arguments) {
        return new Function(name, type, Tools.nullSafe(arguments));
    }

    @Support
    @NotNull
    public static <T> Field<T> function(Name name, Class<T> type, Field<?>... arguments) {
        return function(name, getDataType(type), Tools.nullSafe(arguments));
    }

    @Support
    @NotNull
    public static <T> Field<T> function(Name name, DataType<T> type, Field<?>... arguments) {
        return new Function(name, type, Tools.nullSafe(arguments));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Support
    @NotNull
    public static <T> Field<T> function(Name name, DataType<T> type, Field<?> argument) {
        return new Function1(name, type, Tools.nullSafe(argument));
    }

    @PlainSQL
    @Support
    @NotNull
    public static <T> AggregateFunction<T> aggregate(String name, Class<T> type, Field<?>... arguments) {
        return aggregate(name, getDataType(type), Tools.nullSafe(arguments));
    }

    @PlainSQL
    @Support
    @NotNull
    public static <T> AggregateFunction<T> aggregate(String name, DataType<T> type, Field<?>... arguments) {
        return new DefaultAggregateFunction(name, type, Tools.nullSafe(arguments));
    }

    @Support
    @NotNull
    public static <T> AggregateFunction<T> aggregate(Name name, Class<T> type, Field<?>... arguments) {
        return aggregate(name, getDataType(type), Tools.nullSafe(arguments));
    }

    @Support
    @NotNull
    public static <T> AggregateFunction<T> aggregate(Name name, DataType<T> type, Field<?>... arguments) {
        return new DefaultAggregateFunction(name, type, Tools.nullSafe(arguments));
    }

    @PlainSQL
    @Support
    @NotNull
    public static <T> AggregateFunction<T> aggregateDistinct(String name, Class<T> type, Field<?>... arguments) {
        return aggregateDistinct(name, getDataType(type), Tools.nullSafe(arguments));
    }

    @PlainSQL
    @Support
    @NotNull
    public static <T> AggregateFunction<T> aggregateDistinct(String name, DataType<T> type, Field<?>... arguments) {
        return new DefaultAggregateFunction(true, name, (DataType) type, Tools.nullSafe(arguments));
    }

    @Support
    @NotNull
    public static <T> AggregateFunction<T> aggregateDistinct(Name name, Class<T> type, Field<?>... arguments) {
        return aggregateDistinct(name, getDataType(type), Tools.nullSafe(arguments));
    }

    @Support
    @NotNull
    public static <T> AggregateFunction<T> aggregateDistinct(Name name, DataType<T> type, Field<?>... arguments) {
        return new DefaultAggregateFunction(true, name, (DataType) type, Tools.nullSafe(arguments));
    }

    @PlainSQL
    @Support
    @NotNull
    public static Condition condition(SQL sql) {
        return new SQLCondition(sql);
    }

    @PlainSQL
    @Support
    @NotNull
    public static Condition condition(String sql) {
        return condition(sql, new Object[0]);
    }

    @PlainSQL
    @Support
    @NotNull
    public static Condition condition(String sql, Object... bindings) {
        return condition(sql(sql, bindings));
    }

    @PlainSQL
    @Support
    @NotNull
    public static Condition condition(String sql, QueryPart... parts) {
        return condition(sql, (Object[]) parts);
    }

    @Support
    @NotNull
    public static Condition condition(Boolean value) {
        return condition(Tools.field(value));
    }

    @Support
    @NotNull
    public static Condition condition(Map<Field<?>, ?> map) {
        return new MapCondition(map);
    }

    @Support
    @NotNull
    public static Condition condition(Record record) {
        return new RecordCondition(record);
    }

    @Support
    @NotNull
    public static Condition noCondition() {
        return NoCondition.INSTANCE;
    }

    @Support
    @NotNull
    public static True trueCondition() {
        return TrueCondition.INSTANCE;
    }

    @Support
    @NotNull
    public static False falseCondition() {
        return FalseCondition.INSTANCE;
    }

    @Support
    @NotNull
    public static Null nullCondition() {
        return NullCondition.INSTANCE;
    }

    @Support
    @NotNull
    public static Condition and(Condition left, Condition right) {
        return condition(Operator.AND, left, right);
    }

    @Support
    @NotNull
    public static Condition and(Condition... conditions) {
        return condition(Operator.AND, conditions);
    }

    @Support
    @NotNull
    public static Condition and(Collection<? extends Condition> conditions) {
        return condition(Operator.AND, conditions);
    }

    @Support
    @NotNull
    public static Condition or(Condition left, Condition right) {
        return condition(Operator.OR, left, right);
    }

    @Support
    @NotNull
    public static Condition or(Condition... conditions) {
        return condition(Operator.OR, conditions);
    }

    @Support
    @NotNull
    public static Condition or(Collection<? extends Condition> conditions) {
        return condition(Operator.OR, conditions);
    }

    @Support
    @NotNull
    public static Condition xor(Condition left, Condition right) {
        return condition(Operator.XOR, left, right);
    }

    @Support
    @NotNull
    public static Condition xor(Condition... conditions) {
        return condition(Operator.XOR, conditions);
    }

    @Support
    @NotNull
    public static Condition xor(Collection<? extends Condition> conditions) {
        return condition(Operator.XOR, conditions);
    }

    @Support
    @NotNull
    public static Condition condition(Operator operator, Condition left, Condition right) {
        if (left == null || (left instanceof NoCondition)) {
            return right == null ? noCondition() : right;
        }
        if (right == null || (right instanceof NoCondition)) {
            return left;
        }
        if (operator == Operator.AND) {
            return new And(left, right);
        }
        if (operator == Operator.XOR) {
            return new Xor(left, right);
        }
        return new Or(left, right);
    }

    @Support
    @NotNull
    public static Condition condition(Operator operator, Condition... conditions) {
        return condition(operator, Arrays.asList(conditions));
    }

    @Support
    @NotNull
    public static Condition condition(Operator operator, Collection<? extends Condition> conditions) {
        Condition result = null;
        for (Condition condition : conditions) {
            if (result == null) {
                result = condition;
            } else {
                result = condition(operator, result, condition);
            }
        }
        if (result != null) {
            return result;
        }
        if (!conditions.isEmpty()) {
            return noCondition();
        }
        return operator.identity();
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T> Field<Integer> field(Field<T> field, T... list) {
        return field((Field) field, Tools.fieldsArray(list, field.getDataType()));
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T> Field<Integer> field(Field<T> field, Field<T>... list) {
        return new FieldFunction(field, list);
    }

    @Support
    @NotNull
    public static <T> Field<T> field(SelectField<T> field) {
        if (field instanceof Field) {
            Field<T> f = (Field) field;
            return f;
        }
        if (field instanceof AbstractRow) {
            AbstractRow<?> r = (AbstractRow) field;
            return r.rf();
        }
        if (field instanceof AbstractTable) {
            AbstractTable<?> t = (AbstractTable) field;
            return t.tf();
        }
        return field("{0}", (DataType) field.getDataType(), field);
    }

    @Support
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static Field<Record> rowField(RowN row) {
        return new RowAsField(row);
    }

    @Support
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static <T1> Field<Record1<T1>> rowField(Row1<T1> row) {
        return new RowAsField(row);
    }

    @Support
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static <T1, T2> Field<Record2<T1, T2>> rowField(Row2<T1, T2> row) {
        return new RowAsField(row);
    }

    @Support
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static <T1, T2, T3> Field<Record3<T1, T2, T3>> rowField(Row3<T1, T2, T3> row) {
        return new RowAsField(row);
    }

    @Support
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static <T1, T2, T3, T4> Field<Record4<T1, T2, T3, T4>> rowField(Row4<T1, T2, T3, T4> row) {
        return new RowAsField(row);
    }

    @Support
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static <T1, T2, T3, T4, T5> Field<Record5<T1, T2, T3, T4, T5>> rowField(Row5<T1, T2, T3, T4, T5> row) {
        return new RowAsField(row);
    }

    @Support
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static <T1, T2, T3, T4, T5, T6> Field<Record6<T1, T2, T3, T4, T5, T6>> rowField(Row6<T1, T2, T3, T4, T5, T6> row) {
        return new RowAsField(row);
    }

    @Support
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7> Field<Record7<T1, T2, T3, T4, T5, T6, T7>> rowField(Row7<T1, T2, T3, T4, T5, T6, T7> row) {
        return new RowAsField(row);
    }

    @Support
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8> Field<Record8<T1, T2, T3, T4, T5, T6, T7, T8>> rowField(Row8<T1, T2, T3, T4, T5, T6, T7, T8> row) {
        return new RowAsField(row);
    }

    @Support
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Field<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> rowField(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row) {
        return new RowAsField(row);
    }

    @Support
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Field<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> rowField(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row) {
        return new RowAsField(row);
    }

    @Support
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Field<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> rowField(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return new RowAsField(row);
    }

    @Support
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Field<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> rowField(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row) {
        return new RowAsField(row);
    }

    @Support
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Field<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> rowField(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> row) {
        return new RowAsField(row);
    }

    @Support
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Field<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> rowField(Row14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> row) {
        return new RowAsField(row);
    }

    @Support
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Field<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> rowField(Row15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> row) {
        return new RowAsField(row);
    }

    @Support
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Field<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> rowField(Row16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> row) {
        return new RowAsField(row);
    }

    @Support
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> Field<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> rowField(Row17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> row) {
        return new RowAsField(row);
    }

    @Support
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> Field<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> rowField(Row18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> row) {
        return new RowAsField(row);
    }

    @Support
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> Field<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> rowField(Row19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> row) {
        return new RowAsField(row);
    }

    @Support
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> Field<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> rowField(Row20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> row) {
        return new RowAsField(row);
    }

    @Support
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> Field<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> rowField(Row21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> row) {
        return new RowAsField(row);
    }

    @Support
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> Field<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> rowField(Row22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> row) {
        return new RowAsField(row);
    }

    @Support
    @NotNull
    public static <T> Field<T> field(Select<? extends Record1<T>> select) {
        if (select == null) {
            return inline((Object) null);
        }
        return select.asField();
    }

    @Support
    @NotNull
    public static <T> Field<T> if_(Condition condition, T ifTrue, T ifFalse) {
        return iif0(Names.N_IF, condition, Tools.field(ifTrue), Tools.field(ifFalse));
    }

    @Support
    @NotNull
    public static <T> Field<T> if_(Condition condition, T ifTrue, Field<T> ifFalse) {
        return iif0(Names.N_IF, condition, Tools.field(ifTrue), Tools.nullSafe(ifFalse));
    }

    @Support
    @NotNull
    public static <T> Field<T> if_(Condition condition, Field<T> ifTrue, T ifFalse) {
        return iif0(Names.N_IF, condition, Tools.nullSafe(ifTrue), Tools.field(ifFalse));
    }

    @Support
    @NotNull
    public static <T> Field<T> if_(Condition condition, Field<T> ifTrue, Field<T> ifFalse) {
        return iif0(Names.N_IF, condition, Tools.nullSafe(ifTrue), Tools.nullSafe(ifFalse));
    }

    @Support
    @NotNull
    public static Case choose() {
        return decode();
    }

    @Support
    @NotNull
    public static <V> CaseValueStep<V> choose(V value) {
        return decode().value((Case) value);
    }

    @Support
    @NotNull
    public static <V> CaseValueStep<V> choose(Field<V> value) {
        return decode().value((Field) value);
    }

    @Support
    @NotNull
    public static <T> Field<T> choose(int index, T... values) {
        return choose((Field<Integer>) val(index), Tools.fieldsArray(values));
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T> Field<T> choose(int index, Field<T>... values) {
        return choose((Field<Integer>) val(index), (Field[]) values);
    }

    @Support
    @NotNull
    public static <T> Field<T> choose(Field<Integer> index, T... values) {
        return choose(index, Tools.fieldsArray(values));
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T> Field<T> choose(Field<Integer> index, Field<T>... values) {
        return new Choose(index, values);
    }

    @Support
    @NotNull
    public static Case case_() {
        return decode();
    }

    @Support
    @NotNull
    public static <V> CaseValueStep<V> case_(V value) {
        return decode().value((Case) value);
    }

    @Support
    @NotNull
    public static <V> CaseValueStep<V> case_(Field<V> value) {
        return decode().value((Field) value);
    }

    @Support
    @NotNull
    public static <T> CaseConditionStep<T> when(Condition condition, T result) {
        return decode().when(condition, (Condition) result);
    }

    @Support
    @NotNull
    public static <T> CaseConditionStep<T> when(Condition condition, Field<T> result) {
        return decode().when(condition, (Field) result);
    }

    @Support
    @NotNull
    public static <T> CaseConditionStep<T> when(Condition condition, Select<? extends Record1<T>> result) {
        return decode().when(condition, (Select) result);
    }

    @Support
    @NotNull
    public static <T> CaseConditionStep<T> when(Field<Boolean> condition, T result) {
        return decode().when(condition, (Field<Boolean>) result);
    }

    @Support
    @NotNull
    public static <T> CaseConditionStep<T> when(Field<Boolean> condition, Field<T> result) {
        return decode().when(condition, (Field) result);
    }

    @Support
    @NotNull
    public static <T> CaseConditionStep<T> when(Field<Boolean> condition, Select<? extends Record1<T>> result) {
        return decode().when(condition, (Select) result);
    }

    @Support
    @NotNull
    public static Case decode() {
        return new CaseImpl();
    }

    @Support
    @NotNull
    public static <Z, T> Field<Z> decode(T value, T search, Z result) {
        return decode(value, search, result, new Object[0]);
    }

    @Support
    @NotNull
    public static <Z, T> Field<Z> decode(T value, T search, Z result, Object... more) {
        return decode(Tools.field(value), Tools.field(search), Tools.field(result), (Field<?>[]) Tools.fieldsArray(more));
    }

    @Support
    @NotNull
    public static <Z, T> Field<Z> decode(Field<T> value, Field<T> search, Field<Z> result) {
        return decode(Tools.nullSafe(value), Tools.nullSafe(search), Tools.nullSafe(result), Tools.EMPTY_FIELD);
    }

    @Support
    @NotNull
    public static <Z, T> Field<Z> decode(Field<T> value, Field<T> search, Field<Z> result, Field<?>... more) {
        return new Decode(Tools.nullSafe(value), Tools.nullSafe(search), Tools.nullSafe(result), Tools.nullSafe(more));
    }

    @Support
    @NotNull
    public static <T> Field<T> coerce(Object value, Field<T> as) {
        return Tools.field(value).coerce(as);
    }

    @Support
    @NotNull
    public static <T> Field<T> coerce(Object value, Class<T> as) {
        return Tools.field(value).coerce(as);
    }

    @Support
    @NotNull
    public static <T> Field<T> coerce(Object value, DataType<T> as) {
        return Tools.field(value).coerce(as);
    }

    @Support
    @NotNull
    public static <T> Field<T> coerce(Field<?> field, Field<T> as) {
        return Tools.nullSafe(field).coerce(as);
    }

    @Support
    @NotNull
    public static <T> Field<T> coerce(Field<?> field, Class<T> as) {
        return Tools.nullSafe(field).coerce(as);
    }

    @Support
    @NotNull
    public static <T> Field<T> coerce(Field<?> field, DataType<T> as) {
        return Tools.nullSafe(field).coerce(as);
    }

    @Support
    @NotNull
    public static <T> Field<T> cast(Object value, Field<T> as) {
        return Tools.field(value, as).cast(as);
    }

    @Support
    @NotNull
    public static <T> Field<T> cast(Field<?> field, Field<T> as) {
        return Tools.nullSafe(field).cast(as);
    }

    @Support
    @NotNull
    public static <T> Field<T> castNull(Field<T> field) {
        return (Field<T>) inline((Object) null).cast(field);
    }

    @Support
    @NotNull
    public static <T> Field<T> cast(Object value, Class<T> type) {
        return Tools.field(value, type).cast(type);
    }

    @Support
    @NotNull
    public static <T> Field<T> cast(Field<?> field, Class<T> type) {
        return Tools.nullSafe(field).cast(type);
    }

    @Support
    @NotNull
    public static <T> Field<T> castNull(DataType<T> dataType) {
        return (Field<T>) inline((Object) null).cast(dataType);
    }

    @Support
    @NotNull
    public static <T> Field<T> cast(Object value, DataType<T> type) {
        return Tools.field(value).cast(type);
    }

    @Support
    @NotNull
    public static <T> Field<T> cast(Field<?> field, DataType<T> type) {
        return Tools.nullSafe(field).cast(type);
    }

    @Support
    @NotNull
    public static <T> Field<T> castNull(Class<T> cls) {
        return (Field<T>) inline((Object) null).cast(cls);
    }

    @Support
    @NotNull
    public static <T> Field<T> coalesce(T value, T... values) {
        return coalesce0(Tools.field(value), Tools.fieldsArray(values));
    }

    @Support
    @NotNull
    public static <T> Field<T> coalesce(Field<T> field, T value) {
        return coalesce0(field, Tools.field(value, field));
    }

    @Support
    @NotNull
    public static <T> Field<T> coalesce(Field<T> field, Field<?>... fields) {
        return coalesce0(field, fields);
    }

    static <T> Field<T> coalesce0(Field<T> field, Field<?>... fields) {
        return new Coalesce(Tools.nullSafe(Tools.combine((Field<?>) field, fields)));
    }

    @Support
    @NotNull
    public static <Z> Field<Z> nvl2(Field<?> value, Z valueIfNotNull, Z valueIfNull) {
        return nvl20(Tools.nullSafe(value), Tools.field(valueIfNotNull), Tools.field(valueIfNull));
    }

    @Support
    @NotNull
    public static <Z> Field<Z> nvl2(Field<?> value, Z valueIfNotNull, Field<Z> valueIfNull) {
        return nvl20(Tools.nullSafe(value), Tools.field(valueIfNotNull, valueIfNull), Tools.nullSafe(valueIfNull));
    }

    @Support
    @NotNull
    public static <Z> Field<Z> nvl2(Field<?> value, Field<Z> valueIfNotNull, Z valueIfNull) {
        return nvl20(Tools.nullSafe(value), Tools.nullSafe(valueIfNotNull), Tools.field(valueIfNull, valueIfNotNull));
    }

    @Support
    @NotNull
    public static <Z> Field<Z> nvl2(Field<?> value, Field<Z> valueIfNotNull, Field<Z> valueIfNull) {
        return nvl20(value, valueIfNotNull, valueIfNull);
    }

    static <Z> Field<Z> nvl20(Field<?> value, Field<Z> valueIfNotNull, Field<Z> valueIfNull) {
        return new Nvl2(Tools.nullSafe(value), Tools.nullSafe(valueIfNotNull), Tools.nullSafe(valueIfNull));
    }

    @Support
    @NotNull
    public static <T> Field<T> iif(Condition condition, T ifTrue, T ifFalse) {
        return iif0(Names.N_IIF, condition, Tools.field(ifTrue), Tools.field(ifFalse));
    }

    @Support
    @NotNull
    public static <T> Field<T> iif(Condition condition, T ifTrue, Field<T> ifFalse) {
        return iif0(Names.N_IIF, condition, Tools.field(ifTrue, ifFalse), Tools.nullSafe(ifFalse));
    }

    @Support
    @NotNull
    public static <T> Field<T> iif(Condition condition, Field<T> ifTrue, T ifFalse) {
        return iif0(Names.N_IIF, condition, Tools.nullSafe(ifTrue), Tools.field(ifFalse, ifTrue));
    }

    @Support
    @NotNull
    public static <T> Field<T> iif(Condition condition, Field<T> ifTrue, Field<T> ifFalse) {
        return iif0(Names.N_IIF, condition, ifTrue, ifFalse);
    }

    static <T> Field<T> iif0(Name name, Condition condition, Field<T> ifTrue, Field<T> ifFalse) {
        return new Iif(name, condition, Tools.nullSafe(ifTrue), Tools.nullSafe(ifFalse));
    }

    @Support
    @NotNull
    public static Condition exists(Select<?> query) {
        return new Exists(query);
    }

    @Support
    @NotNull
    public static Condition notExists(Select<?> query) {
        return not(exists(query));
    }

    @Support
    @NotNull
    public static Condition not(Condition condition) {
        return new Not(condition);
    }

    @Support
    @NotNull
    public static Field<Boolean> not(Field<Boolean> field) {
        return new NotField(field);
    }

    @Support
    @NotNull
    public static Condition unique(Select<?> query) {
        return new Unique(query);
    }

    @Support
    @NotNull
    public static Condition notUnique(Select<?> query) {
        return not(unique(query));
    }

    @Support
    @NotNull
    public static <T> Field<T> excluded(Field<T> field) {
        return new Excluded(field);
    }

    @Support
    @NotNull
    public static <T extends Number> Field<T> abs(T value) {
        return new Abs(Tools.field(value));
    }

    @Support
    @NotNull
    public static <T extends Number> Field<T> abs(Field<T> value) {
        return new Abs(value);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<BigDecimal> acos(Number value) {
        return new Acos(Tools.field(value));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<BigDecimal> acos(Field<? extends Number> value) {
        return new Acos(value);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<BigDecimal> acosh(Number value) {
        return new Acosh(Tools.field(value));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<BigDecimal> acosh(Field<? extends Number> value) {
        return new Acosh(value);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<BigDecimal> acoth(Number value) {
        return new Acoth(Tools.field(value));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<BigDecimal> acoth(Field<? extends Number> value) {
        return new Acoth(value);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<BigDecimal> asin(Number value) {
        return new Asin(Tools.field(value));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<BigDecimal> asin(Field<? extends Number> value) {
        return new Asin(value);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<BigDecimal> asinh(Number value) {
        return new Asinh(Tools.field(value));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<BigDecimal> asinh(Field<? extends Number> value) {
        return new Asinh(value);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<BigDecimal> atan(Number value) {
        return new Atan(Tools.field(value));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<BigDecimal> atan(Field<? extends Number> value) {
        return new Atan(value);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<BigDecimal> atan2(Number x, Number y) {
        return new Atan2(Tools.field(x), Tools.field(y));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<BigDecimal> atan2(Number x, Field<? extends Number> y) {
        return new Atan2(Tools.field(x), y);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<BigDecimal> atan2(Field<? extends Number> x, Number y) {
        return new Atan2(x, Tools.field(y));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<BigDecimal> atan2(Field<? extends Number> x, Field<? extends Number> y) {
        return new Atan2(x, y);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<BigDecimal> atanh(Number value) {
        return new Atanh(Tools.field(value));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<BigDecimal> atanh(Field<? extends Number> value) {
        return new Atanh(value);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitAnd(T arg1, T arg2) {
        return new BitAnd(Tools.field(arg1), Tools.field(arg2));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitAnd(T arg1, Field<T> arg2) {
        return new BitAnd(Tools.field(arg1), arg2);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitAnd(Field<T> arg1, T arg2) {
        return new BitAnd(arg1, Tools.field(arg2, arg1));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitAnd(Field<T> arg1, Field<T> arg2) {
        return new BitAnd(arg1, arg2);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<Integer> bitCount(Number value) {
        return new BitCount(Tools.field(value));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<Integer> bitCount(Field<? extends Number> value) {
        return new BitCount(value);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitGet(Field<T> value, int bit) {
        return new BitGet(value, Tools.field(bit));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitGet(Field<T> value, Field<? extends Number> bit) {
        return new BitGet(value, bit);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitNand(T arg1, T arg2) {
        return new BitNand(Tools.field(arg1), Tools.field(arg2));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitNand(T arg1, Field<T> arg2) {
        return new BitNand(Tools.field(arg1), arg2);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitNand(Field<T> arg1, T arg2) {
        return new BitNand(arg1, Tools.field(arg2, arg1));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitNand(Field<T> arg1, Field<T> arg2) {
        return new BitNand(arg1, arg2);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitNor(T arg1, T arg2) {
        return new BitNor(Tools.field(arg1), Tools.field(arg2));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitNor(T arg1, Field<T> arg2) {
        return new BitNor(Tools.field(arg1), arg2);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitNor(Field<T> arg1, T arg2) {
        return new BitNor(arg1, Tools.field(arg2, arg1));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitNor(Field<T> arg1, Field<T> arg2) {
        return new BitNor(arg1, arg2);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitNot(T arg1) {
        return new BitNot(Tools.field(arg1));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitNot(Field<T> arg1) {
        return new BitNot(arg1);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitOr(T arg1, T arg2) {
        return new BitOr(Tools.field(arg1), Tools.field(arg2));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitOr(T arg1, Field<T> arg2) {
        return new BitOr(Tools.field(arg1), arg2);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitOr(Field<T> arg1, T arg2) {
        return new BitOr(arg1, Tools.field(arg2, arg1));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitOr(Field<T> arg1, Field<T> arg2) {
        return new BitOr(arg1, arg2);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitSet(Field<T> value, int bit, T newValue) {
        return new BitSet(value, Tools.field(bit), Tools.field(newValue, value));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitSet(Field<T> value, int bit, Field<T> newValue) {
        return new BitSet(value, Tools.field(bit), newValue);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitSet(Field<T> value, Field<? extends Number> bit, T newValue) {
        return new BitSet(value, bit, Tools.field(newValue, value));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitSet(Field<T> value, Field<? extends Number> bit, Field<T> newValue) {
        return new BitSet(value, bit, newValue);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitSet(Field<T> value, int bit) {
        return new BitSet(value, Tools.field(bit));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitSet(Field<T> value, Field<? extends Number> bit) {
        return new BitSet(value, bit);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitXNor(T arg1, T arg2) {
        return new BitXNor(Tools.field(arg1), Tools.field(arg2));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitXNor(T arg1, Field<T> arg2) {
        return new BitXNor(Tools.field(arg1), arg2);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitXNor(Field<T> arg1, T arg2) {
        return new BitXNor(arg1, Tools.field(arg2, arg1));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitXNor(Field<T> arg1, Field<T> arg2) {
        return new BitXNor(arg1, arg2);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitXor(T arg1, T arg2) {
        return new BitXor(Tools.field(arg1), Tools.field(arg2));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitXor(T arg1, Field<T> arg2) {
        return new BitXor(Tools.field(arg1), arg2);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitXor(Field<T> arg1, T arg2) {
        return new BitXor(arg1, Tools.field(arg2, arg1));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> bitXor(Field<T> arg1, Field<T> arg2) {
        return new BitXor(arg1, arg2);
    }

    @Support
    @NotNull
    public static <T extends Number> Field<T> ceil(T value) {
        return new Ceil(Tools.field(value));
    }

    @Support
    @NotNull
    public static <T extends Number> Field<T> ceil(Field<T> value) {
        return new Ceil(value);
    }

    @Support
    @NotNull
    public static Field<BigDecimal> cos(Number value) {
        return new Cos(Tools.field(value));
    }

    @Support
    @NotNull
    public static Field<BigDecimal> cos(Field<? extends Number> value) {
        return new Cos(value);
    }

    @Support
    @NotNull
    public static Field<BigDecimal> cosh(Number value) {
        return new Cosh(Tools.field(value));
    }

    @Support
    @NotNull
    public static Field<BigDecimal> cosh(Field<? extends Number> value) {
        return new Cosh(value);
    }

    @Support
    @NotNull
    public static Field<BigDecimal> cot(Number value) {
        return new Cot(Tools.field(value));
    }

    @Support
    @NotNull
    public static Field<BigDecimal> cot(Field<? extends Number> value) {
        return new Cot(value);
    }

    @Support
    @NotNull
    public static Field<BigDecimal> coth(Number value) {
        return new Coth(Tools.field(value));
    }

    @Support
    @NotNull
    public static Field<BigDecimal> coth(Field<? extends Number> value) {
        return new Coth(value);
    }

    @Support
    @NotNull
    public static Field<BigDecimal> deg(Number radians) {
        return new Degrees(Tools.field(radians));
    }

    @Support
    @NotNull
    public static Field<BigDecimal> deg(Field<? extends Number> radians) {
        return new Degrees(radians);
    }

    @Support
    @NotNull
    public static Field<BigDecimal> e() {
        return new Euler();
    }

    @Support
    @NotNull
    public static Field<BigDecimal> exp(Number value) {
        return new Exp(Tools.field(value));
    }

    @Support
    @NotNull
    public static Field<BigDecimal> exp(Field<? extends Number> value) {
        return new Exp(value);
    }

    @Support
    @NotNull
    public static <T extends Number> Field<T> floor(T value) {
        return new Floor(Tools.field(value));
    }

    @Support
    @NotNull
    public static <T extends Number> Field<T> floor(Field<T> value) {
        return new Floor(value);
    }

    @Support
    @NotNull
    public static Field<BigDecimal> ln(Number value) {
        return new Ln(Tools.field(value));
    }

    @Support
    @NotNull
    public static Field<BigDecimal> ln(Field<? extends Number> value) {
        return new Ln(value);
    }

    @Support
    @NotNull
    public static Field<BigDecimal> log(Number value, int base) {
        return new Log(Tools.field(value), Tools.field(base));
    }

    @Support
    @NotNull
    public static Field<BigDecimal> log(Number value, Field<? extends Number> base) {
        return new Log(Tools.field(value), base);
    }

    @Support
    @NotNull
    public static Field<BigDecimal> log(Field<? extends Number> value, int base) {
        return new Log(value, Tools.field(base));
    }

    @Support
    @NotNull
    public static Field<BigDecimal> log(Field<? extends Number> value, Field<? extends Number> base) {
        return new Log(value, base);
    }

    @Support
    @NotNull
    public static Field<BigDecimal> log10(Number value) {
        return new Log10(Tools.field(value));
    }

    @Support
    @NotNull
    public static Field<BigDecimal> log10(Field<? extends Number> value) {
        return new Log10(value);
    }

    @Support
    @NotNull
    public static Field<BigDecimal> pi() {
        return new Pi();
    }

    @Support
    @NotNull
    public static Field<BigDecimal> power(Number base, Number exponent) {
        return new Power(Tools.field(base), Tools.field(exponent));
    }

    @Support
    @NotNull
    public static Field<BigDecimal> power(Number base, Field<? extends Number> exponent) {
        return new Power(Tools.field(base), exponent);
    }

    @Support
    @NotNull
    public static Field<BigDecimal> power(Field<? extends Number> base, Number exponent) {
        return new Power(base, Tools.field(exponent));
    }

    @Support
    @NotNull
    public static Field<BigDecimal> power(Field<? extends Number> base, Field<? extends Number> exponent) {
        return new Power(base, exponent);
    }

    @Support
    @NotNull
    public static Field<BigDecimal> rad(Number degrees) {
        return new Radians(Tools.field(degrees));
    }

    @Support
    @NotNull
    public static Field<BigDecimal> rad(Field<? extends Number> degrees) {
        return new Radians(degrees);
    }

    @Support
    @NotNull
    public static Field<BigDecimal> rand() {
        return new Rand();
    }

    @Support
    @NotNull
    public static <T extends Number> Field<T> round(T value, int decimals) {
        return new Round(Tools.field(value), Tools.field(decimals));
    }

    @Support
    @NotNull
    public static <T extends Number> Field<T> round(T value, Field<Integer> decimals) {
        return new Round(Tools.field(value), decimals);
    }

    @Support
    @NotNull
    public static <T extends Number> Field<T> round(Field<T> value, int decimals) {
        return new Round(value, Tools.field(decimals));
    }

    @Support
    @NotNull
    public static <T extends Number> Field<T> round(Field<T> value, Field<Integer> decimals) {
        return new Round(value, decimals);
    }

    @Support
    @NotNull
    public static <T extends Number> Field<T> round(T value) {
        return new Round(Tools.field(value));
    }

    @Support
    @NotNull
    public static <T extends Number> Field<T> round(Field<T> value) {
        return new Round(value);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> shl(T value, Number count) {
        return new Shl(Tools.field(value), Tools.field(count));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> shl(T value, Field<? extends Number> count) {
        return new Shl(Tools.field(value), count);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> shl(Field<T> value, Number count) {
        return new Shl(value, Tools.field(count));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> shl(Field<T> value, Field<? extends Number> count) {
        return new Shl(value, count);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> shr(T value, Number count) {
        return new Shr(Tools.field(value), Tools.field(count));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> shr(T value, Field<? extends Number> count) {
        return new Shr(Tools.field(value), count);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> shr(Field<T> value, Number count) {
        return new Shr(value, Tools.field(count));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> shr(Field<T> value, Field<? extends Number> count) {
        return new Shr(value, count);
    }

    @Support
    @NotNull
    public static Field<Integer> sign(Number value) {
        return new Sign(Tools.field(value));
    }

    @Support
    @NotNull
    public static Field<Integer> sign(Field<? extends Number> value) {
        return new Sign(value);
    }

    @Support
    @NotNull
    public static Field<BigDecimal> sin(Number value) {
        return new Sin(Tools.field(value));
    }

    @Support
    @NotNull
    public static Field<BigDecimal> sin(Field<? extends Number> value) {
        return new Sin(value);
    }

    @Support
    @NotNull
    public static Field<BigDecimal> sinh(Number value) {
        return new Sinh(Tools.field(value));
    }

    @Support
    @NotNull
    public static Field<BigDecimal> sinh(Field<? extends Number> value) {
        return new Sinh(value);
    }

    @Support
    @NotNull
    public static Field<BigDecimal> sqrt(Number value) {
        return new Sqrt(Tools.field(value));
    }

    @Support
    @NotNull
    public static Field<BigDecimal> sqrt(Field<? extends Number> value) {
        return new Sqrt(value);
    }

    @Support
    @NotNull
    public static <T extends Number> Field<T> square(T value) {
        return new Square(Tools.field(value));
    }

    @Support
    @NotNull
    public static <T extends Number> Field<T> square(Field<T> value) {
        return new Square(value);
    }

    @Support
    @NotNull
    public static Field<BigDecimal> tan(Number value) {
        return new Tan(Tools.field(value));
    }

    @Support
    @NotNull
    public static Field<BigDecimal> tan(Field<? extends Number> value) {
        return new Tan(value);
    }

    @Support
    @NotNull
    public static Field<BigDecimal> tanh(Number value) {
        return new Tanh(Tools.field(value));
    }

    @Support
    @NotNull
    public static Field<BigDecimal> tanh(Field<? extends Number> value) {
        return new Tanh(value);
    }

    @Support
    @NotNull
    public static Field<BigDecimal> tau() {
        return new Tau();
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> trunc(T value, int decimals) {
        return new Trunc(Tools.field(value), Tools.field(decimals));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> trunc(T value, Field<Integer> decimals) {
        return new Trunc(Tools.field(value), decimals);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> trunc(Field<T> value, int decimals) {
        return new Trunc(value, Tools.field(decimals));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> Field<T> trunc(Field<T> value, Field<Integer> decimals) {
        return new Trunc(value, decimals);
    }

    @Support
    @NotNull
    public static <T extends Number> Field<T> widthBucket(Field<T> field, T low, T high, int buckets) {
        return new WidthBucket(field, Tools.field(low, field), Tools.field(high, field), Tools.field(buckets));
    }

    @Support
    @NotNull
    public static <T extends Number> Field<T> widthBucket(Field<T> field, Field<T> low, Field<T> high, Field<Integer> buckets) {
        return new WidthBucket(field, low, high, buckets);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<Integer> ascii(String string) {
        return new Ascii(Tools.field(string));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<Integer> ascii(Field<String> string) {
        return new Ascii(string);
    }

    @Support
    @NotNull
    public static Field<Integer> bitLength(String string) {
        return new BitLength(Tools.field(string));
    }

    @Support
    @NotNull
    public static Field<Integer> bitLength(Field<String> string) {
        return new BitLength(string);
    }

    @Support
    @NotNull
    public static Field<Integer> charLength(String string) {
        return new CharLength(Tools.field(string));
    }

    @Support
    @NotNull
    public static Field<Integer> charLength(Field<String> string) {
        return new CharLength(string);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> chr(Number value) {
        return new Chr(Tools.field(value));
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> chr(Field<? extends Number> value) {
        return new Chr(value);
    }

    @Support
    @NotNull
    public static Field<String> digits(Number value) {
        return new Digits(Tools.field(value));
    }

    @Support
    @NotNull
    public static Field<String> digits(Field<? extends Number> value) {
        return new Digits(value);
    }

    @Support
    @NotNull
    public static Field<String> left(String string, int length) {
        return new Left(Tools.field(string), Tools.field(length));
    }

    @Support
    @NotNull
    public static Field<String> left(String string, Field<? extends Number> length) {
        return new Left(Tools.field(string), length);
    }

    @Support
    @NotNull
    public static Field<String> left(Field<String> string, int length) {
        return new Left(string, Tools.field(length));
    }

    @Support
    @NotNull
    public static Field<String> left(Field<String> string, Field<? extends Number> length) {
        return new Left(string, length);
    }

    @Support
    @NotNull
    public static Field<Integer> length(String string) {
        return charLength(string);
    }

    @Support
    @NotNull
    public static Field<Integer> length(Field<String> string) {
        return charLength(string);
    }

    @Support
    @NotNull
    public static Field<String> lower(String string) {
        return new Lower(Tools.field(string));
    }

    @Support
    @NotNull
    public static Field<String> lower(Field<String> string) {
        return new Lower(string);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> lpad(Field<String> string, int length, String character) {
        return new Lpad(string, Tools.field(length), Tools.field(character));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> lpad(Field<String> string, int length, Field<String> character) {
        return new Lpad(string, Tools.field(length), character);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> lpad(Field<String> string, Field<? extends Number> length, String character) {
        return new Lpad(string, length, Tools.field(character));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> lpad(Field<String> string, Field<? extends Number> length, Field<String> character) {
        return new Lpad(string, length, character);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> lpad(Field<String> string, int length) {
        return new Lpad(string, Tools.field(length));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> lpad(Field<String> string, Field<? extends Number> length) {
        return new Lpad(string, length);
    }

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> ltrim(String string, String characters) {
        return new Ltrim(Tools.field(string), Tools.field(characters));
    }

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> ltrim(String string, Field<String> characters) {
        return new Ltrim(Tools.field(string), characters);
    }

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> ltrim(Field<String> string, String characters) {
        return new Ltrim(string, Tools.field(characters));
    }

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> ltrim(Field<String> string, Field<String> characters) {
        return new Ltrim(string, characters);
    }

    @Support
    @NotNull
    public static Field<String> ltrim(String string) {
        return new Ltrim(Tools.field(string));
    }

    @Support
    @NotNull
    public static Field<String> ltrim(Field<String> string) {
        return new Ltrim(string);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> md5(String string) {
        return new Md5(Tools.field(string));
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> md5(Field<String> string) {
        return new Md5(string);
    }

    @Support
    @NotNull
    public static Field<String> mid(Field<String> string, int startingPosition, int length) {
        return substring(string, startingPosition, length);
    }

    @Support
    @NotNull
    public static Field<String> mid(Field<String> string, int startingPosition, Field<? extends Number> length) {
        return substring(string, startingPosition, length);
    }

    @Support
    @NotNull
    public static Field<String> mid(Field<String> string, Field<? extends Number> startingPosition, int length) {
        return substring(string, startingPosition, length);
    }

    @Support
    @NotNull
    public static Field<String> mid(Field<String> string, Field<? extends Number> startingPosition, Field<? extends Number> length) {
        return substring(string, startingPosition, length);
    }

    @Support
    @NotNull
    public static Field<String> mid(Field<String> string, int startingPosition) {
        return substring(string, startingPosition);
    }

    @Support
    @NotNull
    public static Field<String> mid(Field<String> string, Field<? extends Number> startingPosition) {
        return substring(string, startingPosition);
    }

    @Support
    @NotNull
    public static Field<Integer> octetLength(String string) {
        return new OctetLength(Tools.field(string));
    }

    @Support
    @NotNull
    public static Field<Integer> octetLength(Field<String> string) {
        return new OctetLength(string);
    }

    @Support
    @NotNull
    public static Field<String> overlay(Field<String> in, String placing, Number startIndex, Number length) {
        return new Overlay(in, Tools.field(placing), Tools.field(startIndex), Tools.field(length));
    }

    @Support
    @NotNull
    public static Field<String> overlay(Field<String> in, Field<String> placing, Field<? extends Number> startIndex, Field<? extends Number> length) {
        return new Overlay(in, placing, startIndex, length);
    }

    @Support
    @NotNull
    public static Field<String> overlay(Field<String> in, String placing, Number startIndex) {
        return new Overlay(in, Tools.field(placing), Tools.field(startIndex));
    }

    @Support
    @NotNull
    public static Field<String> overlay(Field<String> in, Field<String> placing, Field<? extends Number> startIndex) {
        return new Overlay(in, placing, startIndex);
    }

    @Support
    @NotNull
    public static Field<Integer> position(String in, String search, int startIndex) {
        return new Position(Tools.field(in), Tools.field(search), Tools.field(startIndex));
    }

    @Support
    @NotNull
    public static Field<Integer> position(String in, String search, Field<? extends Number> startIndex) {
        return new Position(Tools.field(in), Tools.field(search), startIndex);
    }

    @Support
    @NotNull
    public static Field<Integer> position(String in, Field<String> search, int startIndex) {
        return new Position(Tools.field(in), search, Tools.field(startIndex));
    }

    @Support
    @NotNull
    public static Field<Integer> position(String in, Field<String> search, Field<? extends Number> startIndex) {
        return new Position(Tools.field(in), search, startIndex);
    }

    @Support
    @NotNull
    public static Field<Integer> position(Field<String> in, String search, int startIndex) {
        return new Position(in, Tools.field(search), Tools.field(startIndex));
    }

    @Support
    @NotNull
    public static Field<Integer> position(Field<String> in, String search, Field<? extends Number> startIndex) {
        return new Position(in, Tools.field(search), startIndex);
    }

    @Support
    @NotNull
    public static Field<Integer> position(Field<String> in, Field<String> search, int startIndex) {
        return new Position(in, search, Tools.field(startIndex));
    }

    @Support
    @NotNull
    public static Field<Integer> position(Field<String> in, Field<String> search, Field<? extends Number> startIndex) {
        return new Position(in, search, startIndex);
    }

    @Support
    @NotNull
    public static Field<Integer> position(String in, String search) {
        return new Position(Tools.field(in), Tools.field(search));
    }

    @Support
    @NotNull
    public static Field<Integer> position(String in, Field<String> search) {
        return new Position(Tools.field(in), search);
    }

    @Support
    @NotNull
    public static Field<Integer> position(Field<String> in, String search) {
        return new Position(in, Tools.field(search));
    }

    @Support
    @NotNull
    public static Field<Integer> position(Field<String> in, Field<String> search) {
        return new Position(in, search);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> repeat(String string, int count) {
        return new Repeat(Tools.field(string), Tools.field(count));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> repeat(String string, Field<? extends Number> count) {
        return new Repeat(Tools.field(string), count);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> repeat(Field<String> string, int count) {
        return new Repeat(string, Tools.field(count));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> repeat(Field<String> string, Field<? extends Number> count) {
        return new Repeat(string, count);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> replace(Field<String> string, String search, String replace) {
        return new Replace(string, Tools.field(search), Tools.field(replace));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> replace(Field<String> string, String search, Field<String> replace) {
        return new Replace(string, Tools.field(search), replace);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> replace(Field<String> string, Field<String> search, String replace) {
        return new Replace(string, search, Tools.field(replace));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> replace(Field<String> string, Field<String> search, Field<String> replace) {
        return new Replace(string, search, replace);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> replace(Field<String> string, String search) {
        return new Replace(string, Tools.field(search));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> replace(Field<String> string, Field<String> search) {
        return new Replace(string, search);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> reverse(String string) {
        return new Reverse(Tools.field(string));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> reverse(Field<String> string) {
        return new Reverse(string);
    }

    @Support
    @NotNull
    public static Field<String> right(String string, int length) {
        return new Right(Tools.field(string), Tools.field(length));
    }

    @Support
    @NotNull
    public static Field<String> right(String string, Field<? extends Number> length) {
        return new Right(Tools.field(string), length);
    }

    @Support
    @NotNull
    public static Field<String> right(Field<String> string, int length) {
        return new Right(string, Tools.field(length));
    }

    @Support
    @NotNull
    public static Field<String> right(Field<String> string, Field<? extends Number> length) {
        return new Right(string, length);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> rpad(Field<String> string, int length, String character) {
        return new Rpad(string, Tools.field(length), Tools.field(character));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> rpad(Field<String> string, int length, Field<String> character) {
        return new Rpad(string, Tools.field(length), character);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> rpad(Field<String> string, Field<? extends Number> length, String character) {
        return new Rpad(string, length, Tools.field(character));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> rpad(Field<String> string, Field<? extends Number> length, Field<String> character) {
        return new Rpad(string, length, character);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> rpad(Field<String> string, int length) {
        return new Rpad(string, Tools.field(length));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> rpad(Field<String> string, Field<? extends Number> length) {
        return new Rpad(string, length);
    }

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO})
    @NotNull
    public static Field<String> rtrim(String string, String characters) {
        return new Rtrim(Tools.field(string), Tools.field(characters));
    }

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO})
    @NotNull
    public static Field<String> rtrim(String string, Field<String> characters) {
        return new Rtrim(Tools.field(string), characters);
    }

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO})
    @NotNull
    public static Field<String> rtrim(Field<String> string, String characters) {
        return new Rtrim(string, Tools.field(characters));
    }

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO})
    @NotNull
    public static Field<String> rtrim(Field<String> string, Field<String> characters) {
        return new Rtrim(string, characters);
    }

    @Support
    @NotNull
    public static Field<String> rtrim(String string) {
        return new Rtrim(Tools.field(string));
    }

    @Support
    @NotNull
    public static Field<String> rtrim(Field<String> string) {
        return new Rtrim(string);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> space(Number count) {
        return new Space(Tools.field(count));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> space(Field<? extends Number> count) {
        return new Space(count);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> splitPart(Field<String> string, String delimiter, Number n) {
        return new SplitPart(string, Tools.field(delimiter), Tools.field(n));
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> splitPart(Field<String> string, String delimiter, Field<? extends Number> n) {
        return new SplitPart(string, Tools.field(delimiter), n);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> splitPart(Field<String> string, Field<String> delimiter, Number n) {
        return new SplitPart(string, delimiter, Tools.field(n));
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> splitPart(Field<String> string, Field<String> delimiter, Field<? extends Number> n) {
        return new SplitPart(string, delimiter, n);
    }

    @Support
    @NotNull
    public static Field<String> substring(Field<String> string, int startingPosition, int length) {
        return new Substring(string, Tools.field(startingPosition), Tools.field(length));
    }

    @Support
    @NotNull
    public static Field<String> substring(Field<String> string, int startingPosition, Field<? extends Number> length) {
        return new Substring(string, Tools.field(startingPosition), length);
    }

    @Support
    @NotNull
    public static Field<String> substring(Field<String> string, Field<? extends Number> startingPosition, int length) {
        return new Substring(string, startingPosition, Tools.field(length));
    }

    @Support
    @NotNull
    public static Field<String> substring(Field<String> string, Field<? extends Number> startingPosition, Field<? extends Number> length) {
        return new Substring(string, startingPosition, length);
    }

    @Support
    @NotNull
    public static Field<String> substring(Field<String> string, int startingPosition) {
        return new Substring(string, Tools.field(startingPosition));
    }

    @Support
    @NotNull
    public static Field<String> substring(Field<String> string, Field<? extends Number> startingPosition) {
        return new Substring(string, startingPosition);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @NotNull
    public static Field<String> substringIndex(Field<String> string, String delimiter, int n) {
        return new SubstringIndex(string, Tools.field(delimiter), Tools.field(n));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @NotNull
    public static Field<String> substringIndex(Field<String> string, String delimiter, Field<? extends Number> n) {
        return new SubstringIndex(string, Tools.field(delimiter), n);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @NotNull
    public static Field<String> substringIndex(Field<String> string, Field<String> delimiter, int n) {
        return new SubstringIndex(string, delimiter, Tools.field(n));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @NotNull
    public static Field<String> substringIndex(Field<String> string, Field<String> delimiter, Field<? extends Number> n) {
        return new SubstringIndex(string, delimiter, n);
    }

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @NotNull
    public static Field<String> toChar(Object value, String formatMask) {
        return new ToChar(Tools.field(value), Tools.field(formatMask));
    }

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @NotNull
    public static Field<String> toChar(Object value, Field<String> formatMask) {
        return new ToChar(Tools.field(value), formatMask);
    }

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @NotNull
    public static Field<String> toChar(Field<?> value, String formatMask) {
        return new ToChar(value, Tools.field(formatMask));
    }

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @NotNull
    public static Field<String> toChar(Field<?> value, Field<String> formatMask) {
        return new ToChar(value, formatMask);
    }

    @Support
    @NotNull
    public static Field<String> toChar(Object value) {
        return new ToChar(Tools.field(value));
    }

    @Support
    @NotNull
    public static Field<String> toChar(Field<?> value) {
        return new ToChar(value);
    }

    @Support({SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<Date> toDate(String value, String formatMask) {
        return new ToDate(Tools.field(value), Tools.field(formatMask));
    }

    @Support({SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<Date> toDate(String value, Field<String> formatMask) {
        return new ToDate(Tools.field(value), formatMask);
    }

    @Support({SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<Date> toDate(Field<String> value, String formatMask) {
        return new ToDate(value, Tools.field(formatMask));
    }

    @Support({SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<Date> toDate(Field<String> value, Field<String> formatMask) {
        return new ToDate(value, formatMask);
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> toHex(Number value) {
        return new ToHex(Tools.field(value));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> toHex(Field<? extends Number> value) {
        return new ToHex(value);
    }

    @Support({SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<Timestamp> toTimestamp(String value, String formatMask) {
        return new ToTimestamp(Tools.field(value), Tools.field(formatMask));
    }

    @Support({SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<Timestamp> toTimestamp(String value, Field<String> formatMask) {
        return new ToTimestamp(Tools.field(value), formatMask);
    }

    @Support({SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<Timestamp> toTimestamp(Field<String> value, String formatMask) {
        return new ToTimestamp(value, Tools.field(formatMask));
    }

    @Support({SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<Timestamp> toTimestamp(Field<String> value, Field<String> formatMask) {
        return new ToTimestamp(value, formatMask);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> translate(Field<String> string, String from, String to) {
        return new Translate(string, Tools.field(from), Tools.field(to));
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> translate(Field<String> string, String from, Field<String> to) {
        return new Translate(string, Tools.field(from), to);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> translate(Field<String> string, Field<String> from, String to) {
        return new Translate(string, from, Tools.field(to));
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> translate(Field<String> string, Field<String> from, Field<String> to) {
        return new Translate(string, from, to);
    }

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<String> trim(String string, String characters) {
        return new Trim(Tools.field(string), Tools.field(characters));
    }

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<String> trim(String string, Field<String> characters) {
        return new Trim(Tools.field(string), characters);
    }

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<String> trim(Field<String> string, String characters) {
        return new Trim(string, Tools.field(characters));
    }

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<String> trim(Field<String> string, Field<String> characters) {
        return new Trim(string, characters);
    }

    @Support
    @NotNull
    public static Field<String> trim(String string) {
        return new Trim(Tools.field(string));
    }

    @Support
    @NotNull
    public static Field<String> trim(Field<String> string) {
        return new Trim(string);
    }

    @Support
    @NotNull
    public static Field<String> upper(String string) {
        return new Upper(Tools.field(string));
    }

    @Support
    @NotNull
    public static Field<String> upper(Field<String> string) {
        return new Upper(string);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO})
    @NotNull
    public static Field<UUID> uuid() {
        return new Uuid();
    }

    @Support
    @NotNull
    public static Field<Date> dateAdd(Date date, Number interval, DatePart datePart) {
        return new DateAdd(Tools.field(date), Tools.field(interval), datePart);
    }

    @Support
    @NotNull
    public static Field<Date> dateAdd(Date date, Field<? extends Number> interval, DatePart datePart) {
        return new DateAdd(Tools.field(date), interval, datePart);
    }

    @Support
    @NotNull
    public static Field<Date> dateAdd(Field<Date> date, Number interval, DatePart datePart) {
        return new DateAdd(date, Tools.field(interval), datePart);
    }

    @Support
    @NotNull
    public static Field<Date> dateAdd(Field<Date> date, Field<? extends Number> interval, DatePart datePart) {
        return new DateAdd(date, interval, datePart);
    }

    @Support
    @NotNull
    public static Field<Date> dateAdd(Date date, Number interval) {
        return new DateAdd(Tools.field(date), Tools.field(interval));
    }

    @Support
    @NotNull
    public static Field<Date> dateAdd(Date date, Field<? extends Number> interval) {
        return new DateAdd(Tools.field(date), interval);
    }

    @Support
    @NotNull
    public static Field<Date> dateAdd(Field<Date> date, Number interval) {
        return new DateAdd(date, Tools.field(interval));
    }

    @Support
    @NotNull
    public static Field<Date> dateAdd(Field<Date> date, Field<? extends Number> interval) {
        return new DateAdd(date, interval);
    }

    @Support
    @NotNull
    public static Field<LocalDate> localDateAdd(LocalDate date, Number interval, DatePart datePart) {
        return new DateAdd(Tools.field(date), Tools.field(interval), datePart);
    }

    @Support
    @NotNull
    public static Field<LocalDate> localDateAdd(LocalDate date, Field<? extends Number> interval, DatePart datePart) {
        return new DateAdd(Tools.field(date), interval, datePart);
    }

    @Support
    @NotNull
    public static Field<LocalDate> localDateAdd(Field<LocalDate> date, Number interval, DatePart datePart) {
        return new DateAdd(date, Tools.field(interval), datePart);
    }

    @Support
    @NotNull
    public static Field<LocalDate> localDateAdd(Field<LocalDate> date, Field<? extends Number> interval, DatePart datePart) {
        return new DateAdd(date, interval, datePart);
    }

    @Support
    @NotNull
    public static Field<LocalDate> localDateAdd(LocalDate date, Number interval) {
        return new DateAdd(Tools.field(date), Tools.field(interval));
    }

    @Support
    @NotNull
    public static Field<LocalDate> localDateAdd(LocalDate date, Field<? extends Number> interval) {
        return new DateAdd(Tools.field(date), interval);
    }

    @Support
    @NotNull
    public static Field<LocalDate> localDateAdd(Field<LocalDate> date, Number interval) {
        return new DateAdd(date, Tools.field(interval));
    }

    @Support
    @NotNull
    public static Field<LocalDate> localDateAdd(Field<LocalDate> date, Field<? extends Number> interval) {
        return new DateAdd(date, interval);
    }

    @Support
    @NotNull
    public static Field<Timestamp> timestampAdd(Timestamp date, Number interval, DatePart datePart) {
        return new DateAdd(Tools.field(date), Tools.field(interval), datePart);
    }

    @Support
    @NotNull
    public static Field<Timestamp> timestampAdd(Timestamp date, Field<? extends Number> interval, DatePart datePart) {
        return new DateAdd(Tools.field(date), interval, datePart);
    }

    @Support
    @NotNull
    public static Field<Timestamp> timestampAdd(Field<Timestamp> date, Number interval, DatePart datePart) {
        return new DateAdd(date, Tools.field(interval), datePart);
    }

    @Support
    @NotNull
    public static Field<Timestamp> timestampAdd(Field<Timestamp> date, Field<? extends Number> interval, DatePart datePart) {
        return new DateAdd(date, interval, datePart);
    }

    @Support
    @NotNull
    public static Field<Timestamp> timestampAdd(Timestamp date, Number interval) {
        return new DateAdd(Tools.field(date), Tools.field(interval));
    }

    @Support
    @NotNull
    public static Field<Timestamp> timestampAdd(Timestamp date, Field<? extends Number> interval) {
        return new DateAdd(Tools.field(date), interval);
    }

    @Support
    @NotNull
    public static Field<Timestamp> timestampAdd(Field<Timestamp> date, Number interval) {
        return new DateAdd(date, Tools.field(interval));
    }

    @Support
    @NotNull
    public static Field<Timestamp> timestampAdd(Field<Timestamp> date, Field<? extends Number> interval) {
        return new DateAdd(date, interval);
    }

    @Support
    @NotNull
    public static Field<LocalDateTime> localDateTimeAdd(LocalDateTime date, Number interval, DatePart datePart) {
        return new DateAdd(Tools.field(date), Tools.field(interval), datePart);
    }

    @Support
    @NotNull
    public static Field<LocalDateTime> localDateTimeAdd(LocalDateTime date, Field<? extends Number> interval, DatePart datePart) {
        return new DateAdd(Tools.field(date), interval, datePart);
    }

    @Support
    @NotNull
    public static Field<LocalDateTime> localDateTimeAdd(Field<LocalDateTime> date, Number interval, DatePart datePart) {
        return new DateAdd(date, Tools.field(interval), datePart);
    }

    @Support
    @NotNull
    public static Field<LocalDateTime> localDateTimeAdd(Field<LocalDateTime> date, Field<? extends Number> interval, DatePart datePart) {
        return new DateAdd(date, interval, datePart);
    }

    @Support
    @NotNull
    public static Field<LocalDateTime> localDateTimeAdd(LocalDateTime date, Number interval) {
        return new DateAdd(Tools.field(date), Tools.field(interval));
    }

    @Support
    @NotNull
    public static Field<LocalDateTime> localDateTimeAdd(LocalDateTime date, Field<? extends Number> interval) {
        return new DateAdd(Tools.field(date), interval);
    }

    @Support
    @NotNull
    public static Field<LocalDateTime> localDateTimeAdd(Field<LocalDateTime> date, Number interval) {
        return new DateAdd(date, Tools.field(interval));
    }

    @Support
    @NotNull
    public static Field<LocalDateTime> localDateTimeAdd(Field<LocalDateTime> date, Field<? extends Number> interval) {
        return new DateAdd(date, interval);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<Integer> cardinality(Field<? extends Object[]> array) {
        return new Cardinality(array);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T> arrayGet(Field<T[]> array, int index) {
        return new ArrayGet(array, Tools.field(index));
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T> arrayGet(Field<T[]> array, Field<Integer> index) {
        return new ArrayGet(array, index);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T[]> arrayConcat(T[] arg1, T[] arg2) {
        return new ArrayConcat(Tools.field(arg1), Tools.field(arg2));
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T[]> arrayConcat(T[] arg1, Field<T[]> arg2) {
        return new ArrayConcat(Tools.field(arg1), arg2);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T[]> arrayConcat(Field<T[]> arg1, T[] arg2) {
        return new ArrayConcat(arg1, Tools.field(arg2, arg1));
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T[]> arrayConcat(Field<T[]> arg1, Field<T[]> arg2) {
        return new ArrayConcat(arg1, arg2);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T[]> arrayAppend(T[] arg1, T arg2) {
        return new ArrayAppend(Tools.field(arg1), Tools.field(arg2));
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T[]> arrayAppend(T[] arg1, Field<T> arg2) {
        return new ArrayAppend(Tools.field(arg1), arg2);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T[]> arrayAppend(Field<T[]> arg1, T arg2) {
        return new ArrayAppend(arg1, Tools.field(arg2));
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T[]> arrayAppend(Field<T[]> arg1, Field<T> arg2) {
        return new ArrayAppend(arg1, arg2);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T[]> arrayPrepend(T arg1, T[] arg2) {
        return new ArrayPrepend(Tools.field(arg1), Tools.field(arg2));
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T[]> arrayPrepend(T arg1, Field<T[]> arg2) {
        return new ArrayPrepend(Tools.field(arg1), arg2);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T[]> arrayPrepend(Field<T> arg1, T[] arg2) {
        return new ArrayPrepend(arg1, Tools.field(arg2));
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T[]> arrayPrepend(Field<T> arg1, Field<T[]> arg2) {
        return new ArrayPrepend(arg1, arg2);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Condition arrayOverlap(T[] arg1, T[] arg2) {
        return new ArrayOverlap(Tools.field(arg1), Tools.field(arg2));
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Condition arrayOverlap(T[] arg1, Field<T[]> arg2) {
        return new ArrayOverlap(Tools.field(arg1), arg2);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Condition arrayOverlap(Field<T[]> arg1, T[] arg2) {
        return new ArrayOverlap(arg1, Tools.field(arg2, arg1));
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Condition arrayOverlap(Field<T[]> arg1, Field<T[]> arg2) {
        return new ArrayOverlap(arg1, arg2);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T[]> arrayRemove(T[] arg1, T arg2) {
        return new ArrayRemove(Tools.field(arg1), Tools.field(arg2));
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T[]> arrayRemove(T[] arg1, Field<T> arg2) {
        return new ArrayRemove(Tools.field(arg1), arg2);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T[]> arrayRemove(Field<T[]> arg1, T arg2) {
        return new ArrayRemove(arg1, Tools.field(arg2));
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T[]> arrayRemove(Field<T[]> arg1, Field<T> arg2) {
        return new ArrayRemove(arg1, arg2);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T[]> arrayReplace(T[] arg1, T arg2, T arg3) {
        return new ArrayReplace(Tools.field(arg1), Tools.field(arg2), Tools.field(arg3));
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T[]> arrayReplace(Field<T[]> arg1, Field<T> arg2, Field<T> arg3) {
        return new ArrayReplace(arg1, arg2, arg3);
    }

    @Support
    @NotNull
    public static <T> Field<T> nvl(T value, T defaultValue) {
        return new Nvl(Tools.field(value), Tools.field(defaultValue));
    }

    @Support
    @NotNull
    public static <T> Field<T> nvl(T value, Field<T> defaultValue) {
        return new Nvl(Tools.field(value), defaultValue);
    }

    @Support
    @NotNull
    public static <T> Field<T> nvl(Field<T> value, T defaultValue) {
        return new Nvl(value, Tools.field(defaultValue, value));
    }

    @Support
    @NotNull
    public static <T> Field<T> nvl(Field<T> value, Field<T> defaultValue) {
        return new Nvl(value, defaultValue);
    }

    @Support
    @NotNull
    public static <T> Field<T> isnull(T value, T defaultValue) {
        return nvl(value, defaultValue);
    }

    @Support
    @NotNull
    public static <T> Field<T> isnull(T value, Field<T> defaultValue) {
        return nvl((Object) value, (Field) defaultValue);
    }

    @Support
    @NotNull
    public static <T> Field<T> isnull(Field<T> value, T defaultValue) {
        return nvl((Field) value, (Object) defaultValue);
    }

    @Support
    @NotNull
    public static <T> Field<T> isnull(Field<T> value, Field<T> defaultValue) {
        return nvl((Field) value, (Field) defaultValue);
    }

    @Support
    @NotNull
    public static <T> Field<T> ifnull(T value, T defaultValue) {
        return nvl(value, defaultValue);
    }

    @Support
    @NotNull
    public static <T> Field<T> ifnull(T value, Field<T> defaultValue) {
        return nvl((Object) value, (Field) defaultValue);
    }

    @Support
    @NotNull
    public static <T> Field<T> ifnull(Field<T> value, T defaultValue) {
        return nvl((Field) value, (Object) defaultValue);
    }

    @Support
    @NotNull
    public static <T> Field<T> ifnull(Field<T> value, Field<T> defaultValue) {
        return nvl((Field) value, (Field) defaultValue);
    }

    @Support
    @NotNull
    public static <T> Field<T> nullif(T value, T other) {
        return new Nullif(Tools.field(value), Tools.field(other));
    }

    @Support
    @NotNull
    public static <T> Field<T> nullif(T value, Field<T> other) {
        return new Nullif(Tools.field(value), other);
    }

    @Support
    @NotNull
    public static <T> Field<T> nullif(Field<T> value, T other) {
        return new Nullif(value, Tools.field(other, value));
    }

    @Support
    @NotNull
    public static <T> Field<T> nullif(Field<T> value, Field<T> other) {
        return new Nullif(value, other);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.TRINO})
    @NotNull
    public static <T> Field<T> tryCast(Object value, DataType<T> dataType) {
        return new TryCast(Tools.field(value), dataType);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.TRINO})
    @NotNull
    public static <T> Field<T> tryCast(Field<?> value, DataType<T> dataType) {
        return new TryCast(value, dataType);
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> currentCatalog() {
        return new CurrentCatalog();
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> currentSchema() {
        return new CurrentSchema();
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> currentUser() {
        return new CurrentUser();
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static Field<XML> xmlcomment(String comment) {
        return new XMLComment(Tools.field(comment));
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static Field<XML> xmlcomment(Field<String> comment) {
        return new XMLComment(comment);
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static Field<XML> xmlconcat(Field<?>... args) {
        return new XMLConcat(Arrays.asList(args));
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static Field<XML> xmlconcat(Collection<? extends Field<?>> args) {
        return new XMLConcat(new QueryPartList(args));
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static Field<XML> xmlforest(Field<?>... fields) {
        return new XMLForest(Arrays.asList(fields));
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static Field<XML> xmlforest(Collection<? extends Field<?>> fields) {
        return new XMLForest(new QueryPartList(fields));
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static Field<XML> xmlpi(String target, Field<?> content) {
        return new XMLPi(name(target), content);
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static Field<XML> xmlpi(Name target, Field<?> content) {
        return new XMLPi(target, content);
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static Field<XML> xmlpi(String target) {
        return new XMLPi(name(target));
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static Field<XML> xmlpi(Name target) {
        return new XMLPi(target);
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static <T> Field<T> xmlserializeDocument(XML value, DataType<T> type) {
        return new XMLSerialize(false, Tools.field(value), type);
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static <T> Field<T> xmlserializeDocument(Field<XML> value, DataType<T> type) {
        return new XMLSerialize(false, value, type);
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static <T> Field<T> xmlserializeContent(XML value, DataType<T> type) {
        return new XMLSerialize(true, Tools.field(value), type);
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static <T> Field<T> xmlserializeContent(Field<XML> value, DataType<T> type) {
        return new XMLSerialize(true, value, type);
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONArrayNullStep<JSON> jsonArray(Field<?>... fields) {
        return new JSONArray(SQLDataType.JSON, Arrays.asList(fields));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONArrayNullStep<JSON> jsonArray(Collection<? extends Field<?>> fields) {
        return new JSONArray(SQLDataType.JSON, new QueryPartList(fields));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONArrayNullStep<JSONB> jsonbArray(Field<?>... fields) {
        return new JSONArray(SQLDataType.JSONB, Arrays.asList(fields));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONArrayNullStep<JSONB> jsonbArray(Collection<? extends Field<?>> fields) {
        return new JSONArray(SQLDataType.JSONB, new QueryPartList(fields));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONObjectNullStep<JSON> jsonObject(JSONEntry<?>... entries) {
        return new JSONObject(SQLDataType.JSON, Arrays.asList(entries));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONObjectNullStep<JSON> jsonObject(Collection<? extends JSONEntry<?>> entries) {
        return new JSONObject(SQLDataType.JSON, new QueryPartList(entries));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONObjectNullStep<JSONB> jsonbObject(JSONEntry<?>... entries) {
        return new JSONObject(SQLDataType.JSONB, Arrays.asList(entries));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONObjectNullStep<JSONB> jsonbObject(Collection<? extends JSONEntry<?>> entries) {
        return new JSONObject(SQLDataType.JSONB, new QueryPartList(entries));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<JSON> jsonGetElement(JSON field, int index) {
        return new JSONGetElement(Tools.field(field), Tools.field(index));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<JSON> jsonGetElement(JSON field, Field<Integer> index) {
        return new JSONGetElement(Tools.field(field), index);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<JSON> jsonGetElement(Field<JSON> field, int index) {
        return new JSONGetElement(field, Tools.field(index));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<JSON> jsonGetElement(Field<JSON> field, Field<Integer> index) {
        return new JSONGetElement(field, index);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<JSONB> jsonbGetElement(JSONB field, int index) {
        return new JSONBGetElement(Tools.field(field), Tools.field(index));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<JSONB> jsonbGetElement(JSONB field, Field<Integer> index) {
        return new JSONBGetElement(Tools.field(field), index);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<JSONB> jsonbGetElement(Field<JSONB> field, int index) {
        return new JSONBGetElement(field, Tools.field(index));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<JSONB> jsonbGetElement(Field<JSONB> field, Field<Integer> index) {
        return new JSONBGetElement(field, index);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> jsonGetElementAsText(JSON field, int index) {
        return new JSONGetElementAsText(Tools.field(field), Tools.field(index));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> jsonGetElementAsText(JSON field, Field<Integer> index) {
        return new JSONGetElementAsText(Tools.field(field), index);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> jsonGetElementAsText(Field<JSON> field, int index) {
        return new JSONGetElementAsText(field, Tools.field(index));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> jsonGetElementAsText(Field<JSON> field, Field<Integer> index) {
        return new JSONGetElementAsText(field, index);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> jsonbGetElementAsText(JSONB field, int index) {
        return new JSONBGetElementAsText(Tools.field(field), Tools.field(index));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> jsonbGetElementAsText(JSONB field, Field<Integer> index) {
        return new JSONBGetElementAsText(Tools.field(field), index);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> jsonbGetElementAsText(Field<JSONB> field, int index) {
        return new JSONBGetElementAsText(field, Tools.field(index));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> jsonbGetElementAsText(Field<JSONB> field, Field<Integer> index) {
        return new JSONBGetElementAsText(field, index);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<JSON> jsonGetAttribute(JSON field, String attribute) {
        return new JSONGetAttribute(Tools.field(field), Tools.field(attribute));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<JSON> jsonGetAttribute(JSON field, Field<String> attribute) {
        return new JSONGetAttribute(Tools.field(field), attribute);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<JSON> jsonGetAttribute(Field<JSON> field, String attribute) {
        return new JSONGetAttribute(field, Tools.field(attribute));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<JSON> jsonGetAttribute(Field<JSON> field, Field<String> attribute) {
        return new JSONGetAttribute(field, attribute);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<JSONB> jsonbGetAttribute(JSONB field, String attribute) {
        return new JSONBGetAttribute(Tools.field(field), Tools.field(attribute));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<JSONB> jsonbGetAttribute(JSONB field, Field<String> attribute) {
        return new JSONBGetAttribute(Tools.field(field), attribute);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<JSONB> jsonbGetAttribute(Field<JSONB> field, String attribute) {
        return new JSONBGetAttribute(field, Tools.field(attribute));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<JSONB> jsonbGetAttribute(Field<JSONB> field, Field<String> attribute) {
        return new JSONBGetAttribute(field, attribute);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> jsonGetAttributeAsText(JSON field, String attribute) {
        return new JSONGetAttributeAsText(Tools.field(field), Tools.field(attribute));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> jsonGetAttributeAsText(JSON field, Field<String> attribute) {
        return new JSONGetAttributeAsText(Tools.field(field), attribute);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> jsonGetAttributeAsText(Field<JSON> field, String attribute) {
        return new JSONGetAttributeAsText(field, Tools.field(attribute));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> jsonGetAttributeAsText(Field<JSON> field, Field<String> attribute) {
        return new JSONGetAttributeAsText(field, attribute);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> jsonbGetAttributeAsText(JSONB field, String attribute) {
        return new JSONBGetAttributeAsText(Tools.field(field), Tools.field(attribute));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> jsonbGetAttributeAsText(JSONB field, Field<String> attribute) {
        return new JSONBGetAttributeAsText(Tools.field(field), attribute);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> jsonbGetAttributeAsText(Field<JSONB> field, String attribute) {
        return new JSONBGetAttributeAsText(field, Tools.field(attribute));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> jsonbGetAttributeAsText(Field<JSONB> field, Field<String> attribute) {
        return new JSONBGetAttributeAsText(field, attribute);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<JSON> jsonKeys(JSON field) {
        return new JSONKeys(Tools.field(field));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<JSON> jsonKeys(Field<JSON> field) {
        return new JSONKeys(field);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<JSONB> jsonbKeys(JSONB field) {
        return new JSONBKeys(Tools.field(field));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<JSONB> jsonbKeys(Field<JSONB> field) {
        return new JSONBKeys(field);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE})
    @NotNull
    public static Field<JSON> jsonSet(Field<JSON> field, String path, Object value) {
        return new JSONSet(field, Tools.field(path), Tools.field(value));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE})
    @NotNull
    public static Field<JSON> jsonSet(Field<JSON> field, String path, Field<?> value) {
        return new JSONSet(field, Tools.field(path), value);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE})
    @NotNull
    public static Field<JSON> jsonSet(Field<JSON> field, Field<String> path, Object value) {
        return new JSONSet(field, path, Tools.field(value));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE})
    @NotNull
    public static Field<JSON> jsonSet(Field<JSON> field, Field<String> path, Field<?> value) {
        return new JSONSet(field, path, value);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE})
    @NotNull
    public static Field<JSONB> jsonbSet(Field<JSONB> field, String path, Object value) {
        return new JSONBSet(field, Tools.field(path), Tools.field(value));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE})
    @NotNull
    public static Field<JSONB> jsonbSet(Field<JSONB> field, String path, Field<?> value) {
        return new JSONBSet(field, Tools.field(path), value);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE})
    @NotNull
    public static Field<JSONB> jsonbSet(Field<JSONB> field, Field<String> path, Object value) {
        return new JSONBSet(field, path, Tools.field(value));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE})
    @NotNull
    public static Field<JSONB> jsonbSet(Field<JSONB> field, Field<String> path, Field<?> value) {
        return new JSONBSet(field, path, value);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE})
    @NotNull
    public static Field<JSON> jsonInsert(Field<JSON> field, String path, Object value) {
        return new JSONInsert(field, Tools.field(path), Tools.field(value));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE})
    @NotNull
    public static Field<JSON> jsonInsert(Field<JSON> field, String path, Field<?> value) {
        return new JSONInsert(field, Tools.field(path), value);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE})
    @NotNull
    public static Field<JSON> jsonInsert(Field<JSON> field, Field<String> path, Object value) {
        return new JSONInsert(field, path, Tools.field(value));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE})
    @NotNull
    public static Field<JSON> jsonInsert(Field<JSON> field, Field<String> path, Field<?> value) {
        return new JSONInsert(field, path, value);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE})
    @NotNull
    public static Field<JSONB> jsonbInsert(Field<JSONB> field, String path, Object value) {
        return new JSONBInsert(field, Tools.field(path), Tools.field(value));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE})
    @NotNull
    public static Field<JSONB> jsonbInsert(Field<JSONB> field, String path, Field<?> value) {
        return new JSONBInsert(field, Tools.field(path), value);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE})
    @NotNull
    public static Field<JSONB> jsonbInsert(Field<JSONB> field, Field<String> path, Object value) {
        return new JSONBInsert(field, path, Tools.field(value));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE})
    @NotNull
    public static Field<JSONB> jsonbInsert(Field<JSONB> field, Field<String> path, Field<?> value) {
        return new JSONBInsert(field, path, value);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE})
    @NotNull
    public static Field<JSON> jsonReplace(Field<JSON> field, String path, Object value) {
        return new JSONReplace(field, Tools.field(path), Tools.field(value));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE})
    @NotNull
    public static Field<JSON> jsonReplace(Field<JSON> field, String path, Field<?> value) {
        return new JSONReplace(field, Tools.field(path), value);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE})
    @NotNull
    public static Field<JSON> jsonReplace(Field<JSON> field, Field<String> path, Object value) {
        return new JSONReplace(field, path, Tools.field(value));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE})
    @NotNull
    public static Field<JSON> jsonReplace(Field<JSON> field, Field<String> path, Field<?> value) {
        return new JSONReplace(field, path, value);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE})
    @NotNull
    public static Field<JSONB> jsonbReplace(Field<JSONB> field, String path, Object value) {
        return new JSONBReplace(field, Tools.field(path), Tools.field(value));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE})
    @NotNull
    public static Field<JSONB> jsonbReplace(Field<JSONB> field, String path, Field<?> value) {
        return new JSONBReplace(field, Tools.field(path), value);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE})
    @NotNull
    public static Field<JSONB> jsonbReplace(Field<JSONB> field, Field<String> path, Object value) {
        return new JSONBReplace(field, path, Tools.field(value));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE})
    @NotNull
    public static Field<JSONB> jsonbReplace(Field<JSONB> field, Field<String> path, Field<?> value) {
        return new JSONBReplace(field, path, value);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE})
    @NotNull
    public static Field<JSON> jsonRemove(Field<JSON> field, String path) {
        return new JSONRemove(field, Tools.field(path));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE})
    @NotNull
    public static Field<JSON> jsonRemove(Field<JSON> field, Field<String> path) {
        return new JSONRemove(field, path);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE})
    @NotNull
    public static Field<JSONB> jsonbRemove(Field<JSONB> field, String path) {
        return new JSONBRemove(field, Tools.field(path));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE})
    @NotNull
    public static Field<JSONB> jsonbRemove(Field<JSONB> field, Field<String> path) {
        return new JSONBRemove(field, path);
    }

    @Support
    @NotNull
    public static Field<Boolean> field(Condition condition) {
        if (condition instanceof NoCondition) {
            return noField(SQLDataType.BOOLEAN);
        }
        if (!(condition instanceof FieldCondition)) {
            return new ConditionAsField(condition);
        }
        FieldCondition f = (FieldCondition) condition;
        return f.field;
    }

    @Support
    @NotNull
    public static Condition condition(Field<Boolean> field) {
        if (field instanceof Condition) {
            return (Condition) field;
        }
        if (field instanceof NoField) {
            return noCondition();
        }
        if (!(field instanceof ConditionAsField)) {
            return new FieldCondition(field);
        }
        ConditionAsField c = (ConditionAsField) field;
        return c.condition;
    }

    @Support
    @NotNull
    public static <T> AggregateFunction<T> anyValue(Field<T> field) {
        return new AnyValue(field);
    }

    @Support
    @NotNull
    public static AggregateFunction<BigDecimal> avg(Field<? extends Number> field) {
        return new Avg(field, false);
    }

    @Support
    @NotNull
    public static AggregateFunction<BigDecimal> avgDistinct(Field<? extends Number> field) {
        return new Avg(field, true);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> AggregateFunction<T> bitAndAgg(Field<T> value) {
        return new BitAndAgg(value);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> AggregateFunction<T> bitOrAgg(Field<T> value) {
        return new BitOrAgg(value);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> AggregateFunction<T> bitXorAgg(Field<T> value) {
        return new BitXorAgg(value);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> AggregateFunction<T> bitNandAgg(Field<T> value) {
        return new BitNandAgg(value);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> AggregateFunction<T> bitNorAgg(Field<T> value) {
        return new BitNorAgg(value);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T extends Number> AggregateFunction<T> bitXNorAgg(Field<T> value) {
        return new BitXNorAgg(value);
    }

    @Support
    @NotNull
    public static AggregateFunction<Boolean> boolAnd(Field<Boolean> condition) {
        return new BoolAnd(condition(condition));
    }

    @Support
    @NotNull
    public static AggregateFunction<Boolean> boolAnd(Condition condition) {
        return new BoolAnd(condition);
    }

    @Support
    @NotNull
    public static AggregateFunction<Boolean> boolOr(Field<Boolean> condition) {
        return new BoolOr(condition(condition));
    }

    @Support
    @NotNull
    public static AggregateFunction<Boolean> boolOr(Condition condition) {
        return new BoolOr(condition);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static AggregateFunction<BigDecimal> corr(Field<? extends Number> y, Field<? extends Number> x) {
        return new Corr(y, x);
    }

    @Support
    @NotNull
    public static AggregateFunction<Integer> count(Field<?> field) {
        return new Count(field, false);
    }

    @Support
    @NotNull
    public static AggregateFunction<Integer> countDistinct(Field<?> field) {
        return new Count(field, true);
    }

    @Support
    @NotNull
    public static AggregateFunction<BigDecimal> covarSamp(Field<? extends Number> y, Field<? extends Number> x) {
        return new CovarSamp(y, x);
    }

    @Support
    @NotNull
    public static AggregateFunction<BigDecimal> covarPop(Field<? extends Number> y, Field<? extends Number> x) {
        return new CovarPop(y, x);
    }

    @Support
    @NotNull
    public static AggregateFunction<Boolean> every(Field<Boolean> condition) {
        return boolAnd(condition);
    }

    @Support
    @NotNull
    public static AggregateFunction<Boolean> every(Condition condition) {
        return boolAnd(condition);
    }

    @Support
    @NotNull
    public static <T> AggregateFunction<T> max(Field<T> field) {
        return new Max(field, false);
    }

    @Support
    @NotNull
    public static <T> AggregateFunction<T> maxDistinct(Field<T> field) {
        return new Max(field, true);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static AggregateFunction<BigDecimal> median(Field<? extends Number> field) {
        return new Median(field);
    }

    @Support
    @NotNull
    public static <T> AggregateFunction<T> min(Field<T> field) {
        return new Min(field, false);
    }

    @Support
    @NotNull
    public static <T> AggregateFunction<T> minDistinct(Field<T> field) {
        return new Min(field, true);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static AggregateFunction<BigDecimal> product(Field<? extends Number> field) {
        return new Product(field, false);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static AggregateFunction<BigDecimal> productDistinct(Field<? extends Number> field) {
        return new Product(field, true);
    }

    @Support
    @NotNull
    public static AggregateFunction<BigDecimal> regrAvgX(Field<? extends Number> y, Field<? extends Number> x) {
        return new RegrAvgX(y, x);
    }

    @Support
    @NotNull
    public static AggregateFunction<BigDecimal> regrAvgY(Field<? extends Number> y, Field<? extends Number> x) {
        return new RegrAvgY(y, x);
    }

    @Support
    @NotNull
    public static AggregateFunction<BigDecimal> regrCount(Field<? extends Number> y, Field<? extends Number> x) {
        return new RegrCount(y, x);
    }

    @Support
    @NotNull
    public static AggregateFunction<BigDecimal> regrIntercept(Field<? extends Number> y, Field<? extends Number> x) {
        return new RegrIntercept(y, x);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static AggregateFunction<BigDecimal> regrR2(Field<? extends Number> y, Field<? extends Number> x) {
        return new RegrR2(y, x);
    }

    @Support
    @NotNull
    public static AggregateFunction<BigDecimal> regrSlope(Field<? extends Number> y, Field<? extends Number> x) {
        return new RegrSlope(y, x);
    }

    @Support
    @NotNull
    public static AggregateFunction<BigDecimal> regrSXX(Field<? extends Number> y, Field<? extends Number> x) {
        return new RegrSxx(y, x);
    }

    @Support
    @NotNull
    public static AggregateFunction<BigDecimal> regrSXY(Field<? extends Number> y, Field<? extends Number> x) {
        return new RegrSxy(y, x);
    }

    @Support
    @NotNull
    public static AggregateFunction<BigDecimal> regrSYY(Field<? extends Number> y, Field<? extends Number> x) {
        return new RegrSyy(y, x);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static AggregateFunction<BigDecimal> stddevPop(Field<? extends Number> field) {
        return new StddevPop(field);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static AggregateFunction<BigDecimal> stddevSamp(Field<? extends Number> field) {
        return new StddevSamp(field);
    }

    @Support
    @NotNull
    public static AggregateFunction<BigDecimal> sum(Field<? extends Number> field) {
        return new Sum(field, false);
    }

    @Support
    @NotNull
    public static AggregateFunction<BigDecimal> sumDistinct(Field<? extends Number> field) {
        return new Sum(field, true);
    }

    @Support
    @NotNull
    public static AggregateFunction<BigDecimal> varPop(Field<? extends Number> field) {
        return new VarPop(field);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static AggregateFunction<BigDecimal> varSamp(Field<? extends Number> field) {
        return new VarSamp(field);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<String> rpad(Field<String> field, int length, char character) {
        return rpad(field, length, Character.toString(character));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<String> lpad(Field<String> field, int length, char character) {
        return lpad(field, length, Character.toString(character));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static String escape(String value, char escape) {
        String esc = escape;
        return StringUtils.replace(StringUtils.replace(StringUtils.replace(value, esc, esc + esc), QuickTargetSourceCreator.PREFIX_THREAD_LOCAL, esc + "%"), "_", esc + "_");
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<String> escape(Field<String> field, char escape) {
        String esc = escape;
        Field<String> replace = replace(field, inline(esc), inline(esc + esc));
        return replace(replace(replace, inline(QuickTargetSourceCreator.PREFIX_THREAD_LOCAL), inline(esc + "%")), inline("_"), inline(esc + "_"));
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> regexpReplaceAll(Field<String> field, String pattern, String replacement) {
        return regexpReplaceAll(field, Tools.field(pattern), Tools.field(replacement));
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> regexpReplaceAll(Field<String> field, Field<String> pattern, Field<String> replacement) {
        return new RegexpReplace(field, Tools.nullSafe(pattern), Tools.nullSafe(replacement), true);
    }

    @Support({SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> regexpReplaceFirst(Field<String> field, String pattern, String replacement) {
        return regexpReplaceFirst(field, Tools.field(pattern), Tools.field(replacement));
    }

    @Support({SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> regexpReplaceFirst(Field<String> field, Field<String> pattern, Field<String> replacement) {
        return new RegexpReplace(field, Tools.nullSafe(pattern), Tools.nullSafe(replacement), false);
    }

    @Support
    @NotNull
    public static Field<String> insert(Field<String> in, Number startIndex, Number length, String placing) {
        return insert((Field<String>) Tools.nullSafe(in), (Field<? extends Number>) Tools.field(startIndex), (Field<? extends Number>) Tools.field(length), Tools.field(placing));
    }

    @Support
    @NotNull
    public static Field<String> insert(Field<String> in, Field<? extends Number> startIndex, Field<? extends Number> length, Field<String> placing) {
        return overlay(in, placing, startIndex, length);
    }

    @Support
    @NotNull
    public static Field<String> concat(Field<String> field, String value) {
        return concat((Field<?>[]) new Field[]{Tools.nullSafe(field), Tools.field(value)});
    }

    @Support
    @NotNull
    public static Field<String> concat(String value, Field<String> field) {
        return concat((Field<?>[]) new Field[]{Tools.field(value), Tools.nullSafe(field)});
    }

    @Support
    @NotNull
    public static Field<String> concat(String... values) {
        return concat((Field<?>[]) Tools.fieldsArray(values));
    }

    @Support
    @NotNull
    public static Field<String> concat(Field<?>... fields) {
        return new Concat(Tools.nullSafe(fields));
    }

    @Support
    @NotNull
    public static Field<Date> currentDate() {
        return new CurrentDate(SQLDataType.DATE.notNull());
    }

    @Support
    @NotNull
    public static Field<Time> currentTime() {
        return new CurrentTime(SQLDataType.TIME.notNull());
    }

    @Support
    @NotNull
    public static Field<Timestamp> currentTimestamp() {
        return new CurrentTimestamp(SQLDataType.TIMESTAMP.notNull());
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Timestamp> currentTimestamp(int precision) {
        return currentTimestamp(Tools.field(precision));
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Timestamp> currentTimestamp(Field<Integer> precision) {
        return new CurrentTimestamp(SQLDataType.TIMESTAMP.notNull(), precision);
    }

    @Support
    @NotNull
    public static Field<Timestamp> now() {
        return currentTimestamp();
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Timestamp> now(Field<Integer> precision) {
        return currentTimestamp(precision);
    }

    @Support
    @NotNull
    public static Field<LocalDate> currentLocalDate() {
        return new CurrentDate(SQLDataType.LOCALDATE.notNull());
    }

    @Support
    @NotNull
    public static Field<LocalTime> currentLocalTime() {
        return new CurrentTime(SQLDataType.LOCALTIME.notNull());
    }

    @Support
    @NotNull
    public static Field<LocalDateTime> currentLocalDateTime() {
        return new CurrentTimestamp(SQLDataType.LOCALDATETIME.notNull());
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    public static Field<LocalDateTime> currentLocalDateTime(int precision) {
        return currentLocalDateTime(Tools.field(precision));
    }

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    public static Field<LocalDateTime> currentLocalDateTime(Field<Integer> precision) {
        return new CurrentTimestamp(SQLDataType.LOCALDATETIME.notNull(), precision);
    }

    @Support
    @NotNull
    public static Field<OffsetTime> currentOffsetTime() {
        return currentTime().cast(SQLDataType.OFFSETTIME.notNull());
    }

    @Support
    @NotNull
    public static Field<OffsetDateTime> currentOffsetDateTime() {
        return currentTimestamp().cast(SQLDataType.OFFSETDATETIME.notNull());
    }

    @Support
    @NotNull
    public static Field<Instant> currentInstant() {
        return currentTimestamp().cast(SQLDataType.INSTANT.notNull());
    }

    @Support
    @NotNull
    public static Field<Integer> dateDiff(Date endDate, Date startDate) {
        return dateDiff(Tools.field(endDate), Tools.field(startDate));
    }

    @Support
    @NotNull
    public static Field<Integer> dateDiff(Field<Date> endDate, Date startDate) {
        return dateDiff((Field<Date>) Tools.nullSafe(endDate), Tools.field(startDate));
    }

    @Support
    @NotNull
    public static Field<Integer> dateDiff(Date endDate, Field<Date> startDate) {
        return dateDiff(Tools.field(endDate), (Field<Date>) Tools.nullSafe(startDate));
    }

    @Support
    @NotNull
    public static Field<Integer> dateDiff(Field<Date> endDate, Field<Date> startDate) {
        return new DateDiff(null, Tools.nullSafe(startDate), Tools.nullSafe(endDate));
    }

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<Integer> dateDiff(DatePart part, Date startDate, Date endDate) {
        return dateDiff(part, Tools.field(startDate), Tools.field(endDate));
    }

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<Integer> dateDiff(DatePart part, Field<Date> startDate, Date endDate) {
        return dateDiff(part, (Field<Date>) Tools.nullSafe(startDate), Tools.field(endDate));
    }

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<Integer> dateDiff(DatePart part, Date startDate, Field<Date> endDate) {
        return dateDiff(part, Tools.field(startDate), (Field<Date>) Tools.nullSafe(endDate));
    }

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<Integer> dateDiff(DatePart part, Field<Date> startDate, Field<Date> endDate) {
        return new DateDiff(part, Tools.nullSafe(startDate), Tools.nullSafe(endDate));
    }

    @Support
    @NotNull
    public static Field<Date> dateSub(Date date, Number interval) {
        return dateSub(Tools.field(date), (Field<? extends Number>) Tools.field(interval));
    }

    @Support
    @NotNull
    public static <T> Field<Date> dateSub(Date date, Field<? extends Number> interval) {
        return dateSub(Tools.field(date), (Field<? extends Number>) Tools.nullSafe(interval));
    }

    @Support
    @NotNull
    public static <T> Field<Date> dateSub(Field<Date> date, Number interval) {
        return dateSub((Field<Date>) Tools.nullSafe(date), (Field<? extends Number>) Tools.field(interval));
    }

    @Support
    @NotNull
    public static Field<Date> dateSub(Field<Date> date, Field<? extends Number> interval) {
        return Tools.nullSafe(date).sub(interval);
    }

    @Support
    @NotNull
    public static Field<Date> dateSub(Date date, Number interval, DatePart datePart) {
        return dateSub(Tools.field(date), (Field<? extends Number>) Tools.field(interval), datePart);
    }

    @Support
    @NotNull
    public static Field<Date> dateSub(Date date, Field<? extends Number> interval, DatePart datePart) {
        return dateSub(Tools.field(date), (Field<? extends Number>) Tools.nullSafe(interval), datePart);
    }

    @Support
    @NotNull
    public static Field<Date> dateSub(Field<Date> date, Number interval, DatePart datePart) {
        return dateSub((Field<Date>) Tools.nullSafe(date), (Field<? extends Number>) Tools.field(interval), datePart);
    }

    @Support
    @NotNull
    public static Field<Date> dateSub(Field<Date> date, Field<? extends Number> interval, DatePart datePart) {
        return new DateAdd(Tools.nullSafe(date), Tools.nullSafe(interval).neg(), datePart);
    }

    @Support
    @NotNull
    public static Field<Timestamp> timestampSub(Timestamp timestamp, Number interval) {
        return timestampSub(Tools.field(timestamp), (Field<? extends Number>) Tools.field(interval));
    }

    @Support
    @NotNull
    public static Field<Timestamp> timestampSub(Field<Timestamp> timestamp, Field<? extends Number> interval) {
        return Tools.nullSafe(timestamp).sub(interval);
    }

    @Support
    @NotNull
    public static Field<Timestamp> timestampSub(Timestamp date, Number interval, DatePart datePart) {
        return new DateAdd(Tools.field(date), Tools.field(interval).neg(), datePart);
    }

    @Support
    @NotNull
    public static Field<Timestamp> timestampSub(Timestamp date, Field<? extends Number> interval, DatePart datePart) {
        return new DateAdd(Tools.field(date), Tools.nullSafe(interval).neg(), datePart);
    }

    @Support
    @NotNull
    public static Field<Timestamp> timestampSub(Field<Timestamp> date, Number interval, DatePart datePart) {
        return new DateAdd(Tools.nullSafe(date), Tools.field(interval).neg(), datePart);
    }

    @Support
    @NotNull
    public static Field<Timestamp> timestampSub(Field<Timestamp> date, Field<? extends Number> interval, DatePart datePart) {
        return new DateAdd(Tools.nullSafe(date), Tools.nullSafe(interval).neg(), datePart);
    }

    @Support
    @NotNull
    public static Field<DayToSecond> timestampDiff(Timestamp timestamp1, Timestamp timestamp2) {
        return timestampDiff(Tools.field(timestamp1), Tools.field(timestamp2));
    }

    @Support
    @NotNull
    public static Field<DayToSecond> timestampDiff(Field<Timestamp> timestamp1, Timestamp timestamp2) {
        return timestampDiff((Field<Timestamp>) Tools.nullSafe(timestamp1), Tools.field(timestamp2));
    }

    @Support
    @NotNull
    public static Field<DayToSecond> timestampDiff(Timestamp timestamp1, Field<Timestamp> timestamp2) {
        return timestampDiff(Tools.field(timestamp1), (Field<Timestamp>) Tools.nullSafe(timestamp2));
    }

    @Support
    @NotNull
    public static Field<DayToSecond> timestampDiff(Field<Timestamp> timestamp1, Field<Timestamp> timestamp2) {
        return new TimestampDiff(Tools.nullSafe(timestamp1), Tools.nullSafe(timestamp2));
    }

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Integer> timestampDiff(DatePart part, Timestamp startDate, Timestamp endDate) {
        return timestampDiff(part, Tools.field(startDate), Tools.field(endDate));
    }

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Integer> timestampDiff(DatePart part, Field<Timestamp> startDate, Timestamp endDate) {
        return timestampDiff(part, (Field<Timestamp>) Tools.nullSafe(startDate), Tools.field(endDate));
    }

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Integer> timestampDiff(DatePart part, Timestamp startDate, Field<Timestamp> endDate) {
        return timestampDiff(part, Tools.field(startDate), (Field<Timestamp>) Tools.nullSafe(endDate));
    }

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Integer> timestampDiff(DatePart part, Field<Timestamp> startDate, Field<Timestamp> endDate) {
        return new DateDiff(part, Tools.nullSafe(startDate), Tools.nullSafe(endDate));
    }

    @Support
    @NotNull
    public static Field<Integer> localDateDiff(LocalDate endDate, LocalDate startDate) {
        return localDateDiff(Tools.field(endDate), Tools.field(startDate));
    }

    @Support
    @NotNull
    public static Field<Integer> localDateDiff(Field<LocalDate> endDate, LocalDate startDate) {
        return localDateDiff((Field<LocalDate>) Tools.nullSafe(endDate), Tools.field(startDate));
    }

    @Support
    @NotNull
    public static Field<Integer> localDateDiff(LocalDate endDate, Field<LocalDate> startDate) {
        return localDateDiff(Tools.field(endDate), (Field<LocalDate>) Tools.nullSafe(startDate));
    }

    @Support
    @NotNull
    public static Field<Integer> localDateDiff(Field<LocalDate> endDate, Field<LocalDate> startDate) {
        return new DateDiff(null, Tools.nullSafe(startDate), Tools.nullSafe(endDate));
    }

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Integer> localDateDiff(DatePart part, LocalDate startDate, LocalDate endDate) {
        return localDateDiff(part, Tools.field(startDate), Tools.field(endDate));
    }

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Integer> localDateDiff(DatePart part, Field<LocalDate> startDate, LocalDate endDate) {
        return localDateDiff(part, (Field<LocalDate>) Tools.nullSafe(startDate), Tools.field(endDate));
    }

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Integer> localDateDiff(DatePart part, LocalDate startDate, Field<LocalDate> endDate) {
        return localDateDiff(part, Tools.field(startDate), (Field<LocalDate>) Tools.nullSafe(endDate));
    }

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Integer> localDateDiff(DatePart part, Field<LocalDate> startDate, Field<LocalDate> endDate) {
        return new DateDiff(part, Tools.nullSafe(startDate), Tools.nullSafe(endDate));
    }

    @Support
    @NotNull
    public static Field<LocalDate> localDateSub(LocalDate date, Number interval) {
        return localDateSub(Tools.field(date), (Field<? extends Number>) Tools.field(interval));
    }

    @Support
    @NotNull
    public static Field<LocalDate> localDateSub(Field<LocalDate> date, Field<? extends Number> interval) {
        return Tools.nullSafe(date).sub(interval);
    }

    @Support
    @NotNull
    public static Field<LocalDate> localDateSub(LocalDate date, Number interval, DatePart datePart) {
        return localDateSub(Tools.field(date), (Field<? extends Number>) Tools.field(interval), datePart);
    }

    @Support
    @NotNull
    public static Field<LocalDate> localDateSub(LocalDate date, Field<? extends Number> interval, DatePart datePart) {
        return localDateSub(Tools.field(date), (Field<? extends Number>) Tools.nullSafe(interval), datePart);
    }

    @Support
    @NotNull
    public static Field<LocalDate> localDateSub(Field<LocalDate> date, Number interval, DatePart datePart) {
        return localDateSub((Field<LocalDate>) Tools.nullSafe(date), (Field<? extends Number>) Tools.field(interval), datePart);
    }

    @Support
    @NotNull
    public static Field<LocalDate> localDateSub(Field<LocalDate> date, Field<? extends Number> interval, DatePart datePart) {
        return new DateAdd(Tools.nullSafe(date), Tools.nullSafe(interval).neg(), datePart);
    }

    @Support
    @NotNull
    public static Field<LocalDateTime> localDateTimeSub(LocalDateTime timestamp, Number interval) {
        return localDateTimeSub(Tools.field(timestamp), (Field<? extends Number>) Tools.field(interval));
    }

    @Support
    @NotNull
    public static Field<LocalDateTime> localDateTimeSub(Field<LocalDateTime> timestamp, Field<? extends Number> interval) {
        return Tools.nullSafe(timestamp).sub(interval);
    }

    @Support
    @NotNull
    public static Field<LocalDateTime> localDateTimeSub(LocalDateTime date, Number interval, DatePart datePart) {
        return new DateAdd(Tools.field(date), Tools.field(interval).neg(), datePart);
    }

    @Support
    @NotNull
    public static Field<LocalDateTime> localDateTimeSub(LocalDateTime date, Field<? extends Number> interval, DatePart datePart) {
        return new DateAdd(Tools.field(date), Tools.nullSafe(interval).neg(), datePart);
    }

    @Support
    @NotNull
    public static Field<LocalDateTime> localDateTimeSub(Field<LocalDateTime> date, Number interval, DatePart datePart) {
        return new DateAdd(Tools.nullSafe(date), Tools.field(interval).neg(), datePart);
    }

    @Support
    @NotNull
    public static Field<LocalDateTime> localDateTimeSub(Field<LocalDateTime> date, Field<? extends Number> interval, DatePart datePart) {
        return new DateAdd(Tools.nullSafe(date), Tools.nullSafe(interval).neg(), datePart);
    }

    @Support
    @NotNull
    public static Field<DayToSecond> localDateTimeDiff(LocalDateTime timestamp1, LocalDateTime timestamp2) {
        return localDateTimeDiff(Tools.field(timestamp1), Tools.field(timestamp2));
    }

    @Support
    @NotNull
    public static Field<DayToSecond> localDateTimeDiff(Field<LocalDateTime> timestamp1, LocalDateTime timestamp2) {
        return localDateTimeDiff((Field<LocalDateTime>) Tools.nullSafe(timestamp1), Tools.field(timestamp2));
    }

    @Support
    @NotNull
    public static Field<DayToSecond> localDateTimeDiff(LocalDateTime timestamp1, Field<LocalDateTime> timestamp2) {
        return localDateTimeDiff(Tools.field(timestamp1), (Field<LocalDateTime>) Tools.nullSafe(timestamp2));
    }

    @Support
    @NotNull
    public static Field<DayToSecond> localDateTimeDiff(Field<LocalDateTime> timestamp1, Field<LocalDateTime> timestamp2) {
        return new TimestampDiff(Tools.nullSafe(timestamp1), Tools.nullSafe(timestamp2));
    }

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Integer> localDateTimeDiff(DatePart part, LocalDateTime startDate, LocalDateTime endDate) {
        return localDateTimeDiff(part, Tools.field(startDate), Tools.field(endDate));
    }

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Integer> localDateTimeDiff(DatePart part, Field<LocalDateTime> startDate, LocalDateTime endDate) {
        return localDateTimeDiff(part, (Field<LocalDateTime>) Tools.nullSafe(startDate), Tools.field(endDate));
    }

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Integer> localDateTimeDiff(DatePart part, LocalDateTime startDate, Field<LocalDateTime> endDate) {
        return localDateTimeDiff(part, Tools.field(startDate), (Field<LocalDateTime>) Tools.nullSafe(endDate));
    }

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Integer> localDateTimeDiff(DatePart part, Field<LocalDateTime> startDate, Field<LocalDateTime> endDate) {
        return new DateDiff(part, Tools.nullSafe(startDate), Tools.nullSafe(endDate));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Date> trunc(Date date) {
        return trunc(date, DatePart.DAY);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Date> trunc(Date date, DatePart part) {
        return trunc(Tools.field(date), part);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static Field<LocalDate> trunc(LocalDate date) {
        return trunc(date, DatePart.DAY);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static Field<LocalDate> trunc(LocalDate date, DatePart part) {
        return trunc(Tools.field(date), part);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Timestamp> trunc(Timestamp timestamp) {
        return trunc(timestamp, DatePart.DAY);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Timestamp> trunc(Timestamp timestamp, DatePart part) {
        return trunc(Tools.field(timestamp), part);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static Field<LocalDateTime> trunc(LocalDateTime timestamp) {
        return trunc(timestamp, DatePart.DAY);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static Field<LocalDateTime> trunc(LocalDateTime timestamp, DatePart part) {
        return trunc(Tools.field(timestamp), part);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static <T> Field<T> trunc(Field<T> date) {
        return trunc(date, DatePart.DAY);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static <T> Field<T> trunc(Field<T> date, DatePart part) {
        return new TruncDate(date, part);
    }

    @Support
    @NotNull
    public static Field<Integer> extract(java.util.Date value, DatePart datePart) {
        return extract(Tools.field((Timestamp) Convert.convert(value, Timestamp.class)), datePart);
    }

    @Support
    @NotNull
    public static Field<Integer> extract(Temporal value, DatePart datePart) {
        return extract((Field<?>) Tools.field(value), datePart);
    }

    @Support
    @NotNull
    public static Field<Integer> extract(Field<?> field, DatePart datePart) {
        return new Extract(Tools.nullSafe(field), datePart);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<Integer> epoch(java.util.Date value) {
        return extract(value, DatePart.EPOCH);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<Integer> epoch(Temporal value) {
        return extract(value, DatePart.EPOCH);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<Integer> epoch(Field<?> field) {
        return extract(field, DatePart.EPOCH);
    }

    @Support
    @NotNull
    public static Field<Integer> millennium(java.util.Date value) {
        return extract(value, DatePart.MILLENNIUM);
    }

    @Support
    @NotNull
    public static Field<Integer> millennium(Temporal value) {
        return extract(value, DatePart.MILLENNIUM);
    }

    @Support
    @NotNull
    public static Field<Integer> millennium(Field<?> field) {
        return extract(field, DatePart.MILLENNIUM);
    }

    @Support
    @NotNull
    public static Field<Integer> century(java.util.Date value) {
        return extract(value, DatePart.CENTURY);
    }

    @Support
    @NotNull
    public static Field<Integer> century(Temporal value) {
        return extract(value, DatePart.CENTURY);
    }

    @Support
    @NotNull
    public static Field<Integer> century(Field<?> field) {
        return extract(field, DatePart.CENTURY);
    }

    @Support
    @NotNull
    public static Field<Integer> decade(java.util.Date value) {
        return extract(value, DatePart.DECADE);
    }

    @Support
    @NotNull
    public static Field<Integer> decade(Temporal value) {
        return extract(value, DatePart.DECADE);
    }

    @Support
    @NotNull
    public static Field<Integer> decade(Field<?> field) {
        return extract(field, DatePart.DECADE);
    }

    @Support
    @NotNull
    public static Field<Integer> quarter(java.util.Date value) {
        return extract(value, DatePart.QUARTER);
    }

    @Support
    @NotNull
    public static Field<Integer> quarter(Temporal value) {
        return extract(value, DatePart.QUARTER);
    }

    @Support
    @NotNull
    public static Field<Integer> quarter(Field<?> field) {
        return extract(field, DatePart.QUARTER);
    }

    @Support
    @NotNull
    public static Field<Integer> year(java.util.Date value) {
        return extract(value, DatePart.YEAR);
    }

    @Support
    @NotNull
    public static Field<Integer> year(Temporal value) {
        return extract(value, DatePart.YEAR);
    }

    @Support
    @NotNull
    public static Field<Integer> year(Field<?> field) {
        return extract(field, DatePart.YEAR);
    }

    @Support
    @NotNull
    public static Field<Integer> month(java.util.Date value) {
        return extract(value, DatePart.MONTH);
    }

    @Support
    @NotNull
    public static Field<Integer> month(Temporal value) {
        return extract(value, DatePart.MONTH);
    }

    @Support
    @NotNull
    public static Field<Integer> month(Field<?> field) {
        return extract(field, DatePart.MONTH);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Integer> week(java.util.Date value) {
        return extract(value, DatePart.WEEK);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Integer> week(Temporal value) {
        return extract(value, DatePart.WEEK);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Integer> week(Field<?> field) {
        return extract(field, DatePart.WEEK);
    }

    @Support
    @NotNull
    public static Field<Integer> day(java.util.Date value) {
        return extract(value, DatePart.DAY);
    }

    @Support
    @NotNull
    public static Field<Integer> day(Temporal value) {
        return extract(value, DatePart.DAY);
    }

    @Support
    @NotNull
    public static Field<Integer> day(Field<?> field) {
        return extract(field, DatePart.DAY);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<Integer> dayOfWeek(java.util.Date value) {
        return extract(value, DatePart.DAY_OF_WEEK);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<Integer> dayOfWeek(Temporal value) {
        return extract(value, DatePart.DAY_OF_WEEK);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<Integer> dayOfWeek(Field<?> field) {
        return extract(field, DatePart.DAY_OF_WEEK);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<Integer> isoDayOfWeek(java.util.Date value) {
        return extract(value, DatePart.ISO_DAY_OF_WEEK);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<Integer> isoDayOfWeek(Temporal value) {
        return extract(value, DatePart.ISO_DAY_OF_WEEK);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<Integer> isoDayOfWeek(Field<?> field) {
        return extract(field, DatePart.ISO_DAY_OF_WEEK);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<Integer> dayOfYear(java.util.Date value) {
        return extract(value, DatePart.DAY_OF_YEAR);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<Integer> dayOfYear(Temporal value) {
        return extract(value, DatePart.DAY_OF_YEAR);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<Integer> dayOfYear(Field<?> field) {
        return extract(field, DatePart.DAY_OF_YEAR);
    }

    @Support
    @NotNull
    public static Field<Integer> hour(java.util.Date value) {
        return extract(value, DatePart.HOUR);
    }

    @Support
    @NotNull
    public static Field<Integer> hour(Temporal value) {
        return extract(value, DatePart.HOUR);
    }

    @Support
    @NotNull
    public static Field<Integer> hour(Field<?> field) {
        return extract(field, DatePart.HOUR);
    }

    @Support
    @NotNull
    public static Field<Integer> minute(java.util.Date value) {
        return extract(value, DatePart.MINUTE);
    }

    @Support
    @NotNull
    public static Field<Integer> minute(Temporal value) {
        return extract(value, DatePart.MINUTE);
    }

    @Support
    @NotNull
    public static Field<Integer> minute(Field<?> field) {
        return extract(field, DatePart.MINUTE);
    }

    @Support
    @NotNull
    public static Field<Integer> second(java.util.Date value) {
        return extract(value, DatePart.SECOND);
    }

    @Support
    @NotNull
    public static Field<Integer> second(Temporal value) {
        return extract(value, DatePart.SECOND);
    }

    @Support
    @NotNull
    public static Field<Integer> second(Field<?> field) {
        return extract(field, DatePart.SECOND);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Integer> millisecond(java.util.Date value) {
        return extract(value, DatePart.MILLISECOND);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Integer> millisecond(Temporal value) {
        return extract(value, DatePart.MILLISECOND);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Integer> millisecond(Field<?> field) {
        return extract(field, DatePart.MILLISECOND);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Integer> microsecond(java.util.Date value) {
        return extract(value, DatePart.MICROSECOND);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Integer> microsecond(Temporal value) {
        return extract(value, DatePart.MICROSECOND);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Integer> microsecond(Field<?> field) {
        return extract(field, DatePart.MICROSECOND);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Integer> timezone(java.util.Date value) {
        return extract(value, DatePart.TIMEZONE);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Integer> timezone(Temporal value) {
        return extract(value, DatePart.TIMEZONE);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Integer> timezone(Field<?> field) {
        return extract(field, DatePart.TIMEZONE);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Integer> timezoneHour(java.util.Date value) {
        return extract(value, DatePart.TIMEZONE_HOUR);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Integer> timezoneHour(Temporal value) {
        return extract(value, DatePart.TIMEZONE_HOUR);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Integer> timezoneHour(Field<?> field) {
        return extract(field, DatePart.TIMEZONE_HOUR);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Integer> timezoneMinute(java.util.Date value) {
        return extract(value, DatePart.TIMEZONE_MINUTE);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Integer> timezoneMinute(Temporal value) {
        return extract(value, DatePart.TIMEZONE_MINUTE);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static Field<Integer> timezoneMinute(Field<?> field) {
        return extract(field, DatePart.TIMEZONE_MINUTE);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<Date> date(String value) {
        return Tools.field((Date) Convert.convert(value, Date.class));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<Date> date(java.util.Date value) {
        return Tools.field((Date) Convert.convert(value, Date.class));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<Date> date(Field<? extends java.util.Date> field) {
        return new DateOrTime(field, SQLDataType.DATE);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<Time> time(String value) {
        return Tools.field((Time) Convert.convert(value, Time.class));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<Time> time(java.util.Date value) {
        return Tools.field((Time) Convert.convert(value, Time.class));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<Time> time(Field<? extends java.util.Date> field) {
        return new DateOrTime(field, SQLDataType.TIME);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<Timestamp> timestamp(String value) {
        return Tools.field((Timestamp) Convert.convert(value, Timestamp.class));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<Timestamp> timestamp(java.util.Date value) {
        return Tools.field((Timestamp) Convert.convert(value, Timestamp.class));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<Timestamp> timestamp(Field<? extends java.util.Date> field) {
        return new DateOrTime(field, SQLDataType.TIMESTAMP);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<LocalDate> localDate(String value) {
        return Tools.field((LocalDate) Convert.convert(value, LocalDate.class));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<LocalDate> localDate(LocalDate value) {
        return Tools.field(value);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<LocalDate> localDate(Field<LocalDate> field) {
        return new DateOrTime(field, SQLDataType.LOCALDATE);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<LocalTime> localTime(String value) {
        return Tools.field((LocalTime) Convert.convert(value, LocalTime.class));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<LocalTime> localTime(LocalTime value) {
        return Tools.field(value);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<LocalTime> localTime(Field<LocalTime> field) {
        return new DateOrTime(field, SQLDataType.LOCALTIME);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<LocalDateTime> localDateTime(String value) {
        return Tools.field((LocalDateTime) Convert.convert(value, LocalDateTime.class));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<LocalDateTime> localDateTime(LocalDateTime value) {
        return Tools.field(value);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<LocalDateTime> localDateTime(Field<LocalDateTime> field) {
        return new DateOrTime(field, SQLDataType.LOCALDATETIME);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<OffsetTime> offsetTime(String value) {
        return Tools.field((OffsetTime) Convert.convert(value, OffsetTime.class));
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<OffsetTime> offsetTime(OffsetTime value) {
        return Tools.field(value);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<OffsetTime> offsetTime(Field<OffsetTime> field) {
        return new DateOrTime(field, SQLDataType.OFFSETTIME);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<OffsetDateTime> offsetDateTime(String value) {
        return Tools.field((OffsetDateTime) Convert.convert(value, OffsetDateTime.class));
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<OffsetDateTime> offsetDateTime(OffsetDateTime value) {
        return Tools.field(value);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<OffsetDateTime> offsetDateTime(Field<OffsetDateTime> field) {
        return new DateOrTime(field, SQLDataType.OFFSETDATETIME);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<Instant> instant(String value) {
        return Tools.field((Instant) Convert.convert(value, Instant.class));
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<Instant> instant(Instant value) {
        return Tools.field(value);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static Field<Instant> instant(Field<Instant> field) {
        return new DateOrTime(field, SQLDataType.INSTANT);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static Field<LocalDate> toLocalDate(String value, String format) {
        return toDate(value, format).coerce(SQLDataType.LOCALDATE);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static Field<LocalDate> toLocalDate(String value, Field<String> format) {
        return toDate(value, format).coerce(SQLDataType.LOCALDATE);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static Field<LocalDate> toLocalDate(Field<String> value, String format) {
        return toDate(value, format).coerce(SQLDataType.LOCALDATE);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static Field<LocalDate> toLocalDate(Field<String> value, Field<String> format) {
        return toDate(value, format).coerce(SQLDataType.LOCALDATE);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static Field<LocalDateTime> toLocalDateTime(String value, String format) {
        return toTimestamp(value, format).coerce(SQLDataType.LOCALDATETIME);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static Field<LocalDateTime> toLocalDateTime(String value, Field<String> format) {
        return toTimestamp(value, format).coerce(SQLDataType.LOCALDATETIME);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static Field<LocalDateTime> toLocalDateTime(Field<String> value, String format) {
        return toTimestamp(value, format).coerce(SQLDataType.LOCALDATETIME);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    public static Field<LocalDateTime> toLocalDateTime(Field<String> value, Field<String> format) {
        return toTimestamp(value, format).coerce(SQLDataType.LOCALDATETIME);
    }

    @Support
    @NotNull
    public static GroupField emptyGroupingSet() {
        return EmptyGroupingSet.INSTANCE;
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO})
    @NotNull
    public static GroupField rollup(Field<?>... fields) {
        return rollup((FieldOrRow[]) Tools.nullSafe(fields));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO})
    @NotNull
    public static GroupField rollup(FieldOrRow... fields) {
        return new Rollup(fields);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.POSTGRES, SQLDialect.TRINO})
    @NotNull
    public static GroupField cube(Field<?>... fields) {
        return cube((FieldOrRow[]) Tools.nullSafe(fields));
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.POSTGRES, SQLDialect.TRINO})
    @NotNull
    public static GroupField cube(FieldOrRow... fields) {
        return new Cube(fields);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.POSTGRES, SQLDialect.TRINO})
    @NotNull
    public static GroupField groupingSets(Field<?>... fields) {
        List<Field<?>>[] array = (List[]) Tools.map(fields, f -> {
            return Arrays.asList(f);
        }, x$0 -> {
            return new List[x$0];
        });
        return groupingSets(array);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.POSTGRES, SQLDialect.TRINO})
    @NotNull
    public static GroupField groupingSets(Field<?>[]... fieldSets) {
        List<Field<?>>[] array = (List[]) Tools.map(fieldSets, f -> {
            return Arrays.asList(f);
        }, x$0 -> {
            return new List[x$0];
        });
        return groupingSets(array);
    }

    @SafeVarargs
    @Support({SQLDialect.DUCKDB, SQLDialect.POSTGRES, SQLDialect.TRINO})
    @NotNull
    public static GroupField groupingSets(Collection<? extends Field<?>>... fieldSets) {
        return new GroupingSets(fieldSets);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO})
    @NotNull
    public static Field<Integer> grouping(Field<?> field) {
        return function("grouping", SQLDataType.INTEGER, (Field<?>[]) new Field[]{field});
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T> Field<T> greatest(T value, T... values) {
        return greatest(Tools.field(value), (Field<?>[]) Tools.fieldsArray(values));
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T> Field<T> greatest(Field<T> field, Field<?>... others) {
        return new Greatest(Tools.nullSafe(Tools.combine((Field<?>) field, others)));
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T> Field<T> least(T value, T... values) {
        return least(Tools.field(value), (Field<?>[]) Tools.fieldsArray(values));
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T> Field<T> least(Field<T> field, Field<?>... others) {
        return new Least(Tools.nullSafe(Tools.combine((Field<?>) field, others)));
    }

    @Support
    @NotNull
    public static <T extends Number> Field<T> neg(Field<T> field) {
        return field.neg();
    }

    @Support
    @NotNull
    public static <T extends Number> Field<T> minus(Field<T> field) {
        return field.neg();
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    public static <T extends Number> Field<T> trunc(T number) {
        return trunc(Tools.field(number), inline(0));
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static Field<XML> xmlparseDocument(String content) {
        return xmlparseDocument(Tools.field(content));
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static Field<XML> xmlparseDocument(Field<String> content) {
        return new XMLParse(content, QOM.DocumentOrContent.DOCUMENT);
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static Field<XML> xmlparseContent(String content) {
        return xmlparseContent(Tools.field(content));
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static Field<XML> xmlparseContent(Field<String> content) {
        return new XMLParse(content, QOM.DocumentOrContent.CONTENT);
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static Field<XML> xmlelement(String name, Field<?>... content) {
        return xmlelement(name(name), (XMLAttributes) null, Arrays.asList(content));
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static Field<XML> xmlelement(String name, Collection<? extends Field<?>> content) {
        return xmlelement(name(name), (XMLAttributes) null, content);
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static Field<XML> xmlelement(Name name, Field<?>... content) {
        return xmlelement(name, (XMLAttributes) null, Arrays.asList(content));
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static Field<XML> xmlelement(Name name, Collection<? extends Field<?>> content) {
        return xmlelement(name, (XMLAttributes) null, content);
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static Field<XML> xmlelement(String name, XMLAttributes attributes, Field<?>... content) {
        return xmlelement(name(name), attributes, Arrays.asList(content));
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static Field<XML> xmlelement(String name, XMLAttributes attributes, Collection<? extends Field<?>> content) {
        return xmlelement(name(name), attributes, content);
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static Field<XML> xmlelement(Name name, XMLAttributes attributes, Field<?>... content) {
        return xmlelement(name, attributes, Arrays.asList(content));
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static Field<XML> xmlelement(Name name, XMLAttributes attributes, Collection<? extends Field<?>> content) {
        return new XMLElement(name, attributes, content);
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static XMLAttributes xmlattributes(Field<?>... attributes) {
        return xmlattributes(Arrays.asList(attributes));
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static XMLAttributes xmlattributes(Collection<? extends Field<?>> attributes) {
        return new XMLAttributesImpl(attributes);
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static XMLAggOrderByStep<XML> xmlagg(Field<XML> field) {
        return new XMLAgg(field);
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static XMLQueryPassingStep xmlquery(String xpath) {
        return xmlquery(Tools.field(xpath));
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static XMLQueryPassingStep xmlquery(Field<String> xpath) {
        return new XMLQuery(xpath);
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static XMLExistsPassingStep xmlexists(String xpath) {
        return xmlexists(Tools.field(xpath));
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static XMLExistsPassingStep xmlexists(Field<String> xpath) {
        return new XMLExists(xpath);
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static XMLTablePassingStep xmltable(String xpath) {
        return xmltable(Tools.field(xpath));
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static XMLTablePassingStep xmltable(Field<String> xpath) {
        return new XMLTable(xpath);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONValueOnStep<JSON> jsonValue(Field<JSON> json, String path) {
        return jsonValue(json, Tools.field(path));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONValueOnStep<JSON> jsonValue(Field<JSON> json, Field<String> path) {
        return new JSONValue(SQLDataType.JSON, json, path, null);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONValueOnStep<JSONB> jsonbValue(Field<JSONB> json, String path) {
        return jsonbValue(json, Tools.field(path));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONValueOnStep<JSONB> jsonbValue(Field<JSONB> json, Field<String> path) {
        return new JSONValue(SQLDataType.JSONB, json, path, null);
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONEntryValueStep key(String key) {
        return key(Tools.field(key));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONEntryValueStep key(Field<String> key) {
        return new JSONEntryImpl(key);
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> JSONEntry<T> jsonEntry(Field<T> value) {
        return jsonEntry(value.getName(), (Field) value);
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> JSONEntry<T> jsonEntry(String key, T value) {
        return jsonEntry((Field<String>) Tools.field(key), Tools.field(value));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> JSONEntry<T> jsonEntry(String key, Field<T> value) {
        return jsonEntry((Field<String>) Tools.field(key), (Field) value);
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> JSONEntry<T> jsonEntry(String key, Select<? extends Record1<T>> value) {
        return jsonEntry((Field<String>) Tools.field(key), field(value));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> JSONEntry<T> jsonEntry(Field<String> key, T value) {
        return jsonEntry(key, Tools.field(value));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> JSONEntry<T> jsonEntry(Field<String> key, Field<T> value) {
        return new JSONEntryImpl(key, value);
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> JSONEntry<T> jsonEntry(Field<String> key, Select<? extends Record1<T>> value) {
        return jsonEntry(key, field(value));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONObjectNullStep<JSON> jsonObject(String key, Field<?> value) {
        return jsonObject((JSONEntry<?>[]) new JSONEntry[]{jsonEntry(key, (Field) value)});
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONObjectNullStep<JSON> jsonObject(Field<String> key, Field<?> value) {
        return jsonObject((JSONEntry<?>[]) new JSONEntry[]{jsonEntry(key, (Field) value)});
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONObjectNullStep<JSON> jsonObject(Field<String> key, Select<? extends Record1<?>> value) {
        return jsonObject((JSONEntry<?>[]) new JSONEntry[]{jsonEntry(key, value)});
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.19")
    @NotNull
    public static JSONObjectNullStep<JSON> jsonObject(Field<?>... entries) {
        return new JSONObject(SQLDataType.JSON, Tools.jsonEntries(entries));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONObjectNullStep<JSONB> jsonbObject(String key, Field<?> value) {
        return jsonbObject((JSONEntry<?>[]) new JSONEntry[]{jsonEntry(key, (Field) value)});
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONObjectNullStep<JSONB> jsonbObject(Field<String> key, Field<?> value) {
        return jsonbObject((JSONEntry<?>[]) new JSONEntry[]{jsonEntry(key, (Field) value)});
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONObjectNullStep<JSONB> jsonbObject(Field<String> key, Select<? extends Record1<?>> value) {
        return jsonbObject((JSONEntry<?>[]) new JSONEntry[]{jsonEntry(key, value)});
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.19")
    @NotNull
    public static JSONObjectNullStep<JSONB> jsonbObject(Field<?>... entries) {
        return new JSONObject(SQLDataType.JSONB, Tools.jsonEntries(entries));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONObjectNullStep<JSON> jsonObject() {
        return jsonObject(Tools.EMPTY_JSONENTRY);
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONObjectNullStep<JSONB> jsonbObject() {
        return jsonbObject(Tools.EMPTY_JSONENTRY);
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONArrayAggOrderByStep<JSON> jsonArrayAgg(Field<?> value) {
        return new JSONArrayAgg(SQLDataType.JSON, value, false);
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONArrayAggOrderByStep<JSONB> jsonbArrayAgg(Field<?> value) {
        return new JSONArrayAgg(SQLDataType.JSONB, value, false);
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE, SQLDialect.TRINO})
    @NotNull
    public static JSONArrayAggOrderByStep<JSON> jsonArrayAggDistinct(Field<?> value) {
        return new JSONArrayAgg(SQLDataType.JSON, value, true);
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONArrayAggOrderByStep<JSONB> jsonbArrayAggDistinct(Field<?> value) {
        return new JSONArrayAgg(SQLDataType.JSONB, value, true);
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONObjectAggNullStep<JSON> jsonObjectAgg(Field<?> value) {
        return jsonObjectAgg((JSONEntry<?>) jsonEntry(value));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONObjectAggNullStep<JSON> jsonObjectAgg(String key, Field<?> value) {
        return jsonObjectAgg(Tools.field(key), value);
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONObjectAggNullStep<JSON> jsonObjectAgg(Field<String> key, Field<?> value) {
        return jsonObjectAgg((JSONEntry<?>) jsonEntry(key, (Field) value));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONObjectAggNullStep<JSON> jsonObjectAgg(JSONEntry<?> entry) {
        return new JSONObjectAgg(SQLDataType.JSON, entry);
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONObjectAggNullStep<JSONB> jsonbObjectAgg(Field<?> field) {
        return jsonbObjectAgg((JSONEntry<?>) jsonEntry(field));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONObjectAggNullStep<JSONB> jsonbObjectAgg(String key, Field<?> value) {
        return jsonbObjectAgg(Tools.field(key), value);
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONObjectAggNullStep<JSONB> jsonbObjectAgg(Field<String> key, Field<?> value) {
        return jsonbObjectAgg((JSONEntry<?>) jsonEntry(key, (Field) value));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONObjectAggNullStep<JSONB> jsonbObjectAgg(JSONEntry<?> entry) {
        return new JSONObjectAgg(SQLDataType.JSONB, entry);
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    public static JSONExistsOnStep jsonExists(Field<JSON> field, String path) {
        return jsonExists(field, Tools.field(path));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONExistsOnStep jsonExists(Field<JSON> field, Field<String> path) {
        return new JSONExists(field, Tools.nullSafe(path));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONExistsOnStep jsonbExists(Field<JSONB> field, String path) {
        return jsonbExists(field, Tools.field(path));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONExistsOnStep jsonbExists(Field<JSONB> field, Field<String> path) {
        return new JSONExists(field, Tools.nullSafe(path));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONTableColumnsFirstStep jsonTable(JSON json, String path) {
        return jsonTable(Tools.field(json), Tools.field(path));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONTableColumnsFirstStep jsonTable(Field<JSON> json, Field<String> path) {
        return new JSONTable(Tools.nullSafe(json), Tools.nullSafe(path));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONTableColumnsFirstStep jsonbTable(JSONB json, String path) {
        return jsonbTable(Tools.field(json), Tools.field(path));
    }

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static JSONTableColumnsFirstStep jsonbTable(Field<JSONB> json, Field<String> path) {
        return new JSONTable(Tools.nullSafe(json), Tools.nullSafe(path));
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T[]> array(T... values) {
        return array(Tools.fields(values));
    }

    @SafeVarargs
    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T[]> array(Field<T>... fields) {
        return array(Arrays.asList(fields));
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T[]> array(Collection<? extends Field<T>> fields) {
        return new Array(fields);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T[]> array(Select<? extends Record1<T>> select) {
        return new ArrayQuery(select);
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <R extends Record> Field<Result<R>> multiset(TableLike<R> table) {
        return new Multiset(table);
    }

    @Support
    @NotNull
    public static AggregateFunction<Integer> count() {
        return count((Field<?>) DefaultAggregateFunction.ASTERISK.get());
    }

    @Support
    @NotNull
    public static AggregateFunction<Integer> count(SelectFieldOrAsterisk field) {
        return new DefaultAggregateFunction(Names.N_COUNT, SQLDataType.INTEGER, (Field<?>[]) new Field[]{field("{0}", field)});
    }

    @Support
    @NotNull
    public static AggregateFunction<Integer> count(Table<?> table) {
        return new CountTable(table, false);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static AggregateFunction<Integer> countDistinct(SelectFieldOrAsterisk field) {
        return new DefaultAggregateFunction(true, Names.N_COUNT, (DataType) SQLDataType.INTEGER, (Field<?>[]) new Field[]{field("{0}", field)});
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static AggregateFunction<Integer> countDistinct(Table<?> table) {
        return new CountTable(table, true);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    public static AggregateFunction<Integer> countDistinct(Field<?>... fields) {
        Field<?>[] fields2 = Tools.nullSafe(fields);
        return fields2.length == 0 ? countDistinct(asterisk()) : new DefaultAggregateFunction(true, Names.N_COUNT, (DataType) SQLDataType.INTEGER, fields2);
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> ArrayAggOrderByStep<T[]> arrayAgg(Field<T> field) {
        return new ArrayAgg(false, Tools.nullSafe(field));
    }

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> ArrayAggOrderByStep<T[]> arrayAggDistinct(Field<T> field) {
        return new ArrayAgg(true, Tools.nullSafe(field));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static ArrayAggOrderByStep<Result<Record>> multisetAgg(Collection<? extends SelectField<?>> fields) {
        return new MultisetAgg(false, row((Collection<?>) fields));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static ArrayAggOrderByStep<Result<Record>> multisetAgg(SelectField<?>... fields) {
        return new MultisetAgg(false, row(fields));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @ApiStatus.Obsolete
    @NotNull
    public static ArrayAggOrderByStep<Result<Record>> multisetAgg(Field<?>... fields) {
        return new MultisetAgg(false, row((SelectField<?>[]) fields));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1> ArrayAggOrderByStep<Result<Record1<T1>>> multisetAgg(SelectField<T1> field1) {
        return new MultisetAgg(false, row((SelectField) field1));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2> ArrayAggOrderByStep<Result<Record2<T1, T2>>> multisetAgg(SelectField<T1> field1, SelectField<T2> field2) {
        return new MultisetAgg(false, row((SelectField) field1, (SelectField) field2));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3> ArrayAggOrderByStep<Result<Record3<T1, T2, T3>>> multisetAgg(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3) {
        return new MultisetAgg(false, row((SelectField) field1, (SelectField) field2, (SelectField) field3));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4> ArrayAggOrderByStep<Result<Record4<T1, T2, T3, T4>>> multisetAgg(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4) {
        return new MultisetAgg(false, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5> ArrayAggOrderByStep<Result<Record5<T1, T2, T3, T4, T5>>> multisetAgg(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5) {
        return new MultisetAgg(false, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6> ArrayAggOrderByStep<Result<Record6<T1, T2, T3, T4, T5, T6>>> multisetAgg(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6) {
        return new MultisetAgg(false, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7> ArrayAggOrderByStep<Result<Record7<T1, T2, T3, T4, T5, T6, T7>>> multisetAgg(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7) {
        return new MultisetAgg(false, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8> ArrayAggOrderByStep<Result<Record8<T1, T2, T3, T4, T5, T6, T7, T8>>> multisetAgg(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8) {
        return new MultisetAgg(false, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7, (SelectField) field8));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> ArrayAggOrderByStep<Result<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>> multisetAgg(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9) {
        return new MultisetAgg(false, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7, (SelectField) field8, (SelectField) field9));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> ArrayAggOrderByStep<Result<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>>> multisetAgg(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10) {
        return new MultisetAgg(false, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7, (SelectField) field8, (SelectField) field9, (SelectField) field10));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> ArrayAggOrderByStep<Result<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>>> multisetAgg(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11) {
        return new MultisetAgg(false, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7, (SelectField) field8, (SelectField) field9, (SelectField) field10, (SelectField) field11));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> ArrayAggOrderByStep<Result<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>>> multisetAgg(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12) {
        return new MultisetAgg(false, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7, (SelectField) field8, (SelectField) field9, (SelectField) field10, (SelectField) field11, (SelectField) field12));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> ArrayAggOrderByStep<Result<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>>> multisetAgg(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13) {
        return new MultisetAgg(false, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7, (SelectField) field8, (SelectField) field9, (SelectField) field10, (SelectField) field11, (SelectField) field12, (SelectField) field13));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> ArrayAggOrderByStep<Result<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>>> multisetAgg(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14) {
        return new MultisetAgg(false, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7, (SelectField) field8, (SelectField) field9, (SelectField) field10, (SelectField) field11, (SelectField) field12, (SelectField) field13, (SelectField) field14));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> ArrayAggOrderByStep<Result<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>>> multisetAgg(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15) {
        return new MultisetAgg(false, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7, (SelectField) field8, (SelectField) field9, (SelectField) field10, (SelectField) field11, (SelectField) field12, (SelectField) field13, (SelectField) field14, (SelectField) field15));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> ArrayAggOrderByStep<Result<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>>> multisetAgg(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16) {
        return new MultisetAgg(false, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7, (SelectField) field8, (SelectField) field9, (SelectField) field10, (SelectField) field11, (SelectField) field12, (SelectField) field13, (SelectField) field14, (SelectField) field15, (SelectField) field16));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> ArrayAggOrderByStep<Result<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>>> multisetAgg(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17) {
        return new MultisetAgg(false, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7, (SelectField) field8, (SelectField) field9, (SelectField) field10, (SelectField) field11, (SelectField) field12, (SelectField) field13, (SelectField) field14, (SelectField) field15, (SelectField) field16, (SelectField) field17));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> ArrayAggOrderByStep<Result<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>>> multisetAgg(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18) {
        return new MultisetAgg(false, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7, (SelectField) field8, (SelectField) field9, (SelectField) field10, (SelectField) field11, (SelectField) field12, (SelectField) field13, (SelectField) field14, (SelectField) field15, (SelectField) field16, (SelectField) field17, (SelectField) field18));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> ArrayAggOrderByStep<Result<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>>> multisetAgg(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18, SelectField<T19> field19) {
        return new MultisetAgg(false, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7, (SelectField) field8, (SelectField) field9, (SelectField) field10, (SelectField) field11, (SelectField) field12, (SelectField) field13, (SelectField) field14, (SelectField) field15, (SelectField) field16, (SelectField) field17, (SelectField) field18, (SelectField) field19));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> ArrayAggOrderByStep<Result<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>>> multisetAgg(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18, SelectField<T19> field19, SelectField<T20> field20) {
        return new MultisetAgg(false, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7, (SelectField) field8, (SelectField) field9, (SelectField) field10, (SelectField) field11, (SelectField) field12, (SelectField) field13, (SelectField) field14, (SelectField) field15, (SelectField) field16, (SelectField) field17, (SelectField) field18, (SelectField) field19, (SelectField) field20));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> ArrayAggOrderByStep<Result<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>>> multisetAgg(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18, SelectField<T19> field19, SelectField<T20> field20, SelectField<T21> field21) {
        return new MultisetAgg(false, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7, (SelectField) field8, (SelectField) field9, (SelectField) field10, (SelectField) field11, (SelectField) field12, (SelectField) field13, (SelectField) field14, (SelectField) field15, (SelectField) field16, (SelectField) field17, (SelectField) field18, (SelectField) field19, (SelectField) field20, (SelectField) field21));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> ArrayAggOrderByStep<Result<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>>> multisetAgg(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18, SelectField<T19> field19, SelectField<T20> field20, SelectField<T21> field21, SelectField<T22> field22) {
        return new MultisetAgg(false, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7, (SelectField) field8, (SelectField) field9, (SelectField) field10, (SelectField) field11, (SelectField) field12, (SelectField) field13, (SelectField) field14, (SelectField) field15, (SelectField) field16, (SelectField) field17, (SelectField) field18, (SelectField) field19, (SelectField) field20, (SelectField) field21, (SelectField) field22));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static ArrayAggOrderByStep<Result<Record>> multisetAggDistinct(Collection<? extends Field<?>> fields) {
        return new MultisetAgg(true, row((Collection<?>) fields));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    public static ArrayAggOrderByStep<Result<Record>> multisetAggDistinct(Field<?>... fields) {
        return new MultisetAgg(true, row((SelectField<?>[]) fields));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1> ArrayAggOrderByStep<Result<Record1<T1>>> multisetAggDistinct(SelectField<T1> field1) {
        return new MultisetAgg(true, row((SelectField) field1));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2> ArrayAggOrderByStep<Result<Record2<T1, T2>>> multisetAggDistinct(SelectField<T1> field1, SelectField<T2> field2) {
        return new MultisetAgg(true, row((SelectField) field1, (SelectField) field2));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3> ArrayAggOrderByStep<Result<Record3<T1, T2, T3>>> multisetAggDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3) {
        return new MultisetAgg(true, row((SelectField) field1, (SelectField) field2, (SelectField) field3));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4> ArrayAggOrderByStep<Result<Record4<T1, T2, T3, T4>>> multisetAggDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4) {
        return new MultisetAgg(true, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5> ArrayAggOrderByStep<Result<Record5<T1, T2, T3, T4, T5>>> multisetAggDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5) {
        return new MultisetAgg(true, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6> ArrayAggOrderByStep<Result<Record6<T1, T2, T3, T4, T5, T6>>> multisetAggDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6) {
        return new MultisetAgg(true, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7> ArrayAggOrderByStep<Result<Record7<T1, T2, T3, T4, T5, T6, T7>>> multisetAggDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7) {
        return new MultisetAgg(true, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8> ArrayAggOrderByStep<Result<Record8<T1, T2, T3, T4, T5, T6, T7, T8>>> multisetAggDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8) {
        return new MultisetAgg(true, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7, (SelectField) field8));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> ArrayAggOrderByStep<Result<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>> multisetAggDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9) {
        return new MultisetAgg(true, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7, (SelectField) field8, (SelectField) field9));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> ArrayAggOrderByStep<Result<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>>> multisetAggDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10) {
        return new MultisetAgg(true, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7, (SelectField) field8, (SelectField) field9, (SelectField) field10));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> ArrayAggOrderByStep<Result<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>>> multisetAggDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11) {
        return new MultisetAgg(true, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7, (SelectField) field8, (SelectField) field9, (SelectField) field10, (SelectField) field11));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> ArrayAggOrderByStep<Result<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>>> multisetAggDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12) {
        return new MultisetAgg(true, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7, (SelectField) field8, (SelectField) field9, (SelectField) field10, (SelectField) field11, (SelectField) field12));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> ArrayAggOrderByStep<Result<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>>> multisetAggDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13) {
        return new MultisetAgg(true, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7, (SelectField) field8, (SelectField) field9, (SelectField) field10, (SelectField) field11, (SelectField) field12, (SelectField) field13));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> ArrayAggOrderByStep<Result<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>>> multisetAggDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14) {
        return new MultisetAgg(true, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7, (SelectField) field8, (SelectField) field9, (SelectField) field10, (SelectField) field11, (SelectField) field12, (SelectField) field13, (SelectField) field14));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> ArrayAggOrderByStep<Result<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>>> multisetAggDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15) {
        return new MultisetAgg(true, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7, (SelectField) field8, (SelectField) field9, (SelectField) field10, (SelectField) field11, (SelectField) field12, (SelectField) field13, (SelectField) field14, (SelectField) field15));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> ArrayAggOrderByStep<Result<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>>> multisetAggDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16) {
        return new MultisetAgg(true, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7, (SelectField) field8, (SelectField) field9, (SelectField) field10, (SelectField) field11, (SelectField) field12, (SelectField) field13, (SelectField) field14, (SelectField) field15, (SelectField) field16));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> ArrayAggOrderByStep<Result<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>>> multisetAggDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17) {
        return new MultisetAgg(true, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7, (SelectField) field8, (SelectField) field9, (SelectField) field10, (SelectField) field11, (SelectField) field12, (SelectField) field13, (SelectField) field14, (SelectField) field15, (SelectField) field16, (SelectField) field17));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> ArrayAggOrderByStep<Result<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>>> multisetAggDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18) {
        return new MultisetAgg(true, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7, (SelectField) field8, (SelectField) field9, (SelectField) field10, (SelectField) field11, (SelectField) field12, (SelectField) field13, (SelectField) field14, (SelectField) field15, (SelectField) field16, (SelectField) field17, (SelectField) field18));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> ArrayAggOrderByStep<Result<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>>> multisetAggDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18, SelectField<T19> field19) {
        return new MultisetAgg(true, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7, (SelectField) field8, (SelectField) field9, (SelectField) field10, (SelectField) field11, (SelectField) field12, (SelectField) field13, (SelectField) field14, (SelectField) field15, (SelectField) field16, (SelectField) field17, (SelectField) field18, (SelectField) field19));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> ArrayAggOrderByStep<Result<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>>> multisetAggDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18, SelectField<T19> field19, SelectField<T20> field20) {
        return new MultisetAgg(true, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7, (SelectField) field8, (SelectField) field9, (SelectField) field10, (SelectField) field11, (SelectField) field12, (SelectField) field13, (SelectField) field14, (SelectField) field15, (SelectField) field16, (SelectField) field17, (SelectField) field18, (SelectField) field19, (SelectField) field20));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> ArrayAggOrderByStep<Result<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>>> multisetAggDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18, SelectField<T19> field19, SelectField<T20> field20, SelectField<T21> field21) {
        return new MultisetAgg(true, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7, (SelectField) field8, (SelectField) field9, (SelectField) field10, (SelectField) field11, (SelectField) field12, (SelectField) field13, (SelectField) field14, (SelectField) field15, (SelectField) field16, (SelectField) field17, (SelectField) field18, (SelectField) field19, (SelectField) field20, (SelectField) field21));
    }

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> ArrayAggOrderByStep<Result<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>>> multisetAggDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18, SelectField<T19> field19, SelectField<T20> field20, SelectField<T21> field21, SelectField<T22> field22) {
        return new MultisetAgg(true, row((SelectField) field1, (SelectField) field2, (SelectField) field3, (SelectField) field4, (SelectField) field5, (SelectField) field6, (SelectField) field7, (SelectField) field8, (SelectField) field9, (SelectField) field10, (SelectField) field11, (SelectField) field12, (SelectField) field13, (SelectField) field14, (SelectField) field15, (SelectField) field16, (SelectField) field17, (SelectField) field18, (SelectField) field19, (SelectField) field20, (SelectField) field21, (SelectField) field22));
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES})
    @NotNull
    public static <T> AggregateFunction<T> mode(Field<T> field) {
        return new Mode(Tools.nullSafe(field));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO})
    @NotNull
    public static OrderedAggregateFunction<String> listAgg(Field<?> field) {
        return new ListAgg(false, Tools.nullSafe(field));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO})
    @NotNull
    public static OrderedAggregateFunction<String> listAgg(Field<?> field, String separator) {
        return new ListAgg(false, Tools.nullSafe(field), inline(separator));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    public static OrderedAggregateFunction<String> listAggDistinct(Field<?> field) {
        return new ListAgg(true, Tools.nullSafe(field));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    public static OrderedAggregateFunction<String> listAggDistinct(Field<?> field, String separator) {
        return new ListAgg(true, Tools.nullSafe(field), inline(separator));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO})
    @NotNull
    public static GroupConcatOrderByStep groupConcat(Field<?> field) {
        return new GroupConcat(Tools.nullSafe(field));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO})
    @Deprecated(forRemoval = true, since = "3.12")
    @NotNull
    public static AggregateFunction<String> groupConcat(Field<?> field, String separator) {
        return new GroupConcat(Tools.nullSafe(field)).separator(separator);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    public static GroupConcatOrderByStep groupConcatDistinct(Field<?> field) {
        return new GroupConcat(Tools.nullSafe(field), true);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static OrderedAggregateFunctionOfDeferredType mode() {
        return new ModeDeferred();
    }

    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static OrderedAggregateFunction<Integer> rank(Field<?>... fields) {
        return new DefaultAggregateFunction(Names.N_RANK, SQLDataType.INTEGER, fields);
    }

    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static OrderedAggregateFunction<Integer> rank(Collection<? extends Field<?>> fields) {
        return new DefaultAggregateFunction(Names.N_RANK, SQLDataType.INTEGER, (Field<?>[]) fields.toArray(Tools.EMPTY_FIELD));
    }

    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static OrderedAggregateFunction<Integer> denseRank(Field<?>... fields) {
        return new DefaultAggregateFunction(Names.N_DENSE_RANK, SQLDataType.INTEGER, fields);
    }

    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static OrderedAggregateFunction<Integer> denseRank(Collection<? extends Field<?>> fields) {
        return new DefaultAggregateFunction(Names.N_DENSE_RANK, SQLDataType.INTEGER, (Field<?>[]) fields.toArray(Tools.EMPTY_FIELD));
    }

    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static OrderedAggregateFunction<Integer> percentRank(Field<?>... fields) {
        return new DefaultAggregateFunction(Names.N_PERCENT_RANK, SQLDataType.INTEGER, fields);
    }

    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static OrderedAggregateFunction<Integer> percentRank(Collection<? extends Field<?>> fields) {
        return new DefaultAggregateFunction(Names.N_PERCENT_RANK, SQLDataType.INTEGER, (Field<?>[]) fields.toArray(Tools.EMPTY_FIELD));
    }

    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static OrderedAggregateFunction<BigDecimal> cumeDist(Field<?>... fields) {
        return new DefaultAggregateFunction(Names.N_CUME_DIST, SQLDataType.NUMERIC, fields);
    }

    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static OrderedAggregateFunction<BigDecimal> cumeDist(Collection<? extends Field<?>> fields) {
        return new DefaultAggregateFunction(Names.N_CUME_DIST, SQLDataType.NUMERIC, (Field<?>[]) fields.toArray(Tools.EMPTY_FIELD));
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static OrderedAggregateFunction<BigDecimal> percentileCont(Number number) {
        return percentileCont(val(number));
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static OrderedAggregateFunction<BigDecimal> percentileCont(Field<? extends Number> field) {
        return new DefaultAggregateFunction(Names.N_PERCENTILE_CONT, SQLDataType.NUMERIC, (Field<?>[]) new Field[]{Tools.nullSafe(field)});
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static OrderedAggregateFunction<BigDecimal> percentileDisc(Number number) {
        return percentileDisc(val(number));
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static OrderedAggregateFunction<BigDecimal> percentileDisc(Field<? extends Number> field) {
        return new DefaultAggregateFunction(Names.N_PERCENTILE_DISC, SQLDataType.NUMERIC, (Field<?>[]) new Field[]{Tools.nullSafe(field)});
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationOrderByStep partitionBy(GroupField... fields) {
        return new WindowSpecificationImpl().partitionBy(fields);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationOrderByStep partitionBy(Collection<? extends GroupField> fields) {
        return new WindowSpecificationImpl().partitionBy(fields);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationRowsStep orderBy(Field<?>... fields) {
        return new WindowSpecificationImpl().orderBy((OrderField<?>[]) fields);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationRowsStep orderBy(OrderField<?>... fields) {
        return new WindowSpecificationImpl().orderBy(fields);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationRowsStep orderBy(Collection<? extends OrderField<?>> fields) {
        return new WindowSpecificationImpl().orderBy(fields);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationExcludeStep rowsUnboundedPreceding() {
        return new WindowSpecificationImpl().rowsUnboundedPreceding();
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationExcludeStep rowsPreceding(int number) {
        return new WindowSpecificationImpl().rowsPreceding(number);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationExcludeStep rowsCurrentRow() {
        return new WindowSpecificationImpl().rowsCurrentRow();
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationExcludeStep rowsUnboundedFollowing() {
        return new WindowSpecificationImpl().rowsUnboundedFollowing();
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationExcludeStep rowsFollowing(int number) {
        return new WindowSpecificationImpl().rowsFollowing(number);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationRowsAndStep rowsBetweenUnboundedPreceding() {
        return new WindowSpecificationImpl().rowsBetweenUnboundedPreceding();
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationRowsAndStep rowsBetweenPreceding(int number) {
        return new WindowSpecificationImpl().rowsBetweenPreceding(number);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationRowsAndStep rowsBetweenCurrentRow() {
        return new WindowSpecificationImpl().rowsBetweenCurrentRow();
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationRowsAndStep rowsBetweenUnboundedFollowing() {
        return new WindowSpecificationImpl().rowsBetweenUnboundedFollowing();
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationRowsAndStep rowsBetweenFollowing(int number) {
        return new WindowSpecificationImpl().rowsBetweenFollowing(number);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationExcludeStep rangeUnboundedPreceding() {
        return new WindowSpecificationImpl().rangeUnboundedPreceding();
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationExcludeStep rangePreceding(int number) {
        return new WindowSpecificationImpl().rangePreceding(number);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationExcludeStep rangeCurrentRow() {
        return new WindowSpecificationImpl().rangeCurrentRow();
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationExcludeStep rangeUnboundedFollowing() {
        return new WindowSpecificationImpl().rangeUnboundedFollowing();
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationExcludeStep rangeFollowing(int number) {
        return new WindowSpecificationImpl().rangeFollowing(number);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationRowsAndStep rangeBetweenUnboundedPreceding() {
        return new WindowSpecificationImpl().rangeBetweenUnboundedPreceding();
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationRowsAndStep rangeBetweenPreceding(int number) {
        return new WindowSpecificationImpl().rangeBetweenPreceding(number);
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationRowsAndStep rangeBetweenCurrentRow() {
        return new WindowSpecificationImpl().rangeBetweenCurrentRow();
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationRowsAndStep rangeBetweenUnboundedFollowing() {
        return new WindowSpecificationImpl().rangeBetweenUnboundedFollowing();
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationRowsAndStep rangeBetweenFollowing(int number) {
        return new WindowSpecificationImpl().rangeBetweenFollowing(number);
    }

    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationExcludeStep groupsUnboundedPreceding() {
        return new WindowSpecificationImpl().groupsUnboundedPreceding();
    }

    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationExcludeStep groupsPreceding(int number) {
        return new WindowSpecificationImpl().groupsPreceding(number);
    }

    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationExcludeStep groupsCurrentRow() {
        return new WindowSpecificationImpl().groupsCurrentRow();
    }

    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationExcludeStep groupsUnboundedFollowing() {
        return new WindowSpecificationImpl().groupsUnboundedFollowing();
    }

    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationExcludeStep groupsFollowing(int number) {
        return new WindowSpecificationImpl().groupsFollowing(number);
    }

    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationRowsAndStep groupsBetweenUnboundedPreceding() {
        return new WindowSpecificationImpl().groupsBetweenUnboundedPreceding();
    }

    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationRowsAndStep groupsBetweenPreceding(int number) {
        return new WindowSpecificationImpl().groupsBetweenPreceding(number);
    }

    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationRowsAndStep groupsBetweenCurrentRow() {
        return new WindowSpecificationImpl().groupsBetweenCurrentRow();
    }

    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationRowsAndStep groupsBetweenUnboundedFollowing() {
        return new WindowSpecificationImpl().groupsBetweenUnboundedFollowing();
    }

    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowSpecificationRowsAndStep groupsBetweenFollowing(int number) {
        return new WindowSpecificationImpl().groupsBetweenFollowing(number);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowOverStep<Integer> rowNumber() {
        return new RowNumber();
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowOverStep<Integer> rank() {
        return new Rank();
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowOverStep<Integer> denseRank() {
        return new DenseRank();
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowOverStep<BigDecimal> percentRank() {
        return new PercentRank();
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowOverStep<BigDecimal> cumeDist() {
        return new CumeDist();
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowOverStep<Integer> ntile(int number) {
        return new Ntile(inline(number));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowOverStep<Integer> ntile(Field<Integer> number) {
        return new Ntile(number);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowOverStep<BigDecimal> ratioToReport(Number number) {
        return ratioToReport((Field<? extends Number>) Tools.field(number));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static WindowOverStep<BigDecimal> ratioToReport(Field<? extends Number> field) {
        return new RatioToReport(Tools.nullSafe(field));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> WindowIgnoreNullsStep<T> firstValue(Field<T> field) {
        return new FirstValue(Tools.nullSafe(field));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> WindowIgnoreNullsStep<T> lastValue(Field<T> field) {
        return new LastValue(Tools.nullSafe(field));
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> WindowFromFirstLastStep<T> nthValue(Field<T> field, int nth) {
        return nthValue(field, val(nth));
    }

    @Support({SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> WindowFromFirstLastStep<T> nthValue(Field<T> field, Field<Integer> nth) {
        return new NthValue(Tools.nullSafe(field), Tools.nullSafe(nth));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> WindowIgnoreNullsStep<T> lead(Field<T> field) {
        return new Lead(Tools.nullSafe(field), null, null);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> WindowIgnoreNullsStep<T> lead(Field<T> field, int offset) {
        return lead(field, inline(offset));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> WindowIgnoreNullsStep<T> lead(Field<T> field, Field<Integer> offset) {
        return new Lead(Tools.nullSafe(field), Tools.nullSafe(offset), null);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> WindowIgnoreNullsStep<T> lead(Field<T> field, int offset, T defaultValue) {
        return lead(Tools.nullSafe(field), (Field<Integer>) inline(offset), Tools.field(defaultValue, field));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> WindowIgnoreNullsStep<T> lead(Field<T> field, int offset, Field<T> defaultValue) {
        return lead((Field) field, (Field<Integer>) inline(offset), (Field) defaultValue);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> WindowIgnoreNullsStep<T> lead(Field<T> field, Field<Integer> offset, T defaultValue) {
        return lead((Field) field, (Field<Integer>) Tools.nullSafe(offset), Tools.field(defaultValue, field));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> WindowIgnoreNullsStep<T> lead(Field<T> field, Field<Integer> offset, Field<T> defaultValue) {
        return new Lead(Tools.nullSafe(field), Tools.nullSafe(offset), Tools.nullSafe(defaultValue));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> WindowIgnoreNullsStep<T> lag(Field<T> field) {
        return new Lag(Tools.nullSafe(field), null, null);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> WindowIgnoreNullsStep<T> lag(Field<T> field, int offset) {
        return lag(field, inline(offset));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> WindowIgnoreNullsStep<T> lag(Field<T> field, Field<Integer> offset) {
        return new Lag(Tools.nullSafe(field), Tools.nullSafe(offset), null);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> WindowIgnoreNullsStep<T> lag(Field<T> field, int offset, T defaultValue) {
        return lag(Tools.nullSafe(field), (Field<Integer>) inline(offset), Tools.field(defaultValue, field));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> WindowIgnoreNullsStep<T> lag(Field<T> field, int offset, Field<T> defaultValue) {
        return lag((Field) field, (Field<Integer>) inline(offset), (Field) defaultValue);
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> WindowIgnoreNullsStep<T> lag(Field<T> field, Field<Integer> offset, T defaultValue) {
        return lag((Field) field, offset, Tools.field(defaultValue, field));
    }

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> WindowIgnoreNullsStep<T> lag(Field<T> field, Field<Integer> offset, Field<T> defaultValue) {
        return new Lag(Tools.nullSafe(field), Tools.nullSafe(offset), Tools.nullSafe(defaultValue));
    }

    @Support
    @NotNull
    public static <T> Param<Object> param() {
        return param(Object.class);
    }

    @Support
    @NotNull
    public static <T> Param<T> param(Class<T> type) {
        return param(DefaultDataType.getDataType((SQLDialect) null, type));
    }

    @Support
    @NotNull
    public static <T> Param<T> param(DataType<T> type) {
        return new Val(null, type, false);
    }

    @Support
    @NotNull
    public static <T> Param<T> param(Field<T> field) {
        return param(field.getDataType());
    }

    @Support
    @NotNull
    public static Param<Object> param(String name) {
        return param(name, Object.class);
    }

    @Support
    @NotNull
    public static <T> Param<T> param(String name, Class<T> type) {
        return param(name, DefaultDataType.getDataType((SQLDialect) null, type));
    }

    @Support
    @NotNull
    public static <T> Param<T> param(String name, DataType<T> type) {
        return new Val(null, type, false, name);
    }

    @Support
    @NotNull
    public static <T> Param<T> param(String name, Field<T> type) {
        return param(name, (DataType) type.getDataType());
    }

    @Support
    @NotNull
    public static <T> Param<T> param(String name, T value) {
        return new Val(value, val(value).getDataType(), false, name);
    }

    @Support
    @NotNull
    public static <T> Param<T> value(T value) {
        return val(value);
    }

    @Support
    @NotNull
    public static Param<Byte> value(byte value) {
        return value(Byte.valueOf(value), SQLDataType.TINYINT);
    }

    @Support
    @NotNull
    public static Param<Byte> value(Byte value) {
        return value(value, SQLDataType.TINYINT);
    }

    @Support
    @NotNull
    public static Param<UByte> value(UByte value) {
        return value(value, SQLDataType.TINYINTUNSIGNED);
    }

    @Support
    @NotNull
    public static Param<Short> value(short value) {
        return value(Short.valueOf(value), SQLDataType.SMALLINT);
    }

    @Support
    @NotNull
    public static Param<Short> value(Short value) {
        return value(value, SQLDataType.SMALLINT);
    }

    @Support
    @NotNull
    public static Param<UShort> value(UShort value) {
        return value(value, SQLDataType.SMALLINTUNSIGNED);
    }

    @Support
    @NotNull
    public static Param<Integer> value(int value) {
        return value(Integer.valueOf(value), SQLDataType.INTEGER);
    }

    @Support
    @NotNull
    public static Param<Integer> value(Integer value) {
        return value(value, SQLDataType.INTEGER);
    }

    @Support
    @NotNull
    public static Param<UInteger> value(UInteger value) {
        return value(value, SQLDataType.INTEGERUNSIGNED);
    }

    @Support
    @NotNull
    public static Param<Long> value(long value) {
        return value(Long.valueOf(value), SQLDataType.BIGINT);
    }

    @Support
    @NotNull
    public static Param<Long> value(Long value) {
        return value(value, SQLDataType.BIGINT);
    }

    @Support
    @NotNull
    public static Param<ULong> value(ULong value) {
        return value(value, SQLDataType.BIGINTUNSIGNED);
    }

    @Support
    @NotNull
    public static Param<Float> value(float value) {
        return value(Float.valueOf(value), SQLDataType.REAL);
    }

    @Support
    @NotNull
    public static Param<Float> value(Float value) {
        return value(value, SQLDataType.REAL);
    }

    @Support
    @NotNull
    public static Param<Double> value(double value) {
        return value(Double.valueOf(value), SQLDataType.DOUBLE);
    }

    @Support
    @NotNull
    public static Param<Double> value(Double value) {
        return value(value, SQLDataType.DOUBLE);
    }

    @Support
    @NotNull
    public static Param<Boolean> value(boolean value) {
        return value(Boolean.valueOf(value), SQLDataType.BOOLEAN);
    }

    @Support
    @NotNull
    public static Param<Boolean> value(Boolean value) {
        return value(value, SQLDataType.BOOLEAN);
    }

    @Support
    @NotNull
    public static Param<BigDecimal> value(BigDecimal value) {
        return value(value, SQLDataType.DECIMAL);
    }

    @Support
    @NotNull
    public static Param<BigInteger> value(BigInteger value) {
        return value(value, SQLDataType.DECIMAL_INTEGER);
    }

    @Support
    @NotNull
    public static Param<byte[]> value(byte[] value) {
        return value(value, SQLDataType.VARBINARY);
    }

    @Support
    @NotNull
    public static Param<String> value(String value) {
        return value(value, SQLDataType.VARCHAR);
    }

    @Support
    @NotNull
    public static Param<Date> value(Date value) {
        return value(value, SQLDataType.DATE);
    }

    @Support
    @NotNull
    public static Param<Time> value(Time value) {
        return value(value, SQLDataType.TIME);
    }

    @Support
    @NotNull
    public static Param<Timestamp> value(Timestamp value) {
        return value(value, SQLDataType.TIMESTAMP);
    }

    @Support
    @NotNull
    public static Param<LocalDate> value(LocalDate value) {
        return value(value, SQLDataType.LOCALDATE);
    }

    @Support
    @NotNull
    public static Param<LocalTime> value(LocalTime value) {
        return value(value, SQLDataType.LOCALTIME);
    }

    @Support
    @NotNull
    public static Param<LocalDateTime> value(LocalDateTime value) {
        return value(value, SQLDataType.LOCALDATETIME);
    }

    @Support
    @NotNull
    public static Param<OffsetTime> value(OffsetTime value) {
        return value(value, SQLDataType.OFFSETTIME);
    }

    @Support
    @NotNull
    public static Param<OffsetDateTime> value(OffsetDateTime value) {
        return value(value, SQLDataType.OFFSETDATETIME);
    }

    @Support
    @NotNull
    public static Param<Instant> value(Instant value) {
        return value(value, SQLDataType.INSTANT);
    }

    @Support
    @NotNull
    public static Param<UUID> value(UUID value) {
        return value(value, SQLDataType.UUID);
    }

    @Support
    @NotNull
    public static Param<JSON> value(JSON value) {
        return value(value, SQLDataType.JSON);
    }

    @Support
    @NotNull
    public static Param<JSONB> value(JSONB value) {
        return value(value, SQLDataType.JSONB);
    }

    @Support
    @NotNull
    public static Param<XML> value(XML value) {
        return value(value, SQLDataType.XML);
    }

    @Support
    @NotNull
    public static <T> Param<T> value(Object value, Class<T> type) {
        return val(value, type);
    }

    @Support
    @NotNull
    public static <T> Param<T> value(Object value, Field<T> field) {
        return val(value, field);
    }

    @Support
    @NotNull
    public static <T> Param<T> value(Object value, DataType<T> type) {
        return val(value, type);
    }

    @Support
    @NotNull
    public static <T> Field<T> inlined(Field<T> field) {
        return CustomField.of(field.getQualifiedName(), field.getDataType(), (Consumer<? super Context<?>>) c -> {
            c.visit(field, ParamType.INLINED);
        });
    }

    @Support
    @NotNull
    public static Condition inlined(Condition condition) {
        return CustomCondition.of(c -> {
            c.visit(condition, ParamType.INLINED);
        });
    }

    @Support
    @NotNull
    public static QueryPart inlined(QueryPart part) {
        return CustomQueryPart.of(c -> {
            c.visit(part, ParamType.INLINED);
        });
    }

    @Support
    @NotNull
    public static <T> Param<T> inline(T value) {
        AbstractParamX<T> val = (AbstractParamX) val(value);
        val.setInline0(true);
        return val;
    }

    @Support
    @NotNull
    public static Param<Byte> inline(byte value) {
        return inline(Byte.valueOf(value), SQLDataType.TINYINT);
    }

    @Support
    @NotNull
    public static Param<Byte> inline(Byte value) {
        return inline(value, SQLDataType.TINYINT);
    }

    @Support
    @NotNull
    public static Param<UByte> inline(UByte value) {
        return inline(value, SQLDataType.TINYINTUNSIGNED);
    }

    @Support
    @NotNull
    public static Param<Short> inline(short value) {
        return inline(Short.valueOf(value), SQLDataType.SMALLINT);
    }

    @Support
    @NotNull
    public static Param<Short> inline(Short value) {
        return inline(value, SQLDataType.SMALLINT);
    }

    @Support
    @NotNull
    public static Param<UShort> inline(UShort value) {
        return inline(value, SQLDataType.SMALLINTUNSIGNED);
    }

    @Support
    @NotNull
    public static Param<Integer> inline(int value) {
        return inline(Integer.valueOf(value), SQLDataType.INTEGER);
    }

    @Support
    @NotNull
    public static Param<Integer> inline(Integer value) {
        return inline(value, SQLDataType.INTEGER);
    }

    @Support
    @NotNull
    public static Param<UInteger> inline(UInteger value) {
        return inline(value, SQLDataType.INTEGERUNSIGNED);
    }

    @Support
    @NotNull
    public static Param<Long> inline(long value) {
        return inline(Long.valueOf(value), SQLDataType.BIGINT);
    }

    @Support
    @NotNull
    public static Param<Long> inline(Long value) {
        return inline(value, SQLDataType.BIGINT);
    }

    @Support
    @NotNull
    public static Param<ULong> inline(ULong value) {
        return inline(value, SQLDataType.BIGINTUNSIGNED);
    }

    @Support
    @NotNull
    public static Param<Float> inline(float value) {
        return inline(Float.valueOf(value), SQLDataType.REAL);
    }

    @Support
    @NotNull
    public static Param<Float> inline(Float value) {
        return inline(value, SQLDataType.REAL);
    }

    @Support
    @NotNull
    public static Param<Double> inline(double value) {
        return inline(Double.valueOf(value), SQLDataType.DOUBLE);
    }

    @Support
    @NotNull
    public static Param<Double> inline(Double value) {
        return inline(value, SQLDataType.DOUBLE);
    }

    @Support
    @NotNull
    public static Param<Boolean> inline(boolean value) {
        return inline(Boolean.valueOf(value), SQLDataType.BOOLEAN);
    }

    @Support
    @NotNull
    public static Param<Boolean> inline(Boolean value) {
        return inline(value, SQLDataType.BOOLEAN);
    }

    @Support
    @NotNull
    public static Param<BigDecimal> inline(BigDecimal value) {
        return inline(value, SQLDataType.DECIMAL);
    }

    @Support
    @NotNull
    public static Param<BigInteger> inline(BigInteger value) {
        return inline(value, SQLDataType.DECIMAL_INTEGER);
    }

    @Support
    @NotNull
    public static Param<byte[]> inline(byte[] value) {
        return inline(value, SQLDataType.VARBINARY);
    }

    @Support
    @NotNull
    public static Param<String> inline(String value) {
        return inline(value, SQLDataType.VARCHAR);
    }

    @Support
    @NotNull
    public static Param<Date> inline(Date value) {
        return inline(value, SQLDataType.DATE);
    }

    @Support
    @NotNull
    public static Param<Time> inline(Time value) {
        return inline(value, SQLDataType.TIME);
    }

    @Support
    @NotNull
    public static Param<Timestamp> inline(Timestamp value) {
        return inline(value, SQLDataType.TIMESTAMP);
    }

    @Support
    @NotNull
    public static Param<LocalDate> inline(LocalDate value) {
        return inline(value, SQLDataType.LOCALDATE);
    }

    @Support
    @NotNull
    public static Param<LocalTime> inline(LocalTime value) {
        return inline(value, SQLDataType.LOCALTIME);
    }

    @Support
    @NotNull
    public static Param<LocalDateTime> inline(LocalDateTime value) {
        return inline(value, SQLDataType.LOCALDATETIME);
    }

    @Support
    @NotNull
    public static Param<OffsetTime> inline(OffsetTime value) {
        return inline(value, SQLDataType.OFFSETTIME);
    }

    @Support
    @NotNull
    public static Param<OffsetDateTime> inline(OffsetDateTime value) {
        return inline(value, SQLDataType.OFFSETDATETIME);
    }

    @Support
    @NotNull
    public static Param<Instant> inline(Instant value) {
        return inline(value, SQLDataType.INSTANT);
    }

    @Support
    @NotNull
    public static Param<UUID> inline(UUID value) {
        return inline(value, SQLDataType.UUID);
    }

    @Support
    @NotNull
    public static Param<JSON> inline(JSON value) {
        return inline(value, SQLDataType.JSON);
    }

    @Support
    @NotNull
    public static Param<JSONB> inline(JSONB value) {
        return inline(value, SQLDataType.JSONB);
    }

    @Support
    @NotNull
    public static Param<XML> inline(XML value) {
        return inline(value, SQLDataType.XML);
    }

    @Support
    @NotNull
    public static Param<String> inline(char character) {
        return inline(character);
    }

    @Support
    @NotNull
    public static Param<String> inline(Character character) {
        return inline(character == null ? null : character);
    }

    @Support
    @NotNull
    public static Param<String> inline(CharSequence character) {
        return inline(character == null ? null : String.valueOf(character));
    }

    @Support
    @NotNull
    public static <T> Param<T> inline(Object value, Class<T> type) {
        AbstractParamX<T> val = (AbstractParamX) val(value, type);
        val.setInline0(true);
        return val;
    }

    @Support
    @NotNull
    public static <T> Param<T> inline(Object value, Field<T> field) {
        AbstractParamX<T> val = (AbstractParamX) val(value, field);
        val.setInline0(true);
        return val;
    }

    @Support
    @NotNull
    public static <T> Param<T> inline(Object value, DataType<T> type) {
        AbstractParamX<T> val = (AbstractParamX) val(value, type);
        val.setInline0(true);
        return val;
    }

    @Support
    @NotNull
    public static <T> Param<T> val(T value) {
        return val0(value, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <T> Param<T> val0(T value, boolean inferredDataType) {
        Class type = value == null ? Object.class : value.getClass();
        return val0(value, getDataType0(type), inferredDataType);
    }

    @Support
    @NotNull
    public static Param<Byte> val(byte value) {
        return val(Byte.valueOf(value), SQLDataType.TINYINT);
    }

    @Support
    @NotNull
    public static Param<Byte> val(Byte value) {
        return val(value, SQLDataType.TINYINT);
    }

    @Support
    @NotNull
    public static Param<UByte> val(UByte value) {
        return val(value, SQLDataType.TINYINTUNSIGNED);
    }

    @Support
    @NotNull
    public static Param<Short> val(short value) {
        return val(Short.valueOf(value), SQLDataType.SMALLINT);
    }

    @Support
    @NotNull
    public static Param<Short> val(Short value) {
        return val(value, SQLDataType.SMALLINT);
    }

    @Support
    @NotNull
    public static Param<UShort> val(UShort value) {
        return val(value, SQLDataType.SMALLINTUNSIGNED);
    }

    @Support
    @NotNull
    public static Param<Integer> val(int value) {
        return val(Integer.valueOf(value), SQLDataType.INTEGER);
    }

    @Support
    @NotNull
    public static Param<Integer> val(Integer value) {
        return val(value, SQLDataType.INTEGER);
    }

    @Support
    @NotNull
    public static Param<UInteger> val(UInteger value) {
        return val(value, SQLDataType.INTEGERUNSIGNED);
    }

    @Support
    @NotNull
    public static Param<Long> val(long value) {
        return val(Long.valueOf(value), SQLDataType.BIGINT);
    }

    @Support
    @NotNull
    public static Param<Long> val(Long value) {
        return val(value, SQLDataType.BIGINT);
    }

    @Support
    @NotNull
    public static Param<ULong> val(ULong value) {
        return val(value, SQLDataType.BIGINTUNSIGNED);
    }

    @Support
    @NotNull
    public static Param<Float> val(float value) {
        return val(Float.valueOf(value), SQLDataType.REAL);
    }

    @Support
    @NotNull
    public static Param<Float> val(Float value) {
        return val(value, SQLDataType.REAL);
    }

    @Support
    @NotNull
    public static Param<Double> val(double value) {
        return val(Double.valueOf(value), SQLDataType.DOUBLE);
    }

    @Support
    @NotNull
    public static Param<Double> val(Double value) {
        return val(value, SQLDataType.DOUBLE);
    }

    @Support
    @NotNull
    public static Param<Boolean> val(boolean value) {
        return val(Boolean.valueOf(value), SQLDataType.BOOLEAN);
    }

    @Support
    @NotNull
    public static Param<Boolean> val(Boolean value) {
        return val(value, SQLDataType.BOOLEAN);
    }

    @Support
    @NotNull
    public static Param<BigDecimal> val(BigDecimal value) {
        return val(value, SQLDataType.DECIMAL);
    }

    @Support
    @NotNull
    public static Param<BigInteger> val(BigInteger value) {
        return val(value, SQLDataType.DECIMAL_INTEGER);
    }

    @Support
    @NotNull
    public static Param<byte[]> val(byte[] value) {
        return val(value, SQLDataType.VARBINARY);
    }

    @Support
    @NotNull
    public static Param<String> val(String value) {
        return val(value, SQLDataType.VARCHAR);
    }

    @Support
    @NotNull
    public static Param<Date> val(Date value) {
        return val(value, SQLDataType.DATE);
    }

    @Support
    @NotNull
    public static Param<Time> val(Time value) {
        return val(value, SQLDataType.TIME);
    }

    @Support
    @NotNull
    public static Param<Timestamp> val(Timestamp value) {
        return val(value, SQLDataType.TIMESTAMP);
    }

    @Support
    @NotNull
    public static Param<LocalDate> val(LocalDate value) {
        return val(value, SQLDataType.LOCALDATE);
    }

    @Support
    @NotNull
    public static Param<LocalTime> val(LocalTime value) {
        return val(value, SQLDataType.LOCALTIME);
    }

    @Support
    @NotNull
    public static Param<LocalDateTime> val(LocalDateTime value) {
        return val(value, SQLDataType.LOCALDATETIME);
    }

    @Support
    @NotNull
    public static Param<OffsetTime> val(OffsetTime value) {
        return val(value, SQLDataType.OFFSETTIME);
    }

    @Support
    @NotNull
    public static Param<OffsetDateTime> val(OffsetDateTime value) {
        return val(value, SQLDataType.OFFSETDATETIME);
    }

    @Support
    @NotNull
    public static Param<Instant> val(Instant value) {
        return val(value, SQLDataType.INSTANT);
    }

    @Support
    @NotNull
    public static Param<UUID> val(UUID value) {
        return val(value, SQLDataType.UUID);
    }

    @Support
    @NotNull
    public static Param<JSON> val(JSON value) {
        return val(value, SQLDataType.JSON);
    }

    @Support
    @NotNull
    public static Param<JSONB> val(JSONB value) {
        return val(value, SQLDataType.JSONB);
    }

    @Support
    @NotNull
    public static Param<XML> val(XML value) {
        return val(value, SQLDataType.XML);
    }

    @Support
    @NotNull
    public static <T> Param<T> val(Object value, Class<T> type) {
        return val(value, getDataType(type));
    }

    @Support
    @NotNull
    public static <T> Param<T> val(Object value, Field<T> field) {
        return val(value, Tools.nullSafeDataType(field));
    }

    @Support
    @NotNull
    public static <T> Param<T> val(Object value, DataType<T> type) {
        return val0(value, type, false);
    }

    private static <T> Param<T> val0(Object value, DataType<T> type, boolean inferredDataType) {
        if (value instanceof QualifiedRecord) {
            QualifiedRecord<?> r = (QualifiedRecord) value;
            return new QualifiedRecordConstant(r, r.getQualifier());
        }
        if (value == null && QualifiedRecord.class.isAssignableFrom(type.getType())) {
            return new QualifiedRecordConstant(null, Tools.getRecordQualifier((DataType<?>) type));
        }
        if (value instanceof Val) {
            Val<?> p = (Val) value;
            return p.convertTo(type);
        }
        T converted = type.convert(value);
        return new Val(converted, mostSpecific(converted, type), inferredDataType);
    }

    private static <T> DataType<T> mostSpecific(T value, DataType<T> dataType) {
        Class<?> cls;
        Class<?> type;
        if (value != null && !(dataType instanceof ConvertedDataType) && (cls = value.getClass()) != (type = dataType.getType()) && type.isAssignableFrom(cls)) {
            return DefaultDataType.getDataType(null, cls, dataType);
        }
        return dataType;
    }

    @NotNull
    public static <T1> RecordType<Record> recordType(Field<?>[] fields) {
        return new FieldsImpl(fields);
    }

    @NotNull
    public static <T1> RecordType<Record> recordType(Collection<? extends Field<?>> fields) {
        return new FieldsImpl(fields);
    }

    @NotNull
    public static <T1> RecordType<Record1<T1>> recordType(Field<T1> field1) {
        return new FieldsImpl((SelectField<?>[]) new SelectField[]{field1});
    }

    @NotNull
    public static <T1, T2> RecordType<Record2<T1, T2>> recordType(Field<T1> field1, Field<T2> field2) {
        return new FieldsImpl((SelectField<?>[]) new SelectField[]{field1, field2});
    }

    @NotNull
    public static <T1, T2, T3> RecordType<Record3<T1, T2, T3>> recordType(Field<T1> field1, Field<T2> field2, Field<T3> field3) {
        return new FieldsImpl((SelectField<?>[]) new SelectField[]{field1, field2, field3});
    }

    @NotNull
    public static <T1, T2, T3, T4> RecordType<Record4<T1, T2, T3, T4>> recordType(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4) {
        return new FieldsImpl((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4});
    }

    @NotNull
    public static <T1, T2, T3, T4, T5> RecordType<Record5<T1, T2, T3, T4, T5>> recordType(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5) {
        return new FieldsImpl((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5});
    }

    @NotNull
    public static <T1, T2, T3, T4, T5, T6> RecordType<Record6<T1, T2, T3, T4, T5, T6>> recordType(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6) {
        return new FieldsImpl((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6});
    }

    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7> RecordType<Record7<T1, T2, T3, T4, T5, T6, T7>> recordType(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7) {
        return new FieldsImpl((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7});
    }

    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8> RecordType<Record8<T1, T2, T3, T4, T5, T6, T7, T8>> recordType(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8) {
        return new FieldsImpl((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8});
    }

    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> RecordType<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> recordType(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9) {
        return new FieldsImpl((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8, field9});
    }

    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> RecordType<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> recordType(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10) {
        return new FieldsImpl((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10});
    }

    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> RecordType<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> recordType(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11) {
        return new FieldsImpl((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11});
    }

    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> RecordType<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> recordType(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12) {
        return new FieldsImpl((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12});
    }

    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> RecordType<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> recordType(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13) {
        return new FieldsImpl((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13});
    }

    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> RecordType<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> recordType(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14) {
        return new FieldsImpl((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14});
    }

    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> RecordType<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> recordType(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15) {
        return new FieldsImpl((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15});
    }

    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> RecordType<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> recordType(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16) {
        return new FieldsImpl((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16});
    }

    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> RecordType<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> recordType(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17) {
        return new FieldsImpl((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17});
    }

    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> RecordType<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> recordType(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18) {
        return new FieldsImpl((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18});
    }

    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> RecordType<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> recordType(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19) {
        return new FieldsImpl((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19});
    }

    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> RecordType<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> recordType(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20) {
        return new FieldsImpl((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20});
    }

    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> RecordType<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> recordType(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21) {
        return new FieldsImpl((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21});
    }

    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> RecordType<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> recordType(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22) {
        return new FieldsImpl((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22});
    }

    @Support
    @NotNull
    public static <T1> Row1<T1> row(T1 t1) {
        return row((SelectField) Tools.field(t1));
    }

    @Support
    @NotNull
    public static <T1, T2> Row2<T1, T2> row(T1 t1, T2 t2) {
        return row((SelectField) Tools.field(t1), (SelectField) Tools.field(t2));
    }

    @Support
    @NotNull
    public static <T1, T2, T3> Row3<T1, T2, T3> row(T1 t1, T2 t2, T3 t3) {
        return row((SelectField) Tools.field(t1), (SelectField) Tools.field(t2), (SelectField) Tools.field(t3));
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4> Row4<T1, T2, T3, T4> row(T1 t1, T2 t2, T3 t3, T4 t4) {
        return row((SelectField) Tools.field(t1), (SelectField) Tools.field(t2), (SelectField) Tools.field(t3), (SelectField) Tools.field(t4));
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5> Row5<T1, T2, T3, T4, T5> row(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        return row((SelectField) Tools.field(t1), (SelectField) Tools.field(t2), (SelectField) Tools.field(t3), (SelectField) Tools.field(t4), (SelectField) Tools.field(t5));
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6> Row6<T1, T2, T3, T4, T5, T6> row(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6) {
        return row((SelectField) Tools.field(t1), (SelectField) Tools.field(t2), (SelectField) Tools.field(t3), (SelectField) Tools.field(t4), (SelectField) Tools.field(t5), (SelectField) Tools.field(t6));
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7> Row7<T1, T2, T3, T4, T5, T6, T7> row(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7) {
        return row((SelectField) Tools.field(t1), (SelectField) Tools.field(t2), (SelectField) Tools.field(t3), (SelectField) Tools.field(t4), (SelectField) Tools.field(t5), (SelectField) Tools.field(t6), (SelectField) Tools.field(t7));
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8> Row8<T1, T2, T3, T4, T5, T6, T7, T8> row(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8) {
        return row((SelectField) Tools.field(t1), (SelectField) Tools.field(t2), (SelectField) Tools.field(t3), (SelectField) Tools.field(t4), (SelectField) Tools.field(t5), (SelectField) Tools.field(t6), (SelectField) Tools.field(t7), (SelectField) Tools.field(t8));
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9) {
        return row((SelectField) Tools.field(t1), (SelectField) Tools.field(t2), (SelectField) Tools.field(t3), (SelectField) Tools.field(t4), (SelectField) Tools.field(t5), (SelectField) Tools.field(t6), (SelectField) Tools.field(t7), (SelectField) Tools.field(t8), (SelectField) Tools.field(t9));
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10) {
        return row((SelectField) Tools.field(t1), (SelectField) Tools.field(t2), (SelectField) Tools.field(t3), (SelectField) Tools.field(t4), (SelectField) Tools.field(t5), (SelectField) Tools.field(t6), (SelectField) Tools.field(t7), (SelectField) Tools.field(t8), (SelectField) Tools.field(t9), (SelectField) Tools.field(t10));
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return row((SelectField) Tools.field(t1), (SelectField) Tools.field(t2), (SelectField) Tools.field(t3), (SelectField) Tools.field(t4), (SelectField) Tools.field(t5), (SelectField) Tools.field(t6), (SelectField) Tools.field(t7), (SelectField) Tools.field(t8), (SelectField) Tools.field(t9), (SelectField) Tools.field(t10), (SelectField) Tools.field(t11));
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12) {
        return row((SelectField) Tools.field(t1), (SelectField) Tools.field(t2), (SelectField) Tools.field(t3), (SelectField) Tools.field(t4), (SelectField) Tools.field(t5), (SelectField) Tools.field(t6), (SelectField) Tools.field(t7), (SelectField) Tools.field(t8), (SelectField) Tools.field(t9), (SelectField) Tools.field(t10), (SelectField) Tools.field(t11), (SelectField) Tools.field(t12));
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> row(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13) {
        return row((SelectField) Tools.field(t1), (SelectField) Tools.field(t2), (SelectField) Tools.field(t3), (SelectField) Tools.field(t4), (SelectField) Tools.field(t5), (SelectField) Tools.field(t6), (SelectField) Tools.field(t7), (SelectField) Tools.field(t8), (SelectField) Tools.field(t9), (SelectField) Tools.field(t10), (SelectField) Tools.field(t11), (SelectField) Tools.field(t12), (SelectField) Tools.field(t13));
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Row14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> row(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14) {
        return row((SelectField) Tools.field(t1), (SelectField) Tools.field(t2), (SelectField) Tools.field(t3), (SelectField) Tools.field(t4), (SelectField) Tools.field(t5), (SelectField) Tools.field(t6), (SelectField) Tools.field(t7), (SelectField) Tools.field(t8), (SelectField) Tools.field(t9), (SelectField) Tools.field(t10), (SelectField) Tools.field(t11), (SelectField) Tools.field(t12), (SelectField) Tools.field(t13), (SelectField) Tools.field(t14));
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Row15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> row(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15) {
        return row((SelectField) Tools.field(t1), (SelectField) Tools.field(t2), (SelectField) Tools.field(t3), (SelectField) Tools.field(t4), (SelectField) Tools.field(t5), (SelectField) Tools.field(t6), (SelectField) Tools.field(t7), (SelectField) Tools.field(t8), (SelectField) Tools.field(t9), (SelectField) Tools.field(t10), (SelectField) Tools.field(t11), (SelectField) Tools.field(t12), (SelectField) Tools.field(t13), (SelectField) Tools.field(t14), (SelectField) Tools.field(t15));
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Row16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> row(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16) {
        return row((SelectField) Tools.field(t1), (SelectField) Tools.field(t2), (SelectField) Tools.field(t3), (SelectField) Tools.field(t4), (SelectField) Tools.field(t5), (SelectField) Tools.field(t6), (SelectField) Tools.field(t7), (SelectField) Tools.field(t8), (SelectField) Tools.field(t9), (SelectField) Tools.field(t10), (SelectField) Tools.field(t11), (SelectField) Tools.field(t12), (SelectField) Tools.field(t13), (SelectField) Tools.field(t14), (SelectField) Tools.field(t15), (SelectField) Tools.field(t16));
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> Row17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> row(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16, T17 t17) {
        return row((SelectField) Tools.field(t1), (SelectField) Tools.field(t2), (SelectField) Tools.field(t3), (SelectField) Tools.field(t4), (SelectField) Tools.field(t5), (SelectField) Tools.field(t6), (SelectField) Tools.field(t7), (SelectField) Tools.field(t8), (SelectField) Tools.field(t9), (SelectField) Tools.field(t10), (SelectField) Tools.field(t11), (SelectField) Tools.field(t12), (SelectField) Tools.field(t13), (SelectField) Tools.field(t14), (SelectField) Tools.field(t15), (SelectField) Tools.field(t16), (SelectField) Tools.field(t17));
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> Row18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> row(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16, T17 t17, T18 t18) {
        return row((SelectField) Tools.field(t1), (SelectField) Tools.field(t2), (SelectField) Tools.field(t3), (SelectField) Tools.field(t4), (SelectField) Tools.field(t5), (SelectField) Tools.field(t6), (SelectField) Tools.field(t7), (SelectField) Tools.field(t8), (SelectField) Tools.field(t9), (SelectField) Tools.field(t10), (SelectField) Tools.field(t11), (SelectField) Tools.field(t12), (SelectField) Tools.field(t13), (SelectField) Tools.field(t14), (SelectField) Tools.field(t15), (SelectField) Tools.field(t16), (SelectField) Tools.field(t17), (SelectField) Tools.field(t18));
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> Row19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> row(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16, T17 t17, T18 t18, T19 t19) {
        return row((SelectField) Tools.field(t1), (SelectField) Tools.field(t2), (SelectField) Tools.field(t3), (SelectField) Tools.field(t4), (SelectField) Tools.field(t5), (SelectField) Tools.field(t6), (SelectField) Tools.field(t7), (SelectField) Tools.field(t8), (SelectField) Tools.field(t9), (SelectField) Tools.field(t10), (SelectField) Tools.field(t11), (SelectField) Tools.field(t12), (SelectField) Tools.field(t13), (SelectField) Tools.field(t14), (SelectField) Tools.field(t15), (SelectField) Tools.field(t16), (SelectField) Tools.field(t17), (SelectField) Tools.field(t18), (SelectField) Tools.field(t19));
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> Row20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> row(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16, T17 t17, T18 t18, T19 t19, T20 t20) {
        return row((SelectField) Tools.field(t1), (SelectField) Tools.field(t2), (SelectField) Tools.field(t3), (SelectField) Tools.field(t4), (SelectField) Tools.field(t5), (SelectField) Tools.field(t6), (SelectField) Tools.field(t7), (SelectField) Tools.field(t8), (SelectField) Tools.field(t9), (SelectField) Tools.field(t10), (SelectField) Tools.field(t11), (SelectField) Tools.field(t12), (SelectField) Tools.field(t13), (SelectField) Tools.field(t14), (SelectField) Tools.field(t15), (SelectField) Tools.field(t16), (SelectField) Tools.field(t17), (SelectField) Tools.field(t18), (SelectField) Tools.field(t19), (SelectField) Tools.field(t20));
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> Row21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> row(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16, T17 t17, T18 t18, T19 t19, T20 t20, T21 t21) {
        return row((SelectField) Tools.field(t1), (SelectField) Tools.field(t2), (SelectField) Tools.field(t3), (SelectField) Tools.field(t4), (SelectField) Tools.field(t5), (SelectField) Tools.field(t6), (SelectField) Tools.field(t7), (SelectField) Tools.field(t8), (SelectField) Tools.field(t9), (SelectField) Tools.field(t10), (SelectField) Tools.field(t11), (SelectField) Tools.field(t12), (SelectField) Tools.field(t13), (SelectField) Tools.field(t14), (SelectField) Tools.field(t15), (SelectField) Tools.field(t16), (SelectField) Tools.field(t17), (SelectField) Tools.field(t18), (SelectField) Tools.field(t19), (SelectField) Tools.field(t20), (SelectField) Tools.field(t21));
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> Row22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> row(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16, T17 t17, T18 t18, T19 t19, T20 t20, T21 t21, T22 t22) {
        return row((SelectField) Tools.field(t1), (SelectField) Tools.field(t2), (SelectField) Tools.field(t3), (SelectField) Tools.field(t4), (SelectField) Tools.field(t5), (SelectField) Tools.field(t6), (SelectField) Tools.field(t7), (SelectField) Tools.field(t8), (SelectField) Tools.field(t9), (SelectField) Tools.field(t10), (SelectField) Tools.field(t11), (SelectField) Tools.field(t12), (SelectField) Tools.field(t13), (SelectField) Tools.field(t14), (SelectField) Tools.field(t15), (SelectField) Tools.field(t16), (SelectField) Tools.field(t17), (SelectField) Tools.field(t18), (SelectField) Tools.field(t19), (SelectField) Tools.field(t20), (SelectField) Tools.field(t21), (SelectField) Tools.field(t22));
    }

    @Support
    @NotNull
    public static RowN row(Object... values) {
        return row((SelectField<?>[]) Tools.fieldsArray(values));
    }

    @Support
    @NotNull
    public static <T1> Row1<T1> row(SelectField<T1> field1) {
        return new RowImpl1(field1);
    }

    @Support
    @NotNull
    public static <T1, T2> Row2<T1, T2> row(SelectField<T1> field1, SelectField<T2> field2) {
        return new RowImpl2(field1, field2);
    }

    @Support
    @NotNull
    public static <T1, T2, T3> Row3<T1, T2, T3> row(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3) {
        return new RowImpl3(field1, field2, field3);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4> Row4<T1, T2, T3, T4> row(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4) {
        return new RowImpl4(field1, field2, field3, field4);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5> Row5<T1, T2, T3, T4, T5> row(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5) {
        return new RowImpl5(field1, field2, field3, field4, field5);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6> Row6<T1, T2, T3, T4, T5, T6> row(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6) {
        return new RowImpl6(field1, field2, field3, field4, field5, field6);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7> Row7<T1, T2, T3, T4, T5, T6, T7> row(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7) {
        return new RowImpl7(field1, field2, field3, field4, field5, field6, field7);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8> Row8<T1, T2, T3, T4, T5, T6, T7, T8> row(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8) {
        return new RowImpl8(field1, field2, field3, field4, field5, field6, field7, field8);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9) {
        return new RowImpl9(field1, field2, field3, field4, field5, field6, field7, field8, field9);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10) {
        return new RowImpl10(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11) {
        return new RowImpl11(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12) {
        return new RowImpl12(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> row(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13) {
        return new RowImpl13(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Row14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> row(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14) {
        return new RowImpl14(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Row15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> row(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15) {
        return new RowImpl15(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Row16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> row(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16) {
        return new RowImpl16(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> Row17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> row(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17) {
        return new RowImpl17(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> Row18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> row(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18) {
        return new RowImpl18(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> Row19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> row(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18, SelectField<T19> field19) {
        return new RowImpl19(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> Row20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> row(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18, SelectField<T19> field19, SelectField<T20> field20) {
        return new RowImpl20(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> Row21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> row(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18, SelectField<T19> field19, SelectField<T20> field20, SelectField<T21> field21) {
        return new RowImpl21(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21);
    }

    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> Row22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> row(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18, SelectField<T19> field19, SelectField<T20> field20, SelectField<T21> field21, SelectField<T22> field22) {
        return new RowImpl22(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22);
    }

    @Support
    @NotNull
    public static RowN row(SelectField<?>... values) {
        return new RowImplN(values);
    }

    @Support
    @NotNull
    public static RowN row(Collection<?> values) {
        return row(values.toArray());
    }

    @Support
    @NotNull
    public static Table<Record> values(RowN... rows) {
        return values0(rows);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Support
    @NotNull
    public static Table<Record> values0(Row... rows) {
        return new Values(Values.assertNotEmpty(rows));
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T1> Table<Record1<T1>> values(Row1<T1>... rows) {
        return new Values(rows);
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T1, T2> Table<Record2<T1, T2>> values(Row2<T1, T2>... rows) {
        return new Values(rows);
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T1, T2, T3> Table<Record3<T1, T2, T3>> values(Row3<T1, T2, T3>... rows) {
        return new Values(rows);
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T1, T2, T3, T4> Table<Record4<T1, T2, T3, T4>> values(Row4<T1, T2, T3, T4>... rows) {
        return new Values(rows);
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5> Table<Record5<T1, T2, T3, T4, T5>> values(Row5<T1, T2, T3, T4, T5>... rows) {
        return new Values(rows);
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6> Table<Record6<T1, T2, T3, T4, T5, T6>> values(Row6<T1, T2, T3, T4, T5, T6>... rows) {
        return new Values(rows);
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7> Table<Record7<T1, T2, T3, T4, T5, T6, T7>> values(Row7<T1, T2, T3, T4, T5, T6, T7>... rows) {
        return new Values(rows);
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8> Table<Record8<T1, T2, T3, T4, T5, T6, T7, T8>> values(Row8<T1, T2, T3, T4, T5, T6, T7, T8>... rows) {
        return new Values(rows);
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Table<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> values(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9>... rows) {
        return new Values(rows);
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Table<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> values(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>... rows) {
        return new Values(rows);
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Table<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> values(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>... rows) {
        return new Values(rows);
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Table<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> values(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>... rows) {
        return new Values(rows);
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Table<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> values(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>... rows) {
        return new Values(rows);
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Table<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> values(Row14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>... rows) {
        return new Values(rows);
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Table<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> values(Row15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>... rows) {
        return new Values(rows);
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Table<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> values(Row16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>... rows) {
        return new Values(rows);
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> Table<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> values(Row17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>... rows) {
        return new Values(rows);
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> Table<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> values(Row18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>... rows) {
        return new Values(rows);
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> Table<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> values(Row19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>... rows) {
        return new Values(rows);
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> Table<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> values(Row20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>... rows) {
        return new Values(rows);
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> Table<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> values(Row21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>... rows) {
        return new Values(rows);
    }

    @SafeVarargs
    @Support
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> Table<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> values(Row22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>... rows) {
        return new Values(rows);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    public static <T> Field<T> nullSafe(Field<T> field) {
        return Tools.nullSafe(field);
    }

    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    protected static <T> Field<T> nullSafe(Field<T> field, DataType<?> type) {
        return Tools.nullSafe(field, type);
    }

    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    protected static Field<?>[] nullSafe(Field<?>... fields) {
        return Tools.nullSafe(fields);
    }

    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    protected static Field<?>[] nullSafe(Field<?>[] fields, DataType<?> type) {
        return Tools.nullSafe(fields, type);
    }

    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    protected static List<Field<?>> nullSafeList(Field<?>... fields) {
        return Tools.nullSafeList(fields);
    }

    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    protected static List<Field<?>> nullSafeList(Field<?>[] fields, DataType<?> type) {
        return Tools.nullSafeList(fields, type);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    public static <T> DataType<T> nullSafeDataType(Field<T> field) {
        return Tools.nullSafeDataType(field);
    }

    @Support
    @NotNull
    public static Asterisk asterisk() {
        return AsteriskImpl.INSTANCE.get();
    }

    @Support
    @NotNull
    public static Param<Integer> zero() {
        return inline(0);
    }

    @Support
    @NotNull
    public static Param<Integer> one() {
        return inline(1);
    }

    @Support
    @NotNull
    public static Param<Integer> two() {
        return inline(2);
    }

    @Support
    @Deprecated(forRemoval = true, since = "3.11")
    @NotNull
    public static <T> DataType<T> getDataType(Class<T> type) {
        return DefaultDataType.getDataType(SQLDialect.DEFAULT, type);
    }

    static <T> DataType<T> getDataType0(Class<T> type) {
        DataType t = DefaultDataType.getDataType(SQLDialect.DEFAULT, type, SQLDataType.OTHER);
        if (t instanceof LegacyConvertedDataType) {
            LegacyConvertedDataType l = (LegacyConvertedDataType) t;
            return new DataTypeProxy(l);
        }
        if (t != SQLDataType.OTHER) {
            return t;
        }
        return DefaultDataType.getDataType(SQLDialect.DEFAULT, type, new DataTypeProxy((AbstractDataType) SQLDataType.OTHER));
    }

    private static final DSLContext dsl() {
        return using(new DefaultConfiguration());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public DSL() {
        throw new UnsupportedOperationException();
    }
}
