package br.com.jpo.transaction;

public class JPOTransactionException extends RuntimeException {

	private static final long serialVersionUID = 1106829619395309021L;

	public JPOTransactionException(Throwable root) {
		super(root);
	}

	public JPOTransactionException(String string, Throwable root) {
		super(string, root);
	}

	public JPOTransactionException(String s) {
		super(s);
	}
}
