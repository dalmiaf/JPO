package br.com.jpo.sql.impl;

import br.com.jpo.dao.EntityDAOContext;
import br.com.jpo.sql.SQLServiceProvider;
import br.com.jpo.sql.SQLServiceProviderFactory;

public class SQLServiceProviderFactoryImpl implements SQLServiceProviderFactory {

	@Override
	public SQLServiceProvider create(EntityDAOContext context) {
		return new SQLServiceProviderImpl(context);
	}

}