package br.com.jpo.sql.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import br.com.jpo.dao.EntityDAOContext;
import br.com.jpo.metadata.entity.EntityColumnMetadata;
import br.com.jpo.metadata.entity.EntityReferenceMetadata;
import br.com.jpo.metadata.entity.InstanceMetadata;
import br.com.jpo.metadata.entity.InstanceMetadataFactory;
import br.com.jpo.metadata.entity.InstanceMetadataNotFoudException;
import br.com.jpo.sql.SQLServiceProvider;
import br.com.jpo.utils.MapUtils;
import br.com.jpo.utils.StringUtils;

public class SQLServiceProviderImpl implements SQLServiceProvider {

	private InstanceMetadata instanceMetadata;
	private InstanceMetadataFactory instanceMetadataFactory;
	private boolean useNamedParameter;

	public SQLServiceProviderImpl(EntityDAOContext context) {
		instanceMetadata = context.getInstanceMetadata();
		instanceMetadataFactory = context.getSessionFactory().getInstanceMetadataFactory();
		useNamedParameter = true;
	}

	@Override
	public boolean isUseNamedParameter() {
		return useNamedParameter;
	}

	@Override
	public void setUseNamedParameter(boolean useNamedParameter) {
		this.useNamedParameter = useNamedParameter;
	}

	@Override
	public StringBuffer buildInsert() throws InstanceMetadataNotFoudException {
		throwMe();
		Map<String, EntityColumnMetadata> columnsMetadata = MapUtils.sortMapEntityColumnMetadata(instanceMetadata.getEntityMetadata().getEntityColumnsMetadata());

		StringBuffer query = new StringBuffer();
		StringBuffer fields = new StringBuffer();
		StringBuffer values = new StringBuffer();

		for (Iterator<Entry<String, EntityColumnMetadata>> iterator = columnsMetadata.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, EntityColumnMetadata> entry = iterator.next();
			EntityColumnMetadata columnMetadata = entry.getValue();

			fields.append(columnMetadata.getName());

			if (useNamedParameter) {
				values.append(":"+columnMetadata.getName());
			} else {
				values.append(PARAMETER);
			}

			if (iterator.hasNext()) {
				fields.append(SEPARATOR+" ");
				values.append(SEPARATOR+" ");
			}
		}

		query.append(INSERT_INTO);
		query.append(" "+instanceMetadata.getEntityMetadata().getName());
		query.append(" "+OPEN_PARENTHESES);
		query.append(" "+fields.toString());
		query.append(" "+CLOSE_PARENTHESES);
		query.append(" "+VALUES);
		query.append(" "+OPEN_PARENTHESES);
		query.append(" "+values.toString());
		query.append(" "+CLOSE_PARENTHESES+" ");

		return query;
	}

	@Override
	public StringBuffer buildUpdate(Map<String, EntityColumnMetadata> columnsMetadata) throws InstanceMetadataNotFoudException {
		return buildUpdateInternal(columnsMetadata);
	}

	@Override
	public StringBuffer buildUpdate() throws InstanceMetadataNotFoudException {
		Map<String, EntityColumnMetadata> columnsMetadata = MapUtils.sortMapEntityColumnMetadata(instanceMetadata.getEntityMetadata().getEntityColumnsMetadata());
		return buildUpdateInternal(columnsMetadata);
	}

	private StringBuffer buildUpdateInternal(Map<String, EntityColumnMetadata> columnsMetadata) throws InstanceMetadataNotFoudException {
		throwMe();

		StringBuffer query = new StringBuffer();
		StringBuffer fields = new StringBuffer();
		StringBuffer keys = new StringBuffer();

		for (Iterator<Entry<String, EntityColumnMetadata>> iterator = columnsMetadata.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, EntityColumnMetadata> entry = iterator.next();
			EntityColumnMetadata columnMetadata = entry.getValue();

			if (!columnMetadata.isPrimaryKey()) {
				if (!fields.toString().isEmpty()) {
					fields.append(SEPARATOR+" ");					
				}

				if (useNamedParameter) {
					fields.append(columnMetadata.getName()+" "+EQUAL+" :"+columnMetadata.getName());					
				} else {
					fields.append(columnMetadata.getName()+" "+EQUAL+" "+PARAMETER);
				}
			} else {
				if (!keys.toString().isEmpty()) {
					keys.append(" "+AND+" ");
				}

				if (useNamedParameter) {
					keys.append(columnMetadata.getName()+" "+EQUAL+" :"+columnMetadata.getName());
				} else {
					keys.append(columnMetadata.getName()+" "+EQUAL+" "+PARAMETER);
				}
			}
		}

		query.append(UPDATE);
		query.append(" "+instanceMetadata.getEntityMetadata().getName());
		query.append(" "+SET);
		query.append(" "+fields.toString());
		query.append(" "+WHERE);
		query.append(" "+keys.toString()+" ");

		return query;
	}

