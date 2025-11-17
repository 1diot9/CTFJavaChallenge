package cn.hutool.setting.dialect;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.NoResourceException;
import cn.hutool.core.map.SafeConcurrentHashMap;
import cn.hutool.core.util.StrUtil;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/setting/dialect/PropsUtil.class */
public class PropsUtil {
    private static final Map<String, Props> propsMap = new SafeConcurrentHashMap();

    public static Props get(String name) {
        return propsMap.computeIfAbsent(name, filePath -> {
            String extName = FileUtil.extName(filePath);
            if (StrUtil.isEmpty(extName)) {
                filePath = filePath + ".properties";
            }
            return new Props(filePath);
        });
    }

    public static Props getFirstFound(String... names) {
        for (String name : names) {
            try {
                return get(name);
            } catch (NoResourceException e) {
            }
        }
        return null;
    }

    public static Props getSystemProps() {
        return new Props(System.getProperties());
    }
}
