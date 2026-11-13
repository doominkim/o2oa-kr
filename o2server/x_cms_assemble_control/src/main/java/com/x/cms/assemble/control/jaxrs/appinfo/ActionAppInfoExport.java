package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;
import com.x.cms.core.entity.element.Form;
import com.x.cms.core.entity.element.Script;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewCategory;
import com.x.cms.core.entity.element.ViewFieldConfig;

public class ActionAppInfoExport extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionAppInfoExport.class);

	/**
	 * 게시판 설계 내보내기 기능 구현 1. 게시판 정보 2. 모든 분류 정보 3. 모든 양식 설계 4. 뷰(목록 및 데이터 뷰) 설계 5. 데이터 사전 설계 6. 스크립트 정보
	 *
	 * @param request
	 * @param effectivePerson
	 * @param appInfoId
	 * @return
	 * @throws Exception
	 */
	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String appInfoId)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		AppInfo appInfo = null;
		Boolean check = true;

		if (StringUtils.isEmpty(appInfoId)) {
			check = false;
			Exception exception = new ExceptionAppInfoIdEmpty();
			result.error(exception);
		}
		// 게시판 정보 조회 시도, 게시판 정보 존재 여부 확인
		if (check) {
			try {
				appInfo = appInfoServiceAdv.get(appInfoId);
				if (appInfo == null) {
					check = false;
					Exception exception = new ExceptionAppInfoNotExists(appInfoId);
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAppInfoProcess(e, "지정한 ID의 게시판 정보 객체를 조회하는 중 오류가 발생했습니다. ID:" + appInfoId);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			try {
				wo = Wo.copier.copy(appInfo);
			} catch (Exception e) {
				Exception exception = new ExceptionAppInfoProcess(e, "조회한 게시판 정보 객체를 출력 데이터로 변환하는 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		// 모든 분류 목록 조회 시작
		if (check) {
			List<CategoryInfo> categoryList = null;
			try {
				categoryList = categoryInfoServiceAdv.listByAppId(appInfoId);
				if (ListTools.isNotEmpty(categoryList)) {
					wo.setCategories(WoCategory.copier.copy(categoryList));
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAppInfoProcess(e, "게시판 ID로 전체 분류 ID 목록을 조회하는 중 오류가 발생했습니다. ID:" + appInfoId);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		// 모든 양식 목록 조회 시작
		if (check) {
			List<Form> formList = null;
			try {
				formList = formServiceAdv.listByAppId(appInfoId);
				if (ListTools.isNotEmpty(formList)) {
					wo.setForms(WoForm.copier.copy(formList));
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAppInfoProcess(e, "게시판 ID로 전체 양식 ID 목록을 조회하는 중 오류가 발생했습니다. ID:" + appInfoId);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		// 모든 스크립트 목록 조회 시작
		if (check) {
			List<Script> scriptList = null;
			try {
				scriptList = scriptServiceAdv.listWithAppId(appInfoId);
				if (ListTools.isNotEmpty(scriptList)) {
					wo.setScripts(WoScript.copier.copy(scriptList));
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAppInfoProcess(e, "게시판 ID로 전체 스크립트 목록을 조회하는 중 오류가 발생했습니다. ID:" + appInfoId);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		result.setData(wo);
		return result;
	}

	/**
	 * 1. 게시판 정보 2. 모든 분류 정보 3. 모든 양식 설계 4. 뷰(목록 및 데이터 뷰) 설계 5. 데이터 사전 설계 6. 스크립트 정보
	 */
	public static class Wo extends AppInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> excludes = new ArrayList<>();

		public static final WrapCopier<AppInfo, Wo> copier = WrapCopierFactory.wo(AppInfo.class, Wo.class, null,
				Wo.excludes);

		@FieldDescribe("게시판 내 모든 분류 정보 목록")
		private List<WoCategory> categories = null;

		@FieldDescribe("게시판 내 모든 양식 정보 목록")
		private List<WoForm> forms = null;

		@FieldDescribe("게시판 내 모든 목록 뷰 정보 목록")
		private List<WoView> views = null;

		@FieldDescribe("게시판 내 모든 스크립트 정보 목록")
		private List<WoScript> scripts = null;

		@FieldDescribe("게시판 내 모든 데이터 사전 정보 목록")
		private List<WoAppDict> dicts = null;

		public List<WoCategory> getCategories() {
			return categories;
		}

		public void setCategories(List<WoCategory> categories) {
			this.categories = categories;
		}

		public List<WoForm> getForms() {
			return forms;
		}

		public void setForms(List<WoForm> forms) {
			this.forms = forms;
		}

		public List<WoView> getViews() {
			return views;
		}

		public void setViews(List<WoView> views) {
			this.views = views;
		}

		public List<WoScript> getScripts() {
			return scripts;
		}

		public void setScripts(List<WoScript> scripts) {
			this.scripts = scripts;
		}

		public List<WoAppDict> getDicts() {
			return dicts;
		}

		public void setDicts(List<WoAppDict> dicts) {
			this.dicts = dicts;
		}
	}

	/**
	 * 출력용 분류 정보 객체
	 */
	public static class WoCategory extends CategoryInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> excludes = new ArrayList<>();

		public static final WrapCopier<CategoryInfo, WoCategory> copier = WrapCopierFactory.wo(CategoryInfo.class,
				WoCategory.class, null, WoCategory.excludes);

	}

	/**
	 * 출력용 양식 정보 객체
	 */
	public static class WoForm extends Form {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> excludes = new ArrayList<>();

		public static final WrapCopier<Form, WoForm> copier = WrapCopierFactory.wo(Form.class, WoForm.class, null,
				WoForm.excludes);

	}

	/**
	 * 출력용 목록 뷰 정보 객체
	 */
	public static class WoView extends View {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> excludes = new ArrayList<>();

		public static final WrapCopier<View, WoView> copier = WrapCopierFactory.wo(View.class, WoView.class, null,
				WoView.excludes);

		@FieldDescribe("목록 뷰의 모든 목록 열 정보")
		private List<ViewFieldConfig> fields = null;

		@FieldDescribe("목록 뷰의 모든 분류 연결 관계")
		private List<ViewCategory> viewCatagories = null;

		public List<ViewFieldConfig> getFields() {
			return fields;
		}

		public void setFields(List<ViewFieldConfig> fields) {
			this.fields = fields;
		}

		public List<ViewCategory> getViewCatagories() {
			return viewCatagories;
		}

		public void setViewCatagories(List<ViewCategory> viewCatagories) {
			this.viewCatagories = viewCatagories;
		}
	}

	/**
	 * 출력용 데이터 사전 정보 객체
	 */
	public static class WoScript extends Script {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> excludes = new ArrayList<>();

		public static final WrapCopier<Script, WoScript> copier = WrapCopierFactory.wo(Script.class, WoScript.class,
				null, WoScript.excludes);

	}

	/**
	 * 출력용 데이터 사전 정보 객체
	 */
	public static class WoAppDict extends AppDict {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> excludes = new ArrayList<>();

		public static final WrapCopier<Script, WoScript> copier = WrapCopierFactory.wo(Script.class, WoScript.class,
				null, WoScript.excludes);

		@FieldDescribe("데이터 사전의 모든 객체 목록")
		private List<WoAppDictItem> items = null;
	}

	/**
	 * 출력용 데이터 사전 정보 객체
	 */
	public static class WoAppDictItem extends AppDictItem {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> excludes = new ArrayList<>();

		public static final WrapCopier<AppDictItem, WoAppDictItem> copier = WrapCopierFactory.wo(AppDictItem.class,
				WoAppDictItem.class, null, WoAppDictItem.excludes);

	}
}
