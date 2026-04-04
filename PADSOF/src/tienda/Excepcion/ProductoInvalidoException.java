package Excepcion;

public class ProductoInvalidoException extends CheckPointException {

	private static final long serialVersionUID = 1L;

	public ProductoInvalidoException() {
		super("Los datos del producto no son válidos.");
	}

	public ProductoInvalidoException(String message) {
		super(message);
	}
}
