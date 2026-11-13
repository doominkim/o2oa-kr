package com.x.query.core.entity;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.CollectionUtils;

/**
 * 업무 필드 접근 권한 확장 설정
 *
 * @author sword
 */
public class ItemAccessProperties extends JsonProperties {

    private static final long serialVersionUID = -1259157593040432239L;

    @FieldDescribe("조회 가능 대상 DN 목록: 사용자, 조직, 그룹, 역할.")
    private List<String> readerList;

    @FieldDescribe("조회 불가 대상 DN 목록: 사용자, 조직, 그룹, 역할.")
    private List<String> excludeReaderList;

    @FieldDescribe("조회 가능 프로세스 활동 목록.")
    private List<ItemAccessActivity> readActivityList;

    @FieldDescribe("조회 불가 프로세스 활동 목록.")
    private List<ItemAccessActivity> excludeReadActivityList;

    @FieldDescribe("편집 가능 대상 DN 목록: 사용자, 조직, 그룹, 역할.")
    private List<String> editorList;

    @FieldDescribe("편집 불가 대상 DN 목록: 사용자, 조직, 그룹, 역할.")
    private List<String> excludeEditorList;

    @FieldDescribe("편집 가능 프로세스 활동 목록.")
    private List<ItemAccessActivity> editActivityList;

    @FieldDescribe("편집 불가 프로세스 활동 목록.")
    private List<ItemAccessActivity> excludeEditActivityList;

    @FieldDescribe("확장 정보.")
    private String extension;

    public List<String> getReaderList() {
        return readerList == null ? Collections.emptyList() : readerList;
    }

	public List<String> getReaderAndEditorList() {
		if(!getReaderList().isEmpty()){
			return Stream.concat(getReaderList().stream(), getEditorList().stream())
					.distinct().collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

    public void setReaderList(List<String> readerList) {
        this.readerList = readerList;
    }

    public List<ItemAccessActivity> getReadActivityList() {
        return readActivityList == null ? Collections.emptyList() : readActivityList;
    }

    public void setReadActivityList(
            List<ItemAccessActivity> readActivityList) {
        this.readActivityList = readActivityList;
    }

    public List<String> getReadActivityIdList() {
        if (!getReadActivityList().isEmpty()) {
            return Stream.concat(getReadActivityList().stream(), getEditActivityList().stream())
                    .map(ItemAccessActivity::getUnique).distinct().collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<String> getEditorList() {
        return editorList == null ? Collections.emptyList() : editorList;
    }

    public void setEditorList(List<String> editorList) {
        this.editorList = editorList;
    }

    public List<ItemAccessActivity> getEditActivityList() {
        return editActivityList == null ? Collections.emptyList() : editActivityList;
    }

    public List<String> getEditActivityIdList() {
        return getEditActivityList().stream().map(ItemAccessActivity::getUnique).distinct().collect(
                Collectors.toList());
    }

    public void setEditActivityList(
            List<ItemAccessActivity> editActivityList) {
        this.editActivityList = editActivityList;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public List<String> getExcludeReaderList() {
        return excludeReaderList == null ? Collections.emptyList() : excludeReaderList;
    }

    public List<String> getExcludeReaderListExcludeEditor(List<String> authList) {
        if(CollectionUtils.isNotEmpty(excludeReaderList)){
            if(CollectionUtils.containsAny(getExcludeEditorList(), authList)){
                return getExcludeReaderList();
            }
            if(CollectionUtils.containsAny(getEditorList(), authList)){
                return Collections.emptyList();
            }
            return getExcludeReaderList();
        }
        return Collections.emptyList();
    }

    public void setExcludeReaderList(List<String> excludeReaderList) {
        this.excludeReaderList = excludeReaderList;
    }

    public List<ItemAccessActivity> getExcludeReadActivityList() {
        return excludeReadActivityList == null ? Collections.emptyList() : excludeReadActivityList;
    }

    public List<String> getExcludeReadActivityIdList() {
        if(!getExcludeReadActivityList().isEmpty()){
            List<String> excludeEditActivityIdList = getExcludeEditActivityList().stream()
                    .map(ItemAccessActivity::getUnique).collect(Collectors.toList());
            List<String> editActivityIdList = getEditActivityList().stream()
                    .map(ItemAccessActivity::getUnique).filter(o -> !excludeEditActivityIdList.contains(o))
                    .collect(Collectors.toList());
            return getExcludeReadActivityList().stream()
                    .map(ItemAccessActivity::getUnique).filter(o -> !editActivityIdList.contains(o))
                    .distinct().collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public void setExcludeReadActivityList(
            List<ItemAccessActivity> excludeReadActivityList) {
        this.excludeReadActivityList = excludeReadActivityList;
    }

    public List<String> getExcludeEditorList() {
        return excludeEditorList == null ? Collections.emptyList() : excludeEditorList;
    }

    public void setExcludeEditorList(List<String> excludeEditorList) {
        this.excludeEditorList = excludeEditorList;
    }

    public List<ItemAccessActivity> getExcludeEditActivityList() {
        return excludeEditActivityList == null ? Collections.emptyList() : excludeEditActivityList;
    }

    public List<String> getExcludeEditActivityIdList() {
        return getExcludeEditActivityList().stream().map(ItemAccessActivity::getUnique).distinct().collect(
                Collectors.toList());
    }

    public void setExcludeEditActivityList(
            List<ItemAccessActivity> excludeEditActivityList) {
        this.excludeEditActivityList = excludeEditActivityList;
    }

    public List<String> getReadConfigList() {
        List<String> readConfigList = Stream.concat(getReaderList().stream(),
                        getReadActivityIdList().stream())
                .distinct().collect(Collectors.toCollection(ArrayList::new));
        if(!readConfigList.isEmpty()){
            readConfigList.addAll(getEditorList());
            readConfigList.addAll(getEditActivityIdList());
            return readConfigList;
        }
        return Collections.emptyList();
    }

    public List<String> getExcludeListForRead(List<String> authList) {
        List<String> excludeList = Stream.concat(getExcludeReaderList().stream(), getExcludeReadActivityIdList().stream())
                .distinct().collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(excludeList)){
            if(CollectionUtils.containsAny(getExcludeEditorList(), authList)){
                return excludeList;
            }
            if(CollectionUtils.containsAny(getEditorList(), authList)){
                return Collections.emptyList();
            }
            return excludeList;
        }
        return Collections.emptyList();
    }
}
