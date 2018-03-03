package br.com.jpo.transaction.utils;

import java.util.LinkedHashSet;

import javax.transaction.Synchronization;

public class SynchronizationRegistry {

	private LinkedHashSet<Synchronization> synchronizations;

	public SynchronizationRegistry() {
		synchronizations = new LinkedHashSet<Synchronization>();
	}

	public void registerSynchronization(Synchronization synchronization) throws Exception {
		if (synchronization == null) {
			throw new Exception("Synchronization não pode ser nulo.");
		}

		synchronizations.add(synchronization);
	}

	public void notifySynchronizationsBeforeTransactionCompletion() {
		if (synchronizations != null) {
			for (Synchronization synchronization : synchronizations) {
				synchronization.beforeCompletion();
			}
		}
	}

	public void notifySynchronizationsAfterTransactionCompletion(int status) {
		if (synchronizations != null) {
			for (Synchronization synchronization : this.synchronizations) {
				synchronization.afterCompletion(status);
			}
		}
	}
}
