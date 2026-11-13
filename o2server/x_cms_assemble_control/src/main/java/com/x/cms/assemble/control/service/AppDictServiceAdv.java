package com.x.cms.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.element.AppDict;

/**
 * 게시판 정보 관리 서비스 클래스(고급)
 * 고급 서비스는 Service로 트랜잭션 제어 가능
 * 
 * @author O2LEE
 */
public class AppDictServiceAdv {

	private AppDictService appDictService = new AppDictService();

	/**
	 * 게시판 ID로 모든 데이터 사전 정보 ID 목록 조회
	 * 
	 * @param appId
	 * @return
	 * @throws Exception
	 */
	public List<String> listIdsWithAppId( String appId ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appDictService.listIdsWithAppId( emc, appId );
		} catch (Exception e) {
			throw e;
		}
	}

    /**
     * 게시판 ID로 모든 데이터 사전 정보 목록 조회
     *
     * @param appId
     * @return
     * @throws Exception
     */
    public List<AppDict> listWithAppId(String appId ) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            List<String> ids = appDictService.listIdsWithAppId( emc, appId );
            if(ListTools.isNotEmpty(ids) ){
                return appDictService.list( emc, ids );
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

}
