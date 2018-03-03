package br.com.jpo.metadata.entity.impl;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import br.com.jpo.connection.impl.JdbcWrapper;
import br.com.jpo.dao.KeyGenerator;
import br.com.jpo.metadata.entity.EntityColumnMetadata;
import br.com.jpo.metadata.entity.EntityKeyMetadata;
import br.com.jpo.metadata.entity.EntityMetadata;
import br.com.jpo.metadata.entity.EntityReferenceMetadata;
import br.com.jpo.metadata.entity.EntityReferenceMetadata.CascadeType;
import br.com.jpo.metadata.entity.EntityReferenceMetadata.FetchType;
import br.com.jpo.metadata.entity.EntityReferenceMetadata.RelationType;
import br.com.jpo.metadata.entity.InitializeInstanceMetadataException;
import br.com.jpo.metadata.entity.InstanceMetadata;
import br.com.jpo.metadata.entity.InstanceMetadataFactory;
import br.com.jpo.metadata.entity.listener.InstanceListener;
import br.com.jpo.session.JPOSessionFactory;
import br.com.jpo.utils.BigDecimalUtils;
import br.com.jpo.utils.ClasspathUtils;
import br.com.jpo.utils.MetadataProxyManager;
import br.com.jpo.utils.StringUtils;

public class InstanceMetadataFactoryImpl implements InstanceMetadataFactory {

	private static Map<String, InstanceMetadata> instancesMetadata = new HashMap<String, InstanceMetadata>();
	private JPOSessionFactory sessionFactory;

	public InstanceMetadataFactoryImpl() {
		
	}

