package br.com.jpo.connection.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import br.com.jpo.connection.ConnectionProvider;
import br.com.jpo.utils.PropertiesUtils;

public class DriverManagerConnectionProvider implements ConnectionProvider {

	private String							user;
	private String							password;
	private String 							url;
	private Integer 						isolation;
	private final ArrayList<Connection> 	pool = new ArrayList<Connection>();
	private int 							poolSize;
	private boolean 						autocommit;

	@Override
	public void configure(Properties properties) throws Exception {
		String driverClass = properties.getProperty(DRIVER);

		user = properties.getProperty(USER);
		password = properties.getProperty(PASSWORD);
		poolSize = PropertiesUtils.getPropertyAsInt(properties, POOL_SIZE, 20);
		autocommit = PropertiesUtils.getPropertyAsBoolean(properties, AUTO_COMMIT, false);
		isolation = PropertiesUtils.getPropertyAsInteger(properties, ISOLATION, null);
		url = properties.getProperty(URL);

		if (driverClass == null) {
			throw new Exception("No JDBC Driver class was specified by property "+ DRIVER);
		} else {
			try {
				Class.forName(driverClass);
			} catch (ClassNotFoundException e) {
				throw new Exception("JDBC Driver class not found: " + driverClass, e);
			}
		}

		if (url == null) {
			throw new Exception("JDBC URL was not specified by property "+URL);
		}
	}

	@Override
	public Connection getConnection() throws Exception {
		synchronized (pool) {
			if (!pool.isEmpty()) {
				int last = pool.size() - 1;
				Connection pooled = (Connection) pool.remove(last);

				if (isolation != null) {
					pooled.setTransactionIsolation(isolation.intValue());
				}

				if (pooled.getAutoCommit() != autocommit) {
					pooled.setAutoCommit(autocommit);
				}

				return pooled;
			}
		}

		Connection conn = DriverManager.getConnection(url, user, password);

		if (isolation != null) {
			conn.setTransactionIsolation(isolation.intValue());
		}

		if (conn.getAutoCommit() != autocommit) {
			conn.setAutoCommit(autocommit);
		}

		return ConnectionProxy.wrapConnection(conn);
	}

	@Override
	public void close(Connection connection) throws Exception {
		synchronized (pool) {
			int currentSize = pool.size();
			if (currentSize < poolSize) {
				pool.add(connection);
				return;
			}
		}

		connection.close();
	}

	@Override
	public void close() throws Exception {
		Iterator<Connection> iter = pool.iterator();

		while (iter.hasNext()) {
			iter.next().close();
		}

		pool.clear();
	}

}
