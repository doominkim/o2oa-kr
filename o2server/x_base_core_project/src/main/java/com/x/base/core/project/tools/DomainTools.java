package com.x.base.core.project.tools;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class DomainTools {

	public static final String HTTP_PROTOCOL_HEAD = "http://";
	public static final int HTTP_PROTOCOL_HEAD_LENGTH = HTTP_PROTOCOL_HEAD.length();
	public static final String HTTPS_PROTOCOL_HEAD = "https://";
	public static final int HTTPS_PROTOCOL_HEAD_LENGTH = HTTPS_PROTOCOL_HEAD.length();

	public static final boolean isLetterOrDigit(char c) {
		return ('0' <= c && c <= '9') || ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');
	}

	/**
	 * url의 도메인 부분 조회, 주의: 도메인 부분에 포트 번호 포함.
	 * 
	 * @param url
	 * @return
	 */
	public static final String getDomain(String url) {
		// 0. 파라미터 검사
		if (url == null)
			return null;

		// 1. 도메인 추출
		String domain;

		int l = 0;
		if (url.startsWith(HTTP_PROTOCOL_HEAD)) {
			l = HTTP_PROTOCOL_HEAD_LENGTH;
		} else if (url.startsWith(HTTPS_PROTOCOL_HEAD)) {
			l = HTTPS_PROTOCOL_HEAD_LENGTH;
		}
		int slash = url.indexOf('/', l);
		if (slash > 0) {
			domain = url.substring(l, slash);
		} else {
			if (l > 0) {
				domain = url.substring(l);
			} else {
				domain = url;
			}
		}
		return domain;
	}

	public static final String getDomainWithoutPort(String url) {
		String domain = getDomain(url);
		if (domain == null)
			return null;
		int idx = domain.indexOf(':');
		if (idx > 0) {
			return domain.substring(0, idx);
		} else {
			return domain;
		}
	}

	public static final String getDomainWithProtocal(String url) {
		// 0. 파라미터 검사
		if (url == null)
			return null;

		// 1. 도메인 추출
		String domain;

		int l = 0;
		if (url.startsWith(HTTP_PROTOCOL_HEAD)) {
			l = HTTP_PROTOCOL_HEAD_LENGTH;
		} else if (url.startsWith(HTTPS_PROTOCOL_HEAD)) {
			l = HTTPS_PROTOCOL_HEAD_LENGTH;
		}

		int slash = url.indexOf('/', l);
		if (slash > 0) {
			domain = url.substring(0, slash);
		} else {
			domain = url;
		}
		return domain;
	}

	public static final String regulateUrl(String url) {
		if (url == null)
			return null;

		boolean needProt = false;
		boolean needTail = false;
		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			needProt = true;
		}
		if (isDomain(url) && !url.endsWith("/")) {
			needTail = true;
		}
		if (needProt) {
			url = "http://" + url;
		}
		if (needTail) {
			url += '/';
		}

		return url;
	}

	/**
	 * 문자열이 URL인지 판정하고 정규화된 URL 문자열 반환. 정규화 규칙: 1.http://로 시작; 2.포트가 80이면 생략; 3.
	 * 순수 도메인이면 "/"로 끝맺음
	 * 
	 * @param query 검사 문자열
	 * @return 결과가 URL이면 정규화 결과, 아니면 null 반환.
	 */
	private static final String[] traditionalUrlPostfix = { ".com", ".biz", ".pro", ".aero", ".coop", ".museum",
			".mobi", ".edu", ".gov", ".info", ".mil", ".name", ".net", ".org", ".jobs", ".travel", ".mil", ".arpa",
			".int", ".cat", ".asia", ".tel" };
	private static final String[] internationalTraditionalUrlPostfix = { ".com", ".net", ".edu", };
	private static final String[] regionalUrlPostfix = {

			".ac", ".ad", ".ae", ".af", ".ag", ".ai", ".al", ".am", ".an", ".ao", ".aq", ".ar", ".as", ".at", ".au",
			".aw", ".az", ".ba", ".bb", ".bd", ".be", ".bf", ".bg", ".bh", ".bi", ".bj", ".bm", ".bn", ".bo", ".br",
			".bs", ".bt", ".bv", ".bw", ".by", ".bz", ".ca", ".cc", ".cd", ".cf", ".cg", ".ch", ".ci", ".ck", ".cl",
			".cm", ".cn", ".co", ".cr", ".cs", ".cu", ".cv", ".cx", ".cy", ".cz", ".de", ".dj", ".dk", ".dm", ".do",
			".dz", ".ec", ".eu", ".fi", ".fj", ".fk", ".fm", ".fo", ".fr", ".fx", ".ga", ".gb", ".gd", ".ge", ".gf",
			".gh", ".gi", ".gl", ".gp", ".gq", ".gf", ".gm", ".gn", ".gr", ".gs", ".gt", ".gu", ".gw", ".gy", ".hk",
			".hm", ".hn", ".hr", ".ht", ".hu", ".id", ".ie", ".il", ".in", ".io", ".iq", ".ir", ".is", ".it", ".jm",
			".jo", ".jp", ".ke", ".kg", ".kh", ".ki", ".km", ".kn", ".kp", ".kr", ".kw", ".ky", ".kz", ".la", ".lb",
			".lc", ".li", ".lk", ".lr", ".ls", ".lt", ".lu", ".lv", ".ly", ".ma", ".mc", ".md", ".mg", ".mh", ".mk",
			".ml", ".mm", ".mn", ".mo", ".mp", ".mq", ".mr", ".ms", ".mt", ".mu", ".mv", ".mw", ".mx", ".my", ".mz",
			".na", ".nc", ".ne", ".nf", ".ng", ".ni", ".nl", ".no", ".np", ".nr", ".nt", ".nu", ".nz", ".om", ".pa",
			".pe", ".pf", ".pg", ".ph", ".pk", ".pl", ".pm", ".pn", ".pt", ".pr", ".pw", ".py", ".qa", ".re", ".ro",
			".ru", ".rw", ".sa", ".sb", ".sc", ".sd", ".se", ".sg", ".sh", ".si", ".sj", ".sk", ".sl", ".sm", ".sn",
			".so", ".sr", ".st", ".su", ".sv", ".sy", ".sz", ".tc", ".td", ".tf", ".tg", ".th", ".tj", ".tk", ".tm",
			".tn", ".to", ".tp", ".tr", ".tt", ".tv", ".tw", ".tz", ".ua", ".ug", ".uk", ".um", ".us", ".uy", ".uz",
			".va", ".vc", ".ve", ".vg", ".vi", ".vn", ".vu", ".wf", ".ws", ".ye", ".yt", ".yu", ".za", ".zm", ".zr",
			".zw", ".ad", ".ae", ".af", ".ag", ".ai", ".al", ".am", ".an", ".ao", ".aq", ".ar", ".as", ".at", ".au",
			".aw", ".az", ".ba", ".bb", ".bd", ".be", ".bf", ".bg", ".bh", ".bi", ".bj", ".bm", ".bn", ".bo", ".br",
			".bs", ".bt", ".bv", ".bw", ".by", ".bz", ".ca", ".cc", ".cf", ".cg", ".ch", ".ci", ".ck", ".cl", ".cm",
			".cn", ".co", ".cq", ".cr", ".cu", ".cv", ".cx", ".cy", ".cz", ".de", ".dj", ".dk", ".dm", ".do", ".dz",
			".ec", ".ee", ".eg", ".eh", ".es", ".et", ".ev", ".fi", ".fj", ".fk", ".fm", ".fo", ".fr", ".ga", ".gb",
			".gd", ".ge", ".gf", ".gh", ".gi", ".gl", ".gm", ".gn", ".gp", ".gr", ".gt", ".gu", ".gw", ".gy", ".hk",
			".hm", ".hn", ".hr", ".ht", ".hu", ".id", ".ie", ".il", ".in", ".io", ".iq", ".ir", ".is", ".it", ".jm",
			".jo", ".jp", ".ke", ".kg", ".kh", ".ki", ".km", ".kn", ".kp", ".kr", ".kw", ".ky", ".kz", ".la", ".lb",
			".lc", ".li", ".lk", ".lr", ".ls", ".lt", ".lu", ".lv", ".ly", ".ma", ".mc", ".md", ".me", ".mg", ".mh",
			".ml", ".mm", ".mn", ".mo", ".mp", ".mq", ".mr", ".ms", ".mt", ".mv", ".mw", ".mx", ".my", ".mz", ".na",
			".nc", ".ne", ".nf", ".ng", ".ni", ".nl", ".no", ".np", ".nr", ".nt", ".nu", ".nz", ".om", ".pa", ".pe",
			".pf", ".pg", ".ph", ".pk", ".pl", ".pm", ".pn", ".pr", ".pt", ".pw", ".py", ".qa", ".re", ".ro", ".rs",
			".ru", ".rw", ".sa", ".sb", ".sc", ".sd", ".se", ".sg", ".sh", ".si", ".sj", ".sk", ".sl", ".sm", ".sn",
			".so", ".sr", ".st", ".su", ".sy", ".sz", ".tc", ".td", ".tf", ".tg", ".th", ".tj", ".tk", ".tl", ".tm",
			".tn", ".to", ".tp", ".tr", ".tt", ".tv", ".tw", ".tz", ".ua", ".ug", ".uk", ".us", ".uy", ".va", ".vc",
			".ve", ".vg", ".vn", ".vu", ".wf", ".ws", ".ye", ".yu", ".za", ".zm", ".zr", ".zw"

	};
	// A C-연구 기관; COM-공업, 상업, 금융 등 전문 분야; EDU-교육 기관; GOV-정부 부처; NET-인
	// 터넷, 접속망의 정보 센터와 운영 센터; ORG
	private static final String[] fixupPostfix = new String[] { ".cn", ".bj", ".id", ".co", ".il", ".co", ".jp", ".co",
			".kr", ".co", ".nr", ".co", ".uk", ".co", ".uz", ".co", ".cn", ".ac", ".cn", ".com", ".cn", ".edu", ".cn",
			".gov", ".cn", ".net", ".cn", ".org", ".cn", ".sh", ".cn", ".tj", ".cn", ".cq", ".cn", ".he", ".cn", ".sx",
			".cn", ".nm", ".cn", ".ln", ".cn", ".jl", ".cn", ".hl", ".cn", ".js", ".cn", ".zj", ".cn", ".ah", ".cn",
			".fj", ".cn", ".jx", ".cn", ".sd", ".cn", ".ha", ".cn", ".hb", ".cn", ".hn", ".cn", ".gd", ".cn", ".gx",
			".cn", ".hi", ".cn", ".sc", ".cn", ".gz", ".cn", ".yn", ".cn", ".xz", ".cn", ".sn", ".cn", ".gs", ".cn",
			".qh", ".cn", ".nx", ".cn", ".xj", ".cn", ".tw", ".cn", ".hk", ".cn", ".mo", ".ru", ".net", };
	public static HashMap<String, HashMap<String, Object>> urlPostfixMap = new HashMap<String, HashMap<String, Object>>();
	public static HashMap<String, String> regionalUrlPostfixMap = new HashMap<String, String>();
	public static HashMap<String, String> traditionalUrlPostfixMap = new HashMap<String, String>();
	// 전체 최상위 도메인 목록(앞의 . 제외)
	public static HashMap<String, HashMap<String, Object>> urlPostfixMap_noDot = new HashMap<String, HashMap<String, Object>>();
	// 처음 정의된 업무 관련 최상위 도메인 목록(앞의 . 제외)
	public static HashMap<String, String> regionalUrlPostfixMap_noDot = new HashMap<String, String>();
	// 나중에 추가된 국가 최상위 도메인 목록(앞의 . 제외)
	public static HashMap<String, String> traditionalUrlPostfixMap_noDot = new HashMap<String, String>();

	static {
		for (int i = 0; i < traditionalUrlPostfix.length; i++) {
			if (traditionalUrlPostfix[i] != null) {
				String temp = traditionalUrlPostfix[i].trim();
				traditionalUrlPostfixMap.put(temp, null);
				urlPostfixMap.put(temp, null);
				if (temp.length() > 0) {
					temp = temp.substring(1);
					traditionalUrlPostfixMap_noDot.put(temp, null);
					urlPostfixMap_noDot.put(temp, null);
				}
			}
		}
		for (int i = 0; i < regionalUrlPostfix.length; i++) {
			if (regionalUrlPostfix[i] != null) {
				String temp = regionalUrlPostfix[i].trim();
				regionalUrlPostfixMap.put(temp, null);
				urlPostfixMap.put(temp, null);
				HashMap<String, Object> obj = (HashMap<String, Object>) urlPostfixMap.get(temp);
				for (String international : internationalTraditionalUrlPostfix) {
					if (obj == null) {
						obj = new HashMap<String, Object>();
						urlPostfixMap.put(temp, obj);
					}
					obj.put(international, null);
				}
				if (temp.length() > 0) {
					temp = temp.substring(1);
					regionalUrlPostfixMap_noDot.put(temp.substring(1), null);
					urlPostfixMap_noDot.put(temp, null);
				}
			}
		}
		for (int i = 0; i < fixupPostfix.length && i + 1 < fixupPostfix.length; i += 2) {
			String key = fixupPostfix[i];
			String val = fixupPostfix[i + 1];
			{
				HashMap<String, Object> obj = (HashMap<String, Object>) urlPostfixMap.get(key);
				if (obj == null) {
					obj = new HashMap<String, Object>();
					urlPostfixMap.put(key, obj);
				}
				obj.put(val, null);
			}
			if (key.length() > 0 && val.length() > 0) {
				key = key.substring(1);
				val = val.substring(1);
				HashMap<String, Object> obj = (HashMap<String, Object>) urlPostfixMap_noDot.get(key);
				if (obj == null) {
					obj = new HashMap<String, Object>();
					urlPostfixMap_noDot.put(key, obj);
				}
				obj.put(val, null);
			}
		}
	}

	public static final String URL_PATH_SEPERATOR = "/";
	public static final String URL_HTTP_HEAD = "http://";
	public static final String URL_DOMAIN_SEPERATOR = ".";

	/**
	 * 문자열이 URL인지 판정하고 정규화된 URL 문자열 반환. 정규화 규칙: 1.http://로 시작; 2.포트가 80이면 생략; 3.
	 * 순수 도메인이면 "/"로 끝맺음
	 * 
	 * @param query 검사 문자열
	 * @return 결과가 URL이면 정규화 결과, 아니면 null 반환.
	 */
	public static final String getLookupUrl(String query) {
		String temp = query.trim();
		String domain;
		String filePath = URL_PATH_SEPERATOR;

		String protocalHead = HTTP_PROTOCOL_HEAD;

		String tempLower = temp.toLowerCase();
		if (tempLower.startsWith(HTTP_PROTOCOL_HEAD)) {
			protocalHead = HTTP_PROTOCOL_HEAD;
			temp = temp.substring(HTTP_PROTOCOL_HEAD_LENGTH);
		} else if (tempLower.startsWith(HTTPS_PROTOCOL_HEAD)) {
			protocalHead = HTTPS_PROTOCOL_HEAD;
			temp = temp.substring(HTTPS_PROTOCOL_HEAD_LENGTH);
		}
		int idxSlash = temp.indexOf('/');
		int idxColon = temp.indexOf(':');

		int port = 80;
		if (idxSlash < 0) { // 순수 도메인
			if (idxColon > 0) {
				try {
					port = Integer.parseInt(temp.substring(idxColon + 1));
				} catch (NumberFormatException e) {
					return null;
				}
				domain = temp.substring(0, idxColon);
			} else
				domain = temp;
			filePath = URL_PATH_SEPERATOR;
		} else { // 도메인+디렉터리
			if (idxColon > 0 && idxColon < idxSlash) {
				try {
					port = Integer.parseInt(temp.substring(idxColon + 1, idxSlash));
				} catch (NumberFormatException e) {
					return null;
				}
				domain = temp.substring(0, idxColon);
			} else {
				domain = temp.substring(0, idxSlash);
			}
			filePath = temp.substring(idxSlash);
		}
		// port가 유효 범위인지 판단
		if (port <= 0 || port > 65535) {
			return null;
		}
		// 도메인 부분 유효성 판단

		domain = validateDomain(domain);
		// URL로 확정
		if (domain != null) {

			String result;
			if (port == 80) {
				result = protocalHead + domain + filePath;
			} else {
				result = protocalHead + domain + ':' + port + filePath;
			}
			return result;
		}
		return null;
	}

	public static final boolean isIP(String domain) {
		if (domain == null)
			return false;

		boolean isValid = false;
		// 판단 1: xxx.xxx.xxx.xxx 형식 IP 주소
		try {
			StringTokenizer token = new StringTokenizer(domain, URL_DOMAIN_SEPERATOR);
			int i;
			for (i = 0; i < 4; i++) {
				int tempInt = Integer.parseInt(token.nextToken());
				if (tempInt < 0 || tempInt > 255)
					break;
			}
			if (i == 4) {
				if (!token.hasMoreTokens()) {
					// 검증 성공
					isValid = true;
				}
			}
		} catch (NoSuchElementException e) {
		} catch (NumberFormatException e) {
		}
		return isValid;

	}

	/**
	 * 도메인 부분이 RFC 규격에 맞는지 검사
	 * 
	 * @param domain
	 * @return null이면 파라미터가 도메인이 아님, 아니면 domain 자체 반환
	 * 
	 */
	public static final String validateDomain(String domain) {
		if (domain == null)
			return null;

		// 판단 0: 유효하지 않은 문자 없음
		for (int i = 0; i < domain.length(); i++) {

			char c = domain.charAt(i);

			if (c > 0x7f) {
				return null;
			} else if (!isLetterOrDigit(c)) {
				// 도메인은 기호를 포함할 수 없으나 '.' 또는 '-' 또는 '_'는 포함 가능하며, 이 세 기호로 시작하거나 끝날 수 없음
				if ((c == '.' && i != 0 && i != domain.length() - 1)
						|| ((c == '-' || c == '_') && i != 0 && i != domain.length() - 1)) {
					continue;
				} else {
					return null;
				}
			}
		}

		boolean isValid = false;
		do {
			if (isIP(domain)) {
				isValid = true;
				break;
			}
			// 아니면 다른 형식 충족 여부 판단
			{
				isValid = true;
				// 판단 2.1: xx.xxxx.com 형식 도메인(문자 구성 유효성 판단)
				StringTokenizer token = new StringTokenizer(domain, URL_DOMAIN_SEPERATOR);
				while (token.hasMoreTokens()) {
					String tok = token.nextToken();
					if (tok.length() == 0 || tok.startsWith(".") || tok.endsWith(".") || tok.startsWith("-")
							|| tok.endsWith("-") || tok.startsWith("_") || tok.endsWith("_")) {
						isValid = false;
						break;
					}
				}
				if (isValid && domain.indexOf("..") >= 0)
					isValid = false;
				// 도메인 형식 불충족, 탈출
				if (!isValid)
					break; // do .. while(false);
			}
			// 판단 2: xx.xxxx.com 형식 도메인(접미사로 판단)
			{
				isValid = false;
				domain = domain.toLowerCase();
				int p = domain.lastIndexOf('.');
				try {
					String postfix = domain.substring(p);
					if (urlPostfixMap.containsKey(postfix)) {
						isValid = true;
						// 검증 성공, 탈출하여 다른 템플릿 판단 미실행
						break; // do .. while(false);
					}
				} catch (IndexOutOfBoundsException e) {
				}
			}
		} while (false);

		// URL로 확정
		if (isValid) {
			return domain;
		} else {
			return null;
		}
	}

	/**
	 * URL이 도메인 형식 URL인지 판단
	 * 
	 * @param url url은 이 클래스의 URL 정규화 함수 규칙에 맞는 URL이어야 함
	 * @return
	 */
	public static final boolean isDomain(String url) {
		int t = 0;
		if (url.startsWith(HTTP_PROTOCOL_HEAD)) {
			t = HTTP_PROTOCOL_HEAD_LENGTH;
		} else if (url.startsWith(HTTPS_PROTOCOL_HEAD)) {
			t = HTTPS_PROTOCOL_HEAD_LENGTH;
		}
		t = url.indexOf('/', t);
		if (t < 0 || t == url.length() - 1)
			return true;
		return false;
	}

	/**
	 * url의 1차 도메인 찾기, 1차 도메인 형식: [a-z0-9]([a-z0-9\-]*[a-z0-9])?\.{최상위 도메인} 또는
	 * [a-z0-9]([a-z0-9\-]*[a-z0-9])?\.{도메인 업체 제공 도메인}.{최상위 도메인}
	 * 
	 * @param url url은 이 클래스의 URL 정규화 함수 규칙에 맞는 URL이어야 함
	 * @return 파라미터가 url이 아니면 null, 아니면 해당 최상위 도메인 문자열 반환, 예:
	 *         "http://www.sogou.com.cn/"의 반환값은 "sogou.com.cn"
	 */
	@SuppressWarnings("rawtypes")
	public static final String getMainDomain(String url) {

		String domain = getDomainWithoutPort(url);

		if (domain == null)
			return null;

		HashMap map = urlPostfixMap;
		int lastDot = domain.length();
		int last = lastDot;
		do {
			last = domain.lastIndexOf('.', lastDot - 1);

			// 앞에 더 이상 '.'가 없음
			if (last < 0)
				break;
			// n+1차 도메인이 더 이상 없음
			if (map == null)
				break;

			String topDomain = domain.substring(last, lastDot);

			if (!map.containsKey(topDomain))
				break;
			else
				map = (HashMap) map.get(topDomain);
			lastDot = last;
		} while (true);
		if (lastDot == domain.length()) {
			return null; // 최상위 도메인 없음
		} else {
			if (last < 0) { // xxx.com.cn
				return domain;
			} else { // xxx.domain.com.cn
				return domain.substring(last + 1);
			}
		}
	}

	/**
	 * url이 www로 시작하지 않는 1차 도메인인지 검사, 1차 도메인 형식: [a-z0-9]([a-z0-9\-]*[a-z0-9])?\.{임의 최상위 도메인} 또는
	 * [a-z0-9]([a-z0-9\-]*[a-z0-9])?\.{전통 최상위 도메인}.{지역 최상위 도메인}
	 * 
	 * @param url url은 이 클래스의 URL 정규화 함수 규칙에 맞는 URL이어야 함
	 * @return
	 */
	public static final boolean isNonWWW(String url) {

		String domain = getDomainWithoutPort(url);

		if (domain == null)
			return false;

		String mainDomain = getMainDomain(domain);
		return (mainDomain != null && mainDomain.equals(domain));

	}

	/**
	 * URL 문자열에서 QueryString 문자열 조회
	 * 
	 * @param url 프로토콜 헤더, 도메인 부분 등을 포함한 전체 URL 문자열
	 * @return url 파라미터가 null이거나 url에 '?' 문자가 없으면 null
	 *         아니면 RFC 표준에 따라 첫 '?' 이후 첫 '#' 사이 부분을 QueryString 문자열로 반환
	 */
	public static final String getQueryString(String url) {
		if (url == null)
			return null;
		int index = url.indexOf('?');
		if (index < 0) {
			return null;
		}
		index++;
		int hash = url.indexOf('#', index);
		if (hash < 0) {
			return url.substring(index);
		} else {
			return url.substring(index, hash);
		}
	}

	private static final boolean checkHexChar(byte[] str, int i) {
		if (str == null || i >= str.length)
			return false;
		byte ch1 = str[i];
		return (ch1 >= '0' && ch1 <= '9') || (ch1 >= 'a' && ch1 <= 'f') || (ch1 >= 'A' && ch1 <= 'F');
	}

	private static final boolean checkMultiHexChar(byte[] str, int idx, int n) {
		for (int i = 0; i < n; i++) {
			if (!checkHexChar(str, idx + i))
				return false;
		}
		return true;
	}

	private static final boolean tryPut(byte[] buff, int idx, byte b) {
		if (buff == null || idx < 0 || idx >= buff.length)
			return false;
		buff[idx] = b;
		return true;
	}
