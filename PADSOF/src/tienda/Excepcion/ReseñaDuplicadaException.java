package Excepcion;

public class ReseñaDuplicadaException extends CheckPointException {
	private static final long serialVersionUID = 1L;

	public ReseñaDuplicadaException() {
		super("Este cliente ya ha reseñado este producto.");
	}

	public ReseñaDuplicadaException(String message) {
		super(message);
	}
}
