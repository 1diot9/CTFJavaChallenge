package org.springframework.boot.loader.launch;

import org.springframework.boot.loader.launch.Archive;

/* loaded from: server.jar:org/springframework/boot/loader/launch/WarLauncher.class */
public class WarLauncher extends ExecutableArchiveLauncher {
    public WarLauncher() throws Exception {
    }

    protected WarLauncher(Archive archive) throws Exception {
        super(archive);
    }

    @Override // org.springframework.boot.loader.launch.ExecutableArchiveLauncher
    public boolean isIncludedOnClassPath(Archive.Entry entry) {
        return isLibraryFileOrClassesDirectory(entry);
    }

    @Override // org.springframework.boot.loader.launch.ExecutableArchiveLauncher
    protected String getEntryPathPrefix() {
        return "WEB-INF/";
    }

    static boolean isLibraryFileOrClassesDirectory(Archive.Entry entry) {
        String name = entry.name();
        if (entry.isDirectory()) {
            return name.equals("WEB-INF/classes/");
        }
        return name.startsWith("WEB-INF/lib/") || name.startsWith("WEB-INF/lib-provided/");
    }

    public static void main(String[] args) throws Exception {
        new WarLauncher().launch(args);
    }
}