//	private static final boolean tryMultiPut(byte[]buff, int idx, byte[] b){
//		if( buff == null || b == null || idx < 0 || idx + b.length > buff.length ) return false;
//		System.arraycopy(b, 0, buff, idx, b.length);
//		return true;
//	}

	/**
	 * 범용 url 디코딩, %xx 등으로 인코딩된 문자열을 해당 byte로 바로 변환, 구체 인코딩 미고려
	 * 
	 * @see genericUrlDecode(String url, byte[]buff, int flag)
	 * @param url  디코딩할 url.
	 * @param buff 디코딩된 데이터를 저장할 버퍼
	 * @return >=0 파싱 성공, buff 내 데이터 길이 -1 파라미터 문제, url이 비어 있음 -2 파라미터 문제, buff 공간 부족 -3 알 수 없는 문제 -4
	 *         GBK 인코딩으로 확인
	 */
	public static final int genericUrlDecode(String url, byte[] buff) {
		return genericUrlDecode(url, buff, 0);
	}

	/**
	 * 범용 url 디코딩, %xx를 해당 byte로 바로 변환, 구체 인코딩 미고려, %FF,%uFFFF 네 가지 인코딩 방식 지원. 주의: 여기서 말하는 인코딩은 "GBK"와 달라 혼동하지 말 것
	 * 
	 * @param url  디코딩할 url.
	 * @param buff 디코딩된 데이터를 저장할 버퍼
	 * @param flag 형식 확장 파라미터, 0은 url 인코딩만 처리, 첫 비트가 1이면 apache 형식 인코딩 처리 필요
	 * @return >=0 파싱 성공, buff 내 데이터 길이 -1 파라미터 문제, url이 비어 있음 -2 파라미터 문제, buff 공간 부족 -3 알 수 없는 문제 -4
	 *         GBK 인코딩으로 확인
	 */
	public static final int genericUrlDecode(String url1, byte[] buff, int flag) {
		if (url1 == null || buff == null) {
			return -1;
		}
		byte[] data = url1.getBytes();
		byte[] bb = buff;
		int idx = 0;
		for (int i = 0; i < data.length; i++) { // 인코딩된 것으로 알려진 문자열은 반드시 3자를 초과
			if (data[i] == '%') { // %FF와 %uFFFF의 경우 처리
				if (checkMultiHexChar(data, i + 1, 2)) { // %ff 형식 처리
					try {
						int a = Integer.parseInt(new String(data, i + 1, 2), 16);
						if (!tryPut(bb, idx, (byte) a))
							return -2;
						idx++;
						i += 2;
					} catch (Exception e) {
						return -3;
					}
				} else if (i + 1 < data.length && data[i + 1] == 'u') { // %uFFFF 형식 처리
					if (checkMultiHexChar(data, i + 2, 4)) {
						// gbk 인코딩으로 확인 가능
						return -4;
					}
				}
			} else if ((flag & 1) == 1) {
				// apache 형식 파싱을 위한 확장 예약
				if (data[i] == '\\' && i + 1 < data.length && data[i + 1] == 'x') { // \xFF 형식 처리
					if (checkMultiHexChar(data, i + 2, 2)) {
						try {
							int a = Integer.parseInt(new String(data, i + 2, 2), 16);
							if (!tryPut(bb, idx, (byte) a))
								return -2;
							idx++;
							i += 3;
						} catch (Exception e) {
							return -3;
						}
					}
				}
			} else {
				// 기타 문자 넣기
				if (!tryPut(bb, idx, (byte) data[i]))
					return -2;
				idx++;
			}
		}

		return idx;
	}

	/**
	 * byte 스트림으로 가능한 문자 인코딩 판단, jchardet 패키지의 probe 메커니즘으로 인코딩 유형을 판단하며 일정한 오판율 있음
	 * 스트림은 하나의 통일된 규칙으로 인코딩되어야 하며, 데이터가 많을수록 판정 정확도가 높음
	 * 
	 * @param s 판정할 스트림
	 * @return 가능한 인코딩 유형 배열, "UTF-8", "GBK", "UTF-16" 등. 파라미터에 문제가 있으면 null 반환
	 */
