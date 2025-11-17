package cn.hutool.core.img;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/img/ImgUtil.class */
public class ImgUtil {
    public static final String IMAGE_TYPE_GIF = "gif";
    public static final String IMAGE_TYPE_JPG = "jpg";
    public static final String IMAGE_TYPE_JPEG = "jpeg";
    public static final String IMAGE_TYPE_BMP = "bmp";
    public static final String IMAGE_TYPE_PNG = "png";
    public static final String IMAGE_TYPE_PSD = "psd";

    public static void scale(File srcImageFile, File destImageFile, float scale) {
        scale((Image) read(srcImageFile), destImageFile, scale);
    }

    public static void scale(InputStream srcStream, OutputStream destStream, float scale) {
        scale((Image) read(srcStream), destStream, scale);
    }

    public static void scale(ImageInputStream srcStream, ImageOutputStream destStream, float scale) {
        scale((Image) read(srcStream), destStream, scale);
    }

    public static void scale(Image srcImg, File destFile, float scale) throws IORuntimeException {
        Img.from(srcImg).setTargetImageType(FileUtil.extName(destFile)).scale(scale).write(destFile);
    }

    public static void scale(Image srcImg, OutputStream out, float scale) throws IORuntimeException {
        scale(srcImg, getImageOutputStream(out), scale);
    }

    public static void scale(Image srcImg, ImageOutputStream destImageStream, float scale) throws IORuntimeException {
        writeJpg(scale(srcImg, scale), destImageStream);
    }

    public static Image scale(Image srcImg, float scale) {
        return Img.from(srcImg).scale(scale).getImg();
    }

    public static Image scale(Image srcImg, int width, int height) {
        return Img.from(srcImg).scale(width, height).getImg();
    }

    public static void scale(File srcImageFile, File destImageFile, int width, int height, Color fixedColor) throws IORuntimeException {
        Img.from(srcImageFile).setTargetImageType(FileUtil.extName(destImageFile)).scale(width, height, fixedColor).write(destImageFile);
    }

    public static void scale(InputStream srcStream, OutputStream destStream, int width, int height, Color fixedColor) throws IORuntimeException {
        scale((Image) read(srcStream), getImageOutputStream(destStream), width, height, fixedColor);
    }

    public static void scale(ImageInputStream srcStream, ImageOutputStream destStream, int width, int height, Color fixedColor) throws IORuntimeException {
        scale((Image) read(srcStream), destStream, width, height, fixedColor);
    }

    public static void scale(Image srcImage, ImageOutputStream destImageStream, int width, int height, Color fixedColor) throws IORuntimeException {
        writeJpg(scale(srcImage, width, height, fixedColor), destImageStream);
    }

    public static Image scale(Image srcImage, int width, int height, Color fixedColor) {
        return Img.from(srcImage).scale(width, height, fixedColor).getImg();
    }

    public static void cut(File srcImgFile, File destImgFile, Rectangle rectangle) {
        cut((Image) read(srcImgFile), destImgFile, rectangle);
    }

    public static void cut(InputStream srcStream, OutputStream destStream, Rectangle rectangle) {
        cut((Image) read(srcStream), destStream, rectangle);
    }

    public static void cut(ImageInputStream srcStream, ImageOutputStream destStream, Rectangle rectangle) {
        cut((Image) read(srcStream), destStream, rectangle);
    }

    public static void cut(Image srcImage, File destFile, Rectangle rectangle) throws IORuntimeException {
        write(cut(srcImage, rectangle), destFile);
    }

    public static void cut(Image srcImage, OutputStream out, Rectangle rectangle) throws IORuntimeException {
        cut(srcImage, getImageOutputStream(out), rectangle);
    }

    public static void cut(Image srcImage, ImageOutputStream destImageStream, Rectangle rectangle) throws IORuntimeException {
        writeJpg(cut(srcImage, rectangle), destImageStream);
    }

    public static Image cut(Image srcImage, Rectangle rectangle) {
        return Img.from(srcImage).setPositionBaseCentre(false).cut(rectangle).getImg();
    }

    public static Image cut(Image srcImage, int x, int y) {
        return cut(srcImage, x, y, -1);
    }

