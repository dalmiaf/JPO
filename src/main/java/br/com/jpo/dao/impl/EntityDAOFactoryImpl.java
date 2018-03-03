package br.com.jpo.dao.impl;

import java.util.HashMap;
import java.util.Map;

import br.com.jpo.dao.EntityDAO;
import br.com.jpo.dao.EntityDAOContext;
import br.com.jpo.dao.EntityDAOFactory;
import br.com.jpo.dao.InitializeEntityDAOException;
import br.com.jpo.metadata.entity.InitializeInstanceMetadataException;
import br.com.jpo.metadata.entity.InstanceMetadata;
import br.com.jpo.session.JPOSessionFactory;

public class EntityDAOFactoryImpl implements EntityDAOFactory {

	private static Map<String, EntityDAO> entitiesDAO = new HashMap<String, EntityDAO>();
	private JPOSessionFactory sessionFactory;

	public EntityDAOFactoryImpl() throws Exception {
		
	}

	@Override
	public void configure(JPOSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public EntityDAO create(String instanceName) throws InitializeEntityDAOException, InitializeInstanceMetadataException {
		InstanceMetadata instanceMetadata = sessionFactory.getInstanceMetadataFactory().create(instanceName);

		return create(instanceMetadata);
	}

	@Override
	public EntityDAO create(InstanceMetadata instanceMetadata) throws InitializeEntityDAOException {
		if (instanceMetadata == null) {
			throw new InitializeEntityDAOException();
		}

		EntityDAO entityDAO = entitiesDAO.get(instanceMetadata.getName());

		if (entityDAO == null) {
			EntityDAOContext context = new EntityDAOContextImpl(instanceMetadata, sessionFactory);
			entityDAO = new EntityDAOImpl(context);

			entitiesDAO.put(instanceMetadata.getName(), entityDAO);
		}

		return entityDAO;
	}

}