//	public static final String[] probeAllCharsets(byte[] s) {
//		if( s == null || s.length == 0 ) return null;
//		return probeAllCharsets(s,s.length);
//	}
//	public static final String[] probeAllCharsets(byte[] s, int limit) {
//		if( s == null || limit <=0 || limit > s.length ) return null;
//		nsDetector det = new nsDetector(nsDetector.SIMPLIFIED_CHINESE);
//		nsICharsetDetectionObserver c = null;
//		det.Init(c);
//		int limitPerIteration = 1024;
//		if (limit <= limitPerIteration) {
//			det.DoIt(s, limit, false);
//			det.Done();
//
//			String prob[] = det.getProbableCharsets();
//			return prob;
//		}
//		byte[] bytes = new byte[limitPerIteration];
//		int index = 0;
//		while ((index + limitPerIteration) < limit) {
//			System.arraycopy(s, index, bytes, 0, limitPerIteration);
//			if (det.DoIt(bytes, bytes.length, false)) {
//				det.Done();
//				String prob[] = det.getProbableCharsets();
//				return prob;
//			}
//			index += limitPerIteration;
//		}
//		System.arraycopy(s, index, bytes, 0, limit - index);
//		det.DoIt(bytes, bytes.length, false);
//		det.Done();
//		String prob[] = det.getProbableCharsets();
//
//		return prob;
//	}

	/**
	 * 문자열의 가능한 인코딩 유형 판단
	 * 
	 * @param line 인코딩된 문자열, 보통 url 파라미터
	 * @return 가능한 인코딩 유형 "GBK", "UTF-8" 등, 오류 시 null 반환
	 */
