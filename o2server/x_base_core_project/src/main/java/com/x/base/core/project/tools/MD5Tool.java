package com.x.base.core.project.tools;

import org.apache.commons.lang3.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * @author louguodong
 *
 */
public class MD5Tool {

    public static String getMD5(byte[] source) {
        String s = null;
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };// 바이트를 16진수 문자로 변환하는 데 사용
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            md.update(source);
            byte tmp[] = md.digest();// MD5 계산 결과는 128비트 정수이며,
            // 바이트로 표현하면 16바이트
            char str[] = new char[16 * 2];// 각 바이트를 16진수로 표현하면 두 문자를 사용하므로 16
            // 진수로 표현하려면 32개 문자가 필요
            int k = 0;// 변환 결과에서 해당 문자 위치
            for (int i = 0; i < 16; i++) {// 첫 번째 바이트부터 MD5의 각 바이트를// 16
                // 진수 문자로 변환
                byte byte0 = tmp[i];// i번째 바이트 가져오기
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];// 바이트 상위 4비트 숫자 변환,// >>>
                // 논리 오른쪽 시프트, 부호 비트도 함께 시프트
                str[k++] = hexDigits[byte0 & 0xf];// 바이트 하위 4비트 숫자 변환

            }
            s = new String(str);// 변환된 결과를 문자열로 변환

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return s;
    }

    public static String getMD5Str(String source) {
        if (StringUtils.isNotEmpty(source)) {
            return getMD5(source.getBytes());
        } else {
            return "";
        }
    }

    public static String md5(String text) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(text.getBytes("UTF-8"));
            byte[] b = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception{
        // TODO Auto-generated method stub
        String test = MD5Tool.getMD5("30013110wHKFp0zRCXdkSXxl54fcee1a-037d-421b-b5cf-cf0f815e960920230323094937742STuid0000011679535045335UnSudxCsM1gpTB6pha1m8dCW1ufHQc2G1.0".getBytes());
        System.out.println(test);
        test = MD5Tool.getMD5("30013110wHKFp0zRCXdkSXxl54fcee1a-037d-421b-b5cf-cf0f815e960920230323094937742STuid0000011679535045335UnSudxCsM1gpTB6pha1m8dCW1ufHQc2G1.0".getBytes());
        System.out.println(test);

    }

}
