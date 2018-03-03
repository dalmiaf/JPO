package br.com.jpo.metadata.entity.event;

import java.util.Collection;

import br.com.jpo.bean.DynamicBean;

public class FinderInstanceEvent {

	public static final int   AFTER_LOAD 		= 0;

	Collection<DynamicBean> beans;

	public FinderInstanceEvent(Collection<DynamicBean> beans) {
		this.beans = beans;
	}

	public Collection<DynamicBean> getBeans() {
		return beans;
	}
}
