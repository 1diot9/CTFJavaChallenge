package cn.hutool.setting;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.io.resource.FileResource;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.io.resource.UrlResource;
import cn.hutool.core.io.watch.SimpleWatcher;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.WatchUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import cn.hutool.setting.dialect.Props;
import java.io.Closeable;
import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/setting/Setting.class */
public class Setting extends AbsSetting implements Map<String, String> {
    private static final long serialVersionUID = 3618305164959883393L;
    public static final Charset DEFAULT_CHARSET = CharsetUtil.CHARSET_UTF_8;
    public static final String EXT_NAME = "setting";
    private final GroupedMap groupedMap;
    protected Charset charset;
    protected boolean isUseVariable;
    protected Resource resource;
    private SettingLoader settingLoader;
    private WatchMonitor watchMonitor;

    public static Setting create() {
        return new Setting();
    }

    public Setting() {
        this.groupedMap = new GroupedMap();
        this.charset = DEFAULT_CHARSET;
    }

    public Setting(String path) {
        this(path, false);
    }

    public Setting(String path, boolean isUseVariable) {
        this(path, DEFAULT_CHARSET, isUseVariable);
    }

    public Setting(String path, Charset charset, boolean isUseVariable) {
        this.groupedMap = new GroupedMap();
        Assert.notBlank(path, "Blank setting path !", new Object[0]);
        init(ResourceUtil.getResourceObj(path), charset, isUseVariable);
    }

    public Setting(File configFile, Charset charset, boolean isUseVariable) {
        this.groupedMap = new GroupedMap();
        Assert.notNull(configFile, "Null setting file define!", new Object[0]);
        init(new FileResource(configFile), charset, isUseVariable);
    }

    public Setting(String path, Class<?> clazz, Charset charset, boolean isUseVariable) {
        this.groupedMap = new GroupedMap();
        Assert.notBlank(path, "Blank setting path !", new Object[0]);
        init(new ClassPathResource(path, clazz), charset, isUseVariable);
    }

    public Setting(URL url, Charset charset, boolean isUseVariable) {
        this.groupedMap = new GroupedMap();
        Assert.notNull(url, "Null setting url define!", new Object[0]);
        init(new UrlResource(url), charset, isUseVariable);
    }

    public Setting(Resource resource, Charset charset, boolean isUseVariable) {
        this.groupedMap = new GroupedMap();
        init(resource, charset, isUseVariable);
    }

    public boolean init(Resource resource, Charset charset, boolean isUseVariable) {
        Assert.notNull(resource, "Setting resource must be not null!", new Object[0]);
        this.resource = resource;
        this.charset = charset;
        this.isUseVariable = isUseVariable;
        return load();
    }

    public synchronized boolean load() {
        if (null == this.settingLoader) {
            this.settingLoader = new SettingLoader(this.groupedMap, this.charset, this.isUseVariable);
        }
        return this.settingLoader.load(this.resource);
    }

    public void autoLoad(boolean autoReload) {
        autoLoad(autoReload, null);
    }

    public void autoLoad(boolean autoReload, final Consumer<Boolean> callback) {
        if (autoReload) {
            Assert.notNull(this.resource, "Setting resource must be not null !", new Object[0]);
            if (null != this.watchMonitor) {
                this.watchMonitor.close();
            }
            this.watchMonitor = WatchUtil.createModify(this.resource.getUrl(), new SimpleWatcher() { // from class: cn.hutool.setting.Setting.1
                @Override // cn.hutool.core.io.watch.watchers.IgnoreWatcher, cn.hutool.core.io.watch.Watcher
                public void onModify(WatchEvent<?> event, Path currentPath) {
                    boolean success = Setting.this.load();
                    if (callback != null) {
                        callback.accept(Boolean.valueOf(success));
                    }
                }
            });
            this.watchMonitor.start();
            StaticLog.debug("Auto load for [{}] listenning...", this.resource.getUrl());
            return;
        }
        IoUtil.close((Closeable) this.watchMonitor);
        this.watchMonitor = null;
    }

    public URL getSettingUrl() {
        if (null == this.resource) {
            return null;
        }
        return this.resource.getUrl();
    }

    public String getSettingPath() {
        URL settingUrl = getSettingUrl();
        if (null == settingUrl) {
            return null;
        }
        return settingUrl.getPath();
    }

    @Override // java.util.Map
    public int size() {
        return this.groupedMap.size();
    }

    @Override // cn.hutool.setting.AbsSetting
    public String getByGroup(String key, String group) {
        return this.groupedMap.get(group, key);
    }

    public Object getAndRemove(String... keys) {
        Object value = null;
        for (String key : keys) {
            value = remove((Object) key);
            if (null != value) {
                break;
            }
        }
        return value;
    }

    public String getAndRemoveStr(String... keys) {
        String value = null;
        for (String key : keys) {
            value = remove((Object) key);
            if (null != value) {
                break;
            }
        }
        return value;
    }

    public Map<String, String> getMap(String group) {
        LinkedHashMap<String, String> map = this.groupedMap.get((Object) group);
        return null != map ? map : new LinkedHashMap(0);
    }

    public Setting getSetting(String group) {
        Setting setting = new Setting();
        setting.putAll(getMap(group));
        return setting;
    }

