package br.com.jpo.dao;

import java.util.Collection;
import java.util.Map;

import br.com.jpo.bean.DynamicBean;
import br.com.jpo.metadata.entity.InstanceMetadata;

public interface EntityDAO {

	void save(DynamicBean bean) throws Exception;

	void update(DynamicBean bean) throws Exception;

	void delete(DynamicBean bean) throws Exception;

	void loadRelationship(DynamicBean bean, String referenceName) throws Exception;

	DynamicBean getDefaultBean() throws Exception;

	InstanceMetadata getInstanceMetadata() throws Exception;

	DynamicBean findByPrimaryKey(Map<String, Object> primaryKey) throws Exception;

    Collection<DynamicBean> findCustom(FinderCustom finder) throws Exception;
}