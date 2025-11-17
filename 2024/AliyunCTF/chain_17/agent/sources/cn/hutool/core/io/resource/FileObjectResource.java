package cn.hutool.core.io.resource;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import javax.tools.FileObject;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/resource/FileObjectResource.class */
public class FileObjectResource implements Resource {
    private final FileObject fileObject;

    public FileObjectResource(FileObject fileObject) {
        this.fileObject = fileObject;
    }

    public FileObject getFileObject() {
        return this.fileObject;
    }

    @Override // cn.hutool.core.io.resource.Resource
    public String getName() {
        return this.fileObject.getName();
    }

    @Override // cn.hutool.core.io.resource.Resource
    public URL getUrl() {
        try {
            return this.fileObject.toUri().toURL();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    @Override // cn.hutool.core.io.resource.Resource
    public InputStream getStream() {
        try {
            return this.fileObject.openInputStream();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    @Override // cn.hutool.core.io.resource.Resource
    public BufferedReader getReader(Charset charset) {
        try {
            return IoUtil.getReader(this.fileObject.openReader(false));
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }
}
