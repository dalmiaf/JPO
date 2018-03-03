package br.com.jpo.metadata.entity.listener;

import br.com.jpo.metadata.entity.event.FinderInstanceEvent;
import br.com.jpo.metadata.entity.event.PersistenceInstanceEvent;

public interface InstanceListener {

	void beforeInsert(PersistenceInstanceEvent event) throws Exception;

	void afterInsert(PersistenceInstanceEvent event) throws Exception;

	void beforeUpdate(PersistenceInstanceEvent event) throws Exception;

	void afterUpate(PersistenceInstanceEvent event) throws Exception;

	void beforeDelete(PersistenceInstanceEvent event) throws Exception;

	void afterDelete(PersistenceInstanceEvent event) throws Exception;

	void beforeLoad(FinderInstanceEvent event) throws Exception;
}