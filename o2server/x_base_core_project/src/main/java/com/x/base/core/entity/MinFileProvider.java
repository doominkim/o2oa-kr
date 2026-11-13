package com.x.base.core.entity;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemConfigBuilder;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.AbstractOriginatingFileProvider;
import org.apache.commons.vfs2.provider.GenericFileName;
import org.apache.commons.vfs2.provider.HostFileNameParser;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * commons-vfs2 provider for {@code min://accessKey:secretKey@host:port/bucket/key}.
 * Talks to an S3 compatible store (MinIO, Garage, ...) with path-style requests
 * signed by AWS SigV4. Folders are virtual (key prefixes).
 */
public class MinFileProvider extends AbstractOriginatingFileProvider {

	public static final String SCHEME = "min";

	static final Collection<Capability> CAPABILITIES = Collections.unmodifiableCollection(Arrays.asList(
			Capability.CREATE, Capability.DELETE, Capability.GET_TYPE, Capability.GET_LAST_MODIFIED,
			Capability.LIST_CHILDREN, Capability.READ_CONTENT, Capability.URI, Capability.WRITE_CONTENT));

	/* 서명은 요청마다 하므로 HTTP 클라이언트는 파일시스템과 무관하게 provider 하나가 공유한다. */
	private final CloseableHttpClient httpClient;

	public MinFileProvider() {
		setFileNameParser(new HostFileNameParser(-1));
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setMaxTotal(200);
		connectionManager.setDefaultMaxPerRoute(200);
		this.httpClient = HttpClients.custom().setConnectionManager(connectionManager).disableContentCompression()
				.disableCookieManagement().disableRedirectHandling().build();
	}

	@Override
	protected FileSystem doCreateFileSystem(FileName rootName, FileSystemOptions fileSystemOptions)
			throws FileSystemException {
		return new MinFileSystem((GenericFileName) rootName, fileSystemOptions, httpClient);
	}

	@Override
	public FileSystemConfigBuilder getConfigBuilder() {
		return MinFileSystemConfigBuilder.getInstance();
	}

	@Override
	public Collection<Capability> getCapabilities() {
		return CAPABILITIES;
	}

	@Override
	public void close() {
		super.close();
		try {
			httpClient.close();
		} catch (IOException e) {
			// ignore on shutdown
		}
	}
}
