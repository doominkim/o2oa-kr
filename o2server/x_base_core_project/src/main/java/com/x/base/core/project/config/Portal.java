package com.x.base.core.project.config;

import java.io.File;
import java.util.LinkedHashMap;

import org.apache.commons.io.FileUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.BaseTools;
import com.x.base.core.project.tools.DefaultCharset;

public class Portal extends ConfigObject {

	private static final Boolean DEFAULT_WEBSOCKETENABLE = true;

	public static Portal defaultInstance() {
		return new Portal();
	}

	public Portal() {
		this.indexPage = new IndexPage();
		this.loginPage = new LoginPage();
		this.urlMapping = null;

	}

	@FieldDescribe("url 변환 설정.")
	@Deprecated(since = "7.2", forRemoval = true)
	private LinkedHashMap<String, String> urlMapping;

	@FieldDescribe("사용자 정의 홈 페이지 설정.")
	private IndexPage indexPage;

	public IndexPage getIndexPage() {
		if (null == indexPage) {
			this.indexPage = new IndexPage();
		}
		return this.indexPage;
	}

	@FieldDescribe("사용자 정의 로그인 페이지 설정.")
	private LoginPage loginPage;

	public LoginPage getLoginPage() {
		if (null == loginPage) {
			this.loginPage = new LoginPage();
		}
		return this.loginPage;
	}

	public void setLoginPage(LoginPage loginPage) {
		this.loginPage = loginPage;
	}

	public void setIndexPage(IndexPage indexPage) {
		this.indexPage = indexPage;
	}

	public static class LoginPage extends ConfigObject {

		public static LoginPage defaultInstance() {
			return new LoginPage();
		}

		public LoginPage() {
			this.enable = false;
			this.portal = "";
			this.page = "";
		}

		@FieldDescribe("사용자 정의 로그인 페이지 사용 여부.")
		private Boolean enable;
		@FieldDescribe("로그인 페이지가 속한 portal 지정, id, name, alias 사용 가능.")
		private String portal;
		@FieldDescribe("지정한 로그인 페이지, name, alias, id 사용 가능")
		private String page;

		public Boolean getEnable() {
			return enable;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}

		public String getPortal() {
			return portal;
		}

		public void setPortal(String portal) {
			this.portal = portal;
		}

		public String getPage() {
			return page;
		}

		public void setPage(String page) {
			this.page = page;
		}

	}

	public static class IndexPage extends ConfigObject {

		public static IndexPage defaultInstance() {
			return new IndexPage();
		}

		public IndexPage() {
			this.enable = false;
			this.portal = "";
			this.page = "";
		}

		@FieldDescribe("사용자 정의 홈 페이지 사용 여부.")
		private Boolean enable;
		@FieldDescribe("홈 페이지가 속한 portal 지정, id, name, alias 사용 가능.")
		private String portal;
		@FieldDescribe("지정한 홈 페이지, name, alias, id 사용 가능")
		private String page;

		public Boolean getEnable() {
			return enable;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}

		public String getPortal() {
			return portal;
		}

		public void setPortal(String portal) {
			this.portal = portal;
		}

		public String getPage() {
			return page;
		}

		public void setPage(String page) {
			this.page = page;
		}

	}

	public void save() throws Exception {
		File file = new File(Config.base(), Config.PATH_CONFIG_PORTAL);
		FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
		BaseTools.executeSyncFile(Config.PATH_CONFIG_PORTAL);
	}

	public LinkedHashMap<String, String> getUrlMapping() {
		return urlMapping;
	}

}