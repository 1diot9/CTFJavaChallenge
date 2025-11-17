package cn.hutool.setting.yaml;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Dict;
import java.io.Closeable;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/setting/yaml/YamlUtil.class */
public class YamlUtil {
    public static Dict loadByPath(String path) {
        return (Dict) loadByPath(path, Dict.class);
    }

    public static <T> T loadByPath(String str, Class<T> cls) {
        return (T) load(ResourceUtil.getStream(str), cls);
    }

    public static <T> T load(InputStream inputStream, Class<T> cls) {
        return (T) load(IoUtil.getBomReader(inputStream), cls);
    }

    public static Dict load(Reader reader) {
        return (Dict) load(reader, Dict.class);
    }

    public static <T> T load(Reader reader, Class<T> cls) {
        return (T) load(reader, cls, true);
    }

    public static <T> T load(Reader reader, Class<T> cls, boolean z) {
        Assert.notNull(reader, "Reader must be not null !", new Object[0]);
        if (null == cls) {
            cls = Object.class;
        }
        try {
            T t = (T) new Yaml().loadAs(reader, cls);
            if (z) {
                IoUtil.close((Closeable) reader);
            }
            return t;
        } catch (Throwable th) {
            if (z) {
                IoUtil.close((Closeable) reader);
            }
            throw th;
        }
    }

    public static void dump(Object object, Writer writer) {
        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dump(object, writer, options);
    }

    public static void dump(Object object, Writer writer, DumperOptions dumperOptions) {
        if (null == dumperOptions) {
            dumperOptions = new DumperOptions();
        }
        Yaml yaml = new Yaml(dumperOptions);
        yaml.dump(object, writer);
    }
}
