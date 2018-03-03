package br.com.jpo.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import br.com.jpo.transaction.JPOTransaction;

public class ServiceManager {

	private static Map<String, Class> fullNameServiceClass;

	static {
		fullNameServiceClass = new HashMap<String, Class>();
		fullNameServiceClass.put(DataSetService.class.getSimpleName(), DataSetService.class);
	}

	public static String getFullNameServiceClass(String name) {
		if (fullNameServiceClass.containsKey(name)) {
			return fullNameServiceClass.get(name).getName();
		} else {
			throw new IllegalArgumentException("O Serviço '" +name+ "' não está registrado para a aplicação.");
		}
	}

	@SuppressWarnings("rawtypes")
	public static void registryService(Class clazz) {
		if (fullNameServiceClass.containsKey(clazz.getSimpleName())) {
			throw new IllegalStateException("Não é possível registar serviços com o mesmo nome.");
		}

		fullNameServiceClass.put(clazz.getSimpleName(), clazz);
	}

	@SuppressWarnings("rawtypes")
	public static void unregistryService(Class clazz) {
		fullNameServiceClass.remove(clazz.getSimpleName());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object invokeService(ServiceParameter param) throws Exception {
		try {
			Class serviceClass = Class.forName(getFullNameServiceClass(param.getClassName()));
			Constructor constructor = serviceClass.getConstructor(param.getConstructorTypes());
			Object serviceInstance = constructor.newInstance(param.getParamConstructor());
			Method method = serviceClass.getMethod(param.getMethodName(), param.getMethodTypes());

			return method.invoke(serviceInstance, param.getParamMethod());

		} catch(Exception ex) {
			throw ex;
		}
	}

	public static boolean isRequiredTransaction(ServiceParameter param) throws Exception {
		return containsAnnotation(param, JPOTransaction.class);
	}

	public static boolean containsAnnotation(ServiceParameter param, Class annotation) throws Exception {
		Class serviceClass = Class.forName(getFullNameServiceClass(param.getClassName()));
		Method method = serviceClass.getMethod(param.getMethodName(), param.getMethodTypes());

		Annotation[] annotations = method.getAnnotations();

		boolean containsAnnotation = false;

		if (annotations!= null && annotation != null) {
			for (int i = 0; i < annotations.length; i++) {
				Annotation ann = annotations[i];

				if (ann.annotationType().isAssignableFrom(annotation)) {
					containsAnnotation = true;
					break;
				}
			}
		}

		return containsAnnotation;
	}
}