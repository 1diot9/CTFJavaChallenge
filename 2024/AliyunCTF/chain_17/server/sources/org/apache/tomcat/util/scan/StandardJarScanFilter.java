package org.apache.tomcat.util.scan;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.tomcat.JarScanFilter;
import org.apache.tomcat.JarScanType;
import org.apache.tomcat.util.file.Matcher;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/scan/StandardJarScanFilter.class */
public class StandardJarScanFilter implements JarScanFilter {
    private static final String defaultScan;
    private static final boolean defaultSkipAll;
    private static final Set<String> defaultSkipSet = new HashSet();
    private static final Set<String> defaultScanSet = new HashSet();
    private static final String defaultSkip = System.getProperty(Constants.SKIP_JARS_PROPERTY);
    private final ReadWriteLock configurationLock = new ReentrantReadWriteLock();
    private boolean defaultTldScan = true;
    private boolean defaultPluggabilityScan = true;
    private String tldSkip = defaultSkip;
    private final Set<String> tldSkipSet = new HashSet(defaultSkipSet);
    private String tldScan = defaultScan;
    private final Set<String> tldScanSet = new HashSet(defaultScanSet);
    private String pluggabilitySkip = defaultSkip;
    private final Set<String> pluggabilitySkipSet = new HashSet(defaultSkipSet);
    private String pluggabilityScan = defaultScan;
    private final Set<String> pluggabilityScanSet = new HashSet(defaultScanSet);

    static {
        populateSetFromAttribute(defaultSkip, defaultSkipSet);
        defaultScan = System.getProperty(Constants.SCAN_JARS_PROPERTY);
        populateSetFromAttribute(defaultScan, defaultScanSet);
        defaultSkipAll = (defaultSkipSet.contains("*") || defaultSkipSet.contains("*.jar")) && defaultScanSet.isEmpty();
    }

    public String getTldSkip() {
        return this.tldSkip;
    }

    public void setTldSkip(String tldSkip) {
        this.tldSkip = tldSkip;
        Lock writeLock = this.configurationLock.writeLock();
        writeLock.lock();
        try {
            populateSetFromAttribute(tldSkip, this.tldSkipSet);
        } finally {
            writeLock.unlock();
        }
    }

    public String getTldScan() {
        return this.tldScan;
    }

    public void setTldScan(String tldScan) {
        this.tldScan = tldScan;
        Lock writeLock = this.configurationLock.writeLock();
        writeLock.lock();
        try {
            populateSetFromAttribute(tldScan, this.tldScanSet);
        } finally {
            writeLock.unlock();
        }
    }

    @Override // org.apache.tomcat.JarScanFilter
    public boolean isSkipAll() {
        return defaultSkipAll;
    }

    public boolean isDefaultTldScan() {
        return this.defaultTldScan;
    }

    public void setDefaultTldScan(boolean defaultTldScan) {
        this.defaultTldScan = defaultTldScan;
    }

    public String getPluggabilitySkip() {
        return this.pluggabilitySkip;
    }

    public void setPluggabilitySkip(String pluggabilitySkip) {
        this.pluggabilitySkip = pluggabilitySkip;
        Lock writeLock = this.configurationLock.writeLock();
        writeLock.lock();
        try {
            populateSetFromAttribute(pluggabilitySkip, this.pluggabilitySkipSet);
        } finally {
            writeLock.unlock();
        }
    }

    public String getPluggabilityScan() {
        return this.pluggabilityScan;
    }

    public void setPluggabilityScan(String pluggabilityScan) {
        this.pluggabilityScan = pluggabilityScan;
        Lock writeLock = this.configurationLock.writeLock();
        writeLock.lock();
        try {
            populateSetFromAttribute(pluggabilityScan, this.pluggabilityScanSet);
        } finally {
            writeLock.unlock();
        }
    }

    public boolean isDefaultPluggabilityScan() {
        return this.defaultPluggabilityScan;
    }

    public void setDefaultPluggabilityScan(boolean defaultPluggabilityScan) {
        this.defaultPluggabilityScan = defaultPluggabilityScan;
    }

    @Override // org.apache.tomcat.JarScanFilter
    public boolean check(JarScanType jarScanType, String jarName) {
        boolean defaultScan2;
        Set<String> toSkip;
        Set<String> toScan;
        Lock readLock = this.configurationLock.readLock();
        readLock.lock();
        try {
            switch (jarScanType) {
                case TLD:
                    defaultScan2 = this.defaultTldScan;
                    toSkip = this.tldSkipSet;
                    toScan = this.tldScanSet;
                    break;
                case PLUGGABILITY:
                    defaultScan2 = this.defaultPluggabilityScan;
                    toSkip = this.pluggabilitySkipSet;
                    toScan = this.pluggabilityScanSet;
                    break;
                case OTHER:
                default:
                    defaultScan2 = true;
                    toSkip = defaultSkipSet;
                    toScan = defaultScanSet;
                    break;
            }
            if (defaultScan2) {
                if (!Matcher.matchName(toSkip, jarName)) {
                    readLock.unlock();
                    return true;
                }
                if (Matcher.matchName(toScan, jarName)) {
                    return true;
                }
                readLock.unlock();
                return false;
            }
            if (!Matcher.matchName(toScan, jarName)) {
                readLock.unlock();
                return false;
            }
            if (Matcher.matchName(toSkip, jarName)) {
                readLock.unlock();
                return false;
            }
            readLock.unlock();
            return true;
        } finally {
            readLock.unlock();
        }
    }

    private static void populateSetFromAttribute(String attribute, Set<String> set) {
        set.clear();
        if (attribute != null) {
            StringTokenizer tokenizer = new StringTokenizer(attribute, ",");
            while (tokenizer.hasMoreElements()) {
                String token = tokenizer.nextToken().trim();
                if (token.length() > 0) {
                    set.add(token);
                }
            }
        }
    }
}
