package br.com.jpo.bean;

import java.sql.ResultSet;

import br.com.jpo.metadata.entity.EntityColumnMetadata;
import br.com.jpo.metadata.entity.EntityReferenceMetadata;

public interface DynamicBeanManager {

	DynamicBean loadDefaultDynamicBean() throws LoadDynamicBeanException;

	DynamicBean loadDynamicBeanByInstanceMetadata(DynamicBean bean) throws LoadDynamicBeanException;

	DynamicBean loadDynamicBeanByResultSet(ResultSet resultSet) throws LoadDynamicBeanException;

	DynamicBean loadDynamicBeanByResultSet(ResultSet resultSet, DynamicBean bean) throws LoadDynamicBeanException;

	DynamicBean loadDynamicBeanByResultSet(ResultSet resultSet, DynamicBean bean, boolean useAlias) throws LoadDynamicBeanException;

	DynamicAttribute loadDynamicAttributeByEntityColumnMetadata(EntityColumnMetadata columnMetadata) throws LoadDynamicBeanException;

	DynamicAttribute loadDynamicAttributeByEntityColumnMetadata(DynamicAttribute attribute, EntityColumnMetadata columnMetadata) throws LoadDynamicBeanException;

	DynamicReference loadDynamicReferenceByEntityReferenceMetadata(EntityReferenceMetadata referenceMetadata);

	DynamicReference loadDynamicReferenceByEntityReferenceMetadata(DynamicReference reference, EntityReferenceMetadata referenceMetadata);

}