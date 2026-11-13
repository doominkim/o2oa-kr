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
 * Translates server messages (exception text, prompts) to Korean using the
 * table in {@code /ko-messages.json} ({@code [{"src": ..., "ko": ...}]}).
 * Sources are the Chinese or English literals found in the server code.
 *
 * <p>
 * Lookup order: exact message, then templates with slf4j {@code {}} slots
 * (arguments are carried over in order). Chinese messages concatenated from
 * several literals additionally get known fragments replaced. English
 * messages only use exact and template matches with a long enough literal
 * part, so ordinary words inside identifiers are never rewritten. Korean or
 * unknown messages are returned unchanged.
 */
public final class KoreanMessages {

	private static final Pattern HAN = Pattern.compile("[\\u3400-\\u4dbf\\u4e00-\\u9fff\\uf900-\\ufaff]");
	private static final Pattern LATIN_WORDS = Pattern.compile("[A-Za-z]{2,}.*[A-Za-z]{2,}", Pattern.DOTALL);
	private static final String SLOT = "{}";
	/** English templates need this many literal characters, so "{}" or "{}: {}" never match everything. */
	private static final int MIN_ENGLISH_TEMPLATE_LITERAL = 8;

	private KoreanMessages() {
	}

	private static final class Table {
		private final Map<String, String> exact = new HashMap<>();
		private final List<Template> hanTemplates = new ArrayList<>();
		private final List<Template> englishTemplates = new ArrayList<>();
		private final List<String[]> fragments = new ArrayList<>();

		private Table(List<Entry> entries) {
			for (Entry e : entries) {
				if (e.src == null || e.ko == null || e.src.trim().isEmpty()) {
					continue;
				}
				boolean han = HAN.matcher(e.src).find();
				exact.putIfAbsent(e.src, e.ko);
				if (e.src.contains(SLOT)) {
					Template t = new Template(e.src, e.ko);
					if (han) {
						hanTemplates.add(t);
					} else if (t.literalLength >= MIN_ENGLISH_TEMPLATE_LITERAL) {
						englishTemplates.add(t);
					}
				} else if (han) {
					fragments.add(new String[] { e.src, e.ko });
				}
			}
			// Longer patterns first so specific messages win over generic ones.
			Comparator<Template> longestFirst = Comparator.comparingInt((Template t) -> t.literalLength).reversed();
			hanTemplates.sort(longestFirst);
			englishTemplates.sort(longestFirst);
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
		private String src;
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
		if (message == null) {
			return null;
		}
		boolean han = HAN.matcher(message).find();
		if (!han && !LATIN_WORDS.matcher(message).find()) {
			return message;
		}
		Table table = Holder.TABLE;
		String exact = table.exact.get(message);
		if (exact != null) {
			return exact;
		}
		for (Template t : han ? table.hanTemplates : table.englishTemplates) {
			String out = t.apply(message);
			if (out != null) {
				return out;
			}
		}
		if (!han) {
			return message;
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
