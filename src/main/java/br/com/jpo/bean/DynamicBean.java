package br.com.jpo.bean;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import br.com.jpo.metadata.entity.InstanceMetadata;


public interface DynamicBean extends DynamicObject {

	Map<String, DynamicAttribute> getPrimaryKeyAsDynamicAttribute();

	Map<String, Object> getPrimaryKeyAsValue();

	Object getAttribute(String attributeName);

	void setAttribute(String attributeName, Object attributeValue);

	void addAttribute(DynamicObject attribute);

	Map<String, Object> getAttributes();

	BigDecimal asBigDecimal(String attributeName);

	BigDecimal asBigDecimalOrZero(String attributeName);

	Integer asInteger(String attributeName);

	String asString(String attributeName);

	Long asLong(String attributeName);

	Double asDouble(String attributeName);

	Float asFloat(String attributeName);

	Boolean asBoolean(String attributeName);

	Date asDate(String attributeName);

	Timestamp asTimestamp(String attributeName);

	DynamicBean asDynamicBean(String referenceName);

	Collection<DynamicBean> asCollection(String referenceName);

	boolean containsAttribute(String attributeName);

	boolean containsReference(String referenceName);

	DynamicReference getDynamicReference(String referenceName);

	Map<String, Object> getDynamicReferences();

	void addDynamicReference(DynamicReference reference);

	void removeDynamicReference(String referenceName);

	DynamicBean buildClone();

	boolean equals(Object obj);

	InstanceMetadata getInstanceMetadata();

	void setInstanceMetadata(InstanceMetadata instanceMetadata);
}