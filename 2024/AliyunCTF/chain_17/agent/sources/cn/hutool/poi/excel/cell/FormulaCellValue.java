package cn.hutool.poi.excel.cell;

import org.apache.poi.ss.usermodel.Cell;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/cell/FormulaCellValue.class */
public class FormulaCellValue implements CellValue<String>, CellSetter {
    private final String formula;
    private final Object result;

    public FormulaCellValue(String formula) {
        this(formula, null);
    }

    public FormulaCellValue(String formula, Object result) {
        this.formula = formula;
        this.result = result;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.poi.excel.cell.CellValue
    public String getValue() {
        return this.formula;
    }

    @Override // cn.hutool.poi.excel.cell.CellSetter
    public void setValue(Cell cell) {
        cell.setCellFormula(this.formula);
    }

    public Object getResult() {
        return this.result;
    }

    public String toString() {
        return getResult().toString();
    }
}
