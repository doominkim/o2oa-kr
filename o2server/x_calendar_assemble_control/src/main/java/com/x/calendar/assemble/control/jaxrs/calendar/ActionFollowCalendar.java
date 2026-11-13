package com.x.calendar.assemble.control.jaxrs.calendar;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.calendar.core.entity.Calendar;

/**
 * 공개 캘린더 구독(비공개 캘린더는 구독 불가)
 * 
 * @author O2LEE
 *
 */
public class ActionFollowCalendar extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionFollowCalendar.class );

	@AuditLog(operation = "关注日历")
	protected ActionResult<WrapOutBoolean> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		Wo wo = new Wo();
		Calendar calendar = null;
		Boolean check = true;
		
		if( check ){
			if( StringUtils.isEmpty( id ) ){
				check = false;
				Exception exception = new ExceptionCalendarIdEmpty();
				result.error( exception );
			}
		}
		
		if( check ){
			try {
				calendar = calendarServiceAdv.get( id );
				if( calendar == null ) {
					check = false;
					Exception exception = new ExceptionCalendarNotExists( id );
					result.error( exception );
				}else {
					if( !calendar.getIsPublic() ) {
						check = false;
						Exception exception = new ExceptionCalendarInfoProcess( "공개되지 않은 캘린더는 구독할 수 없습니다!" );
						result.error( exception );
					}
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCalendarInfoProcess( e, "ID로 캘린더 정보를 조회하는 중 오류가 발생했습니다. ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if( check ){
			if(
					( StringUtils.equalsAnyIgnoreCase( calendar.getCreateor(), effectivePerson.getDistinguishedName() ) ) ||
					( StringUtils.equalsAnyIgnoreCase( calendar.getTarget(), effectivePerson.getDistinguishedName() ) ) ||
					( StringUtils.equalsAnyIgnoreCase( calendar.getCreateor(), "SYSTEM" ) )
			){
				check = false;
				Exception exception = new ExceptionCalendarInfoProcess( "자신의 캘린더는 구독할 필요가 없습니다. ID:" + id );
				result.error( exception );
			}
		}

		if( check ){
			try {
				check = calendarServiceAdv.follow( effectivePerson, id );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCalendarInfoProcess( e, "ID로 캘린더를 구독하는 중 오류가 발생했습니다. ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		wo.setValue( check );
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapOutBoolean  {
	}

}