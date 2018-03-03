package br.com.jpo.session.impl;

import java.util.Properties;

import br.com.jpo.bean.DynamicBeanManagerFactory;
import br.com.jpo.connection.ConnectionProvider;
import br.com.jpo.dao.EntityDAOCacheFactory;
import br.com.jpo.dao.EntityDAOFactory;
import br.com.jpo.metadata.entity.InstanceMetadataFactory;
import br.com.jpo.session.JPOSession;
import br.com.jpo.session.JPOSessionFactory;
import br.com.jpo.sql.SQLServiceProviderFactory;
import br.com.jpo.transaction.JPOTransactionFactory;
import br.com.jpo.transaction.JPOTransactionLockFactory;
import br.com.jpo.utils.ClasspathUtils;

public class JPOSessionFactoryImpl implements JPOSessionFactory {

	private static ThreadLocal<JPOSession>			currentSession;
	private static JPOSessionFactoryImpl			sessionFactory;

	private ConnectionProvider						connectionProvider;
	private JPOTransactionFactory					transactionFactory;
	private EntityDAOFactory						entityDAOFactory;
	private InstanceMetadataFactory					instanceMetadataFactory;
	private SQLServiceProviderFactory				sqlServiceProviderFactory;
	private DynamicBeanManagerFactory				dynamicBeanManagerFactory;
	private EntityDAOCacheFactory 					entityDAOCacheFactory;
	private JPOTransactionLockFactory 				transactionLockFactory;
	private Properties 								properties;
	private StringBuffer							msgErrorDefault;

	private JPOSessionFactoryImpl(Properties properties) throws Exception {
		currentSession = new ThreadLocal<JPOSession>();
		this.properties = properties;

		initializeMsgErrorDefault();
		initializeConnectionProvider();
		initializeTransactionFactory();
		initializeEntityDAOFactory();
		initializeInstanceMetadataFactory();
		initializeSQLServiceProviderFactory();
		initializeDynamicBeanManagerFactory();
		initializeEntityDAOCacheFactory();
		initializeTransactionLockFactory();
	}

	public static JPOSessionFactoryImpl configure(Properties properties) throws Exception {
		if (sessionFactory == null) {
			sessionFactory = new JPOSessionFactoryImpl(properties);
		}

		return sessionFactory;
	}

	public static JPOSessionFactoryImpl getInstance() throws Exception {
		if (sessionFactory == null) {
			throw new Exception("Não existe instância de JPOSessionFactory configurada.");
		}

		return sessionFactory;
	}

	@Override
	public void openSession() throws Exception {
		JPOSession session = (JPOSession) currentSession.get();

		if (session == null) {
			session = new JPOSessionImpl(this);

			currentSession.set(session);
		}
	}

	@Override
	public JPOSession getCurrentSession() throws Exception {
		JPOSession session = currentSession.get();

		if (session == null) {
			throw new Exception("Não existe sessão aberta.");
		} else if (!session.isOpen()) {
			throw new Exception("A sessão corrente foi finalizada.");
		}

		return session;
	}

	@Override
	public void closeSession(JPOSession session) throws Exception {
		if (session != null) {
			session.close();
			currentSession.remove();
		}
	}

	@Override
	public boolean hasCurrentSession() throws Exception {
		JPOSession session = currentSession.get();

		return session != null;
	}

	@Override
	public ConnectionProvider getConnectionProvider() {
		return connectionProvider;
	}

	@Override
	public JPOTransactionFactory getTransactionFactory() {
		return transactionFactory;
	}

	@Override
	public EntityDAOFactory getEntityDAOFactory() {
		return entityDAOFactory;
	}

	@Override
	public InstanceMetadataFactory getInstanceMetadataFactory() {
		return instanceMetadataFactory;
	}

	@Override
	public SQLServiceProviderFactory getSQLServiceProviderFactory() {
		return sqlServiceProviderFactory;
	}

	@Override
	public DynamicBeanManagerFactory getDynamicBeanManagerFactory() {
		return dynamicBeanManagerFactory;
	}

	@Override
	public EntityDAOCacheFactory getEntityDAOCacheFactory() {
		return entityDAOCacheFactory;
	}

	@Override
	public JPOTransactionLockFactory getTransactionLockFactory() {
		return transactionLockFactory;
	}

	private void initializeMsgErrorDefault() {
		msgErrorDefault = new StringBuffer();
		msgErrorDefault.append("Não foi possível inicializar o recurso %s. Possíveis causas:\n");
		msgErrorDefault.append("- A propriedade '%s' não foi definida.\n");
		msgErrorDefault.append("- A classe especificada não foi encontrada.\n");
		msgErrorDefault.append("- A classe especificada não implementa a interface %s.\n");
		msgErrorDefault.append("- A classe especificada não possui um construtor público e sem parâmetros.");
	}

