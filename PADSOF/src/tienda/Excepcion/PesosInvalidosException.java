package Excepcion;


public class PesosInvalidosException extends CheckPointException {

    private static final long serialVersionUID = 1L;
	private final double pesoValoracion;
    private final double pesoCompras;
    private final double pesoCategorias;

    public PesosInvalidosException(double pesoValoracion, double pesoCompras, double pesoCategorias) {
        super("Pesos invalidos (" + pesoValoracion + ", " + pesoCompras + ", " + pesoCategorias
                + "): no pueden ser negativos ni todos cero.");
        this.pesoValoracion  = pesoValoracion;
        this.pesoCompras     = pesoCompras;
        this.pesoCategorias  = pesoCategorias;
    }

    public double getPesoValoracion()  { return pesoValoracion; }
    public double getPesoCompras()     { return pesoCompras; }
    public double getPesoCategorias()  { return pesoCategorias; }
}
