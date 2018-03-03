package br.com.jpo.transaction;

import javax.transaction.Synchronization;

public interface JPOTransaction {

	void begin() throws Exception;

	void commit() throws Exception;

	void rollback() throws Exception;

	void setRollbackOnly() throws Exception;

	boolean isActive() throws Exception;

	void registerSynchronization(Synchronization synchronization) throws Exception;
}