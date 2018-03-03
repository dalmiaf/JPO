package br.com.jpo.metadata.entity;

public class InitializeInstanceMetadataException extends Exception {

	private static final long serialVersionUID = 8513829472892204506L;
	private static final String msg = "Problemas na inicialização dos metadados das colunas da entidade.";

	public InitializeInstanceMetadataException() {
		super(msg);
	}

	public InitializeInstanceMetadataException(String message) {
		super(message);
	}

	public InitializeInstanceMetadataException(Throwable cause) {
		super(cause);
	}
	
	public static void throwMe(Throwable error) throws InitializeInstanceMetadataException {
		if (error instanceof InitializeInstanceMetadataException) {
			throw (InitializeInstanceMetadataException) error;
		}

		InitializeInstanceMetadataException exception = new InitializeInstanceMetadataException(msg+"\n"+error.getMessage());
		exception.initCause(error);

		throw exception;
	}
}
