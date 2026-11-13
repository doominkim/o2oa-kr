package com.x.program.center.jaxrs.datastructure;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * 모든 앱의 엔티티 의존성 및 데이터 테이블 의존성 조회
 */
class ActionGetAllModuleStructure extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionGetAllModuleStructure.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, HttpServletRequest request) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		Wo wo = null;

		// 모든 모듈 순회
		for (String className : OFFICIAL_MODULE_SORTED_TEMPLATE) {
			wo = getEntityStructure(className);
			wos.add(wo);
		}
		result.setData(wos);
		return result;
	}

	/**
	 * 지정 모듈의 엔티티 의존성 정보 조회
	 * 
	 * @param className
	 * @return
	 */
	private Wo getEntityStructure(String className) {
		Wo wo = new Wo();
		List<WoTable> woTables = new ArrayList<>();
		wo.setModuleName(className);
		try {
			Class cls = Thread.currentThread().getContextClassLoader().loadClass(className);
			Class cls_annotation_moudle = Thread.currentThread().getContextClassLoader()
					.loadClass("com.x.base.core.project.annotation.Module");
			Method method_containerEntities = cls_annotation_moudle.getMethod("containerEntities");

			Annotation annotation = cls.getAnnotation(cls_annotation_moudle);
			Object result = null;
			if (annotation != null) {
				result = method_containerEntities.invoke(annotation);
				if (result != null) {
					String[] containerEntities = (String[]) result;
					if (containerEntities != null && containerEntities.length > 0) {
						for (String containerEntity : containerEntities) {
							woTables.add(getTableStructure(containerEntity));
						}
					}
				}
			}
		} catch (ClassNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		wo.setTables(woTables);
		return wo;
	}

	/**
	 * 지정 엔티티에 대응하는 테이블 이름 정보 조회
	 * 
	 * @param containerEntity
	 * @return
	 */
	private WoTable getTableStructure(String containerEntity) {
		WoTable woTable = new WoTable();
		woTable.setEntityName(containerEntity);
		try {
			Class cls_entity = Thread.currentThread().getContextClassLoader().loadClass(containerEntity);
			Class cls_annotation_table = Thread.currentThread().getContextClassLoader()
					.loadClass("javax.persistence.Table");
			Method cls_annotation_table_method_name = cls_annotation_table.getMethod("name");

			Annotation annotation_table = cls_entity.getAnnotation(cls_annotation_table);
			Object result = null;
			if (annotation_table != null) {
				result = cls_annotation_table_method_name.invoke(annotation_table);
				woTable.setTableName(result.toString());
			}
		} catch (ClassNotFoundException | NoSuchMethodException e) {
			logger.info("엔티티 클래스를 해석할 수 없습니다" + containerEntity + ", 클래스 의존성을 확인하십시오.");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return woTable;
	}

	public static class Wo {

		@FieldDescribe("앱 모듈 이름")
		private String moduleName;

		@FieldDescribe("데이터베이스 테이블 정보")
		private List<WoTable> tables;

		public String getModuleName() {
			return moduleName;
		}

		public void setModuleName(String moduleName) {
			this.moduleName = moduleName;
		}

		public List<WoTable> getTables() {
			return tables;
		}

		public void setTables(List<WoTable> tables) {
			this.tables = tables;
		}
	}

	public static class WoTable {

		@FieldDescribe("데이터베이스 테이블 이름")
		private String tableName;

		@FieldDescribe("엔티티 클래스 이름")
		private String entityName;

		public String getEntityName() {
			return entityName;
		}

		public void setEntityName(String entityName) {
			this.entityName = entityName;
		}

		public String getTableName() {
			return tableName;
		}

		public void setTableName(String tableName) {
			this.tableName = tableName;
		}
	}
}
