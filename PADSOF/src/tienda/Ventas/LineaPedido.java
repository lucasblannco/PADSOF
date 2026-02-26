package tienda.Ventas;

import tienda.Productos.ProductoVenta;

public class LineaPedido {
    private ProductoVenta producto;
    private int cantidad;
    private double precioVenta; // Precio en el momento de la compra

    public LineaPedido(ProductoVenta producto, int cantidad, double precioVenta) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioVenta = precioVenta;
    }
    
    public double precio() {
    	return precioVenta * cantidad;
    }
}
