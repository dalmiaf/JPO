package br.com.jpo.session.impl;

import java.sql.Connection;

import br.com.jpo.connection.impl.ConnectionManager;
import br.com.jpo.session.JPOSession;
import br.com.jpo.session.JPOSessionContext;
import br.com.jpo.transaction.JPOTransaction;

public class JPOSessionContextImpl implements JPOSessionContext {

	private JPOSession session;
	private ConnectionManager connectionManager;
	private JPOTransaction transaction;

	public JPOSessionContextImpl(JPOSession session) throws Exception {
		this.session = session;
		connectionManager = new ConnectionManager(session.getSessionFactory());
	}

	@Override
	public JPOTransaction getTransaction() throws Exception {
		if (transaction == null) {
			transaction = session.getSessionFactory().getTransactionFactory().create(this);
		}

		return transaction;
	}

	@Override
	public ConnectionManager getConnectionManager() throws Exception {
		return connectionManager;
	}

	@Override
	public Connection getConnection() throws Exception {
		if (!session.isOpen()) {
			throw new Exception("Session is closed.");
		}

		return connectionManager.getConnection();
	}

}