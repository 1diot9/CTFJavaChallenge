package cn.hutool.extra.qrcode;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.img.Img;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.ansi.AnsiColors;
import cn.hutool.core.lang.ansi.AnsiElement;
import cn.hutool.core.lang.ansi.AnsiEncoder;
import cn.hutool.core.lang.ansi.ForeOrBack;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/qrcode/QrCodeUtil.class */
public class QrCodeUtil {
    public static final String QR_TYPE_SVG = "svg";
    public static final String QR_TYPE_TXT = "txt";
    private static final AnsiColors ansiColors = new AnsiColors(AnsiColors.BitDepth.EIGHT);

    public static String generateAsBase64(String content, QrConfig qrConfig, String targetType, String logoBase64) {
        return generateAsBase64(content, qrConfig, targetType, Base64.decode(logoBase64));
    }

    public static String generateAsBase64(String content, QrConfig qrConfig, String targetType, byte[] logo) {
        return generateAsBase64(content, qrConfig, targetType, (Image) ImgUtil.toImage(logo));
    }

    public static String generateAsBase64(String content, QrConfig qrConfig, String targetType, Image logo) {
        qrConfig.setImg(logo);
        return generateAsBase64(content, qrConfig, targetType);
    }

    public static String generateAsBase64(String content, QrConfig qrConfig, String targetType) {
        String result;
        boolean z = -1;
        switch (targetType.hashCode()) {
            case 114276:
                if (targetType.equals(QR_TYPE_SVG)) {
                    z = false;
                    break;
                }
                break;
            case 115312:
                if (targetType.equals(QR_TYPE_TXT)) {
                    z = true;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                String svg = generateAsSvg(content, qrConfig);
                result = svgToBase64(svg);
                break;
            case true:
                String txt = generateAsAsciiArt(content, qrConfig);
                result = txtToBase64(txt);
                break;
            default:
                BufferedImage img = generate(content, qrConfig);
                result = ImgUtil.toBase64DataUri(img, targetType);
                break;
        }
        return result;
    }

    private static String txtToBase64(String txt) {
        return URLUtil.getDataUri("text/plain", "base64", Base64.encode(txt));
    }

    private static String svgToBase64(String svg) {
        return URLUtil.getDataUri("image/svg+xml", "base64", Base64.encode(svg));
    }

    public static String generateAsSvg(String content, QrConfig qrConfig) {
        BitMatrix bitMatrix = encode(content, qrConfig);
        return toSVG(bitMatrix, qrConfig);
    }

    public static String generateAsAsciiArt(String content) {
        return generateAsAsciiArt(content, 0, 0, 1);
    }

    public static String generateAsAsciiArt(String content, QrConfig qrConfig) {
        BitMatrix bitMatrix = encode(content, qrConfig);
        return toAsciiArt(bitMatrix, qrConfig);
    }

    public static String generateAsAsciiArt(String content, int width, int height, int margin) {
        QrConfig qrConfig = new QrConfig(width, height).setMargin(Integer.valueOf(margin));
        return generateAsAsciiArt(content, qrConfig);
    }

    public static byte[] generatePng(String content, int width, int height) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        generate(content, width, height, ImgUtil.IMAGE_TYPE_PNG, out);
        return out.toByteArray();
    }

