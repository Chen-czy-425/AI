package com.aiProject.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * 图片验证码工具类（原生Java实现，无任何第三方依赖）
 */
public class CaptchaUtil {

    private static final int WIDTH = 120;    // 图片宽度
    private static final int HEIGHT = 40;    // 图片高度
    private static final int LENGTH = 4;     // 验证码字符个数
    private static final String CODE_CHAR = "ABCDEFGHJKLMNPQRSTUVWXYZ2345678"; // 验证码字符集（去掉易混淆字符）

    /**
     * 生成验证码图片，并输出到输出流
     * @param out 输出流（响应流/文件流）
     * @return 验证码文本
     */
    public static String generateCaptcha(OutputStream out) throws IOException {
        // 1. 创建图片对象
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 2. 设置背景色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 3. 生成随机验证码
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < LENGTH; i++) {
            String ch = String.valueOf(CODE_CHAR.charAt(random.nextInt(CODE_CHAR.length())));
            code.append(ch);

            // 随机颜色
            g.setColor(new Color(random.nextInt(150), random.nextInt(150), random.nextInt(150)));
            g.setFont(new Font("Arial", Font.BOLD, 28));
            // 画出字符
            g.drawString(ch, 20 + i * 22, 30);
        }

        // 4. 添加干扰线（防机器识别）
        g.setColor(Color.GRAY);
        for (int i = 0; i < 6; i++) {
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }

        // 5. 释放资源
        g.dispose();

        // 6. 输出图片
        ImageIO.write(image, "png", out);
        return code.toString();
    }
}
