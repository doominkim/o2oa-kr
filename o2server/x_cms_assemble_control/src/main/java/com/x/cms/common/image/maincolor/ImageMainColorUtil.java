package com.x.cms.common.image.maincolor;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.x.base.core.project.tools.ListTools;

public class ImageMainColorUtil {
	
	BufferedImage image = null;
	
	public int max(int a, int b, int c) {
		int result = 0;
		if (a >= b && a >= c) {
			result = 1;
		} else if (b >= a && b >= c) {
			result = 2;
		} else if (c >= b && c >= a) {
			result = 3;
		}
		return result;
	}

	// 특정 위치의 픽셀 RBG 가져오기
	public Point getRGB(int x, int y) {
		Point point = new Point();
		int pixel = image.getRGB(x, y);
		point.R = (pixel & 0xff0000) >> 16;
		point.G = (pixel & 0xff00) >> 8;
		point.B = (pixel & 0xff);
		return point;
	}

	public boolean isEnd( List<PointGroup> rootPoint ) {
		Point oldpoint = new Point();
		Point newpoint = new Point();
		for (PointGroup pointGroup : rootPoint) {
			oldpoint.add(pointGroup.point);
			newpoint.add(pointGroup.getNewRGB());
			pointGroup.point = pointGroup.getNewRGB();
		}
		if (oldpoint.equals(newpoint)) {
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Point> getThreeFamilly( List<PointGroup> rootPoint ) {
		List<Point> pointList = new ArrayList<Point>();
		// 흰색에 가까운 색 제거
		Iterator<PointGroup> it = rootPoint.iterator();
		int removedCount_white = 0;
		int removedCount_black = 0;
		while (it.hasNext()) {
			PointGroup i = it.next();
			if (i.point.colorDistance( new Point(255, 255, 255) ) < 10) {
				//흰색에 가까운 색상 값 제거
				it.remove();
				removedCount_white ++;
			}else if (i.point.colorDistance( new Point(0, 0, 0) ) < 10) {
				//검은색에 가까운 색상 값 제거
				it.remove();
				removedCount_black ++;
			}
		}
		// RGB 합계로 색 정렬
		Collections.sort( rootPoint, new pointCampare() );
		// 색이 3개 이하이면 바로 출력
		if ( rootPoint.size() <= 3) {
			for (int i = 0; i < rootPoint.size(); i++ ) {
				pointList.add( rootPoint.get(i).point );
			}
			return pointList;
		}

		int index = 1;
		int min = 100000;
		// 가장 가까운 색 3개 가져오기
		for (int i = 1; i < rootPoint.size() - 1; i++) {
			int temp = rootPoint.get(i + 1).point.colorDistance(rootPoint.get(i).point)
					+ rootPoint.get(i).point.colorDistance(rootPoint.get(i - 1).point);
			if (temp < min) {
				min = temp;
				index = i;
			}
		}
		// R-G-B 중 최댓값 기준 정렬
		int max = 0;
		// RGB 중 최대 성분 가져오기
		if (rootPoint.get(index).point.R >= rootPoint.get(index).point.G && rootPoint.get(index).point.R >= rootPoint.get(index).point.B) {
			max = 1;
		} else if (rootPoint.get(index).point.G >= rootPoint.get(index).point.R && rootPoint.get(index).point.G >= rootPoint.get(index).point.B) {
			max = 2;
		} else {
			max = 3;
		}

		switch (max) {
		// R 값 기준 정렬
		case 1:
			int temp = max(rootPoint.get(index - 1).point.R, rootPoint.get(index).point.R, rootPoint.get(index + 1).point.R);
			pointList.add(rootPoint.get(index - 2 + temp).point);
			if (temp == 1) {
				if (rootPoint.get(index).point.R > rootPoint.get(index + 1).point.R) {
					pointList.add(rootPoint.get(index).point);
					pointList.add(rootPoint.get(index + 1).point);
				} else {
					pointList.add(rootPoint.get(index + 1).point);
					pointList.add(rootPoint.get(index).point);
				}
			}
			if (temp == 2) {
				if (rootPoint.get(index - 1).point.R > rootPoint.get(index + 1).point.R) {
					pointList.add(rootPoint.get(index - 1).point);
					pointList.add(rootPoint.get(index + 1).point);
				} else {
					pointList.add(rootPoint.get(index + 1).point);
					pointList.add(rootPoint.get(index - 1).point);
				}
			}
			if (temp == 3) {
				if (rootPoint.get(index - 1).point.R > rootPoint.get(index).point.R) {
					pointList.add(rootPoint.get(index - 1).point);
					pointList.add(rootPoint.get(index).point);
				} else {
					pointList.add(rootPoint.get(index).point);
					pointList.add(rootPoint.get(index - 1).point);
				}
			}
			break;
		// G 값 기준 정렬
		case 2:
			int temp1 = max(rootPoint.get(index - 1).point.G, rootPoint.get(index).point.G,
					rootPoint.get(index + 1).point.G);
			pointList.add(rootPoint.get(index - 2 + temp1).point);
			if (temp1 == 1) {
				if (rootPoint.get(index).point.R > rootPoint.get(index + 1).point.R) {
					pointList.add(rootPoint.get(index).point);
					pointList.add(rootPoint.get(index + 1).point);
				} else {
					pointList.add(rootPoint.get(index + 1).point);
					pointList.add(rootPoint.get(index).point);
				}
			}
			if (temp1 == 2) {
				if (rootPoint.get(index - 1).point.R > rootPoint.get(index + 1).point.R) {
					pointList.add(rootPoint.get(index - 1).point);
					pointList.add(rootPoint.get(index + 1).point);
				} else {
					pointList.add(rootPoint.get(index + 1).point);
					pointList.add(rootPoint.get(index - 1).point);
				}
			}
			if (temp1 == 3) {
				if (rootPoint.get(index - 1).point.R > rootPoint.get(index).point.R) {
					pointList.add(rootPoint.get(index - 1).point);
					pointList.add(rootPoint.get(index).point);
				} else {
					pointList.add(rootPoint.get(index).point);
					pointList.add(rootPoint.get(index - 1).point);
				}
			}
			break;
		// B 값 기준 정렬
		case 3:
			int temp3 = max(rootPoint.get(index - 1).point.B, rootPoint.get(index).point.B, rootPoint.get(index + 1).point.B);
			pointList.add(rootPoint.get(index - 2 + temp3).point);
			if (temp3 == 1) {
				if (rootPoint.get(index).point.R > rootPoint.get(index + 1).point.R) {
					pointList.add(rootPoint.get(index).point);
					pointList.add(rootPoint.get(index + 1).point);
				} else {
					pointList.add(rootPoint.get(index + 1).point);
					pointList.add(rootPoint.get(index).point);
				}
			}
			if (temp3 == 2) {
				if (rootPoint.get(index - 1).point.R > rootPoint.get(index + 1).point.R) {
					pointList.add(rootPoint.get(index - 1).point);
					pointList.add(rootPoint.get(index + 1).point);
				} else {
					pointList.add(rootPoint.get(index + 1).point);
					pointList.add(rootPoint.get(index - 1).point);
				}
			}
			if (temp3 == 3) {
				if (rootPoint.get(index - 1).point.R > rootPoint.get(index).point.R) {
					pointList.add(rootPoint.get(index - 1).point);
					pointList.add(rootPoint.get(index).point);
				} else {
					pointList.add(rootPoint.get(index).point);
					pointList.add(rootPoint.get(index - 1).point);
				}
			}
			break;
		default:
			break;
		}
		if( ListTools.isEmpty( pointList ) ){
			if( removedCount_white > removedCount_black ){
				pointList.add( new Point(220,220,220) );
			}else{
				pointList.add( new Point(65,66,67) );
			}
		}
		return pointList;
	}

	/**
	 * 이미지로 배색 구성 계산
	 * 
	 * @param image
	 *            bufferImage
	 * @param rootPointNum
	 *            시드 포인트 개수
	 * @param colorNum
	 *            얻은 색조 개수
	 * @return
	 */
	public List<String> getColorSolution( BufferedImage image, int rootPointNum, int colorNum) {
		this.image = image;
		int width = image.getWidth();
		int height = image.getHeight();
		// 최대 반복 횟수 정의
		int maxIterater = 5000000;
		int offsetWidth = (int) ( width / 32 );
		int offsetHeight = (int) ( height / 32 );

		// 32*32개 점을 무작위로 얻어 그중 rootPointNum개 점의 RGB 값을 시드 포인트로 사용
		List<PointGroup> rootPoint = new ArrayList<PointGroup>();		
		for( int i = 0; i < width; i += offsetWidth ){
			for ( int j = 0; j < height; j += offsetHeight ) {
				PointGroup pg = new PointGroup();
				pg.point = getRGB( i, j );
				rootPoint.add( pg );
			}
		}			
		// 시드 포인트 클러스터 초기 임계값 설정
		int threshold = 6;
		// 현재 시드 포인트 수가 사용자 설정 상한보다 크면 임계값 상향
		while ( rootPoint.size() > rootPointNum ) {
			for (int i = 0; i < rootPoint.size(); i++){
				for (int j = i + 1; j < rootPoint.size(); j++) {
					if ( rootPoint.get(i).point.colorDistance(rootPoint.get(j).point) < threshold ) {
						rootPoint.remove(j);
					}
				}
			}
			threshold += 1;
		}
		int count = 0;
		do {
			// 이미지의 모든 픽셀을 순회하며 각 점을 시드 포인트 하나에 추가
			for (int i = 0; i < width; i++)
				for (int j = 0; j < height; j++) {
					Point point = getRGB(i, j);
					int index = 0;
					int dis = 10000;
					for (int m = 0; m < rootPoint.size(); m++) {
						int curDis = point.colorDistance(rootPoint.get(m).point);
						if (curDis < dis) {
							dis = curDis;
							index = m;
						}
					}
					rootPoint.get(index).addPoint(point);
					count++;
				}
			// 새 시드 포인트 평균 RGB가 기존 시드 포인트와 같은지 확인, 같으면 수렴 완료
			if (isEnd(rootPoint)) {
				break;
			}
		} while (count < maxIterater);
		
		List<Point> pointList = getThreeFamilly(rootPoint);
		List<String> list = new ArrayList<>();
		for (int i = 0; i < pointList.size() && i<colorNum; i++) {
			list.add( pointList.get(i).toString() );
		}
		return list;
	}

	public static void main(String[] args){
//		File file = new File("E:/icon_okr72x.png");
//		BufferedImage bi = null;
//		try {
//			bi = ImageIO.read(file);
//			ImageMainColorUtil ImageUtil = new ImageMainColorUtil();
//			List<String> list = ImageUtil.getColorSolution( bi, 30, 1);
//			if( list != null && !list.isEmpty() ){
//				list.forEach( s -> System.out.println(s));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
	}
	
}