	private void initializeConnectionProvider() throws Exception {
		try {
			String path = properties.getProperty(ConnectionProvider.PROVIDER);

			connectionProvider = (ConnectionProvider) ClasspathUtils.getInstance(path);
			connectionProvider.configure(properties);
		} catch(Exception e) {
			String msg = String.format(msgErrorDefault.toString(), ConnectionProvider.class.getName(), ConnectionProvider.PROVIDER, ConnectionProvider.class.getName());

			throw new Exception(msg);
		}
	}

	private void initializeTransactionFactory() throws Exception {
		try {
			String path = properties.getProperty(JPOTransactionFactory.TRANSACTION_FACTORY);

			transactionFactory = (JPOTransactionFactory) ClasspathUtils.getInstance(path);
		} catch(Exception e) {
			String msg = String.format(msgErrorDefault.toString(), JPOTransactionFactory.class.getName(), JPOTransactionFactory.TRANSACTION_FACTORY, JPOTransactionFactory.class.getName());

			throw new Exception(msg);
		}
	}

	private void initializeEntityDAOFactory() throws Exception {
		try {
			String path = properties.getProperty(EntityDAOFactory.ENTITY_DAO_FACTORY);

			entityDAOFactory = (EntityDAOFactory) ClasspathUtils.getInstance(path);
			entityDAOFactory.configure(this);
		} catch(Exception e) {
			String msg = String.format(msgErrorDefault.toString(), EntityDAOFactory.class.getName(), EntityDAOFactory.ENTITY_DAO_FACTORY, EntityDAOFactory.class.getName());

			throw new Exception(msg);
		}
	}

	private void initializeInstanceMetadataFactory() throws Exception {
		try {
			String path = properties.getProperty(InstanceMetadataFactory.INSTANCE_METADATA_FACTORY);

			instanceMetadataFactory = (InstanceMetadataFactory) ClasspathUtils.getInstance(path);
			instanceMetadataFactory.configure(this);
		} catch(Exception e) {
			String msg = String.format(msgErrorDefault.toString(), InstanceMetadataFactory.class.getName(), InstanceMetadataFactory.INSTANCE_METADATA_FACTORY, InstanceMetadataFactory.class.getName());

			throw new Exception(msg);
		}
	}

	private void initializeSQLServiceProviderFactory() throws Exception {
		try {
			String path = properties.getProperty(SQLServiceProviderFactory.SQL_SERVICE_PROVIDER_FACTORY);

			sqlServiceProviderFactory = (SQLServiceProviderFactory) ClasspathUtils.getInstance(path);
		} catch(Exception e) {
			String msg = String.format(msgErrorDefault.toString(), SQLServiceProviderFactory.class.getName(), SQLServiceProviderFactory.SQL_SERVICE_PROVIDER_FACTORY, SQLServiceProviderFactory.class.getName());

			throw new Exception(msg);
		}
	}

	private void initializeDynamicBeanManagerFactory() throws Exception {
		try {
			String path = properties.getProperty(DynamicBeanManagerFactory.DYNAMIC_BEAN_MANAGER_FACTORY);

			dynamicBeanManagerFactory = (DynamicBeanManagerFactory) ClasspathUtils.getInstance(path);
			dynamicBeanManagerFactory.configure(this);
		} catch(Exception e) {
			String msg = String.format(msgErrorDefault.toString(), DynamicBeanManagerFactory.class.getName(), DynamicBeanManagerFactory.DYNAMIC_BEAN_MANAGER_FACTORY, DynamicBeanManagerFactory.class.getName());

			throw new Exception(msg);
		}
	}

	private void initializeEntityDAOCacheFactory() throws Exception {
		try {
			String path = properties.getProperty(EntityDAOFactory.ENTITY_DAO_CACHE_FACTORY);

			if (path != null) {
				entityDAOCacheFactory = (EntityDAOCacheFactory) ClasspathUtils.getInstance(path);
			}
		} catch(Exception e) {
			String msg = String.format(msgErrorDefault.toString(), EntityDAOFactory.class.getName(), EntityDAOFactory.ENTITY_DAO_CACHE_FACTORY, EntityDAOFactory.class.getName());

			throw new Exception(msg);
		}
	}

	private void initializeTransactionLockFactory() throws Exception {
		try {
			String path = properties.getProperty(JPOTransactionLockFactory.TRANSACTION_LOCK_FACTORY);

			transactionLockFactory = (JPOTransactionLockFactory) ClasspathUtils.getInstance(path);
			transactionLockFactory.congigure(this);
		} catch(Exception e) {
			String msg = String.format(msgErrorDefault.toString(), JPOTransactionLockFactory.class.getName(), JPOTransactionLockFactory.TRANSACTION_LOCK_FACTORY, JPOTransactionLockFactory.class.getName());

			throw new Exception(msg);
		}
	}

}