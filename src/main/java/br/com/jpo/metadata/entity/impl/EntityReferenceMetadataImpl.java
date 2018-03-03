package br.com.jpo.metadata.entity.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import br.com.jpo.metadata.entity.EntityReferenceMetadata;
import br.com.jpo.metadata.entity.InstanceMetadata;


public class EntityReferenceMetadataImpl implements EntityReferenceMetadata {

	private static final long serialVersionUID = -5213076944128692366L;

	private String name;
	private String tableName;
	private String description;
	private Collection<CascadeType> cascadeType;
	private RelationType relationType;
	private FetchType fetchType;
	private String criterionRelationship;
	private boolean nullable;
	private Map<String, String> fieldsConnectionRelationship;
	private InstanceMetadata instanceMetadata;

	public EntityReferenceMetadataImpl() {
		fieldsConnectionRelationship = new HashMap<String, String>();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String entityReferenceName) {
		this.name = entityReferenceName;
	}

	@Override
	public String getTableName() {
		return tableName;
	}

	@Override
	public void setTableName(String tableName) {
		this.tableName = tableName;
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
	public Collection<CascadeType> getCascadeType() {
		return cascadeType;
	}

	@Override
	public void setCascadeType(Collection<CascadeType> cascadeType) {
		this.cascadeType = cascadeType;
	}

	@Override
	public RelationType getRelationType() {
		return relationType;
	}

	@Override
	public void setRelationType(RelationType relationType) {
		this.relationType = relationType;
	}

	@Override
	public FetchType getFetchType() {
		return fetchType;
	}

	@Override
	public void setFetchType(FetchType fetchType) {
		this.fetchType = fetchType;
	}

	@Override
	public String getCriterionRelationship() {
		return criterionRelationship;
	}

	@Override
	public void setCriterionRelationship(String criterionRelationship) {
		this.criterionRelationship = criterionRelationship;
	}

	@Override
	public boolean isNullable() {
		return nullable;
	}

	@Override
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	@Override
	public Map<String, String> getFieldsConnectionRelationship() {
		return fieldsConnectionRelationship;
	}

	@Override
	public void addFieldConnectionRelationship(String nameFieldOrig, String nameFieldDest) {
		fieldsConnectionRelationship.put(nameFieldOrig, nameFieldDest);
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
	public String toString() {
		StringBuilder toStringClass = new StringBuilder();
		StringBuilder cascade = new StringBuilder();
		StringBuilder fieldsRelationship = new StringBuilder();

		if (cascadeType != null) {
			for (Iterator<CascadeType> iterator = cascadeType.iterator(); iterator.hasNext();) {
				CascadeType type = iterator.next();

				cascade.append(type);

				if (iterator.hasNext()) {
					cascade.append(", ");
				}

			}
		}

		for (Iterator<Entry<String, String>> iterator = fieldsConnectionRelationship.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, String> entry = iterator.next();
			fieldsRelationship.append(instanceMetadata.getName()+"."+entry.getKey()+" = "+getName()+"."+entry.getValue());

			if (iterator.hasNext()) {
				fieldsRelationship.append(", ");
			}
		}

		toStringClass.append("EntityReferenceMetadata -> "+getName()+" [ ");
		toStringClass.append("ENTITY_NAME = "+getTableName());
		toStringClass.append(", DESCRIPTION = "+getDescription());
		toStringClass.append(", NULLABLE = "+isNullable());
		toStringClass.append(", CRITERIO_RELATIONSHIP = "+getCriterionRelationship());
		toStringClass.append(", FETCH_TYPE = "+getFetchType());
		toStringClass.append(", RELATION_TYPE = "+getRelationType());
		toStringClass.append(", CASCADE_TYPE = {"+cascade.toString()+"}");
		toStringClass.append(", FIELDS_RELATIONSHIP = {"+fieldsRelationship.toString()+"}");
		toStringClass.append(" ]");

		return toStringClass.toString();
	}
}