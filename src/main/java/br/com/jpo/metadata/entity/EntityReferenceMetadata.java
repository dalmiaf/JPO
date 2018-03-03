package br.com.jpo.metadata.entity;

import java.util.Collection;
import java.util.Map;

/**
 * Essa classe representa uma das Referências/Ligações da Entidade/Tabela mapeada no dicionário de dados.
 * Comtém todas as informções necessárias de metadados da rferência.
 * 
 * @author Dalmi Alves Ferreira
 *
 */
public interface EntityReferenceMetadata extends Metadata {

	String CRITERIOLIGACAO						= "CRITERIOLIGACAO";
	String NOMEINSTANCIADESTINO					= "NOMEINSTANCIADESTINO";
	String NOMEINSTANCIAORIGEM					= "NOMEINSTANCIAORIGEM";
	String OBRIGATORIO							= "OBRIGATORIO";
	String TIPOBUSCA							= "TIPOBUSCA";
	String TIPOCASCATA							= "TIPOCASCATA";
	String TIPORELACAO							= "TIPORELACAO";

	public enum CascadeType {
		ALL,
		DELETE_CASCADE,
		INSERT_CASCADE,
		UPDATE_CASCADE,
		NONE
	}

	public enum RelationType {
		MANY_TO_MANY,
		MANY_TO_ONE,
		ONE_TO_MANY,
		ONE_TO_ONE
	}

	public enum FetchType {
		EAGER,
		LAZY
	}

	/*
	 * A inclusão de métodos aqui requer atenção na Classe MetadataProxyManager, pois essa
	 * é responsável por criar o proxy EntityReferenceMetadata.
	 */

	String getTableName();

	void setTableName(String tableName);

	Collection<CascadeType> getCascadeType();

	void setCascadeType(Collection<CascadeType> cascadeType);

	RelationType getRelationType();

	void setRelationType(RelationType relationType);

	FetchType getFetchType();

	void setFetchType(FetchType fetchType);

	String getCriterionRelationship();

	void setCriterionRelationship(String criterionRelationship);

	boolean isNullable();

	void setNullable(boolean nullable);

	Map<String, String> getFieldsConnectionRelationship();

	void addFieldConnectionRelationship(String nameFieldOrig, String nameFieldDest);

	InstanceMetadata getInstanceMetadata();

	void setInstanceMetadata(InstanceMetadata instanceMetadata);
}