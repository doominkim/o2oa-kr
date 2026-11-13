package com.x.base.core.project.build;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.openjpa.persistence.jdbc.ContainerTable;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.JsonProperties;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.Module;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

public class CheckCore {

	public static void main(String... args) throws Exception {
		try (ScanResult sr = new ClassGraph().disableJarScanning().enableAnnotationInfo().scan()) {
			List<ClassInfo> classInfos = sr.getClassesWithAnnotation(ContainerEntity.class.getName());
			List<Class<?>> classes = new ArrayList<>();
			for (ClassInfo info : classInfos) {
				classes.add(Thread.currentThread().getContextClassLoader().loadClass(info.getName()));
			}
			checkColumnName(classes);
			checkColumnLength(classes);
			checkLobIndex(classes);
			checkBooleanIndex(classes);
			checkListFieldContainerTableName(classes);
			checkFieldDescribeOnStatic(classes);
			checkTableNameUniqueConstraintName(classes);
			checkIdCreateTimeUpdateTimeSequenceIndex(classes);
			checkEnum(classes);
			checkDumpSize(classes);

		}
	}

	public static String packageName(String name) throws ClassNotFoundException {
		try (ScanResult scanResult = new ClassGraph().enableAnnotationInfo().scan()) {
			List<ClassInfo> classInfos = scanResult.getClassesWithAnnotation(Module.class.getName());
			for (ClassInfo info : classInfos) {
				if (StringUtils.equals(info.getSimpleName(), name)) {
					Class<?> cls = Thread.currentThread().getContextClassLoader().loadClass(info.getName());
					Module module = cls.getAnnotation(Module.class);
					return module.packageName();
				}
			}
		}
		return null;
	}

	/*
	 * 데이터베이스 필드명이 ColumnNamePrefix + fieldName인지 검사
	 */
	public static void checkColumnName(List<Class<?>> classes) {
		for (Class<?> cls : classes) {
			List<Field> fields = FieldUtils.getAllFieldsList(cls);
			for (Field field : fields) {
				Column col = field.getAnnotation(Column.class);
				if ((null != col) && (!StringUtils.equals(JpaObject.ColumnNamePrefix + field.getName(), col.name()))) {
					System.err.println(String.format("checkColumnName error: class: %s, field: %s.", cls.getName(),
							field.getName()));
				}
			}
		}
	}

	/*
	 * Lob 유형 필드에 인덱스를 추가했는지 검사
	 */
	public static void checkLobIndex(List<Class<?>> classes) {
		for (Class<?> cls : classes) {
			List<Field> fields = FieldUtils.getAllFieldsList(cls);
			for (Field field : fields) {
				Lob lob = field.getAnnotation(Lob.class);
				Index index = field.getAnnotation(Index.class);
				if ((null != lob) && (null != index)) {
					System.err.println(String.format("checkLobIndex error: class: %s, field: %s.", cls.getName(),
							field.getName()));
				}
			}
		}
	}

	/*
	 * Boolean 유형 필드에 인덱스를 추가했는지 검사
	 */
	public static void checkBooleanIndex(List<Class<?>> classes) {
		for (Class<?> cls : classes) {
			List<Field> fields = FieldUtils.getAllFieldsList(cls);
			for (Field field : fields) {
				if (Boolean.class.isAssignableFrom(field.getType())) {
					org.apache.openjpa.persistence.jdbc.Index index = field
							.getAnnotation(org.apache.openjpa.persistence.jdbc.Index.class);
					if (null != index) {
						System.err.println(String.format("checkBooleanIndex error: class: %s, field: %s.",
								cls.getName(), field.getName()));
					}
				}
			}
		}
	}

	public static void checkListFieldContainerTableName(List<Class<?>> classes) throws IllegalAccessException {
		for (Class<?> cls : classes) {
			List<Field> fields = FieldUtils.getAllFieldsList(cls);
			for (Field field : fields) {
				if (List.class.isAssignableFrom(field.getType())) {
					ContainerTable containerTable = field.getAnnotation(ContainerTable.class);
					if (null != containerTable) {
						String name = FieldUtils.readStaticField(cls, "TABLE", true).toString()
								+ JpaObject.ContainerTableNameMiddle + field.getName();
						if (!StringUtils.equals(name, containerTable.name())) {
							System.err.println(
									String.format("checkListFieldContainerTableName error: class: %s, field: %s.",
											cls.getName(), field.getName()));
						}
					}
				}
			}
		}
	}

