package br.com.jpo.connection.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import br.com.jpo.utils.JdbcUtils;

public class JdbcWrapper {

	private Connection connection;

	public JdbcWrapper(Connection connection) {
		this.connection = connection;
	}

	public PreparedStatement getPreparedStatementForSearch(String sql) throws Exception {
        return getPreparedStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    }

	public PreparedStatement getPreparedStatement(String sql) throws Exception {
		return getPreparedStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
	}

	public PreparedStatement getPreparedStatement(String sql, int resultSetType, int resultSetConcurrency) throws Exception {
        PreparedStatement pstm = PreparedStatementProxy.wrapStatement(getConnection(), sql, resultSetType, resultSetConcurrency);

        return pstm;
    }

	public Connection getConnection() throws SQLException {
		return connection;
	}

	public DatabaseMetaData getMetaData() throws SQLException {
		Connection connection = getConnection();

		return connection.getMetaData();
	}

	public void close() {
		try {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (Exception ignored) {
			}
		} finally {
			connection = null;
		}
	}

	public static void close(JdbcWrapper jdbcWrapper) {
		if (jdbcWrapper != null) {
			jdbcWrapper.close();
		}
	}

	public static void close(Connection connection, Statement statement, ResultSet resultSet) {
		close(connection);
		close(statement);
		close(resultSet);
	}

	public static void close(Connection connection) {
		JdbcUtils.close(connection);
	}

	public static void close(Statement statement) {
		JdbcUtils.close(statement);
	}

	public static void close(ResultSet resultSet) {
		JdbcUtils.close(resultSet);
	}
}