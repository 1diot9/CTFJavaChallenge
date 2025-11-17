package org.apache.tomcat.jni;

import java.io.File;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/jni/Library.class */
public final class Library {
    private static final String CATALINA_HOME_PROP = "catalina.home";
    private static final String[] NAMES = {"tcnative-2", "libtcnative-2", "tcnative-1", "libtcnative-1"};
    private static Library _instance = null;
    public static int TCN_MAJOR_VERSION = 0;
    public static int TCN_MINOR_VERSION = 0;
    public static int TCN_PATCH_VERSION = 0;
    public static int TCN_IS_DEV_VERSION = 0;
    public static int APR_MAJOR_VERSION = 0;
    public static int APR_MINOR_VERSION = 0;
    public static int APR_PATCH_VERSION = 0;
    public static int APR_IS_DEV_VERSION = 0;

    private static native boolean initialize();

    public static native void terminate();

    private static native int version(int i);

    public static native String versionString();

    public static native String aprVersionString();

    private Library() throws Exception {
        boolean loaded = false;
        StringBuilder err = new StringBuilder();
        File binLib = new File(System.getProperty("catalina.home"), "bin");
        for (int i = 0; i < NAMES.length; i++) {
            File library = new File(binLib, System.mapLibraryName(NAMES[i]));
            try {
                System.load(library.getAbsolutePath());
                loaded = true;
            } catch (ThreadDeath | VirtualMachineError t) {
                throw t;
            } catch (Throwable t2) {
                if (library.exists()) {
                    throw t2;
                }
                if (i > 0) {
                    err.append(", ");
                }
                err.append(t2.getMessage());
            }
            if (loaded) {
                break;
            }
        }
        if (!loaded) {
            String path = System.getProperty("java.library.path");
            String[] paths = path.split(File.pathSeparator);
            for (String value : NAMES) {
                try {
                    System.loadLibrary(value);
                    loaded = true;
                } catch (ThreadDeath | VirtualMachineError t3) {
                    throw t3;
                } catch (Throwable t4) {
                    String name = System.mapLibraryName(value);
                    for (String s : paths) {
                        File fd = new File(s, name);
                        if (fd.exists()) {
                            throw t4;
                        }
                    }
                    if (err.length() > 0) {
                        err.append(", ");
                    }
                    err.append(t4.getMessage());
                }
                if (loaded) {
                    break;
                }
            }
        }
        if (!loaded) {
            StringBuilder names = new StringBuilder();
            for (String name2 : NAMES) {
                names.append(name2);
                names.append(", ");
            }
            throw new LibraryNotFoundError(names.substring(0, names.length() - 2), err.toString());
        }
    }

    private Library(String libraryName) {
        System.loadLibrary(libraryName);
    }

    public static synchronized boolean initialize(String libraryName) throws Exception {
        if (_instance == null) {
            if (libraryName == null) {
                _instance = new Library();
            } else {
                _instance = new Library(libraryName);
            }
            TCN_MAJOR_VERSION = version(1);
            TCN_MINOR_VERSION = version(2);
            TCN_PATCH_VERSION = version(3);
            TCN_IS_DEV_VERSION = version(4);
            APR_MAJOR_VERSION = version(17);
            APR_MINOR_VERSION = version(18);
            APR_PATCH_VERSION = version(19);
            APR_IS_DEV_VERSION = version(20);
            if (APR_MAJOR_VERSION < 1) {
                throw new UnsatisfiedLinkError("Unsupported APR Version (" + aprVersionString() + ")");
            }
        }
        return initialize();
    }
}
