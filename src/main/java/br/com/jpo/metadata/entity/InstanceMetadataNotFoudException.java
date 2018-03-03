package br.com.jpo.metadata.entity;

public class InstanceMetadataNotFoudException extends Exception {

	private static final long serialVersionUID = 8558551173065438623L;
	private static final String msg = "Não existe instância configurada com o nome informado.";

	public InstanceMetadataNotFoudException() {
		super(msg);
	}

	public InstanceMetadataNotFoudException(String message) {
		super(message);
	}

	public InstanceMetadataNotFoudException(Throwable cause) {
		super(cause);
	}

	public static void throwMe(Throwable error) throws InstanceMetadataNotFoudException {
		if (error instanceof InstanceMetadataNotFoudException) {
			throw (InstanceMetadataNotFoudException) error;
		}

		InstanceMetadataNotFoudException exception = new InstanceMetadataNotFoudException(msg+"\n"+error.getMessage());
		exception.initCause(error);

		throw exception;
	}
}
