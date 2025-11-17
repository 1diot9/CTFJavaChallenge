package cn.hutool.extra.template.engine.velocity;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.apache.velocity.util.ExtProperties;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/template/engine/velocity/SimpleStringResourceLoader.class */
public class SimpleStringResourceLoader extends ResourceLoader {
    public void init(ExtProperties configuration) {
    }

    public InputStream getResourceStream(String source) throws ResourceNotFoundException {
        return IoUtil.toStream(source, CharsetUtil.CHARSET_UTF_8);
    }

    public Reader getResourceReader(String source, String encoding) throws ResourceNotFoundException {
        return new StringReader(source);
    }

    public boolean isSourceModified(Resource resource) {
        return false;
    }

    public long getLastModified(Resource resource) {
        return 0L;
    }
}
