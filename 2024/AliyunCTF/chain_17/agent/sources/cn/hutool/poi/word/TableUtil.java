package cn.hutool.poi.word;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/word/TableUtil.class */
public class TableUtil {
    public static XWPFTable createTable(XWPFDocument doc) {
        return createTable(doc, null);
    }

    public static XWPFTable createTable(XWPFDocument doc, Iterable<?> data) {
        Assert.notNull(doc, "XWPFDocument must be not null !", new Object[0]);
        XWPFTable table = doc.createTable();
        table.removeRow(0);
        return writeTable(table, data);
    }

    public static XWPFTable writeTable(XWPFTable table, Iterable<?> data) {
        Assert.notNull(table, "XWPFTable must be not null !", new Object[0]);
        if (IterUtil.isEmpty(data)) {
            return table;
        }
        boolean isFirst = true;
        for (Object rowData : data) {
            writeRow(table.createRow(), rowData, isFirst);
            if (isFirst) {
                isFirst = false;
            }
        }
        return table;
    }

    public static void writeRow(XWPFTableRow row, Object rowBean, boolean isWriteKeyAsHead) {
        Map rowMap;
        if (rowBean instanceof Iterable) {
            writeRow(row, (Iterable) rowBean);
            return;
        }
        if (rowBean instanceof Map) {
            rowMap = (Map) rowBean;
        } else if (BeanUtil.isBean(rowBean.getClass())) {
            rowMap = BeanUtil.beanToMap(rowBean, (Map<String, Object>) new LinkedHashMap(), false, false);
        } else {
            writeRow(row, CollUtil.newArrayList(rowBean), isWriteKeyAsHead);
            return;
        }
        writeRow(row, (Map<?, ?>) rowMap, isWriteKeyAsHead);
    }

    public static void writeRow(XWPFTableRow row, Map<?, ?> rowMap, boolean isWriteKeyAsHead) {
        if (MapUtil.isEmpty(rowMap)) {
            return;
        }
        if (isWriteKeyAsHead) {
            writeRow(row, rowMap.keySet());
            row = row.getTable().createRow();
        }
        writeRow(row, rowMap.values());
    }

    public static void writeRow(XWPFTableRow row, Iterable<?> rowData) {
        int index = 0;
        for (Object cellData : rowData) {
            XWPFTableCell cell = getOrCreateCell(row, index);
            cell.setText(Convert.toStr(cellData));
            index++;
        }
    }

    public static XWPFTableRow getOrCreateRow(XWPFTable table, int index) {
        XWPFTableRow row = table.getRow(index);
        if (null == row) {
            row = table.createRow();
        }
        return row;
    }

    public static XWPFTableCell getOrCreateCell(XWPFTableRow row, int index) {
        XWPFTableCell cell = row.getCell(index);
        if (null == cell) {
            cell = row.createCell();
        }
        return cell;
    }
}
