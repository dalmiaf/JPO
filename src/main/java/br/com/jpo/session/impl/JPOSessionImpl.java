package br.com.jpo.session.impl;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import br.com.jpo.bean.DynamicBean;
import br.com.jpo.connection.impl.JdbcWrapper;
import br.com.jpo.dao.EntityDAO;
import br.com.jpo.dao.EntityDAOFactory;
import br.com.jpo.dao.FinderCustom;
import br.com.jpo.dao.impl.EntityDAOCache;
import br.com.jpo.metadata.entity.InstanceMetadataFactory;
import br.com.jpo.session.JPOSession;
import br.com.jpo.session.JPOSessionContext;
import br.com.jpo.session.JPOSessionFactory;
import br.com.jpo.transaction.JPOTransaction;
import br.com.jpo.utils.UIDGenerator;

final class JPOSessionImpl implements JPOSession {

	private static final String						JPO_SESSION					= "JPOSessionID";

	private String 									sessionID;
	private boolean									openSession;
	private EntityDAOFactory						entityDAOFactory;
	private InstanceMetadataFactory					instanceMetadataFactory;
	private JPOSessionFactory 						sessionFactory;
	private JPOSessionContext						sessionContext;
	private JPOTransaction							transaction;
	private Collection<Runnable>					unlockers;

	public JPOSessionImpl(JPOSessionFactory sessionFactory) throws Exception {
		this.sessionFactory = sessionFactory;
		entityDAOFactory = sessionFactory.getEntityDAOFactory();
		instanceMetadataFactory = sessionFactory.getInstanceMetadataFactory();
		sessionID = JPO_SESSION + UIDGenerator.getNextID();
		sessionContext = new JPOSessionContextImpl(this);
		unlockers = new ArrayList<Runnable>();
		openSession = true;
	}

	@Override
	public void save(DynamicBean bean) throws Exception {
		validateSession();
		validadeTransaction();
		validateBean(bean);

		getEntityDAO(bean.getName()).save(bean);
	}

	@Override
	public void update(DynamicBean bean) throws Exception {
		validateSession();
		validadeTransaction();
		validateBean(bean);

		getEntityDAO(bean.getName()).update(bean);
	}

	@Override
	public void delete(DynamicBean bean) throws Exception {
		validateSession();
		validadeTransaction();
		validateBean(bean);

		getEntityDAO(bean.getName()).delete(bean);
	}

	@Override
	public DynamicBean getDefaultBean(String name) throws Exception {
		validateSession();

		return getEntityDAO(name).getDefaultBean();
	}

	@Override
	public DynamicBean findByPrimaryKey(String name, Map<String, Object> primaryKey) throws Exception {
		validateSession();

		return getEntityDAO(name).findByPrimaryKey(primaryKey);
	}

	@Override
	public Collection<DynamicBean> findCustom(FinderCustom finder) throws Exception {
		validateSession();

		return getEntityDAO(finder.getInstanceName()).findCustom(finder);
	}

	@Override
	public void beginTransaction() throws Exception {
		if (transaction != null) {
			throw new Exception("Já existe uma transação ativa.");
		}

		validateSession();

		try {
			transaction = sessionContext.getTransaction();
			transaction.begin();

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Não foi possível obter uma transação.");
		}
	}

	@Override
	public void commit() throws Exception {
		validateSession();
		validadeTransaction();

		transaction.commit();
	}

	@Override
	public void rollback() throws Exception {
		validateSession();
		validadeTransaction();

		transaction.rollback();
	}

	@Override
	public void close() throws Exception {
		validateSession();
		openSession = false;
		sessionContext.getConnectionManager().close();
		EntityDAOCache.getInstance().invalidate();
	}

	@Override
	public boolean isOpen() {
		return openSession;
	}

	@Override
	public JPOTransaction getTransaction() {
		return transaction;
	}

	@Override
	public boolean hasTransaction() {
		try {
			return transaction != null && (transaction.isActive());
		} catch (Exception ignored) {
		}

		return false;
	}

	@Override
	public String getSessionID() {
		return sessionID;
	}

	@Override
	public void registryUnlocker(Runnable runnable) throws Exception {
		unlockers.add(runnable);
	}

	@Override
	public Connection getConnection() throws Exception {
		return sessionContext.getConnection();
	}

	@Override
	public JdbcWrapper getJdbcWrapper() throws Exception {
		return new JdbcWrapper(sessionContext.getConnection());
	}

	@Override
	public JPOSessionFactory getSessionFactory() throws Exception {
		return sessionFactory;
	}

	@Override
	public JPOSessionContext getSessionContext() throws Exception {
		return sessionContext;
	}

	private EntityDAO getEntityDAO(String name) throws Exception {
		return entityDAOFactory.create(instanceMetadataFactory.create(name));
	}

	private void validateSession() {
		if (!openSession) {
			throw new IllegalStateException("Sessão fechada!");
		}
	}

	private void validadeTransaction() throws Exception {
		if (transaction == null) {
			throw new IllegalStateException("Transação não foi iniciada.");
		} else if(!transaction.isActive()) {
			throw new IllegalStateException("A operação requer uma transação ativa.");
		}
	}

	private void validateBean(DynamicBean bean) {
		if (bean == null) {
			throw new IllegalStateException("O DynamicBean não foi inicializado corretamente.");
		} else if (bean.getName() == null) {
			throw new IllegalStateException("O DynamicBean não foi inicializado corretamente.");
		}
	}
}