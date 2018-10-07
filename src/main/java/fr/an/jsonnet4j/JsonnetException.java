package fr.an.jsonnet4j;

public class JsonnetException extends RuntimeException {

	/** */
	private static final long serialVersionUID = 1L;

	public JsonnetException(String message) {
		super(message);
	}

	public JsonnetException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
