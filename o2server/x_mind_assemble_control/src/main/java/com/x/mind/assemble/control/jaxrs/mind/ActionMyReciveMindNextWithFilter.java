package com.x.mind.assemble.control.jaxrs.mind;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

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
 * 내가 받은 모든 공유 파일 정보 조회
 * @author O2LEE
 *
 */
public class ActionMyReciveMindNextWithFilter extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionMyReciveMindNextWithFilter.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String startId, Integer count, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		Wi wi = null;
		List<String> mindIds_recived = null;
		List<MindBaseInfo> mindEnityies = null;
		List<String> personNames = new ArrayList<>();
		List<String> unitNames = null;
		List<String> groupNames = null;
		String source = null;
		Boolean check = true;
		String personName = effectivePerson.getDistinguishedName();
		
		if( check ){
			try {
				wi = this.convertToWrapIn( jsonElement, Wi.class );
				if( wi != null ) {
					source = wi.getSource();
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionFolderWrapInConvert(e, jsonElement == null?"None":XGsonBuilder.instance().toJson(jsonElement));
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		//먼저 mindIds_inFolder 필터로 내 모든 조직과 그룹 조회
		if( check ){
			try {
				unitNames = userManagerService.listUnitNamesWithPerson(effectivePerson.getDistinguishedName());
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionMindQuery( e,  "사용자가 속한 모든 조직 이름 목록을 조회하는 중 오류가 발생했습니다.", effectivePerson.getDistinguishedName() );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
				
		if( check ){
			try {
				groupNames = userManagerService.listGroupNamesByPerson(effectivePerson.getDistinguishedName());
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionMindQuery( e,  "사용자가 속한 모든 조직 이름 목록을 조회하는 중 오류가 발생했습니다.", effectivePerson.getDistinguishedName() );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
				
		if( check ){
			//먼저 mindIds_inFolder 필터로 내가 다른 사용자에게 공유한 모든 마인드맵 파일 ID 조회
			if( StringUtils.isNotEmpty( source )) {
				try {
					List<String> targetList = new ArrayList<>();
					targetList.add( personName );
					if(  ListTools.isNotEmpty( unitNames )) {
						targetList.addAll(unitNames);
					}
					if(  ListTools.isNotEmpty( groupNames )) {
						targetList.addAll(groupNames);
					}
					mindIds_recived = mindInfoService.listSharedMindIdsFromRecord( source, targetList, null );
				}catch( Exception e ) {
					check = false;
					Exception exception = new ExceptionMindQuery( e,  "사용자가 다른 사람에게 공유한 마인드맵 정보를 조회하는 중 오류가 발생했습니다.", effectivePerson.getDistinguishedName() );
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		personNames.add( effectivePerson.getDistinguishedName() );
		if( check ){
			try {
				mindEnityies = mindInfoService.listNextPageWithFilter( startId, count, wi.getKey(), null, true, null, null, 
						personNames, unitNames, groupNames, wi.getOrderField(), wi.getOrderType(), mindIds_recived );
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
		
		@FieldDescribe( "공유자" )
		private String source = null;
		
		@FieldDescribe( "키워드" )
		private String key = null;

		@FieldDescribe( "정렬 열: 기본값 sequence" )
		private String orderField =  JpaObject.sequence_FIELDNAME ;
		
		@FieldDescribe( "정렬 방식: DESC|ASC, 기본값 DESC" )
		private String orderType = "DESC";
		
		public String getSource() {
			return source;
		}

		public void setSource(String source) {
			this.source = source;
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