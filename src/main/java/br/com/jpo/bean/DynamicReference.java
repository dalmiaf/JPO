package br.com.jpo.bean;

import br.com.jpo.metadata.entity.EntityReferenceMetadata;


public interface DynamicReference extends DynamicObject {

	DynamicBean getBean();

	void setBean(DynamicBean bean);

	void setEntityReferenceMetadata(EntityReferenceMetadata entityReferenceMetadata);

	EntityReferenceMetadata getEntityReferenceMetadata();

	void loadReference();

	DynamicReference buildClone();

}