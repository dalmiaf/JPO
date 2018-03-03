package br.com.jpo.session;

import br.com.jpo.bean.DynamicBeanManagerFactory;
import br.com.jpo.connection.ConnectionProvider;
import br.com.jpo.dao.EntityDAOCacheFactory;
import br.com.jpo.dao.EntityDAOFactory;
import br.com.jpo.metadata.entity.InstanceMetadataFactory;
import br.com.jpo.sql.SQLServiceProviderFactory;
import br.com.jpo.transaction.JPOTransactionFactory;
import br.com.jpo.transaction.JPOTransactionLockFactory;

public interface JPOSessionFactory {

	void openSession() throws Exception;

	JPOSession getCurrentSession() throws Exception;

	void closeSession(JPOSession session) throws Exception;

	boolean hasCurrentSession() throws Exception;

	ConnectionProvider getConnectionProvider();

	JPOTransactionFactory getTransactionFactory();

	EntityDAOFactory getEntityDAOFactory();

	InstanceMetadataFactory getInstanceMetadataFactory();

	SQLServiceProviderFactory getSQLServiceProviderFactory();

	DynamicBeanManagerFactory getDynamicBeanManagerFactory();

	EntityDAOCacheFactory getEntityDAOCacheFactory();

	JPOTransactionLockFactory getTransactionLockFactory();

}