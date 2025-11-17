package cn.hutool.core.io;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.poi.excel.ExcelUtil;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;
import org.apache.tomcat.jni.SSL;
import org.springframework.http.MediaType;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/FileMagicNumber.class */
public enum FileMagicNumber {
    UNKNOWN(null, null) { // from class: cn.hutool.core.io.FileMagicNumber.1
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return false;
        }
    },
    JPEG("image/jpeg", ImgUtil.IMAGE_TYPE_JPG) { // from class: cn.hutool.core.io.FileMagicNumber.2
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 2 && Objects.equals(Byte.valueOf(bytes[0]), (byte) -1) && Objects.equals(Byte.valueOf(bytes[1]), (byte) -40) && Objects.equals(Byte.valueOf(bytes[2]), (byte) -1);
        }
    },
    JXR("image/vnd.ms-photo", "jxr") { // from class: cn.hutool.core.io.FileMagicNumber.3
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 2 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 73) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 73) && Objects.equals(Byte.valueOf(bytes[2]), (byte) -68);
        }
    },
    APNG("image/apng", "apng") { // from class: cn.hutool.core.io.FileMagicNumber.4
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            boolean b = bytes.length > 8 && Objects.equals(Byte.valueOf(bytes[0]), (byte) -119) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 80) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 78) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 71) && Objects.equals(Byte.valueOf(bytes[4]), (byte) 13) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 10) && Objects.equals(Byte.valueOf(bytes[6]), (byte) 26) && Objects.equals(Byte.valueOf(bytes[7]), (byte) 10);
            if (b) {
                int i = 8;
                while (i < bytes.length) {
                    try {
                        int dataLength = new BigInteger(1, Arrays.copyOfRange(bytes, i, i + 4)).intValue();
                        int i2 = i + 4;
                        byte[] bytes1 = Arrays.copyOfRange(bytes, i2, i2 + 4);
                        String chunkType = new String(bytes1);
                        int i3 = i2 + 4;
                        if (Objects.equals(chunkType, "IDAT") || Objects.equals(chunkType, "IEND")) {
                            return false;
                        }
                        if (Objects.equals(chunkType, "acTL")) {
                            return true;
                        }
                        i = i3 + dataLength + 4;
                    } catch (Exception e) {
                        return false;
                    }
                }
                return false;
            }
            return false;
        }
    },
    PNG("image/png", ImgUtil.IMAGE_TYPE_PNG) { // from class: cn.hutool.core.io.FileMagicNumber.5
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 3 && Objects.equals(Byte.valueOf(bytes[0]), (byte) -119) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 80) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 78) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 71);
        }
    },
    GIF("image/gif", ImgUtil.IMAGE_TYPE_GIF) { // from class: cn.hutool.core.io.FileMagicNumber.6
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 2 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 71) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 73) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 70);
        }
    },
    BMP("image/bmp", ImgUtil.IMAGE_TYPE_BMP) { // from class: cn.hutool.core.io.FileMagicNumber.7
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 1 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 66) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 77);
        }
    },
    TIFF("image/tiff", "tiff") { // from class: cn.hutool.core.io.FileMagicNumber.8
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            if (bytes.length < 4) {
                return false;
            }
            boolean flag1 = Objects.equals(Byte.valueOf(bytes[0]), (byte) 73) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 73) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 42) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 0);
            boolean flag2 = Objects.equals(Byte.valueOf(bytes[0]), (byte) 77) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 77) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 0) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 42);
            return flag1 || flag2;
        }
    },
    DWG("image/vnd.dwg", "dwg") { // from class: cn.hutool.core.io.FileMagicNumber.9
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 10 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 65) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 67) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 49) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 48);
        }
    },
    WEBP("image/webp", "webp") { // from class: cn.hutool.core.io.FileMagicNumber.10
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 11 && Objects.equals(Byte.valueOf(bytes[8]), (byte) 87) && Objects.equals(Byte.valueOf(bytes[9]), (byte) 69) && Objects.equals(Byte.valueOf(bytes[10]), (byte) 66) && Objects.equals(Byte.valueOf(bytes[11]), (byte) 80);
        }
    },
    PSD("image/vnd.adobe.photoshop", ImgUtil.IMAGE_TYPE_PSD) { // from class: cn.hutool.core.io.FileMagicNumber.11
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 3 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 56) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 66) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 80) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 83);
        }
    },
    ICO("image/x-icon", "ico") { // from class: cn.hutool.core.io.FileMagicNumber.12
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 3 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 0) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 0) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 1) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 0);
        }
    },
    XCF("image/x-xcf", "xcf") { // from class: cn.hutool.core.io.FileMagicNumber.13
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 9 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 103) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 105) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 109) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 112) && Objects.equals(Byte.valueOf(bytes[4]), (byte) 32) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 120) && Objects.equals(Byte.valueOf(bytes[6]), (byte) 99) && Objects.equals(Byte.valueOf(bytes[7]), (byte) 102) && Objects.equals(Byte.valueOf(bytes[8]), (byte) 32) && Objects.equals(Byte.valueOf(bytes[9]), (byte) 118);
        }
    },
    WAV("audio/x-wav", "wav") { // from class: cn.hutool.core.io.FileMagicNumber.14
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 11 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 82) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 73) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 70) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 70) && Objects.equals(Byte.valueOf(bytes[8]), (byte) 87) && Objects.equals(Byte.valueOf(bytes[9]), (byte) 65) && Objects.equals(Byte.valueOf(bytes[10]), (byte) 86) && Objects.equals(Byte.valueOf(bytes[11]), (byte) 69);
        }
    },
    MIDI("audio/midi", "midi") { // from class: cn.hutool.core.io.FileMagicNumber.15
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 3 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 77) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 84) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 104) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 100);
        }
    },
    MP3("audio/mpeg", "mp3") { // from class: cn.hutool.core.io.FileMagicNumber.16
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            if (bytes.length < 2) {
                return false;
            }
            boolean flag1 = Objects.equals(Byte.valueOf(bytes[0]), (byte) 73) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 68) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 51);
            boolean flag2 = Objects.equals(Byte.valueOf(bytes[0]), (byte) -1) && Objects.equals(Byte.valueOf(bytes[1]), (byte) -5);
            boolean flag3 = Objects.equals(Byte.valueOf(bytes[0]), (byte) -1) && Objects.equals(Byte.valueOf(bytes[1]), (byte) -13);
            boolean flag4 = Objects.equals(Byte.valueOf(bytes[0]), (byte) -1) && Objects.equals(Byte.valueOf(bytes[1]), (byte) -14);
            return flag1 || flag2 || flag3 || flag4;
        }
    },
    OGG("audio/ogg", "ogg") { // from class: cn.hutool.core.io.FileMagicNumber.17
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 3 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 79) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 103) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 103) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 83);
        }
    },
    FLAC("audio/x-flac", "flac") { // from class: cn.hutool.core.io.FileMagicNumber.18
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 3 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 102) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 76) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 97) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 67);
        }
    },
    M4A("audio/mp4", "m4a") { // from class: cn.hutool.core.io.FileMagicNumber.19
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return (bytes.length > 10 && Objects.equals(Byte.valueOf(bytes[4]), (byte) 102) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 116) && Objects.equals(Byte.valueOf(bytes[6]), (byte) 121) && Objects.equals(Byte.valueOf(bytes[7]), (byte) 112) && Objects.equals(Byte.valueOf(bytes[8]), (byte) 77) && Objects.equals(Byte.valueOf(bytes[9]), (byte) 52) && Objects.equals(Byte.valueOf(bytes[10]), (byte) 65)) || (Objects.equals(Byte.valueOf(bytes[0]), (byte) 77) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 52) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 65) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 32));
        }
    },
    AAC("audio/aac", "aac") { // from class: cn.hutool.core.io.FileMagicNumber.20
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            if (bytes.length < 1) {
                return false;
            }
            boolean flag1 = Objects.equals(Byte.valueOf(bytes[0]), (byte) -1) && Objects.equals(Byte.valueOf(bytes[1]), (byte) -15);
            boolean flag2 = Objects.equals(Byte.valueOf(bytes[0]), (byte) -1) && Objects.equals(Byte.valueOf(bytes[1]), (byte) -7);
            return flag1 || flag2;
        }
    },
    AMR("audio/amr", "amr") { // from class: cn.hutool.core.io.FileMagicNumber.21
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            if (bytes.length < 11) {
                return false;
            }
            boolean flag1 = Objects.equals(Byte.valueOf(bytes[0]), (byte) 35) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 33) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 65) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 77) && Objects.equals(Byte.valueOf(bytes[4]), (byte) 82) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 10);
            boolean flag2 = Objects.equals(Byte.valueOf(bytes[0]), (byte) 35) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 33) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 65) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 77) && Objects.equals(Byte.valueOf(bytes[4]), (byte) 82) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 95) && Objects.equals(Byte.valueOf(bytes[6]), (byte) 77) && Objects.equals(Byte.valueOf(bytes[7]), (byte) 67) && Objects.equals(Byte.valueOf(bytes[8]), (byte) 49) && Objects.equals(Byte.valueOf(bytes[9]), (byte) 46) && Objects.equals(Byte.valueOf(bytes[10]), (byte) 48) && Objects.equals(Byte.valueOf(bytes[11]), (byte) 10);
            return flag1 || flag2;
        }
    },
    AC3("audio/ac3", "ac3") { // from class: cn.hutool.core.io.FileMagicNumber.22
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 2 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 11) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 119);
        }
    },
    AIFF("audio/x-aiff", "aiff") { // from class: cn.hutool.core.io.FileMagicNumber.23
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 11 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 70) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 79) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 82) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 77) && Objects.equals(Byte.valueOf(bytes[8]), (byte) 65) && Objects.equals(Byte.valueOf(bytes[9]), (byte) 73) && Objects.equals(Byte.valueOf(bytes[10]), (byte) 70) && Objects.equals(Byte.valueOf(bytes[11]), (byte) 70);
        }
    },
    WOFF("font/woff", "woff") { // from class: cn.hutool.core.io.FileMagicNumber.24
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            if (bytes.length < 8) {
                return false;
            }
            boolean flag1 = Objects.equals(Byte.valueOf(bytes[0]), (byte) 119) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 79) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 70) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 70);
            boolean flag2 = Objects.equals(Byte.valueOf(bytes[4]), (byte) 0) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 1) && Objects.equals(Byte.valueOf(bytes[6]), (byte) 0) && Objects.equals(Byte.valueOf(bytes[7]), (byte) 0);
            boolean flag3 = Objects.equals(Byte.valueOf(bytes[4]), (byte) 79) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 84) && Objects.equals(Byte.valueOf(bytes[6]), (byte) 84) && Objects.equals(Byte.valueOf(bytes[7]), (byte) 79);
            boolean flag4 = Objects.equals(Byte.valueOf(bytes[4]), (byte) 116) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 114) && Objects.equals(Byte.valueOf(bytes[6]), (byte) 117) && Objects.equals(Byte.valueOf(bytes[7]), (byte) 101);
            return flag1 && (flag2 || flag3 || flag4);
        }
    },
    WOFF2("font/woff2", "woff2") { // from class: cn.hutool.core.io.FileMagicNumber.25
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            if (bytes.length < 8) {
                return false;
            }
            boolean flag1 = Objects.equals(Byte.valueOf(bytes[0]), (byte) 119) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 79) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 70) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 50);
            boolean flag2 = Objects.equals(Byte.valueOf(bytes[4]), (byte) 0) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 1) && Objects.equals(Byte.valueOf(bytes[6]), (byte) 0) && Objects.equals(Byte.valueOf(bytes[7]), (byte) 0);
            boolean flag3 = Objects.equals(Byte.valueOf(bytes[4]), (byte) 79) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 84) && Objects.equals(Byte.valueOf(bytes[6]), (byte) 84) && Objects.equals(Byte.valueOf(bytes[7]), (byte) 79);
            boolean flag4 = Objects.equals(Byte.valueOf(bytes[4]), (byte) 116) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 114) && Objects.equals(Byte.valueOf(bytes[6]), (byte) 117) && Objects.equals(Byte.valueOf(bytes[7]), (byte) 101);
            return flag1 && (flag2 || flag3 || flag4);
        }
    },
    TTF("font/ttf", "ttf") { // from class: cn.hutool.core.io.FileMagicNumber.26
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 4 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 0) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 1) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 0) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 0) && Objects.equals(Byte.valueOf(bytes[4]), (byte) 0);
        }
    },
    OTF("font/otf", "otf") { // from class: cn.hutool.core.io.FileMagicNumber.27
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 4 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 79) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 84) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 84) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 79) && Objects.equals(Byte.valueOf(bytes[4]), (byte) 0);
        }
    },
    EPUB("application/epub+zip", "epub") { // from class: cn.hutool.core.io.FileMagicNumber.28
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 58 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 80) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 75) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 3) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 4) && Objects.equals(Byte.valueOf(bytes[30]), (byte) 109) && Objects.equals(Byte.valueOf(bytes[31]), (byte) 105) && Objects.equals(Byte.valueOf(bytes[32]), (byte) 109) && Objects.equals(Byte.valueOf(bytes[33]), (byte) 101) && Objects.equals(Byte.valueOf(bytes[34]), (byte) 116) && Objects.equals(Byte.valueOf(bytes[35]), (byte) 121) && Objects.equals(Byte.valueOf(bytes[36]), (byte) 112) && Objects.equals(Byte.valueOf(bytes[37]), (byte) 101) && Objects.equals(Byte.valueOf(bytes[38]), (byte) 97) && Objects.equals(Byte.valueOf(bytes[39]), (byte) 112) && Objects.equals(Byte.valueOf(bytes[40]), (byte) 112) && Objects.equals(Byte.valueOf(bytes[41]), (byte) 108) && Objects.equals(Byte.valueOf(bytes[42]), (byte) 105) && Objects.equals(Byte.valueOf(bytes[43]), (byte) 99) && Objects.equals(Byte.valueOf(bytes[44]), (byte) 97) && Objects.equals(Byte.valueOf(bytes[45]), (byte) 116) && Objects.equals(Byte.valueOf(bytes[46]), (byte) 105) && Objects.equals(Byte.valueOf(bytes[47]), (byte) 111) && Objects.equals(Byte.valueOf(bytes[48]), (byte) 110) && Objects.equals(Byte.valueOf(bytes[49]), (byte) 47) && Objects.equals(Byte.valueOf(bytes[50]), (byte) 101) && Objects.equals(Byte.valueOf(bytes[51]), (byte) 112) && Objects.equals(Byte.valueOf(bytes[52]), (byte) 117) && Objects.equals(Byte.valueOf(bytes[53]), (byte) 98) && Objects.equals(Byte.valueOf(bytes[54]), (byte) 43) && Objects.equals(Byte.valueOf(bytes[55]), (byte) 122) && Objects.equals(Byte.valueOf(bytes[56]), (byte) 105) && Objects.equals(Byte.valueOf(bytes[57]), (byte) 112);
        }
    },
    ZIP("application/zip", "zip") { // from class: cn.hutool.core.io.FileMagicNumber.29
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            if (bytes.length < 4) {
                return false;
            }
            boolean flag1 = Objects.equals(Byte.valueOf(bytes[0]), (byte) 80) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 75);
            boolean flag2 = Objects.equals(Byte.valueOf(bytes[2]), (byte) 3) || Objects.equals(Byte.valueOf(bytes[2]), (byte) 5) || Objects.equals(Byte.valueOf(bytes[2]), (byte) 7);
            boolean flag3 = Objects.equals(Byte.valueOf(bytes[3]), (byte) 4) || Objects.equals(Byte.valueOf(bytes[3]), (byte) 6) || Objects.equals(Byte.valueOf(bytes[3]), (byte) 8);
            return flag1 && flag2 && flag3;
        }
    },
    TAR("application/x-tar", "tar") { // from class: cn.hutool.core.io.FileMagicNumber.30
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 261 && Objects.equals(Byte.valueOf(bytes[257]), (byte) 117) && Objects.equals(Byte.valueOf(bytes[258]), (byte) 115) && Objects.equals(Byte.valueOf(bytes[259]), (byte) 116) && Objects.equals(Byte.valueOf(bytes[260]), (byte) 97) && Objects.equals(Byte.valueOf(bytes[261]), (byte) 114);
        }
    },
    RAR("application/x-rar-compressed", "rar") { // from class: cn.hutool.core.io.FileMagicNumber.31
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 6 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 82) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 97) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 114) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 33) && Objects.equals(Byte.valueOf(bytes[4]), (byte) 26) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 7) && (Objects.equals(Byte.valueOf(bytes[6]), (byte) 0) || Objects.equals(Byte.valueOf(bytes[6]), (byte) 1));
        }
    },
    GZ("application/gzip", "gz") { // from class: cn.hutool.core.io.FileMagicNumber.32
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 2 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 31) && Objects.equals(Byte.valueOf(bytes[1]), (byte) -117) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 8);
        }
    },
    BZ2("application/x-bzip2", "bz2") { // from class: cn.hutool.core.io.FileMagicNumber.33
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 2 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 66) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 90) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 104);
        }
    },
    SevenZ("application/x-7z-compressed", "7z") { // from class: cn.hutool.core.io.FileMagicNumber.34
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 6 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 55) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 122) && Objects.equals(Byte.valueOf(bytes[2]), (byte) -68) && Objects.equals(Byte.valueOf(bytes[3]), (byte) -81) && Objects.equals(Byte.valueOf(bytes[4]), (byte) 39) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 28) && Objects.equals(Byte.valueOf(bytes[6]), (byte) 0);
        }
    },
    PDF(MediaType.APPLICATION_PDF_VALUE, "pdf") { // from class: cn.hutool.core.io.FileMagicNumber.35
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            if (bytes.length > 3 && Objects.equals(Byte.valueOf(bytes[0]), (byte) -17) && Objects.equals(Byte.valueOf(bytes[1]), (byte) -69) && Objects.equals(Byte.valueOf(bytes[2]), (byte) -65)) {
                bytes = Arrays.copyOfRange(bytes, 3, bytes.length);
            }
            return bytes.length > 3 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 37) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 80) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 68) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 70);
        }
    },
    EXE("application/x-msdownload", "exe") { // from class: cn.hutool.core.io.FileMagicNumber.36
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 1 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 77) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 90);
        }
    },
    SWF("application/x-shockwave-flash", "swf") { // from class: cn.hutool.core.io.FileMagicNumber.37
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 2 && (Objects.equals(Byte.valueOf(bytes[0]), 67) || Objects.equals(Byte.valueOf(bytes[0]), (byte) 70)) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 87) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 83);
        }
    },
    RTF("application/rtf", "rtf") { // from class: cn.hutool.core.io.FileMagicNumber.38
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 4 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 123) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 92) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 114) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 116) && Objects.equals(Byte.valueOf(bytes[4]), (byte) 102);
        }
    },
    NES("application/x-nintendo-nes-rom", "nes") { // from class: cn.hutool.core.io.FileMagicNumber.39
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 3 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 78) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 69) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 83) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 26);
        }
    },
    CRX("application/x-google-chrome-extension", "crx") { // from class: cn.hutool.core.io.FileMagicNumber.40
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 3 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 67) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 114) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 50) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 52);
        }
    },
    CAB("application/vnd.ms-cab-compressed", "cab") { // from class: cn.hutool.core.io.FileMagicNumber.41
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            if (bytes.length < 4) {
                return false;
            }
            boolean flag1 = Objects.equals(Byte.valueOf(bytes[0]), (byte) 77) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 83) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 67) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 70);
            boolean flag2 = Objects.equals(Byte.valueOf(bytes[0]), (byte) 73) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 83) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 99) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 40);
            return flag1 || flag2;
        }
    },
    PS("application/postscript", "ps") { // from class: cn.hutool.core.io.FileMagicNumber.42
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 1 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 37) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 33);
        }
    },
    XZ("application/x-xz", "xz") { // from class: cn.hutool.core.io.FileMagicNumber.43
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 5 && Objects.equals(Byte.valueOf(bytes[0]), (byte) -3) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 55) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 122) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 88) && Objects.equals(Byte.valueOf(bytes[4]), (byte) 90) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 0);
        }
    },
    SQLITE("application/x-sqlite3", "sqlite") { // from class: cn.hutool.core.io.FileMagicNumber.44
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 15 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 83) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 81) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 76) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 105) && Objects.equals(Byte.valueOf(bytes[4]), (byte) 116) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 101) && Objects.equals(Byte.valueOf(bytes[6]), (byte) 32) && Objects.equals(Byte.valueOf(bytes[7]), (byte) 102) && Objects.equals(Byte.valueOf(bytes[8]), (byte) 111) && Objects.equals(Byte.valueOf(bytes[9]), (byte) 114) && Objects.equals(Byte.valueOf(bytes[10]), (byte) 109) && Objects.equals(Byte.valueOf(bytes[11]), (byte) 97) && Objects.equals(Byte.valueOf(bytes[12]), (byte) 116) && Objects.equals(Byte.valueOf(bytes[13]), (byte) 32) && Objects.equals(Byte.valueOf(bytes[14]), (byte) 51) && Objects.equals(Byte.valueOf(bytes[15]), (byte) 0);
        }
    },
    DEB("application/x-deb", "deb") { // from class: cn.hutool.core.io.FileMagicNumber.45
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 20 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 33) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 60) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 97) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 114) && Objects.equals(Byte.valueOf(bytes[4]), (byte) 99) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 104) && Objects.equals(Byte.valueOf(bytes[6]), (byte) 62) && Objects.equals(Byte.valueOf(bytes[7]), (byte) 10) && Objects.equals(Byte.valueOf(bytes[8]), (byte) 100) && Objects.equals(Byte.valueOf(bytes[9]), (byte) 101) && Objects.equals(Byte.valueOf(bytes[10]), (byte) 98) && Objects.equals(Byte.valueOf(bytes[11]), (byte) 105) && Objects.equals(Byte.valueOf(bytes[12]), (byte) 97) && Objects.equals(Byte.valueOf(bytes[13]), (byte) 110) && Objects.equals(Byte.valueOf(bytes[14]), (byte) 45) && Objects.equals(Byte.valueOf(bytes[15]), (byte) 98) && Objects.equals(Byte.valueOf(bytes[16]), (byte) 105) && Objects.equals(Byte.valueOf(bytes[17]), (byte) 110) && Objects.equals(Byte.valueOf(bytes[18]), (byte) 97) && Objects.equals(Byte.valueOf(bytes[19]), (byte) 114) && Objects.equals(Byte.valueOf(bytes[20]), (byte) 121);
        }
    },
    AR("application/x-unix-archive", "ar") { // from class: cn.hutool.core.io.FileMagicNumber.46
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 6 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 33) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 60) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 97) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 114) && Objects.equals(Byte.valueOf(bytes[4]), (byte) 99) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 104) && Objects.equals(Byte.valueOf(bytes[6]), (byte) 62);
        }
    },
    LZOP("application/x-lzop", "lzo") { // from class: cn.hutool.core.io.FileMagicNumber.47
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 7 && Objects.equals(Byte.valueOf(bytes[0]), (byte) -119) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 76) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 90) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 79) && Objects.equals(Byte.valueOf(bytes[4]), (byte) 0) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 13) && Objects.equals(Byte.valueOf(bytes[6]), (byte) 10) && Objects.equals(Byte.valueOf(bytes[7]), (byte) 26);
        }
    },
    LZ("application/x-lzip", "lz") { // from class: cn.hutool.core.io.FileMagicNumber.48
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 3 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 76) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 90) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 73) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 80);
        }
    },
    ELF("application/x-executable", "elf") { // from class: cn.hutool.core.io.FileMagicNumber.49
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 52 && Objects.equals(Byte.valueOf(bytes[0]), Byte.MAX_VALUE) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 69) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 76) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 70);
        }
    },
    LZ4("application/x-lz4", "lz4") { // from class: cn.hutool.core.io.FileMagicNumber.50
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 4 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 4) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 34) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 77) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 24);
        }
    },
    BR("application/x-brotli", "br") { // from class: cn.hutool.core.io.FileMagicNumber.51
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 3 && Objects.equals(Byte.valueOf(bytes[0]), (byte) -50) && Objects.equals(Byte.valueOf(bytes[1]), (byte) -78) && Objects.equals(Byte.valueOf(bytes[2]), (byte) -49) && Objects.equals(Byte.valueOf(bytes[3]), (byte) -127);
        }
    },
    DCM("application/x-dicom", "dcm") { // from class: cn.hutool.core.io.FileMagicNumber.52
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 128 && Objects.equals(Byte.valueOf(bytes[128]), (byte) 68) && Objects.equals(Byte.valueOf(bytes[129]), (byte) 73) && Objects.equals(Byte.valueOf(bytes[130]), (byte) 67) && Objects.equals(Byte.valueOf(bytes[131]), (byte) 77);
        }
    },
    RPM("application/x-rpm", "rpm") { // from class: cn.hutool.core.io.FileMagicNumber.53
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 4 && Objects.equals(Byte.valueOf(bytes[0]), (byte) -19) && Objects.equals(Byte.valueOf(bytes[1]), (byte) -85) && Objects.equals(Byte.valueOf(bytes[2]), (byte) -18) && Objects.equals(Byte.valueOf(bytes[3]), (byte) -37);
        }
    },
    ZSTD("application/x-zstd", "zst") { // from class: cn.hutool.core.io.FileMagicNumber.54
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            int length = bytes.length;
            if (length < 5) {
                return false;
            }
            byte[] buf1 = {34, 35, 36, 37, 38, 39, 40};
            boolean flag1 = ArrayUtil.contains(buf1, bytes[0]) && Objects.equals(Byte.valueOf(bytes[1]), (byte) -75) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 47) && Objects.equals(Byte.valueOf(bytes[3]), (byte) -3);
            if (flag1) {
                return true;
            }
            return (bytes[0] & 240) == 80 && bytes[1] == 42 && bytes[2] == 77 && bytes[3] == 24;
        }
    },
    MP4("video/mp4", "mp4") { // from class: cn.hutool.core.io.FileMagicNumber.55
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            if (bytes.length < 13) {
                return false;
            }
            boolean flag1 = Objects.equals(Byte.valueOf(bytes[4]), (byte) 102) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 116) && Objects.equals(Byte.valueOf(bytes[6]), (byte) 121) && Objects.equals(Byte.valueOf(bytes[7]), (byte) 112) && Objects.equals(Byte.valueOf(bytes[8]), (byte) 77) && Objects.equals(Byte.valueOf(bytes[9]), (byte) 83) && Objects.equals(Byte.valueOf(bytes[10]), (byte) 78) && Objects.equals(Byte.valueOf(bytes[11]), (byte) 86);
            boolean flag2 = Objects.equals(Byte.valueOf(bytes[4]), (byte) 102) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 116) && Objects.equals(Byte.valueOf(bytes[6]), (byte) 121) && Objects.equals(Byte.valueOf(bytes[7]), (byte) 112) && Objects.equals(Byte.valueOf(bytes[8]), (byte) 105) && Objects.equals(Byte.valueOf(bytes[9]), (byte) 115) && Objects.equals(Byte.valueOf(bytes[10]), (byte) 111) && Objects.equals(Byte.valueOf(bytes[11]), (byte) 109);
            return flag1 || flag2;
        }
    },
    AVI("video/x-msvideo", "avi") { // from class: cn.hutool.core.io.FileMagicNumber.56
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 11 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 82) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 73) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 70) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 70) && Objects.equals(Byte.valueOf(bytes[8]), (byte) 65) && Objects.equals(Byte.valueOf(bytes[9]), (byte) 86) && Objects.equals(Byte.valueOf(bytes[10]), (byte) 73) && Objects.equals(Byte.valueOf(bytes[11]), (byte) 32);
        }
    },
    WMV("video/x-ms-wmv", "wmv") { // from class: cn.hutool.core.io.FileMagicNumber.57
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 9 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 48) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 38) && Objects.equals(Byte.valueOf(bytes[2]), (byte) -78) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 117) && Objects.equals(Byte.valueOf(bytes[4]), (byte) -114) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 102) && Objects.equals(Byte.valueOf(bytes[6]), (byte) -49) && Objects.equals(Byte.valueOf(bytes[7]), (byte) 17) && Objects.equals(Byte.valueOf(bytes[8]), (byte) -90) && Objects.equals(Byte.valueOf(bytes[9]), (byte) -39);
        }
    },
    M4V("video/x-m4v", "m4v") { // from class: cn.hutool.core.io.FileMagicNumber.58
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            if (bytes.length < 12) {
                return false;
            }
            boolean flag1 = Objects.equals(Byte.valueOf(bytes[4]), (byte) 102) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 116) && Objects.equals(Byte.valueOf(bytes[6]), (byte) 121) && Objects.equals(Byte.valueOf(bytes[7]), (byte) 112) && Objects.equals(Byte.valueOf(bytes[8]), (byte) 77) && Objects.equals(Byte.valueOf(bytes[9]), (byte) 52) && Objects.equals(Byte.valueOf(bytes[10]), (byte) 86) && Objects.equals(Byte.valueOf(bytes[11]), (byte) 32);
            boolean flag2 = Objects.equals(Byte.valueOf(bytes[4]), (byte) 102) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 116) && Objects.equals(Byte.valueOf(bytes[6]), (byte) 121) && Objects.equals(Byte.valueOf(bytes[7]), (byte) 112) && Objects.equals(Byte.valueOf(bytes[8]), (byte) 109) && Objects.equals(Byte.valueOf(bytes[9]), (byte) 112) && Objects.equals(Byte.valueOf(bytes[10]), (byte) 52) && Objects.equals(Byte.valueOf(bytes[11]), (byte) 50);
            return flag1 || flag2;
        }
    },
    FLV("video/x-flv", "flv") { // from class: cn.hutool.core.io.FileMagicNumber.59
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 3 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 70) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 76) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 86) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 1);
        }
    },
    MKV("video/x-matroska", "mkv") { // from class: cn.hutool.core.io.FileMagicNumber.60
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            boolean flag1 = bytes.length > 11 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 26) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 69) && Objects.equals(Byte.valueOf(bytes[2]), (byte) -33) && Objects.equals(Byte.valueOf(bytes[3]), (byte) -93);
            if (flag1) {
                byte[] bytes1 = {66, -126, -120, 109, 97, 116, 114, 111, 115, 107, 97};
                int index = FileMagicNumber.indexOf(bytes, bytes1);
                return index > 0;
            }
            return false;
        }
    },
    WEBM("video/webm", "webm") { // from class: cn.hutool.core.io.FileMagicNumber.61
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            boolean flag1 = bytes.length > 8 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 26) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 69) && Objects.equals(Byte.valueOf(bytes[2]), (byte) -33) && Objects.equals(Byte.valueOf(bytes[3]), (byte) -93);
            if (flag1) {
                byte[] bytes1 = {66, -126, -120, 119, 101, 98, 109};
                int index = FileMagicNumber.indexOf(bytes, bytes1);
                return index > 0;
            }
            return false;
        }
    },
    MOV("video/quicktime", "mov") { // from class: cn.hutool.core.io.FileMagicNumber.62
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            if (bytes.length < 12) {
                return false;
            }
            boolean flag1 = Objects.equals(Byte.valueOf(bytes[4]), (byte) 102) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 116) && Objects.equals(Byte.valueOf(bytes[6]), (byte) 121) && Objects.equals(Byte.valueOf(bytes[7]), (byte) 112) && Objects.equals(Byte.valueOf(bytes[8]), (byte) 113) && Objects.equals(Byte.valueOf(bytes[9]), (byte) 116) && Objects.equals(Byte.valueOf(bytes[10]), (byte) 32) && Objects.equals(Byte.valueOf(bytes[11]), (byte) 32);
            boolean flag2 = Objects.equals(Byte.valueOf(bytes[4]), (byte) 109) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 111) && Objects.equals(Byte.valueOf(bytes[6]), (byte) 111) && Objects.equals(Byte.valueOf(bytes[7]), (byte) 118);
            boolean flag3 = Objects.equals(Byte.valueOf(bytes[4]), (byte) 102) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 114) && Objects.equals(Byte.valueOf(bytes[6]), (byte) 101) && Objects.equals(Byte.valueOf(bytes[7]), (byte) 101);
            boolean flag4 = Objects.equals(Byte.valueOf(bytes[4]), (byte) 109) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 100) && Objects.equals(Byte.valueOf(bytes[6]), (byte) 97) && Objects.equals(Byte.valueOf(bytes[7]), (byte) 116);
            boolean flag5 = Objects.equals(Byte.valueOf(bytes[4]), (byte) 119) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 105) && Objects.equals(Byte.valueOf(bytes[6]), (byte) 100) && Objects.equals(Byte.valueOf(bytes[7]), (byte) 101);
            boolean flag6 = Objects.equals(Byte.valueOf(bytes[4]), (byte) 112) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 110) && Objects.equals(Byte.valueOf(bytes[6]), (byte) 111) && Objects.equals(Byte.valueOf(bytes[7]), (byte) 116);
            boolean flag7 = Objects.equals(Byte.valueOf(bytes[4]), (byte) 115) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 107) && Objects.equals(Byte.valueOf(bytes[6]), (byte) 105) && Objects.equals(Byte.valueOf(bytes[7]), (byte) 112);
            return flag1 || flag2 || flag3 || flag4 || flag5 || flag6 || flag7;
        }
    },
    MPEG("video/mpeg", "mpg") { // from class: cn.hutool.core.io.FileMagicNumber.63
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 3 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 0) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 0) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 1) && bytes[3] >= -80 && bytes[3] <= -65;
        }
    },
    RMVB("video/vnd.rn-realvideo", "rmvb") { // from class: cn.hutool.core.io.FileMagicNumber.64
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 4 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 46) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 82) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 77) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 70);
        }
    },
    M3GP("video/3gpp", "3gp") { // from class: cn.hutool.core.io.FileMagicNumber.65
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 10 && Objects.equals(Byte.valueOf(bytes[4]), (byte) 102) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 116) && Objects.equals(Byte.valueOf(bytes[6]), (byte) 121) && Objects.equals(Byte.valueOf(bytes[7]), (byte) 112) && Objects.equals(Byte.valueOf(bytes[8]), (byte) 51) && Objects.equals(Byte.valueOf(bytes[9]), (byte) 103) && Objects.equals(Byte.valueOf(bytes[10]), (byte) 112);
        }
    },
    DOC("application/msword", "doc") { // from class: cn.hutool.core.io.FileMagicNumber.66
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            byte[] byte1 = {-48, -49, 17, -32, -95, -79, 26, -31};
            boolean flag1 = bytes.length > 515 && Arrays.equals(Arrays.copyOfRange(bytes, 0, 8), byte1);
            if (flag1) {
                byte[] byte2 = {-20, -91, -63, 0};
                boolean flag2 = Arrays.equals(Arrays.copyOfRange(bytes, 512, SSL.SSL_INFO_SERVER_V_END), byte2);
                byte[] byte3 = {0, 10, 0, 0, 0, 77, 83, 87, 111, 114, 100, 68, 111, 99, 0, 16, 0, 0, 0, 87, 111, 114, 100, 46, 68, 111, 99, 117, 109, 101, 110, 116, 46, 56, 0, -12, 57, -78, 113};
                byte[] range = Arrays.copyOfRange(bytes, 2075, 2142);
                boolean flag3 = bytes.length > 2142 && FileMagicNumber.indexOf(range, byte3) > 0;
                return flag2 || flag3;
            }
            return false;
        }
    },
    XLS(ExcelUtil.XLS_CONTENT_TYPE, "xls") { // from class: cn.hutool.core.io.FileMagicNumber.67
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            byte[] byte1 = {-48, -49, 17, -32, -95, -79, 26, -31};
            boolean flag1 = bytes.length > 520 && Arrays.equals(Arrays.copyOfRange(bytes, 0, 8), byte1);
            if (flag1) {
                byte[] byte2 = {-3, -1, -1, -1};
                boolean flag2 = Arrays.equals(Arrays.copyOfRange(bytes, 512, SSL.SSL_INFO_SERVER_V_END), byte2) && (bytes[518] == 0 || bytes[518] == 2);
                byte[] byte3 = {9, 8, 16, 0, 0, 6, 5, 0};
                boolean flag3 = Arrays.equals(Arrays.copyOfRange(bytes, 512, 520), byte3);
                byte[] byte4 = {-30, 0, 0, 0, 92, 0, 112, 0, 4, 0, 0, 67, 97, 108, 99};
                boolean flag4 = bytes.length > 2095 && Arrays.equals(Arrays.copyOfRange(bytes, 1568, 2095), byte4);
                return flag2 || flag3 || flag4;
            }
            return false;
        }
    },
    PPT("application/vnd.ms-powerpoint", "ppt") { // from class: cn.hutool.core.io.FileMagicNumber.68
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            byte[] byte1 = {-48, -49, 17, -32, -95, -79, 26, -31};
            boolean flag1 = bytes.length > 524 && Arrays.equals(Arrays.copyOfRange(bytes, 0, 8), byte1);
            if (flag1) {
                byte[] byte2 = {-96, 70, 29, -16};
                byte[] byteRange = Arrays.copyOfRange(bytes, 512, SSL.SSL_INFO_SERVER_V_END);
                boolean flag2 = Arrays.equals(byteRange, byte2);
                byte[] byte3 = {0, 110, 30, -16};
                boolean flag3 = Arrays.equals(byteRange, byte3);
                byte[] byte4 = {15, 0, -24, 3};
                boolean flag4 = Arrays.equals(byteRange, byte4);
                byte[] byte5 = {-3, -1, -1, -1};
                boolean flag5 = Arrays.equals(byteRange, byte5) && bytes[522] == 0 && bytes[523] == 0;
                byte[] byte6 = {0, -71, 41, -24, 17, 0, 0, 0, 77, 83, 32, 80, 111, 119, 101, 114, 80, 111, 105, 110, 116, 32, 57, 55};
                boolean flag6 = bytes.length > 2096 && Arrays.equals(Arrays.copyOfRange(bytes, 2072, 2096), byte6);
                return flag2 || flag3 || flag4 || flag5 || flag6;
            }
            return false;
        }
    },
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx") { // from class: cn.hutool.core.io.FileMagicNumber.69
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return Objects.equals(FileMagicNumber.matchDocument(bytes), DOCX);
        }
    },
    PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation", "pptx") { // from class: cn.hutool.core.io.FileMagicNumber.70
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return Objects.equals(FileMagicNumber.matchDocument(bytes), PPTX);
        }
    },
    XLSX(ExcelUtil.XLSX_CONTENT_TYPE, "xlsx") { // from class: cn.hutool.core.io.FileMagicNumber.71
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return Objects.equals(FileMagicNumber.matchDocument(bytes), XLSX);
        }
    },
    WASM("application/wasm", "wasm") { // from class: cn.hutool.core.io.FileMagicNumber.72
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 7 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 0) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 97) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 115) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 109) && Objects.equals(Byte.valueOf(bytes[4]), (byte) 1) && Objects.equals(Byte.valueOf(bytes[5]), (byte) 0) && Objects.equals(Byte.valueOf(bytes[6]), (byte) 0) && Objects.equals(Byte.valueOf(bytes[7]), (byte) 0);
        }
    },
    DEX("application/vnd.android.dex", "dex") { // from class: cn.hutool.core.io.FileMagicNumber.73
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 36 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 100) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 101) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 120) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 10) && Objects.equals(Byte.valueOf(bytes[36]), (byte) 112);
        }
    },
    DEY("application/vnd.android.dey", "dey") { // from class: cn.hutool.core.io.FileMagicNumber.74
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            return bytes.length > 100 && Objects.equals(Byte.valueOf(bytes[0]), (byte) 100) && Objects.equals(Byte.valueOf(bytes[1]), (byte) 101) && Objects.equals(Byte.valueOf(bytes[2]), (byte) 121) && Objects.equals(Byte.valueOf(bytes[3]), (byte) 10) && DEX.match(Arrays.copyOfRange(bytes, 40, 100));
        }
    },
    EML("message/rfc822", "eml") { // from class: cn.hutool.core.io.FileMagicNumber.75
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            if (bytes.length < 8) {
                return false;
            }
            byte[] byte1 = {70, 114, 111, 109, 32, 32, 32};
            byte[] byte2 = {70, 114, 111, 109, 32, 63, 63, 63};
            byte[] byte3 = {70, 114, 111, 109, 58, 32};
            byte[] byte4 = {82, 101, 116, 117, 114, 110, 45, 80, 97, 116, 104, 58, 32};
            return Arrays.equals(Arrays.copyOfRange(bytes, 0, 7), byte1) || Arrays.equals(Arrays.copyOfRange(bytes, 0, 8), byte2) || Arrays.equals(Arrays.copyOfRange(bytes, 0, 6), byte3) || (bytes.length > 13 && Arrays.equals(Arrays.copyOfRange(bytes, 0, 13), byte4));
        }
    },
    MDB("application/vnd.ms-access", "mdb") { // from class: cn.hutool.core.io.FileMagicNumber.76
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            byte[] byte1 = {0, 1, 0, 0, 83, 116, 97, 110, 100, 97, 114, 100, 32, 74, 101, 116, 32, 68, 66};
            return bytes.length > 18 && Arrays.equals(Arrays.copyOfRange(bytes, 0, 18), byte1);
        }
    },
    CHM("application/vnd.ms-htmlhelp", "chm") { // from class: cn.hutool.core.io.FileMagicNumber.77
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            byte[] byte1 = {73, 84, 83, 70};
            return bytes.length > 4 && Arrays.equals(Arrays.copyOfRange(bytes, 0, 4), byte1);
        }
    },
    CLASS("application/java-vm", "class") { // from class: cn.hutool.core.io.FileMagicNumber.78
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            byte[] byte1 = {-54, -2, -70, -66};
            return bytes.length > 4 && Arrays.equals(Arrays.copyOfRange(bytes, 0, 4), byte1);
        }
    },
    TORRENT("application/x-bittorrent", "torrent") { // from class: cn.hutool.core.io.FileMagicNumber.79
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            byte[] byte1 = {100, 56, 58, 97, 110, 110, 111, 117, 110, 99, 101};
            return bytes.length > 11 && Arrays.equals(Arrays.copyOfRange(bytes, 0, 11), byte1);
        }
    },
    WPD("application/vnd.wordperfect", "wpd") { // from class: cn.hutool.core.io.FileMagicNumber.80
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            byte[] byte1 = {-1, 87, 80, 67};
            return bytes.length > 4 && Arrays.equals(Arrays.copyOfRange(bytes, 0, 4), byte1);
        }
    },
    DBX("", "dbx") { // from class: cn.hutool.core.io.FileMagicNumber.81
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            byte[] byte1 = {-49, -83, 18, -2};
            return bytes.length > 4 && Arrays.equals(Arrays.copyOfRange(bytes, 0, 4), byte1);
        }
    },
    PST("application/vnd.ms-outlook-pst", "pst") { // from class: cn.hutool.core.io.FileMagicNumber.82
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            byte[] byte1 = {33, 66, 68, 78};
            return bytes.length > 4 && Arrays.equals(Arrays.copyOfRange(bytes, 0, 4), byte1);
        }
    },
    RAM("audio/x-pn-realaudio", "ram") { // from class: cn.hutool.core.io.FileMagicNumber.83
        @Override // cn.hutool.core.io.FileMagicNumber
        public boolean match(byte[] bytes) {
            byte[] byte1 = {46, 114, 97, -3, 0};
            return bytes.length > 5 && Arrays.equals(Arrays.copyOfRange(bytes, 0, 5), byte1);
        }
    };

    private final String mimeType;
    private final String extension;

    public abstract boolean match(byte[] bArr);

    FileMagicNumber(String mimeType, String extension) {
        this.mimeType = mimeType;
        this.extension = extension;
    }

    public static FileMagicNumber getMagicNumber(byte[] bytes) {
        FileMagicNumber number = (FileMagicNumber) Arrays.stream(values()).filter(fileMagicNumber -> {
            return fileMagicNumber.match(bytes);
        }).findFirst().orElse(UNKNOWN);
        if (number.equals(ZIP)) {
            FileMagicNumber fn = matchDocument(bytes);
            return fn == UNKNOWN ? ZIP : fn;
        }
        return number;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public String getExtension() {
        return this.extension;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0043, code lost:            r6 = r6 + 1;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static int indexOf(byte[] r4, byte[] r5) {
        /*
            r0 = r4
            if (r0 == 0) goto Lf
            r0 = r5
            if (r0 == 0) goto Lf
            r0 = r4
            int r0 = r0.length
            r1 = r5
            int r1 = r1.length
            if (r0 >= r1) goto L11
        Lf:
            r0 = -1
            return r0
        L11:
            r0 = r5
            int r0 = r0.length
            if (r0 != 0) goto L18
            r0 = 0
            return r0
        L18:
            r0 = 0
            r6 = r0
        L1a:
            r0 = r6
            r1 = r4
            int r1 = r1.length
            r2 = r5
            int r2 = r2.length
            int r1 = r1 - r2
            r2 = 1
            int r1 = r1 + r2
            if (r0 >= r1) goto L49
            r0 = 0
            r7 = r0
        L27:
            r0 = r7
            r1 = r5
            int r1 = r1.length
            if (r0 >= r1) goto L41
            r0 = r4
            r1 = r6
            r2 = r7
            int r1 = r1 + r2
            r0 = r0[r1]
            r1 = r5
            r2 = r7
            r1 = r1[r2]
            if (r0 == r1) goto L3b
            goto L43
        L3b:
            int r7 = r7 + 1
            goto L27
        L41:
            r0 = r6
            return r0
        L43:
            int r6 = r6 + 1
            goto L1a
        L49:
            r0 = -1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: cn.hutool.core.io.FileMagicNumber.indexOf(byte[], byte[]):int");
    }

    private static boolean compareBytes(byte[] buf, byte[] slice, int startOffset) {
        int sl = slice.length;
        if (startOffset + sl > buf.length) {
            return false;
        }
        byte[] sub = Arrays.copyOfRange(buf, startOffset, startOffset + sl);
        return Arrays.equals(sub, slice);
    }

    private static FileMagicNumber matchOpenXmlMime(byte[] bytes, int offset) {
        byte[] word = {119, 111, 114, 100, 47};
        byte[] ppt = {112, 112, 116, 47};
        byte[] xl = {120, 108, 47};
        if (compareBytes(bytes, word, offset)) {
            return DOCX;
        }
        if (compareBytes(bytes, ppt, offset)) {
            return PPTX;
        }
        if (compareBytes(bytes, xl, offset)) {
            return XLSX;
        }
        return UNKNOWN;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static FileMagicNumber matchDocument(byte[] bytes) {
        FileMagicNumber fileMagicNumber = matchOpenXmlMime(bytes, 30);
        if (false == fileMagicNumber.equals(UNKNOWN)) {
            return fileMagicNumber;
        }
        byte[] bytes1 = {91, 67, 111, 110, 116, 101, 110, 116, 95, 84, 121, 112, 101, 115, 93, 46, 120, 109, 108};
        byte[] bytes2 = {95, 114, 101, 108, 115, 47, 46, 114, 101, 108, 115};
        byte[] bytes3 = {100, 111, 99, 80, 114, 111, 112, 115};
        boolean flag1 = compareBytes(bytes, bytes1, 30);
        boolean flag2 = compareBytes(bytes, bytes2, 30);
        boolean flag3 = compareBytes(bytes, bytes3, 30);
        if (false == (flag1 || flag2 || flag3)) {
            return UNKNOWN;
        }
        int index = 0;
        for (int i = 0; i < 4; i++) {
            index = searchSignature(bytes, index + 4, 6000);
            if (index != -1) {
                FileMagicNumber fn = matchOpenXmlMime(bytes, index + 30);
                if (false == fn.equals(UNKNOWN)) {
                    return fn;
                }
            }
        }
        return UNKNOWN;
    }

    private static int searchSignature(byte[] bytes, int start, int rangeNum) {
        byte[] signature = {80, 75, 3, 4};
        int length = bytes.length;
        int end = start + rangeNum;
        if (end > length) {
            end = length;
        }
        int index = indexOf(Arrays.copyOfRange(bytes, start, end), signature);
        if (index == -1) {
            return -1;
        }
        return start + index;
    }
}
