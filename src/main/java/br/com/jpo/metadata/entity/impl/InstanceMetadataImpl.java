package br.com.jpo.metadata.entity.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.com.jpo.metadata.entity.EntityMetadata;
import br.com.jpo.metadata.entity.EntityReferenceMetadata;
import br.com.jpo.metadata.entity.InstanceMetadata;
import br.com.jpo.metadata.entity.listener.InstanceListener;

public class InstanceMetadataImpl implements InstanceMetadata {

	private static final long serialVersionUID = -7393070860306297171L;

	private String name;
	private String description;
	private String criteria;
	private EntityMetadata entityMetadata;
	private Map<String, EntityReferenceMetadata> referencesMetadata;
	private List<InstanceListener> listeners;

	public InstanceMetadataImpl() {
		referencesMetadata = new HashMap<String, EntityReferenceMetadata>();
		listeners = new ArrayList<InstanceListener>();
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
	public String getCriteria() {
		return criteria;
	}

	@Override
	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	@Override
	public EntityMetadata getEntityMetadata() {
		return entityMetadata;
	}

	@Override
	public void setEntityMetadata(EntityMetadata entityMetadata) {
		this.entityMetadata = entityMetadata;
	}
	
	@Override
	public Map<String, EntityReferenceMetadata> getEntityReferencesMetadata() {
		if (referencesMetadata == null) {
			referencesMetadata = new HashMap<String, EntityReferenceMetadata>();
		}

		return referencesMetadata;
	}

	@Override
	public EntityReferenceMetadata getEntityReferenceMetadata(String entityReferenceName) {
		if (referencesMetadata.containsKey(entityReferenceName)) {
			return referencesMetadata.get(entityReferenceName);
		}

		return null;
	}

	@Override
	public void addEntityReferenceMetadata(EntityReferenceMetadata entityReferenceMetadata) {
		if (entityReferenceMetadata != null) {
			referencesMetadata.put(entityReferenceMetadata.getName(), entityReferenceMetadata);
		}
	}

	@Override
	public Collection<InstanceListener> getInstanceListeners() {
		return listeners;
	}

	@Override
	public void addInstanceListener(InstanceListener instanceListener) {
		listeners.add(instanceListener);
	}

	@Override
	public void removeInstanceListener(InstanceListener instanceListener) {
		listeners.remove(instanceListener);
	}

	@Override
	public String toString() {
		StringBuilder toStringClass = new StringBuilder();
		StringBuilder toStringEntity = new StringBuilder();
		StringBuilder toStringReferences = new StringBuilder();

		if (entityMetadata != null) {
			toStringEntity.append(entityMetadata.toString());
		}

		for (Iterator<Entry<String, EntityReferenceMetadata>> iterator = getEntityReferencesMetadata().entrySet().iterator(); iterator.hasNext();) {
			Entry<String,EntityReferenceMetadata> entry = iterator.next();
			EntityReferenceMetadata referenceMetadata = entry.getValue();

			toStringReferences.append(referenceMetadata.toString());

			if (iterator.hasNext()) {
				toStringReferences.append("\n");
			}
		}

		toStringClass.append("InstanceMetadata -> [Name: "+getName()+", Description: "+getDescription()+"]");
		toStringClass.append("\n");
		toStringClass.append(toStringEntity.toString());
		toStringClass.append("\n");
		toStringClass.append(toStringReferences);

		return toStringClass.toString();
	}
}