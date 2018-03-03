package br.com.jpo.utils;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import br.com.jpo.bean.DynamicAttribute;
import br.com.jpo.metadata.entity.EntityColumnMetadata;

public class MapUtils {

	public static Map<String, EntityColumnMetadata> sortMapEntityColumnMetadata(Map<String, EntityColumnMetadata> map) {
		Comparator<String> comparator = new Comparator<String>() {
			public int compare(String key1, String key2) {
				return key1.compareTo(key2);
			}
		};

		Map<String, EntityColumnMetadata> orderedMap = new TreeMap<String, EntityColumnMetadata>(comparator);
		orderedMap.putAll(map);

		return orderedMap;
	}

	public static Map<String, DynamicAttribute> sortMapDynamicAttribute(Map<String, DynamicAttribute> map) {
		Comparator<String> comparator = new Comparator<String>() {
			public int compare(String key1, String key2) {
				return key1.compareTo(key2);
			}
		};

		Map<String, DynamicAttribute> orderedMap = new TreeMap<String, DynamicAttribute>(comparator);
		orderedMap.putAll(map);

		return orderedMap;
	}

	public static Map<String, Object> sortMapObject(Map<String, Object> map) {
		Comparator<String> comparator = new Comparator<String>() {
			public int compare(String key1, String key2) {
				return key1.compareTo(key2);
			}
		};

		Map<String, Object> orderedMap = new TreeMap<String, Object>(comparator);
		orderedMap.putAll(map);

		return orderedMap;
	}
}
