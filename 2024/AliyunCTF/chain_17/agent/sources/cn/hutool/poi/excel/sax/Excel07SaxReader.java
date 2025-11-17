package cn.hutool.poi.excel.sax;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import cn.hutool.poi.exceptions.POIException;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStrings;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/sax/Excel07SaxReader.class */
public class Excel07SaxReader implements ExcelSaxReader<Excel07SaxReader> {
    private final SheetDataSaxHandler handler;

    public Excel07SaxReader(RowHandler rowHandler) {
        this.handler = new SheetDataSaxHandler(rowHandler);
    }

    public Excel07SaxReader setRowHandler(RowHandler rowHandler) {
        this.handler.setRowHandler(rowHandler);
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.poi.excel.sax.ExcelSaxReader
    public Excel07SaxReader read(File file, int rid) throws POIException {
        return read(file, ExcelSaxReader.RID_PREFIX + rid);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.poi.excel.sax.ExcelSaxReader
    public Excel07SaxReader read(File file, String idOrRidOrSheetName) throws POIException {
        try {
            OPCPackage open = OPCPackage.open(file, PackageAccess.READ);
            Throwable th = null;
            try {
                try {
                    Excel07SaxReader read = read(open, idOrRidOrSheetName);
                    if (open != null) {
                        if (0 != 0) {
                            try {
                                open.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            open.close();
                        }
                    }
                    return read;
                } finally {
                }
            } finally {
            }
        } catch (InvalidFormatException | IOException e) {
            throw new POIException((Throwable) e);
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.poi.excel.sax.ExcelSaxReader
    public Excel07SaxReader read(InputStream in, int rid) throws POIException {
        return read(in, ExcelSaxReader.RID_PREFIX + rid);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.poi.excel.sax.ExcelSaxReader
    public Excel07SaxReader read(InputStream in, String idOrRidOrSheetName) throws POIException {
        try {
            OPCPackage opcPackage = OPCPackage.open(in);
            Throwable th = null;
            try {
                try {
                    Excel07SaxReader read = read(opcPackage, idOrRidOrSheetName);
                    if (opcPackage != null) {
                        if (0 != 0) {
                            try {
                                opcPackage.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            opcPackage.close();
                        }
                    }
                    return read;
                } catch (Throwable th3) {
                    if (opcPackage != null) {
                        if (th != null) {
                            try {
                                opcPackage.close();
                            } catch (Throwable th4) {
                                th.addSuppressed(th4);
                            }
                        } else {
                            opcPackage.close();
                        }
                    }
                    throw th3;
                }
            } finally {
            }
        } catch (InvalidFormatException e) {
            throw new POIException((Throwable) e);
        } catch (IOException e2) {
            throw new IORuntimeException(e2);
        }
    }

    public Excel07SaxReader read(OPCPackage opcPackage, int rid) throws POIException {
        return read(opcPackage, ExcelSaxReader.RID_PREFIX + rid);
    }

    public Excel07SaxReader read(OPCPackage opcPackage, String idOrRidOrSheetName) throws POIException {
        try {
            return read(new XSSFReader(opcPackage), idOrRidOrSheetName);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } catch (OpenXML4JException e2) {
            throw new POIException((Throwable) e2);
        }
    }

    public Excel07SaxReader read(XSSFReader xssfReader, String idOrRidOrSheetName) throws POIException {
        try {
            this.handler.stylesTable = xssfReader.getStylesTable();
        } catch (IOException | InvalidFormatException e) {
        }
        this.handler.sharedStrings = (SharedStrings) ReflectUtil.invoke(xssfReader, "getSharedStringsTable", new Object[0]);
        return readSheets(xssfReader, idOrRidOrSheetName);
    }

    private Excel07SaxReader readSheets(XSSFReader xssfReader, String idOrRidOrSheetName) throws POIException {
        this.handler.sheetIndex = getSheetIndex(xssfReader, idOrRidOrSheetName);
        InputStream sheetInputStream = null;
        try {
            try {
                if (this.handler.sheetIndex > -1) {
                    sheetInputStream = xssfReader.getSheet(ExcelSaxReader.RID_PREFIX + (this.handler.sheetIndex + 1));
                    ExcelSaxUtil.readFrom(sheetInputStream, this.handler);
                    this.handler.rowHandler.doAfterAllAnalysed();
                } else {
                    this.handler.sheetIndex = -1;
                    Iterator<InputStream> sheetInputStreams = xssfReader.getSheetsData();
                    while (sheetInputStreams.hasNext()) {
                        this.handler.index = 0;
                        this.handler.sheetIndex++;
                        sheetInputStream = sheetInputStreams.next();
                        ExcelSaxUtil.readFrom(sheetInputStream, this.handler);
                        this.handler.rowHandler.doAfterAllAnalysed();
                    }
                }
                sheetInputStream = sheetInputStream;
                return this;
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e2) {
                throw new POIException(e2);
            }
        } finally {
            IoUtil.close((Closeable) null);
        }
    }

    private int getSheetIndex(XSSFReader xssfReader, String idOrRidOrSheetName) {
        if (StrUtil.startWithIgnoreCase(idOrRidOrSheetName, ExcelSaxReader.RID_PREFIX)) {
            return Integer.parseInt(StrUtil.removePrefixIgnoreCase(idOrRidOrSheetName, ExcelSaxReader.RID_PREFIX));
        }
        SheetRidReader ridReader = SheetRidReader.parse(xssfReader);
        if (StrUtil.startWithIgnoreCase(idOrRidOrSheetName, ExcelSaxReader.SHEET_NAME_PREFIX)) {
            idOrRidOrSheetName = StrUtil.removePrefixIgnoreCase(idOrRidOrSheetName, ExcelSaxReader.SHEET_NAME_PREFIX);
            Integer rid = ridReader.getRidByNameBase0(idOrRidOrSheetName);
            if (null != rid) {
                return rid.intValue();
            }
        } else {
            Integer rid2 = ridReader.getRidByNameBase0(idOrRidOrSheetName);
            if (null != rid2) {
                return rid2.intValue();
            }
            try {
                int sheetIndex = Integer.parseInt(idOrRidOrSheetName);
                return ((Integer) ObjectUtil.defaultIfNull(ridReader.getRidBySheetIdBase0(sheetIndex), Integer.valueOf(sheetIndex))).intValue();
            } catch (NumberFormatException e) {
            }
        }
        throw new IllegalArgumentException("Invalid rId or id or sheetName: " + idOrRidOrSheetName);
    }
}
