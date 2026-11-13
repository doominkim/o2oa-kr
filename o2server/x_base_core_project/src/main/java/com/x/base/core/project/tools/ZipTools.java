package com.x.base.core.project.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ExtraDataRecord;
import net.lingala.zip4j.model.FileHeader;

/**
 * zip 유틸리티 클래스
 * 
 * @author sword
 */
public class ZipTools {

	private static final int ZIP_MAGIC_NUMBER_1 = 0x50;
	private static final int ZIP_MAGIC_NUMBER_2 = 0x4B;

	private static final int BUFFER_SIZE = 2 * 1024;
	private static final List<String> DEFAULT_IGNORE_LIST = List.of("__MACOSX", ".DS_Store");

	public static boolean isZipFile(Path path) {
		if (Files.exists(path) && Files.isRegularFile(path)) {
			try (InputStream in = Files.newInputStream(path)) {
				int magicNumber1 = in.read();
				int magicNumber2 = in.read();
				return magicNumber1 == ZIP_MAGIC_NUMBER_1 && magicNumber2 == ZIP_MAGIC_NUMBER_2;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static void unZip(File source, List<String> ignoreList, File dist, boolean asNew, Charset charset) {
		try {
			if (ignoreList == null) {
				ignoreList = new ArrayList<>(DEFAULT_IGNORE_LIST);
			} else {
				ignoreList.addAll(DEFAULT_IGNORE_LIST);
			}
			ZipFile zipFile = new ZipFile(source);
			if (charset == null) {
				charset = DefaultCharset.charset;
			}
			zipFile.setCharset(charset);
			List<FileHeader> fileHeaderList = zipFile.getFileHeaders();
			for (FileHeader fileHeader : fileHeaderList) {
				if (isFromExtraData(fileHeader) && DefaultCharset.charset.name() == charset.name()) {
					unZip(source, ignoreList, dist, asNew, DefaultCharset.charset_gbk);
					return;
				}
				String name = fileHeader.getFileName();
				if (name.length() < 2) {
					continue;
				}
				if (isMember(name, ignoreList)) {
					continue;
				}
				if (fileHeader.isDirectory()) {
					File dir = new File(dist, name);
					if (dir.exists() && !dist.getAbsolutePath().startsWith(dir.getAbsolutePath())
							&& name.indexOf("/") == name.lastIndexOf("/") && asNew) {
						FileUtils.cleanDirectory(dir);
					}
					FileUtils.forceMkdir(dir);
				} else {
					zipFile.extractFile(fileHeader, dist.getAbsolutePath());
				}
			}
			fileHeaderList.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void unZip(File source, List<String> ignoreList, File dist, List<String> dist2List, File dist2,
			boolean asNew, Charset charset) {
		try {
			if (ignoreList == null) {
				ignoreList = new ArrayList<>(DEFAULT_IGNORE_LIST);
			} else {
				ignoreList.addAll(DEFAULT_IGNORE_LIST);
			}
			ZipFile zipFile = new ZipFile(source);
			if (charset == null) {
				charset = DefaultCharset.charset;
			}
			zipFile.setCharset(charset);
			List<FileHeader> fileHeaderList = zipFile.getFileHeaders();
			for (FileHeader fileHeader : fileHeaderList) {
				if (isFromExtraData(fileHeader) && DefaultCharset.charset.name() == charset.name()) {
					unZip(source, ignoreList, dist, dist2List, dist2, asNew, DefaultCharset.charset_gbk);
					return;
				}
				String name = fileHeader.getFileName();
				if (name.length() < 2) {
					continue;
				}
				if (isMember(name, ignoreList)) {
					continue;
				}
				if (fileHeader.isDirectory()) {
					if (name.indexOf("/") == name.lastIndexOf("/") && asNew) {
						File dir = new File(dist, name);
						if (isMember(name, dist2List)) {
							dir = new File(dist2, name);
						}
						if (dir.exists()) {
							FileUtils.cleanDirectory(dir);
						}
						FileUtils.forceMkdir(dir);
					}
				} else {
					String path = dist.getAbsolutePath();
					if (isMember(name, dist2List)) {
						path = dist2.getAbsolutePath();
					}
					zipFile.extractFile(fileHeader, path);
				}
			}
			fileHeaderList.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isMember(final String name, List<String> list) {
		if (ListTools.isEmpty(list)) {
			return true;
		}
		if (StringUtils.isBlank(name)) {
			return false;
		}
		return list.stream().filter(s -> (name.startsWith(s) || name.startsWith("/" + s))).findFirst().isPresent();
	}

	public static boolean isFromExtraData(FileHeader fileHeader) {
		if (fileHeader.getExtraDataRecords() != null) {
			for (ExtraDataRecord extraDataRecord : fileHeader.getExtraDataRecords()) {
				long identifier = extraDataRecord.getHeader();
				if (identifier == 0x7075) {
					byte[] bytes = extraDataRecord.getData();
					ByteBuffer buffer = ByteBuffer.wrap(bytes);
					byte version = buffer.get();
					assert (version == 1);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * ZIP 압축 방법1
	 * 
	 * @param sourceFile 압축할 폴더
	 * @param out        압축 파일 출력 스트림
	 * @param fileList   압축할 파일 목록, null 가능
	 * @return boolean
	 */
	public static boolean toZip(File sourceFile, OutputStream out, List<String> fileList) {
		try (ZipOutputStream zos = new ZipOutputStream(out)) {
			compress(sourceFile, zos, "", fileList);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * ZIP 압축 방법2
	 * 
	 * @param srcFiles 압축할 파일 목록
	 * @param out      압축 파일 출력 스트림
	 * @throws RuntimeException 압축 실패 시 런타임 예외 발생
	 */
	public static void toZip(List<File> srcFiles, OutputStream out) throws RuntimeException {
		ZipOutputStream zos = null;
		try {
			zos = new ZipOutputStream(out);
			for (File srcFile : srcFiles) {
				byte[] buf = new byte[BUFFER_SIZE];
				zos.putNextEntry(new ZipEntry(srcFile.getName()));
				int len;
				FileInputStream in = new FileInputStream(srcFile);
				while ((len = in.read(buf)) != -1) {
					zos.write(buf, 0, len);
				}
				zos.closeEntry();
				in.close();
			}
		} catch (Exception e) {
			throw new RuntimeException("zip error from ZipUtils", e);
		} finally {
			if (zos != null) {
				try {
					zos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 재귀 압축 메서드
	 * 
	 * @param sourceFile 원본 파일
	 * @param zos        zip 출력 스트림
	 * @param name       압축 후 이름
	 * @throws Exception
	 */
	private static void compress(File sourceFile, ZipOutputStream zos, String name, List<String> fileList)
			throws Exception {
		byte[] buf = new byte[BUFFER_SIZE];
		if (sourceFile.isFile()) {
			// zip 출력 스트림에 zip 엔트리 추가, 생성자의 name은 zip 엔트리 파일 이름
			if (StringUtils.isBlank(name)) {
				name = sourceFile.getName();
			}
			if (sourceFile != null) {
				if (name.startsWith(File.separator)) {
					fileList.add(name.substring(1));
				} else {
					fileList.add(name);
				}
			}
			zos.putNextEntry(new ZipEntry(name));
			// 파일을 zip 출력 스트림으로 copy
			int len;
			FileInputStream in = new FileInputStream(sourceFile);
			while ((len = in.read(buf)) != -1) {
				zos.write(buf, 0, len);
			}
			zos.closeEntry();
			in.close();
		} else {
			File[] listFiles = sourceFile.listFiles();
			if (listFiles == null || listFiles.length == 0) {
				if (StringUtils.isNotBlank(name)) {
					zos.putNextEntry(new ZipEntry(name + File.separator));
					zos.closeEntry();
				}
			} else {
				for (File file : listFiles) {
					compress(file, zos, name + File.separator + file.getName(), fileList);
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println("===================");
		File source = new File("/Users/chengjian/dev/tmp/test/测试文件夹2");
		File dist = new File("/Users/chengjian/dev/tmp/test/测试文件夹1");
		dist = new File(dist, source.getName());
		FileUtils.copyDirectory(source, dist);
		System.out.println("===================");
	}
}
