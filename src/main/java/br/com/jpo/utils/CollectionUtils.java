package br.com.jpo.utils;

import java.util.ArrayList;
import java.util.Collection;

public class CollectionUtils {

	public static boolean isEmpty(Collection<?> c) {
		return c == null || c.isEmpty();
	}

	public static boolean isNotEmpty(Collection<?> c) {
		return !isEmpty(c);
	}

	public static boolean contains(Collection<?> c, Object o) {
		return c != null && o != null && c.contains(o);
	}

	public static boolean notContains(Collection<?> c, Object o) {
		return !contains(c, o);
	}

	public static Collection<?> getEmptyAsNull(Collection<?> c) {
		if (c == null || c.isEmpty()) {
			return null;
		}

		return c;
	}

	public static Collection<?> getNullAsEmpty(Collection<?> c) {
		if (c == null) {
			return new ArrayList();
		}

		return c;
	}
}
