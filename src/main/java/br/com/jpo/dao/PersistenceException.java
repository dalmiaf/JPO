package br.com.jpo.dao;

public class PersistenceException extends Exception {

	private static final long serialVersionUID = 5165311059455805143L;

	public PersistenceException() {
        super();
    }

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(Throwable cause) {
        super(cause);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void throwMe(Throwable error) throws PersistenceException {
        if(error instanceof PersistenceException) {
            throw ( PersistenceException ) error;
        }

        if(error instanceof NullPointerException) {
            error.printStackTrace();
        }

        String errorMsg = error.getMessage();

        if(errorMsg == null) {
            errorMsg = "Erro no mecanismo de persistência.";
        }

        PersistenceException persistenceException = new PersistenceException(errorMsg);

        persistenceException.initCause(error);

        throw persistenceException;
    }
}
