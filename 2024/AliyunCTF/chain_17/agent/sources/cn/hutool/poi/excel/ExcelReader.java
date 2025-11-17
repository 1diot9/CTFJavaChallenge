package cn.hutool.poi.excel;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.poi.excel.cell.CellEditor;
import cn.hutool.poi.excel.cell.CellHandler;
import cn.hutool.poi.excel.cell.CellUtil;
import cn.hutool.poi.excel.reader.BeanSheetReader;
import cn.hutool.poi.excel.reader.ColumnSheetReader;
import cn.hutool.poi.excel.reader.ListSheetReader;
import cn.hutool.poi.excel.reader.MapSheetReader;
import cn.hutool.poi.excel.reader.SheetReader;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.extractor.ExcelExtractor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/ExcelReader.class */
public class ExcelReader extends ExcelBase<ExcelReader> {
    private boolean ignoreEmptyRow;
    private CellEditor cellEditor;

    public ExcelReader(String excelFilePath, int sheetIndex) {
        this(FileUtil.file(excelFilePath), sheetIndex);
    }

    public ExcelReader(String excelFilePath, String sheetName) {
        this(FileUtil.file(excelFilePath), sheetName);
    }

    public ExcelReader(File bookFile, int sheetIndex) {
        this(WorkbookUtil.createBook(bookFile, true), sheetIndex);
        this.destFile = bookFile;
    }

    public ExcelReader(File bookFile, String sheetName) {
        this(WorkbookUtil.createBook(bookFile, true), sheetName);
        this.destFile = bookFile;
    }

    public ExcelReader(InputStream bookStream, int sheetIndex) {
        this(WorkbookUtil.createBook(bookStream), sheetIndex);
    }

    public ExcelReader(InputStream bookStream, String sheetName) {
        this(WorkbookUtil.createBook(bookStream), sheetName);
    }

    public ExcelReader(Workbook book, int sheetIndex) {
        this(book.getSheetAt(sheetIndex));
    }

    public ExcelReader(Workbook book, String sheetName) {
        this(book.getSheet(sheetName));
    }

    public ExcelReader(Sheet sheet) {
        super(sheet);
        this.ignoreEmptyRow = true;
    }

    public boolean isIgnoreEmptyRow() {
        return this.ignoreEmptyRow;
    }

    public ExcelReader setIgnoreEmptyRow(boolean ignoreEmptyRow) {
        this.ignoreEmptyRow = ignoreEmptyRow;
        return this;
    }

    public ExcelReader setCellEditor(CellEditor cellEditor) {
        this.cellEditor = cellEditor;
        return this;
    }

    public List<List<Object>> read() {
        return read(0);
    }

    public List<List<Object>> read(int startRowIndex) {
        return read(startRowIndex, Integer.MAX_VALUE);
    }

    public List<List<Object>> read(int startRowIndex, int endRowIndex) {
        return read(startRowIndex, endRowIndex, true);
    }

    public List<List<Object>> read(int startRowIndex, int endRowIndex, boolean aliasFirstLine) {
        ListSheetReader reader = new ListSheetReader(startRowIndex, endRowIndex, aliasFirstLine);
        reader.setCellEditor(this.cellEditor);
        reader.setIgnoreEmptyRow(this.ignoreEmptyRow);
        reader.setHeaderAlias(this.headerAlias);
        return (List) read(reader);
    }

    public List<Object> readColumn(int columnIndex, int startRowIndex) {
        return readColumn(columnIndex, startRowIndex, Integer.MAX_VALUE);
    }

    public List<Object> readColumn(int columnIndex, int startRowIndex, int endRowIndex) {
        ColumnSheetReader reader = new ColumnSheetReader(columnIndex, startRowIndex, endRowIndex);
        reader.setCellEditor(this.cellEditor);
        reader.setIgnoreEmptyRow(this.ignoreEmptyRow);
        reader.setHeaderAlias(this.headerAlias);
        return (List) read(reader);
    }

    public void read(CellHandler cellHandler) {
        read(0, Integer.MAX_VALUE, cellHandler);
    }

    public void read(int startRowIndex, int endRowIndex, CellHandler cellHandler) {
        checkNotClosed();
        int startRowIndex2 = Math.max(startRowIndex, this.sheet.getFirstRowNum());
        int endRowIndex2 = Math.min(endRowIndex, this.sheet.getLastRowNum());
        for (int y = startRowIndex2; y <= endRowIndex2; y++) {
            Row row = this.sheet.getRow(y);
            if (null != row) {
                short columnSize = row.getLastCellNum();
                short s = 0;
                while (true) {
                    short x = s;
                    if (x < columnSize) {
                        Cell cell = row.getCell(x);
                        cellHandler.handle(cell, CellUtil.getCellValue(cell));
                        s = (short) (x + 1);
                    }
                }
            }
        }
    }

    public List<Map<String, Object>> readAll() {
        return read(0, 1, Integer.MAX_VALUE);
    }

    public List<Map<String, Object>> read(int headerRowIndex, int startRowIndex, int endRowIndex) {
        MapSheetReader reader = new MapSheetReader(headerRowIndex, startRowIndex, endRowIndex);
        reader.setCellEditor(this.cellEditor);
        reader.setIgnoreEmptyRow(this.ignoreEmptyRow);
        reader.setHeaderAlias(this.headerAlias);
        return (List) read(reader);
    }

    public <T> List<T> readAll(Class<T> beanType) {
        return read(0, 1, Integer.MAX_VALUE, beanType);
    }

    public <T> List<T> read(int headerRowIndex, int startRowIndex, Class<T> beanType) {
        return read(headerRowIndex, startRowIndex, Integer.MAX_VALUE, beanType);
    }

    public <T> List<T> read(int headerRowIndex, int startRowIndex, int endRowIndex, Class<T> beanType) {
        BeanSheetReader<T> reader = new BeanSheetReader<>(headerRowIndex, startRowIndex, endRowIndex, beanType);
        reader.setCellEditor(this.cellEditor);
        reader.setIgnoreEmptyRow(this.ignoreEmptyRow);
        reader.setHeaderAlias(this.headerAlias);
        return (List) read(reader);
    }

    public <T> T read(SheetReader<T> sheetReader) {
        checkNotClosed();
        return (T) ((SheetReader) Assert.notNull(sheetReader)).read(this.sheet);
    }

    public String readAsText(boolean withSheetName) {
        return ExcelExtractorUtil.readAsText(this.workbook, withSheetName);
    }

    public ExcelExtractor getExtractor() {
        return ExcelExtractorUtil.getExtractor(this.workbook);
    }

    public List<Object> readRow(int rowIndex) {
        return readRow(this.sheet.getRow(rowIndex));
    }

    public Object readCellValue(int x, int y) {
        return CellUtil.getCellValue(getCell(x, y), this.cellEditor);
    }

    public ExcelWriter getWriter() {
        return ExcelUtil.getWriter(this.destFile, this.sheet.getSheetName());
    }

    private List<Object> readRow(Row row) {
        return RowUtil.readRow(row, this.cellEditor);
    }

    private void checkNotClosed() {
        Assert.isFalse(this.isClosed, "ExcelReader has been closed!", new Object[0]);
    }
}
