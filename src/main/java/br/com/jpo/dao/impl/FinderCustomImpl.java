package br.com.jpo.dao.impl;

import java.util.Map;
import java.util.TreeMap;

import br.com.jpo.dao.FinderCustom;



public class FinderCustomImpl implements FinderCustom {

	private String 					instanceName;
	private int 					maxRows;
	private String 					where;
	private Map<String, Object> 	namedParameters;

	public FinderCustomImpl() {
		this.namedParameters = new TreeMap<String, Object>();
	}

	public FinderCustomImpl(String instanceName, String where, Map<String, Object> namedParameters) {
		this();
		this.instanceName = instanceName;
		this.where = where;
		this.namedParameters = namedParameters;
	}

	public FinderCustomImpl(String instanceName, String where, Map<String, Object> namedParameters, int maxRows) {
		this(instanceName, where, namedParameters);
		this.maxRows = maxRows;
	}

	@Override
	public String getInstanceName() {
		return instanceName;
	}

	@Override
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	@Override
	public int getMaxRows() {
		return maxRows;
	}

	@Override
	public void setMaxRows(int maxRows) {
		this.maxRows = maxRows;
	}

	@Override
	public void setWhere(String where) {
		this.where = where;
	}

	@Override
	public String getWhere() {
		return where;
	}

	@Override
	public Map<String, Object> getNamedParameters() {
		return namedParameters;
	}

	@Override
	public void addNamedParameters(String key, Object value) {
		namedParameters.put(key, value);
	}
}