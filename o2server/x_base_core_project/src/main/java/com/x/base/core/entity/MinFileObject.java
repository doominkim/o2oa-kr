package com.x.base.core.entity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.AbstractFileObject;
import org.apache.commons.vfs2.provider.UriParser;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * An S3 object (FILE) or key prefix (virtual FOLDER). Path layout:
 * {@code /bucket/key}. The file system root lists buckets, a bucket is always a
 * folder.
 */
public class MinFileObject extends AbstractFileObject<MinFileSystem> {

	private static final String DELIMITER = "/";

	/** null at the file system root. */
	private final String bucket;
	/** empty for the bucket itself. */
	private final String key;

	private FileType type;
	private long contentSize;
	private long lastModified;

	protected MinFileObject(AbstractFileName name, MinFileSystem fileSystem) throws FileSystemException {
		super(name, fileSystem);
		String path = name.getPathDecoded();
		while (path.startsWith(DELIMITER)) {
			path = path.substring(1);
		}
		if (path.isEmpty()) {
			this.bucket = null;
			this.key = "";
		} else {
			int idx = path.indexOf('/');
			this.bucket = idx < 0 ? path : path.substring(0, idx);
			this.key = idx < 0 ? "" : path.substring(idx + 1);
		}
	}

	@Override
	protected void doAttach() throws Exception {
		if (bucket == null || key.isEmpty()) {
			type = FileType.FOLDER;
			return;
		}
		try (CloseableHttpResponse response = fs().execute(HttpHead.METHOD_NAME, bucket, key, null, null, null)) {
			int status = response.getStatusLine().getStatusCode();
			if (status == HttpStatus.SC_OK) {
				type = FileType.FILE;
				Header length = response.getFirstHeader("Content-Length");
				contentSize = length == null ? 0L : Long.parseLong(length.getValue().trim());
				Header modified = response.getFirstHeader("Last-Modified");
				Date date = modified == null ? null : DateUtils.parseDate(modified.getValue());
				lastModified = date == null ? 0L : date.getTime();
				return;
			}
			if (status != HttpStatus.SC_NOT_FOUND) {
				throw fs().error("HEAD", bucket, key, response);
			}
		}
		type = hasAnyUnder(key + DELIMITER) ? FileType.FOLDER : FileType.IMAGINARY;
	}

	@Override
	protected void doDetach() throws Exception {
		type = null;
		contentSize = 0L;
		lastModified = 0L;
	}

	@Override
	protected FileType doGetType() throws Exception {
		return type;
	}

	@Override
	protected long doGetContentSize() throws Exception {
		return contentSize;
	}

	@Override
	protected long doGetLastModifiedTime() throws Exception {
		return lastModified;
	}

	@Override
	protected InputStream doGetInputStream() throws Exception {
		return openObject();
	}

	@Override
	protected InputStream doGetInputStream(int bufferSize) throws Exception {
		return openObject();
	}

	@Override
	protected OutputStream doGetOutputStream(boolean append) throws Exception {
		if (append) {
			throw new IOException("append is not supported by S3: " + bucket + "/" + key);
		}
		return new UploadOutputStream();
	}

	/** S3에는 폴더가 없다. 상위 폴더 생성 요청은 가상으로 처리한다. */
	@Override
	protected void doCreateFolder() throws Exception {
		// virtual folder: nothing to create
	}

	@Override
	protected void doDelete() throws Exception {
		if (bucket == null || key.isEmpty()) {
			throw new IOException("deleting a bucket or the root is not supported: " + bucket);
		}
		// 폴더면 다른 도구가 만들었을 수 있는 "key/" 마커만 지운다.
		deleteObject(type == FileType.FOLDER ? key + DELIMITER : key);
	}

	@Override
	protected String[] doListChildren() throws Exception {
		List<String> names = (bucket == null) ? listBuckets() : listPrefix();
		String[] encoded = new String[names.size()];
		for (int i = 0; i < encoded.length; i++) {
			encoded[i] = UriParser.encode(names.get(i));
		}
		return encoded;
	}

	private MinFileSystem fs() {
		return getAbstractFileSystem();
	}

