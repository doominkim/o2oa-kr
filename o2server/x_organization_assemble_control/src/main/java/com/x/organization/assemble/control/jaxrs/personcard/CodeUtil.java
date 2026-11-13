package com.x.organization.assemble.control.jaxrs.personcard;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

public class CodeUtil {
	private static final int QRCOLOR = 0xFF000000; // 기본값은 검은색
	private static final int BGWHITE = 0xFFFFFFFF; // 배경 색상
	private static final int WIDTH = 300; // QR 코드 너비
	private static final int HEIGHT = 300; // QR 코드 높이

	/**
	 * logo가 포함된 QR 코드 이미지 생성
	 * 
	 * @param logoFile /logo 이미지 파일
	 * @param codeFile /QR 코드 이미지
	 * @param qrUrl    /QR 코드에 저장할 정보: vcard 형식
	 * @param note     /QR 코드 설명 정보
	 */
	public static void drawLogoQRCode(File logoFile, File codeFile, String qrUrl, String note) {
		try {
			MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
			// 매개변수 순서: 인코딩 내용, 인코딩 유형, 생성 이미지 너비, 생성 이미지 높이, 설정 매개변수
			BitMatrix bm = multiFormatWriter.encode(qrUrl, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, hints);
			BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

			// QR 코드 데이터로 Bitmap 이미지 생성 시작, 각각 검정(0xFFFFFFFF) 흰색(0xFF000000) 두 색으로 설정
			for (int x = 0; x < WIDTH; x++) {
				for (int y = 0; y < HEIGHT; y++) {
					image.setRGB(x, y, bm.get(x, y) ? QRCOLOR : BGWHITE);
				}
			}

			int width = image.getWidth();
			int height = image.getHeight();
			if (Objects.nonNull(logoFile) && logoFile.exists()) {
				// 그리기 객체 생성
				Graphics2D g = image.createGraphics();
				// Logo 이미지 읽기
				BufferedImage logo = ImageIO.read(logoFile);
				// logo 이미지 그리기 시작
				g.drawImage(logo, width * 2 / 5, height * 2 / 5, width * 2 / 10, height * 2 / 10, null);
				g.dispose();
				logo.flush();
			}

			// 사용자 정의 텍스트 설명
			if (StringUtils.isNotEmpty(note)) {
				// 새 이미지, logo가 포함된 QR 코드 아래에 텍스트 추가
				BufferedImage outImage = new BufferedImage(400, 445, BufferedImage.TYPE_4BYTE_ABGR);
				Graphics2D outg = outImage.createGraphics();
				// 새 패널에 QR 코드 그리기
				outg.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
				// 새 패널에 텍스트 그리기
				outg.setColor(Color.BLACK);
				outg.setFont(new Font("楷体", Font.BOLD, 30)); // 글꼴, 글꼴 스타일, 글꼴 크기
				int strWidth = outg.getFontMetrics().stringWidth(note);
				if (strWidth > WIDTH) {
					// //길이가 너무 길면 앞부분만 자름
					// 길이가 너무 길면 줄바꿈
					String note1 = note.substring(0, note.length() / 2);
					String note2 = note.substring(note.length() / 2, note.length());
					int strWidth1 = outg.getFontMetrics().stringWidth(note1);
					int strWidth2 = outg.getFontMetrics().stringWidth(note2);
					outg.drawString(note1, 200 - strWidth1 / 2, height + (outImage.getHeight() - height) / 2 + 12);
					BufferedImage outImage2 = new BufferedImage(400, 485, BufferedImage.TYPE_4BYTE_ABGR);
					Graphics2D outg2 = outImage2.createGraphics();
					outg2.drawImage(outImage, 0, 0, outImage.getWidth(), outImage.getHeight(), null);
					outg2.setColor(Color.BLACK);
					outg2.setFont(new Font("宋体", Font.BOLD, 30)); // 글꼴, 글꼴 스타일, 글꼴 크기
					outg2.drawString(note2, 200 - strWidth2 / 2,
							outImage.getHeight() + (outImage2.getHeight() - outImage.getHeight()) / 2 + 5);
					outg2.dispose();
					outImage2.flush();
					outImage = outImage2;
				} else {
					outg.drawString(note, 200 - strWidth / 2, height + (outImage.getHeight() - height) / 2 + 12); // 텍스트 그리기
				}
				outg.dispose();
				outImage.flush();
				image = outImage;
			}

			image.flush();

			ImageIO.write(image, "png", codeFile); // TODO
			if (Objects.nonNull(logoFile) && logoFile.exists()) {
				logoFile.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * logo가 포함된 QR 코드 이미지 생성
	 * 
	 * @param logoFile /logo 이미지 파일
	 * @param codeFile /QR 코드 이미지
	 * @param qrUrl    /QR 코드에 저장할 정보: vcard 형식
	 * @param note     /QR 코드 설명 정보
	 */
	public static byte[] drawLogoQRCodeByte(BufferedImage logo, ByteArrayOutputStream codeFile, String qrUrl,
			String note) {
		byte[] bs = null;
		try {
			MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
			// 매개변수 순서: 인코딩 내용, 인코딩 유형, 생성 이미지 너비, 생성 이미지 높이, 설정 매개변수
			BitMatrix bm = multiFormatWriter.encode(qrUrl, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, hints);
			BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

			// QR 코드 데이터로 Bitmap 이미지 생성 시작, 각각 검정(0xFFFFFFFF) 흰색(0xFF000000) 두 색으로 설정
			for (int x = 0; x < WIDTH; x++) {
				for (int y = 0; y < HEIGHT; y++) {
					image.setRGB(x, y, bm.get(x, y) ? QRCOLOR : BGWHITE);
				}
			}

			int width = image.getWidth();
			int height = image.getHeight();
			if (Objects.nonNull(logo)) {
				// 그리기 객체 생성
				Graphics2D g = image.createGraphics();
				// Logo 이미지 읽기
				// BufferedImage logo = ImageIO.read(logoFile);
				// logo 이미지 그리기 시작
				g.drawImage(logo, width * 2 / 5, height * 2 / 5, width * 2 / 10, height * 2 / 10, null);
				g.dispose();
				logo.flush();
			}

			// 사용자 정의 텍스트 설명
			if (StringUtils.isNotEmpty(note)) {
				// 새 이미지, logo가 포함된 QR 코드 아래에 텍스트 추가
				BufferedImage outImage = new BufferedImage(400, 445, BufferedImage.TYPE_4BYTE_ABGR);
				Graphics2D outg = outImage.createGraphics();
				// 새 패널에 QR 코드 그리기
				outg.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
				// 새 패널에 텍스트 그리기
				outg.setColor(Color.BLACK);
				outg.setFont(new Font("楷体", Font.BOLD, 30)); // 글꼴, 글꼴 스타일, 글꼴 크기
				int strWidth = outg.getFontMetrics().stringWidth(note);
				if (strWidth > WIDTH) {
					// //길이가 너무 길면 앞부분만 자름
					// 길이가 너무 길면 줄바꿈
					String note1 = note.substring(0, note.length() / 2);
					String note2 = note.substring(note.length() / 2, note.length());
					int strWidth1 = outg.getFontMetrics().stringWidth(note1);
					int strWidth2 = outg.getFontMetrics().stringWidth(note2);
					outg.drawString(note1, 200 - strWidth1 / 2, height + (outImage.getHeight() - height) / 2 + 12);
					BufferedImage outImage2 = new BufferedImage(400, 485, BufferedImage.TYPE_4BYTE_ABGR);
					Graphics2D outg2 = outImage2.createGraphics();
					outg2.drawImage(outImage, 0, 0, outImage.getWidth(), outImage.getHeight(), null);
					outg2.setColor(Color.BLACK);
					outg2.setFont(new Font("宋体", Font.BOLD, 30)); // 글꼴, 글꼴 스타일, 글꼴 크기
					outg2.drawString(note2, 200 - strWidth2 / 2,
							outImage.getHeight() + (outImage2.getHeight() - outImage.getHeight()) / 2 + 5);
					outg2.dispose();
					outImage2.flush();
					outImage = outImage2;
				} else {
					outg.drawString(note, 200 - strWidth / 2, height + (outImage.getHeight() - height) / 2 + 12); // 텍스트 그리기
				}
				outg.dispose();
				outImage.flush();
				image = outImage;
			}

			image.flush();

			ImageIO.write(image, "png", codeFile); // TODO
			bs = codeFile.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bs;
	}

	// QR 코드 매개변수 설정용
	private static Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>() {
		private static final long serialVersionUID = 1L;
		{
			// put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);//
			// QR 코드 오류 정정 수준 설정(H가 최고 수준) 상세 수준 정보
			put(EncodeHintType.CHARACTER_SET, "utf-8");// 인코딩 방식 설정
			put(EncodeHintType.MARGIN, 1);
		}
	};

	/**
	 * base64 문자를 디코딩해 파일로 저장
	 *
	 * @param base64Code
	 * @param targetPath
	 * @throws Exception
	 */
	public static File decoderBase64File(String targetPath, String base64Code) throws Exception {
		byte[] buffer = Base64.decodeBase64(base64Code);
		try (FileOutputStream out = new FileOutputStream(targetPath)) {
			out.write(buffer);
		}
		File file = new File(targetPath);
		return file;
	}
}
