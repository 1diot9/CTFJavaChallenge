package cn.hutool.core.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.map.MapUtil;
import java.util.Hashtable;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/util/JNDIUtil.class */
public class JNDIUtil {
    public static InitialDirContext createInitialDirContext(Map<String, String> environment) {
        try {
            if (MapUtil.isEmpty(environment)) {
                return new InitialDirContext();
            }
            return new InitialDirContext((Hashtable) Convert.convert(Hashtable.class, (Object) environment));
        } catch (NamingException e) {
            throw new UtilException((Throwable) e);
        }
    }

    public static InitialContext createInitialContext(Map<String, String> environment) {
        try {
            if (MapUtil.isEmpty(environment)) {
                return new InitialContext();
            }
            return new InitialContext((Hashtable) Convert.convert(Hashtable.class, (Object) environment));
        } catch (NamingException e) {
            throw new UtilException((Throwable) e);
        }
    }

    public static Attributes getAttributes(String uri, String... attrIds) {
        try {
            return createInitialDirContext(null).getAttributes(uri, attrIds);
        } catch (NamingException e) {
            throw new UtilException((Throwable) e);
        }
    }
}
