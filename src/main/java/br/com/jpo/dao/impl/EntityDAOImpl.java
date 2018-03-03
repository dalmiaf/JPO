package br.com.jpo.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import br.com.jpo.bean.DynamicBean;
import br.com.jpo.bean.DynamicBeanManager;
import br.com.jpo.bean.DynamicBeanManagerFactory;
import br.com.jpo.connection.impl.JdbcWrapper;
import br.com.jpo.dao.EntityDAO;
import br.com.jpo.dao.EntityDAOContext;
import br.com.jpo.dao.FinderCustom;
import br.com.jpo.dao.KeyGenerateEvent;
import br.com.jpo.dao.KeyGenerator;
import br.com.jpo.metadata.entity.EntityColumnMetadata;
import br.com.jpo.metadata.entity.EntityKeyMetadata;
import br.com.jpo.metadata.entity.EntityReferenceMetadata;
import br.com.jpo.metadata.entity.EntityReferenceMetadata.FetchType;
import br.com.jpo.metadata.entity.EntityReferenceMetadata.RelationType;
import br.com.jpo.metadata.entity.InstanceMetadata;
import br.com.jpo.metadata.entity.event.FinderInstanceEvent;
import br.com.jpo.metadata.entity.event.InstanceEventDispatcher;
import br.com.jpo.metadata.entity.event.PersistenceInstanceEvent;
import br.com.jpo.session.JPOSession;
import br.com.jpo.session.JPOSessionContext;
import br.com.jpo.session.JPOSessionFactory;
import br.com.jpo.sql.NativeSQL;
import br.com.jpo.sql.SQLServiceProvider;
import br.com.jpo.transaction.JPOTransactionLockFactory;

public class EntityDAOImpl implements EntityDAO {

	private final JPOSessionFactory sessionFactory;
	private final InstanceMetadata instanceMetadata;
	private final SQLServiceProvider sqlServiceProvider;
	private final DynamicBeanManagerFactory dynamicBeanManagerFactory;
	private final DynamicBeanManager dynamicBeanManager;
	private final JPOTransactionLockFactory transactionLockFactory;
	private final InstanceEventDispatcher instanceEventDispatcher;
	private final EntityDAOCache cache;

	public EntityDAOImpl(EntityDAOContext context) {
		sessionFactory = context.getSessionFactory();
		instanceMetadata = context.getInstanceMetadata();
		dynamicBeanManagerFactory = sessionFactory.getDynamicBeanManagerFactory();
		sqlServiceProvider = sessionFactory.getSQLServiceProviderFactory().create(context);
		dynamicBeanManager = dynamicBeanManagerFactory.create(instanceMetadata);
		transactionLockFactory = sessionFactory.getTransactionLockFactory();
		cache = sessionFactory.getEntityDAOCacheFactory().create();

		instanceEventDispatcher = new InstanceEventDispatcher(this);
	}

	@Override
	public void save(DynamicBean bean) throws Exception {
		JdbcWrapper jdbcWrapper = null;
		NativeSQL nativeSQL = null;

		try {
			jdbcWrapper = getJdbcWrapper();

			if (bean != null) {
				instanceEventDispatcher.dispatchPersistenceInstanceEvent(PersistenceInstanceEvent.BEFORE_INSERT, bean);

				generateKey(bean);

				nativeSQL = new NativeSQL(jdbcWrapper);
				nativeSQL.appendSql(sqlServiceProvider.buildInsert().toString());

				for (Entry<String, EntityColumnMetadata> entry: instanceMetadata.getEntityMetadata().getEntityColumnsMetadata().entrySet()) {
					nativeSQL.addNamedParameter(entry.getKey(), bean.getAttribute(entry.getKey()));
				}

				nativeSQL.executeUpdate();

				if (isUseCache()) {
					cache.remove(bean);
				}

				bean = findByPrimaryKey(bean.getPrimaryKeyAsValue());

				instanceEventDispatcher.dispatchPersistenceInstanceEvent(PersistenceInstanceEvent.AFTER_INSERT, bean);
			} else {
				throw new Exception("DynamicBean não pode ser nulo.");
			}
		} catch (Exception ex) {
			throw new Exception(ex);
		} finally {
			NativeSQL.releaseResources(nativeSQL);
		}
	}

