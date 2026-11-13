package com.x.mind.assemble.control.jaxrs.mind;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionFolderWrapInConvert;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindQuery;
import com.x.mind.entity.MindBaseInfo;

/**
 * 내가 공유한 모든 마인드맵 파일 정보 조회
 * @author O2LEE
 *
 */
public class ActionMyShareMindNextWithFilter extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionMyShareMindNextWithFilter.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String startId, Integer count, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		Wi wi = null;
		List<MindBaseInfo> mindEnityies = null;
		String folderId = null;
		Boolean check = true;
		
		if( check ){
			try {
				wi = this.convertToWrapIn( jsonElement, Wi.class );
				if( wi != null ) {
					folderId = wi.getFolderId();
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionFolderWrapInConvert(e, jsonElement == null?"None":XGsonBuilder.instance().toJson(jsonElement));
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				mindEnityies = mindInfoService.listNextPageWithFilter(startId, count, wi.getKey(), folderId, true, effectivePerson.getDistinguishedName(), 
						null, wi.getSharePersons(), wi.getShareUnits(), wi.getShareGroups(), wi.getOrderField(), wi.getOrderType(), null );
				if( ListTools.isNotEmpty( mindEnityies )) {
					wos = Wo.copier.copy(mindEnityies);
					SortTools.desc(wos, "updateTime");
					result.setData( wos );
				}
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionMindQuery( e, "ID 목록으로 마인드맵 정보를 조회하는 중 오류가 발생했습니다!" );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wi{
		
		@FieldDescribe( "지정 디렉터리 ID" )
		private String folderId = null;
		
		@FieldDescribe( "키워드" )
		private String key = null;
		
		@FieldDescribe( "공유자 목록" )
		private List<String> sharePersons = null;
		
		@FieldDescribe( "공유 조직 목록" )
		private List<String> shareUnits = null;

		@FieldDescribe( "공유 역할 목록" )
		private List<String> shareGroups = null;

		@FieldDescribe( "정렬 열: 기본값 sequence" )
		private String orderField =  JpaObject.sequence_FIELDNAME ;
		
		@FieldDescribe( "정렬 방식: DESC|ASC, 기본값 DESC" )
		private String orderType = "DESC";
		
		public String getFolderId() {
			return folderId;
		}

		public void setFolderId(String folderId) {
			this.folderId = folderId;
		}

		public String getOrderField() {
			return orderField;
		}

		public String getOrderType() {
			return orderType;
		}

		public void setOrderField(String orderField) {
			this.orderField = orderField;
		}

		public void setOrderType(String orderType) {
			this.orderType = orderType;
		}

		public List<String> getSharePersons() {
			return sharePersons;
		}

		public List<String> getShareUnits() {
			return shareUnits;
		}

		public List<String> getShareGroups() {
			return shareGroups;
		}

		public void setSharePersons(List<String> sharePersons) {
			this.sharePersons = sharePersons;
		}

		public void setShareUnits(List<String> shareUnits) {
			this.shareUnits = shareUnits;
		}

		public void setShareGroups(List<String> shareGroups) {
			this.shareGroups = shareGroups;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}
		
	}
	
	public static class Wo extends MindBaseInfo  {	
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier<MindBaseInfo, Wo> copier = WrapCopierFactory.wo( MindBaseInfo.class, Wo.class, null,Wo.Excludes);
	}
}