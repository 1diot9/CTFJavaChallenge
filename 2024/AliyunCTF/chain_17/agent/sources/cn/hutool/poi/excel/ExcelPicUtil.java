package cn.hutool.poi.excel;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFPictureData;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/ExcelPicUtil.class */
public class ExcelPicUtil {
    public static Map<String, PictureData> getPicMap(Workbook workbook, int sheetIndex) {
        Assert.notNull(workbook, "Workbook must be not null !", new Object[0]);
        if (sheetIndex < 0) {
            sheetIndex = 0;
        }
        if (workbook instanceof HSSFWorkbook) {
            return getPicMapXls((HSSFWorkbook) workbook, sheetIndex);
        }
        if (workbook instanceof XSSFWorkbook) {
            return getPicMapXlsx((XSSFWorkbook) workbook, sheetIndex);
        }
        throw new IllegalArgumentException(StrUtil.format("Workbook type [{}] is not supported!", workbook.getClass()));
    }

    private static Map<String, PictureData> getPicMapXls(HSSFWorkbook workbook, int sheetIndex) {
        HashMap hashMap = new HashMap();
        List<HSSFPictureData> pictures = workbook.getAllPictures();
        if (CollectionUtil.isNotEmpty((Collection<?>) pictures)) {
            HSSFSheet sheet = workbook.getSheetAt(sheetIndex);
            for (HSSFPicture hSSFPicture : sheet.getDrawingPatriarch().getChildren()) {
                if (hSSFPicture instanceof HSSFPicture) {
                    int pictureIndex = hSSFPicture.getPictureIndex() - 1;
                    HSSFClientAnchor anchor = hSSFPicture.getAnchor();
                    hashMap.put(StrUtil.format("{}_{}", Integer.valueOf(anchor.getRow1()), Short.valueOf(anchor.getCol1())), pictures.get(pictureIndex));
                }
            }
        }
        return hashMap;
    }

    private static Map<String, PictureData> getPicMapXlsx(XSSFWorkbook workbook, int sheetIndex) {
        Map<String, PictureData> sheetIndexPicMap = new LinkedHashMap<>();
        XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
        for (XSSFDrawing xSSFDrawing : sheet.getRelations()) {
            if (xSSFDrawing instanceof XSSFDrawing) {
                XSSFDrawing drawing = xSSFDrawing;
                List<XSSFShape> shapes = drawing.getShapes();
                for (XSSFShape xSSFShape : shapes) {
                    if (xSSFShape instanceof XSSFPicture) {
                        XSSFPicture pic = (XSSFPicture) xSSFShape;
                        CTMarker ctMarker = pic.getPreferredSize().getFrom();
                        sheetIndexPicMap.put(StrUtil.format("{}_{}", Integer.valueOf(ctMarker.getRow()), Integer.valueOf(ctMarker.getCol())), pic.getPictureData());
                    }
                }
            }
        }
        return sheetIndexPicMap;
    }
}
