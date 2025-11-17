package cn.hutool.core.io.file.visitor;

import cn.hutool.core.io.file.PathUtil;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/file/visitor/CopyVisitor.class */
public class CopyVisitor extends SimpleFileVisitor<Path> {
    private final Path source;
    private final Path target;
    private final CopyOption[] copyOptions;
    private boolean isTargetCreated;

    public CopyVisitor(Path source, Path target, CopyOption... copyOptions) {
        if (PathUtil.exists(target, false) && false == PathUtil.isDirectory(target)) {
            throw new IllegalArgumentException("Target must be a directory");
        }
        this.source = source;
        this.target = target;
        this.copyOptions = copyOptions;
    }

    @Override // java.nio.file.SimpleFileVisitor, java.nio.file.FileVisitor
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        initTargetDir();
        Path targetDir = resolveTarget(dir);
        try {
            Files.copy(dir, targetDir, this.copyOptions);
        } catch (FileAlreadyExistsException e) {
            if (false == Files.isDirectory(targetDir, new LinkOption[0])) {
                throw e;
            }
        }
        return FileVisitResult.CONTINUE;
    }

    @Override // java.nio.file.SimpleFileVisitor, java.nio.file.FileVisitor
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        initTargetDir();
        Files.copy(file, resolveTarget(file), this.copyOptions);
        return FileVisitResult.CONTINUE;
    }

    private Path resolveTarget(Path file) {
        return this.target.resolve(this.source.relativize(file));
    }

    private void initTargetDir() {
        if (false == this.isTargetCreated) {
            PathUtil.mkdir(this.target);
            this.isTargetCreated = true;
        }
    }
}
