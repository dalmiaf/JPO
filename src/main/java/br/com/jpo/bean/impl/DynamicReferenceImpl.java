package br.com.jpo.bean.impl;

import br.com.jpo.bean.DynamicBean;
import br.com.jpo.bean.DynamicReference;
import br.com.jpo.dao.EntityDAO;
import br.com.jpo.dao.EntityDAOFactory;
import br.com.jpo.metadata.entity.EntityReferenceMetadata;

public class DynamicReferenceImpl implements DynamicReference {

	private static final long serialVersionUID = 1287799416969524739L;

	private String name;
	private DynamicBean bean;
	private EntityReferenceMetadata entityReferenceMetadata;
	private EntityDAOFactory entityDAOFactory;

	public DynamicReferenceImpl(EntityDAOFactory entityDAOFactory) {
		this.entityDAOFactory = entityDAOFactory;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String referenceName) {
		this.name = referenceName;
	}

	@Override
	public DynamicBean getBean() {
		return bean;
	}

	@Override
	public void setBean(DynamicBean bean) {
		this.bean = bean;
	}

	@Override
	public EntityReferenceMetadata getEntityReferenceMetadata() {
		return entityReferenceMetadata;
	}

	@Override
	public void setEntityReferenceMetadata(EntityReferenceMetadata entityReferenceMetadata) {
		this.entityReferenceMetadata = entityReferenceMetadata;
	}

	@Override
	public void loadReference() {
		try {
			EntityDAO dao = entityDAOFactory.create(bean.getName());
			dao.loadRelationship(bean, name);
		} catch(Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public DynamicReference buildClone() {
		DynamicReference clone = null;

		clone = new DynamicReferenceImpl(entityDAOFactory);

		clone.setName(getName());

		if (getBean() != null) {
			clone.setBean(getBean().buildClone());
		}

		return clone;
	}

}