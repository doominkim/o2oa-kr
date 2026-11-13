package com.x.base.core.entity;

import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.StringTools;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.CacheStrategy;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.cache.NullFilesCache;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.ftp.FtpFileType;
import org.apache.commons.vfs2.provider.ftps.FtpsFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.webdav4.Webdav4FileSystemConfigBuilder;

@MappedSuperclass
public abstract class StorageObject extends SliceJpaObject {

	private static FileSystemManager fileSystemManagerInstance;

	private synchronized FileSystemManager getFileSystemManager() throws FileSystemException {
		if (fileSystemManagerInstance == null) {
			StandardFileSystemManager fs = new StandardFileSystemManager();
			fs.setFilesCache(new NullFilesCache());
			fs.setCacheStrategy(CacheStrategy.ON_RESOLVE);
			fs.init();
			// 무료 배포판에 min provider jar가 없어 직접 구현한 S3 provider를 등록한다.
			if (!fs.hasProvider(MinFileProvider.SCHEME)) {
				fs.addProvider(MinFileProvider.SCHEME, new MinFileProvider());
			}
			fileSystemManagerInstance = fs;

		}
		return fileSystemManagerInstance;
	}

	private static final long serialVersionUID = 7823729771901802653L;

	public static final String PATHSEPARATOR = "/";

	public static final String DELETE_OPERATE = "delete";

	public static final String ENCRYPTKEY_FIELDNAME = "encryptKey";

	public static final String CRYPTOGRAPHIC_ALGORITHM_AES = "AES";
	public static final String CRYPTOGRAPHIC_ALGORITHM_AES_GCM_NOPADDING = "AES/GCM/NoPadding";

	public static final String MIN_HTTPS = "https";

	@FieldDescribe("파일 키.")
	@Column(name = ColumnNamePrefix + ENCRYPTKEY_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String encryptKey;

	public String getEncryptKey() {
		return encryptKey;
	}

	public void setEncryptKey(String encryptKey) {
		this.encryptKey = encryptKey;
	}

	public abstract String path() throws Exception;

	public abstract String getStorage();

	public abstract void setStorage(String storage);

	public abstract Long getLength();

	public abstract void setLength(Long length);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract String getExtension();

	public abstract void setExtension(String extension);

	public abstract Date getLastUpdateTime();

	public abstract void setLastUpdateTime(Date lastUpdateTime);

	public abstract Boolean getDeepPath();

	public abstract void setDeepPath(Boolean deepPath);

	public String path(String operate) throws Exception {
		return this.path();
	}

	@Transient
	private byte[] bytes;

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	/** 내용을 bytes 필드로 가져오기, 가져오기/내보내기에 사용 */
	public Long dumpContent(StorageMapping mapping) throws Exception {
		long length = -1L;
		try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			length = this.readContent(mapping, output);
			if (length < 0) {
				this.setBytes(new byte[] {});
			} else {
				this.setBytes(output.toByteArray());
			}
		}
		return length;
	}

	public Long saveContent(StorageMapping mapping, byte[] bytes, String name) throws Exception {
		try (InputStream input = new ByteArrayInputStream(bytes)) {
			return saveContent(mapping, input, name, 0);
		}
	}

	public Long saveContent(StorageMapping mapping, byte[] bytes, String name, Integer encrypt) throws Exception {
		try (InputStream input = new ByteArrayInputStream(bytes)) {
			return saveContent(mapping, input, name, encrypt);
		}
	}

	public Long saveContent(StorageMapping mapping, InputStream input, String name) throws Exception {
		return this.saveContent(mapping, input, name, 0);
	}

	/**
	 * 가져온 스트림 저장
	 *
	 * @param mapping
	 * @param input
	 * @param name
	 * @param encrypt
	 * @return
	 * @throws Exception
	 */
	public Long saveContent(StorageMapping mapping, InputStream input, String name, Integer encrypt) throws Exception {
		this.setDeepPath(mapping.getDeepPath());
		return this.updateContent(mapping, input, name, encrypt);
	}

	public Long updateContent(StorageMapping mapping, byte[] bytes) throws Exception {
		try (InputStream input = new ByteArrayInputStream(bytes)) {
			return updateContent(mapping, input, null, 0);
		}
	}

