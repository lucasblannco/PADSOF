package Excepcion;

public class TipoProductoDesconocidoException extends FicheroFormatoInvalidoException {
    
    // El orden estándar que estamos usando: (int, String, String)
    public TipoProductoDesconocidoException(int numLinea, String linea, String tipo) {
        // Llamamos al constructor del padre (FicheroFormatoInvalidoException)
        super(numLinea, linea, "Tipo de producto desconocido: " + tipo);
    }
}