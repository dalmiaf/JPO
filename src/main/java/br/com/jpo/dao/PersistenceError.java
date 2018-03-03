package br.com.jpo.dao;

public class PersistenceError extends RuntimeException {

	private static final long serialVersionUID = 8561622571735815550L;

	public PersistenceError() {
		super();
	}

	public PersistenceError(String message) {
		super(message);
	}

	public PersistenceError(Throwable cause) {
		super(cause);
	}

	public PersistenceError(String message, Throwable cause) {
		super(message, cause);
	}

	public static void throwMe(Throwable e) throws PersistenceError {
		if (e instanceof PersistenceError) {
			throw (PersistenceError) e;
		}

		PersistenceError error = new PersistenceError(e.getMessage());
		error.initCause(e);
	}
}