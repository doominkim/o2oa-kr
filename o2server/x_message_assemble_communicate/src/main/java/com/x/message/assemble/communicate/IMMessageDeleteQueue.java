package com.x.message.assemble.communicate;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.message.core.entity.IMMsg;
import com.x.message.core.entity.IMMsgCollection;
import com.x.message.core.entity.IMMsgFile;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class IMMessageDeleteQueue extends AbstractQueue<IMMsg> {

    private static final Logger LOGGER = LoggerFactory.getLogger(IMMessageDeleteQueue.class);

    @Override
    protected void execute(IMMsg imMsg) throws Exception {
        if (StringUtils.isEmpty(imMsg.getId())) {
            LOGGER.warn("id가 전달되지 않아 메시지를 삭제할 수 없습니다");
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("메시지 삭제 시작, id {}", imMsg.getId());
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            IMMsg message = emc.find(imMsg.getId(), IMMsg.class);
            if (null == message) {
                LOGGER.info("메시지 객체를 찾을 수 없음 {}", imMsg.getId());
                return;
            }
            // 즐겨찾기한 메시지 삭제
            List<IMMsgCollection> collections = business.imConversationFactory()
                    .listCollectionByPersonAndMsgId(null, message.getId());
            if (null != collections && !collections.isEmpty()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("연관된 즐겨찾기 메시지 데이터도 함께 삭제, collections {}",
                            collections.size());
                }
                for (IMMsgCollection collection : collections) {
                    emc.beginTransaction(IMMsgCollection.class);
                    emc.remove(collection);
                    emc.commit();
                }
            }
            // 메시지에 해당하는 파일 삭제(있는 경우)
            String fileId = message.getBodyFileId();
            // 먼저 메시지 자체를 삭제
            emc.beginTransaction(IMMsg.class);
            emc.remove(message);
            emc.commit();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("메시지 삭제 완료, id {}", message.toString());
            }
            if (StringUtils.isNotEmpty(fileId)) { // 파일이 포함된 메시지. 해당 파일과 데이터 삭제
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("파일이 포함된 메시지. 유휴 파일 객체인지 조회, fileId {}", fileId);
                }
                // 먼저 이 파일과 연관된 다른 메시지가 있는지 조회
                List<IMMsg> list = business.imConversationFactory().listMessageByFileId(fileId);
                if (null == list || list.isEmpty()) { // 연관 데이터 없음. 파일 객체 삭제
                    IMMsgFile file = emc.find(fileId, IMMsgFile.class);
                    if (null != file) {
                        StorageMapping mapping = ThisApplication.context().storageMappings()
                                .get(IMMsgFile.class, file.getStorage());
                        if (null != mapping) {
                            file.deleteContent(mapping);
                        }
                        emc.beginTransaction(IMMsgFile.class);
                        emc.delete(IMMsgFile.class, file.getId());
                        emc.commit();
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug(" 해당 파일과 데이터 삭제, fileId {}", fileId);
                        }
                    }
                }
            }
        }
    }


}
