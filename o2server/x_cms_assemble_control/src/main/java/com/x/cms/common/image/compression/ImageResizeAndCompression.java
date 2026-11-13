package com.x.cms.common.image.compression;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

  
/**
 * 이미지 압축 도구 클래스 제공 메서드로 생성할 썸네일 크기, 압축 비율, 이미지 품질 등을 설정 가능
 */
public class ImageResizeAndCompression {  
  
    /** 
     * * 이미지 파일 읽기 
     *  
     * @param srcImgPath 
     * @return 
     */  
    private static BufferedImage InputImage(String srcImgPath) throws RuntimeException {  
  
        BufferedImage srcImage = null;
        FileInputStream in = null;
        try {  
            // BufferedImage 객체 생성  
            File file = new File(srcImgPath);  
            in = new FileInputStream(file);  
            byte[] b = new byte[5];  
            in.read(b);  
            srcImage = javax.imageio.ImageIO.read(file);  
        } catch (IOException e) {  
        	e.printStackTrace();  
        	throw new RuntimeException("이미지 파일을 읽는 중 오류가 발생했습니다!", e);
        } finally {
        	if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					throw new RuntimeException("이미지 파일을 읽는 중 오류가 발생했습니다!", e);
				}
			}
        }
        return srcImage;  
    }  
  
    /** 
     * * 지정 이미지 크기와 원본 이미지 품질로 이미지 압축(기본 품질 1) 
     *  
     * @param srcImgPath 
     *            :원본 이미지 경로 
     * @param outImgPath 
     *            :출력할 압축 이미지 경로 
     * @param new_w 
     *            :압축 후 이미지 너비 
     * @param new_h 
     *            :압축 후 이미지 높이 
     */  
    public static void resize(String srcImgPath, String outImgPath,  
            int new_w, int new_h) {  
        resize(srcImgPath, outImgPath, new_w, new_h, 1F);  
    }  
  
    /** 
     * 지정 크기 비율과 원본 이미지 품질로 이미지 압축(기본 품질 1) 
     *  
     * @param srcImgPath 
     *            :원본 이미지 경로 
     * @param outImgPath 
     *            :출력할 압축 이미지 경로 
     * @param ratio 
     *            :압축 후 이미지 크기 비율 
     * @param per 
     *            :백분율 
     */  
    public static void resize(String srcImgPath, String outImgPath,   float ratio) {  
        resize(srcImgPath, outImgPath, ratio, 1F);  
    }  
  
    /** 
     * 지정 길이 또는 너비의 최댓값으로 이미지 압축(기본 품질 1) 
     *  
     * @param srcImgPath 
     *            :원본 이미지 경로 
     * @param outImgPath 
     *            :출력할 압축 이미지 경로 
     * @param maxLength 
     *            :길이 또는 너비의 최댓값 
     * @param per 
     *            :이미지 품질 
     */  
    public static void resize(String srcImgPath, String outImgPath,  
            int maxLength) {  
        resize(srcImgPath, outImgPath, maxLength, 1F);  
    }  
  
    /** 
     * * 지정 이미지 크기와 이미지 품질로 이미지 압축 
     *  
     * @param srcImgPath 
     *            :원본 이미지 경로 
     * @param outImgPath 
     *            :출력할 압축 이미지 경로 
     * @param new_w 
     *            :압축 후 이미지 너비 
     * @param new_h 
     *            :압축 후 이미지 높이 
     * @param per 
     *            :백분율 
     * @author cevencheng
     */  
    public static void resize(String srcImgPath, String outImgPath,  
            int new_w, int new_h, float per) {  
        // 이미지 가져오기  
        BufferedImage src = InputImage(srcImgPath);  
        int old_w = src.getWidth();  
        // 원본 이미지 너비 가져오기  
        int old_h = src.getHeight();  
        // 원본 이미지 길이 가져오기  
        // 원본 이미지 크기로 빈 캔버스 생성  
        BufferedImage tempImg = new BufferedImage(old_w, old_h,  BufferedImage.TYPE_INT_RGB);  
        // 새 캔버스에 원본 이미지의 썸네일 생성  
        Graphics2D g = tempImg.createGraphics();  
        g.setColor(Color.white);  
        g.fillRect(0, 0, old_w, old_h);  
        g.drawImage(src, 0, 0, old_w, old_h, Color.white, null);  
        g.dispose();  
        BufferedImage newImg = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_RGB);  
        newImg.getGraphics().drawImage(  
                tempImg.getScaledInstance(new_w, new_h, Image.SCALE_SMOOTH), 0,  
                0, null);  
        // 메서드를 호출해 이미지 파일 출력  
        outImage(outImgPath, newImg, per);  
    }  
  
    /** 
     * * 지정 크기 비율과 이미지 품질로 이미지 압축 
     *  
     * @param srcImgPath 
     *            :원본 이미지 경로 
     * @param outImgPath 
     *            :출력할 압축 이미지 경로 
     * @param ratio 
     *            :압축 후 이미지 크기 비율 
     * @param per 
     *            :백분율 
     * @author cevencheng
     */  
    public static void resize(String srcImgPath, String outImgPath,  
            float ratio, float per) {  
        // 이미지 가져오기  
        BufferedImage src = InputImage(srcImgPath);  
        int old_w = src.getWidth();  
        // 원본 이미지 너비 가져오기  
        int old_h = src.getHeight();  
        // 원본 이미지 길이 가져오기  
        int new_w = 0;  
        // 새 이미지 너비  
        int new_h = 0;  
        // 새 이미지 길이  
        BufferedImage tempImg = new BufferedImage(old_w, old_h,  
                BufferedImage.TYPE_INT_RGB);  
        Graphics2D g = tempImg.createGraphics();  
        g.setColor(Color.white);  
        // 원본 이미지에서 색을 가져와 새 이미지 그리기g.fillRect(0, 0, old_w, old_h);  
        g.drawImage(src, 0, 0, old_w, old_h, Color.white, null);  
        g.dispose();  
        // 이미지 크기 압축 비율로 새 이미지 크기 계산new_w = (int) Math.round(old_w * ratio);  
        new_h = (int) Math.round(old_h * ratio);  
        BufferedImage newImg = new BufferedImage(new_w, new_h,  
                BufferedImage.TYPE_INT_RGB);  
        newImg.getGraphics().drawImage(  
                tempImg.getScaledInstance(new_w, new_h, Image.SCALE_SMOOTH), 0,  
                0, null);  
        // 메서드를 호출해 이미지 파일 출력OutImage(outImgPath, newImg, per);  
    }  
  
    /** 
     * <b>
     * 길이 또는 너비의 최댓값을 지정해 이미지 압축
     * 	이 메서드 사용 권장 
     * </b>
     * @param srcImgPath 
     *            :원본 이미지 경로 
     * @param outImgPath 
     *            :출력할 압축 이미지 경로 
     * @param maxLength 
     *            :길이 또는 너비의 최댓값 
     * @param per 
     *            :이미지 품질 
     * @author cevencheng
     */  
    public static void resize(String srcImgPath, String outImgPath, int maxLength, float per) {  
        // 이미지 가져오기  
        BufferedImage src = InputImage(srcImgPath);  
        int old_w = src.getWidth();  
        // 원본 이미지 너비 가져오기  
        int old_h = src.getHeight();  
        // 원본 이미지 길이 가져오기  
        int new_w = 0;  
        // 새 이미지 너비  
        int new_h = 0;  
        // 새 이미지 길이  
        BufferedImage tempImg = new BufferedImage(old_w, old_h,  
                BufferedImage.TYPE_INT_RGB);  
        Graphics2D g = tempImg.createGraphics();  
        g.setColor(Color.white);  
        // 원본 이미지에서 색을 가져와 새 이미지 그리기  
        g.fillRect(0, 0, old_w, old_h);  
        g.drawImage(src, 0, 0, old_w, old_h, Color.white, null);  
        g.dispose();  
        // 이미지 크기 압축 비율로 새 이미지 크기 계산  
        if (old_w > old_h) {  
            // 이미지 축소 비율  
            new_w = maxLength;  
            new_h = (int) Math.round(old_h * ((float) maxLength / old_w));  
        } else {  
            new_w = (int) Math.round(old_w * ((float) maxLength / old_h));  
            new_h = maxLength;  
        }  
        BufferedImage newImg = new BufferedImage(new_w, new_h,  
                BufferedImage.TYPE_INT_RGB);  
        newImg.getGraphics().drawImage(  
                tempImg.getScaledInstance(new_w, new_h, Image.SCALE_SMOOTH), 0,  
                0, null);  
        // 메서드를 호출해 이미지 파일 출력  
        outImage(outImgPath, newImg, per);  
    }  
    
    /**
     * 이미지를 지정 너비로 압축, 높이는 비율에 맞게 조정
     * 
     * @param srcImgPath
     * @param outImgPath
     * @param width
     * @param per
     */
    public static void resizeFixedWidth(String srcImgPath, String outImgPath,  
    		int width, float per) {  
    	// 이미지 가져오기  
    	BufferedImage src = InputImage(srcImgPath);  
    	int old_w = src.getWidth();  
    	// 원본 이미지 너비 가져오기  
    	int old_h = src.getHeight();  
    	// 원본 이미지 길이 가져오기  
    	int new_w = 0;  
    	// 새 이미지 너비  
    	int new_h = 0;  
    	// 새 이미지 길이  
    	BufferedImage tempImg = new BufferedImage(old_w, old_h,  
    			BufferedImage.TYPE_INT_RGB);  
    	Graphics2D g = tempImg.createGraphics();  
    	g.setColor(Color.white);  
    	// 원본 이미지에서 색을 가져와 새 이미지 그리기  
    	g.fillRect(0, 0, old_w, old_h);  
    	g.drawImage(src, 0, 0, old_w, old_h, Color.white, null);  
    	g.dispose();  
    	// 이미지 크기 압축 비율로 새 이미지 크기 계산  
    	if (old_w > old_h) {  
    		// 이미지 축소 비율  
    		new_w = width;  
    		new_h = (int) Math.round(old_h * ((float) width / old_w));  
    	} else {  
    		new_w = (int) Math.round(old_w * ((float) width / old_h));  
    		new_h = width;  
    	}  
    	BufferedImage newImg = new BufferedImage(new_w, new_h,  
    			BufferedImage.TYPE_INT_RGB);  
    	newImg.getGraphics().drawImage(  
    			tempImg.getScaledInstance(new_w, new_h, Image.SCALE_SMOOTH), 0,  
    			0, null);  
    	// 메서드를 호출해 이미지 파일 출력  
    	outImage(outImgPath, newImg, per);  
    }  
  
    /** 
     * * 이미지 파일을 지정 경로로 출력, 압축 품질 설정 가능 
     *  
     * @param outImgPath 
     * @param newImg 
     * @param per 
     * @author cevencheng
     */  
    private static void outImage( String outImgPath, BufferedImage newImg, float per ) {  
        // 출력 폴더 경로가 존재하는지 판단, 없으면 생성  
        File file = new File(outImgPath);  
        if (!file.getParentFile().exists()) {  
            file.getParentFile().mkdirs();  
        }
        // 파일 스트림으로 출력
        FileOutputStream fos = null;
        try {  
            fos = new FileOutputStream( outImgPath );
            ImageIO.write( newImg,  "jpeg" , file );
            newImg.flush();  
        } catch (Exception e) { 
        	throw new RuntimeException(e);
        } finally {
        	if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
        }
    }  
    
   /**
    * 이미지 파일을 지정 출력 스트림으로 출력, 압축 품질 설정 가능 
    * @param dest
    * @param newImg
    * @param per
    */
    private static void outImage( OutputStream dest, BufferedImage newImg, float per ) {  
        try { 
            ImageIO.write( newImg,  "jpeg" , dest );
        } catch (Exception e) { 
        	throw new RuntimeException(e);
        }
    }  
  
    /**
     * 이미지 자르기 도구 메서드
     * 
     * @param srcfile 원본 이미지
     * @param outfile 자른 후의 이미지
     * @param x 자르기 꼭짓점 X 좌표
     * @param y 자르기 꼭짓점 Y 좌표
     * @param width 자르기 영역 너비
     * @param height 자르기 영역 높이
     * 
     * @throws IOException
     * @author cevencheng
     */
	public static void cut( File srcfile, File outfile, int x, int y, int width, int height ) throws IOException {
		FileInputStream is = null;
		ImageInputStream iis = null;
		try {
			// 이미지 파일 읽기
			is = new FileInputStream(srcfile);
			/*
			 * 지정 형식을 디코딩할 수 있다고 주장하는, 현재 등록된 모든 ImageReader를 포함한 Iterator 반환.
			 * 매개변수: formatName - 비공식 형식 이름 포함 .(예: "jpeg" 또는 "tiff") 등 .
			 */
			Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("jpg");
			ImageReader reader = it.next();
			// 이미지 스트림 가져오기
			iis = ImageIO.createImageInputStream(is);

			/*
			 * <p>iis: 읽기 소스.true: 앞으로만 검색 </p>. 「앞으로만 검색」으로 표시.
			 * 이 설정은 입력 소스의 이미지를 순서대로만 읽음을 의미하며, reader가 이전에 읽은 이미지 관련 데이터를 포함한 입력 부분의 캐시를 피할 수 있게 함.
			 */
			reader.setInput(iis, true);

			/*
			 * <p>스트림 디코딩 방법을 설명하는 클래스<p>. 입력 시 Java Image I/O
			 * 프레임워크 컨텍스트의 스트림에서 이미지 하나 또는 여러 개를 변환하는 방법 지정에 사용. 특정 이미지 형식의 플러그인은 해당 ImageReader 구현의
			 * getDefaultReadParam 메서드에서 ImageReadParam 인스턴스를 반환.
			 */
			ImageReadParam param = reader.getDefaultReadParam();

			/*
			 * 이미지 자르기 영역. Rectangle은 좌표 공간의 한 영역을 지정하며, Rectangle 객체
			 * 의 왼쪽 위 꼭짓점 좌표(x, y), 너비, 높이로 이 영역을 정의할 수 있음.
			 */
			Rectangle rect = new Rectangle(x, y, width, height);

			// 픽셀 데이터 디코딩 대상으로 사용할 BufferedImage 제공.
			param.setSourceRegion(rect);

			/*
			 * 제공된 ImageReadParam으로 인덱스 imageIndex가 지정한 객체를 읽어 완전한
			 * BufferedImage로 반환.
			 */
			BufferedImage bi = reader.read(0, param);

			// 새 이미지 저장
			ImageIO.write(bi, "jpg", outfile);
		} finally {
			if (is != null) {
				is.close();
			}
			if (iis != null) {
				iis.close();
			}
		}
    }
    
	
	/**
     * 먼저 압축 후 자르는 도구 메서드
     * 
     * @param srcfile 원본 이미지
     * @param outfile 자른 후의 이미지
     * @param width 자르기 영역 너비
     * @param height 자르기 영역 높이
     * 
     * @throws IOException
     * @author cevencheng
     */
	public static void cut( String srcImgPath, String outImgPath, int width, int height ) throws IOException {
        BufferedImage src = InputImage( srcImgPath );  // 이미지 객체 가져오기
        int old_w = src.getWidth();  //원본 이미지 너비 가져오기  
        int old_h = src.getHeight();  //원본 이미지 높이 가져오기
        int new_w = width;
        int new_h = height;
        File outfile = new File(outImgPath);
        BufferedImage tempImg = new BufferedImage( old_w, old_h, BufferedImage.TYPE_INT_RGB );  
        Graphics2D g = tempImg.createGraphics();  
        g.setColor( Color.white );  
        // 원본 이미지에서 색을 가져와 새 이미지 그리기
        g.fillRect(0, 0, old_w, old_h);  
        g.drawImage( src, 0, 0, old_w, old_h, Color.white, null );  
        g.dispose();
        // 이미지 크기 압축 비율로 새 이미지 크기 계산  
        if( ( old_w*1.0 )/new_w < ( new_h*1.0 )/new_h ){//세로 축소 비율이 큼
            if( old_w > new_w ){//실제 이미지 너비가 예상 축소 너비보다 크므로 높이를 비율에 맞게 축소 필요
            	new_h = Integer.parseInt( new java.text.DecimalFormat("0").format(old_h * new_w/(old_w*1.0)) );
            }
        } else {//가로 확대 비율이 큼
            if(old_h > new_h){
            	new_w = Integer.parseInt(new java.text.DecimalFormat("0").format(old_w * new_h/(old_h*1.0)));
            }
        }        
         
        BufferedImage newImg = new BufferedImage( new_w, new_h, BufferedImage.TYPE_INT_RGB);  
        newImg.getGraphics().drawImage(   tempImg.getScaledInstance( new_w, new_h, Image.SCALE_SMOOTH), 0,  0, null );
        
        //자르기 준비
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 메서드를 호출해 이미지 파일 출력
        outImage( baos, newImg, 1 );
		
        ByteArrayInputStream is = new ByteArrayInputStream(baos.toByteArray());
		ImageInputStream iis = null;
		try {
			/*
			 * 지정 형식을 디코딩할 수 있다고 주장하는, 현재 등록된 모든 ImageReader를 포함한 Iterator 반환.
			 * 매개변수: formatName - 비공식 형식 이름 포함 .(예: "jpeg" 또는 "tiff") 등 .
			 */
			Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("jpg");
			ImageReader reader = it.next();
			// 이미지 스트림 가져오기
			iis = ImageIO.createImageInputStream(is);
			/*
			 * <p>iis: 읽기 소스.true: 앞으로만 검색 </p>. 「앞으로만 검색」으로 표시.
			 * 이 설정은 입력 소스의 이미지를 순서대로만 읽음을 의미하며, reader가 이전에 읽은 이미지 관련 데이터를 포함한 입력 부분의 캐시를 피할 수 있게 함.
			 */
			reader.setInput(iis, true);
			/*
			 * <p>스트림 디코딩 방법을 설명하는 클래스<p>. 입력 시 Java Image I/O
			 * 프레임워크 컨텍스트의 스트림에서 이미지 하나 또는 여러 개를 변환하는 방법 지정에 사용. 특정 이미지 형식의 플러그인은 해당 ImageReader 구현의
			 * getDefaultReadParam 메서드에서 ImageReadParam 인스턴스를 반환.
			 */
			ImageReadParam param = reader.getDefaultReadParam();
			/*
			 * 이미지 자르기 영역. Rectangle은 좌표 공간의 한 영역을 지정하며, Rectangle 객체
			 * 의 왼쪽 위 꼭짓점 좌표(x, y), 너비, 높이로 이 영역을 정의할 수 있음.
			 */
			int x=0, y=0;
//			if( new_w > width ){ //수평 가운데 자르기
//				x = ( new_w-width )/2;
//			}else{
//				y = ( new_h-height )/2;
//			}
			Rectangle rect = new Rectangle( x, y, width, height);

			// 픽셀 데이터 디코딩 대상으로 사용할 BufferedImage 제공.
			param.setSourceRegion(rect);
			/*
			 * 제공된 ImageReadParam으로 인덱스 imageIndex가 지정한 객체를 읽어 완전한
			 * BufferedImage로 반환.
			 */
			BufferedImage bi = reader.read(0, param);
			// 새 이미지 저장
			ImageIO.write(bi, "jpg", outfile);
		} finally {
			if (is != null) {
				is.close();
			}
			if (iis != null) {
				iis.close();
			}
		}
    }
	
	public static BufferedImage cut( BufferedImage src, Integer width, Integer height ) throws IOException {
	        int old_w = src.getWidth();  //원본 이미지 너비 가져오기  
	        int old_h = src.getHeight();  //원본 이미지 높이 가져오기
	        int new_w = width;
	        int new_h = height;
	        BufferedImage tempImg = new BufferedImage( old_w, old_h, BufferedImage.TYPE_INT_RGB );  
	        Graphics2D g = tempImg.createGraphics();  
	        g.setColor( Color.white );  
	        // 원본 이미지에서 색을 가져와 새 이미지 그리기
	        g.fillRect(0, 0, old_w, old_h);  
	        g.drawImage( src, 0, 0, old_w, old_h, Color.white, null );  
	        g.dispose();
	        // 이미지 크기 압축 비율로 새 이미지 크기 계산  
	        if( ( old_w*1.0 )/new_w < ( new_h*1.0 )/new_h ){//세로 축소 비율이 큼
	            if( old_w > new_w ){//실제 이미지 너비가 예상 축소 너비보다 크므로 높이를 비율에 맞게 축소 필요
	            	new_h = Integer.parseInt( new java.text.DecimalFormat("0").format(old_h * new_w/(old_w*1.0)) );
	            }
	        } else {//가로 확대 비율이 큼
	            if(old_h > new_h){
	            	new_w = Integer.parseInt(new java.text.DecimalFormat("0").format(old_w * new_h/(old_h*1.0)));
	            }
	        }        
	         
	        BufferedImage newImg = new BufferedImage( new_w, new_h, BufferedImage.TYPE_INT_RGB);  
	        newImg.getGraphics().drawImage(   tempImg.getScaledInstance( new_w, new_h, Image.SCALE_SMOOTH), 0,  0, null );
	        
	        //자르기 준비
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        // 메서드를 호출해 이미지 파일 출력
	        outImage( baos, newImg, 1 );
			
	        ByteArrayInputStream is = new ByteArrayInputStream(baos.toByteArray());
			ImageInputStream iis = null;
			try {
				/*
				 * 지정 형식을 디코딩할 수 있다고 주장하는, 현재 등록된 모든 ImageReader를 포함한 Iterator 반환.
				 * 매개변수: formatName - 비공식 형식 이름 포함 .(예: "jpeg" 또는 "tiff") 등 .
				 */
				Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("jpg");
				ImageReader reader = it.next();
				// 이미지 스트림 가져오기
				iis = ImageIO.createImageInputStream(is);
				/*
				 * <p>iis: 읽기 소스.true: 앞으로만 검색 </p>. 「앞으로만 검색」으로 표시.
				 * 이 설정은 입력 소스의 이미지를 순서대로만 읽음을 의미하며, reader가 이전에 읽은 이미지 관련 데이터를 포함한 입력 부분의 캐시를 피할 수 있게 함.
				 */
				reader.setInput(iis, true);
				/*
				 * <p>스트림 디코딩 방법을 설명하는 클래스<p>. 입력 시 Java Image I/O
				 * 프레임워크 컨텍스트의 스트림에서 이미지 하나 또는 여러 개를 변환하는 방법 지정에 사용. 특정 이미지 형식의 플러그인은 해당 ImageReader 구현의
				 * getDefaultReadParam 메서드에서 ImageReadParam 인스턴스를 반환.
				 */
				ImageReadParam param = reader.getDefaultReadParam();
				/*
				 * 이미지 자르기 영역. Rectangle은 좌표 공간의 한 영역을 지정하며, Rectangle 객체
				 * 의 왼쪽 위 꼭짓점 좌표(x, y), 너비, 높이로 이 영역을 정의할 수 있음.
				 */
				int x=0, y=0;
				if( new_w > width ){ //수평 가운데 자르기
					x = ( new_w-width )/2;
				}else{
					y = ( new_h-height )/2;
				}
				Rectangle rect = new Rectangle( x, y, width, height);

				// 픽셀 데이터 디코딩 대상으로 사용할 BufferedImage 제공.
				param.setSourceRegion(rect);
				/*
				 * 제공된 ImageReadParam으로 인덱스 imageIndex가 지정한 객체를 읽어 완전한
				 * BufferedImage로 반환.
				 */
				BufferedImage bi = reader.read(0, param);
				// 새 이미지 저장
				return bi;
			} finally {
				if (is != null) {
					is.close();
				}
				if (iis != null) {
					iis.close();
				}
			}
	}
	
    public static void main( String args[] ) throws Exception {  
        String srcImg = "d:/Test.png";  
        String tarDir = "d:/Test_400_400.png";
        long startTime = new Date().getTime(); 
        cut( srcImg, tarDir, 400, 250 );
        System.out.println(new Date().getTime() - startTime);  
    }  
}  

	    			