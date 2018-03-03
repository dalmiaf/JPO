package br.com.jpo.service.helper;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import br.com.jpo.bean.DynamicBean;
import br.com.jpo.dao.FinderCustom;
import br.com.jpo.dao.impl.FinderCustomImpl;
import br.com.jpo.metadata.entity.EntityColumnMetadata;
import br.com.jpo.metadata.entity.EntityReferenceMetadata;
import br.com.jpo.metadata.entity.InstanceMetadata;
import br.com.jpo.session.JPOSession;
import br.com.jpo.utils.JsonUtils;
import br.com.jpo.utils.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class DataSetHelper {

	private JPOSession session;

	public DataSetHelper(JPOSession session) {
		this.session = session;
	}

	public JsonObject getMetadata(JsonObject request) {
		DataSetConfig dataSetConfig = null;
		DataSet dataSet = null;

		try {
			dataSetConfig = createDataSetConfig(request);
			dataSet = createDataSet(dataSetConfig.getName());
			return getMetadata(dataSetConfig, dataSet);

		} catch(Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public JsonObject getLoadRecords(JsonObject request) {
		DataSetConfig dataSetConfig = null;
		DataSet dataSet = null;

		try {
			dataSetConfig = createDataSetConfig(request);
			dataSet = createDataSet(dataSetConfig.getName());
			return getLoadRecords(dataSetConfig, dataSet);

		} catch(Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private DataSet createDataSet(String name) throws Exception {
		if (StringUtils.getEmptyAsNull(name) != null) {
			return new DataSet(session, name);
		} else {
			throw new IllegalArgumentException("Não foi possível criar uma instância do DataSet com o nome ["+name+"].");
		}
	}

	private DataSetConfig createDataSetConfig(JsonObject request) {
		JsonObject dataSetJson = request.getAsJsonObject("dataSet");
		String name = dataSetJson.getAsJsonPrimitive("name").getAsString();

		DataSetConfig dataSet = new DataSetConfig();
		dataSet.setName(name);

		JsonArray entities = dataSetJson.getAsJsonArray("entityField");

		for (int i = 0; i < entities.size(); i++) {
			JsonObject entityField = entities.get(i).getAsJsonObject();
			JsonArray fields = entityField.getAsJsonArray("field");
			String path = entityField.getAsJsonPrimitive("path").getAsString();

			for (int j = 0; j < fields.size(); j++) {
				JsonObject field = fields.get(j).getAsJsonObject();
				String fieldName = field.getAsJsonPrimitive("name").getAsString();
				dataSet.addField(path, fieldName);
			}
		}

		return dataSet;
	}

	private JsonObject instanceMetadataToJson(DataSetConfig dataSetConfig, DataSet dataSet, boolean isLoadRecords) {
		if (dataSet == null || dataSetConfig == null) {
			throw new IllegalArgumentException("Inicialize o DataSet e o DataSetConfig corretamente.");
		}

		JsonObject result = new JsonObject();
		JsonObject instanceMetadataJson = new JsonObject();
		result.add("instanceMetadata", instanceMetadataJson);
		instanceMetadataJson.addProperty("name", dataSet.getName());
		instanceMetadataJson.addProperty("description", dataSet.getDescription());

		if (!dataSetConfig.isEmptyFields()) {
			if (isLoadRecords) {
				JsonArray entityColumnsMetadataJson = new JsonArray();
				instanceMetadataJson.add("entityColumnMetadata", entityColumnsMetadataJson);

				for (String field: dataSetConfig.getFieldsByAll()) {
					JsonObject entityColumnMetadataJson = null;

					String fieldName[] = field.split("\\.");
					String refName = fieldName[0];
					String columnName = fieldName[1];

					if (dataSet.getName().equals(refName)) {
						if ("*".equals(columnName)) {
							for (Entry<String, EntityColumnMetadata> entry: dataSet.getColumns().entrySet()) {
								entityColumnMetadataJson = new JsonObject();
								JsonUtils.addProperty(entityColumnMetadataJson, "name", entry.getValue().getName());
								JsonUtils.add(entityColumnsMetadataJson, entityColumnMetadataJson);
							}
						} else {
							entityColumnMetadataJson = new JsonObject();
							JsonUtils.addProperty(entityColumnMetadataJson, "name", fieldName[0]+"_"+fieldName[1]);
							JsonUtils.add(entityColumnsMetadataJson, entityColumnMetadataJson);
						}
					} else {
						if ("*".equals(columnName)) {
							for (EntityColumnMetadata columnMetadata: dataSet.getColumnsByRef(refName)) {
								entityColumnMetadataJson = new JsonObject();
								JsonUtils.addProperty(entityColumnMetadataJson, "name", refName+"_"+columnMetadata.getName());
								JsonUtils.add(entityColumnsMetadataJson, entityColumnMetadataJson);
							}
						} else {
							entityColumnMetadataJson = new JsonObject();
							JsonUtils.addProperty(entityColumnMetadataJson, "name", fieldName[0]+"_"+fieldName[1]);
							JsonUtils.add(entityColumnsMetadataJson, entityColumnMetadataJson);
						}
					}
				}
			} else {
				JsonArray entityColumnsMetadataJson = new JsonArray();
				instanceMetadataJson.add("entityColumnMetadata", entityColumnsMetadataJson);

				JsonArray entityReferencesMetadataJson = new JsonArray();
				instanceMetadataJson.add("entityReferenceMetadata", entityReferencesMetadataJson);

				for (Entry<String, Set<String>> entry: dataSetConfig.getFieldsByRef().entrySet()) {
					String path = entry.getKey();
					Set<String> columnsName = entry.getValue();

					if (dataSet.getName().equals(path)) {
						if (columnsName.size() == 1 && columnsName.iterator().next().equals("*")) {
							for (Entry<String, EntityColumnMetadata> column: dataSet.getColumns().entrySet()) {
								EntityColumnMetadata columnMetadata = column.getValue();
								JsonObject entityColumnMetadataJson = entityColumnMetadataToJson(columnMetadata);
								entityColumnsMetadataJson.add(entityColumnMetadataJson);
							}
						} else {
							for (String columnName: columnsName) {
								EntityColumnMetadata columnMetadata = dataSet.getColumn(columnName);
								JsonObject entityColumnMetadataJson = entityColumnMetadataToJson(columnMetadata);
								entityColumnsMetadataJson.add(entityColumnMetadataJson);
							}
						}
					} else {
						JsonObject entityReferenceMetadataJson = referenceMetadataToJson(dataSetConfig, dataSet, path, columnsName);
						entityReferencesMetadataJson.add(entityReferenceMetadataJson);
					}
				}
			}
		}

		return result;
	}

	private JsonObject entityColumnMetadataToJson(EntityColumnMetadata columnMetadata) {
		JsonObject entityColumnMetadataJson = new JsonObject();

		entityColumnMetadataJson.addProperty("name", columnMetadata.getName());
		entityColumnMetadataJson.addProperty("description", columnMetadata.getDescription());
		entityColumnMetadataJson.addProperty("length", columnMetadata.getLength());
		entityColumnMetadataJson.addProperty("precision", columnMetadata.getPrecision());
		entityColumnMetadataJson.addProperty("dataType", columnMetadata.getDataType());
		entityColumnMetadataJson.addProperty("sqlType", columnMetadata.getSqlType());
		entityColumnMetadataJson.addProperty("mask", StringUtils.getNullAsEmpty(columnMetadata.getMask()));

		return entityColumnMetadataJson;
	}

	private JsonObject referenceMetadataToJson(DataSetConfig dataSetConfig, DataSet dataSet, String referenceName, Set<String> columnsName) {
		JsonObject entityReferenceMetadataJson = new JsonObject();
		EntityReferenceMetadata referenceMetadata = dataSet.getReference(referenceName);
		InstanceMetadata instanceMetadata = dataSet.getInstanceMetadataByRef(referenceMetadata.getName());

		entityReferenceMetadataJson.addProperty("name", referenceMetadata.getName());
		entityReferenceMetadataJson.addProperty("description", referenceMetadata.getDescription());
		entityReferenceMetadataJson.addProperty("criterionRelationship", StringUtils.getNullAsEmpty(referenceMetadata.getCriterionRelationship()));
		entityReferenceMetadataJson.addProperty("relationType", String.valueOf(referenceMetadata.getRelationType()));
		entityReferenceMetadataJson.addProperty("fetchType", String.valueOf(referenceMetadata.getFetchType()));

		JsonArray entityColumnsMetadataJson = new JsonArray();
		entityReferenceMetadataJson.add("entityColumnMetadata", entityColumnsMetadataJson);

		if (columnsName.size() == 1 && columnsName.iterator().next().equals("*")) {
			for (Entry<String, EntityColumnMetadata> column: instanceMetadata.getEntityMetadata().getEntityColumnsMetadata().entrySet()) {
				EntityColumnMetadata columnMetadata = column.getValue();
				JsonObject entityColumnMetadataJson = entityColumnMetadataToJson(columnMetadata);
				entityColumnsMetadataJson.add(entityColumnMetadataJson);
			}
		} else {
			for (String columnName: columnsName) {
				EntityColumnMetadata columnMetadata = instanceMetadata.getEntityMetadata().getEntityColumnMetadata(columnName);
				JsonObject entityColumnMetadataJson = entityColumnMetadataToJson(columnMetadata);
				entityColumnsMetadataJson.add(entityColumnMetadataJson);
			}
		}

		return entityReferenceMetadataJson;
	}

	public JsonObject getMetadata(DataSetConfig dataSetConfig, DataSet dataSet) {
		JsonObject result = new JsonObject();

		JsonObject instanceMetadataJson = instanceMetadataToJson(dataSetConfig, dataSet, false);
		result.add("dataSet", instanceMetadataJson);

		return result;
	}

	private JsonObject getLoadRecords(DataSetConfig dataSetConfig, DataSet dataSet) throws Exception {
		JsonObject result = new JsonObject();

		if (!dataSetConfig.isEmptyFields()) {
			JsonObject instanceMetadataJson = instanceMetadataToJson(dataSetConfig, dataSet, true);
			result.add("dataSet", instanceMetadataJson);
			JsonArray records = new JsonArray();
			instanceMetadataJson.add("records", records);

			FinderCustom finder  = new FinderCustomImpl();
			finder.setInstanceName(dataSet.getName());

			Collection<DynamicBean> beans = session.findCustom(finder);

			for (DynamicBean bean: beans) {
				JsonObject record = new JsonObject();
				int i = -1;

				for (String field : dataSetConfig.getFieldsByAll()) {
					String fieldPath[] = field.split("\\.");
					String refName = fieldPath[0];
					String columnName = fieldPath[1];

					if (bean.getName().equals(refName)) {
						if ("*".equals(columnName)) {
							for (Entry<String, EntityColumnMetadata> entry : dataSet.getColumns().entrySet()) {
								JsonObject value = new JsonObject();
								JsonUtils.addProperty(value, "value", String.valueOf(bean.getAttribute(entry.getValue().getName())));
								JsonUtils.add(record, value, "f" + (++i));
							}
						} else {
							JsonObject value = new JsonObject();
							JsonUtils.addProperty(value, "value", String.valueOf(bean.getAttribute(columnName)));
							JsonUtils.add(record, value, "f" + (++i));
						}
					} else {
						DynamicBean beanRef = bean.asDynamicBean(refName);
						Object attValue = null;

						if ("*".equals(columnName)) {
							for (EntityColumnMetadata columnMetadata : dataSet.getColumnsByRef(refName)) {
								attValue = beanRef != null && beanRef.getAttribute(columnMetadata.getName()) != null ? beanRef.getAttribute(columnMetadata.getName()) : null;

								JsonObject value = new JsonObject();
								JsonUtils.addProperty(value, "value", StringUtils.getNullAsEmpty(attValue));
								JsonUtils.add(record, value, "f" + (++i));
							}
						} else {
							attValue = beanRef != null && beanRef.getAttribute(columnName) != null ? beanRef.getAttribute(columnName) : null;

							JsonObject value = new JsonObject();
							JsonUtils.addProperty(value, "value", StringUtils.getNullAsEmpty(attValue));
							JsonUtils.add(record, value, "f" + (++i));
						}
					}
				}

				JsonUtils.add(records, record);
			}
		}

		return result;
	}

	private class DataSetConfig {

		private String name;
		private Map<String, Set<String>> fieldsByRef;
		private Set<String> fieldsByAll;

		public DataSetConfig() {
			this.fieldsByRef = new HashMap<String, Set<String>>();
			this.fieldsByAll = new HashSet<String>();
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Map<String, Set<String>> getFieldsByRef() {
			return fieldsByRef;
		}

		public Set<String> getFieldsByAll() {
			return fieldsByAll;
		}

		private void addField(String path, String pattern) {
			addFieldByRef(path, pattern);
			addFieldByAll(path, pattern);
		}

		public void addFieldByRef(String path, String pattern) {
			Set<String> fieldList = fieldsByRef.get(path);

			if (fieldList == null) {
				fieldList = new HashSet<String>();
				fieldsByRef.put(path, fieldList);
			}

			fieldList.add(pattern);
		}

		public void addFieldByAll(String path, String pattern) {
			fieldsByAll.add(path+"."+pattern);
		}

		public boolean isEmptyFields() {
			return fieldsByRef == null || fieldsByRef.isEmpty();
		}
	}
}