package org.springframework.boot.system;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/system/ApplicationPid.class */
public class ApplicationPid {
    private static final PosixFilePermission[] WRITE_PERMISSIONS = {PosixFilePermission.OWNER_WRITE, PosixFilePermission.GROUP_WRITE, PosixFilePermission.OTHERS_WRITE};
    private final String pid;

    public ApplicationPid() {
        this.pid = getPid();
    }

    protected ApplicationPid(String pid) {
        this.pid = pid;
    }

    private String getPid() {
        try {
            return Long.toString(ProcessHandle.current().pid());
        } catch (Throwable th) {
            return null;
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ApplicationPid) {
            ApplicationPid other = (ApplicationPid) obj;
            return ObjectUtils.nullSafeEquals(this.pid, other.pid);
        }
        return false;
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.pid);
    }

    public String toString() {
        return this.pid != null ? this.pid : "???";
    }

    public void write(File file) throws IOException {
        Assert.state(this.pid != null, "No PID available");
        createParentDirectory(file);
        if (file.exists()) {
            assertCanOverwrite(file);
        }
        FileWriter writer = new FileWriter(file);
        try {
            writer.append((CharSequence) this.pid);
            writer.close();
        } catch (Throwable th) {
            try {
                writer.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    private void createParentDirectory(File file) {
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
    }

    private void assertCanOverwrite(File file) throws IOException {
        if (!file.canWrite() || !canWritePosixFile(file)) {
            throw new FileNotFoundException(file + " (permission denied)");
        }
    }

    private boolean canWritePosixFile(File file) throws IOException {
        try {
            Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(file.toPath(), new LinkOption[0]);
            for (PosixFilePermission permission : WRITE_PERMISSIONS) {
                if (permissions.contains(permission)) {
                    return true;
                }
            }
            return false;
        } catch (UnsupportedOperationException e) {
            return true;
        }
    }
}
