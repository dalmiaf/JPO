package br.com.jpo.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.com.jpo.dao.KeyGenerator;
import br.com.jpo.metadata.entity.EntityColumnMetadata;
import br.com.jpo.metadata.entity.EntityKeyMetadata;
import br.com.jpo.metadata.entity.EntityMetadata;
import br.com.jpo.metadata.entity.EntityReferenceMetadata;
import br.com.jpo.metadata.entity.InstanceMetadata;
import br.com.jpo.metadata.entity.listener.InstanceListener;

public class MetadataProxyManager {

	public static InstanceMetadata getInstanceMetadataProxy(final InstanceMetadata instanceMetadata) {
		InvocationHandler invocation = new InvocationHandler() {

			private InstanceMetadata delegate = instanceMetadata;
			private Map<String, Object> values = new HashMap<String, Object>();
			private Map<String, EntityReferenceMetadata> references = new HashMap<String, EntityReferenceMetadata>();
			private List<InstanceListener> listeners = new ArrayList<InstanceListener>();

			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				String methodName = method.getName();
				Object result = null;
				boolean callDelegate = true;

				boolean notArguments = (args == null || args.length == 0);
				boolean onlyOneArgument = (args != null && args.length == 1);

				if ("addInstanceListener".equals(methodName) && onlyOneArgument) {
					Object obj = args[0];

					if (obj != null && obj instanceof InstanceListener) {
						InstanceListener listener = (InstanceListener) obj;

						listeners.add(listener);
					}
				} if ("removeInstanceListener".equals(methodName) && onlyOneArgument) {
					Object obj = args[0];

					if (obj != null && obj instanceof InstanceListener) {
						InstanceListener listener = (InstanceListener) obj;

						listeners.remove(listener);
					}
				} else if ("addEntityReferenceMetadata".equals(methodName) && onlyOneArgument) {
					Object obj = args[0];

					if (obj != null && obj instanceof EntityReferenceMetadata) {
						EntityReferenceMetadata entityReferenceMetadata = (EntityReferenceMetadata) obj;

						references.put(entityReferenceMetadata.getName(), entityReferenceMetadata);
						callDelegate = false;
					}
				} else if ("getEntityReferenceMetadata".equals(methodName) && onlyOneArgument) {
					Object obj = args[0];

					if (obj != null && obj instanceof String) {
						String propName = (String) obj;

						if (references.containsKey(propName)) {
							result = references.get(propName);
							callDelegate = false;
						}
					}
				} else {
					boolean isSetter = methodName.startsWith("set") && onlyOneArgument;
					boolean isGetter = (methodName.startsWith("get") || methodName.startsWith("is")) && notArguments;

					if (isSetter) {
						String propName = methodName.substring(3).toLowerCase();
						Object propValue = (args != null ? args[0] : null);

						values.put(propName, propValue);
						callDelegate = false;
					} else if (isGetter) {
						String propName = methodName.startsWith("is") ? methodName.substring(2) : methodName.substring(3);

						if (values.containsKey(propName)) {
							result = values.get(propName);
							callDelegate = false;
						}
					}
				}

				if (callDelegate) {
					result = method.invoke(delegate, args);

					if ("getEntityMetadata".equals(methodName)) {
						result = getEntityMetadataProxy((EntityMetadata) result);
					} else if ("getEntityReferenceMetadata".equals(methodName)) {
						result = getEntityReferenceMetadataProxy((EntityReferenceMetadata) result);
					}
				}

				return result;
			}

		};

