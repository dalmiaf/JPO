package br.com.jpo.dao.impl;

import br.com.jpo.dao.EntityDAOContext;
import br.com.jpo.metadata.entity.InstanceMetadata;
import br.com.jpo.session.JPOSessionFactory;

public class EntityDAOContextImpl implements EntityDAOContext {

	private InstanceMetadata instanceMetadata;
	private JPOSessionFactory sessionFactory;

	public EntityDAOContextImpl(InstanceMetadata instanceMetadata, JPOSessionFactory sessionFactory) {
		this.instanceMetadata = instanceMetadata;
		this.sessionFactory = sessionFactory;
	}

	@Override
	public InstanceMetadata getInstanceMetadata() {
		return instanceMetadata;
	}

	@Override
	public JPOSessionFactory getSessionFactory() {
		return sessionFactory;
	}

}