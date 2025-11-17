package org.h2.tools;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import org.h2.engine.Constants;
import org.h2.store.FileLister;
import org.h2.store.fs.FileUtils;
import org.h2.util.Tool;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/tools/DeleteDbFiles.class */
public class DeleteDbFiles extends Tool {
    public static void main(String... strArr) throws SQLException {
        new DeleteDbFiles().runTool(strArr);
    }

    @Override // org.h2.util.Tool
    public void runTool(String... strArr) throws SQLException {
        String str = ".";
        String str2 = null;
        boolean z = false;
        int i = 0;
        while (strArr != null && i < strArr.length) {
            String str3 = strArr[i];
            if (str3.equals("-dir")) {
                i++;
                str = strArr[i];
            } else if (str3.equals("-db")) {
                i++;
                str2 = strArr[i];
            } else if (str3.equals("-quiet")) {
                z = true;
            } else {
                if (str3.equals("-help") || str3.equals("-?")) {
                    showUsage();
                    return;
                }
                showUsageAndThrowUnsupportedOption(str3);
            }
            i++;
        }
        process(str, str2, z);
    }

    public static void execute(String str, String str2, boolean z) {
        new DeleteDbFiles().process(str, str2, z);
    }

    private void process(String str, String str2, boolean z) {
        ArrayList<String> databaseFiles = FileLister.getDatabaseFiles(str, str2, true);
        if (databaseFiles.isEmpty() && !z) {
            printNoDatabaseFilesFound(str, str2);
        }
        Iterator<String> it = databaseFiles.iterator();
        while (it.hasNext()) {
            String next = it.next();
            process(next, z);
            if (!z) {
                this.out.println("Processed: " + next);
            }
        }
    }

    private static void process(String str, boolean z) {
        if (FileUtils.isDirectory(str)) {
            FileUtils.tryDelete(str);
        } else if (z || str.endsWith(Constants.SUFFIX_TEMP_FILE) || str.endsWith(Constants.SUFFIX_TRACE_FILE)) {
            FileUtils.tryDelete(str);
        } else {
            FileUtils.delete(str);
        }
    }
}
