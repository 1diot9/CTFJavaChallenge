package ch.qos.logback.core.testUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/testUtil/FileToBufferUtil.class */
public class FileToBufferUtil {
    public static void readIntoList(File file, List<String> stringList) throws IOException {
        if (file.getName().endsWith(".gz")) {
            gzFileReadIntoList(file, stringList);
        } else if (file.getName().endsWith(".zip")) {
            zipFileReadIntoList(file, stringList);
        } else {
            regularReadIntoList(file, stringList);
        }
    }

    private static void zipFileReadIntoList(File file, List<String> stringList) throws IOException {
        System.out.println("Reading zip file [" + String.valueOf(file) + "]");
        ZipFile zipFile = new ZipFile(file);
        try {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            ZipEntry entry = entries.nextElement();
            readInputStream(zipFile.getInputStream(entry), stringList);
            zipFile.close();
        } catch (Throwable th) {
            try {
                zipFile.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    static void readInputStream(InputStream is, List<String> stringList) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        while (true) {
            String line = in.readLine();
            if (line != null) {
                stringList.add(line);
            } else {
                in.close();
                return;
            }
        }
    }

    public static void regularReadIntoList(File file, List<String> stringList) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        BufferedReader in = new BufferedReader(new InputStreamReader(fis));
        while (true) {
            String line = in.readLine();
            if (line != null) {
                stringList.add(line);
            } else {
                in.close();
                return;
            }
        }
    }

    public static void gzFileReadIntoList(File file, List<String> stringList) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        GZIPInputStream gzis = new GZIPInputStream(fis);
        readInputStream(gzis, stringList);
    }
}
