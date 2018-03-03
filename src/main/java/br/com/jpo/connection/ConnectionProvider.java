package br.com.jpo.connection;

import java.sql.Connection;
import java.util.Properties;

public interface ConnectionProvider {

	public static final String PROVIDER							= "br.com.jpo.connection.provider_name";
	public static final String USER 							= "br.com.jpo.connection.user";
	public static final String PASSWORD 						= "br.com.jpo.connection.password";
	public static final String URL	 							= "br.com.jpo.connection.url";
	public static final String DRIVER	 						= "br.com.jpo.connection.driver_class";
	public static final String AUTO_COMMIT     			 		= "br.com.jpo.connection.auto_commit";
	public static final String JNDI_NAME						= "br.com.jpo.connection.jndi_name";
	public static final String ISOLATION						= "br.com.jpo.connection.isolation";
	public static final String POOL_SIZE						= "br.com.jpo.connection.pool_size";

	void configure(Properties properties) throws Exception;

	Connection getConnection() throws Exception;

	void close(Connection connection) throws Exception;

	void close() throws Exception;
}