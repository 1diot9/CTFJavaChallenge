package org.springframework.web.servlet.view.document;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/document/AbstractPdfStamperView.class */
public abstract class AbstractPdfStamperView extends AbstractUrlBasedView {
    protected abstract void mergePdfDocument(Map<String, Object> model, PdfStamper stamper, HttpServletRequest request, HttpServletResponse response) throws Exception;

    public AbstractPdfStamperView() {
        setContentType(MediaType.APPLICATION_PDF_VALUE);
    }

    @Override // org.springframework.web.servlet.view.AbstractView
    protected boolean generatesDownloadContent() {
        return true;
    }

    @Override // org.springframework.web.servlet.view.AbstractView
    protected final void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ByteArrayOutputStream baos = createTemporaryOutputStream();
        PdfReader reader = readPdfResource();
        PdfStamper stamper = new PdfStamper(reader, baos);
        mergePdfDocument(model, stamper, request, response);
        stamper.close();
        writeToResponse(response, baos);
    }

    protected PdfReader readPdfResource() throws IOException {
        String url = getUrl();
        Assert.state(url != null, "'url' not set");
        return new PdfReader(obtainApplicationContext().getResource(url).getInputStream());
    }
}
