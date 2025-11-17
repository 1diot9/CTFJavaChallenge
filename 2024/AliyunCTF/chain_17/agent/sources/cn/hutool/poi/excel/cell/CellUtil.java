package cn.hutool.poi.excel.cell;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.StyleSet;
import cn.hutool.poi.excel.cell.setters.CellSetterFactory;
import cn.hutool.poi.excel.cell.values.ErrorCellValue;
import cn.hutool.poi.excel.cell.values.NumericCellValue;
import cn.hutool.poi.excel.editors.TrimEditor;
import java.util.function.Supplier;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.ss.util.SheetUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/cell/CellUtil.class */
public class CellUtil {
    public static Object getCellValue(Cell cell) {
        return getCellValue(cell, false);
    }

    public static Object getCellValue(Cell cell, boolean isTrimCellValue) {
        if (null == cell) {
            return null;
        }
        return getCellValue(cell, cell.getCellType(), isTrimCellValue);
    }

    public static Object getCellValue(Cell cell, CellEditor cellEditor) {
        return getCellValue(cell, (CellType) null, cellEditor);
    }

    public static Object getCellValue(Cell cell, CellType cellType, boolean isTrimCellValue) {
        return getCellValue(cell, cellType, isTrimCellValue ? new TrimEditor() : null);
    }

