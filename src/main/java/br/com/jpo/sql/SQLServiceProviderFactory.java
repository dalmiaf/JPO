package br.com.jpo.sql;

import br.com.jpo.dao.EntityDAOContext;

public interface SQLServiceProviderFactory {

	String SQL_SERVICE_PROVIDER_FACTORY = "br.com.jpo.sql.sql_service_provider_factory";

	SQLServiceProvider create(EntityDAOContext context);

}