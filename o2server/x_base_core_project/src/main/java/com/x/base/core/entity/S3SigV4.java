package com.x.base.core.entity;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * AWS Signature Version 4 header signing for S3 REST requests (service "s3").
 */
final class S3SigV4 {

	/** SHA-256 of an empty payload. */
	static final String EMPTY_SHA256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";

	private static final String ALGORITHM = "AWS4-HMAC-SHA256";
	private static final String SIGNED_HEADERS = "host;x-amz-content-sha256;x-amz-date";
	private static final DateTimeFormatter AMZ_DATE = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
	private static final DateTimeFormatter SCOPE_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
	private static final char[] HEX = "0123456789abcdef".toCharArray();

	private S3SigV4() {
	}

	/**
	 * Sets Host, x-amz-date, x-amz-content-sha256 and Authorization headers.
	 *
	 * @param canonicalUri   already URI-encoded path, identical to the request path
	 * @param canonicalQuery already encoded and sorted query, identical to the
	 *                       request query (empty string when none)
	 */
	static void sign(HttpRequestBase request, String host, String canonicalUri, String canonicalQuery,
			String payloadHash, String accessKey, String secretKey, String region) {
		ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
		String amzDate = AMZ_DATE.format(now);
		String scopeDate = SCOPE_DATE.format(now);
		String canonicalRequest = request.getMethod() + '\n' + canonicalUri + '\n' + canonicalQuery + '\n' + "host:"
				+ host + '\n' + "x-amz-content-sha256:" + payloadHash + '\n' + "x-amz-date:" + amzDate + '\n' + '\n'
				+ SIGNED_HEADERS + '\n' + payloadHash;
		String scope = scopeDate + '/' + region + "/s3/aws4_request";
		String stringToSign = ALGORITHM + '\n' + amzDate + '\n' + scope + '\n'
				+ hex(sha256(canonicalRequest.getBytes(StandardCharsets.UTF_8)));
		byte[] key = hmac(("AWS4" + secretKey).getBytes(StandardCharsets.UTF_8), scopeDate);
		key = hmac(key, region);
		key = hmac(key, "s3");
		key = hmac(key, "aws4_request");
		String signature = hex(hmac(key, stringToSign));
		request.setHeader("Host", host);
		request.setHeader("x-amz-date", amzDate);
		request.setHeader("x-amz-content-sha256", payloadHash);
		request.setHeader("Authorization", ALGORITHM + " Credential=" + accessKey + '/' + scope + ", SignedHeaders="
				+ SIGNED_HEADERS + ", Signature=" + signature);
	}

	/** Encoded, key-sorted query string as required by SigV4. */
	static String canonicalQuery(Map<String, String> params) {
		if (params == null || params.isEmpty()) {
			return "";
		}
		TreeMap<String, String> sorted = new TreeMap<>();
		params.forEach((k, v) -> sorted.put(uriEncode(k, true), uriEncode(v == null ? "" : v, true)));
		StringBuilder sb = new StringBuilder();
		sorted.forEach((k, v) -> {
			if (sb.length() > 0) {
				sb.append('&');
			}
			sb.append(k).append('=').append(v);
		});
		return sb.toString();
	}

	/** RFC 3986 encoding: only A-Z a-z 0-9 - _ . ~ stay literal; '/' optional. */
	static String uriEncode(String value, boolean encodeSlash) {
		byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
		StringBuilder sb = new StringBuilder(bytes.length + 16);
		for (byte b : bytes) {
			char c = (char) (b & 0xff);
			if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '-' || c == '_'
					|| c == '.' || c == '~' || (c == '/' && !encodeSlash)) {
				sb.append(c);
			} else {
				sb.append('%').append(Character.toUpperCase(HEX[(b >> 4) & 0xf]))
						.append(Character.toUpperCase(HEX[b & 0xf]));
			}
		}
		return sb.toString();
	}

	static MessageDigest newSha256() {
		try {
			return MessageDigest.getInstance("SHA-256");
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException(e);
		}
	}

	static String hex(byte[] bytes) {
		char[] out = new char[bytes.length * 2];
		for (int i = 0; i < bytes.length; i++) {
			out[i * 2] = HEX[(bytes[i] >> 4) & 0xf];
			out[i * 2 + 1] = HEX[bytes[i] & 0xf];
		}
		return new String(out);
	}

	private static byte[] sha256(byte[] data) {
		return newSha256().digest(data);
	}

	private static byte[] hmac(byte[] key, String data) {
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(new SecretKeySpec(key, "HmacSHA256"));
			return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException(e);
		}
	}
}
