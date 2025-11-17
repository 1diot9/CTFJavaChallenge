package cn.hutool.poi.word;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.poi.exceptions.POIException;
import java.awt.Font;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/word/Word07Writer.class */
public class Word07Writer implements Closeable {
    private final XWPFDocument doc;
    protected File destFile;
    protected boolean isClosed;

    public Word07Writer() {
        this(new XWPFDocument());
    }

    public Word07Writer(File destFile) {
        this(DocUtil.create(destFile), destFile);
    }

    public Word07Writer(XWPFDocument doc) {
        this(doc, null);
    }

    public Word07Writer(XWPFDocument doc, File destFile) {
        this.doc = doc;
        this.destFile = destFile;
    }

    public XWPFDocument getDoc() {
        return this.doc;
    }

    public Word07Writer setDestFile(File destFile) {
        this.destFile = destFile;
        return this;
    }

    public Word07Writer addText(Font font, String... texts) {
        return addText(null, font, texts);
    }

    public Word07Writer addText(ParagraphAlignment align, Font font, String... texts) {
        XWPFParagraph p = this.doc.createParagraph();
        if (null != align) {
            p.setAlignment(align);
        }
        if (ArrayUtil.isNotEmpty((Object[]) texts)) {
            for (String text : texts) {
                XWPFRun run = p.createRun();
                run.setText(text);
                if (null != font) {
                    run.setFontFamily(font.getFamily());
                    run.setFontSize(font.getSize());
                    run.setBold(font.isBold());
                    run.setItalic(font.isItalic());
                }
            }
        }
        return this;
    }

    public Word07Writer addTable(Iterable<?> data) {
        TableUtil.createTable(this.doc, data);
        return this;
    }

    public Word07Writer addPicture(File picFile, int width, int height) {
        PicType picType;
        String fileName = picFile.getName();
        String extName = FileUtil.extName(fileName).toUpperCase();
        try {
            picType = PicType.valueOf(extName);
        } catch (IllegalArgumentException e) {
            picType = PicType.JPEG;
        }
        return addPicture(FileUtil.getInputStream(picFile), picType, fileName, width, height);
    }

    public Word07Writer addPicture(InputStream in, PicType picType, String fileName, int width, int height) {
        return addPicture(in, picType, fileName, width, height, ParagraphAlignment.CENTER);
    }

    public Word07Writer addPicture(InputStream in, PicType picType, String fileName, int width, int height, ParagraphAlignment align) {
        XWPFParagraph paragraph = this.doc.createParagraph();
        paragraph.setAlignment(align);
        XWPFRun run = paragraph.createRun();
        try {
            try {
                run.addPicture(in, picType.getValue(), fileName, Units.toEMU(width), Units.toEMU(height));
                IoUtil.close((Closeable) in);
                return this;
            } catch (InvalidFormatException e) {
                throw new POIException((Throwable) e);
            } catch (IOException e2) {
                throw new IORuntimeException(e2);
            }
        } catch (Throwable th) {
            IoUtil.close((Closeable) in);
            throw th;
        }
    }

    public Word07Writer flush() throws IORuntimeException {
        return flush(this.destFile);
    }

    public Word07Writer flush(File destFile) throws IORuntimeException {
        Assert.notNull(destFile, "[destFile] is null, and you must call setDestFile(File) first or call flush(OutputStream).", new Object[0]);
        return flush(FileUtil.getOutputStream(destFile), true);
    }

    public Word07Writer flush(OutputStream out) throws IORuntimeException {
        return flush(out, false);
    }

    public Word07Writer flush(OutputStream out, boolean isCloseOut) throws IORuntimeException {
        Assert.isFalse(this.isClosed, "WordWriter has been closed!", new Object[0]);
        try {
            try {
                this.doc.write(out);
                out.flush();
                if (isCloseOut) {
                    IoUtil.close((Closeable) out);
                }
                return this;
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        } catch (Throwable th) {
            if (isCloseOut) {
                IoUtil.close((Closeable) out);
            }
            throw th;
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        if (null != this.destFile) {
            flush();
        }
        closeWithoutFlush();
    }

    protected void closeWithoutFlush() {
        IoUtil.close((Closeable) this.doc);
        this.isClosed = true;
    }
}
