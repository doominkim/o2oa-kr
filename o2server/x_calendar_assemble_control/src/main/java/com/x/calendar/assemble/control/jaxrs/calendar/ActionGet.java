package com.x.calendar.assemble.control.jaxrs.calendar;

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
import com.x.calendar.assemble.control.ThisApplication;
import com.x.calendar.core.entity.Calendar;

public class ActionGet extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionGet.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		Calendar calendar = null;
		Boolean check = true;
		List<String> unitNames = null;
		List<String> groupNames = null;
		String personName = effectivePerson.getDistinguishedName();
		
		if( check ){
			if( StringUtils.isEmpty( id ) ){
				check = false;
				Exception exception = new ExceptionCalendarIdEmpty();
				result.error( exception );
			}
		}

		if( check ){
			try {
				unitNames = userManagerService.listUnitNamesWithPerson(personName);
				groupNames = userManagerService.listGroupNamesByPerson(personName);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCalendarInfoProcess( e, "사용자의 조직 및 그룹 목록을 조회하는 중 오류가 발생했습니다." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				calendar = calendarServiceAdv.get( id );
				if( calendar == null ) {
					check = false;
					Exception exception = new ExceptionCalendarNotExists( id );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCalendarInfoProcess( e, "ID로 캘린더 정보를 조회하는 중 오류가 발생했습니다. ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				wrap = Wo.copier.copy( calendar );
				if( StringUtils.equalsAnyIgnoreCase("SYSTEM", calendar.getCreateor() )){
					wrap.setCreateor( effectivePerson.getDistinguishedName() );
				}
				wrap.setManageable( ThisApplication.isCalendarManager( effectivePerson, wrap ) );
				wrap.setPublishable( ThisApplication.isCalendarPublisher( effectivePerson, unitNames, groupNames, wrap ) );

				if(ListTools.isNotEmpty( wrap.getManageablePersonList() )){
					for( String person : wrap.getManageablePersonList() ){
						if( StringUtils.equalsAnyIgnoreCase( person, "SYSTEM")){
							if( !wrap.getManageablePersonList().contains( effectivePerson.getDistinguishedName() )){
								wrap.getManageablePersonList().add( effectivePerson.getDistinguishedName() );
							}
						}
					}
				}

				if(ListTools.isNotEmpty( wrap.getViewablePersonList() )){
					for( String person : wrap.getViewablePersonList() ){
						if( StringUtils.equalsAnyIgnoreCase( person, "SYSTEM")){
							if( !wrap.getViewablePersonList().contains( effectivePerson.getDistinguishedName() )){
								wrap.getViewablePersonList().add( effectivePerson.getDistinguishedName() );
							}
						}
					}
				}

				if(ListTools.isNotEmpty( wrap.getViewablePersonList() )){
					for( String person : wrap.getViewablePersonList() ){
						if( StringUtils.equalsAnyIgnoreCase( person, "SYSTEM")){
							if( !wrap.getViewablePersonList().contains( effectivePerson.getDistinguishedName() )){
								wrap.getViewablePersonList().add( effectivePerson.getDistinguishedName() );
							}
						}
					}
				}
			} catch (Exception e) {
				Exception exception = new ExceptionCalendarInfoProcess( e, "조회한 캘린더 정보를 출력 정보로 변환하는 중 오류가 발생했습니다." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		result.setData( wrap );
		return result;
	}

	public static class Wo extends Calendar  {
		
		@FieldDescribe("사용자의 해당 캘린더 관리 가능 여부.")
		private Boolean manageable = false;
		
		@FieldDescribe("사용자의 해당 캘린더 일정 이벤트 게시 가능 여부.")
		private Boolean publishable = false;
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier<Calendar, Wo> copier = WrapCopierFactory.wo( Calendar.class, Wo.class, null,Wo.Excludes);

		public Boolean getManageable() {
			return manageable;
		}

		public Boolean getPublishable() {
			return publishable;
		}

		public void setManageable(Boolean manageable) {
			this.manageable = manageable;
		}

		public void setPublishable(Boolean publishable) {
			this.publishable = publishable;
		}		
	}

}