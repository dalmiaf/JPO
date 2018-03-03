package br.com.jpo.metadata.entity;

import java.util.Collection;

import br.com.jpo.dao.KeyGenerator;

public interface EntityKeyMetadata extends Metadata {

	// Metadados relativos ao Dicionário de Dados
	String TDDCHAVE 							= "TDDCHAVE";
	String CAMPOCHAVE							= "CAMPOCHAVE";
	String NOMETABELA							= "NOMETABELA";
	String GERADORCHAVE							= "GERADORCHAVE";
	String TIPOCHAVE							= "TIPOCHAVE";
	String ULTIMACHAVE							= "ULTIMACHAVE";

	String getType();

	void setType(String type);

	String getKeyField();

	void setKeyField(String keyField);

	KeyGenerator getKeyGenerator();

    Collection<String> getKeyMembers();
}