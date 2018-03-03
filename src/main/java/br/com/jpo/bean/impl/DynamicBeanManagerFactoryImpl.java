package br.com.jpo.bean.impl;

import br.com.jpo.bean.DynamicBeanManager;
import br.com.jpo.bean.DynamicBeanManagerFactory;
import br.com.jpo.metadata.entity.InitializeInstanceMetadataException;
import br.com.jpo.metadata.entity.InstanceMetadata;
import br.com.jpo.session.JPOSessionFactory;

public class DynamicBeanManagerFactoryImpl implements DynamicBeanManagerFactory {

	private JPOSessionFactory sessionFactory;

	@Override
	public void configure(JPOSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public DynamicBeanManager create(String instanceName) throws InitializeInstanceMetadataException {
		return create(sessionFactory.getInstanceMetadataFactory().create(instanceName));
	}

	@Override
	public DynamicBeanManager create(InstanceMetadata instanceMetadata) {
		DynamicBeanManager manager = DynamicBeanManagerImpl.configure(sessionFactory, instanceMetadata);

		return manager;
	}

}