    public Properties getProperties(String group) {
        Properties properties = new Properties();
        properties.putAll(getMap(group));
        return properties;
    }

    public Props getProps(String group) {
        Props props = new Props();
        props.putAll(getMap(group));
        return props;
    }

    public void store() {
        URL resourceUrl = getSettingUrl();
        Assert.notNull(resourceUrl, "Setting path must be not null !", new Object[0]);
        store(FileUtil.file(resourceUrl));
    }

    public void store(String absolutePath) {
        store(FileUtil.touch(absolutePath));
    }

    public void store(File file) {
        if (null == this.settingLoader) {
            this.settingLoader = new SettingLoader(this.groupedMap, this.charset, this.isUseVariable);
        }
        this.settingLoader.store(file);
    }

    public Properties toProperties() {
        Properties properties = new Properties();
        for (Map.Entry<String, LinkedHashMap<String, String>> groupEntry : this.groupedMap.entrySet()) {
            String group = groupEntry.getKey();
            for (Map.Entry<String, String> entry : groupEntry.getValue().entrySet()) {
                properties.setProperty(StrUtil.isEmpty(group) ? entry.getKey() : group + '.' + entry.getKey(), entry.getValue());
            }
        }
        return properties;
    }

    public GroupedMap getGroupedMap() {
        return this.groupedMap;
    }

    public List<String> getGroups() {
        return CollUtil.newArrayList((Collection) this.groupedMap.keySet());
    }

    public Setting setVarRegex(String regex) {
        if (null == this.settingLoader) {
            throw new NullPointerException("SettingLoader is null !");
        }
        this.settingLoader.setVarRegex(regex);
        return this;
    }

    public Setting setCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public boolean isEmpty(String group) {
        return this.groupedMap.isEmpty(group);
    }

    public boolean containsKey(String group, String key) {
        return this.groupedMap.containsKey(group, key);
    }

    public boolean containsValue(String group, String value) {
        return this.groupedMap.containsValue(group, value);
    }

    public String get(String group, String key) {
        return this.groupedMap.get(group, key);
    }

    public String putByGroup(String key, String group, String value) {
        return this.groupedMap.put(group, key, value);
    }

    public String remove(String group, Object key) {
        return this.groupedMap.remove(group, Convert.toStr(key));
    }

    public Setting putAll(String group, Map<? extends String, ? extends String> m) {
        this.groupedMap.putAll(group, m);
        return this;
    }

    public Setting addSetting(Setting setting) {
        for (Map.Entry<String, LinkedHashMap<String, String>> e : setting.getGroupedMap().entrySet()) {
            putAll(e.getKey(), e.getValue());
        }
        return this;
    }

    public Setting clear(String group) {
        this.groupedMap.clear(group);
        return this;
    }

    public Set<String> keySet(String group) {
        return this.groupedMap.keySet(group);
    }

    public Collection<String> values(String group) {
        return this.groupedMap.values(group);
    }

    public Set<Map.Entry<String, String>> entrySet(String group) {
        return this.groupedMap.entrySet(group);
    }

    public Setting set(String key, String value) {
        put(key, value);
        return this;
    }

    public Setting setByGroup(String key, String group, String value) {
        putByGroup(key, group, value);
        return this;
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return this.groupedMap.isEmpty();
    }

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        return this.groupedMap.containsKey("", Convert.toStr(key));
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        return this.groupedMap.containsValue("", Convert.toStr(value));
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.Map
    public String get(Object key) {
        return this.groupedMap.get("", Convert.toStr(key));
    }

    @Override // java.util.Map
    public String put(String key, String value) {
        return this.groupedMap.put("", key, value);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.Map
    public String remove(Object key) {
        return this.groupedMap.remove("", Convert.toStr(key));
    }

    @Override // java.util.Map
    public void putAll(Map<? extends String, ? extends String> m) {
        this.groupedMap.putAll("", m);
    }

    @Override // java.util.Map
    public void clear() {
        this.groupedMap.clear("");
    }

    @Override // java.util.Map
    public Set<String> keySet() {
        return this.groupedMap.keySet("");
    }

    @Override // java.util.Map
    public Collection<String> values() {
        return this.groupedMap.values("");
    }

    @Override // java.util.Map
    public Set<Map.Entry<String, String>> entrySet() {
        return this.groupedMap.entrySet("");
    }

    @Override // java.util.Map
    public int hashCode() {
        int result = (31 * 1) + (this.charset == null ? 0 : this.charset.hashCode());
        return (31 * ((31 * ((31 * result) + this.groupedMap.hashCode())) + (this.isUseVariable ? 1231 : 1237))) + (this.resource == null ? 0 : this.resource.hashCode());
    }

    @Override // java.util.Map
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Setting other = (Setting) obj;
        if (this.charset == null) {
            if (other.charset != null) {
                return false;
            }
        } else if (false == this.charset.equals(other.charset)) {
            return false;
        }
        if (false == this.groupedMap.equals(other.groupedMap) || this.isUseVariable != other.isUseVariable) {
            return false;
        }
        if (this.resource == null) {
            return other.resource == null;
        }
        return this.resource.equals(other.resource);
    }

    public String toString() {
        return this.groupedMap.toString();
    }
}
