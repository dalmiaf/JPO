package br.com.jpo.transaction.utils;

import java.lang.reflect.Method;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.xa.XAResource;

public class TransactionDelegate implements Transaction {

	private Object transaction;

	public TransactionDelegate(Object transaction) {
		this.transaction = transaction;
	}

	@Override
	public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, SystemException {
		invokeMethodVoid(getMethod("commit", null), null);
	}

	@Override
	public boolean delistResource(XAResource arg0, int arg1) throws IllegalStateException, SystemException {
		Class[] parameterTypes = new Class[] { XAResource.class, int.class };
		Object[] parameterValues = new Object[] { arg0, arg1 };

		return (boolean) invokeMethodWhitReturn(getMethod("delistResource", parameterTypes), parameterValues);
	}

	@Override
	public boolean enlistResource(XAResource arg0) throws RollbackException, IllegalStateException, SystemException {
		Class[] parameterTypes = new Class[] { XAResource.class };
		Object[] parameterValues = new Object[] { arg0 };

		return (boolean) invokeMethodWhitReturn(getMethod("enlistResource", parameterTypes), parameterValues);
	}

	@Override
	public int getStatus() throws SystemException {
		return (Integer) invokeMethodWhitReturn(getMethod("getStatus", null), null);
	}

	@Override
	public void registerSynchronization(Synchronization arg0) throws RollbackException, IllegalStateException, SystemException {
		Class[] parameterTypes = new Class[] { Synchronization.class };
		Object[] parameterValues = new Object[] { arg0 };

		invokeMethodVoid(getMethod("", parameterTypes), parameterValues);
	}

	@Override
	public void rollback() throws IllegalStateException, SystemException {
		invokeMethodVoid(getMethod("rollback", null), null);
	}

	@Override
	public void setRollbackOnly() throws IllegalStateException, SystemException {
		invokeMethodVoid(getMethod("setRollbackOnly", null), null);
	}

	private void invokeMethodVoid(Method method, Object[] parameterValues) {
		try{
			method.invoke(transaction, parameterValues);
		} catch(Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private Object invokeMethodWhitReturn(Method method, Object[] parameterValues) {
		Object returnValue = null;

		try{
			returnValue = method.invoke(transaction, parameterValues);
		} catch(Exception e) {
			throw new IllegalStateException(e);
		}

		return returnValue;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Method getMethod(String methodName, Class[] parameterTypes) {
		Class clazz = transaction.getClass();

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