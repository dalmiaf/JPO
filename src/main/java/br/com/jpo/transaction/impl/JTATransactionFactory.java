package br.com.jpo.transaction.impl;

import br.com.jpo.session.JPOSessionContext;
import br.com.jpo.transaction.JPOTransaction;
import br.com.jpo.transaction.JPOTransactionFactory;

public class JTATransactionFactory implements JPOTransactionFactory {

	@Override
	public JPOTransaction create(JPOSessionContext sessionContext) throws Exception {
		return new JTATransaction();
	}

}