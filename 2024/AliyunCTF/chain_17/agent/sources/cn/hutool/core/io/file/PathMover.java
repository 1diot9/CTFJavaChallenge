package cn.hutool.core.io.file;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.file.visitor.MoveVisitor;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjUtil;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/file/PathMover.class */
public class PathMover {
    private final Path src;
    private final Path target;
    private final CopyOption[] options;

    public static PathMover of(Path src, Path target, boolean isOverride) {
        return of(src, target, isOverride ? new CopyOption[]{StandardCopyOption.REPLACE_EXISTING} : new CopyOption[0]);
    }

    public static PathMover of(Path src, Path target, CopyOption[] options) {
        return new PathMover(src, target, options);
    }

    public PathMover(Path src, Path target, CopyOption[] options) {
        Assert.notNull(target, "Src path must be not null !", new Object[0]);
        if (false == PathUtil.exists(src, false)) {
            throw new IllegalArgumentException("Src path is not exist!");
        }
        this.src = src;
        this.target = (Path) Assert.notNull(target, "Target path must be not null !", new Object[0]);
        this.options = (CopyOption[]) ObjUtil.defaultIfNull(options, new CopyOption[0]);
    }

    public Path move() {
        Path src = this.src;
        Path target = this.target;
        CopyOption[] options = this.options;
        if (PathUtil.isDirectory(target)) {
            target = target.resolve(src.getFileName());
        }
        if (Files.exists(target, new LinkOption[0]) && PathUtil.equals(src, target)) {
            return target;
        }
        PathUtil.mkParentDirs(target);
        try {
            return Files.move(src, target, options);
        } catch (IOException e) {
            if (e instanceof FileAlreadyExistsException) {
                throw new IORuntimeException(e);
            }
            walkMove(src, target, options);
            PathUtil.del(src);
            return target;
        }
    }

    public Path moveContent() {
        Path src = this.src;
        if (PathUtil.isExistsAndNotDirectory(this.target, false)) {
            return move();
        }
        Path target = this.target;
        if (PathUtil.isExistsAndNotDirectory(target, false)) {
            throw new IllegalArgumentException("Can not move dir content to a file");
        }
        if (PathUtil.equals(src, target)) {
            return target;
        }
        CopyOption[] options = this.options;
        walkMove(src, target, options);
        return target;
    }

    private static void walkMove(Path src, Path target, CopyOption... options) {
        try {
            Files.walkFileTree(src, new MoveVisitor(src, target, options));
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }
}
