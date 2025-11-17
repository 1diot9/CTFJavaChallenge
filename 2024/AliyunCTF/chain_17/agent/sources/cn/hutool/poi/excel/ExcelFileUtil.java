package cn.hutool.poi.excel;

import cn.hutool.core.io.IORuntimeException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.poifs.filesystem.FileMagic;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/ExcelFileUtil.class */
public class ExcelFileUtil {
    public static boolean isXls(InputStream in) {
        return FileMagic.OLE2 == getFileMagic(in);
    }

    public static boolean isXlsx(InputStream in) {
        return FileMagic.OOXML == getFileMagic(in);
    }

    public static boolean isXlsx(File file) {
        try {
            return FileMagic.valueOf(file) == FileMagic.OOXML;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    private static FileMagic getFileMagic(InputStream in) {
        try {
            FileMagic magic = FileMagic.valueOf(FileMagic.prepareToCheckMagic(in));
            return magic;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }
}
