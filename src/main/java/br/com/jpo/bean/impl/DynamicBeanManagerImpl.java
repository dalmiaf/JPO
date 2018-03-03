package br.com.jpo.bean.impl;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import br.com.jpo.bean.DynamicAttribute;
import br.com.jpo.bean.DynamicBean;
import br.com.jpo.bean.DynamicBeanManager;
import br.com.jpo.bean.DynamicReference;
import br.com.jpo.bean.LoadDynamicBeanException;
import br.com.jpo.metadata.entity.EntityColumnMetadata;
import br.com.jpo.metadata.entity.EntityReferenceMetadata;
import br.com.jpo.metadata.entity.InstanceMetadata;
import br.com.jpo.session.JPOSessionFactory;
import br.com.jpo.utils.DataType;

public class DynamicBeanManagerImpl implements DynamicBeanManager {

	private static Map<String, DynamicBeanManager> managers = new HashMap<String, DynamicBeanManager>();
	private JPOSessionFactory sessionFactory;
	private InstanceMetadata instanceMetadata;

	private DynamicBeanManagerImpl(JPOSessionFactory sessionFactory, InstanceMetadata instanceMetadata) {
		this.sessionFactory = sessionFactory;
		this.instanceMetadata = instanceMetadata;
	}

	public static DynamicBeanManager configure(JPOSessionFactory sessionFactory, InstanceMetadata instanceMetadata) {
		DynamicBeanManager manager = managers.get(instanceMetadata.getName());

		if (manager == null) {
			manager = new DynamicBeanManagerImpl(sessionFactory, instanceMetadata);
			managers.put(instanceMetadata.getName(), manager);
		}

		return manager;
	}

	@Override
	public DynamicBean loadDefaultDynamicBean() throws LoadDynamicBeanException {
		DynamicBean bean = new DynamicBeanImpl();
		loadDynamicBeanWithInstanceMetadataInternal(bean);

		return bean;
	}

	@Override
	public DynamicBean loadDynamicBeanByInstanceMetadata(DynamicBean bean) throws LoadDynamicBeanException {
		loadDynamicBeanWithInstanceMetadataInternal(bean);

		return bean;
	}

	@Override
	public DynamicBean loadDynamicBeanByResultSet(ResultSet resultSet) throws LoadDynamicBeanException {
		DynamicBean bean = new DynamicBeanImpl();

		loadDynamicBeanWithInstanceMetadataInternal(bean);
		populateDynamicBeanInternal(resultSet, bean);

		return bean;
	}

	@Override
	public DynamicBean loadDynamicBeanByResultSet(ResultSet resultSet, DynamicBean bean) throws LoadDynamicBeanException {
		populateDynamicBeanInternal(resultSet, bean);

		return bean;
	}

	@Override
	public DynamicBean loadDynamicBeanByResultSet(ResultSet resultSet, DynamicBean bean, boolean useAlias) throws LoadDynamicBeanException {
		populateDynamicBeanInternal(resultSet, bean, useAlias);

		return bean;
	}

	@Override
	public DynamicAttribute loadDynamicAttributeByEntityColumnMetadata(EntityColumnMetadata columnMetadata) throws LoadDynamicBeanException {
		DynamicAttribute attribute = null;

		if (columnMetadata != null) {
			attribute = new DynamicAttributeImpl();
			loadDynamicAttributeByEntityColumnMetadata(attribute, columnMetadata);
		}

		return attribute;
	}

	@Override
	public DynamicAttribute loadDynamicAttributeByEntityColumnMetadata(DynamicAttribute attribute, EntityColumnMetadata columnMetadata)	throws LoadDynamicBeanException {
		if (attribute != null && columnMetadata != null) {
			attribute.setDescription(columnMetadata.getDescription());
			attribute.setName(columnMetadata.getName());
			attribute.setOrder(0);
			attribute.setPrimaryKey(columnMetadata.isPrimaryKey());
			attribute.setType(getType(columnMetadata.getDataType()));
			attribute.setValue(columnMetadata.getDefaultValue());
			attribute.setDynamicBean(null);
		}

		return attribute;
	}

	@Override
	public DynamicReference loadDynamicReferenceByEntityReferenceMetadata(EntityReferenceMetadata referenceMetadata) { 
		DynamicReference reference = null;

		if (referenceMetadata != null) {
			try {
				reference = new DynamicReferenceImpl(sessionFactory.getEntityDAOFactory());
			} catch(Exception e) {
				throw new IllegalStateException(e);
			}

			loadDynamicReferenceByEntityReferenceMetadata(reference, referenceMetadata);
		}

		return reference;
	}