	@Override
	public void update(DynamicBean bean) throws Exception {
		JdbcWrapper jdbcWrapper = null;
		NativeSQL nativeSQL = null;

		try {
			jdbcWrapper = getJdbcWrapper();

			if (bean != null) {
				instanceEventDispatcher.dispatchPersistenceInstanceEvent(PersistenceInstanceEvent.BEFORE_UPDATE, bean);

				nativeSQL = new NativeSQL(jdbcWrapper);
				nativeSQL.appendSql(sqlServiceProvider.buildUpdate().toString());

				for (Entry<String, EntityColumnMetadata> entry: instanceMetadata.getEntityMetadata().getEntityColumnsMetadata().entrySet()) {
					nativeSQL.addNamedParameter(entry.getKey(), bean.getAttribute(entry.getKey()));
				}

				nativeSQL.executeUpdate();

				if (isUseCache()) {
					cache.remove(bean);
				}

				instanceEventDispatcher.dispatchPersistenceInstanceEvent(PersistenceInstanceEvent.AFTER_UPDATE, bean);
			} else {
				throw new Exception("DynamicBean não pode ser nulo.");
			}
		} catch (Exception ex) {
			throw new Exception(ex);
		} finally {
			NativeSQL.releaseResources(nativeSQL);
		}
	}

	@Override
	public void delete(DynamicBean bean) throws Exception {
		JdbcWrapper jdbcWrapper = null;
		NativeSQL nativeSQL = null;

		try {
			jdbcWrapper = getJdbcWrapper();

			if (bean != null) {
				instanceEventDispatcher.dispatchPersistenceInstanceEvent(PersistenceInstanceEvent.BEFORE_DELETE, bean);

				nativeSQL = new NativeSQL(jdbcWrapper);
				nativeSQL.appendSql(sqlServiceProvider.buildDelete().toString());

				for (Entry<String, EntityColumnMetadata> entry: instanceMetadata.getEntityMetadata().getEntityColumnsMetadata().entrySet()) {
					if (entry.getValue().isPrimaryKey()) {
						nativeSQL.addNamedParameter(entry.getKey(), bean.getAttribute(entry.getKey()));
					}
				}

				nativeSQL.executeUpdate();

				if (isUseCache()) {
					cache.remove(bean);
				}

				instanceEventDispatcher.dispatchPersistenceInstanceEvent(PersistenceInstanceEvent.AFTER_DELETE, bean);
			} else {
				throw new Exception("DynamicBean não pode ser nulo.");
			}
		} catch (Exception ex) {
			throw new Exception(ex);
		} finally {
			NativeSQL.releaseResources(nativeSQL);
		}
	}

	@Override
	public void loadRelationship(DynamicBean bean, String referenceName) throws Exception {
		EntityReferenceMetadata entityReferenceMetadata = instanceMetadata.getEntityReferenceMetadata(referenceName);

		if (RelationType.ONE_TO_ONE.equals(entityReferenceMetadata.getRelationType()) || RelationType.MANY_TO_ONE.equals(entityReferenceMetadata.getRelationType())) {
			resolveManyToOneRelationship(bean, entityReferenceMetadata);
		} else if (RelationType.ONE_TO_MANY.equals(entityReferenceMetadata.getRelationType()) || RelationType.MANY_TO_MANY.equals(entityReferenceMetadata.getRelationType())) {
			resolveOneToManyRelationship(bean, entityReferenceMetadata);
		}
	}

	@Override
	public DynamicBean getDefaultBean() throws Exception {
		return dynamicBeanManager.loadDefaultDynamicBean();
	}

	@Override
	public InstanceMetadata getInstanceMetadata() throws Exception {
		return instanceMetadata;
	}

