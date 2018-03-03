package br.com.jpo.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class OpenSessionFilter implements Filter {

	@Override
	public void destroy() {
		// nada a fazer
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		/*JPOSession session = null;

		try {
			JPOSessionFactory.getInstance().openSession();
			session = JPOSessionFactory.getInstance().getCurrentSession();
			session.beginTransaction();

			chain.doFilter(request, response);

			session.commit();

		} catch(Exception ex) {
			if (session != null) {
				try {
					session.rollback();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			ex.printStackTrace();
		} finally {
			try {
				sessionFactory.closeSession(session);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}*/
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// nada a fazer
	}
}