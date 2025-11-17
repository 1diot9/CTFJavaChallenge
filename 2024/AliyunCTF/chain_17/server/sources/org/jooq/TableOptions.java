package org.jooq;

import java.io.Serializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/TableOptions.class */
public final class TableOptions implements Serializable {
    private static final TableOptions C_EXPRESSION = new TableOptions(TableType.EXPRESSION);
    private static final TableOptions C_FUNCTION = new TableOptions(TableType.FUNCTION);
    private static final TableOptions C_MATERIALIZED_VIEW = materializedView((String) null);
    private static final TableOptions C_TABLE = new TableOptions(TableType.TABLE);
    private static final TableOptions C_TEMPORARY = new TableOptions(TableType.TEMPORARY);
    private static final TableOptions C_VIEW = view((String) null);
    private final TableType type;
    private final OnCommit onCommit;
    private final Select<?> select;
    private final String source;

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/TableOptions$OnCommit.class */
    public enum OnCommit {
        DELETE_ROWS,
        PRESERVE_ROWS,
        DROP
    }

    private TableOptions(TableType type) {
        this.type = type;
        this.onCommit = null;
        this.select = null;
        this.source = null;
    }

    private TableOptions(OnCommit onCommit) {
        this.type = TableType.TEMPORARY;
        this.onCommit = onCommit;
        this.select = null;
        this.source = null;
    }

    private TableOptions(TableType type, Select<?> select) {
        this.type = type;
        this.onCommit = null;
        this.select = select;
        this.source = select == null ? null : select.toString();
    }

    private TableOptions(TableType type, String source) {
        this.type = type;
        this.onCommit = null;
        this.select = null;
        this.source = source;
    }

    @NotNull
    public static final TableOptions of(TableType tableType) {
        switch (tableType) {
            case TABLE:
            case UNKNOWN:
            default:
                return table();
            case TEMPORARY:
                return temporaryTable();
            case VIEW:
                return view();
            case MATERIALIZED_VIEW:
                return materializedView();
            case FUNCTION:
                return function();
            case EXPRESSION:
                return expression();
        }
    }

    @NotNull
    public static final TableOptions table() {
        return C_TABLE;
    }

    @NotNull
    public static final TableOptions temporaryTable() {
        return C_TEMPORARY;
    }

    @NotNull
    public static final TableOptions temporaryTable(OnCommit onCommit) {
        return new TableOptions(onCommit);
    }

    @NotNull
    public static final TableOptions temporaryTable(QOM.TableCommitAction onCommit) {
        if (onCommit == null) {
            return new TableOptions((OnCommit) null);
        }
        switch (onCommit) {
            case DELETE_ROWS:
                return temporaryTable(OnCommit.DELETE_ROWS);
            case PRESERVE_ROWS:
                return temporaryTable(OnCommit.PRESERVE_ROWS);
            case DROP:
                return temporaryTable(OnCommit.DROP);
            default:
                throw new IllegalArgumentException("TableCommitAction not supported: " + String.valueOf(onCommit));
        }
    }

    @NotNull
    public static final TableOptions view() {
        return C_VIEW;
    }

    @NotNull
    public static final TableOptions view(Select<?> select) {
        return new TableOptions(TableType.VIEW, select);
    }

    @NotNull
    public static final TableOptions view(String source) {
        return new TableOptions(TableType.VIEW, source);
    }

    @NotNull
    public static final TableOptions materializedView() {
        return C_MATERIALIZED_VIEW;
    }

    @NotNull
    public static final TableOptions materializedView(Select<?> select) {
        return new TableOptions(TableType.MATERIALIZED_VIEW, select);
    }

    @NotNull
    public static final TableOptions materializedView(String source) {
        return new TableOptions(TableType.MATERIALIZED_VIEW, source);
    }

    @NotNull
    public static final TableOptions expression() {
        return C_EXPRESSION;
    }

    @NotNull
    public static final TableOptions function() {
        return C_FUNCTION;
    }

    @NotNull
    public static final TableOptions function(String source) {
        return new TableOptions(TableType.FUNCTION, source);
    }

    @NotNull
    public final TableType type() {
        return this.type;
    }

    @Nullable
    public final OnCommit onCommit() {
        return this.onCommit;
    }

    @Nullable
    public final Select<?> select() {
        return this.select;
    }

    @Nullable
    public final String source() {
        return this.source;
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/TableOptions$TableType.class */
    public enum TableType {
        TABLE,
        TEMPORARY,
        VIEW,
        MATERIALIZED_VIEW,
        FUNCTION,
        EXPRESSION,
        UNKNOWN;

        public final boolean isView() {
            return this == VIEW || this == MATERIALIZED_VIEW;
        }

        public final boolean isFunction() {
            return this == FUNCTION;
        }

        public final boolean isTable() {
            return this == TABLE || this == TEMPORARY;
        }
    }

    public int hashCode() {
        int result = (31 * 1) + (this.onCommit == null ? 0 : this.onCommit.hashCode());
        return (31 * ((31 * ((31 * result) + (this.select == null ? 0 : this.select.hashCode()))) + (this.source == null ? 0 : this.source.hashCode()))) + (this.type == null ? 0 : this.type.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TableOptions other = (TableOptions) obj;
        if (this.onCommit != other.onCommit) {
            return false;
        }
        if (this.select == null) {
            if (other.select != null) {
                return false;
            }
        } else if (!this.select.equals(other.select)) {
            return false;
        }
        if (this.source == null) {
            if (other.source != null) {
                return false;
            }
        } else if (!this.source.equals(other.source)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        return true;
    }

    public String toString() {
        return "TableOptions[" + String.valueOf(this.type) + "]";
    }
}
