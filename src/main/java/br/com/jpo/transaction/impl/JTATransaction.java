package br.com.jpo.transaction.impl;

import javax.transaction.Status;
import javax.transaction.Synchronization;

import br.com.jpo.transaction.JPOTransaction;
import br.com.jpo.transaction.JPOTransactionException;
import br.com.jpo.transaction.utils.SynchronizationRegistry;
import br.com.jpo.transaction.utils.TransactionDelegate;
import br.com.jpo.transaction.utils.TransactionManagerUtils;

public class JTATransaction implements JPOTransaction {

	private SynchronizationRegistry synchronizationRegistry;
	private TransactionDelegate transaction;
	private boolean begin;
	private int status;

	public JTATransaction() {
		synchronizationRegistry = new SynchronizationRegistry();
		status = Status.STATUS_ACTIVE;
	}

	@Override
	public void begin() throws Exception {
		if (begin) {
			return;
		}

		if (status == Status.STATUS_UNKNOWN) {
			throw new Exception("Não é possível iniciar uma transação com falha de commit.");
		}

		try {
			transaction =  TransactionManagerUtils.getTransaction();

		} catch (Exception e) {
			throw new Exception("A inicialização da transação falhou.", e);
		}

		begin = true;
	}

	@Override
	public void commit() throws Exception {
		if (!begin) {
			throw new Exception("A transação não foi iniciada corretamente.");
		}

		if (status == Status.STATUS_MARKED_ROLLBACK) {
			rollback();
		} else {
			status = Status.STATUS_PREPARING;

			notifySynchronizationsBeforeTransactionCompletion();

			try {
				status = Status.STATUS_COMMITTING;

				transaction.commit();

				status = Status.STATUS_COMMITTED;

				notifySynchronizationsAfterTransactionCompletion(status);
			} catch (Exception e) {
				status = Status.STATUS_UNKNOWN;

				notifySynchronizationsAfterTransactionCompletion(status);

				throw new Exception("O commit falhou.", e);
			}
		}
	}

	@Override
	public void rollback() throws Exception {
		if (!begin && status != Status.STATUS_UNKNOWN) {
			throw new JPOTransactionException("A transação não foi iniciada corretamente ou o processo de commit falhou.");
		}

		if (status != Status.STATUS_UNKNOWN) {
			try {
				transaction.rollback();

				status = Status.STATUS_ROLLEDBACK;

				notifySynchronizationsAfterTransactionCompletion(status);
			} catch (Exception e) {
				status = Status. STATUS_UNKNOWN;

				notifySynchronizationsAfterTransactionCompletion(status);

				throw new JPOTransactionException("O rollback falhou.", e);
			}
		}		
	}

	@Override
	public void setRollbackOnly() throws Exception {
		status = Status.STATUS_MARKED_ROLLBACK;
	}

	@Override
	public boolean isActive() throws Exception {
		return status == Status.STATUS_ACTIVE;
	}

	@Override
	public void registerSynchronization(Synchronization synchronization) throws Exception {
		synchronizationRegistry.registerSynchronization(synchronization);
	}

	private void notifySynchronizationsBeforeTransactionCompletion() {
		synchronizationRegistry.notifySynchronizationsBeforeTransactionCompletion();
	}

	private void notifySynchronizationsAfterTransactionCompletion(int status) {
		synchronizationRegistry.notifySynchronizationsAfterTransactionCompletion(status);
	}

}