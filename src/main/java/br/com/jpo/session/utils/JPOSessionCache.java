package br.com.jpo.session.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import br.com.jpo.bean.DynamicBean;
import br.com.jpo.utils.MapUtils;
import br.com.jpo.utils.StringUtils;

public class JPOSessionCache {

	private static JPOSessionCache sessionCache;
	private static Map<Integer, DynamicBean> dynamicBeanCacheByFindByPrimaryKey = new HashMap<Integer, DynamicBean>();
	private static Map<Integer, DynamicBean> dynamicBeanCacheByfindCustom = new HashMap<Integer, DynamicBean>();

	private JPOSessionCache() {
		// singleton
	}

	public static JPOSessionCache getInstance() {
		if (sessionCache == null) {
			sessionCache = new JPOSessionCache();
		}

		return sessionCache;
	}

	public void add(DynamicBean dynamicBean) {
		dynamicBeanCacheByFindByPrimaryKey.put(getUniqueId(dynamicBean.getName(), dynamicBean.getPrimaryKeyAsValue()), dynamicBean);
	}

	public void remove(DynamicBean dynamicBean) {
		dynamicBeanCacheByFindByPrimaryKey.remove(getUniqueId(dynamicBean.getName(), dynamicBean.getPrimaryKeyAsValue()));
	}

	public DynamicBean get(String name, Map<String, Object> primaryKey) {
		return dynamicBeanCacheByFindByPrimaryKey.get(getUniqueId(name, primaryKey));
	}

	public boolean contains(String name, Map<String, Object> primaryKey) {
		return dynamicBeanCacheByFindByPrimaryKey.containsKey(getUniqueId(name, primaryKey));
	}

	public void invalidate() {
		dynamicBeanCacheByFindByPrimaryKey.clear();
	}

	private int getUniqueId(String name, Map<String, Object> primaryKey) {
		Map<String, Object> orderedMap = MapUtils.sortMapObject(primaryKey);

		StringBuilder sb = new StringBuilder();
		sb.append(name);

		for (Iterator<Entry<String, Object>> iterator = orderedMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, Object> entry = iterator.next();
			Object value = entry.getValue();

			sb.append(StringUtils.toStringOrEmpty(value));
		}

		return sb.toString().hashCode();
	}
}