	@Override
	public DynamicBean findByPrimaryKey(Map<String, Object> primaryKey) throws Exception {
		JdbcWrapper jdbcWrapper = null;
		NativeSQL nativeSQL = null;
		DynamicBean bean = null;

		try {
			jdbcWrapper = getJdbcWrapper();

			if (primaryKey != null) {
				nativeSQL = new NativeSQL(jdbcWrapper);
				nativeSQL.appendSql(sqlServiceProvider.buildSelectByPrimaryKey().toString());

				for (Entry<String, EntityColumnMetadata> entry: instanceMetadata.getEntityMetadata().getEntityColumnsMetadata().entrySet()) {
					if (entry.getValue().isPrimaryKey()) {
						nativeSQL.addNamedParameter(entry.getKey(), primaryKey.get(entry.getKey()));
					}
				}

				int uniqueID = isUseCache() ? cache.getUniqueId("findByPrimaryKey."+instanceMetadata.getName(), nativeSQL.getSqlBuf().toString(), nativeSQL.getNamedParameters()) : null;

				if (isUseCache() && cache.contains(uniqueID)) {
					bean = cache.getAsDynamicBean(uniqueID);
				} else {
					ResultSet resultSet = nativeSQL.executeQuery();

					if (resultSet.next()) {
						bean = dynamicBeanManager.loadDynamicBeanByResultSet(resultSet);
						resolveDynamicReference(bean);
					}

					if (isUseCache()) {
						cache.add(uniqueID, bean);
					}
				}

				instanceEventDispatcher.dispatchFinderInstanceEvent(FinderInstanceEvent.AFTER_LOAD, bean);
			} else {
				throw new Exception("DynamicBean não pode ser nulo.");
			}
		} catch (Exception ex) {
			throw new Exception(ex);
		} finally {
			NativeSQL.releaseResources(nativeSQL);
		}

		return bean;
	}

	@Override
	public Collection<DynamicBean> findCustom(FinderCustom finder) throws Exception {
		JdbcWrapper jdbcWrapper = null;
		NativeSQL nativeSQL = null;
		Collection<DynamicBean> beans = new ArrayList<DynamicBean>();

		try {
			jdbcWrapper = getJdbcWrapper();

			if (finder != null) {
				nativeSQL = new NativeSQL(jdbcWrapper);
				nativeSQL.setMaxRows(finder.getMaxRows());
				nativeSQL.appendSql(sqlServiceProvider.buildSelectCustom(finder.getWhere()).toString());

				nativeSQL.addNamedParameter(finder.getNamedParameters());

				int uniqueID = isUseCache() ? cache.getUniqueId("findCustom."+instanceMetadata.getName(), nativeSQL.getSqlBuf().toString(), nativeSQL.getNamedParameters()): null;

				if (isUseCache() && cache.contains(uniqueID)) {
					beans = cache.getAsDynamicBeanCollection(uniqueID);
				} else {
					ResultSet resultSet = nativeSQL.executeQuery();

					while (resultSet.next()) {
						DynamicBean bean = dynamicBeanManager.loadDynamicBeanByResultSet(resultSet);
						resolveDynamicReference(bean);
						beans.add(bean);
					}

					if (isUseCache()) {
						cache.add(uniqueID, beans);
					}
				}

				instanceEventDispatcher.dispatchFinderInstanceEvent(FinderInstanceEvent.AFTER_LOAD, beans);
			} else {
				throw new Exception("DynamicBean não pode ser nulo.");
			}
		} catch (Exception ex) {
			throw new Exception(ex);
		} finally {
			NativeSQL.releaseResources(nativeSQL);
		}

		return beans;
	}

	private void resolveDynamicReference(DynamicBean bean) throws Exception {
		for (Iterator<Entry<String, EntityReferenceMetadata>> iterator = instanceMetadata.getEntityReferencesMetadata().entrySet().iterator(); iterator.hasNext();) {
			Entry<String, EntityReferenceMetadata> entry = iterator.next();
			EntityReferenceMetadata entityReferenceMetadata = entry.getValue();

			if (FetchType.EAGER.equals(entityReferenceMetadata.getFetchType())) {
				if (RelationType.ONE_TO_ONE.equals(entityReferenceMetadata.getRelationType()) || RelationType.MANY_TO_ONE.equals(entityReferenceMetadata.getRelationType())) {
					resolveManyToOneRelationship(bean, entityReferenceMetadata);
				} else if (RelationType.ONE_TO_MANY.equals(entityReferenceMetadata.getRelationType()) || RelationType.MANY_TO_MANY.equals(entityReferenceMetadata.getRelationType())) {
					resolveOneToManyRelationship(bean, entityReferenceMetadata);
				}
			}
		}
	}

