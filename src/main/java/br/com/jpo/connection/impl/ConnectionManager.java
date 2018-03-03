package br.com.jpo.connection.impl;

import java.sql.Connection;
import java.sql.SQLException;

import br.com.jpo.session.JPOSessionFactory;

public class ConnectionManager {

	private Connection connection;
	private JPOSessionFactory sessionFactory;
	private boolean closed;

	public ConnectionManager(JPOSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public JPOSessionFactory getJPOSessionFactory() {
		return sessionFactory;
	}

	public Connection getConnection() throws Exception {
		if (closed) {
			throw new Exception("connection manager has been closed");
		}

		if (connection == null) {
			openConnection();
		}

		return connection;
	}

	public Connection close() throws Exception {
		try {
			Connection localConnection = cleanup();
			return localConnection;
		} finally {
			closed = true;
		}
	}

	public boolean isAutoCommit() throws SQLException {
		return (connection == null) || (connection.isClosed()) || (connection.getAutoCommit());
	}

	private void openConnection() throws Exception {
		if (connection != null) {
			return;
		}

		try {
			connection = sessionFactory.getConnectionProvider().getConnection();
		} catch (SQLException e) {
			throw new Exception("Cannot open connection: "+e.getMessage());
		}
	}

	private void closeConnection() throws Exception {
		try {
			sessionFactory.getConnectionProvider().close(connection);
			connection = null;
		} catch (SQLException e) {
			throw new Exception("Cannot release connection: "+e.getMessage());
		}
	}

	private Connection cleanup() throws Exception {
		if (connection == null) {
			return null;
		}

		closeConnection();

		return connection;
	}
}