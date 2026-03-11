package productos;

import java.util.*;

public class Stock {

    private ArrayList<ProductoVenta> productos;

    public Stock() {
        this.productos = new ArrayList<>();
    }

    public boolean añadirProducto(ProductoVenta producto) {
        if (producto == null) {
            return false;
        }
        return this.productos.add(producto);
    }

    public boolean eliminarProducto(ProductoVenta producto) {
        if (producto == null) {
            return false;
        }
        return this.productos.remove(producto);
    }

    public ArrayList<ProductoVenta> getProductos() {
        return new ArrayList<>(this.productos);
    }

    // De menos a más stock
    public ArrayList<ProductoVenta> getProductosOrdenadosStockAsc() {
        ArrayList<ProductoVenta> copia = new ArrayList<>(this.productos);

        Collections.sort(copia, Comparator.comparingInt(ProductoVenta::getStockDisponible));

        return copia;
    }

    // De más a menos stock
    public ArrayList<ProductoVenta> getProductosOrdenadosStockDesc() {
        ArrayList<ProductoVenta> copia = new ArrayList<>(this.productos);

        Collections.sort(copia, Comparator.comparingInt(ProductoVenta::getStockDisponible).reversed());

        return copia;
    }
}


/* ============ ESTA CLASE SOBRA ============== */