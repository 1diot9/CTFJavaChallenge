package cn.hutool.poi.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.extractor.ExcelExtractor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/ExcelExtractorUtil.class */
public class ExcelExtractorUtil {
    public static ExcelExtractor getExtractor(Workbook wb) {
        org.apache.poi.hssf.extractor.ExcelExtractor xSSFExcelExtractor;
        if (wb instanceof HSSFWorkbook) {
            xSSFExcelExtractor = new org.apache.poi.hssf.extractor.ExcelExtractor((HSSFWorkbook) wb);
        } else {
            xSSFExcelExtractor = new XSSFExcelExtractor((XSSFWorkbook) wb);
        }
        return xSSFExcelExtractor;
    }

    public static String readAsText(Workbook wb, boolean withSheetName) {
        ExcelExtractor extractor = getExtractor(wb);
        extractor.setIncludeSheetNames(withSheetName);
        return extractor.getText();
    }
}
