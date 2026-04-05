package Excepcion;

public class TipoProductoDesconocidoException extends FicheroFormatoInvalidoException {

	public TipoProductoDesconocidoException(int numLinea, String linea, String tipo) {
		super(numLinea, linea, "Tipo de producto desconocido: " + tipo);
	}
}