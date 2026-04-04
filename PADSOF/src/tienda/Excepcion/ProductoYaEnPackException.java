package Excepcion;

public class ProductoYaEnPackException extends CheckPointException {

	private static final long serialVersionUID = 1L;

	public ProductoYaEnPackException() {
		super("Ese producto ya está incluido en el pack.");
	}

	public ProductoYaEnPackException(String message) {
		super(message);
	}
}
