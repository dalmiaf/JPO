package br.com.jpo.metadata.entity.event;

import java.util.HashMap;
import java.util.Map;

import br.com.jpo.bean.DynamicBean;

public class PersistenceInstanceEvent {

	public static final int   BEFORE_INSERT 	= 0;
    public static final int   AFTER_INSERT 		= 1;
    public static final int   BEFORE_DELETE 	= 2;
    public static final int   AFTER_DELETE 		= 3;
    public static final int   BEFORE_UPDATE 	= 4;
    public static final int   AFTER_UPDATE 		= 5;

    private DynamicBean bean;
    private Map<String, Object> modifiedFields;

    public PersistenceInstanceEvent(DynamicBean bean) {
    	this(bean, null);
    }

    public PersistenceInstanceEvent(DynamicBean bean, Map<String, Object> modifiedFields) {
    	this.bean = bean;
    	this.modifiedFields = modifiedFields;
    }

	public DynamicBean getBean() {
		return bean;
	}

	public Map<String, Object> getModifiedFields() {
		if (modifiedFields == null) {
			modifiedFields = new HashMap<String, Object>();
		}

		return modifiedFields;
	}
}