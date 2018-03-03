package br.com.jpo.service.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import br.com.jpo.metadata.entity.EntityColumnMetadata;
import br.com.jpo.metadata.entity.EntityReferenceMetadata;
import br.com.jpo.metadata.entity.InitializeInstanceMetadataException;
import br.com.jpo.metadata.entity.InstanceMetadata;
import br.com.jpo.metadata.entity.InstanceMetadataFactory;
import br.com.jpo.session.JPOSession;

public class DataSet {

	private InstanceMetadata instanceMetadata;
	private InstanceMetadataFactory instanceMetadataFactory;
	private Map<String, EntityColumnMetadata> columns;
	private Map<String, EntityColumnMetadata> columnsByAll;
	private Map<String, Collection<EntityColumnMetadata>> columnsByRef;
	private Map<String, EntityReferenceMetadata> references;
	private Map<String, InstanceMetadata> instanceMetadataByRef;

	public DataSet(JPOSession session, String name) throws Exception {
		instanceMetadataFactory = session.getSessionFactory().getInstanceMetadataFactory();
		instanceMetadata = instanceMetadataFactory.create(name);
		columns = new HashMap<String, EntityColumnMetadata>();
		columnsByAll = new HashMap<String, EntityColumnMetadata>();
		columnsByRef = new HashMap<String, Collection<EntityColumnMetadata>>();
		references = new HashMap<String, EntityReferenceMetadata>();
		instanceMetadataByRef = new HashMap<String, InstanceMetadata>();
		initialize();
	}

	private void initialize() throws InitializeInstanceMetadataException {
		for (Iterator<Entry<String, EntityColumnMetadata>> iterator = instanceMetadata.getEntityMetadata().getEntityColumnsMetadata().entrySet().iterator(); iterator.hasNext();) {
			Entry<String, EntityColumnMetadata> entry = iterator.next();
			String columnName = entry.getKey();
			EntityColumnMetadata columnMetadata = entry.getValue();

			columns.put(columnName, columnMetadata);
			columnsByAll.put(instanceMetadata.getName() +"."+ columnName, columnMetadata);
		}

		for (Iterator<Entry<String, EntityReferenceMetadata>> iterator = instanceMetadata.getEntityReferencesMetadata().entrySet().iterator(); iterator.hasNext();) {
			Entry<String, EntityReferenceMetadata> entry = iterator.next();
			String referenceName = entry.getKey();
			EntityReferenceMetadata referenceMetadata = entry.getValue();

			InstanceMetadata insRefMetadata = instanceMetadataFactory.create(referenceName);
			instanceMetadataByRef.put(referenceName, insRefMetadata);
			references.put(referenceName, referenceMetadata);

			for (Iterator<Entry<String, EntityColumnMetadata>> itRef = insRefMetadata.getEntityMetadata().getEntityColumnsMetadata().entrySet().iterator(); itRef.hasNext();) {
				Entry<String, EntityColumnMetadata> entryRef = itRef.next();
				String columnName = entryRef.getKey();
				EntityColumnMetadata columnMetadata = entryRef.getValue();

				Collection<EntityColumnMetadata> colsByRef = columnsByRef.get(referenceName);

				if (colsByRef == null) {
					colsByRef = new ArrayList<EntityColumnMetadata>();
					columnsByRef.put(referenceName, colsByRef);
				}

				colsByRef.add(columnMetadata);
				columnsByAll.put(referenceName +"."+ columnName, columnMetadata);
			}
		}
	}

	public String getName() {
		return instanceMetadata.getName();
	}

	public String getDescription() {
		return instanceMetadata.getDescription();
	}

	public InstanceMetadata getInstanceMetadata() {
		return instanceMetadata;
	}

	public Map<String, EntityColumnMetadata> getColumns() {
		return columns;
	}

	public EntityColumnMetadata getColumn(String name) {
		return columns.get(name);
	}

	public Map<String, EntityColumnMetadata> getColumnsByAll() {
		return columnsByAll;
	}

	public EntityColumnMetadata getColumnByAll(String name) {
		return columnsByAll.get(name);
	}

	public Map<String, Collection<EntityColumnMetadata>> getColumnsByRef() {
		return columnsByRef;
	}

	public Collection<EntityColumnMetadata> getColumnsByRef(String name) {
		return columnsByRef.get(name);
	}

	public EntityColumnMetadata getColumnByRef(String referenceName, String columnName) {
		if (columnsByRef.containsKey(referenceName)) {
			for (EntityColumnMetadata columnMetadata: columnsByRef.get(referenceName)) {
				if (columnMetadata.getName().equals(columnName)) {
					return columnMetadata;
				}
			}
		}

		return null;
	}

	public Map<String, EntityReferenceMetadata> getReferences() {
		return references;
	}

	public EntityReferenceMetadata getReference(String name) {
		return references.get(name);
	}

	public Map<String, InstanceMetadata> getInstanceMetadataByRef() {
		return instanceMetadataByRef;
	}

	public InstanceMetadata getInstanceMetadataByRef(String name) {
		return instanceMetadataByRef.get(name);
	}
}