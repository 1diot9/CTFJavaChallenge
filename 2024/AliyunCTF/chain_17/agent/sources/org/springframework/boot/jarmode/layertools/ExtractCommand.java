package org.springframework.boot.jarmode.layertools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.springframework.boot.jarmode.layertools.Command;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-jarmode-layertools-3.2.2.jar:org/springframework/boot/jarmode/layertools/ExtractCommand.class */
class ExtractCommand extends Command {
    static final Command.Option DESTINATION_OPTION = Command.Option.of("destination", "string", "The destination to extract files to");
    private final Context context;
    private final Layers layers;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ExtractCommand(Context context) {
        this(context, Layers.get(context));
    }

    ExtractCommand(Context context, Layers layers) {
        super("extract", "Extracts layers from the jar for image creation", Command.Options.of(DESTINATION_OPTION), Command.Parameters.of("[<layer>...]"));
        this.context = context;
        this.layers = layers;
    }

    @Override // org.springframework.boot.jarmode.layertools.Command
    protected void run(Map<Command.Option, String> options, List<String> parameters) {
        try {
            File destination = options.containsKey(DESTINATION_OPTION) ? new File(options.get(DESTINATION_OPTION)) : this.context.getWorkingDir();
            for (String layer : this.layers) {
                if (parameters.isEmpty() || parameters.contains(layer)) {
                    mkDirs(new File(destination, layer));
                }
            }
            ZipInputStream zip = new ZipInputStream(new FileInputStream(this.context.getArchiveFile()));
            try {
                ZipEntry entry = zip.getNextEntry();
                Assert.state(entry != null, "File '" + this.context.getArchiveFile().toString() + "' is not compatible with layertools; ensure jar file is valid and launch script is not enabled");
                while (entry != null) {
                    if (!entry.isDirectory()) {
                        String layer2 = this.layers.getLayer(entry);
                        if (parameters.isEmpty() || parameters.contains(layer2)) {
                            write(zip, entry, new File(destination, layer2));
                        }
                    }
                    entry = zip.getNextEntry();
                }
                zip.close();
            } finally {
            }
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void write(ZipInputStream zip, ZipEntry entry, File destination) throws IOException {
        String canonicalOutputPath = destination.getCanonicalPath() + File.separator;
        File file = new File(destination, entry.getName());
        String canonicalEntryPath = file.getCanonicalPath();
        Assert.state(canonicalEntryPath.startsWith(canonicalOutputPath), (Supplier<String>) () -> {
            return "Entry '" + entry.getName() + "' would be written to '" + canonicalEntryPath + "'. This is outside the output location of '" + canonicalOutputPath + "'. Verify the contents of your archive.";
        });
        mkParentDirs(file);
        OutputStream out = new FileOutputStream(file);
        try {
            StreamUtils.copy(zip, out);
            out.close();
            try {
                ((BasicFileAttributeView) Files.getFileAttributeView(file.toPath(), BasicFileAttributeView.class, new LinkOption[0])).setTimes(entry.getLastModifiedTime(), entry.getLastAccessTime(), entry.getCreationTime());
            } catch (IOException e) {
            }
        } catch (Throwable th) {
            try {
                out.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    private void mkParentDirs(File file) throws IOException {
        mkDirs(file.getParentFile());
    }

    private void mkDirs(File file) throws IOException {
        if (!file.exists() && !file.mkdirs()) {
            throw new IOException("Unable to create directory " + file);
        }
    }
}
