package ventas;

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
    
    public double getSubtotal() {
        return producto.getPrecioOficial() * cantidad;
    }
    
    public boolean productoPertence(ProductoVenta p) {
    	if (producto.getId() == p.getId()) {
    		return true;
    	}
    	return false;
    }
    
    public ProductoVenta getProducto() {
    	return this.producto;
    }
    
    public int getCantidad() {
        return cantidad;
    }
}
