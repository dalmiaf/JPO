package br.com.jpo.bean;

public class LoadDynamicBeanException extends Exception {

	private static final long serialVersionUID = 8044450076035645337L;
	private static final String msg = "Problemas no carregamento do DynamicBean.";

	public LoadDynamicBeanException() {
		super(msg);
	}

	public LoadDynamicBeanException(String message) {
		super(message);
	}

	public LoadDynamicBeanException(Throwable cause) {
		super(cause);
	}

	public static void throwMe(Throwable error) throws LoadDynamicBeanException {
		if (error instanceof LoadDynamicBeanException) {
			throw (LoadDynamicBeanException) error;
		}

		LoadDynamicBeanException exception = new LoadDynamicBeanException(msg+"\n"+error.getMessage());
		exception.initCause(error);

		throw exception;
	}
}