package tienda.Productos;

import java.util.List;
import java.util.ArrayList;

public class Pack extends ProductoVenta {
    private List<ProductoVenta> productosIncluidos;
    private double descuentoPorcentaje; // Ej: 0.10 para un 10%
    //este descuento existe por ser un pack. es un descuento por comprar varias cosas. ademas, el pack podria tener un descuento

    public Pack(String nombre, String descripcion, double descuentoPorcentaje) {
        super(); // Llama al constructor de ProductoVenta
        this.setNombre(nombre);
        this.setDescripcion(descripcion);
        this.productosIncluidos = new ArrayList<>();
        this.descuentoPorcentaje = descuentoPorcentaje;
    }

    public void añadirProducto(ProductoVenta p) {
        this.productosIncluidos.add(p);
    }

    
    @Override
    public double calcularPrecioFinal() {
        double sumaPrecios = 0;
        for (ProductoVenta p : productosIncluidos) {
            sumaPrecios += p.calcularPrecioFinal();// tenemos en cuenta el precio con descuento
            //sumaPrecios += p.getPrecioOficial(); // tenemos en cuenta el precio oficial
        }
        
        return sumaPrecios * (1 - descuentoPorcentaje);
    }

    // --- GETTERS Y SETTERS ---
    public List<ProductoVenta> getProductosIncluidos() {
        return productosIncluidos;
    }

    public void setProductosIncluidos(List<ProductoVenta> productosIncluidos) {
        this.productosIncluidos = productosIncluidos;
    }

    public double getDescuentoPorcentaje() {
        return descuentoPorcentaje;
    }

    public void setDescuentoPorcentaje(double descuentoPorcentaje) {
        this.descuentoPorcentaje = descuentoPorcentaje;
    }
}