//	public static final String probeCharset(String line){
//		String dft = null;
//		String ret = null;
//		int limit = Integer.MAX_VALUE>>2;
//		if( line == null || line.length() == 0 || line.length() >= limit ) 
//			return dft;
//		byte[] buff = new byte[line.length()*2];
//		int n = genericUrlDecode(line, buff);
//		if( n >= 0 ){
//			ret = probeCharset(buff, 0, n);
//			try{
//				String oldret = probeCharset2(buff, 0, n);
//				if (!ret.equals(oldret)){
//					StringBuilder sb = new StringBuilder();
//					sb.append("charset diff:");
//					sb.append(ret);
//					sb.append(" ");
//					sb.append(oldret);
//					sb.append(" ");
//					sb.append(line);
//					System.out.println(sb.toString());
//				}
//			}catch(Exception e){
//				System.out.println("charset diff:exception "+line);
//			}
//		} else if( n == -1 ){
//			// 파라미터 문제
//			ret = dft;
//		} else if( n == -2 ){ // 공간 부족, 재시도
//			buff = new byte[line.length()*4];
//			n = genericUrlDecode(line, buff);
//			if( n < 0 ){
//				ret = dft;
//			} else {
//				ret = probeCharset(buff, 0, n);
//			}
//		} else if( n == -4 ){ // n == -4 인코딩 유형을 GBK로 확인
//			ret = "GBK";
//		} else {// n == -3 알 수 없는 오류
//			ret = dft;
//		}
//		return ret;
//		
//	}

