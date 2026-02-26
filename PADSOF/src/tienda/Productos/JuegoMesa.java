package tienda.Productos;



public class JuegoMesa extends ProductoVenta {
    private int minJugadores;
    private int maxJugadores;
    private int minEdad;
    private int maxEdad;
    private String tipoJuego;

    public JuegoMesa() {
        super();
    }

    @Override
    public double calcularPrecioFinal() {
        return this.precio;
    }
}