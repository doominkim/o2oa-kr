package com.x.base.core.project.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionAttachmentInvalid;
import com.x.base.core.project.exception.ExceptionAttachmentInvalidCallback;
import com.x.base.core.project.exception.ExceptionFileNameInvalid;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.gson.GsonPropertyObject;

/**
 * @author sword
 */
public class FileTools {

	public static String parent(String path) {
		int idx = StringUtils.lastIndexOfAny(path, new String[] { "\\", "/" });
		if (idx > 0) {
			return StringUtils.substring(path, 0, idx);
		} else {
			return path;
		}
	}

	/**
	 * 디렉터리 생성-상위 재귀
	 *
	 * @param dist
	 * @throws Exception
	 */
	public static void forceMkdir(File dist) throws Exception {
		if (!dist.exists()) {
			File parent = dist.getParentFile();
			forceMkdir(parent);
			FileUtils.forceMkdir(dist);
		}
	}

	/**
	 * 폴더 아래 모든 파일 조회 + 퍼지 조회(퍼지 조회가 필요 없으면 queryStr에 빈 값 또는 null 전달)
	 *
	 * @param folderPath 경로
	 * @param queryStr   유사 검색 문자열
	 * @return
	 */
	public static Map<String, List<FileInfo>> getFiles(String folderPath, String queryStr, String splitPath) {
		Map<String, List<FileInfo>> map = new HashMap<>();
		List<FileInfo> files = new ArrayList<>();
		List<FileInfo> folders = new ArrayList<>();
		File f = new File(folderPath);
		if (f.exists()) {
			if (!f.isDirectory()) { // 경로가 파일임
				String path = f.getAbsolutePath().replaceAll("\\\\", "/");
				if (StringUtils.isNotEmpty(splitPath) && f.getAbsolutePath().indexOf(splitPath) > -1) {
					path = StringUtils.substringAfter(path, splitPath);
				}
				if (StringUtils.isEmpty(queryStr) || f.getName().indexOf(queryStr) != -1) {
					files.add(new FileInfo(path, f.getName(), "file", f.lastModified()));
				}
			} else { // 경로가 폴더임
				File fa[] = f.listFiles();
				for (int i = 0; i < fa.length; i++) {
					File fs = fa[i];
					String path = fs.getAbsolutePath().replaceAll("\\\\", "/");
					if (StringUtils.isNotEmpty(splitPath) && path.indexOf(splitPath) > -1) {
						path = StringUtils.substringAfter(path, splitPath);
					}
					if (StringUtils.isEmpty(queryStr) || fs.getName().indexOf(queryStr) != -1) {
						if (fs.isDirectory()) {
							folders.add(new FileInfo(path, fs.getName(), "folder", f.lastModified()));
						} else {
							files.add(new FileInfo(path, fs.getName(), "file", f.lastModified()));
						}
					}
				}
			}
		}
		map.put("files", files);
		map.put("folders", folders);
		return map;
	}

	public static File parent(File file) {
		return new File(parent(file.getAbsolutePath()));
	}

	public static class FileInfo extends GsonPropertyObject {

		private String filePath;

		private String fileName;

		private String fileType;

		private Date lastModifyTime;

		public FileInfo() {
		}

		public FileInfo(String filePath, String fileName, String fileType, long time) {
			this.filePath = filePath;
			this.fileName = fileName;
			this.fileType = fileType;
			this.lastModifyTime = new Date(time);
		}

		public String getFilePath() {
			return filePath;
		}

		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getFileType() {
			return fileType;
		}

		public void setFileType(String fileType) {
			this.fileType = fileType;
		}

		public Date getLastModifyTime() {
			return lastModifyTime;
		}

		public void setLastModifyTime(Date lastModifyTime) {
			this.lastModifyTime = lastModifyTime;
		}
	}

	public static String toFileName(String name) {
		/*
		 * windows 파일명에는 \ / : * ? " < > | 영문 문자를 포함할 수 없어, 여기서는 "."、"'"로 대체. \/:?| 는 .로 대체 "<>
		 * '로 대체
		 */
		name = name.replaceAll("[/\\\\:*?|]", "_");
		name = name.replaceAll("[\"<>]", "'");
		return name;
	}

	/**
	 * 첨부파일이 크기·파일 형식 제약에 맞는지 판단
	 *
	 * @param size
	 * @param fileName
	 * @param callback
	 * @throws Exception
	 */
	public static void verifyConstraint(long size, String fileName, String callback) throws Exception {
		if(StringUtils.isBlank(fileName)){
			throw new ExceptionFileNameInvalid(fileName);
		}
		fileName = fileName.replace("\r", " ").replace("\n", " ");
		if(!StringTools.isFileName(fileName)){
			throw new ExceptionFileNameInvalid(fileName);
		}
		String fileType = FilenameUtils.getExtension(fileName).toLowerCase();
		if(StringUtils.isBlank(fileType)){
			throw new ExceptionFileNameInvalid(fileName);
		}
		if (Config.general().getAttachmentConfig().getFileSize() != null && Config.general().getAttachmentConfig().getFileSize() > 0) {
			size = size / (1024 * 1024);
			if (size > Config.general().getAttachmentConfig().getFileSize()) {
				if (StringUtils.isNotEmpty(callback)) {
					throw new ExceptionAttachmentInvalidCallback(callback, fileName, Config.general().getAttachmentConfig().getFileSize());
				} else {
					throw new ExceptionAttachmentInvalid(fileName, Config.general().getAttachmentConfig().getFileSize());
				}
			}
		}
		if ((Config.general().getAttachmentConfig().getFileTypeIncludes() != null && !Config.general().getAttachmentConfig().getFileTypeIncludes().isEmpty())
				&& (!ListTools.contains(Config.general().getAttachmentConfig().getFileTypeIncludes(), fileType))) {
			if (StringUtils.isNotEmpty(callback)) {
				throw new ExceptionAttachmentInvalidCallback(callback, fileName);
			} else {
				throw new ExceptionAttachmentInvalid(fileName);
			}
		}
		if ((Config.general().getAttachmentConfig().getFileTypeExcludes() != null && !Config.general().getAttachmentConfig().getFileTypeExcludes().isEmpty())
				&& (ListTools.contains(Config.general().getAttachmentConfig().getFileTypeExcludes(), fileType))) {
			if (StringUtils.isNotEmpty(callback)) {
				throw new ExceptionAttachmentInvalidCallback(callback, fileName);
			} else {
				throw new ExceptionAttachmentInvalid(fileName);
			}
		}
	}

}