	@Override
	public StringBuffer buildDelete() throws InstanceMetadataNotFoudException {
		throwMe();
		Map<String, EntityColumnMetadata> columnsMetadata = MapUtils.sortMapEntityColumnMetadata(instanceMetadata.getEntityMetadata().getEntityColumnsMetadata());

		StringBuffer query = new StringBuffer();
		StringBuffer keys = new StringBuffer();

		for (Iterator<Entry<String, EntityColumnMetadata>> iterator = columnsMetadata.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, EntityColumnMetadata> entry = iterator.next();
			EntityColumnMetadata columnMetadata = entry.getValue();

			if (columnMetadata.isPrimaryKey())  {
				if (!keys.toString().isEmpty()) {
					keys.append(" "+AND+" ");
				}

				if (useNamedParameter) {
					keys.append(columnMetadata.getName()+" "+EQUAL+" :"+columnMetadata.getName());
				} else {
					keys.append(columnMetadata.getName()+" "+EQUAL+" "+PARAMETER);
				}
			}
		}

		query.append(DELETE);
		query.append(" "+FROM);
		query.append(" "+instanceMetadata.getEntityMetadata().getName());
		query.append(" "+WHERE);
		query.append(" "+keys.toString()+" ");

		return query;
	}

	@Override
	public StringBuffer buildSelectByPrimaryKey() throws InstanceMetadataNotFoudException {
		throwMe();
		Map<String, EntityColumnMetadata> columnsMetadata = MapUtils.sortMapEntityColumnMetadata(instanceMetadata.getEntityMetadata().getEntityColumnsMetadata());
		String alias = instanceMetadata.getEntityMetadata().getName();

		StringBuffer query = new StringBuffer();
		StringBuffer fields = new StringBuffer();
		StringBuffer keys = new StringBuffer();

		for (Iterator<Entry<String, EntityColumnMetadata>> iterator = columnsMetadata.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, EntityColumnMetadata> entry = iterator.next();
			EntityColumnMetadata columnMetadata = entry.getValue();

			fields.append(alias+"."+columnMetadata.getName());

			if (iterator.hasNext()) {
				fields.append(SEPARATOR+" ");
			}

			if (columnMetadata.isPrimaryKey())  {
				if (!keys.toString().isEmpty()) {
					keys.append(" "+AND+" ");
				}

				if (useNamedParameter) {
					keys.append(alias+"."+columnMetadata.getName()+" "+EQUAL+" :"+columnMetadata.getName());
				} else {
					keys.append(alias+"."+columnMetadata.getName()+" "+EQUAL+" "+PARAMETER);
				}
			}
		}

		query.append(SELECT);
		query.append(" "+fields.toString());
		query.append(" "+FROM);
		query.append(" "+instanceMetadata.getEntityMetadata().getName()+" "+alias);
		query.append(" "+WHERE);
		query.append(" "+keys.toString()+" ");

		if (instanceMetadata.getCriteria() != null) {
			query.append(" AND("+instanceMetadata.getCriteria()+") ");
		}

		return query;
	}

