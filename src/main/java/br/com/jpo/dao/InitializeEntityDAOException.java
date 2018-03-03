package br.com.jpo.dao;

public class InitializeEntityDAOException extends Exception {

	private static final long serialVersionUID = 8150473361787710442L;

	private static final String msg = "Problemas na inicialização do EntityDAO.";

	public InitializeEntityDAOException() {
		super(msg);
	}

	public InitializeEntityDAOException(String message) {
		super(message);
	}

	public InitializeEntityDAOException(Throwable cause) {
		super(cause);
	}

	public static void throwMe(Throwable error) throws InitializeEntityDAOException {
		if (error instanceof InitializeEntityDAOException) {
			throw (InitializeEntityDAOException) error;
		}

		InitializeEntityDAOException exception = new InitializeEntityDAOException(msg+"\n"+error.getMessage());
		exception.initCause(error);

		throw exception;
	}
}