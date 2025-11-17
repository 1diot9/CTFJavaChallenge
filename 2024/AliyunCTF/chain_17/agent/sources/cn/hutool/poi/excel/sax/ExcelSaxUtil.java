package cn.hutool.poi.excel.sax;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.DependencyException;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.RegexPool;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelDateUtil;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import cn.hutool.poi.exceptions.POIException;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/sax/ExcelSaxUtil.class */
public class ExcelSaxUtil {
    public static final char CELL_FILL_CHAR = '@';
    public static final int MAX_CELL_BIT = 3;

    public static ExcelSaxReader<?> createSaxReader(boolean isXlsx, RowHandler rowHandler) {
        return isXlsx ? new Excel07SaxReader(rowHandler) : new Excel03SaxReader(rowHandler);
    }

    public static Object getDataValue(CellDataType cellDataType, String value, SharedStrings sharedStrings, String numFmtString) {
        Object result;
        if (null == value) {
            return null;
        }
        if (null == cellDataType) {
            cellDataType = CellDataType.NULL;
        }
        switch (cellDataType) {
            case BOOL:
                result = Boolean.valueOf(value.charAt(0) != '0');
                break;
            case ERROR:
                result = StrUtil.format("\\\"ERROR: {} ", value);
                break;
            case FORMULA:
                result = StrUtil.format("\"{}\"", value);
                break;
            case INLINESTR:
                result = new XSSFRichTextString(value).toString();
                break;
            case SSTINDEX:
                try {
                    int index = Integer.parseInt(value);
                    result = sharedStrings.getItemAt(index).getString();
                    break;
                } catch (NumberFormatException e) {
                    result = value;
                    break;
                }
            case NUMBER:
                try {
                    result = getNumberValue(value, numFmtString);
                    break;
                } catch (NumberFormatException e2) {
                    result = value;
                    break;
                }
            case DATE:
                try {
                    result = getDateValue(value);
                    break;
                } catch (Exception e3) {
                    result = value;
                    break;
                }
            default:
                result = value;
                break;
        }
        return result;
    }

    public static String formatCellContent(String value, int numFmtIndex, String numFmtString) {
        if (null != numFmtString) {
            try {
                value = new DataFormatter().formatRawCellContents(Double.parseDouble(value), numFmtIndex, numFmtString);
            } catch (NumberFormatException e) {
            }
        }
        return value;
    }

    public static int countNullCell(String preRef, String ref) {
        String preXfd = StrUtil.nullToDefault(preRef, StrPool.AT).replaceAll(RegexPool.NUMBERS, "");
        String xfd = StrUtil.nullToDefault(ref, StrPool.AT).replaceAll(RegexPool.NUMBERS, "");
        String preXfd2 = StrUtil.fillBefore(preXfd, '@', 3);
        String xfd2 = StrUtil.fillBefore(xfd, '@', 3);
        char[] preLetter = preXfd2.toCharArray();
        char[] letter = xfd2.toCharArray();
        int res = ((letter[0] - preLetter[0]) * 26 * 26) + ((letter[1] - preLetter[1]) * 26) + (letter[2] - preLetter[2]);
        return res - 1;
    }

    public static void readFrom(InputStream xmlDocStream, ContentHandler handler) throws DependencyException, POIException, IORuntimeException {
        try {
            XMLReader xmlReader = XMLHelper.newXMLReader();
            xmlReader.setContentHandler(handler);
            try {
                xmlReader.parse(new InputSource(xmlDocStream));
            } catch (IOException e) {
                throw new IORuntimeException(e);
            } catch (SAXException e2) {
                throw new POIException(e2);
            }
        } catch (ParserConfigurationException | SAXException e3) {
            if (e3.getMessage().contains("org.apache.xerces.parsers.SAXParser")) {
                throw new DependencyException(e3, "You need to add 'xerces:xercesImpl' to your project and version >= 2.11.0", new Object[0]);
            }
            throw new POIException(e3);
        }
    }

    public static boolean isDateFormat(CellValueRecordInterface cell, FormatTrackingHSSFListener formatListener) {
        int formatIndex = formatListener.getFormatIndex(cell);
        String formatString = formatListener.getFormatString(cell);
        return isDateFormat(formatIndex, formatString);
    }

    public static boolean isDateFormat(int formatIndex, String formatString) {
        return ExcelDateUtil.isDateFormat(formatIndex, formatString);
    }

    public static DateTime getDateValue(String value) {
        return getDateValue(Double.parseDouble(value));
    }

    public static DateTime getDateValue(double value) {
        return DateUtil.date(org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value, false));
    }

    public static Object getNumberOrDateValue(CellValueRecordInterface cell, double value, FormatTrackingHSSFListener formatListener) {
        if (isDateFormat(cell, formatListener)) {
            return getDateValue(value);
        }
        return getNumberValue(value, formatListener.getFormatString(cell));
    }

    private static Number getNumberValue(String value, String numFmtString) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        return getNumberValue(Double.parseDouble(value), numFmtString);
    }

    private static Number getNumberValue(double numValue, String numFmtString) {
        if (null != numFmtString && false == StrUtil.contains((CharSequence) numFmtString, '.')) {
            long longPart = (long) numValue;
            if (longPart == numValue) {
                return Long.valueOf(longPart);
            }
        }
        return Double.valueOf(numValue);
    }
}