	/*@Override
	public StringBuilder buildSelectByAll() throws InstanceMetadataNotFoudException {
		throwMe();
		Map<String, EntityColumnMetadata> columnsMetadata = MapUtils.sortMapEntityColumnMetadata(instanceMetadata.getEntityMetadata().getEntityColumnsMetadata());
		String alias = instanceMetadata.getEntityMetadata().getName();

		StringBuilder query = new StringBuilder();
		StringBuilder fields = new StringBuilder();

		for (Iterator<Entry<String, EntityColumnMetadata>> iterator = columnsMetadata.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, EntityColumnMetadata> entry = iterator.next();
			EntityColumnMetadata columnMetadata = entry.getValue();

			fields.append(alias+"."+columnMetadata.getName());

			if (iterator.hasNext()) {
				fields.append(SEPARATOR+" ");
			}
		}

		query.append(SELECT);
		query.append(" "+fields.toString());
		query.append(" "+FROM);
		query.append(" "+instanceMetadata.getEntityMetadata().getName()+" "+alias);

		if (instanceMetadata.getCriteria() != null) {
			query.append(" "+WHERE);
			query.append(" "+instanceMetadata.getCriteria());
		}

		return query;
	}*/

	@Override
	public StringBuffer buildSelectByReference(String name) throws InstanceMetadataNotFoudException {
		throwMe();
		EntityReferenceMetadata entityReferenceMetadata = instanceMetadata.getEntityReferenceMetadata(name);
		StringBuffer query = new StringBuffer();

		if (entityReferenceMetadata != null) {
			InstanceMetadata instanceMetadataRef = getInstanceMetadata(name);
			String alias = instanceMetadata.getEntityMetadata().getName();

			Map<String, EntityColumnMetadata> columnsMetadata = MapUtils.sortMapEntityColumnMetadata(instanceMetadataRef.getEntityMetadata().getEntityColumnsMetadata());
			Map<String, String> fieldsConnectionRelationship = entityReferenceMetadata.getFieldsConnectionRelationship();

			StringBuilder fields = new StringBuilder();
			StringBuilder where = new StringBuilder();

			for (Iterator<Entry<String, EntityColumnMetadata>> iterator = columnsMetadata.entrySet().iterator(); iterator.hasNext();) {
				Entry<String,EntityColumnMetadata> entry = iterator.next();
				EntityColumnMetadata columnMetadata = entry.getValue();

				fields.append(alias+"."+columnMetadata.getName());

				if (iterator.hasNext()) {
					fields.append(SEPARATOR+" ");
				}
			}

			for (Iterator<Entry<String, String>> iterator = fieldsConnectionRelationship.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, String> entry = iterator.next();

				if (useNamedParameter) {
					where.append(alias+"."+entry.getKey()+" "+EQUAL+" :"+entry.getValue());
				} else {
					where.append(alias+"."+entry.getKey()+" "+EQUAL+" "+PARAMETER);
				}
			}

			query.append(SELECT);
			query.append(" "+fields.toString());
			query.append(" "+FROM);
			query.append(" "+instanceMetadataRef.getEntityMetadata().getName()+" "+alias);
			query.append(" "+WHERE);
			query.append(" "+where.toString()+" ");
		}

		return query;
	}

