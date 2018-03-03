package br.com.jpo.metadata.entity;

import java.util.Map;

/**
 * Essa classe representa cada Entidade/Tabela mapeada no dicionário de dados.
 * Comtém todas as informções necessárias de metadados da entidade.
 * 
 * @author Dalmi Alves Ferreira
 *
 */
public interface EntityMetadata extends Metadata {

	// Metadados relativos ao DatabaseMetaData
	String REF_GENERATION						= "REF_GENERATION";
	String SELF_REFERENCING_COL_NAME			= "SELF_REFERENCING_COL_NAME";
	String TABLE_TYPE							= "TABLE_TYPE";
	String TYPE_SCHEM							= "TYPE_SCHEM";

	// Metadados relativos ao Dicionário de Dados
	String DESCRTABELA							= "DESCRTABELA";
	String IDTABELA								= "IDTABELA";
	String NOMETABELA							= "NOMETABELA";
	String TDDTABELA							= "TDDTABELA";

	/*
	 * A inclusão de métodos aqui requer atenção na Classe MetadataProxyManager, pois essa
	 * é responsável por criar o proxy EntityMetadata.
	 */

	String getDescription();

	void setDescription(String description);

	Map<String, EntityColumnMetadata> getEntityColumnsMetadata();

	EntityColumnMetadata getEntityColumnMetadata(String columnName);

	void addEntityColumnMetadata(EntityColumnMetadata entityColumnMetadata);

	EntityKeyMetadata getEntityKeyMetadata();

	void setEntityKeyMetadata(EntityKeyMetadata entityKeyMetadata);
}