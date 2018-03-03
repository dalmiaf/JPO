package br.com.jpo.metadata.entity.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import br.com.jpo.metadata.entity.EntityColumnMetadata;
import br.com.jpo.metadata.entity.EntityKeyMetadata;
import br.com.jpo.metadata.entity.EntityMetadata;


public class EntityMetadataImpl implements EntityMetadata {

	private static final long serialVersionUID = -1289898613067971963L;

	private String name;
	private String description;
	private EntityKeyMetadata entityKeyMetadata;
	private Map<String, EntityColumnMetadata> columnsMetadata;

	public EntityMetadataImpl() {
		this.columnsMetadata = new HashMap<String, EntityColumnMetadata>();
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
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public Map<String, EntityColumnMetadata> getEntityColumnsMetadata() {
		if (columnsMetadata == null) {
			columnsMetadata = new HashMap<String, EntityColumnMetadata>();
		}

		return columnsMetadata;
	}

	@Override
	public EntityColumnMetadata getEntityColumnMetadata(String columnName) {
		if (columnsMetadata.containsKey(columnName)) {
			return columnsMetadata.get(columnName);
		}

		return null;
	}

	@Override
	public void addEntityColumnMetadata(EntityColumnMetadata columnMetadata) {
		if (columnMetadata != null) {
			columnsMetadata.put(columnMetadata.getName(), columnMetadata);
		}
	}

	@Override
	public EntityKeyMetadata getEntityKeyMetadata() {
		return entityKeyMetadata;
	}

	@Override
	public void setEntityKeyMetadata(EntityKeyMetadata entityKeyMetadata) {
		this.entityKeyMetadata = entityKeyMetadata;
	}

	@Override
	public String toString() {
		StringBuilder toStringClass = new StringBuilder();
		StringBuilder toStringColumns = new StringBuilder();

		for (Iterator<Entry<String, EntityColumnMetadata>> iterator = getEntityColumnsMetadata().entrySet().iterator(); iterator.hasNext();) {
			Entry<String,EntityColumnMetadata> entry = iterator.next();
			EntityColumnMetadata columnMetadata = entry.getValue();

			toStringColumns.append(columnMetadata.toString());

			if (iterator.hasNext()) {
				toStringColumns.append("\n");
			}
		}

		toStringClass.append("EntityMetadata -> [ Name: "+getName()+", Description: "+getDescription()+" ]");
		toStringClass.append("\n");
		toStringClass.append(toStringColumns.toString());

		return toStringClass.toString();
	}
}