	/*@Override
	public StringBuilder buildSelectForceLoadEager() throws InstanceMetadataNotFoudException {
		throwMe();
		Map<String, EntityColumnMetadata> columnsMetadata = MapUtils.sortMapEntityColumnMetadata(instanceMetadata.getEntityMetadata().getEntityColumnsMetadata());
		Map<String, EntityReferenceMetadata> referencesMetadata = instanceMetadata.getEntityReferencesMetadata();

		String alias = instanceMetadata.getEntityMetadata().getName();
		StringBuilder query = new StringBuilder();
		StringBuilder fields = new StringBuilder();
		StringBuilder fieldsByReference = new StringBuilder();
		StringBuilder joinTables = new StringBuilder();

		for (Iterator<Entry<String, EntityColumnMetadata>> iterator = columnsMetadata.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, EntityColumnMetadata> entry = iterator.next();
			EntityColumnMetadata columnMetadata = entry.getValue();

			fields.append(alias+"."+columnMetadata.getName());

			if (iterator.hasNext()) {
				fields.append(SEPARATOR+" ");
			}
		}

		for (Iterator<Entry<String, EntityReferenceMetadata>> iterator = referencesMetadata.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, EntityReferenceMetadata> entry = iterator.next();
			EntityReferenceMetadata referenceMetadata = entry.getValue();

			if (RelationType.ONE_TO_ONE.equals(referenceMetadata.getRelationType()) || RelationType.MANY_TO_ONE.equals(referenceMetadata.getRelationType())) {
				InstanceMetadata instanceMetadataByReference = getInstanceMetadata(referenceMetadata.getName());
				Map<String, EntityColumnMetadata> columnsMetadataByReference = MapUtils.sortMapEntityColumnMetadata(instanceMetadataByReference.getEntityMetadata().getEntityColumnsMetadata());
				String aliasRef = instanceMetadataByReference.getEntityMetadata().getName();

				String join = null;

				if (referenceMetadata.isNullable()) {
					join = " LEFT JOIN ";
				} else {
					join = " INNER JOIN ";
				}

				StringBuilder fieldsConnection = new StringBuilder();

				for (Iterator<Entry<String, String>> iteratorFieldsCon = referenceMetadata.getFieldsConnectionRelationship().entrySet().iterator(); iteratorFieldsCon.hasNext();) {
					Entry<String, String> entryFieldsCon = iteratorFieldsCon.next();
					fieldsConnection.append("  ON " +alias+ "." +entryFieldsCon.getKey()+ " = " +aliasRef+ "." +entryFieldsCon.getValue());
				}

				joinTables.append(join).append(" " +aliasRef+ " " +aliasRef).append(fieldsConnection.toString());

				if (!columnsMetadataByReference.isEmpty()) {
					fieldsByReference.append(SEPARATOR+" ");					
				}

				for (Iterator<Entry<String, EntityColumnMetadata>> iteratorRef = columnsMetadataByReference.entrySet().iterator(); iteratorRef.hasNext();) {
					Entry<String, EntityColumnMetadata> entryRef = iteratorRef.next();
					EntityColumnMetadata columnMetadata = entryRef.getValue();

					fieldsByReference.append(aliasRef+"."+columnMetadata.getName());

					if (iteratorRef.hasNext()) {
						fieldsByReference.append(SEPARATOR+" ");
					}
				}
			}
		}

		query.append(SELECT);
		query.append(" "+fields.toString());
		query.append(" "+fieldsByReference.toString());
		query.append(" "+FROM);
		query.append(" "+alias+" "+alias);
		query.append(" "+joinTables.toString());

		if (instanceMetadata.getCriteria() != null) {
			query.append(" "+WHERE);
			query.append(" "+instanceMetadata.getCriteria());
		}

		return query;
	}*/

	@Override
	public StringBuffer buildSelectCustom(String where) throws InstanceMetadataNotFoudException {
		throwMe();
		Map<String, EntityColumnMetadata> columnsMetadata = MapUtils.sortMapEntityColumnMetadata(instanceMetadata.getEntityMetadata().getEntityColumnsMetadata());
		String alias = instanceMetadata.getEntityMetadata().getName();

		StringBuffer query = new StringBuffer();
		StringBuffer fields = new StringBuffer();

		for (Iterator<Entry<String, EntityColumnMetadata>> iterator = columnsMetadata.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, EntityColumnMetadata> entry = iterator.next();
			EntityColumnMetadata columnMetadata = entry.getValue();

			fields.append(alias+"."+columnMetadata.getName());

			if (iterator.hasNext()) {
				fields.append(SEPARATOR+" ");
			}
		}

		query.append(SELECT);
		query.append(" "+fields.toString());
		query.append(" "+FROM);
		query.append(" "+instanceMetadata.getEntityMetadata().getName()+" "+alias);

		if (where != null) {
			where = StringUtils.replaceString("this.", alias+".", where, true);
			query.append(" "+WHERE);
			query.append(" "+where+" ");
		}

		if (instanceMetadata.getCriteria() != null) {
			query.append(" AND("+instanceMetadata.getCriteria()+") ");
		}

		return query;
	}

	private InstanceMetadata getInstanceMetadata(String name) throws InstanceMetadataNotFoudException {
		try {
			return instanceMetadataFactory.create(name);
		} catch (Exception e) {
			throw new InstanceMetadataNotFoudException(e);
		}
	}

	private void throwMe() throws InstanceMetadataNotFoudException {
		if (instanceMetadata == null) {
			throw new InstanceMetadataNotFoudException();
		}
	}
}