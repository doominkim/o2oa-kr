package com.x.base.core.project.tools;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author sword
 * @date 2023/03/29 16:22
 **/
public class SSLHttpClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(SSLHttpClientUtil.class);

    /** keystore 유형 JKS*/
    private static final String KEY_STORE_TYPE_JKS = "JKS";
    private CloseableHttpClient httpClient;
    /**
     * @throws Exception
     */
    public SSLHttpClientUtil(String password, String p12FilePath) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE_JKS);
        KeyStore trustKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        InputStream instream = new FileInputStream(p12FilePath);

        try {
            //키 저장소 비밀번호
            keyStore.load(instream, password.toCharArray());
        } catch (CertificateException e) {
            logger.warn("클라이언트 신뢰 인증서 로드 오류");
            logger.error(e);
        } finally {
            try {
                instream.close();
            } catch (Exception ignore) {
            }
        }
        SSLContext sslcontext = SSLContexts.custom()
                //서버 인증서 검증 무시
                .loadTrustMaterial(new TrustStrategy() {
                    @Override
                    public boolean isTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        return true;
                    }

                })
                .loadKeyMaterial(keyStore, password.toCharArray())
                .build();

        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                sslcontext,
                new String[]{"TLSv1.1"},
                null,
                SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        this.httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .build();
    }

    /**
     * post 요청 전송
     *
     * @param url
     * @param map
     * @throws Exception
     */
    public String post(String url, Map<String, Object> map) throws Exception {
        // POST 요청 선언
        HttpPost httpPost = new HttpPost(url);
        // map이 비어 있지 않은지 판단
        if (null != map) {
            // 파라미터를 담을 List 선언
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            // map을 순회하며 파라미터를 list에 설정
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }
            // form 양식 객체 생성
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, "utf-8");
            formEntity.setContentType("Content-Type:application/json");
            // 양식 객체를 httpPost에 설정
            httpPost.setEntity(formEntity);
        }
        // HttpClient로 요청을 보내고 response 반환
        CloseableHttpResponse response = this.httpClient.execute(httpPost);
        // 엔티티 조회
        HttpEntity entity = response.getEntity();
        // 엔티티를 문자열로 변환
        String res = EntityUtils.toString(entity, Charset.defaultCharset());
        EntityUtils.consume(entity);
        return res;
    }


    /**
     * POST 요청 전송
     *
     * @param url
     * @param map
     * @return
     * @throws Exception
     */
    public String get(String url, Map<String, Object> map,Map<String,String> headers) throws Exception {
        String params = null;
        if (null != map) {
            // 파라미터를 담을 List 선언
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            // map을 순회하며 파라미터를 list에 설정
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                list.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }
            // 파라미터 변환
            params = EntityUtils.toString(new UrlEncodedFormEntity(list, "utf-8"));
        }
        url += StringUtils.isNotBlank(params) ? ("?" + params) : "";
        HttpGet httpGet = new HttpGet(url);
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entity : headers.entrySet()) {
                httpGet.setHeader(entity.getKey(), entity.getValue());
            }
        }
        // HttpClient로 요청을 보내고 response 반환
        CloseableHttpResponse response = httpClient.execute(httpGet);
        // 엔티티 조회
        HttpEntity entity = response.getEntity();
        // 엔티티를 문자열로 변환
        String res = EntityUtils.toString(entity, Charset.defaultCharset());
        EntityUtils.consume(entity);
        response.close();
        return res;
    }

    /**
     * POST 요청 전송(JSON 파라미터)
     *
     * @param url
     * @param json
     * @return
     * @throws IOException
     */
    public String post(String url, String json,Map<String,String> headers) throws IOException {
        // POST 요청 선언
        HttpPost httpPost = new HttpPost(url);
        // 클라이언트가 서버로 보내는 데이터 형식
        httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
        httpPost.setHeader("Accept", "application/json");
        StringEntity param = new StringEntity(json, ContentType.APPLICATION_JSON);
        httpPost.setEntity(param);
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entity : headers.entrySet()) {
                httpPost.setHeader(entity.getKey(), entity.getValue());
            }
        }
        CloseableHttpResponse resp = this.httpClient.execute(httpPost);
        HttpEntity entity = resp.getEntity();
        // 엔티티를 문자열로 변환
        String res = EntityUtils.toString(entity, Charset.defaultCharset());
        EntityUtils.consume(entity);
        resp.close();
        logger.info("post-- json:{} || res:{} ",json,res);
        return res;
    }
}
