package br.com.jpo.metadata.entity.impl;

import br.com.jpo.metadata.entity.EntityColumnMetadata;
import br.com.jpo.metadata.entity.EntityMetadata;


public class EntityColumnMetadataImpl implements EntityColumnMetadata {

	private static final long serialVersionUID = 5861521334685694293L;

	private String name;
	private int sqlType;
	private int length;
	private boolean nullable;
	private boolean primaryKey;
	private Object defaultValue;
	private int precision;
	private boolean autoIncrement;
	private boolean calculated;
	private String description;
	private String expression;
	private String mask;
	private boolean mandatory;
	private String dataType;
	private boolean visible;
	private EntityMetadata entityMetadata;

	public EntityColumnMetadataImpl() {
		
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int getSqlType() {
		return sqlType;
	}

	@Override
	public void setSqlType(int type) {
		this.sqlType = type;
	}

	@Override
	public int getLength() {
		return length;
	}

	@Override
	public void setLength(int length) {
		this.length = length;
	}

	@Override
	public boolean isNullable() {
		return nullable;
	}

	@Override
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	@Override
	public boolean isPrimaryKey() {
		return primaryKey;
	}

	@Override
	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	@Override
	public Object getDefaultValue() {
		return defaultValue;
	}

	@Override
	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public int getPrecision() {
		return precision;
	}

	@Override
	public void setPrecision(int precision) {
		this.precision = precision;
	}

	@Override
	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	@Override
	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	@Override
	public EntityMetadata getEntityMetadata() {
		return entityMetadata;
	}

	@Override
	public void setEntityMetadata(EntityMetadata entityMetadata) {
		this.entityMetadata = entityMetadata;
	}

	@Override
	public boolean isCalculated() {
		return calculated;
	}

	@Override
	public void setCalculated(boolean calculated) {
		this.calculated = calculated;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getExpression() {
		return expression;
	}

	@Override
	public void setExpression(String expression) {
		this.expression = expression;
	}

	@Override
	public String getMask() {
		return mask;
	}

	@Override
	public void setMask(String mask) {
		this.mask = mask;
	}

	@Override
	public boolean isMandatory() {
		return mandatory;
	}

	@Override
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	@Override
	public String getDataType() {
		return dataType;
	}

	@Override
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("EntityColumnMetadata -> "+getName()+" = [ ");
		sb.append(" AUTO_INCREMENT: "+isAutoIncrement());
		sb.append(", CALCULATED: "+isCalculated());
		sb.append(", EXPRESSION: "+getExpression());
		sb.append(", DATA_TYPE: "+getDataType());
		sb.append(", DESCRIPTION: "+getDescription());
		sb.append(", LENGTH: "+getLength());
		sb.append(", MANDATORY: "+isMandatory());
		sb.append(", MASK: "+getMask());
		sb.append(", NULLABLE: "+isNullable());
		sb.append(", PRECISION: "+getPrecision());
		sb.append(", PRIMARY_KEY: "+isPrimaryKey());
		sb.append(", TYPE: "+getSqlType());
		sb.append(", VISIBLE: "+isVisible());
		sb.append(" ]");

		return sb.toString();
	}
}
