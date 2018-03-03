package br.com.jpo.metadata.entity;

/**
 * Essa classe representa cada Campo/Atributo de uma Entidade/Tabela mapeada no dicionário de dados.
 * Comtém todas as informções necessárias de metadados do atributo.
 * 
 * @author Dalmi Alves Ferreira
 *
 */
public interface EntityColumnMetadata extends Metadata {

	// Metadados relativos ao DatabaseMetaData
	String BUFFER_LENGTH 						= "BUFFER_LENGTH";
	String CHAR_OCTET_LENGTH 					= "CHAR_OCTET_LENGTH";
	String COLUMN_DEF 							= "COLUMN_DEF";
	String COLUMN_NAME 							= "COLUMN_NAME";
	String COLUMN_SIZE 							= "COLUMN_SIZE";
	String DATA_TYPE 							= "DATA_TYPE";
	String DECIMAL_DIGITS 						= "DECIMAL_DIGITS";
	String IS_AUTOINCREMENT						= "IS_AUTOINCREMENT";
	String IS_GENERATEDCOLUMN					= "IS_GENERATEDCOLUMN";
	String IS_NULLABLE							= "IS_NULLABLE";
	String NULLABLE 							= "NULLABLE";
	String NUM_PREC_RADIX 						= "NUM_PREC_RADIX";
	String ORDINAL_POSITION 					= "ORDINAL_POSITION";
	String SCOPE_CATALOG 						= "SCOPE_CATALOG";
	String SCOPE_SCHEMA 						= "SCOPE_SCHEMA";
	String SCOPE_TABLE							= "SCOPE_TABLE";
	String SOURCE_DATA_TYPE						= "SOURCE_DATA_TYPE";
	String SQL_DATA_TYPE 						= "SQL_DATA_TYPE";
	String SQL_DATETIME_SUB 					= "SQL_DATETIME_SUB";

	// Metadados relativos ao Dicionário de Dados
	String CALCULADO							= "CALCULADO";
	String DESCRCAMPO							= "DESCRCAMPO";
	String EXPRESSAO							= "EXPRESSAO";
	String IDCAMPO								= "IDCAMPO";
	String MASCARA								= "MASCARA";
	String NOMECAMPO							= "NOMECAMPO";
	String NOMETABELA							= "NOMETABELA";
	String OBRIGATORIO							= "OBRIGATORIO";
	String TDDCAMPO								= "TDDCAMPO";
	String TIPOCAMPO							= "TIPOCAMPO";
	String VISIVEL								= "VISIVEL";

	/*
	 * A inclusão de métodos aqui requer atenção na Classe MetadataProxyManager, pois essa
	 * é responsável por criar o proxy EntityColumnMetadata.
	 */

	int getSqlType();

	void setSqlType(int sqlType);

	int getLength();

	void setLength(int length);

	boolean isNullable();

	void setNullable(boolean nullable);

	boolean isPrimaryKey();

	void setPrimaryKey(boolean primaryKey);

	Object getDefaultValue();

	void setDefaultValue(Object defaultValue);

	int getPrecision();

	void setPrecision(int precision);

	boolean isAutoIncrement();

	void setAutoIncrement(boolean autoIncrement);

	EntityMetadata getEntityMetadata();

	void setEntityMetadata(EntityMetadata entityMetadata);

	boolean isCalculated();

	void setCalculated(boolean calculated);

	String getExpression();

	void setExpression(String expression);

	String getMask();

	void setMask(String mask);

	boolean isMandatory();

	void setMandatory(boolean mandatory);

	String getDataType();

	void setDataType(String dataType);

	boolean isVisible();

	void setVisible(boolean visible);
}