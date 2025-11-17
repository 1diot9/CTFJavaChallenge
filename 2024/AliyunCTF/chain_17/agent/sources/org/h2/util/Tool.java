package org.h2.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Properties;
import org.h2.api.ErrorCode;
import org.h2.message.DbException;
import org.h2.store.FileLister;
import org.h2.store.fs.FileUtils;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/Tool.class */
public abstract class Tool {
    protected PrintStream out = System.out;
    private Properties resources;

    public abstract void runTool(String... strArr) throws SQLException;

    public void setOut(PrintStream printStream) {
        this.out = printStream;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SQLException showUsageAndThrowUnsupportedOption(String str) throws SQLException {
        showUsage();
        throw throwUnsupportedOption(str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SQLException throwUnsupportedOption(String str) throws SQLException {
        throw DbException.getJdbcSQLException(ErrorCode.FEATURE_NOT_SUPPORTED_1, str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void printNoDatabaseFilesFound(String str, String str2) {
        StringBuilder sb;
        String dir = FileLister.getDir(str);
        if (!FileUtils.isDirectory(dir)) {
            sb = new StringBuilder("Directory not found: ");
            sb.append(dir);
        } else {
            sb = new StringBuilder("No database files have been found");
            sb.append(" in directory ").append(dir);
            if (str2 != null) {
                sb.append(" for the database ").append(str2);
            }
        }
        this.out.println(sb.toString());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void showUsage() {
        if (this.resources == null) {
            this.resources = new Properties();
            try {
                byte[] resource = Utils.getResource("/org/h2/res/javadoc.properties");
                if (resource != null) {
                    this.resources.load(new ByteArrayInputStream(resource));
                }
            } catch (IOException e) {
                this.out.println("Cannot load /org/h2/res/javadoc.properties");
            }
        }
        String name = getClass().getName();
        this.out.println(this.resources.get(name));
        this.out.println("Usage: java " + getClass().getName() + " <options>");
        this.out.println(this.resources.get(name + ".main"));
        this.out.println("See also https://h2database.com/javadoc/" + name.replace('.', '/') + ThymeleafProperties.DEFAULT_SUFFIX);
    }

    public static boolean isOption(String str, String str2) {
        if (str.equals(str2)) {
            return true;
        }
        if (str.startsWith(str2)) {
            throw DbException.getUnsupportedException("expected: " + str2 + " got: " + str);
        }
        return false;
    }
}
