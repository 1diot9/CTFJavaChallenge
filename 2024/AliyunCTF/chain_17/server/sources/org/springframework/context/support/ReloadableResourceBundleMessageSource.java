package org.springframework.context.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.context.MessageSource;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/ReloadableResourceBundleMessageSource.class */
public class ReloadableResourceBundleMessageSource extends AbstractResourceBasedMessageSource implements ResourceLoaderAware {
    private static final String XML_EXTENSION = ".xml";

    @Nullable
    private Properties fileEncodings;
    private List<String> fileExtensions = List.of(".properties", ".xml");
    private boolean concurrentRefresh = true;
    private PropertiesPersister propertiesPersister = DefaultPropertiesPersister.INSTANCE;
    private ResourceLoader resourceLoader = new DefaultResourceLoader();
    private final ConcurrentMap<String, Map<Locale, List<String>>> cachedFilenames = new ConcurrentHashMap();
    private final ConcurrentMap<String, PropertiesHolder> cachedProperties = new ConcurrentHashMap();
    private final ConcurrentMap<Locale, PropertiesHolder> cachedMergedProperties = new ConcurrentHashMap();

    public void setFileExtensions(List<String> fileExtensions) {
        Assert.isTrue(!CollectionUtils.isEmpty(fileExtensions), "At least one file extension is required");
        for (String extension : fileExtensions) {
            if (!extension.startsWith(".")) {
                throw new IllegalArgumentException("File extension '" + extension + "' should start with '.'");
            }
        }
        this.fileExtensions = Collections.unmodifiableList(fileExtensions);
    }

    public void setFileEncodings(Properties fileEncodings) {
        this.fileEncodings = fileEncodings;
    }

    public void setConcurrentRefresh(boolean concurrentRefresh) {
        this.concurrentRefresh = concurrentRefresh;
    }

    public void setPropertiesPersister(@Nullable PropertiesPersister propertiesPersister) {
        this.propertiesPersister = propertiesPersister != null ? propertiesPersister : DefaultPropertiesPersister.INSTANCE;
    }

    @Override // org.springframework.context.ResourceLoaderAware
    public void setResourceLoader(@Nullable ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader != null ? resourceLoader : new DefaultResourceLoader();
    }

