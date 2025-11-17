package cn.hutool.poi.excel;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.comparator.IndexedComparator;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.map.SafeConcurrentHashMap;
import cn.hutool.core.map.TableMap;
import cn.hutool.core.map.multi.RowKeyTable;
import cn.hutool.core.map.multi.Table;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.poi.excel.cell.CellLocation;
import cn.hutool.poi.excel.cell.CellUtil;
import cn.hutool.poi.excel.style.Align;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.invoke.SerializedLambda;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.poi.common.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.springframework.cglib.core.Constants;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/ExcelWriter.class */
public class ExcelWriter extends ExcelBase<ExcelWriter> {
    private AtomicInteger currentRow;
    private boolean onlyAlias;
    private Comparator<String> aliasComparator;
    private StyleSet styleSet;
    private Map<String, Integer> headLocationCache;

    @Override // cn.hutool.poi.excel.ExcelBase
    public /* bridge */ /* synthetic */ ExcelWriter setHeaderAlias(Map map) {
        return setHeaderAlias((Map<String, String>) map);
    }

    private static /* synthetic */ Object $deserializeLambda$(SerializedLambda lambda) {
        String implMethodName = lambda.getImplMethodName();
        boolean z = -1;
        switch (implMethodName.hashCode()) {
            case 1818100338:
                if (implMethodName.equals(Constants.CONSTRUCTOR_NAME)) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                if (lambda.getImplMethodKind() == 8 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/builder/Builder") && lambda.getFunctionalInterfaceMethodName().equals(JsonPOJOBuilder.DEFAULT_BUILD_METHOD) && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/core/map/TableMap") && lambda.getImplMethodSignature().equals("()V")) {
                    return TableMap::new;
                }
                break;
        }
        throw new IllegalArgumentException("Invalid lambda deserialization");
    }

    public ExcelWriter() {
        this(false);
    }

    public ExcelWriter(boolean isXlsx) {
        this(WorkbookUtil.createBook(isXlsx), (String) null);
    }

    public ExcelWriter(String destFilePath) {
        this(destFilePath, (String) null);
    }

    public ExcelWriter(boolean isXlsx, String sheetName) {
        this(WorkbookUtil.createBook(isXlsx), sheetName);
    }

    public ExcelWriter(String destFilePath, String sheetName) {
        this(FileUtil.file(destFilePath), sheetName);
    }

    public ExcelWriter(File destFile) {
        this(destFile, (String) null);
    }

    public ExcelWriter(File destFile, String sheetName) {
        this(WorkbookUtil.createBookForWriter(destFile), sheetName);
        this.destFile = destFile;
    }

    public ExcelWriter(Workbook workbook, String sheetName) {
        this(WorkbookUtil.getOrCreateSheet(workbook, sheetName));
    }

    public ExcelWriter(Sheet sheet) {
        super(sheet);
        this.currentRow = new AtomicInteger(0);
        this.styleSet = new StyleSet(this.workbook);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.poi.excel.ExcelBase
    public ExcelWriter setSheet(int sheetIndex) {
        reset();
        return (ExcelWriter) super.setSheet(sheetIndex);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.poi.excel.ExcelBase
    public ExcelWriter setSheet(String sheetName) {
        reset();
        return (ExcelWriter) super.setSheet(sheetName);
    }

    public ExcelWriter reset() {
        resetRow();
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.poi.excel.ExcelBase
    public ExcelWriter renameSheet(String sheetName) {
        return renameSheet(this.workbook.getSheetIndex(this.sheet), sheetName);
    }

    public ExcelWriter renameSheet(int sheet, String sheetName) {
        this.workbook.setSheetName(sheet, sheetName);
        return this;
    }

    public ExcelWriter autoSizeColumnAll() {
        int columnCount = getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            autoSizeColumn(i);
        }
        return this;
    }

    public ExcelWriter autoSizeColumn(int columnIndex) {
        this.sheet.autoSizeColumn(columnIndex);
        return this;
    }

    public ExcelWriter autoSizeColumn(int columnIndex, boolean useMergedCells) {
        this.sheet.autoSizeColumn(columnIndex, useMergedCells);
        return this;
    }

    public ExcelWriter disableDefaultStyle() {
        return setStyleSet(null);
    }

    public ExcelWriter setStyleSet(StyleSet styleSet) {
        this.styleSet = styleSet;
        return this;
    }

    public StyleSet getStyleSet() {
        return this.styleSet;
    }

    public CellStyle getHeadCellStyle() {
        return this.styleSet.headCellStyle;
    }

    public CellStyle getCellStyle() {
        if (null == this.styleSet) {
            return null;
        }
        return this.styleSet.cellStyle;
    }

    public int getCurrentRow() {
        return this.currentRow.get();
    }

    public String getDisposition(String fileName, Charset charset) {
        if (null == charset) {
            charset = CharsetUtil.CHARSET_UTF_8;
        }
        if (StrUtil.isBlank(fileName)) {
            fileName = IdUtil.fastSimpleUUID();
        }
        return StrUtil.format("attachment; filename=\"{}\"", StrUtil.addSuffixIfNot(URLUtil.encodeAll(fileName, charset), isXlsx() ? ".xlsx" : ".xls"));
    }

    public String getContentType() {
        return isXlsx() ? ExcelUtil.XLSX_CONTENT_TYPE : ExcelUtil.XLS_CONTENT_TYPE;
    }

    public ExcelWriter setCurrentRow(int rowIndex) {
        this.currentRow.set(rowIndex);
        return this;
    }

    public ExcelWriter setCurrentRowToEnd() {
        return setCurrentRow(getRowCount());
    }

    public ExcelWriter passCurrentRow() {
        this.currentRow.incrementAndGet();
        return this;
    }

    public ExcelWriter passRows(int rows) {
        this.currentRow.addAndGet(rows);
        return this;
    }

    public ExcelWriter resetRow() {
        this.currentRow.set(0);
        return this;
    }

    public ExcelWriter setDestFile(File destFile) {
        this.destFile = destFile;
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.poi.excel.ExcelBase
    public ExcelWriter setHeaderAlias(Map<String, String> headerAlias) {
        this.aliasComparator = null;
        return (ExcelWriter) super.setHeaderAlias(headerAlias);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.poi.excel.ExcelBase
    public ExcelWriter clearHeaderAlias() {
        this.aliasComparator = null;
        return (ExcelWriter) super.clearHeaderAlias();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.poi.excel.ExcelBase
    public ExcelWriter addHeaderAlias(String name, String alias) {
        this.aliasComparator = null;
        return (ExcelWriter) super.addHeaderAlias(name, alias);
    }

    public ExcelWriter setOnlyAlias(boolean isOnlyAlias) {
        this.onlyAlias = isOnlyAlias;
        return this;
    }

    public ExcelWriter setFreezePane(int rowSplit) {
        return setFreezePane(0, rowSplit);
    }

    public ExcelWriter setFreezePane(int colSplit, int rowSplit) {
        getSheet().createFreezePane(colSplit, rowSplit);
        return this;
    }

    public ExcelWriter setColumnWidth(int columnIndex, int width) {
        if (columnIndex < 0) {
            this.sheet.setDefaultColumnWidth(width);
        } else {
            this.sheet.setColumnWidth(columnIndex, width * 256);
        }
        return this;
    }

    public ExcelWriter setDefaultRowHeight(int height) {
        return setRowHeight(-1, height);
    }

    public ExcelWriter setRowHeight(int rownum, int height) {
        if (rownum < 0) {
            this.sheet.setDefaultRowHeightInPoints(height);
        } else {
            Row row = this.sheet.getRow(rownum);
            if (null != row) {
                row.setHeightInPoints(height);
            }
        }
        return this;
    }

    public ExcelWriter setHeaderOrFooter(String text, Align align, boolean isFooter) {
        Footer footer = isFooter ? this.sheet.getFooter() : this.sheet.getHeader();
        switch (align) {
            case LEFT:
                footer.setLeft(text);
                break;
            case RIGHT:
                footer.setRight(text);
                break;
            case CENTER:
                footer.setCenter(text);
                break;
        }
        return this;
    }

    public ExcelWriter addSelect(int x, int y, String... selectList) {
        return addSelect(new CellRangeAddressList(y, y, x, x), selectList);
    }

    public ExcelWriter addSelect(CellRangeAddressList regions, String... selectList) {
        DataValidationHelper validationHelper = this.sheet.getDataValidationHelper();
        DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(selectList);
        DataValidation dataValidation = validationHelper.createValidation(constraint, regions);
        if (dataValidation instanceof XSSFDataValidation) {
            dataValidation.setSuppressDropDownArrow(true);
            dataValidation.setShowErrorBox(true);
        } else {
            dataValidation.setSuppressDropDownArrow(false);
        }
        return addValidationData(dataValidation);
    }

    public ExcelWriter addValidationData(DataValidation dataValidation) {
        this.sheet.addValidationData(dataValidation);
        return this;
    }

    public ExcelWriter merge(int lastColumn) {
        return merge(lastColumn, null);
    }

    public ExcelWriter merge(int lastColumn, Object content) {
        return merge(lastColumn, content, true);
    }

    public ExcelWriter merge(int lastColumn, Object content, boolean isSetHeaderStyle) {
        Assert.isFalse(this.isClosed, "ExcelWriter has been closed!", new Object[0]);
        int rowIndex = this.currentRow.get();
        merge(rowIndex, rowIndex, 0, lastColumn, content, isSetHeaderStyle);
        if (null != content) {
            this.currentRow.incrementAndGet();
        }
        return this;
    }

    public ExcelWriter merge(int firstRow, int lastRow, int firstColumn, int lastColumn, Object content, boolean isSetHeaderStyle) {
        Assert.isFalse(this.isClosed, "ExcelWriter has been closed!", new Object[0]);
        CellStyle style = null;
        if (null != this.styleSet) {
            style = this.styleSet.getStyleByValueType(content, isSetHeaderStyle);
        }
        return merge(firstRow, lastRow, firstColumn, lastColumn, content, style);
    }

    public ExcelWriter merge(int firstRow, int lastRow, int firstColumn, int lastColumn, Object content, CellStyle cellStyle) {
        Assert.isFalse(this.isClosed, "ExcelWriter has been closed!", new Object[0]);
        CellUtil.mergingCells(getSheet(), firstRow, lastRow, firstColumn, lastColumn, cellStyle);
        if (null != content) {
            Cell cell = getOrCreateCell(firstColumn, firstRow);
            CellUtil.setCellValue(cell, content, cellStyle);
        }
        return this;
    }

    public ExcelWriter write(Iterable<?> data) {
        return write(data, 0 == getCurrentRow());
    }

    public ExcelWriter write(Iterable<?> data, boolean isWriteKeyAsHead) {
        Assert.isFalse(this.isClosed, "ExcelWriter has been closed!", new Object[0]);
        boolean isFirst = true;
        for (Object object : data) {
            writeRow(object, isFirst && isWriteKeyAsHead);
            if (isFirst) {
                isFirst = false;
            }
        }
        return this;
    }

    public ExcelWriter write(Iterable<?> data, Comparator<String> comparator) {
        Map<?, ?> map;
        Assert.isFalse(this.isClosed, "ExcelWriter has been closed!", new Object[0]);
        boolean isFirstRow = true;
        for (Object obj : data) {
            if (obj instanceof Map) {
                map = new TreeMap<>(comparator);
                map.putAll((Map) obj);
            } else {
                map = BeanUtil.beanToMap(obj, (Map<String, Object>) new TreeMap(comparator), false, false);
            }
            writeRow(map, isFirstRow);
            if (isFirstRow) {
                isFirstRow = false;
            }
        }
        return this;
    }

    public ExcelWriter writeImg(File imgFile, int col1, int row1, int col2, int row2) {
        return writeImg(imgFile, 0, 0, 0, 0, col1, row1, col2, row2);
    }

    public ExcelWriter writeImg(File imgFile, int dx1, int dy1, int dx2, int dy2, int col1, int row1, int col2, int row2) {
        return writeImg(imgFile, 6, dx1, dy1, dx2, dy2, col1, row1, col2, row2);
    }

    public ExcelWriter writeImg(File imgFile, int imgType, int dx1, int dy1, int dx2, int dy2, int col1, int row1, int col2, int row2) {
        return writeImg(FileUtil.readBytes(imgFile), imgType, dx1, dy1, dx2, dy2, col1, row1, col2, row2);
    }

    public ExcelWriter writeImg(byte[] pictureData, int imgType, int dx1, int dy1, int dx2, int dy2, int col1, int row1, int col2, int row2) {
        Drawing<?> patriarch = this.sheet.createDrawingPatriarch();
        ClientAnchor anchor = this.workbook.getCreationHelper().createClientAnchor();
        anchor.setDx1(dx1);
        anchor.setDy1(dy1);
        anchor.setDx2(dx2);
        anchor.setDy2(dy2);
        anchor.setCol1(col1);
        anchor.setRow1(row1);
        anchor.setCol2(col2);
        anchor.setRow2(row2);
        patriarch.createPicture(anchor, this.workbook.addPicture(pictureData, imgType));
        return this;
    }

    public ExcelWriter writeHeadRow(Iterable<?> rowData) {
        Assert.isFalse(this.isClosed, "ExcelWriter has been closed!", new Object[0]);
        this.headLocationCache = new SafeConcurrentHashMap();
        Row row = this.sheet.createRow(this.currentRow.getAndIncrement());
        int i = 0;
        for (Object value : rowData) {
            Cell cell = row.createCell(i);
            CellUtil.setCellValue(cell, value, this.styleSet, true);
            this.headLocationCache.put(StrUtil.toString(value), Integer.valueOf(i));
            i++;
        }
        return this;
    }

    public ExcelWriter writeSecHeadRow(Iterable<?> rowData) {
        Row row = RowUtil.getOrCreateRow(this.sheet, this.currentRow.getAndIncrement());
        Iterator<?> iterator = rowData.iterator();
        if (row.getLastCellNum() != 0) {
            for (int i = 0; i < this.workbook.getSpreadsheetVersion().getMaxColumns(); i++) {
                Cell cell = row.getCell(i);
                if (cell == null) {
                    if (!iterator.hasNext()) {
                        break;
                    }
                    Cell cell2 = row.createCell(i);
                    CellUtil.setCellValue(cell2, iterator.next(), this.styleSet, true);
                }
            }
        } else {
            writeHeadRow(rowData);
        }
        return this;
    }

    public ExcelWriter writeRow(Object rowBean, boolean isWriteKeyAsHead) {
        Map rowMap;
        if (rowBean instanceof Map) {
            if (MapUtil.isNotEmpty(this.headerAlias)) {
                rowMap = MapUtil.newTreeMap((Map) rowBean, getCachedAliasComparator());
            } else {
                rowMap = (Map) rowBean;
            }
        } else {
            if (rowBean instanceof Iterable) {
                return writeRow((Iterable) rowBean);
            }
            if (rowBean instanceof Hyperlink) {
                return writeRow(CollUtil.newArrayList(rowBean), isWriteKeyAsHead);
            }
            if (BeanUtil.isBean(rowBean.getClass())) {
                if (MapUtil.isEmpty(this.headerAlias)) {
                    rowMap = BeanUtil.beanToMap(rowBean, (Map<String, Object>) new LinkedHashMap(), false, false);
                } else {
                    rowMap = BeanUtil.beanToMap(rowBean, (Map<String, Object>) new TreeMap(getCachedAliasComparator()), false, false);
                }
            } else {
                return writeRow(CollUtil.newArrayList(rowBean), isWriteKeyAsHead);
            }
        }
        return writeRow((Map<?, ?>) rowMap, isWriteKeyAsHead);
    }

    public ExcelWriter writeRow(Map<?, ?> rowMap, boolean isWriteKeyAsHead) {
        Assert.isFalse(this.isClosed, "ExcelWriter has been closed!", new Object[0]);
        if (MapUtil.isEmpty(rowMap)) {
            return passCurrentRow();
        }
        Table<?, ?, ?> aliasTable = aliasTable(rowMap);
        if (isWriteKeyAsHead) {
            writeHeadRow(aliasTable.columnKeys());
            int i = 0;
            for (Object key : aliasTable.rowKeySet()) {
                this.headLocationCache.putIfAbsent(StrUtil.toString(key), Integer.valueOf(i));
                i++;
            }
        }
        if (MapUtil.isNotEmpty(this.headLocationCache)) {
            Row row = RowUtil.getOrCreateRow(this.sheet, this.currentRow.getAndIncrement());
            Iterator<Table.Cell<R, C, V>> it = aliasTable.iterator();
            while (it.hasNext()) {
                Table.Cell<?, ?, ?> cell = (Table.Cell) it.next();
                Integer location = this.headLocationCache.get(StrUtil.toString(cell.getRowKey()));
                if (null == location) {
                    location = this.headLocationCache.get(StrUtil.toString(cell.getColumnKey()));
                }
                if (null != location) {
                    CellUtil.setCellValue(CellUtil.getOrCreateCell(row, location.intValue()), cell.getValue(), this.styleSet, false);
                }
            }
        } else {
            writeRow(aliasTable.values());
        }
        return this;
    }

    public ExcelWriter writeRow(Iterable<?> rowData) {
        Assert.isFalse(this.isClosed, "ExcelWriter has been closed!", new Object[0]);
        RowUtil.writeRow(this.sheet.createRow(this.currentRow.getAndIncrement()), rowData, this.styleSet, false);
        return this;
    }

    public ExcelWriter writeCellValue(String locationRef, Object value) {
        CellLocation cellLocation = ExcelUtil.toLocation(locationRef);
        return writeCellValue(cellLocation.getX(), cellLocation.getY(), value);
    }

    public ExcelWriter writeCellValue(int x, int y, Object value) {
        Cell cell = getOrCreateCell(x, y);
        CellUtil.setCellValue(cell, value, this.styleSet, false);
        return this;
    }

    public ExcelWriter setStyle(CellStyle style, String locationRef) {
        CellLocation cellLocation = ExcelUtil.toLocation(locationRef);
        return setStyle(style, cellLocation.getX(), cellLocation.getY());
    }

    public ExcelWriter setStyle(CellStyle style, int x, int y) {
        Cell cell = getOrCreateCell(x, y);
        cell.setCellStyle(style);
        return this;
    }

    public ExcelWriter setRowStyle(int y, CellStyle style) {
        getOrCreateRow(y).setRowStyle(style);
        return this;
    }

    public ExcelWriter setRowStyleIfHasData(int y, CellStyle style) {
        if (y < 0) {
            throw new IllegalArgumentException("Invalid row number (" + y + ")");
        }
        int columnCount = getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            setStyle(style, i, y);
        }
        return this;
    }

    public ExcelWriter setColumnStyle(int x, CellStyle style) {
        this.sheet.setDefaultColumnStyle(x, style);
        return this;
    }

    public ExcelWriter setColumnStyleIfHasData(int x, int y, CellStyle style) {
        if (x < 0) {
            throw new IllegalArgumentException("Invalid column number (" + x + ")");
        }
        if (y < 0) {
            throw new IllegalArgumentException("Invalid row number (" + y + ")");
        }
        int rowCount = getRowCount();
        for (int i = y; i < rowCount; i++) {
            setStyle(style, x, i);
        }
        return this;
    }

    public Font createFont() {
        return getWorkbook().createFont();
    }

    public ExcelWriter flush() throws IORuntimeException {
        return flush(this.destFile);
    }

    public ExcelWriter flush(File destFile) throws IORuntimeException {
        Assert.notNull(destFile, "[destFile] is null, and you must call setDestFile(File) first or call flush(OutputStream).", new Object[0]);
        return flush(FileUtil.getOutputStream(destFile), true);
    }

    public ExcelWriter flush(OutputStream out) throws IORuntimeException {
        return flush(out, false);
    }

    public ExcelWriter flush(OutputStream out, boolean isCloseOut) throws IORuntimeException {
        Assert.isFalse(this.isClosed, "ExcelWriter has been closed!", new Object[0]);
        try {
            try {
                this.workbook.write(out);
                out.flush();
                if (isCloseOut) {
                    IoUtil.close((Closeable) out);
                }
                return this;
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        } catch (Throwable th) {
            if (isCloseOut) {
                IoUtil.close((Closeable) out);
            }
            throw th;
        }
    }

    @Override // cn.hutool.poi.excel.ExcelBase, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        if (null != this.destFile) {
            flush();
        }
        closeWithoutFlush();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void closeWithoutFlush() {
        super.close();
        this.currentRow = null;
        this.styleSet = null;
    }

    private Table<?, ?, ?> aliasTable(Map<?, ?> rowMap) {
        Table<Object, Object, Object> filteredTable = new RowKeyTable<>(new LinkedHashMap(), TableMap::new);
        if (MapUtil.isEmpty(this.headerAlias)) {
            rowMap.forEach((key, value) -> {
                filteredTable.put(key, key, value);
            });
        } else {
            rowMap.forEach((key2, value2) -> {
                String aliasName = this.headerAlias.get(StrUtil.toString(key2));
                if (null != aliasName) {
                    filteredTable.put(key2, aliasName, value2);
                } else if (false == this.onlyAlias) {
                    filteredTable.put(key2, key2, value2);
                }
            });
        }
        return filteredTable;
    }

    private Comparator<String> getCachedAliasComparator() {
        if (MapUtil.isEmpty(this.headerAlias)) {
            return null;
        }
        Comparator<String> aliasComparator = this.aliasComparator;
        if (null == aliasComparator) {
            Set<String> keySet = this.headerAlias.keySet();
            aliasComparator = new IndexedComparator(keySet.toArray(new String[0]));
            this.aliasComparator = aliasComparator;
        }
        return aliasComparator;
    }
}
