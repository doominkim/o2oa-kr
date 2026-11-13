package com.x.bbs.assemble.control.service;

import java.util.List;
import java.util.Optional;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSConfigSetting;



/**
 * 플랫폼 설정
 * @author sword
 */
public class BBSConfigSettingService{

	private static  Logger logger = LoggerFactory.getLogger( BBSConfigSettingService.class );

	private Cache.CacheCategory cacheCategory = new Cache.CacheCategory(BBSConfigSetting.class);

	/**
	 * 전달된 ID로 데이터베이스에서 BBSConfigSetting 객체 조회
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public BBSConfigSetting get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, BBSConfigSetting.class );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 데이터베이스에 BBSConfigSetting 객체 저장
	 * @param bbsConfigSetting
	 */
	public BBSConfigSetting save( BBSConfigSetting bbsConfigSetting ) throws Exception {
		BBSConfigSetting _bbsConfigSetting = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			_bbsConfigSetting =  emc.find( bbsConfigSetting.getId(), BBSConfigSetting.class );
			if( _bbsConfigSetting != null ){
				emc.beginTransaction( BBSConfigSetting.class );
				bbsConfigSetting.copyTo( _bbsConfigSetting, JpaObject.FieldsUnmodify  );
				emc.check( _bbsConfigSetting, CheckPersistType.all );
				emc.commit();
			}else{
				emc.beginTransaction( BBSConfigSetting.class );
				emc.persist( bbsConfigSetting, CheckPersistType.all);
				emc.commit();
			}
			CacheManager.notify(BBSConfigSetting.class);
		}catch( Exception e ){
			logger.warn( "BBSConfigSetting update/ got a error!" );
			throw e;
		}
		return bbsConfigSetting;
	}

	/**
	 * 데이터베이스에 BBSConfigSetting 객체 저장
	 * @param bbsConfigSetting
	 */
	public BBSConfigSetting update( BBSConfigSetting bbsConfigSetting ) throws Exception {
		BBSConfigSetting _bbsConfigSetting = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business( emc );
			_bbsConfigSetting =  business.configSettingFactory().getWithConfigCode( bbsConfigSetting.getConfigCode() );
			if( _bbsConfigSetting != null ){
				emc.beginTransaction( BBSConfigSetting.class );
				bbsConfigSetting.copyTo( _bbsConfigSetting, JpaObject.FieldsUnmodify  );
				emc.check( _bbsConfigSetting, CheckPersistType.all );
				emc.commit();
				CacheManager.notify(BBSConfigSetting.class);
			}else{
				throw new Exception("config setting '"+ bbsConfigSetting.getConfigCode() +"'  not exists");
			}
		}catch( Exception e ){
			logger.warn( "BBSConfigSetting update/ got a error!" );
			throw e;
		}
		return bbsConfigSetting;
	}

	/**
	 * ID로 데이터베이스에서 BBSConfigSetting 객체 삭제
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		BBSConfigSetting bbsConfigSetting = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//먼저 작업할 앱 정보 존재 여부를 ID로 조회하고, 없으면 작업을 계속할 수 없음
			bbsConfigSetting = emc.find(id, BBSConfigSetting.class);
			if (null == bbsConfigSetting) {
				logger.warn( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.beginTransaction( BBSConfigSetting.class );
				emc.remove( bbsConfigSetting, CheckRemoveType.all );
				emc.commit();
				CacheManager.notify(BBSConfigSetting.class);
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 전달된 ID로 데이터베이스에서 BBSConfigSetting 객체 조회
	 * @param configCode
	 * @return
	 * @throws Exception
	 */
	public String getValueWithConfigCode( String configCode ) throws Exception {
		if( configCode  == null || configCode.isEmpty() ){
			throw new Exception( "configCode is null!" );
		}
		Cache.CacheKey cacheKey = new Cache.CacheKey(BBSConfigSetting.class.getName(), configCode);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			return String.valueOf(optional.get());
		} else {
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				Business business = new Business(emc);
				String result =  business.configSettingFactory().getValueWithConfigCode( configCode );
				if(result != null){
					CacheManager.put(cacheCategory, cacheKey, result);
				}
				return result;
			}catch( Exception e ){
				throw e;
			}
		}
	}

	/**
	 * 포럼 닉네임 사용 여부, 기본값 사용 안 함
	 */
	public boolean useNickName() {
		try {
			String configValue = this.getValueWithConfigCode(BBSConfigSetting.BBS_CONFIG_USE_NICKNAME);
			if(BBSConfigSetting.BBS_CONFIG_USE_NICKNAME_YES.equalsIgnoreCase(configValue)){
				return true;
			}
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		return false;
	}

	/**
	 * 전달된 ID로 데이터베이스에서 BBSConfigSetting 객체 조회
	 * @param configCode
	 * @return
	 * @throws Exception
	 */
	public BBSConfigSetting getWithConfigCode( String configCode ) throws Exception {
		if( configCode  == null || configCode.isEmpty() ){
			throw new Exception( "configCode is null!" );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.configSettingFactory().getWithConfigCode(configCode);
		}catch( Exception e ){
			throw e;
		}
	}

	public List<BBSConfigSetting> listAll() throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.configSettingFactory().listAll();
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 설정 항목 존재 여부를 확인하고, 없으면 정보로 새 설정 항목 생성
	 * @param configCode
	 * @param configName
	 * @param configValue
	 * @param description
	 * @throws Exception
	 */
	public void checkAndInitSystemConfig( String configCode, String configName, String configValue, String description, String type, String selectContent, Boolean isMultiple, Integer orderNumber ) throws Exception {
		if( configCode  == null || configCode.isEmpty() ){
			throw new Exception( "configCode is null!" );
		}
		if( configName  == null || configName.isEmpty() ){
			throw new Exception( "configName is null!" );
		}
		Business business = null;
		BBSConfigSetting bbsConfigSetting = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			bbsConfigSetting = business.configSettingFactory().getWithConfigCode( configCode );
		}catch( Exception e ){
			logger.warn( "system find system config{'configCode':'"+configCode+"'} got an exception. " );
			throw e;
		}
		//설정이 없으면 새 설정 레코드 생성
		if( bbsConfigSetting == null ){
			bbsConfigSetting = new BBSConfigSetting();
			bbsConfigSetting.setConfigCode( configCode );
			bbsConfigSetting.setConfigName( configName );
			bbsConfigSetting.setConfigValue( configValue );
			bbsConfigSetting.setDescription( description );
			bbsConfigSetting.setOrderNumber( orderNumber );
			bbsConfigSetting.setValueType( type );
			bbsConfigSetting.setSelectContent( selectContent );
			bbsConfigSetting.setIsMultiple( isMultiple );
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				emc.beginTransaction( BBSConfigSetting.class );
				emc.persist( bbsConfigSetting, CheckPersistType.all );
				emc.commit();
			}catch( Exception e ){
				logger.warn("system persist new system config{'configCode':'"+configCode+"'} got an exception. " );
				throw e;
			}
		}else{
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				bbsConfigSetting = emc.find( bbsConfigSetting.getId(), BBSConfigSetting.class );
				emc.beginTransaction( BBSConfigSetting.class );
				if( !configName.equals( bbsConfigSetting.getConfigName() ) ){
					bbsConfigSetting.setConfigName( configName );
				}
				if( orderNumber != bbsConfigSetting.getOrderNumber() ){
					bbsConfigSetting.setOrderNumber(orderNumber);
				}
				if( description != null ){
					bbsConfigSetting.setDescription( description );
				}
				if( type != null ){
					bbsConfigSetting.setValueType( type );
				}
				if( selectContent != null ){
					bbsConfigSetting.setSelectContent( selectContent );
				}
				if( isMultiple != null ){
					bbsConfigSetting.setIsMultiple( isMultiple );
				}
				emc.check( bbsConfigSetting, CheckPersistType.all );
				emc.commit();
			}catch( Exception e ){
				logger.warn("system update system config{'configCode':'"+configCode+"'} got an exception. "  );
				throw e;
			}
		}
	}

	/**
	 *  BBS_LOGO_NAME	포럼 시스템 이름	ADMIN_AND_ALLLEADER
	 */
	public void initAllSystemConfig() {
		String value = null, description = null, type = null, selectContent = null;
		Boolean isMultiple = false;
		Integer ordernumber = 0;

		value = "사내 포럼";
		type = "text";
		selectContent = null;
		isMultiple = false;
		description = "포럼 시스템 이름: 시스템 이름을 지정할 수 있습니다. 예: 최상위 조직 이름을 포럼 이름으로 사용";
		try {
			checkAndInitSystemConfig("BBS_LOGO_NAME", "포럼 시스템 이름", value, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'BBS_LOGO_NAME' got an exception." );
			logger.error(e);
		}

		value = " - O2OA";
		type = "text";
		selectContent = null;
		isMultiple = false;
		description = "포럼 제목 접미사: 전체 웹 페이지 제목 뒤에 붙는 내용입니다. 비워 둘 수 있습니다.";
		try {
			checkAndInitSystemConfig("BBS_TITLE_TAIL", "포럼 제목 접미사", value, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'BBS_TITLE_TAIL' got an exception." );
			logger.error(e);
		}

		value = "信息|问题|投票";
		type = "select";
		selectContent = "信息|问题|投票";
		isMultiple = true;
		description = "주제 유형: 정보|질문|투표 중 다중 선택.";
		try {
			checkAndInitSystemConfig("BBS_SUBJECT_TYPECATAGORY", "주제 유형", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'BBS_SUBJECT_TYPECATAGORY' got an exception." );
			logger.error(e);
		}

		value = "Recursively";
		type = "select";
		selectContent = "Single|Recursively";
		isMultiple = false;
		description = "댓글 삭제 방식: 선택 값 Single|Recursively, 단일 선택.";
		try {
			checkAndInitSystemConfig("BBS_REPLY_DELETETYPE", "댓글 삭제 방식", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'BBS_REPLY_DELETETYPE' got an exception." );
			logger.error(e);
		}

		value = "ASC";
		type = "select";
		selectContent = "ASC|DESC";
		isMultiple = false;
		description = "댓글 정렬 방식: 선택 값 ASC|DESC, 작성 시간 오름차순|내림차순, 단일 선택.";
		try {
			checkAndInitSystemConfig("BBS_REPLY_SORTTYPE", "댓글 정렬 방식", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'BBS_REPLY_SORTTYPE' got an exception." );
			logger.error(e);
		}

		value = "DESC";
		type = "select";
		selectContent = "ASC|DESC";
		isMultiple = false;
		description = "내 댓글 목록 정렬 방식: 선택 값 ASC|DESC, 작성 시간 오름차순|내림차순, 단일 선택.";
		try {
			checkAndInitSystemConfig("BBS_MYREPLY_SORTTYPE", "내 댓글 정렬 방식", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'BBS_MYREPLY_SORTTYPE' got an exception." );
			logger.error(e);
		}

		value = "YES";
		type = "select";
		selectContent = "YES|NO";
		isMultiple = false;
		description = "익명 접근 허용 여부: 선택 값 YES|NO(허용|허용 안 함), 단일 선택.";
		try {
			checkAndInitSystemConfig("BBS_ANONYMOUS_PERMISSION", "익명 접근 허용 여부", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'BBS_ANONYMOUS_PERMISSION' got an exception." );
			logger.error(e);
		}

		value = "YES";
		type = "select";
		selectContent = "YES|NO";
		isMultiple = false;
		description = "닉네임 사용 여부: 선택 값 YES|NO(허용|허용 안 함), 단일 선택.";
		try {
			checkAndInitSystemConfig("BBS_USE_NICKNAME", "닉네임 사용 여부", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'BBS_USE_NICKNAME' got an exception." );
			logger.error(e);
		}
	}
}
