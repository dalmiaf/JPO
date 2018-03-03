package br.com.jpo.transaction.impl;

import br.com.jpo.transaction.JPOTransactionLockContext;

public class JPOTransactionLockContextImpl implements JPOTransactionLockContext {

	private String resourceName;
	private boolean waitFor;

	public JPOTransactionLockContextImpl(String resourceName, boolean waitFor) {
		this.resourceName = resourceName;
		this.waitFor = waitFor;
	}

	@Override
	public String getResourceName() {
		return resourceName;
	}

	@Override
	public boolean isWaitFor() {
		return waitFor;
	}

}