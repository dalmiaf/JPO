package br.com.jpo.sql;

import java.util.Map;

import br.com.jpo.metadata.entity.EntityColumnMetadata;
import br.com.jpo.metadata.entity.InstanceMetadataNotFoudException;



public interface SQLServiceProvider {

	/*
	 * Scripts SQL
	 */
	String AND 						= "AND";
	String BETWEEN					= "BETWEEN";
	String DELETE					= "DELETE";
	String FROM 					= "FROM";
	String GROUP_BY 				= "GROUP BY";
	String IN						= "IN";
	String INSERT_INTO				= "INSERT INTO";
	String VALUES					= "VALUES";
	String OR						= "OR";
	String ORDER_BY					= "ORDER BY";
	String SELECT 					= "SELECT";
	String SEPARATOR				= ",";
	String SET						= "SET";
	String UPDATE					= "UPDATE";
	String WHERE 					= "WHERE";

	/*
	 * Operators SQL
	 */
	String EQUAL					= "=";
	String GREATER_THAN_OR_EQUAL	= ">=";
	String LESS_THAN_OR_EQUAL 		= "<=";
	String NOT_EQUAL				= "<>";
	String PARAMETER				= "?";
	String OPEN_PARENTHESES			= "(";
	String CLOSE_PARENTHESES		= ")";

	boolean isUseNamedParameter();

	void setUseNamedParameter(boolean useNamedParameter);

	StringBuffer buildInsert() throws InstanceMetadataNotFoudException;

	StringBuffer buildUpdate() throws InstanceMetadataNotFoudException;

	StringBuffer buildUpdate(Map<String, EntityColumnMetadata> columnsMetadata) throws InstanceMetadataNotFoudException;

	StringBuffer buildDelete() throws InstanceMetadataNotFoudException;

	StringBuffer buildSelectByPrimaryKey() throws InstanceMetadataNotFoudException;

	StringBuffer buildSelectByReference(String name) throws InstanceMetadataNotFoudException;

	StringBuffer buildSelectCustom(String where) throws InstanceMetadataNotFoudException;
}