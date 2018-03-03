package br.com.jpo.dao.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import br.com.jpo.bean.DynamicBean;
import br.com.jpo.utils.StringUtils;

public class EntityDAOCache {

	private static EntityDAOCache dynamicBeanCache;
	private static Map<Integer, Object> cache = new HashMap<Integer, Object>();

	private EntityDAOCache() {
		
	}

	public static EntityDAOCache getInstance() {
		if (dynamicBeanCache == null) {
			dynamicBeanCache = new EntityDAOCache();
		}

		return dynamicBeanCache;
	}

	public void add(int uniqueID, Object obj) {
		cache.put(uniqueID, obj);
	}

	public void remove(int uniqueID) {
		cache.remove(uniqueID);
	}

	public void remove(DynamicBean bean) {
		for (Iterator<Entry<Integer, Object>> iterator = cache.entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, Object> entry = iterator.next();
			Object value = entry.getValue();

			if (value instanceof DynamicBean) {
				DynamicBean beanOnCache = (DynamicBean) value;

				if (bean.equals(beanOnCache)) {
					iterator.remove();
				}
			} else if (value instanceof Collection) {
				Collection<DynamicBean> beansOnChache = (Collection<DynamicBean>) value;

				for (DynamicBean beanOnCache: beansOnChache) {
					if (bean.equals(beanOnCache)) {
						iterator.remove();
						break;
					}
				}
			}
		}
	}

	public void invalidate() {
		cache.clear();
	}

	public Object get(int uniqueID) {
		return cache.get(uniqueID);
	}

	public DynamicBean getAsDynamicBean(int uniqueID) {
		return (DynamicBean) get(uniqueID);
	}

	public Collection<DynamicBean> getAsDynamicBeanCollection(int uniqueID) {
		return (Collection<DynamicBean>) get(uniqueID);
	}

	public boolean contains(int uniqueID) {
		return cache.containsKey(uniqueID);
	}

	public int getUniqueId(String name, String query, Map<String, Object> parameters) {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append(query);

		if (parameters != null) {
			for (Iterator<Entry<String, Object>> iterator = parameters.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, Object> entry = iterator.next();
				Object value = entry.getValue();

				sb.append(StringUtils.toStringOrEmpty(value));
			}
		}

		return sb.toString().hashCode();
	}
}