    public static Object getCellValue(Cell cell, CellType cellType, CellEditor cellEditor) {
        Object value;
        if (null == cell) {
            return null;
        }
        if (cell instanceof NullCell) {
            if (null == cellEditor) {
                return null;
            }
            return cellEditor.edit(cell, null);
        }
        if (null == cellType) {
            cellType = cell.getCellType();
        }
        Cell mergedCell = getMergedRegionCell(cell);
        if (mergedCell != cell) {
            cell = mergedCell;
            cellType = cell.getCellType();
        }
        switch (AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[cellType.ordinal()]) {
            case 1:
                value = new NumericCellValue(cell).getValue();
                break;
            case 2:
                value = Boolean.valueOf(cell.getBooleanCellValue());
                break;
            case 3:
                value = getCellValue(cell, cell.getCachedFormulaResultType(), cellEditor);
                break;
            case 4:
                value = "";
                break;
            case 5:
                value = new ErrorCellValue(cell).getValue();
                break;
            default:
                value = cell.getStringCellValue();
                break;
        }
        return null == cellEditor ? value : cellEditor.edit(cell, value);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: cn.hutool.poi.excel.cell.CellUtil$1, reason: invalid class name */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/cell/CellUtil$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$ss$usermodel$CellType = new int[CellType.values().length];

        static {
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.NUMERIC.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.BOOLEAN.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.FORMULA.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.BLANK.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.ERROR.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public static void setCellValue(Cell cell, Object value, StyleSet styleSet, boolean isHeader) {
        if (null == cell) {
            return;
        }
        if (null != styleSet) {
            cell.setCellStyle(styleSet.getStyleByValueType(value, isHeader));
        }
        setCellValue(cell, value);
    }

    public static void setCellValue(Cell cell, Object value, CellStyle style) {
        setCellValue(cell, cell1 -> {
            setCellValue(cell, value);
            if (null != style) {
                cell1.setCellStyle(style);
            }
        });
    }

    public static void setCellValue(Cell cell, Object value) {
        if (null == cell) {
            return;
        }
        if (CellType.BLANK != cell.getCellType()) {
            cell.setBlank();
        }
        CellSetterFactory.createCellSetter(value).setValue(cell);
    }

    public static Cell getCell(Row row, int cellIndex) {
        if (null == row) {
            return null;
        }
        Cell cell = row.getCell(cellIndex);
        if (null == cell) {
            return new NullCell(row, cellIndex);
        }
        return cell;
    }

    public static Cell getOrCreateCell(Row row, int cellIndex) {
        if (null == row) {
            return null;
        }
        Cell cell = row.getCell(cellIndex);
        if (null == cell) {
            cell = row.createCell(cellIndex);
        }
        return cell;
    }

    public static boolean isMergedRegion(Sheet sheet, String locationRef) {
        CellLocation cellLocation = ExcelUtil.toLocation(locationRef);
        return isMergedRegion(sheet, cellLocation.getX(), cellLocation.getY());
    }

    public static boolean isMergedRegion(Cell cell) {
        return isMergedRegion(cell.getSheet(), cell.getColumnIndex(), cell.getRowIndex());
    }

    public static boolean isMergedRegion(Sheet sheet, int x, int y) {
        if (sheet != null) {
            int sheetMergeCount = sheet.getNumMergedRegions();
            for (int i = 0; i < sheetMergeCount; i++) {
                CellRangeAddress ca = sheet.getMergedRegion(i);
                if (y >= ca.getFirstRow() && y <= ca.getLastRow() && x >= ca.getFirstColumn() && x <= ca.getLastColumn()) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public static CellRangeAddress getCellRangeAddress(Sheet sheet, String locationRef) {
        CellLocation cellLocation = ExcelUtil.toLocation(locationRef);
        return getCellRangeAddress(sheet, cellLocation.getX(), cellLocation.getY());
    }

    public static CellRangeAddress getCellRangeAddress(Cell cell) {
        return getCellRangeAddress(cell.getSheet(), cell.getColumnIndex(), cell.getRowIndex());
    }

    public static CellRangeAddress getCellRangeAddress(Sheet sheet, int x, int y) {
        if (sheet != null) {
            int sheetMergeCount = sheet.getNumMergedRegions();
            for (int i = 0; i < sheetMergeCount; i++) {
                CellRangeAddress ca = sheet.getMergedRegion(i);
                if (y >= ca.getFirstRow() && y <= ca.getLastRow() && x >= ca.getFirstColumn() && x <= ca.getLastColumn()) {
                    return ca;
                }
            }
            return null;
        }
        return null;
    }

    public static void setMergedRegionStyle(Cell cell, CellStyle cellStyle) {
        CellRangeAddress cellRangeAddress = getCellRangeAddress(cell);
        if (cellRangeAddress != null) {
            setMergeCellStyle(cellStyle, cellRangeAddress, cell.getSheet());
        }
    }

    public static int mergingCells(Sheet sheet, int firstRow, int lastRow, int firstColumn, int lastColumn) {
        return mergingCells(sheet, firstRow, lastRow, firstColumn, lastColumn, null);
    }

    public static int mergingCells(Sheet sheet, int firstRow, int lastRow, int firstColumn, int lastColumn, CellStyle cellStyle) {
        CellRangeAddress cellRangeAddress = new CellRangeAddress(firstRow, lastRow, firstColumn, lastColumn);
        setMergeCellStyle(cellStyle, cellRangeAddress, sheet);
        return sheet.addMergedRegion(cellRangeAddress);
    }

    public static Object getMergedRegionValue(Sheet sheet, String locationRef) {
        CellLocation cellLocation = ExcelUtil.toLocation(locationRef);
        return getMergedRegionValue(sheet, cellLocation.getX(), cellLocation.getY());
    }

    public static Object getMergedRegionValue(Sheet sheet, int x, int y) {
        return getCellValue(SheetUtil.getCell(sheet, x, y));
    }

    public static Cell getMergedRegionCell(Cell cell) {
        if (null == cell) {
            return null;
        }
        return (Cell) ObjectUtil.defaultIfNull(getCellIfMergedRegion(cell.getSheet(), cell.getColumnIndex(), cell.getRowIndex()), cell);
    }

    public static Cell getMergedRegionCell(Sheet sheet, int x, int y) {
        return (Cell) ObjectUtil.defaultIfNull(getCellIfMergedRegion(sheet, x, y), (Supplier<? extends Cell>) () -> {
            return SheetUtil.getCell(sheet, y, x);
        });
    }

    public static void setComment(Cell cell, String commentText, String commentAuthor, ClientAnchor anchor) {
        Sheet sheet = cell.getSheet();
        Workbook wb = sheet.getWorkbook();
        Drawing<?> drawing = sheet.createDrawingPatriarch();
        CreationHelper factory = wb.getCreationHelper();
        if (anchor == null) {
            anchor = factory.createClientAnchor();
            anchor.setCol1(cell.getColumnIndex() + 1);
            anchor.setCol2(cell.getColumnIndex() + 3);
            anchor.setRow1(cell.getRowIndex());
            anchor.setRow2(cell.getRowIndex() + 2);
            anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
        }
        Comment comment = drawing.createCellComment(anchor);
        comment.setAddress(cell.getAddress());
        comment.setString(factory.createRichTextString(commentText));
        comment.setAuthor(StrUtil.nullToEmpty(commentAuthor));
        cell.setCellComment(comment);
    }

    private static Cell getCellIfMergedRegion(Sheet sheet, int x, int y) {
        for (CellRangeAddress ca : sheet.getMergedRegions()) {
            if (ca.isInRange(y, x)) {
                return SheetUtil.getCell(sheet, ca.getFirstRow(), ca.getFirstColumn());
            }
        }
        return null;
    }

    private static void setMergeCellStyle(CellStyle cellStyle, CellRangeAddress cellRangeAddress, Sheet sheet) {
        if (null != cellStyle) {
            RegionUtil.setBorderTop(cellStyle.getBorderTop(), cellRangeAddress, sheet);
            RegionUtil.setBorderRight(cellStyle.getBorderRight(), cellRangeAddress, sheet);
            RegionUtil.setBorderBottom(cellStyle.getBorderBottom(), cellRangeAddress, sheet);
            RegionUtil.setBorderLeft(cellStyle.getBorderLeft(), cellRangeAddress, sheet);
            RegionUtil.setTopBorderColor(cellStyle.getTopBorderColor(), cellRangeAddress, sheet);
            RegionUtil.setRightBorderColor(cellStyle.getRightBorderColor(), cellRangeAddress, sheet);
            RegionUtil.setLeftBorderColor(cellStyle.getLeftBorderColor(), cellRangeAddress, sheet);
            RegionUtil.setBottomBorderColor(cellStyle.getBottomBorderColor(), cellRangeAddress, sheet);
        }
    }
}