    @Override // org.springframework.context.support.AbstractMessageSource
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        if (getCacheMillis() < 0) {
            PropertiesHolder propHolder = getMergedProperties(locale);
            String result = propHolder.getProperty(code);
            if (result != null) {
                return result;
            }
            return null;
        }
        for (String basename : getBasenameSet()) {
            List<String> filenames = calculateAllFilenames(basename, locale);
            for (String filename : filenames) {
                PropertiesHolder propHolder2 = getProperties(filename);
                String result2 = propHolder2.getProperty(code);
                if (result2 != null) {
                    return result2;
                }
            }
        }
        return null;
    }

    @Override // org.springframework.context.support.AbstractMessageSource
    @Nullable
    protected MessageFormat resolveCode(String code, Locale locale) {
        if (getCacheMillis() < 0) {
            PropertiesHolder propHolder = getMergedProperties(locale);
            MessageFormat result = propHolder.getMessageFormat(code, locale);
            if (result != null) {
                return result;
            }
            return null;
        }
        for (String basename : getBasenameSet()) {
            List<String> filenames = calculateAllFilenames(basename, locale);
            for (String filename : filenames) {
                PropertiesHolder propHolder2 = getProperties(filename);
                MessageFormat result2 = propHolder2.getMessageFormat(code, locale);
                if (result2 != null) {
                    return result2;
                }
            }
        }
        return null;
    }

    protected PropertiesHolder getMergedProperties(Locale locale) {
        PropertiesHolder mergedHolder = this.cachedMergedProperties.get(locale);
        if (mergedHolder != null) {
            return mergedHolder;
        }
        Properties mergedProps = newProperties();
        long latestTimestamp = -1;
        String[] basenames = StringUtils.toStringArray(getBasenameSet());
        for (int i = basenames.length - 1; i >= 0; i--) {
            List<String> filenames = calculateAllFilenames(basenames[i], locale);
            for (int j = filenames.size() - 1; j >= 0; j--) {
                String filename = filenames.get(j);
                PropertiesHolder propHolder = getProperties(filename);
                if (propHolder.getProperties() != null) {
                    mergedProps.putAll(propHolder.getProperties());
                    if (propHolder.getFileTimestamp() > latestTimestamp) {
                        latestTimestamp = propHolder.getFileTimestamp();
                    }
                }
            }
        }
        PropertiesHolder mergedHolder2 = new PropertiesHolder(mergedProps, latestTimestamp);
        PropertiesHolder existing = this.cachedMergedProperties.putIfAbsent(locale, mergedHolder2);
        if (existing != null) {
            mergedHolder2 = existing;
        }
        return mergedHolder2;
    }

    protected List<String> calculateAllFilenames(String basename, Locale locale) {
        List<String> filenames;
        Map<Locale, List<String>> localeMap = this.cachedFilenames.get(basename);
        if (localeMap != null && (filenames = localeMap.get(locale)) != null) {
            return filenames;
        }
        List<String> filenames2 = new ArrayList<>(7);
        filenames2.addAll(calculateFilenamesForLocale(basename, locale));
        Locale defaultLocale = getDefaultLocale();
        if (defaultLocale != null && !defaultLocale.equals(locale)) {
            List<String> fallbackFilenames = calculateFilenamesForLocale(basename, defaultLocale);
            for (String fallbackFilename : fallbackFilenames) {
                if (!filenames2.contains(fallbackFilename)) {
                    filenames2.add(fallbackFilename);
                }
            }
        }
        filenames2.add(basename);
        if (localeMap == null) {
            localeMap = new ConcurrentHashMap();
            Map<Locale, List<String>> existing = this.cachedFilenames.putIfAbsent(basename, localeMap);
            if (existing != null) {
                localeMap = existing;
            }
        }
        localeMap.put(locale, filenames2);
        return filenames2;
    }

    protected List<String> calculateFilenamesForLocale(String basename, Locale locale) {
        List<String> result = new ArrayList<>(3);
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        StringBuilder temp = new StringBuilder(basename);
        temp.append('_');
        if (language.length() > 0) {
            temp.append(language);
            result.add(0, temp.toString());
        }
        temp.append('_');
        if (country.length() > 0) {
            temp.append(country);
            result.add(0, temp.toString());
        }
        if (variant.length() > 0 && (language.length() > 0 || country.length() > 0)) {
            temp.append('_').append(variant);
            result.add(0, temp.toString());
        }
        return result;
    }

    protected PropertiesHolder getProperties(String filename) {
        PropertiesHolder propHolder = this.cachedProperties.get(filename);
        long originalTimestamp = -2;
        if (propHolder != null) {
            originalTimestamp = propHolder.getRefreshTimestamp();
            if (originalTimestamp == -1 || originalTimestamp > System.currentTimeMillis() - getCacheMillis()) {
                return propHolder;
            }
        } else {
            propHolder = new PropertiesHolder();
            PropertiesHolder existingHolder = this.cachedProperties.putIfAbsent(filename, propHolder);
            if (existingHolder != null) {
                propHolder = existingHolder;
            }
        }
        if (this.concurrentRefresh && propHolder.getRefreshTimestamp() >= 0) {
            if (!propHolder.refreshLock.tryLock()) {
                return propHolder;
            }
        } else {
            propHolder.refreshLock.lock();
        }
        try {
            PropertiesHolder existingHolder2 = this.cachedProperties.get(filename);
            if (existingHolder2 != null && existingHolder2.getRefreshTimestamp() > originalTimestamp) {
                return existingHolder2;
            }
            PropertiesHolder refreshProperties = refreshProperties(filename, propHolder);
            propHolder.refreshLock.unlock();
            return refreshProperties;
        } finally {
            propHolder.refreshLock.unlock();
        }
    }

    protected PropertiesHolder refreshProperties(String filename, @Nullable PropertiesHolder propHolder) {
        PropertiesHolder propHolder2;
        long refreshTimestamp = getCacheMillis() < 0 ? -1L : System.currentTimeMillis();
        Resource resource = resolveResource(filename);
        if (resource.exists()) {
            long fileTimestamp = -1;
            if (getCacheMillis() >= 0) {
                try {
                    fileTimestamp = resource.lastModified();
                    if (propHolder != null && propHolder.getFileTimestamp() == fileTimestamp) {
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Re-caching properties for filename [" + filename + "] - file hasn't been modified");
                        }
                        propHolder.setRefreshTimestamp(refreshTimestamp);
                        return propHolder;
                    }
                } catch (IOException ex) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug(resource + " could not be resolved in the file system - assuming that it hasn't changed", ex);
                    }
                    fileTimestamp = -1;
                }
            }
            try {
                Properties props = loadProperties(resource, filename);
                propHolder2 = new PropertiesHolder(props, fileTimestamp);
            } catch (IOException ex2) {
                if (this.logger.isWarnEnabled()) {
                    this.logger.warn("Could not parse properties file [" + resource.getFilename() + "]", ex2);
                }
                propHolder2 = new PropertiesHolder();
            }
        } else {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("No properties file found for [" + filename + "]");
            }
            propHolder2 = new PropertiesHolder();
        }
        propHolder2.setRefreshTimestamp(refreshTimestamp);
        this.cachedProperties.put(filename, propHolder2);
        return propHolder2;
    }

    protected Resource resolveResource(String filename) {
        Resource resource = null;
        for (String fileExtension : this.fileExtensions) {
            resource = this.resourceLoader.getResource(filename + fileExtension);
            if (resource.exists()) {
                return resource;
            }
        }
        return (Resource) Objects.requireNonNull(resource);
    }

    protected Properties loadProperties(Resource resource, String filename) throws IOException {
        Properties props = newProperties();
        InputStream is = resource.getInputStream();
        try {
            String resourceFilename = resource.getFilename();
            if (resourceFilename != null && resourceFilename.endsWith(".xml")) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Loading properties [" + resource.getFilename() + "]");
                }
                this.propertiesPersister.loadFromXml(props, is);
            } else {
                String encoding = null;
                if (this.fileEncodings != null) {
                    encoding = this.fileEncodings.getProperty(filename);
                }
                if (encoding == null) {
                    encoding = getDefaultEncoding();
                }
                if (encoding != null) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Loading properties [" + resource.getFilename() + "] with encoding '" + encoding + "'");
                    }
                    this.propertiesPersister.load(props, new InputStreamReader(is, encoding));
                } else {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Loading properties [" + resource.getFilename() + "]");
                    }
                    this.propertiesPersister.load(props, is);
                }
            }
            if (is != null) {
                is.close();
            }
            return props;
        } catch (Throwable th) {
            if (is != null) {
                try {
                    is.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    protected Properties newProperties() {
        return new Properties();
    }

    public void clearCache() {
        this.logger.debug("Clearing entire resource bundle cache");
        this.cachedProperties.clear();
        this.cachedMergedProperties.clear();
    }

    public void clearCacheIncludingAncestors() {
        clearCache();
        MessageSource parentMessageSource = getParentMessageSource();
        if (parentMessageSource instanceof ReloadableResourceBundleMessageSource) {
            ReloadableResourceBundleMessageSource reloadableMsgSrc = (ReloadableResourceBundleMessageSource) parentMessageSource;
            reloadableMsgSrc.clearCacheIncludingAncestors();
        }
    }

    public String toString() {
        return getClass().getName() + ": basenames=" + getBasenameSet();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/ReloadableResourceBundleMessageSource$PropertiesHolder.class */
    public class PropertiesHolder {

        @Nullable
        private final Properties properties;
        private final long fileTimestamp;
        private volatile long refreshTimestamp;
        private final ReentrantLock refreshLock;
        private final ConcurrentMap<String, Map<Locale, MessageFormat>> cachedMessageFormats;

        public PropertiesHolder() {
            this.refreshTimestamp = -2L;
            this.refreshLock = new ReentrantLock();
            this.cachedMessageFormats = new ConcurrentHashMap();
            this.properties = null;
            this.fileTimestamp = -1L;
        }

        public PropertiesHolder(Properties properties, long fileTimestamp) {
            this.refreshTimestamp = -2L;
            this.refreshLock = new ReentrantLock();
            this.cachedMessageFormats = new ConcurrentHashMap();
            this.properties = properties;
            this.fileTimestamp = fileTimestamp;
        }

        @Nullable
        public Properties getProperties() {
            return this.properties;
        }

        public long getFileTimestamp() {
            return this.fileTimestamp;
        }

        public void setRefreshTimestamp(long refreshTimestamp) {
            this.refreshTimestamp = refreshTimestamp;
        }

        public long getRefreshTimestamp() {
            return this.refreshTimestamp;
        }

        @Nullable
        public String getProperty(String code) {
            if (this.properties == null) {
                return null;
            }
            return this.properties.getProperty(code);
        }

        @Nullable
        public MessageFormat getMessageFormat(String code, Locale locale) {
            MessageFormat result;
            if (this.properties == null) {
                return null;
            }
            Map<Locale, MessageFormat> localeMap = this.cachedMessageFormats.get(code);
            if (localeMap != null && (result = localeMap.get(locale)) != null) {
                return result;
            }
            String msg = this.properties.getProperty(code);
            if (msg != null) {
                if (localeMap == null) {
                    localeMap = new ConcurrentHashMap();
                    Map<Locale, MessageFormat> existing = this.cachedMessageFormats.putIfAbsent(code, localeMap);
                    if (existing != null) {
                        localeMap = existing;
                    }
                }
                MessageFormat result2 = ReloadableResourceBundleMessageSource.this.createMessageFormat(msg, locale);
                localeMap.put(locale, result2);
                return result2;
            }
            return null;
        }
    }
}
