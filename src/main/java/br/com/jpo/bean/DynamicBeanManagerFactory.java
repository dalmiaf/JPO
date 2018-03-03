package br.com.jpo.bean;

import br.com.jpo.metadata.entity.InitializeInstanceMetadataException;
import br.com.jpo.metadata.entity.InstanceMetadata;
import br.com.jpo.session.JPOSessionFactory;


public interface DynamicBeanManagerFactory {

	String DYNAMIC_BEAN_MANAGER_FACTORY = "br.com.jpo.bean.dynamic_bean_manager_factory";

	void configure(JPOSessionFactory sessionFactory);

	DynamicBeanManager create(String instanceName) throws InitializeInstanceMetadataException;

	DynamicBeanManager create(InstanceMetadata instanceMetadata);
}