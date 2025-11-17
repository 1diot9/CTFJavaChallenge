package cn.hutool.poi.excel.sax.handler;

import java.util.List;
import org.apache.poi.ss.usermodel.CellStyle;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/sax/handler/RowHandler.class */
public interface RowHandler {
    void handle(int i, long j, List<Object> list);

    default void handleCell(int sheetIndex, long rowIndex, int cellIndex, Object value, CellStyle xssfCellStyle) {
    }

    default void doAfterAllAnalysed() {
    }
}