		return (InstanceMetadata) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { InstanceMetadata.class }, invocation);
	}

	public static EntityMetadata getEntityMetadataProxy(final EntityMetadata entityMetadata) {
		InvocationHandler invocation = new InvocationHandler() {

			private EntityMetadata delegate = entityMetadata;
			private Map<String, Object> values = new HashMap<String, Object>();
			private Map<String, EntityColumnMetadata> columns = new HashMap<String, EntityColumnMetadata>();
			private EntityKeyMetadata keyMetadata = null;

			@SuppressWarnings("unchecked")
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				String methodName = method.getName();
				Object result = null;
				boolean callDelegate = true;

				boolean notArguments = (args == null || args.length == 0);
				boolean onlyOneArgument = (args != null && args.length == 1);

				if ("addEntityColumnMetadata".equals(methodName) && onlyOneArgument) {
					Object obj = args[0];

					if (obj != null && obj instanceof EntityReferenceMetadata) {
						EntityColumnMetadata entityColumnMetadata = (EntityColumnMetadata) obj;

						columns.put(entityColumnMetadata.getName(), entityColumnMetadata);
						callDelegate = false;
					}
				} else if ("getEntityColumnMetadata".equals(methodName) && onlyOneArgument) {
					Object obj = args[0];

					if (obj != null && obj instanceof String) {
						String propName = (String) obj;

						if (columns.containsKey(propName)) {
							result = columns.get(propName);
							callDelegate = false;
						}
					}
				} else if ("getEntityKeyMetadata".equals(methodName)) {
					if (keyMetadata != null) {
						result = keyMetadata;
						callDelegate = false;
					}
				} else if ("setEntityKeyMetadata".equals(methodName) && onlyOneArgument) {
					Object obj = args[0];

					if (obj == null || obj instanceof EntityKeyMetadata) {
						keyMetadata = (EntityKeyMetadata) obj;
						callDelegate = false;
					}
				} else {
					boolean isSetter = methodName.startsWith("set") && onlyOneArgument;
					boolean isGetter = (methodName.startsWith("get") || methodName.startsWith("is")) && notArguments;

					if (isSetter) {
						String propName = methodName.substring(3).toLowerCase();
						Object propValue = (args != null ? args[0] : null);

						values.put(propName, propValue);
						callDelegate = false;
					} else if (isGetter) {
						String propName = methodName.startsWith("is") ? methodName.substring(2) : methodName.substring(3);

						if (values.containsKey(propName)) {
							result = values.get(propName);
							callDelegate = false;
						}
					}
				}

				if (callDelegate) {
					result = method.invoke(delegate, args);

					if ("getEntityColumnMetadata".equals(methodName)) {
						if (result != null && result instanceof EntityColumnMetadata) {
							result = getEntityColumnMetadataProxy((EntityColumnMetadata) result);
						}
					} else if ("getEntityColumnsMetadata".equals(methodName)) {
						if (result != null && result instanceof Map) {
							Map<String, EntityColumnMetadata> columnsOrig = (Map<String, EntityColumnMetadata>) result;
							Map<String, EntityColumnMetadata> columnsDest = new HashMap<String, EntityColumnMetadata>();

							if (!columns.isEmpty()) {
								columnsDest.putAll(columns);
							}

							for (Iterator<Entry<String, EntityColumnMetadata>> iterator = columnsOrig.entrySet().iterator(); iterator.hasNext();) {
								Entry<String, EntityColumnMetadata> entry = iterator.next();
								EntityColumnMetadata entityColumnMetadata = entry.getValue();

								columnsDest.put(entityColumnMetadata.getName(), getEntityColumnMetadataProxy(entityColumnMetadata));
							}

							result = columnsDest;
						}
					} else if ("getEntityColumnMetadataKeySequence".equals(methodName)) {
						if (result != null && result instanceof EntityColumnMetadata) {
							result = getEntityColumnMetadataProxy((EntityColumnMetadata) result);
						}
					} else if ("getEntityKeyMetadata".equals(methodName)) {
						if (result != null && result instanceof EntityKeyMetadata) {
							result = getEntityKeyMetadataProxy((EntityKeyMetadata) result);
						}
					}
				}

				return result;
			}

		};

		return (EntityMetadata) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { EntityMetadata.class }, invocation);
	}

	public static EntityReferenceMetadata getEntityReferenceMetadataProxy(final EntityReferenceMetadata entityReferenceMetadata) {
		InvocationHandler invocation = new InvocationHandler() {

			private EntityReferenceMetadata delegate = entityReferenceMetadata;
			private Map<String, Object> values = new HashMap<String, Object>();

			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				String methodName = method.getName();
				Object result = null;
				boolean callDelegate = true;

				boolean notArguments = (args == null || args.length == 0);
				boolean onlyOneArgument = (args != null && args.length == 1);

				if ("TRATA OUTROS CASOS".equals(methodName) && onlyOneArgument) {
				} else {
					boolean isSetter = methodName.startsWith("set") && onlyOneArgument;
					boolean isGetter = (methodName.startsWith("get") || methodName.startsWith("is")) && notArguments;

					if (isSetter) {
						String propName = methodName.substring(3).toLowerCase();
						Object propValue = (args != null ? args[0] : null);

						values.put(propName, propValue);
						callDelegate = false;
					} else if (isGetter) {
						String propName = methodName.startsWith("is") ? methodName.substring(2) : methodName.substring(3);

						if (values.containsKey(propName)) {
							result = values.get(propName);
							callDelegate = false;
						}
					}
				}

				if (callDelegate) {
					result = method.invoke(delegate, args);

					if ("getInstanceMetadata".equals(methodName)) {
						result = getInstanceMetadataProxy((InstanceMetadata) result);
					}
				}

				return result;
			}
		};

		return (EntityReferenceMetadata) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { EntityReferenceMetadata.class }, invocation);
	}

	public static EntityColumnMetadata getEntityColumnMetadataProxy(final EntityColumnMetadata entityColumnMetadata) {
		InvocationHandler invocation = new InvocationHandler() {

			private EntityColumnMetadata delegate = entityColumnMetadata;
			private Map<String, Object> values = new HashMap<String, Object>();

			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				String methodName = method.getName();
				Object result = null;
				boolean callDelegate = true;

				boolean notArguments = (args == null || args.length == 0);
				boolean onlyOneArgument = (args != null && args.length == 1);

				if ("TRATA OUTROS CASOS".equals(methodName) && onlyOneArgument) {
				} else {
					boolean isSetter = methodName.startsWith("set") && onlyOneArgument;
					boolean isGetter = (methodName.startsWith("get") || methodName.startsWith("is")) && notArguments;

					if (isSetter) {
						String propName = methodName.substring(3).toLowerCase();
						Object propValue = (args != null ? args[0] : null);

						values.put(propName, propValue);
						callDelegate = false;
					} else if (isGetter) {
						String propName = methodName.startsWith("is") ? methodName.substring(2) : methodName.substring(3);

						if (values.containsKey(propName)) {
							result = values.get(propName);
							callDelegate = false;
						}
					}
				}

				if (callDelegate) {
					result = method.invoke(delegate, args);

					if ("getEntityMetadata".equals(methodName)) {
						result = getEntityMetadataProxy((EntityMetadata) result);
					}
				}

				return result;
			}
		};

		return (EntityColumnMetadata) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { EntityColumnMetadata.class }, invocation);
	}

	public static EntityKeyMetadata getEntityKeyMetadataProxy(final EntityKeyMetadata entityKeyMetadata) {
		InvocationHandler invocation = new InvocationHandler() {

			private EntityKeyMetadata delegate = entityKeyMetadata;
			private Map<String, Object> values = new HashMap<String, Object>();
			private Collection<String> KeyMembers;

			@SuppressWarnings("unchecked")
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				String methodName = method.getName();
				Object result = null;
				boolean callDelegate = true;

				boolean notArguments = (args == null || args.length == 0);
				boolean onlyOneArgument = (args != null && args.length == 1);

				if ("getKeyMembers".equals(methodName)) {
					if (KeyMembers == null) {
						KeyMembers = new ArrayList<String>();

						result = method.invoke(delegate, args);

						if (result instanceof Collection) {
							Collection<String> columnsMetadata = (Collection<String>) result;

							for (String key: columnsMetadata) {
								KeyMembers.add(key);
							}
						}
					}

					result = KeyMembers;
					callDelegate = false;
				} else {
					boolean isSetter = methodName.startsWith("set") && onlyOneArgument;
					boolean isGetter = (methodName.startsWith("get") || methodName.startsWith("is")) && notArguments;

					if (isSetter) {
						String propName = methodName.substring(3).toLowerCase();
						Object propValue = (args != null ? args[0] : null);

						values.put(propName, propValue);
						callDelegate = false;
					} else if (isGetter) {
						String propName = methodName.startsWith("is") ? methodName.substring(2) : methodName.substring(3);

						if (values.containsKey(propName)) {
							result = values.get(propName);
							callDelegate = false;
						}
					}
				}

				if (callDelegate) {
					result = method.invoke(delegate, args);

					if ("getKeyGenerator".equals(methodName) && result instanceof KeyGenerator) {
						result = getKeyGeneratorProxy((KeyGenerator) result);
					}
				}

				return result;
			}

		};

		return (EntityKeyMetadata) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { EntityKeyMetadata.class }, invocation);
	}

	public static KeyGenerator getKeyGeneratorProxy(final KeyGenerator keyGenerator) {
		InvocationHandler invocation = new InvocationHandler() {

			private KeyGenerator delegate = keyGenerator;

			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				String methodName = method.getName();
				Object result = null;
				boolean callDelegate = true;

				if ("generateKey".equals(methodName)) {
					result = method.invoke(delegate, args);
					callDelegate = false;
				}

				if (callDelegate) {
					result = method.invoke(delegate, args);
				}

				return result;
			}

		};

		return (KeyGenerator) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { KeyGenerator.class }, invocation);
	}
}