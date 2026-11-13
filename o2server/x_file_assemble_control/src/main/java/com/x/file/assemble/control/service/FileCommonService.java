package com.x.file.assemble.control.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.config.StorageMapping;
import com.x.file.assemble.control.Business;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.open.FileStatus;
import com.x.file.core.entity.open.OriginFile;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Folder2;

public class FileCommonService {

    /**
     * 첨부파일을 내려받아 zip으로 압축
     * @param emc
     * @param files
     * @param folders
     * @throws Exception
     */
    public void downToZip(EntityManagerContainer emc, List<Attachment2> files, List<Folder2> folders, OutputStream os) throws Exception {
        Map<String, OriginFile> filePathMap = new HashMap<>();
        List<String> emptyFolderList = new ArrayList<>();
        /* zip 압축 파일 내 디렉터리 구조 생성 */
        if(folders!=null && !folders.isEmpty()) {
            Business business = new Business( emc );
            for (Folder2 folder : folders) {
                String parentPath = ""; // 초기 경로가 비어 있음
                generateFolderPath(emc, business, emptyFolderList, filePathMap, parentPath, folder);
            }
        }
        if(files!=null) {
            for (Attachment2 att : files) {
                String parentPath = ""; // 초기 경로가 비어 있음
                generateFilePath(emc, filePathMap, parentPath, att);
            }
        }
        ZipOutputStream zos = null;
        try{
            zos = new ZipOutputStream(os);
            for (Map.Entry<String, OriginFile> entry : filePathMap.entrySet()) {
                zos.putNextEntry(new ZipEntry(entry.getKey()));
                StorageMapping mapping = ThisApplication.context().storageMappings().get(OriginFile.class,
                        entry.getValue().getStorage());
                try (ByteArrayOutputStream os1 = new ByteArrayOutputStream()) {
                    entry.getValue().readContent(mapping, os1);
                    byte[] bs = os1.toByteArray();
                    os1.close();
                    zos.write(bs);
                }
            }
            // zip에 빈 폴더 추가
            for (String emptyFolder : emptyFolderList) {
                zos.putNextEntry(new ZipEntry(emptyFolder));
            }
        }finally {
            if(zos!=null){
                zos.close();
            }
        }
    }

    private void generateFolderPath(EntityManagerContainer emc, Business business, List<String> emptyFolderList,Map<String, OriginFile> filePathMap, String parentPath, Folder2 folder) throws Exception {
        if (parentPath.length() > 0) {
            parentPath = parentPath + File.separator + folder.getName();
        } else {
            parentPath = folder.getName();
        }

        boolean emptyFolder = true;

        List<Folder2> subfolders =  business.folder2().listSubDirect1(folder.getId(), FileStatus.VALID.getName());
        for (Folder2 subfolder : subfolders) {
            emptyFolder = false;
            generateFolderPath(emc ,business, emptyFolderList, filePathMap, parentPath, subfolder);
        }

        List<Attachment2> subfiles = business.attachment2().listWithFolder2(folder.getId(),FileStatus.VALID.getName());
        for (Attachment2 subfile : subfiles) {
            emptyFolder = false;
            generateFilePath(emc, filePathMap, parentPath, subfile);
        }

        if (emptyFolder) {
            parentPath += File.separator;// 경로 끝에 구분자를 붙여야 빈 폴더가 생성됨
            emptyFolderList.add(parentPath);
        }
    }

    private void generateFilePath(EntityManagerContainer emc, Map<String, OriginFile> filePathMap, String parentPath, Attachment2 att) throws Exception {
        String filename = att.getName();
        String filePath;
        if (parentPath.length() > 0) {
            filePath = parentPath + File.separator + filename;
        } else {
            filePath = filename;
        }
        OriginFile originFile = emc.find(att.getOriginFile(),OriginFile.class);
        if(originFile!=null) {
            filePathMap.put(filePath, originFile);
        }
    }
}
