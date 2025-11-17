package org.h2.result;

import java.util.Arrays;
import org.h2.engine.Session;
import org.h2.util.MathUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/result/ResultWithPaddedStrings.class */
public class ResultWithPaddedStrings implements ResultInterface {
    private final ResultInterface source;

    public static ResultInterface get(ResultInterface resultInterface) {
        int visibleColumnCount = resultInterface.getVisibleColumnCount();
        for (int i = 0; i < visibleColumnCount; i++) {
            if (resultInterface.getColumnType(i).getValueType() == 1) {
                return new ResultWithPaddedStrings(resultInterface);
            }
        }
        return resultInterface;
    }

    private ResultWithPaddedStrings(ResultInterface resultInterface) {
        this.source = resultInterface;
    }

    @Override // org.h2.result.ResultInterface
    public void reset() {
        this.source.reset();
    }

    @Override // org.h2.result.ResultInterface
    public Value[] currentRow() {
        int visibleColumnCount = this.source.getVisibleColumnCount();
        Value[] valueArr = (Value[]) Arrays.copyOf(this.source.currentRow(), visibleColumnCount);
        for (int i = 0; i < visibleColumnCount; i++) {
            TypeInfo columnType = this.source.getColumnType(i);
            if (columnType.getValueType() == 1) {
                long precision = columnType.getPrecision();
                if (precision == 2147483647L) {
                    precision = 1;
                }
                String string = valueArr[i].getString();
                if (string != null && string.length() < precision) {
                    valueArr[i] = ValueVarchar.get(rightPadWithSpaces(string, MathUtils.convertLongToInt(precision)));
                }
            }
        }
        return valueArr;
    }

    private static String rightPadWithSpaces(String str, int i) {
        int length = str.length();
        if (i <= length) {
            return str;
        }
        char[] cArr = new char[i];
        str.getChars(0, length, cArr, 0);
        Arrays.fill(cArr, length, i, ' ');
        return new String(cArr);
    }

    @Override // org.h2.result.ResultInterface
    public boolean next() {
        return this.source.next();
    }

    @Override // org.h2.result.ResultInterface
    public long getRowId() {
        return this.source.getRowId();
    }

    @Override // org.h2.result.ResultInterface
    public boolean isAfterLast() {
        return this.source.isAfterLast();
    }

    @Override // org.h2.result.ResultInterface
    public int getVisibleColumnCount() {
        return this.source.getVisibleColumnCount();
    }

    @Override // org.h2.result.ResultInterface
    public long getRowCount() {
        return this.source.getRowCount();
    }

    @Override // org.h2.result.ResultInterface
    public boolean hasNext() {
        return this.source.hasNext();
    }

    @Override // org.h2.result.ResultInterface
    public boolean needToClose() {
        return this.source.needToClose();
    }

    @Override // org.h2.result.ResultInterface, java.lang.AutoCloseable
    public void close() {
        this.source.close();
    }

    @Override // org.h2.result.ResultInterface
    public String getAlias(int i) {
        return this.source.getAlias(i);
    }

    @Override // org.h2.result.ResultInterface
    public String getSchemaName(int i) {
        return this.source.getSchemaName(i);
    }

    @Override // org.h2.result.ResultInterface
    public String getTableName(int i) {
        return this.source.getTableName(i);
    }

    @Override // org.h2.result.ResultInterface
    public String getColumnName(int i) {
        return this.source.getColumnName(i);
    }

    @Override // org.h2.result.ResultInterface
    public TypeInfo getColumnType(int i) {
        return this.source.getColumnType(i);
    }

    @Override // org.h2.result.ResultInterface
    public boolean isIdentity(int i) {
        return this.source.isIdentity(i);
    }

    @Override // org.h2.result.ResultInterface
    public int getNullable(int i) {
        return this.source.getNullable(i);
    }

    @Override // org.h2.result.ResultInterface
    public void setFetchSize(int i) {
        this.source.setFetchSize(i);
    }

    @Override // org.h2.result.ResultInterface
    public int getFetchSize() {
        return this.source.getFetchSize();
    }

    @Override // org.h2.result.ResultInterface
    public boolean isLazy() {
        return this.source.isLazy();
    }

    @Override // org.h2.result.ResultInterface
    public boolean isClosed() {
        return this.source.isClosed();
    }

    @Override // org.h2.result.ResultInterface
    public ResultInterface createShallowCopy(Session session) {
        ResultInterface createShallowCopy = this.source.createShallowCopy(session);
        if (createShallowCopy != null) {
            return new ResultWithPaddedStrings(createShallowCopy);
        }
        return null;
    }
}
