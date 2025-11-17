package cn.hutool.core.swing.clipboard;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/swing/clipboard/ClipboardListener.class */
public interface ClipboardListener {
    Transferable onChange(Clipboard clipboard, Transferable transferable);
}