//	private static final float count(String str){
//		if( str == null || str.length() == 0 ) return 1.0f;
//		int total = 0;
//		float sum = 0.0f;
//		for(int i=0;i<str.length(); i++){
//			char ch = str.charAt(i);
//			if( ch > 128 ){
//				total ++;
//				if( !GB2312Charset.has(ch) ){
//					if( ch >= '\ufff0' ){
//						sum += 2.0f;
//					} else {
//						sum += 1.0f;
//					}
//				} else {
//					if( ch < 39 * 8 * 64 ){
//						sum += 0.05;
//					} else if( ch < 58 * 8 * 64 ){
//						sum += 0.07;
//					} else if( ch > 80 * 8 * 64 ){
//						sum += 0.2;
//					}
//				}
//			}
//		}
//		return total == 0 ? 1 : (sum)/total;
//	}

//	private static final float count2(String str){
//		if( str == null || str.length() == 0 ) return 1.0f;
//		int total = 0;
//		float sum = 0.0f;
//		for(int i=0;i<str.length(); i++){
//			char ch = str.charAt(i);
//			if( ch > 128 ){
//				total ++;
//				if( !GB2312Charset.has(ch) ){
//					if( ch >= '\ufff0' ){
//						sum += 2.0f;
//					} else {
//						sum += 1.0f;
//					}
//				} else {
//					if( ch < 39 * 8 * 64 ){
//						sum += 0.05;
//					} else if( ch < 58 * 8 * 64 ){
//						sum += 0.07;
//					} else if( ch > 80 * 8 * 64 ){
//						sum += 0.2;
//					} else if( !GB2312Charset.has2312(ch) ){
//						sum += 0.15;
//					}
//				}
//			}
//		}
//		return total == 0 ? 1 : (sum)/total;
//	}
	/**
	 * 스트림 구간의 인코딩 유형 판정
	 * 
	 * @param buff  원본 스트림, 데이터가 많을수록 정확하지만 성능도 저하
	 * @param start 시작 위치
	 * @param limit 스트림 길이
	 * @return null 파라미터 오류 또는 판정 실패
	 */
