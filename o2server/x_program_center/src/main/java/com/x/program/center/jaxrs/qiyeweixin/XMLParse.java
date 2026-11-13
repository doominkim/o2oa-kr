/**
 * 기업 위챗이 기업 백엔드로 보내는 메시지 암복호화 예제 코드.
 * 
 * @copyright Copyright (c) 1998-2014 Tencent Inc.
 */

// ------------------------------------------------------------------------

package com.x.program.center.jaxrs.qiyeweixin;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * XMLParse class
 *
 * 메시지 형식에서 암호문 추출 및 회신 메시지 형식 생성 인터페이스 제공.
 */
class XMLParse {

	/**
	 * xml 데이터 패킷에서 암호화 메시지 추출
	 * @param xmltext 추출할 xml 문자열
	 * @return 추출한 암호화 메시지 문자열
	 * @throws AesException 
	 */
	public static Object[] extract(String xmltext) throws AesException     {
		Object[] result = new Object[3];
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setExpandEntityReferences(false);
			dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl",true);
			dbf.setFeature("http://xml.org/sax/features/external-general-entities",false);
			dbf.setFeature("http://xml.org/sax/features/external-parameter-entities",false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader(xmltext);
			InputSource is = new InputSource(sr);
			Document document = db.parse(is);
			Element root = document.getDocumentElement();
			NodeList nodelist1 = root.getElementsByTagName("Encrypt");
			NodeList nodelist2 = root.getElementsByTagName("ToUserName");
			result[0] = 0;
			result[1] = nodelist1.item(0).getTextContent();
			result[2] = nodelist2.item(0).getTextContent();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AesException(AesException.ParseXmlError);
		}
	}

	/**
	 * xml 메시지 생성
	 * @param encrypt 암호화된 메시지 암호문
	 * @param signature 보안 서명
	 * @param timestamp 타임스탬프
	 * @param nonce 랜덤 문자열
	 * @return 생성된 xml 문자열
	 */
	public static String generate(String encrypt, String signature, String timestamp, String nonce) {

		String format = "<xml>\n" + "<Encrypt><![CDATA[%1$s]]></Encrypt>\n"
				+ "<MsgSignature><![CDATA[%2$s]]></MsgSignature>\n"
				+ "<TimeStamp>%3$s</TimeStamp>\n" + "<Nonce><![CDATA[%4$s]]></Nonce>\n" + "</xml>";
		return String.format(format, encrypt, signature, timestamp, nonce);

	}
}
