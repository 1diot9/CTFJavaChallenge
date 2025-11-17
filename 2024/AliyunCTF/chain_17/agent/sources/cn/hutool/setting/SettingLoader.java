package cn.hutool.setting;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.SystemPropsUtil;
import cn.hutool.log.Log;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/setting/SettingLoader.class */
public class SettingLoader {
    private static final Log log = Log.get();
    private static final char COMMENT_FLAG_PRE = '#';
    private char assignFlag;
    private String varRegex;
    private final Charset charset;
    private final boolean isUseVariable;
    private final GroupedMap groupedMap;

    public SettingLoader(GroupedMap groupedMap) {
        this(groupedMap, CharsetUtil.CHARSET_UTF_8, false);
    }

    public SettingLoader(GroupedMap groupedMap, Charset charset, boolean isUseVariable) {
        this.assignFlag = '=';
        this.varRegex = "\\$\\{(.*?)\\}";
        this.groupedMap = groupedMap;
        this.charset = charset;
        this.isUseVariable = isUseVariable;
    }

    public boolean load(Resource resource) {
        if (resource == null) {
            throw new NullPointerException("Null setting url define!");
        }
        log.debug("Load setting file [{}]", resource);
        InputStream settingStream = null;
        try {
            try {
                settingStream = resource.getStream();
                load(settingStream);
                IoUtil.close((Closeable) settingStream);
                return true;
            } catch (Exception e) {
                log.error(e, "Load setting error!", new Object[0]);
                IoUtil.close((Closeable) settingStream);
                return false;
            }
        } catch (Throwable th) {
            IoUtil.close((Closeable) settingStream);
            throw th;
        }
    }

    public synchronized boolean load(InputStream settingStream) throws IOException {
        this.groupedMap.clear();
        BufferedReader reader = null;
        try {
            reader = IoUtil.getReader(settingStream, this.charset);
            String group = null;
            while (true) {
                String line = reader.readLine();
                if (line != null) {
                    String line2 = line.trim();
                    if (!StrUtil.isBlank(line2) && !StrUtil.startWith((CharSequence) line2, '#')) {
                        if (StrUtil.isSurround((CharSequence) line2, '[', ']')) {
                            group = line2.substring(1, line2.length() - 1).trim();
                        } else {
                            String[] keyValue = StrUtil.splitToArray(line2, this.assignFlag, 2);
                            if (keyValue.length >= 2) {
                                String value = keyValue[1].trim();
                                if (this.isUseVariable) {
                                    value = replaceVar(group, value);
                                }
                                this.groupedMap.put(group, keyValue[0].trim(), value);
                            }
                        }
                    }
                } else {
                    IoUtil.close((Closeable) reader);
                    return true;
                }
            }
        } catch (Throwable th) {
            IoUtil.close((Closeable) reader);
            throw th;
        }
    }

    public void setVarRegex(String regex) {
        this.varRegex = regex;
    }

    public void setAssignFlag(char assignFlag) {
        this.assignFlag = assignFlag;
    }

    public void store(String absolutePath) {
        store(FileUtil.touch(absolutePath));
    }

    public void store(File file) {
        Assert.notNull(file, "File to store must be not null !", new Object[0]);
        log.debug("Store Setting to [{}]...", file.getAbsolutePath());
        PrintWriter writer = null;
        try {
            writer = FileUtil.getPrintWriter(file, this.charset, false);
            store(writer);
            IoUtil.close((Closeable) writer);
        } catch (Throwable th) {
            IoUtil.close((Closeable) writer);
            throw th;
        }
    }

    private synchronized void store(PrintWriter writer) {
        for (Map.Entry<String, LinkedHashMap<String, String>> groupEntry : this.groupedMap.entrySet()) {
            writer.println(StrUtil.format("{}{}{}", '[', groupEntry.getKey(), ']'));
            for (Map.Entry<String, String> entry : groupEntry.getValue().entrySet()) {
                writer.println(StrUtil.format("{} {} {}", entry.getKey(), Character.valueOf(this.assignFlag), entry.getValue()));
            }
        }
    }

    private String replaceVar(String group, String value) {
        Set<String> vars = (Set) ReUtil.findAll(this.varRegex, value, 0, new HashSet());
        for (String var : vars) {
            String key = ReUtil.get(this.varRegex, var, 1);
            if (StrUtil.isNotBlank(key)) {
                String varValue = this.groupedMap.get(group, key);
                if (null == varValue) {
                    List<String> groupAndKey = StrUtil.split(key, '.', 2);
                    if (groupAndKey.size() > 1) {
                        varValue = this.groupedMap.get(groupAndKey.get(0), groupAndKey.get(1));
                    }
                }
                if (null == varValue) {
                    varValue = SystemPropsUtil.get(key);
                }
                if (null != varValue) {
                    value = value.replace(var, varValue);
                }
            }
        }
        return value;
    }
}
