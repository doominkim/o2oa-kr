package com.x.server.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public final class AiAgent {

	private static final Logger logger = LoggerFactory.getLogger(AiAgent.class);

	/**
	 * 시작 단계 오류 출력 구간. 이 시간 동안 stderr는 메인 서버 console에 출력됨. 이 시간 이후에도 계속
	 * stderr를 읽지만 출력하지 않아 자식 프로세스 블로킹을 방지.
	 */
	private static final Duration STARTUP_ERROR_WINDOW = Duration.ofSeconds(30);

	private final String base;

	private Process process;

	public AiAgent(String base) {
		this.base = base;
		Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "aiagent-process-shutdown-hook"));
	}

	public synchronized void startIfExists() {
		if (process != null && process.isAlive()) {
			return;
		}

		Path dir = Paths.get(base, "servers/aiagent");
		Path javaCmd = resolveJavaCmd(dir);
		Path bootJar = dir.resolve("boot.jar");

		if (!Files.isDirectory(dir)) {
			return;
		}
		if (!Files.isRegularFile(javaCmd)) {
			return;
		}
		if (!Files.isRegularFile(bootJar)) {
			return;
		}

		try {
			Process p = buildProcess(dir, javaCmd, bootJar).start();
			process = p;

			startErrorReader(p, STARTUP_ERROR_WINDOW);

			p.onExit().thenRun(() -> {
				if (process == p) {
					process = null;
				}
			});

		} catch (IOException e) {
			logger.error(e);
		}
	}

	private void shutdown() {
		Process p = this.process;
		if (p == null) {
			return;
		}

		try {
			if (!p.isAlive()) {
				return;
			}

			p.destroy();

			if (!p.waitFor(3, TimeUnit.SECONDS) && p.isAlive()) {
				p.destroyForcibly();
				p.waitFor(3, TimeUnit.SECONDS);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			if (p.isAlive()) {
				p.destroyForcibly();
			}
		}
	}

	private Path resolveJavaCmd(Path dir) {
		String os = OsArchDetect.os();
		if (OsArchDetect.OS_WINDOWS_X64.equals(os)) {
			return dir.resolve("jvm").resolve(os).resolve("bin").resolve("java.exe");
		}
		return dir.resolve("jvm").resolve(os).resolve("bin").resolve("java");
	}

	private static ProcessBuilder buildProcess(Path dir, Path javaCmd, Path bootJar) {
		ProcessBuilder pb = new ProcessBuilder(javaCmd.toAbsolutePath().toString(), "--add-modules",
				"jdk.incubator.vector", "--enable-native-access=ALL-UNNAMED", "-Xmx2g",
				"-XX:NativeMemoryTracking=summary", "-jar", bootJar.toAbsolutePath().toString());

		pb.directory(dir.toFile());

		// 표준 출력 불필요
		pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);

		// 오류 출력은 PIPE로 유지해 부모 프로세스가 읽도록 함
		pb.redirectError(ProcessBuilder.Redirect.PIPE);

		return pb;
	}

	private static void startErrorReader(Process process, Duration startupWindow) {
		final long deadlineNanos = System.nanoTime() + startupWindow.toNanos();

		Thread t = new Thread(() -> {
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {

				String line;
				while ((line = reader.readLine()) != null) {
					if (System.nanoTime() <= deadlineNanos) {
						System.err.println("[aiagent] " + line);
					}
					// 시작 구간 이후에는 출력하지 않지만 stderr 막힘을 피하려고 계속 읽음
				}
			} catch (IOException e) {
				// 보통 프로세스 종료나 스트림 닫힘 시 발생하므로 오류로 격상할 필요 없음
			}
		}, "aiagent-stderr-reader");
		t.setDaemon(true);
		t.start();
	}
}