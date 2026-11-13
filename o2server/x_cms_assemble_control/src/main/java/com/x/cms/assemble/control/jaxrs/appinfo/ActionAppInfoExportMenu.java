package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.Form;
import com.x.cms.core.entity.element.Script;

public class ActionAppInfoExportMenu extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionAppInfoExportMenu.class );

    /**
     * 게시판 설계 가져오기 내용 목록 조회 구현
     * 1. 모든 분류 정보 목록
     * 2. 모든 양식 정보 목록
     * 3. 모든 뷰(목록 및 데이터 뷰) 정보 목록
     * 4. 모든 데이터 사전 목록
     * 5. 모든 스크립트 정보 목록
     * 6. 모든 분류-뷰 연결 정보
     *
     * @param request
     * @param effectivePerson
     * @param appInfoId
     * @return
     * @throws Exception
     */
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String appInfoId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		AppInfo appInfo = null;
		Boolean check = true;
		
		if( StringUtils.isEmpty(appInfoId) ){
			check = false;
			Exception exception = new ExceptionAppInfoIdEmpty();
			result.error( exception );
		}

        //게시판 정보 조회 시도, 게시판 정보 존재 여부 확인
        if( check ){
            try {
                appInfo = appInfoServiceAdv.get( appInfoId );
                if( appInfo == null ){
                    check = false;
                    Exception exception = new ExceptionAppInfoNotExists( appInfoId );
                    result.error( exception );
                }
            } catch (Exception e) {
                check = false;
                Exception exception = new ExceptionAppInfoProcess( e, "지정한 ID의 게시판 정보 객체를 조회하는 중 오류가 발생했습니다. ID:" + appInfoId );
                result.error( exception );
                logger.error( e, effectivePerson, request, null);
            }
        }

        //모든 분류 목록 조회 시작
        if( check ){
            List<CategoryInfo> categoryList = null;
            try{
                categoryList = categoryInfoServiceAdv.listByAppId( appInfoId );
                if(ListTools.isNotEmpty(categoryList) ){
                    List<WoEntry> categories = new ArrayList<>();
                    for( CategoryInfo category : categoryList ){
                        categories.add( new WoEntry( category.getId(), category.getCategoryName()) );
                    }
                    wo.setCategories( categories );
                }
            }catch(Exception e){
                check = false;
                Exception exception = new ExceptionAppInfoProcess( e, "게시판 ID로 전체 분류 ID 목록을 조회하는 중 오류가 발생했습니다. ID:" + appInfoId );
                result.error( exception );
                logger.error( e, effectivePerson, request, null);
            }
        }

        //모든 양식 목록 조회 시작
        if( check ){
            List<Form> formList = null;
            try{
                formList = formServiceAdv.listByAppId( appInfoId );
                if(ListTools.isNotEmpty(formList) ){
                    List<WoEntry> forms = new ArrayList<>();
                    for( Form form : formList ){
                        forms.add( new WoEntry( form.getId(), form.getName() ) );
                    }
                    wo.setForms( forms );
                }
            }catch(Exception e){
                check = false;
                Exception exception = new ExceptionAppInfoProcess( e, "게시판 ID로 전체 양식 ID 목록을 조회하는 중 오류가 발생했습니다. ID:" + appInfoId );
                result.error( exception );
                logger.error( e, effectivePerson, request, null);
            }
        }

        //모든 스크립트 목록 조회 시작
        if( check ){
            List<Script> scriptList = null;
            try{
                scriptList = scriptServiceAdv.listWithAppId(appInfoId);
                if(ListTools.isNotEmpty(scriptList) ){
                    List<WoEntry> scripts = new ArrayList<>();
                    for( Script script : scriptList ){
                        scripts.add( new WoEntry( script.getId(), script.getName() ) );
                    }
                    wo.setScripts( scripts );
                }
            }catch(Exception e){
                check = false;
                Exception exception = new ExceptionAppInfoProcess( e, "게시판 ID로 전체 스크립트 목록을 조회하는 중 오류가 발생했습니다. ID:" + appInfoId );
                result.error( exception );
                logger.error( e, effectivePerson, request, null);
            }
        }

        //모든 데이터 사전 목록 조회 시작
        if( check ){
            List<AppDict> dictList = null;
            try{
                dictList = appDictServiceAdv.listWithAppId( appInfoId );
                if(ListTools.isNotEmpty(dictList) ){
                    List<WoEntry> dictEntry = new ArrayList<>();
                    for( AppDict dict : dictList ){
                        dictEntry.add( new WoEntry( dict.getId(), dict.getName() ) );
                    }
                    wo.setScripts( dictEntry );
                }
            }catch(Exception e){
                check = false;
                Exception exception = new ExceptionAppInfoProcess( e, "게시판 ID로 전체 데이터 사전 목록을 조회하는 중 오류가 발생했습니다. ID:" + appInfoId );
                result.error( exception );
                logger.error( e, effectivePerson, request, null);
            }
        }

        result.setData( wo );
		return result;
	}

    /**
     * 1. 모든 분류 정보 목록
     * 2. 모든 양식 정보 목록
     * 3. 모든 뷰(목록 및 데이터 뷰) 정보 목록
     * 4. 모든 데이터 사전 목록
     * 5. 모든 스크립트 정보 목록
     * 6. 모든 분류-뷰 연결 정보
     */
    public static class Wo  {

        @FieldDescribe("게시판 내 모든 양식 정보 목록")
        private List<WoEntry> forms = null;

        @FieldDescribe("게시판 내 모든 분류 정보 목록")
        private List<WoEntry> categories = null;

        @FieldDescribe("게시판 내 모든 스크립트 정보 목록")
        private List<WoEntry> scripts = null;

        @FieldDescribe("게시판 내 모든 데이터 사전 정보 목록")
        private List<WoEntry> dicts = null;

        @FieldDescribe("게시판 내 모든 목록 뷰 정보 목록")
        private List<WoEntry> views = null;

        @FieldDescribe("게시판 내 모든 데이터 뷰 정보 목록")
        private List<WoEntry> queryViews = null;

        public List<WoEntry> getForms() {
            return forms;
        }

        public void setForms(List<WoEntry> forms) {
            this.forms = forms;
        }

        public List<WoEntry> getCategories() {
            return categories;
        }

        public void setCategories(List<WoEntry> categories) {
            this.categories = categories;
        }

        public List<WoEntry> getScripts() {
            return scripts;
        }

        public void setScripts(List<WoEntry> scripts) {
            this.scripts = scripts;
        }

        public List<WoEntry> getDicts() {
            return dicts;
        }

        public void setDicts(List<WoEntry> dicts) {
            this.dicts = dicts;
        }

        public List<WoEntry> getViews() {
            return views;
        }

        public void setViews(List<WoEntry> views) {
            this.views = views;
        }

        public List<WoEntry> getQueryViews() {
            return queryViews;
        }

        public void setQueryViews(List<WoEntry> queryViews) {
            this.queryViews = queryViews;
        }
    }

    public static class WoEntry  {

        @FieldDescribe("정보 이름")
        private String e_name = null;

        @FieldDescribe("정보 ID")
        private String e_id = null;

        public WoEntry( String e_id, String e_name){
            this.e_id = e_id;
            this.e_name = e_name;
        }

        public String getE_name() {
            return e_name;
        }

        public void setE_name(String e_name) {
            this.e_name = e_name;
        }

        public String getE_id() {
            return e_id;
        }

        public void setE_id(String e_id) {
            this.e_id = e_id;
        }
    }
}