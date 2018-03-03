package br.com.jpo.transaction;

import br.com.jpo.session.JPOSessionContext;

public interface JPOTransactionFactory {

	String TRANSACTION_FACTORY = "br.com.jpo.transaction_factory";

	JPOTransaction create(JPOSessionContext sessionContext) throws Exception;

}