package org.jooq.impl;

import java.io.Serializable;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Objects;
import org.jooq.Configuration;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.exception.SQLDialectNotSupportedException;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/MetaDataFieldProvider.class */
public final class MetaDataFieldProvider implements Serializable {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) MetaDataFieldProvider.class);
    private final FieldsImpl<Record> fields;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MetaDataFieldProvider(Configuration configuration, ResultSetMetaData meta) {
        this.fields = init(configuration, meta);
    }

    private static FieldsImpl<Record> init(Configuration configuration, ResultSetMetaData meta) {
        Field<?>[] fields;
        Name name;
        int columnCount = 0;
        try {
            columnCount = meta.getColumnCount();
            fields = new Field[columnCount];
        } catch (SQLException e) {
            log.info("Cannot fetch column count for cursor : " + e.getMessage());
            fields = new Field[]{DSL.field("dummy")};
        }
        for (int i = 1; i <= columnCount; i++) {
            try {
                String columnLabel = meta.getColumnLabel(i);
                String columnName = meta.getColumnName(i);
                if (Objects.equals(columnName, columnLabel)) {
                    try {
                        String columnSchema = meta.getSchemaName(i);
                        String columnTable = meta.getTableName(i);
                        if (!StringUtils.isEmpty(columnSchema)) {
                            name = DSL.name(columnSchema, columnTable, columnName);
                        } else if (!StringUtils.isEmpty(columnTable)) {
                            name = DSL.name(columnTable, columnName);
                        } else {
                            name = DSL.name(columnName);
                        }
                    } catch (SQLException e2) {
                        name = DSL.name(columnLabel);
                    }
                } else {
                    name = DSL.name(columnLabel);
                }
                int precision = meta.getPrecision(i);
                int scale = meta.getScale(i);
                DataType<?> dataType = SQLDataType.OTHER;
                String type = meta.getColumnTypeName(i);
                try {
                    dataType = DefaultDataType.getDataType(configuration.family(), type, precision, scale, !Boolean.FALSE.equals(configuration.settings().isForceIntegerTypesOnZeroScaleDecimals()));
                    if (dataType.hasPrecision()) {
                        dataType = dataType.precision(precision);
                    }
                    if (dataType.hasScale()) {
                        dataType = dataType.scale(scale);
                    }
                    if (dataType.hasLength()) {
                        dataType = dataType.length(precision);
                    }
                } catch (SQLDialectNotSupportedException e3) {
                    log.debug("Not supported by dialect", e3.getMessage());
                }
                fields[i - 1] = DSL.field(name, dataType);
            } catch (SQLException e4) {
                throw Tools.translate((String) null, e4);
            }
        }
        return new FieldsImpl<>(fields);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Field<?>[] getFields() {
        return this.fields.fields();
    }

    public String toString() {
        return this.fields.toString();
    }
}
