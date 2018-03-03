package br.com.jpo.transaction.utils;

import java.lang.reflect.Method;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

public class TransactionManagerDelegate {

	private Object transactionManager;

	public TransactionManagerDelegate(Object transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void begin() throws NotSupportedException, SystemException {
		invokeMethodVoid(getMethod("begin", null), null);
	}

	public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
		invokeMethodVoid(getMethod("commit", null), null);
	}

	public int getStatus() throws SystemException {
		return (int) invokeMethodWhitReturn(getMethod("getStatus", null), null);
	}

	public Object getTransaction() throws SystemException {
		return invokeMethodWhitReturn(getMethod("getTransaction", null), null);
	}

	public void resume(Object transaction) throws InvalidTransactionException, IllegalStateException, SystemException {
		Class[] parameterTypes = new Class[] { transaction.getClass() };
		Object[] parameterValues = new Object[] { transaction };

		invokeMethodVoid(getMethod("resume", parameterTypes), parameterValues);
	}

	public void rollback() throws IllegalStateException, SecurityException, SystemException {
		invokeMethodVoid(getMethod("rollback", null), null);
	}

	public void setRollbackOnly() throws IllegalStateException, SystemException {
		invokeMethodVoid(getMethod("setRollbackOnly", null), null);
	}

	public void setTransactionTimeout(int timeout) throws SystemException {
		Class[] parameterTypes = new Class[] { int.class };
		Object[] parameterValues = new Object[] { timeout };

		invokeMethodVoid(getMethod("setTransactionTimeout", parameterTypes), parameterValues);
	}

	public Object suspend() throws SystemException {
		return invokeMethodWhitReturn(getMethod("suspend", null), null);
	}

	private void invokeMethodVoid(Method method, Object[] parameterValues) {
		try{
			method.invoke(transactionManager, parameterValues);
		} catch(Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private Object invokeMethodWhitReturn(Method method, Object[] parameterValues) {
		Object returnValue = null;

		try{
			returnValue = method.invoke(transactionManager, parameterValues);
		} catch(Exception e) {
			throw new IllegalStateException(e);
		}

		return returnValue;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Method getMethod(String methodName, Class[] parameterTypes) {
		Class clazz = transactionManager.getClass();

		Method method = null;

		try {
			method = clazz.getMethod(methodName, parameterTypes);
		} catch(Exception e) {
			e.printStackTrace();
			throw new IllegalStateException("Não foi possível identificar o método.");
		}

		return method;
	}
}