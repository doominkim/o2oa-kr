package com.x.cms.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.element.Script;

/**
 * 게시판 정보 관리 서비스 클래스(고급)
 * 고급 서비스는 Service로 트랜잭션 제어 가능
 * 
 * @author O2LEE
 */
public class ScriptServiceAdv {

	private ScriptService scriptService = new ScriptService();

	/**
	 * 게시판 ID로 모든 스크립트 정보 ID 목록 조회
	 * 
	 * @param appId
	 * @return
	 * @throws Exception
	 */
	public List<String> listIdsWithAppId( String appId ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return scriptService.listIdsWithAppId( emc, appId);
		} catch (Exception e) {
			throw e;
		}
	}

    /**
     * 게시판 ID로 모든 스크립트 정보 목록 조회
     *
     * @param appId
     * @return
     * @throws Exception
     */
    public List<Script> listWithAppId(String appId ) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            List<String> ids = scriptService.listIdsWithAppId( emc, appId);
            if(ListTools.isNotEmpty(ids) ){
                return emc.list( Script.class, ids );
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

}
