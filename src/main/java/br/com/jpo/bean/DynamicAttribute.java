package br.com.jpo.bean;


public interface DynamicAttribute extends DynamicObject {

	Class<?> getType();

	void setType(Class<?> type);

	Object getValue();

	void setValue(Object value);

	boolean isPrimaryKey();

	void setPrimaryKey(boolean primaryKey);

	Integer getOrder();

	void setOrder(Integer order);

	String getDescription();

	void setDescription(String description);

	DynamicBean getDynamicBean();

	void setDynamicBean(DynamicBean dynamicBean);

	DynamicAttribute buildClone();
}