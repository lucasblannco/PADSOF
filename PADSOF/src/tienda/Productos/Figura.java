package tienda.Productos;



public class Figura extends ProductoVenta {
    private double altura;
    private double ancho;
    private double largo;
    private String material;
    private String marca;

    public Figura() {
        super();
    }

    @Override
    public double calcularPrecioFinal() {
        return this.precio;
    }
}
