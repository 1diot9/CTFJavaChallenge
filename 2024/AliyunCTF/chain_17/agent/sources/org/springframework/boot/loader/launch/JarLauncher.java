package org.springframework.boot.loader.launch;

import org.springframework.boot.loader.launch.Archive;

/* loaded from: agent.jar:org/springframework/boot/loader/launch/JarLauncher.class */
public class JarLauncher extends ExecutableArchiveLauncher {
    public JarLauncher() throws Exception {
    }

    protected JarLauncher(Archive archive) throws Exception {
        super(archive);
    }

    @Override // org.springframework.boot.loader.launch.ExecutableArchiveLauncher
    protected boolean isIncludedOnClassPath(Archive.Entry entry) {
        return isLibraryFileOrClassesDirectory(entry);
    }

    @Override // org.springframework.boot.loader.launch.ExecutableArchiveLauncher
    protected String getEntryPathPrefix() {
        return "BOOT-INF/";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isLibraryFileOrClassesDirectory(Archive.Entry entry) {
        String name = entry.name();
        if (entry.isDirectory()) {
            return name.equals("BOOT-INF/classes/");
        }
        return name.startsWith("BOOT-INF/lib/");
    }

    public static void main(String[] args) throws Exception {
        new JarLauncher().launch(args);
    }
}
