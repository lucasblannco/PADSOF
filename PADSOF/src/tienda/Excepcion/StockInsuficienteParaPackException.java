package Excepcion;

public class StockInsuficienteParaPackException extends CheckPointException {

	private static final long serialVersionUID = 1L;

	public StockInsuficienteParaPackException() {
		super("No hay stock suficiente para añadir ese producto al pack.");
	}

	public StockInsuficienteParaPackException(String message) {
		super(message);
	}
}
