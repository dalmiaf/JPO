package br.com.jpo.transaction;

import br.com.jpo.session.JPOSessionFactory;

public interface JPOTransactionLockFactory {

	String TRANSACTION_LOCK_FACTORY  = "br.com.jpo.transaction.transaction_lock_factory";

	void congigure(JPOSessionFactory sessionFactory);

	JPOTransactionLock create();

}