package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.cms.core.entity.AppInfo;

public class ActionListAll extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionListAll.class );
	
	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = null;
		List<AppInfo> appInfoList = null;
		Boolean check = true;

		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass() );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);

		if (optional.isPresent()) {
			result.setData((List<Wo>)optional.get());
		} else {
			try {
				appInfoList = appInfoServiceAdv.listAll( null, "전체");
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAppInfoProcess( e, "전체 게시판 정보 객체를 조회하는 중 오류가 발생했습니다" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
			if( check ){
				if( ListTools.isNotEmpty( appInfoList ) ){
					try {
						wos = Wo.copier.copy( appInfoList );
						SortTools.desc( wos, "appInfoSeq");
						CacheManager.put(cacheCategory, cacheKey, wos);
						result.setData( wos );
					} catch (Exception e) {
						Exception exception = new ExceptionAppInfoProcess( e, "조회한 게시판 정보 객체를 출력 데이터로 변환하는 중 오류가 발생했습니다." );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}
		}
		return result;
	}
}