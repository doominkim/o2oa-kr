package com.x.program.center.jaxrs.captcha;

import com.x.base.core.project.bean.tuple.Quadruple;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.util.Random;
import javax.imageio.ImageIO;

public class CaptchaGenerator {

    private final int width;
    private final int height;
    private int noiseLineCount = 15;
    private final Random random = new SecureRandom();

    public CaptchaGenerator(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public static CaptchaGenerator of(int width, int height) {
        return new CaptchaGenerator(width, height);
    }

    public Quadruple<Integer, Integer, BufferedImage, Integer> generator() {
        // 1. 난수 및 정답 생성
        int num1 = random.nextInt(50); // 0 to 99
        int num2 = random.nextInt(50); // 0 to 99
        int answer = num1 + num2;
        String expression = String.format("%d+%d=?", num1, num2);

        // 2. 이미지 버퍼 생성
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 안티앨리어싱 설정으로 글자를 선명하게
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 3. 배경 그리기
        g2d.setColor(getRandomColor(200, 250)); // 밝은 배경
        g2d.fillRect(0, 0, width, height);

        // 4. 방해선 그리기
        g2d.setColor(getRandomColor(160, 200)); // 방해선 색상
        g2d.setStroke(new BasicStroke(1.5f)); // 선 굵기 설정
        for (int i = 0; i < noiseLineCount; i++) {
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            g2d.drawLine(x1, y1, x2, y2);
        }

        // 5. 인증 코드 텍스트 그리기
        // 글꼴 설정 (난이도를 높이려면 글꼴을 무작위 선택 가능)
        g2d.setFont(new Font(null, Font.BOLD | Font.ITALIC, height - 15)); // 글꼴 크기는 이미지 높이에 맞춰 조정

        // 글자 색상 설정 (진한 색)
        g2d.setColor(getRandomColor(20, 130));

        // 글자 위치를 계산해 가운데 정렬 (근사)
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(expression);
        int textAscent = fm.getAscent(); // 글자 기준선 위 높이
        int textDescent = fm.getDescent(); // 글자 기준선 아래 높이

        // 중심 X 좌표 + 약간의 무작위 오프셋
        int x = (width - textWidth) / 2 + random.nextInt(6) - 3;
        // 중심 Y 좌표 (Ascent 기준 조정으로 세로 가운데 정렬) + 약간의 무작위 오프셋
        int y = (height - (textAscent + textDescent)) / 2 + textAscent + random.nextInt(6) - 3;

        // 문자열 그리기
        g2d.drawString(expression, x, y);

        // 6. 리소스 정리
        g2d.dispose();

        // 7. 결과 반환
        return Quadruple.of(height, width, image, answer);
    }

    // --- 보조 메서드: 무작위 색상 조회 ---
    private Color getRandomColor(int fc, int bc) {
        // 지정 범위에서 무작위 색상 생성
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        if (fc < 0) {
            fc = 0;
        }
        if (bc < 0) {
            bc = 0;
        }
        if (fc > bc) {
            fc = bc - 1; // fc <= bc 보장
        }

        int r = fc + random.nextInt(bc - fc + 1);
        int g = fc + random.nextInt(bc - fc + 1);
        int b = fc + random.nextInt(bc - fc + 1);
        return new Color(r, g, b);
    }

    // --- Getters/Setters (선택) ---
    public void setNoiseLineCount(int noiseLineCount) {
        this.noiseLineCount = noiseLineCount;
    }

}
