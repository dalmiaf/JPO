package br.com.jpo.metadata.entity;

import java.io.Serializable;

/**
 * Essa classe representa cada Estrutura de metadados, todas as classes que 
 * represemtam metadados deve estender ou implementar essa interface.
 * Contém todas as informções necessárias de metadados.
 * 
 * @author Dalmi Alves Ferreira
 *
 */
public interface Metadata extends Serializable {

	int ORACLE_DIALECT							= 0;
	int MYSQL_DIALECT						    = 1;
	int MSSQL_DIALECT							= 2;

	String COLUMN_TYPE							= "COLUMN_TYPE";
	String GRANTEE								= "GRANTEE";
	String GRANTOR								= "GRANTOR";
	String IS_GRANTABLE							= "IS_GRANTABLE";
	String KEY_SEQ								= "KEY_SEQ";
	String LENGTH								= "LENGTH";
	String PK_NAME								= "PK_NAME";
	String PRECISION							= "PRECISION";
	String PRIVILEGE							= "PRIVILEGE";
	String RADIX								= "RADIX";
	String REMARKS 								= "REMARKS";
	String SCALE								= "SCALE";
	String SCOPE								= "SCOPE";
	String TABLE								= "TABLE";
	String TABLE_CAT 							= "TABLE_CAT";
	String TABLE_CATALOG						= "TABLE_CATALOG";
	String TABLE_NAME 							= "TABLE_NAME";
	String TABLE_SCHEM 							= "TABLE_SCHEM";
	String TYPE_NAME							= "TYPE_NAME";
	String VIEW									= "VIEW";

	String getName();

	void setName(String name);

	String getDescription();

	void setDescription(String description);
}