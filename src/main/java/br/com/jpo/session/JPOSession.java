package br.com.jpo.session;

import java.sql.Connection;
import java.util.Collection;
import java.util.Map;

import br.com.jpo.bean.DynamicBean;
import br.com.jpo.connection.impl.JdbcWrapper;
import br.com.jpo.dao.FinderCustom;
import br.com.jpo.transaction.JPOTransaction;

public interface JPOSession {

	void save(DynamicBean bean) throws Exception;

	void update(DynamicBean bean) throws Exception;

	void delete(DynamicBean bean) throws Exception;

	DynamicBean getDefaultBean(String name) throws Exception;

	DynamicBean findByPrimaryKey(String name, Map<String, Object> primaryKey) throws Exception;

    Collection<DynamicBean> findCustom(FinderCustom finder) throws Exception;

	void beginTransaction() throws Exception;

	void commit() throws Exception;

	void rollback() throws Exception;

	void close() throws Exception;

	boolean isOpen();

	String getSessionID();

	JPOTransaction getTransaction();

	boolean hasTransaction();

	void registryUnlocker(Runnable runnable) throws Exception;

	Connection getConnection() throws Exception;

	JdbcWrapper getJdbcWrapper() throws Exception;

	JPOSessionFactory getSessionFactory() throws Exception;

	JPOSessionContext getSessionContext() throws Exception;
}