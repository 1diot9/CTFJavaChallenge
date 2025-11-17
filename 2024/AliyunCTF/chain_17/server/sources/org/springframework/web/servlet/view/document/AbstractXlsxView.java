package org.springframework.web.servlet.view.document;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/document/AbstractXlsxView.class */
public abstract class AbstractXlsxView extends AbstractXlsView {
    public AbstractXlsxView() {
        setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    @Override // org.springframework.web.servlet.view.document.AbstractXlsView
    /* renamed from: createWorkbook */
    protected Workbook mo2913createWorkbook(Map<String, Object> model, HttpServletRequest request) {
        return new XSSFWorkbook();
    }
}
