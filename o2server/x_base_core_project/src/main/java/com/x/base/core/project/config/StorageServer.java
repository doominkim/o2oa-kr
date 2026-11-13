package com.x.base.core.project.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.StorageProtocol;
import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.FieldDescribe;

public class StorageServer extends ConfigObject {

	private static final Integer default_port = 20040;
	private static final String default_passivePorts = "29000-30000";
	private static final String default_prefix = "";
	/** 2의 8제곱 이하 최대 소수 */
	private static final String default_name = "251";
	private static final Boolean default_deepPath = false;

	public static StorageServer defaultInstance() {
		return new StorageServer();
	}

	public StorageServer() {
		this.enable = true;
		this.port = default_port;
		this.sslEnable = false;
		this.name = default_name;
		// this.accounts = new CopyOnWriteArrayList<Account>();
		this.prefix = default_prefix;
		this.deepPath = default_deepPath;
	}

	@FieldDescribe("사용 여부, 첨부파일, 이미지 등 바이너리 스트림 파일을 별도 파일 서버에 저장, 여러 종류의 파일 서버 지원, 기본적으로 ftp 서버를 파일 서버로 사용, 각 노드에서 파일 서버를 하나씩 시작해 고성능 제공 가능.")
	private Boolean enable;
	@FieldDescribe("ftp 서버 포트, 이 포트는 외부에 개방하지 않아도 되며 ApplicationServer만 접근하고 사용자에게 직접 서비스를 제공하지 않음.")
	private Integer port;
	@FieldDescribe("ssl 전송 암호화 사용 여부, 사용 시 config/keystore 파일을 키 파일로 사용. config/token.json 파일의 sslKeyStorePassword 필드를 키 비밀번호로, sslKeyManagerPassword를 관리 비밀번호로 사용.")
	private Boolean sslEnable;
	@FieldDescribe("이름, 여러 노드 간 중복 불가, 기본값 251.")
	private String name;
//	@FieldDescribe("바이너리 스트림 파일은 여러 계정에 분할 저장되며, 분류별 저장 설정을 개별 지정 가능, 일반적으로 설정 불필요.")
//	private CopyOnWriteArrayList<Account> accounts;
	@FieldDescribe("ftp 전송은 능동과 수동으로 구분, 수동 전송 사용 시 수동 포트 범위 설정, 기본값 29000-30000.")
	private String passivePorts;
	@FieldDescribe("경로 접두사.")
	private String prefix;
	@FieldDescribe("더 깊은 경로 사용")
	private Boolean deepPath;

	public List<Account> getCalculatedAccounts() throws Exception {
		// if (ListTools.isEmpty(accounts)) {
		List<Account> accounts = new ArrayList<>();
		for (StorageType o : StorageType.values()) {
			Account account = new Account();
			account.setProtocol(StorageProtocol.ftp);
			account.setUsername(o.toString());
			account.setWeight(100);
			account.setPassword(Config.token().getPassword());
			accounts.add(account);
		}
		// }
		return accounts;
	}

	public String getPrefix() {
		return StringUtils.isBlank(this.prefix) ? default_prefix : this.prefix;
	}

	public Boolean getDeepPath() {
		return BooleanUtils.isTrue(this.deepPath);
	}

	public String getPassivePorts() {
		return StringUtils.isBlank(this.passivePorts) ? default_passivePorts : this.passivePorts;
	}

	public String getName() {
		return StringUtils.isBlank(this.name) ? default_name : this.name;
	}

	public class Account {

		private StorageProtocol protocol;

		private String username;

		private String password;

		private Integer weight;

		public Integer getWeight() {
			return weight;
		}

		public void setWeight(Integer weight) {
			this.weight = weight;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public StorageProtocol getProtocol() {
			return protocol;
		}

		public void setProtocol(StorageProtocol protocol) {
			this.protocol = protocol;
		}

	}

	public Integer getPort() {
		if (null != this.port && this.port > 0) {
			return this.port;
		}
		return default_port;
	}

	public Boolean getSslEnable() {
		return BooleanUtils.isTrue(this.sslEnable);
	}

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setSslEnable(Boolean sslEnable) {
		this.sslEnable = sslEnable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

//	public void setAccounts(CopyOnWriteArrayList<Account> accounts) {
//		this.accounts = accounts;
//	}
//
//	public CopyOnWriteArrayList<Account> getAccounts() {
//		if (null == this.accounts) {
//			return new CopyOnWriteArrayList<Account>();
//		}
//		return accounts;
//	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPassivePorts(String passivePorts) {
		this.passivePorts = passivePorts;
	}

}
