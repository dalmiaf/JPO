package br.com.jpo.session;

import java.sql.Connection;

import br.com.jpo.connection.impl.ConnectionManager;
import br.com.jpo.transaction.JPOTransaction;

public interface JPOSessionContext {

	JPOTransaction getTransaction() throws Exception;

	ConnectionManager getConnectionManager() throws Exception;

	Connection getConnection() throws Exception;

}