	public Long updateContent(StorageMapping mapping, byte[] bytes, String name) throws Exception {
		try (ByteArrayInputStream input = new ByteArrayInputStream(bytes)) {
			return updateContent(mapping, input, name, 0);
		}
	}

	public Long updateContent(StorageMapping mapping, byte[] bytes, Integer encrypt) throws Exception {
		try (ByteArrayInputStream input = new ByteArrayInputStream(bytes)) {
			return updateContent(mapping, input, null, encrypt);
		}
	}

	public Long updateContent(StorageMapping mapping, byte[] bytes, String name, Integer encrypt) throws Exception {
		try (ByteArrayInputStream input = new ByteArrayInputStream(bytes)) {
			return updateContent(mapping, input, name, encrypt);
		}
	}

	public Long updateContent(StorageMapping mapping, InputStream input) throws Exception {
		return updateContent(mapping, input, null, 0);
	}

	public Long updateContent(StorageMapping mapping, InputStream input, String name) throws Exception {
		return updateContent(mapping, input, name, 0);
	}

	public Long updateContent(StorageMapping mapping, InputStream input, Integer encrypt) throws Exception {
		return updateContent(mapping, input, null, encrypt);
	}

	/**
	 * 내용 업데이트
	 *
	 * @param mapping
	 * @param input
	 * @param name
	 * @param encrypt
	 * @return
	 * @throws Exception
	 */
	public Long updateContent(StorageMapping mapping, InputStream input, String name, Integer encrypt)
			throws Exception {
		Objects.requireNonNull(encrypt);
		if (StringUtils.isNotEmpty(name)) {
			this.setName(name);
			this.setExtension(StringUtils.lowerCase(FilenameUtils.getExtension(name)));
		}
		if ((encrypt == 1) || (encrypt == 2)) {
			if (StringUtils.isBlank(this.getEncryptKey())) {
				this.setEncryptKey(encrypt.toString() + StringUtils.substring(StringTools.uniqueToken(), 0, 31));
			}
		} else {
			this.setEncryptKey("");
		}
		return this.vfsUpdateContent(mapping, input);
	}

