package br.com.jpo.transaction;

public interface JPOTransactionLock {

	void lockResource(JPOTransactionLockContext context) throws Exception;
}