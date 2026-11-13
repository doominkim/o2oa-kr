package com.x.mind.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.tools.ListTools;
import com.x.mind.assemble.common.date.DateOperation;
import com.x.mind.entity.MindBaseInfo;
import com.x.mind.entity.MindContentInfo;
import com.x.mind.entity.MindIconInfo;
import com.x.mind.entity.MindRecycleInfo;
import com.x.mind.entity.MindShareRecord;
import com.x.mind.entity.MindVersionContent;
import com.x.mind.entity.MindVersionInfo;

/**
 * 마인드맵 정보 서비스 클래스
 * @author O2LEE
 *
 */
public class MindInfoService{
	private ServiceMindInfoQuery serviceMindInfoQuery = new ServiceMindInfoQuery();
	private ServiceMindInfoPersist serviceMindInfoPersist = new ServiceMindInfoPersist();
	private ServiceMindVersionInfoQuery serviceMindVersionInfoQuery = new ServiceMindVersionInfoQuery();
	private ServiceMindVersionInfoPersist serviceMindVersionInfoPersist = new ServiceMindVersionInfoPersist();
	private ServiceMindShareRecordQuery serviceMindShareRecordQuery = new ServiceMindShareRecordQuery();
	private ServiceMindShareRecordPersist serviceMindShareRecordPersist = new ServiceMindShareRecordPersist();
	private DateOperation dssateOperation = new DateOperation();
	/**
	 * 지정 디렉터리 ID로 디렉터리의 모든 마인드맵 정보 ID 목록 조회
	 * @param person
	 * @return
	 * @throws Exception 
	 */
	public List<String> listIdsWithFolder(String folderId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindInfoQuery.listIdsWithFolder( emc, folderId );
		      } catch (Exception e) {
		            throw e;
		      }
	}
	
	/**
	 * 지정 ID로 마인드맵 정보 조회
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public MindBaseInfo getMindBaseInfo(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindInfoQuery.getMindBaseInfo(emc, id);
		      } catch (Exception e) {
		            throw e;
		      }
	}
	
	/**
	 * 마인드맵 ID로 마인드맵 썸네일 Base64 인코딩 정보 조회
	 * @param mindId
	 * @return
	 * @throws Exception
	 */
	public MindIconInfo getMindIconInfo(String mindId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find( mindId, MindIconInfo.class );
		      } catch (Exception e) {
		    throw e;
		      }
	}
	
	/**
	 * ID로 휴지통 마인드맵 정보 조회
	 * @param recycleId
	 * @return
	 * @throws Exception 
	 */
	public MindRecycleInfo getMindRecycleInfo(String recycleId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find( recycleId, MindRecycleInfo.class );
		      } catch (Exception e) {
		    throw e;
		      }
	}
	
	/**
	 * 지정 ID 목록으로 마인드맵 정보 목록 조회
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<MindBaseInfo> listMindBaseInfoWithIds(List<String> ids) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindInfoQuery.listWithIds( emc, ids );
	      } catch (Exception e) {
	            throw e;
	      }
	}

	/**
	 * 마인드맵 정보 저장
	 * @param _mindBaseInfo
	 * @param content
	 * @return
	 * @throws Exception 
	 */
	public MindBaseInfo save( MindBaseInfo _mindBaseInfo, String content, int maxVersionCount ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			
			//마인드맵 정보 저장
			MindBaseInfo mindBaseInfo = serviceMindInfoPersist.save( emc, _mindBaseInfo,  content);
			
			String mindId = mindBaseInfo.getId();
			//마인드맵 저장 시 기존 버전 상황에 따라 새 버전 추가 여부 결정
			MindVersionInfo mindVersionInfo = null;
			//최신 버전 상황 조회
			mindVersionInfo = serviceMindVersionInfoQuery.getLatestVersionWithMind( emc, mindId );
			if( mindVersionInfo != null ) {
				//버전이 있으면 버전 정보 비교
				//10분 이내에 데이터 크기가 20% 넘게 바뀌면 새 버전을 만들고, 그렇지 않으면 만들지 않음,
				long diff_min = dssateOperation.getDiff( mindVersionInfo.getUpdateTime(), mindBaseInfo.getUpdateTime() );
				if( diff_min < 10 ) {
					MindContentInfo mindContentInfo = serviceMindInfoQuery.getMindContentInfo(emc,  mindId );
					MindVersionContent mindVersionContent = serviceMindVersionInfoQuery.getMindVersionContentInfo(emc, mindVersionInfo.getId() );
					//데이터 크기를 비교해 20% 넘게 바뀌면 새 버전 생성
					if( dataDiff( mindContentInfo.getContent(), mindVersionContent.getContent() ) > 0.2 ) {
						//10분이 지나면 새 버전 생성
						serviceMindVersionInfoPersist.createFromMind( emc, _mindBaseInfo, content, maxVersionCount );
					}
				}else {
					//10분이 지나면 새 버전 생성
					serviceMindVersionInfoPersist.createFromMind( emc, _mindBaseInfo, content, maxVersionCount );
				}
			}else {
				serviceMindVersionInfoPersist.createFromMind( emc, _mindBaseInfo, content, maxVersionCount );
			}
			return mindBaseInfo;
	      } catch (Exception e) {
	            throw e;
	      }
	}

	private double dataDiff(String new_content, String old_content ) {
		int new_length = 0;
		int old_length = 0;
		if( new_content != null ) {
			new_length = new_content.getBytes().length;
		}
		if( old_content != null ) {
			old_length = old_content.getBytes().length;
		}
		if( old_length == new_length ) {
			return 0;
		}else if( old_length > new_length ) {
			return ((double)old_length - (double)new_length)/(double)old_length;
		}else {
			return ((double)new_length - (double)old_length)/(double)new_length;
		}
	}

	/**
	 * 마인드맵 조회, 다음 페이지
	 * @param id
	 * @param count
	 * @param name
	 * @param folderId
	 * @param shared
	 * @param creator
	 * @param creatorUnit
	 * @param sharePersons
	 * @param shareUnits
	 * @param shareGroups
	 * @param orderField
	 * @param orderType
	 * @param inMindIds
	 * @return
	 * @throws Exception
	 */
	public List<MindBaseInfo> listNextPageWithFilter(String id, Integer count, String key, String folderId, Boolean shared,
			String creator, String creatorUnit, List<String> sharePersons, List<String> shareUnits, List<String> shareGroups, 
			String orderField, String orderType, List<String> inMindIds ) throws Exception {
		if( orderField == null || orderField.isEmpty() ){
			orderField =  JpaObject.sequence_FIELDNAME;
		}
		if( orderType == null || orderType.isEmpty() ){
			orderType = "DESC";
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return serviceMindInfoQuery.listNextPageWithFilter( emc, id, count, key, folderId, shared,
					creator, creatorUnit, sharePersons, shareUnits, shareGroups, orderField, orderType, inMindIds );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 휴지통 마인드맵 조회, 다음 페이지
	 * @param id
	 * @param count
	 * @param name
	 * @param folderId
	 * @param shared
	 * @param creator
	 * @param creatorUnit
	 * @param orderField
	 * @param orderType
	 * @return
	 * @throws Exception 
	 */
	public List<MindRecycleInfo> listRecycleNextPageWithFilter(String id, Integer count, String key, String folderId, Boolean shared,
			String creator, String creatorUnit, String orderField, String orderType, List<String> inMindIds ) throws Exception {
		if( orderField == null || orderField.isEmpty() ){
			orderField =  JpaObject.sequence_FIELDNAME;
		}
		if( orderType == null || orderType.isEmpty() ){
			orderType = "DESC";
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return serviceMindInfoQuery.listRecycleNextPageWithFilter( emc, id, count, key, folderId, shared,
					creator, creatorUnit, orderField, orderType, inMindIds );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * ID로 마인드맵 내용 조회
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public String getContent(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				return serviceMindInfoQuery.getMindContent(emc, id);
		      } catch (Exception e) {
		            throw e;
		      }
	}
	
	/**
	 * 특정 디렉터리의 마인드맵 수 조회
	 * @param folderId
	 * @return
	 * @throws Exception 
	 */
	public Long countMindWithFolder(String folderId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindInfoQuery.countMindWithFolder(emc, folderId);
		      } catch (Exception e) {
		    throw e;
		      }
	}

	/**
	 * 마인드맵 ID로 마인드맵 정보 삭제
	 * 기본 정보, 상세 내용 및 공유 정보
	 * @param mindId
	 * @return 
	 * @throws Exception 
	 */
	public Boolean destroyMind(String mindId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindInfoPersist.destroyMind(emc, mindId);
		      } catch (Exception e) {
		    throw e;
		      }
	}
	
	/**
	 * 마인드맵 ID로 마인드맵 정보를 휴지통으로 이동
	 * 기본 정보 및 공유 정보
	 * @param mindId
	 * @return 
	 * @return 
	 * @throws Exception 
	 */
	public Boolean recycle(String mindId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindInfoPersist.recycle(emc, mindId);
		      } catch (Exception e) {
		    throw e;
		      }
	}

	/**
	 * ID로 휴지통에서 마인드맵 정보 복원
	 * 기본 정보 및 공유 정보
	 * @param recycleId
	 * @return 
	 * @throws Exception 
	 */
	public Boolean restore(String recycleId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindInfoPersist.restore(emc, recycleId);
		      } catch (Exception e) {
		    throw e;
		      }
	}	
	
	/**
	 * 마인드맵 썸네일 수정
	 * @param mindId
	 * @param base64
	 * @throws Exception 
	 */
	public void updateIcon(String mindId, String base64) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			MindIconInfo mindIconInfo = emc.find( mindId, MindIconInfo.class );
			emc.beginTransaction( MindIconInfo.class );
			if( mindIconInfo != null ) {
				mindIconInfo.setContent(base64);
				emc.check( mindIconInfo, CheckPersistType.all );
			}else {
				mindIconInfo = new MindIconInfo();
				mindIconInfo.setId(mindId);
				mindIconInfo.setContent(base64);
				emc.persist( mindIconInfo, CheckPersistType.all );
			}
			emc.commit();
		      } catch (Exception e) {
		    throw e;
		      }
	}
	
	/**
	 * 마인드맵 ID로 마인드맵 썸네일 Base64 인코딩 내용 조회
	 * @param mindId
	 * @return
	 * @throws Exception
	 */
	public String getMindIconBase64(String mindId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			MindIconInfo mindIconInfo = emc.find( mindId, MindIconInfo.class );
			if( mindIconInfo != null ) {
				return mindIconInfo.getContent();
			}
			return null;
		      } catch (Exception e) {
		    throw e;
		      }
	}
	
	/**
	 * 마인드맵 ID로 모든 버전 조회(10개만 유지)
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public List<String> listVersionsWithMindId(String mindId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindVersionInfoQuery.listVersionsWithMindId( emc, mindId);
		      } catch (Exception e) {
		    throw e;
		      }
	}

	/**
	 * ID 목록으로 마인드맵 버전 정보 목록 조회
	 * @param ids
	 * @return
	 * @throws Exception 
	 */
	public List<MindVersionInfo> listVersionWithIds(List<String> ids) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindVersionInfoQuery.listWithIds(emc, ids);
		      } catch (Exception e) {
		    throw e;
		      }
	}

	/**
	 * 지정 ID로 마인드맵 버전 정보 객체 조회
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public MindVersionInfo getMindVersionInfo(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindVersionInfoQuery.getMindVersionInfo(emc, id);
		      } catch (Exception e) {
		    throw e;
		      }
	}

	/**
	 * 지정 ID로 지정 마인드맵 버전의 상세 내용 조회
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public String getMindVersionContent(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindVersionInfoQuery.getMindVersionContent(emc, id);
		} catch (Exception e) {
		    throw e;
		}
	}

	/**
	 * 마인드맵 정보를 지정 사용자·조직에 공유
	 * @param mindBaseInfo 
	 * @param mindShareRecords
	 * @throws Exception 
	 */
	public void share(MindBaseInfo mindBaseInfo, List<MindShareRecord> mindShareRecords ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			serviceMindShareRecordPersist.share( emc, mindBaseInfo, mindShareRecords );
		} catch (Exception e) {
		    throw e;
		}
	}
	
	/**
	 * 마인드맵 ID로 마인드맵 공유 기록 조회
	 * @param mindId 
	 * @throws Exception 
	 */
	public List<String> listShareRecordWithMindId(String mindId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindShareRecordQuery.listIdsWithMindId( emc, mindId );
		} catch (Exception e) {
		    throw e;
		}
	}

	/**
	 * 공유 취소
	 * @param shareRecordId
	 * @throws Exception
	 */
	public void shareCancel(String shareRecordId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			serviceMindShareRecordPersist.shareCancel(emc, shareRecordId);
		} catch (Exception e) {
		    throw e;
		}
	}

	public MindShareRecord getMindShareRecord(String shareRecordId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find( shareRecordId, MindShareRecord.class );
		} catch (Exception e) {
		    throw e;
		}
	}

	public List<MindShareRecord> listSharedRecords( List<String> ids ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindShareRecordQuery.listSharedRecords(emc, ids );
		} catch (Exception e) {
		    throw e;
		}
	}
	
	public List<String> listSharedRecordIds(String source, List<String> targetList, List<String> inMindIds) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindShareRecordQuery.listSharedRecordIds( emc, source, targetList, inMindIds );
		} catch (Exception e) {
		    throw e;
		}
	}

	public List<String> listSharedMindIdsFromRecord(String source, List<String> targetList, List<String> inMindIds) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindShareRecordQuery.listSharedMindIdsFromRecord( emc, source, targetList, inMindIds );
		} catch (Exception e) {
		    throw e;
		}
	}

	public Boolean moveToFolder( List<String> targetMindIds, String folderId ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			MindBaseInfo mind = null;
			if( ListTools.isNotEmpty( targetMindIds )) {
				emc.beginTransaction( MindBaseInfo.class );
				for( String id : targetMindIds ) {
					 mind = emc.find( id, MindBaseInfo.class );
					 if( mind != null ) {
							mind.setFolderId(folderId);
							emc.check( mind, CheckPersistType.all );
					 }
				}
				emc.commit();
			}
			return true;
		} catch (Exception e) {
		    throw e;
		}
	}
}
