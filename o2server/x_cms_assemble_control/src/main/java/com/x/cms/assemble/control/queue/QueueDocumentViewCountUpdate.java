package com.x.cms.assemble.control.queue;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;
import com.x.query.core.entity.Item;

/**
 * Document 조회 시 총 조회수를 item의 document에 반영해야 함(뷰에서 사용), 큐에서 비동기로 수정
 */
public class QueueDocumentViewCountUpdate extends AbstractQueue<Document> {
	
	private Gson gson = XGsonBuilder.instance();

	public void execute( Document obj ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Document document = emc.find( obj.getId(), Document.class);
			JsonElement jsonElement = null;
			
			if ( null == document ) {
				throw new ExceptionDocumentNotExists( obj.getId() );
			}
			if( obj.getViewCount() > document.getViewCount() ) {
				document.setViewCount( obj.getViewCount() );
			}
			jsonElement = gson.toJsonTree( document );
			
			/** DataItem 데이터 갱신. */
			updateData( new Business(emc), document, jsonElement, "$document" );
		}
	}
	
	void updateData(Business business, Document document, JsonElement jsonElement, String... paths) throws Exception {
		DataItemConverter<Item> converter = new DataItemConverter<>(Item.class);
		List<Item> exists = business.itemFactory().listWithDocmentWithPath( document.getId(), paths );
		List<Item> currents = converter.disassemble(jsonElement, paths);
		List<Item> removes = converter.subtract(exists, currents);
		List<Item> adds = converter.subtract(currents, exists);
		
		if ((!removes.isEmpty()) || (!adds.isEmpty())) {
			business.entityManagerContainer().beginTransaction(Item.class);
			for (Item _o : removes) {
				business.entityManagerContainer().remove(_o);
			}
			for (Item _o : adds) {
				this.fill(_o, document);
				business.entityManagerContainer().persist(_o);
			}
			business.entityManagerContainer().commit();
		}
	}
	
	void fill(Item o, Document document) {
		/** DateItem과 Document를 같은 파티션에 배치 */
		o.setDistributeFactor(document.getDistributeFactor());
		o.setBundle(document.getId());
		o.setItemCategory(ItemCategory.cms);
	}
}
