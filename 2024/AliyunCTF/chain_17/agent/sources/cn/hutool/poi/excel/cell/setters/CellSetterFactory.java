package cn.hutool.poi.excel.cell.setters;

import cn.hutool.poi.excel.cell.CellSetter;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.RichTextString;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/cell/setters/CellSetterFactory.class */
public class CellSetterFactory {
    public static CellSetter createCellSetter(Object value) {
        if (null == value) {
            return NullCellSetter.INSTANCE;
        }
        if (value instanceof CellSetter) {
            return (CellSetter) value;
        }
        if (value instanceof Date) {
            return new DateCellSetter((Date) value);
        }
        if (value instanceof TemporalAccessor) {
            return new TemporalAccessorCellSetter((TemporalAccessor) value);
        }
        if (value instanceof Calendar) {
            return new CalendarCellSetter((Calendar) value);
        }
        if (value instanceof Boolean) {
            return new BooleanCellSetter((Boolean) value);
        }
        if (value instanceof RichTextString) {
            return new RichTextCellSetter((RichTextString) value);
        }
        if (value instanceof Number) {
            return new NumberCellSetter((Number) value);
        }
        if (value instanceof Hyperlink) {
            return new HyperlinkCellSetter((Hyperlink) value);
        }
        return new CharSequenceCellSetter(value.toString());
    }
}
