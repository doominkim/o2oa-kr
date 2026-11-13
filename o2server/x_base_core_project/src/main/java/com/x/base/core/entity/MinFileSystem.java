package com.x.base.core.entity;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.AbstractFileSystem;
import org.apache.commons.vfs2.provider.GenericFileName;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * One S3 endpoint + credential pair. Holds connection settings and performs
 * signed path-style requests for {@link MinFileObject}.
 */
public class MinFileSystem extends AbstractFileSystem {

	private static final int ERROR_BODY_LIMIT = 4096;

	private final CloseableHttpClient httpClient;
	private final String endpoint;
	private final String hostHeader;
	private final String accessKey;
	private final String secretKey;
	private final String region;
	private final RequestConfig requestConfig;

	protected MinFileSystem(GenericFileName rootName, FileSystemOptions options, CloseableHttpClient httpClient) {
		super(rootName, null, options);
		MinFileSystemConfigBuilder builder = MinFileSystemConfigBuilder.getInstance();
		boolean https = builder.isUseHttps(options);
		int port = rootName.getPort();
		boolean defaultPort = port <= 0 || (https && port == 443) || (!https && port == 80);
		this.hostHeader = defaultPort ? rootName.getHostName() : rootName.getHostName() + ":" + port;
		this.endpoint = (https ? "https://" : "http://") + this.hostHeader;
		this.accessKey = rootName.getUserName();
		this.secretKey = rootName.getPassword();
		this.region = builder.getRegion(options);
		int timeout = (int) Math.min(Integer.MAX_VALUE, Math.max(0L, builder.getTaskTimeOut(options)));
		this.requestConfig = RequestConfig.custom().setConnectTimeout(timeout).setSocketTimeout(timeout)
				.setConnectionRequestTimeout(timeout).build();
		this.httpClient = httpClient;
	}

	@Override
	protected void addCapabilities(Collection<Capability> caps) {
		caps.addAll(MinFileProvider.CAPABILITIES);
	}

	@Override
	protected FileObject createFile(AbstractFileName name) throws Exception {
		return new MinFileObject(name, this);
	}

	/**
	 * Executes a signed request. Caller must close the response.
	 *
	 * @param bucket null for the service root (ListBuckets)
	 * @param key    null or empty for bucket level requests
	 */
	CloseableHttpResponse execute(String method, String bucket, String key, Map<String, String> query,
			HttpEntity entity, String payloadHash) throws IOException {
		StringBuilder uri = new StringBuilder("/");
		if (bucket != null) {
			uri.append(S3SigV4.uriEncode(bucket, true));
			if (key != null && !key.isEmpty()) {
				uri.append('/').append(S3SigV4.uriEncode(key, false));
			}
		}
		String canonicalUri = uri.toString();
		String canonicalQuery = S3SigV4.canonicalQuery(query);
		String target = endpoint + canonicalUri + (canonicalQuery.isEmpty() ? "" : "?" + canonicalQuery);
		HttpRequestBase request;
		switch (method) {
		case HttpHead.METHOD_NAME:
			request = new HttpHead(target);
			break;
		case HttpGet.METHOD_NAME:
			request = new HttpGet(target);
			break;
		case HttpPut.METHOD_NAME:
			request = new HttpPut(target);
			break;
		case HttpDelete.METHOD_NAME:
			request = new HttpDelete(target);
			break;
		default:
			throw new IllegalArgumentException("unsupported method: " + method);
		}
		if (entity != null) {
			((HttpEntityEnclosingRequestBase) request).setEntity(entity);
		}
		request.setConfig(requestConfig);
		S3SigV4.sign(request, hostHeader, canonicalUri, canonicalQuery,
				payloadHash == null ? S3SigV4.EMPTY_SHA256 : payloadHash, accessKey, secretKey, region);
		return httpClient.execute(request);
	}

	/** Builds an exception from an S3 error response (status + Code + Message). */
	IOException error(String operation, String bucket, String key, CloseableHttpResponse response) {
		int status = response.getStatusLine().getStatusCode();
		String detail = "";
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			try (InputStream in = entity.getContent()) {
				byte[] body = in.readNBytes(ERROR_BODY_LIMIT);
				String text = new String(body, StandardCharsets.UTF_8);
				detail = (tag(text, "Code") + " " + tag(text, "Message")).trim();
			} catch (IOException e) {
				// keep status only
			}
		}
		return new IOException("S3 " + operation + " " + bucket + "/" + (key == null ? "" : key) + " failed: HTTP "
				+ status + (detail.isEmpty() ? "" : " " + detail));
	}

	static void consume(CloseableHttpResponse response) {
		EntityUtils.consumeQuietly(response.getEntity());
	}

	static Document parseXml(InputStream in) throws IOException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			factory.setExpandEntityReferences(false);
			factory.setNamespaceAware(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(in);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException("invalid S3 xml response", e);
		}
	}

	static String text(Document doc, String tag) {
		NodeList list = doc.getElementsByTagName(tag);
		return list.getLength() == 0 ? null : list.item(0).getTextContent();
	}

	private static String tag(String xml, String name) {
		int start = xml.indexOf("<" + name + ">");
		int end = xml.indexOf("</" + name + ">");
		if (start < 0 || end < start) {
			return "";
		}
		return xml.substring(start + name.length() + 2, end);
	}
}
