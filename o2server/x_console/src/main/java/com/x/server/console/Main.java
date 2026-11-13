package com.x.server.console;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.eclipse.jetty.plus.jndi.Resource;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.server.console.log.Log4j2Configuration;

public class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	private static final String MANIFEST_FILENAME = "manifest.cfg";
	private static final String GITIGNORE_FILENAME = ".gitignore";
	private static final LinkedBlockingQueue<String> commandQueue = new LinkedBlockingQueue<>();
	private static NodeAgent nodeAgent;

	public static NodeAgent getNodeAgent() {
		return nodeAgent;
	}

	private static void init() throws Exception {
		String base = getBasePath().toString();
		pid(base);
		scanWar(base);
		cleanCommandSwap(base);
		cleanTempDir(base);
		createTempClassesDirectory(base);
		Log4j2Configuration.reconfigure();
		ResourceFactory.init();
		CommandFactory.printStartHelp();
		new Resource(Config.RESOURCE_COMMANDQUEUE, commandQueue);// 콘솔 명령 큐 등록, 명령 큐는 유일하며 변경 불가.
		// commandTerminatedSignal 블로킹 큐 등록
		new Resource(Config.RESOURCE_COMMANDTERMINATEDSIGNAL_CTL_RD, new LinkedBlockingQueue<>());
		// aiagent 시작
		new AiAgent(base).startIfExists();
	}

	public static void main(String[] args) throws Exception {
		init();
		if (null == Config.currentNode()) {
			throw new IllegalStateException("현재 노드를 찾을 수 없습니다. config/node_{name}.json과 local/node.cfg 파일의 이름이 일치하는지 확인하세요.");
		}
		CommandThreads.start(commandQueue);
		startNodeAgent();
		// 예약 작업 시작
		SchedulerBuilder.start();

		if (BooleanUtils.isTrue(Config.currentNode().autoStart())) {
			commandQueue.put("start");
		}
		CommandThreads.join();
	}

	private static void cleanCommandSwap(String base) {
		try (RandomAccessFile raf = new RandomAccessFile(new File(base, "command.swap"), "rw");
				FileChannel channel = raf.getChannel()) {
			FileLock lock = channel.lock(); // 파일 잠금
			channel.truncate(0); // 파일 비우기
			lock.release(); // 파일 잠금 해제
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}

	private static void startNodeAgent() throws Exception {
		if (BooleanUtils.isTrue(Config.currentNode().nodeAgentEnable())) {
			nodeAgent = new NodeAgent();
			nodeAgent.setCommandQueue(commandQueue);
			nodeAgent.setDaemon(true);
			nodeAgent.start();
		}
	}

	private static void createTempClassesDirectory(String base) throws IOException {
		File dir = new File(base, "local/temp/classes");
		FileUtils.forceMkdir(dir);
		FileUtils.cleanDirectory(dir);
	}

	/**
	 * store 디렉터리의 war 파일이 모두 manifest.cfg에 있는지 확인
	 *
	 * @param base o2server 루트 디렉터리
	 */
	private static void scanWar(String base) throws IOException {
		File dir = new File(base, "store");
		File manifest = new File(dir, MANIFEST_FILENAME);
		if ((!manifest.exists()) || manifest.isDirectory()) {
			LOGGER.print("시작 과정에서 store 디렉터리 스캔 생략.");
			return;
		}
		List<String> manifestNames = readManifest(manifest);
		for (File o : dir.listFiles()) {
			if ((o.isDirectory() && o.getName().equals("jars")) || (o.getName().equals(MANIFEST_FILENAME))
					|| (o.getName().equals(GITIGNORE_FILENAME))) {
				continue;
			}
			if (!manifestNames.contains(o.getName())) {
				LOGGER.print("store 스캔 중 잘못된 파일 삭제:{}.", o.getName());
				Files.delete(o.toPath());
			}
		}
	}

	/**
	 * Main.class가 있는 디렉터리부터 위로 재귀하여 version.o2가 있는 디렉터리를 찾으면 그것이 프로그램 루트 디렉터리.
	 *
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private static Path getBasePath() throws IOException, URISyntaxException {
		Path path = Paths
				.get(new URI("file://" + Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()));
		while (Files.exists(path)) {
			Path versionFile = path.resolve("version.o2");
			if (Files.exists(versionFile) && Files.isRegularFile(versionFile)) {
				return path.toAbsolutePath();
			}
			path = path.getParent();
		}
		throw new IOException("can not define o2server base directory.");
	}

	private static void cleanTempDir(String base) throws IOException {
		File temp = new File(base, "local/temp");
		FileUtils.forceMkdir(temp);
		FileUtils.cleanDirectory(temp);
	}

	private static List<String> readManifest(File file) throws IOException {
		List<String> list = new ArrayList<>();
		try (FileReader fileReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fileReader)) {
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				list.add(line);
			}
		}
		return list;
	}

	private static void pid(String base) throws IOException {
		RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
		String jvmName = runtimeBean.getName();
		long pid = Long.parseLong(jvmName.split("@")[0]);
		Path path = Paths.get(base, "pid.log");
		Files.write(path, Long.toString(pid).getBytes(), StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);
	}
}
