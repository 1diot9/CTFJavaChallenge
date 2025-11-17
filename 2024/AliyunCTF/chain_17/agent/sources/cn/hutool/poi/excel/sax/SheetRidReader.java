package cn.hutool.poi.excel.sax;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.exceptions.POIException;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/sax/SheetRidReader.class */
public class SheetRidReader extends DefaultHandler {
    private static final String TAG_NAME = "sheet";
    private static final String RID_ATTR = "r:id";
    private static final String SHEET_ID_ATTR = "sheetId";
    private static final String NAME_ATTR = "name";
    private final Map<Integer, Integer> ID_RID_MAP = new LinkedHashMap();
    private final Map<String, Integer> NAME_RID_MAP = new LinkedHashMap();

    public static SheetRidReader parse(XSSFReader reader) {
        return new SheetRidReader().read(reader);
    }

    public SheetRidReader read(XSSFReader xssfReader) {
        InputStream workbookData = null;
        try {
            try {
                try {
                    workbookData = xssfReader.getWorkbookData();
                    ExcelSaxUtil.readFrom(workbookData, this);
                    IoUtil.close((Closeable) workbookData);
                    return this;
                } catch (IOException e) {
                    throw new IORuntimeException(e);
                }
            } catch (InvalidFormatException e2) {
                throw new POIException((Throwable) e2);
            }
        } catch (Throwable th) {
            IoUtil.close((Closeable) workbookData);
            throw th;
        }
    }

    public Integer getRidBySheetId(int sheetId) {
        return this.ID_RID_MAP.get(Integer.valueOf(sheetId));
    }

    public Integer getRidBySheetIdBase0(int sheetId) {
        Integer rid = getRidBySheetId(sheetId + 1);
        if (null != rid) {
            return Integer.valueOf(rid.intValue() - 1);
        }
        return null;
    }

    public Integer getRidByName(String sheetName) {
        return this.NAME_RID_MAP.get(sheetName);
    }

    public Integer getRidByNameBase0(String sheetName) {
        Integer rid = getRidByName(sheetName);
        if (null != rid) {
            return Integer.valueOf(rid.intValue() - 1);
        }
        return null;
    }

    public Integer getRidByIndex(int index) {
        return (Integer) CollUtil.get(this.NAME_RID_MAP.values(), index);
    }

    public Integer getRidByIndexBase0(int index) {
        Integer rid = (Integer) CollUtil.get(this.NAME_RID_MAP.values(), index);
        if (null != rid) {
            return Integer.valueOf(rid.intValue() - 1);
        }
        return null;
    }

    public List<String> getSheetNames() {
        return ListUtil.toList((Collection) this.NAME_RID_MAP.keySet());
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (TAG_NAME.equalsIgnoreCase(localName)) {
            String ridStr = attributes.getValue(RID_ATTR);
            if (StrUtil.isEmpty(ridStr)) {
                return;
            }
            int rid = Integer.parseInt(StrUtil.removePrefixIgnoreCase(ridStr, ExcelSaxReader.RID_PREFIX));
            String name = attributes.getValue("name");
            if (StrUtil.isNotEmpty(name)) {
                this.NAME_RID_MAP.put(name, Integer.valueOf(rid));
            }
            String sheetIdStr = attributes.getValue(SHEET_ID_ATTR);
            if (StrUtil.isNotEmpty(sheetIdStr)) {
                this.ID_RID_MAP.put(Integer.valueOf(Integer.parseInt(sheetIdStr)), Integer.valueOf(rid));
            }
        }
    }
}
