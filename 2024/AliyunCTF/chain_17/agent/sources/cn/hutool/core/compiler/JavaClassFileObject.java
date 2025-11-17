package cn.hutool.core.compiler;

import cn.hutool.core.util.URLUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/compiler/JavaClassFileObject.class */
class JavaClassFileObject extends SimpleJavaFileObject {
    private final ByteArrayOutputStream byteArrayOutputStream;

    /* JADX INFO: Access modifiers changed from: protected */
    public JavaClassFileObject(String className) {
        super(URLUtil.getStringURI(className.replace('.', '/') + JavaFileObject.Kind.CLASS.extension), JavaFileObject.Kind.CLASS);
        this.byteArrayOutputStream = new ByteArrayOutputStream();
    }

    public InputStream openInputStream() {
        return new ByteArrayInputStream(this.byteArrayOutputStream.toByteArray());
    }

    public OutputStream openOutputStream() {
        return this.byteArrayOutputStream;
    }
}
