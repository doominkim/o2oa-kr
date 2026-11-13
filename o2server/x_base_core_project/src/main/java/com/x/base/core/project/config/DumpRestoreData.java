package com.x.base.core.project.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.ListTools;

public class DumpRestoreData extends ConfigObject {

    private static final long serialVersionUID = 8910820385137391619L;

    public static DumpRestoreData defaultInstance() {
        return new DumpRestoreData();
    }

    public static final String MODE_FULL = "full";
    public static final String MODE_LITE = "lite";
    public static final String RESTOREOVERRIDE_CLEAN = "clean";
    public static final String RESTOREOVERRIDE_SKIPEXISTED = "skipExisted";

    public static final Boolean DEFAULT_EXCEPTIONINVALIDSTORAGE = false;
    public static final Boolean DEFAULT_ATTACHSTORAGE = true;
    public static final String DEFAULT_ITEMCATEGORY = "";
    public static final Boolean DEFAULT_PARALLEL = false;
    public static final Boolean DEFAULT_REDISTRIBUTESTORAGE = false;

    public DumpRestoreData() {
        this.includes = new ArrayList<>();
        this.excludes = new ArrayList<>();
        this.mode = MODE_LITE;
        this.attachStorage = DEFAULT_ATTACHSTORAGE;
        this.exceptionInvalidStorage = DEFAULT_EXCEPTIONINVALIDSTORAGE;
        this.itemCategory = DEFAULT_ITEMCATEGORY;
        this.parallel = DEFAULT_PARALLEL;
        this.redistributeStorage = DEFAULT_REDISTRIBUTESTORAGE;
        this.restoreOverride = RESTOREOVERRIDE_CLEAN;
    }

    @FieldDescribe("내보내기/가져오기 포함 객체, 와일드카드 * 사용 가능, 예: 결재 대기 데이터만 내보내기: com.x.processplatform.core.entity.content.Task.")
    private List<String> includes;

    @FieldDescribe("내보내기/가져오기 제외 객체, 와일드카드 * 사용 가능, 예: 프로세스 인스턴스 데이터 제외 com.x.processplatform.core.entity.content.*")
    private List<String> excludes;

    @FieldDescribe("데이터 내보내기 모드, lite|full, 기본값 lite")
    private String mode;

    @FieldDescribe("storage를 조회할 수 없을 때 오류 발생 여부.")
    private Boolean exceptionInvalidStorage;

    @FieldDescribe("storage에 저장된 파일도 함께 가져오기/내보내기 여부.")
    private Boolean attachStorage;

    @FieldDescribe("재분배 여부.")
    private Boolean redistributeStorage;

    @FieldDescribe("병렬 내보내기 사용, 기본값 true")
    private Boolean parallel;

    @FieldDescribe("데이터 가져오기 방식, clean: 비우고 다시 가져오기, skipExisted: 같은 id 데이터가 있으면 건너뜀. 기본 방식은 clean.")
    private String restoreOverride;

    @FieldDescribe("com.x.query.core.entity.Item의 itemCategory를 별도 필터링, 가능한 값 pp, cms, bbs, pp_dict.")
    private String itemCategory;

    public Boolean getParallel() {
        return BooleanUtils.isNotFalse(parallel);
    }

    public Boolean getRedistributeStorage() {
        return BooleanUtils.isTrue(redistributeStorage);
    }

    public void setRedistributeStorage(Boolean redistributeStorage) {
        this.redistributeStorage = redistributeStorage;
    }

    public Boolean getAttachStorage() {
        return attachStorage;
    }

    public void setAttachStorage(Boolean attachStorage) {
        this.attachStorage = attachStorage;
    }

    public String getItemCategory() {
        return this.itemCategory;
    }

    public Boolean getExceptionInvalidStorage() {
        return BooleanUtils.isNotFalse(exceptionInvalidStorage);
    }

    public String getMode() {
        return StringUtils.equals(MODE_FULL, mode) ? MODE_FULL : MODE_LITE;
    }

    public List<String> getIncludes() {
        List<String> list = new ArrayList<>();
        for (String str : ListTools.trim(includes, true, true)) {
            list.add(str);
        }
        return list;
    }

    public List<String> getExcludes() {
        List<String> list = new ArrayList<>();
        for (String str : ListTools.trim(excludes, true, true)) {
            list.add(str);
        }
        return list;
    }

    public void setIncludes(List<String> includes) {
        this.includes = includes;
    }

    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }

    public void setExceptionInvalidStorage(Boolean exceptionInvalidStorage) {
        this.exceptionInvalidStorage = exceptionInvalidStorage;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getRestoreOverride() {
        return restoreOverride;
    }

    public void setRestoreOverride(String restoreOverride) {
        this.restoreOverride = restoreOverride;
    }

}
