package br.com.jpo.transaction.impl;

import java.sql.SQLException;

import javax.transaction.Status;
import javax.transaction.Synchronization;

import br.com.jpo.session.JPOSessionContext;
import br.com.jpo.transaction.JPOTransaction;
import br.com.jpo.transaction.JPOTransactionException;
import br.com.jpo.transaction.utils.SynchronizationRegistry;

public class JdbcTransaction implements JPOTransaction {

	private JPOSessionContext sessionContext;
	private SynchronizationRegistry synchronizationRegistry;
	private boolean toggleAutoCommit;
	private boolean begin;
	private int status;

	public JdbcTransaction(JPOSessionContext sessionContext) {
		this.sessionContext = sessionContext;
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
			toggleAutoCommit = sessionContext.getConnection().getAutoCommit();

			if (toggleAutoCommit) {
				sessionContext.getConnection().setAutoCommit(false);
			}
		} catch (SQLException e) {
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

				commitAndResetAutoCommit();

				status = Status.STATUS_COMMITTED;

				notifySynchronizationsAfterTransactionCompletion(status);
			} catch (SQLException e) {
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
				rollbackAndResetAutoCommit();

				status = Status.STATUS_ROLLEDBACK;

				notifySynchronizationsAfterTransactionCompletion(status);
			} catch (SQLException e) {
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

	private void commitAndResetAutoCommit() throws Exception {
		try {
			sessionContext.getConnection().commit();
		} finally {
			toggleAutoCommit();
		}
	}

	private void toggleAutoCommit() throws Exception {
		if (toggleAutoCommit) {
			sessionContext.getConnection().setAutoCommit(true);
		}
	}

	private void rollbackAndResetAutoCommit() throws Exception {
		try {
			sessionContext.getConnection().rollback();
		} finally {
			toggleAutoCommit();
		}
	}

	private void notifySynchronizationsBeforeTransactionCompletion() {
		synchronizationRegistry.notifySynchronizationsBeforeTransactionCompletion();
	}

	private void notifySynchronizationsAfterTransactionCompletion(int status) {
		synchronizationRegistry.notifySynchronizationsAfterTransactionCompletion(status);
	}
}