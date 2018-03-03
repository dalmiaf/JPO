package br.com.jpo.transaction;


public interface JPOTransactionLockContext {

	String getResourceName();

	boolean isWaitFor();

}