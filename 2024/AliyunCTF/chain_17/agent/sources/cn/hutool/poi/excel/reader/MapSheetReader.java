package cn.hutool.poi.excel.reader;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Sheet;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/reader/MapSheetReader.class */
public class MapSheetReader extends AbstractSheetReader<List<Map<String, Object>>> {
    private final int headerRowIndex;

    public MapSheetReader(int headerRowIndex, int startRowIndex, int endRowIndex) {
        super(startRowIndex, endRowIndex);
        this.headerRowIndex = headerRowIndex;
    }

    @Override // cn.hutool.poi.excel.reader.SheetReader
    public List<Map<String, Object>> read(Sheet sheet) {
        int firstRowNum = sheet.getFirstRowNum();
        int lastRowNum = sheet.getLastRowNum();
        if (lastRowNum < 0) {
            return ListUtil.empty();
        }
        if (this.headerRowIndex < firstRowNum) {
            throw new IndexOutOfBoundsException(StrUtil.format("Header row index {} is lower than first row index {}.", Integer.valueOf(this.headerRowIndex), Integer.valueOf(firstRowNum)));
        }
        if (this.headerRowIndex > lastRowNum) {
            throw new IndexOutOfBoundsException(StrUtil.format("Header row index {} is greater than last row index {}.", Integer.valueOf(this.headerRowIndex), Integer.valueOf(lastRowNum)));
        }
        if (this.startRowIndex > lastRowNum) {
            return ListUtil.empty();
        }
        int startRowIndex = Math.max(this.startRowIndex, firstRowNum);
        int endRowIndex = Math.min(this.endRowIndex, lastRowNum);
        List<String> headerList = aliasHeader(readRow(sheet, this.headerRowIndex));
        List<Map<String, Object>> result = new ArrayList<>((endRowIndex - startRowIndex) + 1);
        for (int i = startRowIndex; i <= endRowIndex; i++) {
            if (i != this.headerRowIndex) {
                List<Object> rowList = readRow(sheet, i);
                if (CollUtil.isNotEmpty((Collection<?>) rowList) || false == this.ignoreEmptyRow) {
                    result.add(IterUtil.toMap((Iterable) headerList, (Iterable) rowList, true));
                }
            }
        }
        return result;
    }
}
