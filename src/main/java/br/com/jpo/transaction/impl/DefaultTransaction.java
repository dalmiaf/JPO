package br.com.jpo.transaction.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;

import br.com.jpo.session.JPOSessionContext;
import br.com.jpo.transaction.JPOTransaction;
import br.com.jpo.transaction.utils.SynchronizationRegistry;

public class DefaultTransaction implements JPOTransaction {

	private JPOSessionContext sessionContext;
	private Connection connection;
	private SynchronizationRegistry synchronizationRegistry;
	private boolean begin;
	private int status;

	public DefaultTransaction(JPOSessionContext sessionContext) {
		this.sessionContext = sessionContext;
		synchronizationRegistry = new SynchronizationRegistry();
		status = Status.STATUS_ACTIVE;
	}

	@Override
	public void begin() throws Exception {
		if (begin) {
			return;
		}

		connection = sessionContext.getConnection();

		begin = true;
	}

	@Override
	public void commit() throws Exception {
		if (status == Status.STATUS_MARKED_ROLLBACK) {
			rollback();
		} else {
			status = Status.STATUS_PREPARING;

			notifySynchronizationsBeforeTransactionCompletion();

			status = Status.STATUS_COMMITTING;

			try {
				connection.commit();
				connection.close();
			} catch (SQLException sqle) {
				status = Status.STATUS_UNKNOWN;
				throw new SystemException("O commit falhou.");
			}

			status = Status.STATUS_COMMITTED;

			notifySynchronizationsAfterTransactionCompletion(status);
		}
	}

	@Override
	public void rollback() throws Exception {
		if (status != Status.STATUS_UNKNOWN) {
			try {
				status = Status.STATUS_ROLLEDBACK;

				connection.rollback();
				connection.close();

				notifySynchronizationsAfterTransactionCompletion(status);

			} catch (SQLException sqle) {
				status = Status. STATUS_UNKNOWN;

				notifySynchronizationsAfterTransactionCompletion(status);

				throw new SystemException("O rollback falhou.");
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