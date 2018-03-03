package br.com.jpo;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.jpo.bean.DynamicBean;
import br.com.jpo.bean.DynamicBeanManagerFactory;
import br.com.jpo.bean.impl.DynamicBeanManagerFactoryImpl;
import br.com.jpo.connection.ConnectionProvider;
import br.com.jpo.connection.impl.DriverManagerConnectionProvider;
import br.com.jpo.dao.EntityDAOFactory;
import br.com.jpo.dao.FinderCustom;
import br.com.jpo.dao.impl.EntityDAOCacheFactoryImpl;
import br.com.jpo.dao.impl.EntityDAOFactoryImpl;
import br.com.jpo.dao.impl.FinderCustomImpl;
import br.com.jpo.metadata.entity.InstanceMetadataFactory;
import br.com.jpo.metadata.entity.impl.InstanceMetadataFactoryImpl;
import br.com.jpo.session.JPOSession;
import br.com.jpo.session.JPOSessionFactory;
import br.com.jpo.session.impl.JPOSessionFactoryImpl;
import br.com.jpo.sql.SQLServiceProviderFactory;
import br.com.jpo.sql.impl.SQLServiceProviderFactoryImpl;
import br.com.jpo.transaction.JPOTransactionFactory;
import br.com.jpo.transaction.JPOTransactionLockFactory;
import br.com.jpo.transaction.impl.DefaultTransactionFactory;
import br.com.jpo.transaction.impl.JPOTransactionLockFactoryImpl;

public class TesteJPOSession {

	private JPOSessionFactory sessionFactory = null;
	private JPOSession session = null;
	private static Properties properties;

	static {
		properties = new Properties();

		properties.setProperty(ConnectionProvider.PROVIDER, DriverManagerConnectionProvider.class.getName());
		properties.setProperty(ConnectionProvider.USER, "root");
		properties.setProperty(ConnectionProvider.URL, "jdbc:mysql://localhost:3306/CONTAS");
		properties.setProperty(ConnectionProvider.DRIVER, "com.mysql.jdbc.Driver");
		properties.setProperty(ConnectionProvider.PASSWORD, "javaseven");
		properties.setProperty(JPOTransactionFactory.TRANSACTION_FACTORY, DefaultTransactionFactory.class.getName());
		properties.setProperty(EntityDAOFactory.ENTITY_DAO_FACTORY, EntityDAOFactoryImpl.class.getName());
		properties.setProperty(InstanceMetadataFactory.INSTANCE_METADATA_FACTORY, InstanceMetadataFactoryImpl.class.getName());
		properties.setProperty(SQLServiceProviderFactory.SQL_SERVICE_PROVIDER_FACTORY, SQLServiceProviderFactoryImpl.class.getName());
		properties.setProperty(DynamicBeanManagerFactory.DYNAMIC_BEAN_MANAGER_FACTORY, DynamicBeanManagerFactoryImpl.class.getName());
		properties.setProperty(EntityDAOFactory.ENTITY_DAO_CACHE_FACTORY, EntityDAOCacheFactoryImpl.class.getName());
		properties.setProperty(JPOTransactionLockFactory.TRANSACTION_LOCK_FACTORY, JPOTransactionLockFactoryImpl.class.getName());
	}

	@Before
	public void initializeSession() throws Exception {
		JPOSessionFactoryImpl.configure(properties);
		sessionFactory = JPOSessionFactoryImpl.getInstance();
		sessionFactory.openSession();
		session = sessionFactory.getCurrentSession();
		session.beginTransaction();
	}

	@After
	public void finalizeSession() throws Exception {
		try{
			if (session.hasTransaction()) {
				session.commit();
			}
		} catch(Exception e) {
			session.rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	@Test
	public void testeJPOSession() throws Exception {
		Map<String, Object> primaryKey = new HashMap<String, Object>();
		primaryKey.put("CODPERMISSAO", BigDecimal.valueOf(1));

		DynamicBean bean = session.findByPrimaryKey("Permissao", primaryKey);

		System.out.println(bean.asString("DESCRPERMISSAO"));
	}

	@Test
	public void testeSessionSave() throws Exception {
		DynamicBean bean = session.getDefaultBean("Parceiro");

		bean.setAttribute("NOMEPARCEIRO", "Teste Session Save");
		bean.setAttribute("CLIENTE", "N");
		bean.setAttribute("FORNECEDOR", "S");
		bean.setAttribute("CPFCNPJ", "66666666666");
		bean.setAttribute("ATIVO", "S");

		session.save(bean);
	}

	@Test
	public void testeJPOSessionCache() throws Exception {
		Map<String, Object> primaryKey = new HashMap<String, Object>();
		primaryKey.put("CODPARCEIRO", BigDecimal.valueOf(7));

		DynamicBean bean = session.findByPrimaryKey("Parceiro", primaryKey);

		System.out.println(bean.asString("NOMEPARCEIRO"));

		bean = session.findByPrimaryKey("Parceiro", primaryKey);

		bean.setAttribute("NOMEPARCEIRO", "kkkkkkkkkkkkkkkk");
		session.update(bean);

		bean = session.findByPrimaryKey("Parceiro", primaryKey);

		System.out.println(bean.asString("NOMEPARCEIRO"));
	}

	@Test
	public void testeJPOSessionCache2() throws Exception {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String where = "this.ATIVO = 'S'";

		FinderCustom finder = new FinderCustomImpl("Parceiro", where, parameters);
		Collection<DynamicBean> beans = session.findCustom(finder);

		for (DynamicBean bean: beans) {
			System.out.println(bean.getAttribute("NOMEPARCEIRO"));
		}

		boolean invalidateCache = true;

		if (invalidateCache) {
			for (DynamicBean bean: beans) {
				if (BigDecimal.valueOf(7).intValue() == bean.asInteger("CODPARCEIRO")) {
					bean.setAttribute("NOMEPARCEIRO", "bbbbbbbbbbbbbbbbbbbbbb");
					session.update(bean);
				}
			}
		}

		beans = session.findCustom(finder);

		for (DynamicBean bean: beans) {
			System.out.println(bean.getAttribute("NOMEPARCEIRO"));
		}
	}

	@Test
	public void testeSessionSaveComChaveComposta() throws Exception {
		DynamicBean notaBean = session.getDefaultBean("Nota");
		notaBean.setAttribute("NUMNOTA", BigDecimal.valueOf(123));

		session.save(notaBean);

		DynamicBean itemNotaBean = session.getDefaultBean("ItemNota");
		itemNotaBean.setAttribute("IDNOTA", notaBean.getAttribute("IDNOTA"));

		session.save(itemNotaBean);

		DynamicBean itemNotaBean2 = session.getDefaultBean("ItemNota");
		itemNotaBean2.setAttribute("IDNOTA", notaBean.getAttribute("IDNOTA"));

		session.save(itemNotaBean2);

	}

	@Test
	public void testeTableKeyGenerator() throws Exception {
		DynamicBean bean = session.getDefaultBean("GrupoUsuario");
		bean.setAttribute("DESCRGRUPO", "Teste Table Key Gererator");

		session.save(bean);
	}

	public static void main(String args[]) throws Exception {
		TesteJPOSession teste = new TesteJPOSession();
		teste.initializeSession();

		teste.testeJPOSessionCache();

		teste.finalizeSession();
	}
}