    public static Image cut(Image srcImage, int x, int y, int radius) {
        return Img.from(srcImage).cut(x, y, radius).getImg();
    }

    public static void slice(File srcImageFile, File descDir, int destWidth, int destHeight) {
        slice((Image) read(srcImageFile), descDir, destWidth, destHeight);
    }

    public static void slice(Image srcImage, File descDir, int destWidth, int destHeight) {
        int cols;
        int rows;
        if (destWidth <= 0) {
            destWidth = 200;
        }
        if (destHeight <= 0) {
            destHeight = 150;
        }
        int srcWidth = srcImage.getWidth((ImageObserver) null);
        int srcHeight = srcImage.getHeight((ImageObserver) null);
        if (srcWidth < destWidth) {
            destWidth = srcWidth;
        }
        if (srcHeight < destHeight) {
            destHeight = srcHeight;
        }
        if (srcWidth % destWidth == 0) {
            cols = srcWidth / destWidth;
        } else {
            cols = ((int) Math.floor(srcWidth / destWidth)) + 1;
        }
        if (srcHeight % destHeight == 0) {
            rows = srcHeight / destHeight;
        } else {
            rows = ((int) Math.floor(srcHeight / destHeight)) + 1;
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Image tag = cut(srcImage, new Rectangle(j * destWidth, i * destHeight, destWidth, destHeight));
                write(tag, FileUtil.file(descDir, "_r" + i + "_c" + j + ".jpg"));
            }
        }
    }

    public static void sliceByRowsAndCols(File srcImageFile, File destDir, int rows, int cols) {
        sliceByRowsAndCols(srcImageFile, destDir, IMAGE_TYPE_JPEG, rows, cols);
    }

    public static void sliceByRowsAndCols(File srcImageFile, File destDir, String format, int rows, int cols) {
        try {
            sliceByRowsAndCols((Image) ImageIO.read(srcImageFile), destDir, format, rows, cols);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static void sliceByRowsAndCols(Image srcImage, File destDir, int rows, int cols) {
        sliceByRowsAndCols(srcImage, destDir, IMAGE_TYPE_JPEG, rows, cols);
    }

    public static void sliceByRowsAndCols(Image srcImage, File destDir, String format, int rows, int cols) {
        if (false == destDir.exists()) {
            FileUtil.mkdir(destDir);
        } else if (false == destDir.isDirectory()) {
            throw new IllegalArgumentException("Destination Dir must be a Directory !");
        }
        if (rows <= 0 || rows > 20) {
            rows = 2;
        }
        if (cols <= 0 || cols > 20) {
            cols = 2;
        }
        try {
            BufferedImage bi = toBufferedImage(srcImage);
            int srcWidth = bi.getWidth();
            int srcHeight = bi.getHeight();
            int destWidth = NumberUtil.partValue(srcWidth, cols);
            int destHeight = NumberUtil.partValue(srcHeight, rows);
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    Image tag = cut(bi, new Rectangle(j * destWidth, i * destHeight, destWidth, destHeight));
                    ImageIO.write(toRenderedImage(tag), format, new File(destDir, "_r" + i + "_c" + j + "." + format));
                }
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static void convert(File srcImageFile, File destImageFile) {
        Assert.notNull(srcImageFile);
        Assert.notNull(destImageFile);
        Assert.isFalse(srcImageFile.equals(destImageFile), "Src file is equals to dest file!", new Object[0]);
        String srcExtName = FileUtil.extName(srcImageFile);
        String destExtName = FileUtil.extName(destImageFile);
        if (StrUtil.equalsIgnoreCase(srcExtName, destExtName)) {
            FileUtil.copy(srcImageFile, destImageFile, true);
        }
        ImageOutputStream imageOutputStream = null;
        try {
            imageOutputStream = getImageOutputStream(destImageFile);
            convert(read(srcImageFile), destExtName, imageOutputStream, StrUtil.equalsIgnoreCase(IMAGE_TYPE_PNG, srcExtName));
            IoUtil.close((Closeable) imageOutputStream);
        } catch (Throwable th) {
            IoUtil.close((Closeable) imageOutputStream);
            throw th;
        }
    }

    public static void convert(InputStream srcStream, String formatName, OutputStream destStream) {
        write((Image) read(srcStream), formatName, getImageOutputStream(destStream));
    }

    public static void convert(Image srcImage, String formatName, ImageOutputStream destImageStream, boolean isSrcPng) {
        try {
            ImageIO.write(isSrcPng ? copyImage(srcImage, 1) : toBufferedImage(srcImage), formatName, destImageStream);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static void gray(File srcImageFile, File destImageFile) {
        gray((Image) read(srcImageFile), destImageFile);
    }

    public static void gray(InputStream srcStream, OutputStream destStream) {
        gray((Image) read(srcStream), getImageOutputStream(destStream));
    }

    public static void gray(ImageInputStream srcStream, ImageOutputStream destStream) {
        gray((Image) read(srcStream), destStream);
    }

    public static void gray(Image srcImage, File outFile) {
        write(gray(srcImage), outFile);
    }

    public static void gray(Image srcImage, OutputStream out) {
        gray(srcImage, getImageOutputStream(out));
    }

    public static void gray(Image srcImage, ImageOutputStream destImageStream) throws IORuntimeException {
        writeJpg(gray(srcImage), destImageStream);
    }

    public static Image gray(Image srcImage) {
        return Img.from(srcImage).gray().getImg();
    }

    public static void binary(File srcImageFile, File destImageFile) {
        binary((Image) read(srcImageFile), destImageFile);
    }

    public static void binary(InputStream srcStream, OutputStream destStream, String imageType) {
        binary((Image) read(srcStream), getImageOutputStream(destStream), imageType);
    }

    public static void binary(ImageInputStream srcStream, ImageOutputStream destStream, String imageType) {
        binary((Image) read(srcStream), destStream, imageType);
    }

    public static void binary(Image srcImage, File outFile) {
        write(binary(srcImage), outFile);
    }

    public static void binary(Image srcImage, OutputStream out, String imageType) {
        binary(srcImage, getImageOutputStream(out), imageType);
    }

    public static void binary(Image srcImage, ImageOutputStream destImageStream, String imageType) throws IORuntimeException {
        write(binary(srcImage), imageType, destImageStream);
    }

    public static Image binary(Image srcImage) {
        return Img.from(srcImage).binary().getImg();
    }

    public static void pressText(File imageFile, File destFile, String pressText, Color color, Font font, int x, int y, float alpha) {
        pressText((Image) read(imageFile), destFile, pressText, color, font, x, y, alpha);
    }

    public static void pressText(InputStream srcStream, OutputStream destStream, String pressText, Color color, Font font, int x, int y, float alpha) {
        pressText((Image) read(srcStream), getImageOutputStream(destStream), pressText, color, font, x, y, alpha);
    }

    public static void pressText(ImageInputStream srcStream, ImageOutputStream destStream, String pressText, Color color, Font font, int x, int y, float alpha) {
        pressText((Image) read(srcStream), destStream, pressText, color, font, x, y, alpha);
    }

    public static void pressText(Image srcImage, File destFile, String pressText, Color color, Font font, int x, int y, float alpha) throws IORuntimeException {
        write(pressText(srcImage, pressText, color, font, x, y, alpha), destFile);
    }

    public static void pressText(Image srcImage, OutputStream to, String pressText, Color color, Font font, int x, int y, float alpha) throws IORuntimeException {
        pressText(srcImage, getImageOutputStream(to), pressText, color, font, x, y, alpha);
    }

    public static void pressText(Image srcImage, ImageOutputStream destImageStream, String pressText, Color color, Font font, int x, int y, float alpha) throws IORuntimeException {
        writeJpg(pressText(srcImage, pressText, color, font, x, y, alpha), destImageStream);
    }

    public static Image pressText(Image srcImage, String pressText, Color color, Font font, int x, int y, float alpha) {
        return Img.from(srcImage).pressText(pressText, color, font, x, y, alpha).getImg();
    }

    public static void pressImage(File srcImageFile, File destImageFile, Image pressImg, int x, int y, float alpha) {
        pressImage((Image) read(srcImageFile), destImageFile, pressImg, x, y, alpha);
    }

    public static void pressImage(InputStream srcStream, OutputStream destStream, Image pressImg, int x, int y, float alpha) {
        pressImage((Image) read(srcStream), getImageOutputStream(destStream), pressImg, x, y, alpha);
    }

    public static void pressImage(ImageInputStream srcStream, ImageOutputStream destStream, Image pressImg, int x, int y, float alpha) throws IORuntimeException {
        pressImage((Image) read(srcStream), destStream, pressImg, x, y, alpha);
    }

    public static void pressImage(Image srcImage, File outFile, Image pressImg, int x, int y, float alpha) throws IORuntimeException {
        write(pressImage(srcImage, pressImg, x, y, alpha), outFile);
    }

    public static void pressImage(Image srcImage, OutputStream out, Image pressImg, int x, int y, float alpha) throws IORuntimeException {
        pressImage(srcImage, getImageOutputStream(out), pressImg, x, y, alpha);
    }

    public static void pressImage(Image srcImage, ImageOutputStream destImageStream, Image pressImg, int x, int y, float alpha) throws IORuntimeException {
        writeJpg(pressImage(srcImage, pressImg, x, y, alpha), destImageStream);
    }

    public static Image pressImage(Image srcImage, Image pressImg, int x, int y, float alpha) {
        return Img.from(srcImage).pressImage(pressImg, x, y, alpha).getImg();
    }

    public static Image pressImage(Image srcImage, Image pressImg, Rectangle rectangle, float alpha) {
        return Img.from(srcImage).pressImage(pressImg, rectangle, alpha).getImg();
    }

    public static void rotate(File imageFile, int degree, File outFile) throws IORuntimeException {
        rotate((Image) read(imageFile), degree, outFile);
    }

    public static void rotate(Image image, int degree, File outFile) throws IORuntimeException {
        write(rotate(image, degree), outFile);
    }

    public static void rotate(Image image, int degree, OutputStream out) throws IORuntimeException {
        writeJpg(rotate(image, degree), getImageOutputStream(out));
    }

    public static void rotate(Image image, int degree, ImageOutputStream out) throws IORuntimeException {
        writeJpg(rotate(image, degree), out);
    }

    public static Image rotate(Image image, int degree) {
        return Img.from(image).rotate(degree).getImg();
    }

    public static void flip(File imageFile, File outFile) throws IORuntimeException {
        flip((Image) read(imageFile), outFile);
    }

    public static void flip(Image image, File outFile) throws IORuntimeException {
        write(flip(image), outFile);
    }

    public static void flip(Image image, OutputStream out) throws IORuntimeException {
        flip(image, getImageOutputStream(out));
    }

    public static void flip(Image image, ImageOutputStream out) throws IORuntimeException {
        writeJpg(flip(image), out);
    }

    public static Image flip(Image image) {
        return Img.from(image).flip().getImg();
    }

    public static void compress(File imageFile, File outFile, float quality) throws IORuntimeException {
        Img.from(imageFile).setQuality(quality).write(outFile);
    }

    public static RenderedImage toRenderedImage(Image img) {
        if (img instanceof RenderedImage) {
            return (RenderedImage) img;
        }
        return copyImage(img, 1);
    }

    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        return copyImage(img, 1);
    }

    public static BufferedImage toBufferedImage(Image image, String imageType) {
        return toBufferedImage(image, imageType, (Color) null);
    }

    public static BufferedImage toBufferedImage(Image image, String imageType, Color backgroundColor) {
        int type = IMAGE_TYPE_PNG.equalsIgnoreCase(imageType) ? 2 : 1;
        return toBufferedImage(image, type, backgroundColor);
    }

    public static BufferedImage toBufferedImage(Image image, int imageType) {
        if (image instanceof BufferedImage) {
            BufferedImage bufferedImage = (BufferedImage) image;
            if (imageType != bufferedImage.getType()) {
                bufferedImage = copyImage(image, imageType);
            }
            return bufferedImage;
        }
        return copyImage(image, imageType);
    }

    public static BufferedImage toBufferedImage(Image image, int imageType, Color backgroundColor) {
        if (image instanceof BufferedImage) {
            BufferedImage bufferedImage = (BufferedImage) image;
            if (imageType != bufferedImage.getType()) {
                bufferedImage = copyImage(image, imageType, backgroundColor);
            }
            return bufferedImage;
        }
        return copyImage(image, imageType, backgroundColor);
    }

    public static BufferedImage copyImage(Image img, int imageType) {
        return copyImage(img, imageType, null);
    }

    public static BufferedImage copyImage(Image img, int imageType, Color backgroundColor) {
        Image img2 = new ImageIcon(img).getImage();
        BufferedImage bimage = new BufferedImage(img2.getWidth((ImageObserver) null), img2.getHeight((ImageObserver) null), imageType);
        Graphics2D bGr = GraphicsUtil.createGraphics(bimage, backgroundColor);
        bGr.drawImage(img2, 0, 0, (ImageObserver) null);
        bGr.dispose();
        return bimage;
    }

    public static BufferedImage createCompatibleImage(int width, int height, int transparency) throws HeadlessException {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gs = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gs.getDefaultConfiguration();
        return gc.createCompatibleImage(width, height, transparency);
    }

    public static BufferedImage toImage(String base64) throws IORuntimeException {
        return toImage(Base64.decode(base64));
    }

    public static BufferedImage toImage(byte[] imageBytes) throws IORuntimeException {
        return read(new ByteArrayInputStream(imageBytes));
    }

    public static ByteArrayInputStream toStream(Image image, String imageType) {
        return IoUtil.toStream(toBytes(image, imageType));
    }

    public static String toBase64DataUri(Image image, String imageType) {
        return URLUtil.getDataUri("image/" + imageType, "base64", toBase64(image, imageType));
    }

    public static String toBase64(Image image, String imageType) {
        return Base64.encode(toBytes(image, imageType));
    }

    public static byte[] toBytes(Image image, String imageType) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        write(image, imageType, out);
        return out.toByteArray();
    }

    public static void createImage(String str, Font font, Color backgroundColor, Color fontColor, ImageOutputStream out) throws IORuntimeException {
        writePng((Image) createImage(str, font, backgroundColor, fontColor, 2), out);
    }

    public static void createTransparentImage(String str, Font font, Color fontColor, ImageOutputStream out) throws IORuntimeException {
        writePng((Image) createImage(str, font, (Color) null, fontColor, 2), out);
    }

    public static BufferedImage createImage(String str, Font font, Color backgroundColor, Color fontColor, int imageType) throws IORuntimeException {
        Rectangle2D r = getRectangle(str, font);
        int unitHeight = (int) Math.floor(r.getHeight());
        int width = ((int) Math.round(r.getWidth())) + 1;
        int height = unitHeight + 3;
        BufferedImage image = new BufferedImage(width, height, imageType);
        Graphics g = image.getGraphics();
        if (null != backgroundColor) {
            g.setColor(backgroundColor);
            g.fillRect(0, 0, width, height);
        }
        g.setColor((Color) ObjectUtil.defaultIfNull(fontColor, Color.BLACK));
        g.setFont(font);
        g.drawString(str, 0, font.getSize());
        g.dispose();
        return image;
    }

    public static Rectangle2D getRectangle(String str, Font font) {
        return font.getStringBounds(str, new FontRenderContext(AffineTransform.getScaleInstance(1.0d, 1.0d), false, false));
    }

    public static Font createFont(File fontFile) {
        return FontUtil.createFont(fontFile);
    }

    public static Font createFont(InputStream fontStream) {
        return FontUtil.createFont(fontStream);
    }

    public static Graphics2D createGraphics(BufferedImage image, Color color) {
        return GraphicsUtil.createGraphics(image, color);
    }

    public static void writeJpg(Image image, ImageOutputStream destImageStream) throws IORuntimeException {
        write(image, IMAGE_TYPE_JPG, destImageStream);
    }

    public static void writePng(Image image, ImageOutputStream destImageStream) throws IORuntimeException {
        write(image, IMAGE_TYPE_PNG, destImageStream);
    }

    public static void writeJpg(Image image, OutputStream out) throws IORuntimeException {
        write(image, IMAGE_TYPE_JPG, out);
    }

    public static void writePng(Image image, OutputStream out) throws IORuntimeException {
        write(image, IMAGE_TYPE_PNG, out);
    }

    public static void write(ImageInputStream srcStream, String formatName, ImageOutputStream destStream) {
        write((Image) read(srcStream), formatName, destStream);
    }

    public static void write(Image image, String imageType, OutputStream out) throws IORuntimeException {
        write(image, imageType, getImageOutputStream(out));
    }

    public static boolean write(Image image, String imageType, ImageOutputStream destImageStream) throws IORuntimeException {
        return write(image, imageType, destImageStream, 1.0f);
    }

    public static boolean write(Image image, String imageType, ImageOutputStream destImageStream, float quality) throws IORuntimeException {
        return write(image, imageType, destImageStream, quality, null);
    }

    public static boolean write(Image image, String imageType, ImageOutputStream destImageStream, float quality, Color backgroundColor) throws IORuntimeException {
        if (StrUtil.isBlank(imageType)) {
            imageType = IMAGE_TYPE_JPG;
        }
        BufferedImage bufferedImage = toBufferedImage(image, imageType, backgroundColor);
        ImageWriter writer = getWriter(bufferedImage, imageType);
        return write((Image) bufferedImage, writer, destImageStream, quality);
    }

    public static void write(Image image, File targetFile) throws IORuntimeException {
        FileUtil.touch(targetFile);
        ImageOutputStream out = null;
        try {
            out = getImageOutputStream(targetFile);
            write(image, FileUtil.extName(targetFile), out);
            IoUtil.close((Closeable) out);
        } catch (Throwable th) {
            IoUtil.close((Closeable) out);
            throw th;
        }
    }

    public static boolean write(Image image, ImageWriter writer, ImageOutputStream output, float quality) {
        if (writer == null) {
            return false;
        }
        writer.setOutput(output);
        RenderedImage renderedImage = toRenderedImage(image);
        ImageWriteParam imgWriteParams = null;
        if (quality > 0.0f && quality < 1.0f) {
            imgWriteParams = writer.getDefaultWriteParam();
            if (imgWriteParams.canWriteCompressed()) {
                imgWriteParams.setCompressionMode(2);
                imgWriteParams.setCompressionQuality(quality);
                ColorModel colorModel = renderedImage.getColorModel();
                imgWriteParams.setDestinationType(new ImageTypeSpecifier(colorModel, colorModel.createCompatibleSampleModel(16, 16)));
            }
        }
        try {
            try {
                if (null != imgWriteParams) {
                    writer.write((IIOMetadata) null, new IIOImage(renderedImage, (List) null, (IIOMetadata) null), imgWriteParams);
                } else {
                    writer.write(renderedImage);
                }
                output.flush();
                writer.dispose();
                return true;
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        } catch (Throwable th) {
            writer.dispose();
            throw th;
        }
    }

    public static ImageReader getReader(String type) {
        Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName(type);
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    public static BufferedImage read(String imageFilePath) {
        return read(FileUtil.file(imageFilePath));
    }

    public static BufferedImage read(File imageFile) {
        try {
            BufferedImage result = ImageIO.read(imageFile);
            if (null == result) {
                throw new IllegalArgumentException("Image type of file [" + imageFile.getName() + "] is not supported!");
            }
            return result;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static Image getImage(URL url) {
        return Toolkit.getDefaultToolkit().getImage(url);
    }

    public static BufferedImage read(Resource resource) {
        return read(resource.getStream());
    }

    public static BufferedImage read(InputStream imageStream) {
        try {
            BufferedImage result = ImageIO.read(imageStream);
            if (null == result) {
                throw new IllegalArgumentException("Image type is not supported!");
            }
            return result;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static BufferedImage read(ImageInputStream imageStream) {
        try {
            BufferedImage result = ImageIO.read(imageStream);
            if (null == result) {
                throw new IllegalArgumentException("Image type is not supported!");
            }
            return result;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static BufferedImage read(URL imageUrl) {
        try {
            BufferedImage result = ImageIO.read(imageUrl);
            if (null == result) {
                throw new IllegalArgumentException("Image type of [" + imageUrl + "] is not supported!");
            }
            return result;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static ImageOutputStream getImageOutputStream(OutputStream out) throws IORuntimeException {
        try {
            ImageOutputStream result = ImageIO.createImageOutputStream(out);
            if (null == result) {
                throw new IllegalArgumentException("Image type is not supported!");
            }
            return result;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static ImageOutputStream getImageOutputStream(File outFile) throws IORuntimeException {
        try {
            ImageOutputStream result = ImageIO.createImageOutputStream(outFile);
            if (null == result) {
                throw new IllegalArgumentException("Image type of file [" + outFile.getName() + "] is not supported!");
            }
            return result;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static ImageInputStream getImageInputStream(InputStream in) throws IORuntimeException {
        try {
            ImageOutputStream result = ImageIO.createImageOutputStream(in);
            if (null == result) {
                throw new IllegalArgumentException("Image type is not supported!");
            }
            return result;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static ImageWriter getWriter(Image img, String formatName) {
        ImageTypeSpecifier type = ImageTypeSpecifier.createFromRenderedImage(toBufferedImage(img, formatName));
        Iterator<ImageWriter> iter = ImageIO.getImageWriters(type, formatName);
        if (iter.hasNext()) {
            return iter.next();
        }
        return null;
    }

    public static ImageWriter getWriter(String formatName) {
        ImageWriter writer = null;
        Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName(formatName);
        if (iter.hasNext()) {
            writer = iter.next();
        }
        if (null == writer) {
            Iterator<ImageWriter> iter2 = ImageIO.getImageWritersBySuffix(formatName);
            if (iter2.hasNext()) {
                writer = iter2.next();
            }
        }
        return writer;
    }

    public static String toHex(Color color) {
        return ColorUtil.toHex(color);
    }

    public static String toHex(int r, int g, int b) {
        return ColorUtil.toHex(r, g, b);
    }

    public static Color hexToColor(String hex) {
        return ColorUtil.hexToColor(hex);
    }

    public static Color getColor(int rgb) {
        return ColorUtil.getColor(rgb);
    }

    public static Color getColor(String colorName) {
        return ColorUtil.getColor(colorName);
    }

    public static Color randomColor() {
        return ColorUtil.randomColor();
    }

    public static Color randomColor(Random random) {
        return ColorUtil.randomColor(random);
    }

    public static Point getPointBaseCentre(Rectangle rectangle, int backgroundWidth, int backgroundHeight) {
        return new Point(rectangle.x + (Math.abs(backgroundWidth - rectangle.width) / 2), rectangle.y + (Math.abs(backgroundHeight - rectangle.height) / 2));
    }

    public static String getMainColor(BufferedImage image, int[]... rgbFilters) {
        return ColorUtil.getMainColor(image, rgbFilters);
    }

    public static boolean backgroundRemoval(String inputPath, String outputPath, int tolerance) {
        return BackgroundRemoval.backgroundRemoval(inputPath, outputPath, tolerance);
    }

    public static boolean backgroundRemoval(File input, File output, int tolerance) {
        return BackgroundRemoval.backgroundRemoval(input, output, tolerance);
    }

    public static boolean backgroundRemoval(File input, File output, Color override, int tolerance) {
        return BackgroundRemoval.backgroundRemoval(input, output, override, tolerance);
    }

    public static BufferedImage backgroundRemoval(BufferedImage bufferedImage, Color override, int tolerance) {
        return BackgroundRemoval.backgroundRemoval(bufferedImage, override, tolerance);
    }

    public static BufferedImage backgroundRemoval(ByteArrayOutputStream outputStream, Color override, int tolerance) {
        return BackgroundRemoval.backgroundRemoval(outputStream, override, tolerance);
    }

    public static BufferedImage colorConvert(ColorSpace colorSpace, BufferedImage image) {
        return filter((BufferedImageOp) new ColorConvertOp(colorSpace, (RenderingHints) null), image);
    }

    public static BufferedImage transform(AffineTransform xform, BufferedImage image) {
        return filter((BufferedImageOp) new AffineTransformOp(xform, (RenderingHints) null), image);
    }

    public static BufferedImage filter(BufferedImageOp op, BufferedImage image) {
        return op.filter(image, (BufferedImage) null);
    }

    public static Image filter(ImageFilter filter, Image image) {
        return Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), filter));
    }
}
