package cn.hutool.poi.excel.reader;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.RowUtil;
import cn.hutool.poi.excel.cell.CellEditor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Sheet;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/reader/AbstractSheetReader.class */
public abstract class AbstractSheetReader<T> implements SheetReader<T> {
    protected final int startRowIndex;
    protected final int endRowIndex;
    protected boolean ignoreEmptyRow = true;
    protected CellEditor cellEditor;
    private Map<String, String> headerAlias;

    public AbstractSheetReader(int startRowIndex, int endRowIndex) {
        this.startRowIndex = startRowIndex;
        this.endRowIndex = endRowIndex;
    }

    public void setCellEditor(CellEditor cellEditor) {
        this.cellEditor = cellEditor;
    }

    public void setIgnoreEmptyRow(boolean ignoreEmptyRow) {
        this.ignoreEmptyRow = ignoreEmptyRow;
    }

    public void setHeaderAlias(Map<String, String> headerAlias) {
        this.headerAlias = headerAlias;
    }

    public void addHeaderAlias(String header, String alias) {
        Map<String, String> headerAlias = this.headerAlias;
        if (null == headerAlias) {
            headerAlias = new LinkedHashMap();
        }
        this.headerAlias = headerAlias;
        this.headerAlias.put(header, alias);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<String> aliasHeader(List<Object> headerList) {
        if (CollUtil.isEmpty((Collection<?>) headerList)) {
            return new ArrayList(0);
        }
        int size = headerList.size();
        ArrayList<String> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(aliasHeader(headerList.get(i), i));
        }
        return result;
    }

    protected String aliasHeader(Object headerObj, int index) {
        if (null == headerObj) {
            return ExcelUtil.indexToColName(index);
        }
        String header = headerObj.toString();
        if (null != this.headerAlias) {
            return (String) ObjectUtil.defaultIfNull(this.headerAlias.get(header), header);
        }
        return header;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<Object> readRow(Sheet sheet, int rowIndex) {
        return RowUtil.readRow(sheet.getRow(rowIndex), this.cellEditor);
    }
}