	@Override
	public void configure(JPOSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public InstanceMetadata create(String instanceName) throws InitializeInstanceMetadataException {
		InstanceMetadata instanceMetadata = instancesMetadata.get(instanceName);

		if (instanceMetadata == null) {
			instanceMetadata = getInstanceMetadata(instanceName);

			if (instanceMetadata != null) {
				instancesMetadata.put(instanceMetadata.getName(), instanceMetadata);
			} else {
				throw new InitializeInstanceMetadataException();
			}
		}

		return MetadataProxyManager.getInstanceMetadataProxy(instanceMetadata);
	}

	@Override
	public void addListener(String instanceName, InstanceListener listener) throws InitializeInstanceMetadataException {
		InstanceMetadata instanceMetadata = instancesMetadata.get(instanceName);

		if (instanceMetadata != null) {
			instanceMetadata.addInstanceListener(listener);
		} else {
			throw new InitializeInstanceMetadataException();
		}
	}

	@Override
	public void removeListener(String instanceName, InstanceListener listener) throws InitializeInstanceMetadataException {
		InstanceMetadata instanceMetadata = instancesMetadata.get(instanceName);

		if (instanceMetadata != null) {
			instanceMetadata.removeInstanceListener(listener);
		} else {
			throw new InitializeInstanceMetadataException();
		}
	}

	private InstanceMetadata getInstanceMetadata(String instanceName) throws InitializeInstanceMetadataException {
		JdbcWrapper jdbcWrapper = null;
		ResultSet tddInstanciaResultSet = null;
		PreparedStatement tddInstanciaStatement = null;
		InstanceMetadata instanceMetadata = null;

		try {
			jdbcWrapper = getJdbcWrapper();

			tddInstanciaStatement = jdbcWrapper.getPreparedStatementForSearch("SELECT * FROM TDDINSTANCIA WHERE NOMEINSTANCIA = ?");
			tddInstanciaStatement.setString(1, instanceName);
			tddInstanciaResultSet = tddInstanciaStatement.executeQuery();

			if (tddInstanciaResultSet.next()) {
				EntityMetadata entityMetadata = getEntityMetadata(jdbcWrapper, tddInstanciaResultSet.getString(InstanceMetadata.NOMETABELA));

				instanceMetadata = new InstanceMetadataImpl();
				instanceMetadata.setName(tddInstanciaResultSet.getString(InstanceMetadata.NOMEINSTANCIA));
				instanceMetadata.setDescription(tddInstanciaResultSet.getString(InstanceMetadata.DESCRINSTANCIA));
				instanceMetadata.setCriteria(tddInstanciaResultSet.getString(InstanceMetadata.CRITERIOINSTANCIA));
				instanceMetadata.setEntityMetadata(entityMetadata);

				resolveEntityReferenceMetadata(jdbcWrapper, instanceMetadata);

			}
		} catch(Exception e) {
			InitializeInstanceMetadataException.throwMe(e);
		} finally {
			JdbcWrapper.close(tddInstanciaResultSet);
			JdbcWrapper.close(tddInstanciaStatement);
			JdbcWrapper.close(jdbcWrapper);
		}

		return instanceMetadata;
	}

	private EntityMetadata getEntityMetadata(JdbcWrapper jdbcWrapper, String entityName) throws InitializeInstanceMetadataException {
		ResultSet tddTabResultSet = null;
		PreparedStatement tddTabStatement = null;
		EntityMetadata entityMetadata = null;

		try {
			tddTabStatement = jdbcWrapper.getPreparedStatementForSearch("SELECT * FROM TDDTABELA WHERE NOMETABELA = ?");
			tddTabStatement.setString(1, entityName);
			tddTabResultSet = tddTabStatement.executeQuery();

			if (tddTabResultSet.next()) {
				entityMetadata = new EntityMetadataImpl();
				entityMetadata.setName(tddTabResultSet.getString(EntityMetadata.NOMETABELA));
				entityMetadata.setDescription(tddTabResultSet.getString(EntityMetadata.DESCRTABELA));

				resolveEntityColumnMetadata(jdbcWrapper, entityMetadata);
				resolveEntityKeyMetadata(jdbcWrapper, entityMetadata);
			}
		} catch(Exception e) {
			InitializeInstanceMetadataException.throwMe(e);
		} finally {
			JdbcWrapper.close(tddTabResultSet);
			JdbcWrapper.close(tddTabStatement);
		}

		return entityMetadata;
	}

	private void resolveEntityColumnMetadata(JdbcWrapper jdbcWrapper, EntityMetadata entityMetadata) throws InitializeInstanceMetadataException {
		ResultSet columnsResultSet = null;
		ResultSet tddCamResultSet = null;
		PreparedStatement tddCamStatement = null;

		try {
			DatabaseMetaData databaseMetadata = jdbcWrapper.getMetaData();

			tddCamStatement = jdbcWrapper.getPreparedStatementForSearch("SELECT * FROM TDDCAMPO WHERE NOMETABELA = ?");
			tddCamStatement.setString(1, entityMetadata.getName());
			tddCamResultSet = tddCamStatement.executeQuery();

			while (tddCamResultSet.next()) {
				EntityColumnMetadata columnMetadata = new EntityColumnMetadataImpl();

				columnMetadata.setEntityMetadata(entityMetadata);
				columnMetadata.setName(tddCamResultSet.getString(EntityColumnMetadata.NOMECAMPO));
				columnMetadata.setCalculated("S".equals(tddCamResultSet.getString(EntityColumnMetadata.CALCULADO)));
				columnMetadata.setDataType(tddCamResultSet.getString(EntityColumnMetadata.TIPOCAMPO));
				columnMetadata.setExpression(tddCamResultSet.getString(EntityColumnMetadata.EXPRESSAO));
				columnMetadata.setMandatory("S".equals(tddCamResultSet.getString(EntityColumnMetadata.OBRIGATORIO)));
				columnMetadata.setMask(tddCamResultSet.getString(EntityColumnMetadata.MASCARA));
				columnMetadata.setDescription(tddCamResultSet.getString(EntityColumnMetadata.DESCRCAMPO));
				columnMetadata.setVisible("S".equals(tddCamResultSet.getString(EntityColumnMetadata.VISIVEL)));

				columnsResultSet = databaseMetadata.getColumns(null, null, entityMetadata.getName(), columnMetadata.getName());

				if (columnsResultSet.next()) {
					columnMetadata.setLength(columnsResultSet.getInt(EntityColumnMetadata.COLUMN_SIZE));
					columnMetadata.setNullable(columnsResultSet.getBoolean(EntityColumnMetadata.IS_NULLABLE));
					columnMetadata.setSqlType(columnsResultSet.getInt(EntityColumnMetadata.DATA_TYPE));
					columnMetadata.setPrecision(columnsResultSet.getInt(EntityColumnMetadata.DECIMAL_DIGITS));
					columnMetadata.setDefaultValue(getDafaultValue(columnMetadata, columnsResultSet.getObject(EntityColumnMetadata.COLUMN_DEF)));
					columnMetadata.setAutoIncrement(columnsResultSet.getBoolean(EntityColumnMetadata.IS_AUTOINCREMENT));
				}

				entityMetadata.addEntityColumnMetadata(columnMetadata);
			}

		} catch(Exception e) {
			InitializeInstanceMetadataException.throwMe(e);
		} finally {
			JdbcWrapper.close(columnsResultSet);
			JdbcWrapper.close(tddCamResultSet);
			JdbcWrapper.close(tddCamStatement);
		}
	}

	private void resolveEntityKeyMetadata(JdbcWrapper jdbcWrapper, EntityMetadata entityMetadata) throws InitializeInstanceMetadataException {
		ResultSet columnsResultSet = null;
		ResultSet primaryKeysResultSet = null;
		ResultSet tddKeyResultSet = null;
		PreparedStatement tddKeyStatement = null;

		try {
			DatabaseMetaData databaseMetadata = jdbcWrapper.getMetaData();

			tddKeyStatement = jdbcWrapper.getPreparedStatementForSearch("SELECT * FROM TDDCHAVE WHERE NOMETABELA = ?");
			tddKeyStatement.setString(1, entityMetadata.getName());
			tddKeyResultSet = tddKeyStatement.executeQuery();

			primaryKeysResultSet = databaseMetadata.getPrimaryKeys(null, null, entityMetadata.getName());

			Collection<String> keyMembers = new ArrayList<String>();

			while (primaryKeysResultSet.next()) {
				String columnName = primaryKeysResultSet.getString(EntityColumnMetadata.COLUMN_NAME).toUpperCase();
				entityMetadata.getEntityColumnMetadata(columnName).setPrimaryKey(true);
				keyMembers.add(columnName);
			}

			String keyField = null;
			String type = null;
			KeyGenerator keyGenerator = null;

			if (tddKeyResultSet.next()) {
				keyField = tddKeyResultSet.getString(EntityKeyMetadata.CAMPOCHAVE);
				type = tddKeyResultSet.getString(EntityKeyMetadata.TIPOCHAVE);
				String generatorResource = tddKeyResultSet.getString(EntityKeyMetadata.GERADORCHAVE);

				if (StringUtils.getEmptyAsNull(generatorResource) != null) {
					keyGenerator = (KeyGenerator) ClasspathUtils.getInstance(generatorResource);
				}
			}

			EntityKeyMetadata entityKeyMetadata = new EntityKeyMetadataImpl(keyGenerator, keyMembers);
			entityKeyMetadata.setName(KeyGenerator.KEY_GENERATOR+"_"+entityKeyMetadata.getName());
			entityKeyMetadata.setDescription("Generated Primary Key");
			entityKeyMetadata.setKeyField(keyField);
			entityKeyMetadata.setType(type);

			entityMetadata.setEntityKeyMetadata(entityKeyMetadata);
		} catch(Exception e) {
			InitializeInstanceMetadataException.throwMe(e);
		} finally {
			JdbcWrapper.close(columnsResultSet);
			JdbcWrapper.close(tddKeyResultSet);
			JdbcWrapper.close(primaryKeysResultSet);
			JdbcWrapper.close(tddKeyStatement);
		}
	}

	private void resolveEntityReferenceMetadata(JdbcWrapper jdbcWrapper, InstanceMetadata instanceMetadata) throws InitializeInstanceMetadataException {
		ResultSet tddInstanceResultSet = null;
		PreparedStatement tddInstanceStatement = null;

		try {

			StringBuilder query = new StringBuilder();
			query.append(" SELECT");
			query.append("   INSDEST.NOMETABELA");
			query.append("  ,INSDEST.NOMEINSTANCIA");
			query.append("  ,INSDEST.DESCRINSTANCIA");
			query.append("  ,LIG.CRITERIOLIGACAO");
			query.append("  ,LIG.OBRIGATORIO");
			query.append("  ,LIG.TIPOBUSCA");
			query.append("  ,LIG.TIPOCASCATA");
			query.append("  ,LIG.TIPORELACAO");
			query.append(" FROM TDDINSTANCIA INS");
			query.append("   INNER JOIN TDDLIGACAO LIG ON (INS.NOMEINSTANCIA = LIG.NOMEINSTANCIAORIGEM)");
			query.append("   INNER JOIN TDDINSTANCIA INSDEST ON (LIG.NOMEINSTANCIADESTINO = INSDEST.NOMEINSTANCIA)");
			query.append(" WHERE");
			query.append("   INS.NOMEINSTANCIA = ?");

			tddInstanceStatement = jdbcWrapper.getPreparedStatementForSearch(query.toString());
			tddInstanceStatement.setString(1, instanceMetadata.getName());
			tddInstanceResultSet = tddInstanceStatement.executeQuery();

			while (tddInstanceResultSet.next()) {
				EntityReferenceMetadata entityReferenceMetadata = new EntityReferenceMetadataImpl();

				entityReferenceMetadata.setInstanceMetadata(instanceMetadata);
				entityReferenceMetadata.setDescription(tddInstanceResultSet.getString(InstanceMetadata.DESCRINSTANCIA));
				entityReferenceMetadata.setCascadeType(buildCascadeType(tddInstanceResultSet.getString(EntityReferenceMetadata.TIPOCASCATA)));
				entityReferenceMetadata.setCriterionRelationship(tddInstanceResultSet.getString(EntityReferenceMetadata.CRITERIOLIGACAO));
				entityReferenceMetadata.setTableName(tddInstanceResultSet.getString(InstanceMetadata.NOMETABELA));
				entityReferenceMetadata.setName(tddInstanceResultSet.getString(InstanceMetadata.NOMEINSTANCIA));
				entityReferenceMetadata.setFetchType(buildFetchType(tddInstanceResultSet.getString(EntityReferenceMetadata.TIPOBUSCA)));
				entityReferenceMetadata.setNullable("S".equals(tddInstanceResultSet.getString(EntityReferenceMetadata.OBRIGATORIO)));
				entityReferenceMetadata.setRelationType(buildRelationType(tddInstanceResultSet.getString(EntityReferenceMetadata.TIPORELACAO)));
				resolveFieldsConnectionRelationship(jdbcWrapper, instanceMetadata, entityReferenceMetadata);

				instanceMetadata.addEntityReferenceMetadata(entityReferenceMetadata);
			}
		} catch(Exception e) {
			InitializeInstanceMetadataException.throwMe(e);
		} finally {
			JdbcWrapper.close(tddInstanceResultSet);
			JdbcWrapper.close(tddInstanceStatement);
		}
	}

	private void resolveFieldsConnectionRelationship(JdbcWrapper jdbcWrapper, InstanceMetadata instanceMetadata, EntityReferenceMetadata entityReferenceMetadata) throws InitializeInstanceMetadataException {
		ResultSet fieldsConnectionRelationshipResultSet = null;
		PreparedStatement fieldsConnectionRelationshipStatement = null;

		try {
			StringBuilder query = new StringBuilder();
			query.append(" SELECT");
			query.append("   CAMORIG.NOMECAMPO AS 'NOMECAMPOORIG'");
			query.append("  ,CAMDEST.NOMECAMPO AS 'NOMECAMPODEST'");
			query.append(" FROM TDDINSTANCIA INS");
			query.append("   INNER JOIN TDDLIGACAO LIG ON (INS.NOMEINSTANCIA = LIG.NOMEINSTANCIAORIGEM)");
			query.append("   INNER JOIN TDDINSTANCIA INSDEST ON (LIG.NOMEINSTANCIADESTINO = INSDEST.NOMEINSTANCIA)");
			query.append("   INNER JOIN TDDCAMPOLIGACAO CLIG ON (LIG.NOMEINSTANCIAORIGEM = CLIG.NOMEINSTANCIAORIGEM AND LIG.NOMEINSTANCIADESTINO = CLIG.NOMEINSTANCIADESTINO)");
			query.append("   INNER JOIN TDDCAMPO CAMORIG ON (CLIG.NOMECAMPOORIGEM = CAMORIG.NOMECAMPO AND CLIG.NOMETABELAORIGEM = CAMORIG.NOMETABELA)");
			query.append("   INNER JOIN TDDCAMPO CAMDEST ON (CLIG.NOMECAMPOORIGEM = CAMDEST.NOMECAMPO AND CLIG.NOMETABELADESTINO = CAMDEST.NOMETABELA)");
			query.append(" WHERE");
			query.append("   INS.NOMEINSTANCIA = ?");
			query.append("   AND INSDEST.NOMEINSTANCIA = ?");

			fieldsConnectionRelationshipStatement = jdbcWrapper.getPreparedStatementForSearch(query.toString());
			fieldsConnectionRelationshipStatement.setString(1, instanceMetadata.getName());
			fieldsConnectionRelationshipStatement.setString(2, entityReferenceMetadata.getName());
			fieldsConnectionRelationshipResultSet = fieldsConnectionRelationshipStatement.executeQuery();

			while (fieldsConnectionRelationshipResultSet.next()) {
				entityReferenceMetadata.addFieldConnectionRelationship(fieldsConnectionRelationshipResultSet.getString("NOMECAMPOORIG"), fieldsConnectionRelationshipResultSet.getString("NOMECAMPODEST"));
			}
		} catch(Exception e) {
			InitializeInstanceMetadataException.throwMe(e);
		} finally {
			JdbcWrapper.close(fieldsConnectionRelationshipResultSet);
			JdbcWrapper.close(fieldsConnectionRelationshipStatement);
		}
	}

	private JdbcWrapper getJdbcWrapper() throws Exception {
		return new JdbcWrapper(sessionFactory.getConnectionProvider().getConnection());
	}

	private RelationType buildRelationType(String relation) {
		if (RelationType.MANY_TO_MANY.toString().equals(relation)) {
			return RelationType.MANY_TO_MANY;
		} else if (RelationType.MANY_TO_ONE.toString().equals(relation)) {
			return RelationType.MANY_TO_ONE;
		} else if (RelationType.ONE_TO_MANY.toString().equals(relation)) {
			return RelationType.ONE_TO_MANY;
		} else if (RelationType.ONE_TO_ONE.toString().equals(relation)) {
			return RelationType.ONE_TO_ONE;
		} else {
			throw new IllegalArgumentException("Tipo de Relacionamento não existe.");
		}
	}

	private FetchType buildFetchType(String fetch) throws Exception {
		if (FetchType.EAGER.toString().equals(fetch)) {
			return FetchType.EAGER;
		} else if (FetchType.LAZY.toString().equals(fetch)) {
			return FetchType.LAZY;
		} else {
			throw new IllegalArgumentException("Tipo de Busca não existe.");
		}
	}

	private Collection<CascadeType> buildCascadeType(String cascade) throws Exception {
		if (cascade == null || cascade.isEmpty()) {
			return null;
		}

		Collection<CascadeType> cascadeTypes = new ArrayList<CascadeType>();

		Scanner scanner = new Scanner(cascade);
		scanner.useDelimiter(",");

		if (scanner.hasNext()) {
			cascadeTypes.add(getCascadeType(scanner.next()));
		}

		return cascadeTypes;
	}

	private CascadeType getCascadeType(String cascade) {
		if (CascadeType.ALL.toString().equals(cascade)) {
			return CascadeType.ALL;
		} else if (CascadeType.DELETE_CASCADE.toString().equals(cascade)) {
			return CascadeType.DELETE_CASCADE;
		} else if (CascadeType.INSERT_CASCADE.toString().equals(cascade)) {
			return CascadeType.INSERT_CASCADE;
		} else if (CascadeType.UPDATE_CASCADE.toString().equals(cascade)) {
			return CascadeType.UPDATE_CASCADE;
		} else if (CascadeType.NONE.toString().equals(cascade)) {
			return CascadeType.NONE;
		} else {
			throw new IllegalArgumentException("Tipo de Cascade não existe.");
		}
	}

	private Object getDafaultValue(EntityColumnMetadata columnMetadata, Object value) {
		Object defaultValue = null;

		if (columnMetadata != null && value != null) {
			switch (columnMetadata.getDataType().toUpperCase()) {
				case DataType.BIGDECIMAL:
					defaultValue = BigDecimalUtils.getBigDecimalOrNull(value);
					break;
				case DataType.BOOLEAN:
					defaultValue = Boolean.valueOf(value.toString());
					break;
				case DataType.STRING:
					defaultValue = String.valueOf(value);
					break;
				case DataType.TIMESTAMP:
					defaultValue = (Timestamp) value;
					break;
			}
		}

		return defaultValue;
	}

	private static class DataType {
		static final String BIGDECIMAL 	= "BIGDECIMAL";
		static final String BOOLEAN		= "BOOLEAN";
		static final String STRING		= "STRING";
		static final String TIMESTAMP	= "TIMESTAMP";
	}
}