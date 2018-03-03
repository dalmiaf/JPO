package br.com.jpo.dao;

import br.com.jpo.bean.DynamicBean;
import br.com.jpo.connection.impl.JdbcWrapper;
import br.com.jpo.transaction.JPOTransactionLock;

public class KeyGenerateEvent {

	private JPOTransactionLock 		transactionLock;
	private EntityDAO   			entityDAO;
    private DynamicBean				dynamicBean;
    private JdbcWrapper 			jdbcWrapper;

    public KeyGenerateEvent(JPOTransactionLock transactionLock, EntityDAO dao, JdbcWrapper jdbc, DynamicBean bean) {
    	this.transactionLock 	= transactionLock;
        this.entityDAO 			= dao;
        this.jdbcWrapper		= jdbc;
        this.dynamicBean		= bean;
    }

	public EntityDAO getEntityDAO() {
		return entityDAO;
	}

	public DynamicBean getDynamicBean() {
		return dynamicBean;
	}

	public JdbcWrapper getJdbcWrapper() {
		return jdbcWrapper;
	}

	public JPOTransactionLock getTransactionLock() {
		return transactionLock;
	}

}