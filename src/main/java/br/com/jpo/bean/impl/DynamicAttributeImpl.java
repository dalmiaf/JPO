package br.com.jpo.bean.impl;

import br.com.jpo.bean.DynamicAttribute;
import br.com.jpo.bean.DynamicBean;

public class DynamicAttributeImpl implements DynamicAttribute {

	private static final long serialVersionUID = 6250146834527945699L;

	private String name;
	private Class<?> type;
	private Object value;
	private boolean primaryKey;
	private Integer order;
	private String description;
	private DynamicBean dynamicBean;

	public DynamicAttributeImpl() {
		
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Class<?> getType() {
		return type;
	}

	@Override
	public void setType(Class<?> type) {
		this.type = type;
		validateValueOrType();
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void setValue(Object value) {
		this.value = value;
		validateValueOrType();
	}

	@Override
	public boolean isPrimaryKey() {
		return primaryKey;
	}

	@Override
	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	@Override
	public Integer getOrder() {
		return order;
	}

	@Override
	public void setOrder(Integer order) {
		this.order = order;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public DynamicBean getDynamicBean() {
		return dynamicBean;
	}

	@Override
	public void setDynamicBean(DynamicBean dynamicBean) {
		this.dynamicBean = dynamicBean;
	}

	@Override
	public DynamicAttribute buildClone() {
		DynamicAttribute clone = null;

		clone = new DynamicAttributeImpl();

		clone.setDescription(getDescription());
		clone.setName(getName());
		clone.setOrder(0);
		clone.setPrimaryKey(isPrimaryKey());
		clone.setType(getType());
		clone.setValue(getValue());

		if (getDynamicBean() != null) {
			clone.setDynamicBean(getDynamicBean().buildClone());
		}

		return clone;
	}

	private void validateValueOrType(){
		if(type != null && value != null && !type.isAssignableFrom(value.getClass())){
			throw new ClassCastException("Tipos de dados incompatíveis "+ type +" / "+ value.getClass());
		}
	}
}
