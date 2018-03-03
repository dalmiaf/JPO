package br.com.jpo.service;

import com.google.gson.JsonObject;

import br.com.jpo.session.JPOSession;

public class ServiceParameter {

	private JPOSession session;
	private JsonObject request;
	private String className;
	private String methodName;
	private Object[] paramConstructor;
	private Class constructorTypes[];
	private Object[] paramMethod;
	private Class methodTypes[];

	public ServiceParameter(String className, String methodName) {
		this(className, methodName, null, null);
	}

	public ServiceParameter(String className, String methodName, JPOSession session, JsonObject request) {
		this.className = className;
		this.methodName = methodName;
		this.session = session;
		this.request = request;
		paramConstructor = new Object[] { session };
		constructorTypes = new Class[] { JPOSession.class };
		paramMethod = new Object[] { request };
		methodTypes = new Class[] { JsonObject.class };
	}

	public JPOSession getSession() {
		return session;
	}

	public void setSession(JPOSession session) {
		this.session = session;
	}

	public JsonObject getRequest() {
		return request;
	}

	public void setRequest(JsonObject request) {
		this.request = request;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Object[] getParamConstructor() {
		return paramConstructor;
	}

	public void setParamConstructor(Object[] paramConstructor) {
		this.paramConstructor = paramConstructor;
	}

	public Class[] getConstructorTypes() {
		return constructorTypes;
	}

	public void setConstructorTypes(Class[] constructorTypes) {
		this.constructorTypes = constructorTypes;
	}

	public Object[] getParamMethod() {
		return paramMethod;
	}

	public void setParamMethod(Object[] paramMethod) {
		this.paramMethod = paramMethod;
	}

	public Class[] getMethodTypes() {
		return methodTypes;
	}

	public void setMethodTypes(Class[] methodTypes) {
		this.methodTypes = methodTypes;
	}
}