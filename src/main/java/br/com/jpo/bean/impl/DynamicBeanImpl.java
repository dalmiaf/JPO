package br.com.jpo.bean.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import br.com.jpo.bean.DynamicAttribute;
import br.com.jpo.bean.DynamicBean;
import br.com.jpo.bean.DynamicObject;
import br.com.jpo.bean.DynamicReference;
import br.com.jpo.metadata.entity.EntityColumnMetadata;
import br.com.jpo.metadata.entity.EntityReferenceMetadata;
import br.com.jpo.metadata.entity.InstanceMetadata;
import br.com.jpo.utils.BigDecimalUtils;

public class DynamicBeanImpl implements DynamicBean {

	private static final long serialVersionUID = 6364250855135826588L;

	private String name;
	private Map<String, Object> attributes;
	private Map<String, Object> references;
	private InstanceMetadata instanceMetadata;

	public DynamicBeanImpl() {
		this.attributes = new HashMap<String, Object>();
		this.references = new HashMap<String, Object>();
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
	public Map<String, DynamicAttribute> getPrimaryKeyAsDynamicAttribute() {
		if (attributes != null && !attributes.isEmpty()) {
			Map<String, DynamicAttribute> primaryKey = null;

			for (Iterator<Entry<String, Object>> iterator = attributes.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, Object> entry = iterator.next();
				Object obj = entry.getValue();

				if (obj instanceof DynamicAttribute) {
					DynamicAttribute attr = (DynamicAttribute) obj;

					if (primaryKey == null) {
						primaryKey = new HashMap<String, DynamicAttribute>();
					}

					if (attr.isPrimaryKey()) {
						primaryKey.put(attr.getName(), attr);
					}
				}
			}

			return primaryKey;
		}

		return null;
	}

	@Override
	public Map<String, Object> getPrimaryKeyAsValue() {
		if (attributes != null && !attributes.isEmpty()) {
			Map<String, Object> primaryKey = null;

			for (Iterator<Entry<String, Object>> iterator = attributes.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, Object> entry = iterator.next();
				Object obj = entry.getValue();

				if (obj instanceof DynamicAttribute) {
					DynamicAttribute attr = (DynamicAttribute) obj;

					if (primaryKey == null) {
						primaryKey = new HashMap<String, Object>();
					}

					if (attr.isPrimaryKey()) {
						primaryKey.put(attr.getName(), attr.getValue());
					}
				}
			}

			return primaryKey;
		}

		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAttribute(String attributeName) {
		Object obj = attributes.get(attributeName) != null ? attributes.get(attributeName) : references.get(attributeName);
		Object attribute = null;

		if (obj != null) {
			if (obj instanceof DynamicReference) {
				((DynamicReference)obj).loadReference();
				attribute = attributes.get(attributeName);
			} else if (obj instanceof DynamicBean) {
				attribute = ((DynamicBean) obj);
			} else if (obj instanceof Collection) {
				attribute = ((Collection) obj);
			} else if (obj instanceof DynamicAttribute) {
				attribute = ((DynamicAttribute) obj).getValue();
			}
		}

		return attribute;
	}

	@Override
	public void setAttribute(String attributeName, Object value) {
		Object obj = attributes.get(attributeName) != null ? attributes.get(attributeName) : references.get(attributeName);

		if (obj != null) {
			if (obj instanceof DynamicReference) {
				if (value instanceof DynamicReference) {
					references.put(attributeName, value);
				} else if (value instanceof DynamicBean) {
					attributes.put(attributeName, value);
				} else if (value instanceof Collection) {
					attributes.put(attributeName, value);
				} else {
					throw new IllegalArgumentException("Tipo de referência não foi mapeado corretamente.");
				}
			} else if (obj instanceof DynamicBean && value instanceof DynamicBean) {
				attributes.put(attributeName, value);
			} else if (obj instanceof DynamicAttribute) {
				DynamicAttribute attribute = (DynamicAttribute) obj;

				if (value == null || attribute.getType().isInstance(value)) {
					attribute.setValue(value);
				} else {
					throw new IllegalArgumentException("O atributo '"+ attribute.getName() +"' é do tipo '"+ attribute.getType() +"' e não pode receber um valor do tipo '"+ value.getClass() +"'.");
				}
			}
		} else {
			throw new IllegalArgumentException("O atributo '"+ attributeName +"' não foi encontrado neste DynamicBean.");
		}
	}

	@Override
	public void addAttribute(DynamicObject attribute) {
		attributes.put(attribute.getName(), attribute);
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public BigDecimal asBigDecimal(String attributeName) {
		return (BigDecimal) getAttribute(attributeName);
	}

	@Override
	public BigDecimal asBigDecimalOrZero(String attributeName) {
		return BigDecimalUtils.getBigDecimalOrZero((BigDecimal) getAttribute(attributeName));
	}

	@Override
	public Integer asInteger(String attributeName) {
		BigDecimal value = asBigDecimal(attributeName);
		return value != null ? value.intValue() : null;
	}

	@Override
	public String asString(String attributeName) {
		return (String) getAttribute(attributeName);
	}

	@Override
	public Long asLong(String attributeName) {
		BigDecimal value = asBigDecimal(attributeName);
		return value != null ? value.longValue() : null;
	}

	@Override
	public Double asDouble(String attributeName) {
		BigDecimal value = asBigDecimal(attributeName);
		return value != null ? value.doubleValue() : null;
	}

	@Override
	public Float asFloat(String attributeName) {
		BigDecimal value = asBigDecimal(attributeName);
		return value != null ? value.floatValue() : null;
	}

	@Override
	public Boolean asBoolean(String attributeName) {
		String s = asString(attributeName);

        return (s != null) && !s.toUpperCase().trim().equals("N");
	}

	@Override
	public Date asDate(String attributeName) {
		Timestamp ts = asTimestamp(attributeName);
		return ts != null ? new Date(ts.getTime()) : null;
	}

	@Override
	public Timestamp asTimestamp(String attributeName) {
		return (Timestamp) getAttribute(attributeName);
	}

	@Override
	public DynamicBean asDynamicBean(String referenceName) {
		return (DynamicBean) getAttribute(referenceName);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<DynamicBean> asCollection(String referenceName) {
		return (Collection<DynamicBean>) getAttribute(referenceName);
	}

	@Override
	public boolean containsAttribute(String attributeName) {
		return attributes.containsKey(attributeName);
	}

	@Override
	public boolean containsReference(String referenceName) {
		return references.containsKey(referenceName);
	}

	@Override
	public DynamicReference getDynamicReference(String referenceName) {
		return (DynamicReference) references.get(referenceName);
	}

	@Override
	public Map<String, Object> getDynamicReferences() {
		return references;
	}

	@Override
	public void addDynamicReference(DynamicReference reference) {
		references.put(reference.getName(), reference);
	}

	@Override
	public void removeDynamicReference(String referenceName) {
		references.remove(referenceName);
	}

	@Override
	public DynamicBean buildClone() {
		DynamicBean clone = null;

		clone = new DynamicBeanImpl();
		clone.setName(getName());
		clone.setInstanceMetadata(getInstanceMetadata());

		for (Entry<String, EntityColumnMetadata> entry: instanceMetadata.getEntityMetadata().getEntityColumnsMetadata().entrySet()) {
			EntityColumnMetadata columnMetadata = entry.getValue();
			Object attr = getAttribute(columnMetadata.getName());

			if (attr != null) {
				if (attr instanceof DynamicAttribute) {
					clone.addAttribute(((DynamicAttribute)attr).buildClone());
				}
			}
		}

		for (Entry<String, EntityReferenceMetadata> entry: instanceMetadata.getEntityReferencesMetadata().entrySet()) {
			EntityReferenceMetadata entityReferenceMetadata = entry.getValue();
			DynamicReference reference = getDynamicReference(entityReferenceMetadata.getName());

			if (reference != null) {
				clone.addDynamicReference(reference.buildClone());
			}
		}

		return clone;
	}

	@Override
	public InstanceMetadata getInstanceMetadata() {
		return instanceMetadata;
	}

	@Override
	public void setInstanceMetadata(InstanceMetadata instanceMetadata) {
		this.instanceMetadata = instanceMetadata;
	}

	@Override
	public boolean equals(Object obj) {
		boolean isEquals = obj != null;

		if (isEquals) {
			try {
				isEquals = obj.getClass().asSubclass(DynamicBean.class) != null;
			} catch (Exception ignored) {
				isEquals = false;
			}
		}

		if (isEquals) {
			isEquals = getName().equals(((DynamicBean) obj).getName());
		}

		if (isEquals) {
			DynamicBean bean = (DynamicBean) obj;

			Map<String, DynamicAttribute> primaryKey = getPrimaryKeyAsDynamicAttribute();
			Map<String, Object> primaryKeyAsValue = getPrimaryKeyAsValue();

			Map<String, DynamicAttribute> primaryKeyObj = bean.getPrimaryKeyAsDynamicAttribute();
			Map<String, Object> primaryKeyAsValueObj = bean.getPrimaryKeyAsValue();

			if (primaryKey.size() == primaryKeyObj.size()) {
				for (Iterator<Entry<String, DynamicAttribute>> iterator = primaryKey.entrySet().iterator(); iterator.hasNext();) {
					Entry<String, DynamicAttribute> entry = iterator.next();

					if (!primaryKeyObj.containsKey(entry.getKey())) {
						isEquals = false;
						break;
					}

					Object value = primaryKeyAsValue.get(entry.getKey());
					Object valueObj = primaryKeyAsValueObj.get(entry.getKey());

					if (value == null && valueObj == null) {
						isEquals = true;
					} else if (value != null && valueObj != null) {
						if (value.getClass().isAssignableFrom(valueObj.getClass())) {
							isEquals = value.equals(valueObj);
						}
					} else {
						isEquals = false;
					}

					if (!isEquals) {
						break;
					}
				}
			}
		}

		return isEquals;
	}

	@Override
	public String toString() {
		if (attributes != null && !attributes.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append(name+ "[");

			for (Iterator<Entry<String, Object>> iterator = attributes.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, Object> entry = iterator.next();
				Object obj = entry.getValue();

				if (obj instanceof DynamicAttribute) {
					DynamicAttribute attr = (DynamicAttribute) obj;

					sb.append(attr.getName()+": "+attr.getValue());

					if (iterator.hasNext()) {
						sb.append(", ");
					}
				}
			}

			sb.append("]");
			return sb.toString();
		}

		return super.toString();
	}
}