package br.com.jpo.metadata.entity;

import br.com.jpo.metadata.entity.listener.InstanceListener;
import br.com.jpo.session.JPOSessionFactory;

public interface InstanceMetadataFactory {

	String INSTANCE_METADATA_FACTORY = "br.com.jpo.metadata.entity.instance_metadata_factory";

	void configure(JPOSessionFactory sessionFactory);

	InstanceMetadata create(String instanceName) throws InitializeInstanceMetadataException;

	void addListener(String instanceName, InstanceListener listener) throws InitializeInstanceMetadataException;

	void removeListener(String instanceName, InstanceListener listener) throws InitializeInstanceMetadataException;
}