	private void resolveManyToOneRelationship(DynamicBean beanOrig, EntityReferenceMetadata entityReferenceMetadata) throws Exception {
		JdbcWrapper jdbcWrapper = null;
		NativeSQL nativeSQL = null;

		try {
			jdbcWrapper = getJdbcWrapper();

			if (beanOrig != null) {
				nativeSQL = new NativeSQL(jdbcWrapper);
				nativeSQL.appendSql(sqlServiceProvider.buildSelectByReference(entityReferenceMetadata.getName()).toString());

				for (Entry<String, String> entry: entityReferenceMetadata.getFieldsConnectionRelationship().entrySet()) {
					nativeSQL.addNamedParameter(entry.getValue(), beanOrig.getAttribute(entry.getKey()));
				}

				ResultSet resultSet = nativeSQL.executeQuery();
				DynamicBeanManager m = dynamicBeanManagerFactory.create(entityReferenceMetadata.getName());
				DynamicBean bean = null;

				if (resultSet.next()) {
					bean = m.loadDynamicBeanByResultSet(resultSet);
					beanOrig.setAttribute(bean.getName(), bean);
				}
			} else {
				throw new Exception("DynamicBean não pode ser nulo.");
			}
		} catch (Exception ex) {
			throw new Exception(ex);
		} finally {
			NativeSQL.releaseResources(nativeSQL);
		}
	}

	private void resolveOneToManyRelationship(DynamicBean beanParent, EntityReferenceMetadata entityReferenceMetadata) throws Exception {
		JdbcWrapper jdbcWrapper = null;
		NativeSQL nativeSQL = null;

		try {
			jdbcWrapper = getJdbcWrapper();

			if (beanParent != null) {
				nativeSQL = new NativeSQL(jdbcWrapper);
				nativeSQL.appendSql(sqlServiceProvider.buildSelectByReference(entityReferenceMetadata.getName()).toString());

				for (Entry<String, String> entry: entityReferenceMetadata.getFieldsConnectionRelationship().entrySet()) {
					nativeSQL.addNamedParameter(entry.getValue(), beanParent.getAttribute(entry.getKey()));
				}

				ResultSet resultSet = nativeSQL.executeQuery();
				DynamicBeanManager m = dynamicBeanManagerFactory.create(entityReferenceMetadata.getName());
				Collection<DynamicBean> beans = new ArrayList<DynamicBean>();

				while (resultSet.next()) {
					DynamicBean bean = m.loadDynamicBeanByResultSet(resultSet);
					beans.add(bean);
				}

				if (!beans.isEmpty()) {
					beanParent.setAttribute(entityReferenceMetadata.getName(), beans);
				}
			} else {
				throw new Exception("DynamicBean não pode ser nulo.");
			}
		} catch (Exception ex) {
			throw new Exception(ex);
		} finally {
			NativeSQL.releaseResources(nativeSQL);
		}
	}

	private JdbcWrapper getJdbcWrapper() throws Exception {
		JPOSession session = sessionFactory.getCurrentSession();
		JPOSessionContext context = session.getSessionContext();

		Connection connection = context.getConnection();

		return new JdbcWrapper(connection);
	}

	private void generateKey(DynamicBean bean) throws Exception {
		EntityKeyMetadata entityKeyMetadata = instanceMetadata.getEntityMetadata().getEntityKeyMetadata();

		KeyGenerator keyGenerator = entityKeyMetadata.getKeyGenerator();

		if (keyGenerator != null) {
			KeyGenerateEvent event = new KeyGenerateEvent(transactionLockFactory.create(), this, getJdbcWrapper(), bean);
			keyGenerator.generateKey(event);
		}
	}

	private boolean isUseCache() {
		return cache != null;
	}

}