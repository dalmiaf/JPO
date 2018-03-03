package br.com.jpo.transaction.impl;

import java.util.HashMap;
import java.util.Map;

import javax.transaction.Status;
import javax.transaction.Synchronization;

import br.com.jpo.session.JPOSession;
import br.com.jpo.session.JPOSessionFactory;
import br.com.jpo.transaction.JPOTransaction;
import br.com.jpo.transaction.JPOTransactionLock;
import br.com.jpo.transaction.JPOTransactionLockContext;

public class JPOTransactionLockImpl implements JPOTransactionLock {

	private static JPOTransactionLockImpl 		transactionLock;
	private Map<String, Object>					lockedResources;
	private JPOSessionFactory					sessionFactory;

	private JPOTransactionLockImpl(JPOSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		lockedResources = new HashMap<String, Object>();
	}

	public static JPOTransactionLockImpl configure(JPOSessionFactory sessionFactory) {
		if (transactionLock == null) {
			transactionLock = new JPOTransactionLockImpl(sessionFactory);
		}

		return transactionLock;
	}

	public static JPOTransactionLockImpl getInstance() throws Exception {
		if (transactionLock == null) {
			throw new Exception("Não existe instância configurada de JPOTransactionLockImpl.");
		}

		return transactionLock;
	}

	@Override
	public void lockResource(JPOTransactionLockContext context) throws Exception {
		String resourceName = context.getResourceName();
		boolean waitFor = context.isWaitFor();

		if (sessionFactory.hasCurrentSession()) {
			JPOSession session = sessionFactory.getCurrentSession();

			if (session.hasTransaction()) {
				JPOTransaction tx = session.getTransaction();

				/* Soh assinalamos o lock para TXs ativas */
				if ((tx != null) && (tx.isActive())) {
					synchronized (lockedResources) {
						Object lockClaimer = session;
						Object lockOwner = lockedResources.get(resourceName);

						boolean registrySync = true;

						if (lockOwner != null) { // já existe lock para resource
							if (!lockOwner.equals(lockClaimer)) { // Não somos os donos do lock
								if (waitFor) {
									do {
										lockedResources.wait(2000);
										lockOwner = lockedResources.get(resourceName);

										// Se o dono do lock é outra sessão então devemos tratar o deadlock
										if (lockClaimer instanceof JPOSession && lockOwner instanceof JPOSession) {
											
										}
									} while (lockedResources.containsKey(resourceName));
								} else {
									throw new Exception("Recurso locado por outra sessão/transação.");
								}
							} else {
								registrySync = false; //se o lock já é da Thread corrente então não precisamos de registrar outro syncronization
							}
						}

						if (registrySync) {
							// Criamos um listener para esta TX, assim conseguiremos liberar o lock ao final da mesma.
							final LockSynchronization synchronization = new LockSynchronization(resourceName);
							tx.registerSynchronization(synchronization);

							//se temos uma sessão então registramos um unlocker que será usado caso essa Thread entre em hang.
							//Desta forma quando a sessão der timeout esse lock será liberado
							if (lockClaimer instanceof JPOSession) {
								((JPOSession) lockClaimer).registryUnlocker(new Runnable() {
									public void run() {
										synchronization.afterCompletion(Status.STATUS_ROLLEDBACK);
									}
								});
							}
						}

						// marcamos o recurso como locked.
						lockedResources.put(resourceName, lockClaimer);
					}
				}
			}
		}
	}

	private class LockSynchronization implements Synchronization {
		String	resourceName;

		public LockSynchronization(String resourceName) {
			this.resourceName = resourceName;
		}

		public void afterCompletion(int status) {
			synchronized (lockedResources) {
				lockedResources.remove(resourceName);
				lockedResources.notifyAll();
			}
		}

		public void beforeCompletion() {
			
		}
	}

}
