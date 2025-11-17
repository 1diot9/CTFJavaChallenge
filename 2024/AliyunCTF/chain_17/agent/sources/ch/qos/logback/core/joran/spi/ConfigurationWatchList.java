package ch.qos.logback.core.joran.spi;

import ch.qos.logback.core.spi.ContextAwareBase;
import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/spi/ConfigurationWatchList.class */
public class ConfigurationWatchList extends ContextAwareBase {
    URL mainURL;
    List<File> fileWatchList = new ArrayList();
    List<Long> lastModifiedList = new ArrayList();

    public ConfigurationWatchList buildClone() {
        ConfigurationWatchList out = new ConfigurationWatchList();
        out.mainURL = this.mainURL;
        out.fileWatchList = new ArrayList(this.fileWatchList);
        out.lastModifiedList = new ArrayList(this.lastModifiedList);
        return out;
    }

    public void clear() {
        this.mainURL = null;
        this.lastModifiedList.clear();
        this.fileWatchList.clear();
    }

    public void setMainURL(URL mainURL) {
        this.mainURL = mainURL;
        if (mainURL != null) {
            addAsFileToWatch(mainURL);
        }
    }

    private void addAsFileToWatch(URL url) {
        File file = convertToFile(url);
        if (file != null) {
            this.fileWatchList.add(file);
            this.lastModifiedList.add(Long.valueOf(file.lastModified()));
        }
    }

    public void addToWatchList(URL url) {
        addAsFileToWatch(url);
    }

    public URL getMainURL() {
        return this.mainURL;
    }

    public List<File> getCopyOfFileWatchList() {
        return new ArrayList(this.fileWatchList);
    }

    public boolean changeDetected() {
        int len = this.fileWatchList.size();
        for (int i = 0; i < len; i++) {
            long lastModified = this.lastModifiedList.get(i).longValue();
            File file = this.fileWatchList.get(i);
            if (lastModified != file.lastModified()) {
                return true;
            }
        }
        return false;
    }

    File convertToFile(URL url) {
        String protocol = url.getProtocol();
        if ("file".equals(protocol)) {
            return new File(URLDecoder.decode(url.getFile()));
        }
        addInfo("URL [" + String.valueOf(url) + "] is not of type file");
        return null;
    }
}
