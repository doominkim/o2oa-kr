package com.x.processplatform.service.processing;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.graalvm.polyglot.Source;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.Applications;
import com.x.base.core.project.Context;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.WrapInteger;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.core.express.Organization;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.service.processing.processor.AeiObjects;

/**
 * 프로세스 일련번호 생성
 * 
 * @author sword
 */
public class SerialBuilder {

	private static final String EMPTY_SYMBOL = "(0)";

	private Context context;

	public SerialBuilder(Context context, EntityManagerContainer emc, String processId, String workId)
			throws Exception {
		this.context = context;
		process = emc.find(processId, Process.class);
		if (null == process) {
			throw new ExceptionEntityNotExist(processId, Process.class);
		}
		work = emc.find(workId, Work.class);
		if (null == work) {
			throw new ExceptionEntityNotExist(workId, Work.class);
		}
		serial = new Serial();
		this.date = new Date();
	}

	private Process process;

	private Work work;

	private Date date;

	public Serial serial;

	List<Object> itemResults = new ArrayList<>();

	private Type collectionType = new TypeToken<ArrayList<SerialTextureItem>>() {
	}.getType();

	public String concrete(AeiObjects aeiObjects) throws Exception {
		StringBuilder stringBuilder = new StringBuilder("");
		String data = process.getSerialTexture();
		if (StringUtils.isNotEmpty(data)) {
			List<SerialTextureItem> list = XGsonBuilder.instance().fromJson(data, collectionType);
			if (!list.isEmpty()) {
				GraalvmScriptingFactory.Bindings bindings = aeiObjects.bindings()
						.putMember(GraalvmScriptingFactory.BINDING_NAME_SERIAL, this.serial)
						.putMember(GraalvmScriptingFactory.BINDING_NAME_PROCESS, this.process);
				for (SerialTextureItem o : list) {
					if ((!StringUtils.equalsIgnoreCase(o.getKey(), "number"))
							&& StringUtils.isNotEmpty(o.getScript())) {
						Source source = GraalvmScriptingFactory.functionalization(o.getScript());
						Optional<String> opt = GraalvmScriptingFactory.evalAsString(source, bindings);
						String s = opt.isPresent() ? opt.get() : "";
						itemResults.add(s);
					} else {
						itemResults.add("");
					}
				}
				for (int i = 0; i < list.size(); i++) {
					SerialTextureItem o = list.get(i);
					if ((StringUtils.equalsIgnoreCase(o.getKey(), "number")) && StringUtils.isNotEmpty(o.getScript())) {
						Source source = GraalvmScriptingFactory.functionalization(o.getScript());
						Optional<String> opt = GraalvmScriptingFactory.evalAsString(source, bindings);
						String s = opt.isPresent() ? opt.get() : "";
						itemResults.add(i, s);
					}
				}
				for (Object o : itemResults) {
					stringBuilder.append(Objects.toString(o, ""));
				}
				return stringBuilder.toString();
			}
		}
		return stringBuilder.toString();
	}

	public class Serial {

		// 텍스트 입력, 텍스트 반환
		public String text(String str) {
			return str;
		}

		// format 날짜 형식 입력, 현재 시간의 연도 부분을 지정 형식으로 반환
		public String year(String format) {
			return DateFormatUtils.format(date, format);
		}

		// format 날짜 형식 입력, work 생성 시간의 연도 부분을 지정 형식으로 반환
		public String createYear(String format) {
			return DateFormatUtils.format(work.getCreateTime(), format);
		}

		// 현재 시간의 월 부분을 지정 형식으로 반환
		public String month(String format) {
			return DateFormatUtils.format(date, format);
		}

		// 생성 시간의 월 부분을 지정 형식으로 반환
		public String createMonth(String format) {
			return DateFormatUtils.format(work.getCreateTime(), format);
		}

		// 현재 시간의 일 부분을 지정 형식으로 반환
		public String day(String format) {
			return DateFormatUtils.format(date, format);
		}

		// 생성 시간의 일 부분을 지정 형식으로 반환
		public String createDay(String format) {
			return DateFormatUtils.format(work.getCreateTime(), format);
		}

		// 생성 신분 소속 조직 반환
		public String unit() {
			return work.getCreatorUnit();
		}

		// 생성 사용자 반환
		public String person() {
			return work.getCreatorPerson();
		}

		// 생성 신분 반환
		public String identity() {
			return work.getCreatorIdentity();
		}

		// 생성 조직의 지정 조직 속성 반환
		public String unitAttribute(String name) throws Exception {
			String result = "";
			Organization org = new Organization(context);
			List<String> attributes = org.unitAttribute().listAttributeWithUnitWithName(work.getCreatorUnit(), name);
			if (ListTools.isNotEmpty(attributes)) {
				result = StringUtils.join(attributes, ",");
			}
			return result;
		}

		// 생성 사용자의 지정 속성 반환
		public String personAttribute(String name) throws Exception {
			String result = "";
			Organization org = new Organization(context);
			List<String> attributes = org.personAttribute().listAttributeWithPersonWithName(work.getCreatorPerson(),
					name);
			if (ListTools.isNotEmpty(attributes)) {
				result = StringUtils.join(attributes, ",");
			}
			return result;
		}

		// 다음 번호 값 조회, 입력 keys는 번호 기준, size는 반환 길이, 부족하면 0으로 채움
		public String nextSerialNumber(List<Integer> keys, Integer size) throws Exception {
			String name = "";
			for (Integer i : keys) {
				name += itemResults.get(i).toString();
			}
			Integer number = this.nextNumber(name);
			if (size > 0) {
				return String.format("%0" + size + "d", number);
			} else {
				return number.toString();
			}
		}

		private Integer nextNumber(String name) throws Exception {
			if (StringUtils.isBlank(name)) {
				name = EMPTY_SYMBOL;
			}
			WrapInteger wrapInteger = ThisApplication.context().applications()
					.postQuery(x_processplatform_service_processing.class,
							Applications.joinQueryUri("work", "process", process.getId(), "name", name, "serial"), null,
							process.getApplication())
					.getData(WrapInteger.class);
			return wrapInteger.getValue();
		}
	}

	public class SerialTextureItem {

		private String key;
		private String script;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getScript() {
			return script;
		}

		public void setScript(String script) {
			this.script = script;
		}
	}
}
