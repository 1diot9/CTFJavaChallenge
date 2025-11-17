package org.springframework.web.servlet.view.document;

import cn.hutool.poi.excel.ExcelUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.servlet.view.AbstractView;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/document/AbstractXlsView.class */
public abstract class AbstractXlsView extends AbstractView {
    protected abstract void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception;

    public AbstractXlsView() {
        setContentType(ExcelUtil.XLS_CONTENT_TYPE);
    }

    @Override // org.springframework.web.servlet.view.AbstractView
    protected boolean generatesDownloadContent() {
        return true;
    }

    @Override // org.springframework.web.servlet.view.AbstractView
    protected final void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Workbook workbook = mo2826createWorkbook(model, request);
        buildExcelDocument(model, workbook, request, response);
        response.setContentType(getContentType());
        renderWorkbook(workbook, response);
    }

    /* renamed from: createWorkbook */
    protected Workbook mo2826createWorkbook(Map<String, Object> model, HttpServletRequest request) {
        return new HSSFWorkbook();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void renderWorkbook(Workbook workbook, HttpServletResponse response) throws IOException {
        ServletOutputStream out = response.getOutputStream();
        workbook.write(out);
        workbook.close();
    }
}
