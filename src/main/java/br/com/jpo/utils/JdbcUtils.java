package br.com.jpo.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import javax.activation.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;

import br.com.jpo.connection.ConnectionProvider;
import br.com.jpo.metadata.entity.Metadata;

public class JdbcUtils {

	public static Connection getConnection(Properties properties) throws Exception {
		String driver = properties.getProperty(ConnectionProvider.DRIVER);
		String url = properties.getProperty(ConnectionProvider.URL);
		String user = properties.getProperty(ConnectionProvider.USER);
		String password = properties.getProperty(ConnectionProvider.PASSWORD);

		Class.forName(driver);

		return DriverManager.getConnection(url, user, password);
	}

	public static DataSource lookup(String dsName) throws Exception {
		Context context = new InitialContext();
		Context javaContext = (Context) context.lookup("java:");
		DataSource dataSource = (DataSource) javaContext.lookup(dsName);

		return dataSource;
	}

	public static boolean isDataBaseConnection(Connection connection, int dbConnection) throws Exception {
		DatabaseMetaData md = connection.getMetaData();
		String databaseName = md.getDatabaseProductName().toUpperCase();

		if (dbConnection == Metadata.MYSQL_DIALECT) {
			return databaseName.indexOf("MYSQL") > -1;
		} else if (dbConnection ==  Metadata.ORACLE_DIALECT) {
			return databaseName.indexOf("ORACLE") > -1;
		} else if(dbConnection == Metadata.MSSQL_DIALECT) {
			return databaseName.indexOf("SQL SERVER") > -1;
		} else {
			return false;
		}
	}
	
	public static void close(Connection connection, Statement statement, ResultSet resultSet) {
		close(connection);
		close(statement);
		close(resultSet);
	}

	public static void close(Connection connection) {
		try {
			if (connection != null){
				connection.close();
			}
		} catch (Exception erro) {
			erro.printStackTrace();
		}
	}

	public static void close(Statement statement) {
		try {
			if (statement != null){
				statement.close();
			}
		} catch (Exception erro) {
			erro.printStackTrace();
		}
	}

	public static void close(ResultSet resultSet) {
		try {
			if (resultSet != null){
				resultSet.close();
			}
		} catch (Exception erro) {
			erro.printStackTrace();
		}
	}
}