package ch.qos.logback.core.util;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.rolling.RolloverFailure;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/util/FileUtil.class */
public class FileUtil extends ContextAwareBase {
    static final int BUF_SIZE = 32768;

    public FileUtil(Context context) {
        setContext(context);
    }

    public static URL fileToURL(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unexpected exception on file [" + String.valueOf(file) + "]", e);
        }
    }

    public static boolean createMissingParentDirectories(File file) {
        File parent = file.getParentFile();
        if (parent == null) {
            return true;
        }
        parent.mkdirs();
        return parent.exists();
    }

    public String resourceAsString(ClassLoader classLoader, String resourceName) {
        URL url = classLoader.getResource(resourceName);
        if (url == null) {
            addError("Failed to find resource [" + resourceName + "]");
            return null;
        }
        InputStreamReader isr = null;
        try {
            try {
                URLConnection urlConnection = url.openConnection();
                urlConnection.setUseCaches(false);
                isr = new InputStreamReader(urlConnection.getInputStream());
                char[] buf = new char[128];
                StringBuilder builder = new StringBuilder();
                while (true) {
                    int count = isr.read(buf, 0, buf.length);
                    if (count == -1) {
                        break;
                    }
                    builder.append(buf, 0, count);
                }
                String sb = builder.toString();
                if (isr != null) {
                    try {
                        isr.close();
                    } catch (IOException e) {
                    }
                }
                return sb;
            } catch (Throwable th) {
                if (isr != null) {
                    try {
                        isr.close();
                    } catch (IOException e2) {
                    }
                }
                throw th;
            }
        } catch (IOException e3) {
            addError("Failed to open " + resourceName, e3);
            if (isr == null) {
                return null;
            }
            try {
                isr.close();
                return null;
            } catch (IOException e4) {
                return null;
            }
        }
    }

    public void copy(String src, String destination) throws RolloverFailure {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            try {
                BufferedInputStream bis2 = new BufferedInputStream(new FileInputStream(src));
                BufferedOutputStream bos2 = new BufferedOutputStream(new FileOutputStream(destination));
                byte[] inbuf = new byte[32768];
                while (true) {
                    int n = bis2.read(inbuf);
                    if (n == -1) {
                        break;
                    } else {
                        bos2.write(inbuf, 0, n);
                    }
                }
                bis2.close();
                bis = null;
                bos2.close();
                bos = null;
                if (0 != 0) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                    }
                }
                if (0 != 0) {
                    try {
                        bos.close();
                    } catch (IOException e2) {
                    }
                }
            } catch (IOException ioe) {
                String msg = "Failed to copy [" + src + "] to [" + destination + "]";
                addError(msg, ioe);
                throw new RolloverFailure(msg);
            }
        } catch (Throwable th) {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e3) {
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e4) {
                }
            }
            throw th;
        }
    }
}
