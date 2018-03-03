package br.com.jpo.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ClasspathUtils {

	public static Class<?> getClassFromContextClassLoader(String className) throws Exception {
		return getClassFromContextClassLoader(className, null);
	}

	public static Class<?> getClassFromContextClassLoader(String className, ClassLoader loader) throws Exception {
		Class<?> c = null;

		if (loader != null) {
			try {
				c = Class.forName(className, true, loader);
			} catch (Exception ignored) {
			}
		}

		if (c == null) {
			try {
				c = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
			} catch (Exception e) {
				c = Class.forName(className);
			}
		}

		return c;
	}

	public static List<InputStream> getResourceInputStream(String resource) throws URISyntaxException, IOException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		Enumeration<URL> resources = cl.getResources(resource);
		List<InputStream> in = new ArrayList<InputStream>();

		while (resources.hasMoreElements()) {
			URL url = resources.nextElement();
			in.add(url.openStream());
		}

		return in;
	}

	public static InputStream getFirtResourceInputStream(String resource) throws URISyntaxException, IOException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		return cl.getResourceAsStream(resource);
	}

	public static Object getInstance(String className) throws Exception {
		try {
			Class<?> serviceClass = Class.forName(className);
			Constructor<?> constructor = serviceClass.getConstructor();
			Object instance = constructor.newInstance();

			return instance;

		} catch(Exception ex) {
			throw ex;
		}
	}

	public static Object getInstance(String className, Class<?> constructorTypes[], Object[] paramConstructor) throws Exception {
		try {
			Class<?> serviceClass = Class.forName(className);
			Constructor<?> constructor = serviceClass.getConstructor(constructorTypes);
			Object instance = constructor.newInstance(paramConstructor);

			return instance;

		} catch(Exception ex) {
			throw ex;
		}
	}
}