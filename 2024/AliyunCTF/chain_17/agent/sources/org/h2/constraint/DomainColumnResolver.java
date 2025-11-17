package org.h2.constraint;

import org.h2.table.Column;
import org.h2.table.ColumnResolver;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/constraint/DomainColumnResolver.class */
public class DomainColumnResolver implements ColumnResolver {
    private final Column column;
    private Value value;
    private String name;

    public DomainColumnResolver(TypeInfo typeInfo) {
        this.column = new Column("VALUE", typeInfo);
    }

    public void setValue(Value value) {
        this.value = value;
    }

    @Override // org.h2.table.ColumnResolver
    public Value getValue(Column column) {
        return this.value;
    }

    @Override // org.h2.table.ColumnResolver
    public Column[] getColumns() {
        return new Column[]{this.column};
    }

    @Override // org.h2.table.ColumnResolver
    public Column findColumn(String str) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setColumnName(String str) {
        this.name = str;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void resetColumnName() {
        this.name = null;
    }

    public String getColumnName() {
        return this.name;
    }

    public TypeInfo getValueType() {
        return this.column.getType();
    }
}
