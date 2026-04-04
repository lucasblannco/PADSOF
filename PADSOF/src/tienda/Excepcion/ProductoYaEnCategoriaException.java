package Excepcion;

public class ProductoYaEnCategoriaException extends CheckPointException {

	private static final long serialVersionUID = 1L;

	public ProductoYaEnCategoriaException() {
		super("El producto ya pertenece a esa categoría.");
	}

	public ProductoYaEnCategoriaException(String message) {
		super(message);
	}
}