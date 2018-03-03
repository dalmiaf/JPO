package br.com.jpo.metadata.entity;

import java.util.Collection;
import java.util.Map;

import br.com.jpo.metadata.entity.listener.InstanceListener;


/**
 * Essa classe representa cada Instância de uma Entidade/Tabela mapeada no dicionário de dados.
 * Comtém todas as informções necessárias de metadados da instância.
 * 
 * @author Dalmi Alves Ferreira
 *
 */
public interface InstanceMetadata extends Metadata {

	String DESCRINSTANCIA						= "DESCRINSTANCIA";
	String CRITERIOINSTANCIA					= "CRITERIOINSTANCIA";
	String NOMEINSTANCIA						= "NOMEINSTANCIA";
	String NOMETABELA							= "NOMETABELA";

	/*
	 * A inclusão de métodos aqui requer atenção na Classe MetadataProxyManager, pois essa
	 * é responsável por criar o proxy InstanceMetadata.
	 */

	String getCriteria();

	void setCriteria(String criteria);

	EntityMetadata getEntityMetadata();

	void setEntityMetadata(EntityMetadata entityMetadata);

	Map<String, EntityReferenceMetadata> getEntityReferencesMetadata();

	EntityReferenceMetadata getEntityReferenceMetadata(String entityReferenceName);

	void addEntityReferenceMetadata(EntityReferenceMetadata entityReferenceMetadata);

	Collection<InstanceListener> getInstanceListeners();

	void addInstanceListener(InstanceListener instanceListener);

	void removeInstanceListener(InstanceListener instanceListener);
}