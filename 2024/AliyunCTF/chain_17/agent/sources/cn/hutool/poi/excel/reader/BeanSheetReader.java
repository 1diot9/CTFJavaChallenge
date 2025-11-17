package cn.hutool.poi.excel.reader;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.poi.excel.cell.CellEditor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Sheet;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/reader/BeanSheetReader.class */
public class BeanSheetReader<T> implements SheetReader<List<T>> {
    private final Class<T> beanClass;
    private final MapSheetReader mapSheetReader;

    public BeanSheetReader(int headerRowIndex, int startRowIndex, int endRowIndex, Class<T> beanClass) {
        this.mapSheetReader = new MapSheetReader(headerRowIndex, startRowIndex, endRowIndex);
        this.beanClass = beanClass;
    }

    @Override // cn.hutool.poi.excel.reader.SheetReader
    public List<T> read(Sheet sheet) {
        List<T> list = (List<T>) this.mapSheetReader.read(sheet);
        if (Map.class.isAssignableFrom(this.beanClass)) {
            return list;
        }
        ArrayList arrayList = new ArrayList(list.size());
        CopyOptions ignoreError = CopyOptions.create().setIgnoreError(true);
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            arrayList.add(BeanUtil.toBean((Map) it.next(), this.beanClass, ignoreError));
        }
        return arrayList;
    }

    public void setCellEditor(CellEditor cellEditor) {
        this.mapSheetReader.setCellEditor(cellEditor);
    }

    public void setIgnoreEmptyRow(boolean ignoreEmptyRow) {
        this.mapSheetReader.setIgnoreEmptyRow(ignoreEmptyRow);
    }

    public void setHeaderAlias(Map<String, String> headerAlias) {
        this.mapSheetReader.setHeaderAlias(headerAlias);
    }

    public void addHeaderAlias(String header, String alias) {
        this.mapSheetReader.addHeaderAlias(header, alias);
    }
}
