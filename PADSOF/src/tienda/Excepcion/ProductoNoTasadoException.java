package Excepcion;


public class ProductoNoTasadoException extends CheckPointException {

    private static final long serialVersionUID = 1L;
	private final String idProducto;
    private final String nombreProducto;

    public ProductoNoTasadoException(String idProducto, String nombreProducto) {
        super("El producto " + nombreProducto + " (id: " + idProducto
                + ") no puede participar en un intercambio porque aun no ha sido tasado.");
        this.idProducto     = idProducto;
        this.nombreProducto = nombreProducto;
    }

    public String getIdProducto()     { return idProducto; }
    public String getNombreProducto() { return nombreProducto; }
}