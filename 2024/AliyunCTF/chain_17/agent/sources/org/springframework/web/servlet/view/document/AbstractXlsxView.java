package org.springframework.web.servlet.view.document;

import cn.hutool.poi.excel.ExcelUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/document/AbstractXlsxView.class */
public abstract class AbstractXlsxView extends AbstractXlsView {
    public AbstractXlsxView() {
        setContentType(ExcelUtil.XLSX_CONTENT_TYPE);
    }

    @Override // org.springframework.web.servlet.view.document.AbstractXlsView
    /* renamed from: createWorkbook */
    protected Workbook mo2826createWorkbook(Map<String, Object> model, HttpServletRequest request) {
        return new XSSFWorkbook();
    }
}
