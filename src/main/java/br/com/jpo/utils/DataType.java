package br.com.jpo.utils;


public enum DataType {

	BIGDECIMAL	("BIGDECIMAL"),
	BOOLEAN		("BOOLEAN"),
	INTEGER		("INTEGER"),
	COLLECTION	("COLLECTION"),
	DOUBLE		("DOUBLE"),
	DYNAMICBEAN	("DYNAMICBEAN"),
	FLOAT		("FLOAT"),
	LONG		("LONG"),
	STRING		("STRING");

	@SuppressWarnings("unchecked")
	DataType(String type) {
		this.type = type;

		try{
			if("BIGDECIMAL".equalsIgnoreCase(type)){
				this.classType = (Class<Object>) Class.forName("java.math.BigDecimal");
			} else if("BOOLEAN".equalsIgnoreCase(type)){
				this.classType = (Class<Object>) Class.forName("java.lang.Boolean");
			} else if("INTEGER".equalsIgnoreCase(type)){
				this.classType = (Class<Object>) Class.forName("java.lang.Integer");
			} else if("COLLECTION".equalsIgnoreCase(type)) {
				this.classType = (Class<Object>) Class.forName("java.util.Collection");
			} else if("DOUBLE".equalsIgnoreCase(type)){
				this.classType = (Class<Object>) Class.forName("java.lang.Double");
			} else if("DYNAMICBEAN".equalsIgnoreCase(type)) {
				this.classType = (Class<Object>) Class.forName("br.com.gpw.utils.dynamic.bean.DynamicBean");
			} else if("FLOAT".equalsIgnoreCase(type)){
				this.classType = (Class<Object>) Class.forName("java.lang.Float");
			} else if("LONG".equalsIgnoreCase(type)){
				this.classType = (Class<Object>) Class.forName("java.lang.Long");
			} else if("STRING".equalsIgnoreCase(type)){
				this.classType = (Class<Object>) Class.forName("java.lang.String");				
			}
		} catch(Exception e){
			// ignore
		}
	}

	private String type;
	private Class<Object> classType;

	public String getType() {
		return type;
	}

	public Class<Object> getClassType() {
		return classType;
	}
}