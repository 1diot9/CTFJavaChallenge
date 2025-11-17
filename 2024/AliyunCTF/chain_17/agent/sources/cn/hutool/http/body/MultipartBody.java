package cn.hutool.http.body;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpGlobalConfig;
import cn.hutool.http.MultipartOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/body/MultipartBody.class */
public class MultipartBody implements RequestBody {
    private static final String CONTENT_TYPE_MULTIPART_PREFIX = ContentType.MULTIPART.getValue() + "; boundary=";
    private final Map<String, Object> form;
    private final Charset charset;
    private final String boundary = HttpGlobalConfig.getBoundary();

    public static MultipartBody create(Map<String, Object> form, Charset charset) {
        return new MultipartBody(form, charset);
    }

    public String getContentType() {
        return CONTENT_TYPE_MULTIPART_PREFIX + this.boundary;
    }

    public MultipartBody(Map<String, Object> form, Charset charset) {
        this.form = form;
        this.charset = charset;
    }

    @Override // cn.hutool.http.body.RequestBody
    public void write(OutputStream out) {
        MultipartOutputStream stream = new MultipartOutputStream(out, this.charset, this.boundary);
        if (MapUtil.isNotEmpty(this.form)) {
            Map<String, Object> map = this.form;
            stream.getClass();
            map.forEach(stream::write);
        }
        stream.finish();
    }

    public String toString() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        write(out);
        return IoUtil.toStr(out, this.charset);
    }
}
