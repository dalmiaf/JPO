package br.com.jpo.dao;

import java.util.Map;

public interface FinderCustom {

	String getInstanceName();

	void setInstanceName(String instanceName);

	int getMaxRows();

	void setMaxRows(int maxRows);

	void setWhere(String where);

	String getWhere();

	Map<String, Object> getNamedParameters();

	void addNamedParameters(String key, Object value);
}