	/*
	 * @FieldDescribe를 static 필드에 붙였는지 검사, 그렇다면 위아래 줄이 잘못된 것임
	 */
	public static void checkFieldDescribeOnStatic(List<Class<?>> classes) {
		for (Class<?> cls : classes) {
			List<Field> fields = FieldUtils.getFieldsListWithAnnotation(cls, FieldDescribe.class);
			for (Field field : fields) {
				if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
					System.err.println(String.format("checkFieldDescribeOnStatic error: class: %s, field: %s.",
							cls.getName(), field.getName()));
				}
			}
		}
	}

	/*
	 * 제약 조건 이름의 table 이름과 entity 클래스의 TABLE 이름이 일치하는지 검사
	 */
	public static void checkTableNameUniqueConstraintName(List<Class<?>> classes) throws IllegalAccessException {
		for (Class<?> cls : classes) {
			Table table = cls.getAnnotation(Table.class);
			String name = Objects.toString(FieldUtils.readStaticField(cls, "TABLE", true));
			if (!StringUtils.equals(table.name(), name)) {
				System.err.println("table name not match:" + cls);
			}
			for (UniqueConstraint u : table.uniqueConstraints()) {
				if (!StringUtils.startsWith(u.name(), table.name())) {
					System.err.println(
							String.format("checkTableNameUniqueConstraintName error: class: %s.", cls.getName()));
				}
			}
		}
	}

	/*
	 * 클래스에 createTime, updateTime, sequence 인덱스가 있는지 검사, 이 인덱스들은 이미 클래스의 제약 조건으로 지정됨
	 */
	public static void checkIdCreateTimeUpdateTimeSequenceIndex(List<Class<?>> classes) {
		for (Class<?> cls : classes) {
			Field idField = FieldUtils.getField(cls, JpaObject.id_FIELDNAME, true);
			Field createTimeField = FieldUtils.getField(cls, JpaObject.createTime_FIELDNAME, true);
			Field updateTimeField = FieldUtils.getField(cls, JpaObject.updateTime_FIELDNAME, true);
			Field sequenceField = FieldUtils.getField(cls, JpaObject.sequence_FIELDNAME, true);
			if ((null != idField.getAnnotation(Index.class)) || (null != createTimeField.getAnnotation(Index.class))
					|| (null != updateTimeField.getAnnotation(Index.class))
					|| (null != sequenceField.getAnnotation(Index.class))) {
				System.err.println(
						String.format("checkIdCreateTimeUpdateTimeSequenceIndex error: class: %s.", cls.getName()));
			}
		}
	}

	/*
	 * entity에 중복 필드가 있는지 검사
	 */
	public static void checkEnum(List<Class<?>> classes) {
		for (Class<?> cls : classes) {
			List<Field> fields = FieldUtils.getFieldsListWithAnnotation(cls, FieldDescribe.class);
			for (Field field : fields) {
				if (field.getType().isEnum()) {
					Enumerated enumerated = field.getAnnotation(Enumerated.class);
					Column column = field.getAnnotation(Column.class);
					if (null == enumerated || (!Objects.equals(EnumType.STRING, enumerated.value())) || (null == column)
							|| column.length() > 200) {
						System.err.println(String.format("checkEnum error: class: %s, field: %s.", cls.getName(),
								field.getName()));
					}
				}
			}
		}
	}

	/* String lob 외의 필드에 길이를 지정했는지 검사 */
	public static void checkColumnLength(List<Class<?>> classes) {
		for (Class<?> cls : classes) {
			List<Field> fields = FieldUtils.getFieldsListWithAnnotation(cls, Column.class);
			for (Field field : fields) {
				if ((!String.class.isAssignableFrom(field.getType())) && (!field.getType().isEnum())
						&& (!JsonProperties.class.isAssignableFrom(field.getType()))) {
					Lob lob = cls.getAnnotation(Lob.class);
					if (null != lob) {
						Column column = field.getAnnotation(Column.class);
						if (column.length() > 255) {
							System.err.println(String.format("checkColumnLength error: class: %s, field: %s.",
									cls.getName(), field.getName()));
						}
					}
				}
			}
		}
	}

	/* StorageObject의 dumpSize가 올바르게 설정되었는지 검사 */
	public static void checkDumpSize(List<Class<?>> classes) {
		for (Class<?> cls : classes) {
			if (StorageObject.class.isAssignableFrom(cls)) {
				ContainerEntity containerEntity = cls.getAnnotation(ContainerEntity.class);
				if (containerEntity.dumpSize() > 10) {
					System.err.println(String.format("checkDumpSize error: class: %s.", cls.getName()));
				}
			}
		}
	}

	public static void checkIdUnique(List<Class<?>> classes) {
		for (Class<?> cls : classes) {
			Field idField = FieldUtils.getField(cls, JpaObject.id_FIELDNAME, true);
			Column column = idField.getAnnotation(Column.class);
			if (BooleanUtils.isNotTrue(column.unique())) {
				System.err.println(String.format("checkIdUnique error: class: %s.", cls.getName()));
			}
		}
	}
}
