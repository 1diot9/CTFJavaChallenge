package cn.hutool.captcha;

import cn.hutool.core.img.GraphicsUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/captcha/CircleCaptcha.class */
public class CircleCaptcha extends AbstractCaptcha {
    private static final long serialVersionUID = -7096627300356535494L;

    public CircleCaptcha(int width, int height) {
        this(width, height, 5);
    }

    public CircleCaptcha(int width, int height, int codeCount) {
        this(width, height, codeCount, 15);
    }

    public CircleCaptcha(int width, int height, int codeCount, int interfereCount) {
        super(width, height, codeCount, interfereCount);
    }

    @Override // cn.hutool.captcha.AbstractCaptcha
    public Image createImage(String code) {
        BufferedImage image = new BufferedImage(this.width, this.height, 1);
        Graphics2D g = ImgUtil.createGraphics(image, (Color) ObjectUtil.defaultIfNull(this.background, Color.WHITE));
        drawInterfere(g);
        drawString(g, code);
        return image;
    }

    private void drawString(Graphics2D g, String code) {
        if (null != this.textAlpha) {
            g.setComposite(this.textAlpha);
        }
        GraphicsUtil.drawStringColourful(g, code, this.font, this.width, this.height);
    }

    private void drawInterfere(Graphics2D g) {
        ThreadLocalRandom random = RandomUtil.getRandom();
        for (int i = 0; i < this.interfereCount; i++) {
            g.setColor(ImgUtil.randomColor(random));
            g.drawOval(random.nextInt(this.width), random.nextInt(this.height), random.nextInt(this.height >> 1), random.nextInt(this.height >> 1));
        }
    }
}
