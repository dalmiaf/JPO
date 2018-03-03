package br.com.jpo.transaction.impl;

import br.com.jpo.session.JPOSessionFactory;
import br.com.jpo.transaction.JPOTransactionLock;
import br.com.jpo.transaction.JPOTransactionLockFactory;

public class JPOTransactionLockFactoryImpl implements JPOTransactionLockFactory {

	private JPOSessionFactory sessionFactory;

	@Override
	public void congigure(JPOSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public JPOTransactionLock create() {
		return JPOTransactionLockImpl.configure(sessionFactory);
	}

}
