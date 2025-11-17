package cn.hutool.poi.excel;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.exceptions.POIException;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/WorkbookUtil.class */
public class WorkbookUtil {
    public static Workbook createBook(String excelFilePath) {
        return createBook(excelFilePath, false);
    }

    public static Workbook createBook(String excelFilePath, boolean readOnly) {
        return createBook(FileUtil.file(excelFilePath), null, readOnly);
    }

    public static Workbook createBook(File excelFile) {
        return createBook(excelFile, false);
    }

    public static Workbook createBook(File excelFile, boolean readOnly) {
        return createBook(excelFile, null, readOnly);
    }

    public static Workbook createBookForWriter(File excelFile) {
        if (null == excelFile) {
            return createBook(true);
        }
        if (excelFile.exists()) {
            return createBook(FileUtil.getInputStream(excelFile));
        }
        return createBook(StrUtil.endWithIgnoreCase(excelFile.getName(), ".xlsx"));
    }

    public static Workbook createBook(File excelFile, String password) {
        return createBook(excelFile, password, false);
    }

    public static Workbook createBook(File excelFile, String password, boolean readOnly) {
        try {
            return WorkbookFactory.create(excelFile, password, readOnly);
        } catch (Exception e) {
            throw new POIException(e);
        }
    }

    public static Workbook createBook(InputStream in) {
        return createBook(in, (String) null);
    }

    public static Workbook createBook(InputStream in, String password) {
        try {
            try {
                Workbook create = WorkbookFactory.create(IoUtil.toMarkSupportStream(in), password);
                IoUtil.close((Closeable) in);
                return create;
            } catch (Exception e) {
                throw new POIException(e);
            }
        } catch (Throwable th) {
            IoUtil.close((Closeable) in);
            throw th;
        }
    }

    public static Workbook createBook(boolean isXlsx) {
        try {
            return WorkbookFactory.create(isXlsx);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static SXSSFWorkbook createSXSSFBook(String excelFilePath) {
        return createSXSSFBook(excelFilePath, false);
    }

    public static SXSSFWorkbook createSXSSFBook(String excelFilePath, boolean readOnly) {
        return createSXSSFBook(FileUtil.file(excelFilePath), (String) null, readOnly);
    }

    public static SXSSFWorkbook createSXSSFBook(File excelFile) {
        return createSXSSFBook(excelFile, false);
    }

    public static SXSSFWorkbook createSXSSFBook(File excelFile, boolean readOnly) {
        return createSXSSFBook(excelFile, (String) null, readOnly);
    }

    public static SXSSFWorkbook createSXSSFBook(File excelFile, String password) {
        return createSXSSFBook(excelFile, password, false);
    }

    public static SXSSFWorkbook createSXSSFBook(File excelFile, String password, boolean readOnly) {
        return toSXSSFBook(createBook(excelFile, password, readOnly));
    }

    public static SXSSFWorkbook createSXSSFBook(InputStream in) {
        return createSXSSFBook(in, (String) null);
    }

    public static SXSSFWorkbook createSXSSFBook(InputStream in, String password) {
        return toSXSSFBook(createBook(in, password));
    }

    public static SXSSFWorkbook createSXSSFBook() {
        return new SXSSFWorkbook();
    }

    public static SXSSFWorkbook createSXSSFBook(int rowAccessWindowSize) {
        return new SXSSFWorkbook(rowAccessWindowSize);
    }

    public static SXSSFWorkbook createSXSSFBook(int rowAccessWindowSize, boolean compressTmpFiles, boolean useSharedStringsTable) {
        return new SXSSFWorkbook((XSSFWorkbook) null, rowAccessWindowSize, compressTmpFiles, useSharedStringsTable);
    }

    public static void writeBook(Workbook book, OutputStream out) throws IORuntimeException {
        try {
            book.write(out);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static Sheet getOrCreateSheet(Workbook book, String sheetName) {
        if (null == book) {
            return null;
        }
        String sheetName2 = StrUtil.isBlank(sheetName) ? "sheet1" : sheetName;
        Sheet sheet = book.getSheet(sheetName2);
        if (null == sheet) {
            sheet = book.createSheet(sheetName2);
        }
        return sheet;
    }

    public static Sheet getOrCreateSheet(Workbook book, int sheetIndex) {
        Sheet sheet = null;
        try {
            sheet = book.getSheetAt(sheetIndex);
        } catch (IllegalArgumentException e) {
        }
        if (null == sheet) {
            sheet = book.createSheet();
        }
        return sheet;
    }

    public static boolean isEmpty(Sheet sheet) {
        return null == sheet || (sheet.getLastRowNum() == 0 && sheet.getPhysicalNumberOfRows() == 0);
    }

    private static SXSSFWorkbook toSXSSFBook(Workbook book) {
        if (book instanceof SXSSFWorkbook) {
            return (SXSSFWorkbook) book;
        }
        if (book instanceof XSSFWorkbook) {
            return new SXSSFWorkbook((XSSFWorkbook) book);
        }
        throw new POIException("The input is not a [xlsx] format.");
    }
}
