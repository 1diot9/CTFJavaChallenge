package org.springframework.web.servlet.view.document;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/document/AbstractXlsxStreamingView.class */
public abstract class AbstractXlsxStreamingView extends AbstractXlsxView {
    @Override // org.springframework.web.servlet.view.document.AbstractXlsxView, org.springframework.web.servlet.view.document.AbstractXlsView
    /* renamed from: createWorkbook, reason: collision with other method in class */
    protected /* bridge */ /* synthetic */ Workbook mo2826createWorkbook(Map model, HttpServletRequest request) {
        return createWorkbook((Map<String, Object>) model, request);
    }

    protected SXSSFWorkbook createWorkbook(Map<String, Object> model, HttpServletRequest request) {
        return new SXSSFWorkbook();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.view.document.AbstractXlsView
    public void renderWorkbook(Workbook workbook, HttpServletResponse response) throws IOException {
        super.renderWorkbook(workbook, response);
        ((SXSSFWorkbook) workbook).dispose();
    }
}