    public static byte[] generatePng(String content, QrConfig config) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        generate(content, config, ImgUtil.IMAGE_TYPE_PNG, out);
        return out.toByteArray();
    }

    public static File generate(String content, int width, int height, File targetFile) {
        String extName = FileUtil.extName(targetFile);
        boolean z = -1;
        switch (extName.hashCode()) {
            case 114276:
                if (extName.equals(QR_TYPE_SVG)) {
                    z = false;
                    break;
                }
                break;
            case 115312:
                if (extName.equals(QR_TYPE_TXT)) {
                    z = true;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                String svg = generateAsSvg(content, new QrConfig(width, height));
                FileUtil.writeString(svg, targetFile, StandardCharsets.UTF_8);
                break;
            case true:
                String txt = generateAsAsciiArt(content, new QrConfig(width, height));
                FileUtil.writeString(txt, targetFile, StandardCharsets.UTF_8);
                break;
            default:
                BufferedImage image = generate(content, width, height);
                ImgUtil.write(image, targetFile);
                break;
        }
        return targetFile;
    }

    public static File generate(String content, QrConfig config, File targetFile) {
        String extName = FileUtil.extName(targetFile);
        boolean z = -1;
        switch (extName.hashCode()) {
            case 114276:
                if (extName.equals(QR_TYPE_SVG)) {
                    z = false;
                    break;
                }
                break;
            case 115312:
                if (extName.equals(QR_TYPE_TXT)) {
                    z = true;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                String svg = generateAsSvg(content, config);
                FileUtil.writeString(svg, targetFile, StandardCharsets.UTF_8);
                break;
            case true:
                String txt = generateAsAsciiArt(content, config);
                FileUtil.writeString(txt, targetFile, StandardCharsets.UTF_8);
                break;
            default:
                BufferedImage image = generate(content, config);
                ImgUtil.write(image, targetFile);
                break;
        }
        return targetFile;
    }

    public static void generate(String content, int width, int height, String targetType, OutputStream out) {
        boolean z = -1;
        switch (targetType.hashCode()) {
            case 114276:
                if (targetType.equals(QR_TYPE_SVG)) {
                    z = false;
                    break;
                }
                break;
            case 115312:
                if (targetType.equals(QR_TYPE_TXT)) {
                    z = true;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                String svg = generateAsSvg(content, new QrConfig(width, height));
                IoUtil.writeUtf8(out, false, svg);
                return;
            case true:
                String txt = generateAsAsciiArt(content, new QrConfig(width, height));
                IoUtil.writeUtf8(out, false, txt);
                return;
            default:
                BufferedImage image = generate(content, width, height);
                ImgUtil.write((Image) image, targetType, out);
                return;
        }
    }

    public static void generate(String content, QrConfig config, String targetType, OutputStream out) {
        boolean z = -1;
        switch (targetType.hashCode()) {
            case 114276:
                if (targetType.equals(QR_TYPE_SVG)) {
                    z = false;
                    break;
                }
                break;
            case 115312:
                if (targetType.equals(QR_TYPE_TXT)) {
                    z = true;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                String svg = generateAsSvg(content, config);
                IoUtil.writeUtf8(out, false, svg);
                return;
            case true:
                String txt = generateAsAsciiArt(content, config);
                IoUtil.writeUtf8(out, false, txt);
                return;
            default:
                BufferedImage image = generate(content, config);
                ImgUtil.write((Image) image, targetType, out);
                return;
        }
    }

    public static BufferedImage generate(String content, int width, int height) {
        return generate(content, new QrConfig(width, height));
    }

    public static BufferedImage generate(String content, BarcodeFormat format, int width, int height) {
        return generate(content, format, new QrConfig(width, height));
    }

    public static BufferedImage generate(String content, QrConfig config) {
        return generate(content, BarcodeFormat.QR_CODE, config);
    }

    public static BufferedImage generate(String content, BarcodeFormat format, QrConfig config) {
        int height;
        int width;
        BitMatrix bitMatrix = encode(content, format, config);
        BufferedImage image = toImage(bitMatrix, config.foreColor != null ? config.foreColor.intValue() : Color.BLACK.getRGB(), config.backColor);
        Image logoImg = config.img;
        if (null != logoImg && BarcodeFormat.QR_CODE == format) {
            int qrWidth = image.getWidth();
            int qrHeight = image.getHeight();
            if (qrWidth < qrHeight) {
                width = qrWidth / config.ratio;
                height = (logoImg.getHeight((ImageObserver) null) * width) / logoImg.getWidth((ImageObserver) null);
            } else {
                height = qrHeight / config.ratio;
                width = (logoImg.getWidth((ImageObserver) null) * height) / logoImg.getHeight((ImageObserver) null);
            }
            Img.from((Image) image).pressImage(Img.from(logoImg).round(0.3d).getImg(), new Rectangle(width, height), 1.0f);
        }
        return image;
    }

    public static BitMatrix encode(String content, int width, int height) {
        return encode(content, BarcodeFormat.QR_CODE, width, height);
    }

    public static BitMatrix encode(String content, QrConfig config) {
        return encode(content, BarcodeFormat.QR_CODE, config);
    }

    public static BitMatrix encode(String content, BarcodeFormat format, int width, int height) {
        return encode(content, format, new QrConfig(width, height));
    }

    public static BitMatrix encode(String content, BarcodeFormat format, QrConfig config) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        if (null == config) {
            config = new QrConfig();
        }
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(content, format, config.width, config.height, config.toHints(format));
            return bitMatrix;
        } catch (WriterException e) {
            throw new QrCodeException((Throwable) e);
        }
    }

    public static String decode(InputStream qrCodeInputStream) {
        return decode((Image) ImgUtil.read(qrCodeInputStream));
    }

    public static String decode(File qrCodeFile) {
        return decode((Image) ImgUtil.read(qrCodeFile));
    }

    public static String decode(Image image) {
        return decode(image, true, false);
    }

    public static String decode(Image image, boolean isTryHarder, boolean isPureBarcode) {
        return decode(image, buildHints(isTryHarder, isPureBarcode));
    }

    public static String decode(Image image, Map<DecodeHintType, Object> hints) {
        MultiFormatReader formatReader = new MultiFormatReader();
        formatReader.setHints(hints);
        LuminanceSource source = new BufferedImageLuminanceSource(ImgUtil.toBufferedImage(image));
        Result result = _decode(formatReader, new HybridBinarizer(source));
        if (null == result) {
            result = _decode(formatReader, new GlobalHistogramBinarizer(source));
        }
        if (null != result) {
            return result.getText();
        }
        return null;
    }

    public static BufferedImage toImage(BitMatrix matrix, int foreColor, Integer backColor) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, null == backColor ? 2 : 1);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (matrix.get(x, y)) {
                    image.setRGB(x, y, foreColor);
                } else if (null != backColor) {
                    image.setRGB(x, y, backColor.intValue());
                }
            }
        }
        return image;
    }

    public static String toSVG(BitMatrix matrix, QrConfig qrConfig) {
        return toSVG(matrix, qrConfig.foreColor, qrConfig.backColor, qrConfig.img, qrConfig.getRatio());
    }

    public static String toSVG(BitMatrix matrix, Integer foreColor, Integer backColor, Image logoImg, int ratio) {
        StringBuilder sb = new StringBuilder();
        int qrWidth = matrix.getWidth();
        int qrHeight = matrix.getHeight();
        int moduleHeight = qrHeight == 1 ? qrWidth / 2 : 1;
        for (int y = 0; y < qrHeight; y++) {
            for (int x = 0; x < qrWidth; x++) {
                if (matrix.get(x, y)) {
                    sb.append(" M").append(x).append(",").append(y).append("h1v").append(moduleHeight).append("h-1z");
                }
            }
        }
        int qrHeight2 = qrHeight * moduleHeight;
        String logoBase64 = "";
        int logoWidth = 0;
        int logoHeight = 0;
        int logoX = 0;
        int logoY = 0;
        if (logoImg != null) {
            logoBase64 = ImgUtil.toBase64DataUri(logoImg, ImgUtil.IMAGE_TYPE_PNG);
            if (qrWidth < qrHeight2) {
                logoWidth = qrWidth / ratio;
                logoHeight = (logoImg.getHeight((ImageObserver) null) * logoWidth) / logoImg.getWidth((ImageObserver) null);
            } else {
                logoHeight = qrHeight2 / ratio;
                logoWidth = (logoImg.getWidth((ImageObserver) null) * logoHeight) / logoImg.getHeight((ImageObserver) null);
            }
            logoX = (qrWidth - logoWidth) / 2;
            logoY = (qrHeight2 - logoHeight) / 2;
        }
        StringBuilder result = StrUtil.builder();
        result.append("<svg width=\"").append(qrWidth).append("\" height=\"").append(qrHeight2).append("\" \n");
        if (backColor != null) {
            Color back = new Color(backColor.intValue(), true);
            result.append("style=\"background-color:rgba(").append(back.getRed()).append(",").append(back.getGreen()).append(",").append(back.getBlue()).append(",").append(back.getAlpha()).append(")\"\n");
        }
        result.append("viewBox=\"0 0 ").append(qrWidth).append(CharSequenceUtil.SPACE).append(qrHeight2).append("\" \n");
        result.append("xmlns=\"http://www.w3.org/2000/svg\" \n");
        result.append("xmlns:xlink=\"http://www.w3.org/1999/xlink\" >\n");
        result.append("<path d=\"").append((CharSequence) sb).append("\" ");
        if (foreColor != null) {
            Color fore = new Color(foreColor.intValue(), true);
            result.append("stroke=\"rgba(").append(fore.getRed()).append(",").append(fore.getGreen()).append(",").append(fore.getBlue()).append(",").append(fore.getAlpha()).append(")\"");
        }
        result.append(" /> \n");
        if (StrUtil.isNotBlank(logoBase64)) {
            result.append("<image xlink:href=\"").append(logoBase64).append("\" height=\"").append(logoHeight).append("\" width=\"").append(logoWidth).append("\" y=\"").append(logoY).append("\" x=\"").append(logoX).append("\" />\n");
        }
        result.append("</svg>");
        return result.toString();
    }

    public static String toAsciiArt(BitMatrix bitMatrix, QrConfig qrConfig) {
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        AnsiElement foreground = qrConfig.foreColor == null ? null : rgbToAnsi8BitElement(qrConfig.foreColor.intValue(), ForeOrBack.FORE);
        AnsiElement background = qrConfig.backColor == null ? null : rgbToAnsi8BitElement(qrConfig.backColor.intValue(), ForeOrBack.BACK);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i <= height; i += 2) {
            StringBuilder rowBuilder = new StringBuilder();
            for (int j = 0; j < width; j++) {
                boolean tp = bitMatrix.get(i, j);
                boolean bt = i + 1 >= height || bitMatrix.get(i + 1, j);
                if (tp && bt) {
                    rowBuilder.append(' ');
                } else if (tp) {
                    rowBuilder.append((char) 9604);
                } else if (bt) {
                    rowBuilder.append((char) 9600);
                } else {
                    rowBuilder.append((char) 9608);
                }
            }
            builder.append(AnsiEncoder.encode(foreground, background, rowBuilder)).append('\n');
        }
        return builder.toString();
    }

    private static AnsiElement rgbToAnsi8BitElement(int rgb, ForeOrBack foreOrBack) {
        return ansiColors.findClosest(new Color(rgb)).toAnsiElement(foreOrBack);
    }

    private static Map<DecodeHintType, Object> buildHints(boolean isTryHarder, boolean isPureBarcode) {
        HashMap<DecodeHintType, Object> hints = new HashMap<>();
        hints.put(DecodeHintType.CHARACTER_SET, CharsetUtil.UTF_8);
        if (isTryHarder) {
            hints.put(DecodeHintType.TRY_HARDER, true);
        }
        if (isPureBarcode) {
            hints.put(DecodeHintType.PURE_BARCODE, true);
        }
        return hints;
    }

    private static Result _decode(MultiFormatReader formatReader, Binarizer binarizer) {
        try {
            return formatReader.decodeWithState(new BinaryBitmap(binarizer));
        } catch (NotFoundException e) {
            return null;
        }
    }
}
