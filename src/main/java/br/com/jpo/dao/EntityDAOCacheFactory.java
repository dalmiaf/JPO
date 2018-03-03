package br.com.jpo.dao;

import br.com.jpo.dao.impl.EntityDAOCache;

public interface EntityDAOCacheFactory {

	EntityDAOCache create();
}