	private InputStream openObject() throws IOException {
		CloseableHttpResponse response = fs().execute(HttpGet.METHOD_NAME, bucket, key, null, null, null);
		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			try {
				throw fs().error("GET", bucket, key, response);
			} finally {
				response.close();
			}
		}
		return new FilterInputStream(response.getEntity().getContent()) {
			@Override
			public void close() throws IOException {
				try {
					super.close();
				} finally {
					response.close();
				}
			}
		};
	}

	private void deleteObject(String objectKey) throws IOException {
		try (CloseableHttpResponse response = fs().execute(HttpDelete.METHOD_NAME, bucket, objectKey, null, null,
				null)) {
			int status = response.getStatusLine().getStatusCode();
			if (status != HttpStatus.SC_NO_CONTENT && status != HttpStatus.SC_OK
					&& status != HttpStatus.SC_NOT_FOUND) {
				throw fs().error("DELETE", bucket, objectKey, response);
			}
			MinFileSystem.consume(response);
		}
	}

	private boolean hasAnyUnder(String prefix) throws IOException {
		Map<String, String> query = new LinkedHashMap<>();
		query.put("list-type", "2");
		query.put("prefix", prefix);
		query.put("max-keys", "1");
		Document doc = listObjects(query);
		String count = MinFileSystem.text(doc, "KeyCount");
		if (count != null) {
			return Integer.parseInt(count.trim()) > 0;
		}
		return doc.getElementsByTagName("Contents").getLength() > 0
				|| doc.getElementsByTagName("CommonPrefixes").getLength() > 0;
	}

	private List<String> listPrefix() throws IOException {
		String prefix = key.isEmpty() ? "" : key + DELIMITER;
		List<String> names = new ArrayList<>();
		String token = null;
		do {
			Map<String, String> query = new LinkedHashMap<>();
			query.put("list-type", "2");
			query.put("prefix", prefix);
			query.put("delimiter", DELIMITER);
			if (token != null) {
				query.put("continuation-token", token);
			}
			Document doc = listObjects(query);
			collect(doc, "CommonPrefixes", "Prefix", prefix, names);
			collect(doc, "Contents", "Key", prefix, names);
			token = "true".equals(MinFileSystem.text(doc, "IsTruncated"))
					? MinFileSystem.text(doc, "NextContinuationToken")
					: null;
		} while (token != null && !token.isEmpty());
		return names;
	}

	private static void collect(Document doc, String parentTag, String childTag, String prefix, List<String> names) {
		NodeList nodes = doc.getElementsByTagName(parentTag);
		for (int i = 0; i < nodes.getLength(); i++) {
			NodeList values = ((Element) nodes.item(i)).getElementsByTagName(childTag);
			if (values.getLength() == 0) {
				continue;
			}
			String name = values.item(0).getTextContent().substring(prefix.length());
			if (name.endsWith(DELIMITER)) {
				name = name.substring(0, name.length() - 1);
			}
			if (!name.isEmpty()) {
				names.add(name);
			}
		}
	}

	private Document listObjects(Map<String, String> query) throws IOException {
		try (CloseableHttpResponse response = fs().execute(HttpGet.METHOD_NAME, bucket, null, query, null, null)) {
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw fs().error("LIST", bucket, query.get("prefix"), response);
			}
			try (InputStream in = response.getEntity().getContent()) {
				return MinFileSystem.parseXml(in);
			}
		}
	}

	private List<String> listBuckets() throws IOException {
		try (CloseableHttpResponse response = fs().execute(HttpGet.METHOD_NAME, null, null, null, null, null)) {
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw fs().error("LIST", "", null, response);
			}
			Document doc;
			try (InputStream in = response.getEntity().getContent()) {
				doc = MinFileSystem.parseXml(in);
			}
			List<String> names = new ArrayList<>();
			NodeList buckets = doc.getElementsByTagName("Bucket");
			for (int i = 0; i < buckets.getLength(); i++) {
				NodeList name = ((Element) buckets.item(i)).getElementsByTagName("Name");
				if (name.getLength() > 0) {
					names.add(name.item(0).getTextContent());
				}
			}
			return names;
		}
	}

	/**
	 * 내용을 임시 파일에 버퍼링하면서 SHA-256을 계산하고, close 시점에 한 번에 PUT한다.
	 */
	private final class UploadOutputStream extends OutputStream {

		private final File temp;
		private final MessageDigest digest;
		private final OutputStream out;
		private long written;
		private boolean closed;

		UploadOutputStream() throws IOException {
			this.temp = Files.createTempFile("o2min-", ".upload").toFile();
			this.digest = S3SigV4.newSha256();
			try {
				this.out = new BufferedOutputStream(new DigestOutputStream(Files.newOutputStream(temp.toPath()), digest),
						64 * 1024);
			} catch (IOException e) {
				Files.deleteIfExists(temp.toPath());
				throw e;
			}
		}

		@Override
		public void write(int b) throws IOException {
			out.write(b);
			written++;
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			out.write(b, off, len);
			written += len;
		}

		@Override
		public void flush() throws IOException {
			out.flush();
		}

		@Override
		public void close() throws IOException {
			if (closed) {
				return;
			}
			closed = true;
			try {
				out.close();
				String payloadHash = S3SigV4.hex(digest.digest());
				FileEntity entity = new FileEntity(temp, ContentType.APPLICATION_OCTET_STREAM);
				try (CloseableHttpResponse response = fs().execute(HttpPut.METHOD_NAME, bucket, key, null, entity,
						payloadHash)) {
					if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
						throw fs().error("PUT", bucket, key, response);
					}
					MinFileSystem.consume(response);
				}
				contentSize = written;
				lastModified = System.currentTimeMillis();
			} finally {
				Files.deleteIfExists(temp.toPath());
			}
		}
	}
}
