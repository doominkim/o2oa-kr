package com.x.base.core.project.i18n;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Translates Chinese server messages (exception text, prompts) to Korean using
 * the table in {@code /ko-messages.json} ({@code [{"zh": ..., "ko": ...}]}).
 *
 * <p>
 * Lookup order: exact message, then templates with slf4j {@code {}} slots
 * (arguments are carried over in order), then replacement of known fragments
 * for messages concatenated from several literals. Messages without Chinese
 * characters are returned unchanged.
 */
public final class KoreanMessages {

	private static final Pattern HAN = Pattern.compile("[\\u3400-\\u4dbf\\u4e00-\\u9fff\\uf900-\\ufaff]");
	private static final String SLOT = "{}";

	private KoreanMessages() {
	}

	private static final class Table {
		private final Map<String, String> exact = new HashMap<>();
		private final List<Template> templates = new ArrayList<>();
		private final List<String[]> fragments = new ArrayList<>();

		private Table(List<Entry> entries) {
			for (Entry e : entries) {
				if (e.zh == null || e.ko == null || !HAN.matcher(e.zh).find()) {
					continue;
				}
				exact.putIfAbsent(e.zh, e.ko);
				if (e.zh.contains(SLOT)) {
					templates.add(new Template(e.zh, e.ko));
				} else if (e.zh.trim().length() > 0) {
					fragments.add(new String[] { e.zh, e.ko });
				}
			}
			// Longer patterns first so specific messages win over generic fragments.
			templates.sort(Comparator.comparingInt((Template t) -> t.literalLength).reversed());
			fragments.sort(Comparator.comparingInt((String[] f) -> f[0].length()).reversed());
		}
	}

	private static final class Template {
		private final Pattern pattern;
		private final String[] koParts;
		private final int literalLength;

		private Template(String zh, String ko) {
			String[] parts = zh.split(Pattern.quote(SLOT), -1);
			StringBuilder regex = new StringBuilder("^");
			int length = 0;
			for (int i = 0; i < parts.length; i++) {
				if (i > 0) {
					regex.append("(.*?)");
				}
				regex.append(Pattern.quote(parts[i]));
				length += parts[i].length();
			}
			this.pattern = Pattern.compile(regex.append('$').toString(), Pattern.DOTALL);
			this.koParts = ko.split(Pattern.quote(SLOT), -1);
			this.literalLength = length;
		}

		private String apply(String message) {
			Matcher m = pattern.matcher(message);
			if (!m.matches() || koParts.length != m.groupCount() + 1) {
				return null;
			}
			StringBuilder out = new StringBuilder(koParts[0]);
			for (int i = 1; i < koParts.length; i++) {
				out.append(m.group(i)).append(koParts[i]);
			}
			return out.toString();
		}
	}

	private static final class Entry {
		private String zh;
		private String ko;
	}

	private static final class Holder {
		private static final Table TABLE = load();

		private static Table load() {
			try (InputStream in = KoreanMessages.class.getResourceAsStream("/ko-messages.json")) {
				if (in == null) {
					return new Table(new ArrayList<>());
				}
				List<Entry> entries = new Gson().fromJson(new InputStreamReader(in, StandardCharsets.UTF_8),
						new TypeToken<List<Entry>>() {
						}.getType());
				return new Table(entries == null ? new ArrayList<>() : entries);
			} catch (Exception e) {
				return new Table(new ArrayList<>());
			}
		}
	}

	public static String translate(String message) {
		if (message == null || !HAN.matcher(message).find()) {
			return message;
		}
		Table table = Holder.TABLE;
		String exact = table.exact.get(message);
		if (exact != null) {
			return exact;
		}
		for (Template t : table.templates) {
			String out = t.apply(message);
			if (out != null) {
				return out;
			}
		}
		String out = message;
		for (String[] f : table.fragments) {
			if (out.contains(f[0])) {
				out = out.replace(f[0], f[1]);
				if (!HAN.matcher(out).find()) {
					break;
				}
			}
		}
		return out;
	}
}
