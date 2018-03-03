package br.com.jpo.dao.impl;

import br.com.jpo.dao.EntityDAOCacheFactory;

public class EntityDAOCacheFactoryImpl implements EntityDAOCacheFactory {

	@Override
	public EntityDAOCache create() {
		return EntityDAOCache.getInstance();
	}

}