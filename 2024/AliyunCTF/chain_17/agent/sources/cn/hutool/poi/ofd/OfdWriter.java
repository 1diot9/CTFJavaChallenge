package cn.hutool.poi.ofd;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.PathUtil;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import org.ofdrw.font.Font;
import org.ofdrw.layout.OFDDoc;
import org.ofdrw.layout.edit.Annotation;
import org.ofdrw.layout.element.Div;
import org.ofdrw.layout.element.Img;
import org.ofdrw.layout.element.Paragraph;
import org.ofdrw.reader.OFDReader;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/ofd/OfdWriter.class */
public class OfdWriter implements Serializable, Closeable {
    private static final long serialVersionUID = 1;
    private final OFDDoc doc;

    public OfdWriter(File file) {
        this(file.toPath());
    }

    public OfdWriter(Path file) {
        try {
            if (PathUtil.exists(file, true)) {
                this.doc = new OFDDoc(new OFDReader(file), file);
            } else {
                this.doc = new OFDDoc(file);
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public OfdWriter(OutputStream out) {
        this.doc = new OFDDoc(out);
    }

    public OfdWriter addText(Font font, String... texts) {
        Paragraph paragraph = new Paragraph();
        if (null != font) {
            paragraph.setDefaultFont(font);
        }
        for (String text : texts) {
            paragraph.add(text);
        }
        return add(paragraph);
    }

    public OfdWriter addPicture(File picFile, int width, int height) {
        return addPicture(picFile.toPath(), width, height);
    }

    public OfdWriter addPicture(Path picFile, int width, int height) {
        try {
            Img img = new Img(width, height, picFile);
            return add(img);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public OfdWriter add(Div div) {
        this.doc.add(div);
        return this;
    }

    public OfdWriter add(int page, Annotation annotation) {
        try {
            this.doc.addAnnotation(page, annotation);
            return this;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        IoUtil.close((Closeable) this.doc);
    }
}
