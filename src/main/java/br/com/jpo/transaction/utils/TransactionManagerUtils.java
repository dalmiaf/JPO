package br.com.jpo.transaction.utils;

import java.lang.reflect.Method;

import javax.naming.InitialContext;
import javax.transaction.Status;


public class TransactionManagerUtils {

	private static Object transactionManager;
	private static Object userTransaction;

	private static final String[] defaultTransactionNames = { "java:/TransactionManager", "java:comp/pm/TransactionManager", "java:comp/TransactionManager", "java:comp/UserTransaction" };

	private static synchronized Object lookupTransactionManager(String transactionName) throws Exception {
		if (transactionManager == null) {
			String[] names = null;

			if (transactionName == null) {
				names = defaultTransactionNames;
			} else {
				names = new String[]{transactionName};
			}

			for (int i = 0; i < names.length; i++) {
				try {
					Object txManager = new InitialContext().lookup(names[i]);

					transactionManager = new TransactionManagerDelegate(txManager);

					break;

				} catch (Exception e) {
					if ((i + 1) >= names.length) {
						throw e;
					}
				}
			}
		}

		return transactionManager;
	}

	private static synchronized Object lookupUserTransaction() throws Exception {
		if (userTransaction == null) {
			try {
				userTransaction = new InitialContext().lookup("java:comp/UserTransaction");
			} catch (Exception e) {
				throw e;
			}
		}

		return userTransaction;
	}

	public static Object getTransactionManager() throws Exception {
		return lookupTransactionManager(null);
	}

	public static Object getUserTransaction() throws Exception {
		return lookupUserTransaction();
	}

	public static TransactionDelegate getTransaction() throws Exception {
		return getTransactionInternal("java:/TransactionManager");
	}

	public static TransactionDelegate getTransaction(String transactionName) throws Exception {
		return getTransactionInternal(transactionName);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static TransactionDelegate getTransactionInternal(String transactionName) throws Exception {
		Object transactionManager = TransactionManagerUtils.lookupTransactionManager(transactionName);

		Class clazz = transactionManager.getClass();

		Method begin = clazz.getMethod("begin", null);
		Method suspend = clazz.getMethod("suspend", null);
		Method getTransaction = clazz.getMethod("getTransaction", null);
		Method getStatus = clazz.getMethod("getStatus", null);

		if (suspend != null) {
			suspend.invoke(transactionManager, null);
		}

		if (getStatus != null) {
			Object result = getStatus.invoke(transactionManager, null);

			if (result instanceof Integer) {
				Integer status = (Integer) result;

				if (status.equals(Status.STATUS_NO_TRANSACTION)) {
					if (begin != null) {
						begin.invoke(transactionManager, null);
					}
				}
			}
		}

		TransactionDelegate transaction = null;

		if (getTransaction != null) {
			Object result = getTransaction.invoke(transactionManager, null);

			if (result != null) {
				transaction = new TransactionDelegate(result);
			}
		}

		return transaction;
	}
}