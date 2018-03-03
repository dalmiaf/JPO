package br.com.jpo.connection.impl;

import java.sql.Connection;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import br.com.jpo.connection.ConnectionProvider;
import br.com.jpo.utils.PropertiesUtils;

public class DataSourceConnectionProvider implements ConnectionProvider {

	private DataSource dataSource;
	private boolean autoCommit;
	private Integer isolation;

	@Override
	public void configure(Properties properties) throws Exception {
		String jndiName = properties.getProperty(JNDI_NAME);

		autoCommit = PropertiesUtils.getPropertyAsBoolean(properties, AUTO_COMMIT, false);
		isolation = PropertiesUtils.getPropertyAsInteger(properties, ISOLATION, null);

		Context context = new InitialContext();
		Context javaContext = (Context) context.lookup("java:");

		dataSource = (DataSource) javaContext.lookup(jndiName);
	}

	@Override
	public Connection getConnection() throws Exception {
		Connection connection = dataSource.getConnection();

		if (isolation != null) {
			connection.setTransactionIsolation(isolation.intValue());
		}

		if (connection.getAutoCommit() != autoCommit) {
			connection.setAutoCommit(autoCommit);
		}

		return ConnectionProxy.wrapConnection(connection);
	}

	@Override
	public void close(Connection connection) throws Exception {
		connection.close();
	}

	@Override
	public void close() throws Exception {
		dataSource = null;
	}
}