package cn.hutool.poi.excel.cell.setters;

import cn.hutool.poi.excel.cell.CellSetter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import org.apache.poi.ss.usermodel.Cell;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/cell/setters/TemporalAccessorCellSetter.class */
public class TemporalAccessorCellSetter implements CellSetter {
    private final TemporalAccessor value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TemporalAccessorCellSetter(TemporalAccessor value) {
        this.value = value;
    }

    @Override // cn.hutool.poi.excel.cell.CellSetter
    public void setValue(Cell cell) {
        if (this.value instanceof Instant) {
            cell.setCellValue(Date.from((Instant) this.value));
        } else if (this.value instanceof LocalDateTime) {
            cell.setCellValue((LocalDateTime) this.value);
        } else if (this.value instanceof LocalDate) {
            cell.setCellValue((LocalDate) this.value);
        }
    }
}
