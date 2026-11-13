package com.x.program.init;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.tools.ZipTools;
import com.x.program.init.Missions.Mission;

public class MissionRestore implements Mission {
	private static final Logger logger = LoggerFactory.getLogger(MissionRestore.class);

	private String stamp;

	public String getStamp() {
		return stamp;
	}

	public void setStamp(String stamp) {
		this.stamp = stamp;
	}

	public static final String INIT_DATA_STAMP = "initData";

	@Override
	public void execute(Missions.Messages messages) {
		messages.head(MissionRestore.class.getSimpleName());
		try {
			messages.msg("executing");
			Path path = Config.path_local_temp(true).resolve(getStamp() + ".zip");
			if(!Files.exists(path)){
				path = Config.path_localSample(true).resolve(INIT_DATA_STAMP + ".zip");
				if(!Files.exists(path)) {
					return;
				}
			}
			if (!ZipTools.isZipFile(path)) {
				throw new ExceptionMissionExecute("file is not zip file format.");
			}
			logger.info("ready to init product data...");
			Path unzipFolder = Config.path_local_temp(true).resolve(getStamp());
			ZipTools.unZip(path.toFile(), null, unzipFolder.toFile(), true, StandardCharsets.UTF_8);
			if ((null == Config.externalDataSources().enable())
					|| BooleanUtils.isNotTrue(Config.externalDataSources().enable())) {
				Config.resource_commandQueue().add("start dataSkipInit");
				Config.resource_commandQueue().add("ctl -initResourceFactory");
				// 명령 큐는 멀티스레드로 실행되므로, 이후 ctl -initResourceFactory가 디렉터리를 조작하면 디렉터리 중복 삭제 충돌이 발생할 수 있음.
				Thread.sleep(5000);
			}
			if ((null == Config.externalStorageSources())
					|| BooleanUtils.isNotTrue(Config.externalStorageSources().getEnable())) {
				Config.resource_commandQueue().add("start storageSkipInit");
				// 명령 큐는 멀티스레드로 실행되므로, 이후 ctl -initResourceFactory가 디렉터리를 조작하면 디렉터리 중복 삭제 충돌이 발생할 수 있음.
				Thread.sleep(5000);
			}
			Optional<Path> folder = locationFolder(unzipFolder);
			if (folder.isPresent()) {
				Config.resource_commandQueue().add("ctl -rd " + folder.get().toAbsolutePath().toString());
				Config.resource_commandTerminatedSignal_ctl_rd().take();// 실행 완료 신호 대기.
				messages.msg("success");
			} else {
				messages.err("can not find catalog.json in folder:{}.", unzipFolder);
			}
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
			messages.err(ie.getMessage());
			throw new ExceptionMissionExecute(ie);
		} catch (Exception e) {
			messages.err(e.getMessage());
			throw new ExceptionMissionExecute(e);
		}
	}

	private static Optional<Path> locationFolder(Path path) throws IOException {
		PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:**/catalog.json");
		try (Stream<Path> stream = Files.walk(path, 10)) {
			Optional<Path> opt = stream.filter(pathMatcher::matches).findFirst();
			if (opt.isPresent()) {
				return Optional.of(opt.get().getParent().toAbsolutePath());
			}
		}
		return Optional.empty();
	}

}
