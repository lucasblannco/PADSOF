package Excepcion;

public class CheckPointException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CheckPointException(String mensaje) {
		super(mensaje);
	}
}