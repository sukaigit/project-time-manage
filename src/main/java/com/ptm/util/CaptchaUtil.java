package com.ptm.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

/**
 * 验证码工具类：生成4位随机字母数字验证码图片并返回Base64字符串
 */
public class CaptchaUtil {

    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final int CODE_LENGTH = 4;
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final Random RANDOM = new Random();

    /**
     * 生成验证码
     *
     * @return 包含验证码文本和Base64图片的 CaptchaResult
     */
    public static CaptchaResult generateCaptcha() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 背景
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 边框
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(0, 0, WIDTH - 1, HEIGHT - 1);

        // 干扰线
        g.setColor(new Color(200, 200, 200));
        for (int i = 0; i < 6; i++) {
            int x1 = RANDOM.nextInt(WIDTH);
            int y1 = RANDOM.nextInt(HEIGHT);
            int x2 = RANDOM.nextInt(WIDTH);
            int y2 = RANDOM.nextInt(HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }

        // 噪点
        g.setColor(new Color(180, 180, 180));
        for (int i = 0; i < 40; i++) {
            int x = RANDOM.nextInt(WIDTH);
            int y = RANDOM.nextInt(HEIGHT);
            g.drawRect(x, y, 1, 1);
        }

        // 验证码字符
        Font font = new Font("Arial", Font.BOLD | Font.ITALIC, 24);
        g.setFont(font);
        for (int i = 0; i < CODE_LENGTH; i++) {
            char c = CHARS.charAt(RANDOM.nextInt(CHARS.length()));
            code.append(c);
            g.setColor(new Color(RANDOM.nextInt(150), RANDOM.nextInt(150), RANDOM.nextInt(150)));
            int x = 10 + i * 26;
            int y = 30 + RANDOM.nextInt(6);
            g.drawString(String.valueOf(c), x, y);
        }

        g.dispose();

        // 转 Base64
        String base64;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            base64 = Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            throw new RuntimeException("验证码图片生成失败", e);
        }

        // 文本转为大写
        String codeText = code.toString();
        return new CaptchaResult(codeText.toUpperCase(), "data:image/png;base64," + base64);
    }

    /**
     * 验证码结果
     */
    public static class CaptchaResult {
        private final String code;
        private final String image;

        public CaptchaResult(String code, String image) {
            this.code = code;
            this.image = image;
        }

        public String getCode() {
            return code;
        }

        public String getImage() {
            return image;
        }
    }
}