//	public static final String probeCharset(byte[] bb, int start, int limit){
//
//		if (bb == null || start < 0 || limit <= 0 || limit + start > bb.length) {
//			return null;
//		}
//		byte[] buff = new byte[limit];
//		System.arraycopy(bb, start, buff, 0, limit);
//		String[] css = probeAllCharsets(buff);
//		for (String cs : css) {
//			if ( cs.equals("GBK") || cs.equals("GB2312")
//					|| cs.equals("GB18030"))
//				return "GBK";
//			else if( cs.equals("UTF-8") ){
//				// fixup
//				// chardet 패키지가 “일” 등 일부 글자 인식에 뚜렷한 문제가 있음
//				{
//					try{
//						String str = new String( bb, start, limit, cs);
//						if (str != null && str.contains("한국")){
//							return "UTF-8";
//						}
//						if (str != null && str.contains("돼지v")){
//							return "GBK";
//						}
//						if(count(str) > 0.5){
//							cs = "GBK";
//						}
//						if(count(str) == 0.5){
//							try{
//								str = new String( bb, start, limit, "GBK");
//								if (count(str) < 0.5){
//									return "GBK";
//								}
//							}catch(Exception e){
//								cs = "UTF-8";
//							}
//						}
//					}catch(Exception e){
//						cs = "GBK";
//					}
//				}
//				return cs;
//			}
//		}
//		return null;
//
//	}
//	
//	public static final String probeCharset2(byte[] bb, int start, int limit){
//
//		if (bb == null || start < 0 || limit <= 0 || limit + start > bb.length) {
//			return null;
//		}
//		byte[] buff = new byte[limit];
//		System.arraycopy(bb, start, buff, 0, limit);
//		String[] css = probeAllCharsets(buff);
//		String maybeSET = null;
//		for (String cs : css) {
//			if ( cs.equals("GBK") || cs.equals("GB2312")
//					|| cs.equals("GB18030"))
//				return "GBK";
//			else if( cs.equals("UTF-8") ){
//				// fixup
//				// chardet 패키지가 “일” 등 일부 글자 인식에 뚜렷한 문제가 있음
//				{
//					try{
//						String str = new String( bb, start, limit, cs);
//						float cnt = count2(CharsetConverter.getInstance().getGBKOfBig5(str));  
//						if (cnt > 0.5){
//							return "GBK";
//						}
//						
//						try{
//							str = new String( bb, start, limit, "GBK");
//							float cnt2 = count2(str);  
//							if (cnt2+0.1 < cnt){
//								//gbk 가능성이 utf8보다 높지만, chardet가 gbk로 판단하지 않으면 utf8 사용
//								maybeSET = "UTF-8";
//								continue;
//							}else if (cnt2 == 0.0 && cnt > 0.0){
//								maybeSET = "UTF-8";
//								continue;
//							}
//						}catch(Exception e){
//							cs = "UTF-8";
//						}
//						
//					}catch(Exception e){
//						cs = "GBK";
//					}
//				}
//				return cs;
//			}
//		}
//		return maybeSET;
//
//	}

	/**
	 * url 파라미터 또는 파라미터 포함 url에서 특정 파라미터 추출, "#" 앵커 경우 고려
	 * 
	 * @param url   원본 문자열
	 * @param param 찾을 파라미터
	 * @return 찾은 해당 파라미터, null은 파라미터가 없거나 유효하지 않음
	 */
	public static final String getParameter(String url, String param) {
		if (url == null || param == null)
			return null;
		String key = param + "=";
		int right = url.indexOf("/#");
		if (right < 0) {
			right = url.indexOf('#');
		} else {
			right = url.indexOf('#', right + 2);
		}
		if (right < 0)
			right = url.length();
		int left = -1;
		while (true) {
			int idx = url.indexOf(key, left + 1);
			if (idx < 0) {
				return null;
			} else if (idx == 0) {
				left = idx;
				break;
			} else if (url.charAt(idx - 1) == '?' || url.charAt(idx - 1) == '&' || url.charAt(idx - 1) == '#') {
				left = idx;
				break;
			} else {
				left = idx;
			}
		}
		// 찾지 못함
		if (left < 0)
			return null;
		left += key.length();
		// # 앵커 처리
		if (left >= right)
			return null;

		int end = url.indexOf('&', left + 1);
		if (end > 0 && end < right)
			right = end;
		return url.substring(left, right);
	}

}
