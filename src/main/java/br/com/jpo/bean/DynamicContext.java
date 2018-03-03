package br.com.jpo.bean;

import br.com.jpo.metadata.entity.Metadata;
import br.com.jpo.session.JPOSession;

public interface DynamicContext {

	JPOSession getSession();

	Metadata getMetadata();
}