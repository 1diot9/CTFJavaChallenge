package cn.hutool.core.compiler;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.URLUtil;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/compiler/JavaSourceFileObject.class */
public class JavaSourceFileObject extends SimpleJavaFileObject {
    private InputStream inputStream;
    private String sourceCode;

    /* JADX INFO: Access modifiers changed from: protected */
    public JavaSourceFileObject(URI uri) {
        super(uri, JavaFileObject.Kind.SOURCE);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JavaSourceFileObject(String className, String code, Charset charset) {
        this(className, IoUtil.toStream(code, charset));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JavaSourceFileObject(String name, InputStream inputStream) {
        this(URLUtil.getStringURI(name.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension));
        this.inputStream = inputStream;
    }

    public InputStream openInputStream() throws IOException {
        if (this.inputStream == null) {
            this.inputStream = toUri().toURL().openStream();
        }
        return new BufferedInputStream(this.inputStream);
    }

    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        if (this.sourceCode == null) {
            InputStream in = openInputStream();
            Throwable th = null;
            try {
                try {
                    this.sourceCode = IoUtil.readUtf8(in);
                    if (in != null) {
                        if (0 != 0) {
                            try {
                                in.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            in.close();
                        }
                    }
                } finally {
                }
            } catch (Throwable th3) {
                if (in != null) {
                    if (th != null) {
                        try {
                            in.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    } else {
                        in.close();
                    }
                }
                throw th3;
            }
        }
        return this.sourceCode;
    }
}
