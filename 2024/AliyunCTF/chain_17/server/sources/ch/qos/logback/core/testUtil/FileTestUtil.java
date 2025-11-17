package ch.qos.logback.core.testUtil;

import java.io.File;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/testUtil/FileTestUtil.class */
public class FileTestUtil {
    public static void makeTestOutputDir() {
        File target = new File(CoreTestConstants.TARGET_DIR);
        if (target.exists() && target.isDirectory()) {
            File testoutput = new File(CoreTestConstants.OUTPUT_DIR_PREFIX);
            if (!testoutput.exists()) {
                boolean result = testoutput.mkdir();
                if (!result) {
                    throw new IllegalStateException("Failed to create " + String.valueOf(testoutput));
                }
                return;
            }
            return;
        }
        throw new IllegalStateException("target/ does not exist");
    }
}
