package br.com.jpo.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

import br.com.jpo.bean.DynamicBeanManagerFactory;
import br.com.jpo.bean.impl.DynamicBeanManagerFactoryImpl;
import br.com.jpo.connection.ConnectionProvider;
import br.com.jpo.connection.impl.DataSourceConnectionProvider;
import br.com.jpo.dao.EntityDAOFactory;
import br.com.jpo.dao.impl.EntityDAOCacheFactoryImpl;
import br.com.jpo.dao.impl.EntityDAOFactoryImpl;
import br.com.jpo.metadata.entity.InstanceMetadataFactory;
import br.com.jpo.metadata.entity.impl.InstanceMetadataFactoryImpl;
import br.com.jpo.service.ServiceManager;
import br.com.jpo.service.ServiceParameter;
import br.com.jpo.session.JPOSession;
import br.com.jpo.session.JPOSessionFactory;
import br.com.jpo.session.impl.JPOSessionFactoryImpl;
import br.com.jpo.sql.SQLServiceProviderFactory;
import br.com.jpo.sql.impl.SQLServiceProviderFactoryImpl;
import br.com.jpo.transaction.JPOTransactionFactory;
import br.com.jpo.transaction.JPOTransactionLockFactory;
import br.com.jpo.transaction.impl.JPOTransactionLockFactoryImpl;
import br.com.jpo.transaction.impl.JTATransactionFactory;
import br.com.jpo.utils.JsonUtils;
import br.com.jpo.utils.StringUtils;

public class HttpServiceServlet extends HttpServlet {

	private static final long serialVersionUID = -841420474330705943L;
	private JPOSessionFactory sessionFactory;

	private static Properties properties;

	static {
		properties = new Properties();

		properties.setProperty(ConnectionProvider.PROVIDER, DataSourceConnectionProvider.class.getName());
		properties.setProperty(ConnectionProvider.JNDI_NAME, "jdbc/Datasource");
		properties.setProperty(JPOTransactionFactory.TRANSACTION_FACTORY, JTATransactionFactory.class.getName());
		properties.setProperty(EntityDAOFactory.ENTITY_DAO_FACTORY, EntityDAOFactoryImpl.class.getName());
		properties.setProperty(InstanceMetadataFactory.INSTANCE_METADATA_FACTORY, InstanceMetadataFactoryImpl.class.getName());
		properties.setProperty(SQLServiceProviderFactory.SQL_SERVICE_PROVIDER_FACTORY, SQLServiceProviderFactoryImpl.class.getName());
		properties.setProperty(DynamicBeanManagerFactory.DYNAMIC_BEAN_MANAGER_FACTORY, DynamicBeanManagerFactoryImpl.class.getName());
		properties.setProperty(EntityDAOFactory.ENTITY_DAO_CACHE_FACTORY, EntityDAOCacheFactoryImpl.class.getName());
		properties.setProperty(JPOTransactionLockFactory.TRANSACTION_LOCK_FACTORY, JPOTransactionLockFactoryImpl.class.getName());
	}

	@Override
	public void destroy() {
		super.destroy();
	}

	@Override
	public void init() throws ServletException {
		super.init();
		try {
			sessionFactory = JPOSessionFactoryImpl.configure(properties);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JsonObject requestJson = JsonUtils.parse(request.getReader());
		String service = JsonUtils.getAsString(requestJson, "serviceName");
		JsonObject requestBody = JsonUtils.getAsJsonObject(requestJson, "requestBody");

		String serviceName[] = StringUtils.splitDot(service);
		Object returnService = null;
		JPOSession session = null;
		boolean isRequiredTransaction = false;

		try {
			String className = StringUtils.getEmptyAsNull(serviceName, 0);
			String methodName = StringUtils.getEmptyAsNull(serviceName, 1);

			ServiceParameter paramRequiredTransaction = new ServiceParameter(className, methodName);
			isRequiredTransaction = ServiceManager.isRequiredTransaction(paramRequiredTransaction);

			sessionFactory.openSession();
			session = sessionFactory.getCurrentSession();

			if (isRequiredTransaction) {
				session.beginTransaction();
			}

			ServiceParameter parameter = new ServiceParameter(className, methodName, session, requestBody);
			returnService = ServiceManager.invokeService(parameter);

			if (isRequiredTransaction) {
				session.commit();
			}

		} catch (Exception e) {
			e.printStackTrace();

			if (session != null && isRequiredTransaction) {
				try {
					session.rollback();
				} catch (Exception ignored) {
				}
			}

			JsonObject error = new JsonObject();
			error.addProperty("status", 0);
			error.addProperty("statusMessage", e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
			returnService = error;
		} finally {
			try {
				sessionFactory.closeSession(session);
			} catch (Exception ignored) {
			}
		}

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.write(StringUtils.toStringOrEmpty(returnService));
		out.flush();
		out.close();
	}

}