package br.com.jpo.metadata.entity.impl;

import java.util.ArrayList;
import java.util.Collection;

import br.com.jpo.dao.KeyGenerator;
import br.com.jpo.metadata.entity.EntityKeyMetadata;

public class EntityKeyMetadataImpl implements EntityKeyMetadata {

	private static final long serialVersionUID = 5130189444461902010L;

	private String name;
	private String description;
	private String type;
	private String keyField;
	private KeyGenerator keyGenerator;
	private Collection<String> keyMembers;

	public EntityKeyMetadataImpl(KeyGenerator keyGenerator, Collection<String> keyMembers) {
		this.keyGenerator = keyGenerator;
		this.keyMembers = keyMembers;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getKeyField() {
		return keyField;
	}

	@Override
	public void setKeyField(String keyField) {
		this.keyField = keyField;
	}

	@Override
	public KeyGenerator getKeyGenerator() {
		return keyGenerator;
	}

	@Override
	public Collection<String> getKeyMembers() {
		if (keyMembers == null) {
			keyMembers = new ArrayList<String>();
		}

		return keyMembers;
	}
}