	@Override
	public DynamicReference loadDynamicReferenceByEntityReferenceMetadata(DynamicReference reference, EntityReferenceMetadata referenceMetadata) {
		if (reference != null && referenceMetadata != null) {
			reference.setEntityReferenceMetadata(referenceMetadata);
			reference.setName(referenceMetadata.getName());
			reference.setBean(null);
		}

		return reference;
	}

	private void loadDynamicBeanWithInstanceMetadataInternal(DynamicBean bean) throws LoadDynamicBeanException {
		if (instanceMetadata != null && bean != null) {
			bean.setName(instanceMetadata.getName());
			bean.setInstanceMetadata(instanceMetadata);

			for (Entry<String, EntityColumnMetadata> entry: instanceMetadata.getEntityMetadata().getEntityColumnsMetadata().entrySet()) {
				EntityColumnMetadata columnMetadata = entry.getValue();

				DynamicAttribute attribute = loadDynamicAttributeByEntityColumnMetadata(columnMetadata);
				attribute.setDynamicBean(bean);

				bean.addAttribute(attribute);
			}

			for (Entry<String, EntityReferenceMetadata> entry: instanceMetadata.getEntityReferencesMetadata().entrySet()) {
				EntityReferenceMetadata entityReferenceMetadata = entry.getValue();

				DynamicReference reference = loadDynamicReferenceByEntityReferenceMetadata(entityReferenceMetadata);
				reference.setBean(bean);
				reference.setEntityReferenceMetadata(entityReferenceMetadata);

				bean.addDynamicReference(reference);
			}
		}
	}

	private void populateDynamicBeanInternal(ResultSet resultSet, DynamicBean bean) throws LoadDynamicBeanException {
		populateDynamicBeanInternal(resultSet, bean, false);
	}

	private void populateDynamicBeanInternal(ResultSet resultSet, DynamicBean bean, boolean useAlias) throws LoadDynamicBeanException {
		String alias = "";

		if (useAlias) {
			alias = instanceMetadata.getEntityMetadata().getName()+".";
		}

		for (Entry<String, EntityColumnMetadata> entry: instanceMetadata.getEntityMetadata().getEntityColumnsMetadata().entrySet()) {
			EntityColumnMetadata columnMetadata = entry.getValue();
			Object value = null;

			try {
				switch(columnMetadata.getDataType()) {
					case "BigDecimal":
						value = resultSet.getBigDecimal(alias+entry.getKey());
						break;
					case "Boolean":
						value = resultSet.getBoolean(alias+entry.getKey());
						break;
					case "Bytes":
						value = resultSet.getBytes(alias+entry.getKey());
						break;
					case "String":
						value = resultSet.getString(alias+entry.getKey());
						break;
					case "Timestamp":
						value = resultSet.getTimestamp(alias+entry.getKey());
						break;
					default:
						value = resultSet.getObject(alias+entry.getKey());
				}
			} catch (Exception  e) {
				throw new LoadDynamicBeanException("");
			}

			bean.setAttribute(entry.getKey(), value);
		}
	}

	private Class<Object> getType(String type) {
		if("BIGDECIMAL".equalsIgnoreCase(type)){
			return DataType.BIGDECIMAL.getClassType();
		} else if(DataType.BOOLEAN.getType().equalsIgnoreCase(type)) {
			return DataType.BOOLEAN.getClassType();
		} else if(DataType.INTEGER.getType().equalsIgnoreCase(type)) {
			return DataType.INTEGER.getClassType();
		} else if(DataType.COLLECTION.getType().equalsIgnoreCase(type)) {
			return DataType.COLLECTION.getClassType();
		} else if(DataType.DOUBLE.getType().equalsIgnoreCase(type)) {
			return DataType.DOUBLE.getClassType();
		} else if(DataType.DYNAMICBEAN.getType().equalsIgnoreCase(type)) {
			return DataType.DYNAMICBEAN.getClassType();
		} else if(DataType.FLOAT.getType().equalsIgnoreCase(type)) {
			return DataType.FLOAT.getClassType();
		} else if(DataType.LONG.getType().equalsIgnoreCase(type)) {
			return DataType.LONG.getClassType();
		} else if(DataType.STRING.getType().equalsIgnoreCase(type)) {
			return DataType.STRING.getClassType();
		} else {
			throw new IllegalArgumentException("O tipo de dado "+ type +" é desconhecido ou não é suportado pelo sistema.");
		}
	}
}