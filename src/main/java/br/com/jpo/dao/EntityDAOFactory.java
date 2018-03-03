package br.com.jpo.dao;

import br.com.jpo.metadata.entity.InitializeInstanceMetadataException;
import br.com.jpo.metadata.entity.InstanceMetadata;
import br.com.jpo.session.JPOSessionFactory;

public interface EntityDAOFactory {

	String ENTITY_DAO_FACTORY 						= "br.com.jpo.dao.entitydao_factory";
	String ENTITY_DAO_CACHE_FACTORY					= "br.com.jpo.dao.cache_provider_factory";

	void configure(JPOSessionFactory sessionFactory);

	EntityDAO create(String instanceName) throws InitializeEntityDAOException, InitializeInstanceMetadataException;

	EntityDAO create(InstanceMetadata instanceMetadata) throws InitializeEntityDAOException;
}