	/**
	 * 내용 읽기
	 *
	 * @param mapping
	 * @return
	 * @throws Exception
	 */
	public byte[] readContent(StorageMapping mapping) throws Exception {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			readContent(mapping, baos);
			return baos.toByteArray();
		}
	}

	/**
	 * 내용을 output으로 출력
	 *
	 * @param mapping
	 * @param output
	 * @return
	 * @throws Exception
	 */
	public Long readContent(StorageMapping mapping, OutputStream output) throws Exception {
		return vfsReadContent(mapping, output);
	}

	/**
	 * 내용 존재 여부 확인
	 *
	 * @param mapping
	 * @return
	 * @throws Exception
	 */
	public boolean existContent(StorageMapping mapping) throws Exception {
		return vfsExistContent(mapping);
	}

	public void deleteContent(StorageMapping mapping) throws Exception {
		vfsDeleteContent(mapping);
	}

	/**
	 * 전체 접근 경로의 앞부분 조회
	 *
	 * @param mapping
	 * @return
	 * @throws IllegalStateException
	 * @throws UnsupportedEncodingException
	 */
	private String getPrefix(StorageMapping mapping) throws IllegalStateException, UnsupportedEncodingException {
		String prefix = "";
		if (null == mapping.getProtocol()) {
			throw new IllegalStateException("storage protocol is null.");
		}
		String split = "//";
		switch (mapping.getProtocol()) {
		case ftp:
			prefix = "ftp://" + URLEncoder.encode(mapping.getUsername(), DefaultCharset.name) + ":"
					+ URLEncoder.encode(mapping.getPassword(), DefaultCharset.name) + "@" + mapping.getHost() + ":"
					+ mapping.getPort();
			break;
		case ftps:
			prefix = "ftps://" + URLEncoder.encode(mapping.getUsername(), DefaultCharset.name) + ":"
					+ URLEncoder.encode(mapping.getPassword(), DefaultCharset.name) + "@" + mapping.getHost() + ":"
					+ mapping.getPort();
			break;
		case sftp:
			prefix = "sftp://" + URLEncoder.encode(mapping.getUsername(), DefaultCharset.name) + ":"
					+ URLEncoder.encode(mapping.getPassword(), DefaultCharset.name) + "@" + mapping.getHost() + ":"
					+ mapping.getPort();
			break;
		case cifs:
			prefix = "smb://" + URLEncoder.encode(mapping.getUsername(), DefaultCharset.name) + ":"
					+ URLEncoder.encode(mapping.getPassword(), DefaultCharset.name) + "@" + mapping.getHost() + ":"
					+ mapping.getPort();
			break;
		case webdav:
			prefix = "webdav4://" + URLEncoder.encode(mapping.getUsername(), DefaultCharset.name) + ":"
					+ URLEncoder.encode(mapping.getPassword(), DefaultCharset.name) + "@" + mapping.getHost() + ":"
					+ mapping.getPort();
			break;
		case ali:
			prefix = "ali://" + URLEncoder.encode(mapping.getUsername(), DefaultCharset.name) + ":"
					+ URLEncoder.encode(mapping.getPassword(), DefaultCharset.name) + "@" + mapping.getHost() + "/"
					+ mapping.getName();
			break;
		case s3:
			prefix = "s3://" + URLEncoder.encode(mapping.getUsername(), DefaultCharset.name) + ":"
					+ URLEncoder.encode(mapping.getPassword(), DefaultCharset.name) + "@" + mapping.getHost();
			if (StringUtils.isNotBlank(mapping.getName()) && !mapping.getHost().startsWith(mapping.getName())) {
				prefix = prefix + "/" + mapping.getName();
			}
			break;
		case min:
			prefix = "min://" + URLEncoder.encode(mapping.getUsername(), DefaultCharset.name) + ":"
					+ URLEncoder.encode(mapping.getPassword(), DefaultCharset.name) + "@";
			if (mapping.getHost().indexOf(split) > -1) {
				prefix = prefix + StringUtils.substringAfter(mapping.getHost(), split);
			} else {
				prefix = prefix + mapping.getHost();
			}
			prefix = prefix.equals("/") ? prefix + mapping.getName() : prefix + "/" + mapping.getName();
			break;
		case cos:
			prefix = "cos://" + URLEncoder.encode(mapping.getUsername(), DefaultCharset.name) + ":"
					+ URLEncoder.encode(mapping.getPassword(), DefaultCharset.name) + "@";
			if (mapping.getHost().indexOf(split) > -1) {
				prefix = prefix + StringUtils.substringAfter(mapping.getHost(), split);
			} else {
				prefix = prefix + mapping.getHost();
			}
			break;
		case file:
			prefix = "file://";
			break;
		default:
			break;
		}
		String mappingPrefix = "";
		if (StringUtils.isNotBlank(mapping.getPrefix())) {
			if (mapping.getPrefix().startsWith("/")) {
				mappingPrefix = mapping.getPrefix();
			} else {
				mappingPrefix = "/" + mapping.getPrefix();
			}
			if (mappingPrefix.endsWith("/")) {
				mappingPrefix = mappingPrefix.substring(0, mappingPrefix.length() - 1);
			}
		}
		return prefix + mappingPrefix;
	}

	private FileSystemOptions getOptions(StorageMapping mapping) throws FileSystemException {
		FileSystemOptions opts = new FileSystemOptions();
		if (null == mapping.getProtocol()) {
			throw new IllegalStateException("storage protocol is null.");
		}
		switch (mapping.getProtocol()) {
		case sftp:
			SftpFileSystemConfigBuilder sftpBuilder = SftpFileSystemConfigBuilder.getInstance();
			sftpBuilder.setConnectTimeout(opts, Duration.ofMillis(10000));
			sftpBuilder.setSessionTimeout(opts, Duration.ofMillis(30000));
			sftpBuilder.setFileNameEncoding(opts, DefaultCharset.name);
			// By default, the path is relative to the user's home directory. This can be
			// changed with:
			sftpBuilder.setStrictHostKeyChecking(opts, "no");
			sftpBuilder.setUserDirIsRoot(opts, false);
			break;
		case ftp:
			FtpFileSystemConfigBuilder ftpBuilder = FtpFileSystemConfigBuilder.getInstance();
			/*
			 * 패시브 모드를 사용하면 알리클라우드 centos7에서 자주 연결 실패 발생 Caused by: java.net.ConnectException:
			 * Connection timed out (Connection timed out) at
			 * java.net.PlainSocketImpl.socketConnect(Native Method) at
			 * java.net.AbstractPlainSocketImpl.doConnect(AbstractPlainSocketImpl.java:350)
			 * at java.net.AbstractPlainSocketImpl.connectToAddress(AbstractPlainSocketImpl.
			 * java:206) at
			 * java.net.AbstractPlainSocketImpl.connect(AbstractPlainSocketImpl.java:188) at
			 * java.net.SocksSocketImpl.connect(SocksSocketImpl.java:392) at
			 * java.net.Socket.connect(Socket.java:589)
			 */
			ftpBuilder.setPassiveMode(opts, true);
			// IP 검증 강제 생략
			ftpBuilder.setRemoteVerification(opts, false);
			// FtpFileType.BINARY is the default
			ftpBuilder.setFileType(opts, FtpFileType.BINARY);
			ftpBuilder.setConnectTimeout(opts, Duration.ofMillis(10000));
			ftpBuilder.setSoTimeout(opts, Duration.ofMillis(10000));
			ftpBuilder.setControlEncoding(opts, DefaultCharset.name);
			break;
		case ftps:
			FtpsFileSystemConfigBuilder ftpsBuilder = FtpsFileSystemConfigBuilder.getInstance();
			ftpsBuilder.setPassiveMode(opts, true);
			// IP 검증 강제 생략
			ftpsBuilder.setRemoteVerification(opts, false);
			// FtpFileType.BINARY is the default
			ftpsBuilder.setFileType(opts, FtpFileType.BINARY);
			ftpsBuilder.setConnectTimeout(opts, Duration.ofMillis(10000));
			ftpsBuilder.setSoTimeout(opts, Duration.ofMillis(10000));
			ftpsBuilder.setControlEncoding(opts, DefaultCharset.name);
			break;
		case webdav:
			Webdav4FileSystemConfigBuilder webdavBuilder = Webdav4FileSystemConfigBuilder.getInstance();
			webdavBuilder.setConnectionTimeout(opts, Duration.ofMillis(10000));
			webdavBuilder.setSoTimeout(opts, Duration.ofMillis(10000));
			webdavBuilder.setUrlCharset(opts, DefaultCharset.name);
			webdavBuilder.setMaxConnectionsPerHost(opts, 200);
			webdavBuilder.setMaxTotalConnections(opts, 200);
			webdavBuilder.setFollowRedirect(opts, true);
			break;
		case min:
			MinFileSystemConfigBuilder minBuilder = MinFileSystemConfigBuilder.getInstance();
			minBuilder.setTaskTimeOut(opts, 10000L);
			minBuilder.setUseHttps(opts, mapping.getHost().startsWith(MIN_HTTPS));
			break;
		default:
			break;
		}
		return opts;
	}

	private Long vfsUpdateContent(StorageMapping mapping, InputStream inputStream) throws Exception {
		String prefix = this.getPrefix(mapping);
		String path = this.path();
		if (StringUtils.isEmpty(path)) {
			throw new IllegalStateException("path can not be empty.");
		}
		FileSystemOptions options = this.getOptions(mapping);
		long length = -1L;
		FileSystemManager manager = this.getFileSystemManager();
		/*
		 * 두 번 판단 필요. 프론트에서 nginx로 분산하는 경우 여러 파일 업로드가 동시에 발생할 수 있고, 동시에 파일 저장 디렉터리를 생성하면 백엔드에서 오류가 발생함
		 * org.apache.commons.vfs2.FileSystemException: Could not create folder
		 * "ftp://processPlatform:***@o2.server01.com:20040/20200601/1beb018a-5009-4baa-a9ef-7e903f9d48ef".
		 * 이 경우 다시 요청하여 파일 조회를 시도하면 문제가 해결됨.
		 */
		for (int i = 0; i < 2; i++) {
			try (FileObject fo = manager.resolveFile(prefix + PATHSEPARATOR + path, options);
					OutputStream output = fo.getContent().getOutputStream()) {
				length = computeIfHandleEncrypt(inputStream, output);
				this.setLength(length);
				if ((!Objects.equals(StorageProtocol.webdav, mapping.getProtocol()))
						&& (!Objects.equals(StorageProtocol.sftp, mapping.getProtocol()))
						&& (!Objects.equals(StorageProtocol.ali, mapping.getProtocol()))
						&& (!Objects.equals(StorageProtocol.s3, mapping.getProtocol()))) {
					/* webdav를 닫으면 commons.httpClient를 닫으려고 시도함 */
					manager.closeFileSystem(fo.getFileSystem());
				}
				this.setStorage(mapping.getName());
				this.setLastUpdateTime(new Date());
				break;
			} catch (FileSystemException fse) {
				if (i != 0) {
					// 첫 번째 오류는 건너뛰고 바로 두 번째 실행. 두 번째도 오류이면 오류 발생.
					throw fse;
				}
			}
		}
		return length;
	}

	/**
	 * vfs 데이터 읽기
	 *
	 * @param mapping
	 * @param output
	 * @return
	 * @throws Exception
	 */
	private Long vfsReadContent(StorageMapping mapping, OutputStream output) throws Exception {
		long length = -1L;
		FileSystemManager manager = this.getFileSystemManager();
		String prefix = this.getPrefix(mapping);
		String path = this.path();
		FileSystemOptions options = this.getOptions(mapping);
		try (FileObject fo = manager.resolveFile(prefix + PATHSEPARATOR + path, options)) {
			if (fo.exists() && fo.isFile()) {
				try (InputStream input = fo.getContent().getInputStream()) {
					length = computeIfHandleDecrypt(input, output);
				}
			} else {
				throw new FileNotFoundException(
						fo.getPublicURIString() + " not existed, object:" + this.toString() + ".");
			}
			if (!Objects.equals(StorageProtocol.webdav, mapping.getProtocol())
					&& (!Objects.equals(StorageProtocol.sftp, mapping.getProtocol()))
					&& (!Objects.equals(StorageProtocol.ali, mapping.getProtocol()))
					&& (!Objects.equals(StorageProtocol.s3, mapping.getProtocol()))) {
				/* webdav를 닫으면 commons.httpClient를 닫으려고 시도함 */
				manager.closeFileSystem(fo.getFileSystem());
			}
		}
		return length;
	}

	private boolean vfsExistContent(StorageMapping mapping) throws Exception {
		FileSystemManager manager = this.getFileSystemManager();
		String prefix = this.getPrefix(mapping);
		String path = this.path();
		FileSystemOptions options = this.getOptions(mapping);
		try (FileObject fo = manager.resolveFile(prefix + PATHSEPARATOR + path, options)) {
			return (fo.exists() && fo.isFile());
		}
	}

	/**
	 * 내용 삭제, 동시에 상위 디렉터리(한 단계만)가 비었는지 판단하여 비었으면 상위 디렉터리 삭제
	 *
	 * @param mapping
	 * @throws Exception
	 */
	private void vfsDeleteContent(StorageMapping mapping) throws Exception {
		FileSystemManager manager = this.getFileSystemManager();
		String prefix = this.getPrefix(mapping);
		String path = this.path(DELETE_OPERATE);
		FileSystemOptions options = this.getOptions(mapping);
		try (FileObject fo = manager.resolveFile(prefix + PATHSEPARATOR + path, options)) {
			if (fo.exists() && fo.isFile()) {
				fo.delete();
				if ((!StringUtils.startsWith(path, PATHSEPARATOR)) && (StringUtils.contains(path, PATHSEPARATOR))) {
					FileObject parent = fo.getParent();
					if ((null != parent) && parent.exists() && parent.isFolder()
							&& (parent.getChildren().length == 0)) {
						parent.delete();
					}
				}
			}
			if (!Objects.equals(StorageProtocol.webdav, mapping.getProtocol())
					&& (!Objects.equals(StorageProtocol.sftp, mapping.getProtocol()))
					&& (!Objects.equals(StorageProtocol.ali, mapping.getProtocol()))
					&& (!Objects.equals(StorageProtocol.s3, mapping.getProtocol()))) {
				// webdav를 닫으면 commons.httpClient를 닫으려고 시도함
				manager.closeFileSystem(fo.getFileSystem());
			}
		}
	}

	private Long computeIfHandleEncrypt(InputStream inputStream, OutputStream outputStream)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException {
		if (StringUtils.isNotBlank(this.getEncryptKey())) {
			if (StringUtils.startsWith(this.getEncryptKey(), "1")) {
				return encryptAES(inputStream, outputStream);
			} else if (StringUtils.startsWith(this.getEncryptKey(), "2")) {
				return encryptAESGCMNoPadding(inputStream, outputStream);
			} else {
				throw new IllegalStateException("error encrypt type.");
			}
		} else {
			return IOUtils.copyLarge(inputStream, outputStream);
		}
	}

	private Long computeIfHandleDecrypt(InputStream inputStream, OutputStream outputStream) throws IOException,
			NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
		if (StringUtils.isNotBlank(this.getEncryptKey())) {
			if (StringUtils.startsWith(this.getEncryptKey(), "1")) {
				return decryptAES(inputStream, outputStream);
			} else if (StringUtils.startsWith(this.getEncryptKey(), "2")) {
				return decryptAESGCMNoPadding(inputStream, outputStream);
			} else {
				throw new IllegalStateException("error encrypt type.");
			}
		} else {
			return IOUtils.copyLarge(inputStream, outputStream);
		}
	}

	private SecretKey computeIfEncryptKeyAbsent() {
		return new SecretKeySpec(this.getEncryptKey().getBytes(), CRYPTOGRAPHIC_ALGORITHM_AES);
	}

	private Long encryptAES(InputStream inputStream, OutputStream outputStream)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException {
		Cipher cipher = Cipher.getInstance(CRYPTOGRAPHIC_ALGORITHM_AES);
		cipher.init(Cipher.ENCRYPT_MODE, computeIfEncryptKeyAbsent());
		try (CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
			byte[] buffer = new byte[4096];
			int bytesRead;
			long length = 0L;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				cipherOutputStream.write(buffer, 0, bytesRead);
				length += bytesRead;
			}
			return length;
		}
	}

	private Long decryptAES(InputStream inputStream, OutputStream outputStream)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException {
		Cipher cipher = Cipher.getInstance(CRYPTOGRAPHIC_ALGORITHM_AES);
		cipher.init(Cipher.DECRYPT_MODE, computeIfEncryptKeyAbsent());
		try (CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher)) {
			byte[] buffer = new byte[4096];
			int bytesRead;
			long length = 0L;
			while ((bytesRead = cipherInputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
				length += bytesRead;
			}
			return length;
		}
	}

	private Long encryptAESGCMNoPadding(InputStream inputStream, OutputStream outputStream)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException {
		Cipher cipher = Cipher.getInstance(CRYPTOGRAPHIC_ALGORITHM_AES_GCM_NOPADDING);
		cipher.init(Cipher.ENCRYPT_MODE, computeIfEncryptKeyAbsent());
		byte[] iv = cipher.getIV();
		outputStream.write(iv);
		try (CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
			byte[] buffer = new byte[4096];
			int bytesRead;
			long length = 0L;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				cipherOutputStream.write(buffer, 0, bytesRead);
				length += bytesRead;
			}
			return length;
		}
	}

	private Long decryptAESGCMNoPadding(InputStream inputStream, OutputStream outputStream)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException,
			InvalidAlgorithmParameterException {
		byte[] iv = new byte[12]; // Read the IV from the encrypted stream
		if (12 != inputStream.read(iv)) {
			throw new IllegalStateException("Read the IV from the encrypted stream error.");
		}
		Cipher cipher = Cipher.getInstance(CRYPTOGRAPHIC_ALGORITHM_AES_GCM_NOPADDING);
		GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv);
		cipher.init(Cipher.DECRYPT_MODE, computeIfEncryptKeyAbsent(), gcmParameterSpec);
		try (CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher)) {
			byte[] buffer = new byte[4096];
			int bytesRead;
			long length = 0L;
			while ((bytesRead = cipherInputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
				length += bytesRead;
			}
			return length;
		}
	}

}
