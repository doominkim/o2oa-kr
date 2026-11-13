package com.x.program.init;

import com.x.base.core.project.tools.DefaultCharset;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.h2.tools.RunScript;

import com.google.gson.JsonObject;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.BaseTools;
import com.x.base.core.project.tools.H2Tools;
import com.x.program.init.Missions.Mission;

public class MissionSetSecret implements Mission {

	private String secret;

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	@Override
	public void execute(Missions.Messages messages) {
		messages.head(MissionSetSecret.class.getSimpleName());
		try {
			if (StringUtils.isBlank(getSecret())) {
				throw new IllegalArgumentException("비밀번호가 비어 있습니다");
			}
			messages.msg("executing");
			this.changeInternalDataServerPassword(Config.token().getPassword(), getSecret());
			this.changeTokenPassword(getSecret());
			Config.resource_commandQueue().add("ctl -flushConfig");
			Config.resource_commandQueue().add("ctl -initResourceFactory");
			Config.regenerate();
			// 명령 큐는 멀티스레드로 실행되므로, 이후 ctl -initResourceFactory가 디렉터리를 조작하면 디렉터리 중복 삭제 충돌이 발생할 수 있음.
			Thread.sleep(5000);
			messages.msg("success");
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		} catch (Exception e) {
			messages.err(e.getMessage());
			throw new ExceptionMissionExecute(e);
		}
	}

	private void changeInternalDataServerPassword(String oldPassword, String newPassword)
			throws IOException, URISyntaxException, SQLException {
		org.h2.Driver.load();
		Path path = Config.pathLocalRepositoryData(true).resolve(H2Tools.FILENAME_DATABASE);
		if (Files.exists(path)) {
			try (Connection conn = DriverManager.getConnection("jdbc:h2:"
					+ Config.pathLocalRepositoryData(true).resolve(H2Tools.DATABASE).toAbsolutePath().toString(),
					H2Tools.USER, oldPassword)) {
				RunScript.execute(conn,
						new StringReader("ALTER USER " + H2Tools.USER + " SET PASSWORD '" + newPassword + "'"));
			} catch (SQLException e) {
				throw new ExceptionMissionExecute("로컬 H2 데이터베이스 비밀번호 변경에 실패했습니다. 데이터베이스 파일이 손상되었거나 기존 비밀번호가 일치하지 않습니다.");
			}
		}
	}

	private void changeTokenPassword(String secret) throws Exception {
		Config.token().setPassword(secret);
		File file = new File(Config.base(), Config.PATH_CONFIG_TOKEN);
		FileUtils.write(file, XGsonBuilder.toJson(Config.token()), DefaultCharset.charset);
		Config.flush();
	}

	public static boolean check() {
		JsonObject jsonObject = BaseTools.readConfigObject(Config.PATH_CONFIG_TOKEN, JsonObject.class);
		String value = XGsonBuilder.extractString(jsonObject, "password");
		return StringUtils.isBlank(value);
	}

}
