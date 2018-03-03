package br.com.jpo.service.interfaces;

import br.com.jpo.session.JPOSession;

public abstract class ServiceBase {

	protected JPOSession session;

	public ServiceBase(JPOSession session) {
		this.session = session;
	}
}
