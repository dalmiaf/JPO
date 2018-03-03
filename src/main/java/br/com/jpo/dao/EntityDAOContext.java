package br.com.jpo.dao;

import br.com.jpo.metadata.entity.InstanceMetadata;
import br.com.jpo.session.JPOSessionFactory;

public interface EntityDAOContext {

	InstanceMetadata getInstanceMetadata();

	JPOSessionFactory getSessionFactory();
}
