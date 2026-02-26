package tienda.Ventas;

import tienda.Productos.ProductoVenta;

public class LineaCarrito {
    private ProductoVenta producto;
    private int cantidad;

    public LineaCarrito(ProductoVenta producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public ProductoVenta getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double obtenerSubtotal() {
        return producto.calcularPrecioFinal() * cantidad;
    }
}
