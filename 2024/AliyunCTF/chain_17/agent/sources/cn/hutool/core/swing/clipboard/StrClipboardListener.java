package cn.hutool.core.swing.clipboard;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/swing/clipboard/StrClipboardListener.class */
public abstract class StrClipboardListener implements ClipboardListener, Serializable {
    private static final long serialVersionUID = 1;

    public abstract Transferable onChange(Clipboard clipboard, String str);

    @Override // cn.hutool.core.swing.clipboard.ClipboardListener
    public Transferable onChange(Clipboard clipboard, Transferable contents) {
        if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return onChange(clipboard, ClipboardUtil.getStr(contents));
        }
        return null;
    }
}
