package cn.hutool.poi.excel.sax.handler;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.func.Func1;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/sax/handler/AbstractRowHandler.class */
public abstract class AbstractRowHandler<T> implements RowHandler {
    protected final int startRowIndex;
    protected final int endRowIndex;
    protected Func1<List<Object>, T> convertFunc;

    public abstract void handleData(int i, long j, T t);

    public AbstractRowHandler(int startRowIndex, int endRowIndex) {
        this.startRowIndex = startRowIndex;
        this.endRowIndex = endRowIndex;
    }

    @Override // cn.hutool.poi.excel.sax.handler.RowHandler
    public void handle(int sheetIndex, long rowIndex, List<Object> rowCells) {
        Assert.notNull(this.convertFunc);
        if (rowIndex < this.startRowIndex || rowIndex > this.endRowIndex) {
            return;
        }
        handleData(sheetIndex, rowIndex, this.convertFunc.callWithRuntimeException(rowCells));
    }
}
