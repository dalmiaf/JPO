package br.com.jpo.metadata.entity.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import br.com.jpo.bean.DynamicBean;
import br.com.jpo.dao.EntityDAO;
import br.com.jpo.metadata.entity.listener.InstanceListener;

public class InstanceEventDispatcher {

	private EntityDAO entityDAO;

	public InstanceEventDispatcher(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}

	public void dispatchPersistenceInstanceEvent(int event, DynamicBean bean) throws Exception {
		dispatchPersistenceInstanceEvent(event, bean, null);
	}

	public void dispatchPersistenceInstanceEvent(int event, DynamicBean bean, Map<String, Object> modifiedFields) throws Exception {
		try {
			Collection<InstanceListener> listeners = entityDAO.getInstanceMetadata().getInstanceListeners();

			if (listeners != null && !listeners.isEmpty()) {
				for (InstanceListener listener: listeners) {
					PersistenceInstanceEvent instanceEvent = new PersistenceInstanceEvent(bean, modifiedFields);

					if (event == PersistenceInstanceEvent.BEFORE_INSERT) {
						listener.beforeInsert(instanceEvent);
					} else if (event == PersistenceInstanceEvent.AFTER_INSERT) {
						listener.afterInsert(instanceEvent);
					} else if (event == PersistenceInstanceEvent.BEFORE_UPDATE) {
						listener.beforeUpdate(instanceEvent);
					} else if (event == PersistenceInstanceEvent.AFTER_UPDATE) {
						listener.afterUpate(instanceEvent);
					} else if (event == PersistenceInstanceEvent.BEFORE_DELETE) {
						listener.beforeDelete(instanceEvent);
					} else if (event == PersistenceInstanceEvent.AFTER_DELETE) {
						listener.afterDelete(instanceEvent);
					}
				}
			}
		} catch(Exception e) {
			throw e;
		}
	}

	public void dispatchFinderInstanceEvent(int event, DynamicBean bean) throws Exception {
		Collection<DynamicBean> beans = new ArrayList<DynamicBean>();
		beans.add(bean);

		dispatchFinderInstanceEvent(event, beans);
	}

	public void dispatchFinderInstanceEvent(int event, Collection<DynamicBean> beans) throws Exception {
		try {
			Collection<InstanceListener> listeners = entityDAO.getInstanceMetadata().getInstanceListeners();

			if (listeners != null && !listeners.isEmpty()) {
				for (InstanceListener listener: listeners) {
					FinderInstanceEvent finderEvent = new FinderInstanceEvent(beans);

					if (event == FinderInstanceEvent.AFTER_LOAD) {
						listener.beforeLoad(finderEvent);
					}
				}
			}
		} catch(Exception e) {
			throw e;
		}
	}
}