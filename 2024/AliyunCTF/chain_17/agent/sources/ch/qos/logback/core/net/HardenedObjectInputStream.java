package ch.qos.logback.core.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/net/HardenedObjectInputStream.class */
public class HardenedObjectInputStream extends ObjectInputStream {
    private final List<String> whitelistedClassNames;
    private static final String[] JAVA_PACKAGES = {"java.lang", "java.util"};
    private static final int DEPTH_LIMIT = 16;
    private static final int ARRAY_LIMIT = 10000;

    public HardenedObjectInputStream(InputStream in, String[] whitelist) throws IOException {
        super(in);
        initObjectFilter();
        this.whitelistedClassNames = new ArrayList();
        if (whitelist != null) {
            for (String str : whitelist) {
                this.whitelistedClassNames.add(str);
            }
        }
    }

    private void initObjectFilter() {
        setObjectInputFilter(ObjectInputFilter.Config.createFilter("maxarray=10000;maxdepth=16;"));
    }

    public HardenedObjectInputStream(InputStream in, List<String> whitelist) throws IOException {
        super(in);
        initObjectFilter();
        this.whitelistedClassNames = new ArrayList();
        this.whitelistedClassNames.addAll(whitelist);
    }

    @Override // java.io.ObjectInputStream
    protected Class<?> resolveClass(ObjectStreamClass anObjectStreamClass) throws IOException, ClassNotFoundException {
        String incomingClassName = anObjectStreamClass.getName();
        if (!isWhitelisted(incomingClassName)) {
            throw new InvalidClassException("Unauthorized deserialization attempt", anObjectStreamClass.getName());
        }
        return super.resolveClass(anObjectStreamClass);
    }

    private boolean isWhitelisted(String incomingClassName) {
        for (int i = 0; i < JAVA_PACKAGES.length; i++) {
            if (incomingClassName.startsWith(JAVA_PACKAGES[i])) {
                return true;
            }
        }
        for (String whiteListed : this.whitelistedClassNames) {
            if (incomingClassName.equals(whiteListed)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void addToWhitelist(List<String> additionalAuthorizedClasses) {
        this.whitelistedClassNames.addAll(additionalAuthorizedClasses);
    }
}
