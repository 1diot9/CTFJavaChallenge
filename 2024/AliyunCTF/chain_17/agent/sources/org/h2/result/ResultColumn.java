package org.h2.result;

import java.io.IOException;
import org.h2.value.Transfer;
import org.h2.value.TypeInfo;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/result/ResultColumn.class */
public class ResultColumn {
    final String alias;
    final String schemaName;
    final String tableName;
    final String columnName;
    final TypeInfo columnType;
    final boolean identity;
    final int nullable;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ResultColumn(Transfer transfer) throws IOException {
        this.alias = transfer.readString();
        this.schemaName = transfer.readString();
        this.tableName = transfer.readString();
        this.columnName = transfer.readString();
        this.columnType = transfer.readTypeInfo();
        if (transfer.getVersion() < 20) {
            transfer.readInt();
        }
        this.identity = transfer.readBoolean();
        this.nullable = transfer.readInt();
    }

    public static void writeColumn(Transfer transfer, ResultInterface resultInterface, int i) throws IOException {
        transfer.writeString(resultInterface.getAlias(i));
        transfer.writeString(resultInterface.getSchemaName(i));
        transfer.writeString(resultInterface.getTableName(i));
        transfer.writeString(resultInterface.getColumnName(i));
        TypeInfo columnType = resultInterface.getColumnType(i);
        transfer.writeTypeInfo(columnType);
        if (transfer.getVersion() < 20) {
            transfer.writeInt(columnType.getDisplaySize());
        }
        transfer.writeBoolean(resultInterface.isIdentity(i));
        transfer.writeInt(resultInterface.getNullable(i));
    }
}
