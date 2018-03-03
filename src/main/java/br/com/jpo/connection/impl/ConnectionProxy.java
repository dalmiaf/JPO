package br.com.jpo.connection.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class ConnectionProxy implements InvocationHandler {

	private Connection 			delegateConnection;
	private int					statementCounter;

	private ConnectionProxy(Connection connection) throws Exception {
		this.delegateConnection = connection;
	}

	public static Connection wrapConnection(Connection connection) throws Exception {
		ConnectionProxy proxy = new ConnectionProxy(connection);

		return (Connection) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { Connection.class }, proxy);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		try {
			String methodName = method.getName();
			Object result = null;

			if ("close".equals(methodName)) {
				result = method.invoke(delegateConnection, args);
			} else if ("getMetaData".equals(methodName)) {
				DatabaseMetadataProxy sp = new DatabaseMetadataProxy((DatabaseMetaData) method.invoke(delegateConnection, args), proxy);
				result = (DatabaseMetaData) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { DatabaseMetaData.class }, sp);
			} else if ("prepareCall".equals(methodName)) {
				result = method.invoke(delegateConnection, args);
			} else if ("prepareStatement".equals(methodName)) {
				result = method.invoke(delegateConnection, args);
			} else {
				result = method.invoke(delegateConnection, args);
			}

			if (result instanceof Statement) {
				StatementProxy sp = null;

				if (result instanceof CallableStatement) {
					sp = new StatementProxy((Statement) result, null, statementCounter);
					result = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { CallableStatement.class }, sp);
				} else if (result instanceof PreparedStatement) {
					sp = new StatementProxy((Statement) result, (String) args[0], statementCounter);
					result = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { PreparedStatement.class }, sp);
				} else {
					sp = new StatementProxy((Statement) result, null, statementCounter);
					result = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { Statement.class }, sp);
				}

				sp.proxy = result;
				statementCounter++;
			}

			return result;
		} catch(Exception ex) {
			throw ex.getCause();
		} finally {
			
		}
	}

	private class DatabaseMetadataProxy implements InvocationHandler {
		DatabaseMetaData	databaseMetaDataDelegate;
		Object				connectionProxy;

		DatabaseMetadataProxy(DatabaseMetaData databaseMetaData, Object connectionProxy) {
			this.databaseMetaDataDelegate = databaseMetaData;
			this.connectionProxy = connectionProxy;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			try {
				String methodName = method.getName();
				Object result = null;

				if ("getConnection".equals(methodName)) {
					return connectionProxy;
				} else {
					result = method.invoke(databaseMetaDataDelegate, args);
				}

				if (result instanceof ResultSet) {
					ResultSetMetadataProxy rsProxy = new ResultSetMetadataProxy((ResultSet) result);
					result = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { ResultSet.class }, rsProxy);
				}

				return result;
			} catch (InvocationTargetException e) {
				throw e.getCause();
			}
		}
	}

	private class ResultSetMetadataProxy implements InvocationHandler {
		ResultSet resultSetDelegate;

		public ResultSetMetadataProxy(ResultSet resultSet) {
			this.resultSetDelegate = resultSet;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			try {
				String methodName = method.getName();
				Object result = null;

				result = method.invoke(resultSetDelegate, args);

				return result;
			} catch (InvocationTargetException e) {
				throw e.getCause();
			}
		}
	}

	private class StatementProxy implements InvocationHandler {
		Map<Integer, Object>		resulSets;
		Integer						keyStatement;
		Object						proxy;
		Statement					delegateStatement;
		String						preparedSql;
		int							resultSetCounter;
		boolean						statementIsClosing;
		Map<Object, Object>			params;

		StatementProxy(Statement stm, String sql, int key) {
			delegateStatement = stm;
			resulSets = new HashMap<Integer, Object>();
			preparedSql = sql;
			keyStatement = new Integer(key);
			params = new HashMap<Object, Object>();
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			try {
				String methodName = method.getName();
				Object result = null;

				if ("close".equals(methodName)) {
					result = method.invoke(delegateStatement, args);
				} else if ("cancel".equals(methodName)) {
					result = method.invoke(delegateStatement, args);
				} else if ("execute".equals(methodName)) {
					result = method.invoke(delegateStatement, args);
				} else if ("executeQuery".equals(methodName)) {
					result = method.invoke(delegateStatement, args);
				} else if ("executeUpdate".equals(methodName)) {
					result = method.invoke(delegateStatement, args);
				} else {
					result = method.invoke(delegateStatement, args);
				}

				if (result instanceof ResultSet) {
					Integer rsetID = new Integer(resultSetCounter++);

					ResultSetProxy rsProxy = new ResultSetProxy((ResultSet) result, rsetID);
					result = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { ResultSet.class }, rsProxy);

					resulSets.put(rsetID, result);
				}

				return result;
			} catch (InvocationTargetException e) {
				throw e.getCause();
			}
		}

		private class ResultSetProxy implements InvocationHandler {
			ResultSet	delegateResultSet;
			Integer		keyRset;

			ResultSetProxy(ResultSet resultSet, Integer key) {
				delegateResultSet = resultSet;
				keyRset = key;
			}

			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				try {
					String methodName = method.getName();
					Object result = null;

					if ("close".equals(methodName)) {
						result = method.invoke(delegateResultSet, args);
					} else if ("next".equals(methodName)) {
						result = method.invoke(delegateResultSet, args);
					} else if ("updateRow".equals(methodName)) {
						result = method.invoke(delegateResultSet, args);
					} else if ("getMetaData".equals(methodName)) {
						result = method.invoke(delegateResultSet, args);
					} else if("getString".equals(methodName)){
						result = method.invoke(delegateResultSet, args);
					} else {
						result = method.invoke(delegateResultSet, args);
					}

					return result;
				} catch (InvocationTargetException e) {
					throw e.getCause();
				}
			}
		}
	}
}